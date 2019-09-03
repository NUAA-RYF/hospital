package com.thundersoft.hospital.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.MyLocationStyle;
import com.thundersoft.hospital.R;
import com.xuexiang.xui.utils.StatusBarUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends AppCompatActivity {


    @BindView(R.id.map_mapView)
    MapView mMapView;

    private AMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        ActivityController.addActivity(this);

        mMapView.onCreate(savedInstanceState);

        initMap();
    }

    /**
     * 初始化高德地图
     */
    private void initMap() {
        //沉浸式状态栏
        StatusBarUtils.translucent(this);

        //地图初始化及定位点显示
        if (mMap == null) {
            mMap = mMapView.getMap();

            //UI控件交互,定位控件,比例尺控件
            UiSettings uiSettings = mMap.getUiSettings();
            uiSettings.setMyLocationButtonEnabled(true);
            uiSettings.setScaleControlsEnabled(true);


            //定位形式,显示位置,刷新时长5000MS,跟随模式
            MyLocationStyle locationStyle = new MyLocationStyle();
            locationStyle.showMyLocation(true)
                    .myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);

            //地图显示形式及是否显示自己
            mMap.setMyLocationEnabled(true);
            mMap.setMyLocationStyle(locationStyle);
            //地图缩放级别17级,离地面越近,缩放级别越高,最大20级(10M)
            mMap.moveCamera(CameraUpdateFactory.zoomTo(17));

        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        ActivityController.removeActivity(this);
    }
}
