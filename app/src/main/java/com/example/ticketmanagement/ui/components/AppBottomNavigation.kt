package com.example.ticketmanagement.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.ticketmanagement.data.UserRole

@Composable
fun AppBottomNavigation(
    currentRole: UserRole,
    currentScreen: String,
    onScreenSelected: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == "scan",
            onClick = { onScreenSelected("scan") },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
            label = { Text("სკანერი") }
        )

        if (currentRole == UserRole.ADMIN) {
            NavigationBarItem(
                selected = currentScreen == "create",
                onClick = { onScreenSelected("create") },
                icon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
                label = { Text("შექმნა") }
            )
        }

        NavigationBarItem(
            selected = currentScreen == "list",
            onClick = { onScreenSelected("list") },
            icon = { Icon(Icons.Default.List, contentDescription = null) },
            label = { Text("ბილეთები") }
        )
    }
}