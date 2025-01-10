package pl.kele.concurrency

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import pl.kele.concurrency.viewmodel.FileTransferViewModel
import pl.kele.concurrency.viewmodel.UserDataViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val fileTransferViewModel: FileTransferViewModel by viewModels()
            val userDataViewModel: UserDataViewModel by viewModels()
            App(
                fileTransferViewModel,
                userDataViewModel
            )
        }
    }
}
