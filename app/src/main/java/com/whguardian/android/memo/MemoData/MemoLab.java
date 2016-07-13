package com.whguardian.android.memo.MemoData;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/05.
 */
public class MemoLab {
    private static final String TAG = "MemoLab";
    private static final String FILENAME = "memos.json";


    private ArrayList<MemoInfo> mMemoInfos;
    private MemoIntentJSONSerializer mSerializer;

    private static MemoLab sMemoLab;
    private Context mAppContext;

    private MemoLab(Context appContext) {
        mAppContext = appContext;
        mMemoInfos = new ArrayList<>();
        mSerializer = new MemoIntentJSONSerializer(mAppContext, FILENAME);

        try {
            mMemoInfos = mSerializer.loadMemos();
        } catch (Exception e) {
            mMemoInfos = new ArrayList<>();
            Log.e(TAG, "Error loading memoInfos: ", e);
        }
    }

    public static MemoLab get(Context c) {
        if (sMemoLab == null) {
            sMemoLab = new MemoLab(c.getApplicationContext());
        }
        return sMemoLab;
    }

    //获取备忘数组
    public ArrayList<MemoInfo> getmMemoInfos() {
        return mMemoInfos;
    }


    //获取匹配的备忘信息
    public MemoInfo getMemoInfo(UUID id) {
        for (MemoInfo m: mMemoInfos) {
            if (m.getId().equals(id)) {
                return  m;
            }
        }
        return null;
    }

    public void deleteMemoInfo(MemoInfo m) {
        mMemoInfos.remove(m);
    }

    public boolean saveMemos() {
        try {
            mSerializer.saveMemos(mMemoInfos);
            Log.d(TAG, "memoInfos saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving memeoInfos: ", e);
            return false;
        }
    }
}
