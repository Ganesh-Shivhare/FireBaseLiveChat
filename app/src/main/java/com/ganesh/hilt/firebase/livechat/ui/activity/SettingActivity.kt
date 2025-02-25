package com.ganesh.hilt.firebase.livechat.ui.activity

import android.content.Intent
import android.os.Bundle
import com.bumptech.glide.Glide
import com.ganesh.hilt.firebase.livechat.R
import com.ganesh.hilt.firebase.livechat.data.User
import com.ganesh.hilt.firebase.livechat.databinding.ActivitySettingBinding
import com.ganesh.hilt.firebase.livechat.ui.BaseActivity
import com.ganesh.hilt.firebase.livechat.utils.DialogHelper
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingActivity : BaseActivity() {

    private lateinit var currentUserData: User
    private val binding by lazy { ActivitySettingBinding.inflate(layoutInflater) }
    private val logoutDialog by lazy {
        DialogHelper.getLogoutDialog(this) {
            if (it) {
                loginViewModel.logoutUser()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializeView()
        setupObservers()
    }

    private fun initializeView() {
        with(binding) {
            tvLogout.setOnClickListener {
                if (!logoutDialog.isShowing) logoutDialog.show()
            }
            tvEditProfile.setOnClickListener {
                if (::currentUserData.isInitialized) {
                    val intent = Intent(this@SettingActivity, ProfileSetupActivity::class.java)
                    intent.putExtra("updateUserProfile", Gson().toJson(currentUserData))
                    startActivity(intent)
                }
            }

            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupObservers() {
        userDetailViewModel.isUserDataAvailable()

        userDetailViewModel.myUserProfile.observe(this) { result ->
            result.onSuccess {
                currentUserData = it

                with(binding) {
                    Glide.with(ivProfilePic).load(currentUserData.avatarImagePath)
                        .placeholder(R.drawable.ic_profile).into(ivProfilePic)

                    etEmail.setText(currentUserData.email)
                    etEmail.isEnabled = false

                    etName.setText(currentUserData.name)
                    etName.isEnabled = false

                    etPhoneNumber.setText(currentUserData.phoneNumber)
                    etPhoneNumber.isEnabled = false
                }
            }.onFailure {}
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