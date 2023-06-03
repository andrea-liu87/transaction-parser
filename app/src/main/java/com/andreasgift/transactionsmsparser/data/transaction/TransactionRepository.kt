package com.andreasgift.transactionsmsparser.data.transaction

import android.content.Context
import com.andreasgift.transactionparser.TransactionParser
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val appContext: Context,
    private val dao: TransactionDao,
    private val parser: TransactionParser
) {
    suspend fun fetchTransactionFromDB()  = dao.getAllTransaction()

    suspend fun insertTransaction(transaction: Transaction){dao.insertTransaction(transaction)}

    fun getAllTransactions(bankCodes: List<String>, onSuccessListener: (List<com.andreasgift.transactionparser.model.Transaction>) -> Unit) =
        parser.fetchAllTransactions(appContext, bankCodes, onSuccessListener)

    fun getLastTransactions(bankCodes: List<String>, onSuccessListener: (List<com.andreasgift.transactionparser.model.Transaction>) -> Unit, noOfTransactions : Int) =
        parser.fetchLastTransactions(appContext, bankCodes, onSuccessListener, noOfTransactions)
}