package com.ganesh.hilt.firebase.livechat.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isVisible
import com.ganesh.hilt.firebase.livechat.databinding.ActivityChatListBinding

class ChatListActivity : BaseActivity() {

    private val binding: ActivityChatListBinding by lazy {
        ActivityChatListBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        with(binding) {
            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val textIsEmpty = s?.length == 0
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