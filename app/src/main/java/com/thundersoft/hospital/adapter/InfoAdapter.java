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
import com.thundersoft.hospital.model.IllnessInfo;
import com.xuexiang.xui.adapter.simple.XUISimpleAdapter;
import com.xuexiang.xui.widget.button.shadowbutton.RippleShadowShadowButton;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;
import com.xuexiang.xui.widget.toast.XToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {


    private List<IllnessInfo> mIllnessInfoList;

    private Context mContext;

    public InfoAdapter(List<IllnessInfo> illnessInfoList) {
        mIllnessInfoList = illnessInfoList;
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
        IllnessInfo mInfo = mIllnessInfoList.get(position);
        holder.mIllnessName.setText(mInfo.getUserIllnessName());
        holder.mNamePhone.setText(String.format("姓名: %s      手机号:%s", mInfo.getUserName(), mInfo.getUserPhone()));
        holder.mAgeGender.setText(String.format("年龄: %s      性  别:  %s", mInfo.getUserAge(), mInfo.getUserGender()));
        holder.mAddress.setText(mInfo.getUserAddress());
        holder.mContent.setText(mInfo.getUserIllnessInfo());

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
                        Intent editInfo = new Intent(mContext,EditIllnessActivity.class);
                        editInfo.putExtra("handle", EditIllnessActivity.CHANGE);
                        editInfo.putExtra("illnessId", mInfo.getId());
                        mContext.startActivity(editInfo);
                        break;
                    case "删除":
                        //删除选项
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                        mBuilder.setPositiveButton("删除", (dialogInterface, i) -> {
                            mInfo.delete();
                            dialogInterface.dismiss();
                            Toast success = XToast.success(mContext,"删除成功!");
                            success.setGravity(Gravity.CENTER,0,0);
                            success.show();

                            //发送数据改变广播
                            Intent intent = new Intent("com.thundersoft.hospital.broadcast.DATA_CHANGE");
                            mContext.sendBroadcast(intent);
                        }).setTitle("正在删除")
                                .setNegativeButton("取消",((dialogInterface, i) -> dialogInterface.dismiss()))
                                .setMessage("是否要删除"+mInfo.getUserIllnessName()+"?")
                                .show();
                        break;
                }
                mPopupMenu.dismiss();
            });

            mPopupMenu.showDown(view);
        });
    }

    @Override
    public int getItemCount() {
        return mIllnessInfoList.size();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.info_illness_name)
        TextView mIllnessName;
        @BindView(R.id.info_menu)
        RippleShadowShadowButton mMenu;
        @BindView(R.id.info_name_phone)
        TextView mNamePhone;
        @BindView(R.id.info_age_gender)
        TextView mAgeGender;
        @BindView(R.id.info_address_input)
        TextView mAddress;
        @BindView(R.id.info_content_input)
        TextView mContent;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
