package com.inaxdevelopers.monitorbatterystatus.batteryusage.service;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import com.inaxdevelopers.monitorbatterystatus.acitvites.BatteryUsageActivity;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.data.DataManager;
import com.inaxdevelopers.monitorbatterystatus.utils.MyUtils;


public class AppService extends Service {
    static final long CHECK_INTERVAL = 400;
    public static final String SERVICE_ACTION = "service_action";
    public static final String SERVICE_ACTION_CHECK = "service_action_check";
    private Context mContext;
    private DataManager mManager;
    private Handler mHandler = new Handler();
    private Runnable mRepeatCheckTask = new Runnable() { 
        @Override 
        public void run() {
            if (!AppService.this.mManager.hasPermission(AppService.this.mContext)) {
                AppService.this.mHandler.postDelayed(AppService.this.mRepeatCheckTask, AppService.CHECK_INTERVAL);
                return;
            }
            AppService.this.mHandler.removeCallbacks(AppService.this.mRepeatCheckTask);
            AppService appService = AppService.this;
            appService.startService(new Intent(appService.mContext, AlarmService.class));
            Intent intent = new Intent(AppService.this.mContext, BatteryUsageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            AppService.this.startActivity(intent);
        }
    };

    @Override 
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override 
    public void onCreate() {
        super.onCreate();
        this.mContext = getApplicationContext();
        this.mManager = new DataManager();
    }

    @Override 
    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent == null) {
            return Service.START_STICKY;
        }
        String stringExtra = intent.getStringExtra(SERVICE_ACTION);
        if (TextUtils.isEmpty(stringExtra)) {
            return Service.START_STICKY;
        }
        stringExtra.hashCode();
        if (stringExtra.equals(SERVICE_ACTION_CHECK)) {
            startIntervalCheck();
            return Service.START_STICKY;
        }
        return Service.START_STICKY;
    }

    @Override 
    public void onDestroy() {
        try {
            startService(new Intent(this.mContext, AppService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startIntervalCheck() {
        boolean z;
        try {
            this.mManager.requestPermission(this.mContext);
            z = true;
        } catch (ActivityNotFoundException unused) {
            z = false;
        }
        if (z) {
            MyUtils.showMessage(this.mContext, "Please find App and grant usage permission");
            this.mHandler.post(this.mRepeatCheckTask);
            return;
        }
        MyUtils.showMessage(this.mContext, "Your device not support monitoring");
    }
}
