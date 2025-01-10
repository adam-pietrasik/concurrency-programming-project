package pl.kele.concurrency.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pl.kele.concurrency.model.UserData

class UserDataViewModel : ViewModel() {

    private val _userDataList = MutableStateFlow<List<UserData>>(emptyList())
    val mUserDataList = _userDataList.asStateFlow()

    fun addUser(fileSize: Long) {
        val user = UserData(
            id = (_userDataList.value.size + 1),
            userName = "User ${_userDataList.value.size + 1}",
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
        }.sortedByDescending { it.priority }
        _userDataList.value = emptyList()
        _userDataList.value = newList
    }

    fun removeUsers() {
        _userDataList.value = emptyList()
    }

    fun removeUser(user: UserData) {
        println("toDelete = $user")
        _userDataList.update { userList ->
            userList.filter { u -> u.id != user.id }
        }
//        _userDataList.value -= user
    }
}