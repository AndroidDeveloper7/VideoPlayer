package com.telegram.videoplayer.downloader.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.telegram.videoplayer.downloader.Adshandler;
import com.telegram.videoplayer.downloader.R;

public final class SplashActivity extends AppCompatActivity {

    @SuppressWarnings("EmptyMethod")
    public final void setPrefenrencUtils() {
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(1024);
        SharedPreferences sharedPreferences = getSharedPreferences("EASYMONEY", 0);
        SplashActivity splashActivity = this;
        setPrefenrencUtils();
        Adshandler.loadad(this);
        new Handler().postDelayed(() -> startActivity(new Intent(SplashActivity.this, MainActivity.class)), 3000);
    }

}
