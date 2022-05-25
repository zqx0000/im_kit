package com.yimaxiaoerlang.im_kit.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.utils.ImageUtils;
import com.yimaxiaoerlang.im_kit.utils.SystemBarHelper;
import com.yimaxiaoerlang.im_kit.utils.ToastUtils;
import com.yimaxiaoerlang.im_kit.view.actionbar.ITitleBarLayout;
import com.yimaxiaoerlang.im_kit.view.actionbar.TitleBarLayout;
import com.yimaxiaoerlang.im_core.core.YMIMClient;
import com.yimaxiaoerlang.im_core.core.other.YMResultCallback;
import com.yimaxiaoerlang.im_core.model.YMContacts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectFriendActivity extends AppCompatActivity {
    private static final String IS_MUL = "is_mul";
    public static final int SELECT_FRIEND_REQ = 2200;
    public static final String SELECT_FRIEND_RESULT = "select_friend_result";

    public static void start(Activity context, Boolean isMul) {
        Intent starter = new Intent(context, SelectFriendActivity.class);
        starter.putExtra(IS_MUL, isMul);
        context.startActivityForResult(starter, SELECT_FRIEND_REQ);
    }

    private final BaseQuickAdapter<YMContacts, BaseViewHolder> adapter = new BaseQuickAdapter<YMContacts, BaseViewHolder>(R.layout.item_select_friend) {
        @Override
        public void convert(BaseViewHolder holder, YMContacts item) {
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
                        if (!isMul) selectedList.clear();
                        selectedList.add(item);
                    }
                }
            });
        }


    };

    private final List<YMContacts> friends = new ArrayList<>();
    private final List<YMContacts> selectedList = new ArrayList<>();
    private boolean isMul = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);
        isMul = getIntent().getBooleanExtra(IS_MUL, false);
        initTitleBar();
        initRecyclerView();
        initData();
    }

    private void initTitleBar() {
        TitleBarLayout titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("选择好友", ITitleBarLayout.Position.MIDDLE);
        SystemBarHelper.setStatusBarDarkMode(this);
        SystemBarHelper.immersiveStatusBar(this, 0f);
        SystemBarHelper.setPadding(this, titleBar);
        titleBar.getLeftGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titleBar.setTitle("完成", ITitleBarLayout.Position.RIGHT);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedList.isEmpty()) {
                    ToastUtils.normal("请至少选择一位好友");
                    return;
                }
                Intent intent = getIntent();
                intent.putExtra(SELECT_FRIEND_RESULT, (Serializable) selectedList);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter_, @NonNull View view, int position) {
                YMContacts item = adapter.getItem(position);
                if (selectedList.contains(item)) {
                    selectedList.remove(item);
                } else {
                    if (!isMul) selectedList.clear();
                    selectedList.add(item);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initData() {
        YMIMClient.friendManager().getFriendsListWithCompletion(new YMResultCallback<List<YMContacts>>() {
            @Override
            public void onSuccess(List<YMContacts> var1) {
                friends.clear();
                friends.addAll(var1);
                adapter.setList(friends);
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }
}