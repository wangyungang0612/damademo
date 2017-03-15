package com.shmingjiang.mltx.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 */
public class MLSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    private static final String DBNAME = "host.db";

    public MLSQLiteOpenHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*当日冰箱外包装尺寸*/
        db.execSQL("create table ib(id integer primary key autoincrement," +
                "r3code varchar(20),icname varchar(20), deth varchar(20)," +
                "with varchar(20), height varchar(20), cmd varchar(20),location varchar(20))");
        /*冰箱外包装尺寸*/
        db.execSQL("create table allib(id integer primary key autoincrement," +
                "r3code varchar(20),icname varchar(20), deth varchar(20)," +
                "with varchar(20), height varchar(20), cmd varchar(20),location varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
