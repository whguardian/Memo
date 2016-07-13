package com.whguardian.android.memo.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.whguardian.android.memo.Camera.CameraActivity;
import com.whguardian.android.memo.Camera.CameraListFragment;
import com.whguardian.android.memo.GuestModule.RegisterFragment;
import com.whguardian.android.memo.ListModule.ContactsListFragment;
import com.whguardian.android.memo.ListModule.MemoListFragment;
import com.whguardian.android.memo.ListModule.OverdueListFragment;
import com.whguardian.android.memo.MemoInfo.MemoActivity;
import com.whguardian.android.memo.R;
import com.whguardian.android.memo.Setting.SettingActivity;
import com.whguardian.android.memo.Setting.SettingFragment;
import com.whguardian.android.memo.Sketch.SketchActivity;
import com.whguardian.android.memo.Sketch.SketchListFragment;
import com.whguardian.android.memo.SpecialDays.SDCreateFragment;
import com.whguardian.android.memo.SpecialDays.SDListFragment;

/**
 * Created by whguardian_control on 16/04/04.
 * Comment:app主框架，连接基本所有Activity， Fragment
 */
public class CalendarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String TAG = "CalendarActivity";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_SPECIAL_DAYS = "specialDays";

    private static final int REQUEST_CAMERA_CAPUTRE = 7;
    /*
    * 设置是否显示addButton
    * */
    private boolean menuTag = false;

    private Button loginButton;

    /*
    *
    * PopupWindows组件
    *
    * */
    private PopupWindow pop; //主
    private View popRootView;
    private Button popMemoButton;
    private Button popCameraButton;
    private Button popSketchButton;
    private Button popSpecialButton;
    private Button popCloseButton; //关闭pop

    /*
    * 管理Fragment
    * */
    private FragmentManager fm = getFragmentManager();
    private FragmentTransaction fragmentTransaction = fm.beginTransaction();
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_tablayout);
        Log.i(TAG, "CalendarActivity is creating");

        /*
        * 工具栏（用以替换ActionBar,继承View,
        * ViewGroup，需要被实例化后使用）
        * */
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Drawer菜单设置
        setDrawer(toolbar);

        fragment = fm.findFragmentById(R.id.fragmentContainer);

        /*
        * 创建Fragment
        * if（）用以在旋转后保证Fragment不重复
        * */
        if (fragment == null) {
            fragment = new CalendarFragment();
            fragmentTransaction
                    .add(R.id.fragmentContainer, fragment)
                    .addToBackStack(fragment.getClass().getName())
                    .commit();
        }

        /*
        *
        *
        * PopupWindow设置，获取Pop的View，实例化Pop需要View，
        * Layout_width, layout_height，注册所有哦按钮的监听
        * Activity实现View.OnClickListener
        *
        * */
        popRootView = getLayoutInflater().inflate(R.layout.popwindow_create, null);
        pop = new PopupWindow(popRootView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());

        popMemoButton = (Button)popRootView.findViewById(R.id.button_memo_create);
        popMemoButton.setOnClickListener(this);
        popCameraButton = (Button)popRootView.findViewById(R.id.button_picture_create);
        popCameraButton.setOnClickListener(this);
        popSketchButton = (Button)popRootView.findViewById(R.id.button_sketch_create);
        popSketchButton.setOnClickListener(this);
        popSpecialButton = (Button) popRootView.findViewById(R.id.button_special_create);
        popSpecialButton.setOnClickListener(this);
        popCloseButton = (Button)popRootView.findViewById(R.id.popupwindow_close);
        popCloseButton.setOnClickListener(this);
    }

    /*
    *
    * 从share preferences中获取是否旋转屏幕
    * 通过获得系统资源可以设置，具体设置在SettingFragment中
    *
    * */
    @Override
    public void onResume() {
        super.onResume();
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(SettingFragment.ORIENTATION_IS_RUN, true)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        Log.i(TAG, "CalendarActivity is onResume()");
    }

    //处理打开Drawer时onBackPressed（）
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.Drawer_layout);
//        FragmentManager fm = getFragmentManager();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(pop.isShowing()) {
            pop.dismiss();
        }
//        else if (fm.getBackStackEntryCount() != 0) {
//            fm.popBackStackImmediate(CalendarFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tablayout, menu);

        //切换Fragment时使用
        menu.findItem(R.id.menu_memo_add).setVisible(!menuTag);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_memo_add:
                if (pop.isShowing()) {
//                    pop.dismiss();
                } else {
                    /*
                    * 打开Pop，pop的锚点为Activity底部
                    * */
                    View rootView = LayoutInflater.from(CalendarActivity.this)
                            .inflate(R.layout.activity_fragment_tablayout, null);
                    pop.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
                }
//                Intent i = new Intent(this, MemoActivity.class);
//                i.putExtra(MemoActivity.HAS_UUID, false);
//                startActivityForResult(i, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.Drawer_layout);
        switch (item.getItemId()) {
            case R.id.nav_menu_item_calendar:
                Fragment calendarFragment = new CalendarFragment();
                fragmentTransaction
                        .replace(R.id.fragmentContainer, calendarFragment)
                        .commit();
                drawer.closeDrawer(GravityCompat.START);
                menuTag = false;
                invalidateOptionsMenu();
                return true;
            case R.id.nav_menu_item_expired:
                Fragment overdueListFragment = new OverdueListFragment();
                fragmentTransaction
                        .replace(R.id.fragmentContainer, overdueListFragment)
                        .commit();
                drawer.closeDrawer(GravityCompat.START);
                menuTag = true;
                invalidateOptionsMenu();
                return true;
//            case R.id.nav_menu_item_contacts:
//                Fragment contactsListFragment= new ContactsListFragment();
//                fragmentTransaction
//                        .replace(R.id.fragmentContainer, contactsListFragment)
//                        .commit();
//                drawer.closeDrawer(GravityCompat.START);
//                menuTag = true;
//                invalidateOptionsMenu();
//                return true;
            case R.id.nav_menu_item_list:
                Fragment memoListFragment = new MemoListFragment();
                fragmentTransaction
                        .replace(R.id.fragmentContainer, memoListFragment)
                        .commit();
                drawer.closeDrawer(GravityCompat.START);
                menuTag = true;
                invalidateOptionsMenu();
                return true;
            case R.id.nav_menu_item_photos:
                Fragment CameraGridFragment = new CameraListFragment();
                fragmentTransaction
                        .replace(R.id.fragmentContainer, CameraGridFragment)
                        .commit();
                drawer.closeDrawer(GravityCompat.START);
                menuTag = true;
                invalidateOptionsMenu();
                return true;
            case R.id.nav_menu_item_sketch:
                Fragment sketchListFragment = new SketchListFragment();
                fragmentTransaction
                        .replace(R.id.fragmentContainer, sketchListFragment)
                        .commit();
                drawer.closeDrawer(GravityCompat.START);
                menuTag = true;
                invalidateOptionsMenu();
                return true;
            case R.id.nav_menu_item_specialDay:
                Fragment sdListFragment = new SDListFragment();
                fragmentTransaction
                        .replace(R.id.fragmentContainer, sdListFragment)
                        .commit();
                drawer.closeDrawer(GravityCompat.START);
                menuTag = true;
                invalidateOptionsMenu();
                return true;
            case R.id.nav_menu_item_setting:
                Intent i = new Intent(this, SettingActivity.class);
                startActivityForResult(i, 1);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.button_memo_create:
                pop.dismiss();
                i = new Intent(this, MemoActivity.class);
                i.putExtra(MemoActivity.HAS_UUID, false);
                startActivityForResult(i, 0);
                break;
            case R.id.button_picture_create:
                pop.dismiss();
//                i = new Intent(this, CameraActivity.class);
//                startActivityForResult(i, 0);

                /*
                * 摄像机请求
                *
                * */
                i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, REQUEST_CAMERA_CAPUTRE);
                }
                break;
            case R.id.button_sketch_create:
                pop.dismiss();
                i = new Intent(this, SketchActivity.class);
                i.putExtra(SketchActivity.HAS_UUID, false);
                startActivityForResult(i, 0);
                break;
            case R.id.button_special_create:
                Log.i(TAG, "get Button");
                pop.dismiss();
                fm = getFragmentManager();
                SDCreateFragment sdCreateDialog = SDCreateFragment.newInstance(null);
                sdCreateDialog.show(fm, DIALOG_SPECIAL_DAYS);
                break;
            case R.id.popupwindow_close:
                if (pop.isShowing()) {
                    pop.dismiss();
                }
                break;
        }
        pop.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_CAMERA_CAPUTRE:
                Bundle extras = data.getExtras();

                Intent cameraIntent = new Intent(this, CameraActivity.class);
                cameraIntent.putExtras(extras);
                startActivityForResult(cameraIntent, 0);
                Log.i(TAG,"照片已经被拍摄");
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("deprecation")
    private void setDrawer(Toolbar toolbar) {
        //Drawer，主菜单
        final DrawerLayout drawer = (DrawerLayout)findViewById(R.id.Drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView nav = (NavigationView)findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);

        //NavigationView HeaderLayout获取
        loginButton = (Button)nav.getHeaderView(0)
                .findViewById(R.id.nav_menu_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (true) {
//                    Fragment loginFragment = new LoginFragment();
                    Fragment registerFragment = new RegisterFragment();
                    fm.beginTransaction()
                            .replace(R.id.fragmentContainer, registerFragment)
                            .addToBackStack(null)
                            .commit();
                    drawer.closeDrawer(GravityCompat.START);
                    invalidateOptionsMenu();
                    menuTag = true;
                } else {

                }
            }
        });
    }
}
