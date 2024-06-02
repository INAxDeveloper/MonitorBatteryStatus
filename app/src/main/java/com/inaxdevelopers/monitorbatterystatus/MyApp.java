package com.inaxdevelopers.monitorbatterystatus;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.github.mikephil.charting.BuildConfig;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.data.AppItem;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.data.DataManager;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.db.DbHistoryExecutor;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.db.DbIgnoreExecutor;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.util.PreferenceManager;

import java.util.ArrayList;
import java.util.Iterator;

public class MyApp extends Application {

    public static final String CHANNEL_ID = "BatteryMonitor";

    public static boolean isActive_adMob = true;
    SharedPreferences.Editor myEdit;
    SharedPreferences sharedPreferences;
    String str = "11$11$11$0";

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        PreferenceManager.init(this);
        DbIgnoreExecutor.init(context);
        DbHistoryExecutor.init(context);
        DataManager.init();
        addDefaultIgnoreAppsToDB();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void addDefaultIgnoreAppsToDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList arrayList = new ArrayList();
                arrayList.add("com.android.settings");
                arrayList.add(BuildConfig.APPLICATION_ID);
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    AppItem appItem = new AppItem();
                    appItem.mPackageName = (String) it.next();
                    appItem.mEventTime = System.currentTimeMillis();
                    DbIgnoreExecutor.getInstance().insertItem(appItem);
                }
            }
        }).run();
    }


    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((NotificationManager) getSystemService(NotificationManager.class)).createNotificationChannel(new NotificationChannel(CHANNEL_ID, "Battery Monitor", NotificationManager.IMPORTANCE_DEFAULT));
        }
    }

}

