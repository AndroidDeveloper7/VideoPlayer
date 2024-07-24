package com.telegram.videoplayer.downloader.equalizer.eq;

import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.util.Log;

import com.telegram.videoplayer.downloader.utildata.PreferenceUtil;

public class BassBoosts {
    private static BassBoost bassBoost;

    public static void initBass(int i) {
        EndBass();
        try {
            bassBoost = new BassBoost(0, i);
            short s = (short) PreferenceUtil.getInstance().saveEq().getInt(PreferenceUtil.BASS_BOOST, 0);
            if (s > 0) {
                setBassBoostStrength(s);
            } else {
                setBassBoostStrength((short) 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void EndBass() {
        BassBoost bassBoost2 = bassBoost;
        if (bassBoost2 != null) {
            bassBoost2.release();
            bassBoost = null;
        }
    }

    public static void setBassBoostStrength(short s) {
        BassBoost bassBoost2 = bassBoost;
        if (bassBoost2 != null && bassBoost2.getStrengthSupported() && s >= 0 && s <= 1000) {
            try {
                bassBoost.setStrength(s);
                saveBass(s);
            } catch (IllegalArgumentException unused) {
                Log.e("BassBoosts", "Bassboost effect not supported");
            } catch (IllegalStateException unused2) {
                Log.e("BassBoosts", "Bassboost cannot get strength supported");
            } catch (UnsupportedOperationException unused3) {
                Log.e("BassBoosts", "Bassboost library not loaded");
            } catch (RuntimeException unused4) {
                Log.e("BassBoosts", "Bassboost effect not found");
            }
        }
    }

    public static void saveBass(short s) {
        SharedPreferences.Editor edit = PreferenceUtil.getInstance().saveEq().edit();
        edit.putInt(PreferenceUtil.BASS_BOOST, s);
        edit.apply();
        Log.e("strength ", " : " + ((int) s));
        Log.e("savestr ", " : " + ((int) ((short) PreferenceUtil.getInstance().saveEq().getInt(PreferenceUtil.BASS_BOOST, 0))));
    }

    public static void setEnabled(boolean z) {
        BassBoost bassBoost2 = bassBoost;
        if (bassBoost2 != null) {
            bassBoost2.setEnabled(z);
        }
    }
}
