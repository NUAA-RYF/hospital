package com.thundersoft.hospital.adapter;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.thundersoft.hospital.R;
import com.thundersoft.hospital.model.FirstAid;
import com.thundersoft.hospital.util.HttpUrl;
import com.thundersoft.hospital.util.HttpUtil;
import com.thundersoft.hospital.util.LoadJsonUtil;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;
import com.xuexiang.xui.widget.toast.XToast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.thundersoft.hospital.util.HttpUrl.*;

public class FirstAidAdapter extends RecyclerView.Adapter<FirstAidAdapter.ViewHolder> {

    private static final int CONNECT_FAILED = 1;

    private static final int DELETE_SUCCESS = 2;

    private static final int DELETE_FAILED = 3;

    private List<FirstAid> mFirstAidList;

    private Context mContext;

    public FirstAidAdapter(List<FirstAid> firstAidList) {
        mFirstAidList = firstAidList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.aid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirstAid mFirstAid = mFirstAidList.get(position);
        holder.mDiseaseName.setText(mFirstAid.getDiseaseName());
        if (mFirstAid.getState() == 0){
            holder.mHandle.setText("等待处理");
        }
        if (mFirstAid.getState() == 1){
            holder.mHandle.setText("正在处理");
        }
        if (mFirstAid.getState() == 2){
            holder.mHandle.setText("处理结束");
        }

        holder.mDelete.setOnClickListener(view -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
            mBuilder.setTitle("正在删除")
                    .setMessage("是否删除 "+mFirstAid.getDiseaseName()+" 的急救申请?")
                    .setNegativeButton("取消",(dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("删除",(dialogInterface, i) -> {
                        //删除一键急救的申请
                        String address = HOSPITAL + FIRST_AID_DELETE;
                        RequestBody formBody = new FormBody.Builder()
                                .add("id", String.valueOf(mFirstAid.getId()))
                                .build();
                        deleteFirstAidFromServer(address,formBody);
                        dialogInterface.dismiss();
                    }).show();
        });
    }

    private void deleteFirstAidFromServer(String address,RequestBody formBody){
        HttpUtil.doPostRequest(address, formBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = new Message();
                message.what = CONNECT_FAILED;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = Objects.requireNonNull(response.body()).string();
                boolean result = LoadJsonUtil.messageIsSuccess(responseText);
                Message message = new Message();
                if (result){
                    message.what = DELETE_SUCCESS;
                }else {
                    message.what = DELETE_FAILED;
                }
                mHandler.sendMessage(message);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mFirstAidList.size();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.aid_diseaseName)
        TextView mDiseaseName;
        @BindView(R.id.aid_delete)
        ImageView mDelete;
        @BindView(R.id.aid_handle)
        SuperButton mHandle;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case CONNECT_FAILED:
                    XToast.warning(mContext,"连接失败,请检查网络!");
                    break;
                case DELETE_SUCCESS:
                    //发送急救数据改变广播
                    Intent mFriendChangeBroadcast = new Intent("com.thundersoft.hospital.broadcast.FirstAid_CHANGE");
                    mContext.sendBroadcast(mFriendChangeBroadcast);
                    break;
                case DELETE_FAILED:
                    XToast.error(mContext,"数据删除失败!");
                    break;
            }
        }
    };
}
