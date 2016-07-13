package com.whguardian.android.memo.MemoInfo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;

import com.whguardian.android.memo.AlarmReceiver;
import com.whguardian.android.memo.Calendar.CalendarActivity;
import com.whguardian.android.memo.DBManager;
import com.whguardian.android.memo.MemoData.MemoInfo;
import com.whguardian.android.memo.MemoData.MemoLab;
import com.whguardian.android.memo.MemoRemindService;
import com.whguardian.android.memo.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Created by whguardian_control on 16/03/15.
 * comment: 备忘录备忘
 */
public class MemoFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MemoFragment";

    //测试用间隔时间
//    private static final int INTERAVL = 1000 * 61;

    //创建时传递信息
    public static final String EXTRA_MEMO_ID =
            "com.whguardian.android.memo.memoInfoId";
    public static final String HAS_UUID =
            "com.whguardian.android.memo.hasNoUUID";

    //Dialog标识
    private static final String DIALOG_BEGIN_CALENDAR = "begin_calendar";
    private static final String DIALOG_END_CALENDAR = "end_calendar";
    private static final String DIALOG_BEGIN_TIME = "begin_time";
    private static final String DIALOG_END_TIME = "end_time";

    //requestCode
    private static final int REQUEST_BEGIN_CALENDAR = 0;
    private static final int REQUEST_END_CALENDAR = 1;
    private static final int REQUEST_BEGIN_TIME = 2;
    private static final int REQUEST_END_TIME = 3;
    //请求系统原生备忘录requestCode
    private static final int REQUEST_CONTACTS = 110;

    private EditText titleEditText;
    private EditText contentEditText;
    private Button beginCalendarButton;
    private Button endCalendarButton;
    private Button beginTimeBtn;
    private Button endTimeBtn;
    private Button saveBtn;
    /*
    * 调用原生通讯录的按钮
    * */
    private EditText contactsEditText;
    private Button contactsButton;
    private String username;
    private String usernumber;

    //初始化数据Buffer
    private CharSequence mTitle = "no theme";
    private CharSequence mContent = "no detail";
    private Date mBeginTime = new Date();
    private Date mEndTime = new Date();
    //将四个时间按钮的数据收集
    private Date mBeginTime1;
    private Date mBeginTime2;
    private Date mEndTime1;
    private Date mEndTime2;
//    private boolean mIsAllDay = false;


    private FragmentManager dateFM;

    private MemoInfo memoInfo;

    private DBManager db;

    public static MemoFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_MEMO_ID, uuid);
        args.putBoolean(HAS_UUID, true);

        MemoFragment fragment = new MemoFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //发现传递的Memo或创建一个Memo
        if (getArguments().getSerializable(EXTRA_MEMO_ID) == null) {
            //创建一个新Memo
            memoInfo = new MemoInfo();
            //在不操作视图的情况的默认值
            memoInfo.setTitle(mTitle.toString());
            memoInfo.setContent(mContent.toString());
            memoInfo.setBeginTime(mBeginTime);
            memoInfo.setEndTime(mEndTime);
            //为saveMemo中的数据赋予初值
            mBeginTime1 = memoInfo.getBeginTime();
            mBeginTime2 = memoInfo.getBeginTime();
            mEndTime1 = memoInfo.getEndTime();
            mEndTime2 = memoInfo.getEndTime();

        } else {
            //通过Argument获得Memo实例
            UUID uuid = (UUID) getArguments().getSerializable(EXTRA_MEMO_ID);
            memoInfo = MemoLab.get(getActivity()).getMemoInfo(uuid);
            //防止打开已有memo无操作就改变时间设置
            mBeginTime1 = memoInfo.getBeginTime();
            mBeginTime2 = memoInfo.getBeginTime();
            mEndTime1 = memoInfo.getEndTime();
            mEndTime2 = memoInfo.getEndTime();
            if (memoInfo.getmPhoneNumber() != null) {
                usernumber = memoInfo.getmPhoneNumber();
            }
        }
    }

//    public static MemoFragment newInstance(boolean hasUUID) {
//        Bundle args = new Bundle();
//        args.putBoolean(HAS_UUID, hasUUID);
//
//        MemoFragment fragment = new MemoFragment();
//        fragment.setArguments(args);
//
//        return fragment;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_memo, container, false);
        setRetainInstance(true);

        titleEditText = (EditText) v.findViewById(R.id.memo_title);
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //输入改变是设置MemoInfo标题
                memoInfo.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        contentEditText = (EditText) v.findViewById(R.id.memo_detail);
        contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //设置改变时社会中Memo具体内容
                memoInfo.setContent(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        GregorianCalendar calendar = new GregorianCalendar();

        /*
        * 初始化所有View
        * */
        beginCalendarButton = (Button) v.findViewById(R.id.begin_date_calender);
        beginCalendarButton.setText(String.format("%tD%n", calendar));
        beginCalendarButton.setOnClickListener(this);
        endCalendarButton = (Button) v.findViewById(R.id.end_date_calender);
        endCalendarButton.setText(String.format("%tD%n", calendar));
        endCalendarButton.setOnClickListener(this);
        beginTimeBtn = (Button) v.findViewById(R.id.begin_date_time);
        beginTimeBtn.setText(String.format("%tR", calendar));
        beginTimeBtn.setOnClickListener(this);
        endTimeBtn = (Button) v.findViewById(R.id.end_date_time);
        endTimeBtn.setText(String.format("%tR", calendar));
        endTimeBtn.setOnClickListener(this);

        /*
        * 调用原生通讯录
        *
        * */
        contactsEditText = (EditText)v.findViewById(R.id.editText_contacts);
        contactsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usernumber = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        contactsButton = (Button)v.findViewById(R.id.button_contacts);
        contactsButton.setOnClickListener(this);


        /*
        *
        * 判断在创建fragment时是否有arguments被传递
        * 如果有，获取arguments并重新为需要的View赋值
        *
        * */
        if (getArguments().getSerializable(EXTRA_MEMO_ID) != null) {
            //从ListFragment进入时，重新设置视图中的初始显示数据
            titleEditText.setText(memoInfo.getTitle());
            contentEditText.setText(memoInfo.getContent());
            beginCalendarButton.setText(String.format("%tD%n", memoInfo.getBeginTime()));
            endCalendarButton.setText(String.format("%tD%n", memoInfo.getEndTime()));
            beginTimeBtn.setText(String.format("%tR", memoInfo.getBeginTime()));
            endTimeBtn.setText(String.format("%tR", memoInfo.getEndTime()));
            if (usernumber != null) {
                contactsEditText.setText(usernumber);
            }
        }

        saveBtn = (Button) v.findViewById(R.id.memo_create);
        saveBtn.setOnClickListener(this);

        return v;
    }

    //View.onClickListener()
    @Override
    public void onClick(View v) {
        dateFM = getActivity().getFragmentManager();
        switch (v.getId()) {

            /*
            *
            * 四个时间Button，通过onActivityResult（）回传会的数
            * 据更改四个按钮中的时间，并保存时间到缓存，在点击
            * save时组合时间
            *
            * */
            case R.id.begin_date_calender:
                DatePickerFragment beginDatePickerFragment =
                        DatePickerFragment.newInstance(memoInfo.getBeginTime());
                beginDatePickerFragment.setTargetFragment(MemoFragment.this,
                        REQUEST_BEGIN_CALENDAR);
                beginDatePickerFragment.show(dateFM, DIALOG_BEGIN_CALENDAR);
                break;
            case R.id.end_date_calender:
                DatePickerFragment endDatePickerFragment =
                        DatePickerFragment.newInstance(memoInfo.getEndTime());
                endDatePickerFragment.setTargetFragment(MemoFragment.this,
                        REQUEST_END_CALENDAR);
                endDatePickerFragment.show(dateFM, DIALOG_END_CALENDAR);
                break;
            case R.id.begin_date_time:
                TimePickerFragment beginTimePickerFragment = TimePickerFragment.newInstance();
                beginTimePickerFragment.setTargetFragment(MemoFragment.this,
                        REQUEST_BEGIN_TIME);
                beginTimePickerFragment.show(dateFM, DIALOG_BEGIN_TIME);
                break;
            case R.id.end_date_time:
                TimePickerFragment endTimePickerFragment = TimePickerFragment.newInstance();
                endTimePickerFragment.setTargetFragment(MemoFragment.this,
                        REQUEST_END_TIME);
                endTimePickerFragment.show(dateFM, DIALOG_END_TIME);
                break;

            /*
            *
            * 调用原生通讯录
            *
            * */
            case R.id.button_contacts:
                Intent contactsIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                getActivity().startActivityForResult(contactsIntent, REQUEST_CONTACTS);
                break;


            case R.id.memo_create:
                GregorianCalendar calendar = new GregorianCalendar();

                /*
                *
                * 组合时间，形成两个完整的开始与结束时间
                * */
                calendar.setTime(mBeginTime1);
                int bYear = calendar.get(Calendar.YEAR);
                int bMonth = calendar.get(Calendar.MONTH);
                int bDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                //开始time
                calendar.setTime(mBeginTime2);
                //获得24小时制
                int bHour = calendar.get(Calendar.HOUR_OF_DAY);
                /*
                获得12小时
                int bHour = calendar.get(Calendar.HOUR);
                 */
                int bMinute = calendar.get(Calendar.MINUTE);

                mBeginTime = new GregorianCalendar(bYear, bMonth, bDayOfMonth, bHour, bMinute)
                        .getTime();
                memoInfo.setBeginTime(mBeginTime);

                //结束年月
                calendar.setTime(mEndTime1);
                int eYear = calendar.get(Calendar.YEAR);
                int eMonth = calendar.get(Calendar.MONTH);
                int eDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                calendar.setTime(mEndTime2);
                int eHour = calendar.get(Calendar.HOUR_OF_DAY);
                int eMinute = calendar.get(Calendar.MINUTE);

                mEndTime = new GregorianCalendar(eYear, eMonth, eDayOfMonth, eHour, eMinute)
                        .getTime();
                memoInfo.setEndTime(mEndTime);

                /*
                * 保存电话
                * */
                if (usernumber != null) {
                    memoInfo.setmPhoneNumber(usernumber);
                }

                //
                //
                ///
                ///
                ///正在修改
                ///
                ///
                ///
                ///
                //

//                if (!getArguments().getBoolean(HAS_UUID)) {
                //创建或修改Memo
                //数据库内Memo创建或修改
                db = new DBManager(getActivity());
                if (getArguments().getSerializable(EXTRA_MEMO_ID) == null) {
//                    memoInfo = new MemoInfo();
//                    memoInfo.setTitle(mTitle.toString());
//                    memoInfo.setContent(mContent.toString());
//                    memoInfo.setBeginTime(mBeginTime);
//                    memoInfo.setEndTime(mEndTime);
                    MemoLab.get(getActivity()).getmMemoInfos().add(memoInfo);

                    db.insert(memoInfo);
                } else {
                    db.update(memoInfo);
//                    memoInfo = MemoLab.get(getActivity())
//                            .getMemoInfo((UUID)getArguments().getSerializable(EXTRA_MEMO_ID));
                    //下面代码有大问题，可能设置为错误
//                    memoInfo.setTitle(mTitle.toString());
//                    memoInfo.setContent(mContent.toString());
//                    memoInfo.setBeginTime(mBeginTime);
//                    memoInfo.setEndTime(mEndTime);
                }
                db.closeDatabase();


//                提取下一个需要notify的memo uuid
//                db = new DBManager(getActivity());
//                Bundle bundle = new Bundle();
//                UUID memoUUID;
//                //在当前时间点是否有备忘存在，存在立即notify
//                if (db.hasCurrentMemo()) {
//                    memoUUID = db.findCurrentMemo();
//                    bundle.putString(MemoRemindService.EXTRA_ID, memoUUID.toString());
//                } else {
//                    //查询最近时间点的备忘，发送给AlarmManager
//                    memoUUID = db.getNextMemo();
//                    bundle.putString(MemoRemindService.EXTRA_ID, memoUUID.toString());
//                }
//                //关闭数据库
//                db.closeDatabase();

//                bundle.putString(MemoRemindService.EXTRA_ID, memoInfo.getId().toString());
                Log.i(TAG, "在MemoFragment中的UUID是：" + memoInfo.getId().toString());
                Intent i = new Intent(getActivity(), MemoRemindService.class);
//                i.putExtras(bundle);
//                i.putExtra(MemoRemindService.EXTRA_ID, memoInfo.getId());
//                Log.i(TAG, "uuid:" + memoInfo.getId() + "beginTime is:" + memoInfo.getBeginTime());
                getActivity().startService(i);

                //Test for Notification
//                Intent i = new Intent(getActivity(), AlarmReceiver.class);
//                i.putExtra(AlarmReceiver.EXTRA_UUID, memoInfo.getId());
//                PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, i, 0);
//
//                Calendar alarmCalendar = Calendar.getInstance();
//                alarmCalendar.setTime(memoInfo.getBeginTime());
//
//                AlarmManager alarmManager =
//                        (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pi);
//                alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), INTERAVL, pi);
                //Test for Notification

                getActivity().finish();
//                CalendarActivity.calendarActivity.finish();
//                Intent intent = new Intent(getActivity(), CalendarActivity.class);
//                startActivity(intent);
                break;
            default:
                break;
        }
    }

    //被使用与onClick中switch case R.id.memo_create


    /*
    *
    * 重写Activity.onActivityResult(...)在Fragment间传递信息
    * 在onActivityResult(...)被调用时会调用Fragment中的
    * onActivityResult(...)
    *
    * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        Date date;

        switch (requestCode) {
            case REQUEST_BEGIN_CALENDAR:
                date = (Date) data
                        .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                beginCalendarButton.setText(String.format("%tD%n", date));
                mBeginTime1 = date;
                break;
            case REQUEST_END_CALENDAR:
                date = (Date) data
                        .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                endCalendarButton.setText(String.format("%tD%n", date));
                mEndTime1 = date;
                break;
            case REQUEST_BEGIN_TIME:
                date = (Date) data
                        .getSerializableExtra(TimePickerFragment.EXTRA_DATA);
                beginTimeBtn.setText(String.format("%tR", date));
                mBeginTime2 = date;
                break;
            case REQUEST_END_TIME:
                date = (Date) data
                        .getSerializableExtra(TimePickerFragment.EXTRA_DATA);
                endTimeBtn.setText(String.format("%tR", date));
                mEndTime2 = date;
                break;
            /*
            *
            * 通过调用系统原生的通讯录换取需要的联系人
            * 通过StartActivityForResult(...)区分回调
            *
            * */
            case REQUEST_CONTACTS:
                Log.i(TAG,"从原生备忘录捕获信息");
                ContentResolver reContentResolverol = getActivity().getContentResolver();
                Uri contactData = data.getData();
                @SuppressWarnings("deprecation")
//                Cursor cursor = managedQuery(contactData, null, null, null, null);
                Cursor cursor = reContentResolverol.query(contactData, null, null, null, null);
                cursor.moveToFirst();
                username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                        null,
                        null);
                while (phone.moveToNext()) {
                    usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contentEditText.setText(usernumber+" ("+username+")");
                }
                cursor.close();
                phone.close();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MemoLab.get(getActivity()).saveMemos();
        Log.i(TAG, "MemoLab is saved");
    }
}
