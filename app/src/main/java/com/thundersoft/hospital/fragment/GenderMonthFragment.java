package com.thundersoft.hospital.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.thundersoft.hospital.R;
import com.thundersoft.hospital.model.GenderMonth;
import com.thundersoft.hospital.util.HttpUtil;
import com.thundersoft.hospital.util.LoadJsonUtil;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xui.widget.edittext.materialedittext.validation.RegexpValidator;
import com.xuexiang.xui.widget.popupwindow.bar.CookieBar;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;
import com.xuexiang.xui.widget.toast.XToast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class GenderMonthFragment extends Fragment {


    private static final String KEY = "27e281130003dee5";

    private static final int SHOW = 1;

    @BindView(R.id.gender_month_age)
    MaterialEditText mAge;
    @BindView(R.id.gender_month_gender)
    MaterialSpinner mGender;
    @BindView(R.id.gender_month_submit)
    SuperButton mSubmit;

    private View rootView;

    private Context mContext;

    private Activity mActivity;

    private List<String> mGenderList;

    public static GenderMonthFragment newInstance() {
        GenderMonthFragment fragment = new GenderMonthFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_gender_month, container, false);
        }
        ButterKnife.bind(this, rootView);

        //初始化数据
        initData();
        //初始化控件
        initControls();
        return rootView;
    }


    private void initData() {
        mGenderList = new ArrayList<>();
        mGenderList.add("请选择性别");
        mGenderList.add("男");
        mGenderList.add("女");
    }

    private void initControls() {
        //性别列表
        mGender.setItems(mGenderList);

        //年龄输入框只可为数字
        mAge.addValidator(new RegexpValidator("只能输入数字!", "\\d+"));

        //提交
        mSubmit.setOnClickListener(view -> {
            //获取年龄和性别并且验证是否合法
            String age = String.valueOf(mAge.getText());
            String gender = String.valueOf(mGender.getText());
            boolean result = isInputAvailable(age, gender);

            //若合法
            if (result) {
                String address = "https://api.jisuapi.com/snsn/month?appkey=" + KEY +
                        "&age=" + age +
                        "&sex=" + gender;

                HttpUtil.sendOkHttpRequest(address, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Toast error = XToast.error(mContext, "网络请求失败,请检查网络连接!");
                        error.setGravity(Gravity.CENTER, 0, 0);
                        error.show();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseText = Objects.requireNonNull(response.body()).string();
                        GenderMonth genderMonth = LoadJsonUtil.GenderMonth(responseText);

                        if (genderMonth != null) {
                            //获取今年和明年建议怀孕的月份
                            StringBuilder thisYear = new StringBuilder();
                            for (String month : genderMonth.getResult().getThisyear()) {
                                thisYear.append(month).append("月 ");
                            }
                            StringBuilder nextYear = new StringBuilder();
                            for (String month : genderMonth.getResult().getNextyear()) {
                                nextYear.append(month).append("月 ");
                            }

                            //最终信息
                            String info = "通过年龄和怀孕月份判断男女，也可以通过男女来选择怀孕月份。" + "\n" +
                                    "仅供娱乐，据说很准哦。" + "\n" +
                                    "今年月份: " +
                                    thisYear.toString() + "\n" +
                                    "明年月份: " +
                                    nextYear.toString();

                            Message message = new Message();
                            message.what = SHOW;
                            message.obj = info;
                            mHandler.sendMessage(message);
                        }
                    }
                });
            }
        });
    }

    /**
     * 年龄和性别验证
     * @param age    年龄
     * @param gender 性别
     * @return 成功返回true
     */
    private boolean isInputAvailable(String age, String gender) {
        if (TextUtils.isEmpty(age)) {
            Toast error = XToast.error(mContext, "请输入年龄!");
            error.setGravity(Gravity.CENTER, 0, 0);
            error.show();
            return false;
        } else {
            for (int i = 0; i < age.length(); i++) {
                char a = age.charAt(i);
                if (a > '9' || a < '0') {
                    Toast error = XToast.error(mContext, "输入年龄仅能为数字!");
                    error.setGravity(Gravity.CENTER, 0, 0);
                    error.show();
                    return false;
                }
            }
        }
        if (Integer.parseInt(age) < 18 || Integer.parseInt(age) > 45){
            Toast error = XToast.error(mContext, "年龄要不小于18岁且不大于45岁喔!");
            error.setGravity(Gravity.CENTER, 0, 0);
            error.show();
            return false;
        }
        if (gender.equals("请选择性别")) {
            Toast error = XToast.error(mContext, "请选择性别!");
            error.setGravity(Gravity.CENTER, 0, 0);
            error.show();
            return false;
        }
        return true;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == SHOW) {
                String info = String.valueOf(msg.obj);
                //顶部显示信息
                CookieBar.builder(mActivity)
                        .setBackgroundColor(R.color.xui_config_color_red)
                        .setTitle("建议怀孕月份")
                        .setDuration(20000)
                        .setLayoutGravity(Gravity.TOP)
                        .setMessage(info)
                        .setAction("关闭", view -> {
                        })
                        .show();
            }
        }
    };
}
