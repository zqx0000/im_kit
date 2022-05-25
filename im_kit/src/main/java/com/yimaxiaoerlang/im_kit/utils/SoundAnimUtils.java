package com.yimaxiaoerlang.im_kit.utils;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.View;


import com.yimaxiaoerlang.im_kit.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by any on 2016/9/18.
 */
public class SoundAnimUtils {
    private static List<AnimBean> animBeanList = new ArrayList<AnimBean>();
    public static int LEFT = 1;
    public static int RIGHT = 2;
    public static MediaPlayer mediaPlayer;

    public static void startAnim(View v, int about, String file) {
        AnimationDrawable animationDrawable;
        for (AnimBean i : animBeanList) {
            if (i.getSoundFile().equals(file)) {
                stopAll();
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                return;
            }
        }
        stopAll();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.reset();
                mediaPlayer = null;
                stopAll();
            }
        });
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(file);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (about == LEFT) {
            v.setBackgroundResource(R.drawable.sound_left);
            animationDrawable = (AnimationDrawable) v.getBackground();
            animationDrawable.setOneShot(false);
            animationDrawable.start();
            AnimBean animBean = new AnimBean();
            animBean.setAbout(about);
            animBean.setSoundFile(file);
            animBean.setAnimationDrawable(animationDrawable);
            animBean.setV(v);
            animBeanList.add(animBean);
        } else {
            v.setBackgroundResource(R.drawable.sound_right);
            animationDrawable = (AnimationDrawable) v.getBackground();
            animationDrawable.setOneShot(false);
            animationDrawable.start();
            AnimBean animBean = new AnimBean();
            animBean.setAbout(about);
            animBean.setSoundFile(file);
            animBean.setAnimationDrawable(animationDrawable);
            animBean.setV(v);
            animBeanList.add(animBean);
        }

    }

    public static void stopAll() {
        for (AnimBean i : animBeanList) {
            i.getAnimationDrawable().stop();
            if (i.getAbout() == LEFT) {
                i.getV().setBackgroundResource(R.drawable.rc_voice_receive_play3);
            } else {
                i.getV().setBackgroundResource(R.drawable.rc_voice_send_play3);
            }
        }
        animBeanList.clear();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
