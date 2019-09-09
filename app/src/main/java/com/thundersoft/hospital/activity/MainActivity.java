package com.thundersoft.hospital.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.thundersoft.hospital.R;
import com.thundersoft.hospital.adapter.PagerAdapter;
import com.thundersoft.hospital.fragment.HomeFragment;
import com.thundersoft.hospital.fragment.InfoFragment;
import com.thundersoft.hospital.fragment.FriendFragment;
import com.thundersoft.hospital.model.User;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.tabbar.TabSegment;
import com.xuexiang.xui.widget.toast.XToast;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.main_viewPager)
    ViewPager mViewPager;
    @BindView(R.id.main_tabSegment)
    TabSegment mTabSegment;
    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.main_drawerLayout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.main_navigation)
    NavigationView mNavigation;

    private PagerAdapter mAdapter;
    private TabSegment.Tab mTab_Home;
    private TabSegment.Tab mTab_Message;
    private TabSegment.Tab mTab_Me;
    private long mExitTimes;

    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //获取用户信息
        getUserInfoFromIntent();
        //初始化数据
        initData();
        //初始化控件
        initControls();
    }

    /**
     * 初始化数据
     * 碎片初始化
     * 底部导航栏Tab初始化
     */
    private void initData() {
        ActivityController.addActivity(this);

        mExitTimes = System.currentTimeMillis();

        List<Fragment> mFragmentList = new ArrayList<>();
        mFragmentList.add(HomeFragment.newInstance());
        mFragmentList.add(InfoFragment.newInstance());
        mFragmentList.add(FriendFragment.newInstance());
        mAdapter = new PagerAdapter(getSupportFragmentManager(), mFragmentList);

        mTab_Home = new TabSegment.Tab(
                ContextCompat.getDrawable(this, R.mipmap.icon_home),
                null,
                "首页", true
        );

        mTab_Message = new TabSegment.Tab(
                ContextCompat.getDrawable(this, R.mipmap.icon_message),
                null,
                "消息", true
        );

        mTab_Me = new TabSegment.Tab(
                ContextCompat.getDrawable(this, R.mipmap.icon_personal),
                null,
                "好友", true
        );
    }

    /**
     * 初始化控件
     * 标题栏和状态栏
     * 适配器属性设置
     * 底部导航栏初始化
     */
    private void initControls() {
        //标题栏
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
            actionBar.setHomeAsUpIndicator(R.mipmap.icon_menu);
        }
        //状态栏沉浸式
        StatusBarUtils.translucent(this);

        //导航栏头部用户信息
        ConstraintLayout constraintLayout = (ConstraintLayout) mNavigation.inflateHeaderView(R.layout.nav_header);
        TextView userName = constraintLayout.findViewById(R.id.nav_HeadName);
        TextView userPhone = constraintLayout.findViewById(R.id.nav_HeadPhone);
        if (mCurrentUser != null){
            userName.setText(mCurrentUser.getUserName());
            userPhone.setText(mCurrentUser.getPhone());
        }else {
            userName.setText("未登录");
            userPhone.setText("手机号码");
        }

        //抽屉式侧方导航栏
        mNavigation.setCheckedItem(R.id.nav_personal);

        mNavigation.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_personal:
                    //个人中心
                    Intent personalIntent = setUserInfoIntoIntent(mCurrentUser);
                    personalIntent.setClass(this,PersonalActivity.class);
                    startActivity(personalIntent);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.nav_weight:
                    //健康检测
                    Intent BMIIntent = new Intent(this,BMIActivity.class);
                    startActivity(BMIIntent);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.nav_child:
                    //生男生女
                    Intent childIntent = new Intent(this,ChildActivity.class);
                    startActivity(childIntent);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.nav_logout:
                    AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
                    mDialog.setTitle("退出登录")
                            .setMessage("是否确认退出登录?")
                            .setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss())
                            .setPositiveButton("退出",(dialogInterface, i) -> {
                                //删除用户信息
                                DataSupport.deleteAll(User.class);

                                //关闭对话框和侧方导航栏
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                dialogInterface.dismiss();

                                //关闭所有活动,并进入登录页面
                                ActivityController.finishAll();
                                Intent loginIntent = new Intent(this,AccountLoginActivity.class);
                                startActivity(loginIntent);
                            })
                            .show();
                default:
                    break;
            }
            return true;
        });

        //适配器和底部导航栏
        mViewPager.setAdapter(mAdapter);
        mTabSegment.addTab(mTab_Home);
        mTabSegment.addTab(mTab_Message);
        mTabSegment.addTab(mTab_Me);
        mTabSegment.setupWithViewPager(mViewPager, false, true);
    }

    /**
     * 菜单栏选择
     * 侧方导航栏
     * 地图显示
     * @param item 菜单栏项目
     * @return 返回true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.menu_map:
                Intent mIntent = new Intent(this, MapActivity.class);
                startActivity(mIntent);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 菜单栏显示
     * @param menu 菜单
     * @return 成功返回true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /**
     * 时差不超过2秒
     * 点击两次返回键退出程序
     */
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mExitTimes > 2000){
            mExitTimes = System.currentTimeMillis();
            Toast normal = XToast.normal(this,"再次点击返回键,退出程序!");
            normal.setGravity(Gravity.CENTER,0,0);
            normal.show();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }

    /**
     * 从意图中获取用户信息
     */
    private void getUserInfoFromIntent(){
        Bundle userBundle = getIntent().getBundleExtra("user");
        if (userBundle != null) {
            mCurrentUser = (User) userBundle.get("user");
        }else {
            Log.e(TAG, "getUserInfoFromIntent: 用户信息为空!");
        }
    }

    /**
     * 将用户信息捆绑意图
     * @param user 用户
     * @return 返回意图
     */
    private Intent setUserInfoIntoIntent(User user){
        Intent mIntent = new Intent();
        Bundle userBundle = new Bundle();
        userBundle.putParcelable("user",user);
        mIntent.putExtra("user",userBundle);
        return mIntent;
    }
}
