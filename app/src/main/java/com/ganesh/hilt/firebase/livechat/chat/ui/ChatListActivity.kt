package com.ganesh.hilt.firebase.livechat.chat.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ganesh.hilt.firebase.livechat.databinding.ActivityChatListBinding

class ChatListActivity : AppCompatActivity() {

    private val binding: ActivityChatListBinding by lazy {
        ActivityChatListBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
}