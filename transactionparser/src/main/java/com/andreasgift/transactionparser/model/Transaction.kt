package com.andreasgift.transactionparser.model

import java.util.Currency

data class Transaction (
    val id:Int,
    val amount: Double,
    val currency: Currency,
    val transactionType: TransactionType,
    val date: String,
    val source: String,
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