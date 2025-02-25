package com.ganesh.hilt.firebase.livechat.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserStatusViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    fun updateUserStatus(status: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(userId)

        val statusMap = mapOf(
            "userStatus.status" to status,
            "userStatus.lastSeen" to System.currentTimeMillis()
        )

        userRef.update(statusMap)
            .addOnSuccessListener {
                Log.d("UserStatus", "Updated to $status")
            }
            .addOnFailureListener { e ->
                Log.e("UserStatus", "Failed to update status: ", e)
            }
    }
}
