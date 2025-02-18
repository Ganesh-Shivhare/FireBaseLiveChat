package com.ganesh.hilt.firebase.livechat.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ganesh.hilt.firebase.livechat.databinding.ActivityFirstBinding
import com.ganesh.hilt.firebase.livechat.viewModel.LoginViewModel
import com.ganesh.hilt.firebase.livechat.viewModel.UserDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstActivity : AppCompatActivity() {

    private val binding by lazy { ActivityFirstBinding.inflate(layoutInflater) }
    private val viewModel: LoginViewModel by viewModels()
    private val userDetailViewModel: UserDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.isLoggedIn()
        viewModel.isLoggedIn.observe(this) { result ->
            result.onSuccess {
                userDetailViewModel.isUserDataAvailable()

                Toast.makeText(this, "Login Successful: $it", Toast.LENGTH_LONG).show()
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
                    startActivity(Intent(this@FirstActivity, ChatListActivity::class.java))
                    finish()
                }
            }.onFailure {

            }
        }

    }
}