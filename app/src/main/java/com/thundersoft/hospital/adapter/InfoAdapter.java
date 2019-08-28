package com.thundersoft.hospital.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thundersoft.hospital.R;
import com.thundersoft.hospital.model.IllnessInfo;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.adapter.simple.XUISimpleAdapter;
import com.xuexiang.xui.widget.button.shadowbutton.RippleShadowShadowButton;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;
import com.xuexiang.xui.widget.textview.ExpandableTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {


    private List<IllnessInfo> mIllnessInfoList;

    private Context mContext;

    private View view;

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
        holder.mAddress.setText(String.format("地址: %s", mInfo.getUserAddress()));
        holder.mIllnessContent.setText(String.format("病情%s", mInfo.getUserIllnessInfo()));

        //右上角菜单栏按钮
        holder.mMenu.setOnClickListener(view -> {
            String[] itemList= new String[]{"编辑","删除"};
            XUISimpleAdapter mAdapter = XUISimpleAdapter.create(mContext,itemList);
            XUISimplePopup mPopupMenu = new XUISimplePopup(mContext,mAdapter);
            mPopupMenu.create((adapter, item, itemPosition) -> {
                //弹出编辑和删除操作
                String result = String.valueOf(item.getTitle());
                switch (result){
                    case "编辑":
                        Toast.makeText(mContext, "你点击了编辑!", Toast.LENGTH_SHORT).show();
                        break;
                    case "删除":
                        Toast.makeText(mContext, "你点击了删除!", Toast.LENGTH_SHORT).show();
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
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.info_illness_name)
        TextView mIllnessName;
        @BindView(R.id.info_menu)
        RippleShadowShadowButton mMenu;
        @BindView(R.id.info_name_phone)
        TextView mNamePhone;
        @BindView(R.id.info_address)
        TextView mAddress;
        @BindView(R.id.info_expand_address)
        ExpandableTextView mExpandAddress;
        @BindView(R.id.info_illness_content)
        TextView mIllnessContent;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
