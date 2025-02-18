package com.ganesh.hilt.firebase.livechat.repo

import com.ganesh.hilt.firebase.livechat.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    fun saveUserToFirestore(user: User) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).set(user)
    }

    fun getAllUsers(onResult: (List<User>) -> Unit) {
        firestore.collection("users").get()
            .addOnSuccessListener { result ->
                val users = result.documents.mapNotNull { it.toObject(User::class.java) }
                onResult(users)
            }
    }

    fun isCurrentUserRegister() {

    }
}
