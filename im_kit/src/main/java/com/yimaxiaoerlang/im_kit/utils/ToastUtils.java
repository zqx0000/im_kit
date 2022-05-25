package com.yimaxiaoerlang.im_kit.utils;

import android.content.Context;

import es.dmoral.toasty.Toasty;

public class ToastUtils {
    private static Context context;

    public static void init(Context context) {
        ToastUtils.context = context;
    }

    public static void normal(String msg) {
        Toasty.normal(context, msg).show();
    }
}
