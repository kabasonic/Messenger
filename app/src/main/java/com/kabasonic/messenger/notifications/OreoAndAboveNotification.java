package com.kabasonic.messenger.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.icu.text.StringPrepParseException;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kabasonic.messenger.R;

public class OreoAndAboveNotification extends ContextWrapper {
    public static final String TAG = "OreoAndAbove";
    public static final String ID = "some_id";
    public static final String NAME = "FirebaseApp";

    private NotificationManager notificationManager;

    public OreoAndAboveNotification(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            createChannel();
        }

    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getONotifications(String title, String body, PendingIntent pendingIntent, Uri soundUri, String icon, Bitmap bitmap) {
        Log.d(TAG, "ICON: " + icon);
        Log.d(TAG, "TITLE: " + title);

        return new Notification.Builder(getApplicationContext(), ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.ic_round_mail_24);
    }
}
