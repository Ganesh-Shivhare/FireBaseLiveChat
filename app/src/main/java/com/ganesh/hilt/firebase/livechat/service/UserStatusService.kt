package com.ganesh.hilt.firebase.livechat.service

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ProcessLifecycleOwner
import com.ganesh.hilt.firebase.livechat.MyApplication
import com.ganesh.hilt.firebase.livechat.R
import com.ganesh.hilt.firebase.livechat.repo.UserRepositoryEntryPoint
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors

@AndroidEntryPoint
class UserStatusService : LifecycleService() {
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
    }
    private val userRepository by lazy {
        EntryPointAccessors.fromApplication(
            (MyApplication.myApplication.applicationContext as Application),
            UserRepositoryEntryPoint::class.java
        ).getUserRepository()
    }
    private val fcmToken by lazy {
        sharedPreferences.getString("fcmToken", "") ?: ""
    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                Log.d("TAG_UserStatusService", "onCreate: ")
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            }

            override fun onStart(owner: LifecycleOwner) {
                Log.d("TAG_UserStatusService", "onStart: ")
                // Mark user online when app is active
                userRepository.updateUserStatus("online", fcmToken)

                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            }

            override fun onStop(owner: LifecycleOwner) {
                Log.d("TAG_UserStatusService", "onStop: ")
                // Mark user offline when app is in the background
                userRepository.updateUserStatus("offline", fcmToken)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                Log.d("TAG_UserStatusService", "onDestroy: ")
                // Mark user offline when app is in the background
                userRepository.updateUserStatus("offline", fcmToken)
            }
        })
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d("TAG_UserStatusService", "onTaskRemoved: ")
        // Mark user offline when app is in the background
        userRepository.updateUserStatus("offline", fcmToken)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Ensure user is offline when service stops
        userRepository.updateUserStatus("offline", fcmToken)
    }
}
