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
 * Created by whguardian_control on 16/04/23.
 */
public class SketchIntentJSONSerializer {

    private Context mContext;
    private String filename;

    public SketchIntentJSONSerializer(Context c, String s) {
        mContext = c;
        filename = s;
    }

    public ArrayList<Sketch> LoadSketchs() throws IOException, JSONException {
        ArrayList<Sketch> sketches = new ArrayList<>();
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
                sketches.add(new Sketch(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {

        } finally {
            if (reader != null)
                reader.close();
        }
        return sketches;
    }

    public void saveSketchs(ArrayList<Sketch> sketches)
            throws JSONException, IOException {
        JSONArray array = new JSONArray();
        for (Sketch s: sketches) {
            array.put(s.toJSON());
        }

        Writer writer = null;
        try {
            OutputStream out = mContext
                    .openFileOutput(filename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}




















