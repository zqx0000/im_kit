package com.yimaxiaoerlang.im_kit.utils;

import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageUtils {
    public static void loadImage(ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)){
            Picasso.with(imageView.getContext()).load(url).into(imageView);
        }
    }
}
