package com.whguardian.android.memo.MemoData;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by whguardian_control on 16/05/05.
 */
public class SpecialIntentJSONSerializer {

    private Context mContext;
    private String filename;

    public SpecialIntentJSONSerializer(Context c, String s) {
        mContext = c;
        filename = s;
    }

    public void saveSpecial(ArrayList<SpecialDay> specialDays)
            throws JSONException, IOException{
        JSONArray array = new JSONArray();
        for (SpecialDay s: specialDays) {
            array.put(s.toJSON());
        }

        Writer writer = null;
        try {
            OutputStream out = mContext
                    .openFileOutput(filename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public ArrayList<SpecialDay> LoadSpecial() throws IOException, JSONException {
        ArrayList<SpecialDay> specialDays = new ArrayList<>();
        BufferedReader reader = null;
        try {
            InputStream in = mContext.openFileInput(filename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString())
                    .nextValue();
            for (int i = 0; i < array.length(); i++) {
                specialDays.add(new SpecialDay(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {

        } finally {
            if (reader != null)
                reader.close();
        }
        return specialDays;
    }
}
