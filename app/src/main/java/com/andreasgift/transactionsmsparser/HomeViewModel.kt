package com.andreasgift.transactionsmsparser

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreasgift.transactionsmsparser.data.SMS
import com.andreasgift.transactionsmsparser.data.SMSDatabase
import com.andreasgift.transactionsmsparser.data.SMSRepository
import com.andreasgift.transactionsmsparser.util.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class HomeUiState(
    val loading: Boolean = false, // if necessary
    val smsList: List<SMS> = arrayListOf()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: SMSRepository
) : ViewModel() {
    private val TAG = "TPSMS: ViewModel"

    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState

    init {
        fetchSmsFromDB()
    }

    fun fetchSMSfromServer(BankCodes: List<String>){
        viewModelScope.launch {
            repo.fetchSMSfromServer(BankCodes)
        }
    }

    private fun fetchSmsFromDB(){
        viewModelScope.launch {
            val list = repo.fetchSmsFromDB()
            withContext(Dispatchers.Main){
                _homeState.update {
                    it.copy(smsList = list)
                }
            }
        }
    }
}