package com.andreasgift.transactionsmsparser.data.SMS

import android.content.Context
import com.andreasgift.transactionparser.TransactionParser
import javax.inject.Inject

class SMSRepository @Inject constructor(
    private val appContext: Context,
    private val dao: SMSDao,
    private val transactionParser: TransactionParser
) {
    suspend fun fetchSMSfromServer(BankCodes: List<String>){
        val smsTransaction = transactionParser.fetchAllSms(context = appContext, BankCodes)
        smsTransaction.forEach { dao.insertSms(
            SMS(it.id, it.sender, it.content, it.date)
        )}
        }

    suspend fun fetchSmsFromDB()  = dao.getAllSms()
}