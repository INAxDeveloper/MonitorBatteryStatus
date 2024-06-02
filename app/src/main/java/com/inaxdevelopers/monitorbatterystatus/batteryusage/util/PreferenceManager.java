package com.inaxdevelopers.monitorbatterystatus.batteryusage.util;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferenceManager {
    public static final String FCM_ID = "fcm_id";
    public static final String PREF_LIST_SORT = "sort_list";
    private static final String PREF_NAME = "preference_application";
    public static final String PREF_SETTINGS_HIDE_SYSTEM_APPS = "hide_system_apps";
    public static final String PREF_SETTINGS_HIDE_UNINSTALL_APPS = "hide_uninstall_apps";
    private static PreferenceManager mManager;
    private static SharedPreferences mShare;

    private PreferenceManager() {
    }

    public static void init(Context context) {
        mManager = new PreferenceManager();
        mShare = context.getSharedPreferences(PREF_NAME, 0);
    }

    public static PreferenceManager getInstance() {
        return mManager;
    }

    public void putBoolean(String str, boolean z) {
        mShare.edit().putBoolean(str, z).apply();
    }

    public void putInt(String str, int i) {
        mShare.edit().putInt(str, i).apply();
    }

    public boolean getBoolean(String str) {
        return mShare.getBoolean(str, false);
    }

    public int getInt(String str) {
        return mShare.getInt(str, 0);
    }

    public boolean getUninstallSettings(String str) {
        return mShare.getBoolean(str, true);
    }

    public boolean getSystemSettings(String str) {
        return mShare.getBoolean(str, true);
    }

    public void putString(String str, String str2) {
        mShare.edit().putString(str, str2).apply();
    }

    public String getString(String str) {
        return mShare.getString(str, "");
    }
}
