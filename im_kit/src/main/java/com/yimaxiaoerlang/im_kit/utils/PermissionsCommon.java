package com.yimaxiaoerlang.im_kit.utils;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class PermissionsCommon {
    private RxPermissions rxPermissions;
    private FragmentActivity fragmentActivity;
    private Fragment fragment;

    public PermissionsCommon(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        rxPermissions = new RxPermissions(fragmentActivity);
    }

    public PermissionsCommon(Fragment fragment) {
        this.fragment = fragment;
        rxPermissions = new RxPermissions(fragment);
    }

    public void requestCAMERA(PermissionsCallback callback) {
//        rxPermissions
//                .request(
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.RECORD_AUDIO,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                ).subscribe {  granted ->
//            if (granted) {
//                execute()
//            } else {
//                ToastUtils.normal("请给予录音、相机、存储权限")
//            }
//        }

        rxPermissions
                .request(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                       if (aBoolean) {
                    callback.onSuccess();
                } else {
                     ToastUtils.normal("请给予录音、相机、存储权限");
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void requestStorage(PermissionsCallback callback) {
//        rxPermissions
//                .request(
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//                .subscribe {
//            granted ->
//            if (granted) {
//                execute()
//            } else {
//                ToastUtils.normal("请给予存储权限")
//            }
//        }

        rxPermissions
                .request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    callback.onSuccess();
                } else {
                    ToastUtils.normal("请给予存储权限");
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }


    public void requestAudio(PermissionsCallback callback) {
//        rxPermissions
//                .request(
//                        Manifest.permission.RECORD_AUDIO,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//                .subscribe {
//            granted ->
//            if (granted) {
//                execute()
//            } else {
//                ToastUtils.normal("请给予录音、存储权限")
//            }
//        }
        rxPermissions
                .request(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    callback.onSuccess();
                } else {
                    ToastUtils.normal("请给予录音、存储权限");
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}

