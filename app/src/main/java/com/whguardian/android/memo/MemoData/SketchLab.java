package com.whguardian.android.memo.MemoData;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/20.
 */
public class SketchLab {
    private static final String TAG = "sketchLab";
    private static final String FILENAME = "sketch.json";

    private SketchIntentJSONSerializer mSerializer;

    //内部单例
    private static SketchLab sSketchLab;
    private Context mAppContext;

    //sketch内容数组
    private ArrayList<Sketch> mSKetchs;

    private SketchLab(Context appContext) {
        mAppContext = appContext;
        mSKetchs = new ArrayList<>();
        mSerializer = new SketchIntentJSONSerializer(mAppContext, FILENAME);

        try {
            mSKetchs = mSerializer.LoadSketchs();
        } catch (Exception e) {
            mSKetchs = new ArrayList<>();
            Log.e(TAG, "Error loading crimes: ", e);
        }
    }

    public static SketchLab get(Context c) {
        if (sSketchLab == null) {
            sSketchLab = new SketchLab(c.getApplicationContext());
        }
        return sSketchLab;
    }

    public ArrayList<Sketch> getSkeths() {
        return mSKetchs;
    }

    public Sketch getSketch(UUID uuid) {
        for (Sketch s:mSKetchs) {
            if (s.getId().equals(uuid)) {
                return s;
            }
        }
        return null;
    }

    public void deleteSketch(Sketch sketch) {
        mSKetchs.remove(sketch);
    }

    public boolean saveSketchs() {
        try {
            mSerializer.saveSketchs(mSKetchs);
            Log.d(TAG,  "sketchs saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving sketchs: ", e);
            return false;
        }
    }
}
