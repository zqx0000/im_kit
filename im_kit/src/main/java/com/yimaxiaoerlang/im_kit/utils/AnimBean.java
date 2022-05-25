package com.yimaxiaoerlang.im_kit.utils;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;

/**
 * Created by any on 2016/9/18.
 */
public class AnimBean {
    private AnimationDrawable animationDrawable;
    private View v;
    private int about;
    private String soundFile;
    public AnimationDrawable getAnimationDrawable() {
        return animationDrawable;
    }

    public void setAnimationDrawable(AnimationDrawable animationDrawable) {
        this.animationDrawable = animationDrawable;
    }

    public View getV() {
        return v;
    }

    public void setV(View v) {
        this.v = v;
    }

    public int getAbout() {
        return about;
    }

    public void setAbout(int about) {
        this.about = about;
    }

    public String getSoundFile() {
        return soundFile;
    }

    public void setSoundFile(String soundFile) {
        this.soundFile = soundFile;
    }
}
