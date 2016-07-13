package com.whguardian.android.memo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.whguardian.android.memo.MemoData.MemoInfo;
import com.whguardian.android.memo.MemoData.MemoLab;

import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/09.
 */
public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Receiver broadcast intent: "  + intent.getAction());

        //开机启动备忘提醒服务
        Intent i = new Intent(context, MemoRemindService.class);
        context.startService(i);
    }
}
