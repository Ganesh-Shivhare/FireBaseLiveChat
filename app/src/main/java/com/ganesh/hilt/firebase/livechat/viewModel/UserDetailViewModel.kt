package com.ganesh.hilt.firebase.livechat.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganesh.hilt.firebase.livechat.data.User
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

    private val _addUserProfile = MutableLiveData<Result<Boolean>>()
    val addUserProfile: LiveData<Result<Boolean>> get() = _addUserProfile

    private val _currentUserProfile = MutableLiveData<Result<User>>()
    val currentUserProfile: LiveData<Result<User>> get() = _currentUserProfile

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
                _currentUserProfile.postValue(it.first)
            }
        }
    }

    fun insertUserData(userModel: User) {
        viewModelScope.launch {
            repository.insertUserProfileData(userModel) {
                _currentUserProfile.postValue(it)
            }
        }
    }
}
