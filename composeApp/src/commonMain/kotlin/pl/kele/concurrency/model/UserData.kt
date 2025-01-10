package pl.kele.concurrency.model

import kotlin.math.abs
import kotlin.math.log

data class UserData(
    var id: Int,
    var userName: String,
    var fileSize: Long,
    var timeInQueue: Long,
    var entryTime: Long,
    var priority: Double,
    var isFileUploading: Boolean
) {

    fun updateTimeInQueue(): Long {
        val currentTimestamp = System.currentTimeMillis()
        timeInQueue = currentTimestamp - entryTime
        if (timeInQueue <= 0)
            timeInQueue = 1
        return currentTimestamp - entryTime
    }

    // variant I    logQ(t) + Q/S - version where longer you wait better prio you have
    // variant II   logS(t) + Q/S - version where lower the file better prio you have
    // variant III  logx(t) + Q/(S * t) - version where exact same file size will always give better output for last in line
    // x = abs(S - t)
    // Q - queue length
    // S - file size
    // t - time in queue

    fun updatePriority(queueSize: Int): Double {
//        priority = log(timeInQueue.toDouble(), abs(fileSize.toDouble() - timeInQueue)) + (queueSize + 1.0).div(fileSize * timeInQueue) // Variant III
        priority = log(timeInQueue.toDouble(), fileSize.toDouble()) + (queueSize + 1.0).div(fileSize) // Variant II
//        priority = log(timeInQueue.toDouble(), queueSize + 1.0) + (queueSize + 1.0).div(fileSize) // Variant I
        return priority
    }

}

