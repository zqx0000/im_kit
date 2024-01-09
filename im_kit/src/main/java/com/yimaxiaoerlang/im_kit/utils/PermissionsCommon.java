package com.yimaxiaoerlang.im_kit.utils;

import androidx.fragment.app.FragmentActivity;


public class PermissionsCommon {
    private FragmentActivity fragmentActivity;

    public PermissionsCommon(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    public void requestCAMERA(PermissionsCallback callback) {
        callback.onSuccess();
    }

    public void requestStorage(PermissionsCallback callback) {
        callback.onSuccess();
    }


    public void requestAudio(PermissionsCallback callback) {
        callback.onSuccess();
    }
}

