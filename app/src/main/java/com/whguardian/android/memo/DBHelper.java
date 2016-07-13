package com.whguardian.android.memo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by whguardian_control on 16/04/11.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "memo.sqlite";
    private static final int VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Crete database table
        db.execSQL("CREATE TABLE IF NOT EXISTS memoInfo" +
                "( _id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, title TEXT, content TEXT, " +
                "beginTime TEXT, endTime TEXT, isDone TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS sketch (_id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", uuid TEXT, mark TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int NewVersion) {
        //Implement schema changes and data message here when upgrading

    }
}
