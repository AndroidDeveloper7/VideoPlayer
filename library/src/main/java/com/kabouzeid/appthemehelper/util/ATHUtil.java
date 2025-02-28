package com.kabouzeid.appthemehelper.util;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;

/**
 * @author Aidan Follestad (afollestad)
 */
@SuppressWarnings("ConstantConditions")
public final class ATHUtil {

    public static boolean isWindowBackgroundDark(Context context) {
        return !ColorUtil.isColorLight(ATHUtil.resolveColor(context, android.R.attr.windowBackground));
    }

    public static int resolveColor(Context context, @AttrRes int attr) {
        return resolveColor(context, attr, 0);
    }

    public static int resolveColor(Context context, @AttrRes int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getColor(0, fallback);
        } finally {
            a.recycle();
        }
    }

    public static Class<?> inClassPath(@NonNull String clsName) {
        try {
            return Class.forName(clsName);
        } catch (Throwable t) {
            throw new IllegalStateException(String.format("%s is not in your class path! You must include the associated library.", clsName));
        }
    }

    private ATHUtil() {
    }
}