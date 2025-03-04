package com.ganesh.hilt.firebase.livechat.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ganesh.hilt.firebase.livechat.databinding.ActivityFirstBinding
import com.ganesh.hilt.firebase.livechat.ui.BaseActivity
import com.ganesh.hilt.firebase.livechat.utils.Debug
import com.ganesh.hilt.firebase.livechat.utils.GsonUtils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FirstActivity : BaseActivity() {

    private val binding by lazy { ActivityFirstBinding.inflate(layoutInflater) }
    var finishSplashScreen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        if (Build.VERSION.SDK_INT < 31) setContentView(binding.root)

        // Set up an OnPreDrawListener to the root view.
        val content: View = findViewById(android.R.id.content)
        content.getViewTreeObserver()
            .addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check if the initial data is ready.
                    if (finishSplashScreen) {
                        // The content is ready; start drawing.
                        content.getViewTreeObserver().removeOnPreDrawListener(this)

                        loginViewModel.isLoggedIn()
                        setupObservers()

                        return true
                    } else {
                        // The content is not ready; suspend.
                        return false
                    }
                }
            })


        // 5 seconds timeout to hide splash screen
        Handler(Looper.getMainLooper()).postDelayed({ finishSplashScreen = true }, 1500)
    }

    private fun setupObservers() {
        loginViewModel.isLoggedIn.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Login Successful.", Toast.LENGTH_LONG).show()
                if (it) {
                    userDetailViewModel.isUserDataAvailable()
                } else {
                    startActivity(Intent(this@FirstActivity, LoginActivity::class.java))
                    finish()
                }
            }.onFailure {
                Toast.makeText(this, "Login Failed.", Toast.LENGTH_LONG).show()
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

        userDetailViewModel.myUserProfile.observe(this) { result ->
            result.onSuccess {
                Debug.d("TAG_message", "setupObservers:111 " + GsonUtils.modelToJson(it))
                preferenceClass.setPrefValue("my_profile_data", GsonUtils.modelToJson(it))
            }.onFailure {

            }
        }
    }
}