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
 * Created by whguardian_control on 16/04/08.
 */
public class MemoIntentJSONSerializer {

    private Context mContext;
    private String mFilename;

    public MemoIntentJSONSerializer (Context context ,String filename) {
        mContext = context;
        mFilename = filename;
    }

    public ArrayList<MemoInfo> loadMemos() throws IOException, JSONException {
        ArrayList<MemoInfo> memoInfos = new ArrayList<>();
        BufferedReader reader = null;
        try {
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            JSONArray array = (JSONArray)new JSONTokener(jsonString.toString())
                    .nextValue();

            for (int i = 0; i < array.length(); i++) {
                memoInfos.add(new MemoInfo(array.getJSONObject(i)));
            }
        }catch (FileNotFoundException e) {

        } finally {
            if (reader == null)
                reader.close();
        }
        return memoInfos;
    }

    public void saveMemos(ArrayList<MemoInfo> memoInfos) throws JSONException, IOException {
        JSONArray array = new JSONArray();
        for (MemoInfo m: memoInfos)
            array.put(m.toJSON());

        Writer writer = null;
        try {
            OutputStream out = mContext
                    .openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());

        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
