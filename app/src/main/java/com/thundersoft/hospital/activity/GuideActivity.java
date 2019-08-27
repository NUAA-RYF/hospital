package com.thundersoft.hospital.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.thundersoft.hospital.R;

import butterknife.ButterKnife;

public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        initControls();
        initClickListener();
    }

    private void initControls(){

    }

    private void initClickListener(){

    }


}
