package com.thundersoft.hospital.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.thundersoft.hospital.R;
import com.thundersoft.hospital.adapter.PagerAdapter;
import com.thundersoft.hospital.fragment.HomeFragment;
import com.thundersoft.hospital.fragment.PersonalFragment;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.tabbar.TabSegment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initControls();
    }

    /**
     * 初始化数据
     * 碎片初始化
     * 底部导航栏Tab初始化
     */
    private void initData() {
        List<Fragment> mFragmentList = new ArrayList<>();
        mFragmentList.add(HomeFragment.newInstance());
        mFragmentList.add(PersonalFragment.newInstance());
        mFragmentList.add(PersonalFragment.newInstance());
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
                "我", true
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

        //抽屉式侧方导航栏
        mNavigation.setCheckedItem(R.id.nav_child);
        mNavigation.setCheckedItem(R.id.nav_weight);
        mNavigation.setCheckedItem(R.id.nav_personal);

        mNavigation.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_personal:
                    break;
                case R.id.nav_weight:
                    Intent bmi = new Intent(this,BMIActivity.class);
                    startActivity(bmi);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.nav_child:
                    Intent child = new Intent(this,ChildActivity.class);
                    startActivity(child);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    break;
                default:
                    break;
            }
            return true;
        });

        //适配器和底部导航栏
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(0);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
}
