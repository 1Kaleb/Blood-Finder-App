package com.example.bloodfinder.Notifications;

import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.bloodfinder.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String title, message;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        NotificationCompat.Builder notificaiotnBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("New Notification")
                .setContentText("Response to your Blood request");
        //todo push notification from admin to all ... and if notification id from admin no chat

        int notificaitonId = (int) System.currentTimeMillis();
        NotificationManager mnotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mnotiManager.notify(notificaitonId, notificaiotnBuilder.build());

    }
}

