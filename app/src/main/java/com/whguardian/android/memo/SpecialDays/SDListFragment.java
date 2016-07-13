package com.whguardian.android.memo.SpecialDays;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
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

import com.whguardian.android.memo.MemoData.SpecialDay;
import com.whguardian.android.memo.MemoData.SpecialLab;
import com.whguardian.android.memo.R;

import java.util.ArrayList;

/**
 * Created by whguardian_control on 16/04/24.
 */
public class SDListFragment extends ListFragment {
    private static final String TAG = "SDListFragment";

    private static final int REQUEST_SPECIAL = 0;
    private static final String DIALOG_SPECIAL_DAYS ="specialDays";

    private ListView listView;
    private TextView themeTextView;
    private TextView calendarTextView;

    private SpecialAdapter adapter;
    private ArrayList<SpecialDay> arrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arrayList = SpecialLab.get(getActivity()).getSpecialDays();
        adapter = new SpecialAdapter(arrayList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saveInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, parent, false);
        getActivity().setTitle("特殊");

        listView = (ListView)v.findViewById(android.R.id.list);
        registerForContextMenu(listView);
        setListAdapter(adapter);

        return v;
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
        SpecialDay specialDay = ((SpecialAdapter)getListAdapter()).getItem(position);

        FragmentManager fm = getFragmentManager();
        SDCreateFragment sdCreateDialog = SDCreateFragment.newInstance(specialDay.getId());
        sdCreateDialog.setTargetFragment(SDListFragment.this, REQUEST_SPECIAL);
        sdCreateDialog.show(fm, DIALOG_SPECIAL_DAYS);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info) {
        getActivity().getMenuInflater().inflate(R.menu.memo_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;
        ArrayAdapter adapter = (ArrayAdapter) getListAdapter();
        SpecialDay specialDay = (SpecialDay) adapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.menu_item_delete_memo:
                SpecialLab.get(getActivity()).getSpecialDays().remove(specialDay);
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    public class SpecialAdapter extends ArrayAdapter<SpecialDay> {

        public SpecialAdapter(ArrayList<SpecialDay> specialDays) {
            super(getActivity(), 0, specialDays);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_special, null);
            }

            /*
            * 获取数据源
            *
            * */
            SpecialDay specialDay = getItem(position);

            /*
            *
            * View设置
            * */
            themeTextView = (TextView)convertView.findViewById(R.id.textView_special_theme);
            themeTextView.setText(specialDay.getTheme());
            calendarTextView = (TextView)convertView.findViewById(R.id.textView_special_calendar);
            calendarTextView.setText(String.format("%tF", specialDay.getDate()));

            return convertView;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        adapter.notifyDataSetChanged();
    }
}
