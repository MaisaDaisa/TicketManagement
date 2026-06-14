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

            ticketsCollection.document(ticket.ticketId).set(ticket).await()
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
                trySend(ticketList) // Sends updated list to the UI
            }
        }
        awaitClose { listener.remove() }
    }
}