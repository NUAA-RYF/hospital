package com.thundersoft.hospital.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.thundersoft.hospital.R;
import com.thundersoft.hospital.activity.EditIllnessActivity;
import com.thundersoft.hospital.adapter.InfoAdapter;
import com.thundersoft.hospital.model.IllnessInfo;
import com.xuexiang.xui.widget.toast.XToast;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoFragment extends Fragment {

    @BindView(R.id.info_recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.info_fab_add)
    FloatingActionButton mFabAdd;
    @BindView(R.id.info_fab_menu)
    FloatingActionsMenu mFabMenu;
    @BindView(R.id.info_fab_refresh)
    FloatingActionButton mFabRefresh;

    private View rootView;

    private Context mContext;

    private Activity mActivity;

    private InfoAdapter mInfoAdapter;

    private DataChangeReceiver mReceiver;

    private List<IllnessInfo> mIllnessInfoList;

    public static InfoFragment newInstance() {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_info, container, false);
        }
        ButterKnife.bind(this, rootView);

        //初始化数据
        initData();
        //初始化控件
        initControls();
        //查询病情信息
        queryIllnessInfo();
        //注册广播
        registerDataChangeBroadCast();
        return rootView;
    }

    /**
     * 注册广播
     * 数据变化监听
     */
    private void registerDataChangeBroadCast(){
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.thundersoft.hospital.broadcast.DATA_CHANGE");
        mReceiver = new DataChangeReceiver();
        mActivity.registerReceiver(mReceiver,mIntentFilter);
    }

    /**
     * 注销广播
     * 数据变化监听
     */
    private void unRegisterDataChangeBraodCast(){
        if (mReceiver != null){
            mActivity.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }
    /**
     * 查询病情信息
     */
    private void queryIllnessInfo() {
        //从网络获取数据
        mIllnessInfoList.clear();
        mIllnessInfoList.addAll(DataSupport.findAll(IllnessInfo.class));
        if (mIllnessInfoList.size() > 0) {
            mInfoAdapter.notifyDataSetChanged();
        } else {
            //暂无数据显示
            Toast info = XToast.info(mContext, "暂无病情信息!");
            info.setGravity(Gravity.CENTER, 0, 0);
            info.show();
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mIllnessInfoList = new ArrayList<>();
    }

    /**
     * 初始化控件
     * 添加和刷新按钮
     * 适配器及其布局初始化
     */
    private void initControls() {
        //添加按钮
        mFabAdd.setOnClickListener(view -> {
            Intent addInfo = new Intent(mContext, EditIllnessActivity.class);
            addInfo.putExtra("handle",EditIllnessActivity.ADD);
            mFabMenu.collapse();
            startActivity(addInfo);

        });
        //刷新按钮
        mFabRefresh.setOnClickListener(view -> {
            queryIllnessInfo();
            Toast success = XToast.success(mContext, "刷新完毕!");
            success.setGravity(Gravity.CENTER, 0, 0);
            success.show();
            mFabMenu.collapse();
        });

        //适配器及其布局初始化
        mInfoAdapter = new InfoAdapter(mIllnessInfoList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setAdapter(mInfoAdapter);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    /**
     * 数据改变监听
     */
    class DataChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            queryIllnessInfo();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销广播
        unRegisterDataChangeBraodCast();
    }
}
