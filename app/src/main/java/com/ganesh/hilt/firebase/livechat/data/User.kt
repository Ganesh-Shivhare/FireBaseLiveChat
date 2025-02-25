package com.ganesh.hilt.firebase.livechat.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("uid") @Expose var uid: String = "",
    @SerializedName("name") @Expose var name: String = "",
    @SerializedName("phoneNumber") @Expose var phoneNumber: String = "",
    @SerializedName("email") @Expose var email: String = "",
    @SerializedName("avatarImagePath") @Expose var avatarImagePath: String = "",
    @SerializedName("chatMessage") @Expose var chatMessage: ChatMessage = ChatMessage(),
    @SerializedName("unreadMessageCount") @Expose var unreadMessageCount: Int = 0,
    @SerializedName("UserStatus") @Expose var userStatus: UserStatus = UserStatus(),
)


data class UserStatus(
    @SerializedName("status") @Expose val status: String = "offline",
    @SerializedName("lastSeen") @Expose val lastSeen: Long = -1,
    @SerializedName("typing") @Expose val typing: Boolean = false,
    @SerializedName("typingTo") @Expose val typingTo: String? = null
)
