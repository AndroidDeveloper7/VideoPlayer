package com.kabouzeid.appthemehelper.common.prefs.supportv7;

import android.content.Context;
import androidx.preference.ListPreference;
import android.util.AttributeSet;

import com.kabouzeid.appthemehelper.R;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ATEListPreference extends ListPreference {

    public ATEListPreference(Context context) {
        super(context);
        init();
    }

    public ATEListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ATEListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ATEListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setLayoutResource(R.layout.ate_preference_custom_support);
        if (getSummary() == null || getSummary().toString().trim().isEmpty())
            setSummary("%s");
    }
}
