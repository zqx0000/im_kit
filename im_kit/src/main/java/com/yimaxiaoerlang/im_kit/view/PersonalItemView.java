package com.yimaxiaoerlang.im_kit.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.utils.ImageUtils;

public class PersonalItemView extends RelativeLayout {
    private String uid;
    private String iconUlr;

    private ImageView imageView;
    private SurfaceView surfaceView;

    public PersonalItemView(Context context, String uid, String iconUlr) {
        super(context);
        this.uid = uid;
        this.iconUlr = iconUlr;
        LayoutInflater.from(context).inflate(R.layout.view_personal, this);
        imageView = findViewById(R.id.iv_personal_view_icon);
        ImageUtils.loadImage(imageView, iconUlr);
    }

    public void showSurfaceView(SurfaceView surfaceView) {
        imageView.setVisibility(VISIBLE);
        this.surfaceView = surfaceView;
        addView(surfaceView);
    }

    public void showIcon() {
        if (surfaceView != null) surfaceView.setVisibility(GONE);
        if (imageView != null) imageView.setVisibility(VISIBLE);
    }

    public void hideIcon() {
        if (surfaceView != null) surfaceView.setVisibility(VISIBLE);
        if (imageView != null) imageView.setVisibility(GONE);
    }

    public void remove() {

        if (surfaceView != null) removeView(surfaceView);
    }

}
