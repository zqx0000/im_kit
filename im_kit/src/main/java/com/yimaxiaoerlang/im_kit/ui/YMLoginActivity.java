package com.yimaxiaoerlang.im_kit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.core.YMIMKit;
import com.yimaxiaoerlang.im_kit.core.LoginCallback;
import com.yimaxiaoerlang.im_kit.modlue.SelectUser;
import com.yimaxiaoerlang.im_kit.utils.SPUtil;
import com.yimaxiaoerlang.im_kit.utils.SystemBarHelper;
import com.yimaxiaoerlang.im_kit.utils.ToastUtils;
import com.yimaxiaoerlang.im_kit.view.newinput.SoftKeyboardStateHelper;
import com.yimaxiaoerlang.ym_base.YMConfig;

public class YMLoginActivity extends AppCompatActivity implements LoginCallback {
    private static final String YM_USER_NAME = "YM_USER_NAME";
    private static final String YM_USER_ID = "YM_USER_ID";
    private EditText uidInput;
    private EditText nameInput;
    private LinearLayout layout_main;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SystemBarHelper.immersiveStatusBar(this, 0f);
        initView();
    }

    private void initView() {
        layout_main = findViewById(R.id.layout_main);
        nameInput = findViewById(R.id.et_user_name);
        uidInput = findViewById(R.id.et_uid);
        nameInput.setText(SPUtil.getString(YM_USER_NAME));
        uidInput.setText(SPUtil.getString(YM_USER_ID));
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        SoftKeyboardStateHelper helper = new SoftKeyboardStateHelper(layout_main);
        helper.addSoftKeyboardStateListener(new SoftKeyboardStateHelper.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeight) {
                Log.e("TAG", "onSoftKeyboardOpened: " + keyboardHeight);
                YMIMKit.getInstance().setKeyboardHeight(keyboardHeight);
            }

            @Override
            public void onSoftKeyboardClosed() {

            }
        });

    }

    private void login() {
        if (nameInput.getText().toString().isEmpty()) {
            ToastUtils.normal("请输入用户昵称");
            return;
        }
        if (uidInput.getText().toString().isEmpty()) {
            ToastUtils.normal("请输入用户id");
            return;
        }

        SelectUser selectUser = new SelectUser(nameInput.getText().toString(), uidInput.getText().toString(), "https://wx3.sinaimg.cn/mw600/0076BSS5ly1gh5fv9om40j31900u0e81.jpg");
        YMIMKit.getInstance().login(selectUser, YMLoginActivity.this);
    }

    @Override
    public void loginSuccess() {
        SPUtil.saveString(YM_USER_NAME, nameInput.getText().toString());
        SPUtil.saveString(YM_USER_ID, uidInput.getText().toString());
        startActivity(new Intent(this, IndexActivity.class));
        finish();
    }

    @Override
    public void loginError() {

    }
}
