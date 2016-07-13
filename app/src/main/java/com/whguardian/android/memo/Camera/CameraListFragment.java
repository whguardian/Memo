package com.whguardian.android.memo.Camera;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.IntentService;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.whguardian.android.memo.MemoData.MemoInfo;
import com.whguardian.android.memo.MemoData.PhotoInfo;
import com.whguardian.android.memo.MemoData.PhotoLab;
import com.whguardian.android.memo.MemoInfo.MemoInfoFragment;
import com.whguardian.android.memo.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by whguardian_control on 16/04/25.
 */
public class CameraListFragment extends ListFragment {
    private static final String PHOTOINFO = "info";

    private ArrayList<PhotoInfo> arrayList;
    private PhotoAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        arrayList = PhotoLab.get(getActivity()).getPhotoInfos();
        adapter = new PhotoAdapter(arrayList);

        setListAdapter(adapter);

        return v;
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
//        MemoInfo mMemoInfo = (MemoInfo) (getListAdapter().getItem(position));
        PhotoInfo photoInfo = ((PhotoAdapter)getListAdapter()).getItem(position);

        FragmentManager fm = getFragmentManager();
        PhotoInfoFragment infoFragment = PhotoInfoFragment.newInstance(photoInfo.getId());
        infoFragment.show(fm, PHOTOINFO);

        Intent i = new Intent(getActivity(), PhotoInfoActivity.class);
        i.putExtra(PhotoInfoActivity.EXTRA_UUID, photoInfo.getId().toString());
        startActivityForResult(i, 0);
    }

    class PhotoAdapter extends ArrayAdapter<PhotoInfo> {

        public PhotoAdapter(ArrayList<PhotoInfo> photoInfos) {
            super(getActivity(), 0, photoInfos);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity()
                        .getLayoutInflater().inflate(R.layout.list_item_photo, null);
            }

            PhotoInfo photoInfo = getItem(position);

            TextView detailTextView = (TextView) convertView
                    .findViewById(R.id.textView_detail_photo);
            detailTextView.setText(photoInfo.getDetail());

            TextView dateTextView = (TextView)convertView
                    .findViewById(R.id.textView_date_photo);
            dateTextView.setText(String.format(Locale.getDefault(),"%tF", photoInfo.getDate()));

            return convertView;
        }
    }
}
