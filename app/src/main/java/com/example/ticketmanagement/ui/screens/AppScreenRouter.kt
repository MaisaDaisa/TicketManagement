package com.example.ticketmanagement.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ticketmanagement.data.UserRole
import com.example.ticketmanagement.ui.screens.CreateTicketScreen
import com.example.ticketmanagement.ui.screens.ScanScreen
import com.example.ticketmanagement.ui.screens.TicketListScreen
import com.example.ticketmanagement.ui.viewModel.TicketViewModel

@Composable
fun AppScreenRouter(
    currentScreen: String,
    currentRole: UserRole,
    ticketViewModel: TicketViewModel
) {
    val isSaving by ticketViewModel.isSaving.collectAsState()
    val ticketUiMessage by ticketViewModel.uiMessage.collectAsState()
    val scanResult by ticketViewModel.scanResult.collectAsState()
    val isScanSuccess by ticketViewModel.isScanSuccess.collectAsState()
    val liveTickets = ticketViewModel.ticketList

    when (currentScreen) {
        "scan" -> ScanScreen(
            scanResult = scanResult,
            isScanSuccess = isScanSuccess,
            onScanTriggered = { qrContent -> ticketViewModel.checkAndValidateTicket(qrContent) }
        )
        "create" -> if (currentRole == UserRole.ADMIN) {
            CreateTicketScreen(
                isSaving = isSaving,
                uiMessage = ticketUiMessage,
                onClearMessage = { ticketViewModel.clearMessage() },
                onTicketCreated = { newTicket ->
                    ticketViewModel.createTicket(newTicket) { }
                }
            )
        }
        "list" -> if (currentRole == UserRole.ADMIN) {
            TicketListScreen(tickets = liveTickets)
        }
    }
}