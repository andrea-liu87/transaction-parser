package com.andreasgift.transactionparser

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import com.andreasgift.transactionparser.Constant.Companion.TAG
import com.andreasgift.transactionparser.model.SMS
import com.andreasgift.transactionparser.model.Transaction
import com.andreasgift.transactionparser.model.TransactionType
import com.google.mlkit.nl.entityextraction.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class TransactionParser {
    private fun checkPermissionGranted(context: Context, permission: String) =
        context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

    fun create(){}

    fun checkPermission(context: Activity){
        val granted: Boolean = REQUIRED_PERMISSIONS.all {
            checkPermissionGranted(context, it)
        }

        if (!granted){
            ActivityCompat.requestPermissions(context, REQUIRED_PERMISSIONS, 101)
        }
    }

    /**
     * Fetch all sms content that related to the transactions based on the list of [bankCodes].
     * User need to determine the transactions by SMS sender as input in [bankCodes]
     */
    fun fetchAllSms(context: Context, bankCodes: List<String>) : List<SMS> {
        val smsList = arrayListOf<SMS>()

        val cursor: Cursor? =
            context.contentResolver.query(Uri.parse(Constant.INBOX), null, null, null, null)

        if (cursor != null) {
            val colSender = cursor.getColumnIndex(Constant.ADDRESS)
            val colDate = cursor.getColumnIndex(Constant.DATE)
            val colMessage = cursor.getColumnIndex(Constant.BODY)

            if (cursor.moveToFirst()) {
                do {
                    val content = cursor.getString(colMessage)
                    val sender = cursor.getString(colSender)
                    val date = cursor.getString(colDate)

                    if (bankCodes.map { it.lowercase() }.any { code ->
                            sender.lowercase().contains(code)
                        }) {

                        val dateSms = Util.parsingDateFromLong(date.toLong())
                        smsList.add(SMS(
                                cursor.getString(0).toInt(),
                                sender,
                                content,
                                dateSms
                            )
                        )
                        Log.d(TAG, "from: $sender, content: $content on $dateSms")
                    }
                } while (cursor.moveToNext())
            } else {
                Log.d(TAG, "inbox is empty")
            }
        }
        return smsList
    }

    /**
     * Fetch last [noOfSms] sms content that related to the transactions based on the list of [bankCodes].
     * User need to determine the transactions by SMS sender as input in [bankCodes]
     */
    fun fetchLastSms(context: Context, bankCodes: List<String>, noOfSms: Int) : List<SMS> {
        val smsList = arrayListOf<SMS>()

        val cursor: Cursor? =
            context.contentResolver.query(Uri.parse(Constant.INBOX), null, null, null, null)

        if (cursor != null) {
            val colSender = cursor.getColumnIndex(Constant.ADDRESS)
            val colDate = cursor.getColumnIndex(Constant.DATE)
            val colMessage = cursor.getColumnIndex(Constant.BODY)

            if (cursor.moveToFirst()) {
                do {
                    val content = cursor.getString(colMessage)
                    val sender = cursor.getString(colSender)
                    val date = cursor.getString(colDate)

                    if (bankCodes.map { it.lowercase() }.any { code ->
                            sender.lowercase().contains(code)
                        }) {

                        val dateSms = Util.parsingDateFromLong(date.toLong())
                        smsList.add(SMS(
                            cursor.getString(0).toInt(),
                            sender,
                            content,
                            dateSms
                        )
                        )
                        Log.d(TAG, "from: $sender, content: $content on $dateSms")
                    }
                } while (cursor.moveToNext() && smsList.size < noOfSms)
            } else {
                Log.d(TAG, "inbox is empty")
            }
        }
        return smsList
    }

    /**
     * Fetch sms content that related to the transactions based on the list of [bankCodes]
     * for the last [noOfMonths] months (including this months).
     * User need to determine the transactions by SMS sender as input in [bankCodes]
     */
    fun fetchLastMonthsSms(context: Context, bankCodes: List<String>, noOfMonths: Int) : List<SMS> {
        val calendar = GregorianCalendar()
        calendar.time = Date()
        calendar.add(Calendar.MONTH, -noOfMonths)
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)

        val smsList = arrayListOf<SMS>()

        val cursor: Cursor? =
            context.contentResolver.query(Uri.parse(Constant.INBOX), null, null, null, null)

        if (cursor != null) {
            val colSender = cursor.getColumnIndex(Constant.ADDRESS)
            val colDate = cursor.getColumnIndex(Constant.DATE)
            val colMessage = cursor.getColumnIndex(Constant.BODY)

            if (cursor.moveToFirst()) {
                do {
                    val content = cursor.getString(colMessage)
                    val sender = cursor.getString(colSender)
                    val date = cursor.getString(colDate)

                    if (bankCodes.map { it.lowercase() }.any { code ->
                            sender.lowercase().contains(code)
                        }) {

                        val dateSms = Util.parsingDateFromLong(date.toLong())
                        if (dateFormat.parse(dateSms)!! > calendar.time) {
                            smsList.add(
                                SMS(
                                    cursor.getString(0).toInt(),
                                    sender,
                                    content,
                                    dateSms
                                )
                            )
                            Log.d(TAG, "from: $sender, content: $content on $dateSms")
                        }
                    }
                } while (cursor.moveToNext())
            } else {
                Log.d(TAG, "inbox is empty")
            }
        }
        return smsList
    }

    /**
     * Fetch all transactions based on the list of [bankCodes].
     * User need to determine the transactions by SMS sender as input in [bankCodes]
     */
    fun fetchAllTransactions(context: Context, bankCodes: List<String>, successListener: (List<Transaction>) -> Unit) {
        val smsList = fetchAllSms(context, bankCodes)
        CoroutineScope(Dispatchers.IO).launch {
            val transactionList = parseTransactions(smsList)
            withContext(Dispatchers.Main){
                successListener(transactionList)
                Log.d(TAG, "parsing all transaction is finished")
            }
        }
    }

    /**
     * Fetch all transactions from the last of [noOfMonths] months, based on the list of [bankCodes].
     * User need to determine the transactions by SMS sender as input in [bankCodes]
     */
    fun fetchTransactionsLastMonthsOf(
        context: Context,
        bankCodes: List<String>,
        successListener: (List<Transaction>) -> Unit,
        noOfMonths: Int
    ){
        val smsList = fetchLastMonthsSms(context, bankCodes, noOfMonths)
        CoroutineScope(Dispatchers.IO).launch {
            val transactionList = parseTransactions(smsList)
            withContext(Dispatchers.Main){
                successListener(transactionList)
                Log.d(TAG, "parsing all transaction is finished")
            }
        }
    }

    /**
     * Fetch last [noOfTransaction] transactions, based on the list of [bankCodes].
     * User need to determine the transactions by SMS sender as input in [bankCodes]
     */
    fun fetchLastTransactions(
        context: Context,
        bankCodes: List<String>,
        successListener: (List<Transaction>) -> Unit,
        noOfTransaction: Int
    ) {
        val smsList = fetchAllSms(context, bankCodes)
        val transactionList = arrayListOf<Transaction>()
        CoroutineScope(Dispatchers.IO).launch {
            if (smsList.isNotEmpty()) {
                smsList.forEachIndexed { index, it ->
                    if (transactionList.size < noOfTransaction) {
                        val transaction = parseTransaction(it)
                        if (transaction != null) {
                            transactionList.add(transaction)
                        }
                        Log.d(TAG, "parse transaction $index success")
                    }
                }
            }
            withContext(Dispatchers.Main) {
                successListener(transactionList)
                Log.d(TAG, "parsing all transaction is finished")
            }
        }
    }

    fun setTransactionBroadcastNotification(){}

    private suspend fun parseTransactions(smsList: List<SMS>): List<Transaction> {
        val transactionList = arrayListOf<Transaction>()

        if (smsList.isNotEmpty()) {
            smsList.forEachIndexed { index, it ->
                val transaction = parseTransaction(it)
                if (transaction != null) {
                    transactionList.add(transaction)
                }
                Log.d(TAG, "parse transaction $index success")
            }
        }
        return transactionList
    }

    private suspend fun parseTransaction(sms: SMS): Transaction? {
        val res = CompletableDeferred<Transaction?>()

        val id = sms.id
        val source = sms.sender
        val date = sms.date
        val currency = detectCurrency(sms.content)
        val merchant = detectMerchant(sms.content)
        val type = detectTransactionType(sms.content)

        if (currency == null && type == null){
            res.complete(null)
        }

        val entityExtractor =
            EntityExtraction.getClient(
                EntityExtractorOptions.Builder(EntityExtractorOptions.ENGLISH)
                    .build()
            )

        val params =
            EntityExtractionParams.Builder(sms.content)
                .setEntityTypesFilter(setOf(Entity.TYPE_MONEY, Entity.TYPE_PAYMENT_CARD))
                .build()

        val amountList = arrayListOf<Double>()
        entityExtractor.downloadModelIfNeeded()

        val task = entityExtractor.annotate(params)
            .addOnSuccessListener {
                for (entityAnnotation in it){
                    val entitites = entityAnnotation.entities
                    for (entity in entitites){
                        if (entity is MoneyEntity) {
                            var amount = entity.integerPart.toDouble()
                            val fracPart: Double = if (entity.fractionalPart.toDouble() > 10) {
                                (entity.fractionalPart.toDouble() / 100)
                            } else if (entity.fractionalPart == 0) {
                                0.0
                            } else {
                                (entity.fractionalPart.toDouble() / 10)
                            }
                            amount += fracPart
                            amountList.add(amount)
                        }
                    }
                }
                if (amountList.isNotEmpty()) {
                    val transaction =
                        Transaction(
                            id,
                            amountList[0],
                            currency ?: Currency.getInstance(Locale.getDefault()),
                            type ?: TransactionType.Credit(TransactionType.Credit.CreditType.credit),
                            date,
                            source,
                            merchant)
                    Log.d(TAG, "detect amount : $amountList")
                    res.complete(transaction)
                } else {
                    res.complete(null)
                }
            }
        return res.await()
    }

    private fun detectCurrency(string: String): Currency? {
        val wordsArray = string.lowercase().split(" ")
        Currency.getAvailableCurrencies().forEach { currency ->
            if (wordsArray.contains(currency.currencyCode.lowercase())
                || wordsArray.contains(currency.symbol)
            ) {
                Log.d(TAG, "detect currency : ${currency.currencyCode}")
                return currency
            }
        }
        return null
    }

    private fun detectTransactionType(string: String): TransactionType? {
        val wordsArray = string.lowercase().split(" ")
        TransactionType.Credit.CreditType.values().forEach {
            if (wordsArray.contains(it.name)) {
                Log.d(TAG, "detect transaction type : ${it.name}")
                return TransactionType.Credit(it)
            }
        }
        TransactionType.Debit.DebitType.values().forEach {
            if (wordsArray.contains(it.name)) {
                Log.d(TAG, "detect transaction type : ${it.name}")
                return TransactionType.Debit(it)
            }
        }
        return null
    }

    private fun detectMerchant(string: String): String {
        val wordsArray = string.split(" ")
        var merchant = ""
        var currentIndex = 0
        wordsArray.forEachIndexed { index, it ->
            if (it == it.uppercase() && !isCurrency(it) && !isDouble(it)){
                if (merchant == ""){
                    merchant += it
                } else {
                    if (index == currentIndex + 1) {
                        merchant += " $it"
                    }
                }
                currentIndex = index
            }
        }
        Log.d(TAG, "detect merchant : $merchant")
        return merchant
    }

    private fun isCurrency(string: String):Boolean{
        val localCurrency = Currency.getInstance(Locale.getDefault())
        if (string.lowercase() == localCurrency.currencyCode || string.lowercase() == localCurrency.symbol || string.lowercase() == "$"){
            return true
        }
        Currency.getAvailableCurrencies().forEach {
            if (it.currencyCode.lowercase() == string.lowercase() || it.symbol.lowercase() == string.lowercase()){
                return true
            }
        }
        return false
    }

    private fun isDouble(string: String): Boolean{
        return try {
            val num = string.toDouble()
            true
        } catch (e: java.lang.Exception){
            false
        }
    }

    companion object{
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.READ_SMS
        )
    }
}