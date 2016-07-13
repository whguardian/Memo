package com.whguardian.android.memo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.whguardian.android.memo.Calendar.CalendarActivity;

/**
 * Created by whguardian_control on 16/04/18.
 * comment: 用以在主屏幕显示的widget
 */
public class MemoAppWidget extends AppWidgetProvider {
    private static final String TAG = "AppWidgetProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        RemoteViews remoteViews =
                new RemoteViews(context.getPackageName(), R.layout.appwidget_memo_info);

        Intent i = new Intent(context, CalendarActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);
        remoteViews.setOnClickPendingIntent(R.id.appwidget_memo, pi);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.i(TAG,"AppWidget has been found");
    }
}
