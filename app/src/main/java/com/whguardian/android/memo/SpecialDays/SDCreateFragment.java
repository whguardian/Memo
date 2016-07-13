package com.whguardian.android.memo.SpecialDays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.whguardian.android.memo.MemoData.SpecialDay;
import com.whguardian.android.memo.MemoData.SpecialLab;
import com.whguardian.android.memo.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/22.
 */
public class SDCreateFragment extends DialogFragment implements View.OnClickListener,
        DialogInterface.OnClickListener, DatePicker.OnDateChangedListener{
    private static final String TAG ="SDCreateFragment";
    private static final String EXTRA_ID =
            "android.whguardian.android.memo.SpecialDays.SDCreateFragment.uuid";

    private View v;
    private View mView;

    private AlertDialog.Builder builder;
    private Dialog dialog;

    /*
    * 模型与视图之间传递数据的缓冲器
    * */
    private StringBuffer themeBuffer;
    private StringBuffer contentBuffer;
    private Date dateBuffer;

    private SpecialDay specialDay;

    private EditText themeEdit;
    private EditText contentEdit;
    private DatePicker datePicker;

    public static SDCreateFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_ID, uuid);

        SDCreateFragment fragment = new SDCreateFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /*
        *
        * 创建一个AlertDialog.Builder，在onCreateDialog(...)中
        * 被赋予View，并使用create(...)创建，并返回一个Dialog
        *
        * */
        builder = new AlertDialog.Builder(getActivity())
                .setTitle("特殊")
                .setPositiveButton(android.R.string.ok, this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //初始化片段
        if (getArguments().getSerializable(EXTRA_ID) == null) {
            /*
            * 创建一个SpecialDay实例
            * */
            specialDay = new SpecialDay();
            mView = createSpecialDays();
        } else {
            /*
            * 存在数据传入时，初始化View中的数据为传入数据
            *
            * */
            specialDay = SpecialLab.get(getActivity())
                    .getSpecial((UUID)getArguments().getSerializable(EXTRA_ID));
            themeBuffer = new StringBuffer(specialDay.getTheme());
            contentBuffer = new StringBuffer(specialDay.getContent());
            dateBuffer = specialDay.getDate();
            mView = createSpecialDays(themeBuffer, contentBuffer, dateBuffer);
        }

        setDate();

        //Dialog内容穿件
        builder.setView(mView);
        dialog = builder.create();
        return dialog;
    }



    //创建日期选择
    private View createSpecialDays() {
        v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_specialdays_create, null);

        themeBuffer = new StringBuffer();
        contentBuffer = new StringBuffer();
        dateBuffer = new Date();

        themeEdit = (EditText)v.findViewById(R.id.editText_special_theme);
        themeEdit.addTextChangedListener(new SpecialTextWatcher(themeEdit));
        contentEdit = (EditText) v.findViewById(R.id.editText_special_content);
        contentEdit.addTextChangedListener(new SpecialTextWatcher(contentEdit));
        datePicker = (DatePicker)v.findViewById(R.id.datePicker_specialDays_create);

        return v;
    }

    /*
    * 存在EXTRA_ID时为View赋予初值
    * */
    private View createSpecialDays(StringBuffer theme, StringBuffer content, Date date) {
        v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_specialdays_create, null);

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        int year = gregorianCalendar.get(Calendar.YEAR);
        int month = gregorianCalendar.get(Calendar.MONTH);
        int dayOfMonth = gregorianCalendar.get(Calendar.DAY_OF_MONTH);

        themeEdit = (EditText)v.findViewById(R.id.editText_special_theme);
        themeEdit.setText(theme);
        themeEdit.addTextChangedListener(new SpecialTextWatcher(themeEdit));
        contentEdit = (EditText) v.findViewById(R.id.editText_special_content);
        contentEdit.setText(content);
        contentEdit.addTextChangedListener(new SpecialTextWatcher(contentEdit));
        datePicker = (DatePicker)v.findViewById(R.id.datePicker_specialDays_create);
        datePicker.init(year, month, dayOfMonth, this);


        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        setDate();
        specialDay.setTheme(themeBuffer.toString());
        specialDay.setContent(contentBuffer.toString());
        specialDay.setDate(dateBuffer);
        if (getArguments().getSerializable(EXTRA_ID) == null) {
            SpecialLab.get(getActivity())
                    .getSpecialDays().add(specialDay);
            Log.i(TAG ,"数据已经保存");
        } else {
            Log.i(TAG ,"数据已经保存");
            if (getTargetFragment() == null)
                return;
            Intent i = new Intent();
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        }

    }

    public class SpecialTextWatcher implements TextWatcher {

        EditText ed;

        public SpecialTextWatcher(EditText e) {
            ed = e;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            switch (ed.getId()) {
                case R.id.editText_special_theme:
                    if (themeBuffer.length() != 0) {
                        themeBuffer.delete(0, themeBuffer.length());
                    }
                    themeBuffer.append(s);
                    break;
                case R.id.editText_special_content:
                    if (contentBuffer.length() != 0) {
                        contentBuffer.delete(0, contentBuffer.length());
                    }
                    contentBuffer.append(s);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int dayOfMonth) {
        dateBuffer = new GregorianCalendar(year, month, dayOfMonth).getTime();
        Log.i(TAG, "接听时间变化");
    }

    public void setDate() {
        dateBuffer = new GregorianCalendar(datePicker.getYear(),
                datePicker.getMonth(), datePicker.getDayOfMonth()).getTime();
    }

    @Override
    public void onPause() {
        super.onPause();
        SpecialLab.get(getActivity()).saveSpecials();
    }
}























