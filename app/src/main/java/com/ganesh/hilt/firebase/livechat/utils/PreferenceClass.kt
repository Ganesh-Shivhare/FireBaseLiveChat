package com.ganesh.hilt.firebase.livechat.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.ganesh.hilt.firebase.livechat.R

class PreferenceClass(private val context: Context) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE)
    }
    private val editor: SharedPreferences.Editor by lazy { prefs.edit() }

    fun setPrefValue(key: Int, value: Any): Boolean {
        val keyString = context.getString(key)

        return when (value) {
            is String -> {
                editor.putString(keyString, value).apply()
                true
            }

            is Boolean -> {
                editor.putBoolean(keyString, value).apply()
                true
            }

            is Int -> {
                editor.putInt(keyString, value).apply()
                true
            }

            is Long -> {
                editor.putLong(keyString, value).apply()
                true
            }

            is Float -> {
                editor.putFloat(keyString, value).apply()
                true
            }

            else -> {
                false
            }
        }
    }

    fun setPrefValue(key: String, value: Any): Boolean {

        return when (value) {
            is String -> {
                editor.putString(key, value).apply()
                true
            }

            is Boolean -> {
                editor.putBoolean(key, value).apply()
                true
            }

            is Int -> {
                editor.putInt(key, value).apply()
                true
            }

            is Long -> {
                editor.putLong(key, value).apply()
                true
            }

            is Float -> {
                editor.putFloat(key, value).apply()
                true
            }

            else -> {
                false
            }
        }
    }

    fun getPrefValue(key: Int, defaultValue: Any): Any? {
        val keyString = context.getString(key)

        return when (defaultValue) {
            is String -> {
                prefs.getString(keyString, defaultValue)
            }

            is Boolean -> {
                prefs.getBoolean(keyString, defaultValue)
            }

            is Int -> {
                prefs.getInt(keyString, defaultValue)
            }

            is Long -> {
                prefs.getLong(keyString, defaultValue)
            }

            is Float -> {
                prefs.getFloat(keyString, defaultValue)
            }

            else -> {
                "Invalid Value Type"
            }
        }
    }

    fun getPrefValue(key: String, defaultValue: Any): Any? {
        return when (defaultValue) {
            is String -> {
                prefs.getString(key, defaultValue)
            }

            is Boolean -> {
                prefs.getBoolean(key, defaultValue)
            }

            is Int -> {
                prefs.getInt(key, defaultValue)
            }

            is Long -> {
                prefs.getLong(key, defaultValue)
            }

            is Float -> {
                prefs.getFloat(key, defaultValue)
            }

            else -> {
                "Invalid Value Type"
            }
        }
    }
}