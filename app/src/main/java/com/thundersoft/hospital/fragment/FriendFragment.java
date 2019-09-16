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
import com.thundersoft.hospital.activity.EditFriendActivity;
import com.thundersoft.hospital.activity.EditIllnessActivity;
import com.thundersoft.hospital.adapter.FriendAdapter;
import com.thundersoft.hospital.model.Friend;
import com.thundersoft.hospital.model.User;
import com.thundersoft.hospital.util.HttpUtil;
import com.thundersoft.hospital.util.LoadJsonUtil;
import com.xuexiang.xui.widget.toast.XToast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import static com.thundersoft.hospital.util.HttpUrl.FRIEND_QUERY_USERNAME;
import static com.thundersoft.hospital.util.HttpUrl.HOSPITAL;

public class FriendFragment extends Fragment {

    private static final int QUERY_FRIEND_SUCCESS = 1;

    private static final int QUERY_FRIEND_FAILED = 2;

    private static final int CONNECTED_FAILED = 3;

    @BindView(R.id.friend_friend_recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.friend_fab_add)
    FloatingActionButton mFabAdd;
    @BindView(R.id.friend_fab_refresh)
    FloatingActionButton mFabRefresh;
    @BindView(R.id.friend_fab_menu)
    FloatingActionsMenu mFabMenu;

    private View rootView;

    private List<Friend> mFriendList;

    private FriendAdapter mFriendAdapter;

    private User mUser;

    private Context mContext;

    private FriendChangeReceiver mReceiver;

    private Activity mActivity;

    public static FriendFragment newInstance() {
        FriendFragment fragment = new FriendFragment();
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
            rootView = inflater.inflate(R.layout.fragment_friend, container, false);
        }
        ButterKnife.bind(this, rootView);

        //注册广播监听
        registerFriendChangeBroadCast();

        //初始化数据
        initData();

        //初始化控件
        initConstrols();

        //查询好友
        queryFriend();
        return rootView;
    }

    /**
     * 初始化数据
     * 用户信息获取
     */
    private void initData() {
        Bundle userBundle = Objects.requireNonNull(getActivity()).getIntent().getBundleExtra("user");
        mUser = (User) Objects.requireNonNull(userBundle).get("user");
        mFriendList = new ArrayList<>();
    }

    /**
     * 初始化控件
     * 好友适配器及视图
     */
    private void initConstrols() {
        //添加按钮
        mFabAdd.setOnClickListener(view -> {
            Intent addInfo = new Intent(mContext, EditFriendActivity.class);
            addInfo.putExtra("handle", EditFriendActivity.ADD);
            Bundle userBundle = Objects.requireNonNull(getActivity()).getIntent().getBundleExtra("user");
            addInfo.putExtra("user", userBundle);
            mFabMenu.collapse();
            startActivity(addInfo);
        });

        //刷新按钮
        mFabRefresh.setOnClickListener(view -> {
            queryFriend();
            mFabMenu.collapse();
        });

        //适配器及其布局初始化
        mFriendAdapter = new FriendAdapter(mFriendList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mFriendAdapter);
    }

    /**
     * 查询好友列表
     */
    private void queryFriend() {
        mFriendList.clear();
        queryFriendInfoFromServer();
    }

    /**
     * 从服务器端查询好友信息
     * Post方式
     */
    private void queryFriendInfoFromServer() {
        RequestBody formBody = new FormBody.Builder()
                .add("username",mUser.getUserName())
                .build();
        String address = HOSPITAL + FRIEND_QUERY_USERNAME;
        HttpUtil.doPostRequest(address,formBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //发送失败
                Message message = new Message();
                message.what = CONNECTED_FAILED;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //发送成功
                String responseText = Objects.requireNonNull(response.body()).string();
                List<Friend> currentFriendList = LoadJsonUtil.getFriendList(responseText);

                Message message = new Message();
                if (currentFriendList.size() <= 0) {
                    message.what = QUERY_FRIEND_FAILED;
                } else {
                    mFriendList.addAll(currentFriendList);
                    message.what = QUERY_FRIEND_SUCCESS;
                }
                mHandler.sendMessage(message);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * 注册广播
     * 数据变化监听
     */
    private void registerFriendChangeBroadCast() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.thundersoft.hospital.broadcast.FRIEND_CHANGE");
        mReceiver = new FriendChangeReceiver();
        mActivity.registerReceiver(mReceiver, mIntentFilter);
    }

    /**
     * 注销广播
     */
    private void unRegisterFriendChangeBraodCast() {
        if (mReceiver != null) {
            mActivity.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    /**
     * 好友列表变更监听
     */
    class FriendChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            queryFriend();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case QUERY_FRIEND_SUCCESS:
                    mFriendAdapter.notifyDataSetChanged();
                    break;
                case QUERY_FRIEND_FAILED:
                    XToast.info(mContext, "好友列表为空!").show();
                    break;
                case CONNECTED_FAILED:
                    XToast.warning(mContext, "发送失败,请检查网络!").show();
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterFriendChangeBraodCast();
    }
}
