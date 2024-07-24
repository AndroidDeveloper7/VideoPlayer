package com.telegram.videoplayer.downloader.utildata;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class API26Wrapper {
    public static final String NOTIFICATION_CHANNEL_ID = "Notification";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void startForegroundService(Context context, Intent intent) {
        context.startForegroundService(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Notification.Builder getNotificationBuilder(Context context) {
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_NONE);
        notificationChannel.setImportance(NotificationManager.IMPORTANCE_MIN);
        notificationChannel.enableLights(false);
        notificationChannel.enableVibration(false);
        notificationChannel.setBypassDnd(false);
        notificationChannel.setSound(null, null);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
        return new Notification.Builder(context, NOTIFICATION_CHANNEL_ID);
    }

}
