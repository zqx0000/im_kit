package com.yimaxiaoerlang.im_kit.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allen.library.shape.ShapeTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
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

import java.util.ArrayList;
import java.util.List;

public class GroupInfoActivity extends AppCompatActivity {
    private static final String TAG = "GroupInfoActivity";
    private static final String GROUP_ID = "group_id";

    public static void start(String groupId, Context context) {
        Intent starter = new Intent(context, GroupInfoActivity.class);
        starter.putExtra(GROUP_ID, groupId);
        context.startActivity(starter);
    }

    private BaseQuickAdapter<YMGroupEntity.UserListBean, BaseViewHolder> adapter
            = new BaseQuickAdapter<YMGroupEntity.UserListBean, BaseViewHolder>(R.layout.item_add_member_layout) {
        @Override
        public void convert(BaseViewHolder holder, YMGroupEntity.UserListBean item) {
            holder.setText(R.id.name, item.getUsername());
            ImageUtils.loadImage(holder.getView(R.id.image), item.getUserAvatar());
        }


    };

    private String groupId = "";
    private YMGroupEntity groupEntity;
    private int operatingType = 1; // 操作类型 1 邀请 2 踢人

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        initTitleBar();
        groupId = getIntent().getStringExtra(GROUP_ID);
        initData();
    }

    private void initTitleBar() {
        TitleBarLayout titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("群资料", ITitleBarLayout.Position.MIDDLE);
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
        YMIMClient.chatManager().getConversationInfoWithTargetId(groupId, new YMResultCallback<YMGroupEntity>() {
            @Override
            public void onSuccess(YMGroupEntity group) {
                groupEntity = group;
                if (groupEntity != null) initView();
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        ImageView headImage = findViewById(R.id.head_image);
        TextView groupName = findViewById(R.id.user_name);
        TextView groupIdTv = findViewById(R.id.user_id);
        ShapeTextView edit = findViewById(R.id.edit);
        TextView groupMemberCount = findViewById(R.id.groupMemberCount);
        RecyclerView groupMemberRecycler = findViewById(R.id.chatRecycler);
        LinearLayout groupMemberLayout = findViewById(R.id.groupMemberLayout);
        LinearLayout addMember = findViewById(R.id.addMember);
        SwitchCompat topSw = findViewById(R.id.topSw);
        SwitchCompat messageSw = findViewById(R.id.messageSw);
        Button backGroup = findViewById(R.id.backGroup);
        // 管理群员
        groupMemberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingType = 2;
                SelectFriendActivity.start(GroupInfoActivity.this, true);
            }
        });
        // 退出群聊
        backGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YMIMClient.groupManager().leaveGroupWithGroupId(groupId, new YMResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean var1) {
                        if (var1) {
                            finish();
                        }
                    }

                    @Override
                    public void onError(int errorCode) {

                    }
                });
            }
        });

        // 修改群名称
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupInputCard popupInputCard = new PopupInputCard(GroupInfoActivity.this);
                popupInputCard.setContent(groupEntity.getGroup().getGroupName());
                popupInputCard.setTitle("修改群名称");
                popupInputCard.setOnPositive((result -> {
                    YMIMClient.groupManager().updateGroupNameWithGroupId(groupId, result, new YMResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean var1) {
                            if (var1) {
                                groupName.setText(result);
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

        ImageUtils.loadImage(headImage, groupEntity.getGroup().getGroupAvatar());
        groupName.setText(groupEntity.getGroup().getGroupName());
        groupIdTv.setText("群ID: " + groupEntity.getGroup().getGroupId());
        groupMemberCount.setText("共" + groupEntity.getUserList().size() + "人");
        topSw.setChecked(groupEntity.getGroup().getIsTop() == 1);
        messageSw.setChecked(groupEntity.getGroup().getIsDisturb() == 1);
        // 置顶
        topSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                YMIMClient.chatManager().topConversationWithTargetId(groupId, new YMResultCallback<Boolean>() {
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
                YMIMClient.chatManager().doNotDisturbConversationWithTargetId(groupId, new YMResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean var1) {

                    }

                    @Override
                    public void onError(int errorCode) {

                    }
                });
            }
        });

        groupMemberRecycler.setLayoutManager(new GridLayoutManager(GroupInfoActivity.this, 6));
        groupMemberRecycler.setAdapter(adapter);
        adapter.setNewInstance(groupEntity.getUserList());
        //邀请好友加入群聊
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operatingType = 1;
                SelectFriendActivity.start(GroupInfoActivity.this, true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == Activity.RESULT_OK) {
            if (requestCode == SelectFriendActivity.SELECT_FRIEND_REQ) {
                List<YMContacts> selectList = (ArrayList<YMContacts>) data.getSerializableExtra(SelectFriendActivity.SELECT_FRIEND_RESULT);
                List<String>  idList = new ArrayList<>();
                for (int i = 0; i < selectList.size(); i++) {
                    idList.add(selectList.get(i).getTid() + "");
                }
                if (operatingType == 1) {
                    // 邀请好友
                    YMIMClient.groupManager().inviteIntoGroupWithGroupId(groupId, idList, new YMResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean var1) {
                            if (var1) {
                                ToastUtils.normal("已发送邀请请求");
                            }
                        }

                        @Override
                        public void onError(int errorCode) {

                        }
                    });
                } else {
                    // 踢人
                    YMIMClient.groupManager().kickGroupWithGroupId(groupId, idList, new YMResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean var1) {
                            initData();
                        }

                        @Override
                        public void onError(int errorCode) {

                        }
                    });
                }
            }
        }
    }
}