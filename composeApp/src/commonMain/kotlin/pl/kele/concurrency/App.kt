package pl.kele.concurrency

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.kele.concurrency.model.UserData
import pl.kele.concurrency.viewmodel.FileTransferViewModel
import pl.kele.concurrency.viewmodel.UserDataViewModel
import kotlin.random.Random

const val fileSizeRangeStart = 200L
const val fileSizeRangeEnd = 3_000L

const val fileTransferRangeStart = 180L
const val fileTransferRangeEnd = 200L

const val numberOfFilesRangeStart = 1L
const val numberOfFilesRangeEnd = 5L

@Composable
@Preview
fun App(
    fileTransferViewModel: FileTransferViewModel,
    userDataViewModel: UserDataViewModel
) {
    MaterialTheme {

        val fileSizeStartRange by remember { mutableStateOf(fileSizeRangeStart..fileSizeRangeEnd) }
        var fileSizeRange by remember { mutableStateOf(fileSizeRangeStart..fileSizeRangeEnd) }

        val transferSizeStartRange by remember { mutableStateOf(fileTransferRangeStart..fileTransferRangeEnd) }
        var transferSizeRange by remember { mutableStateOf(fileTransferRangeStart..fileTransferRangeEnd) }

        val numberOfFilesStartRange by remember { mutableStateOf(numberOfFilesRangeStart..numberOfFilesRangeEnd) }
        var numberOfFilesRange by remember { mutableStateOf(numberOfFilesRangeStart..numberOfFilesRangeEnd) }

        val disksList = fileTransferViewModel.mDisksList.collectAsState()

        val isSimulationRunning = fileTransferViewModel.mIsFileTransferRunning

        LaunchedEffect(Unit) {
            fileTransferViewModel.createDisks()
        }

        Column(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxSize()
            ) {

                Column(
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxSize()
                        .background(Color.Gray)
                ) {
                    LazyRow {
                        items(disksList.value) { disk ->
                            Column {
                                Text("Disk ${disk.id}")
                                Text("Current user data: ${disk.currentUser?.id} ${disk.currentFileSize}")
                                Text("Data transfer progress = ${disk.transferredFileSize}")
                                Text("Is data transfer in progress = ${disk.isBusy}")
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                    }

                }

                Row(
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxSize()
                        .background(Color.LightGray)
                ) {
                    SliderContent(
                        fileSizeStartRange,
                        fileSizeRange,
                        onValueChange = { newRange ->
                            fileSizeRange = newRange as LongRange
                        },
                        "File size range [MB]"
                    )
                    SliderContent(
                        transferSizeStartRange,
                        transferSizeRange,
                        onValueChange = { newRange ->
                            transferSizeRange = newRange as LongRange
                        },
                        "File transfer size [MB/s]"
                    )
                    SliderContent(
                        numberOfFilesStartRange,
                        numberOfFilesRange,
                        onValueChange = { newRange ->
                            numberOfFilesRange = newRange as LongRange
                        },
                        "Number of files"
                    )
                }

            }

            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxSize()
            ) {
                val usersList = userDataViewModel.mUserDataList.collectAsState()
                var fileSize by remember { mutableStateOf<List<Long>>(emptyList()) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Button(onClick = {
                            userDataViewModel.updateUsers()
                            fileTransferViewModel.startFileTransfer(
                                usersFlow = userDataViewModel.mUserDataList,
                                transferSizeRange = transferSizeRange,
                                userDataAction = { user ->
                                    if (user?.fileSize?.isNotEmpty() == true) {
                                        userDataViewModel.removeFile(user)
                                        userDataViewModel.updateUsers()
                                    }
                                    else {
                                        user?.let { userDataViewModel.removeUser(it) }
                                    }
                                },
                                updateUserDataAction = { user ->
                                    userDataViewModel.updateUser(user)
                                }
                            )
                        }) {
                            Text("Start simulation")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = {
                            fileTransferViewModel.cancelFileTransfer()
                        }) {
                            Text("Stop simulation")
                        }
                    }

                    Row {
                        Button(
                            onClick = {
                                for (j in 0 ..< Random.nextLong(numberOfFilesRange.first, numberOfFilesRange.last + 1)) {
                                    fileSize += Random.nextLong(fileSizeRange.first, fileSizeRange.last + 1)
                                }
                                fileSize = fileSize.sorted()
                                userDataViewModel.addUser(fileSize)
                                if (isSimulationRunning.value)
                                    userDataViewModel.updateUsers()
                                fileSize = emptyList()
                            }
                        ) {
                            Text("Add user(s)")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = {
                            userDataViewModel.removeUsers()
                        }) {
                            Text("Delete all users")
                        }
                    }


                }

                AddedContent(usersList = usersList.value)

            }
        }
    }
}

@Composable
fun AddedContent(
    modifier: Modifier = Modifier,
    usersList: List<UserData>
) {

    LazyColumn {
        item {
            Row(
                modifier = modifier.fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ID",
                    modifier = Modifier
                        .weight(0.2f)
                        .border(1.dp, Color.Black)
                        .padding(start = 6.dp, bottom = 2.dp))
                Text("FILE SIZE",
                    modifier = Modifier
                        .weight(0.2f)
                        .border(1.dp, Color.Black)
                        .padding(start = 6.dp, bottom = 2.dp))
                Text("TIME IN QUEUE",
                    modifier = Modifier
                        .weight(0.2f)
                        .border(1.dp, Color.Black)
                        .padding(start = 6.dp, bottom = 2.dp))
                Text("PRIORITY",
                    modifier = Modifier
                        .weight(0.2f)
                        .border(1.dp, Color.Black)
                        .padding(start = 6.dp, bottom = 2.dp))
                Text("FILE TRANSFER",
                    modifier = Modifier
                        .weight(0.2f)
                        .border(1.dp, Color.Black)
                        .padding(start = 6.dp, bottom = 2.dp))
            }
        }
        items(usersList) { user ->
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${user.id}",
                    modifier = Modifier
                        .weight(0.2f)
                        .border(1.dp, Color.Black)
                        .padding(start = 6.dp, bottom = 2.dp)
                )
                Text("${user.fileSize}",
                    modifier = Modifier
                        .weight(0.2f)
                        .border(1.dp, Color.Black)
                        .padding(start = 6.dp, bottom = 2.dp)
                )
                Text("${user.timeInQueue}",
                    modifier = Modifier.weight(0.2f).border(1.dp, Color.Black)
                        .padding(start = 6.dp, bottom = 2.dp))
                Text("${user.priority}",
                    modifier = Modifier.weight(0.2f).border(1.dp, Color.Black)
                        .padding(start = 6.dp, bottom = 2.dp))
                Text("${user.isFileUploading}",
                    modifier = Modifier.weight(0.2f).border(1.dp, Color.Black)
                        .padding(start = 6.dp, bottom = 2.dp))
            }
        }
    }

}

@Composable
fun SliderContent(
    sliderStartRange: LongRange,
    sliderSizeRange: LongRange,
    onValueChange: (ClosedRange<Long>) -> Unit,
    sliderTitle: String
) {

    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(sliderTitle)
        Text(sliderSizeRange.last.toString())
        LongRangeSlider(
            sliderStartRange,
            sliderSizeRange,
            onValueChange = onValueChange
        )
        Text(sliderSizeRange.first.toString())

    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LongRangeSlider(
    valueRange: ClosedRange<Long>,
    currentRange: ClosedRange<Long>,
    onValueChange: (ClosedRange<Long>) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null
) {

    val floatRange = valueRange.start.toFloat()..valueRange.endInclusive.toFloat()
    var startValue by remember { mutableStateOf(currentRange.start.toFloat()) }
    var endValue by remember { mutableStateOf(currentRange.endInclusive.toFloat()) }

    RangeSlider(
        value = startValue..endValue,
        onValueChange = { range ->
            startValue = range.start
            endValue = range.endInclusive
            onValueChange(
                range.start.toLong()..range.endInclusive.toLong()
            )
        },
        valueRange = floatRange,
        onValueChangeFinished = onValueChangeFinished,
        modifier = Modifier
            .size(width = 200.dp, height = 200.dp)
            .padding(24.dp)
            .rotate(270f)
    )

}

//@Composable
//fun Table(
//    modifier: Modifier = Modifier,
//    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally
//) {
//
//    Column(
//        modifier = modifier,
//        horizontalAlignment = horizontalAlignment
//    ) {
//
//        TableHeaderRow()
//        TableRow(
//            userData = UserData(1, "Test", 50.0, 1, System.currentTimeMillis(), 1.0)
//        )
//        TableRow(
//            userData = UserData(1, "Test", 50.0, 1, System.currentTimeMillis(), 1.0)
//        )
//        TableRow(
//            userData = UserData(1, "Test", 50.0, 1, System.currentTimeMillis(), 1.0)
//        )
//    }
//
//}
//
//@Composable
//fun TableHeaderRow(modifier: Modifier = Modifier) {
//
//    val headerList = listOf("Id", "User", "File size", "Time in queue", "Entry time", "Priority")
//    Row {
//        for (header in headerList) {
//            TableCell(cellData = header)
//        }
//    }
//
//}
//
//@Composable
//fun TableRow(
//    modifier: Modifier = Modifier,
//    userData: UserData
//) {
//
//    Row(
//        modifier = modifier
//    ) {
//        TableCell(cellData = "${userData.id}")
//        TableCell(cellData = userData.userName)
//        TableCell(cellData = "${userData.fileSize}")
//        TableCell(cellData = "${userData.timeInQueue}")
//        TableCell(cellData = "${userData.entryTime}")
//        TableCell(cellData = "${userData.priority}")
//    }
//
//}
//
//@Composable
//fun TableCell (
//    modifier: Modifier = Modifier
//        .width(100.dp)
//        .border(1.dp, Color.Black),
//    cellData: String
//) {
//
//    Text(
//        modifier = modifier,
//        text = cellData
//    )
//
//}
//






