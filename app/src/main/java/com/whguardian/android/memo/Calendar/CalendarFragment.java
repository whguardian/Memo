package com.whguardian.android.memo.Calendar;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.whguardian.android.memo.DBManager;
import com.whguardian.android.memo.MemoData.MemoInfo;
import com.whguardian.android.memo.MemoData.MemoLab;
import com.whguardian.android.memo.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;
import java.util.zip.Inflater;

/**
 * Created by whguardian_control on 16/04/04.
 */
public class CalendarFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "CalendarFragment";
    private static final String DIALOG_SELECTED_DAY ="selected";

    private static final int VIEW_TAG = 24;

    private TextView calendarTX;
    private TableLayout calendarTable;
    private TextView calendarInfoTX;

    private TextView lastMonthTX;
    private TextView nextMonthTX;

    private GregorianCalendar thisCalendar;
    private GregorianCalendar thisCalendar2;
    private int thisMonth;

    //存储需要显示的日历
//    private ArrayList<String> calendarArrayList;
    private ArrayList<Calendar> calendarArrayList;

    private DBManager db;

    public CalendarFragment() {
    }

    public static CalendarFragment newInstance() {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        *
        *
        * 设置显示页的数据
        *
        * */
        //获取本月
        thisCalendar = new GregorianCalendar();
        thisCalendar2 = new GregorianCalendar();
        thisMonth = thisCalendar.get(Calendar.MONTH);
        //获取在显示的日期数组
        calendarArrayList = new ArrayList<>();
        for (Calendar c:getCalendarArrayList(new GregorianCalendar())) {
            calendarArrayList.add(c);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.sub_calendar);
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        /*
        * 当前，选中月
        * */
        calendarTX = (TextView)v.findViewById(R.id.calendar_today);
        calendarTX.setText(String.format(Locale.getDefault(), "%d-%d",
                thisCalendar.get(Calendar.YEAR),(thisCalendar.get(Calendar.MONTH) + 1) ));
        /*
        *
        * 当前选中月的日历
        *
        * */
        calendarTable = (TableLayout)v.findViewById(R.id.table_calendar);
        setCalendarDate();

        calendarInfoTX = (TextView)v.findViewById(R.id.info_calendar_selected);
        calendarInfoTX.setTag(101);
        calendarInfoTX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),
                        String.format(Locale.getDefault(), "当前点击的日期是：%d", (int)v.getTag()),
                        Toast.LENGTH_SHORT).show();
                v.getTag();

                GregorianCalendar gCalendar = new GregorianCalendar();
                gCalendar.set(Calendar.YEAR, thisCalendar.get(Calendar.YEAR));
                gCalendar.set(Calendar.MONTH, thisCalendar.get(Calendar.MONTH));
                gCalendar.set(Calendar.DAY_OF_MONTH, (int)v.getTag());
                Date mDate = gCalendar.getTime();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String selectedTime = sdf.format(mDate);
                FragmentManager fManager = getActivity().getFragmentManager();
                DayInfoFragment dayInfoFragment = DayInfoFragment.newInstance(selectedTime);
                dayInfoFragment.show(fManager, DIALOG_SELECTED_DAY);
            }
        });

       /*
        * 前后两个月设置
        * */
        lastMonthTX = (TextView)v.findViewById(R.id.calendar_lastMonth);
        lastMonthTX.setText(String.format(Locale.getDefault(), "%d-%d",
                thisCalendar.get(Calendar.YEAR),(thisCalendar.get(Calendar.MONTH)) ));
//        lastMonthTX.setOnClickListener(this);
        lastMonthTX.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.calendar_lastMonth:
                        setAfterMonthChanged(-1);
                        break;
                    case R.id.calendar_nextMonth:
                        setAfterMonthChanged(1);
                        break;
                }
            }
        });

        nextMonthTX = (TextView)v.findViewById(R.id.calendar_nextMonth);
        nextMonthTX.setText(String.format(Locale.getDefault(), "%d-%d",
                thisCalendar.get(Calendar.YEAR),(thisCalendar.get(Calendar.MONTH) + 2) ));
//        nextMonthTX.setOnClickListener(this);
        nextMonthTX.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.calendar_lastMonth:
                        setAfterMonthChanged(-1);
                        break;
                    case R.id.calendar_nextMonth:
                        setAfterMonthChanged(1);
                        break;
                }
            }
        });

        /*
        * 显示点击Button日期的备忘
        * 条件：本月日期，存在备忘
        *
        * */
        calendarInfoTX = (TextView)v.findViewById(R.id.info_calendar_selected);
        calendarInfoTX.setEnabled(false);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
        * 打开数据库
        * */
        db = new DBManager(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        /*
        * 关闭数据库
        * */
        db.closeDatabase();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /*
    *
    * 获取当前的日期月所需要显示的日期
    *
    * */
    private ArrayList<Calendar> getCalendarArrayList(GregorianCalendar gregorianCalendar) {
        GregorianCalendar calendar = gregorianCalendar;
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        ArrayList<Calendar> arrayList = new ArrayList<>();

        //这个星期的第一天是周日，或者获取上一个月的最后一个周日
        if (calendar.get(Calendar.DAY_OF_WEEK) != 1) {
            while (calendar.get(Calendar.DAY_OF_WEEK) != 1) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            }
        }

//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date;

        if (arrayList.isEmpty()) {
            while (arrayList.size() < 42) {
                date = calendar.getTime();
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(date);
//                arrayList.add(formatter.format(date));
                arrayList.add(cal);
                //日期加一天
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        /*
        * 复位被引用的实例thisCalendar2
        * */
        if (!gregorianCalendar.equals(thisCalendar)) {
            gregorianCalendar.add(Calendar.MONTH, -1);
        }

        return arrayList;
    }

    /*
    * 为每一个日期按钮设置日期
    *
    * */
    private void setCalendarDate() {
        int x = 0;
        int dayOfMonth = 1;
        int pastMonth = 0;
        int nextMonth = 1;
        for (int i = 0; i < calendarTable.getChildCount(); i++) {
            TableRow row = (TableRow) calendarTable.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                Button button = (Button)row.getChildAt(j);
                /*
                * 判断显示日期是否与当月日期
                *
                * */
                if (calendarArrayList.get(x).get(Calendar.MONTH) == thisMonth) {
                    button.setText(Integer.toString(dayOfMonth));
                    button.setTextColor(Color.BLACK);
                    /*
                    * 设置Tag为日期
                    * */
                    button.setTag(calendarArrayList.get(x).get(Calendar.DAY_OF_MONTH));
                    dayOfMonth++;
                } else {
//                    非本月按钮
                    button.setTextColor(Color.WHITE);
                    if (calendarArrayList.get(pastMonth).get(Calendar.DAY_OF_MONTH) > 7){
                        button.setText(Integer.toString(calendarArrayList.get(pastMonth).get(Calendar.DAY_OF_MONTH)));
                        /*
                        * 设置上月Tag为0
                        * */
                        button.setTag(0);
                        pastMonth++;
                    } else {
                        button.setText(Integer.toString(nextMonth));
                        /*
                        * 设置下月Tag为100
                        * */
                        button.setTag(100);
                        nextMonth++;
                    }
//                    button.setText();
                }
                button.setOnClickListener(this);
                x++;
            }
        }
    }

    /*
    *
    * 当点击上方年月时切换日历所需要完成的操作
    *
    * */
    private void setAfterMonthChanged(int i) {
        thisCalendar.add(Calendar.MONTH, i);
        thisCalendar2.add(Calendar.MONTH, i);
        thisMonth = thisCalendar.get(Calendar.MONTH);
        calendarArrayList = new ArrayList<>();
        calendarTX.setText(String.format(Locale.getDefault(), "%d-%d",
                thisCalendar.get(Calendar.YEAR),(thisCalendar.get(Calendar.MONTH) + 1) ));
        lastMonthTX.setText(String.format(Locale.getDefault(), "%d-%d",
                thisCalendar.get(Calendar.YEAR),(thisCalendar.get(Calendar.MONTH)) ));
        nextMonthTX.setText(String.format(Locale.getDefault(), "%d-%d",
                thisCalendar.get(Calendar.YEAR),(thisCalendar.get(Calendar.MONTH) + 2) ));
        for (Calendar c:getCalendarArrayList(thisCalendar2)) {
            calendarArrayList.add(c);
        }
        setCalendarDate();
    }

    @Override
    public void onClick(View v) {
        switch ((int)v.getTag()) {
            case 0:
                setAfterMonthChanged(-1);
                break;
            case 100:
                setAfterMonthChanged(1);
                break;
            default:
                GregorianCalendar gc = new GregorianCalendar();
                gc.set(Calendar.YEAR, thisCalendar.get(Calendar.YEAR));
                gc.set(Calendar.MONTH, thisMonth);
                gc.set(Calendar.DAY_OF_MONTH, (int)v.getTag());
                Date d = gc.getTime();

                Toast.makeText(getActivity(), String.format(Locale.getDefault(), "点击日期：%s",String.valueOf(((Button)v).getText())),
                        Toast.LENGTH_SHORT)
                        .show();
                calendarInfoTX.setText(String.format(Locale.getDefault(), "点击日期：%s",
                        String.valueOf(((Button)v).getText())));
                calendarInfoTX.setTag(v.getTag());
                if(db.querySelectedDay(d)) {
                    calendarInfoTX.setText(String.format(Locale.getDefault(), "%tF%n 当前有备忘", d));
                    calendarInfoTX.setEnabled(true);
                } else {
                    calendarInfoTX.setText("当前无备忘");
                    calendarInfoTX.setEnabled(false);
                }
                break;
        }
    }
}
