package com.yimaxiaoerlang.im_kit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.utils.ConversationTime;
import com.yimaxiaoerlang.im_kit.utils.ImageUtils;
import com.othershe.combinebitmap.CombineBitmap;
import com.othershe.combinebitmap.layout.WechatLayoutManager;
import com.yimaxiaoerlang.ym_base.YMLog;
import com.yimaxiaoerlang.im_core.core.YMIMClient;
import com.yimaxiaoerlang.im_core.core.other.YMResultCallback;
import com.yimaxiaoerlang.im_core.model.conversation.YMConversation;
import com.yimaxiaoerlang.im_core.model.message.YMCustomMessage;
import com.yimaxiaoerlang.im_core.model.message.YMFileMessage;
import com.yimaxiaoerlang.im_core.model.message.YMImageMessage;
import com.yimaxiaoerlang.im_core.model.message.YMMessageContent;
import com.yimaxiaoerlang.im_core.model.message.YMTextMessage;
import com.yimaxiaoerlang.im_core.model.message.YMVideoMessage;
import com.yimaxiaoerlang.im_core.model.message.YMVoiceMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private BaseQuickAdapter<YMConversation, BaseViewHolder> adapter = new BaseQuickAdapter<YMConversation, BaseViewHolder>(R.layout.rc_conversationlist_item) {
        @Override
        public void convert(BaseViewHolder holder, YMConversation item) {
            if (item.getConversationType() == YMConversation.ConversationType.CHATROOM) {
                // 群聊类型就生成群组头像
                String[] groupHeads = item.getPortraitUrl().split(",");
                CombineBitmap.init(getContext())
                        .setLayoutManager(new WechatLayoutManager())
                        .setSize(35)
                        .setUrls(groupHeads)
                        .setImageView(holder.getView(R.id.iv_icon))
                        .build();
            } else {
                // 其他类型
                ImageUtils.loadImage(holder.getView(R.id.iv_icon), item.getPortraitUrl());
            }
            holder.setText(R.id.rc_conversation_title, item.getConversationTitle());
            if (item.getLatestMessage() != null) {
                YMMessageContent message = item.getLatestMessage();
                holder.setText(
                        R.id.rc_conversation_date,
                        ConversationTime.convertConversationTime(message.getMessageTime())
                );
                if (message instanceof YMTextMessage) {
                    holder.setText(R.id.rc_conversation_content, ((YMTextMessage) message).getContent());
                } else if (message instanceof YMVideoMessage) {
                    holder.setText(R.id.rc_conversation_content, "[视频]");
                } else if (message instanceof YMImageMessage) {
                    holder.setText(R.id.rc_conversation_content, "[图片]");
                } else if (message instanceof YMVoiceMessage) {
                    holder.setText(R.id.rc_conversation_content, "[语音]");
                } else if (message instanceof YMFileMessage) {
                    holder.setText(R.id.rc_conversation_content, "[文件]");
                } else if (message instanceof YMCustomMessage) {
                    holder.setText(R.id.rc_conversation_content, "[分享消息]");
                } else {
                    holder.setText(R.id.rc_conversation_content, "[消息]");
                }
            }


            holder.setVisible(R.id.rc_conversation_unread, item.getUnreadMessageCount() > 0);
            holder.setVisible(R.id.rc_conversation_no_disturb, false);
            holder.setText(
                    R.id.rc_conversation_unread_count, item.getUnreadMessageCount() > 99 ?
                            "99+"
                            :
                            "" + item.getUnreadMessageCount()
            );

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_room, null);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initRecyclerView() {
        refreshLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        adapter.setEmptyView(R.layout.rc_conversationlist_empty_view);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                YMConversation item = (YMConversation) adapter.getItem(position);
                item.setUnreadMessageCount(0);
                adapter.notifyItemChanged(position);
                YMLog.e("TAG", "conversationId: " + item.getTargetId());
                Intent intent = new Intent(requireContext(), ChatActivity.class);
                intent.putExtra("conversation", item);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        ArrayList<YMConversation.ConversationType> arrayList = new ArrayList<>();
        arrayList.add(YMConversation.ConversationType.CHATROOM);
        YMIMClient.chatManager().getConversationList(arrayList, 1, 999, new YMResultCallback<List<YMConversation>>() {
            @Override
            public void onSuccess(List<YMConversation> data) {
                adapter.setList(data);
                stopRefresh();
            }

            @Override
            public void onError(int errorCode) {
                stopRefresh();
            }

        });
    }

    @Override
    public void onRefresh() {
        initData();
    }

    private void stopRefresh() {
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }, 800);
    }
}
