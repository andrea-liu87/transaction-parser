package com.andreasgift.transactionsmsparser.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.android.material.circularreveal.CircularRevealHelper.Strategy

@Dao
interface SMSDao {

    @Query("SELECT * FROM SMS_table ORDER BY _id DESC")
    suspend fun getAllSms(): List<SMS>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSms(sms: SMS)
}