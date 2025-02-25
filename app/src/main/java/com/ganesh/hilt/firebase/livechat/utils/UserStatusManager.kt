package com.ganesh.hilt.firebase.livechat.utils

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ganesh.hilt.firebase.livechat.viewModel.UserStatusViewModel

object UserStatusManager {
    private var userStatusViewModel: UserStatusViewModel? = null

    fun initialize(viewModelStoreOwner: ViewModelStoreOwner) {
        if (userStatusViewModel == null) {
            userStatusViewModel =
                ViewModelProvider(viewModelStoreOwner)[UserStatusViewModel::class.java]
        }
    }

    fun getViewModel(): UserStatusViewModel? {
        return userStatusViewModel
    }
}
