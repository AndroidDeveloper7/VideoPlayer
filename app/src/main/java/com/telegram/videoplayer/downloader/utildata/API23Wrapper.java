package com.telegram.videoplayer.downloader.utildata;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class API23Wrapper {

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setAlarmWhileIdle(AlarmManager alarmManager, int i, long j, PendingIntent pendingIntent) {
        alarmManager.setAndAllowWhileIdle(i, j, pendingIntent);
    }
}
