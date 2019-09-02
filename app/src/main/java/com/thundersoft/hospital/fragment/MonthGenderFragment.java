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
import com.thundersoft.hospital.model.MonthGender;
import com.thundersoft.hospital.util.HttpUtil;
import com.thundersoft.hospital.util.LoadJsonUtil;
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

public class MonthGenderFragment extends Fragment {


    private static final String KEY = "27e281130003dee5";

    private static final int SHOW = 1;

    @BindView(R.id.month_gender_age)
    ClearEditText mAge;

    @BindView(R.id.month_gender_submit)
    SuperButton mSubmit;
    @BindView(R.id.month_gender_month)
    MaterialSpinner mMonth;

    private View rootView;

    private Context mContext;

    private Activity mActivity;

    public static MonthGenderFragment newInstance() {
        MonthGenderFragment fragment = new MonthGenderFragment();
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
            rootView = inflater.inflate(R.layout.fragment_month_gender, container, false);
        }
        ButterKnife.bind(this, rootView);

        //初始化数据
        initData();

        //初始化控件
        initControls();
        return rootView;
    }


    private void initData() {
        //月份列表
        List<String> month = new ArrayList<>();
        month.add("请选择月份");
        for (int i = 1; i < 13; i++) {
            month.add(i + "月");
        }
        mMonth.setItems(month);
    }

    private void initControls() {
        mSubmit.setOnClickListener(view -> {
            String age = String.valueOf(mAge.getText());
            String month = String.valueOf(mMonth.getText());
            Map<String, String> available = isInputAvailable(age, month);
            if (Objects.requireNonNull(available.get("type")).equals("success")) {
                //请求地址
                String address = "https://api.jisuapi.com/snsn/sex?appkey=" + KEY +
                        "&age=" + age +
                        "&month=" + month;

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
                        MonthGender monthGender = LoadJsonUtil.MonthGender(responseText);
                        if (monthGender != null) {

                            //获取信息并且异步更新UI
                            String monthGenderMessage = "通过年龄和怀孕月份判断男女，也可以通过男女来选择怀孕月份。" + "\n" +
                                    "仅供娱乐，据说很准哦。" + "\n" +
                                    "孩子的性别可能会是" + monthGender.getResult().getSex() + "孩喔!";
                            Message msg = new Message();
                            msg.what = SHOW;
                            msg.obj = monthGenderMessage;
                            mHandler.sendMessage(msg);
                        }
                    }
                });
            }else {
                //输入不合法
                Toast warning = XToast.warning(mContext, Objects.requireNonNull(available.get("msg")));
                warning.setGravity(Gravity.CENTER,0,0);
                warning.show();
            }
        });
    }

    /**
     * 验证年龄和月份是否合法
     * @param age 年龄
     * @param month 月份
     * @return 成功返回true
     */
    private Map<String,String> isInputAvailable(String age, String month) {
        Map<String,String> ret = new HashMap<>();
        if (TextUtils.isEmpty(age)) {
            ret.put("type","error");
            ret.put("msg","请输入年龄!");
            return ret;
        }else {
            for (int i = 0; i < age.length(); i++) {
                char a = age.charAt(i);
                if (a > '9' || a < '0') {
                    ret.put("type","error");
                    ret.put("msg","输入年龄仅能为数字!");
                    return ret;
                }
            }
        }
        if (Integer.parseInt(age) < 18 || Integer.parseInt(age) > 45){

            ret.put("type","error");
            ret.put("msg","年龄要不小于18岁且不大于45岁喔!");
            return ret;
        }
        if (month.equals("请选择月份")) {
            ret.put("type","error");
            ret.put("msg","请选择月份");
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
            if (msg.what == SHOW) {
                String monthGenderMessage = String.valueOf(msg.obj);
                //顶部显示信息
                CookieBar.builder(mActivity)
                        .setBackgroundColor(R.color.xui_config_color_red)
                        .setTitle("生男生女计算表")
                        .setDuration(20000)
                        .setLayoutGravity(Gravity.TOP)
                        .setMessage(monthGenderMessage)
                        .setAction("关闭", view -> {
                        })
                        .show();
            }
        }
    };
}
