package com.example.ticketmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.ticketmanagement.data.UserRole
import com.example.ticketmanagement.ui.components.AppNotificationHandler
import com.example.ticketmanagement.ui.components.AuthenticatedAppShell
import com.example.ticketmanagement.ui.screens.LoginScreen
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
    ticketViewModel: TicketViewModel,
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
            currentRole = currentRole ?: UserRole.HELPER
        )
    }
}