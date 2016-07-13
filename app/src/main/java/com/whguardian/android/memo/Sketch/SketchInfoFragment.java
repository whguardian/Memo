package com.whguardian.android.memo.Sketch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.whguardian.android.memo.MemoData.Sketch;
import com.whguardian.android.memo.MemoData.SketchLab;
import com.whguardian.android.memo.R;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/23.
 */
public class SketchInfoFragment extends DialogFragment implements ListView.OnItemClickListener{
    private static final String TAG = "SketchInfoFragment";
    private static final String EXTRA_ID =
            "com.whguardian.android.memo.Sketch.SketchInfoFragment.ids";


    private ListView listView;
    private ArrayList<String> uuidArray;
    private ArrayList<Sketch> sketches;
    private SketchAdapter sketchAdapter;

    public static SketchInfoFragment newInstance(ArrayList<String> arrayList) {
        Bundle args = new Bundle();
        args.putStringArrayList(EXTRA_ID, arrayList);

        SketchInfoFragment fragment = new SketchInfoFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uuidArray = getArguments().getStringArrayList(EXTRA_ID);
        sketches = new ArrayList<>();
        for (String s: uuidArray) {
            sketches.add(SketchLab.get(getActivity()).getSketch(UUID.fromString(s)));
            Log.i(TAG, "uuid is " + s);
        }
        sketchAdapter = new SketchAdapter(sketches);
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        View v = getActivity()
                .getLayoutInflater().inflate(R.layout.dialog_info_sketch, null);
        listView = (ListView)v.findViewById(R.id.listView_sketch_search);
        listView.setOnItemClickListener(this);
        listView.setAdapter(sketchAdapter);

        return new AlertDialog.Builder(getActivity())
                .setTitle("查找结果")
                .setView(listView)
                .create();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        switch (parent.getId()) {
            case R.id.listView_sketch_search:
                Sketch sketch = ((SketchAdapter)parent.getAdapter()).getItem(position);

                Intent i = new Intent(getActivity(), SketchActivity.class);
                i.putExtra(SketchActivity.HAS_UUID, true);
                i.putExtra(SketchFragment.EXTRA_ID, sketch.getId());
                startActivityForResult(i, 0);
                dismiss();
                break;
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
