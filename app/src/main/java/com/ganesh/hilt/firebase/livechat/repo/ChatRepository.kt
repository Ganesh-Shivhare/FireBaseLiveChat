package com.ganesh.hilt.firebase.livechat.repo

import android.util.Log
import com.ganesh.hilt.firebase.livechat.data.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore, private val auth: FirebaseAuth
) {

    fun sendMessage(receiverId: String, messageText: String) {
        val senderId = auth.currentUser?.uid ?: return
        val chatMessage = ChatMessage(senderId, receiverId, messageText, System.currentTimeMillis())

        val chatId = if (senderId < receiverId) "$senderId-$receiverId" else "$receiverId-$senderId"

        val user = hashMapOf(
            "temp" to System.currentTimeMillis(),
        )

        val temp = firestore.collection("fireChats").document(chatId)
        temp.set(user)
        temp.collection("messages").add(chatMessage).addOnSuccessListener { documentRef ->
            val messageId = documentRef.id  // Get generated document ID

            // Update the message with its document ID
            documentRef.update("messageId", messageId)
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

                firestore.collection("fireChats").document(chatId)
                    .collection("messages").document(chatMessage.messageId)
                    .update("messageRead", true)
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
}
