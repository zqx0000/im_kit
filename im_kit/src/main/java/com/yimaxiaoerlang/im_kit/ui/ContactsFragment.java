package com.yimaxiaoerlang.im_kit.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.yimaxiaoerlang.im_kit.utils.ImageUtils;
import com.squareup.picasso.Picasso;
import com.yimaxiaoerlang.im_core.core.YMIMClient;
import com.yimaxiaoerlang.im_core.core.other.YMResultCallback;
import com.yimaxiaoerlang.im_core.model.YMApplyUserListEntity;
import com.yimaxiaoerlang.im_core.model.conversation.YMConversation;
import com.yimaxiaoerlang.im_core.model.YMContacts;

import java.util.List;

public class ContactsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = ContactsFragment.class.getSimpleName();
    private BaseQuickAdapter<YMContacts, BaseViewHolder> adapter = new BaseQuickAdapter<YMContacts, BaseViewHolder>(R.layout.item_view_userinfo) {
        @Override
        public void convert(BaseViewHolder holder, YMContacts item) {
            holder.setText(R.id.tv_name, item.getUsername());
//                    getView<ImageView>(R.id.iv_icon).loadUrl(item.userAvatar)
            ImageUtils.loadImage(holder.getView(R.id.iv_icon), item.getUserAvatar());
        }


    };

    SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, null);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        refreshLayout.setOnRefreshListener(this);
        RecyclerView rvContacts = (RecyclerView) getView().findViewById(R.id.rv_contacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvContacts.setAdapter(adapter);
        initNewFriend(0);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter_, @NonNull View view, int position) {
                YMContacts contacts = adapter.getData().get(position);
                YMConversation conversation = new YMConversation();
                Log.e(TAG, "onItemClick: " + contacts.getTid() );
                conversation.setTargetId(contacts.getTid());
                conversation.setConversationTitle(contacts.getUsername());
                conversation.setPortraitUrl(contacts.getUserAvatar());
                conversation.setConversationType(YMConversation.ConversationType.PRIVATE);
                Intent intent = new Intent(requireContext(), NewChatActivity.class);

                intent.putExtra("conversation", conversation);
                startActivity(intent);
            }
        });
    }

    private void getFriendsApplyCount() {
        YMIMClient.friendManager().getFriendsApplyListWithPage(1, 9999, new YMResultCallback<YMApplyUserListEntity>() {
            @Override
            public void onSuccess(YMApplyUserListEntity var1) {
                int count = 0;
                for (YMApplyUserListEntity.RecordsBean record : var1.getRecords()) {
                    if (record.getUtState() == 1) {
                        count++;
                    }
                }
                initNewFriend(count);
            }

            @Override
            public void onError(int errorCode) {
                stopRefresh();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initNewFriend(int count) {
        View newFriend = View.inflate(getContext(), R.layout.item_view_userinfo, null);
        ImageView leftIcon = newFriend.findViewById(R.id.iv_icon);
        Picasso.with(getContext()).load(R.mipmap.group_new_friend).into(leftIcon);
        TextView name = newFriend.findViewById(R.id.tv_name);
        TextView rightCount = newFriend.findViewById(R.id.right_count);
        if (count > 0) {
            rightCount.setVisibility(View.VISIBLE);
            rightCount.setText(count + "");
        } else {
            rightCount.setVisibility(View.GONE);
        }
        name.setText("新的朋友");
        newFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewFriendListActivity.start(getContext());
            }
        });
        adapter.setHeaderView(newFriend);
    }

    public void initData() {
        YMIMClient.friendManager().getFriendsListWithCompletion(new YMResultCallback<List<YMContacts>>() {
            public void onSuccess(List<YMContacts> var1) {
                adapter.setNewInstance(var1);
                stopRefresh();
            }

            public void onError(int errorCodet) {
                stopRefresh();
            }

        });
        getFriendsApplyCount();
    }

    private void stopRefresh() {
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }, 800);
    }

    @Override
    public void onRefresh() {
        initData();
    }
}
