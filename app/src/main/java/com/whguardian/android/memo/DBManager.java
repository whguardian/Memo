package com.whguardian.android.memo;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.whguardian.android.memo.MemoData.MemoInfo;
import com.whguardian.android.memo.MemoData.Sketch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/11.
 */
public class DBManager {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    //memoInfo数据
    private String isDone = "true";
    private String unDone = "false";

    //Sketch数据
    private StringBuffer stringBuffer;
    private String lastMarkString;

    private SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public DBManager(Context context) {
        //打开数据库
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    /*
    *
    *
    *
    * memoInfo数据处理
    *
    *
    *
    * */

    public void insert(ArrayList<MemoInfo> memoInfos) {
        //完成所有数据写入数据库
        db.beginTransaction();
        try {
            for (MemoInfo m: memoInfos) {
                //时间格式化
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//                使用insert存储数据
//                ContentValues cv = new ContentValues();
//                cv.put("uuid", m.getId().toString());
//                cv.put("title", m.getTitle());
//                cv.put("content", m.getContent());
//                cv.put("beginTime", dateFormat.format(m.getBeginTime()));
//                cv.put("endTime", dateFormat.format(m.getEndTime()));
//
//                db.insert("memo.sqlite", null, cv);

                db.execSQL("INSERT INTO memoInfo(uuid, title, content, beginTime, endTime, isDone) " +
                        "VALUES(?, ?, ?, ?, ?, ?)",
                        new Object[]{m.getId().toString(), m.getTitle(), m.getContent(),
                                dateFormat.format(m.getBeginTime()),
                                dateFormat.format(m.getEndTime()), unDone});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    //单条数据写入数据库
    public void insert(MemoInfo m) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        db.execSQL("INSERT INTO memoInfo(uuid, title, content, beginTime, endTime, isDone) " +
                        "VALUES(?, ?, ?, ?, ?, ?)",
                new Object[]{m.getId().toString(), m.getTitle(), m.getContent(),
                        dateFormat.format(m.getBeginTime()),
                        dateFormat.format(m.getEndTime()), unDone});
    }

    //修改数据库数据
    public void update(MemoInfo m) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        db.execSQL("UPDATE memoInfo set title = ?, content = ?, beginTime = ?, endTime = ?, isDone = ?" +
                "WHERE uuid = ?", new Object[]{m.getTitle(), m.getContent(),
                dateFormat.format(m.getBeginTime()),
                dateFormat.format(m.getEndTime()), unDone, m.getId().toString()});
    }

    //通过uuid删除数据库中Memo，主要服务与ListFragment ContextMenu delete操作，以及备忘被提醒后
    public void delete(UUID uuid) {
        db.execSQL("DELETE FROM memoInfo WHERE uuid = ?", new String[]{uuid.toString()});
    }

    //过期备忘处理删除
    public void deleteOverdue() {
        //通过精确到秒删除过期备忘
        db.execSQL("DELETE FROM memoInfo WHERE Date(beginTime) < Date('now','localtime')");
    }

    //发现是否有比新录入更大时间点
    public boolean queryDate(Date date) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Cursor cursor = db.rawQuery("SELECT * FROM memoInfo WHERE DateTime(beginTime) >  DateTime(?)",
                new String[]{dateFormat.format(date)});

        if (!(cursor.getCount() == 0)){
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false ;
        }
    }

    //查询最小时间
    public UUID findMinTime() {
        UUID id;
        Cursor cursor = db.rawQuery("SELECT * FROM memoInfo ORDER BY DateTime(beginTime) ASC",
                null);
        cursor.moveToNext();
        id = UUID.fromString(cursor.getString(cursor.getColumnIndex("uuid")));
        cursor.close();
        return id;
    }

//    选中日期是否存在备忘
    public boolean querySelectedDay(Date date) {
        Cursor cursor = db.rawQuery("SELECT uuid FROM memoInfo WHERE date(beginTime) = date(?) OR date(endTime) = date(?)",
                new String[]{dateFormat.format(date), dateFormat.format(date)});
        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    //
    public ArrayList<UUID> findMemoSeletedDay(Date date) {
        ArrayList<UUID> arrayList = new ArrayList<>();
        //date()精确到天 dateTime精确到秒
        Cursor cursor = db.rawQuery("SELECT uuid FROM memoInfo WHERE date(beginTime) = date(?) OR date(endTime) = date(?)",
                new String[]{dateFormat.format(date), dateFormat.format(date)});
//        cursor.moveToFirst();
//        arrayList.add(UUID.fromString(cursor.getString(cursor.getColumnIndex("uuid"))));
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                arrayList.add(UUID.fromString(cursor.getString(cursor.getColumnIndex("uuid"))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    // OR date(endTime) = date('now', 'localtime')
    public boolean queryToday() {
        Cursor cursor = db.rawQuery("SELECT uuid FROM memoInfo WHERE date(beginTime) = date('now', 'localtime') OR date(endTime) = date('now', 'localtime')",
                null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    //获取当天备忘
    public ArrayList<UUID> findMemoOfToday() {
        ArrayList<UUID> arrayList = new ArrayList<>();
        //date()精确到天 dateTime精确到秒
        Cursor cursor = db.rawQuery("SELECT uuid FROM memoInfo WHERE date(beginTime) = date('now', 'localtime') OR date(endTime) = date('now', 'localtime')",
                null);
//        cursor.moveToFirst();
//        arrayList.add(UUID.fromString(cursor.getString(cursor.getColumnIndex("uuid"))));
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
             do {
                arrayList.add(UUID.fromString(cursor.getString(cursor.getColumnIndex("uuid"))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    public ArrayList<UUID> findMemo() {
        ArrayList<UUID> arrayList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT uuid FROM memoInfo WHERE date(endTime) >= date('now', 'localtime')",
                null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                arrayList.add(UUID.fromString(cursor.getString(cursor.getColumnIndex("uuid"))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    //获取所有过期备忘uuid
    public ArrayList<UUID> findOverdueMemo() {
        ArrayList<UUID> arrayList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT uuid FROM memoInfo WHERE date(endTime) < date('now', 'localtime')",
                null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                arrayList.add(UUID.fromString(cursor.getString(cursor.getColumnIndex("uuid"))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    //
    //
    //
    //这句有问题
    //
    //
    //
    //判断当前时间点是否有未notify的备忘
    public boolean hasCurrentMemo() {
        Cursor cursor = db.rawQuery("SELECT * FROM memoInfo WHERE dateTime(beginTime) <= dateTime('now', 'localtime') AND isDone = ?", new String[]{unDone});
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    //查询当前时间点未notify的备忘
    public UUID findCurrentMemo() {
        Cursor cursor = db.rawQuery("SELECT * FROM memoInfo WHERE dateTime(beginTime) <= dateTime('now', 'localtime') AND isDone = ?", new String[]{unDone});
        cursor.moveToFirst();
        UUID uuid = UUID.fromString(cursor.getString(cursor.getColumnIndex("uuid")));
        cursor.close();

        return uuid;
    }

    //判断是否依然存在memo
    public boolean hasNextMemo() {
        Cursor cursor = db.rawQuery("SELECT * FROM memoInfo WHERE dateTime(beginTime) > dateTime('now', 'localtime')", null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    //获得下一个memo
    public UUID getNextMemo() {
        Cursor cursor = db.rawQuery("SELECT * FROM memoInfo WHERE dateTime(beginTime) > dateTime('now', 'localtime') ORDER BY beginTime ASC", null);
        cursor.moveToFirst();
        UUID uuid = UUID.fromString(cursor.getString(cursor.getColumnIndex("uuid")));
        cursor.close();

        return uuid;
    }

    public void setMemoIsDone(UUID uuid) {
        db.execSQL("UPDATE memoInfo set isDone = ? WHERE uuid = ?" , new String[]{isDone, uuid.toString()});
    }

    public void deleteAll() {
        db.execSQL("DELETE FROM memoInfo");
        db.execSQL("DELETE FROM sketch");
    }

    //关闭数据库
    public void closeDatabase() {
        db.close();
    }

    public boolean hasDataInDB() {
        Cursor cursor = db.rawQuery("SELECT * FROM memoInfo", null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    /*
    *
    *
    *
    * sketch数据库处理
    *
    *
    *
    * */
    public void insertSketch(Sketch sketch) {
        lastMarkString = mergeMark(sketch.getMark());
        db.execSQL("INSERT INTO sketch (uuid, mark) VALUES(?, ?)",
                new String[]{sketch.getId().toString(), lastMarkString});
    }

    public void updateSketch(Sketch sketch) {
        lastMarkString = mergeMark(sketch.getMark());
        db.execSQL("UPDATE sketch set mark = ? WHERE uuid = ?",
                new String[]{lastMarkString, sketch.getId().toString()});
    }

    public void deleteSketch(UUID uuid) {
        db.execSQL("DELETE FROM sketch WHERE uuid = ?", new String[]{uuid.toString()});
    }

    public ArrayList<UUID> searchSketch(String s) {
        stringBuffer = new StringBuffer();
        stringBuffer.append("*").append(s).append("*");
        ArrayList<UUID> arrayList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM sketch WHERE mark GLOB ?",
                new String[]{stringBuffer.toString()});
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                arrayList.add(UUID.fromString(cursor.getString(cursor.getColumnIndex("uuid"))));
            } while (cursor.moveToNext());
            cursor.close();
            return arrayList;
        } else {
            cursor.close();
            return null;
        }
    }

    private String mergeMark(ArrayList<String> as) {
        stringBuffer = new StringBuffer();
        for (String s:as) {
            stringBuffer.append(s);
        }
        return stringBuffer.toString();
    }
}

















































