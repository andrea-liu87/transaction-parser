package com.andreasgift.transactionsmsparser

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.andreasgift.transactionsmsparser.data.SMS
import com.andreasgift.transactionsmsparser.data.SMSDatabase
import com.andreasgift.transactionsmsparser.ui.screen.HomeScreen
import com.andreasgift.transactionsmsparser.util.Constant.Companion.ADDRESS
import com.andreasgift.transactionsmsparser.util.Constant.Companion.BODY
import com.andreasgift.transactionsmsparser.util.Constant.Companion.DATE
import com.andreasgift.transactionsmsparser.util.Constant.Companion.INBOX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import live.onedata.vo.tablet.ptt.ui.theme.TransactionParserTheme
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val TAG = "TPSMS : MainActivity"

    val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.READ_SMS
        )

    private val homeViewModel by viewModels<HomeViewModel>()
    val BankCodes = arrayListOf("Liv", "DBS")

    @Inject
    lateinit var db: SMSDatabase

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted) {
                homeViewModel.fetchSMSfromServer(BankCodes)
            } else {
                permissions.forEach {
                        Toast.makeText(
                            this,
                            "${it.key} Permissions not granted by the user.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (allPermissionsGranted()) {
            homeViewModel.fetchSMSfromServer(BankCodes)
        } else {
            permReqLauncher.launch(REQUIRED_PERMISSIONS)
        }

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
