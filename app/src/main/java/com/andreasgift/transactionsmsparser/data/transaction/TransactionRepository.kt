package com.andreasgift.transactionsmsparser.data.transaction

import android.content.Context
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val dao: TransactionDao
) {
    suspend fun fetchTransactionFromDB()  = dao.getAllTransaction()

    suspend fun insertTransaction(transaction: Transaction){dao.insertTransaction(transaction)}
}