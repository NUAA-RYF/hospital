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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.thundersoft.hospital.R;
import com.thundersoft.hospital.adapter.FirstAidAdapter;
import com.thundersoft.hospital.model.FirstAid;
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
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.thundersoft.hospital.util.HttpUrl.FIRST_AID_QUERY_USERNAME;
import static com.thundersoft.hospital.util.HttpUrl.HOSPITAL;

public class FirstAidFragment extends Fragment {

    private static final int CONNECTED_FAILED = 1;

    private static final int QUERY_SUCCESS = 2;

    @BindView(R.id.first_recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.aid_fab_refresh)
    FloatingActionButton mFabRefresh;
    @BindView(R.id.aid_fab_menu)
    FloatingActionsMenu mFabMenu;

    private View rootView;

    private List<FirstAid> mFirstAidList;

    private FirstAidAdapter mFirstAidAdapter;

    private Context mContext;

    private User mUser;

    private FirstAidChangeReceiver mReceiver;

    private Activity mActivity;

    public static FirstAidFragment newInstance() {
        FirstAidFragment fragment = new FirstAidFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mContext = getContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_first_aid, container, false);
        }
        ButterKnife.bind(this, rootView);

        //注册
        registerFirstAidChangeBroadCast();

        //初始化数据
        initData();

        //初始化控件
        initControls();

        //查询FA
        queryFirstAid();
        return rootView;
    }

    /**
     * 初始化数据
     * 用户信息
     */
    private void initData() {
        //获取当前用户信息
        Bundle userBundle = Objects.requireNonNull(getActivity()).getIntent().getBundleExtra("user");
        mUser = (User) Objects.requireNonNull(userBundle).get("user");
    }

    /**
     * 初始化控件
     * 适配器
     */
    private void initControls() {
        //刷新按钮
        mFabRefresh.setOnClickListener(view -> {
            queryFirstAid();
            mFabMenu.collapse();
        });

        //初始化适配器
        mFirstAidList = new ArrayList<>();
        mFirstAidAdapter = new FirstAidAdapter(mFirstAidList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mFirstAidAdapter);
    }

    /**
     * 查询急救信息
     */
    private void queryFirstAid() {
        mFirstAidList.clear();
        queryFirstAidFromServer();
    }

    /**
     * 从服务器查询急救信息
     */
    private void queryFirstAidFromServer() {
        RequestBody formBody = new FormBody.Builder()
                .add("username", mUser.getUserName())
                .build();
        String address = HOSPITAL + FIRST_AID_QUERY_USERNAME;
        HttpUtil.doPostRequest(address, formBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = new Message();
                message.what = CONNECTED_FAILED;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = Objects.requireNonNull(response.body()).string();
                List<FirstAid> mCurrentFirstAidList = LoadJsonUtil.getFirstAidList(responseText);
                mFirstAidList.addAll(mCurrentFirstAidList);
                Message message = new Message();
                message.what = QUERY_SUCCESS;
                mHandler.sendMessage(message);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case CONNECTED_FAILED:
                    XToast.warning(mContext, "连接失败,请检查网络!").show();
                    break;
                case QUERY_SUCCESS:
                    mFirstAidAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    /**
     * 注册广播
     * 数据变化监听
     */
    private void registerFirstAidChangeBroadCast() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.thundersoft.hospital.broadcast.FirstAid_CHANGE");
        mReceiver = new FirstAidChangeReceiver();
        mActivity.registerReceiver(mReceiver, mIntentFilter);
    }

    /**
     * 注销广播
     */
    private void unRegisterFirstAidChangeBraodCast() {
        if (mReceiver != null) {
            mActivity.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    /**
     * 急救列表变更监听
     */
    class FirstAidChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            queryFirstAid();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterFirstAidChangeBraodCast();
    }
}
