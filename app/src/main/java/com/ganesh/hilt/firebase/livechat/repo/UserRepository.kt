package com.ganesh.hilt.firebase.livechat.repo

import android.util.Log
import com.ganesh.hilt.firebase.livechat.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth, private val firestore: FirebaseFirestore
) {

    fun getAllUsers(onResult: (ArrayList<User>) -> Unit) {
        val users = ArrayList<User>()
        firestore.collection("users").get().addOnSuccessListener { result ->
            result.documents.forEach {
                it.toObject(User::class.java)?.let { userData -> users.add(userData) }
            }
            onResult(users)
        }
    }

    fun searchUser(query: String, onResult: (ArrayList<User>) -> Unit) {
        val users = ArrayList<User>()

        getAllUsers {
            Log.d("TAG_searchResults", "addOnSuccessListener: " + it.size)
            it.forEach { userData ->
                users.add(userData)
            }

            val searchUserList = ArrayList<User>()
            users.forEach { userData ->
                if (userData.uid != auth.currentUser?.uid) {
                    if (userData.email.contains(query, true) || userData.phoneNumber.contains(
                            query, true
                        ) || userData.name.contains(query, true)
                    ) {
                        searchUserList.add(userData)
                    }
                }
            }

            onResult(searchUserList)
        }
    }

    fun isUserDataAvailable(result: (Pair<Result<User>, Result<Boolean>>) -> Unit) {
        var user = User()

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
                                    val email = document.getString("email").toString()
                                    val phoneNumber = document.getString("phoneNumber").toString()
                                    val uId = document.getString("uid").toString()
                                    val name = document.getString("name").toString()
                                    val avatarImagePath =
                                        document.getString("avatarImagePath").toString()
                                    user = User(uId, name, phoneNumber, email, avatarImagePath)

                                    result(Pair(Result.success(user), Result.success(false)))
                                } else {
                                    // UID does not exist, redirect to profile setup
                                    result(Pair(Result.success(user), Result.success(true)))
                                }
                            }
                    } else {
                        // Users collection does not exist, create a new document
                        result(Pair(Result.success(user), Result.success(true)))
                    }
                }
            }
        } catch (e: Exception) {
            result(Pair(Result.failure(e), Result.failure(e)))
        }
    }

    fun insertUserProfileData(userModel: User, result: (Result<User>) -> Unit) {
        if (userModel.uid.isEmpty()) {
            userModel.uid = auth.currentUser?.uid ?: return
        }

        val user = hashMapOf(
            "uid" to userModel.uid,
            "name" to userModel.name,
            "phoneNumber" to userModel.phoneNumber,
            "email" to userModel.email,
            "avatarImagePath" to userModel.avatarImagePath
        )

        firestore.collection("users").document(userModel.uid).set(user).addOnSuccessListener {
            Log.d("TAG_dataInserted", "insertUserData: $it")
            result(Result.success(userModel))
        }.addOnFailureListener {
            result(Result.failure(it))
        }
    }
}
