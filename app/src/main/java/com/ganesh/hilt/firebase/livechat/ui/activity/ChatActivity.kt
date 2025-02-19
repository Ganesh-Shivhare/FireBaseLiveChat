package com.ganesh.hilt.firebase.livechat.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ganesh.hilt.firebase.livechat.R
import com.ganesh.hilt.firebase.livechat.data.User
import com.ganesh.hilt.firebase.livechat.databinding.ActivityChatBinding
import com.ganesh.hilt.firebase.livechat.ui.BaseActivity
import com.ganesh.hilt.firebase.livechat.ui.adapter.MessageListAdapter
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : BaseActivity() {
    private var isUserAtBottom: Boolean = true
    private val binding by lazy { ActivityChatBinding.inflate(layoutInflater) }
    private lateinit var currentUserData: User
    private lateinit var receiverUserData: User
    private val messageListAdapter by lazy {
        MessageListAdapter(this@ChatActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializeView()
        setupObservers()
    }

    private fun initializeView() {
        with(binding) {
            val receiver = intent?.getStringExtra("receiverUserData")
            receiverUserData = Gson().fromJson(receiver, User::class.java)
            binding.rvChats.adapter = messageListAdapter

            tvUserName.text = receiverUserData.name
            Glide.with(ivProfilePic).load(receiverUserData.avatarImagePath)
                .placeholder(R.drawable.ic_profile).into(ivProfilePic)

            tvStatus.isVisible = false

            etMessage.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                    etMessage.error = null
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            ivSend.setOnClickListener {
                val message = etMessage.text.toString()
                if (message.trim().isEmpty()) {
                    etMessage.error = "Enter Message"
                    return@setOnClickListener
                }

                if (messageListAdapter.itemCount > 0) {
                    rvChats.smoothScrollToPosition(messageListAdapter.itemCount - 1)
                }
                chatViewModel.sendMessage(receiverUserData.uid, message)

                etMessage.setText("")
//                etMessage.hideKeyboard()
            }

            ivScrollToBottom.setOnClickListener {
                if (messageListAdapter.itemCount > 0) {
                    rvChats.smoothScrollToPosition(messageListAdapter.itemCount - 1)
                }
            }

            rvChats.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount

                    // Check if the user is at the bottom
                    isUserAtBottom = lastVisibleItemPosition == totalItemCount - 1
                    ivScrollToBottom.isVisible = !isUserAtBottom
                }
            })
        }
    }

    private fun setupObservers() {
        userDetailViewModel.isUserDataAvailable()

        userDetailViewModel.currentUserProfile.observe(this) { result ->
            result.onSuccess {
                currentUserData = it
                messageListAdapter.setCurrentUserId(currentUserData.uid)
            }.onFailure {}
        }

        chatViewModel.getMessages(receiverUserData.uid) {
            Log.d("TAG_message", "setupObservers: " + it.size)
            messageListAdapter.updateUserList(it)

            if (isUserAtBottom) {
                if (messageListAdapter.itemCount > 0) {
                    binding.rvChats.smoothScrollToPosition(it.size - 1)
                }
            }
        }
    }
}