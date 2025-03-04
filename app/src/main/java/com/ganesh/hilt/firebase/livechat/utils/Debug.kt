package com.ganesh.hilt.firebase.livechat.utils

import android.content.Context
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.ganesh.hilt.firebase.livechat.BuildConfig

/**
 * use for app log if Debug is false our app logs disable
 */
object Debug {
    private val debugApp: Boolean = BuildConfig.DEBUG
    fun e(tag: String, msg: String) {
        if (debugApp) {
            Log.e(tag, msg)
        }
    }

    fun showText(msg: String, textView: TextView) {
        if (debugApp) {
            textView.text = "" + msg
        }
    }

    fun i(tag: String, msg: String) {
        if (debugApp) {
            Log.i(tag, msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (debugApp) {
            Log.w(tag, msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (debugApp) {
            Log.d(tag, msg)
        }
    }

    fun v(tag: String, msg: String) {
        if (debugApp) {
            Log.v(tag, msg)
        }
    }

    fun printException(e: Exception) {
        if (debugApp) {
            Log.d("LocalBaseActivity", "PrintException: " + e.message)
            e.printStackTrace()
        }
    }

    fun showToast(context: Context?, message: String) {
        if (debugApp) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}