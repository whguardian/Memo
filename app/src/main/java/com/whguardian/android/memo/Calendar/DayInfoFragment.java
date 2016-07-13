package com.whguardian.android.memo.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.whguardian.android.memo.DBManager;
import com.whguardian.android.memo.MemoData.MemoInfo;
import com.whguardian.android.memo.MemoData.MemoLab;
import com.whguardian.android.memo.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/20.
 */
public class DayInfoFragment extends DialogFragment{
    private static final String EXTRA_DATE = "extraDate";

    private Date date;
    private ArrayList<MemoInfo> memoInfos;

    private AlertDialog dialog;

    private ListView listView;

    public static DayInfoFragment newInstance(String Date) {
        Bundle args = new Bundle();
        args.putString(EXTRA_DATE, Date);

        DayInfoFragment fragment = new DayInfoFragment();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_list, null);

        listView = (ListView)v.findViewById(android.R.id.list);

        SimpleDateFormat fmt =new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            date = fmt.parse(getArguments().getString(EXTRA_DATE));
        } catch (Exception e) {

        }

        DBManager db = new DBManager(getActivity());
        ArrayList<UUID> uuids = db.findMemoSeletedDay(date);
        memoInfos = new ArrayList<>();
        for (UUID u: uuids) {
            memoInfos.add(MemoLab.get(getActivity()).getMemoInfo(u));
        }

        MemoAdapter memoAdapter = new MemoAdapter(memoInfos);
        listView.setAdapter(memoAdapter);

        dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getArguments().getString(EXTRA_DATE))
                .setView(v)
                .create();

        return dialog;
    }

    private class MemoAdapter extends ArrayAdapter<MemoInfo> {

        public MemoAdapter(ArrayList<MemoInfo> memoInfos) {
            super(getActivity(), 0, memoInfos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_memo, null);
            }

            MemoInfo m = getItem(position);

            TextView titleTextView =
                    (TextView)convertView.findViewById(R.id.title_memo_list_item);
            titleTextView.setText(m.getTitle());
            TextView contentTextView =
                    (TextView)convertView.findViewById(R.id.content_memo_list_item);
            contentTextView.setText(m.getContent());
            TextView timeTextView =
                    (TextView)convertView.findViewById(R.id.time_memo_list_item);
            timeTextView.setText(String.format("%tD %tR - %tD %tR",
                    m.getBeginTime(), m.getBeginTime(), m.getEndTime(), m.getEndTime()));

            return convertView;
        }
    }
}
