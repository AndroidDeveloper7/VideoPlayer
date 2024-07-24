package com.kabouzeid.appthemehelper.common.prefs.supportv7;

import android.content.Context;
import androidx.preference.EditTextPreference;
import android.util.AttributeSet;

import com.kabouzeid.appthemehelper.R;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ATEEditTextPreference extends EditTextPreference {

    public ATEEditTextPreference(Context context) {
        super(context);
        init();
    }

    public ATEEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ATEEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ATEEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setLayoutResource(R.layout.ate_preference_custom_support);
    }
}
