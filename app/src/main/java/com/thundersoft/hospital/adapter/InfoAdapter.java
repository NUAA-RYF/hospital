package com.thundersoft.hospital.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.thundersoft.hospital.R;
import com.thundersoft.hospital.activity.EditIllnessActivity;
import com.thundersoft.hospital.model.Disease;
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
import okhttp3.Response;

import static com.thundersoft.hospital.util.HttpUrl.DISEASE_DELETE;
import static com.thundersoft.hospital.util.HttpUrl.HOSPITAL;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {


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

        //一键急救
        holder.mFirstAid.setOnClickListener(view -> {
            Toast firstAid = XToast.info(mContext, "一键急救!");
            firstAid.setGravity(Gravity.CENTER, 0, 0);
            firstAid.show();
        });
    }

    private void deleteDiseaseFromServer(String id){
        String address = HOSPITAL + DISEASE_DELETE + "?id=" + id;
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast warning = XToast.warning(mContext, "删除失败,请检查网络!");
                warning.setGravity(Gravity.CENTER, 0, 0);
                warning.show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = Objects.requireNonNull(response.body()).string();
                boolean result = LoadJsonUtil.deleteInfo(responseText);
                if (result){
                    Toast success = XToast.success(mContext, "删除成功!");
                    success.setGravity(Gravity.CENTER, 0, 0);
                    success.show();
                    //发送数据改变广播
                    Intent intent = new Intent("com.thundersoft.hospital.broadcast.DATA_CHANGE");
                    mContext.sendBroadcast(intent);
                }else {
                    Toast warning = XToast.warning(mContext, "删除失败!");
                    warning.setGravity(Gravity.CENTER, 0, 0);
                    warning.show();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return mDiseaseList.size();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder{
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

}
