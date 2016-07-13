package com.whguardian.android.memo;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.whguardian.android.memo.MemoData.MemoInfo;
import com.whguardian.android.memo.MemoData.MemoLab;

import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/14.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    public static final String EXTRA_UUID =
            "com.whguardian.android.memo.AlarmReceiver.uuid";

    private DBManager db;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "receiver is running");

        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        wl.acquire();

        //静态的bundle中获取uuid
        UUID uuid = UUID.fromString(MemoRemindService.bundle.getString(EXTRA_UUID));

//        UUID uuid = UUID.fromString(intent.getStringExtra(EXTRA_UUID));
//        UUID uuid = UUID.fromString(intent.getStringExtra(EXTRA_UUID));
        Log.i(TAG, "在AlarmReceiver中的UUID是" + uuid.toString());

        MemoInfo memoInfo = MemoLab.get(context)
                .getMemoInfo(uuid);

        Resources r = context.getResources();
//        PendingIntent i = PendingIntent

        String content = memoInfo.getContent() + String.format("%tD%n %tR", memoInfo.getBeginTime(),
                memoInfo.getBeginTime()) +
                String.format("%tD%n %tR", memoInfo.getEndTime(),
                        memoInfo.getEndTime());

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(memoInfo.getTitle())
                .setContentText(content)
                .build();

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

        if (memoInfo.getmPhoneNumber() != null) {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(memoInfo.getmPhoneNumber(), null, memoInfo.getContent(), null, null);
        }

        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁点亮显示器
        kl.disableKeyguard();
        //锁定
        kl.reenableKeyguard();

        db = new DBManager(context);
        db.setMemoIsDone(uuid);
        db.closeDatabase();
        Log.i(TAG,"将显示Notification的memo标记为isDone");

        Intent i = new Intent(context, MemoRemindService.class);
        context.startService(i);

        wl.release();
    }
}
