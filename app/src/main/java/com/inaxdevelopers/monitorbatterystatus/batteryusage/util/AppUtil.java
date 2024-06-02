package com.inaxdevelopers.monitorbatterystatus.batteryusage.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;


import com.inaxdevelopers.monitorbatterystatus.R;

import java.util.Calendar;
import java.util.Locale;


public final class AppUtil {
    private static final long A_DAY = 86400000;

    public static String parsePackageName(PackageManager packageManager, String str) {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(str, 128);
        } catch (PackageManager.NameNotFoundException unused) {
            applicationInfo = null;
        }
        return applicationInfo != null ? (String) packageManager.getApplicationLabel(applicationInfo) : str;
    }

    public static Drawable getPackageIcon(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationIcon(str);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return context.getResources().getDrawable(R.drawable.icon200);
        }
    }

    public static String formatMilliSeconds(long j) {
        long j2 = j / 1000;
        if (j2 < 60) {
            return String.format("%ss", Long.valueOf(j2));
        }
        if (j2 < 3600) {
            return String.format("%sm %ss", Long.valueOf(j2 / 60), Long.valueOf(j2 % 60));
        }
        long j3 = j2 % 3600;
        return String.format("%sh %sm %ss", Long.valueOf(j2 / 3600), Long.valueOf(j3 / 60), Long.valueOf(j3 % 60));
    }

    public static boolean isSystemApp(PackageManager packageManager, String str) {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
            if (applicationInfo == null) {
                return false;
            }
            if ((applicationInfo.flags & 1) == 0) {
                if ((applicationInfo.flags & 128) == 0) {
                    return false;
                }
            }
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isInstalled(PackageManager packageManager, String str) {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            applicationInfo = null;
        }
        return applicationInfo != null;
    }

    public static boolean openable(PackageManager packageManager, String str) {
        return packageManager.getLaunchIntentForPackage(str) != null;
    }

    public static int getAppUid(PackageManager packageManager, String str) {
        try {
            return packageManager.getApplicationInfo(str, 0).uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    
    public static class AnonymousClass1 {
        static final int[] $SwitchMap$com$angel$powersaver$bc$batteryusage$util$SortEnum;

        static {
            int[] iArr = new int[SortEnum.values().length];
            $SwitchMap$com$angel$powersaver$bc$batteryusage$util$SortEnum = iArr;
            iArr[SortEnum.TODAY.ordinal()] = 1;
            iArr[SortEnum.YESTERDAY.ordinal()] = 2;
            iArr[SortEnum.THIS_WEEK.ordinal()] = 3;
            iArr[SortEnum.THIS_MONTH.ordinal()] = 4;
            iArr[SortEnum.THIS_YEAR.ordinal()] = 5;
        }
    }

    public static long[] getTimeRange(SortEnum sortEnum) {
        long[] thisYear;
        int i = AnonymousClass1.$SwitchMap$com$angel$powersaver$bc$batteryusage$util$SortEnum[sortEnum.ordinal()];
        if (i == 1) {
            thisYear = getTodayRange();
        } else if (i == 2) {
            thisYear = getYesterday();
        } else if (i == 3) {
            thisYear = getThisWeek();
        } else if (i == 4) {
            thisYear = getThisMonth();
        } else if (i != 5) {
            thisYear = getTodayRange();
        } else {
            thisYear = getThisYear();
        }
        Log.d("**********", thisYear[0] + " ~ " + thisYear[1]);
        return thisYear;
    }

    private static long[] getTodayRange() {
        long currentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return new long[]{calendar.getTimeInMillis(), currentTimeMillis};
    }

    public static long getYesterdayTimestamp() {
        long currentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis - A_DAY);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    private static long[] getYesterday() {
        long currentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis - A_DAY);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        long timeInMillis = calendar.getTimeInMillis();
        long j = A_DAY + timeInMillis;
        if (j <= currentTimeMillis) {
            currentTimeMillis = j;
        }
        return new long[]{timeInMillis, currentTimeMillis};
    }

    private static long[] getThisWeek() {
        long currentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.set(7, 2);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        long timeInMillis = calendar.getTimeInMillis();
        long j = A_DAY + timeInMillis;
        if (j <= currentTimeMillis) {
            currentTimeMillis = j;
        }
        return new long[]{timeInMillis, currentTimeMillis};
    }

    private static long[] getThisMonth() {
        long currentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.set(5, 1);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return new long[]{calendar.getTimeInMillis(), currentTimeMillis};
    }

    private static long[] getThisYear() {
        long currentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, Calendar.getInstance().get(1));
        calendar.set(2, 0);
        calendar.set(5, 1);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return new long[]{calendar.getTimeInMillis(), currentTimeMillis};
    }

    public static String humanReadableByteCount(long j) {
        if (j < 1024) {
            return j + " B";
        }
        double d = j;
        int log = (int) (Math.log(d) / Math.log(1024.0d));
        return String.format(Locale.getDefault(), "%.1f %sB", Double.valueOf(d / Math.pow(1024.0d, log)), "KMGTPE".charAt(log - 1) + "");
    }
}
