package com.telegram.videoplayer.downloader.utildata;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import androidx.preference.PreferenceManager;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.telegram.videoplayer.downloader.R;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.telegram.videoplayer.downloader.ads.AppOpenManager;

@SuppressWarnings("EmptyMethod")
public class MyApplication extends Application {
    public static final Point displaySize = new Point();
    public static final String prefName = "myPref";
    public static volatile Handler applicationHandler = null;
    public static float density = 1.0f;
    public static Context mContext;
    public static SharedPreferences preferences;
    private static SharedPreferences eqPref;
    private static MyApplication instance;
    private static SharedPreferences mPreferences;
    public SharedPreferences.Editor editor;

    public static SharedPreferences getmPreferences() {
        return mPreferences;
    }

    public static SharedPreferences getEqPref() {
        return eqPref;
    }

    public static MyApplication getInstance() {
        return instance;
    }


    public static Context getAppContext() {
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }


    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
        Context applicationContext = getApplicationContext();
        mContext = applicationContext;

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                new AppOpenManager(MyApplication.this);
            }
        });

        mPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        eqPref = mContext.getSharedPreferences(PreferenceUtil.SAVE_EQ, 0);
        SharedPreferences sharedPreferences = getSharedPreferences(prefName, 0);
        preferences = sharedPreferences;
        this.editor = sharedPreferences.edit();
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
        if (!ThemeStore.isConfigured(this, 1)) {
            ThemeStore.editTheme(this).primaryColorRes(R.color.colorAccent).accentColorRes(R.color.colorAccent).commit();
        }
        applicationHandler = new Handler(mContext.getMainLooper());
        checkDisplaySize();
        density = mContext.getResources().getDisplayMetrics().density;
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(new DisplayImageOptions.Builder().considerExifParams(true).resetViewBeforeLoading(true).showImageOnLoading(R.drawable.nophotos).showImageOnFail(R.drawable.nophotos).delayBeforeLoading(0).build()).memoryCacheExtraOptions(480, 800).threadPoolSize(5).build());
    }

    public void onTerminate() {
        super.onTerminate();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    @SuppressWarnings("deprecation")
    public void checkDisplaySize() {
        Display defaultDisplay;
        try {
            WindowManager windowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
            if (windowManager != null && (defaultDisplay = windowManager.getDefaultDisplay()) != null) {
                if (Build.VERSION.SDK_INT < 13) {
                    displaySize.set(defaultDisplay.getWidth(), defaultDisplay.getHeight());
                } else {
                    defaultDisplay.getSize(displaySize);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
