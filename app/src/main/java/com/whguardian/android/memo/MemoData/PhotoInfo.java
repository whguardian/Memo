package com.whguardian.android.memo.MemoData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/25.
 */
public class PhotoInfo {

    private UUID mId;

    private static final String JSON_ID = "id";
    private static final String JSON_DETAIL = "detail";
    private static final String JSON_PHOTO_NAME ="photoName";
    private static final String JSON_DATE = "date";

    private String detail;
    private String photoName;
    private Date date;

    public PhotoInfo() {
        mId = UUID.randomUUID();
    }

    public PhotoInfo(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        if (json.has(JSON_DETAIL)) {
            detail = json.getString(JSON_DETAIL);
        }
        photoName = json.getString(JSON_PHOTO_NAME);
        date =new Date(json.getLong(JSON_DATE));
    }

    public UUID getId() {
        return mId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_DETAIL, detail);
        json.put(JSON_PHOTO_NAME, photoName);
        json.put(JSON_DATE, date.getTime());
        return json;
    }
}
