package com.ganesh.hilt.firebase.livechat.repo

import com.ganesh.hilt.firebase.livechat.data.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepository @Inject constructor(private val firestore: FirebaseFirestore, private val auth: FirebaseAuth) {

    fun sendMessage(receiverId: String, messageText: String) {
        val senderId = auth.currentUser?.uid ?: return
        val chatMessage = ChatMessage(senderId, receiverId, messageText, System.currentTimeMillis())

        val chatId = if (senderId < receiverId) "$senderId-$receiverId" else "$receiverId-$senderId"
        firestore.collection("chats").document(chatId).collection("messages")
            .add(chatMessage)
    }

    fun getMessages(chatId: String, onMessagesReceived: (List<ChatMessage>) -> Unit) {
        firestore.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                val messages = snapshot?.documents?.mapNotNull { it.toObject(ChatMessage::class.java) } ?: emptyList()
                onMessagesReceived(messages)
            }
    }
}
