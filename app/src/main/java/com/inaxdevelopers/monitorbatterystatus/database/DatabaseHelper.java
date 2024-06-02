package com.inaxdevelopers.monitorbatterystatus.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.inaxdevelopers.monitorbatterystatus.model.HistoryModel;
import com.inaxdevelopers.monitorbatterystatus.utils.Constants;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "BatteryMonitor.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME_HISTORY = "tblChargingHistory";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 1);
    }

    @Override 
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE 'tblChargingHistory'(HistoryId INTEGER PRIMARY KEY AUTOINCREMENT,StartEndDate TEXT,StartEndTime TEXT, Plugged TEXT,Health TEXT,Status TEXT,BatteryLevel TEXT, Voltage REAL)");
    }

    @Override 
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        onCreate(sQLiteDatabase);
    }

    public Boolean addHistoryData(String str, String str2, String str3, String str4, String str5, String str6, float f) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.chargingStartEndDate, str);
        contentValues.put(Constants.chargingStartEndTime, str2);
        contentValues.put(Constants.plugged, str3);
        contentValues.put(Constants.health, str4);
        contentValues.put(Constants.status, str5);
        contentValues.put(Constants.level, str6);
        contentValues.put(Constants.voltage, Float.valueOf(f));
        long insert = writableDatabase.insert(TABLE_NAME_HISTORY, null, contentValues);
        writableDatabase.close();
        if (insert == -1) {
            return false;
        }
        return true;
    }

    public ArrayList<HistoryModel> getHistoryData() {
        ArrayList<HistoryModel> arrayList = new ArrayList<>();
        Cursor rawQuery = getReadableDatabase().rawQuery("SELECT * FROM tblChargingHistory ORDER by HistoryId DESC", null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new HistoryModel(rawQuery.getInt(rawQuery.getColumnIndex(Constants.historyId)), rawQuery.getString(rawQuery.getColumnIndex(Constants.chargingStartEndDate)), rawQuery.getString(rawQuery.getColumnIndex(Constants.chargingStartEndTime)), rawQuery.getString(rawQuery.getColumnIndex(Constants.plugged)), rawQuery.getString(rawQuery.getColumnIndex(Constants.health)), rawQuery.getString(rawQuery.getColumnIndex(Constants.status)), rawQuery.getString(rawQuery.getColumnIndex(Constants.level)), rawQuery.getFloat(rawQuery.getColumnIndex(Constants.voltage))));
            }
        }
        return arrayList;
    }

    public Boolean deleteHistory(int i) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        int delete = writableDatabase.delete(TABLE_NAME_HISTORY, "HistoryId= '" + i + "'", null);
        writableDatabase.close();
        if (delete == 0) {
            return false;
        }
        return true;
    }

    public ArrayList<HistoryModel> getHistoryLevel(String str) {
        String str2;
        ArrayList<HistoryModel> arrayList = new ArrayList<>();
        if (str.equals("All Data")) {
            str2 = "SELECT * FROM tblChargingHistory";
        } else {
            str2 = "SELECT * FROM tblChargingHistory where StartEndDate >= Datetime('" + str + "')";
        }
        Log.e("HHHH", "Date111  " + str);
        Cursor rawQuery = getReadableDatabase().rawQuery(str2, null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new HistoryModel(rawQuery.getInt(rawQuery.getColumnIndex(Constants.historyId)), rawQuery.getString(rawQuery.getColumnIndex(Constants.chargingStartEndDate)), rawQuery.getString(rawQuery.getColumnIndex(Constants.chargingStartEndTime)), rawQuery.getString(rawQuery.getColumnIndex(Constants.plugged)), rawQuery.getString(rawQuery.getColumnIndex(Constants.health)), rawQuery.getString(rawQuery.getColumnIndex(Constants.status)), rawQuery.getString(rawQuery.getColumnIndex(Constants.level)), rawQuery.getFloat(rawQuery.getColumnIndex(Constants.voltage))));
            }
        }
        return arrayList;
    }

    public ArrayList<HistoryModel> getHistoryAccordingDate(String str) {
        ArrayList<HistoryModel> arrayList = new ArrayList<>();
        Cursor rawQuery = getReadableDatabase().rawQuery("SELECT * FROM tblChargingHistory where StartEndDate == '" + str + "'", null);
        if (rawQuery != null) {
            while (rawQuery.moveToNext()) {
                arrayList.add(new HistoryModel(rawQuery.getInt(rawQuery.getColumnIndex(Constants.historyId)), rawQuery.getString(rawQuery.getColumnIndex(Constants.chargingStartEndDate)), rawQuery.getString(rawQuery.getColumnIndex(Constants.chargingStartEndTime)), rawQuery.getString(rawQuery.getColumnIndex(Constants.plugged)), rawQuery.getString(rawQuery.getColumnIndex(Constants.health)), rawQuery.getString(rawQuery.getColumnIndex(Constants.status)), rawQuery.getString(rawQuery.getColumnIndex(Constants.level)), rawQuery.getFloat(rawQuery.getColumnIndex(Constants.voltage))));
            }
        }
        return arrayList;
    }
}
