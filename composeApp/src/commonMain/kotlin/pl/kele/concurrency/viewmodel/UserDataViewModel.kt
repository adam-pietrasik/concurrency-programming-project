package pl.kele.concurrency.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pl.kele.concurrency.model.UserData

class UserDataViewModel : ViewModel() {

    private val _userDataList = MutableStateFlow<List<UserData>>(emptyList())
    val mUserDataList = _userDataList.asStateFlow()

    fun addUser(fileSize: List<Long>) {
        val user = UserData(
            fileSize = fileSize,
            entryTime = System.currentTimeMillis(),
            timeInQueue = 0,
            priority = 0.0,
            isFileUploading = false
        )
        _userDataList.value += user
    }

    fun updateUsers() {
        val newList = _userDataList.value.map { user ->
            user.copy(
                timeInQueue = user.updateTimeInQueue(),
                priority = user.updatePriority(_userDataList.value.size)
            )
        }.sortedByDescending { it.priority  }.sortedByDescending { !it.isFileUploading }


        _userDataList.value = emptyList()
        _userDataList.value = newList
    }

    fun updateUser(user: UserData) {
        val newList = _userDataList.value.map { u ->
            if (u.id == user.id) {
                u.copy(
                    isFileUploading = user.isFileUploading,
                )
            } else {
                u.copy()
            }
        }.sortedByDescending { it.priority }.sortedByDescending { !it.isFileUploading }
        _userDataList.value = emptyList()
        _userDataList.value = newList
    }

    fun removeUsers() {
        _userDataList.value = emptyList()
        UserData.index = 0
    }

    fun removeFile(user: UserData) {
        user.fileSize = user.fileSize.drop(1)
        val newList = _userDataList.value.map { u ->
            if (u.id == user.id) {
                u.copy(fileSize = user.fileSize, isFileUploading = user.isFileUploading)
            } else
                u
        }
        _userDataList.value = emptyList()
        _userDataList.value = newList
        if (user.fileSize.isEmpty()) {
            removeUser(user)
        }
    }

    fun removeUser(user: UserData) {
        _userDataList.update { userList ->
            userList.filter { u ->
                u.id != user.id
            }
        }
    }
}