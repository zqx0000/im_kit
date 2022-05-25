package com.yimaxiaoerlang.im_kit.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allen.library.shape.ShapeTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.utils.SystemBarHelper;
import com.yimaxiaoerlang.im_kit.view.actionbar.ITitleBarLayout;
import com.yimaxiaoerlang.im_kit.view.actionbar.TitleBarLayout;
import com.yimaxiaoerlang.im_core.core.YMIMClient;
import com.yimaxiaoerlang.im_core.core.other.YMResultCallback;
import com.yimaxiaoerlang.im_core.model.YMChatroomEntity;
import com.yimaxiaoerlang.im_core.model.conversation.YMConversation;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomListActivity extends AppCompatActivity {
    public static void start(Context context) {
        Intent starter = new Intent(context, ChatRoomListActivity.class);
        context.startActivity(starter);
    }
    private TitleBarLayout titleBar;

    private final BaseQuickAdapter<YMChatroomEntity, BaseViewHolder> adapter = new BaseQuickAdapter<YMChatroomEntity, BaseViewHolder>(R.layout.item_chatroom_list) {
        @Override
        public void convert(BaseViewHolder holder, YMChatroomEntity item) {
            holder.setText(R.id.tv_name, item.getChatRoom().getGroupName());
            ShapeTextView apply = holder.getView(R.id.apply);
            apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YMIMClient.roomManager().joinRoom(item.getChatRoom().getGroupId(), new YMResultCallback<Object>() {
                        @Override
                        public void onSuccess(Object var1) {

                        }

                        @Override
                        public void onError(int errorCode) {

                        }
                    });

                    YMConversation conversation = new YMConversation();
                    conversation.setTargetId(item.getChatRoom().getGroupId());
                    conversation.setGroupId(item.getChatRoom().getGroupId());
                    conversation.setConversationType(YMConversation.ConversationType.CHATROOM);
                    conversation.setConversationTitle(item.getChatRoom().getGroupName());
                    conversation.setPortraitUrl(item.getChatRoom().getGroupAvatar());
                    Intent intent = new Intent(ChatRoomListActivity.this, ChatActivity.class);

                    intent.putExtra("conversation", conversation);
                    startActivity(intent);
                }
            });

        }


    };

    private final List<YMChatroomEntity> rooms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_list);
        initTitleBar();
        initSearchBar();
        initRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData("");
    }

    private void initTitleBar() {
        titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("聊天室列表", ITitleBarLayout.Position.MIDDLE);
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

    private void initSearchBar() {
        EditText searchEdit = findViewById(R.id.searchEdit);
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    initData(searchEdit.getText().toString());
                }
                return false;
            }
        });
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                initData(searchEdit.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    private void initData(String keyword) {
        YMIMClient.roomManager().searchRoomList(keyword, new YMResultCallback<List<YMChatroomEntity>>() {
            @Override
            public void onSuccess(List<YMChatroomEntity> var1) {
                rooms.clear();
                rooms.addAll(var1);
                adapter.setList(rooms);
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }
}