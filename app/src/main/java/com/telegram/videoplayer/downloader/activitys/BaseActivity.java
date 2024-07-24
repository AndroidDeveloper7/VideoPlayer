package com.telegram.videoplayer.downloader.activitys;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.telegram.videoplayer.downloader.utildata.PreferenceUtil;
import com.kabouzeid.appthemehelper.util.MaterialDialogsUtil;


public class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle bundle) {
        setTheme(PreferenceUtil.getInstance(this).getGeneralTheme());
        super.onCreate(bundle);
        MaterialDialogsUtil.updateMaterialDialogsThemeSingleton(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
