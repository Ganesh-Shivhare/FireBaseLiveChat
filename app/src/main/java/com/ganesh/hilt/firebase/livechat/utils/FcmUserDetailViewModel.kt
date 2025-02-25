package com.ganesh.hilt.firebase.livechat.utils

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ganesh.hilt.firebase.livechat.viewModel.UserDetailViewModel

object FcmUserDetailViewModel {
    private var fcmUserDetailViewModel: UserDetailViewModel? = null

    fun initialize(viewModelStoreOwner: ViewModelStoreOwner) {
        if (fcmUserDetailViewModel == null) {
            fcmUserDetailViewModel =
                ViewModelProvider(viewModelStoreOwner)[UserDetailViewModel::class.java]
        }
    }

    fun getViewModel(): UserDetailViewModel? {
        return fcmUserDetailViewModel
    }
}
