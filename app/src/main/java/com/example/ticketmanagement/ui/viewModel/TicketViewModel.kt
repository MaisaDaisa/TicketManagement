package com.example.ticketmanagement.ui.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketmanagement.data.Ticket
import com.example.ticketmanagement.data.TicketRepository
import com.example.ticketmanagement.data.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TicketViewModel : ViewModel() {
    private val repository = TicketRepository()
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var currentUserEmail by mutableStateOf<String?>(auth.currentUser?.email)
    var currentUserRole by mutableStateOf<UserRole?>(null)
    var isAuthenticating by mutableStateOf(false)

    val ticketList = mutableStateListOf<Ticket>()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage

    init {
        auth.currentUser?.email?.let { email ->
            checkUserRole(email)
        }
    }

    fun signInWithEmail(emailInput: String, passwordInput: String) {
        if (emailInput.isBlank() || passwordInput.isBlank()) {
            _uiMessage.value = "გთხოვთ შეავსოთ ყველა ველი"
            return
        }

        viewModelScope.launch {
            isAuthenticating = true
            _uiMessage.value = null
            try {
                val authResult = auth.signInWithEmailAndPassword(emailInput.trim(), passwordInput).await()
                val email = authResult.user?.email

                currentUserEmail = email
                if (email != null) {
                    checkUserRole(email)
                }
            } catch (e: Exception) {
                _uiMessage.value = "არასწორი მეილი ან პაროლი!"
                isAuthenticating = false
            }
        }
    }

    private fun checkUserRole(email: String) {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            isAuthenticating = false
            return
        }

        viewModelScope.launch {
            try {
                val userDoc = db.collection("users").document(uid).get().await()

                if (userDoc.exists()) {
                    val roleString = userDoc.getString("role")
                    currentUserRole = when (roleString) {
                        "ADMIN" -> UserRole.ADMIN
                        "HELPER" -> UserRole.HELPER
                        else -> null
                    }
                } else {
                    currentUserRole = null
                    _uiMessage.value = "თქვენი მომხმარებელი ბაზაში არ მოიძებნა!"
                }

                if (currentUserRole == UserRole.ADMIN) {
                    observeTickets()
                }
            } catch (e: Exception) {
                _uiMessage.value = "ბაზიდან როლის წაკითხვის შეცდომა: ${e.localizedMessage}"
            } finally {
                isAuthenticating = false
            }
        }
    }

    private fun observeTickets() {
        viewModelScope.launch {
            repository.getTicketsRealtime().collect { updatedList ->
                ticketList.clear()
                ticketList.addAll(updatedList)
            }
        }
    }

    fun createTicket(ticket: Ticket, onSuccess: () -> Unit) {
        if (currentUserRole != UserRole.ADMIN) {
            _uiMessage.value = "ბილეთის შექმნის უფლება არ გაქვთ!"
            return
        }
        viewModelScope.launch {
            _isSaving.value = true
            val result = repository.createTicket(ticket)
            _isSaving.value = false
            if (result.isSuccess) {
                _uiMessage.value = "ბილეთი წარმატებით შეიქმნა!"
                onSuccess()
            } else {
                _uiMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun signOut() {
        auth.signOut()
        currentUserEmail = null
        currentUserRole = null
        ticketList.clear()
    }

    fun clearMessage() { _uiMessage.value = null }
}