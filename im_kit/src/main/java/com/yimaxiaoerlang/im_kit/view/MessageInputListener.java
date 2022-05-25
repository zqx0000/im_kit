package com.yimaxiaoerlang.im_kit.view;

public interface MessageInputListener {
    void sendText(String text);

    void sendVoice(String file, int duration);

    void selectPhoto();

    void shoot();

    void call();

    void greetingCard();
}
