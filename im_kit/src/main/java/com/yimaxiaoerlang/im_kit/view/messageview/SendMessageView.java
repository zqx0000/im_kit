package com.yimaxiaoerlang.im_kit.view.messageview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.utils.SoundAnimUtils;
import com.yimaxiaoerlang.im_core.model.message.YMVoiceMessage;

public class SendMessageView extends MessageView {

    public SendMessageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_send_message, this);
        initView();
    }
    @Override
    public void showVoice(YMVoiceMessage voiceMessage) {
        super.showVoice(voiceMessage);
        voiceCard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundAnimUtils.startAnim(
                        voiceImage,
                        SoundAnimUtils.RIGHT,
                        voiceMessage.getUrl()
                );
            }
        });

    }
}
