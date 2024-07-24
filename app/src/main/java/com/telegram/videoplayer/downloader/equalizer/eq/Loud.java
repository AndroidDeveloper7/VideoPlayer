package com.telegram.videoplayer.downloader.equalizer.eq;

import android.content.SharedPreferences;
import android.media.audiofx.LoudnessEnhancer;
import android.util.Log;

import com.telegram.videoplayer.downloader.utildata.PreferenceUtil;

public class Loud {
    private static LoudnessEnhancer loudnessEnhancer;

    public static void initLoudnessEnhancer(int i) {
        EndLoudnessEnhancer();
        try {
            loudnessEnhancer = new LoudnessEnhancer(i);
            int i2 = PreferenceUtil.getInstance().saveEq().getInt(PreferenceUtil.LOUD_BOOST, 0);
            setLoudnessEnhancerGain(Math.max(i2, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLoudnessEnhancerGain(int i) {
        LoudnessEnhancer loudnessEnhancer2 = loudnessEnhancer;
        if (loudnessEnhancer2 != null && i >= 0 && i <= 100) {
            try {
                loudnessEnhancer2.setTargetGain(i);
                saveLoudnessEnhancer(i);
            } catch (IllegalArgumentException unused) {
                Log.e(PreferenceUtil.LOUD_BOOST, "Loud effect not supported");
            } catch (IllegalStateException unused2) {
                Log.e(PreferenceUtil.LOUD_BOOST, "Loud cannot get gain supported");
            } catch (UnsupportedOperationException unused3) {
                Log.e(PreferenceUtil.LOUD_BOOST, "Loud library not loaded");
            } catch (RuntimeException unused4) {
                Log.e(PreferenceUtil.LOUD_BOOST, "Loud effect not found");
            }
        }
    }

    public static void EndLoudnessEnhancer() {
        LoudnessEnhancer loudnessEnhancer2 = loudnessEnhancer;
        if (loudnessEnhancer2 != null) {
            loudnessEnhancer2.release();
            loudnessEnhancer = null;
        }
    }

    public static void saveLoudnessEnhancer(int i) {
        SharedPreferences.Editor edit = PreferenceUtil.getInstance().saveEq().edit();
        edit.putInt(PreferenceUtil.LOUD_BOOST, i);
        edit.apply();
    }

    public static void setEnabled(boolean z) {
        LoudnessEnhancer loudnessEnhancer2 = loudnessEnhancer;
        if (loudnessEnhancer2 != null) {
            loudnessEnhancer2.setEnabled(z);
        }
    }
}
