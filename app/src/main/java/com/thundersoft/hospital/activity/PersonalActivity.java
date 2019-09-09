package com.thundersoft.hospital.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.thundersoft.hospital.R;
import com.thundersoft.hospital.model.User;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonalActivity extends AppCompatActivity {

    private static final String TAG = "PersonalActivity";

    @BindView(R.id.personal_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.personal_username)
    SuperTextView mUsername;
    @BindView(R.id.personal_password)
    SuperTextView mPassword;
    @BindView(R.id.personal_phone)
    SuperTextView mPhone;

    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        ButterKnife.bind(this);
        ActivityController.addActivity(this);
        //获取用户信息
        getUserInfoFromIntent();
        //初始化控件
        initControls();
    }


    /**
     * 初始化控件
     * 沉浸式状态栏和标题栏
     */
    private void initControls() {

        //沉浸式状态栏和标题栏
        StatusBarUtils.translucent(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("个人信息");
        }

        mUsername.setRightString(mCurrentUser.getUserName());
        mPhone.setRightString(mCurrentUser.getPhone());

        mPassword.setRightTvClickListener(() -> {
            //进入修改密码页面

        });
    }

    /**
     * 从意图中获取用户信息
     */
    private void getUserInfoFromIntent () {
        Bundle userBundle = getIntent().getBundleExtra("user");
        if (userBundle != null) {
            mCurrentUser = (User) userBundle.get("user");
        } else {
            Log.e(TAG, "getUserInfoFromIntent: 用户信息为空!");
        }
    }

    /**
     * 菜单栏选项
     * @param item 选项
     * @return 返回true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }
}
