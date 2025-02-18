package com.ganesh.hilt.firebase.livechat.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.ganesh.hilt.firebase.livechat.databinding.ActivitySettingBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingActivity : BaseActivity() {

    private val binding by lazy { ActivitySettingBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.tvLogout.setOnClickListener {
            loginViewModel.logoutUser()
        }

        loginViewModel.isLoggedIn.observe(this) { result ->
            result.onSuccess {
                if (!it) {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    this.finish() // if the activity running has it's own context
                }
            }.onFailure { }
        }
    }
}