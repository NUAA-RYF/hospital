package com.thundersoft.hospital.fragment;

import android.content.Context;
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

    private View rootView;

    private Context mContext;

    private InfoAdapter mInfoAdapter;

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_info, container, false);
        }
        ButterKnife.bind(this,rootView);

        //初始化数据
        initData();
        //初始化控件
        initControls();
        //查询病情信息
        queryIllnessInfo();
        return rootView;
    }


    /**
     * 查询病情信息
     */
    private void queryIllnessInfo(){
        //从网络获取数据
        mIllnessInfoList.addAll(DataSupport.findAll(IllnessInfo.class));
        if (mIllnessInfoList.size() > 0){
            mInfoAdapter.notifyDataSetChanged();
        }else {
            //暂无数据显示
            Toast info = XToast.info(mContext,"暂无病情信息!");
            info.setGravity(Gravity.CENTER,0,0);
            info.show();
        }
    }
    /**
     * 初始化数据
     */
    private void initData(){
        mIllnessInfoList = new ArrayList<>();
    }

    /**
     * 初始化控件
     */
    private void initControls(){

        //适配器及其布局初始化
        mInfoAdapter = new InfoAdapter(mIllnessInfoList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setAdapter(mInfoAdapter);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }
}
