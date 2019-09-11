package com.thundersoft.hospital.adapter;

import android.annotation.SuppressLint;
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
import com.thundersoft.hospital.activity.EditFriendActivity;
import com.thundersoft.hospital.model.Friend;
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
import okhttp3.Response;

import static com.thundersoft.hospital.util.HttpUrl.FRIEND_DELETE;
import static com.thundersoft.hospital.util.HttpUrl.FRIEND_UPDATE_CLOSE;
import static com.thundersoft.hospital.util.HttpUrl.HOSPITAL;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private static final int CONNECTED_FAILED = 1;

    private static final int DELETE_SUCCESS = 2;

    private static final int DELETE_FAILED = 3;

    private static final int UPDATE_SUCCESS = 4;

    private static final int UPDATE_FAILED = 5;

    private List<Friend> mFriendList;

    private Context mContext;

    public FriendAdapter(List<Friend> friendList) {
        mFriendList = friendList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friend friend = mFriendList.get(position);
        holder.mName.setText(friend.getName());
        holder.mRelation.setText(friend.getRelation());
        holder.mPhone.setText(friend.getPhone());

        if (friend.isClose()) {
            holder.mClose.setText("申请关联");
        } else {
            holder.mClose.setText("解除关联");
        }
        //编辑好友信息
        holder.mEdit.setOnClickListener(view -> {
            //进入编辑好友信息页面
            Intent mEditIntent = new Intent(mContext, EditFriendActivity.class);
            mEditIntent.putExtra("handle", EditFriendActivity.CHANGE);
            mEditIntent.putExtra("friendId", friend.getId());
            mContext.startActivity(mEditIntent);
        });

        //申请关联和取消关联
        holder.mClose.setOnClickListener(view -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
            if (friend.isClose()) {
                //取消关联
                mBuilder.setTitle("是否解除与" + friend.getName() + "的关联?")
                        .setMessage("取消成功后,使用一键急救将无法通知到" + friend.getPhone() + "。是否解除?")
                        .setPositiveButton("解除关联", (dialogInterface, i) -> {
                            String address = HOSPITAL + FRIEND_UPDATE_CLOSE + "?id=" + friend.getId() + "&close=" + false;
                            updateCloseById(address);
                        })
                        .setNegativeButton("取消操作", ((dialogInterface, i) -> dialogInterface.dismiss()))
                        .show();

            } else {
                //申请关联
                mBuilder.setTitle("是否申请与" + friend.getName() + "的关联?")
                        .setMessage("申请成功后,使用一键急救将可以将通知发送到" + friend.getPhone() + "。是否关联?")
                        .setPositiveButton("申请关联", (dialogInterface, i) -> {
                            String address = HOSPITAL + FRIEND_UPDATE_CLOSE + "?id=" + friend.getId() + "&close=" + true;
                            updateCloseById(address);
                        })
                        .setNegativeButton("取消操作", ((dialogInterface, i) -> dialogInterface.dismiss()))
                        .show();
            }
        });

        //删除
        holder.mDelete.setOnClickListener(view -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
            mBuilder.setPositiveButton("删除", (dialogInterface, i) -> {
                //删除信息
                String address = HOSPITAL + FRIEND_DELETE + "?id=" + friend.getId();
                deleteFriendById(address);
                dialogInterface.dismiss();
            }).setPositiveButton("取消", ((dialogInterface, i) -> dialogInterface.dismiss()))
                    .setTitle("是否删除?")
                    .setMessage("是否删除 " + friend.getName() + "及其相关信息?")
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return mFriendList.size();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.friend_name)
        TextView mName;
        @BindView(R.id.friend_relation)
        TextView mRelation;
        @BindView(R.id.friend_phone)
        TextView mPhone;
        @BindView(R.id.friend_delete)
        ImageView mDelete;
        @BindView(R.id.friend_edit)
        SuperButton mEdit;
        @BindView(R.id.friend_close)
        SuperButton mClose;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 按ID删除好友信息
     *
     * @param address 地址
     */
    private void deleteFriendById(String address) {
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = new Message();
                message.what = CONNECTED_FAILED;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = Objects.requireNonNull(response.body()).string();
                boolean result = LoadJsonUtil.messageIsSuccess(responseText);
                Message message = new Message();
                if (result) {
                    message.what = DELETE_SUCCESS;
                } else {
                    message.what = DELETE_FAILED;
                }
                mHandler.sendMessage(message);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Intent mFriendChangeBroadcast = new Intent("com.thundersoft.hospital.broadcast.FRIEND_CHANGE");
            switch (msg.what) {
                case CONNECTED_FAILED:
                    XToast.warning(mContext, "连接失败,请检查网络!").show();
                    break;
                case DELETE_SUCCESS:
                    XToast.success(mContext, "删除成功!").show();
                    //数据改变发送广播
                    mContext.sendBroadcast(mFriendChangeBroadcast);
                    break;
                case DELETE_FAILED:
                    XToast.warning(mContext, "删除失败!").show();
                    break;
                case UPDATE_SUCCESS:
                    XToast.success(mContext, "申请成功!").show();
                    //数据改变发送广播
                    mContext.sendBroadcast(mFriendChangeBroadcast);
                    break;
                case UPDATE_FAILED:
                    XToast.warning(mContext, "申请失败!").show();
                    break;
            }
        }
    };

    /**
     * 按ID修改好友是否关联
     *
     * @param address 地址
     */
    private void updateCloseById(String address) {
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = new Message();
                message.what = CONNECTED_FAILED;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = Objects.requireNonNull(response.body()).string();
                boolean result = LoadJsonUtil.messageIsSuccess(responseText);

                Message message = new Message();
                if (result) {
                    message.what = UPDATE_SUCCESS;
                } else {
                    message.what = UPDATE_FAILED;
                }
                mHandler.sendMessage(message);
            }
        });
    }
}
