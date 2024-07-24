package com.telegram.videoplayer.downloader.duplicate.utilts;

import android.app.Activity;
import android.os.Build;

import androidx.core.content.ContextCompat;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.DecimalFormat;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Utils {
    public static String formatSize(long j) {
        if (j <= 0) {
            return "";
        }
        double d = (double) j;
        int log10 = (int) (Math.log10(d) / Math.log10(1024.0d));
        StringBuilder sb = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
        double pow = Math.pow(1024.0d, log10);
        Double.isNaN(d);
        Double.isNaN(d);
        sb.append(decimalFormat.format(d / pow));
        sb.append(StringUtils.SPACE);
        sb.append(new String[]{"B", "KB", "MB", "GB", "TB"}[log10]);
        return sb.toString();
    }

    public static File[] getFileList(String str) {
        File file = new File(str);
        if (!file.isDirectory()) {
            return new File[0];
        }
        return file.listFiles();
    }

    public static boolean checkSelfPermission(Activity activity, String str) {
        return isAndroid23() && ContextCompat.checkSelfPermission(activity, str) != 0;
    }

    public static boolean isAndroid23() {
        return Build.VERSION.SDK_INT >= 23;
    }
}
