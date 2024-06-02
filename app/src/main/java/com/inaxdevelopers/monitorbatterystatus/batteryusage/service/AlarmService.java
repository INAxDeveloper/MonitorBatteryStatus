package com.inaxdevelopers.monitorbatterystatus.batteryusage.service;

import android.app.IntentService;
import android.content.Intent;


import com.inaxdevelopers.monitorbatterystatus.batteryusage.data.AppItem;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.data.DataManager;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.data.HistoryItem;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.db.DbHistoryExecutor;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.util.AlarmUtil;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.util.AppUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AlarmService extends IntentService {
    private static final String ALARM_SERVICE_NAME = "alarm.service";

    public AlarmService() {
        super(ALARM_SERVICE_NAME);
    }

    @Override 
    public void onHandleIntent(Intent intent) {
        for (AppItem appItem : DataManager.getInstance().getApps(getApplicationContext(), 0, 1)) {
            HistoryItem historyItem = new HistoryItem();
            historyItem.mName = appItem.mName;
            historyItem.mPackageName = appItem.mPackageName;
            historyItem.mMobileTraffic = appItem.mMobile;
            historyItem.mIsSystem = AppUtil.isSystemApp(getPackageManager(), appItem.mPackageName) ? 1 : 0;
            historyItem.mDuration = appItem.mUsageTime;
            historyItem.mTimeStamp = AppUtil.getYesterdayTimestamp();
            historyItem.mDate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date(historyItem.mTimeStamp));
            DbHistoryExecutor.getInstance().insert(historyItem);
        }
        AlarmUtil.setAlarm(getApplicationContext());
    }
}
