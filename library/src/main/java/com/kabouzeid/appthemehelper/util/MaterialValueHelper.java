package com.kabouzeid.appthemehelper.util;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

import com.kabouzeid.appthemehelper.R;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public final class MaterialValueHelper {

    @SuppressLint("PrivateResource")
    @ColorInt
    public static int getPrimaryTextColor(final Context context, boolean dark) {
        if (dark) {
            return ContextCompat.getColor(context, R.color.primary_text_default_material_light);
        }
        return ContextCompat.getColor(context, R.color.primary_text_default_material_light);
    }

    @SuppressLint("PrivateResource")
    @ColorInt
    public static int getSecondaryTextColor(final Context context, boolean dark) {
        if (dark) {
            return ContextCompat.getColor(context, R.color.secondary_text_default_material_light);
        }
        return ContextCompat.getColor(context, R.color.secondary_text_default_material_light);
    }

    private MaterialValueHelper() {
    }
}
