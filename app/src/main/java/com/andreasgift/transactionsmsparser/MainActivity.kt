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
import com.andreasgift.transactionparser.TransactionParser
import com.andreasgift.transactionsmsparser.data.SMS.SMSDatabase
import com.andreasgift.transactionsmsparser.ui.screen.HomeScreen
import dagger.hilt.android.AndroidEntryPoint
import live.onedata.vo.tablet.ptt.ui.theme.TransactionParserTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val TAG = "TPSMS : MainActivity"

    private val homeViewModel by viewModels<HomeViewModel>()

    @Inject
    lateinit var parser:TransactionParser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
}
