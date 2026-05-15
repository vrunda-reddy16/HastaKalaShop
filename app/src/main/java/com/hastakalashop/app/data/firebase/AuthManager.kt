package com.hastakalashop.app.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    private val auth: FirebaseAuth
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val uid: String?
        get() = auth.currentUser?.uid

    suspend fun ensureSignedIn(): String {
        val existing = auth.currentUser
        if (existing != null) return existing.uid
        val result = auth.signInAnonymously().await()
        return result.user?.uid ?: throw IllegalStateException("Anonymous sign-in failed")
    }
}