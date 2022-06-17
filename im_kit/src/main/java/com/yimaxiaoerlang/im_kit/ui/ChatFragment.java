package com.yimaxiaoerlang.im_kit.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.previewlibrary.GPreviewBuilder;
import com.previewlibrary.ZoomMediaLoader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.impl.ScrollBoundaryDeciderAdapter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.yimaxiaoerlang.im_core.core.YMIMClient;
import com.yimaxiaoerlang.im_core.core.other.YMResultCallback;
import com.yimaxiaoerlang.im_core.model.YMGroupEntity;
import com.yimaxiaoerlang.im_core.model.YMPersonnel;
import com.yimaxiaoerlang.im_core.model.conversation.YMConversation;
import com.yimaxiaoerlang.im_core.model.message.YMImageMessage;
import com.yimaxiaoerlang.im_core.model.message.YMMessage;
import com.yimaxiaoerlang.im_core.model.message.YMTextMessage;
import com.yimaxiaoerlang.im_core.model.message.YMVideoMessage;
import com.yimaxiaoerlang.im_core.model.message.YMVoiceMessage;
import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.dialog.LodingUtils;
import com.yimaxiaoerlang.im_kit.modlue.PrewImage;
import com.yimaxiaoerlang.im_kit.utils.Image;
import com.yimaxiaoerlang.im_kit.utils.ImageSize;
import com.yimaxiaoerlang.im_kit.utils.MediaFileUtil;
import com.yimaxiaoerlang.im_kit.utils.MessageCenter;
import com.yimaxiaoerlang.im_kit.utils.MessageReceiveListener;
import com.yimaxiaoerlang.im_kit.utils.SavePhoto;
import com.yimaxiaoerlang.im_kit.utils.TestImageLoader;
import com.yimaxiaoerlang.im_kit.utils.ToastUtils;
import com.yimaxiaoerlang.im_kit.utils.UserUtils;
import com.yimaxiaoerlang.im_kit.view.MessageInpitView;
import com.yimaxiaoerlang.im_kit.view.MessageInputListener;
import com.yimaxiaoerlang.im_kit.view.messageview.ItemMessageClickListener;
import com.yimaxiaoerlang.im_kit.view.messageview.MessageItemView;
import com.yimaxiaoerlang.rtc_kit.model.CallUser;
import com.yimaxiaoerlang.rtc_kit.ui.CallPersonSelectActivity;
import com.yimaxiaoerlang.rtc_kit.ui.CallSingleActivity;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment implements MessageReceiveListener {

    private static final String TAG = ChatFragment.class.getSimpleName();
    private YMGroupEntity conversationInfo;
    private int index = 1;
    private boolean nodata = false;
    private int CMERA_REQUEST_CODE = 220;
    private int PHOTO_REQUEST_CODE = 222;
    private YMConversation.ConversationType conversationType = YMConversation.ConversationType.PRIVATE;
    private String conversationId = "";

    public static ChatFragment newInstance(int conversationType, String gid) {

        Bundle args = new Bundle();
        args.putInt("conversationType", conversationType);
        args.putString("gid", gid);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private BaseQuickAdapter<YMMessage, BaseViewHolder> adapter = new BaseQuickAdapter<YMMessage, BaseViewHolder>(R.layout.item_message) {
        @Override
        public void convert(BaseViewHolder holder, YMMessage item) {
            ((MessageItemView) holder.getView(R.id.item_messgae)).setMessage(item);
            ((MessageItemView) holder.getView(R.id.item_messgae))
                    .setItemClickListener(new ItemMessageClickListener() {
                        @Override
                        public void video(String url) {
                            Intent intent = new Intent(requireContext(), PlayVideoActivity.class);
                            intent.putExtra("url", url);
                            startActivity(intent);
                        }

                        @Override
                        public void image(String url) {
                            openImage(url);
                        }

                        @Override
                        public void custom(String json) {

                        }
                    });
        }

    };
    private RecyclerView recycler;
    private ClassicsFooter footer;
    private SmartRefreshLayout refreshLayout;
    private MessageInpitView inputView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ZoomMediaLoader.getInstance().init(new TestImageLoader());
        initData();
    }

    private void initView(View root) {
        recycler = root.findViewById(R.id.chatRecycler);
        footer = root.findViewById(R.id.footer);
        refreshLayout = root.findViewById(R.id.refreshLayout);
        inputView = root.findViewById(R.id.input_view);


        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
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
                Matisse.from(requireActivity())
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
                        new Intent(requireContext(), CmeraActivity.class),
                        CMERA_REQUEST_CODE
                );
            }

            @Override
            public void call() {
                if (conversationType == YMConversation.ConversationType.GROUP) {

                    if (conversationInfo == null || conversationInfo.getUserList() == null || conversationInfo.getUserList().isEmpty()) {
                        ToastUtils.normal("没有可以呼叫的人");
                        return;
                    }
                    ArrayList<CallUser> users = new ArrayList<>();

                    for (YMGroupEntity.UserListBean personnel : conversationInfo.getUserList()) {
                        if (!personnel.getUserId().equals(UserUtils.getInstance().getUser().getUid())) {
                            users.add(new CallUser(personnel.getUserId(), personnel.getUsername(), personnel.getUserAvatar()));
                        }
                    }
                    users.add(new CallUser(UserUtils.getInstance().getUser().getUid(), UserUtils.getInstance().getUser().getUsername(), UserUtils.getInstance().getUser().getUserAvatar()));
                    CallPersonSelectActivity.start(
                            requireContext(),
                            conversationId,
                            users,
                            true
                    );

                } else {
                    if (conversationInfo == null) {
                        ToastUtils.normal("没有可以呼叫的人");
                        return;
                    }
                    CallSingleActivity.call(
                            requireActivity(),
                            conversationInfo.getUserList().get(conversationInfo.getUserList().size() - 1).getUserId(),
                            conversationInfo.getGroup().getGroupId(),
                            conversationInfo.getGroup().getGroupName(),
                            conversationInfo.getGroup().getGroupAvatar(),
                            true
                    );
                }
            }

        });
    }

    public MessageInpitView getInputView() {
        return inputView;
    }


    /**
     * 初始化数据
     */
    private void initData() {
        conversationId = requireArguments().getString("gid");
        int type = requireArguments().getInt("conversationType", 1);
        switch (type) {
            case 0:
                conversationType = YMConversation.ConversationType.PRIVATE;
                break;
            case 1:
                conversationType = YMConversation.ConversationType.GROUP;
                break;
            case 2:
                conversationType = YMConversation.ConversationType.CHATROOM;
                break;
        }
        initChat();
    }

    /**
     * 初始化聊天
     */
    private void initChat() {
        // 获取会话信息
        YMIMClient.chatManager().getConversationInfoWithTargetId(conversationId, new YMResultCallback<YMGroupEntity>() {
            @Override
            public void onSuccess(YMGroupEntity var1) {
                conversationInfo = var1;
                if (conversationInfo != null) {
                    LodingUtils.getInstance().stopLoading();
                    getMessageList();
                    registerMessageReceive();
                } else {
                    ToastUtils.normal("获取信息错误不能聊天");
                }
            }

            @Override
            public void onError(int errorCode) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == PHOTO_REQUEST_CODE) {
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

    /**
     * 注册接收消息的监听
     */
    private void registerMessageReceive() {
        MessageCenter.getInstance().setChatReceiveListener(conversationId, this);
    }

    /**
     * 获取消息列表
     */
    private void getMessageList() {
        YMIMClient.chatManager().getHistoryMessageWithTargetId(conversationId, index, 20, new
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
     * 发送文字消息
     */
    private void sendTextMessgae(String msg) {
        YMTextMessage content = YMTextMessage.create(msg);
        YMMessage message = new YMMessage(conversationId, content);
        sendMessage(message);
    }

    /**
     * 发送语音消息
     */
    private void sendVoiceMessgae(String file, int duration) {
        YMVoiceMessage content = new YMVoiceMessage(duration, file);
        YMMessage message = new YMMessage(conversationId, content);
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
        YMMessage message = new YMMessage(conversationId, content);
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
        String dir = requireActivity().getCacheDir() + "/thumb/";
        File newFile = new File(dir);
        if (!newFile.exists()) {
            newFile.mkdirs();
        }

        new SavePhoto(requireContext()).saveBitmap(bitmap, "thumb.png");
        content.setLocalThumbnail(dir + "thumb.png");
        YMMessage message = new YMMessage(conversationId, content);
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

}
