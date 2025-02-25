package com.ganesh.hilt.firebase.livechat.repo

import android.util.Log
import com.ganesh.hilt.firebase.livechat.data.User
import com.ganesh.hilt.firebase.livechat.data.UserStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val chatRepository: ChatRepository
) {

    fun getAllUsers(onResult: (ArrayList<User>) -> Unit) {
        firestore.collection("users").addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("Firestore", "Error listening for chats", error)
                return@addSnapshotListener
            }

            val users = ArrayList<User>()
            if (snapshots != null && !snapshots.isEmpty) {
                for (document in snapshots.documents) {
                    document.toObject(User::class.java)?.let { userData -> users.add(userData) }
                }

                onResult(users)
            }
        }
    }

    fun searchUser(query: String, onResult: (ArrayList<User>) -> Unit) {
        val users = ArrayList<User>()

        getAllUsers {
            Log.d("TAG_searchResults", "addOnSuccessListener: " + it.size)
            it.forEach { userData ->
                users.add(userData)
            }

            var searchUserList = ArrayList<User>()
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

            searchUserList = removeDuplicateUsers(searchUserList)
            onResult(searchUserList)
        }
    }

    fun getUserById(uId: String, onResult: (User) -> Unit) {
        val users = ArrayList<User>()

        getAllUsers {
            Log.d("TAG_searchResults", "addOnSuccessListener: " + it.size)
            it.forEach { userData ->
                users.add(userData)
            }

            users.forEach { userData ->
                if (userData.uid != auth.currentUser?.uid) {
                    if (userData.uid.contains(uId)) {
                        onResult(userData)
                    }
                }
            }
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

    fun getCurrentlyChatsUsers(result: (ArrayList<User>) -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("fireChats").addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("TAG_currentUsers", "Error listening for chats", error)
                return@addSnapshotListener
            }

            if (snapshots != null && !snapshots.isEmpty) {
                var userChatList = ArrayList<User>()

                snapshots.documents.forEachIndexed { index, document ->
                    val receiverId = document.id.replace(uid, "").replace("-", "")
                    Log.d("TAG_currentUsers", "Found chat: ${receiverId}")

                    getUserById(receiverId) { userData ->
                        chatRepository.getMessages(receiverId) { chatMessages ->
                            if (chatMessages.isNotEmpty()) {
                                userData.chatMessage = chatMessages.last()
                            }

                            var unreadMessageCount = 0
                            chatMessages.forEach {
                                if (it.senderId != uid && !it.messageRead) {
                                    unreadMessageCount++
                                }
                            }
                            userData.unreadMessageCount = unreadMessageCount

                            userChatList.add(userData)

                            userChatList = removeDuplicateUsers(userChatList)

                            result(userChatList)
                        }
                    }
                    Log.d("ChatUpdate", "Chat ID: ${document.id}")
                }
                // Handle the updated chat list
            } else {
                Log.d("ChatUpdate", "No chats available")
            }
        }
    }

    private fun removeDuplicateUsers(dataList: ArrayList<User>): ArrayList<User> {
        val uniqueUsers = LinkedHashMap<String, User>()

        for (data in dataList) {
            uniqueUsers[data.uid] = data // Replaces earlier entries, keeping the last one
        }

        return ArrayList(uniqueUsers.values)
    }

    private fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Update user profile data (name, phone, email, avatar)
     */
    fun updateUserData(name: String, phoneNumber: String, email: String, avatarImagePath: String) {
        val userId = getUserId() ?: return
        val userRef = firestore.collection("users").document(userId)

        val user = User(
            uid = userId,
            name = name,
            phoneNumber = phoneNumber,
            email = email,
            avatarImagePath = avatarImagePath,
            userStatus = UserStatus()
        )

        userRef.set(user)
    }

    /**
     * Update user's online status and last seen
     */
    fun updateUserStatus(status: String) {
        val userId = getUserId() ?: return
        val userRef = firestore.collection("users").document(userId)

        val statusMap = mapOf(
            "userStatus.status" to status, "userStatus.lastSeen" to System.currentTimeMillis()
        )

        userRef.update(statusMap)
    }

    /**
     * Update typing status for a particular receiver
     */
    fun setUserTyping(isTyping: Boolean) {
        val userId = getUserId() ?: return
        val userRef = firestore.collection("users").document(userId)

        val typingMap = mapOf(
            "userStatus.typing" to isTyping
        )

        userRef.update(typingMap)
    }

    /**
     * Listen for real-time updates on user status
     */
    fun listenForUserUpdates(userId: String, onUserUpdate: (User?) -> Unit) {
        val userRef = firestore.collection("users").document(userId)

        userRef.addSnapshotListener { snapshot, _ ->
            val user = snapshot?.toObject(User::class.java)
            onUserUpdate(user)
        }
    }

    fun setUserReceiverID(uid: String) {
        val userId = getUserId() ?: return
        val userRef = firestore.collection("users").document(userId)

        val typingMap = mapOf(
            "userStatus.typingTo" to uid
        )

        userRef.update(typingMap)
    }
}
