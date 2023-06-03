package com.andreasgift.transactionsmsparser

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreasgift.transactionparser.model.SMS
import com.andreasgift.transactionparser.model.Transaction
import com.andreasgift.transactionsmsparser.data.SMS.SMSRepository
import com.andreasgift.transactionsmsparser.data.transaction.TransactionRepository
import com.andreasgift.transactionsmsparser.data.transaction.TransactionType
import com.google.mlkit.nl.entityextraction.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.*
import javax.inject.Inject

data class HomeUiState(
    val loading: Boolean = false, // if necessary
    val smsList: List<SMS> = arrayListOf(),
    val transactionList: List<Transaction> = arrayListOf()
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
        fetchSMSfromServer(arrayListOf("Liv"))
        fetchSampleTransactions()
    }

    private fun fetchSMSfromServer(BankCodes: List<String>) {
        viewModelScope.launch {
            repo.fetchSMSfromServer(BankCodes)
            withContext(Dispatchers.Main){
                fetchSmsFromDB()
            }
        }
    }

    private fun fetchSampleTransactions() {
        viewModelScope.launch {
            val list = arrayListOf<Transaction>()
            val listener: (List<Transaction>) -> Unit = { transactionList ->
                transactionList.forEachIndexed { index, it ->
                    Log.d("Transaction : ", "${it.date} ${it.currency} ${it.amount} for ${it.merchant}")
                    list.add(it)
                }
                _homeState.update { it.copy(transactionList = list) }
            }
            transactionRepo.getLastTransactions(arrayListOf("Liv"), listener, 10)
        }
    }

    private fun fetchSmsFromDB() {
        viewModelScope.launch {
            val list = repo.fetchSmsFromDB()
            withContext(Dispatchers.Main){
                _homeState.update {
                    it.copy(smsList = list.subList(0,9).map { sms -> SMS(sms.id, sms.sender, sms.content, sms.date) })
                }
            }
        }
    }
}