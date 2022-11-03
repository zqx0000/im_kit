package com.yimaxiaoerlang.im_kit.ui;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.utils.ConversationTime;
import com.yimaxiaoerlang.im_kit.utils.ImageUtils;
import com.yimaxiaoerlang.im_kit.utils.MessageReceiveListener;
import com.yimaxiaoerlang.im_kit.utils.MessageCenter;
import com.yimaxiaoerlang.im_kit.utils.ScreenUtil;
import com.yimaxiaoerlang.im_kit.view.actionbar.menu.PopActionClickListener;
import com.yimaxiaoerlang.im_kit.view.actionbar.menu.PopDialogAdapter;
import com.yimaxiaoerlang.im_kit.view.actionbar.menu.PopMenuAction;
import com.othershe.combinebitmap.CombineBitmap;
import com.othershe.combinebitmap.layout.WechatLayoutManager;
import com.yimaxiaoerlang.ym_base.YMLog;
import com.yimaxiaoerlang.im_core.core.YMIMClient;
import com.yimaxiaoerlang.im_core.core.other.YMResultCallback;
import com.yimaxiaoerlang.im_core.model.conversation.YMConversation;
import com.yimaxiaoerlang.im_core.model.message.YMCustomMessage;
import com.yimaxiaoerlang.im_core.model.message.YMFileMessage;
import com.yimaxiaoerlang.im_core.model.message.YMImageMessage;
import com.yimaxiaoerlang.im_core.model.message.YMMessage;
import com.yimaxiaoerlang.im_core.model.message.YMMessageContent;
import com.yimaxiaoerlang.im_core.model.message.YMTextMessage;
import com.yimaxiaoerlang.im_core.model.message.YMTimeMessage;
import com.yimaxiaoerlang.im_core.model.message.YMVideoMessage;
import com.yimaxiaoerlang.im_core.model.message.YMVoiceMessage;

import java.util.ArrayList;
import java.util.List;


public class ConversationListFragment extends Fragment implements MessageReceiveListener, SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<YMConversation> conversationArray = new ArrayList<>();
    private BaseQuickAdapter<YMConversation, BaseViewHolder> adapter = new BaseQuickAdapter<YMConversation, BaseViewHolder>(R.layout.item_message_list_layout) {
        @Override
        public void convert(BaseViewHolder holder, YMConversation item) {
            if (item.getConversationType() == YMConversation.ConversationType.GROUP || item.getConversationType() == YMConversation.ConversationType.CHATROOM) {
                // 群聊类型就生成群组头像
                String[] groupHeads = item.getPortraitUrl().split(",");
                CombineBitmap.init(getContext())
                        .setLayoutManager(new WechatLayoutManager())
                        .setSize(35)
                        .setUrls(groupHeads)
                        .setImageView(holder.getView(R.id.headImage))
                        .build();
            } else {
                // 其他类型
                ImageUtils.loadImage(holder.getView(R.id.headImage), item.getPortraitUrl());
            }
            holder.setText(R.id.name, item.getConversationTitle());
            if (item.getLatestMessage() != null) {
                YMMessageContent message = item.getLatestMessage();
                holder.setText(
                        R.id.time,
                        ConversationTime.convertConversationTime(message.getMessageTime())
                );
                if (message instanceof YMTextMessage) {
                    holder.setText(R.id.content, ((YMTextMessage) message).getContent());
                } else if (message instanceof YMVideoMessage) {
                    holder.setText(R.id.content, "[视频]");
                } else if (message instanceof YMImageMessage) {
                    holder.setText(R.id.content, "[图片]");
                } else if (message instanceof YMVoiceMessage) {
                    holder.setText(R.id.content, "[语音]");
                } else if (message instanceof YMFileMessage) {
                    holder.setText(R.id.content, "[文件]");
                } else if (message instanceof YMCustomMessage) {
                    holder.setText(R.id.content, "[分享消息]");
                } else {
                    holder.setText(R.id.content, "[消息]");
                }
            }


            holder.setVisible(R.id.ponit, item.getUnreadMessageCount() > 0);

        }
    };

    private PopDialogAdapter mConversationPopAdapter;
    private PopupWindow mConversationPopWindow;
    private List<PopMenuAction> mConversationPopActions = new ArrayList<>();
    private ListView mConversationPopList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation_list, null);
        return view;
    }

    private RecyclerView rcConversationList;
    private SwipeRefreshLayout rcRefresh;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcConversationList = view.findViewById(R.id.rc_conversation_list);
        rcRefresh = view.findViewById(R.id.refreshLayout);

        initView();
        MessageCenter.getInstance().addReceiveListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        rcRefresh.setOnRefreshListener(this);
        rcConversationList.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcConversationList.setAdapter(adapter);
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
        adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter_, @NonNull View view, int position) {
                showItemPopMenu(view, adapter.getData().get(position));
                return true;
            }
        });
        initPopMenuAction();
    }

    private void initData() {
        ArrayList<YMConversation.ConversationType> arrayList = new ArrayList<>();
        arrayList.add(YMConversation.ConversationType.PRIVATE);
        arrayList.add(YMConversation.ConversationType.GROUP);
        arrayList.add(YMConversation.ConversationType.CHATROOM);
        YMIMClient.chatManager().getConversationList(arrayList, 1, 999, new YMResultCallback<List<YMConversation>>() {
            @Override
            public void onSuccess(List<YMConversation> var1) {
                Log.e("zgj", "获取成功");
                stopRefresh();
                showArray(var1);
            }

            @Override
            public void onError(int errorCode) {
                stopRefresh();
            }

        });
    }


    private void initPopMenuAction() {
        // 设置长按conversation显示PopAction
        List<PopMenuAction> conversationPopActions = new ArrayList<PopMenuAction>();
        PopMenuAction action = new PopMenuAction();
        action.setActionName(getResources().getString(R.string.chat_top));
        action.setActionClickListener(new PopActionClickListener() {
            @Override
            public void onActionClick(int index, Object data) {
                YMConversation c = (YMConversation) data;
                YMIMClient.chatManager().topConversationWithTargetId(c.getGroupId(), new YMResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean var1) {
                        initData();
                    }

                    @Override
                    public void onError(int errorCode) {

                    }
                });
            }
        });
        conversationPopActions.add(action);
        action = new PopMenuAction();
        action.setActionClickListener(new PopActionClickListener() {
            @Override
            public void onActionClick(int index, Object data) {
                YMConversation c = (YMConversation) data;
                YMIMClient.chatManager().removeConversationWithTargetId(c.getGroupId(), new YMResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean var1) {
                        initData();
                    }

                    @Override
                    public void onError(int errorCode) {

                    }
                });
            }
        });
        action.setActionName(getResources().getString(R.string.chat_delete));
        conversationPopActions.add(action);

        action = new PopMenuAction();
        action.setActionName(getResources().getString(R.string.clear_conversation_message));
        action.setActionClickListener(new PopActionClickListener() {
            @Override
            public void onActionClick(int index, Object data) {
                YMConversation c = (YMConversation) data;
                YMIMClient.chatManager().clearMessageWithTargetId(c.getGroupId(), new YMResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean var1) {
                        initData();
                    }

                    @Override
                    public void onError(int errorCode) {

                    }
                });
            }
        });
        conversationPopActions.add(action);

        mConversationPopActions.clear();
        mConversationPopActions.addAll(conversationPopActions);
    }

    /**
     * 长按会话item弹框
     *
     * @param view 长按 view
     */
    private void showItemPopMenu(View view, YMConversation conversation) {
        if (mConversationPopActions == null || mConversationPopActions.size() == 0)
            return;
        View itemPop = LayoutInflater.from(getActivity()).inflate(R.layout.conversation_pop_menu_layout, null);
        mConversationPopList = itemPop.findViewById(R.id.pop_menu_list);
        mConversationPopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PopMenuAction action = mConversationPopActions.get(position);
                if (action.getActionClickListener() != null) {
                    action.getActionClickListener().onActionClick(position, conversation);
                }
                mConversationPopWindow.dismiss();
            }
        });

        mConversationPopAdapter = new PopDialogAdapter();
        mConversationPopList.setAdapter(mConversationPopAdapter);
        mConversationPopAdapter.setDataSource(mConversationPopActions);
        mConversationPopWindow = new PopupWindow(itemPop, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mConversationPopWindow.setBackgroundDrawable(new ColorDrawable());
        mConversationPopWindow.setOutsideTouchable(true);
        mConversationPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        int x = view.getWidth() / 2;
        int y = -view.getHeight() / 3;
        int popHeight = ScreenUtil.dip2px(45) * 3;
        if (y + popHeight + view.getY() + view.getHeight() > rcConversationList.getBottom()) {
            y = y - popHeight;
        }
        mConversationPopWindow.showAsDropDown(view, x, y - 250, Gravity.BOTTOM | Gravity.START);
    }

    private void showArray(List<YMConversation> array) {

        adapter.setList(array);
        conversationArray.clear();
        conversationArray.addAll(array);
    }


    /**
     * 处理socket 中收到的消息
     */
    private void handleMessage(YMMessage message) {
        //消息是否属于当前回话列表中会话
        boolean has = false;
        if (message == null) {
            return;
        }
        for (int i = 0; i < conversationArray.size(); i++) {
            YMConversation conversation = conversationArray.get(i);
            if (conversation.getGroupId().equals(message.getTargetId())) {
                has = true;
                conversation.setLatestMessage(message.getContent());
                if (message.getReadState() == YMMessage.MessageReadState.UNREAD) {//消息已读不更新数字。只更新内容
                    conversation.setUnreadMessageCount(conversation.getUnreadMessageCount() + 1);
                    if (i != 0) {
                        adapter.remove(conversation);
                        adapter.addData(0, conversation);
                    }
                    adapter.notifyItemChanged(i);
                }
            }

        }


        if (!has) {
            initData();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MessageCenter.getInstance().removeReceiveListener(this);
    }


    @Override
    public void onReceive(YMMessage message) {

        if (message != null) {
            if (message.getContent() instanceof YMTimeMessage) {
                return;
            }
            handleMessage(message);
        }
    }

    @Override
    public void onRefresh() {
        initData();
    }

    private void stopRefresh() {
        rcRefresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                rcRefresh.setRefreshing(false);
            }
        }, 800);
    }
}
