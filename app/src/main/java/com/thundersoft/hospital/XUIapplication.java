package com.thundersoft.hospital;

import com.xuexiang.xui.XUI;

import org.litepal.LitePalApplication;

public class XUIapplication extends LitePalApplication {

    @Override
    public void onCreate() {
        XUI.init(this);
        XUI.debug(true);
        super.onCreate();
    }

}
