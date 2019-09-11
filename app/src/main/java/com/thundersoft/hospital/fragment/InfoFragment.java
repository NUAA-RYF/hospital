package com.thundersoft.hospital.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.thundersoft.hospital.model.Disease;
import com.thundersoft.hospital.model.User;
import com.thundersoft.hospital.util.HttpUtil;
import com.thundersoft.hospital.util.LoadJsonUtil;
import com.xuexiang.xui.widget.toast.XToast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.thundersoft.hospital.util.HttpUrl.DISEASE_QUERY_USERNAME;
import static com.thundersoft.hospital.util.HttpUrl.HOSPITAL;

public class InfoFragment extends Fragment {

    private static final int QUERY_SUCCESS = 1;
    private static final int QUERY_FAILED = 2;
    private static final int CONNECTED_FAILED = 3;

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

    private List<Disease> mDiseaseList;

    private User mUser;

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
        queryDiseaseInfo();
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
    private void queryDiseaseInfo() {
        //从网络获取数据
        mDiseaseList.clear();
        queryDiseaseInfoFromServer();
    }

    /**
     * 初始化数据
     * 用户信息
     * 疾病列表
     */
    private void initData() {
        Bundle userBundle = Objects.requireNonNull(getActivity()).getIntent().getBundleExtra("user");
        mUser = (User) Objects.requireNonNull(userBundle).get("user");
        mDiseaseList = new ArrayList<>();
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
            Bundle userBundle = new Bundle();
            userBundle.putParcelable("user", mUser);
            addInfo.putExtra("user", userBundle);
            mFabMenu.collapse();
            startActivity(addInfo);
        });
        //刷新按钮
        mFabRefresh.setOnClickListener(view -> {
            queryDiseaseInfo();
            Toast success = XToast.success(mContext, "刷新完毕!");
            success.setGravity(Gravity.CENTER, 0, 0);
            success.show();
            mFabMenu.collapse();
        });

        //适配器及其布局初始化
        mInfoAdapter = new InfoAdapter(mDiseaseList);
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
            queryDiseaseInfo();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销广播
        unRegisterDataChangeBraodCast();
    }

    /**
     * 从服务器查询疾病信息
     */
    private void queryDiseaseInfoFromServer(){
        //查询疾病信息
        String address = HOSPITAL + DISEASE_QUERY_USERNAME + "?username=" + mUser.getUserName();
        //发出网络请求
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = new Message();
                message.what = CONNECTED_FAILED;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = Objects.requireNonNull(response.body()).string();
                List<Disease> currentList = LoadJsonUtil.getDiseaseList(responseText);
                Message message = new Message();
                if (currentList.size() <= 0){
                    message.what = QUERY_FAILED;
                }else {
                    mDiseaseList.addAll(currentList);
                    message.what = QUERY_SUCCESS;
                }
                mHandler.sendMessage(message);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case QUERY_SUCCESS:
                    mInfoAdapter.notifyDataSetChanged();
                    break;
                case QUERY_FAILED:
                    //暂无数据显示
                    Toast info = XToast.info(mContext, "暂无病情信息!");
                    info.setGravity(Gravity.CENTER, 0, 0);
                    info.show();
                    break;
                case CONNECTED_FAILED:
                    //连接失败,请检查网络
                    Toast warning = XToast.warning(mContext, "连接失败,请检查网络!");
                    warning.setGravity(Gravity.CENTER, 0, 0);
                    warning.show();
                    break;
            }
        }
    };
}
