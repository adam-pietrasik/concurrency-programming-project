package pl.kele.concurrency.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Disk(
    var id: Int = 0,
    isBusy: Boolean = false,
    var currentUser: UserData? = null,
    var currentFileSize: Long = 0,
    transferredFileSize: Long = 0
) {

    var isBusy by mutableStateOf(false)
    var transferredFileSize by mutableStateOf(0L)

}