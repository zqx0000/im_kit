package com.yimaxiaoerlang.im_kit.view.messageview;

public  interface ItemMessageClickListener {
    void video(String url);
    void image(String url);
    void custom(String json);
}
