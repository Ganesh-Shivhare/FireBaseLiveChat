package com.ganesh.hilt.firebase.livechat.ui

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ganesh.hilt.firebase.livechat.utils.PermissionUtils
import com.ganesh.hilt.firebase.livechat.utils.PreferenceClass
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
    internal val permissionUtils: PermissionUtils by lazy { PermissionUtils(this) }
    internal val notificationPermissionList by lazy {
        ArrayList<String>().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(POST_NOTIFICATIONS)
            }
        }
    }

    internal val preferenceClass: PreferenceClass by lazy {
        PreferenceClass(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationManager.cancelAll()
    }

    override fun onResume() {
        super.onResume()
        notificationManager.cancelAll()
    }
}