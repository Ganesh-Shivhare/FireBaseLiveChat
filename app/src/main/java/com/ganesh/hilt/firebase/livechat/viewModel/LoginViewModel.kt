package com.ganesh.hilt.firebase.livechat.viewModel

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganesh.hilt.firebase.livechat.data.User
import com.ganesh.hilt.firebase.livechat.repo.FirebaseAuthRepository
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: FirebaseAuthRepository) :
    ViewModel() {

    private val _authResult = MutableLiveData<Result<String>>()
    val authResult: LiveData<Result<String>> get() = _authResult

    private val _isLoggedIn = MutableLiveData<Result<Boolean>>()
    val isLoggedIn: LiveData<Result<Boolean>> get() = _isLoggedIn

    private val _userData = MutableLiveData<Result<User>>()
    val userData: LiveData<Result<User>> get() = _userData

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authResult.postValue(repository.signInWithEmail(email, password))
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authResult.postValue(repository.signInWithGoogle(idToken))
        }
    }

    fun isLoggedIn() {
        viewModelScope.launch {
            _isLoggedIn.postValue(repository.isUserLoggedIn())
        }
    }

    private val _verificationId = MutableLiveData<String>()
    val verificationId: LiveData<String> get() = _verificationId

    fun sendVerificationCode(
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks,
        activity: AppCompatActivity
    ) {
        repository.sendVerificationCode(phoneNumber, callbacks, activity)
    }

    fun verifyOtp(otp: String) {
        viewModelScope.launch {
            _authResult.postValue(repository.verifyOtp(otp))
        }
    }

    fun getUserData() {
        viewModelScope.launch {
            _userData.postValue(repository.getUserData())
        }
    }

    fun storeVerificationId(id: String) {
        repository.storeVerificationId(id)
    }
}
