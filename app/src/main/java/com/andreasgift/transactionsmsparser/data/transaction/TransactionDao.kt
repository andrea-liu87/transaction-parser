package com.andreasgift.transactionsmsparser.data.transaction

import androidx.room.*

@Dao
interface TransactionDao {

    @Query("SELECT * FROM Transaction_table ORDER BY _id DESC")
    suspend fun getAllTransaction(): List<Transaction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)
}