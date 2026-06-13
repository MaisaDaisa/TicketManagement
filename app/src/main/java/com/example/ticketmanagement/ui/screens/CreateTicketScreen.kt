package com.example.ticketmanagement.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ticketmanagement.data.Ticket

@Composable
fun CreateTicketScreen(
    onTicketCreated: (Ticket) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var seatNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "ახალი ბილეთის რეგისტრაცია",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("სახელი") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("გვარი") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("ელ. ფოსტა") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = seatNumber,
            onValueChange = { seatNumber = it },
            label = { Text("ადგილის ნომერი (უნიკალური)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && seatNumber.isNotBlank()) {
                    onTicketCreated(
                        Ticket(
                            ticketId = "TKT-${(1000..9999).random()}",
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            seatNumber = seatNumber
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("ბილეთის გენერაცია და გაგზავნა", style = MaterialTheme.typography.titleMedium)
        }
    }
}