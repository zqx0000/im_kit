package com.yimaxiaoerlang.im_kit.view.newinput

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.freddy.kulakeyboard.library.IInputPanel
import com.freddy.kulakeyboard.library.KeyboardHelper
import com.freddy.kulakeyboard.library.OnInputPanelStateChangedListener
import com.freddy.kulakeyboard.library.PanelType
import com.freddy.kulakeyboard.library.util.DensityUtil
import com.freddy.kulakeyboard.library.util.UIUtil
import com.yimaxiaoerlang.im_kit.R
import com.yimaxiaoerlang.im_kit.view.AudioRecordButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author  FreddyChen
 * @name
 * @date    2020/06/08 13:40
 * @email   chenshichao@outlook.com
 * @github  https://github.com/FreddyChen
 * @desc
 */
class CInputPanel : LinearLayout, IInputPanel {

    private var panelType = PanelType.NONE
    private var lastPanelType = panelType
    private var isKeyboardOpened = false
    private var rooView: View
    private lateinit var btnVoice: CImageButton
    private lateinit var btnKeyboard: CImageButton
    private lateinit var btnVoicePressed: AudioRecordButton
    private lateinit var etContent: CEditText
    private lateinit var btnExpression: CImageButton
    private lateinit var btnMore: CImageButton
    private var listener: InputPanelListener? = null

    companion object {
        const val TAG = "CInputPanel"
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        rooView = LayoutInflater.from(context).inflate(R.layout.layout_input_panel, this, true)
        init()
        setListeners()
    }

    private var isActive = false

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        btnVoice = rooView.findViewById(R.id.btn_voice)
        btnKeyboard = rooView.findViewById(R.id.btn_keyboard)
        btnVoicePressed = rooView.findViewById(R.id.btn_voice_pressed)
        etContent = rooView.findViewById(R.id.et_content)
        btnExpression = rooView.findViewById(R.id.btn_expression)
        btnMore = rooView.findViewById(R.id.btn_more)
        orientation = HORIZONTAL
        setPadding(
            DensityUtil.dp2px(context, 15.0f),
            DensityUtil.dp2px(context, 10.0f),
            DensityUtil.dp2px(context, 15.0f),
            DensityUtil.dp2px(context, 10.0f)
        )
        gravity = Gravity.BOTTOM
        setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        etContent.inputType = InputType.TYPE_NULL
        etContent.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (!isKeyboardOpened) {
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(100)
                        UIUtil.requestFocus(etContent)
                        UIUtil.showSoftInput(context, etContent)
                    }
                    etContent.resetInputType()
                    btnExpression.setNormalImageResId(R.mipmap.inputbar_emoji)
                    btnExpression.setPressedImageResId(R.mipmap.inputbar_emoji)
                    handleAnimator(PanelType.INPUT_MOTHOD)
                    onInputPanelStateChangedListener?.onShowInputMethodPanel()
                }
                return@setOnTouchListener true
            }
            false
        }

        btnVoicePressed.setAudioFinishRecorderListener { seconds, filePath ->
            listener?.sendVoice(file = filePath, duration = seconds.toInt())
        }
    }

    private fun setListeners() {
        btnVoice.setOnClickListener {
            btnExpression.setNormalImageResId(R.mipmap.inputbar_emoji)
            btnExpression.setPressedImageResId(R.mipmap.inputbar_emoji)
            if (lastPanelType == PanelType.VOICE) {
                btnVoicePressed.visibility = View.GONE
                etContent.visibility = View.VISIBLE
                UIUtil.requestFocus(etContent)
                UIUtil.showSoftInput(context, etContent)
                handleAnimator(PanelType.INPUT_MOTHOD)
                etContent.resetInputType()
            } else {
                btnVoicePressed.visibility = View.VISIBLE
                etContent.visibility = View.GONE
                UIUtil.loseFocus(etContent)
                UIUtil.hideSoftInput(context, etContent)
                handleAnimator(PanelType.VOICE)
                onInputPanelStateChangedListener?.onShowVoicePanel()
            }
        }
        btnExpression.setOnClickListener {
            btnVoicePressed.visibility = View.GONE
            etContent.visibility = View.VISIBLE
            if (lastPanelType == PanelType.EXPRESSION) {
                btnExpression.setNormalImageResId(R.mipmap.inputbar_emoji)
                btnExpression.setPressedImageResId(R.mipmap.inputbar_emoji)
                UIUtil.requestFocus(etContent)
                UIUtil.showSoftInput(context, etContent)
                handleAnimator(PanelType.INPUT_MOTHOD)
                etContent.resetInputType()
            } else {
                btnExpression.setNormalImageResId(R.drawable.ic_chat_keyboard_normal)
                btnExpression.setPressedImageResId(R.drawable.ic_chat_keyboard_pressed)
                UIUtil.loseFocus(etContent)
                UIUtil.hideSoftInput(context, etContent)
                handleAnimator(PanelType.EXPRESSION)
                onInputPanelStateChangedListener?.onShowExpressionPanel()
            }
        }
        btnMore.setOnClickListener {
            btnExpression.setNormalImageResId(R.mipmap.inputbar_emoji)
            btnExpression.setPressedImageResId(R.mipmap.inputbar_emoji)
            btnVoicePressed.visibility = View.GONE
            etContent.visibility = View.VISIBLE
            if (lastPanelType == PanelType.MORE) {
                UIUtil.requestFocus(etContent)
                UIUtil.showSoftInput(context, etContent)
                handleAnimator(PanelType.INPUT_MOTHOD)
                etContent.resetInputType()
            } else {
                UIUtil.loseFocus(etContent)
                UIUtil.hideSoftInput(context, etContent)
                handleAnimator(PanelType.MORE)
                onInputPanelStateChangedListener?.onShowMorePanel()
            }
        }
        etContent.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                listener?.sendText(etContent.text.toString())
                etContent.setText("")
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun handleAnimator(panelType: PanelType) {
        Log.d(TAG, "lastPanelType = $lastPanelType\tpanelType = $panelType")
        if (lastPanelType == panelType) {
            return
        }
        isActive = true
        Log.d(TAG, "isActive = $isActive")
        this.panelType = panelType
        var fromValue = 0.0f
        var toValue = 0.0f
        when (panelType) {
            PanelType.VOICE -> {
                when (lastPanelType) {
                    PanelType.INPUT_MOTHOD -> {
                        fromValue = -KeyboardHelper.inputPanelHeight.toFloat()
                        toValue = 0.0f
                    }
                    PanelType.EXPRESSION -> {
                        fromValue = -KeyboardHelper.expressionPanelHeight.toFloat()
                        toValue = 0.0f
                    }
                    PanelType.MORE -> {
                        fromValue = -KeyboardHelper.morePanelHeight.toFloat()
                        toValue = 0.0f
                    }
                    PanelType.NONE -> {
                        fromValue = 0.0f
                        toValue = 0.0f
                    }
                    else -> {
                    }
                }
            }
            PanelType.INPUT_MOTHOD ->
                when (lastPanelType) {
                    PanelType.VOICE -> {
                        fromValue = 0.0f
                        toValue = -KeyboardHelper.inputPanelHeight.toFloat()
                    }
                    PanelType.EXPRESSION -> {
                        fromValue = -KeyboardHelper.expressionPanelHeight.toFloat()
                        toValue = -KeyboardHelper.inputPanelHeight.toFloat()
                    }
                    PanelType.MORE -> {
                        fromValue = -KeyboardHelper.morePanelHeight.toFloat()
                        toValue = -KeyboardHelper.inputPanelHeight.toFloat()
                    }
                    PanelType.NONE -> {
                        fromValue = 0.0f
                        toValue = -KeyboardHelper.inputPanelHeight.toFloat()
                    }
                    else -> {
                    }
                }
            PanelType.EXPRESSION ->
                when (lastPanelType) {
                    PanelType.INPUT_MOTHOD -> {
                        fromValue = -KeyboardHelper.inputPanelHeight.toFloat()
                        toValue = -KeyboardHelper.expressionPanelHeight.toFloat()
                    }
                    PanelType.VOICE -> {
                        fromValue = 0.0f
                        toValue = -KeyboardHelper.expressionPanelHeight.toFloat()
                    }
                    PanelType.MORE -> {
                        fromValue = -KeyboardHelper.morePanelHeight.toFloat()
                        toValue = -KeyboardHelper.expressionPanelHeight.toFloat()
                    }
                    PanelType.NONE -> {
                        fromValue = 0.0f
                        toValue = -KeyboardHelper.expressionPanelHeight.toFloat()
                    }
                    else -> {
                    }
                }
            PanelType.MORE ->
                when (lastPanelType) {
                    PanelType.INPUT_MOTHOD -> {
                        fromValue = -KeyboardHelper.inputPanelHeight.toFloat()
                        toValue = -KeyboardHelper.morePanelHeight.toFloat()
                    }
                    PanelType.VOICE -> {
                        fromValue = 0.0f
                        toValue = -KeyboardHelper.morePanelHeight.toFloat()
                    }
                    PanelType.EXPRESSION -> {
                        fromValue = -KeyboardHelper.expressionPanelHeight.toFloat()
                        toValue = -KeyboardHelper.morePanelHeight.toFloat()
                    }
                    PanelType.NONE -> {
                        fromValue = 0.0f
                        toValue = -KeyboardHelper.morePanelHeight.toFloat()
                    }
                    else -> {
                    }
                }
            PanelType.NONE ->
                when (lastPanelType) {
                    PanelType.VOICE -> {
                        // from 0.0f to 0.0f
                    }
                    PanelType.INPUT_MOTHOD -> {
                        fromValue = -KeyboardHelper.inputPanelHeight.toFloat()
                        toValue = 0.0f
                    }
                    PanelType.EXPRESSION -> {
                        fromValue = -KeyboardHelper.expressionPanelHeight.toFloat()
                        toValue = 0.0f
                    }
                    PanelType.MORE -> {
                        fromValue = -KeyboardHelper.morePanelHeight.toFloat()
                        toValue = 0.0f
                    }
                    else -> {
                    }
                }
        }
        onLayoutAnimatorHandleListener?.invoke(panelType, lastPanelType, fromValue, toValue)
        lastPanelType = panelType
    }

    private var onLayoutAnimatorHandleListener: ((panelType: PanelType, lastPanelType: PanelType, fromValue: Float, toValue: Float) -> Unit)? =
        null
    private var onInputPanelStateChangedListener: OnInputPanelStateChangedListener? = null
    override fun onSoftKeyboardOpened() {
        isKeyboardOpened = true
        etContent.resetInputType()
    }

    override fun onSoftKeyboardClosed() {
        isKeyboardOpened = false
        etContent.inputType = InputType.TYPE_NULL
        if (lastPanelType == PanelType.INPUT_MOTHOD) {
            UIUtil.loseFocus(etContent)
            UIUtil.hideSoftInput(context, etContent)
            handleAnimator(PanelType.NONE)
        }
    }

    override fun setOnLayoutAnimatorHandleListener(listener: ((panelType: PanelType, lastPanelType: PanelType, fromValue: Float, toValue: Float) -> Unit)?) {
        this.onLayoutAnimatorHandleListener = listener
    }

    override fun setOnInputStateChangedListener(listener: OnInputPanelStateChangedListener?) {
        this.onInputPanelStateChangedListener = listener
    }

    override fun reset() {
        if (!isActive) {
            return
        }
        Log.d(TAG, "reset()")
        UIUtil.loseFocus(etContent)
        UIUtil.hideSoftInput(context, etContent)
        btnExpression.setNormalImageResId(R.mipmap.inputbar_emoji)
        btnExpression.setPressedImageResId(R.mipmap.inputbar_emoji)
        GlobalScope.launch(Dispatchers.Main) {
            delay(100)
            handleAnimator(PanelType.NONE)
        }
        isActive = false
    }

    override fun getPanelHeight(): Int {
        return KeyboardHelper.keyboardHeight
    }

    fun getContentEdit(): CEditText {
        return etContent
    }

    fun setInputPanelListener(listener: InputPanelListener) {
        this.listener = listener
    }

    interface InputPanelListener {
        fun sendText(text: String)

        fun sendVoice(file: String, duration: Int)
    }
}