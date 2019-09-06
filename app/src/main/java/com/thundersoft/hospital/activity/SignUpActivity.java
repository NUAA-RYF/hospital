package com.thundersoft.hospital.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.thundersoft.hospital.R;
import com.thundersoft.hospital.model.User;
import com.thundersoft.hospital.util.HttpUtil;
import com.thundersoft.hospital.util.LoadJsonUtil;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.edittext.PasswordEditText;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;
import com.xuexiang.xui.widget.toast.XToast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.thundersoft.hospital.util.HttpUrl.CLIENT_SIGN_UP;
import static com.thundersoft.hospital.util.HttpUrl.HOSPITAL;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CONNECTED_FAILED = 1;

    private static final int SIGN_UP_FAILED = 2;

    private static final int SIGN_UP_SUCCESS = 3;

    @BindView(R.id.signUp_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.signUp_account)
    MaterialEditText mAccount;
    @BindView(R.id.signUp_password)
    PasswordEditText mPassword;
    @BindView(R.id.signUp_repeat_password)
    PasswordEditText mRepeatPassword;
    @BindView(R.id.signUp_phone)
    MaterialEditText mPhone;
    @BindView(R.id.signUp_signUp)
    SuperButton mSignUp;


    private Context mContext;
    private Activity mActivity;
    private ProgressDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        ActivityController.addActivity(this);
        mContext = this;
        mActivity = this;
        //初始化控件
        initControls();
    }

    private void initControls() {
        //沉浸式状态栏
        StatusBarUtils.translucent(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("注册");
        }

        //初始化按键监听
        mSignUp.setOnClickListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }


    /**
     * 点击事件
     * 注册账号
     *
     * @param view 主视图
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signUp_signUp) {
            String account = mAccount.getEditValue();
            String password = String.valueOf(mPassword.getText());
            String repeat_password = String.valueOf(mRepeatPassword.getText());
            String phone = mPhone.getEditValue();

            Map<String, String> available = isInputAvailable(account, password, repeat_password, phone);
            if (Objects.requireNonNull(available.get("type")).equals("success")) {

                //打开注册加载提示框
                showLoadingDialog();
                //输入合法,向服务端提交数据
                String address = HOSPITAL + CLIENT_SIGN_UP + "?username=" + account +
                        "&password=" + password +
                        "&phone=" + phone;
                HttpUtil.sendOkHttpRequest(address, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        //网络连接失败,向用户提示
                        Message message = new Message();
                        message.what = CONNECTED_FAILED;
                        mHandler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseText = Objects.requireNonNull(response.body()).string();
                        Map<String, String> ret = LoadJsonUtil.signUpAndGetIDORMsg(responseText);

                        //获取后台返回信息
                        if (Objects.requireNonNull(ret.get("type")).equals("success")) {
                            //注册成功,关闭注册加载提示框
                            Message message = new Message();
                            message.what = SIGN_UP_SUCCESS;
                            mHandler.sendMessage(message);

                            //保存用户
                            int id = Integer.parseInt(Objects.requireNonNull(ret.get("id")));
                            User user = new User(id, account, password, phone);
                            user.save();

                            //用户信息与意图绑定后进入意图
                            Intent login = new Intent(mContext, MainActivity.class);
                            Bundle userBundle = new Bundle();
                            userBundle.putParcelable("user", user);
                            login.putExtra("user", userBundle);
                            startActivity(login);

                            //结束活动
                            mActivity.finish();
                        } else {
                            if (ret.get("msg") != null) {
                                Message message = new Message();
                                message.what = SIGN_UP_FAILED;
                                message.obj = ret.get("msg");
                                mHandler.sendMessage(message);
                            }
                        }
                    }
                });
            } else {
                //输入不合法
                Toast warning = XToast.warning(this, Objects.requireNonNull(available.get("msg")));
                warning.setGravity(Gravity.CENTER, 0, 0);
                warning.show();
            }
        }
    }

    /**
     * 输入是否合法
     *
     * @param account         账户 不得为空
     * @param password        密码 不得为空 6-18位 大小写字母和数字混合
     * @param repeat_password 重复密码 与密码相同
     * @param phone           手机号码 不得为空 11位 仅能为数字
     * @return 返回Map类型 type为类型 msg为消息内容
     */
    private Map<String, String> isInputAvailable(String account, String password,
                                                 String repeat_password, String phone) {
        Map<String, String> ret = new HashMap<>();

        //账号是否合法
        if (TextUtils.isEmpty(account)) {
            ret.put("type", "error");
            ret.put("msg", "账号不得为空!");
            return ret;
        }

        //密码是否合法
        if (TextUtils.isEmpty(password)) {
            ret.put("type", "error");
            ret.put("msg", "密码不得为空!");
            return ret;
        } else {
            if (password.length() < 6 || password.length() > 18) {
                ret.put("type", "error");
                ret.put("msg", "密码为6-18位!");
                return ret;
            }
            boolean numFlag = false;
            boolean upCase = false;
            boolean lowCase = false;
            for (int i = 0; i < password.length(); i++) {
                char word = password.charAt(i);
                if (word >= '0' && word <= '9') {
                    numFlag = true;
                }
                if (word >= 'A' && word <= 'Z') {
                    upCase = true;
                }
                if (word >= 'a' && word <= 'z') {
                    lowCase = true;
                }
            }
            if (!(numFlag && upCase && lowCase)) {
                ret.put("type", "error");
                ret.put("msg", "密码为大小写字母和数字的组合!");
                return ret;
            }
        }

        //重复密码是否合法
        if (!repeat_password.equals(password)) {
            ret.put("type", "error");
            ret.put("msg", "两次密码输入不同!");
            return ret;
        }

        //手机号是否合法
        if (TextUtils.isEmpty(phone)) {
            ret.put("type", "error");
            ret.put("msg", "手机号不为空!");
            return ret;
        } else {
            if (phone.length() != 11) {
                ret.put("type", "error");
                ret.put("msg", "手号应为11位!");
                return ret;
            }
            for (int i = 0; i < phone.length(); i++) {
                char num = phone.charAt(i);
                if (num < '0' || num > '9') {
                    ret.put("type", "error");
                    ret.put("msg", "手机号仅能为数字!");
                    return ret;
                }
            }
        }

        ret.put("type", "success");
        return ret;
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            closeLoadingDialog();
            switch (msg.what) {
                case CONNECTED_FAILED:
                    Toast connected = XToast.warning(mContext, "注册失败,请检查网络!");
                    connected.setGravity(Gravity.CENTER, 0, 0);
                    connected.show();
                    break;
                case SIGN_UP_FAILED:
                    String info = String.valueOf(msg.obj);
                    Toast signUp = XToast.warning(mContext, info);
                    signUp.setGravity(Gravity.CENTER, 0, 0);
                    signUp.show();
                    break;
                case SIGN_UP_SUCCESS:
                    Toast success = XToast.success(mContext, "注册失败,请检查网络!");
                    success.setGravity(Gravity.CENTER, 0, 0);
                    success.show();
                    break;
            }
        }
    };


    /**
     * 打开注册加载提示框
     */
    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new ProgressDialog(this);
            mLoadingDialog.setMessage("正在注册...");
            mLoadingDialog.show();
        } else {
            mLoadingDialog.show();
        }
    }

    /**
     * 关闭注册加载提示框
     */
    private void closeLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }
}
