package com.yimaxiaoerlang.im_kit.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatTextView;

import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.utils.ScreenUtil;


public class UnreadCountTextView extends AppCompatTextView {

    private int mNormalSize;
    private Paint mPaint;

    public UnreadCountTextView(Context context) {
        super(context);
        init();
    }

    public UnreadCountTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UnreadCountTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mNormalSize = ScreenUtil.getPxByDp(getContext(), 18.4f);
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.read_dot_bg));
        mPaint.setAntiAlias(true);
        setTextColor(Color.WHITE);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.6f);
    }

    public void setPaintColor(int color) {
        if (mPaint != null) {
            mPaint.setColor(color);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getText().length() == 0) {
            // 没有字符，就在本View中心画一个小圆点
            int l = (getMeasuredWidth() - ScreenUtil.getPxByDp(getContext(), 6)) / 2;
            int t = l;
            int r = getMeasuredWidth() - l;
            int b = r;
            canvas.drawOval(new RectF(l, t, r, b), mPaint);
        } else if (getText().length() == 1) {
            canvas.drawOval(new RectF(0, 0, mNormalSize, mNormalSize), mPaint);
        } else if (getText().length() > 1) {
            canvas.drawRoundRect(new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight()), getMeasuredHeight() / 2, getMeasuredHeight() / 2, mPaint);
        }
        super.onDraw(canvas);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = mNormalSize;
        int height = mNormalSize;
        if (getText().length() > 1) {
            width = mNormalSize + ScreenUtil.getPxByDp(getContext(), (getText().length() - 1) * 10);
        }
        setMeasuredDimension(width, height);
    }
}
