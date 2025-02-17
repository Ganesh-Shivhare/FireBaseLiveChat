package com.ganesh.hilt.firebase.livechat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ganesh.hilt.firebase.livechat.chat.ui.ChatListActivity
import com.ganesh.hilt.firebase.livechat.databinding.ActivityFirstBinding
import com.ganesh.hilt.firebase.livechat.login.ui.LoginActivity
import com.ganesh.hilt.firebase.livechat.login.ui.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstActivity : AppCompatActivity() {

    private val binding by lazy { ActivityFirstBinding.inflate(layoutInflater) }
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.isLoggedIn()
        viewModel.isLoggedIn.observe(this) { result ->
            result.onSuccess {
                if (it) {
                    startActivity(Intent(this@FirstActivity, ChatListActivity::class.java))
                } else {
                    startActivity(Intent(this@FirstActivity, LoginActivity::class.java))
                }
                Toast.makeText(this, "Login Successful: $it", Toast.LENGTH_LONG).show()
            }.onFailure {
                Toast.makeText(this, "Login Failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}