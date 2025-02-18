package com.ganesh.hilt.firebase.livechat.ui


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ganesh.hilt.firebase.livechat.data.User
import com.ganesh.hilt.firebase.livechat.databinding.ActivityProfileSetupBinding
import com.ganesh.hilt.firebase.livechat.viewModel.LoginViewModel
import com.ganesh.hilt.firebase.livechat.viewModel.UserDetailViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileSetupActivity : AppCompatActivity() {

    private val binding: ActivityProfileSetupBinding by lazy {
        ActivityProfileSetupBinding.inflate(layoutInflater)
    }
    private val userDetailViewModel: UserDetailViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var userData: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnSaveProfile.setOnClickListener { saveUserProfile() }

        loginViewModel.getUserData()

        setupObservers()
    }

    private fun setupObservers() {
        loginViewModel.userData.observe(this) { result ->
            result.onSuccess {
                userData = it

                binding.etPhoneNumber.setText(userData.phoneNumber)
                binding.etName.setText(userData.name)
                binding.etEmail.setText(userData.email)

                binding.etEmail.isEnabled = binding.etEmail.text.isNullOrEmpty()
                binding.etPhoneNumber.isEnabled = binding.etPhoneNumber.text.isNullOrEmpty()
            }.onFailure {

            }
        }

        userDetailViewModel.currentUserProfile.observe(this) { result ->
            result.onSuccess {
                Log.d("TAG_dataInserted", "onSuccess: " + Gson().toJson(it))
                startActivity(Intent(this@ProfileSetupActivity, ChatListActivity::class.java))
                finish()
            }.onFailure {
                Log.d("TAG_dataInserted", "onFailure: " + it.message)
            }
        }
    }

    private fun saveUserProfile() {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val phoneNumber = binding.etPhoneNumber.text.toString()
        val userId = if (::userData.isInitialized) userData.uid else ""
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter user name", Toast.LENGTH_SHORT).show()
            return
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show()
            return
        }
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show()
            return
        }

        val user = User(userId, name, phoneNumber, email)
        userDetailViewModel.insertUserData(user)
    }
}

