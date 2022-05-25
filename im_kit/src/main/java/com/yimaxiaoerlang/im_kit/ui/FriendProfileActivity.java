package com.yimaxiaoerlang.im_kit.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.allen.library.shape.ShapeTextView;
import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.utils.ImageUtils;
import com.yimaxiaoerlang.im_kit.utils.SystemBarHelper;
import com.yimaxiaoerlang.im_kit.utils.ToastUtils;
import com.yimaxiaoerlang.im_kit.view.actionbar.ITitleBarLayout;
import com.yimaxiaoerlang.im_kit.view.actionbar.TitleBarLayout;
import com.yimaxiaoerlang.im_kit.view.popupcard.PopupInputCard;
import com.yimaxiaoerlang.im_core.core.YMIMClient;
import com.yimaxiaoerlang.im_core.core.other.YMResultCallback;
import com.yimaxiaoerlang.im_core.model.YMContacts;
import com.yimaxiaoerlang.im_core.model.YMGroupEntity;

public class FriendProfileActivity extends AppCompatActivity {
    private static final String TAG = "FriendProfileActivity";
    private static final String USER_ID = "user_id";
    private static final String CONVERSATION_ID = "conversation_id";
    public static void start(String userId, String conversationId, Context context) {
        Intent starter = new Intent(context, FriendProfileActivity.class);
        starter.putExtra(USER_ID, userId);
        starter.putExtra(CONVERSATION_ID, conversationId);
        context.startActivity(starter);
    }
    private String userId = "";
    private String conversationId = "";
    private YMContacts user;
    private YMGroupEntity conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        userId = getIntent().getStringExtra(USER_ID);
        conversationId = getIntent().getStringExtra(CONVERSATION_ID);
        initTitleBar();
        initData();
    }

    private void initTitleBar() {
        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        TitleBarLayout titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("用户详情", ITitleBarLayout.Position.MIDDLE);
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

    private void initData() {
        // 获取好友信息
        YMIMClient.friendManager().getFriendDetailWithFriendId(userId, new YMResultCallback<YMContacts>() {
            @Override
            public void onSuccess(YMContacts data) {
                user = data;
                if (user != null) initUserView();
            }

            @Override
            public void onError(int errorCode) {

            }
        });

        // 获取会话信息
        YMIMClient.chatManager().getConversationInfoWithTargetId(conversationId, new YMResultCallback<YMGroupEntity>() {
            @Override
            public void onSuccess(YMGroupEntity var1) {
                conversation = var1;
                if (conversation != null) initConversationView();
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initUserView() {
        ImageView headImage = findViewById(R.id.head_image);
        TextView userName = findViewById(R.id.user_name);
        TextView userIdTv = findViewById(R.id.user_id);
        ShapeTextView edit = findViewById(R.id.edit);
        Button add = findViewById(R.id.add);
        Button clear = findViewById(R.id.clear);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriendActivity.start(FriendProfileActivity.this, user.getUsername(), user.getTid() + "", user.getUserAvatar());
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YMIMClient.chatManager().clearMessageWithTargetId(conversationId, new YMResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean var1) {
                       ToastUtils.normal("清除成功");
                    }

                    @Override
                    public void onError(int errorCode) {

                    }
                });
            }
        });
        // 修改备注
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupInputCard popupInputCard = new PopupInputCard(FriendProfileActivity.this);
                popupInputCard.setContent(user.getNoteName() != null ? user.getNoteName() : "");
                popupInputCard.setTitle("修改备注");
                popupInputCard.setOnPositive((result -> {
                    YMIMClient.friendManager().addFriendNoteWithFriendId(userId, result, new YMResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean var1) {
                            if (var1) {
                                userName.setText(result);
                            }
                        }

                        @Override
                        public void onError(int errorCode) {

                        }
                    });
                }));
                popupInputCard.show(findViewById(R.id.container), Gravity.BOTTOM);
            }
        });

        ImageUtils.loadImage(headImage, user.getUserAvatar());
        userName.setText(user.getUsername());
        userIdTv.setText("用户ID: " + user.getTid());


    }

    private void initConversationView() {
        SwitchCompat topSw = findViewById(R.id.topSw);
        SwitchCompat messageSw = findViewById(R.id.messageSw);

        topSw.setChecked(conversation.getGroup().getIsTop() == 1);
        messageSw.setChecked(conversation.getGroup().getIsDisturb() == 1);

        // 置顶
        topSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                YMIMClient.chatManager().topConversationWithTargetId(conversationId, new YMResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean var1) {

                    }

                    @Override
                    public void onError(int errorCode) {

                    }
                });
            }
        });
        // 消息免打扰
        messageSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                YMIMClient.chatManager().doNotDisturbConversationWithTargetId(conversationId, new YMResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean var1) {

                    }

                    @Override
                    public void onError(int errorCode) {

                    }
                });
            }
        });
    }
}