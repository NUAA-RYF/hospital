package com.thundersoft.hospital.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil extends OkHttpClient {



    /**
     * 使用第三方库OkHttp3
     * 发送网络请求
     * @param address  请求的网络地址
     * @param callback 响应
     */
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        new Thread(() -> {
            OkHttpClient mClient = new OkHttpClient();
            Request mRequest = new Request.Builder().url(address).build();
            mClient.newCall(mRequest).enqueue(callback);
        }).start();
    }

    /**
     * 使用Post方式提交数据
     * @param address     地址
     * @param requestBody Post内容
     * @param callback    响应
     */
    public static void doPostRequest(String address, RequestBody requestBody,okhttp3.Callback callback){
        new Thread(()->{
            OkHttpClient mClient = new OkHttpClient();
            Request mRequest = new Request.Builder().url(address).post(requestBody).build();
            mClient.newCall(mRequest).enqueue(callback);
        }).start();
    }
}
