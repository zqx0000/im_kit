package com.yimaxiaoerlang.im_kit.utils;

import android.Manifest;

import androidx.fragment.app.FragmentActivity;

import pub.devrel.easypermissions.EasyPermissions;


public class PermissionsCommon {
    private FragmentActivity fragmentActivity;

    public PermissionsCommon(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    public void requestCAMERA(PermissionsCallback callback) {
        String[] perms = {Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(fragmentActivity, perms)) {
            callback.onSuccess();
        } else {

            EasyPermissions.requestPermissions(fragmentActivity, "请给予录音、相机、存储权限",
                    1, perms);
        }

    }

    public void requestStorage(PermissionsCallback callback) {
        String[] perms = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(fragmentActivity, perms)) {
            callback.onSuccess();
        } else {

            EasyPermissions.requestPermissions(fragmentActivity, "请给予存储权限",
                    1, perms);
        }
    }


    public void requestAudio(PermissionsCallback callback) {
        String[] perms = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(fragmentActivity, perms)) {
            callback.onSuccess();
        } else {

            EasyPermissions.requestPermissions(fragmentActivity, "请给予录音、存储权限",
                    1, perms);
        }
    }
}

