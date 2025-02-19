package com.ganesh.hilt.firebase.livechat.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.ganesh.hilt.firebase.livechat.databinding.ActivityChatListBinding
import com.ganesh.hilt.firebase.livechat.ui.BaseActivity
import com.ganesh.hilt.firebase.livechat.ui.adapter.UserListAdapter
import com.ganesh.hilt.firebase.livechat.utils.hideKeyboard
import com.ganesh.hilt.firebase.livechat.viewModel.FirebaseViewModel
import com.google.gson.Gson

class ChatListActivity : BaseActivity() {

    private val binding: ActivityChatListBinding by lazy {
        ActivityChatListBinding.inflate(layoutInflater)
    }
    private val searchListAdapter: UserListAdapter by lazy { UserListAdapter(this) }
    private val userListAdapter: UserListAdapter by lazy { UserListAdapter(this) }
    private lateinit var viewModel: FirebaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
        setupObservers()

        viewModel = ViewModelProvider(this)[FirebaseViewModel::class.java]
        viewModel.fcmToken.observe(this) { token ->
            Log.d("MainActivity", "FCM Token: $token")
        }

        viewModel.fetchToken()
    }

    private fun setupObservers() {
        userDetailViewModel.getCurrentlyChatsUsers()

        userDetailViewModel.searchResults.observe(this) { userList ->
            Log.d("TAG_searchResults", "setupObservers: " + userList.size)
            userList.forEach {
                Log.d("TAG_searchResults", "setupObservers: " + Gson().toJson(it))
            }
            binding.rvChatList.isVisible =
                userList.isNotEmpty() && binding.etSearch.text.trim().isEmpty()
            searchListAdapter.updateUserList(userList)
        }

        userDetailViewModel.currentlyChatsUsers.observe(this) { chatList ->
            Log.d("TAG_currentUsers", "setupObservers: " + chatList.size)
            chatList.forEach {
                Log.d("TAG_currentUsers", "setupObservers: " + it.chatMessage.message)
            }

            chatList.sortWith { chat1, chat2 ->
                chat2.chatMessage.timestamp.compareTo(chat1.chatMessage.timestamp) // Descending order
            }

            userListAdapter.updateUserList(chatList)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun initView() {
        with(binding) {
            // initSearch Recycler
            rvSearchList.adapter = searchListAdapter
            rvChatList.adapter = userListAdapter

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    userDetailViewModel.searchUsers(s?.toString()?.trim() ?: "")
                    val textIsEmpty = s?.length != 0
                    ivClose.isVisible = textIsEmpty
                    rvSearchList.isVisible = textIsEmpty
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            ivSetting.setOnClickListener {
                startActivity(Intent(this@ChatListActivity, SettingActivity::class.java))
            }

            ivClose.setOnClickListener {
                etSearch.setText("")
                etSearch.hideKeyboard()
            }
        }
    }
}