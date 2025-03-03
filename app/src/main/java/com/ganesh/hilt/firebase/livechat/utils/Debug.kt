package com.ganesh.hilt.firebase.livechat.utils

import android.content.Context
import android.widget.TextView
import android.widget.Toast
import com.ganesh.hilt.firebase.livechat.BuildConfig

/**
 * use for app log if Debug is false our app logs disable
 */
object Debug {
    private const val debugApp = BuildConfig.DEBUG
    fun e(tag: String?, msg: String?) {
        if (debugApp) {
            e(tag, msg)
        }
    }

    fun showtext(msg: String, textView: TextView) {
        if (debugApp) {
            textView.text = "" + msg
        }
    }

    fun i(tag: String?, msg: String?) {
        if (debugApp) {
            i(tag, msg)
        }
    }

    fun w(tag: String?, msg: String?) {
        if (debugApp) {
            w(tag, msg)
        }
    }

    fun d(tag: String?, msg: String?) {
        if (debugApp) {
            d(tag, msg)
        }
    }

    fun v(tag: String?, msg: String?) {
        if (debugApp) {
            v(tag, msg)
        }
    }


    fun PrintException(e: Exception) {
        if (debugApp) {
            d("LocalBaseActivity", "PrintException: " + e.message)
            e.printStackTrace()
        }
    }

    fun showToast(context: Context?, message: String?) {
        if (debugApp) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}