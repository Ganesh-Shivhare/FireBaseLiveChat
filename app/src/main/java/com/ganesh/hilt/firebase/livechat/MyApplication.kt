package com.ganesh.hilt.firebase.livechat

import android.app.Application
import android.content.Intent
import android.os.Build
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(Intent(this, UserStatusService::class.java))
        }
    }
}