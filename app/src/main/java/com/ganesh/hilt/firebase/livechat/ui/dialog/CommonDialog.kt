@file:Suppress("DEPRECATION")

package com.ganesh.hilt.firebase.livechat.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import com.ganesh.hilt.firebase.livechat.databinding.CommonDialogBinding
import com.ganesh.hilt.firebase.livechat.ui.BaseActivity

class CommonDialog(
    private val baseActivity: BaseActivity,
) : Dialog(baseActivity) {

    private val binding: CommonDialogBinding by lazy {
        CommonDialogBinding.inflate(
            layoutInflater
        )
    }
    var title = ""
        set(value) {
            field = value
            binding.tvHeader.text = field
        }
    var message = ""
        set(value) {
            field = value
            binding.tvMessage.text = field
        }
    var positiveBtnText = ""
        set(value) {
            field = value
            binding.tvOkay.text = field
        }

    lateinit var result: ((Boolean) -> Unit)

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(binding.root)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setCancelable(false)


        binding.tvOkay.setOnClickListener {
            if (::result.isInitialized) result(true)
        }

        binding.ivClose.setOnClickListener {
            if (::result.isInitialized) result(false)
            dismiss()
        }
    }
}
