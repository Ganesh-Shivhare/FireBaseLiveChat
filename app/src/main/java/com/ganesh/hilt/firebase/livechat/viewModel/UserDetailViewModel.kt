package com.ganesh.hilt.firebase.livechat.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganesh.hilt.firebase.livechat.data.User
import com.ganesh.hilt.firebase.livechat.data.UserStatus
import com.ganesh.hilt.firebase.livechat.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _searchResults = MutableLiveData<ArrayList<User>>()
    val searchResults: LiveData<ArrayList<User>> get() = _searchResults

    private val _userList = MutableLiveData<ArrayList<User>>()
    val userList: LiveData<ArrayList<User>> get() = _userList

    private val _currentlyChatsUsers = MutableLiveData<ArrayList<User>>()
    val currentlyChatsUsers: LiveData<ArrayList<User>> get() = _currentlyChatsUsers

    private val _addUserProfile = MutableLiveData<Result<Boolean>>()
    val addUserProfile: LiveData<Result<Boolean>> get() = _addUserProfile

    private val _myUserProfile = MutableLiveData<Result<User>>()
    val myUserProfile: LiveData<Result<User>> get() = _myUserProfile

    private val _receiverUserProfile = MutableLiveData<User>()
    val receiverUserProfile: LiveData<User> get() = _receiverUserProfile

    private val _userStatus = MutableLiveData<UserStatus>()
    val userStatus: LiveData<UserStatus> get() = _userStatus

    fun searchUsers(query: String) {
        viewModelScope.launch {
            repository.searchUser(query) {
                _searchResults.postValue(it)
            }
        }
    }

    fun getAllUsers() {
        viewModelScope.launch {
            repository.getAllUsers {
                _userList.postValue(it)
            }
        }
    }

    fun isUserDataAvailable() {
        viewModelScope.launch {
            repository.isUserDataAvailable {
                _addUserProfile.postValue(it.second)
                _myUserProfile.postValue(it.first)
            }
        }
    }

    fun insertUserData(userModel: User) {
        viewModelScope.launch {
            repository.insertUserProfileData(userModel) {
                _myUserProfile.postValue(it)
            }
        }
    }

    fun getCurrentlyChatsUsers() {
        viewModelScope.launch {
            repository.getCurrentlyChatsUsers {
                _currentlyChatsUsers.postValue(it)
            }
        }
    }

    fun getUserById(id: String) {
        viewModelScope.launch {
            repository.getUserById(id) {
                _receiverUserProfile.postValue(it)
            }
        }
    }

    /**
     * Update user's online status (online/offline)
     */
    fun setUserOnlineStatus(status: String) {
        viewModelScope.launch {
            repository.updateUserStatus(status)
        }
    }

    /**
     * Set typing status for a specific user
     */
    fun setTypingStatus(isTyping: Boolean) {
        viewModelScope.launch {
            repository.setUserTyping(isTyping)
        }
    }

    /**
     * Listen for user status updates and update LiveData
     */
    fun listenForUserStatus(userId: String) {
        repository.listenForUserUpdates(userId) { user ->
            _userStatus.postValue(user?.userStatus)
        }
    }

    fun setReceiverID(uid: String) {
        viewModelScope.launch {
            repository.setUserReceiverID(uid)
        }
    }

    fun updateUserStatus(status: String, fcmToken: String) {
        repository.updateUserStatus(status, fcmToken)
    }
}
