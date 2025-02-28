package com.kabouzeid.appthemehelper.util;

import android.graphics.Color;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public final class ColorUtil {

    public static int stripAlpha(@ColorInt int color) {
        return 0xff000000 | color;
    }

    @ColorInt
    public static int shiftColor(@ColorInt int color, @FloatRange(from = 0.0f, to = 2.0f) float by) {
        if (by == 1f) return color;
        int alpha = Color.alpha(color);
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= by; // value component
        return (alpha << 24) + (0x00ffffff & Color.HSVToColor(hsv));
    }

    @ColorInt
    public static int darkenColor(@ColorInt int color) {
        return shiftColor(color, 0.9f);
    }

    public static boolean isColorLight(@ColorInt int color) {
        final double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.4;
    }

    @ColorInt
    public static int adjustAlpha(@ColorInt int color, @FloatRange(from = 0.0, to = 1.0) float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    private ColorUtil() {
    }
}
