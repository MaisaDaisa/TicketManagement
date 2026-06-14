package com.example.ticketmanagement.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ticketmanagement.data.UserRole
import com.example.ticketmanagement.ui.navigation.AppScreenRouter
import com.example.ticketmanagement.ui.viewModel.AuthViewModel
import com.example.ticketmanagement.ui.viewModel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticatedAppShell(
    authViewModel: AuthViewModel,
    ticketViewModel: TicketViewModel,
    currentRole: UserRole,
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
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
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