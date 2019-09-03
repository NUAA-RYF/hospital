package com.thundersoft.hospital.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.thundersoft.hospital.R;
import com.thundersoft.hospital.model.gson.Fortune.Fortune;
import com.thundersoft.hospital.util.HttpUtil;
import com.thundersoft.hospital.util.LoadJsonUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FortuneActivity extends AppCompatActivity {

    private static final String KEY = "27e281130003dee5";

    @BindView(R.id.fortune_img)
    ImageView fortuneImg;
    @BindView(R.id.fortune_toolbar)
    Toolbar fortuneToolbar;
    @BindView(R.id.fortune_collapsing)
    CollapsingToolbarLayout fortuneCollapsing;
    @BindView(R.id.fortune_today_date)
    TextView fortuneTodayDate;
    @BindView(R.id.fortune_today_presummary)
    TextView fortuneTodayPresummary;
    @BindView(R.id.fortune_today_star)
    TextView fortuneTodayStar;
    @BindView(R.id.fortune_today_color)
    TextView fortuneTodayColor;
    @BindView(R.id.fortune_today_number)
    TextView fortuneTodayNumber;
    @BindView(R.id.fortune_today_summary)
    TextView fortuneTodaySummary;
    @BindView(R.id.fortune_today_money)
    TextView fortuneTodayMoney;
    @BindView(R.id.fortune_today_career)
    TextView fortuneTodayCareer;
    @BindView(R.id.fortune_today_love)
    TextView fortuneTodayLove;
    @BindView(R.id.fortune_today_health)
    TextView fortuneTodayHealth;
    @BindView(R.id.fortune_week_date)
    TextView fortuneWeekDate;
    @BindView(R.id.fortune_week_money)
    TextView fortuneWeekMoney;
    @BindView(R.id.fortune_week_career)
    TextView fortuneWeekCareer;
    @BindView(R.id.fortune_week_love)
    TextView fortuneWeekLove;
    @BindView(R.id.fortune_week_health)
    TextView fortuneWeekHealth;
    @BindView(R.id.fortune_week_job)
    TextView fortuneWeekJob;
    @BindView(R.id.fortune_month_date)
    TextView fortuneMonthDate;
    @BindView(R.id.fortune_month_summary)
    TextView fortuneMonthSummary;
    @BindView(R.id.fortune_month_money)
    TextView fortuneMonthMoney;
    @BindView(R.id.fortune_month_career)
    TextView fortuneMonthCareer;
    @BindView(R.id.fortune_month_love)
    TextView fortuneMonthLove;
    @BindView(R.id.fortune_year_date)
    TextView fortuneYearDate;
    @BindView(R.id.fortune_year_summary)
    TextView fortuneYearSummary;
    @BindView(R.id.fortune_year_money)
    TextView fortuneYearMoney;
    @BindView(R.id.fortune_year_career)
    TextView fortuneYearCareer;
    @BindView(R.id.fortune_year_love)
    TextView fortuneYearLove;


    private String fortuneId;

    private Fortune mFortune;

    private String imgUrl;

    private ProgressDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fortune);
        ButterKnife.bind(this);

        initData();

        queryFortuneFromServer();
    }

    /**
     * 初始化数据
     * 星座ID
     * 星座图片URL
     */
    private void initData() {
        ActivityController.addActivity(this);
        fortuneId = getIntent().getStringExtra("fortuneId");
        imgUrl = getIntent().getStringExtra("imgUrl");
    }

    /**
     * 从网络加载星座运势信息
     */
    private void queryFortuneFromServer() {
        showProgressDialog();
        String address = "https://api.jisuapi.com/astro/fortune?astroid=" + fortuneId + "&date=&appkey=" + KEY;
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = Objects.requireNonNull(response.body()).string();
                mFortune = LoadJsonUtil.Fortune(responseText);
                if (mFortune != null) {
                    runOnUiThread(() -> {
                        closeProgressDialog();
                        initControls();
                    });
                }
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initControls() {
        //标题栏
        setSupportActionBar(fortuneToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (mFortune == null) return;
        //加载图片和星座名称
        Glide.with(this).load(imgUrl).into(fortuneImg);
        fortuneCollapsing.setTitle(mFortune.getResult().getAstroname());

        //Today
        Fortune.ResultBean.TodayBean todayBean = mFortune.getResult().getToday();
        fortuneTodayDate.setText(todayBean.getDate());
        fortuneTodayPresummary.setText(String.format("    %s", todayBean.getPresummary()));//概述
        fortuneTodayStar.setText(String.format("贵人星座:    %s", todayBean.getStar()));//贵人星座
        fortuneTodayColor.setText(String.format("幸运颜色:    %s", todayBean.getColor()));//幸运颜色
        fortuneTodayNumber.setText(String.format("幸运数字:    %s", todayBean.getNumber()));//幸运数字
        fortuneTodaySummary.setText(String.format("综合运势:    %s", todayBean.getSummary()));//综合运势
        fortuneTodayMoney.setText(String.format("财运运势:    %s", todayBean.getMoney()));//财运运势
        fortuneTodayCareer.setText(String.format("工作运势:    %s", todayBean.getCareer()));//工作运势
        fortuneTodayLove.setText(String.format("爱情运势:    %s", todayBean.getLove()));//爱情运势
        fortuneTodayHealth.setText(String.format("健康运势:    %s", todayBean.getHealth()));//健康运势

        //Week
        Fortune.ResultBean.WeekBean weekBean = mFortune.getResult().getWeek();
        fortuneWeekDate.setText(weekBean.getDate());
        fortuneWeekMoney.setText(String.format("财运运势:    %s", weekBean.getMoney()));
        fortuneWeekCareer.setText(String.format("工作运势:    %s", weekBean.getCareer()));
        fortuneWeekLove.setText(String.format("爱情运势:    %s", weekBean.getLove()));
        fortuneWeekHealth.setText(String.format("健康运势:    %s", weekBean.getHealth()));
        //fortuneWeekJob.setText(String.format("求职运:    %s", weekBean.getJob()));//求职运


        //Month
        Fortune.ResultBean.MonthBean monthBean = mFortune.getResult().getMonth();
        fortuneMonthDate.setText(monthBean.getDate());
        fortuneMonthSummary.setText(String.format("    %s", monthBean.getSummary()));
        fortuneMonthMoney.setText(String.format("财运运势:    %s", monthBean.getMoney()));
        fortuneMonthCareer.setText(String.format("工作运势:    %s", monthBean.getCareer()));
        fortuneMonthLove.setText(String.format("爱情运势:    %s", monthBean.getLove()));

        //Year
        Fortune.ResultBean.YearBean yearBean = mFortune.getResult().getYear();
        fortuneYearDate.setText(yearBean.getDate());
        fortuneYearSummary.setText(String.format("    %s", yearBean.getSummary()));
        fortuneYearMoney.setText(String.format("财运运势:    %s", yearBean.getMoney()));
        fortuneYearCareer.setText(String.format("工作运势:    %s", yearBean.getCareer()));
        fortuneYearLove.setText(String.format("爱情运势:    %s", yearBean.getLove()));
    }

    /**
     * 标题栏和菜单栏选择
     * @param item 项目
     * @return 返回true或者父类
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 打开加载提示框
     */
    private void showProgressDialog(){
        if (mLoadingDialog == null){
            mLoadingDialog = new ProgressDialog(this);
            mLoadingDialog.setMessage("正在加载数据...");
            mLoadingDialog.setCancelable(false);
        }
        mLoadingDialog.show();
    }

    /**
     * 关闭加载提示框
     */
    private void  closeProgressDialog(){
        if (mLoadingDialog != null){
            mLoadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }
}
