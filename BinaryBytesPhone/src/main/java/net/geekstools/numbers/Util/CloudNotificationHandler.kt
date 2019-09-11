package net.geekstools.numbers.Util

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CloudNotificationHandler : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(">>> ", "From: " + remoteMessage.from!!)
        Log.d(">>> ", "Notification Message Body: " + remoteMessage.notification!!.body!!)
    }
}
