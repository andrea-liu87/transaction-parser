package com.andreasgift.transactionsmsparser.data.SMS

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SMSDao {

    @Query("SELECT * FROM SMS_table ORDER BY _id DESC")
    suspend fun getAllSms(): List<SMS>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSms(sms: SMS)
}