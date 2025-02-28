package com.ganesh.hilt.firebase.livechat.ui

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ganesh.hilt.firebase.livechat.R
import com.ganesh.hilt.firebase.livechat.viewModel.ChatViewModel
import com.ganesh.hilt.firebase.livechat.viewModel.LoginViewModel
import com.ganesh.hilt.firebase.livechat.viewModel.UserDetailViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    internal val loginViewModel: LoginViewModel by viewModels()
    internal val userDetailViewModel: UserDetailViewModel by viewModels()
    internal val chatViewModel: ChatViewModel by viewModels()
    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    internal val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
    }
    internal val editor: SharedPreferences.Editor by lazy { sharedPreferences.edit(); }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationManager.cancelAll()
    }

    override fun onResume() {
        super.onResume()
        notificationManager.cancelAll()
    }
}