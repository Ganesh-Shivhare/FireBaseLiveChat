package com.ganesh.hilt.firebase.livechat.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.ganesh.hilt.firebase.livechat.databinding.ActivityFirstBinding
import com.ganesh.hilt.firebase.livechat.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstActivity : BaseActivity() {

    private val binding by lazy { ActivityFirstBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loginViewModel.isLoggedIn()
        setupObservers()
    }

    private fun setupObservers() {
        loginViewModel.isLoggedIn.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Login Successful: $it", Toast.LENGTH_LONG).show()
                if (it) {
                    userDetailViewModel.isUserDataAvailable()
                } else {
                    startActivity(Intent(this@FirstActivity, LoginActivity::class.java))
                    finish()
                }
            }.onFailure {
                Toast.makeText(this, "Login Failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }

        userDetailViewModel.addUserProfile.observe(this) { result ->
            result.onSuccess {
                if (it) {
                    startActivity(Intent(this@FirstActivity, ProfileSetupActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(
                        this@FirstActivity, ChatListActivity::class.java
                    ).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) })
                    finish()
                }
            }.onFailure {

            }
        }
    }
}