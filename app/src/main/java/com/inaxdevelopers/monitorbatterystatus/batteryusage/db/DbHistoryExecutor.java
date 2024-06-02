package com.inaxdevelopers.monitorbatterystatus.batteryusage.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


import com.inaxdevelopers.monitorbatterystatus.batteryusage.data.HistoryItem;

import java.util.ArrayList;
import java.util.List;


public class DbHistoryExecutor {
    private static DbHelper mHelper;
    private static DbHistoryExecutor sInstance;

    private DbHistoryExecutor() {
    }

    public static void init(Context context) {
        mHelper = new DbHelper(context);
        sInstance = new DbHistoryExecutor();
    }

    public static DbHistoryExecutor getInstance() {
        return sInstance;
    }

    public void clear() {
        mHelper.getWritableDatabase().delete("history", null, null);
    }

    public void insert(HistoryItem historyItem) {
        if (exists(historyItem)) {
            return;
        }
        mHelper.getWritableDatabase().insert("history", null, itemToContentValue(historyItem));
    }

    public List<HistoryItem> getAllItems() throws Throwable {
        ArrayList arrayList = new ArrayList();
        Cursor cursor = null;
        try {
            cursor = mHelper.getReadableDatabase().query("history", new String[]{"_id", "date", "timestamp", "is_system", "duration", "package_name", "mobile", "name"}, null, null, null, null, "duration DESC");
            while (cursor.moveToNext()) {
                try {
                    arrayList.add(cursorToItem(cursor));
                } catch (Throwable unused) {
                }
            }
            return arrayList;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    @SuppressLint("Range")
    private boolean exists(HistoryItem historyItem) {
        Cursor cursor = null;
        try {
            Cursor query = mHelper.getWritableDatabase().query("history", new String[]{"_id"}, "date = ? AND package_name = ?", new String[]{historyItem.mDate, historyItem.mPackageName}, null, null, null);
            if (query.moveToNext()) {
                boolean z = query.getInt(query.getColumnIndex("_id")) > 0;
                if (query != null) {
                    query.close();
                }
                return z;
            }
            if (query != null) {
                query.close();
            }
            if (query != null) {
                query.close();
            }
            return false;
        } catch (Throwable th) {
            if (0 != 0) {
                cursor.close();
            }
            throw th;
        }
    }

    private ContentValues itemToContentValue(HistoryItem historyItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", historyItem.mDate);
        contentValues.put("duration", Long.valueOf(historyItem.mDuration));
        contentValues.put("mobile", Long.valueOf(historyItem.mMobileTraffic));
        contentValues.put("name", historyItem.mName);
        contentValues.put("package_name", historyItem.mPackageName);
        contentValues.put("is_system", Integer.valueOf(historyItem.mIsSystem));
        contentValues.put("timestamp", Long.valueOf(historyItem.mTimeStamp));
        return contentValues;
    }

    @SuppressLint("Range")
    private HistoryItem cursorToItem(Cursor cursor) {
        HistoryItem historyItem = new HistoryItem();
        historyItem.mPackageName = cursor.getString(cursor.getColumnIndex("package_name"));
        historyItem.mName = cursor.getString(cursor.getColumnIndex("name"));
        historyItem.mDate = cursor.getString(cursor.getColumnIndex("date"));
        historyItem.mDuration = cursor.getLong(cursor.getColumnIndex("duration"));
        historyItem.mTimeStamp = cursor.getLong(cursor.getColumnIndex("timestamp"));
        historyItem.mIsSystem = cursor.getInt(cursor.getColumnIndex("is_system"));
        historyItem.mMobileTraffic = cursor.getInt(cursor.getColumnIndex("mobile"));
        return historyItem;
    }
}
