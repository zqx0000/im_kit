package com.yimaxiaoerlang.im_kit.utils;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.previewlibrary.loader.IZoomMediaLoader;
import com.previewlibrary.loader.MySimpleTarget;

public class TestImageLoader implements IZoomMediaLoader {
    @Override
    public void displayImage(@NonNull Fragment context, @NonNull String path, final ImageView imageView, @NonNull final MySimpleTarget simpleTarget) {
//        Glide.with(context)
//                .asBitmap()
//                .load(path)
//                .apply(new RequestOptions().fitCenter())
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        simpleTarget.onResourceReady();
//                        imageView.setImageBitmap(resource);
//                    }
//                });
        ImageUtils.loadImage(imageView, path);
        simpleTarget.onResourceReady();
    }

    @Override
    public void displayGifImage(@NonNull Fragment context, @NonNull String path, ImageView imageView, @NonNull final MySimpleTarget simpleTarget) {
//        Glide.with(context)
//                .asGif()
//                .load(path)
//                //可以解决gif比较几种时 ，加载过慢  //DiskCacheStrategy.NONE
//                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).dontAnimate())
//                //去掉显示动画
//                .listener(new RequestListener<GifDrawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
//                        simpleTarget.onResourceReady();
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
//                        simpleTarget.onLoadFailed(null);
//                        return false;
//                    }
//
//                })
//                .into(imageView);
        ImageUtils.loadImage(imageView, path);
        simpleTarget.onResourceReady();
    }

    @Override
    public void onStop(@NonNull Fragment context) {
//        Glide.with(context).onStop();
    }

    @Override
    public void clearMemory(@NonNull Context c) {
//        Glide.get(c).clearMemory();
    }
}
