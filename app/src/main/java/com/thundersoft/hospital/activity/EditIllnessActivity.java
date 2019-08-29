package com.thundersoft.hospital.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.thundersoft.hospital.R;
import com.thundersoft.hospital.model.IllnessInfo;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;
import com.xuexiang.xui.widget.toast.XToast;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditIllnessActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EditIllnessActivity";
    @BindView(R.id.add_illness_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.add_illness_edit_name)
    MaterialEditText mEditName;
    @BindView(R.id.add_illness_edit_userName)
    MaterialEditText mEditUserName;
    @BindView(R.id.add_illness_edit_phone)
    MaterialEditText mEditPhone;
    @BindView(R.id.add_illness_edit_age)
    MaterialEditText mEditAge;
    @BindView(R.id.add_illness_spinner_gender)
    MaterialSpinner mSpinnerGender;
    @BindView(R.id.add_illness_edit_address)
    MultiLineEditText mEditAddress;
    @BindView(R.id.add_illness_fetchLocation)
    SuperButton mFetchLocation;
    @BindView(R.id.add_illness_edit_content)
    MultiLineEditText mEditContent;
    @BindView(R.id.add_illness_submit)
    SuperButton mSubmit;

    private Context mContext;
    private Activity mActivity;

    private int handle;
    private int infoId;

    private long mExitTimes;

    private static final int LOCATION = 1;
    public static final int ADD = 1;
    public static final int CHANGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_illness);
        ButterKnife.bind(this);
        mContext = this;
        mActivity = this;
        Intent mIntent = getIntent();
        //获取是添加操作还是修改操作
        handle = mIntent.getIntExtra("handle", 0);
        infoId = mIntent.getIntExtra("illnessId", 0);

        //初始化按键监听
        initClickListener();
        //初始化数据
        initData();
        //初始化控件
        initControls();
    }


    /**
     * 初始化输出
     * 下拉框
     * 编辑操作初始化控件
     */
    private void initData() {
        mExitTimes = System.currentTimeMillis();

        //初始化下拉框
        List<String> genderList = new ArrayList<>();
        genderList.add("请选择性别");
        genderList.add("男");
        genderList.add("女");
        mSpinnerGender.setItems(genderList);

        //编辑操作,初始化控件
        if (handle == CHANGE && infoId != 0){
            IllnessInfo mInfo = DataSupport.find(IllnessInfo.class,infoId);
            mEditName.setText(mInfo.getUserIllnessName());
            mEditUserName.setText(mInfo.getUserName());
            mEditPhone.setText(mInfo.getUserPhone());
            mEditAge.setText(mInfo.getUserAge());
            mSpinnerGender.setText(mInfo.getUserGender());
            mEditAddress.setContentText(mInfo.getUserAddress());
            mEditContent.setContentText(mInfo.getUserIllnessInfo());
        }
    }

    /**
     * 初始化按键监听
     * 获取当前位置
     * 提交保存信息
     */
    private void initClickListener() {
        mFetchLocation.setOnClickListener(this);
        mSubmit.setOnClickListener(this);
    }

    /**
     * 初始化控件
     * 标题栏
     */
    private void initControls() {
        //沉浸式状态栏
        StatusBarUtils.translucent(mActivity);
        //标题栏
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("编辑信息");
        }
    }

    /**
     * 按键监听
     * 获取当前位置
     * 提交保存信息
     *
     * @param view 视图
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_illness_fetchLocation:
                //获取当前位置
                //定位客户端
                AMapLocationClient aMapLocationClient = new AMapLocationClient(mContext);
                /*
                高精度模式:GPS和网络获取更精确的一个
                一次定位模式:开
                返回信息:开
                超时时间:8000ms
                 */
                AMapLocationClientOption aMapLocationClientOption = new AMapLocationClientOption();
                aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                aMapLocationClientOption.setOnceLocation(true);
                aMapLocationClientOption.setNeedAddress(true);
                aMapLocationClientOption.setHttpTimeOut(8000);
                AMapLocationListener aMapLocationListener = aMapLocation -> {
                    if (aMapLocation != null) {
                        if (aMapLocation.getErrorCode() == 0) {
                            //可在其中解析amapLocation获取相应内容。
                            Message message = new Message();
                            message.what = LOCATION;
                            message.obj = aMapLocation.getAddress();
                            mHandler.sendMessage(message);
                            aMapLocationClient.stopLocation();
                        } else {
                            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                            Log.e(TAG, "location Error, ErrCode:"
                                    + aMapLocation.getErrorCode() + ", errInfo:"
                                    + aMapLocation.getErrorInfo());
                        }
                    }
                };
                aMapLocationClient.setLocationOption(aMapLocationClientOption);
                aMapLocationClient.setLocationListener(aMapLocationListener);
                aMapLocationClient.startLocation();

                break;
            case R.id.add_illness_submit:
                //判断用户是否登录

                //提交保存信息
                String illName = String.valueOf(mEditName.getText());
                String userName = String.valueOf(mEditUserName.getText());
                String phone = String.valueOf(mEditPhone.getText());
                String age = String.valueOf(mEditAge.getText());
                String gender = String.valueOf(mSpinnerGender.getText());
                String address = mEditAddress.getContentText();
                String content = mEditContent.getContentText();

                //是否合法
                boolean result = isInputAvailable(illName, userName, phone, age, gender, address, content);

                if (result) {
                    //添加信息操作
                    if (handle == ADD) {
                        IllnessInfo mInfo = new IllnessInfo();
                        mInfo.setUserIllnessName(illName);
                        mInfo.setUserName(userName);
                        mInfo.setUserPhone(phone);
                        mInfo.setUserAge(age);
                        mInfo.setUserGender(gender);
                        mInfo.setUserAddress(address);
                        mInfo.setUserIllnessInfo(content);
                        mInfo.save();
                        Toast success = XToast.success(mContext, "保存成功!");
                        success.setGravity(Gravity.CENTER, 0, 0);
                        success.show();
                        Intent intent = new Intent("com.thundersoft.hospital.broadcast.DATA_CHANGE");
                        sendBroadcast(intent);
                        finish();
                    }
                    //修改信息操作
                    if (handle == CHANGE) {
                        IllnessInfo mInfo = DataSupport.find(IllnessInfo.class, infoId);
                        mInfo.setUserIllnessName(illName);
                        mInfo.setUserName(userName);
                        mInfo.setUserPhone(phone);
                        mInfo.setUserAge(age);
                        mInfo.setUserGender(gender);
                        mInfo.setUserAddress(address);
                        mInfo.setUserIllnessInfo(content);
                        mInfo.update(infoId);
                        Toast success = XToast.success(mContext, "修改成功!");
                        success.setGravity(Gravity.CENTER, 0, 0);
                        success.show();
                        Intent intent = new Intent("com.thundersoft.hospital.broadcast.DATA_CHANGE");
                        sendBroadcast(intent);
                        finish();
                    }
                }
                break;
        }
    }

    /**
     * 输入是否合法
     *
     * @param ill_name  疾病名称 不能为空
     * @param user_name 病人姓名 不能为空
     * @param phone     手机号码 不能为空 仅能为数字 必须11位
     * @param age       年   龄 不能为空 仅能为数字
     * @param gender    性   别 必须选择
     * @param address   地址信息 不能为空
     * @param content   疾病详情 不能为空
     * @return 合法返回true
     */
    private boolean isInputAvailable(String ill_name, String user_name,
                                     String phone, String age, String gender,
                                     String address, String content) {
        /*
          输入不能为空
          疾病名称
          病人姓名
          详细地址
          疾病详情
          性别
         */
        if (ill_name.equals("")) {
            Toast warning = XToast.warning(mContext, "请填写疾病名称!");
            warning.setGravity(Gravity.CENTER, 0, 0);
            warning.show();
            return false;
        }
        if (user_name.equals("")) {
            Toast warning = XToast.warning(mContext, "请填写病人姓名!");
            warning.setGravity(Gravity.CENTER, 0, 0);
            warning.show();
            return false;
        }
        if (address.equals("")) {
            Toast warning = XToast.warning(mContext, "请填写详细地址!");
            warning.setGravity(Gravity.CENTER, 0, 0);
            warning.show();
            return false;
        }
        if (content.equals("")) {
            Toast warning = XToast.warning(mContext, "请填写疾病详情!");
            warning.setGravity(Gravity.CENTER, 0, 0);
            warning.show();
            return false;
        }
        if (gender.equals("请选择性别")) {
            Toast warning = XToast.warning(mContext, "请选择性别!");
            warning.setGravity(Gravity.CENTER, 0, 0);
            warning.show();
            return false;
        }

        /*
          内容不为空且仅能为数字
          电话号码必须为11位
          年龄
         */
        if (phone.equals("")) {
            Toast warning = XToast.warning(mContext, "请填写11位手机号码!");
            warning.setGravity(Gravity.CENTER, 0, 0);
            warning.show();
            return false;
        } else {
            for (int i = 0; i < phone.length(); i++) {
                char word = phone.charAt(i);
                if (word > '9' || word < '0') {
                    Toast warning = XToast.warning(mContext, "手机号仅能为数字!");
                    warning.setGravity(Gravity.CENTER, 0, 0);
                    warning.show();
                    return false;
                }
            }
            if (phone.length() != 11) {
                Toast warning = XToast.warning(mContext, "手机号码为11位!");
                warning.setGravity(Gravity.CENTER, 0, 0);
                warning.show();
                return false;
            }
        }
        if (age.equals("")) {
            Toast warning = XToast.warning(mContext, "请填写年龄!");
            warning.setGravity(Gravity.CENTER, 0, 0);
            warning.show();
            return false;
        } else {
            for (int i = 0; i < age.length(); i++) {
                char word = age.charAt(i);
                if (word > '9' || word < '0') {
                    Toast warning = XToast.warning(mContext, "年龄仅能为数字!");
                    warning.setGravity(Gravity.CENTER, 0, 0);
                    warning.show();
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 返回按键
     *
     * @param item 项目
     * @return 返回true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == LOCATION) {
                String address = String.valueOf(msg.obj);
                mEditAddress.setContentText(address);
            }
        }
    };

    /**
     * 时差不超过2秒
     * 点击两次返回键退出程序
     */
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mExitTimes > 2000){
            mExitTimes = System.currentTimeMillis();
            Toast normal = XToast.normal(this,"再次点击返回键,退出信息页面!");
            normal.setGravity(Gravity.CENTER,0,0);
            normal.show();
        }else {
            super.onBackPressed();
        }
    }
}
