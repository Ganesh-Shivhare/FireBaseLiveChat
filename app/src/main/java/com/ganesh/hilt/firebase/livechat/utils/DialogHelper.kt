package com.ganesh.hilt.firebase.livechat.utils

import com.ganesh.hilt.firebase.livechat.ui.BaseActivity
import com.ganesh.hilt.firebase.livechat.ui.dialog.CommonDialog

object DialogHelper {
    fun getLogoutDialog(baseActivity: BaseActivity, result: ((Boolean) -> Unit)): CommonDialog {
        val commonDialog = CommonDialog(baseActivity).apply {
            title = "Logout ?"
            message = "Are you sure you want\nto logout?"
            positiveBtnText = "Logout"
            this.result = result
        }

        return commonDialog
    }
}