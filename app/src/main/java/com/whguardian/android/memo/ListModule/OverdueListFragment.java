package com.whguardian.android.memo.ListModule;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.whguardian.android.memo.DBManager;
import com.whguardian.android.memo.MemoData.MemoInfo;
import com.whguardian.android.memo.MemoData.MemoLab;
import com.whguardian.android.memo.R;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/16.
 */
public class OverdueListFragment extends ListFragment {

    private DBManager db;
    private ArrayList<MemoInfo> memoInfos;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle("Overdue List");

        memoInfos = new ArrayList<>();
//        memoInfos = MemoLab.get(getActivity()).getmMemoInfos();

        db = new DBManager(getActivity());

        ArrayList<UUID> uuidArrayList = db.findOverdueMemo();
        for (UUID uuid: uuidArrayList) {
            memoInfos.add(MemoLab.get(getActivity()).getMemoInfo(uuid));
        }
        db.closeDatabase();

        MemoAdapter adapter = new MemoAdapter(memoInfos);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = layoutInflater.inflate(R.layout.fragment_list, parent, false);
        listView = (ListView)v.findViewById(android.R.id.list);

        registerForContextMenu(listView);

        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.memo_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        int position = info.position;
        ArrayAdapter adapter = (ArrayAdapter) getListAdapter();
        MemoInfo memoInfo = (MemoInfo) adapter.getItem(position);

        switch (menuItem.getItemId()) {
            case R.id.menu_item_delete_memo:
                UUID uuid = memoInfo.getId();
                DBManager db = new DBManager(getActivity());
                db.delete(uuid);
                db.closeDatabase();
                //MemoLab中数据删除
                MemoLab.get(getActivity()).deleteMemoInfo(memoInfo);
                //删除当前ListView中数据
                memoInfos.remove(memoInfo);
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(menuItem);
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
            timeTextView.setText(String.format("%tD%n %tR - %tD%n %tR",
                    m.getBeginTime(), m.getBeginTime(), m.getEndTime(), m.getEndTime()));

            return convertView;
        }
    }
}
