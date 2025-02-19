package com.ganesh.hilt.firebase.livechat.repo

import com.ganesh.hilt.firebase.livechat.data.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore, private val auth: FirebaseAuth
) {

    fun sendMessage(receiverId: String, messageText: String) {
        val senderId = auth.currentUser?.uid ?: return
        val chatMessage =
            ChatMessage(senderId, receiverId, messageText, System.currentTimeMillis())

        val chatId = if (senderId < receiverId) "$senderId-$receiverId" else "$receiverId-$senderId"

        val user = hashMapOf(
            "temp" to System.currentTimeMillis(),
        )

        val temp = firestore.collection("fireChats").document(chatId)
        temp.set(user)
        temp.collection("messages").add(chatMessage)
    }

    fun getMessages(receiverId: String, onMessagesReceived: (List<ChatMessage>) -> Unit) {
        val senderId = auth.currentUser?.uid ?: return
        val chatId = if (senderId < receiverId) "$senderId-$receiverId" else "$receiverId-$senderId"
        firestore.collection("fireChats").document(chatId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                val messages =
                    snapshot?.documents?.mapNotNull { it.toObject(ChatMessage::class.java) }
                        ?: emptyList()
                onMessagesReceived(messages)
            }
    }
}
