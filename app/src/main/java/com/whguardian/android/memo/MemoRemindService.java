package com.whguardian.android.memo;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.whguardian.android.memo.MemoData.MemoInfo;
import com.whguardian.android.memo.MemoData.MemoLab;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/09.
 * Comment:保持后台提醒service不受重启设备影响，创建service
 */
public class MemoRemindService extends IntentService {
    private static final String TAG = "MemoRemindService";
    public static final String EXTRA_ID =
            "com.whguardian.android.memo.MemoRemingService.id";

    //当没有备忘是设置AlarmManager为null
    private boolean setAlarmIsNull = false;

    public static  Bundle bundle = new Bundle();

    private DBManager db;

    private UUID uuid;

    private boolean haveMemo = true;

    public MemoRemindService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "service is start");
        //打开数据库
//        DBManager db = new DBManager(this);
        //删除过期备忘
//        db.deleteOverdue();
        //最小开始时间
//        uuid = db.findMinTime();
        //关闭数据库
//        db.closeDatabase();
    }

    @Override
    public void onHandleIntent(Intent intent) {
        Log.i(TAG, "handle intent");

//        NotificationManager fm = (NotificationManager) getApplicationContext()
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        fm.cancel(10);

        db = new DBManager(this);
        if (db.hasCurrentMemo()) {
            uuid = db.findCurrentMemo();
            Log.i(TAG, "将一个现在或过期的memo加入AlarmManager");
        } else {
            if (db.hasNextMemo()) {
                Log.i(TAG, "讲一个以后的memo加入AlarmManager");
                uuid = db.getNextMemo();
            } else {
//                this.stopSelf();
                haveMemo = false;
                Log.i(TAG, "没有需要提醒的备忘，所有工作完成");
                setAlarmIsNull = true;
            }
        }
        db.closeDatabase();

        if (haveMemo) {
//        uuid = UUID.fromString(intent.getStringExtra(EXTRA_ID));
            Log.i(TAG, "uuid:" + uuid.toString());

            //获取uuid对应备忘开始时间
            MemoInfo memoInfo = MemoLab.get(MemoRemindService.this).getMemoInfo(uuid);
            Log.i(TAG, "Memo中的UUID是：" + memoInfo.getId().toString());

            Date date = memoInfo.getBeginTime();
            Log.i(TAG, "beginTime is" + date);
            //第一次初始化calendar无法初始化 小时 分，需要具体参数赋值
            // 通过第二次赋值完成 年 月 日 小时 分 赋值
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            calendar = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute);





//            Bundle bundle = new Bundle();
            bundle.putString(AlarmReceiver.EXTRA_UUID, uuid.toString());
            Intent i = new Intent(MemoRemindService.this, AlarmReceiver.class);
//            i.putExtras(bundle);
//            i.putExtra(AlarmReceiver.EXTRA_UUID, uuid);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
            Log.i(TAG, "beginTime is" + memoInfo.getBeginTime());





            //闹钟设置
            AlarmManager alarmManager = (AlarmManager)
                    getApplication().getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }

        if (setAlarmIsNull) {
            Intent i = new Intent(MemoRemindService.this, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
            AlarmManager alarmManager = (AlarmManager)
                    getApplication().getSystemService(Context.ALARM_SERVICE);

            //关闭AlarmManager
            alarmManager.cancel(pi);
            pi.cancel();
            Log.i(TAG, "一个AlarmManager被删除");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "service is stop");
    }
}
