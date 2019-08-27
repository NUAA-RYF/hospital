package com.thundersoft.hospital.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thundersoft.hospital.R;
import com.thundersoft.hospital.adapter.TableAdapter;
import com.thundersoft.hospital.model.Table;
import com.thundersoft.hospital.util.HttpUtil;
import com.thundersoft.hospital.util.LoadJsonUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class TableFragment extends Fragment {

    private static final String KEY = "27e281130003dee5";

    @BindView(R.id.table_recyclerView)
    RecyclerView mRecyclerView;

    private View rootView;

    private Activity mActivity;

    private Context mContext;

    private TableAdapter mTableAdapter;

    private List<Table> mTableList;

    public static TableFragment newInstance() {
        TableFragment fragment = new TableFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mContext = getContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_table, container, false);
        }
        ButterKnife.bind(this,rootView);
        //初始化数据
        initData();

        //查询对照表
        queryTable();
        return rootView;
    }


    private void initData(){
        mTableList = new ArrayList<>();


        //适配器及视图初始化
        mTableAdapter = new TableAdapter(mTableList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setAdapter(mTableAdapter);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void queryTable(){
        if (mTableList.size() <= 0){
            queryFromServer();
        }else {
            mTableAdapter.notifyDataSetChanged();
        }
    }
    private void queryFromServer() {
        String address = "https://api.jisuapi.com/snsn/all?appkey="+KEY;
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText = Objects.requireNonNull(response.body()).string();
                boolean result = Table(responseText);

                if (result) mActivity.runOnUiThread(() -> queryTable());
            }
        });
    }

    /**
     * 获得Table对象
     * 放入mTableList中
     * @param response JSON格式字符串
     * @return 成功返回true
     */
    private boolean Table(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONObject msgAll = new JSONObject(response);
                if (msgAll.getString("msg").equals("ok")){
                    JSONObject object = msgAll.getJSONObject("result");
                    for (int i = 18; i < 46; i++) {
                        Table table = new Table();
                        JSONArray jsonArray = object.getJSONArray(String.valueOf(i));
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject a = jsonArray.getJSONObject(j);
                            switch (j){
                                case 0:
                                    table.setAge(String.valueOf(i));
                                    table.setMonth_1(a.getString("month"));
                                    table.setGender_1(a.getString("sex"));
                                    break;
                                case 1:
                                    table.setMonth_2(a.getString("month"));
                                    table.setGender_2(a.getString("sex"));
                                    break;
                                case 2:
                                    table.setMonth_3(a.getString("month"));
                                    table.setGender_3(a.getString("sex"));
                                    break;
                                case 3:
                                    table.setMonth_4(a.getString("month"));
                                    table.setGender_4(a.getString("sex"));
                                    break;
                                case 4:
                                    table.setMonth_5(a.getString("month"));
                                    table.setGender_5(a.getString("sex"));
                                    break;
                                case 5:
                                    table.setMonth_6(a.getString("month"));
                                    table.setGender_6(a.getString("sex"));
                                    break;
                                case 6:
                                    table.setMonth_7(a.getString("month"));
                                    table.setGender_7(a.getString("sex"));
                                    break;
                                case 7:
                                    table.setMonth_8(a.getString("month"));
                                    table.setGender_8(a.getString("sex"));
                                    break;
                                case 8:
                                    table.setMonth_9(a.getString("month"));
                                    table.setGender_9(a.getString("sex"));
                                    break;
                                case 9:
                                    table.setMonth_10(a.getString("month"));
                                    table.setGender_10(a.getString("sex"));
                                    break;
                                case 10:
                                    table.setMonth_11(a.getString("month"));
                                    table.setGender_11(a.getString("sex"));
                                    break;
                                case 11:
                                    table.setMonth_12(a.getString("month"));
                                    table.setGender_12(a.getString("sex"));
                                    break;
                            }
                        }
                        mTableList.add(table);
                    }
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
