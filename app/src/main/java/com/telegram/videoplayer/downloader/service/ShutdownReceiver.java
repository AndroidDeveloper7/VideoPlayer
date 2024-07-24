package com.telegram.videoplayer.downloader.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ShutdownReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "sutdown", Toast.LENGTH_SHORT).show();
        try {
            context.startService(new Intent(context, BackgroundSoundService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
