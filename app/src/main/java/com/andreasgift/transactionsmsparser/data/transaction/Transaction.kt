package com.andreasgift.transactionsmsparser.data.transaction

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andreasgift.transactionsmsparser.util.Constant
import java.util.Currency

@Entity(tableName = "Transaction_table")
data class Transaction (
    @PrimaryKey
    @ColumnInfo("_id")
    val id:Int,

    @ColumnInfo("amount")
    val amount: Double,

    @ColumnInfo("currency")
    val currency: Currency,

    @ColumnInfo("type")
    val transactionType: TransactionType,

    @ColumnInfo("date")
    val date: String,

    @ColumnInfo("source")
    val source: String,

    @ColumnInfo("merchant")
    val merchant: String
)

sealed interface TransactionType {
    data class Debit(val type: DebitType) : TransactionType {

        enum class DebitType  {
            debit
        }
    }
    data class Credit(val type: CreditType) : TransactionType {

        enum class CreditType {
            credit, bill, purchase, payment, withdrawalL, transfer
        }
    }
}