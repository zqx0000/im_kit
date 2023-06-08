package com.yimaxiaoerlang.im_kit.view.newinput

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.freddy.kulakeyboard.library.IPanel
import com.freddy.kulakeyboard.library.util.DensityUtil
import com.yimaxiaoerlang.im_kit.R
import com.yimaxiaoerlang.im_kit.core.YMIMKit
import com.yimaxiaoerlang.im_kit.utils.PermissionsCommon
import com.yimaxiaoerlang.im_kit.view.inputmore.InputMoreActionUnit
import com.yimaxiaoerlang.im_kit.view.inputmore.InputMoreFragment

/**
 * @author  FreddyChen
 * @name
 * @date    2020/06/24 17:19
 * @email   chenshichao@outlook.com
 * @github  https://github.com/FreddyChen
 * @desc
 */
open class CMorePanel : FrameLayout, IPanel {
    private var mActivity: FragmentActivity
    private var mInputMoreActionList = mutableListOf<InputMoreActionUnit>()
    private var mFragmentManager: FragmentManager
    private var mInputMoreFragment: InputMoreFragment
    private var listener: MorePanelActionClickListener? = null
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mActivity = context as FragmentActivity
        mFragmentManager = mActivity.supportFragmentManager
        mInputMoreFragment = InputMoreFragment()
        val a = mActivity.obtainStyledAttributes(attrs, R.styleable.CMorePanel)
        a?.apply {
            val photoActionIcon =
                getResourceId(R.styleable.CMorePanel_photoActionIcon, R.mipmap.xiangce)
            val cameraActionIcon =
                getResourceId(R.styleable.CMorePanel_cameraActionIcon, R.mipmap.paishe)
            val callPhoneActionIcon =
                getResourceId(R.styleable.CMorePanel_callPhoneActionIcon, R.mipmap.tonghua)
            assembleActions(photoActionIcon, cameraActionIcon, callPhoneActionIcon)
        }
        LayoutInflater.from(context).inflate(R.layout.layout_more_panel, this, true)
        init()
    }

    protected open fun assembleActions(
        photoActionIcon: Int,
        cameraActionIcon: Int,
        callPhoneActionIcon: Int
    ) {
        mInputMoreActionList.clear()
        var action = InputMoreActionUnit()
        action.iconResId = photoActionIcon
        action.titleId = R.string.action_photo
        action.onClickListener = OnClickListener {
            PermissionsCommon(context as FragmentActivity).requestStorage {
                listener?.selectPhoto()
            }
        }
        mInputMoreActionList.add(action)
        action = InputMoreActionUnit()
        action.iconResId = cameraActionIcon
        action.titleId = R.string.action_camera
        action.onClickListener = OnClickListener {
            PermissionsCommon(context as FragmentActivity).requestStorage {
                listener?.shoot()
            }
        }
        mInputMoreActionList.add(action)
        if (!YMIMKit.getIsOpenAV()) return
        action = InputMoreActionUnit()
        action.iconResId = callPhoneActionIcon
        action.titleId = R.string.action_call
        action.onClickListener = OnClickListener {
            PermissionsCommon(context as FragmentActivity).requestStorage {
                listener?.call()
            }
        }
        mInputMoreActionList.add(action)
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
        mInputMoreFragment.setActions(mInputMoreActionList)
        mFragmentManager.beginTransaction().replace(R.id.input_more_view, mInputMoreFragment)
            .commitAllowingStateLoss()
    }

    private val mMorePanelInvisibleRunnable =
        Runnable { visibility = View.GONE }

    override fun reset() {
        postDelayed(mMorePanelInvisibleRunnable, 0)
    }

    override fun getPanelHeight(): Int {
        return YMIMKit.getInstance().keyboardHeight - DensityUtil.dp2px(context, 56.0f)
    }

    fun setMoreActionClickListener(listener: MorePanelActionClickListener) {
        this.listener = listener
    }

    interface MorePanelActionClickListener {
        fun selectPhoto()

        fun shoot()

        fun call()
    }
}