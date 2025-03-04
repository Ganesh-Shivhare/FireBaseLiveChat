package com.ganesh.hilt.firebase.livechat.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.ganesh.hilt.firebase.livechat.R
import com.ganesh.hilt.firebase.livechat.databinding.ActivityLoginBinding
import com.ganesh.hilt.firebase.livechat.ui.BaseActivity
import com.ganesh.hilt.firebase.livechat.utils.Debug
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class LoginActivity : BaseActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private var resendEnabled = true

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupGoogleSignIn()

        binding.btnSendCode.setOnClickListener {
            if (resendEnabled) {
                val phoneNumber = binding.etPhoneNumber.text.toString()
                if (phoneNumber.isNotEmpty()) {
                    if (phoneNumber.length > 8) {
                        loginViewModel.sendVerificationCode(
                            phoneNumber, verificationCallbacks, this@LoginActivity
                        )
                        startResendCooldown()
                    } else {
                        Toast.makeText(this, "Phone Number must be > 8", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnVerifyCode.setOnClickListener {
            val otp = binding.etOtp.text.toString()
            if (otp.isEmpty()) {
                Toast.makeText(this, "Enter Otp", Toast.LENGTH_LONG).show()
            } else if (otp.length != 6) {
                Toast.makeText(this, "OTP Must be 6 digit", Toast.LENGTH_LONG).show()
            } else {
                loginViewModel.verifyOtp(otp)
            }
        }

        binding.btnLoginEmail.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (password.isNotEmpty()) {
                if (password.length > 8) {
                    loginViewModel.loginWithEmail(email, password)
                } else {
                    Toast.makeText(this, "Use more than 8 character", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Please Enter Password", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnLoginGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        binding.ivShowPassword.setOnClickListener {
            val cursorPosition = binding.etPassword.selectionEnd
            binding.ivShowPassword.isSelected = !binding.ivShowPassword.isSelected
            if (binding.ivShowPassword.isSelected) {
                binding.etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
            }
            binding.etPassword.setSelection(cursorPosition)
        }

        setupObservers()
    }

    private fun setupObservers() {
        loginViewModel.authResult.observe(this) { result ->
            result.onSuccess {
                Debug.d("TAG_userData", "onCreate: $it")
                Toast.makeText(this, "Login Successful.", Toast.LENGTH_LONG).show()
                loginViewModel.getUserData()
            }.onFailure {
                Toast.makeText(this, "Login Failed.", Toast.LENGTH_LONG).show()
            }
        }

        loginViewModel.userData.observe(this) { result ->
            result.onSuccess {
                Debug.d("TAG_userData", "onCreate: $it")
                userDetailViewModel.isUserDataAvailable()
            }.onFailure {
                Toast.makeText(this, "Login Failed.", Toast.LENGTH_LONG).show()
            }
        }

        userDetailViewModel.addUserProfile.observe(this) { result ->
            result.onSuccess {
                if (it) {
                    startActivity(Intent(this@LoginActivity, ProfileSetupActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(
                        this@LoginActivity, ChatListActivity::class.java
                    ).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) })
                    finish()
                }
            }.onFailure {

            }
        }
    }

    // Google Sign-In Setup
    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Replace with actual client ID
            .requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            Debug.d("TAG_test", "googleSignInLauncher:isComplete " + task.isComplete)
            Debug.d("TAG_test", "googleSignInLauncher:isCanceled " + task.isCanceled)
            Debug.d("TAG_test", "googleSignInLauncher:isSuccessful " + task.isSuccessful)
            task.result?.idToken?.let {
                Debug.d("TAG_test", "googleSignInLauncher: $it")
                loginViewModel.loginWithGoogle(it)
            }
        }

    private val verificationCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                loginViewModel.verifyOtp(credential.smsCode ?: "")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(
                    this@LoginActivity, "Verification Failed.", Toast.LENGTH_LONG
                ).show()
            }

            override fun onCodeSent(
                verificationId: String, token: PhoneAuthProvider.ForceResendingToken
            ) {
                loginViewModel.storeVerificationId(verificationId)
                Toast.makeText(this@LoginActivity, "OTP Sent!", Toast.LENGTH_LONG).show()
            }
        }


    private fun startResendCooldown() {
        resendEnabled = false
        binding.btnVerifyCode.isSelected = true

        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.btnSendCode.text = "Resend OTP in ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                resendEnabled = true
                binding.btnSendCode.text = "Resend OTP"
            }
        }.start()
    }
}
