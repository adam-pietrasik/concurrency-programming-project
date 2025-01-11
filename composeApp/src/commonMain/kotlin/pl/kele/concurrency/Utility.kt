package pl.kele.concurrency

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


object Utility {

    fun convertDate(dateInMilliseconds: Long, dateFormat: String): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault());
        val dateString = formatter.format(Date(dateInMilliseconds))
        return dateString
    }

}