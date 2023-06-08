package com.yimaxiaoerlang.im_kit.view.newinput

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.freddy.kulakeyboard.library.IPanel
import com.freddy.kulakeyboard.library.util.DensityUtil
import com.lqr.emoji.EmotionLayout
import com.yimaxiaoerlang.im_kit.R
import com.yimaxiaoerlang.im_kit.core.YMIMKit

/**
 * @author  FreddyChen
 * @name
 * @date    2020/06/08 14:26
 * @email   chenshichao@outlook.com
 * @github  https://github.com/FreddyChen
 * @desc
 */
@Suppress("UNREACHABLE_CODE")
class CExpressionPanel : LinearLayout, IPanel {

    private var emojiLayout: EmotionLayout
    private var sendBtn: Button
    private var listener: ExpressionPanelListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        LayoutInflater.from(context).inflate(R.layout.layout_expression_panel, this, true)
        emojiLayout = findViewById(R.id.sb_emoji)
        sendBtn = findViewById(R.id.btn_send)
        sendBtn.setOnClickListener {
            listener?.emojiSend(editText?.text.toString())
            editText?.setText("")
        }
        init()
    }

    override fun onVisibilityChanged(
        changedView: View,
        visibility: Int
    ) {
        super.onVisibilityChanged(changedView, visibility)
        val layoutParams = layoutParams
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = getPanelHeight()
        setLayoutParams(layoutParams)
    }

    private fun init() {
        orientation = VERTICAL
        emojiLayout.setEmotionAddVisiable(true)
        emojiLayout.setEmotionSettingVisiable(true)
    }

    private val mExpressionPanelInvisibleRunnable =
        Runnable { visibility = View.GONE }

    override fun reset() {
        postDelayed(mExpressionPanelInvisibleRunnable, 0)
    }

    override fun getPanelHeight(): Int {
        return DensityUtil.dp2px(context, 240.0f)
    }

    private var editText: CEditText? = null
    fun attachEditText(messageEditText: CEditText) {
        editText = messageEditText
        emojiLayout.attachEditText(messageEditText)
    }

    fun setExpressionPanelListener(listener: ExpressionPanelListener) {
        this.listener = listener
    }

    interface ExpressionPanelListener {
        fun emojiSend(content: String)
    }
}