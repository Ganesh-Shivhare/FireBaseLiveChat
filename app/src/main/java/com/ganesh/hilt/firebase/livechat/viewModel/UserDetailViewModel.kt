package com.ganesh.hilt.firebase.livechat.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ganesh.hilt.firebase.livechat.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val auth: FirebaseAuth, private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _searchResults = MutableLiveData<List<User>>()
    val searchResults: LiveData<List<User>> get() = _searchResults

    private val _addUserProfile = MutableLiveData<Result<Boolean>>()
    val addUserProfile: LiveData<Result<Boolean>> get() = _addUserProfile

    private val _currentUserProfile = MutableLiveData<Result<User>>()
    val currentUserProfile: LiveData<Result<User>> get() = _currentUserProfile

    fun searchUsers(query: String) {
        firestore.collection("users").whereEqualTo("phoneNumber", query).get()
            .addOnSuccessListener { result ->
                val users = result.documents.mapNotNull { it.toObject(User::class.java) }
                _searchResults.postValue(users)
            }
    }

    fun isUserDataAvailable() {
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val usersCollection = firestore.collection("users")

                // Check if the users collection exists
                usersCollection.get().addOnCompleteListener { task ->
                    if (task.isSuccessful && !task.result.isEmpty) {
                        // Users collection exists, check if the UID is present
                        usersCollection.document(currentUser.uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    // UID exists, check if email or phone number is available
                                    val email = document.getString("email")
                                    val phoneNumber = document.getString("phoneNumber")

                                    if (!email.isNullOrEmpty() || !phoneNumber.isNullOrEmpty()) {
                                        _addUserProfile.postValue(Result.success(false))
                                    } else {
                                        _addUserProfile.postValue(Result.success(true))
                                    }
                                } else {
                                    // UID does not exist, redirect to profile setup
                                    _addUserProfile.postValue(Result.success(true))
                                }
                            }
                    } else {
                        // Users collection does not exist, create a new document
                        _addUserProfile.postValue(Result.success(true))
                    }
                }
            }
        } catch (e: Exception) {
            _addUserProfile.postValue(Result.failure(e))
        }
    }

    fun insertUserData(userModel: User) {
        if (userModel.uid.isEmpty()) {
            userModel.uid = auth.currentUser?.uid ?: return
        }

        val user = hashMapOf(
            "uid" to userModel.uid,
            "name" to userModel.name,
            "phoneNumber" to userModel.phoneNumber,
            "email" to userModel.email
        )

        firestore.collection("users").document(userModel.uid).set(user).addOnSuccessListener {
            Log.d("TAG_dataInserted", "insertUserData: " + it)
            _currentUserProfile.postValue(Result.success(userModel))
        }.addOnFailureListener {
            _currentUserProfile.postValue(Result.failure(it))
        }
    }
}
