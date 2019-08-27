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
import com.thundersoft.hospital.activity.SolarTermActivity;
import com.thundersoft.hospital.model.Solarterm;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SolarTermsAdapter extends RecyclerView.Adapter<SolarTermsAdapter.ViewHolder> {

    private Context mContext;

    private List<Solarterm> mSolartermList;

    public SolarTermsAdapter(List<Solarterm> solartermList) {
        mSolartermList = solartermList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.solarterm_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Solarterm solarterm = mSolartermList.get(position);
        String []date = solarterm.getJieqiDate().split(" ");
        holder.mName.setText(solarterm.getJieqiName());
        holder.mDate.setText(date[0]);
        Glide.with(mContext).load(solarterm.getImgUrl()).into(holder.mImg);

        holder.itemView.setOnClickListener(view -> {
            Intent mIntent = new Intent(mContext, SolarTermActivity.class);
            mIntent.putExtra("jieqiId",solarterm.getJieqiId());
            mContext.startActivity(mIntent);
        });
    }

    @Override
    public int getItemCount() {
        return mSolartermList.size();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.SolarTerm_img)
        ImageView mImg;
        @BindView(R.id.SolarTerm_name)
        TextView mName;
        @BindView(R.id.SolarTerm_date)
        TextView mDate;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
