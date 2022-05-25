package com.yimaxiaoerlang.im_kit.ui;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.utils.SystemBarHelper;
import com.yimaxiaoerlang.im_kit.utils.UserUtils;
import com.yimaxiaoerlang.im_kit.view.DragPointView;
import com.yimaxiaoerlang.im_kit.view.MainBottomTabGroupView;
import com.yimaxiaoerlang.im_kit.view.MainBottomTabItem;
import com.yimaxiaoerlang.im_kit.view.TabGroupView;
import com.yimaxiaoerlang.im_kit.view.TabItem;
import com.yimaxiaoerlang.im_kit.view.actionbar.menu.Menu;
import com.yimaxiaoerlang.im_kit.view.actionbar.menu.PopActionClickListener;
import com.yimaxiaoerlang.im_kit.view.actionbar.menu.PopMenuAction;
import com.yimaxiaoerlang.im_core.core.YMIMClient;
import com.yimaxiaoerlang.im_core.core.other.YMResultCallback;
import com.yimaxiaoerlang.im_core.model.YMRoomEntity;
import com.yimaxiaoerlang.im_core.model.conversation.YMConversation;
import com.yimaxiaoerlang.im_core.model.YMContacts;
import com.yimaxiaoerlang.im_core.model.YMGroupEntity;

import java.util.ArrayList;
import java.util.List;

public class IndexActivity extends AppCompatActivity {
    private static final String TAG = "IndexActivity";

    /**
     * tab 项枚举
     */
    public enum Tab {
        /**
         * 聊天
         */
        CHAT(0),

        /**
         * 联系人
         */
        CONTACTS(1),

        /**
         * 聊天室
         */
        CHATROOM(2),

        /**
         * 我的
         */
        ME(3);
        private final int value;


        Tab(int value) {
            this.value = value;
        }

        public Tab getType(int value) {
            for (Tab tab : values()) {
                if (value == tab.value) {
                    return tab;
                }
            }
            return null;

        }

    }

    /**
     * tabs 的图片资源
     */
    private ArrayList<Integer> tabImageRes = new ArrayList<>();

    /**
     * 各个 Fragment 界面
     */
    private ArrayList<Fragment> fragments = new ArrayList<>();

    private MainBottomTabGroupView tgBottomTabs;
    private ViewPager vpMainContainer;
    private TextView tvTitle;
    private ImageButton btnMore;
    private RelativeLayout btnSearch;
    private Menu menu;
    private View titleBar;
    private boolean isChatroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        tabImageRes.add(R.drawable.seal_tab_chat_selector);
        tabImageRes.add(R.drawable.seal_tab_contact_list_selector);
        tabImageRes.add(R.drawable.seal_tab_find_selector);
        tabImageRes.add(R.drawable.seal_tab_me_selector);

        tgBottomTabs = findViewById(R.id.tg_bottom_tabs);
        vpMainContainer = findViewById(R.id.vp_main_container);
        tvTitle = findViewById(R.id.tv_title);
        btnMore = findViewById(R.id.btn_more);
        btnSearch = findViewById(R.id.btn_search);
        titleBar = findViewById(R.id.index_title);
        SystemBarHelper.setStatusBarDarkMode(this);
        SystemBarHelper.immersiveStatusBar(this, 0f);
        SystemBarHelper.setPadding(this, titleBar);
        initTabs();
        setConversationMenu(false);
//        // 初始化 fragment 的 viewpager
        initFragmentViewPager();
////        supportActionBar?.hide()
//        getSupportActionBar().hide();


        findViewById(R.id.btn_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menu == null) {
                    return;
                }
                if (menu.isShowing()) {
                    menu.hide();
                } else {
                    menu.show();
                }
            }
        });


    }

    private void startChatActivity(String tid, String title) {
        YMConversation conversation = new YMConversation();
        conversation.setTargetId(tid);
        conversation.setConversationType(YMConversation.ConversationType.PRIVATE);
        conversation.setConversationTitle(title);
        Intent intent = new Intent(IndexActivity.this, ChatActivity.class);
        intent.putExtra("conversation", conversation);
        startActivity(intent);
    }

    private void initTabs() {
        // 初始化 tab
        ArrayList<TabItem> items = new ArrayList<>();
        String[] stringArray = getResources().getStringArray(R.array.tab_names);
        ArrayList<TabItem.AnimationDrawableBean> animationDrawableList = new ArrayList<TabItem.AnimationDrawableBean>();
        animationDrawableList.add(
                new TabItem.AnimationDrawableBean(
                        R.drawable.tab_chat_0,
                        R.drawable.tab_chat_animation_list
                )
        );
        animationDrawableList.add(
                new TabItem.AnimationDrawableBean(
                        R.drawable.tab_contacts_0,
                        R.drawable.tab_contacts_animation_list
                )
        );
        animationDrawableList.add(
                new TabItem.AnimationDrawableBean(
                        R.drawable.tab_chatroom_0,
                        R.drawable.tab_chatroom_animation_list
                )
        );
        animationDrawableList.add(
                new TabItem.AnimationDrawableBean(
                        R.drawable.tab_me_0,
                        R.drawable.tab_me_animation_list
                )
        );
        for (Tab tab : Tab.values()) {
            TabItem tabItem = new TabItem();
            tabItem.id = tab.value;
            tabItem.text = stringArray[tab.value];
            tabItem.animationDrawable = animationDrawableList.get(tab.value);
            items.add(tabItem);
        }

        tgBottomTabs.initView(items, new TabGroupView.OnTabSelectedListener() {
            @Override
            public void onSelected(View view, TabItem item) {
                // 当点击 tab 的后， 也要切换到正确的 fragment 页面
                int currentItem = vpMainContainer.getCurrentItem();
                if (currentItem != item.id) {
                    // 切换布局
                    vpMainContainer.setCurrentItem(item.id);
                    if (item.id == Tab.ME.value) {
                        // 如果是我的页面， 则隐藏红点
//                        ((MainBottomTabItem)tgBottomTabs.getView(Tab.ME.value)).
//                        setRedVisibility(
//                                View.GONE
//                        )
                        ((MainBottomTabItem) tgBottomTabs.getView(Tab.ME.value)).setRedVisibility(View.GONE);


                        tvTitle.setText(getResources().getStringArray(R.array.tab_names)[3]);
                        btnMore.setVisibility(View.GONE);
                        btnSearch.setVisibility(View.GONE);
                    }
                } else if (item.id == Tab.CHAT.value) {
                    setConversationMenu(false);
                    initView(R.drawable.seal_ic_main_more, 0);
                } else if (item.id == Tab.CONTACTS.value) {
                    setConversationMenu(false);
                    initView(R.drawable.seal_ic_main_add_friend, 1);
                } else if (item.id == Tab.CHATROOM.value) {
                    setConversationMenu(true);
                    initView(R.drawable.seal_ic_main_more, 0);
                    tvTitle.setText(getResources().getStringArray(R.array.tab_names)[2]);
                } else if (item.id == Tab.ME.value) {
                    tvTitle.setText(getResources().getStringArray(R.array.tab_names)[3]);
                    btnMore.setVisibility(View.GONE);
                    btnSearch.setVisibility(View.GONE);
                }
            }
        });
        tgBottomTabs.setOnTabDoubleClickListener(new
                                                         MainBottomTabGroupView.OnTabDoubleClickListener() {
                                                             @Override
                                                             public void onDoubleClick(TabItem item, View view) {
                                                                 // 双击定位到某一个未读消息位置
                                                                 if (item.id == Tab.CHAT.value) {
                                                                     //todo
//                    MainConversationListFragment fragment = (MainConversationListFragment) fragments.get(Tab.CHAT.getValue());
//                    fragment.focusUnreadItem();
                                                                 }
                                                             }
                                                         });


        // 未读数拖拽
        ((MainBottomTabItem) tgBottomTabs.getView(Tab.CHAT.value)).setTabUnReadNumDragListener(new DragPointView.OnDragListencer() {
            @Override
            public void onDragOut() {
                ((MainBottomTabItem) tgBottomTabs.getView(Tab.CHAT.value)).setNumVisibility(
                        View.GONE
                );
                //                    showToast(getString(R.string.seal_main_toast_unread_clear_success))
                clearUnreadStatus();
                //隐藏消息的小红点
                ((MainBottomTabItem) tgBottomTabs.getView(Tab.CHAT.value)).setNumVisibility(
                        View.GONE
                );
            }
        });
    }

    private void initView(int p, int i) {
        btnMore.setVisibility(View.VISIBLE);
        btnSearch.setVisibility(View.GONE);
        btnMore.setImageDrawable(getResources().getDrawable(p));
        tvTitle.setText(getResources().getStringArray(R.array.tab_names)[i]);
    }

    /**
     * 清理未读消息状态
     */
    private void clearUnreadStatus() {
//        if (mainViewModel != null) {
//            mainViewModel.clearMessageUnreadStatus()
//        }
    }

    /**
     * 初始化 initFragmentViewPager
     */
    private void initFragmentViewPager() {

        fragments.add(new ConversationListFragment());
        fragments.add(new ContactsFragment());
        fragments.add(new ChatRoomFragment());
        fragments.add(new MainMeFragment());

        // ViewPager 的 Adpater
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(
                getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            public int getCount() {
                return fragments.size();
            }
        };

        vpMainContainer.setAdapter(fragmentPagerAdapter);
        vpMainContainer.setOffscreenPageLimit(fragments.size());
        // 设置页面切换监听
        // 设置页面切换监听
        vpMainContainer.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(
                            int position,
                            float positionOffset,
                            int positionOffsetPixels
                    ) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        // 当页面切换完成之后， 同时也要把 tab 设置到正确的位置
                        Log.e("zgj", "index__$position");
                        tgBottomTabs.setSelected(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

    }

    private void setConversationMenu(boolean isChatroom) {
        this.isChatroom = isChatroom;
        menu = new Menu(this, btnMore);
        PopActionClickListener popActionClickListener = new PopActionClickListener() {
            @Override
            public void onActionClick(int position, Object data) {
                PopMenuAction action = (PopMenuAction) data;
                if (TextUtils.equals(action.getActionName(), getResources().getString(R.string.start_conversation))) {
                    SelectFriendActivity.start(IndexActivity.this, false);
                }

                if (TextUtils.equals(action.getActionName(), getResources().getString(R.string.add_user_friend))) {
                    SearchUserActivity.start(IndexActivity.this);
                }

                if (TextUtils.equals(action.getActionName(), getResources().getString(R.string.create_group_chat))) {
                    SelectFriendActivity.start(IndexActivity.this, true);
                }

                if (TextUtils.equals(action.getActionName(), getResources().getString(R.string.create_chat_room))) {
                    SelectFriendActivity.start(IndexActivity.this, true);
                }

                if (TextUtils.equals(action.getActionName(), getResources().getString(R.string.search_chat_room))) {
                    ChatRoomListActivity.start(IndexActivity.this);
                }

                menu.hide();
            }
        };

        // 设置右上角+号显示PopAction
        List<PopMenuAction> menuActions = new ArrayList<>();

        PopMenuAction action = new PopMenuAction();

        action.setActionName(getResources().getString(R.string.start_conversation));
        action.setActionClickListener(popActionClickListener);
        action.setIconResId(R.mipmap.create_c2c);
        menuActions.add(action);

        action = new PopMenuAction();
        action.setActionName(getResources().getString(R.string.add_user_friend));
        action.setActionClickListener(popActionClickListener);
        action.setIconResId(R.mipmap.create_c2c);
        menuActions.add(action);

        action = new PopMenuAction();
        action.setActionName(getResources().getString(R.string.create_group_chat));
        action.setIconResId(R.mipmap.group_icon);
        action.setActionClickListener(popActionClickListener);
        menuActions.add(action);

        if (isChatroom) {
            menuActions.clear();
            action = new PopMenuAction();
            action.setActionName(getResources().getString(R.string.create_chat_room));
            action.setIconResId(R.mipmap.group_icon);
            action.setActionClickListener(popActionClickListener);
            menuActions.add(action);
            action = new PopMenuAction();
            action.setActionName(getResources().getString(R.string.search_chat_room));
            action.setIconResId(R.mipmap.group_icon);
            action.setActionClickListener(popActionClickListener);
            menuActions.add(action);
        }

        menu.setMenuAction(menuActions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == Activity.RESULT_OK) {
            if (requestCode == SelectFriendActivity.SELECT_FRIEND_REQ) {
                List<YMContacts> selectList = new ArrayList<>();
                selectList = (ArrayList<YMContacts>) data.getSerializableExtra(SelectFriendActivity.SELECT_FRIEND_RESULT);
                Log.e(TAG, "onActivityResult: " + selectList.size());
                if (selectList.size() == 1) {
                    // 创建会话
                    YMContacts contacts = selectList.get(0);
                    startChatActivity(contacts.getTid(), contacts.getUsername());
                } else {
                    // 创建群聊
                    List<YMContacts> groupList = new ArrayList<>();
                    YMContacts lord = new YMContacts();
                    lord.setUserAvatar(UserUtils.getInstance().getUser().getUserAvatar());
                    lord.setUsername(UserUtils.getInstance().getUser().getUsername());
                    lord.setTid(UserUtils.getInstance().getUser().getUid());
                    groupList.add(lord);
                    groupList.addAll(selectList);
                    List<String> idsList = new ArrayList<>();
                    StringBuffer nameSb = new StringBuffer();
                    for (int i = 0; i < groupList.size(); i++) {
                        if (i > 0) {
                            nameSb.append(",");
                        }
                        idsList.add(groupList.get(i).getTid() + "");
                        nameSb.append(groupList.get(i).getUsername());
                    }
                    if (isChatroom) {
                        YMIMClient.roomManager().createRoomWithName(nameSb.toString() + "的聊天室", UserUtils.getInstance().getUser().getUserAvatar(), idsList, new YMResultCallback<YMRoomEntity>() {
                            @Override
                            public void onSuccess(YMRoomEntity group) {

                            }

                            @Override
                            public void onError(int errorCode) {

                            }
                        });
                    } else {
                        YMIMClient.groupManager().createGroupWithName(nameSb.toString() + "的群聊", UserUtils.getInstance().getUser().getUserAvatar(), idsList, new YMResultCallback<YMGroupEntity>() {
                            @Override
                            public void onSuccess(YMGroupEntity group) {

                            }

                            @Override
                            public void onError(int errorCode) {

                            }
                        });
                    }
                }
            } else if (requestCode == SearchUserActivity.SEARCH_FRIEND_REQ) {
                // 搜索添加好友
                YMContacts contacts = (YMContacts) data.getSerializableExtra(SearchUserActivity.SEARCH_FRIEND_RESULT);
//                startChatActivity(contacts.getTid(), contacts.getUsername());
                AddFriendActivity.start(IndexActivity.this, contacts.getUsername(), contacts.getTid(), contacts.getUserAvatar());
            }
        }
    }
}