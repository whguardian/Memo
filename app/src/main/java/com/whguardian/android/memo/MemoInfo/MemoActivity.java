package com.whguardian.android.memo.MemoInfo;

import android.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.whguardian.android.memo.R;

import java.util.UUID;

/**
 * Created by whguardian_control on 16/03/15.
 * comment: 备忘信息活动
 */
public class MemoActivity extends AppCompatActivity {
    public static final String HAS_UUID = "hasUUID";

    private UUID uuid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_nontablayout);

        Toolbar toolbar = (Toolbar)findViewById(R.id.menu_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);


        //
        //
        //正在修改
        //
        //
        //
        //
        if (fragment == null) {
            if (!getIntent().getBooleanExtra(HAS_UUID, false)) {
//                fragment = MemoFragment.newInstance(false);
                fragment = MemoFragment.newInstance(null);
            } else {
                uuid = (UUID)getIntent().getSerializableExtra(MemoFragment.EXTRA_MEMO_ID);
                fragment = MemoFragment.newInstance(uuid);
            }
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}
