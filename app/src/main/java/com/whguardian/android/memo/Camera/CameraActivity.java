package com.whguardian.android.memo.Camera;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.whguardian.android.memo.R;

/**
 * Created by whguardian_control on 16/04/22.
 * Comment:相机管理
 */
public class CameraActivity extends AppCompatActivity {

    private Fragment fragment;
    private FragmentManager fm = getFragmentManager();
    private FragmentTransaction transaction = fm.beginTransaction();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Toolbar toolbar = (Toolbar)findViewById(R.id.menu_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
//            fragment = new CameraFragment();
            fragment = CameraFragment.newInstance(getIntent().getExtras());

            transaction.add(R.id.fragmentContainer, fragment)
                    .commit();

        }
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
