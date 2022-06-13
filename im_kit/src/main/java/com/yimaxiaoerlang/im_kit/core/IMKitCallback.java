package com.yimaxiaoerlang.im_kit.core;

public interface IMKitCallback {
    void onSuccess(Object data);

    void onError(String module, int errCode, String errMsg);
}
