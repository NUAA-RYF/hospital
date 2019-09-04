package com.thundersoft.hospital.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.thundersoft.hospital.R;
import com.thundersoft.hospital.util.HttpUtil;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.button.CountDownButton;
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

import static com.thundersoft.hospital.util.HttpUrl.HOSPITAL;
import static com.thundersoft.hospital.util.HttpUrl.PHONE_LOGIN;

public class PhoneLoginActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.account_login_img)
    ImageView mImg;
    @BindView(R.id.phone_login_phone)
    ClearEditText mPhone;
    @BindView(R.id.phone_login_verifyCode)
    PasswordEditText mVerifyCode;
    @BindView(R.id.phone_login_submit)
    SuperButton mSubmit;
    @BindView(R.id.phone_login_verifyBtn)
    CountDownButton mVerifyBtn;

    private Context mContext;

    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        ButterKnife.bind(this);

        //初始化数据
        initData();

        //初始化控件
        initControls();

        //初始化监听
        initClickListener();
    }

    /**
     * 初始化数据
     * 环境和活动初始化
     * 活动添加至活动控制器中
     */
    private void initData() {
        mContext = this;
        mActivity = this;
        ActivityController.addActivity(mActivity);
    }

    /**
     * 初始化控件
     * 背景图片加载
     */
    private void initControls() {
        StatusBarUtils.translucent(mActivity);
        //加载背景图片
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.drawable_logo);
        Glide.with(mActivity).load(bitmap).into(mImg);
    }

    /**
     * 初始化监听
     * 登录监听
     * 获取验证码监听
     */
    private void initClickListener() {
        mSubmit.setOnClickListener(this);
        mVerifyBtn.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(mActivity);
    }

    /**
     * 登录逻辑和获取验证码逻辑
     * @param view 视图
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.phone_login_submit) {
            String phone = String.valueOf(mPhone.getText());
            String code = String.valueOf(mVerifyCode.getText());

            //验证输入是否正确
            Map<String, String> result = isInputAvailable(phone);
            if (Objects.requireNonNull(result.get("type")).equals("success")) {

                //向服务器提交数据进行验证
                String address = HOSPITAL + PHONE_LOGIN;
                HttpUtil.sendOkHttpRequest(address, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Toast warning = XToast.warning(mContext, "登录失败,请检查网络!");
                        warning.setGravity(Gravity.CENTER, 0, 0);
                        warning.show();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseText = Objects.requireNonNull(response.body()).string();

                        //解析服务器返回的数据


                        //将用户信息与意图捆绑并进入意图
                        Intent login = new Intent(mContext,MainActivity.class);
                        Bundle userBundle = new Bundle();
                        //userBundle.putParcelable("user",user);
                        login.putExtra("user",userBundle);
                        mActivity.startActivity(login);
                        mActivity.finish();
                    }
                });
            } else {
                //向用户提示错误信息
                Toast warning = XToast.warning(mContext, Objects.requireNonNull(result.get("msg")));
                warning.setGravity(Gravity.CENTER, 0, 0);
                warning.show();
            }
            return;
        }
        if (view.getId() == R.id.phone_login_verifyBtn){
            String phoneNum = String.valueOf(mPhone.getText());

            //验证输入是否正确
            Map<String, String> result = isInputAvailable(phoneNum);
            if (Objects.requireNonNull(result.get("type")).equals("success")){

                //向服务器申请发送验证码
                String address = HOSPITAL;
            }
        }
    }

    /**
     * 验证手机号是否合法
     * 不为空 11位 仅能为数字
     * @param phone 手机号
     * @return 返回信息
     */
    private Map<String,String> isInputAvailable(String phone){
        Map<String,String> ret = new HashMap<>();
        if (TextUtils.isEmpty(phone)){
            ret.put("type","error");
            ret.put("msg","手机号不能为空!");
            return ret;
        }
        if (phone.length() != 11){
            ret.put("type","error");
            ret.put("msg","手机号为11位!");
            return ret;
        }
        for (int i = 0; i < phone.length(); i++) {
            char number = phone.charAt(i);
            if (number > '9' || number < '0'){
                ret.put("type","error");
                ret.put("msg","手机号仅能为数字!");
                return ret;
            }
        }
        ret.put("type","success");
        return ret;
    }
}
