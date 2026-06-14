package com.example.ticketmanagement.ui.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketmanagement.data.Ticket
import com.example.ticketmanagement.data.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TicketViewModel : ViewModel() {
    private val repository = TicketRepository()

    val ticketList = mutableStateListOf<Ticket>()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage

    init {
        observeTickets()
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
        viewModelScope.launch {
            _isSaving.value = true
            _uiMessage.value = null

            val result = repository.createTicket(ticket)

            _isSaving.value = false
            if (result.isSuccess) {
                _uiMessage.value = "ბილეთი წარმატებით შეიქმნა!"
                onSuccess()
            } else {
                _uiMessage.value = result.exceptionOrNull()?.message ?: "დაფიქსირდა შეცდომა"
            }
        }
    }

    fun clearMessage() {
        _uiMessage.value = null
    }
}