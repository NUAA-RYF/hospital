package com.thundersoft.hospital.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.thundersoft.hospital.R;
import com.thundersoft.hospital.adapter.PagerAdapter;
import com.thundersoft.hospital.fragment.GenderMonthFragment;
import com.thundersoft.hospital.fragment.MonthGenderFragment;
import com.thundersoft.hospital.fragment.TableFragment;
import com.thundersoft.hospital.model.Table;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.tabbar.TabSegment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChildActivity extends AppCompatActivity {

    @BindView(R.id.child_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.child_tabSegment)
    TabSegment mTabSegment;
    @BindView(R.id.child_viewPager)
    ViewPager mViewPager;

    private PagerAdapter mPagerAdapter;

    private TabSegment.Tab mTab_MonthGender;

    private TabSegment.Tab mTab_GenderMonth;

    private TabSegment.Tab mTab_Table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        ButterKnife.bind(this);

        //初始化数据
        initData();
        //初始化状态栏
        initControls();
    }


    /**
     * 初始化数据
     * Fragment
     * 适配器
     * Tab控件
     */
    private void initData(){
        ActivityController.addActivity(this);

        //初始化Fragment及其PagerAdapter
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(MonthGenderFragment.newInstance());
        fragmentList.add(GenderMonthFragment.newInstance());
        fragmentList.add(TableFragment.newInstance());
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragmentList);

        //初始化Tab控件
        mTab_MonthGender = new TabSegment.Tab("按月份");
        mTab_GenderMonth = new TabSegment.Tab("按性别");
        mTab_Table = new TabSegment.Tab("对照表");
    }

    /**
     * 初始化控件
     * 状态栏
     * 标题栏
     * 导航栏
     */
    private void initControls(){
        //沉浸式状态栏和标题栏
        StatusBarUtils.translucent(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("生男生女");
        }


        //适配器和底部导航栏
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mTabSegment.setHasIndicator(true);
        mTabSegment.addTab(mTab_MonthGender);
        mTabSegment.addTab(mTab_GenderMonth);
        mTabSegment.addTab(mTab_Table);
        mTabSegment.setupWithViewPager(mViewPager,false,true);
    }

    /**
     * 菜单栏选项
     * @param item 选项
     * @return 返回true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }
}
