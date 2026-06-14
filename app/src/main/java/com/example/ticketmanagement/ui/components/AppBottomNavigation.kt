package com.example.ticketmanagement.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ticketmanagement.data.UserRole

@Composable
fun AppBottomNavigation(
    currentRole: UserRole,
    currentScreen: String,
    onScreenSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 0.dp
    ) {
        val itemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        NavigationBarItem(
            selected = currentScreen == "scan",
            onClick = { onScreenSelected("scan") },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
            label = {
                Text(
                    text = "სკანერი",
                    fontWeight = if (currentScreen == "scan") FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = itemColors
        )

        if (currentRole == UserRole.ADMIN) {
            NavigationBarItem(
                selected = currentScreen == "create",
                onClick = { onScreenSelected("create") },
                icon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
                label = {
                    Text(
                        text = "შექმნა",
                        fontWeight = if (currentScreen == "create") FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = itemColors
            )

            NavigationBarItem(
                selected = currentScreen == "list",
                onClick = { onScreenSelected("list") },
                icon = { Icon(Icons.Default.List, contentDescription = null) },
                label = {
                    Text(
                        text = "ბილეთები",
                        fontWeight = if (currentScreen == "list") FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = itemColors
            )
        }
    }
}