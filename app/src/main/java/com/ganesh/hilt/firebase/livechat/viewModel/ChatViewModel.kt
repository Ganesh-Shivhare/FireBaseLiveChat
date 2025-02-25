package com.ganesh.hilt.firebase.livechat.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganesh.hilt.firebase.livechat.data.ChatMessage
import com.ganesh.hilt.firebase.livechat.data.User
import com.ganesh.hilt.firebase.livechat.repo.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: ChatRepository) :
    ViewModel() {

    fun sendMessage(senderUser: User, receiverUser: User, messageText: String) {
        viewModelScope.launch {
            repository.sendMessage(senderUser, receiverUser, messageText)
        }
    }

    fun getMessages(receiverId: String, onMessagesReceived: (List<ChatMessage>) -> Unit) {
        viewModelScope.launch {
            repository.getMessages(receiverId, onMessagesReceived)
        }
    }

    fun updateMessageReadStatus(uid: String, messageList: List<ChatMessage>) {
        viewModelScope.launch {
            repository.updateMessageReadStatus(uid, messageList)
        }
    }
}