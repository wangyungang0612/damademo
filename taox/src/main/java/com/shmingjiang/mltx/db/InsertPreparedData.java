package com.shmingjiang.mltx.db;

import android.content.Context;

import com.shmingjiang.mltx.bean.IceBox;

/**
 * 插入基础数据
 */
public class InsertPreparedData {

    public static void insertData(Context context) {

        //数据更新 2016年9月10日18:53:06
        IceBox item3 = new IceBox("9016007", "565WPCJ", "809", "978", "1843", "2", "5");
        IceBox item4 = new IceBox("9015930", "568WPCJ", "809", "983", "1843", "2", "1");
        IceBox item5 = new IceBox("9016082", "568WPBD", "809", "978", "1843", "2", "2");
        IceBox item6 = new IceBox("9014552", "551WPCX", "809", "983", "1843", "2", "3");
        IceBox item7 = new IceBox("9014575", "568WPCJJ", "809", "983", "1843", "2", "4");

        AllIbDataBaseUtil.createDataBase(context);
        AllIbDataBaseUtil.insert(context, item3);
        AllIbDataBaseUtil.insert(context, item4);
        AllIbDataBaseUtil.insert(context, item5);
        AllIbDataBaseUtil.insert(context, item6);
        AllIbDataBaseUtil.insert(context, item7);
    }
}
