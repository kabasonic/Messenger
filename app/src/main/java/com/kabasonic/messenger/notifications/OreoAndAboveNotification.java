package com.kabasonic.messenger.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class OreoAndAboveNotification extends ContextWrapper {

    public static final String ID = "id";
    public static final String NAME = "FirebaseApp";

    private NotificationManager notificationManager;

    public OreoAndAboveNotification(Context base){
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel();
        }

    }
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(ID,NAME,NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getNotificationManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getNotificationManager(){
        if(notificationManager!=null){
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public NotificationCompat.Builder getONotifications(String title, String body, PendingIntent pendingIntent, Uri soundUri, String icon) {
        return (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext(), ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setSmallIcon(Integer.parseInt(icon));
    }
}
