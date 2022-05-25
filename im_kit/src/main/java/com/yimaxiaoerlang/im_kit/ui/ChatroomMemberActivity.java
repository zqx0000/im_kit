package com.yimaxiaoerlang.im_kit.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.utils.ImageUtils;
import com.yimaxiaoerlang.im_kit.utils.SystemBarHelper;
import com.yimaxiaoerlang.im_kit.utils.ToastUtils;
import com.yimaxiaoerlang.im_kit.view.actionbar.ITitleBarLayout;
import com.yimaxiaoerlang.im_kit.view.actionbar.TitleBarLayout;
import com.yimaxiaoerlang.im_core.core.YMIMClient;
import com.yimaxiaoerlang.im_core.core.other.YMResultCallback;
import com.yimaxiaoerlang.im_core.model.YMGroupEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatroomMemberActivity extends AppCompatActivity {
    private static final String TAG = "ChatRoomInfoActivity";
    private static final String CHATROOM_ID = "chatroom_id";

    public static void start(String chatroomId, Activity context) {
        Intent starter = new Intent(context, ChatRoomInfoActivity.class);
        starter.putExtra(CHATROOM_ID, chatroomId);
        context.startActivityForResult(starter, SELECT_MEMBER_REQ);
    }

    private final BaseQuickAdapter<YMGroupEntity.UserListBean, BaseViewHolder> adapter = new BaseQuickAdapter<YMGroupEntity.UserListBean, BaseViewHolder>(R.layout.item_select_friend) {
        @Override
        public void convert(BaseViewHolder holder, YMGroupEntity.UserListBean item) {
            holder.setText(R.id.tv_name, item.getUsername());
            ImageUtils.loadImage(holder.getView(R.id.iv_icon), item.getUserAvatar());
            CheckBox checkBox = holder.getView(R.id.checkbox);
            if (selectedList.contains(item)) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedList.contains(item)) {
                        selectedList.remove(item);
                    } else {
                        selectedList.add(item);
                    }
                }
            });
        }


    };

    private final List<YMGroupEntity.UserListBean> members = new ArrayList<>();
    private final List<YMGroupEntity.UserListBean> selectedList = new ArrayList<>();

    private String chatroomId = "";
    private YMGroupEntity groupEntity;
    public static final int SELECT_MEMBER_REQ = 3300;
    public static final String SELECT_MEMBER_RESULT = "select_MEMBER_result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_member);
        initTitleBar();
        initData();
    }

    private void initTitleBar() {
        TitleBarLayout titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("聊天室成员", ITitleBarLayout.Position.MIDDLE);
        SystemBarHelper.setStatusBarDarkMode(this);
        SystemBarHelper.immersiveStatusBar(this, 0f);
        SystemBarHelper.setPadding(this, titleBar);
        titleBar.getLeftGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titleBar.setTitle("移除", ITitleBarLayout.Position.RIGHT);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedList.isEmpty()) {
                    ToastUtils.normal("请至少选择一位成员");
                    return;
                }
                Intent intent = getIntent();
                intent.putExtra(SELECT_MEMBER_RESULT, (Serializable) selectedList);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initData() {
        YMIMClient.chatManager().getConversationInfoWithTargetId(chatroomId, new YMResultCallback<YMGroupEntity>() {
            @Override
            public void onSuccess(YMGroupEntity var1) {
                groupEntity = var1;
                adapter.setList(groupEntity.getUserList());
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }


}