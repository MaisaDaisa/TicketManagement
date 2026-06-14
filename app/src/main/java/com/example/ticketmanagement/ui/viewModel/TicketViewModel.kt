package com.example.ticketmanagement.ui.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketmanagement.data.Ticket
import com.example.ticketmanagement.data.TicketRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TicketViewModel : ViewModel() {
    private val repository = TicketRepository()
    private val db = FirebaseFirestore.getInstance()

    val ticketList = mutableStateListOf<Ticket>()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage

    private val _scanResult = MutableStateFlow<String?>(null)
    val scanResult: StateFlow<String?> = _scanResult

    private val _isScanSuccess = MutableStateFlow<Boolean?>(null)
    val isScanSuccess: StateFlow<Boolean?> = _isScanSuccess

    private val _isValidating = MutableStateFlow(false)
    val isValidating: StateFlow<Boolean> = _isValidating

    fun observeTickets() {
        viewModelScope.launch {
            repository.getTicketsRealtime().collect { updatedList ->
                ticketList.clear()
                ticketList.addAll(updatedList)
            }
        }
    }

    fun createTicket(ticket: Ticket, onSuccess: () -> Unit) {
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

    fun checkAndValidateTicket(ticketId: String) {
        viewModelScope.launch {
            _isValidating.value = true
            try {
                val documentRef = db.collection("tickets").document(ticketId)
                val snapshot = documentRef.get().await()

                if (!snapshot.exists()) {
                    _isScanSuccess.value = false
                    _scanResult.value = "ბილეთი ($ticketId) ბაზაში ვერ მოიძებნა!"
                    return@launch
                }

                val isScanned = snapshot.getBoolean("isScanned") ?: false
                val firstName = snapshot.getString("firstName") ?: ""
                val lastName = snapshot.getString("lastName") ?: ""

                if (isScanned) {
                    _isScanSuccess.value = false
                    _scanResult.value = "ეს ბილეთი უკვე გამოყენებულია!\nმფლობელი: $firstName $lastName"
                } else {
                    documentRef.update("isScanned", true).await()
                    _isScanSuccess.value = true
                    _scanResult.value = "ბილეთი ვალიდურია!\nწარმატებით გატარდა: $firstName $lastName"
                }
            } catch (e: Exception) {
                _isScanSuccess.value = false
                _scanResult.value = "შეცდომა: ${e.localizedMessage}"
            } finally {
                _isValidating.value = false
            }
        }
    }

    fun clearScanResult() {
        _scanResult.value = null
        _isScanSuccess.value = null
    }

    fun clearTicketList() {
        ticketList.clear()
    }

    fun clearMessage() { _uiMessage.value = null }
}