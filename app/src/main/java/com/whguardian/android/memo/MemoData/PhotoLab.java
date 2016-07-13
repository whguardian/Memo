package com.whguardian.android.memo.MemoData;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/25.
 */
public class PhotoLab {
    private static final String TAG = "photoLab";
    private static final String FILENAME = "photoInfo.json";

    private static PhotoLab photoLab;
    private Context mAppcontext;

    private ArrayList<PhotoInfo> photoInfos;
    private PhotoInfoIntentJSONSerializer serializer;

    private PhotoLab(Context context) {
        mAppcontext = context;
        serializer = new PhotoInfoIntentJSONSerializer(mAppcontext, FILENAME);
//        photoInfos = new ArrayList<>();
        try {
            photoInfos = serializer.loadPhotoInfo();
        } catch (Exception e) {
            photoInfos = new ArrayList<>();
            Log.e(TAG , "读取失败");
        }
    }

    public static PhotoLab get(Context c) {
        if (photoLab == null) {
            photoLab = new PhotoLab(c.getApplicationContext());
        }
        return photoLab;
    }

    public ArrayList<PhotoInfo> getPhotoInfos() {
        return photoInfos;
    }

    public PhotoInfo getPhotoInfo(UUID uuid) {
        for (PhotoInfo p:photoInfos) {
            if (p.getId().equals(uuid)) {
                return p;
            }
        }
        return null;
    }

    public void removePhotoInfo(PhotoInfo photoInfo) {
        photoInfos.remove(photoInfo);
    }

    public boolean savePhotoInfo() {
        try {
            serializer.savePhotoInfo(photoInfos);
            Log.d(TAG , "保存成功");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "保存失败");
            return false;
        }
    }
}
