package com.inaxdevelopers.monitorbatterystatus.batteryusage.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


import com.inaxdevelopers.monitorbatterystatus.batteryusage.data.AppItem;
import com.inaxdevelopers.monitorbatterystatus.batteryusage.data.IgnoreItem;

import java.util.ArrayList;
import java.util.List;


public class DbIgnoreExecutor {
    private static DbHelper mHelper;
    private static DbIgnoreExecutor sInstance;

    private DbIgnoreExecutor() {
    }

    public static void init(Context context) {
        mHelper = new DbHelper(context);
        sInstance = new DbIgnoreExecutor();
    }

    public static DbIgnoreExecutor getInstance() {
        return sInstance;
    }

    public void insertItem(AppItem appItem) {
        if (exists(appItem.mPackageName)) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("package_name", appItem.mPackageName);
        contentValues.put("created_time", Long.valueOf(System.currentTimeMillis()));
        mHelper.getWritableDatabase().insert("ignore", null, contentValues);
    }

    public void insertItem(String str) {
        if (exists(str)) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("package_name", str);
        contentValues.put("created_time", Long.valueOf(System.currentTimeMillis()));
        mHelper.getWritableDatabase().insert("ignore", null, contentValues);
    }

    public void deleteItem(IgnoreItem ignoreItem) {
        if (exists(ignoreItem.mPackageName)) {
            mHelper.getWritableDatabase().delete("ignore", "package_name = ?", new String[]{ignoreItem.mPackageName});
        }
    }

    public List<IgnoreItem> getAllItems() {
        ArrayList arrayList = new ArrayList();
        Cursor cursor = null;
        try {
            cursor = mHelper.getReadableDatabase().query("ignore", new String[]{"_id", "package_name", "created_time"}, null, null, null, null, "created_time DESC");
            while (cursor.moveToNext()) {
                arrayList.add(cursorToItem(cursor));
            }
            return arrayList;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private boolean exists(String str) {
        Cursor cursor = null;
        try {
            Cursor query = mHelper.getWritableDatabase().query("ignore", new String[]{"_id"}, "package_name = ?", new String[]{str}, null, null, null);
            if (query.moveToNext()) {
                @SuppressLint("Range") boolean z = query.getInt(query.getColumnIndex("_id")) > 0;
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

    @SuppressLint("Range")
    private IgnoreItem cursorToItem(Cursor cursor) {
        IgnoreItem ignoreItem = new IgnoreItem();
        ignoreItem.mPackageName = cursor.getString(cursor.getColumnIndex("package_name"));
        ignoreItem.mCreated = cursor.getLong(cursor.getColumnIndex("created_time"));
        return ignoreItem;
    }
}
