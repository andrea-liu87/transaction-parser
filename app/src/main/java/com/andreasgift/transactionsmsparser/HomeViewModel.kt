package com.andreasgift.transactionsmsparser

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreasgift.transactionsmsparser.data.SMS.SMS
import com.andreasgift.transactionsmsparser.data.SMS.SMSRepository
import com.andreasgift.transactionsmsparser.data.transaction.Transaction
import com.andreasgift.transactionsmsparser.data.transaction.TransactionRepository
import com.andreasgift.transactionsmsparser.data.transaction.TransactionType
import com.google.mlkit.nl.entityextraction.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.ParseException
import java.util.*
import javax.inject.Inject

data class HomeUiState(
    val loading: Boolean = false, // if necessary
    val smsList: List<SMS> = arrayListOf()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: SMSRepository,
    private val transactionRepo: TransactionRepository
) : ViewModel() {
    private val TAG = "TPSMS: ViewModel"

    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState

    init {
        fetchSmsFromDB()
    }

    fun fetchSMSfromServer(BankCodes: List<String>) {
        viewModelScope.launch {
            repo.fetchSMSfromServer(BankCodes)
        }
    }

    private fun fetchSmsFromDB() {
        viewModelScope.launch {
            val list = repo.fetchSmsFromDB()
            parseSMSToTransaction(list[kotlin.random.Random.nextInt(0, list.size - 1)])
            parseSMSToTransaction(list[kotlin.random.Random.nextInt(0, list.size - 1)])
            parseSMSToTransaction(list[kotlin.random.Random.nextInt(0, list.size - 1)])
            withContext(Dispatchers.Main) {
                _homeState.update {
                    it.copy(smsList = list)
                }
            }
        }
    }

    private fun parseSMSToTransaction(sms: SMS) {
        val id = sms.id
        val source = sms.sender
        val date = sms.date
        val currency = detectCurrency(sms.content)
        val merchant = detectMerchant(sms.content)
        val type = detectTransactionType(sms.content)

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
            .addOnSuccessListener {
                entityExtractor.annotate(params)
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
                                    Log.d(TAG, "money entity ${entity.unnormalizedCurrency} $amount")
                                }
                            }
                        }
                        var amount = 0.0
                        if (amountList.size > 0) amount = amountList[0]
                        Log.d(TAG, "transaction $id from $source on $date $currency $amount $type $merchant")
                    }
            }
    }

    private fun detectCurrency(string: String): Currency {
        val wordsArray = string.lowercase().split(" ")
        Currency.getAvailableCurrencies().forEach { currency ->
            if (wordsArray.contains(currency.currencyCode.lowercase())
                || wordsArray.contains(currency.symbol)
            ) {
                Log.d(TAG, "CURRENCY : ${currency.currencyCode}")
                return currency
            }
        }
        return Currency.getInstance(Locale.getDefault())
    }

    private fun detectTransactionType(string: String): TransactionType {
        val wordsArray = string.lowercase().split(" ")
        TransactionType.Credit.CreditType.values().forEach {
            if (wordsArray.contains(it.name)) {
                Log.d(TAG, "TRANSACTION TYPE : ${it.name}")
                return TransactionType.Credit(it)
            }
        }
        TransactionType.Debit.DebitType.values().forEach {
            if (wordsArray.contains(it.name)) {
                Log.d(TAG, "TRANSACTION TYPE : ${it.name}")
                return TransactionType.Debit(it)
            }
        }
        return TransactionType.Credit(TransactionType.Credit.CreditType.credit)
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
}