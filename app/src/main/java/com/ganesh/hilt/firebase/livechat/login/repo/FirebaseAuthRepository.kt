package com.ganesh.hilt.firebase.livechat.login.repo

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(private val auth: FirebaseAuth) {

    // Email/Password Sign In
    suspend fun signInWithEmail(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user?.uid ?: "Unknown User")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Phone Sign In
    suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): Result<String> {
        return try {
            val result = auth.signInWithCredential(credential).await()
            Result.success(result.user?.uid ?: "Unknown User")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Google Sign In
    suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            Result.success(result.user?.uid ?: "Unknown User")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Is Signed In
    fun isUserLoggedIn(): Result<Boolean> {
        return try {
            Result.success(auth.currentUser != null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private var verificationId: String? = null

    fun sendVerificationCode(
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks,
        activity: AppCompatActivity
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity) // Set activity inside ViewModel
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    suspend fun verifyOtp(otp: String): Result<String> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
            val result = auth.signInWithCredential(credential).await()
            Result.success(result.user?.uid ?: "Unknown User")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun storeVerificationId(id: String) {
        verificationId = id
    }
}
