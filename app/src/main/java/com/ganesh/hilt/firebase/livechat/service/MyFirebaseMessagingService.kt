package com.ganesh.hilt.firebase.livechat.service

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.ganesh.hilt.firebase.livechat.MyApplication
import com.ganesh.hilt.firebase.livechat.R
import com.ganesh.hilt.firebase.livechat.data.User
import com.ganesh.hilt.firebase.livechat.repo.UserRepositoryEntryPoint
import com.ganesh.hilt.firebase.livechat.ui.activity.ChatListActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val userRepository by lazy {
        EntryPointAccessors.fromApplication(
            (MyApplication.myApplication.applicationContext as Application),
            UserRepositoryEntryPoint::class.java
        ).getUserRepository()
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("TAG_message", "New Token: $token")
        // You can send this new token to your server or Firestore
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("TAG_message", "From: ${remoteMessage.from}")

        // Check if the message contains data payload
        remoteMessage.data.isNotEmpty().let {
            Log.d("TAG_message", "Message Data: ${remoteMessage.data}")
            val senderUserModel = remoteMessage.data["senderUserModel"] ?: ""
            Log.d("TAG_message", "Message Data:senderUserModel $senderUserModel")

            if (senderUserModel.isEmpty()) return
            val senderUser = Gson().fromJson(senderUserModel, User::class.java)

            userRepository.getMyUserUpdates() { currentUser ->
                Log.d("TAG_message", "getViewModel: userName " + currentUser.name)
                GlobalScope.launch(Dispatchers.Main) {
                    Log.d("TAG_message", "getViewModel:senderUser_uid " + senderUser.uid)

                    Log.d("TAG_message", "observeForever:status " + currentUser.userStatus.status)
                    if (currentUser.userStatus.status == "online" /*&& currentUser.userStatus.typingTo == senderUser.uid*/) {
                        Log.d(
                            "TAG_message", "User is online and in chat. Skipping notification."
                        )
                    } else {
                        showNotification(senderUser)
                    }
                }
            }
        }
    }

    private fun showNotification(senderUser: User) {

        Log.d("TAG_message", "showNotification: ${senderUser.chatMessage.message}")

        val notificationId = System.currentTimeMillis().toInt()
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


        // 🔹 Create the RemoteInput (Text Input Action)
        val remoteInput = RemoteInput.Builder("key_text_reply").setLabel("Reply…").build()

        // 🔹 Create PendingIntent for the Reply Action
        val replyIntent = Intent(this, ReplyReceiver::class.java).apply {
            putExtra("notificationId", notificationId)
            putExtra("senderUserModel", Gson().toJson(senderUser))
        }

        val replyPendingIntent = PendingIntent.getBroadcast(
            this, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        // 🔹 Create the Reply Action
        val replyAction = NotificationCompat.Action.Builder(
            R.drawable.ic_reply, "Reply", replyPendingIntent
        ).addRemoteInput(remoteInput).build()

        val notificationBuilder =
            NotificationCompat.Builder(this, channelId).setSmallIcon(R.drawable.ic_message)
                .setContentTitle("${senderUser.name}: New Message")
                .setContentText(senderUser.chatMessage.message)
                .addAction(replyAction) // 🔹 Add reply action
                .setAutoCancel(true).setSilent(false).setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}


