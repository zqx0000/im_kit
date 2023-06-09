package com.yimaxiaoerlang.im_kit.ui

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.freddy.kulakeyboard.library.KeyboardHelper
import com.freddy.kulakeyboard.library.util.DensityUtil
import com.previewlibrary.GPreviewBuilder
import com.previewlibrary.ZoomMediaLoader
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.impl.ScrollBoundaryDeciderAdapter
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.wuhenzhizao.titlebar.utils.KeyboardConflictCompat
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import com.wuhenzhizao.titlebar.widget.CommonTitleBar.OnTitleBarListener
import com.yimaxiaoerlang.im_core.core.YMIMClient
import com.yimaxiaoerlang.im_core.core.other.YMResultCallback
import com.yimaxiaoerlang.im_core.model.conversation.YMConversation
import com.yimaxiaoerlang.im_core.model.message.YMCustomMessage
import com.yimaxiaoerlang.im_core.model.message.YMImageMessage
import com.yimaxiaoerlang.im_core.model.message.YMMessage
import com.yimaxiaoerlang.im_core.model.message.YMTextMessage
import com.yimaxiaoerlang.im_core.model.message.YMVideoMessage
import com.yimaxiaoerlang.im_core.model.message.YMVoiceMessage
import com.yimaxiaoerlang.im_kit.R
import com.yimaxiaoerlang.im_kit.core.YMIMKit
import com.yimaxiaoerlang.im_kit.dialog.LodingUtils
import com.yimaxiaoerlang.im_kit.modlue.PrewImage
import com.yimaxiaoerlang.im_kit.utils.Image
import com.yimaxiaoerlang.im_kit.utils.KeyboardStateObserver
import com.yimaxiaoerlang.im_kit.utils.MediaFileUtil
import com.yimaxiaoerlang.im_kit.utils.MessageCenter
import com.yimaxiaoerlang.im_kit.utils.MessageReceiveListener
import com.yimaxiaoerlang.im_kit.utils.SavePhoto
import com.yimaxiaoerlang.im_kit.utils.TestImageLoader
import com.yimaxiaoerlang.im_kit.utils.ToastUtils
import com.yimaxiaoerlang.im_kit.utils.UserUtils
import com.yimaxiaoerlang.im_kit.view.messageview.ItemMessageClickListener
import com.yimaxiaoerlang.im_kit.view.messageview.MessageItemView
import com.yimaxiaoerlang.im_kit.view.newinput.CExpressionPanel
import com.yimaxiaoerlang.im_kit.view.newinput.CInputPanel
import com.yimaxiaoerlang.im_kit.view.newinput.CMorePanel
import com.yimaxiaoerlang.im_kit.view.popmenu.ChatPopMenu
import com.yimaxiaoerlang.im_kit.view.popmenu.ChatPopMenu.ChatPopMenuAction
import com.yimaxiaoerlang.rtc_kit.model.CallUser
import com.yimaxiaoerlang.rtc_kit.ui.CallPersonSelectActivity
import com.yimaxiaoerlang.rtc_kit.ui.CallSingleActivity
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.PicassoEngine
import java.io.File
import java.io.IOException

class NewChatActivity : AppCompatActivity(), MessageReceiveListener {
    private var conversation: YMConversation? = null
    private var conversationInfo: YMConversation? = null
    private var index = 1
    private val nodata = false
    private val CMERA_REQUEST_CODE = 220
    private val PHOTO_REQUEST_CODE = 222
    protected var mOnPopActionClickListener: ChatActivity.OnPopActionClickListener? = null
    protected var mPopActions: MutableList<ChatPopMenuAction> = ArrayList()
    private val mSelectedPosition = -1
    private var mChatPopMenu: ChatPopMenu? = null
    private val handler = Handler()
    var runnable = Runnable {
        if (mChatPopMenu != null) {
            mChatPopMenu!!.hide()
        }
    }
    private val adapter by lazy { Adapter() }
    private lateinit var recycler: RecyclerView
    private lateinit var footer: ClassicsFooter
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var titleBar: CommonTitleBar
    private lateinit var container: LinearLayout
    private lateinit var chatInputPanel: CInputPanel
    private lateinit var expressionPanel: CExpressionPanel
    private lateinit var morePanel: CMorePanel
    private lateinit var keyboardHelper: KeyboardHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat)
        ZoomMediaLoader.getInstance().init(TestImageLoader())
        initView()
        initData()
    }

    private fun initView() {
        recycler = findViewById(R.id.chatRecycler)
        footer = findViewById(R.id.footer)
        refreshLayout = findViewById(R.id.refreshLayout)
        container = findViewById(R.id.layout_main)
        chatInputPanel = findViewById(R.id.chat_input_panel)
        expressionPanel = findViewById(R.id.expression_panel)
        expressionPanel.attachEditText(chatInputPanel.getContentEdit())
        morePanel = findViewById(R.id.more_panel)
        titleBar = findViewById(R.id.titlebar)
        titleBar.setListener(OnTitleBarListener { v, action, extra ->
            if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                finish()
            } else if (action == CommonTitleBar.ACTION_RIGHT_BUTTON) {
                //设置
                sendSeeting()
            }
        })

        // 发送、发送语音监听
        chatInputPanel.setInputPanelListener(object : CInputPanel.InputPanelListener {
            override fun sendText(text: String) {
                sendTextMessage(text)
            }

            override fun sendVoice(file: String, duration: Int) {
                sendVoiceMessage(file, duration)
            }

        })
        // 表情发送监听
        expressionPanel.setExpressionPanelListener(object : CExpressionPanel.ExpressionPanelListener {
            override fun emojiSend(content: String) {
                sendTextMessage(content)
            }

        })
        // 更多栏目监听
        morePanel.setMoreActionClickListener(object : CMorePanel.MorePanelActionClickListener {
            override fun selectPhoto() {
                Matisse.from(this@NewChatActivity)
                    .choose(MimeType.ofAll())
                    .countable(true)
                    .maxSelectable(9)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(PicassoEngine())
                    .showPreview(false)
                    .forResult(PHOTO_REQUEST_CODE)
            }

            override fun shoot() {
                startActivityForResult(
                    Intent(this@NewChatActivity, CmeraActivity::class.java),
                    CMERA_REQUEST_CODE
                )
            }

            override fun call() {
                if (conversationInfo!!.conversationType == YMConversation.ConversationType.GROUP) {
                    if (conversationInfo == null || conversation!!.personnels == null || conversation!!.personnels.isEmpty()) {
                        ToastUtils.normal("没有可以呼叫的人")
                        return
                    }
                    val users = java.util.ArrayList<CallUser>()
                    for (personnel in conversation!!.personnels) {
                        if (personnel.uid != UserUtils.getInstance().user.uid) {
                            users.add(CallUser(personnel.uid, personnel.name, personnel.avatar))
                        }
                    }
                    users.add(
                        CallUser(
                            UserUtils.getInstance().user.uid,
                            UserUtils.getInstance().user.username,
                            UserUtils.getInstance().user.userAvatar
                        )
                    )
                    CallPersonSelectActivity.start(
                        this@NewChatActivity,
                        conversationInfo!!.groupId,
                        users,
                        true
                    )
                } else {
                    if (conversationInfo == null) {
                        ToastUtils.normal("没有可以呼叫的人")
                        return
                    }
                    CallSingleActivity.call(
                        this@NewChatActivity,
                        conversationInfo!!.targetId,
                        conversationInfo!!.groupId,
                        conversationInfo!!.conversationTitle,
                        conversationInfo!!.portraitUrl,
                        true
                    )
                }
            }

        })

        recycler.setLayoutManager(LinearLayoutManager(this))
        //        ((LinearLayoutManager) recycler.getLayoutManager()).setStackFromEnd(true);
        val arrow = footer.findViewById<View>(ClassicsFooter.ID_IMAGE_ARROW)
        arrow.scaleY = -1f //必须设置
        recycler.setScaleY(-1f) //必须设置
        refreshLayout.setEnableRefresh(false) //必须关闭
        refreshLayout.setEnableAutoLoadMore(true) //必须关闭
        refreshLayout.setEnableNestedScroll(false) //必须关闭
        refreshLayout.setEnableScrollContentWhenLoaded(true) //必须关闭
        refreshLayout.getLayout().scaleY = -1f //必须设置
        refreshLayout.setScrollBoundaryDecider(object : ScrollBoundaryDeciderAdapter() {
            override fun canLoadMore(content: View): Boolean {
                return super.canRefresh(content) //必须替换
            }
        })
        //监听加载，而不是监听 刷新
        refreshLayout.setOnLoadMoreListener(OnLoadMoreListener { refreshLayout ->
            refreshLayout.layout.postDelayed(object : Runnable {
                override fun run() {
                    if (nodata) {
                        refreshLayout.finishLoadMore()
                        return
                    }
                    index++
                    messageList
                }
            }, 2000)
        })
        recycler.setAdapter(adapter)

        // 消息长按PopAction监听
        mOnPopActionClickListener = object : ChatActivity.OnPopActionClickListener {
            override fun onCopyClick(msg: YMMessage) {
                var content = ""
                //获取剪贴板管理器：
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                // 创建普通字符型ClipData
                var mClipData: ClipData? = null
                if (msg.content is YMTextMessage) {
                    val message = msg.content as YMTextMessage
                    content = message.content
                    mClipData = ClipData.newPlainText("Label", content)
                } else if (msg.content is YMImageMessage) {
                    val message = msg.content as YMImageMessage
                    content = message.url
                    mClipData = ClipData.newRawUri("Label", Uri.parse(content))
                } else if (msg.content is YMVideoMessage) {
                    val message = msg.content as YMVideoMessage
                    content = message.url
                    mClipData = ClipData.newRawUri("Label", Uri.parse(content))
                } else if (msg.content is YMVoiceMessage) {
                    val message = msg.content as YMVoiceMessage
                    content = message.url
                    mClipData = ClipData.newRawUri("Label", Uri.parse(content))
                } else if (msg.content is YMCustomMessage) {
                    val message = msg.content as YMCustomMessage
                    content = message.content
                    mClipData = ClipData.newPlainText("Label", content)
                }
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData!!)
                ToastUtils.normal("复制成功")
            }

            override fun onSendMessageClick(msg: YMMessage, retry: Boolean) {}
            override fun onDeleteMessageClick(msg: YMMessage) {}
            override fun onRevokeMessageClick(msg: YMMessage) {}
            override fun onMultiSelectMessageClick(msg: YMMessage) {}
            override fun onForwardMessageClick(msg: YMMessage) {}
            override fun onReplyMessageClick(msg: YMMessage) {}
        }

        KeyboardStateObserver.getKeyboardStateObserver(this)
            .setKeyboardVisibilityListener(object :
                KeyboardStateObserver.OnKeyboardVisibilityListener {
                override fun onKeyboardShow() {
                    scrollToBottom()
                }

                override fun onKeyboardHide() {

                }

            })

        keyboardHelper = KeyboardHelper()
        keyboardHelper.init(this)
            .bindRootLayout(container)
            .bindRecyclerView(recycler)
            .bindInputPanel(chatInputPanel)
            .bindExpressionPanel(expressionPanel)
            .bindMorePanel(morePanel)
            .setKeyboardHeight(
                if (YMIMKit.getInstance().keyboardHeight == 0) DensityUtil.getScreenHeight(applicationContext) / 5 * 2 else YMIMKit.getInstance().keyboardHeight
            )
            .setOnKeyboardStateListener(object : KeyboardHelper.OnKeyboardStateListener {
                override fun onOpened(keyboardHeight: Int) {
                    YMIMKit.getInstance().keyboardHeight = keyboardHeight
                }

                override fun onClosed() {
                }
            })
        recycler.setHasFixedSize(true)
        recycler.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                keyboardHelper.reset()
            }
            false
        }
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        conversation = intent.getSerializableExtra("conversation") as YMConversation?
        initTitle()
        initChat()
    }

    /**
     * 初始化标题
     */
    private fun initTitle() {
        conversation?.let {
            //设置标题
            titleBar.centerTextView.text = it.conversationTitle
        }

    }

    /**
     * 初始化聊天
     */
    private fun initChat() {
        if (conversation != null) {
            Log.e(TAG, "initChat: " + conversation!!.targetId)
            if (conversation!!.conversationType == YMConversation.ConversationType.PRIVATE) { //单聊
                createPrivateChat(conversation!!.targetId)
            } else if (conversation!!.conversationType == YMConversation.ConversationType.GROUP || conversation!!.conversationType == YMConversation.ConversationType.CHATROOM) { ////群聊
                conversationInfo = conversation
                messageList
                registerMessageReceive()
            }
        }
    }

    fun showItemPopMenu(messageInfo: YMMessage?, view: View?) {
        initPopActions(messageInfo)
        if (mPopActions.size == 0) {
            return
        }
        if (mChatPopMenu != null) {
            mChatPopMenu!!.hide()
            mChatPopMenu = null
            handler.removeCallbacks(runnable)
        }
        mChatPopMenu = ChatPopMenu(this)
        mChatPopMenu!!.setChatPopMenuActionList(mPopActions)
        val location = IntArray(2)
        recycler!!.getLocationOnScreen(location)
        mChatPopMenu!!.show(view, location[1])
        mChatPopMenu!!.setEmptySpaceClickListener { }
        handler.postDelayed(runnable, 10000)

        /*postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mChatPopMenu != null) {
                    mChatPopMenu.hide();
                }
                if (mAdapter != null) {
                    mAdapter.resetSelectableText();
                }
            }
        }, 10000); // 10s后无操作自动消失*/
    }

    private fun initPopActions(msg: YMMessage?) {
        if (msg == null) {
            return
        }
        val actions: MutableList<ChatPopMenuAction> = ArrayList()
        actions.clear()
        var action = ChatPopMenuAction()
        if (msg.content is YMTextMessage) {
            action.actionName = getString(R.string.copy_action)
            action.actionIcon = R.drawable.pop_menu_copy
            action.actionClickListener =
                ChatPopMenuAction.OnClickListener { mOnPopActionClickListener!!.onCopyClick(msg) }
            actions.add(action)
        }
        action = ChatPopMenuAction()
        action.actionName = getString(R.string.delete_action)
        action.actionIcon = R.drawable.pop_menu_delete
        action.actionClickListener =
            ChatPopMenuAction.OnClickListener { mOnPopActionClickListener!!.onDeleteMessageClick(msg) }
        actions.add(action)

        //转发
        action = ChatPopMenuAction()
        action.actionName = getString(R.string.forward_button)
        action.actionIcon = R.drawable.pop_menu_forward
        action.actionClickListener =
            ChatPopMenuAction.OnClickListener {
                mOnPopActionClickListener!!.onForwardMessageClick(
                    msg
                )
            }
        actions.add(action)
        mPopActions.clear()
        mPopActions.addAll(actions)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val obtainPathResult = Matisse.obtainPathResult(data)
            if (obtainPathResult != null) {
                for (s in obtainPathResult) {
                    if (MediaFileUtil.isImageFileType(s)) { //图片
                        sendImageMessage(s)
                    } else {
                        val player = MediaPlayer()
                        var duration = 0.0
                        try {
                            player.setDataSource(s)
                            player.prepare()
                            duration = player.duration.toDouble()
                        } catch (e: IOException) {
//                            e.printStackTrace();
                        }
                        sendVideoMessage(s, (duration / 1000).toInt())
                        player.release()
                    }
                }
            }
            return
        }
        if (requestCode != CMERA_REQUEST_CODE) {
            return
        }
        if (data != null) {
            if (data.extras != null) {
                val url = data.extras!!.getString("url")
                if (MediaFileUtil.isImageFileType(url)) { //图片
                    sendImageMessage(url)
                } else {
                    sendVideoMessage(url, data.extras!!.getInt("time"))
                }
            }
        }
    }

    private fun createPrivateChat(targetId: String) {
        YMIMClient.chatManager()
            .joinPrivateChatWithTargetId(targetId, object : YMResultCallback<YMConversation>() {
                override fun onSuccess(var1: YMConversation) {
                    getConversationWithTargetId(var1.groupId)
                }

                override fun onError(errorCode: Int) {}
            })
    }

    /**
     * 获取群聊信息
     */
    private fun getConversationWithTargetId(groupId: String) {
        YMIMClient.chatManager()
            .getConversationWithTargetId(groupId, object : YMResultCallback<YMConversation?>() {
                override fun onSuccess(conversation: YMConversation?) {
                    conversation!!.unreadMessageCount
                    //                conversationInfo = var1
                    if (conversation == null) {
                        ToastUtils.normal("获取信息错误不能聊天")
                        finish()
                        return
                    }
                    LodingUtils.getInstance().stopLoading()
                    conversationInfo = conversation
                    messageList
                    registerMessageReceive()
                }

                override fun onError(errorCode: Int) {
                    LodingUtils.getInstance().stopLoading()
                    ToastUtils.normal("发生错误")
                }
            })
    }

    /**
     * 注册接收消息的监听
     */
    private fun registerMessageReceive() {
        MessageCenter.getInstance().setChatReceiveListener(conversationInfo!!.groupId, this)
    }

    /**
     * 获取消息列表
     */
    private val messageList: Unit
        private get() {
            YMIMClient.chatManager().getHistoryMessageWithTargetId(
                conversationInfo!!.groupId,
                index,
                20,
                object : YMResultCallback<List<YMMessage>>() {
                    override fun onSuccess(var1: List<YMMessage>) {
                        if (index == 1) {
                            adapter.setList(var1)
                            scrollToBottom()
                            if (!var1.isEmpty()) {
                                readLastMessage(var1[var1.size - 1])
                            }
                        } else {
                            refreshLayout.finishLoadMore()
                            adapter.addData(0, var1)
                            scrollToBottom()
                        }
                    }

                    override fun onError(errorCode: Int) {
                        ToastUtils.normal("发生错误")
                    }
                })
        }

    private fun openImage(url: String) {
        GPreviewBuilder.from(this) //                .setData(list)
            .setSingleData(PrewImage(url))
            .setDuration(0)
            .setCurrentIndex(0)
            .setSingleFling(true) //是否在黑屏区域点击返回
            .setDrag(true) //是否禁用图片拖拽返回
            .setSingleShowType(false)
            .setType(GPreviewBuilder.IndicatorType.Number) //指示器类型
            .start() //启动
    }

    public override fun onDestroy() {
        super.onDestroy()
        MessageCenter.getInstance().cleanChatReceiveListener()
        sendDestroy()
    }

    override fun onReceive(message: YMMessage) {
        Log.e(TAG, "onReceive: " + (message.content as YMTextMessage).content)
        adapter.addData(message)
        scrollToBottom()
        readLastMessage(message)
    }

    private fun scrollToBottom() {
        recycler.adapter?.itemCount?.minus(1)?.let { recycler.scrollToPosition(it) }
    }

    /**
     * 阅读最后一条消息
     */
    private fun readLastMessage(message: YMMessage) {
        YMIMClient.getInstance().readMessage(message)
    }

    /**
     * 发送文字消息
     */
    private fun sendTextMessage(msg: String) {
        val content = YMTextMessage.create(msg)
        val message = YMMessage(conversationInfo!!.groupId, content)
        sendMessage(message)
    }

    /**
     * 发送语音消息
     */
    private fun sendVoiceMessage(file: String, duration: Int) {
        val content = YMVoiceMessage(duration, file)
        val message = YMMessage(conversationInfo!!.groupId, content)
        sendMessage(message)
    }

    /**
     * 发送图片消息
     */
    private fun sendImageMessage(file: String?) {
        val content = YMImageMessage(file)
        val size = Image.imageSize(file)
        content.width = size.width
        content.height = size.height
        val message = YMMessage(conversationInfo!!.groupId, content)
        sendMessage(message)
    }

    /**
     * 发送视频消息
     */
    private fun sendVideoMessage(file: String?, time: Int) {
        val content = YMVideoMessage()
        content.localPath = file
        content.duration = time
        val bitmap = Image.getVideoThumb(file)
        if (bitmap == null) {
            Log.e("EasyPhotos", "bitmap is null")
            return
        }
        content.width = bitmap.width
        content.height = bitmap.height


        //获取录音保存位置
        val dir = "$cacheDir/thumb/"
        val newFile = File(dir)
        if (!newFile.exists()) {
            newFile.mkdirs()
        }
        SavePhoto(this).saveBitmap(bitmap, "thumb.png")
        content.localThumbnail = dir + "thumb.png"
        val message = YMMessage(conversationInfo!!.groupId, content)
        sendMessage(message)
    }

    private fun sendMessage(message: YMMessage) {
        YMIMClient.chatManager().sendMessage(message, object : YMResultCallback<Boolean?>() {
            override fun onSuccess(var1: Boolean?) {
                Log.e("Chat", "发送成功")
            }

            override fun onError(errorCode: Int) {
                Log.e("Chat", "发送失败")
            }
        })
    }

    /**
     * 自动关闭输入法
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideInput(v, ev)) {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    v!!.windowToken,
                    0
                )
                (v as? EditText)?.clearFocus()
            }
            return super.dispatchTouchEvent(ev)
        }
        return if (window.superDispatchTouchEvent(ev)) true else onTouchEvent(ev)
    }

    private fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val location = intArrayOf(0, 0)
            v.getLocationOnScreen(location)
            val left = location[0]
            val top = location[1]
            return event.x < left || event.x > left + v.getWidth() || event.y < top || event.y > top + v.getHeight()
        }
        return false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        KeyboardConflictCompat.assistWindow(window)
    }

    /**
     * 发送右上角事件
     */
    private fun sendSeeting() {
        val intent = Intent("com.mize.young.angel.report")
        intent.putExtra("uid", conversationInfo!!.targetId)
        intent.putExtra("gid", conversationInfo!!.groupId)
        intent.putExtra("name", conversationInfo!!.conversationTitle)
        sendBroadcast(intent)
    }

    /**
     * 发送销毁事件
     */
    private fun sendDestroy() {
        sendBroadcast(Intent("com.mize.young.angel.destroy"))
    }

    private inner class Adapter : BaseQuickAdapter<YMMessage, BaseViewHolder>(
        R.layout.item_message
    ) {
        override fun convert(holder: BaseViewHolder, item: YMMessage) {
            (holder.getView<View>(R.id.item_messgae) as MessageItemView).setMessage(item)
            (holder.getView<View>(R.id.item_messgae) as MessageItemView)
                .setItemClickListener(object : ItemMessageClickListener {
                    override fun video(url: String) {
                        val intent = Intent(this@NewChatActivity, PlayVideoActivity::class.java)
                        intent.putExtra("url", url)
                        startActivity(intent)
                    }

                    override fun image(url: String) {
                        openImage(url)
                    }

                    override fun custom(json: String) {}
                })

            // 长按点击事件
            (holder.getView<View>(R.id.item_messgae) as MessageItemView).setOnLongClickListener {
                showItemPopMenu(item, holder.getView(R.id.item_messgae))
                false
            }
        }
    }

    interface OnPopActionClickListener {
        fun onCopyClick(msg: YMMessage)
        fun onSendMessageClick(msg: YMMessage, retry: Boolean)
        fun onDeleteMessageClick(msg: YMMessage)
        fun onRevokeMessageClick(msg: YMMessage)
        fun onMultiSelectMessageClick(msg: YMMessage)
        fun onForwardMessageClick(msg: YMMessage)
        fun onReplyMessageClick(msg: YMMessage)
    }

    companion object {
        private const val TAG = "ChatActivity"
    }
}