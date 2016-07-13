package com.whguardian.android.memo.MemoData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/24.
 */
public class SpecialDay {

    private UUID mId;

    private static final String JSON_ID = "id";
    private static final String JSON_THEME = "theme";
    private static final String JSON_CONTENT = "content";
    private static final String JSON_DATE = "date";

    private String theme;
    private String content;
    private Date date;

    public SpecialDay() {
       mId = UUID.randomUUID();
    }

    public SpecialDay(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        if (json.has(JSON_THEME)) {
            theme = json.getString(JSON_THEME);
        }
        if (json.has(JSON_CONTENT)) {
            content = json.getString(JSON_CONTENT);
        }
        date = new Date(json.getLong(JSON_DATE));
    }

    public UUID getId() {
        return mId;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        json.put(JSON_THEME, theme);
        json.put(JSON_CONTENT, content);
        json.put(JSON_DATE, date.getTime());
        return json;
    }
}
