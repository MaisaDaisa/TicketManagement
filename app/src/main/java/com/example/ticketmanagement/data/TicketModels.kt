package com.example.ticketmanagement.data

enum class UserRole {
    ADMIN,
    HELPER
}

data class Ticket(
    val ticketId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val seatNumber: String = "",
    val email: String = "",
    @field:JvmField
    val isScanned: Boolean = false,
    val qrCodeUrl: String = ""
)
