package com.inaxdevelopers.monitorbatterystatus.batteryusage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "timeline";
    private static final int DATABASE_VERSION = 2;
    private static final String SQL_CREATE_HISTORY = "CREATE TABLE history (_id INTEGER PRIMARY KEY,package_name TEXT,name TEXT,date TEXT,is_system INTEGER,mobile INTEGER,timestamp INTEGER,duration INTEGER)";
    private static final String SQL_CREATE_IGNORE = "CREATE TABLE ignore (_id INTEGER PRIMARY KEY,package_name TEXT,created_time INTEGER)";
    private static final String SQL_DELETE_HISTORY = "DROP TABLE IF EXISTS ignore";
    private static final String SQL_DELETE_IGNORE = "DROP TABLE IF EXISTS ignore";

    
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 2);
    }

    @Override 
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL(SQL_CREATE_IGNORE);
        sQLiteDatabase.execSQL(SQL_CREATE_HISTORY);
    }

    @Override 
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS ignore");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS ignore");
        onCreate(sQLiteDatabase);
    }

    @Override 
    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        onUpgrade(sQLiteDatabase, i, i2);
    }
}
