package com.shmingjiang.mltx.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.shmingjiang.mltx.bean.IceBox;


import java.util.ArrayList;

/**
 * 所有条目
 * Created by hasee on 2016/2/24.
 */
public class AllIbDataBaseUtil {

    private static MLSQLiteOpenHelper ibDataBase;

    public static void createDataBase(Context context) {
        ibDataBase = new MLSQLiteOpenHelper(context);
    }


    public static SQLiteDatabase getIbDatabase(Context context) {
        ibDataBase = new MLSQLiteOpenHelper(context);
        return ibDataBase.getWritableDatabase();
    }

    public static void insert(Context context, IceBox ib) {

        String[] args = ib.ib2Arrary();
        if (args == null) {
            Toast.makeText(context, "没有冰箱信息！", Toast.LENGTH_LONG).show();
            return;
        }
        SQLiteDatabase database = getIbDatabase(context);
        String sql = "insert into allib(r3code, icname, deth, with, height, cmd,location) values(" +
                " ?, ?, ?, ?, ?, ?,?)";
        database.execSQL(sql, args);
    }

    public static void delete(Context context, IceBox ib) {
        String[] args = ib.ib2Arrary();
        if (args == null) {
            Toast.makeText(context, "没有冰箱信息！", Toast.LENGTH_LONG).show();
            return;
        }
        SQLiteDatabase database = getIbDatabase(context);
        String sql = "delete from allib where r3code = ?";
        database.execSQL(sql, new String[]{ib.getR3code()});
    }

    public static void update(Context context, IceBox ib) {
        SQLiteDatabase database = getIbDatabase(context);
        //更新SQL
        String sql = "update allib set r3code='" + ib.getR3code() + "',icname='" + ib.getIceBoxName() + "',deth='" + ib.getDeth() + "'," +
                "with='" + ib.getWith() + "',height='" + ib.getHeight() + "',cmd='" + ib.getCmd() + "', where r3code = '" + ib.getR3code() + "'";
        database.execSQL(sql);
    }

    public static void query() {

    }

    // 根据二维码找到R3码
    public static IceBox queryItem(String s, Context context) {
        IceBox ib = new IceBox();
        String sql = "select r3code, icname, deth, with, height,cmd,location from allib where r3code=?";
        SQLiteDatabase database = getIbDatabase(context);
        Cursor cursor = database.rawQuery(sql, new String[]{s});
        if (cursor.getCount() == 0) {
            return null;
        }

        while (cursor.moveToNext()) {
            ib.setFlag(true);
            String r3code = cursor.getString(0);
            String icname = cursor.getString(1);
            String deth = cursor.getString(2);
            String with = cursor.getString(3);
            String height = cursor.getString(4);
            String cmd = cursor.getString(5);
            String location = cursor.getString(6);
            ib.setR3code(r3code);
            ib.setIceBoxName(icname);
            ib.setDeth(deth);
            ib.setWith(with);
            ib.setHeight(height);
            ib.setCmd(cmd);
            ib.setLocation(location);
        }
        cursor.close();
        return ib;
    }


    public static ArrayList<IceBox> queryAll(Context context) {
        ArrayList<IceBox> ibList = new ArrayList<>();
        IceBox ib;
        String sql = "select r3code, icname, deth, with, height, cmd,location from allib";
        SQLiteDatabase database = getIbDatabase(context);
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            ib = new IceBox();
            ib.setFlag(true);
            //   String d2code = cursor.getString(0) ;
            String r3code = cursor.getString(0);
            String icname = cursor.getString(1);
            String deth = cursor.getString(2);
            String with = cursor.getString(3);
            String height = cursor.getString(4);
            String cmd = cursor.getString(5);
            String location = cursor.getString(6);
            //   ib.setD2code(d2code);
            ib.setR3code(r3code);
            ib.setIceBoxName(icname);
            ib.setDeth(deth);
            ib.setWith(with);
            ib.setHeight(height);
            ib.setCmd(cmd);
            ib.setLocation(location);
            ibList.add(ib);
        }
        cursor.close();
        return ibList;
    }

}
