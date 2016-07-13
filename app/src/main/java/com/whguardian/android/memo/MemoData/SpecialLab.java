package com.whguardian.android.memo.MemoData;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/24.
 */
public class SpecialLab {
    private static final String TAG = "SpecialLab";
    private static final String FILENAME = "specials.json";

    private ArrayList<SpecialDay> specialDays;
    private SpecialIntentJSONSerializer serializer;

    private static SpecialLab specialLab;
    private Context mAppContext;

    private SpecialLab(Context appContext) {
        mAppContext = appContext;
//        specialDays = new ArrayList<>();
        serializer = new SpecialIntentJSONSerializer(mAppContext, FILENAME);
        try {
            specialDays = serializer.LoadSpecial();
        } catch (Exception e) {
            specialDays = new ArrayList<>();
            Log.e(TAG, "载入失败");
        }
    }

    public static SpecialLab get(Context c) {
        if (specialLab == null) {
            specialLab = new SpecialLab(c.getApplicationContext());
        }
        return specialLab;
    }

    public ArrayList<SpecialDay> getSpecialDays() {
        return specialDays;
    }

    public SpecialDay getSpecial(UUID uuid) {
        for (SpecialDay s:specialDays) {
            if (s.getId().equals(uuid)) {
                return s;
            }
        }
        return null;
    }

    public void deleteSpecialDay(SpecialDay s) {
        specialDays.remove(s);
    }

    public boolean saveSpecials() {
        try {
            serializer.saveSpecial(specialDays);
            Log.d(TAG, "特殊日期被保存到文件");
            return true;
        } catch (Exception  e) {
            Log.e(TAG, "文件保存失败");
            return false;
        }
    }
}
