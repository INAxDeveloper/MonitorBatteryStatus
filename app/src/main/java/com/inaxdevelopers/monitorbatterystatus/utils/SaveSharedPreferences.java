package com.inaxdevelopers.monitorbatterystatus.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SaveSharedPreferences {
    private Context context;
    private String count = "launching_counts";
    private String maxCAmps = "max_charging_amps";
    private String maxCapacity = "max_battery_capacity";
    private String maxDAmps = "max_discharging_amps";
    private String maxTemp = "max_temp";
    private String minCAmps = "min_charging_amps";
    private String minDAmps = "min_discharging_amps";
    private String minTemp = "min_temp";
    private String tempPref = "temperature_preference";

    public SaveSharedPreferences(Context context) {
        this.context = context;
    }

    public void saveMaxCapacity(long j) {
        SharedPreferences.Editor edit = this.context.getSharedPreferences("SharedPreferences", 0).edit();
        edit.putLong(this.maxCapacity, j);
        edit.apply();
    }

    public void saveMaxChargeAmps(int i) {
        SharedPreferences.Editor edit = this.context.getSharedPreferences("SharedPreferences", 0).edit();
        edit.putInt(this.maxCAmps, i);
        edit.apply();
    }

    public void saveMinChargeAmps(int i) {
        SharedPreferences.Editor edit = this.context.getSharedPreferences("SharedPreferences", 0).edit();
        edit.putInt(this.minCAmps, i);
        edit.apply();
    }

    public void saveMaxDischargeAmps(int i) {
        SharedPreferences.Editor edit = this.context.getSharedPreferences("SharedPreferences", 0).edit();
        edit.putInt(this.maxDAmps, i);
        edit.apply();
    }

    public void saveMinDischargeAmps(int i) {
        SharedPreferences.Editor edit = this.context.getSharedPreferences("SharedPreferences", 0).edit();
        edit.putInt(this.minDAmps, i);
        edit.apply();
    }

    public void saveTempPref(boolean z) {
        SharedPreferences.Editor edit = this.context.getSharedPreferences("SharedPreferences", 0).edit();
        edit.putBoolean(this.tempPref, z);
        edit.apply();
    }

    public void increaseCount(int i) {
        SharedPreferences.Editor edit = this.context.getSharedPreferences("SharedPreferences", 0).edit();
        edit.putInt(this.count, i);
        edit.apply();
    }

    public float getMaxCapacity() {
        return (float) this.context.getSharedPreferences("SharedPreferences", 0).getLong(this.maxCapacity, 0L);
    }

    public int getMaxChargeAmps() {
        return this.context.getSharedPreferences("SharedPreferences", 0).getInt(this.maxCAmps, 0);
    }

    public int getMinChargeAmps() {
        return this.context.getSharedPreferences("SharedPreferences", 0).getInt(this.minCAmps, 500);
    }

    public int getMaxDischargeAmps() {
        return this.context.getSharedPreferences("SharedPreferences", 0).getInt(this.maxDAmps, 0);
    }

    public int getMinDischargeAmps() {
        return this.context.getSharedPreferences("SharedPreferences", 0).getInt(this.minDAmps, 500);
    }

    public boolean isCelsius() {
        return this.context.getSharedPreferences("SharedPreferences", 0).getBoolean(this.tempPref, true);
    }

    public int getCount() {
        return this.context.getSharedPreferences("SharedPreferences", 0).getInt(this.count, 0);
    }

    public void resetData() {
        SharedPreferences.Editor edit = this.context.getSharedPreferences("SharedPreferences", 0).edit();
        edit.clear();
        edit.apply();
    }
}
