package com.thundersoft.hospital.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thundersoft.hospital.R;
import com.thundersoft.hospital.util.HttpUtil;
import com.xuexiang.xui.widget.edittext.ClearEditText;
import com.xuexiang.xui.widget.edittext.PasswordEditText;
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

public class AccountLoginActivity extends AppCompatActivity implements View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login);
        ButterKnife.bind(this);
        mContext = this;
        //初始化控件
        initControls();
    }

    private void initControls(){
        mPhone.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.account_login_login:
                String account = String.valueOf(mAccount.getText());
                String password = String.valueOf(mPassword.getText());

                Map<String, String> available = isInputAvailable(account, password);
                if (Objects.requireNonNull(available.get("type")).equals("success")){
                    //从数据库获取数据
                    String address = "";
                    HttpUtil.sendOkHttpRequest(address, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Toast warning = XToast.warning(mContext,"登录失败,请检查网络!");
                            warning.setGravity(Gravity.CENTER,0,0);
                            warning.show();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String responseText = Objects.requireNonNull(response.body()).string();
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
}
