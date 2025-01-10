package pl.kele.concurrency.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import pl.kele.concurrency.model.Disk
import pl.kele.concurrency.model.UserData
import kotlin.random.Random

class FileTransferViewModel : ViewModel() {

    private val NUMBER_OF_DISKS = 5

    private val semaphores = Semaphore(NUMBER_OF_DISKS)
    private val _disksList = MutableStateFlow<List<Disk>>(emptyList())
    val mDisksList = _disksList.asStateFlow()

    private var transferJob: Job? = null
    var mIsFileTransferRunning = mutableStateOf(false)

    fun startFileTransfer(
        usersFlow: StateFlow<List<UserData>>,
        transferSizeRange: LongRange,
        userDataAction: (UserData) -> Unit
    ) {
        if (mIsFileTransferRunning.value)
            return
        mIsFileTransferRunning.value = true
        transferJob = viewModelScope.launch(Dispatchers.IO) {

            usersFlow.collect { users ->
                if (users.isNotEmpty()) {
                    val nextUser = users.first { user -> !user.isFileUploading }
                    delay(1000)
                        viewModelScope.launch {
                            semaphores.acquire()
                            fileTransfer(
                                disk = _disksList.value.firstOrNull { disk -> !disk.isBusy },
                                user = nextUser,
                                transferSpeedMbPerSecond = Random.nextLong(
                                    transferSizeRange.first,
                                    transferSizeRange.last + 1
                                ),
                                userDataAction = userDataAction
                            )
                        }
                }
            }
        }
    }


    private suspend fun fileTransfer(
        disk: Disk?,
        user: UserData,
        transferSpeedMbPerSecond: Long,
        userDataAction: (UserData) -> Unit
    ) {
        if (disk == null)
            return

        val updateIntervalSeconds = 0.1
        var transferredMb = 0.0
        user.isFileUploading = true
        userDataAction.invoke(user)
        disk.currentUser = user
        disk.isBusy = true

        while (transferredMb < user.fileSize) {
            delay((updateIntervalSeconds * 1000).toLong())
            transferredMb += transferSpeedMbPerSecond * updateIntervalSeconds
            disk.transferredFileSize = transferredMb.toLong()
        }

        disk.currentUser = null
        disk.isBusy = false
        println(semaphores.availablePermits)
        semaphores.release()
    }

    fun cancelFileTransfer() {
        mIsFileTransferRunning.value = false
        transferJob?.cancel()
    }

    fun createDisks() {
        viewModelScope.launch {
            for (number in 0..<NUMBER_OF_DISKS) {
                val disk = Disk(
                    id = number
                )
                _disksList.value += disk
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mIsFileTransferRunning.value = false
        transferJob?.cancel()
    }

}