package com.yimaxiaoerlang.im_kit.view.messageview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;


import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.utils.TimeUtils;
import com.yimaxiaoerlang.im_core.model.message.YMMessage;
import com.yimaxiaoerlang.im_core.model.message.YMMessageContent;
import com.yimaxiaoerlang.im_core.model.message.YMTimeMessage;

import java.text.ParseException;

public class MessageItemView extends RelativeLayout implements MessageInterface {
    private TextView timeView;
    private CardView timeCard;

    private SendMessageView sendView;
    private ReceiveMessageView receiveView;

    private MessageInterface messageView;

    public MessageItemView(Context context) {
        super(context);
        init();
    }

    public MessageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_message, this);
        timeCard = findViewById(R.id.card_time);
        timeView = findViewById(R.id.tv_time);
        sendView = findViewById(R.id.send);
        receiveView = findViewById(R.id.recive);

    }

    public void setItemClickListener(ItemMessageClickListener listener) {
        sendView.setItemMessageClickListener(listener);
        receiveView.setItemMessageClickListener(listener);
    }

    @Override
    public void setMessage(YMMessage message) {
        YMMessageContent content = message.getContent();
        messageView = message.getMessageDirection() == YMMessage.MessageDirection.RECEIVE ? receiveView : sendView;

        if (content instanceof YMTimeMessage) {
            showTime((YMTimeMessage) content);

            if (messageView != null) {
                messageView.gone(true);
            }
        } else {
            sendView.setVisibility(GONE);
            receiveView.setVisibility(GONE);
            timeCard.setVisibility(GONE);
            if (messageView != null) {
                messageView.gone(false);
                messageView.setMessage(message);
            }

        }

    }

    @Override
    public void gone(boolean gone) {

    }

    private void showTime(YMTimeMessage timeMessage) {
        sendView.setVisibility(GONE);
        receiveView.setVisibility(GONE);
        timeCard.setVisibility(VISIBLE);
        try {
            timeView.setText(TimeUtils.formatTime(
                    TimeUtils.stringToLong(timeMessage.getTime(), "yyyy-MM-dd HH:mm:ss"),
                    "MM-dd HH:mm"
            ));
        } catch (ParseException e) {
//            e.printStackTrace();
        }
    }
}
