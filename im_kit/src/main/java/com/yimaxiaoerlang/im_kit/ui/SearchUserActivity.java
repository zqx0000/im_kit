package com.yimaxiaoerlang.im_kit.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends AppCompatActivity {
    public static final int SEARCH_FRIEND_REQ = 2201;
    public static final String SEARCH_FRIEND_RESULT = "search_friend_result";

    public static void start(Activity context) {
        Intent starter = new Intent(context, SearchUserActivity.class);
        context.startActivityForResult(starter, SEARCH_FRIEND_REQ);
    }

    private final BaseQuickAdapter<YMContacts, BaseViewHolder> adapter = new BaseQuickAdapter<YMContacts, BaseViewHolder>(R.layout.item_select_friend) {
        @Override
        public void convert(BaseViewHolder holder, YMContacts item) {
            holder.setText(R.id.tv_name, item.getUsername());
            ImageUtils.loadImage(holder.getView(R.id.iv_icon), item.getUserAvatar());
            CheckBox checkBox = holder.getView(R.id.checkbox);
            checkBox.setChecked(contacts == item);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contacts = item;
                }
            });
        }


    };

    private final List<YMContacts> friends = new ArrayList<>();
    private YMContacts contacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
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
        initTitleBar();
        initRecyclerView();
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
                if (contacts == null) {
                    ToastUtils.normal("请至少选择一位好友");
                    return;
                }
                Intent intent = getIntent();
                intent.putExtra(SEARCH_FRIEND_RESULT, contacts);
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
                contacts = adapter.getItem(position);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initData(String keyword) {
        YMIMClient.friendManager().searchFriendsWithKeyword(keyword, new YMResultCallback<List<YMContacts>>() {
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