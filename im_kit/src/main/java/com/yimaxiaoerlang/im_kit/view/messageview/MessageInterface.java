package com.yimaxiaoerlang.im_kit.view.messageview;

import com.yimaxiaoerlang.im_core.model.message.YMMessage;

public interface MessageInterface {
    void setMessage(YMMessage message);

    void gone(boolean gone);
}
