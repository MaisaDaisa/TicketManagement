package com.example.ticketmanagement.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getCurrentUserEmail(): String? = auth.currentUser?.email
    fun getCurrentUserId(): String? = auth.currentUser?.uid
    fun signOut() = auth.signOut()

    suspend fun signIn(email: String, password: String): String? {
        val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
        return authResult.user?.email
    }

    suspend fun getUserRole(uid: String): UserRole? {
        val userDoc = db.collection("users").document(uid).get().await()
        if (userDoc.exists()) {
            val roleString = userDoc.getString("role")
            return when (roleString) {
                "ADMIN" -> UserRole.ADMIN
                "HELPER" -> UserRole.HELPER
                else -> null
            }
        }
        return null
    }
}