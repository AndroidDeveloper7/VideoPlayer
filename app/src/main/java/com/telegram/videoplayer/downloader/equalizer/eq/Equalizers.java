package com.telegram.videoplayer.downloader.equalizer.eq;

import android.content.SharedPreferences;
import android.media.audiofx.Equalizer;

import com.telegram.videoplayer.downloader.utildata.PreferenceUtil;

import org.apache.commons.lang3.StringUtils;

public class Equalizers {
    private static Equalizer equalizer;
    private static short preset;

    public static void initEq(int i) {
        EndEq();
        try {
            equalizer = new Equalizer(0, i);
            short s = (short) PreferenceUtil.getInstance().saveEq().getInt(PreferenceUtil.SAVE_PRESET, 0);
            preset = s;
            if (s < equalizer.getNumberOfPresets()) {
                usePreset(preset);
                return;
            }
            for (short s2 = 0; s2 < equalizer.getNumberOfBands(); s2 = (short) (s2 + 1)) {
                SharedPreferences saveEq = PreferenceUtil.getInstance().saveEq();
                setBandLevel(s2, (short) saveEq.getInt(PreferenceUtil.BAND_LEVEL + ((int) s2), 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void EndEq() {
        Equalizer equalizer2 = equalizer;
        if (equalizer2 != null) {
            equalizer2.release();
            equalizer = null;
        }
    }

    public static short[] getBandLevelRange() {
        Equalizer equalizer2 = equalizer;
        if (equalizer2 != null) {
            return equalizer2.getBandLevelRange();
        }
        return null;
    }

    public static short getPresetNo() {
        Equalizer equalizer2 = equalizer;
        if (equalizer2 != null) {
            return equalizer2.getNumberOfPresets();
        }
        return 0;
    }

    public static String getPresetNames(short s) {
        Equalizer equalizer2 = equalizer;
        return equalizer2 != null ? equalizer2.getPresetName(s) : StringUtils.SPACE;
    }

    public static short getBandLevel(short s) {
        Equalizer equalizer2 = equalizer;
        if (equalizer2 == null) {
            return 0;
        }
        return equalizer2.getBandLevel(s);
    }

    public static void setEnabled(boolean z) {
        Equalizer equalizer2 = equalizer;
        if (equalizer2 != null) {
            equalizer2.setEnabled(z);
        }
    }

    public static void setBandLevel(short s, short s2) {
        Equalizer equalizer2 = equalizer;
        if (equalizer2 != null) {
            equalizer2.setBandLevel(s, s2);
        }
    }

    public static void usePreset(short s) {
        Equalizer equalizer2 = equalizer;
        if (equalizer2 != null && s >= 0) {
            try {
                preset = s;
                equalizer2.usePreset(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static short getNumberOfBands() {
        Equalizer equalizer2 = equalizer;
        if (equalizer2 != null) {
            return equalizer2.getNumberOfBands();
        }
        return 0;
    }

    public static int getCenterFreq(short s) {
        Equalizer equalizer2 = equalizer;
        if (equalizer2 != null) {
            return equalizer2.getCenterFreq(s);
        }
        return 0;
    }

    public static void savePrefs(int i, int i2) {
        if (equalizer != null) {
            SharedPreferences.Editor edit = PreferenceUtil.getInstance().saveEq().edit();
            edit.putInt(PreferenceUtil.BAND_LEVEL + i, i2);
            edit.apply();
        }
    }
}
