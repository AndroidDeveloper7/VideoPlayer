package com.telegram.videoplayer.downloader.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.activitys.DashBoardActivity;
import com.telegram.videoplayer.downloader.utildata.API23Wrapper;
import com.telegram.videoplayer.downloader.utildata.API26Wrapper;
import com.simplemobiletools.commons.helpers.ConstantsKt;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@SuppressWarnings("EmptyMethod")
public class SensorListener extends Service {

    @SuppressLint("WrongConstant")
    public static Notification getNotification(Context context) {
        Notification.Builder notificationBuilder = Build.VERSION.SDK_INT >= 26 ? API26Wrapper.getNotificationBuilder(context) : new Notification.Builder(context);
        notificationBuilder.setContent(getComplexNotificationView(context));
        notificationBuilder.setPriority(2).setShowWhen(false).setContent(getComplexNotificationView(context)).setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, DashBoardActivity.class), ConstantsKt.LICENSE_SMS_MMS)).setSmallIcon(R.mipmap.ic_launcher).setOngoing(true).setAutoCancel(false);
        return notificationBuilder.build();
    }

    private static RemoteViews getComplexNotificationView(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_small);
        remoteViews.setImageViewResource(R.id.imageView2, R.drawable.previous);
        remoteViews.setImageViewResource(R.id.imageView3, R.drawable.pause);
        remoteViews.setImageViewResource(R.id.imageView4, R.drawable.forward);
        remoteViews.setImageViewResource(R.id.imageView5, R.drawable.ic_close_black_24dp);
        return remoteViews;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        Calendar instance = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        Log.e("servicesec", "onStartCommand:fdfff " + simpleDateFormat.format(instance.getTime()));
        showNotification();
        @SuppressLint("WrongConstant") AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(NotificationCompat.CATEGORY_ALARM);
        PendingIntent service = PendingIntent.getService(getApplicationContext(), 2, new Intent(this, SensorListener.class), ConstantsKt.LICENSE_SMS_MMS);
        if (Build.VERSION.SDK_INT >= 23) {
            API23Wrapper.setAlarmWhileIdle(alarmManager, 1, System.currentTimeMillis() + 3600000, service);
        } else {
            alarmManager.set(1, System.currentTimeMillis() + 3600000, service);
        }
        return 1;
    }

    private void showNotification() {
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(1, getNotification(this));
        } else if (getSharedPreferences("pedometer", 0).getBoolean(NOTIFICATION_SERVICE, true)) {
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(1, getNotification(this));
        }
    }

    public void onCreate() {
        super.onCreate();
    }

    public void onTaskRemoved(Intent intent) {
        super.onTaskRemoved(intent);
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
