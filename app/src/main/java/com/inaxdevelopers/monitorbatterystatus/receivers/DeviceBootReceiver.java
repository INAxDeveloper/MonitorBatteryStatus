package com.inaxdevelopers.monitorbatterystatus.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.inaxdevelopers.monitorbatterystatus.services.BatteryService;
import com.inaxdevelopers.monitorbatterystatus.utils.MyUtils;


public class DeviceBootReceiver extends BroadcastReceiver {
    @Override 
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            try {
                if (!MyUtils.getIsSERVICE_KEY(context)) {
                    Intent intent2 = new Intent(context, BatteryService.class);
                    if (Build.VERSION.SDK_INT >= 26) {
                        context.startForegroundService(intent2);
                    } else {
                        context.startService(intent2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
