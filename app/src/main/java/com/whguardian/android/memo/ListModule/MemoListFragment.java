package com.whguardian.android.memo.ListModule;

import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
import android.net.http.SslCertificate;
import android.os.Bundle;
import android.util.Log;
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
import com.whguardian.android.memo.MemoInfo.MemoInfoFragment;
import com.whguardian.android.memo.MemoRemindService;
import com.whguardian.android.memo.R;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by whguardian_control on 16/03/14.
 * Comment: all memo data here
 */
public class MemoListFragment extends ListFragment {
    private static final String TAG = "MemoListFragment";
    private static final String MEMOINFO = "memoInfo";

    private DBManager db;
    private ArrayList<MemoInfo> mMemoInfos;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Memo List");

        mMemoInfos = new ArrayList<>();

        db = new DBManager(getActivity());
        ArrayList<UUID> uuidArrayList = db.findMemo();
        db.closeDatabase();
        for (UUID uuid:uuidArrayList) {
            mMemoInfos.add(MemoLab.get(getActivity()).getMemoInfo(uuid));
        }

        //获得备忘数组
//        mMemoInfos = MemoLab.get(getActivity()).getmMemoInfos();

//        ArrayAdapter<MemoInfo> adapter =
//                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mMemoInfos);

        MemoAdapter adapter = new MemoAdapter(mMemoInfos);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = layoutInflater.inflate(R.layout.fragment_list, parent, false);

        mListView = (ListView)v.findViewById(android.R.id.list);
        //注册ContextMenu菜单
        registerForContextMenu(mListView);

//        ArrayAdapter adapter = (ArrayAdapter) getListAdapter();
//        adapter.notifyDataSetChanged();

        return v;
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
//        MemoInfo mMemoInfo = (MemoInfo) (getListAdapter().getItem(position));
        MemoInfo mMemoInfo = ((MemoAdapter)getListAdapter()).getItem(position);

        FragmentManager fm = getFragmentManager();
        MemoInfoFragment infoFragment = MemoInfoFragment.newInstance(mMemoInfo.getId());
        infoFragment.show(fm, MEMOINFO);

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
                MemoLab.get(getActivity()).deleteMemoInfo(memoInfo);
                mMemoInfos.remove(memoInfo);
                adapter.notifyDataSetChanged();
                //
                Intent i = new Intent(getActivity(), MemoRemindService.class);
                getActivity().startService(i);
                //
                return true;
        }
        return super.onContextItemSelected(menuItem);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        MemoLab mMemoLab = MemoLab.get(getActivity());
        mMemoLab.saveMemos();
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
