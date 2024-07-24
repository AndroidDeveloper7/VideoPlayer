package com.kabouzeid.appthemehelper.common.prefs;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.preference.Preference;
import android.util.AttributeSet;

import com.kabouzeid.appthemehelper.R;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ATEPreference extends Preference {

    public ATEPreference(Context context) {
        super(context);
        init();
    }

    public ATEPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ATEPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ATEPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setLayoutResource(R.layout.ate_preference_custom);
    }
}