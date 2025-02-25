package com.ganesh.hilt.firebase.livechat.service

import android.content.Intent
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserStatusService : LifecycleService() {

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                Log.d("TAG_UserStatusService", "onStart: ")
                updateUserStatus("online") // Mark user online when app is active
            }

            override fun onStop(owner: LifecycleOwner) {
                Log.d("TAG_UserStatusService", "onStop: ")
                updateUserStatus("offline") // Mark user offline when app is in the background
            }

            override fun onDestroy(owner: LifecycleOwner) {
                Log.d("TAG_UserStatusService", "onDestroy: ")
                updateUserStatus("offline") // Mark user offline when app is in the background
            }
        })
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d("TAG_UserStatusService", "onTaskRemoved: ")
        updateUserStatus("offline") // Mark user offline when app is in the background
    }

    override fun onDestroy() {
        super.onDestroy()
        updateUserStatus("offline") // Ensure user is offline when service stops
    }

    private fun updateUserStatus(status: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)

        val statusMap = mapOf(
            "userStatus.status" to status,
            "userStatus.lastSeen" to System.currentTimeMillis()
        )

        userRef.update(statusMap)
            .addOnSuccessListener {
                Log.d("TAG_UserStatusService", "Updated to $status")
            }
            .addOnFailureListener { e ->
                Log.e("TAG_UserStatusService", "Failed to update status: ", e)
            }
    }
}
