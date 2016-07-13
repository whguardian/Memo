package com.whguardian.android.memo.MemoInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import com.whguardian.android.memo.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by whguardian_control on 16/04/06.
 * comment: 备忘录时间弹出设置
 */
public class TimePickerFragment extends DialogFragment
        implements TimePicker.OnTimeChangedListener{

    public static final String EXTRA_DATA =
            "com.whguardian.android.memo.memoTime";

    private Date mDate;

    public static TimePickerFragment newInstance() {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATA, null);

        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setArguments(args);

        return timePickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_time, null);



        mDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        TimePicker timePicker = (TimePicker)v.findViewById(R.id.dialog_date_timePicker);
        //设置时间选择为24小时制
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(this);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_pick)
                .setPositiveButton(R.string.time_choose, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        mDate = new GregorianCalendar(0, 0, 0, hourOfDay, minute).getTime();
    }

    //回调函数，回调时间
    private void sendResult(int resultCode) {
        if(getTargetFragment() == null)
            return;
        Intent i = new Intent();
        i.putExtra(EXTRA_DATA, mDate);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
