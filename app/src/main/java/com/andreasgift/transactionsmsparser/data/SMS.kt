package com.andreasgift.transactionsmsparser.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andreasgift.transactionsmsparser.util.Constant

@Entity(tableName = "SMS_table")
data class SMS (
    @PrimaryKey
    @ColumnInfo("_id")
    val id:Int,

    @ColumnInfo(Constant.ADDRESS)
    val sender: String,

    @ColumnInfo(Constant.BODY)
    val content: String,

    @ColumnInfo(Constant.DATE)
    val date: String
        )