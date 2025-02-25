package com.ganesh.hilt.firebase.livechat.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("senderId") @Expose val senderId: String = "",
    @SerializedName("receiverId") @Expose val receiverId: String = "",
    @SerializedName("message") @Expose val message: String = "",
    @SerializedName("timestamp") @Expose val timestamp: Long = System.currentTimeMillis(),
    @SerializedName("messageRead") @Expose var messageRead: Boolean = false,
    @SerializedName("messageId") @Expose var messageId: String = "",
)
