package com.yimaxiaoerlang.im_kit.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.dialog.LodingUtils;
import com.yimaxiaoerlang.im_kit.modlue.PrewImage;
import com.yimaxiaoerlang.im_kit.utils.Image;
import com.yimaxiaoerlang.im_kit.utils.ImageSize;
import com.yimaxiaoerlang.im_kit.utils.MediaFileUtil;
import com.yimaxiaoerlang.im_kit.utils.MessageReceiveListener;
import com.yimaxiaoerlang.im_kit.utils.MessageCenter;
import com.yimaxiaoerlang.im_kit.utils.SavePhoto;
import com.yimaxiaoerlang.im_kit.utils.TestImageLoader;
import com.yimaxiaoerlang.im_kit.utils.ToastUtils;
import com.yimaxiaoerlang.im_kit.utils.UserUtils;
import com.yimaxiaoerlang.im_kit.view.MessageInpitView;
import com.yimaxiaoerlang.im_kit.view.MessageInputListener;
import com.yimaxiaoerlang.im_kit.view.messageview.ItemMessageClickListener;
import com.yimaxiaoerlang.im_kit.view.messageview.MessageItemView;
import com.previewlibrary.GPreviewBuilder;
import com.previewlibrary.ZoomMediaLoader;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.impl.ScrollBoundaryDeciderAdapter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.wuhenzhizao.titlebar.utils.KeyboardConflictCompat;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.yimaxiaoerlang.rtc_kit.model.CallUser;
import com.yimaxiaoerlang.rtc_kit.ui.CallPersonSelectActivity;
import com.yimaxiaoerlang.rtc_kit.ui.CallSingleActivity;
import com.yimaxiaoerlang.im_core.core.YMIMClient;
import com.yimaxiaoerlang.im_core.core.other.YMResultCallback;
import com.yimaxiaoerlang.im_core.model.conversation.YMConversation;
import com.yimaxiaoerlang.im_core.model.message.YMImageMessage;
import com.yimaxiaoerlang.im_core.model.message.YMMessage;
import com.yimaxiaoerlang.im_core.model.message.YMTextMessage;
import com.yimaxiaoerlang.im_core.model.message.YMVideoMessage;
import com.yimaxiaoerlang.im_core.model.message.YMVoiceMessage;
import com.yimaxiaoerlang.im_core.model.YMPersonnel;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends FragmentActivity implements MessageReceiveListener {
    private static final String TAG = "ChatActivity";
    private YMConversation conversation;
    private YMConversation conversationInfo;
    private int index = 1;
    private boolean nodata = false;
    private int CMERA_REQUEST_CODE = 220;
    private int PHOTO_REQUEST_CODE = 222;

    private BaseQuickAdapter<YMMessage, BaseViewHolder> adapter = new BaseQuickAdapter<YMMessage, BaseViewHolder>(R.layout.item_message) {
        @Override
        public void convert(BaseViewHolder holder, YMMessage item) {
            ((MessageItemView) holder.getView(R.id.item_messgae)).setMessage(item);
            ((MessageItemView) holder.getView(R.id.item_messgae))
                    .setItemClickListener(new ItemMessageClickListener() {
                        @Override
                        public void video(String url) {
                            Intent intent = new Intent(ChatActivity.this, PlayVideoActivity.class);
                            intent.putExtra("url", url);
                            startActivity(intent);
                        }

                        @Override
                        public void image(String url) {
                            openImage(url);
                        }

                        @Override
                        public void custom(String json) {
                            sendCustomMessage(json);
                        }
                    });
        }

    };
    private RecyclerView recycler;
    private ClassicsFooter footer;
    private SmartRefreshLayout refreshLayout;
    private MessageInpitView inputView;
    private CommonTitleBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ZoomMediaLoader.getInstance().init(new TestImageLoader());
        initView();
        initData();
    }

    private void initView() {
        recycler = findViewById(R.id.groupMemberRecycler);
        footer = findViewById(R.id.footer);
        refreshLayout = findViewById(R.id.refreshLayout);
        inputView = findViewById(R.id.input_view);
        titleBar = findViewById(R.id.titlebar);

        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                    finish();
                } else if (action == CommonTitleBar.ACTION_RIGHT_BUTTON) {
                    //设置
                    sendSeeting();
                    finish();

//                    if (conversation.getConversationType() == YMConversation.ConversationType.GROUP) {
//                        GroupInfoActivity.start(conversation.getGroupId(), ChatActivity.this);
//                    } else if (conversation.getConversationType() == YMConversation.ConversationType.CHATROOM) {
//                        ChatRoomInfoActivity.start(conversation.getGroupId(), ChatActivity.this);
//                    } else {
//                        Log.e(TAG, "onClicked: " + conversation.getTargetId());
//                        FriendProfileActivity.start(conversation.getTargetId(), conversation.getGroupId(), ChatActivity.this);
//                    }
                }
            }
        });


        recycler.setLayoutManager(new LinearLayoutManager(this));
//        ((LinearLayoutManager) recycler.getLayoutManager()).setStackFromEnd(true);
        View arrow = footer.findViewById(ClassicsFooter.ID_IMAGE_ARROW);
        arrow.setScaleY(-1f);//必须设置

        recycler.setScaleY(-1f);//必须设置
        refreshLayout.setEnableRefresh(false);//必须关闭
        refreshLayout.setEnableAutoLoadMore(true);//必须关闭
        refreshLayout.setEnableNestedScroll(false);//必须关闭
        refreshLayout.setEnableScrollContentWhenLoaded(true);//必须关闭
        refreshLayout.getLayout().setScaleY(-1);//必须设置
        refreshLayout.setScrollBoundaryDecider(new ScrollBoundaryDeciderAdapter() {
            @Override
            public boolean canLoadMore(View content) {
                return super.canRefresh(content);//必须替换
            }
        });
        //监听加载，而不是监听 刷新
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (nodata) {
                            refreshLayout.finishLoadMore();
                            return;
                        }
                        index++;
                        getMessageList();
                    }
                }, 2000);
            }
        });


        recycler.setAdapter(adapter);
        //输入内容的一个监听
        inputView.setListener(new MessageInputListener() {
            @Override
            public void sendText(String text) {
                sendTextMessgae(text);
            }

            @Override
            public void sendVoice(String file, int duration) {
                sendVoiceMessgae(file, duration);
            }

            @Override
            public void selectPhoto() {
//                openPhotoSelect();
                Matisse.from(ChatActivity.this)
                        .choose(MimeType.ofAll())
                        .countable(true)
                        .maxSelectable(9)
//                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
//                        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new PicassoEngine())
                        .showPreview(false) // Default is `true`
                        .forResult(PHOTO_REQUEST_CODE);
            }

            @Override
            public void shoot() {
                startActivityForResult(
                        new Intent(ChatActivity.this, CmeraActivity.class),
                        CMERA_REQUEST_CODE
                );
            }

            @Override
            public void call() {
                if (conversationInfo.getConversationType() == YMConversation.ConversationType.GROUP) {

                    if (conversationInfo == null || conversation.getPersonnels() == null || conversation.getPersonnels().isEmpty()) {
                        ToastUtils.normal("没有可以呼叫的人");
                        return;
                    }
                    ArrayList<CallUser> users = new ArrayList<>();

                    for (YMPersonnel personnel : conversation.getPersonnels()) {
                        if (!personnel.getUid().equals(UserUtils.getInstance().getUser().getUid())) {
                            users.add(new CallUser(personnel.getUid(), personnel.getName(), personnel.getAvatar()));
                        }
                    }
                    users.add(new CallUser(UserUtils.getInstance().getUser().getUid(), UserUtils.getInstance().getUser().getUsername(), UserUtils.getInstance().getUser().getUserAvatar()));
                    CallPersonSelectActivity.start(
                            ChatActivity.this,
                            conversationInfo.getGroupId(),
                            users,
                            true
                    );

                } else {
                    if (conversationInfo == null) {
                        ToastUtils.normal("没有可以呼叫的人");
                        return;
                    }
                    CallSingleActivity.call(
                            ChatActivity.this,
                            conversationInfo.getTargetId(),
                            conversationInfo.getGroupId(),
                            conversationInfo.getConversationTitle(),
                            conversationInfo.getPortraitUrl(),
                            true
                    );
                }
            }

            @Override
            public void greetingCard() {
                sendGreetingCard();
            }
        });
    }


    /**
     * 初始化数据
     */
    private void initData() {
        conversation = (YMConversation) getIntent().getSerializableExtra("conversation");
        initTitle();
        initChat();
    }

    /**
     * 初始化标题
     */
    private void initTitle() {
        if (conversation != null) {
            //设置标题
            titleBar.getCenterTextView().setText(conversation.getConversationTitle());
        }
    }

    /**
     * 初始化聊天
     */
    private void initChat() {
        if (conversation != null) {
            Log.e(TAG, "initChat: " + conversation.getTargetId());
            if (conversation.getConversationType() == YMConversation.ConversationType.PRIVATE) {//单聊
                createPrivateChat(conversation.getTargetId());
            } else if (conversation.getConversationType() == YMConversation.ConversationType.GROUP || conversation.getConversationType() == YMConversation.ConversationType.CHATROOM) {////群聊
                conversationInfo = conversation;
                getMessageList();
                registerMessageReceive();
            }

        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> obtainPathResult = Matisse.obtainPathResult(data);
            if (obtainPathResult != null) {
                for (String s : obtainPathResult) {
                    if (MediaFileUtil.isImageFileType(s)) {//图片
                        sendImageMessage(s);
                    } else {
                        MediaPlayer player = new MediaPlayer();
                        double duration = 0;
                        try {
                            player.setDataSource(s);
                            player.prepare();
                            duration = player.getDuration();
                        } catch (IOException e) {
//                            e.printStackTrace();
                        }
                        sendVideoMessage(s, ((int) (duration / 1000)));
                        player.release();
                    }
                }
            }

            return;
        }

        if (requestCode != CMERA_REQUEST_CODE) {
            return;
        }

        if (data != null) {
            if (data.getExtras() != null) {
                String url = data.getExtras().getString("url");
                if (MediaFileUtil.isImageFileType(url)) {//图片
                    sendImageMessage(url);
                } else {
                    sendVideoMessage(url, data.getExtras().getInt("time"));
                }
            }
        }

    }

    private void createPrivateChat(String targetId) {
        YMIMClient.chatManager().joinPrivateChatWithTargetId(targetId, new
                YMResultCallback<YMConversation>() {
                    @Override
                    public void onSuccess(YMConversation var1) {

                        getConversationWithTargetId(var1.getGroupId());
                    }

                    public void onError(int errorCode) {

                    }

                });
    }

    /**
     * 获取群聊信息
     */
    private void getConversationWithTargetId(String groupId) {

        YMIMClient.chatManager().getConversationWithTargetId(groupId, new
                YMResultCallback<YMConversation>() {
                    @Override
                    public void onSuccess(YMConversation conversation) {
                        conversation.getUnreadMessageCount();
//                conversationInfo = var1
                        if (conversation == null) {
                            ToastUtils.normal("获取信息错误不能聊天");
                            finish();
                            return;
                        }
                        LodingUtils.getInstance().stopLoading();
                        conversationInfo = conversation;
                        getMessageList();
                        registerMessageReceive();

                    }

                    @Override
                    public void onError(int errorCode) {
                        LodingUtils.getInstance().stopLoading();
                        ToastUtils.normal("发生错误");
                    }

                });
    }


    /**
     * 注册接收消息的监听
     */
    private void registerMessageReceive() {
        MessageCenter.getInstance().setChatReceiveListener(conversationInfo.getGroupId(), this);
    }

    /**
     * 获取消息列表
     */
    private void getMessageList() {
        YMIMClient.chatManager().getHistoryMessageWithTargetId(conversationInfo.getGroupId(), index, 20, new
                YMResultCallback<List<YMMessage>>() {
                    @Override
                    public void onSuccess(List<YMMessage> var1) {
                        if (index == 1) {
                            adapter.setList(var1);
                            recycler.scrollToPosition(var1.size() - 1);

                            if (!var1.isEmpty()) {
                                readLastMessage(var1.get(var1.size() - 1));
                            }
                        } else {
                            refreshLayout.finishLoadMore();
                            adapter.addData(0, var1);
                            recycler.scrollToPosition(var1.size());

                        }


                    }

                    @Override
                    public void onError(int errorCode) {
                        ToastUtils.normal("发生错误");
                    }

                });
    }

    private void openImage(String url) {
//        ImagePreview
//                .getInstance()
//                .setContext(this)
//                .setImage(url)
//
//                //打开下载按钮
//                .setShowDownButton(false)
//                // 开启预览
//                .start();
//        List<UserViewInfo> stringList=new ArrayList<>();

        ArrayList list = new ArrayList();
        list.add(new PrewImage(url));
        GPreviewBuilder.from(this)
//                .setData(list)
                .setSingleData(new PrewImage(url))
                .setDuration(0)
                .setCurrentIndex(0)
                .setSingleFling(true)//是否在黑屏区域点击返回
                .setDrag(true)//是否禁用图片拖拽返回
                .setSingleShowType(false)
                .setType(GPreviewBuilder.IndicatorType.Number)//指示器类型
                .start();//启动

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MessageCenter.getInstance().cleanChatReceiveListener();
        sendDestroy();
    }

    @Override
    public void onReceive(YMMessage message) {
        Log.e(TAG, "onReceive: " + ((YMTextMessage) message.getContent()).getContent());
        adapter.addData(message);
        recycler.smoothScrollToPosition(adapter.getItemCount() - 1);
        readLastMessage(message);
    }

    /**
     * 阅读最后一条消息
     */
    private void readLastMessage(YMMessage message) {
        YMIMClient.getInstance().readMessage(message);
    }


    /**
     * 图片视频选择
     */
    private void openPhotoSelect() {
//        EasyPhotos.createAlbum(this, false, false, GlideEngine.getInstance())
//                .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
////            .complexSelector(false, 2, 3) //参数说明：是否只能选择单类型，视频数，图片数。
//                .complexSelector(true, 3, 9)
//                .start(new SelectCallback() {
//
//                    @Override
//                    public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
//                        if (photos == null) return;
//                        for (Photo photo : photos) {
//                            if (MediaFileUtil.isImageFileType(photo.path)) {//图片
//                                sendImageMessage(photo.path);
//                            } else {
//                                sendVideoMessage(photo.path, ((int) (photo.duration / 1000)));
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//                });
    }

    /**
     * 发送文字消息
     */
    private void sendTextMessgae(String msg) {
        YMTextMessage content = YMTextMessage.create(msg);
        YMMessage message = new YMMessage(conversationInfo.getGroupId(), content);
        sendMessage(message);
    }

    /**
     * 发送语音消息
     */
    private void sendVoiceMessgae(String file, int duration) {
        YMVoiceMessage content = new YMVoiceMessage(duration, file);
        YMMessage message = new YMMessage(conversationInfo.getGroupId(), content);
        sendMessage(message);
    }

    /**
     * 发送图片消息
     */
    private void sendImageMessage(String file) {
        YMImageMessage content = new YMImageMessage(file);
        ImageSize size = Image.imageSize(file);
        content.setWidth(size.getWidth());
        content.setHeight(size.getHeight());
        YMMessage message = new YMMessage(conversationInfo.getGroupId(), content);
        sendMessage(message);
    }

    /**
     * 发送视频消息
     */
    private void sendVideoMessage(String file, int time) {
        YMVideoMessage content = new YMVideoMessage();
        content.setLocalPath(file);
        content.setDuration(time);
        Bitmap bitmap = Image.getVideoThumb(file);
        if (bitmap == null) {
            Log.e("EasyPhotos", "bitmap is null");
            return;
        }
        content.setWidth(bitmap.getWidth());
        content.setHeight(bitmap.getHeight());


        //获取录音保存位置
        String dir = getCacheDir() + "/thumb/";
        File newFile = new File(dir);
        if (!newFile.exists()) {
            newFile.mkdirs();
        }

        new SavePhoto(this).saveBitmap(bitmap, "thumb.png");
        content.setLocalThumbnail(dir + "thumb.png");
        YMMessage message = new YMMessage(conversationInfo.getGroupId(), content);
        sendMessage(message);
    }

    private void sendMessage(YMMessage message) {
        YMIMClient.chatManager().sendMessage(message, new YMResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean var1) {
                Log.e("Chat", "发送成功");
            }

            @Override
            public void onError(int errorCode) {
                Log.e("Chat", "发送失败");
            }

        });
    }

    /**
     * 自动关闭输入法
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                        hideSoftInputFromWindow(
                                v.getWindowToken(),
                                0
                        );
                if (v instanceof EditText) {
                    v.clearFocus();
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        return getWindow().superDispatchTouchEvent(ev) ? true : onTouchEvent(ev);

    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && v instanceof EditText) {
            int[] location = new int[]{0, 0};
            v.getLocationOnScreen(location);
            int left = location[0];
            int top = location[1];
            return (event.getX() < left || event.getX() > left + v.getWidth()
                    || event.getY() < top || event.getY() > top + v.getHeight());
        }
        return false;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        KeyboardConflictCompat.assistWindow(getWindow());
    }

    /**
     * 发送右上角事件
     */
    private void sendSeeting() {
        Intent intent = new Intent("com.mize.young.angel.report");
        intent.putExtra("uid", conversationInfo.getTargetId());
        intent.putExtra("gid", conversationInfo.getGroupId());
        intent.putExtra("name", conversationInfo.getConversationTitle());
        sendBroadcast(intent);

        finish();
    }

    /**
     * 发送贺卡事件
     */
    private void sendGreetingCard() {
        Intent intent = new Intent("com.mize.young.angel.greetingcard");
        intent.putExtra("uid", conversationInfo.getTargetId());
        intent.putExtra("gid", conversationInfo.getGroupId());
        intent.putExtra("name", conversationInfo.getConversationTitle());
        sendBroadcast(intent);
        finish();
    }

    private void sendCustomMessage(String json) {
        Intent intent = new Intent("com.mize.young.angel.custom_message");
        intent.putExtra("json", json);
        sendBroadcast(intent);
        finish();
    }

    /**
     * 发送销毁事件
     */
    private void sendDestroy() {
        sendBroadcast(new Intent("com.mize.young.angel.destroy"));
    }
}