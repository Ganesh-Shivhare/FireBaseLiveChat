package com.ganesh.hilt.firebase.livechat.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.ganesh.hilt.firebase.livechat.MyApplication.Companion.myApplication
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun View.hideKeyboard() {
    val imm = myApplication.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)

    clearFocus()
}

fun Long.formatTimeFromMillis(): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault()) // 12-hour format with AM/PM
    return sdf.format(Date(this))
}

fun Context.getAvatarImageList(): ArrayList<String> {
    val assetManager = assets
    val frameModels = ArrayList<String>()

    fun addFramesFromFolder(path: String) {
        try {
            // List all files and subfolders within the current folder
            val files = assetManager.list(path) ?: return

            // Initialize variables to track different file types for each frame model
            var frameImagePath: String? = null

            for (file in files) {
                val fullPath = "$path/$file"
                val subFiles = assetManager.list(fullPath)

                if (subFiles.isNullOrEmpty()) {
                    // It's a file, classify it based on name or extension
                    when {
                        file.contains("ic_avatar") -> {
                            frameImagePath =
                                "${getAssetsStartPath()}$fullPath"

                            frameModels.add(frameImagePath)
                        }
                    }
                } else {
                    // It's a directory, check if it's the 'font' folder
                    // Recursively process subfolders (if there are other subdirectories)
                    addFramesFromFolder(fullPath)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Start searching from the root folder (e.g., 'instagram')
    addFramesFromFolder("avatars")

    frameModels.sortWith { o1, o2 -> o1.compareTo(o2) }

    return frameModels
}


private fun getAssetsStartPath(): String {
    return "file:///android_asset/"
}
