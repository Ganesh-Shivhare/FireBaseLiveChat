package com.ganesh.hilt.firebase.livechat.ui.activity


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.ganesh.hilt.firebase.livechat.R
import com.ganesh.hilt.firebase.livechat.data.User
import com.ganesh.hilt.firebase.livechat.databinding.ActivityProfileSetupBinding
import com.ganesh.hilt.firebase.livechat.ui.BaseActivity
import com.ganesh.hilt.firebase.livechat.ui.dialog.AvatarSelectionDialog
import com.ganesh.hilt.firebase.livechat.utils.GsonUtils
import com.ganesh.hilt.firebase.livechat.utils.getAvatarImageList
import com.ganesh.hilt.firebase.livechat.viewModel.FirebaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileSetupActivity : BaseActivity() {

    private val binding: ActivityProfileSetupBinding by lazy {
        ActivityProfileSetupBinding.inflate(layoutInflater)
    }
    private lateinit var userData: User
    private val avatarList by lazy { getAvatarImageList() }
    private val avatarSelectionDialog by lazy {
        AvatarSelectionDialog(this).apply {
            setAvatarList(avatarList)
            getSelectedAvatar {
                selectedAvatar = it

                Glide.with(binding.ivProfilePic).load(selectedAvatar)
                    .placeholder(R.drawable.ic_profile).into(binding.ivProfilePic)
                dismiss()
            }
        }
    }
    private var selectedAvatar: String = ""
    private var updateUserProfile: User? = null
    private val viewModel: FirebaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val updateUserProfileData = intent.getStringExtra("updateUserProfile").toString()
        if (updateUserProfileData.isNotEmpty()) {
            updateUserProfile = GsonUtils.jsonToModel(updateUserProfileData, User::class.java)

            binding.btnSaveProfile.text = "Update Profile"
        } else {
            selectedAvatar = avatarList.randomOrNull() ?: ""
            Glide.with(binding.ivProfilePic).load(selectedAvatar).placeholder(R.drawable.ic_profile)
                .into(binding.ivProfilePic)
        }

        viewModel.fcmToken.observe(this) { token ->
            preferenceClass.setPrefValue("fcmToken", token)
        }

        binding.btnSaveProfile.setOnClickListener { saveUserProfile() }

        binding.ivEdit.setOnClickListener {
            avatarSelectionDialog.setSelectedAvatar(selectedAvatar)
            avatarSelectionDialog.show()
        }

        loginViewModel.getUserData()

        setupObservers()
    }

    private fun setupObservers() {
        loginViewModel.userData.observe(this) { result ->
            result.onSuccess {
                if (updateUserProfile != null) {
                    binding.etPhoneNumber.setText(updateUserProfile?.phoneNumber)
                    binding.etName.setText(updateUserProfile?.name)
                    binding.etEmail.setText(updateUserProfile?.email)

                    selectedAvatar = updateUserProfile?.avatarImagePath ?: ""
                    Glide.with(binding.ivProfilePic).load(selectedAvatar)
                        .placeholder(R.drawable.ic_profile).into(binding.ivProfilePic)

                    binding.etEmail.isEnabled = binding.etEmail.text.isNullOrEmpty()
                    binding.etPhoneNumber.isEnabled = binding.etPhoneNumber.text.isNullOrEmpty()
                } else {
                    userData = it

                    binding.etPhoneNumber.setText(userData.phoneNumber)
                    binding.etName.setText(userData.name)
                    binding.etEmail.setText(userData.email)

                    binding.etEmail.isEnabled = binding.etEmail.text.isNullOrEmpty()
                    binding.etPhoneNumber.isEnabled = binding.etPhoneNumber.text.isNullOrEmpty()
                }
            }.onFailure {

            }
        }

        userDetailViewModel.myUserProfile.observe(this) { result ->
            result.onSuccess {
                Log.d("TAG_dataInserted", "onSuccess: " + GsonUtils.modelToJson(it))
                startActivity(Intent(
                    this@ProfileSetupActivity, ChatListActivity::class.java
                ).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) })
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
        val avatarImagePath = selectedAvatar
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

        val user = User(userId, name, phoneNumber, email, avatarImagePath)
        user.userToken = preferenceClass.getPrefValue("fcmToken", "").toString()

        userDetailViewModel.insertUserData(user)
    }
}

