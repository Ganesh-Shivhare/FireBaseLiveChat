package com.ganesh.hilt.firebase.livechat.service

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ProcessLifecycleOwner
import com.ganesh.hilt.firebase.livechat.MyApplication
import com.ganesh.hilt.firebase.livechat.repo.UserRepositoryEntryPoint
import com.ganesh.hilt.firebase.livechat.utils.Debug
import com.ganesh.hilt.firebase.livechat.utils.PreferenceClass
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors

@AndroidEntryPoint
class UserStatusService : LifecycleService() {
    private val preferenceClass: PreferenceClass by lazy {
        PreferenceClass(this)
    }
    private val userRepository by lazy {
        EntryPointAccessors.fromApplication(
            (MyApplication.myApplication.applicationContext as Application),
            UserRepositoryEntryPoint::class.java
        ).getUserRepository()
    }
    private val fcmToken: String by lazy {
        preferenceClass.getPrefValue("fcmToken", "").toString()
    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                Debug.d("TAG_UserStatusService", "onCreate: ")
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            }

            override fun onStart(owner: LifecycleOwner) {
                Debug.d("TAG_UserStatusService", "onStart: ")
                // Mark user online when app is active
                userRepository.updateUserStatus("online", fcmToken)

                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            }

            override fun onStop(owner: LifecycleOwner) {
                Debug.d("TAG_UserStatusService", "onStop: ")
                // Mark user offline when app is in the background
                userRepository.updateUserStatus("offline", fcmToken)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                Debug.d("TAG_UserStatusService", "onDestroy: ")
                // Mark user offline when app is in the background
                userRepository.updateUserStatus("offline", fcmToken)
            }
        })
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Debug.d("TAG_UserStatusService", "onTaskRemoved: ")
        // Mark user offline when app is in the background
        userRepository.updateUserStatus("offline", fcmToken)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Ensure user is offline when service stops
        userRepository.updateUserStatus("offline", fcmToken)
    }
}
