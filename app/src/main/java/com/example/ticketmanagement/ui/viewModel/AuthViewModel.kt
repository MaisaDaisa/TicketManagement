package com.example.ticketmanagement.ui.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketmanagement.data.AuthRepository
import com.example.ticketmanagement.data.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    var currentUserEmail by mutableStateOf<String?>(authRepository.getCurrentUserEmail())
    var currentUserRole by mutableStateOf<UserRole?>(null)
    var isAuthenticating by mutableStateOf(false)

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage

    init {
        authRepository.getCurrentUserEmail()?.let {
            checkUserRole()
        }
    }

    fun signInWithEmail(emailInput: String, passwordInput: String, onSuccess: (UserRole) -> Unit) {
        if (emailInput.isBlank() || passwordInput.isBlank()) {
            _uiMessage.value = "გთხოვთ შეავსოთ ყველა ველი"
            return
        }

        viewModelScope.launch {
            isAuthenticating = true
            _uiMessage.value = null
            try {
                val email = authRepository.signIn(emailInput, passwordInput)
                currentUserEmail = email
                if (email != null) {
                    val uid = authRepository.getCurrentUserId()
                    if (uid != null) {
                        val role = authRepository.getUserRole(uid)
                        if (role != null) {
                            currentUserRole = role
                            onSuccess(role)
                        } else {
                            _uiMessage.value = "თქვენი მომხმარებელი ბაზაში არ მოიძებნა!"
                        }
                    }
                }
            } catch (e: Exception) {
                _uiMessage.value = "არასწორი მეილი ან პაროლი!"
            } finally {
                isAuthenticating = false
            }
        }
    }

    private fun checkUserRole() {
        val uid = authRepository.getCurrentUserId()
        if (uid == null) return

        viewModelScope.launch {
            try {
                currentUserRole = authRepository.getUserRole(uid)
            } catch (e: Exception) {
                _uiMessage.value = "ბაზიდან როლის წაკითხვის შეცდომა: ${e.localizedMessage}"
            }
        }
    }

    fun signOut(onSignOutComplete: () -> Unit) {
        authRepository.signOut()
        currentUserEmail = null
        currentUserRole = null
        onSignOutComplete()
    }

    fun clearMessage() { _uiMessage.value = null }
}