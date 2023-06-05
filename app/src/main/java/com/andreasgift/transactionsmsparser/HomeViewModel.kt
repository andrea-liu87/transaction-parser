package com.andreasgift.transactionsmsparser

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreasgift.transactionparser.TransactionParser
import com.andreasgift.transactionparser.model.SMS
import com.andreasgift.transactionparser.model.Transaction
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
class HomeViewModel : ViewModel() {
    private val TAG = "TPSMS: ViewModel"

    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState

    fun updateTransactions(list: List<Transaction>) {
        _homeState.update {
            it.copy(transactionList = list )
        }
    }

    fun updateSMSData(list: List<SMS>) {
        _homeState.update {
            it.copy(smsList = list )
        }
    }
}