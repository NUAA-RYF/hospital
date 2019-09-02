package com.thundersoft.hospital.activity;

import android.os.Bundle;
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
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.edittext.PasswordEditText;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;
import com.xuexiang.xui.widget.toast.XToast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        //初始化控件
        initControls();
    }

    private void initControls(){
        //沉浸式状态栏
        StatusBarUtils.translucent(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("注册");
        }

        //初始化按键监听
        mSignUp.setOnClickListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }


    /**
     * 点击事件
     * 注册账号
     * @param view 主视图
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signUp_signUp){
            String account = mAccount.getEditValue();
            String password = String.valueOf(mPassword.getText());
            String repeat_password = String.valueOf(mRepeatPassword.getText());
            String phone = mPhone.getEditValue();

            Map<String, String> available = isInputAvailable(account, password, repeat_password, phone);
            if (Objects.requireNonNull(available.get("type")).equals("success")){
                //输入合法,向服务端提交数据

            }else {
                //输入不合法
                Toast warning = XToast.warning(this, Objects.requireNonNull(available.get("msg")));
                warning.setGravity(Gravity.CENTER,0,0);
                warning.show();
            }

        }
    }

    /**
     * 输入是否合法
     * @param account         账户 不得为空
     * @param password        密码 不得为空 6-18位 大小写字母和数字混合
     * @param repeat_password 重复密码 与密码相同
     * @param phone           手机号码 不得为空 11位 仅能为数字
     * @return 返回Map类型 type为类型 msg为消息内容
     */
    private Map<String,String> isInputAvailable(String account,String password,
                                                String repeat_password,String phone){
        Map<String,String> ret = new HashMap<>();

        //账号是否合法
        if (TextUtils.isEmpty(account)){
            ret.put("type","error");
            ret.put("msg","账号不得为空!");
            return ret;
        }

        //密码是否合法
        if (TextUtils.isEmpty(password)){
            ret.put("type","error");
            ret.put("msg","密码不得为空!");
            return ret;
        }else {
            if (password.length() < 6 || password.length() > 18){
                ret.put("type","error");
                ret.put("msg","密码为6-18位!");
                return ret;
            }
            boolean numFlag = false;
            boolean upCase = false;
            boolean lowCase = false;
            for (int i = 0; i < password.length(); i++) {
                char word = password.charAt(i);
                if (word >= '0' && word <= '9'){
                    numFlag = true;
                }
                if (word >= 'A' && word <= 'Z'){
                    upCase = true;
                }
                if (word >= 'a' && word<= 'z'){
                    lowCase = true;
                }
            }
            if (!(numFlag && upCase && lowCase)){
                ret.put("type","error");
                ret.put("msg","密码为大小写字母和数字的组合!");
                return ret;
            }
        }

        //重复密码是否合法
        if (!repeat_password.equals(password)){
            ret.put("type","error");
            ret.put("msg","两次密码输入不同!");
            return ret;
        }

        //手机号是否合法
        if (TextUtils.isEmpty(phone)){
            ret.put("type","error");
            ret.put("msg","手机号不为空!");
            return ret;
        }else {
            if (phone.length() != 11){
                ret.put("type","error");
                ret.put("msg","手号应为11位!");
                return ret;
            }
            for (int i = 0; i < phone.length(); i++) {
                char num = phone.charAt(i);
                if (num < '0' || num > '9'){
                    ret.put("type","error");
                    ret.put("msg","手机号仅能为数字!");
                    return ret;
                }
            }
        }

        ret.put("type","success");
        return ret;
    }


}
