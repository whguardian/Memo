package com.whguardian.android.memo.Sketch;

import android.app.Fragment;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whguardian.android.memo.DBManager;
import com.whguardian.android.memo.MemoData.Sketch;
import com.whguardian.android.memo.MemoData.SketchLab;
import com.whguardian.android.memo.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/20.
 */
public class SketchFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SKetchFragment";
    public static final String EXTRA_ID =
            "com.whguardian.android.memo.Sketch.SketchFragment.id";
    private static final String HAS_UUID =
            "com.whguardian.android.memo..Sketch.SketchFragment.hasUUID";

    //视图
    private EditText contentEditText;
    private TextView mCalendarTextView;
    private TextView addMarkTextView;
    private CardView addMarkCardView;
    private Button saveButton;
    //添加Mark视图中的按钮
    private LinearLayout markLinearLayout;
    private LinearLayout markGroup;
    private EditText markEditText;
    private Button markButton;
    //单个Mark标签
    private CardView markCardView;
    private TextView markTextView;

    //数据保存
    private SketchLab mSketchLab;
    private Sketch mSketch;
    private Date mDate;
    private GregorianCalendar gregorianCalendar;

    private String contentBuffer = "";
    private String markStringBuffer = "";
    private ArrayList<String> markListbuffer = new ArrayList<>();

    /*
    *
    * 数据库
    * */
    private DBManager db;

    public static SketchFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_ID, uuid);
        args.putSerializable(HAS_UUID, true);

        SketchFragment sketchFragment = new SketchFragment();
        sketchFragment.setArguments(args);

        return sketchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mSketchLab = SketchLab.get(getActivity());
        gregorianCalendar = new GregorianCalendar();
        mDate = gregorianCalendar.getTime();

        //标记添加页rootView
        markLinearLayout = (LinearLayout) getActivity().getLayoutInflater()
                .inflate(R.layout.sketch_mark, null);

        //获取是由已创建速写打开 or 新创建速写
        if (getArguments().getSerializable(EXTRA_ID) == null) {
            //创建一个新Sketch
            mSketch = new Sketch();
            mSketch.setSketchDate(mDate);
            mSketch.setMark(new ArrayList<String>());
        } else {
            //找到UUID对应的Sketch
            UUID uuid = (UUID) getArguments().getSerializable(EXTRA_ID);
            Log.i(TAG,"UUID is " + uuid.toString());
            mSketch = mSketchLab.getSketch(uuid);
            contentBuffer = mSketch.getContent();
            for (String s:mSketch.getMark()) {
                markListbuffer.add(s);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saveInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sketch, parent, false);

        //速写内容
        contentEditText = (EditText) v.findViewById(R.id.sketch_content);
        contentEditText.addTextChangedListener(new SketchTextWatcher(contentEditText));

        mCalendarTextView = (TextView) v.findViewById(R.id.calendar_sketch);
        mCalendarTextView.setText(String.format("%tF", mDate));

        //标记页
        addMarkCardView = (CardView) v.findViewById(R.id.cardView_mark_add);
        addMarkTextView = (TextView) v.findViewById(R.id.textView_mark_add);
        addMarkTextView.setOnClickListener(this);

        saveButton = (Button) v.findViewById(R.id.button_sketch_save);
        saveButton.setOnClickListener(this);

        //标记添加页各个视图
        markGroup = (LinearLayout) markLinearLayout.findViewById(R.id.cardView_mark);

        markEditText = (EditText) markLinearLayout.findViewById(R.id.editText_mark);
        markEditText.addTextChangedListener(new SketchTextWatcher(markEditText));

        markButton = (Button) markLinearLayout.findViewById(R.id.button_mark);
        markButton.setOnClickListener(this);

        //存在速写，将内容初始化为速写内容
        if (getArguments().getSerializable(EXTRA_ID) != null) {
            contentEditText.setText(mSketch.getContent());
            replaceMarkView();
            for (String s : mSketch.getMark()) {
                addMark(s);
                markListbuffer.add(s);
            }
        }

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        SketchLab.get(getActivity()).saveSketchs();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_mark_add:
                replaceMarkView();
                break;
            case R.id.button_mark:
//                添加标签同时添加CardView视图
                Toast.makeText(getActivity(), markStringBuffer, Toast.LENGTH_LONG).show();
                String st = addMark(markStringBuffer);
                markListbuffer.add(st);
                markEditText.setText(null);
                break;
            case R.id.button_sketch_save:
                //按下保存保存速写内容
                //如果contentBuffer为空，不能保存
                if (contentBuffer.trim().isEmpty()) {
                    Log.i(TAG, "没有内容输入，请输入");
                    Toast.makeText(getActivity(), "请输入内容后再进行保存", Toast.LENGTH_SHORT)
                    .show();
                    break;
                }

                //实例化数据库
                db = new DBManager(getActivity());
                if (getArguments().getSerializable(EXTRA_ID) != null) {
                    mSketch.setContent(contentBuffer);
                    mSketch.getMark().clear();
                    for (String s:markListbuffer) {
                        mSketch.getMark().add(s);
                    }
                    //数据库
                    db.updateSketch(mSketch);
                } else {
                    mSketchLab.getSkeths().add(mSketch);
                    mSketch.setContent(contentBuffer);
                    for (String s:markListbuffer) {
                        mSketch.getMark().add(s);
                    }
                    //数据库
                    db.insertSketch(mSketch);
                    Log.i(TAG, "uuid is " + mSketch.getId().toString());
                }

                Log.i(TAG, "uuid is " + mSketch.getId().toString());
                //关闭数据库
                db.closeDatabase();
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    class SketchTextWatcher implements TextWatcher {
        EditText view;

        public SketchTextWatcher(EditText v) {
            view = v;
        }

        @Override
        public void afterTextChanged(Editable s) {
            //在输入完成后

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //再输入前
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            switch (view.getId()) {
                case R.id.sketch_content:
                    contentBuffer = s.toString();
                    Log.i(TAG, "Sketch当前内容是：" + contentBuffer);
                    break;
                case R.id.editText_mark:
                    markStringBuffer = s.toString();
                    Log.i(TAG, "Sketch当前内容是：" + markStringBuffer);
                    break;
                default:
                    break;
            }
        }
    }

    private void replaceMarkView() {
        addMarkCardView.removeView(addMarkTextView);
        addMarkCardView.addView(markLinearLayout);
    }

    private String addMark(String markText) {
        markCardView = new CardView(getActivity());
        FrameLayout.LayoutParams fLP = new FrameLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        fLP.width = FrameLayout.LayoutParams.MATCH_PARENT;
        fLP.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        markCardView.setLayoutParams(fLP);

        markTextView = new TextView(getActivity());
        markTextView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout
                .LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.width = FrameLayout.LayoutParams.MATCH_PARENT;
        lp.height = FrameLayout.LayoutParams.MATCH_PARENT;
        lp.setMargins(20, 20, 20, 20);
        markTextView.setLayoutParams(lp);
        //设置一个标签的内容为传入的信息
        markTextView.setText(markText);
//
//                markGroup.addView(markTextView);
        markGroup.addView(markCardView);
        markCardView.addView(markTextView);

        return markText;
    }
}



























