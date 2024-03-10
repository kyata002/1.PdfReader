package com.kyata.pdfreader.services;

import static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kyata.pdfreader.R;
import com.kyata.pdfreader.view.activity.SplashActivity;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Intent intentAct = new Intent(this, SplashActivity.class);
            intentAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            int flag = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                flag = PendingIntent.FLAG_IMMUTABLE;
            }
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.drawable.logo)
                    .setContentIntent(PendingIntent.getActivity(this, 0, intentAct, flag))
                    .setAutoCancel(true);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("notification_channel002",
                        "Notification FCM",
                        NotificationManager.IMPORTANCE_LOW);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0, notificationBuilder.build());
        }
    }

}
