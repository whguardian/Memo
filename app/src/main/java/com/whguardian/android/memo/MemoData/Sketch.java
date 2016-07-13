package com.whguardian.android.memo.MemoData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/20.
 */
public class Sketch {

    private static final String JSON_ID = "id";
    private static final String JSON_CONTENT = "content";
    private static final String JSON_DATE = "date";
    private static final String JSON_MARK = "mark";

    private UUID mId;

    private String mContent;
    private Date mSketchDate;
    private ArrayList<String> mMark;

    public Sketch() {
        mId = UUID.randomUUID();
    }

    public Sketch(JSONObject jsonObject) throws JSONException {
        mId = UUID.fromString(jsonObject.getString(JSON_ID));
        mContent = jsonObject.getString(JSON_CONTENT);
        mSketchDate = new Date(jsonObject.getLong(JSON_DATE));
        mMark = new ArrayList<>();
        if (jsonObject.has(JSON_MARK)) {
            JSONArray jsonArray = jsonObject.getJSONArray(JSON_MARK);
            for (int i = 0; i <jsonArray.length(); i++) {
                mMark.add(jsonArray.getString(i));
            }
        }
    }

    public UUID getId() {
        return mId;
    }


    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public ArrayList<String> getMark() {
        return mMark;
    }

    public void setMark(ArrayList<String> mMark) {
        this.mMark = mMark;
    }

    public Date getSketchDate() {
        return mSketchDate;
    }

    public void setSketchDate(Date mSketchDate) {
        this.mSketchDate = mSketchDate;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_ID, mId.toString());
        jsonObject.put(JSON_CONTENT, mContent);
        jsonObject.put(JSON_DATE, mSketchDate.getTime());

        JSONArray jsonArray = new JSONArray();
        for (String s: mMark) {
            jsonArray.put(s);
        }

        jsonObject.put(JSON_MARK, jsonArray);

        return jsonObject;
    }

}
