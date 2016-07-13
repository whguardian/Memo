package com.whguardian.android.memo.Camera;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.whguardian.android.memo.MemoData.PhotoInfo;
import com.whguardian.android.memo.MemoData.PhotoLab;
import com.whguardian.android.memo.R;

import java.util.UUID;

/**
 * Created by whguardian_control on 16/05/05.
 */
public class PhotoFragment extends Fragment {
    private static final String EXTRA_UUID =
            "com.whguardian.android.memo.Camera.PhotoFragment.uuid";

    private UUID uuid;

    private TextView textView;
    private ImageView imageView;

    private PhotoInfo photoInfo;
    private Bitmap bitmap;

    public static PhotoFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putString(EXTRA_UUID, uuid.toString());

        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uuid = UUID.fromString(getArguments().getString(EXTRA_UUID));
        photoInfo = PhotoLab.get(getActivity()).getPhotoInfo(uuid);
        bitmap = BitmapFactory.decodeFile(photoInfo.getPhotoName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_info_photo, parent, false);

        textView = (TextView)v.findViewById(R.id.textView_photo_info);
        textView.setText(photoInfo.getDetail());
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundColor(Color.WHITE);

        imageView = (ImageView)v.findViewById(R.id.image_photo_info);
        imageView.setImageBitmap(bitmap);

        return v;
    }
}
