package com.inaxdevelopers.monitorbatterystatus.batteryusage.data;

import android.app.AppOpsManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;


import com.inaxdevelopers.monitorbatterystatus.batteryusage.db.DbIgnoreExecutor;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.util.AppUtil;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.util.PreferenceManager;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.util.SortEnum;
import com.inaxdevelopers.monitorbatterystatus.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class DataManager {
    private static DataManager mInstance;

    public static void init() {
        mInstance = new DataManager();
    }

    public static DataManager getInstance() {
        return mInstance;
    }

    public void requestPermission(Context context) {
        Intent intent = new Intent(new Intent("android.settings.USAGE_ACCESS_SETTINGS"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public boolean hasPermission(Context context) {
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        return appOpsManager != null && appOpsManager.checkOpNoThrow("android:get_usage_stats", Process.myUid(), context.getPackageName()) == 0;
    }

    public List<AppItem> getTargetAppTimeline(Context context, String str, int i) {
        ArrayList arrayList = new ArrayList();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usageStatsManager != null) {
            long[] timeRange = AppUtil.getTimeRange(SortEnum.getSortEnum(i));
            UsageEvents.Event event = new UsageEvents.Event();
            AppItem appItem = new AppItem();
            appItem.mPackageName = str;
            appItem.mName = AppUtil.parsePackageName(context.getPackageManager(), str);
            UsageEvents queryEvents = usageStatsManager.queryEvents(timeRange[0], timeRange[1]);
            ClonedEvent clonedEvent = null;
            long j = 0;
            while (queryEvents.hasNextEvent()) {
                queryEvents.getNextEvent(event);
                String packageName = event.getPackageName();
                int eventType = event.getEventType();
                long timeStamp = event.getTimeStamp();
                StringBuilder sb = new StringBuilder();
                sb.append(packageName);
                sb.append(Constants.STRING_EMPTY);
                sb.append(str);
                sb.append(Constants.STRING_EMPTY);
                UsageEvents usageEvents = queryEvents;
                sb.append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date(timeStamp)));
                sb.append(Constants.STRING_EMPTY);
                sb.append(eventType);
                Log.d("||||------>", sb.toString());
                if (packageName.equals(str)) {
                    Log.d("||||||||||>", packageName + Constants.STRING_EMPTY + str + Constants.STRING_EMPTY + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date(timeStamp)) + Constants.STRING_EMPTY + eventType);
                    if (eventType == 1) {
                        Log.d("********", "start " + j);
                        if (j == 0) {
                            appItem.mEventTime = timeStamp;
                            appItem.mEventType = eventType;
                            appItem.mUsageTime = 0L;
                            arrayList.add(appItem.copy());
                            j = timeStamp;
                        }
                    } else if (eventType == 2) {
                        if (j > 0) {
                            clonedEvent = new ClonedEvent(event);
                        }
                        Log.d("********", "add end " + j);
                    }
                } else if (clonedEvent != null) {
                    if (j > 0) {
                        appItem.mEventTime = clonedEvent.timeStamp;
                        appItem.mEventType = clonedEvent.eventType;
                        appItem.mUsageTime = clonedEvent.timeStamp - j;
                        if (appItem.mUsageTime <= 0) {
                            appItem.mUsageTime = 0L;
                        }
                        if (appItem.mUsageTime > Constants.USAGE_TIME_MIX) {
                            appItem.mCount++;
                        }
                        arrayList.add(appItem.copy());
                        j = 0;
                        clonedEvent = null;
                    }
                }
                queryEvents = usageEvents;
            }
        }
        return arrayList;
    }

    public List<AppItem> getApps(Context context, int i, int i2) {
        boolean z;
        String str;
        List<AppItem> arrayList = new ArrayList<>();
        ArrayList arrayList2 = new ArrayList();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        int i3 = 2;
        int i4 = 1;
        if (usageStatsManager != null) {
            HashMap hashMap = new HashMap();
            HashMap hashMap2 = new HashMap();
            long[] timeRange = AppUtil.getTimeRange(SortEnum.getSortEnum(i2));
            UsageEvents queryEvents = usageStatsManager.queryEvents(timeRange[0], timeRange[1]);
            UsageEvents.Event event = new UsageEvents.Event();
            String str2 = "";


            while (queryEvents.hasNextEvent()) {
                queryEvents.getNextEvent(event);
                int eventType = event.getEventType();
                long timeStamp = event.getTimeStamp();
                String packageName = event.getPackageName();
                if (eventType == i4) {
                    if (containItem(arrayList, packageName) == null) {
                        AppItem appItem = new AppItem();
                        appItem.mPackageName = packageName;
                        arrayList.add(appItem);
                    }
                    if (!hashMap.containsKey(packageName)) {
                        hashMap.put(packageName, Long.valueOf(timeStamp));
                    }
                }


                if (eventType == i3 && hashMap.size() > 0 && hashMap.containsKey(packageName)) {
                    hashMap2.put(packageName, new ClonedEvent(event));
                }
                if (TextUtils.isEmpty(str2)) {
                    str2 = packageName;
                }
                if (!str2.equals(packageName)) {
                    if (hashMap.containsKey(str2) && hashMap2.containsKey(str2)) {
                        ClonedEvent clonedEvent = (ClonedEvent) hashMap2.get(str2);
                        AppItem containItem = containItem(arrayList, str2);
                        if (containItem != null) {
                            containItem.mEventTime = clonedEvent.timeStamp;
                            long longValue = clonedEvent.timeStamp - ((Long) hashMap.get(str2)).longValue();
                            if (longValue <= 0) {
                                longValue = 0;
                            }
                            str = packageName;
                            containItem.mUsageTime += longValue;
                            if (longValue > Constants.USAGE_TIME_MIX) {
                                containItem.mCount++;
                            }
                        } else {
                            str = packageName;
                        }
                        hashMap.remove(str2);
                        hashMap2.remove(str2);
                    } else {
                        str = packageName;
                    }
                    str2 = str;
                }
                i3 = 2;
                i4 = 1;
            }
        }

        Log.e("arrayList",""+arrayList.size());
        if (arrayList.size() > 0) {
            Map<String, Long> hashMap3 = new HashMap<>();
            if (Build.VERSION.SDK_INT >= 23) {
                hashMap3 = getMobileData(context, (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE), (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE), i2);
                z = true;
            } else {
                z = false;
            }
            boolean z2 = PreferenceManager.getInstance().getBoolean(PreferenceManager.PREF_SETTINGS_HIDE_SYSTEM_APPS);
            boolean z3 = PreferenceManager.getInstance().getBoolean(PreferenceManager.PREF_SETTINGS_HIDE_UNINSTALL_APPS);
            List<IgnoreItem> allItems = DbIgnoreExecutor.getInstance().getAllItems();
            PackageManager packageManager = context.getPackageManager();



            for (AppItem appItem2 : arrayList) {
                if (AppUtil.openable(packageManager, appItem2.mPackageName) && (!z2 || !AppUtil.isSystemApp(packageManager, appItem2.mPackageName))) {
                    if (!z3 || AppUtil.isInstalled(packageManager, appItem2.mPackageName)) {
                        if (!inIgnoreList(allItems, appItem2.mPackageName)) {
                            if (z) {
                                String str3 = "u" + AppUtil.getAppUid(packageManager, appItem2.mPackageName);
                                if (hashMap3.size() > 0 && hashMap3.containsKey(str3)) {
                                    appItem2.mMobile = hashMap3.get(str3).longValue();
                                }
                            }
                            appItem2.mName = AppUtil.parsePackageName(packageManager, appItem2.mPackageName);
                            arrayList2.add(appItem2);
                        }
                    }
                }
            }
            if (i == 0) {
                Collections.sort(arrayList2, new Comparator<AppItem>() {
                    @Override
                    public int compare(AppItem appItem3, AppItem appItem4) {
                        return (int) (appItem4.mUsageTime - appItem3.mUsageTime);
                    }
                });
            } else if (i == 1) {
                Collections.sort(arrayList2, new Comparator<AppItem>() {
                    @Override
                    public int compare(AppItem appItem3, AppItem appItem4) {
                        return (int) (appItem4.mEventTime - appItem3.mEventTime);
                    }
                });
            } else if (i == 2) {
                Collections.sort(arrayList2, new Comparator<AppItem>() {
                    @Override
                    public int compare(AppItem appItem3, AppItem appItem4) {
                        return appItem4.mCount - appItem3.mCount;
                    }
                });
            } else {
                Collections.sort(arrayList2, new Comparator<AppItem>() {
                    @Override
                    public int compare(AppItem appItem3, AppItem appItem4) {
                        return (int) (appItem4.mMobile - appItem3.mMobile);
                    }
                });
            }
        }
        return arrayList2;
    }

    private Map<String, Long> getMobileData(Context context, TelephonyManager telephonyManager, NetworkStatsManager networkStatsManager, int i) {
        NetworkStats querySummary;
        HashMap hashMap = new HashMap();
        if (ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") == 0) {
            long[] timeRange = AppUtil.getTimeRange(SortEnum.getSortEnum(i));
            try {
                if (Build.VERSION.SDK_INT >= 23 && (querySummary = networkStatsManager.querySummary(0, null, timeRange[0], timeRange[1])) != null) {
                    while (querySummary.hasNextBucket()) {
                        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                        querySummary.getNextBucket(bucket);
                        String str = "u" + bucket.getUid();
                        Log.d("******", str + Constants.STRING_EMPTY + bucket.getTxBytes() + "");
                        if (hashMap.containsKey(str)) {
                            hashMap.put(str, Long.valueOf(((Long) hashMap.get(str)).longValue() + bucket.getTxBytes() + bucket.getRxBytes()));
                        } else {
                            hashMap.put(str, Long.valueOf(bucket.getTxBytes() + bucket.getRxBytes()));
                        }
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(">>>>>", e.getMessage());
            }
        }
        return hashMap;
    }

    private AppItem containItem(List<AppItem> list, String str) {
        for (AppItem appItem : list) {
            if (appItem.mPackageName.equals(str)) {
                return appItem;
            }
        }
        return null;
    }

    private boolean inIgnoreList(List<IgnoreItem> list, String str) {
        for (IgnoreItem ignoreItem : list) {
            if (ignoreItem.mPackageName.equals(str)) {
                return true;
            }
        }
        return false;
    }

    
    class ClonedEvent {
        String eventClass;
        int eventType;
        String packageName;
        long timeStamp;

        ClonedEvent(UsageEvents.Event event) {
            this.packageName = event.getPackageName();
            this.eventClass = event.getClassName();
            this.timeStamp = event.getTimeStamp();
            this.eventType = event.getEventType();
        }
    }
}
