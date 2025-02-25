package com.ganesh.hilt.firebase.livechat.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ganesh.hilt.firebase.livechat.R
import com.ganesh.hilt.firebase.livechat.ui.activity.ChatListActivity
import com.ganesh.hilt.firebase.livechat.utils.FcmUserDetailViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New Token: $token")
        // You can send this new token to your server or Firestore
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCM", "From: ${remoteMessage.from}")

        // Check if the message contains data payload
        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM", "Message Data: ${remoteMessage.data}")
            val senderName = remoteMessage.data["senderName"] ?: "Unknown"
            val title = remoteMessage.data["title"] ?: "New Message"
            val message = remoteMessage.data["message"] ?: "No message"
            val receiverId = remoteMessage.data["receiverId"] ?: return

            FcmUserDetailViewModel.getViewModel()?.let {
                GlobalScope.launch(Dispatchers.Main) {
                    it.listenForUserStatus(receiverId)

                    it.userStatus.observeForever { userStatus ->
                        if (userStatus.status == "online" && userStatus.typingTo == senderName) {
                            Log.d("FCM", "User is online and in chat. Skipping notification.")
                        } else {
                            showNotification(title, message, senderName)
                        }
                    }
                }
            }

            if (FcmUserDetailViewModel.getViewModel() == null) {
                showNotification(title, message, senderName)
            }
        }
    }

    private fun showNotification(title: String, message: String, senderName: String) {


        val channelId = "chat_notifications"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Chat Notifications", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for chat messages"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, ChatListActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE
        )

        val notificationBuilder =
            NotificationCompat.Builder(this, channelId).setSmallIcon(R.drawable.ic_message)
                .setContentTitle("$senderName: $title").setContentText(message).setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(pendingIntent)

        notificationManager.notify(101, notificationBuilder.build())
    }
}


