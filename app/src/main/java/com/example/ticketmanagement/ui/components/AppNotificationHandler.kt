package com.example.ticketmanagement.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ticketmanagement.ui.viewModel.AuthViewModel
import com.example.ticketmanagement.ui.viewModel.TicketViewModel

@Composable
fun AppNotificationHandler(
    authViewModel: AuthViewModel,
    ticketViewModel: TicketViewModel,
    context: Context
) {
    val authUiMessage by authViewModel.uiMessage.collectAsState()
    val ticketUiMessage by ticketViewModel.uiMessage.collectAsState()

    LaunchedEffect(authUiMessage) {
        authUiMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            authViewModel.clearMessage()
        }
    }

    LaunchedEffect(ticketUiMessage) {
        ticketUiMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            ticketViewModel.clearMessage()
        }
    }
}