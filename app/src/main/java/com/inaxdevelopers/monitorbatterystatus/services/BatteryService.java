package com.inaxdevelopers.monitorbatterystatus.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import com.inaxdevelopers.monitorbatterystatus.MainActivity;
import com.inaxdevelopers.monitorbatterystatus.MyApp;
import com.inaxdevelopers.monitorbatterystatus.R;
import com.inaxdevelopers.monitorbatterystatus.receivers.PlugConnectionReceiver;
import com.inaxdevelopers.monitorbatterystatus.utils.MyUtils;


public class BatteryService extends Service {
    public final IBinder iBinder = new LocalBinder();


    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public BatteryService getService() {
            return BatteryService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i2) {
        try {
            if (!MyUtils.getIsSERVICE_KEY(getApplicationContext())) {
                onTaskRemoved(intent);
            }
        } catch (Exception unused) {
        }
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            showNotification(getApplicationContext());
            getApplicationContext().registerReceiver(new PlugConnectionReceiver(), new IntentFilter("android.intent.action.ACTION_POWER_DISCONNECTED"));
            getApplicationContext().registerReceiver(new PlugConnectionReceiver(), new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED"));
            getApplicationContext().registerReceiver(new PlugConnectionReceiver(), new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        } catch (Exception unused) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent intent) {
        Intent intent2 = new Intent(getApplicationContext(), BatteryService.class);
        intent2.setPackage(getPackageName());
        startService(intent2);
        super.onTaskRemoved(intent);
    }
    public void showNotification(Context context) {
        PendingIntent activity = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(2, new NotificationCompat.Builder(this, MyApp.CHANNEL_ID).setContentTitle("Battery Monitor").setContentText("Monitoring your battery").setSmallIcon(R.drawable.icon200).setContentIntent(activity).build());
            return;
        }
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(2, new NotificationCompat.Builder(this).setContentTitle("Battery Monitor").setContentText("Monitoring your battery").setSmallIcon(R.drawable.icon200).setContentIntent(activity).build());
    }
}
