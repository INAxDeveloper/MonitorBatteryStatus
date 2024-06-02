package com.inaxdevelopers.monitorbatterystatus.batteryusage.data;

import java.util.Locale;


public class AppItem {
    public boolean mCanOpen;
    public int mCount;
    public long mEventTime;
    public int mEventType;
    private boolean mIsSystem;
    public long mMobile;
    public String mName;
    public String mPackageName;
    public long mUsageTime;

    public String toString() {
        return String.format(Locale.getDefault(), "name:%s package_name:%s time:%d total:%d type:%d system:%b count:%d", this.mName, this.mPackageName, Long.valueOf(this.mEventTime), Long.valueOf(this.mUsageTime), Integer.valueOf(this.mEventType), Boolean.valueOf(this.mIsSystem), Integer.valueOf(this.mCount));
    }

    public AppItem copy() {
        AppItem appItem = new AppItem();
        appItem.mName = this.mName;
        appItem.mPackageName = this.mPackageName;
        appItem.mEventTime = this.mEventTime;
        appItem.mUsageTime = this.mUsageTime;
        appItem.mEventType = this.mEventType;
        appItem.mIsSystem = this.mIsSystem;
        appItem.mCount = this.mCount;
        return appItem;
    }
}
