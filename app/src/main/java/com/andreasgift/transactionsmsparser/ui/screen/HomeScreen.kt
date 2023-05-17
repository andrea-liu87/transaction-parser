package com.andreasgift.transactionsmsparser.ui.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.andreasgift.transactionsmsparser.HomeViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val homeUiState by viewModel.homeState.collectAsStateWithLifecycle()

    LazyColumn {
        items(items = homeUiState.smsList) { sms ->
            Text("${sms.sender}: ${sms.content} on ${sms.date}\n", color = Color.White)
        }
    }
}