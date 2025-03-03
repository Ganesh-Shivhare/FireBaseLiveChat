package com.ganesh.hilt.firebase.livechat.service

import android.app.Application
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.app.RemoteInput
import com.ganesh.hilt.firebase.livechat.MyApplication
import com.ganesh.hilt.firebase.livechat.R
import com.ganesh.hilt.firebase.livechat.data.ChatMessage
import com.ganesh.hilt.firebase.livechat.data.User
import com.ganesh.hilt.firebase.livechat.repo.ChatRepositoryEntryPoint
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors

@AndroidEntryPoint
class ReplyReceiver : BroadcastReceiver() {

    private val chatRepository by lazy {
        EntryPointAccessors.fromApplication(
            (MyApplication.myApplication.applicationContext as Application),
            ChatRepositoryEntryPoint::class.java
        ).getChatRepository()
    }

    override fun onReceive(context: Context, intent: Intent) {
        val senderUserModel = intent.getStringExtra("senderUserModel") ?: "Unknown"

        // Dismiss Notification
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = intent.getIntExtra("notificationId", 0)
        Log.d("TAG_message", "onReceive:notificationId $notificationId")
        notificationManager.cancel(notificationId)
        notificationManager.cancelAll()

        if (senderUserModel.isEmpty()) return
        val senderUser = Gson().fromJson(senderUserModel, User::class.java)

        if (intent.action == "MARK_AS_READ") {
            var markAsRead = false

            Log.d("TAG_message", "onReceive:name ${senderUser.name}")
            Log.d("TAG_message", "onReceive:uid ${senderUser.uid}")
            chatRepository.getMessages(senderUser.uid) {
                if (!markAsRead) {
                    markAsRead = true
                    chatRepository.changeMessageStatus(senderUser.uid, 2, it)
                }
            }
        } else {
            // Get Reply Message
            val remoteInput = RemoteInput.getResultsFromIntent(intent)
            val replyText = remoteInput?.getCharSequence("key_text_reply")?.toString()
            Log.d("TAG_message", "onReceive:replyText $replyText")

            if (!replyText.isNullOrEmpty()) {
                // Send Reply to FireStore or Firebase Database
                Log.d("TAG_message", "onReceive:replyText11 $replyText")

                // Manually get ViewModel using Hilt EntryPoint
                val myUserProfile =
                    context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE)
                        .getString("my_profile_data", "")

                Log.d("TAG_message", "onReceive:myUserProfile $myUserProfile")
                if (myUserProfile.isNullOrEmpty()) return
                val myUser = Gson().fromJson(myUserProfile, User::class.java)
                chatRepository.sendMessage(myUser, senderUser, replyText)

                val chatMessageList = ArrayList<ChatMessage>().apply { add(senderUser.chatMessage) }
                chatRepository.changeMessageStatus(senderUser.uid, 2, chatMessageList)
            }
        }
    }
}
