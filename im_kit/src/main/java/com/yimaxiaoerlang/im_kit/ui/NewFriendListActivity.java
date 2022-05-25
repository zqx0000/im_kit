package com.yimaxiaoerlang.im_kit.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.utils.ImageUtils;
import com.yimaxiaoerlang.im_kit.utils.SystemBarHelper;
import com.yimaxiaoerlang.im_kit.view.actionbar.ITitleBarLayout;
import com.yimaxiaoerlang.im_kit.view.actionbar.TitleBarLayout;
import com.yimaxiaoerlang.im_core.core.YMIMClient;
import com.yimaxiaoerlang.im_core.core.other.YMResultCallback;
import com.yimaxiaoerlang.im_core.model.YMApplyUserListEntity;

public class NewFriendListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "NewFriendListActivity";

    public static void start(Context context) {
        Intent starter = new Intent(context, NewFriendListActivity.class);
        context.startActivity(starter);
    }

    private BaseQuickAdapter<YMApplyUserListEntity.RecordsBean, BaseViewHolder> adapter =
            new Adapter(R.layout.item_apply_user);

    private class Adapter extends BaseQuickAdapter<YMApplyUserListEntity.RecordsBean, BaseViewHolder> implements LoadMoreModule {

        public Adapter(int layoutResId) {
            super(layoutResId);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void convert(@NonNull BaseViewHolder holder, YMApplyUserListEntity.RecordsBean item) {
            holder.setText(R.id.tv_name, item.getUsername());
            ImageUtils.loadImage(holder.getView(R.id.iv_icon), item.getUserAvatar());
            TextView remark = holder.getView(R.id.remark);
            if (!TextUtils.isEmpty(item.getUtMsg())) {
                remark.setVisibility(View.VISIBLE);
                remark.setText("留言: " + item.getUtMsg());
            } else {
                remark.setVisibility(View.GONE);
            }

            LinearLayout optionLayout = holder.getView(R.id.optionLayout);
            TextView status = holder.getView(R.id.status);
            if (item.getUtState() == 2 || item.getUtState() == 3) {
                optionLayout.setVisibility(View.GONE);
                status.setVisibility(View.VISIBLE);
                if (item.getUtState() == 2) {
                    status.setText("已添加");
                } else {
                    status.setText("已拒绝");
                }
            } else {
                optionLayout.setVisibility(View.VISIBLE);
                status.setVisibility(View.GONE);
            }

            holder.getView(R.id.agree).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleFriend(true, item.getUfid());
                }
            });

            holder.getView(R.id.refuse).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleFriend(false, item.getUfid());
                }
            });

        }
    }

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private int pageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend_list);
        initTitleBar();
        initRefreshLayout();
    }

    private void initRefreshLayout() {
        refreshLayout = findViewById(R.id.refreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        refreshLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getData();
            }
        });
        adapter.getLoadMoreModule().checkDisableLoadMoreIfNotFullPage();
        onRefresh();
    }

    private void getData() {
        pageIndex++;
        YMIMClient.friendManager().getFriendsApplyListWithPage(pageIndex, 10, new YMResultCallback<YMApplyUserListEntity>() {
            @Override
            public void onSuccess(YMApplyUserListEntity var1) {
                Log.e(TAG, "申请好友列表: " + var1.getRecords().size());
                stopRefresh();
                if (pageIndex == 1) {
                    adapter.setNewInstance(var1.getRecords());
                } else {
                    adapter.addData(var1.getRecords());
                }
                if (pageIndex >= var1.getTotal()) adapter.getLoadMoreModule().loadMoreEnd(false);
            }

            @Override
            public void onError(int errorCode) {
                stopRefresh();
            }
        });
    }

    private void handleFriend(Boolean isAgree, String id) {
        YMIMClient.friendManager().handleFriendApplyWithApplyId(id, isAgree, new YMResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean var1) {
                onRefresh();
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }

    private void stopRefresh() {
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }, 800);
    }

    private void initTitleBar() {
        TitleBarLayout titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("新的朋友", ITitleBarLayout.Position.MIDDLE);
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

    @Override
    public void onRefresh() {
        pageIndex = 0;
        getData();
    }
}