package com.kabouzeid.appthemehelper.common.views;

import android.content.Context;
import androidx.appcompat.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.View;

import com.kabouzeid.appthemehelper.ATH;
import com.kabouzeid.appthemehelper.ThemeStore;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ATESwitch extends SwitchCompat {

    public ATESwitch(Context context) {
        super(context);
        init(context);
    }

    public ATESwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ATESwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ATH.setTint(this, ThemeStore.accentColor(context));
    }

    @Override
    public boolean isShown() {
        return getParent() != null && getVisibility() == View.VISIBLE;
    }
}
