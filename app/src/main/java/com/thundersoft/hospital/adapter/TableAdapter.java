package com.thundersoft.hospital.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thundersoft.hospital.R;
import com.thundersoft.hospital.model.Table;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private List<Table> mTableList;

    public TableAdapter(List<Table> tableList) {
        mTableList = tableList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Table table = mTableList.get(position);
        String month_one = table.getMonth_1() + " 月 " + table.getMonth_2() + " 月 " + table.getMonth_3() + " 月 " + table.getMonth_4() + " 月";
        String gender_one = table.getGender_1() + "孩 " + table.getGender_2() + "孩 " + table.getGender_3() + "孩 " + table.getGender_4() + "孩";

        String month_two = table.getMonth_5() + " 月 " + table.getMonth_6() + " 月 " + table.getMonth_7() + " 月 " + table.getMonth_8() + " 月";
        String gender_two = table.getGender_5() + "孩 " + table.getGender_6() + "孩 " + table.getGender_7() + "孩 " + table.getGender_8() + "孩";

        String month_three = table.getMonth_9() + "月 " + table.getMonth_10() + "月 " + table.getMonth_11() + "月 " + table.getMonth_12() + "月";
        String gender_three = table.getGender_9() + "孩 " + table.getGender_10() + "孩 " + table.getGender_11() + "孩 " + table.getGender_12() + "孩";

        holder.mAge.setText(String.format("%s岁对照表", table.getAge()));
        //第一行
        holder.mMonthOne.setText(month_one);
        holder.mGenderOne.setText(gender_one);

        //第二行
        holder.mMonthTwo.setText(month_two);
        holder.mGenderTwo.setText(gender_two);

        //第三行
        holder.mMonthThree.setText(month_three);
        holder.mGenderThree.setText(gender_three);
    }

    @Override
    public int getItemCount() {
        return mTableList.size();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.table_age)
        TextView mAge;
        @BindView(R.id.table_month_one)
        TextView mMonthOne;
        @BindView(R.id.table_gender_one)
        TextView mGenderOne;
        @BindView(R.id.table_month_two)
        TextView mMonthTwo;
        @BindView(R.id.table_gender_two)
        TextView mGenderTwo;
        @BindView(R.id.table_month_three)
        TextView mMonthThree;
        @BindView(R.id.table_gender_three)
        TextView mGenderThree;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
