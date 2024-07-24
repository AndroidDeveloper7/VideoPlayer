package com.kabouzeid.appthemehelper.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.kabouzeid.appthemehelper.ATH;
import com.kabouzeid.appthemehelper.ThemeStore;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ATESeekBar extends SeekBar {

    public ATESeekBar(Context context) {
        super(context);
        init(context);
    }

    public ATESeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ATESeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ATH.setTint(this, ThemeStore.accentColor(context));
    }
}
