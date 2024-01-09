package com.yimaxiaoerlang.im_kit;

import android.app.Application;

import com.yimaxiaoerlang.im_kit.core.YMIMKit;

public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        YMIMKit.getInstance().setAppContext(this);
        YMIMKit.getInstance().initIM("09r277Y47OA8Bd7P69e3", "X5Tm26F1D95759yEvoA5");
        YMIMKit.getInstance().initRTM("09r277Y47OA8Bd7P69e3", "X5Tm26F1D95759yEvoA5");
//        YMIMKit.getInstance().configAddress("https://im.yimaxiaoerlang.cn", "wss://im.yimaxiaoerlang.cn/websocket", "wss://im.yimaxiaoerlang.cn/signal");
        YMIMKit.getInstance().configAddress("http://192.168.124.33:8001", "ws://192.168.124.33:8001/websocket", "ws://192.168.124.33:8001/signal");
    }
}
