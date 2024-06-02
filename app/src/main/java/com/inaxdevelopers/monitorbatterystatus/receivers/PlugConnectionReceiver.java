package com.inaxdevelopers.monitorbatterystatus.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.core.app.NotificationCompat;

import com.inaxdevelopers.monitorbatterystatus.acitvites.BatteryFullAlertActivity;
import com.inaxdevelopers.monitorbatterystatus.database.DatabaseHelper;
import com.inaxdevelopers.monitorbatterystatus.utils.MyUtils;

import java.util.Calendar;


public class PlugConnectionReceiver extends BroadcastReceiver {
    Context context1;
    DatabaseHelper databaseHelper;
    String mHealth;
    int mLevel;
    int mScale;
    String mStatus;
    String mTechnology;
    float mVoltage;
    String plugged = "";

    @Override 
    public void onReceive(Context context, Intent intent) {
        Intent registerReceiver;
        Intent registerReceiver2;
        this.context1 = context;
        this.databaseHelper = new DatabaseHelper(context);
        if (intent.getAction() != null) {
            Calendar calendar = Calendar.getInstance();
            if (MyUtils.getBatteryFullAlert(context) && intent.getAction().equals("android.intent.action.BATTERY_CHANGED") && (registerReceiver2 = context.getApplicationContext().registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"))) != null) {
                int intExtra = registerReceiver2.getIntExtra("level", -1);
                String plugTypeString = MyUtils.getPlugTypeString(registerReceiver2.getIntExtra("plugged", -1));
                if (!MyUtils.getIsRinging(context) && intExtra == 100 && !plugTypeString.equals("Unknown")) {
                    MyUtils.setIsRinging(context, true);
                    Intent intent2 = new Intent(context, BatteryFullAlertActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                }
            }
            if (intent.getAction().equals("android.intent.action.ACTION_POWER_CONNECTED")) {
                Intent registerReceiver3 = context.getApplicationContext().registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
                if (registerReceiver3 != null) {
                    MyUtils.setIsRinging(context, false);
                    this.plugged = MyUtils.getPlugTypeString(registerReceiver3.getIntExtra("plugged", -1));
                    this.mHealth = MyUtils.getHealthString(registerReceiver3.getIntExtra("health", 0));
                    this.mLevel = registerReceiver3.getIntExtra("level", -1);
                    this.mScale = registerReceiver3.getIntExtra("scale", -1);
                    this.mTechnology = registerReceiver3.getExtras().getString("technology");
                    this.mVoltage = registerReceiver3.getIntExtra("voltage", 0) / 1000.0f;
                    String statusString = MyUtils.getStatusString(registerReceiver3.getIntExtra(NotificationCompat.CATEGORY_STATUS, 0));
                    this.mStatus = statusString;
                    if (statusString.equals("Discharging")) {
                        this.mStatus = "Charging";
                    }
                    if (MyUtils.getIsSERVICE_KEY(context)) {
                        return;
                    }
                    this.databaseHelper.addHistoryData(MyUtils.getFormatedDate(calendar.getTime()), MyUtils.getFormatedTime(calendar.getTime()), this.plugged, this.mHealth, this.mStatus, String.valueOf(this.mLevel), this.mVoltage);
                }
            } else if (!intent.getAction().equals("android.intent.action.ACTION_POWER_DISCONNECTED") || (registerReceiver = context.getApplicationContext().registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"))) == null) {
            } else {
                MyUtils.setIsRinging(context, false);
                this.plugged = MyUtils.getPlugTypeString(registerReceiver.getIntExtra("plugged", -1));
                this.mHealth = MyUtils.getHealthString(registerReceiver.getIntExtra("health", 0));
                this.mLevel = registerReceiver.getIntExtra("level", -1);
                this.mScale = registerReceiver.getIntExtra("scale", -1);
                this.mTechnology = registerReceiver.getExtras().getString("technology");
                this.mVoltage = registerReceiver.getIntExtra("voltage", 0) / 1000.0f;
                this.mStatus = MyUtils.getStatusString(registerReceiver.getIntExtra(NotificationCompat.CATEGORY_STATUS, 0));
                if (MyUtils.getIsSERVICE_KEY(context)) {
                    return;
                }
                this.databaseHelper.addHistoryData(MyUtils.getFormatedDate(calendar.getTime()), MyUtils.getFormatedTime(calendar.getTime()), this.plugged, this.mHealth, this.mStatus, String.valueOf(this.mLevel), this.mVoltage);
            }
        }
    }
}
