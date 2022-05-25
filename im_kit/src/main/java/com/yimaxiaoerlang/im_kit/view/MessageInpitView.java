package com.yimaxiaoerlang.im_kit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.lqr.emoji.EmotionLayout;
import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.utils.PermissionsCallback;
import com.yimaxiaoerlang.im_kit.utils.PermissionsCommon;

public class MessageInpitView extends LinearLayout {
    private MessageInputListener listener;
    private EditText textInputView;
    private AudioRecordButton audioRecordButton;
    private ImageView soundImage;
    private ImageView showMore;
    private ConstraintLayout menuLayout;
    private EmotionLayout emojiLayout;
    private Button sendBtn;


    public void setListener(MessageInputListener listener) {
        this.listener = listener;
    }

    public MessageInpitView(Context context) {
        super(context);
        init();
    }

    public MessageInpitView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_chat_bottom, this);
        menuLayout = findViewById(R.id.menu_layout);

        //文字部分
        textInputView = findViewById(R.id.message_input);
        textInputView.setHorizontallyScrolling(false);
        textInputView.setMaxLines(Integer.MAX_VALUE);
        textInputView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                textInputView.setFocusableInTouchMode(true);
                textInputView.requestFocus();
//                ActivityUtils.top.window
//                        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

            }
        });

        textInputView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    emojiVisibility(false);
                    menuVisibility(false);
                }
            }
        });

        textInputView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_SEND) {
                    if (!textView.getText().toString().isEmpty()) {
                        if (listener != null) {
                            listener.sendText(textView.getText().toString());
                            textView.setText("");
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        // 发送按钮
        sendBtn = findViewById(R.id.btn_send);
        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null && !textInputView.getText().toString().isEmpty()) {
                    listener.sendText(textInputView.getText().toString());
                    textInputView.setText("");
                }
            }
        });

        //语音部分
        soundImage = findViewById(R.id.img_sound_layout);
        audioRecordButton = findViewById(R.id.audioButton);
        soundImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new PermissionsCommon((FragmentActivity) getContext()).requestAudio(new PermissionsCallback() {
                    @Override
                    public void onSuccess() {
                        soundVisibility(textInputView.getVisibility() == VISIBLE);
                    }
                });
            }
        });

        audioRecordButton.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
            @Override
            public void onFinished(float seconds, String filePath) {
                Log.e("MessageInpitView", "$seconds" + seconds);
                Log.e("MessageInpitView", "$filePath" + filePath);
                if (listener != null) {
                    listener.sendVoice(filePath, (int) seconds);
                }
            }
        });


        //底部菜单
        showMore = findViewById(R.id.menu_more);
        showMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                menuVisibility(menuLayout.getVisibility() != VISIBLE);
            }
        });

        findViewById(R.id.menu_photo).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new PermissionsCommon((FragmentActivity) getContext()).requestStorage(new PermissionsCallback() {
                    @Override
                    public void onSuccess() {
                        if (listener != null) {
                            listener.selectPhoto();
                        }
                    }
                });
            }
        });
        findViewById(R.id.menu_cmera).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                PermissionsCommon(ActivityUtils.top as FragmentActivity).requestCAMERA {
//                    listener?.shoot()
//                }
                new PermissionsCommon((FragmentActivity) getContext()).requestCAMERA(new PermissionsCallback() {
                    @Override
                    public void onSuccess() {
                        if (listener != null) {
                            listener.shoot();
                        }
                    }
                });
            }
        });
        findViewById(R.id.menu_call).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                PermissionsCommon(ActivityUtils.top as FragmentActivity).requestCAMERA {
//                    listener?.call()
//                }
                new PermissionsCommon((FragmentActivity) getContext()).requestCAMERA(new PermissionsCallback() {
                    @Override
                    public void onSuccess() {
                        if (listener != null) {
                            listener.call();
                        }
                    }
                });
            }
        });
        //表情
        emojiLayout = findViewById(R.id.sb_emoji);
        emojiLayout.attachEditText(textInputView);
        emojiLayout.setEmotionAddVisiable(true);
        emojiLayout.setEmotionSettingVisiable(true);
        findViewById(R.id.iv_emoji).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiVisibility(emojiLayout.getVisibility() != VISIBLE);
            }
        });
        findViewById(R.id.menu_heka).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.greetingCard();
                }
            }
        });

    }

    private void emojiVisibility(boolean show) {
        if (show) {
            emojiLayout.setVisibility(VISIBLE);
            sendBtn.setVisibility(VISIBLE);
            soundVisibility(false);
            menuVisibility(false);
        } else {
            emojiLayout.setVisibility(GONE);
            sendBtn.setVisibility(GONE);
        }
    }

    private void soundVisibility(boolean show) {
        if (show) {
            audioRecordButton.setVisibility(VISIBLE); //显示语音
            textInputView.setVisibility(GONE);//关闭输入
            menuVisibility(false);
            emojiVisibility(false);
        } else {
            audioRecordButton.setVisibility(GONE);
            textInputView.setVisibility(VISIBLE);
        }
    }

    private void menuVisibility(boolean show) {
        if (show) {
            menuLayout.setVisibility(VISIBLE);
            emojiVisibility(false);
        } else {
            menuLayout.setVisibility(GONE);
        }
    }
}
