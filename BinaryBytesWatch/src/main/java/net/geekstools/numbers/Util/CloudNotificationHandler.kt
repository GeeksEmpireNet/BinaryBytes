package net.geekstools.numbers.Util

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CloudNotificationHandler : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
    }
}
