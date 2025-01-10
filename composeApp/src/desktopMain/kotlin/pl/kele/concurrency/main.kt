package pl.kele.concurrency

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.kele.concurrency.viewmodel.FileTransferViewModel
import pl.kele.concurrency.viewmodel.UserDataViewModel

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ConcurrencyProgrammingProject",
        state = rememberWindowState(WindowPlacement.Maximized),
        alwaysOnTop = true,
        onKeyEvent = {
            if (it.key == Key.Escape) {
                exitApplication()
                true
            } else {
                false
            }
        }
    ) {
        val fileTransferViewModel = viewModel { FileTransferViewModel() }
        val userDataViewModel = viewModel { UserDataViewModel() }
        App(
            fileTransferViewModel,
            userDataViewModel
        )
    }
}