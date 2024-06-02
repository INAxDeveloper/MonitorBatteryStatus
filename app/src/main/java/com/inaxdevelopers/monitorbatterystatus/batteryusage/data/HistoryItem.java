package com.inaxdevelopers.monitorbatterystatus.batteryusage.data;

import java.util.Locale;


public class HistoryItem {
    public String mDate;
    public long mDuration;
    public int mIsSystem;
    public long mMobileTraffic;
    public String mName;
    public String mPackageName;
    public long mTimeStamp;

    public String toString() {
        return String.format(Locale.getDefault(), "%s %s %s %d %d %d %d", this.mPackageName, this.mName, this.mDate, Integer.valueOf(this.mIsSystem), Long.valueOf(this.mDuration), Long.valueOf(this.mTimeStamp), Long.valueOf(this.mMobileTraffic));
    }
}
