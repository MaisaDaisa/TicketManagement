package com.example.ticketmanagement

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ticketmanagement.data.UserRole
import com.example.ticketmanagement.ui.components.AppBottomNavigation
import com.example.ticketmanagement.ui.screens.CreateTicketScreen
import com.example.ticketmanagement.ui.screens.LoginScreen
import com.example.ticketmanagement.ui.screens.ScanScreen
import com.example.ticketmanagement.ui.screens.TicketListScreen
import com.example.ticketmanagement.ui.viewModel.AuthViewModel
import com.example.ticketmanagement.ui.viewModel.TicketViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val ticketViewModel: TicketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                MainAppContainer(
                    authViewModel = authViewModel,
                    ticketViewModel = ticketViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(
    authViewModel: AuthViewModel,
    ticketViewModel: TicketViewModel
) {
    var currentScreen by remember { mutableStateOf("scan") }

    val isSaving by ticketViewModel.isSaving.collectAsState()
    val ticketUiMessage by ticketViewModel.uiMessage.collectAsState()
    val scanResult by ticketViewModel.scanResult.collectAsState()
    val isScanSuccess by ticketViewModel.isScanSuccess.collectAsState()
    val liveTickets = ticketViewModel.ticketList

    val authUiMessage by authViewModel.uiMessage.collectAsState()
    val userEmail = authViewModel.currentUserEmail
    val currentRole = authViewModel.currentUserRole
    val isAuthenticating = authViewModel.isAuthenticating

    val context = LocalContext.current

    LaunchedEffect(currentRole) {
        if (currentRole == UserRole.ADMIN) {
            ticketViewModel.observeTickets()
        }
    }

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

    if (userEmail == null) {
        LoginScreen(
            isAuthenticating = isAuthenticating,
            onSignInClick = { email, password ->
                authViewModel.signInWithEmail(email, password, onSuccess = { role ->
                    if (role == UserRole.ADMIN) {
                        ticketViewModel.observeTickets()
                    }
                })
            }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ბილეთების მენეჯმენტი") },
                    actions = {
                        IconButton(onClick = {
                            authViewModel.signOut(onSignOutComplete = {
                                ticketViewModel.clearTicketList()
                                ticketViewModel.clearScanResult()
                                currentScreen = "scan"
                            })
                        }) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Sign Out",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            },
            bottomBar = {
                AppBottomNavigation(
                    currentRole = currentRole ?: UserRole.HELPER,
                    currentScreen = currentScreen,
                    onScreenSelected = { currentScreen = it }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentScreen) {
                    "scan" -> ScanScreen(
                        scanResult = scanResult,
                        isScanSuccess = isScanSuccess,
                        onScanTriggered = { qrContent ->
                            ticketViewModel.checkAndValidateTicket(qrContent)
                        }
                    )
                    "create" -> if (currentRole == UserRole.ADMIN) {
                        CreateTicketScreen(
                            isSaving = isSaving,
                            uiMessage = ticketUiMessage,
                            onClearMessage = { ticketViewModel.clearMessage() },
                            onTicketCreated = { newTicket ->
                                ticketViewModel.createTicket(newTicket, onSuccess = {
                                    currentScreen = "list"
                                })
                            }
                        )
                    }
                    "list" -> if (currentRole == UserRole.ADMIN) {
                        TicketListScreen(tickets = liveTickets)
                    }
                }
            }
        }
    }
}