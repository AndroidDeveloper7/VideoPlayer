package com.telegram.videoplayer.downloader.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

@SuppressWarnings("EmptyMethod")
public class MyService extends Service {
    public static long millisecs;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        new CountDownTimer(millisecs, 1000) {

            public void onTick(long j) {
                Log.e("Count ", " : :" + j);
            }

            public void onFinish() {
                Log.e("Finish ", " : :");
                MyService.this.stopSelf();
            }
        }.start();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
