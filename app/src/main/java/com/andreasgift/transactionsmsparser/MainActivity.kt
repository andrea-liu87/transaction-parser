package com.andreasgift.transactionsmsparser

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andreasgift.transactionparser.TransactionParser
import com.andreasgift.transactionsmsparser.ui.screen.HomeScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import live.onedata.vo.tablet.ptt.ui.theme.TransactionParserTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val TAG = "TPSMS : MainActivity"

    private val homeViewModel by viewModels<HomeViewModel>()

    lateinit var parser:TransactionParser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        parser = TransactionParser()
        parser.checkPermission(this)

        setContent {
            TransactionParserTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(viewModel = homeViewModel)
                }
            }
        }
    }

    fun getLastSms(){
        val list = parser.fetchLastSms(this, arrayListOf("Liv"), 12)
        homeViewModel.updateSMSData(list)
    }

    fun getLastMonthsSms(){
        val list = parser.fetchLastMonthsSms(this, arrayListOf("Liv"), 3)
        homeViewModel.updateSMSData(list)
    }

    fun getLastTransactions(){
        CoroutineScope(Dispatchers.IO).launch {
            parser.fetchLastTransactions(
                this@MainActivity,
                arrayListOf("Liv"),
                {homeViewModel.updateTransactions(it)},
            12)
        }
    }

    fun getLastMonthsTransactions(){
        CoroutineScope(Dispatchers.IO).launch {
            parser.fetchTransactionsLastMonthsOf(
                this@MainActivity,
                arrayListOf("Liv"),
                {homeViewModel.updateTransactions(it)},
                3)
        }
    }
}
