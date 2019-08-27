package com.thundersoft.hospital.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thundersoft.hospital.R;
import com.thundersoft.hospital.activity.FortuneActivity;
import com.thundersoft.hospital.model.Constellation;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConstellationAdapter extends RecyclerView.Adapter<ConstellationAdapter.ViewHolder> {

    private List<Constellation> mConstellationList;

    private Context mContext;
    public ConstellationAdapter(List<Constellation> constellationList) {
        mConstellationList = constellationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.constellation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Constellation mConstellation = mConstellationList.get(position);
        holder.mName.setText(mConstellation.getAstroName());
        holder.mDate.setText(mConstellation.getAstroDate());
        Glide.with(mContext).load(mConstellation.getImgUrl()).into(holder.mImg);

        //意图:星座ID和星座图片URL
        holder.itemView.setOnClickListener(view -> {
            Intent mIntent = new Intent(mContext, FortuneActivity.class);
            mIntent.putExtra("fortuneId",mConstellation.getAstroId());
            mIntent.putExtra("imgUrl",mConstellation.getImgUrl());
            mContext.startActivity(mIntent);
        });
    }

    @Override
    public int getItemCount() {
        return mConstellationList.size();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.constellation_img)
        CircleImageView mImg;
        @BindView(R.id.constellation_name)
        TextView mName;
        @BindView(R.id.constellation_date)
        TextView mDate;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
