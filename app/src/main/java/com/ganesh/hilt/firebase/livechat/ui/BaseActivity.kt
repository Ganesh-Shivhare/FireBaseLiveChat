package com.ganesh.hilt.firebase.livechat.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ganesh.hilt.firebase.livechat.utils.UserStatusManager
import com.ganesh.hilt.firebase.livechat.viewModel.ChatViewModel
import com.ganesh.hilt.firebase.livechat.viewModel.LoginViewModel
import com.ganesh.hilt.firebase.livechat.viewModel.UserDetailViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    internal val loginViewModel: LoginViewModel by viewModels()
    internal val userDetailViewModel: UserDetailViewModel by viewModels()
    internal val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserStatusManager.initialize(this)
    }
}