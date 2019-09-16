package com.thundersoft.hospital.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.thundersoft.hospital.R;
import com.thundersoft.hospital.activity.EditIllnessActivity;
import com.thundersoft.hospital.model.Disease;
import com.thundersoft.hospital.model.FirstAid;
import com.thundersoft.hospital.util.HttpUtil;
import com.thundersoft.hospital.util.LoadJsonUtil;
import com.xuexiang.xui.adapter.simple.XUISimpleAdapter;
import com.xuexiang.xui.widget.button.shadowbutton.RippleShadowShadowButton;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;
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
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.thundersoft.hospital.util.HttpUrl.DISEASE_DELETE;
import static com.thundersoft.hospital.util.HttpUrl.FIRST_AID_INSERT;
import static com.thundersoft.hospital.util.HttpUrl.HOSPITAL;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {

    private static final String TAG = "InfoAdapter";

    private static final int DELETE_SUCCESS = 1;

    private static final int CONNECTED_FAILED = 2;

    private static final int DELETE_FAILED = 3;

    private static final int INSERT_SUCCESS = 4;

    private List<Disease> mDiseaseList;

    private Context mContext;

    public InfoAdapter(List<Disease> diseaseList) {
        mDiseaseList = diseaseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Disease mInfo = mDiseaseList.get(position);

        holder.mIllnessName.setText(mInfo.getDiseaseName());
        holder.mName.setText(mInfo.getName());
        holder.mPhone.setText(mInfo.getPhone());
        holder.mAge.setText(mInfo.getAge());
        holder.mGender.setText(mInfo.getGender());
        holder.mAddress.setText(mInfo.getAddress());
        holder.mContent.setText(mInfo.getDiseaseInfo());

        //右上角菜单栏按钮
        holder.mMenu.setOnClickListener(view -> {
            String[] itemList = new String[]{"编辑", "删除"};
            XUISimpleAdapter mAdapter = XUISimpleAdapter.create(mContext, itemList);
            XUISimplePopup mPopupMenu = new XUISimplePopup(mContext, mAdapter);
            mPopupMenu.create((adapter, item, itemPosition) -> {
                //弹出编辑和删除操作
                String result = String.valueOf(item.getTitle());
                switch (result) {
                    case "编辑":
                        //编辑选项
                        Intent editInfo = new Intent(mContext, EditIllnessActivity.class);
                        editInfo.putExtra("handle", EditIllnessActivity.CHANGE);
                        editInfo.putExtra("illnessId", mInfo.getId());
                        mContext.startActivity(editInfo);
                        break;
                    case "删除":
                        //删除选项
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                        mBuilder.setPositiveButton("删除", (dialogInterface, i) -> {
                            //向网络发出请求删除信息
                            deleteDiseaseFromServer(String.valueOf(mInfo.getId()));
                            dialogInterface.dismiss();
                        }).setTitle("正在删除")
                                .setNegativeButton("取消", ((dialogInterface, i) -> dialogInterface.dismiss()))
                                .setMessage("是否要删除" + mInfo.getDiseaseName() + "?")
                                .show();
                        break;
                }
                mPopupMenu.dismiss();
            });
            mPopupMenu.showDown(view);
        });

        //一键急救主功能
        holder.mFirstAid.setOnClickListener(view -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
            mBuilder.setTitle("一键急救")
                    .setMessage("正在使用"+mInfo.getDiseaseName()+"请求一键急救!")
                    .setNegativeButton("取消",(dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("发出请求",(dialogInterface, i) -> {
                        //将信息传递到服务器后台和关联好友手机号
                        String currentAddress = getCurrentAddress();
                        FirstAid firstAid = new FirstAid(mInfo.getUsername(), mInfo.getName(),
                                mInfo.getAge(), mInfo.getPhone(), mInfo.getGender(), mInfo.getAddress(),
                                mInfo.getDiseaseName(), mInfo.getDiseaseInfo(), currentAddress);
                        String json = new Gson().toJson(firstAid);
                        RequestBody formBody = new FormBody.Builder()
                                .add("firstAidJson",json)
                                .build();
                        String address = HOSPITAL + FIRST_AID_INSERT;
                        insertFirstAidToServer(address,formBody);
                    }).show();
        });
    }

    /**
     * 按ID删除疾病信息
     * @param id ID
     */
    private void deleteDiseaseFromServer(String id) {
        RequestBody formBody = new FormBody.Builder()
                .add("id", id)
                .build();
        String address = HOSPITAL + DISEASE_DELETE;
        HttpUtil.doPostRequest(address, formBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //删除失败,请检查网络
                Message message = new Message();
                message.what = CONNECTED_FAILED;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = Objects.requireNonNull(response.body()).string();
                boolean result = LoadJsonUtil.deleteInfo(responseText);
                Message message = new Message();
                if (result) {
                    //删除成功
                    message.what = DELETE_SUCCESS;

                    //发送数据改变广播
                    Intent intent = new Intent("com.thundersoft.hospital.broadcast.DATA_CHANGE");
                    mContext.sendBroadcast(intent);
                } else {
                    //删除失败
                    message.what = DELETE_FAILED;
                }
                mHandler.sendMessage(message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDiseaseList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.info_illness_name)
        TextView mIllnessName;
        @BindView(R.id.info_menu)
        RippleShadowShadowButton mMenu;
        @BindView(R.id.info_name)
        TextView mName;
        @BindView(R.id.info_phone)
        TextView mPhone;
        @BindView(R.id.info_age)
        TextView mAge;
        @BindView(R.id.info_gender)
        TextView mGender;
        @BindView(R.id.info_address_input)
        TextView mAddress;
        @BindView(R.id.info_content_input)
        TextView mContent;
        @BindView(R.id.info_first_aid)
        SuperButton mFirstAid;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 处理UI控件更新
     */
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case DELETE_SUCCESS:
                    Toast delete_success = XToast.success(mContext, "删除成功!");
                    delete_success.setGravity(Gravity.CENTER, 0, 0);
                    delete_success.show();
                    break;
                case CONNECTED_FAILED:
                    Toast connected_failed = XToast.warning(mContext, "删除失败,请检查网络!");
                    connected_failed.setGravity(Gravity.CENTER, 0, 0);
                    connected_failed.show();
                    break;
                case DELETE_FAILED:
                    Toast delete_failed = XToast.warning(mContext, "删除失败!");
                    delete_failed.setGravity(Gravity.CENTER, 0, 0);
                    delete_failed.show();
                    break;
                case INSERT_SUCCESS:
                    //发送急救数据改变广播
                    Intent mFriendChangeBroadcast = new Intent("com.thundersoft.hospital.broadcast.FirstAid_CHANGE");
                    mContext.sendBroadcast(mFriendChangeBroadcast);
                    XToast.success(mContext,"申请成功!").show();
                    break;
            }
        }
    };

    /**
     * 获取当前地理位置信息
     * @return 返回地理位置
     */
    private String getCurrentAddress() {
        final String[] currentAddress = {null};
        //获取当前位置
        //定位客户端
        AMapLocationClient aMapLocationClient = new AMapLocationClient(mContext);
        /*
        高精度模式:GPS和网络获取更精确的一个
        一次定位模式:开
        返回信息:开
        超时时间:8000ms
         */
        AMapLocationClientOption aMapLocationClientOption = new AMapLocationClientOption();
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        aMapLocationClientOption.setOnceLocation(true);
        aMapLocationClientOption.setNeedAddress(true);
        aMapLocationClientOption.setHttpTimeOut(8000);
        AMapLocationListener aMapLocationListener = aMapLocation -> {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    currentAddress[0] = aMapLocation.getAddress();
                    aMapLocationClient.stopLocation();
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e(TAG, "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        };
        aMapLocationClient.setLocationOption(aMapLocationClientOption);
        aMapLocationClient.setLocationListener(aMapLocationListener);
        aMapLocationClient.startLocation();
        return currentAddress[0];
    }

    private void insertFirstAidToServer(String address,RequestBody formBody){
        HttpUtil.doPostRequest(address, formBody, new Callback() {
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
                if (result){
                    message.what = INSERT_SUCCESS;
                }
                mHandler.sendMessage(message);
            }
        });
    }
}
