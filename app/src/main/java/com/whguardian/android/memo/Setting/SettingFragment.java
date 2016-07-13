package com.whguardian.android.memo.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.whguardian.android.memo.DBManager;
import com.whguardian.android.memo.MemoData.MemoLab;
import com.whguardian.android.memo.MemoRemindService;
import com.whguardian.android.memo.R;

/**
 * Created by whguardian_control on 16/04/04.
 */
public class SettingFragment extends Fragment {
    private static final String TAG = "SettingFragment";
    public static final String ORIENTATION_IS_RUN = "orientationIsRun";

    private Switch orientationSwitch;
    private Button cleanButton;

    public static Fragment newInstance() {
        Bundle args = new Bundle();

        Fragment fragment = new SettingFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstance) {
        View v = inflater.inflate(R.layout.fragment_setting, parent,false);

        orientationSwitch = (Switch)v.findViewById(R.id.switch_orientation);
        orientationSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean(SettingFragment.ORIENTATION_IS_RUN, true));
        orientationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked) {
                    PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .edit()
                            .putBoolean(SettingFragment.ORIENTATION_IS_RUN, true)
                    .apply();
                } else {
                    PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .edit()
                            .putBoolean(SettingFragment.ORIENTATION_IS_RUN, false)
                            .apply();
                }
                getActivity().finish();
                Intent i = new Intent(getContext(), SettingActivity.class);
                startActivity(i);
            }
        });
        cleanButton = (Button)v.findViewById(R.id.memo_clean);
        cleanButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //删除单例中的内容
                MemoLab.get(getActivity()).getmMemoInfos().clear();

                //删除所有数据库内容
                DBManager db = new DBManager(getActivity());
                db.deleteAll();
                boolean hasDataInDB = db.hasDataInDB();
                db.closeDatabase();
                Log.i(TAG,"所有数据被清理:" + hasDataInDB);

                Intent i = new Intent(getActivity(), MemoRemindService.class);
                getActivity().startService(i);

                Toast.makeText(getActivity(), "所有备忘已经删除", Toast.LENGTH_LONG)
                        .show();
            }
        });

        return v;
    }
}
