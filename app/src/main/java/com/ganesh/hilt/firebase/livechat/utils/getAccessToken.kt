package com.ganesh.hilt.firebase.livechat.utils

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun getAccessToken(context: Context, callBack: (String?) -> Unit) {

    CoroutineScope(Dispatchers.IO).launch {
        val googleCredentials = GoogleCredentials
            .fromStream(context.assets.open("service_account.json"))
            .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
        googleCredentials.refresh()
        val token = googleCredentials.getAccessToken().tokenValue

        withContext(Dispatchers.Main) {
            callBack(token)
        }
    }
}

