package com.andreasgift.transactionsmsparser.data.SMS

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SMS::class
    ],
    version = 1,
    exportSchema = true,
)
abstract class SMSDatabase : RoomDatabase() {

    abstract val smsDao : SMSDao

    companion object {
        var instance : SMSDatabase? = null
        val slock = Object()

        fun getInstance(context : Context) : SMSDatabase {
            synchronized(slock) {
                if(instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, SMSDatabase::class.java, "SMSDatabase.db").build()
                }
                return instance as SMSDatabase
            }
        }
    }
}