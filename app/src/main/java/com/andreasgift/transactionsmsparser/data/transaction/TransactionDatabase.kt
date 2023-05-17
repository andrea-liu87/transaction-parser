package com.andreasgift.transactionsmsparser.data.transaction

import android.content.Context
import androidx.room.*

@Database(
    entities = [
        Transaction::class
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(TransactionConverter::class)
abstract class TransactionDatabase : RoomDatabase() {

    abstract val dao : TransactionDao

    companion object {
        var instance : TransactionDatabase? = null
        val slock = Object()

        fun getInstance(context : Context) : TransactionDatabase {
            synchronized(slock) {
                if(instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, TransactionDatabase::class.java, "TransactionDatabase.db").build()
                }
                return instance as TransactionDatabase
            }
        }
    }
}