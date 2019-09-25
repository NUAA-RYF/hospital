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
import com.google.gson.Gson;
import com.thundersoft.hospital.R;
import com.thundersoft.hospital.model.Disease;
import com.thundersoft.hospital.model.User;
import com.thundersoft.hospital.util.HttpUtil;
import com.thundersoft.hospital.util.LoadJsonUtil;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;
import com.xuexiang.xui.widget.toast.XToast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.thundersoft.hospital.util.HttpUrl.DISEASE_INSERT;
import static com.thundersoft.hospital.util.HttpUrl.DISEASE_QUERY_ID;
import static com.thundersoft.hospital.util.HttpUrl.DISEASE_UPDATE;
import static com.thundersoft.hospital.util.HttpUrl.HOSPITAL;

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

    private long mExitTimes;
    private User mUser;
    private Disease mDisease;

    private static final int LOCATION = 1;
    private static final int QUERY_SUCCESS = 2;
    private static final int CONNECTED_FAILED = 3;
    private static final int HANDLE_SUCCESS = 4;
    private static final int HANDLE_FAILED = 5;

    public static final int ADD = 123;
    public static final int CHANGE = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_illness);

        //获取用户信息
        getUserInfoFromIntent();

        //初始化数据
        initData();

        //初始化控件
        initControls();

        //初始化按键监听
        initClickListener();
    }


    /**
     * 初始化数据
     * 下拉框
     * 编辑操作初始化控件
     */
    private void initData() {
        mContext = this;
        mActivity = this;
        mExitTimes = System.currentTimeMillis();

        //控件绑定视图
        ButterKnife.bind(this);
        ActivityController.addActivity(this);

        //获取是添加操作还是修改操作
        Intent mIntent = getIntent();
        handle = mIntent.getIntExtra("handle", 0);
        int infoId = mIntent.getIntExtra("illnessId", 0);

        //初始化下拉框
        List<String> genderList = new ArrayList<>();
        genderList.add("请选择性别");
        genderList.add("男");
        genderList.add("女");
        mSpinnerGender.setItems(genderList);

        //编辑操作,初始化控件
        if (handle == CHANGE && infoId != 0) {
            queryDiseaseInfoById(String.valueOf(infoId));
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
                String disease;

                //提交保存信息
                String name = String.valueOf(mEditUserName.getText());
                String phone = String.valueOf(mEditPhone.getText());
                String age = String.valueOf(mEditAge.getText());
                String gender = String.valueOf(mSpinnerGender.getText());
                String address = mEditAddress.getContentText();
                String diseaseName = String.valueOf(mEditName.getText());
                String diseaseInfo = mEditContent.getContentText();
                //是否合法
                Map<String, String> available = isInputAvailable(diseaseName, name, phone,
                        age, gender, address, diseaseInfo);

                if (Objects.requireNonNull(available.get("type")).equals("success")) {
                    //添加信息操作
                    if (handle == ADD) {
                        mDisease = new Disease(mUser.getUserName(), name, age, phone, gender, address, diseaseName, diseaseInfo);
                        disease = new Gson().toJson(mDisease);
                    } else {
                        //修改信息操作
                        mDisease.setName(name);
                        mDisease.setPhone(phone);
                        mDisease.setAge(age);
                        mDisease.setGender(gender);
                        mDisease.setAddress(address);
                        mDisease.setDiseaseName(diseaseName);
                        mDisease.setDiseaseInfo(diseaseInfo);
                        disease = new Gson().toJson(mDisease);
                    }
                    //向服务器发出修改信息请求
                    saveDiseaseInfo(disease);
                } else {
                    //输入信息不正确,提示用户
                    Toast warning = XToast.warning(mContext, Objects.requireNonNull(available.get("msg")));
                    warning.setGravity(Gravity.CENTER, 0, 0);
                    warning.show();
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
    private Map<String, String> isInputAvailable(String ill_name, String user_name,
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
        Map<String, String> ret = new HashMap<>();
        if (ill_name.equals("")) {
            ret.put("type", "error");
            ret.put("msg", "请填写疾病名称!");
            return ret;
        }
        if (user_name.equals("")) {
            ret.put("type", "error");
            ret.put("msg", "请填写病人姓名!");
            return ret;
        }
        if (address.equals("")) {
            ret.put("type", "error");
            ret.put("msg", "请填写详细地址!");
            return ret;
        }
        if (content.equals("")) {
            ret.put("type", "error");
            ret.put("msg", "请填写疾病详情!");
            return ret;
        }
        if (gender.equals("请选择性别")) {
            ret.put("type", "error");
            ret.put("msg", "请选择性别!");
            return ret;
        }

        /*
          内容不为空且仅能为数字
          电话号码必须为11位
          年龄
         */
        if (phone.equals("")) {
            ret.put("type", "error");
            ret.put("msg", "请填写11位手机号码!");
            return ret;
        } else {
            if (phone.length() != 11) {
                ret.put("type", "error");
                ret.put("msg", "手机号码为11位!");
                return ret;
            }
            for (int i = 0; i < 11; i++) {
                char word = phone.charAt(i);
                if (word > '9' || word < '0') {
                    ret.put("type", "error");
                    ret.put("msg", "手机号仅能为数字!");
                    return ret;
                }
            }
        }

        if (age.equals("")) {
            ret.put("type", "error");
            ret.put("msg", "请填写年龄!");
            return ret;
        } else {
            for (int i = 0; i < age.length(); i++) {
                char word = age.charAt(i);
                if (word > '9' || word < '0') {
                    ret.put("type", "error");
                    ret.put("msg", "年龄仅能为数字!");
                    return ret;
                }
            }
        }
        ret.put("type", "success");
        return ret;
    }

    /**
     * 添加或修改疾病信息
     *
     * @param disease 疾病信息
     */
    private void saveDiseaseInfo(String disease) {
        String address;
        //以POST方式请求时，body携带的请求参数
        RequestBody formBody = new FormBody.Builder()
                .add("disease", disease)
                .build();
        //添加操作OR更新操作
        if (handle == ADD) {
            address = HOSPITAL + DISEASE_INSERT;
        } else {
            address = HOSPITAL + DISEASE_UPDATE;
        }
        //向服务器端以POST方式发出网络请求
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
                boolean result = LoadJsonUtil.messageIsSuccess(responseText);
                Message message = new Message();
                if (result) {
                    message.what = HANDLE_SUCCESS;
                } else {
                    message.what = HANDLE_FAILED;
                }
                mHandler.sendMessage(message);
            }
        });
    }

    /**
     * 按疾病ID查询疾病信息
     *
     * @param id 疾病ID
     */
    private void queryDiseaseInfoById(String id) {
        RequestBody formBody = new FormBody.Builder()
                .add("id", id)
                .build();
        String address = HOSPITAL + DISEASE_QUERY_ID;
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
                mDisease = LoadJsonUtil.getDiseaseById(responseText);
                if (mDisease != null) {
                    Message message = new Message();
                    message.what = QUERY_SUCCESS;
                    mHandler.sendMessage(message);
                }
            }
        });
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
            Toast info;
            switch (msg.what) {
                case QUERY_SUCCESS:
                    mEditName.setText(mDisease.getDiseaseName());
                    mEditUserName.setText(mDisease.getName());
                    mEditPhone.setText(mDisease.getPhone());
                    mEditAge.setText(mDisease.getAge());
                    mSpinnerGender.setText(mDisease.getGender());
                    mEditAddress.setContentText(mDisease.getAddress());
                    mEditContent.setContentText(mDisease.getDiseaseInfo());
                    break;
                case LOCATION:
                    String address = String.valueOf(msg.obj);
                    mEditAddress.setContentText(address);
                    break;
                case HANDLE_SUCCESS:
                    if (handle == ADD) {
                        info = XToast.success(mContext, "添加成功!");
                    } else {
                        info = XToast.success(mContext, "修改成功!");
                    }
                    info.setGravity(Gravity.CENTER, 0, 0);
                    info.show();
                    //数据改变发送广播
                    Intent intent = new Intent("com.thundersoft.hospital.broadcast.DATA_CHANGE");
                    sendBroadcast(intent);
                    finish();
                    break;
                case HANDLE_FAILED:
                    info = XToast.warning(mContext, "操作失败!");
                    info.setGravity(Gravity.CENTER, 0, 0);
                    info.show();
                    break;
                case CONNECTED_FAILED:
                    XToast.warning(mContext, "连接失败,请检查网络!");
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
            mUser = (User) userBundle.get("user");
        } else {
            Log.e(TAG, "getUserInfoFromIntent: 用户信息为空!");
        }
    }

    /**
     * 时差不超过2秒
     * 点击两次返回键退出程序
     */
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mExitTimes > 2000) {
            mExitTimes = System.currentTimeMillis();
            Toast normal = XToast.normal(this, "再次点击返回键,退出信息页面!");
            normal.setGravity(Gravity.CENTER, 0, 0);
            normal.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }
}
