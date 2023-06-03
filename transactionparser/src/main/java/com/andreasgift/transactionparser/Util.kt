package com.andreasgift.transactionparser

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Util {
    companion object {
        fun parsingDateFromLong(date: Long): String {
            val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
            return try {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = date
                formatter.format(calendar.time)
            } catch (e: ParseException){
                ""
            }
        }
    }
}