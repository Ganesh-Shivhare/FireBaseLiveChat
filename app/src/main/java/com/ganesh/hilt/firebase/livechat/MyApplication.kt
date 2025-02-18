package com.ganesh.hilt.firebase.livechat

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    companion object {
        lateinit var myApplication: MyApplication
    }

    override fun onCreate() {
        super.onCreate()
        myApplication = this
    }
}