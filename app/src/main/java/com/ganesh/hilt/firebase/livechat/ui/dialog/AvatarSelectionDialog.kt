@file:Suppress("DEPRECATION")

package com.ganesh.hilt.firebase.livechat.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import com.ganesh.hilt.firebase.livechat.databinding.AvatarListDialogBinding
import com.ganesh.hilt.firebase.livechat.ui.BaseActivity
import com.ganesh.hilt.firebase.livechat.ui.adapter.AvatarListAdapter

class AvatarSelectionDialog(
    private val baseActivity: BaseActivity,
) : Dialog(baseActivity) {

    private val commonDialogBinding: AvatarListDialogBinding by lazy {
        AvatarListDialogBinding.inflate(
            layoutInflater
        )
    }
    private val avatarListAdapter by lazy { AvatarListAdapter(baseActivity) }
    fun setAvatarList(avatarList: ArrayList<String>) {
        avatarListAdapter.updateUserList(avatarList)
    }

    fun setSelectedAvatar(selectedAvatar: String) {
        avatarListAdapter.setSelectedAvatar(selectedAvatar)
    }

    fun getSelectedAvatar(result: (String) -> Unit) {
        avatarListAdapter.getSelectedAvatar(result)
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(commonDialogBinding.root)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setCancelable(false)

        commonDialogBinding.rvAvatar.adapter = avatarListAdapter
        commonDialogBinding.ivClose.setOnClickListener {
            dismiss()
        }
    }
}
