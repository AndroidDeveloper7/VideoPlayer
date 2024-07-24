package com.telegram.videoplayer.downloader.utildata;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.telegram.videoplayer.downloader.R;

@SuppressWarnings({"ResultOfMethodCallIgnored", "SameReturnValue"})
public class PreferenceUtil {
    public static final String AUTOPLAYNEXT = "autoplayon";
    public static final String BAND_LEVEL = "level";
    public static final String BASS_BOOST = "BassBoost";
    public static final String BATTERYLOCK = "batterylock";
    public static final String EQSWITCH = "eqswitch";
    public static final String GENERAL_THEME = "general_theme";
    public static final String LAST_SLEEP_TIMER_VALUE = "last_sleep_timer_value";
    public static final String LOUD_BOOST = "Loud";
    public static final String PRESET_POS = "spinner_position";
    public static final String SAVE_EQ = "Equalizers";
    public static final String SAVE_PRESET = "preset";
    public static final String VIDEOPOSITION = "videoposition";
    public static final String VIDEOURL = "videourl";
    public static final String VIRTUAL_BOOST = "VirtualBoost";
    private static final String FOLDER_SORT_ORDER = "folder_sort_order";
    private static final String FOLDER_VIEW_TYPE = "folder_view_type";
    private static final String LAST_BRIGHTNESS = "last_brightness";
    private static final String LAST_SPEED = "last_speed";
    private static final String LOCK = "lock";
    private static final String LOCK_VIDEO = "lock_lock";
    private static final String List_VIEW_TYPE = "list_view_type";
    private static final String ORIENTATION = "orientation";
    private static final String RECYCLE_VIDEO = "recycle_lock";
    private static final String REPEAT_ONE_VIDEO = "repeat_one";
    private static final String RESBOOl = "resumebool";
    private static final String RESUMEVID = "resumevideo";
    private static final String VIEW_TYPE = "view_type";
    private static final String NOTIFICATION = "notification";
    private static PreferenceUtil sInstance;
    private final SharedPreferences mPreferences;

    public PreferenceUtil(Context context) {
        this.mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceUtil getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceUtil(context.getApplicationContext());
        }
        return sInstance;
    }

    public static PreferenceUtil getInstance() {
        return sInstance;
    }

    public static int getThemeResFromPrefValue1(String str) {
        str.hashCode();
        return str.equals("dark") ? 1 : 0;
    }

    public static int getThemeResFromPrefValue(String str) {
        str.hashCode();
        return R.style.Theme_Phonograph;
    }

    public void saveLastBrightness(float f) {
        this.mPreferences.edit().putFloat(LAST_BRIGHTNESS, f).apply();
    }


    public boolean getAutoplaynext() {
        return this.mPreferences.getBoolean(AUTOPLAYNEXT, true);
    }

    public void setAutoplaynext(Boolean bool) {
        this.mPreferences.edit().putBoolean(AUTOPLAYNEXT, bool).apply();
    }

    public boolean getBatterylock() {
        return this.mPreferences.getBoolean(BATTERYLOCK, true);
    }

    public void setBatterylock(Boolean bool) {
        this.mPreferences.edit().putBoolean(BATTERYLOCK, bool).apply();
    }

    public float getLastBrightness() {
        return this.mPreferences.getFloat(LAST_BRIGHTNESS, 0.5f);
    }

    public void saveLastSpeed(float f) {
        this.mPreferences.edit().putFloat(LAST_SPEED, f).apply();
    }

    public float getLastSpeed() {
        return this.mPreferences.getFloat(LAST_SPEED, 1.0f);
    }

    public void saveSortOrder(int i) {
        this.mPreferences.edit().putInt("sort_order", i).apply();
    }

    public int getSortOrder() {
        return this.mPreferences.getInt("sort_order", 0);
    }

    public void saveOrientation(int i) {
        this.mPreferences.edit().putInt(ORIENTATION, i).apply();
    }

    public int getOrientation() {
        return this.mPreferences.getInt(ORIENTATION, 2);
    }

    public int getResumestatus() {
        return this.mPreferences.getInt(RESUMEVID, 2);
    }

    public void saveResumestatus(int i) {
        this.mPreferences.edit().putInt(RESUMEVID, i).apply();
    }

    public void saveFolderSortOrder(int i) {
        this.mPreferences.edit().putInt(FOLDER_SORT_ORDER, i).apply();
    }

    public int getFolderSortOrder() {
        return this.mPreferences.getInt(FOLDER_SORT_ORDER, 0);
    }

    public void saveFolderAsc(Boolean bool) {
        this.mPreferences.edit().putBoolean(FOLDER_VIEW_TYPE, bool).apply();
    }

    public void saveResumBool(Boolean bool) {
        this.mPreferences.edit().putBoolean(RESBOOl, bool).apply();
    }

    public boolean getResumBool() {
        return this.mPreferences.getBoolean(RESBOOl, true);
    }

    public boolean getFolderAsc() {
        return this.mPreferences.getBoolean(FOLDER_VIEW_TYPE, true);
    }

    public void saveListAsc(Boolean bool) {
        this.mPreferences.edit().putBoolean(List_VIEW_TYPE, bool).apply();
    }

    public boolean getListAsc() {
        return this.mPreferences.getBoolean(List_VIEW_TYPE, true);
    }

    public void saveRepeatOne(Boolean bool) {
        this.mPreferences.edit().putBoolean(REPEAT_ONE_VIDEO, bool).apply();
    }


    public void saveViewType(Boolean bool) {
        this.mPreferences.edit().putBoolean(VIEW_TYPE, bool).apply();
    }

    public boolean getViewType() {
        return this.mPreferences.getBoolean(VIEW_TYPE, true);
    }

    public boolean getLock() {
        return this.mPreferences.getBoolean(LOCK, false);
    }

    public void setLock(Boolean bool) {
        this.mPreferences.edit().putBoolean(LOCK, bool).apply();
    }

    public String getVideoURL() {
        return this.mPreferences.getString(VIDEOURL, "");
    }

    public void setVideoURL(String str) {
        this.mPreferences.edit().putString(VIDEOURL, str).apply();
    }

    public String getLockVideo() {
        return this.mPreferences.getString(LOCK_VIDEO, "");
    }

    public void setLockVideo(String str) {
        this.mPreferences.edit().putString(LOCK_VIDEO, str).apply();
    }

    public String getRecycleVideo() {
        return this.mPreferences.getString(RECYCLE_VIDEO, "");
    }

    public void setRecycleVideo(String str) {
        this.mPreferences.edit().putString(RECYCLE_VIDEO, str).apply();
    }

    public void setVideoPosition(int i) {
        this.mPreferences.edit().putInt(VIDEOPOSITION, i).apply();
    }


    public SharedPreferences saveEq() {
        return MyApplication.getEqPref();
    }

    public void eqSwitch(Boolean bool) {
        SharedPreferences.Editor edit = saveEq().edit();
        edit.putBoolean(EQSWITCH, bool);
        edit.apply();
    }

    public boolean geteqSwitch() {
        return saveEq().getBoolean(EQSWITCH, false);
    }

    public int getPresetPos() {
        return MyApplication.getmPreferences().getInt(PRESET_POS, 0);
    }

    public void savePresetPos(int i) {
        SharedPreferences.Editor edit = MyApplication.getmPreferences().edit();
        edit.putInt(PRESET_POS, i);
        edit.apply();
    }

    public void setResumeVideotime(Long l, String str) {
        SharedPreferences.Editor edit = this.mPreferences.edit();
        edit.putLong("r_" + str, l);
        edit.apply();
    }

    public long getresumeVideotime(String str) {
        return this.mPreferences.getLong("r_" + str, 0);
    }

    public void setIsPlayVideo(Boolean bool, String str) {
        SharedPreferences.Editor edit = this.mPreferences.edit();
        edit.putBoolean("isplay" + str, bool);
        edit.apply();
    }

    public boolean getIsPlayVideo(String str) {
        return this.mPreferences.getBoolean("isplay" + str, false);
    }


    public int getGeneralTheme() {
        return getThemeResFromPrefValue(this.mPreferences.getString(GENERAL_THEME, "light"));
    }


    public int getGeneralTheme1() {
        return getThemeResFromPrefValue1(this.mPreferences.getString(GENERAL_THEME, "light"));
    }

    public int getLastSleepTimerValue() {
        return this.mPreferences.getInt(LAST_SLEEP_TIMER_VALUE, 30);
    }

    public void setLastSleepTimerValue(int i) {
        SharedPreferences.Editor edit = this.mPreferences.edit();
        edit.putInt(LAST_SLEEP_TIMER_VALUE, i);
        edit.apply();
    }


    public void setNotification(Boolean bool) {
        this.mPreferences.edit().putBoolean(NOTIFICATION, bool).apply();
    }

    public boolean getNotification() {
        return this.mPreferences.getBoolean(NOTIFICATION, false);
    }

}
