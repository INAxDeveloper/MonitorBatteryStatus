package com.inaxdevelopers.monitorbatterystatus.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyUtils {
    public static void showMessage(Context context, String str) {
        Toast makeText = Toast.makeText(context, "" + str, Toast.LENGTH_SHORT);
        makeText.setGravity(17, 0, 0);
        makeText.show();
    }

    public static void setIsSERVICE_KEY(Context context, boolean z) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putBoolean("SERVICE_KEY", z);
        edit.commit();
    }

    public static boolean getIsSERVICE_KEY(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("SERVICE_KEY", false);
    }

    public static void setBatteryFullAlert(Context context, boolean z) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putBoolean("BatteryFullAlert", z);
        edit.commit();
    }

    public static boolean getBatteryFullAlert(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("BatteryFullAlert", false);
    }

    public static void setIsRinging(Context context, boolean z) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putBoolean("IsRinging", z);
        edit.commit();
    }

    public static boolean getIsRinging(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("IsRinging", false);
    }

    public static String getFormatedDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
    }

    public static String getFormatedTime(Date date) {
        return new SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(date);
    }

    public static String getFormatedTimeSS(Date date) {
        return new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(date);
    }

    public static String convertMilliSecondsToFormattedDate(long j) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String formatNumber(float f) {
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMinimumFractionDigits(1);
        return numberInstance.format(f);
    }

    public static boolean isMyServiceRunning(Class<?> cls, Context context) {
        for (ActivityManager.RunningServiceInfo runningServiceInfo : ((ActivityManager) context.getSystemService("activity")).getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getHealthString(int i) {
        return i == 2 ? "Good" : i == 3 ? "Over Heat" : i == 4 ? "Dead" : i != 5 ? i != 6 ? Constants.label_unknown : "Failure" : "Over Voltage";
    }

    public static String getPlugTypeString(int i) {
        String str = Constants.label_unknown;
        if (i == 1) {
            return Constants.plug_type_ac;
        }
        if (i == 2) {
            return Constants.plug_type_usb;
        }
        return i != 4 ? str : Constants.plug_type_wireless;
    }

    public static String getStatusString(int i) {
        return i == 2 ? "Charging" : i == 3 ? "Discharging" : i != 4 ? i != 5 ? Constants.label_unknown : "Full" : "Not Charging";
    }
}
