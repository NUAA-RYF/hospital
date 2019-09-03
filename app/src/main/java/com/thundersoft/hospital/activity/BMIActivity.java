package com.thundersoft.hospital.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.thundersoft.hospital.R;
import com.thundersoft.hospital.model.BMI;
import com.thundersoft.hospital.util.HttpUtil;
import com.thundersoft.hospital.util.LoadJsonUtil;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.edittext.ClearEditText;
import com.xuexiang.xui.widget.popupwindow.bar.CookieBar;
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
import okhttp3.Response;

public class BMIActivity extends AppCompatActivity {

    private static final String KEY = "27e281130003dee5";

    private static final int UPDATE = 1;

    @BindView(R.id.bmi_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.bmi_height)
    ClearEditText mHeight;
    @BindView(R.id.bmi_weight)
    ClearEditText mWeight;
    @BindView(R.id.bmi_gender)
    MaterialSpinner mGender;
    @BindView(R.id.bmi_submit)
    SuperButton mSubmit;

    private Context mContext;

    private Activity mActivity;

    private List<String> mGenderList;

    private MiniLoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        ButterKnife.bind(this);

        //初始化数据
        initData();
        //初始化控件
        initControls();
    }


    /**
     * 初始化数据
     * 性别列表初始化
     */
    private void initData() {
        ActivityController.addActivity(this);
        mContext = this;
        mActivity = this;
        mGenderList = new ArrayList<>();
        mGenderList.add("请选择性别");
        mGenderList.add("男");
        mGenderList.add("女");
    }

    /**
     * 初始化控件
     * 标题栏
     */
    private void initControls() {
        //标题栏
        StatusBarUtils.translucent(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("体脂健康器");
        }

        //性别下拉列表
        mGender.setItems(mGenderList);

        //提交按钮
        mSubmit.setOnClickListener(view -> {
            String height = String.valueOf(mHeight.getText());
            String weight = String.valueOf(mWeight.getText());
            String gender = String.valueOf(mGender.getText());
            Map<String, String> isAvailable = infoIsAvailable(height, weight, gender);

            if (Objects.requireNonNull(isAvailable.get("type")).equals("success")) {
                showMiniDialog();
                //从网络获取数据
                String address = "https://api.jisuapi.com/weight/bmi?appkey=" + KEY
                        + "&sex=" + gender
                        + "&height=" + height
                        + "&weight=" + weight;

                HttpUtil.sendOkHttpRequest(address, new Callback() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Toast error = XToast.error(mContext, "网络请求失败,请检查网络连接!");
                        error.setGravity(Gravity.CENTER, 0, 0);
                        error.show();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseText = Objects.requireNonNull(response.body()).string();
                        BMI bmi = LoadJsonUtil.BMI(responseText);

                        if (bmi != null) {
                            //身体健康信息
                            String healthMessage = "体脂BMI: " + bmi.getResult().getBmi() + "\n" +
                                    "正常体脂范围: " + bmi.getResult().getNormbmi() + "\n" +
                                    "理想体重KG: " + bmi.getResult().getIdealweight() + "\n" +
                                    "健康程度: " + bmi.getResult().getLevel() + "\n" +
                                    "发病危险性: " + bmi.getResult().getDanger();
                            Message msg = new Message();
                            msg.what = UPDATE;
                            msg.obj = healthMessage;
                            mHandler.sendMessage(msg);
                        }
                    }
                });
            }else {
                Toast warning = XToast.warning(mContext, Objects.requireNonNull(isAvailable.get("msg")));
                warning.setGravity(Gravity.CENTER,0,0);
                warning.show();
            }
        });
    }

    /**
     * 验证身高,体重,性别是否不为空
     *
     * @param height 身高
     * @param weight 体重
     * @param gender 性别
     * @return 若不为空, 则返回true
     */
    @SuppressLint("CheckResult")
    private Map<String,String> infoIsAvailable(String height, String weight, String gender) {
        Map<String,String> ret = new HashMap<>();
        /*
        身高不为空
        且仅能为数字
         */
        if (TextUtils.isEmpty(height)) {
            ret.put("type","error");
            ret.put("msg","身高不能为空!");
            return ret;
        }else {
            for (int i = 0; i < height.length(); i++) {
                char a = height.charAt(i);
                if (a > '9' || a < '0') {
                    ret.put("type","error");
                    ret.put("msg","输入身高仅能为数字!");
                    return ret;
                }
            }
        }

        /*
        体重不为空
        且仅能为数字
         */
        if (TextUtils.isEmpty(weight)) {
            ret.put("type","error");
            ret.put("msg","体重不能为空!");
            return ret;
        }else {
            for (int i = 0; i < weight.length(); i++) {
                char a = weight.charAt(i);
                if (a > '9' || a < '0') {
                    ret.put("type","error");
                    ret.put("msg","输入体重仅能为数字!");
                    return ret;
                }
            }
        }
        /*
        性别不为空
         */
        if (gender.equals("请选择性别")) {
            ret.put("type","error");
            ret.put("msg","请选择性别!");
            return ret;
        }

        ret.put("type","success");
        return ret;
    }


    /**
     * 异步处理更新UI
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == UPDATE) {
                updateMiniDialog();
                closeMiniDialog();
                String healthMessage = String.valueOf(msg.obj);
                //顶部显示信息
                CookieBar.builder(mActivity)
                        .setBackgroundColor(R.color.xui_config_color_red)
                        .setTitle("健康水平")
                        .setDuration(20000)
                        .setLayoutGravity(Gravity.TOP)
                        .setMessage(healthMessage)
                        .setAction("关闭", view -> {
                        })
                        .show();
            }
        }
    };
    /**
     * 显示加载提示框
     */
    private void showMiniDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = WidgetUtils.getMiniLoadingDialog(mContext, "正在加载数据...");
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.show();
        }else {
            mLoadingDialog.updateMessage("正在加载数据...");
            mLoadingDialog.show();
        }
    }

    /**
     * 关闭加载提示框
     */
    private void closeMiniDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 更新加载提示框信息
     */
    private void updateMiniDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.updateMessage("数据加载完成!");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }
}
