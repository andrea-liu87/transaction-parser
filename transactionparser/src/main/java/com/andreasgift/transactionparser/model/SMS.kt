package com.andreasgift.transactionparser.model

data class SMS (
    val id:Int,
    val sender: String,
    val content: String,
    val date: String
    )