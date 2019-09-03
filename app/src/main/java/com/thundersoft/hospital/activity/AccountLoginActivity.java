package com.thundersoft.hospital.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AccountLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String HOSPITAL = "http://localhost:8080/hospital/system/";

    private static final String LOGIN = "clientUserLogin";

    private static final int LOGIN_FAILED = 1;

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

    private Context mContext;

    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login);

        //初始化数据
        initData();
        //初始化控件
        initControls();
    }

    /**
     * 初始化数据
     * 活动,视图绑定
     */
    private void initData(){
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
    private void initControls(){
        StatusBarUtils.translucent(this);
        mPhone.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
    }


    /**
     * 账号登录
     * 注册
     * 手机登录
     * @param view 视图
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.account_login_login:
                String account = String.valueOf(mAccount.getText());
                String password = String.valueOf(mPassword.getText());

                Map<String, String> available = isInputAvailable(account, password);
                if (Objects.requireNonNull(available.get("type")).equals("success")){
                    //从数据库获取数据
                    String address = HOSPITAL + LOGIN + "?username=" + account + "&password" + password;
                    String body = userLogin(account,password);
                    if (body == null){
                        break;
                    }

                    HttpUtil.sendOkHttpRequest(address,new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Message message = new Message();
                            message.what= LOGIN_FAILED;
                            mHandler.sendMessage(message);
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String responseText = Objects.requireNonNull(response.body()).string();
                            User user = LoadJsonUtil.getUser(responseText);

                            //将用户信息和意图捆绑
                            Intent login = new Intent(mContext,MainActivity.class);
                            Bundle userBundle = new Bundle();
                            userBundle.putParcelable("user",user);
                            login.putExtra("user",userBundle);
                            startActivity(login);
                            mActivity.finish();
                        }
                    });
                }else {
                    Toast warning = XToast.warning(this, Objects.requireNonNull(available.get("msg")));
                    warning.setGravity(Gravity.CENTER,0,0);
                    warning.show();
                }

                break;
            case R.id.signUp_signUp:
                Intent signUp = new Intent(this,SignUpActivity.class);
                startActivity(signUp);
                break;
            case R.id.account_login_phone:

                break;

        }
    }

    /**
     * 输入是否合法
     * @param account  用户名
     * @param password 密码
     * @return         返回Map映射
     */
    private Map<String,String> isInputAvailable(String account,String password){
        Map<String,String> ret = new HashMap<>();
        if (TextUtils.isEmpty(account)){
            ret.put("type","error");
            ret.put("msg","账号不得为空!");
            return ret;
        }
        if (TextUtils.isEmpty(password)){
            ret.put("type","error");
            ret.put("msg","密码不得为空!");
            return ret;
        }
        ret.put("type","success");
        return ret;
    }

    /**
     * 用户登录信息
     * @param username 用户名
     * @param password 密码
     * @return         返回JSON格式字符串
     */
    private String userLogin(String username,String password){
        JSONObject object = new JSONObject();
        try {
            object.put("username",username);
            object.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object.toString();
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case LOGIN_FAILED:
                    Toast warning = XToast.warning(mContext,"登录失败,请检查网络!");
                    warning.setGravity(Gravity.CENTER,0,0);
                    warning.show();
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
}
