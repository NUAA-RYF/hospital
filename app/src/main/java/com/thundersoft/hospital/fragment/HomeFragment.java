package com.thundersoft.hospital.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thundersoft.hospital.R;
import com.thundersoft.hospital.adapter.ConstellationAdapter;
import com.thundersoft.hospital.adapter.SolarTermsAdapter;
import com.thundersoft.hospital.model.Constellation;
import com.thundersoft.hospital.model.Solarterm;
import com.thundersoft.hospital.util.HttpUtil;
import com.thundersoft.hospital.util.LoadJsonUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import org.jetbrains.annotations.NotNull;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private static final String KEY = "27e281130003dee5";

    private static final int QUERY_CONSTELLATION = 1;

    private static final int QUERY_SOLARTERMS = 2;

    @BindView(R.id.home_banner)
    Banner mBanner;
    @BindView(R.id.home_constellation)
    RecyclerView mConstellationView;
    @BindView(R.id.home_solarTerms)
    RecyclerView mSolarTermsView;

    private View rootView;

    private static final String PICTURE1 = "https://i.loli.net/2019/08/20/xhQvSf1FPG5iwa4.png";

    private static final String PICTURE2 = "https://i.loli.net/2019/08/20/OXWFDMsQu7dRlb2.png";

    private static final String PICTURE3 = "https://i.loli.net/2019/08/20/HV9Je6i2kSmBwbO.png";

    private List<Constellation> mConstellationList;

    private List<Solarterm> mSolartermList;

    private ConstellationAdapter mConstellationAdapter;

    private SolarTermsAdapter mSolarTermsAdapter;

    private Context mContext;

    private ProgressDialog mLoadingDialog;

    private boolean isDialogOpen;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        }
        mContext = getContext();
        ButterKnife.bind(this, rootView);

        //初始化数据
        initData();

        //加载轮播图
        initBanner();

        //加载适配器
        initAdapter();

        //加载星座和二十四节气数据
        queryDate();


        return rootView;
    }



    /**
     * 初始化数据
     * 星座列表
     * 节气列表
     */
    private void initData() {
        mConstellationList = DataSupport.findAll(Constellation.class);
        mSolartermList = DataSupport.findAll(Solarterm.class);
        isDialogOpen = false;
    }

    /**
     * 查询星座和节气
     */
    private void queryDate(){
        querySolarTerms();
        queryConstellation();
    }

    /**
     * 查询二十四节气
     */
    private void querySolarTerms() {
        mSolartermList.clear();
        mSolartermList.addAll(DataSupport.findAll(Solarterm.class));
        if (mSolartermList.size() <= 0) {
            String address = "https://api.jisuapi.com/jieqi/query?appkey=" + KEY + "&year=";
            queryFromServer(address, QUERY_SOLARTERMS);
        } else {
            mSolarTermsAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 查询星座
     */
    private void queryConstellation() {
        mConstellationList.clear();
        mConstellationList.addAll(DataSupport.limit(12).find(Constellation.class));
        if (mConstellationList.size() <= 0) {
            String address = "https://api.jisuapi.com/astro/all?appkey=" + KEY;
            queryFromServer(address, QUERY_CONSTELLATION);
        } else {
            mConstellationAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 从网络加载数据
     * 加载星座Constellation
     * 加载二十四节气SolarTerms
     * 显示和关闭加载提示框
     * @param address 请求的网络地址
     * @param Type    请求加载的数据种类
     */
    private void queryFromServer(String address, int Type) {
        if (!isDialogOpen){
            isDialogOpen = true;
            showProgressDialog();
        }
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mLoadingDialog.setMessage("加载数据失败!");
                mLoadingDialog.setCancelable(true);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = Objects.requireNonNull(response.body()).string();
                boolean result = false;
                switch (Type) {
                    case QUERY_CONSTELLATION:
                        result = LoadJsonUtil.Constellation(responseText);
                        break;
                    case QUERY_SOLARTERMS:
                        result = LoadJsonUtil.SolarTerm(responseText);
                        break;
                    default:
                        break;
                }

                if (result) {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        closeProgressDialog();
                        switch (Type) {
                            case QUERY_CONSTELLATION:
                                queryDate();
                                break;
                            case QUERY_SOLARTERMS:
                                queryDate();
                                break;
                            default:
                                break;
                        }
                    });
                }
            }
        });
    }

    /**
     * 加载适配器
     * 十二星座适配器 网格布局3列
     * 二十四节气适配器 网格布局2列
     */
    private void initAdapter() {
        //十二星座适配器
        mConstellationAdapter = new ConstellationAdapter(mConstellationList);
        GridLayoutManager mGridConstellation = new GridLayoutManager(mContext, 3);
        mConstellationView.setLayoutManager(mGridConstellation);
        mConstellationView.setAdapter(mConstellationAdapter);

        //二十四节气适配器
        mSolarTermsAdapter = new SolarTermsAdapter(mSolartermList);
        GridLayoutManager mGridSolarTerms = new GridLayoutManager(mContext,2);
        mSolarTermsView.setLayoutManager(mGridSolarTerms);
        mSolarTermsView.setAdapter(mSolarTermsAdapter);
    }

    /**
     * 加载轮播图
     * 轮播图用Glide加载图片URL地址
     */
    private void initBanner() {
        List<String> url = new ArrayList<>();
        url.add(PICTURE1);
        url.add(PICTURE2);
        url.add(PICTURE3);

        mBanner.setImages(url)                                      //图片URL路径
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR)      //指示器形式
                .setDelayTime(2000)                                 //轮播间隔时间
                .setImageLoader(new GlideImageLoader())             //图片加载方式
                .isAutoPlay(true)                                   //是否自动轮播
                .setIndicatorGravity(BannerConfig.RIGHT)            //指示器位置
                .setBannerAnimation(Transformer.Default)            //图片轮播动画
                .start();
    }

    /**
     * 图片加载方式
     * 使用Glide方式加载图片
     */
    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            //Glide 加载图片简单用法
            Glide.with(context)
                    .load(path)
                    .into(imageView);
        }
    }

    /**
     * 轮播图开始自动轮播
     */
    @Override
    public void onStart() {
        super.onStart();
        mBanner.startAutoPlay();
    }

    /**
     * 轮播图停止自动轮播
     */
    @Override
    public void onStop() {
        super.onStop();
        mBanner.stopAutoPlay();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * 打开加载提示框
     */
    private void showProgressDialog(){
        if (mLoadingDialog == null){
            mLoadingDialog = new ProgressDialog(getActivity());
            mLoadingDialog.setMessage("正在加载数据...");
            mLoadingDialog.setCancelable(false);
        }
        mLoadingDialog.show();
    }

    /**
     * 关闭加载提示框
     */
    private void closeProgressDialog(){
        if (mLoadingDialog != null){
            mLoadingDialog.dismiss();
        }
    }

}
