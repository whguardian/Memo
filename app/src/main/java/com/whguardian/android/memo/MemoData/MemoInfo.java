package com.whguardian.android.memo.MemoData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/03.
 * Project: Memo
 * Comment: Data about Memo
 */
public class MemoInfo {
    private UUID mId;

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_CONTENT = "content";
    private static final String JSON_BEGIN_TIME = "beginTime";
    private static final String JSON_END_TIME = "endTime";
    private static final String JSON_PHONE_NUMBER = "phoneNumber";

    private String mTitle;
    private String mContent;
    private Date mBeginTime;
    private Date mEndTime;
    private boolean mIsAllDay;
    private String mPhoneNumber;

    public MemoInfo() {
        mId = UUID.randomUUID();
    }

    public UUID getId() {
        return mId;

    }

    public MemoInfo(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        mTitle = json.getString(JSON_TITLE);
        mContent = json.getString(JSON_CONTENT);
        mBeginTime = new Date(json.getLong(JSON_BEGIN_TIME));
        mEndTime = new Date(json.getLong(JSON_END_TIME));
        if (json.has(JSON_PHONE_NUMBER)) {
            mPhoneNumber = json.getString(JSON_PHONE_NUMBER);
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_ID, mId.toString());
        jsonObject.put(JSON_TITLE, mTitle);
        jsonObject.put(JSON_CONTENT, mContent);
        jsonObject.put(JSON_BEGIN_TIME, mBeginTime.getTime());
        jsonObject.put(JSON_END_TIME, mEndTime.getTime());
        jsonObject.put(JSON_PHONE_NUMBER, mPhoneNumber);

        return jsonObject;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String Content) {
        this.mContent = Content;
    }

    public Date getBeginTime() {
        return mBeginTime;
    }

    public void setBeginTime(Date BeginTime) {
        this.mBeginTime = BeginTime;
    }

    public Date getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Date EndTime) {
        this.mEndTime = EndTime;
    }

    public boolean getIsAllDay() {
        return mIsAllDay;
    }

    public void setmIsAllDay(boolean isAllDay) {
        this.mIsAllDay = isAllDay;
    }


    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }
}
