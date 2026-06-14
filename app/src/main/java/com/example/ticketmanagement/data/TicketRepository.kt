package com.example.ticketmanagement.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TicketRepository {
    private val db = FirebaseFirestore.getInstance()
    private val ticketsCollection = db.collection("tickets")


    suspend fun createTicket(ticket: Ticket): Result<Unit> {
        return try {
            val existingSeatQuery = ticketsCollection
                .whereEqualTo("seatNumber", ticket.seatNumber)
                .get()
                .await()

            if (!existingSeatQuery.isEmpty) {
                return Result.failure(Exception("ეს ადგილი უკვე დაკავებულია!"))
            }

            val generatedUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=${ticket.ticketId}"
            val ticketWithQr = ticket.copy(qrCodeUrl = generatedUrl)

            ticketsCollection.document(ticketWithQr.ticketId).set(ticketWithQr).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    fun getTicketsRealtime(): Flow<List<Ticket>> = callbackFlow {
        val listener = ticketsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val ticketList = snapshot.toObjects(Ticket::class.java)
                trySend(ticketList)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun validateTicket(ticketId: String): Result<String> {
        return try {
            val documentRef = ticketsCollection.document(ticketId)
            val snapshot = documentRef.get().await()

            if (!snapshot.exists()) {
                return Result.failure(Exception("ბილეთი ვერ მოიძებნა!"))
            }

            val isScanned = snapshot.getBoolean("isScanned") ?: false
            if (isScanned) {
                val firstName = snapshot.getString("firstName") ?: ""
                val lastName = snapshot.getString("lastName") ?: ""
                return Result.failure(Exception("ბილეთი უკვე გამოყენებულია! ($firstName $lastName)"))
            }

            documentRef.update("isScanned", true).await()

            val firstName = snapshot.getString("firstName") ?: ""
            val lastName = snapshot.getString("lastName") ?: ""
            Result.success("ბილეთი ვალიდურია! მომხმარებელი: $firstName $lastName")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}