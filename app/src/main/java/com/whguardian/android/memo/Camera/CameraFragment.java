package com.whguardian.android.memo.Camera;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.whguardian.android.memo.MemoData.PhotoInfo;
import com.whguardian.android.memo.MemoData.PhotoLab;
import com.whguardian.android.memo.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by whguardian_control on 16/04/22.
 */
public class CameraFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "CameraFragment";
    private static final String EXTRA_CAMERA =
            "com.whguardian.android.memo.Camera.CameraFragment.camera";

    private EditText editText;
    private ImageView imageView;
    private Button savedButton;

    private Bitmap photoBit;


    private StringBuilder detailString = new StringBuilder();
    private Date date;
    private String getPhotoName;

    private PhotoInfo photoInfo;

    /*
    * 文件写入操作
    *
    * */
    private OutputStream out;
    private String photoName;

    public static CameraFragment newInstance(Bundle extras) {
        Bundle args = new Bundle();
        args.putBundle(EXTRA_CAMERA, extras);

        CameraFragment fragment = new CameraFragment();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera,container, false);

        date = new Date();

        editText = (EditText)v.findViewById(R.id.editText_picture_description);
        editText.addTextChangedListener(new PictureTextWatcher(editText));

        imageView = (ImageView)v.findViewById(R.id.image_camera);
        imageView.setImageBitmap(getPhoto());

        savedButton = (Button)v.findViewById(R.id.button_picture_saved);
        savedButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_picture_saved:
                if (detailString.length() == 0) {
                    break;
                }
                savePhoto(createFile(getPath()), getPhoto());
                /*
                 *
                 * 添加照片信息
                 *
                 * */
                photoInfo = new PhotoInfo();
                photoInfo.setDetail(detailString.toString());
                photoInfo.setDate(date);
//                photoInfo.setDetail(getPhotoName);
                photoInfo.setPhotoName(getPhotoName);
                PhotoLab.get(getActivity()).getPhotoInfos().add(photoInfo);
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    private class PictureTextWatcher implements TextWatcher {
        EditText editText;

        public PictureTextWatcher(EditText e) {
            editText = e;
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
            if (detailString.length() != 0) {
                detailString.delete(0, detailString.length()-1);
            }
            detailString = new StringBuilder(s);
        }
    }

    /*
    *
    * 从Bundle中获取拍摄使用的图片
    *
    * */
    private Bitmap getPhoto() {
        photoBit = (Bitmap) getArguments().getBundle(EXTRA_CAMERA).get("data");
        Log.i(TAG , "Path是 " + getActivity().getApplicationContext().getFilesDir().getPath());
        Log.i(TAG, "包名是 " + getActivity().getPackageName());
        return photoBit;
    }

    /*
    *
    * 获取存储图片的文件路径
    * 判断是否存在路径，如果不存在文件路径就创建一个
    * 只有在点击saveButton的情况下调用路径，将图片保存到文件中
    * 返回文件
    *
    * */
    private File getPath() {
        String path =
                getActivity().getApplicationContext().getFilesDir().getPath() + "/pictureTemp/";
        File destDir = new File(path);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return destDir;
    }

    /*
    *
    * 创建文件名在指定目录下
    *
    * */
    private File createFile(File path) {
        SimpleDateFormat formatter =  new SimpleDateFormat("yyyy-MM-dd HH:mmZ", Locale.getDefault());
        Date date = Calendar.getInstance().getTime();
        photoName = "IMG_" + formatter.format(date) + ".jpg";
        File photoFile = new File(path, photoName);
        /*
        * 获取照片的路径名与全称
        *
        * */
        getPhotoName = getActivity().getApplicationContext().getFilesDir().getPath()
                + "/pictureTemp/" + photoName;

        return photoFile;
    }

    /*
    *
    * 通过输出流将图片photo写入文件saveFile中
    *
    * */
    private void savePhoto(File savedFile, Bitmap photo) {
        try {
            /*
            * getPath(...)获取路径，createFile(...)路径下需要创建的文件
            * 通过输出流输出
            * */
            out = new BufferedOutputStream(new FileOutputStream(savedFile));
            photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "出现错误，无法保存图片");
        } finally {
            Log.i(TAG, "图片已经被保存到文件中");
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        PhotoLab.get(getActivity()).savePhotoInfo();
    }
}
