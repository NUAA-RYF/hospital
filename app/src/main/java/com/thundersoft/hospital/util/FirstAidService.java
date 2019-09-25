package com.thundersoft.hospital.util;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.thundersoft.hospital.model.User;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import static com.thundersoft.hospital.util.HttpUrl.HOSPITAL;

public class FirstAidService extends Service {
    private static final String TAG = "FirstAidService";

    private User mUser;

    private WebSocket mWebSocket;

    private sendFirstAidReceiver mSendFirstAidReceiver;

    public FirstAidService(User user) {
        this.mUser = user;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle userBundle = intent.getBundleExtra("user");
        if (userBundle != null) {
            mUser = (User) userBundle.get("user");
        }else {
            Log.e(TAG, "getUserInfoFromIntent: 用户信息为空!");
        }
        registReceiver();
        webSocket(mUser.getUserName());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegistReceiver();
    }

    class sendFirstAidReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            mWebSocket.send("申请急救");
        }
    }

    /**
     * 注册广播
     */
    private void registReceiver(){
        if (mSendFirstAidReceiver == null){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.thundersoft.hospital.RECEIVE_AID");
            mSendFirstAidReceiver = new sendFirstAidReceiver();
            registerReceiver(mSendFirstAidReceiver, intentFilter);
        }
    }

    /**
     * 注销广播
     */
    private void unRegistReceiver(){
        if (mSendFirstAidReceiver != null){
            unregisterReceiver(mSendFirstAidReceiver);
        }
    }

    private void webSocket(String username){
        String address = HOSPITAL + username;
        OkHttpClient mClient = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        mClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                super.onMessage(webSocket, text);
                //发送急救数据改变广播
                Intent mFriendChangeBroadcast = new Intent("com.thundersoft.hospital.broadcast.FirstAid_CHANGE");
                sendBroadcast(mFriendChangeBroadcast);
            }

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                mWebSocket = webSocket;
            }
        });
    }
}
