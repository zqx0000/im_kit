package com.yimaxiaoerlang.im_kit.view.messageview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.utils.ImageUtils;
import com.yimaxiaoerlang.im_core.model.message.YMCustomMessage;
import com.yimaxiaoerlang.im_core.model.message.YMImageMessage;
import com.yimaxiaoerlang.im_core.model.message.YMMessage;
import com.yimaxiaoerlang.im_core.model.message.YMMessageContent;
import com.yimaxiaoerlang.im_core.model.message.YMTextMessage;
import com.yimaxiaoerlang.im_core.model.message.YMVideoMessage;
import com.yimaxiaoerlang.im_core.model.message.YMVoiceMessage;
import com.yimaxiaoerlang.im_core.model.YMMessageExtra;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageView extends RelativeLayout implements MessageInterface {
    private ImageView iconView; //头像

    private TextView textView; //文字消息

    private ImageView imageView; //图片消息
    private CardView imageCard; //图片消息

    private TextView voiceTime; //语音消息
    public CardView voiceCard; //语音消息
    public ImageView voiceImage; //语音消息


    private TextView videoTime; //视频消息
    private CardView videoCard; //视频消息
    private ImageView videoImage; //视频消息

    private CardView customCard; //自定义消息
    private ImageView cover;
    private ImageView shareIcon;
    private TextView titleTv;
    private TextView desc;
    private TextView shareName;

    private YMMessage message;
    private ItemMessageClickListener itemMessageClickListener;

    public void setItemMessageClickListener(ItemMessageClickListener itemMessageClickListener) {
        this.itemMessageClickListener = itemMessageClickListener;
    }

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void initView() {
        iconView = findViewById(R.id.iv_icon);
        textView = findViewById(R.id.tv_message);
        imageView = findViewById(R.id.iv_messae);
        imageCard = findViewById(R.id.card_image);
        voiceCard = findViewById(R.id.card_voice);
        voiceImage = findViewById(R.id.iv_sound);
        voiceTime = findViewById(R.id.tv_voice_time);

        videoTime = findViewById(R.id.tv_video_time);
        videoCard = findViewById(R.id.card_video);
        videoImage = findViewById(R.id.iv_video);
        customCard = findViewById(R.id.card_custom);
        cover = findViewById(R.id.cover);
        shareIcon = findViewById(R.id.shareIcon);
        titleTv = findViewById(R.id.title);
        desc = findViewById(R.id.desc);
        shareName = findViewById(R.id.shareName);


    }

    @Override
    public void setMessage(YMMessage message) {
        this.message = message;
//        iconView.loadUrl(message.sendUserIcon)
        ImageUtils.loadImage(iconView, message.getSendUserIcon());
        YMMessageContent content = message.getContent();
        if (content instanceof YMTextMessage) {
            showText((YMTextMessage) content);
        } else if (content instanceof YMImageMessage) {
            showImage((YMImageMessage) content);
        } else if (content instanceof YMVideoMessage) {
            showVideo((YMVideoMessage) content);
        } else if (content instanceof YMVoiceMessage) {
            showVoice((YMVoiceMessage) content);
        } else if (content instanceof YMCustomMessage) {
            showCustomMessage((YMCustomMessage) content);
        }

    }

    @Override
    public void gone(boolean gone) {
        setVisibility(gone ? GONE : VISIBLE);
    }

    private void showCustomMessage(YMCustomMessage message) {
        imageCard.setVisibility(GONE);
        voiceCard.setVisibility(GONE);
        videoCard.setVisibility(GONE);
        textView.setVisibility(GONE);
        customCard.setVisibility(VISIBLE);
        try {
            JSONObject data = new JSONObject(message.getContent());
            String logo = data.getString("logo");
            String appName = data.getString("appName");
            String title = data.getString("title");
            String image = data.getString("image");
            String createTime = data.getString("create_time");

            ImageUtils.loadImage(cover, image);
            ImageUtils.loadImage(shareIcon, logo);
            titleTv.setText(title);
            desc.setText(createTime + "发布");
            shareName.setText(appName);

            customCard.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemMessageClickListener != null) {
                        itemMessageClickListener.custom(message.getContent());
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showText(YMTextMessage textMessage) {
        imageCard.setVisibility(GONE);
        voiceCard.setVisibility(GONE);
        videoCard.setVisibility(GONE);
        customCard.setVisibility(GONE);

        textView.setVisibility(VISIBLE);

        textView.setText(textMessage.getContent());
    }

    private void showImage(YMImageMessage imageMessage) {
        voiceCard.setVisibility(GONE);
        videoCard.setVisibility(GONE);
        textView.setVisibility(GONE);
        customCard.setVisibility(GONE);

        imageCard.setVisibility(VISIBLE);

        YMMessageExtra extra = imageMessage.getExtra();
        YMMessageExtra size = getThumbnailImageSizeWithOrignalSize(extra);
        ViewGroup.LayoutParams layoutParams = imageCard.getLayoutParams();
        layoutParams.height = size.getHeight();
        layoutParams.width = size.getWidth();
        imageCard.setLayoutParams(layoutParams);

        ImageUtils.loadImage(imageView, imageMessage.getUrl());
        imageCard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemMessageClickListener != null) {
                    itemMessageClickListener.image(imageMessage.getUrl());
                }
            }
        });

    }

    private void showVideo(YMVideoMessage videoMessage) {
        imageCard.setVisibility(GONE);
        voiceCard.setVisibility(GONE);
        textView.setVisibility(GONE);
        customCard.setVisibility(GONE);
        videoCard.setVisibility(VISIBLE);
        YMMessageExtra extra = videoMessage.getExtra();
        YMMessageExtra size = getThumbnailImageSizeWithOrignalSize(extra);
        ViewGroup.LayoutParams layoutParams = videoCard.getLayoutParams();
        layoutParams.height = size.getHeight();
        layoutParams.width = size.getWidth();
        videoCard.setLayoutParams(layoutParams);

        ImageUtils.loadImage(videoImage, extra.getImageUrl());
        videoTime.setText(videoMessage.getTime());


        videoCard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemMessageClickListener != null) {
                    itemMessageClickListener.video(videoMessage.getUrl());
                }
            }
        });

    }

    public void showVoice(YMVoiceMessage voiceMessage) {
        imageCard.setVisibility(GONE);
        videoCard.setVisibility(GONE);
        customCard.setVisibility(GONE);
        textView.setVisibility(GONE);

        voiceCard.setVisibility(VISIBLE);

        voiceTime.setText(voiceMessage.getDuration() + "");

    }


    private YMMessageExtra getThumbnailImageSizeWithOrignalSize(YMMessageExtra messageExtra) {
        int imageMaxLength = 120;
        int imageMinLength = 50;
        if (messageExtra.getWidth() == 0 || messageExtra.getHeight() == 0) {
            return new YMMessageExtra(imageMaxLength, imageMinLength);
        }
        int imageWidth = 0;
        int imageHeight = 0;
        if (messageExtra.getWidth() < imageMinLength || messageExtra.getHeight() < imageMinLength) {
            if (messageExtra.getWidth() < messageExtra.getHeight()) {
                imageWidth = imageMinLength;
                imageHeight = imageMinLength * messageExtra.getHeight() / messageExtra.getWidth();
                if (imageHeight > imageMaxLength) {
                    imageHeight = imageMaxLength;
                }
            } else {
                imageHeight = imageMinLength;
                imageWidth = imageMinLength * messageExtra.getWidth() / messageExtra.getHeight();
                if (imageWidth > imageMaxLength) {
                    imageWidth = imageMaxLength;
                }
            }
        } else if (messageExtra.getWidth() < imageMaxLength && messageExtra.getHeight() < imageMaxLength &&
                messageExtra.getWidth() >= imageMinLength && messageExtra.getHeight() >= imageMinLength
        ) {
            if (messageExtra.getWidth() > messageExtra.getHeight()) {
                imageWidth = imageMaxLength;
                imageHeight = imageMaxLength * messageExtra.getHeight() / messageExtra.getWidth();
            } else {
                imageHeight = imageMaxLength;
                imageWidth = imageMaxLength * messageExtra.getWidth() / messageExtra.getHeight();
            }
        } else if (messageExtra.getWidth() >= imageMaxLength || messageExtra.getHeight() >= imageMaxLength) {
            if (messageExtra.getWidth() > messageExtra.getHeight()) {
                if (messageExtra.getWidth() / messageExtra.getHeight() < imageMaxLength / imageMinLength) {
                    imageWidth = imageMaxLength;
                    imageHeight = imageMaxLength * messageExtra.getHeight() / messageExtra.getWidth();
                } else {
                    imageHeight = imageMinLength;
                    imageWidth = imageMinLength * messageExtra.getWidth() / messageExtra.getHeight();
                    if (imageWidth > imageMaxLength) {
                        imageWidth = imageMaxLength;
                    }
                }
            } else {
                if (messageExtra.getHeight() / messageExtra.getWidth() < imageMaxLength / imageMinLength) {
                    imageHeight = imageMaxLength;
                    imageWidth = imageMaxLength * messageExtra.getWidth() / messageExtra.getHeight();
                } else {
                    imageWidth = imageMinLength;
                    imageHeight = imageMinLength * messageExtra.getHeight() / messageExtra.getWidth();
                    if (imageHeight > imageMaxLength) {
                        imageHeight = imageMaxLength;
                    }
                }
            }
        }
        return new YMMessageExtra(imageWidth * 3, imageHeight * 3);
    }
}
