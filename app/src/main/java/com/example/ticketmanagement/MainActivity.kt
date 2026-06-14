package com.example.ticketmanagement

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
                MainAppContainer(authViewModel, ticketViewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(
    authViewModel: AuthViewModel,
    ticketViewModel: TicketViewModel
) {
    val userEmail = authViewModel.currentUserEmail
    val currentRole = authViewModel.currentUserRole
    val context = LocalContext.current

    AppNotificationHandler(authViewModel, ticketViewModel, context)

    LaunchedEffect(currentRole) {
        if (currentRole == UserRole.ADMIN) {
            ticketViewModel.observeTickets()
        }
    }

    if (userEmail == null) {
        LoginScreen(
            isAuthenticating = authViewModel.isAuthenticating,
            onSignInClick = { email, password ->
                authViewModel.signInWithEmail(email, password) { role ->
                    if (role == UserRole.ADMIN) ticketViewModel.observeTickets()
                }
            }
        )
    } else {
        AuthenticatedAppShell(
            authViewModel = authViewModel,
            ticketViewModel = ticketViewModel,
            currentRole = currentRole ?: UserRole.HELPER,
            userEmail = userEmail
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticatedAppShell(
    authViewModel: AuthViewModel,
    ticketViewModel: TicketViewModel,
    currentRole: UserRole,
    userEmail: String
) {
    var currentScreen by remember { mutableStateOf("scan") }

    val screenTitle = when (currentScreen) {
        "scan" -> "სკანირება"
        "create" -> "შექმნა"
        "list" -> "ბილეთების სია"
        else -> "მენეჯმენტი"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = screenTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (currentRole == UserRole.ADMIN) "ადმინისტრატორი" else "დამხმარე",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            authViewModel.signOut {
                                ticketViewModel.clearTicketList()
                                ticketViewModel.clearScanResult()
                                currentScreen = "scan"
                            }
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "გამოსვლა",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            AppBottomNavigation(
                currentRole = currentRole,
                currentScreen = currentScreen,
                onScreenSelected = { currentScreen = it }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AppScreenRouter(currentScreen, currentRole, ticketViewModel)
        }
    }
}

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

@Composable
fun AppNotificationHandler(
    authViewModel: AuthViewModel,
    ticketViewModel: TicketViewModel,
    context: android.content.Context
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