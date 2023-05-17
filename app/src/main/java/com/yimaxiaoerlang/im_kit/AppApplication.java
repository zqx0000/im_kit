package com.yimaxiaoerlang.im_kit;

import android.app.Application;

import com.yimaxiaoerlang.im_kit.core.YMIMKit;

public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        YMIMKit.getInstance().setAppContext(this);
        YMIMKit.getInstance().initIM("o5728KP83S097fE60vTz", "S7D56527914yodN9zO9e");
        YMIMKit.getInstance().configAddress("https://im.yimaxiaoerlang.cn", "wss://im.yimaxiaoerlang.cn/websocket", "wss://im.yimaxiaoerlang.cn/signal");

    }
}
