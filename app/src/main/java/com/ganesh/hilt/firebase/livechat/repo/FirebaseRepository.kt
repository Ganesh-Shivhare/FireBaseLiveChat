package com.ganesh.hilt.firebase.livechat.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ganesh.hilt.firebase.livechat.utils.Debug
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor(private val firebaseMessaging: FirebaseMessaging) {

    fun getToken(): LiveData<String> {
        val tokenLiveData = MutableLiveData<String>()
        firebaseMessaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                tokenLiveData.value = task.result
            } else {
                Debug.e("FirebaseRepository", "Fetching FCM token failed  ${task.exception}")
            }
        }
        return tokenLiveData
    }
}