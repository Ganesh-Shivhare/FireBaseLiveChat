package com.ganesh.hilt.firebase.livechat

import android.app.Application
import android.content.Intent
import com.ganesh.hilt.firebase.livechat.service.UserStatusService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    companion object {
        lateinit var myApplication: MyApplication
    }

    override fun onCreate() {
        super.onCreate()
        // Register lifecycle observer
        myApplication = this
    }
}