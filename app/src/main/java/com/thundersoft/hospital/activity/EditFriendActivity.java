package com.thundersoft.hospital.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.thundersoft.hospital.R;
import com.thundersoft.hospital.model.Friend;
import com.thundersoft.hospital.model.User;
import com.thundersoft.hospital.util.HttpUrl;
import com.thundersoft.hospital.util.HttpUtil;
import com.thundersoft.hospital.util.LoadJsonUtil;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;
import com.xuexiang.xui.widget.toast.XToast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.thundersoft.hospital.util.HttpUrl.*;

public class EditFriendActivity extends AppCompatActivity {

    private static final String TAG = "EditFriendActivity";

    public static final int ADD = 1;

    public static final int CHANGE = 2;

    private static final int CONNECTED_FAILED = 3;

    private static final int QUERY_FAILED = 4;

    private static final int QUERY_SUCCESS = 5;

    private static final int SUBMIT_SUCCESS = 6;

    private static final int SUBMIT_FAILED = 7;

    @BindView(R.id.edit_friend_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.edit_friend_name)
    MaterialEditText mName;
    @BindView(R.id.edit_friend_phone)
    MaterialEditText mPhone;
    @BindView(R.id.edit_friend_relation)
    MaterialEditText mRelation;
    @BindView(R.id.edit_friend_submit)
    SuperButton mSubmit;

    private int handle;

    private Context mContext;

    private Friend mFriend;

    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friend);
        ButterKnife.bind(this);
        ActivityController.addActivity(this);

        //获取当前用户信息
        getUserInfoFromIntent();


        //初始化数据
        initData();


        //初始化控件
        initControls();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mContext = this;

        //判断操作类型,默认为添加操作
        handle = getIntent().getIntExtra("handle", ADD);
        int friendId = getIntent().getIntExtra("friendId", 0);

        //初始化控件
        if (handle == CHANGE) {
            queryFriendById(friendId);
        }
    }

    /**
     * 初始化控件
     */
    private void initControls() {

        //沉浸式状态栏
        StatusBarUtils.translucent(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("编辑好友信息");
        }

        mSubmit.setOnClickListener(view -> {
            //获取UI信息
            String name = mName.getEditValue();
            String relation = mRelation.getEditValue();
            String phone = mPhone.getEditValue();
            Map<String, String> result = isInputAvailable(name, relation, phone);

            //输入是否合法
            if (Objects.requireNonNull(result.get("type")).equals("error")) {
                XToast.warning(mContext, Objects.requireNonNull(result.get("msg"))).show();
                return;
            }
            String address;
            if (handle == ADD) {
                //添加操作
                String username = mCurrentUser.getUserName();
                Friend friend = new Friend(username, name, phone, relation);
                address = HOSPITAL + FRIEND_INSERT + new Gson().toJson(friend);
            } else {
                //修改操作
                Friend friend = new Friend(mFriend.getId(), mFriend.getUsername(), name, phone, relation, mFriend.isClose());
                address = HOSPITAL + FRIEND_UPDATE + new Gson().toJson(friend);
            }
            editFriendInfoToServer(address);
        });
    }

    /**
     * 将好友信息提交至服务器
     *
     * @param address 地址
     */
    private void editFriendInfoToServer(String address) {
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
                boolean result = LoadJsonUtil.messageIsSuccess(responseText);
                Message message = new Message();
                if (result) {
                    message.what = SUBMIT_SUCCESS;
                } else {
                    message.what = SUBMIT_FAILED;
                }
                mHandler.sendMessage(message);
            }
        });
    }

    /**
     * 输入是否合法
     *
     * @param name     姓名
     * @param relation 关系
     * @param phone    手机号码
     * @return 返回信息
     */
    private Map<String, String> isInputAvailable(String name, String relation, String phone) {
        Map<String, String> ret = new HashMap<>();
        if (TextUtils.isEmpty(name)) {
            ret.put("type", "error");
            ret.put("msg", "姓名不得为空!");
            return ret;
        }
        if (name.length() >= 10){
            ret.put("type", "error");
            ret.put("msg", "姓名不得多余10个字!");
            return ret;
        }
        if (TextUtils.isEmpty(relation)) {
            ret.put("type", "error");
            ret.put("msg", "关系不得为空!");
            return ret;
        }
        if (relation.length() >= 6){
            ret.put("type", "error");
            ret.put("msg", "关系不得多于5个字!");
            return ret;
        }
        if (TextUtils.isEmpty(phone)) {
            ret.put("type", "error");
            ret.put("msg", "手机号码不得为空!");
            return ret;
        }
        if (phone.length() != 11) {
            ret.put("type", "error");
            ret.put("msg", "手机号码为11位!");
            return ret;
        }
        for (int i = 0; i < phone.length(); i++) {
            char word = phone.charAt(i);
            if (word > '9' || word < '0') {
                ret.put("type", "error");
                ret.put("msg", "手机号码仅能为数字!");
                return ret;
            }
        }

        ret.put("type", "success");
        return ret;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ActivityController.removeActivity(this);
            finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }

    private void queryFriendById(int id) {
        String address = HOSPITAL + FRIEND_QUERY_ID + "?id=" + id;
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
                mFriend = LoadJsonUtil.getFriendById(responseText);

                if (mFriend != null) {
                    Message message = new Message();
                    message.what = QUERY_SUCCESS;
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case CONNECTED_FAILED:
                    XToast.warning(mContext, "查询失败,请检查网络!").show();
                    break;
                case QUERY_FAILED:
                    XToast.info(mContext, "好友列表为空!").show();
                    break;
                case QUERY_SUCCESS:
                    mName.setText(mFriend.getName());
                    mRelation.setText(mFriend.getRelation());
                    mPhone.setText(mFriend.getPhone());
                    break;
                case SUBMIT_FAILED:
                    break;
                case SUBMIT_SUCCESS:
                    if (handle == ADD){
                        XToast.success(mContext,"添加成功!").show();
                    }else {
                        XToast.success(mContext,"修改成功!").show();
                    }
                    //数据改变发送广播
                    Intent mFriendChangeBroadcast = new Intent("com.thundersoft.hospital.broadcast.FRIEND_CHANGE");
                    sendBroadcast(mFriendChangeBroadcast);
                    finish();
                    break;
            }
        }
    };

    /**
     * 从意图中获取用户信息
     */
    private void getUserInfoFromIntent() {
        Bundle userBundle = getIntent().getBundleExtra("user");
        if (userBundle != null) {
            mCurrentUser = (User) userBundle.get("user");
        } else {
            Log.e(TAG, "getUserInfoFromIntent: 用户信息为空!");
        }
    }
}
