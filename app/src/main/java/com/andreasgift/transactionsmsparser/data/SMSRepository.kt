package com.andreasgift.transactionsmsparser.data

import android.content.ContentValues.TAG
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.andreasgift.transactionsmsparser.util.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SMSRepository @Inject constructor(
    private val appContext: Context,
    private val dao: SMSDao
) {
    suspend fun fetchSMSfromServer(BankCodes: List<String>){
            val cursor: Cursor? =
                appContext.contentResolver.query(Uri.parse(Constant.INBOX), null, null, null, null)

            if (cursor != null) {
                val colSender = cursor.getColumnIndex(Constant.ADDRESS)
                val colDate = cursor.getColumnIndex(Constant.DATE)
                val colMessage = cursor.getColumnIndex(Constant.BODY)

                if (cursor.moveToFirst()) {
                    do {
                        val content = cursor.getString(colMessage)
                        val sender = cursor.getString(colSender)
                        val date = cursor.getString(colDate)

                        if (BankCodes.map { it.lowercase() }.any { code ->
                                sender.lowercase().contains(code)
                            }) {
                            dao.insertSms(
                                SMS(cursor.getString(0).toInt(), sender, content, parsingDate(date.toLong()))
                            )
                            Log.d(TAG, "from $sender content $content on ${parsingDate(date.toLong())}")
                        }
                    } while (cursor.moveToNext())
                } else {
                    Log.d(TAG, "inbox is empty")
                }
            }
        }

    private fun parsingDate(date: Long): String {
        val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
        return try {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date
            formatter.format(calendar.time)
        } catch (e: ParseException){
            ""
        }
    }

    suspend fun fetchSmsFromDB()  = dao.getAllSms()
}