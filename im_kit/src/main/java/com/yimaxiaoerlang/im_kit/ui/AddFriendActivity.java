package com.yimaxiaoerlang.im_kit.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.utils.SystemBarHelper;
import com.yimaxiaoerlang.im_kit.utils.ToastUtils;
import com.yimaxiaoerlang.im_kit.view.actionbar.ITitleBarLayout;
import com.yimaxiaoerlang.im_kit.view.actionbar.TitleBarLayout;
import com.squareup.picasso.Picasso;
import com.yimaxiaoerlang.im_core.core.YMIMClient;
import com.yimaxiaoerlang.im_core.core.other.YMResultCallback;

public class AddFriendActivity extends AppCompatActivity {
    private static final String USER_NAME = "user_name";
    private static final String USER_ID = "user_id";
    private static final String USER_HEAD = "user_head";

    public static void start(Context context, String name, String uid, String userHead) {
        Intent starter = new Intent(context, AddFriendActivity.class);
        starter.putExtra(USER_NAME, name);
        starter.putExtra(USER_ID, uid);
        starter.putExtra(USER_HEAD, userHead);
        context.startActivity(starter);
    }

    private String userName;
    private String userId;
    private TextView userNameTv;
    private TextView userIdTv;
    private Button addBtn;
    private EditText editText;
    private TitleBarLayout titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        userNameTv = findViewById(R.id.user_name);
        userIdTv = findViewById(R.id.user_id);
        editText = findViewById(R.id.edit);
        addBtn = findViewById(R.id.add);
        ImageView headImage = findViewById(R.id.head_image);
        initTitleBar();
        Intent intent = getIntent();
        userName = intent.getStringExtra(USER_NAME);
        userId = intent.getStringExtra(USER_ID);
        String userHead = intent.getStringExtra(USER_HEAD);
        Picasso.with(this).load(userHead).into(headImage);

        userNameTv.setText(userName);
        userIdTv.setText( "用户ID: " + userId);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                YMIMClient.friendManager().applyAddFriendWithTargetId(userId, editText.getText().toString(), new YMResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean var1) {
                        if (var1) {
                            ToastUtils.normal("已发送添加请求");
                            finish();
                        }
                    }

                    @Override
                    public void onError(int errorCode) {
                        ToastUtils.normal("添加失败");
                    }
                });
            }
        });
    }

    private void initTitleBar() {
        titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("添加好友", ITitleBarLayout.Position.MIDDLE);
        SystemBarHelper.setStatusBarDarkMode(this);
        SystemBarHelper.immersiveStatusBar(this, 0f);
        SystemBarHelper.setPadding(this, titleBar);
        titleBar.getLeftGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}