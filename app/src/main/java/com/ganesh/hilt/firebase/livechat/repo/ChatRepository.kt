package com.ganesh.hilt.firebase.livechat.repo

import android.util.Log
import com.ganesh.hilt.firebase.livechat.MyApplication
import com.ganesh.hilt.firebase.livechat.data.ChatMessage
import com.ganesh.hilt.firebase.livechat.data.User
import com.ganesh.hilt.firebase.livechat.utils.getAccessToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject


class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore, private val auth: FirebaseAuth
) {

    fun sendMessage(senderUser: User, receiverUser: User, messageText: String) {
        val senderId = auth.currentUser?.uid ?: return
        val chatMessage =
            ChatMessage(senderId, receiverUser.uid, messageText, System.currentTimeMillis())

        val chatId =
            if (senderId < receiverUser.uid) "$senderId-${receiverUser.uid}" else "${receiverUser.uid}-$senderId"

        val user = hashMapOf(
            "temp" to System.currentTimeMillis(),
        )

        val temp = firestore.collection("fireChats").document(chatId)
        temp.set(user)
        temp.collection("messages").add(chatMessage).addOnSuccessListener { documentRef ->
            val messageId = documentRef.id  // Get generated document ID

            // Update the message with its document ID
            documentRef.update("messageId", messageId)

            // Send FCM Notification
            senderUser.chatMessage = chatMessage
            sendFCMNotification(senderUser, receiverUser, messageText)
        }
    }

    fun updateMessageReadStatus(receiverId: String, messageList: List<ChatMessage>) {
        val userID = auth.currentUser?.uid ?: return
        messageList.forEach { chatMessage ->
            val senderId = chatMessage.senderId
            if (userID != senderId) {
                Log.d("TAG_update", "updateMessageReadStatus:id ${chatMessage.messageId}")
                Log.d("TAG_update", "updateMessageReadStatus:message ${chatMessage.message}")
                val chatId =
                    if (userID < receiverId) "$userID-$receiverId" else "$receiverId-$userID"

                firestore.collection("fireChats").document(chatId).collection("messages")
                    .document(chatMessage.messageId).update("messageRead", true)
            }
        }
    }

    fun getMessages(receiverId: String, onMessagesReceived: (List<ChatMessage>) -> Unit) {
        val senderId = auth.currentUser?.uid ?: return
        val chatId = if (senderId < receiverId) "$senderId-$receiverId" else "$receiverId-$senderId"
        firestore.collection("fireChats").document(chatId).collection("messages")
            .orderBy("timestamp").addSnapshotListener { snapshot, _ ->
                val messages = snapshot?.documents?.mapNotNull { document ->
                    val message = document.toObject(ChatMessage::class.java)
                    Log.d("TAG", "getMessages:message $message")
                    Log.d("TAG", "getMessages: " + document.id)
                    message?.messageId = document.id // Assign Firestore document ID
                    message
                } ?: emptyList()
                onMessagesReceived(messages)
            }
    }

    private fun sendFCMNotification(senderUser: User, receiverUser: User, message: String) {

        if (receiverUser.userToken.isNullOrEmpty()) return

        val userToken = receiverUser.userToken
        Log.d("TAG_userToken", "sendFCMNotification:userToken $userToken")


        getAccessToken(MyApplication.myApplication) { accessToken ->
            Log.d("TAG_userToken", "sendFCMNotification:accessToken $accessToken")
            CoroutineScope(Dispatchers.IO).launch {
                if (accessToken.isNullOrEmpty()) {
                    println(" Failed to retrieve access token!")
                    return@launch
                }

                val client = OkHttpClient()
                val mediaType = "application/json".toMediaType()

                val payload = JSONObject().apply {
                    put("message", JSONObject().apply {
                        put("token", userToken)
                        put("data", JSONObject().apply {
                            put("title", "New Message")
                            put("senderUserModel", Gson().toJson(senderUser))  // Custom data
                        })
                    })
                }

                val body = payload.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("https://fcm.googleapis.com/v1/projects/hiltfirebaselivechat/messages:send") // Replace with your Firebase Project ID
                    .post(body).addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer $accessToken").build()

                try {
                    val response = client.newCall(request).execute()
                    Log.d("TAG_userToken", "sendFCMNotification:execute ")
                    Log.d("TAG_userToken", "sendFCMNotification:body ${response.body?.string()}")
                } catch (e: Exception) {
                    Log.d("TAG_userToken", "sendFCMNotification:Exception ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }
}
