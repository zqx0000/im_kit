package com.yimaxiaoerlang.im_kit.utils;

import com.yimaxiaoerlang.im_core.core.YMIMClient;
import com.yimaxiaoerlang.im_core.core.other.YMReceiveMessageListener;
import com.yimaxiaoerlang.im_core.model.message.YMMessage;

import java.util.ArrayList;

public class MessageCenter implements YMReceiveMessageListener {
    private static final MessageCenter ourInstance = new MessageCenter();

    public static MessageCenter getInstance() {
        return ourInstance;
    }

    private MessageCenter() {
        YMIMClient.getInstance().addReceiveMessageListener(this);
    }

    @Override
    public void onReceiveMessage(YMMessage message) {

        if (message == null) return;
        if (message.getTargetId().equals(targetId)) {
            chatReciveListener.onReceive(message);
            message.setReadState(YMMessage.MessageReadState.READ);
            for (MessageReceiveListener meessageReciceListener : receiveListenerArray) {
                meessageReciceListener.onReceive(message);
            }
        }

    }

    private ArrayList<MessageReceiveListener> receiveListenerArray = new ArrayList<>();
    private String targetId = "";
    private MessageReceiveListener chatReciveListener;

    public void setChatReceiveListener(String targetId, MessageReceiveListener listener) {
        this.targetId = targetId;
        this.chatReciveListener = listener;
    }

    public void cleanChatReceiveListener() {
        this.targetId = "";
        this.chatReciveListener = null;
    }

    public void addReceiveListener(MessageReceiveListener listener) {
        if (!receiveListenerArray.contains(listener)) {
            receiveListenerArray.add(listener);
        }

    }

    public void removeReceiveListener(MessageReceiveListener listener) {
        if (receiveListenerArray.contains(listener)) {
            receiveListenerArray.remove(listener);
        }
        //没有监听结束
        if (receiveListenerArray.isEmpty()) {
            YMIMClient.getInstance().removeReceiveMessageListener(this);
        }
    }
}
