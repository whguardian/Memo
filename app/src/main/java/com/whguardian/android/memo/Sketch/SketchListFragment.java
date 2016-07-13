package com.whguardian.android.memo.Sketch;

import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.whguardian.android.memo.DBManager;
import com.whguardian.android.memo.MemoData.Sketch;
import com.whguardian.android.memo.MemoData.SketchIntentJSONSerializer;
import com.whguardian.android.memo.MemoData.SketchLab;
import com.whguardian.android.memo.R;

import java.util.ArrayList;
import java.util.UUID;


/**
 * Created by whguardian_control on 16/04/20.
 */
public class SketchListFragment extends ListFragment implements View.OnClickListener {
    private static final String TAG = "SketchListFragment";

    private static final String DIALOG_DATE = "info";

    private ListView listView;

    private EditText markSearchEditText;
    private Button markSearchButton;

    private SketchAdapter sketchAdapter;

    private SketchLab sketchLab;
    private ArrayList<Sketch> sketches;
    private Sketch sketch;

    /*
    *
    *
    *
    * 数据库
    *
    *
    *
    * */
    private DBManager db;
    //数据库查找获得UUID
    private ArrayList<UUID> uuidArray = new ArrayList<>();
    private ArrayList<Sketch> searchSketchs;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        getActivity().setTitle("Sketch List");

        //获得Sketch数据
        sketchLab = SketchLab.get(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saveInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_sketch, parent, false);

        listView = (ListView)v.findViewById(android.R.id.list);
        registerForContextMenu(listView);

        markSearchEditText = (EditText)v.findViewById(R.id.editText_sketch_search);
        markSearchEditText.addTextChangedListener(new SketchSearchTextWatcher());
        markSearchButton = (Button)v.findViewById(R.id.button_sketch_search);
        markSearchButton.setOnClickListener(this);

//        sketchListView = (ListView)v.findViewById(android.R.id.list);

        sketches = sketchLab.getSkeths();
        sketchAdapter = new SketchAdapter(sketches);
        setListAdapter(sketchAdapter);

        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info) {
        getActivity().getMenuInflater().inflate(R.menu.memo_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();

        int position = info.position;
        ArrayAdapter adapter = (ArrayAdapter) getListAdapter();
        Sketch sketch = (Sketch) adapter.getItem(position);

        switch (menuItem.getItemId()) {
            case R.id.menu_item_delete_memo:
                UUID uuid = sketch.getId();
                //数据库
                db.deleteSketch(uuid);

                SketchLab.get(getActivity()).getSkeths().remove(sketch);
                sketches.remove(sketch);
                sketchAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(menuItem);
    }

    @Override
    public void onResume() {
        super.onResume();
        db = new DBManager(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        SketchLab.get(getActivity()).saveSketchs();
        db.closeDatabase();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        sketch = ((SketchAdapter)getListAdapter()).getItem(position);

        Intent i = new Intent(getActivity(), SketchActivity.class);
        i.putExtra(SketchActivity.HAS_UUID, true);
        i.putExtra(SketchFragment.EXTRA_ID, sketch.getId());
        startActivityForResult(i, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sketch_search:
                ArrayList<String> aL = new ArrayList<>();
                if (uuidArray != null) {
                    Log.i(TAG, "找到标签对应的数据");
                    for (UUID uuid:uuidArray) {
                        Log.i(TAG, "uuid is " + uuid.toString());
                        aL.add(uuid.toString());
                    }
                    FragmentManager fm = getActivity().getFragmentManager();
                    SketchInfoFragment dialog = SketchInfoFragment.newInstance(aL);
                    dialog.show(fm, DIALOG_DATE);
                }
        }
    }

    class SketchSearchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            uuidArray = db.searchSketch(s.toString());
//            if (uuidArray != null && !uuidArray.isEmpty()) {
//                searchSketchs = new ArrayList<>();
//                for (UUID uuid:uuidArray) {
//                    searchSketchs.add(sketchLab.getSketch(uuid));
//                }
//            } else {
//
//            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class SketchAdapter extends ArrayAdapter<Sketch> {

        public SketchAdapter(ArrayList<Sketch> sketches) {
            super(getActivity(), 0, sketches);
        }

        @Override
        public View getView(int position, View convertView ,ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_sketch, null);
            }

            Sketch s = getItem(position);

            TextView calendarTextView =
                    (TextView)convertView.findViewById(R.id.item_sketch_calendar);
            calendarTextView.setText(String.format("%tF", s.getSketchDate()));
            TextView contentTextView =
                    (TextView)convertView.findViewById(R.id.item_sketch_content);
            contentTextView.setText(s.getContent());

            return convertView;
        }
    }
}
