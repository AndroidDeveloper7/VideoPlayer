package com.telegram.videoplayer.downloader.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.telegram.videoplayer.downloader.utildata.API26Wrapper;

public class BootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= 26) {
            API26Wrapper.startForegroundService(context, new Intent(context, BackgroundSoundService.class));
        } else {
            context.startService(new Intent(context, BackgroundSoundService.class));
        }
    }
}
