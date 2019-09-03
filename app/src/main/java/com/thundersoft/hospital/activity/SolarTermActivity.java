package com.thundersoft.hospital.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.thundersoft.hospital.model.JieQi;
import com.thundersoft.hospital.util.HttpUtil;
import com.thundersoft.hospital.util.LoadJsonUtil;

import org.jetbrains.annotations.NotNull;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SolarTermActivity extends AppCompatActivity {

    private static final String KEY = "27e281130003dee5";

    @BindView(R.id.solar_img)
    ImageView solarImg;
    @BindView(R.id.solar_toolbar)
    Toolbar solarToolbar;
    @BindView(R.id.solar_collapsing)
    CollapsingToolbarLayout solarCollapsing;
    @BindView(R.id.solar_card_date_text)
    TextView solarCardDateText;
    @BindView(R.id.solar_card_jianjie_text)
    TextView solarCardJianjieText;
    @BindView(R.id.solar_card_youlai_text)
    TextView solarCardYoulaiText;
    @BindView(R.id.solar_card_xisu_text)
    TextView solarCardXisuText;
    @BindView(R.id.solar_card_yangsheng_text)
    TextView solarCardYangshengText;

    private String jieqiId;

    private ProgressDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solar_term);
        ButterKnife.bind(this);

        initData();

        initControls();

        queryJieQi();
    }

    /**
     * 初始化数据
     * 获得气节ID
     */
    private void initData(){
        ActivityController.addActivity(this);
        Intent intent = getIntent();
        jieqiId = intent.getStringExtra("jieqiId");
    }


    /**
     * 查询节气
     * 若没有则从网络上加载
     */
    @SuppressLint("ResourceAsColor")
    private void queryJieQi(){
        List<JieQi> jieQiList = DataSupport.where("jieqiid=" + jieqiId).find(JieQi.class);
        if (jieQiList.size() > 0){
            JieQi mJieqi = jieQiList.get(0);
            solarCollapsing.setTitle(mJieqi.getName());
            solarCollapsing.setExpandedTitleColor(R.color.White);
            solarCardDateText.setText(String.format("今年"+mJieqi.getName()+"时间: %s", mJieqi.getDate()));
            solarCardJianjieText.setText(mJieqi.getJianjie());
            solarCardYoulaiText.setText(mJieqi.getYoulai());
            solarCardXisuText.setText(mJieqi.getXisu());
            solarCardYangshengText.setText(mJieqi.getYangsheng());
            Glide.with(this).load(mJieqi.getImgUrl()).into(solarImg);
        }else {
            String address = "https://api.jisuapi.com/jieqi/detail?appkey="
                    +KEY
                    +"&jieqiid="
                    + jieqiId
                    +"&year=";
            queryFromServer(address);
        }
    }

    /**
     * 从服务器获取数据
     * @param address 地址
     */
    private void queryFromServer(String address){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = Objects.requireNonNull(response.body()).string();
                boolean result = LoadJsonUtil.JieQi(responseText);
                if (result){
                    runOnUiThread(() -> {
                        closeProgressDialog();
                        queryJieQi();
                    });
                }
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initControls(){
        setSupportActionBar(solarToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * 菜单栏选项
     * @param item 返回选项
     * @return 返回true
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
