package com.andreasgift.transactionsmsparser.data.transaction

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.Currency

class TransactionConverter {
    @TypeConverter
    fun currencyToJson(value: Currency?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToCurrency(value: String) = Gson().fromJson(value, Currency::class.java)

    @TypeConverter
    fun transactionTypToJson(value: TransactionType?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToTransactionTyp(value: String) = Gson().fromJson(value, TransactionType::class.java)
}