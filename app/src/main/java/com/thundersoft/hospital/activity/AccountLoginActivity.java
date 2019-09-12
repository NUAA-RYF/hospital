package com.thundersoft.hospital.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.thundersoft.hospital.R;
import com.thundersoft.hospital.model.User;
import com.thundersoft.hospital.util.HttpUtil;
import com.thundersoft.hospital.util.LoadJsonUtil;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.edittext.ClearEditText;
import com.xuexiang.xui.widget.edittext.PasswordEditText;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;
import com.xuexiang.xui.widget.toast.XToast;

import org.jetbrains.annotations.NotNull;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.thundersoft.hospital.util.HttpUrl.CLIENT_ACCOUNT_LOGIN;
import static com.thundersoft.hospital.util.HttpUrl.HOSPITAL;

public class AccountLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int LOGIN_FAILED = 1;

    private static final int LOGIN_ERROR_MESSAGE = 2;

    private static final int RET_ISNULL = 3;

    @BindView(R.id.account_login_account)
    ClearEditText mAccount;
    @BindView(R.id.account_login_password)
    PasswordEditText mPassword;
    @BindView(R.id.account_login_phone)
    SuperButton mPhone;
    @BindView(R.id.account_login_signUp)
    SuperButton mSignUp;
    @BindView(R.id.account_login_login)
    SuperButton mLogin;
    @BindView(R.id.account_login_img)
    ImageView mImg;

    private Context mContext;

    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login);
        ButterKnife.bind(this);


        //初始化数据
        initData();

        //初始化控件
        initControls();

        //免验证登录
        secondLoginWithoutRequest();
    }

    /**
     * 初始化数据
     * 活动,视图绑定
     */
    private void initData() {
        ActivityController.addActivity(this);
        ButterKnife.bind(this);
        mContext = this;
        mActivity = this;
    }

    /**
     * 初始化控件
     * 沉浸式状态栏
     * 账号登录 手机登录 注册点击监听
     */
    private void initControls() {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.drawable_logo);
        Glide.with(this).load(bitmap).into(mImg);
        StatusBarUtils.translucent(this);
        mPhone.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
    }


    /**
     * 账号登录
     * 注册
     * 手机登录
     *
     * @param view 视图
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.account_login_login:
                String account = String.valueOf(mAccount.getText());
                String password = String.valueOf(mPassword.getText());

                Map<String, String> available = isInputAvailable(account, password);
                if (Objects.requireNonNull(available.get("type")).equals("success")) {
                    //从数据库获取数据
                    RequestBody formBody = new FormBody.Builder()
                            .add("username",account)
                            .add("password",password)
                            .build();
                    String address = HOSPITAL + CLIENT_ACCOUNT_LOGIN;
                    loginFromServer(address,formBody);
                } else {
                    Toast warning = XToast.warning(this, Objects.requireNonNull(available.get("msg")));
                    warning.setGravity(Gravity.CENTER, 0, 0);
                    warning.show();
                }
                break;
            case R.id.account_login_signUp:
                Intent signUp = new Intent(this, SignUpActivity.class);
                startActivity(signUp);
                break;
            case R.id.account_login_phone:
                Intent phoneLogin = new Intent(this,PhoneLoginActivity.class);
                startActivity(phoneLogin);
                break;

        }
    }

    /**
     * 向服务器申请登录
     * @param address  地址
     * @param formBody 请求参数
     */
    private void loginFromServer(String address, RequestBody formBody){
        HttpUtil.doPostRequest(address, formBody,new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Message message = new Message();
                        message.what = LOGIN_FAILED;
                        mHandler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseText = Objects.requireNonNull(response.body()).string();
                        Map<String,String> ret = LoadJsonUtil.getUser(responseText);
                        if (ret == null){
                            Message message = new Message();
                            message.what = LOGIN_FAILED;
                            mHandler.sendMessage(message);
                            return;
                        }
                        String type = ret.get("type");

                        if(Objects.requireNonNull(type).equals("success")){
                            //获得用户信息
                            int id = Integer.parseInt(Objects.requireNonNull(ret.get("id")));
                            String username = ret.get("username");
                            String password = ret.get("password");
                            String phone = ret.get("phone");
                            User user = new User(id,username,password,phone);
                            user.save();

                            //将用户信息和意图捆绑
                            Intent loginIntent = new Intent(mContext, MainActivity.class);
                            Bundle userBundle = new Bundle();
                            userBundle.putParcelable("user", user);
                            loginIntent.putExtra("user", userBundle);
                            startActivity(loginIntent);
                            mActivity.finish();
                        }else {
                            String msg = ret.get("msg");
                            Message message = new Message();
                            message.what = LOGIN_ERROR_MESSAGE;
                            message.obj = msg;
                            mHandler.sendMessage(message);
                        }
                    }
        });
    }

    /**
     * 输入是否合法
     *
     * @param account  用户名
     * @param password 密码
     * @return 返回Map映射
     */
    private Map<String, String> isInputAvailable(String account, String password) {
        Map<String, String> ret = new HashMap<>();
        if (TextUtils.isEmpty(account)) {
            ret.put("type", "error");
            ret.put("msg", "账号不得为空!");
            return ret;
        }
        if (TextUtils.isEmpty(password)) {
            ret.put("type", "error");
            ret.put("msg", "密码不得为空!");
            return ret;
        }
        ret.put("type", "success");
        return ret;
    }


    /**
     * 登录失败,返回提示信息
     * 在主线程中处理UI更新
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case LOGIN_FAILED:
                    Toast warning = XToast.warning(mContext, "登录失败,请检查网络!");
                    warning.setGravity(Gravity.CENTER, 0, 0);
                    warning.show();
                    break;
                case LOGIN_ERROR_MESSAGE:
                    String errorMsg = String.valueOf(msg.obj);
                    Toast error = XToast.error(mContext, errorMsg);
                    error.setGravity(Gravity.CENTER, 0, 0);
                    error.show();
                    break;
                case RET_ISNULL:
                    Toast retIsNull = XToast.warning(mContext, "登录失败,返回信息为空!");
                    retIsNull.setGravity(Gravity.CENTER, 0, 0);
                    retIsNull.show();
                    break;
            }
        }
    };


    /**
     * 在Controller中移除本活动
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }

    /**
     * 第二次登录免验证
     */
    private void secondLoginWithoutRequest(){
        User user = DataSupport.findFirst(User.class);
        if (user != null){
            //将用户信息与意图捆绑并进入意图
            Intent loginIntent = new Intent(this, MainActivity.class);
            Bundle userBundle = new Bundle();
            userBundle.putParcelable("user", user);
            loginIntent.putExtra("user", userBundle);
            startActivity(loginIntent);
            mActivity.finish();
        }
    }
}
