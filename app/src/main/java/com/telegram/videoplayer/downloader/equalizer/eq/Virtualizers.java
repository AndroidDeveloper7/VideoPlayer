package com.telegram.videoplayer.downloader.equalizer.eq;

import android.content.SharedPreferences;
import android.media.audiofx.Virtualizer;
import android.util.Log;

import com.telegram.videoplayer.downloader.utildata.PreferenceUtil;

public class Virtualizers {
    private static Virtualizer virtualizer;

    public static void initVirtualizer(int i) {
        EndVirtual();
        try {
            virtualizer = new Virtualizer(0, i);
            short s = (short) PreferenceUtil.getInstance().saveEq().getInt(PreferenceUtil.VIRTUAL_BOOST, 0);
            if (s > 0) {
                setVirtualizerStrength(s);
            } else {
                setVirtualizerStrength((short) 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setVirtualizerStrength(short s) {
        Virtualizer virtualizer2 = virtualizer;
        if (virtualizer2 != null && virtualizer2.getStrengthSupported() && s >= 0 && s <= 1000) {
            try {
                virtualizer.setStrength(s);
                saveVirtual(s);
            } catch (IllegalArgumentException unused) {
                Log.e("Virtualizers", "Virtualizers effect not supported");
            } catch (IllegalStateException unused2) {
                Log.e("Virtualizers", "Virtualizers cannot get strength supported");
            } catch (UnsupportedOperationException unused3) {
                Log.e("Virtualizers", "Virtualizers library not loaded");
            } catch (RuntimeException unused4) {
                Log.e("Virtualizers", "Virtualizers effect not found");
            }
        }
    }

    public static void EndVirtual() {
        Virtualizer virtualizer2 = virtualizer;
        if (virtualizer2 != null) {
            virtualizer2.release();
            virtualizer = null;
        }
    }

    public static void saveVirtual(Short sh) {
        SharedPreferences.Editor edit = PreferenceUtil.getInstance().saveEq().edit();
        edit.putInt(PreferenceUtil.VIRTUAL_BOOST, sh);
        edit.apply();
    }

    public static void setEnabled(boolean z) {
        Virtualizer virtualizer2 = virtualizer;
        if (virtualizer2 != null) {
            virtualizer2.setEnabled(z);
        }
    }
}
