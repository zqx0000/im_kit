package com.yimaxiaoerlang.im_kit.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.lqr.emoji.EmotionLayout;
import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.core.YMIMKit;
import com.yimaxiaoerlang.im_kit.utils.PermissionsCallback;
import com.yimaxiaoerlang.im_kit.utils.PermissionsCommon;
import com.yimaxiaoerlang.im_kit.view.inputmore.InputMoreActionUnit;
import com.yimaxiaoerlang.im_kit.view.inputmore.InputMoreFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageInpitView extends LinearLayout {
    private MessageInputListener listener;
    private EditText textInputView;
    private AudioRecordButton audioRecordButton;
    private ImageView soundImage;
    private ImageView showMore;
    private ConstraintLayout menuLayout;
    private EmotionLayout emojiLayout;
    private Button sendBtn;
    private View inputMoreView;
    private CardView cardView;
    private LinearLayout container;
    protected List<InputMoreActionUnit> mInputMoreActionList = new ArrayList<>();
    private FragmentManager mFragmentManager;
    private InputMoreFragment mInputMoreFragment;
    private FragmentActivity mActivity;

    public void setListener(MessageInputListener listener) {
        this.listener = listener;
    }

    public MessageInpitView(Context context) {
        super(context);
        init(null);
    }

    public MessageInpitView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = mActivity.obtainStyledAttributes(attrs, R.styleable.MessageInpitView);
        int backgroundColor = a.getColor(R.styleable.MessageInpitView_backgroundColor, mActivity.getResources().getColor(R.color.MessageInpitView_backgroundColor));
        int editBackgroundColor = a.getColor(R.styleable.MessageInpitView_editBackgroundColor, mActivity.getResources().getColor(R.color.white));
        int textColor = a.getColor(R.styleable.MessageInpitView_textColor, mActivity.getResources().getColor(R.color.color333));
        float textSize = a.getDimension(R.styleable.MessageInpitView_textSize, 15f);
        int hintTextColor = a.getColor(R.styleable.MessageInpitView_hintTextColor, mActivity.getResources().getColor(R.color.white));
        int voiceIcon = a.getResourceId(R.styleable.MessageInpitView_voiceIcon, R.mipmap.inputbar_voice);
        int emojiIcon = a.getResourceId(R.styleable.MessageInpitView_emojiIcon, R.mipmap.inputbar_emoji);
        int moreIcon = a.getResourceId(R.styleable.MessageInpitView_moreIcon, R.mipmap.inputbar_add);
        int photoActionIcon = a.getResourceId(R.styleable.MessageInpitView_photoActionIcon, R.mipmap.xiangce);
        int cameraActionIcon = a.getResourceId(R.styleable.MessageInpitView_cameraActionIcon, R.mipmap.paishe);
        int callPhoneActionIcon = a.getResourceId(R.styleable.MessageInpitView_callPhoneActionIcon, R.mipmap.tonghua);
        assembleActions(photoActionIcon, cameraActionIcon, callPhoneActionIcon);
        mActivity = (FragmentActivity) getContext();
        LayoutInflater.from(getContext()).inflate(R.layout.view_chat_bottom, this);
        container = findViewById(R.id.container);
        container.setBackgroundColor(backgroundColor);
        menuLayout = findViewById(R.id.menu_layout);
        inputMoreView = findViewById(R.id.input_more_view);
        cardView = findViewById(R.id.cardView);
        cardView.setCardBackgroundColor(editBackgroundColor);
        //文字部分
        textInputView = findViewById(R.id.message_input);
        textInputView.setTextSize(textSize);
        textInputView.setTextColor(textColor);
        textInputView.setHintTextColor(hintTextColor);
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

        //语音部分
        soundImage = findViewById(R.id.img_sound_layout);
        soundImage.setImageResource(voiceIcon);
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
        showMore.setImageResource(moreIcon);
        showMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                menuVisibility(inputMoreView.getVisibility() != VISIBLE);
            }
        });

        findViewById(R.id.menu_photo).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new PermissionsCommon((FragmentActivity) getContext()).requestStorage(new PermissionsCallback() {
                    @Override
                    public void onSuccess() {
                        if (listener!=null){
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
                        if (listener!=null){
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
                        if (listener!=null){
                            listener.call();
                        }
                    }
                });
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
        //表情
        emojiLayout = findViewById(R.id.sb_emoji);
        emojiLayout.attachEditText(textInputView);
        emojiLayout.setEmotionAddVisiable(true);
        emojiLayout.setEmotionSettingVisiable(true);
        ImageView emojiIv = findViewById(R.id.iv_emoji);
        emojiIv.setImageResource(emojiIcon);
        emojiIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiVisibility(emojiLayout.getVisibility() != VISIBLE);
            }
        });

    }

    private void showInputMoreLayout() {
        if (mFragmentManager == null) {
            mFragmentManager = mActivity.getSupportFragmentManager();
        }
        if (mInputMoreFragment == null) {
            mInputMoreFragment = new InputMoreFragment();
        }

        mInputMoreFragment.setActions(mInputMoreActionList);
        hideSoftInput();
        inputMoreView.setVisibility(View.VISIBLE);
        mFragmentManager.beginTransaction().replace(R.id.input_more_view, mInputMoreFragment).commitAllowingStateLoss();
    }

    private void hideInputMoreLayout() {
        inputMoreView.setVisibility(View.GONE);
    }

    protected void assembleActions(int photoActionIcon, int cameraActionIcon, int callPhoneActionIcon) {
        mInputMoreActionList.clear();
        InputMoreActionUnit action = new InputMoreActionUnit();
        action.setIconResId(photoActionIcon);
        action.setTitleId(R.string.action_photo);
        action.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new PermissionsCommon((FragmentActivity) getContext()).requestStorage(new PermissionsCallback() {
                    @Override
                    public void onSuccess() {
                        if (listener!=null){
                            listener.selectPhoto();
                        }
                    }
                });
            }
        });
        mInputMoreActionList.add(action);
        action = new InputMoreActionUnit();
        action.setIconResId(cameraActionIcon);
        action.setTitleId(R.string.action_camera);
        action.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new PermissionsCommon((FragmentActivity) getContext()).requestStorage(new PermissionsCallback() {
                    @Override
                    public void onSuccess() {
                        if (listener!=null){
                            listener.shoot();
                        }
                    }
                });
            }
        });
        mInputMoreActionList.add(action);
        if (!YMIMKit.getIsOpenAV()) return;
        action = new InputMoreActionUnit();
        action.setIconResId(callPhoneActionIcon);
        action.setTitleId(R.string.action_call);
        action.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new PermissionsCommon((FragmentActivity) getContext()).requestStorage(new PermissionsCallback() {
                    @Override
                    public void onSuccess() {
                        if (listener!=null){
                            listener.call();
                        }
                    }
                });
            }
        });
        mInputMoreActionList.add(action);

    }

    public void addInputMoreAction(InputMoreActionUnit... actions) {
        if (mFragmentManager == null) {
            mFragmentManager = mActivity.getSupportFragmentManager();
        }
        if (mInputMoreFragment == null) {
            mInputMoreFragment = new InputMoreFragment();
        }
        mInputMoreActionList.addAll(Arrays.asList(actions));
        mInputMoreFragment.setActions(mInputMoreActionList);
        mFragmentManager.beginTransaction().replace(R.id.input_more_view, mInputMoreFragment).commitAllowingStateLoss();
    }

    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textInputView.getWindowToken(), 0);
        textInputView.clearFocus();
        inputMoreView.setVisibility(View.GONE);
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
            menuLayout.setVisibility(GONE);
            emojiVisibility(false);
            showInputMoreLayout();
        } else {
            menuLayout.setVisibility(GONE);
            hideInputMoreLayout();
        }
    }
}
