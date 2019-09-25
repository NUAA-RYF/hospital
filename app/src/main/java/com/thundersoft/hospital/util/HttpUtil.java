package com.thundersoft.hospital.util;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import static com.thundersoft.hospital.util.HttpUrl.*;

public class HttpUtil extends OkHttpClient {

    private static final String TAG = "HttpUtil";


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
