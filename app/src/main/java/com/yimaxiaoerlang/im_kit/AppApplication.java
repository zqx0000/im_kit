package com.yimaxiaoerlang.im_kit;

import android.app.Application;

import com.yimaxiaoerlang.im_kit.core.YMIMKit;

public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        YMIMKit.getInstance().setAppContext(this);
        YMIMKit.getInstance().initIM("7ZC217279tDS3Z659hnw", "A2K8ac23wnPK0427i2ss");
        YMIMKit.getInstance().configAddress("https://im.yuefuximai.com", "wss://im.yuefuximai.com/websocket", "wss://im.yuefuximai.com/signal");

    }
}
