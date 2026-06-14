package com.example.ticketmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ticketmanagement.data.UserRole
import com.example.ticketmanagement.ui.components.AppBottomNavigation
import com.example.ticketmanagement.ui.screens.CreateTicketScreen
import com.example.ticketmanagement.ui.screens.ScanScreen
import com.example.ticketmanagement.ui.screens.TicketListScreen
import com.example.ticketmanagement.ui.viewModel.TicketViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TicketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(viewModel: TicketViewModel) {
    var currentRole by remember { mutableStateOf(UserRole.ADMIN) }
    var currentScreen by remember { mutableStateOf("scan") }

    val isSaving by viewModel.isSaving.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    val liveTickets = viewModel.ticketList

    Scaffold(
        bottomBar = {
            AppBottomNavigation(
                currentRole = currentRole,
                currentScreen = currentScreen,
                onScreenSelected = { currentScreen = it }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                "scan" -> ScanScreen(onScanTriggered = {})
                "create" -> if (currentRole == UserRole.ADMIN) {
                    CreateTicketScreen(
                        isSaving = isSaving,
                        uiMessage = uiMessage,
                        onClearMessage = { viewModel.clearMessage() },
                        onTicketCreated = { newTicket ->
                            viewModel.createTicket(newTicket, onSuccess = {
                                currentScreen = "list"
                            })
                        }
                    )
                }
                "list" -> TicketListScreen(tickets = liveTickets)
            }
        }
    }
}