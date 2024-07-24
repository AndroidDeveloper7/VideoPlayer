package com.telegram.videoplayer.downloader.utildata;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ScreenUtility {
    public static final String DPI = "dpi";
    public static final String PIXEL = "pixel";
    public static final String fragTagVideo = "video";

    public static float getDeviceWidth(Context context, String str) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        str.hashCode();
        if (str.equals(DPI)) {
            return displayMetrics.xdpi;
        }
        if (!str.equals(PIXEL)) {
            return -1.0f;
        }
        return (float) displayMetrics.widthPixels;
    }
}
