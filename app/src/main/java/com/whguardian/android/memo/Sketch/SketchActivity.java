package com.whguardian.android.memo.Sketch;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.whguardian.android.memo.R;
import com.whguardian.android.memo.Setting.SettingFragment;

import java.util.UUID;

/**
 * Created by whguardian_control on 16/04/04.
 */
public class SketchActivity extends AppCompatActivity {
    public static final String HAS_UUID =
            "com.whguardian.android.memo.Sketch.SketchActivity.hasUUID";

    private UUID uuid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_nontablayout);

        setTitle(R.string.sub_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.menu_setting);
        setSupportActionBar(toolbar);
        /*
        * 设置回退按钮
        * 在AndroidManifest中，设置Activity下的
        * meta-data中android.support.PARENT_ACTIVITY
        *
        * */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            if (!getIntent().getBooleanExtra(HAS_UUID, false)) {
                fragment = SketchFragment.newInstance(null);
            } else {
                uuid = (UUID) getIntent().getSerializableExtra(SketchFragment.EXTRA_ID);
                fragment = SketchFragment.newInstance(uuid);
            }
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        /*
        * 设置Activity默认不能选中，无法更改
        * */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        if (PreferenceManager.getDefaultSharedPreferences(this)
//                .getBoolean(SettingFragment.ORIENTATION_IS_RUN, true)) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nontablayout, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                /*
                * 调用工具类设置导航中的回退按钮的实现
                *
                * */
                if (NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
