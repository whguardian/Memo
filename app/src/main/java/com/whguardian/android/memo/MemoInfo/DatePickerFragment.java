package com.whguardian.android.memo.MemoInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.audiofx.EnvironmentalReverb;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.whguardian.android.memo.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by whguardian_control on 16/03/15.
 * comment: 备忘信息日期设置弹出
 */
public class DatePickerFragment extends DialogFragment implements DatePicker.OnDateChangedListener{
    private static final String TAG ="DatePickerFragment";

    public static final String EXTRA_DATE =
            "com.whagurdian.android.memo.memoInfo.DatePickerFragment.Date";

    private Date mDate;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);

        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(args);

        return datePickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDate = new Date();
        mDate = (Date)getArguments().getSerializable(EXTRA_DATE);

        Log.i(TAG, "get the time of memo" + getArguments().getSerializable(EXTRA_DATE));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_calendar, null);

        DatePicker datePicker = (DatePicker) v.findViewById(R.id.dialog_date_datePicker);
        datePicker.init(year, month, day, this);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.calendar_pick)
                .setPositiveButton(R.string.calendar_choose, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    @Override
    public void onDateChanged(DatePicker view , int year, int month, int day) {
        mDate = new GregorianCalendar(year, month, day).getTime();
        Log.i(TAG,"onDateChanged is running");
    }

    //回调函数，回调时间
    private void sendResult(int resultCode) {
        if (getTargetFragment() == null)
            return;
        Intent i = new Intent();
        i.putExtra(EXTRA_DATE, mDate);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, i);
    }


}
