package com.ganesh.hilt.firebase.livechat.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.view.isVisible
import com.ganesh.hilt.firebase.livechat.databinding.ActivityChatListBinding
import com.google.gson.Gson

class ChatListActivity : BaseActivity() {

    private val binding: ActivityChatListBinding by lazy {
        ActivityChatListBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
        setupObservers()
    }

    private fun setupObservers() {
        userDetailViewModel.searchResults.observe(this) { userList ->
            Log.d("TAG_searchResults", "setupObservers: " + userList.size)
            userList.forEach {
                Log.d("TAG_searchResults", "setupObservers: " + Gson().toJson(it))
            }
            binding.rvChatList.isVisible = userList.isNotEmpty()
        }
    }

    private fun initView() {
        with(binding) {
            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                    userDetailViewModel.searchUsers(s?.toString()?.trim() ?: "")
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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
        }
    }
}