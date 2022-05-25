package com.yimaxiaoerlang.im_kit.ui;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.ClickListener;
import com.cjt2325.cameralibrary.listener.ErrorListener;
import com.cjt2325.cameralibrary.listener.JCameraListener;
import com.yimaxiaoerlang.im_kit.R;
import com.yimaxiaoerlang.im_kit.utils.SavePhoto;

public class CmeraActivity extends FragmentActivity {
    private JCameraView jCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmera);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        if (getActionBar() != null) {
            getActionBar().hide();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        jCameraView = findViewById(R.id.jcameraview);
        //设置视频保存路径
        jCameraView.setSaveVideoPath(
//                $filesDir
                getFilesDir() + "/video"
        );

//设置只能录像或只能拍照或两种都可以（默认两种都可以）
        jCameraView.setFeatures(jCameraView.BUTTON_STATE_BOTH);

//设置视频质量
        jCameraView.setMediaQuality(jCameraView.MEDIA_QUALITY_MIDDLE);

//jCameraView监听

        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //打开Camera失败回调
                Log.i("CJT", "open camera error");
            }

            @Override
            public void AudioPermissionError() {
                //没有录取权限回调
                Log.i("CJT", "AudioPermissionError");
            }
        });


        jCameraView.setJCameraLisenter(new JCameraListener() {


            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                Log.i("jCameraView", "bitmap = ${bitmap?.width}");
                String url = new SavePhoto(CmeraActivity.this).saveBitmap(bitmap);
                resultUrl(url,0);
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame, int time) {
                //获取视频路径
                Log.i("CJT", "url = $url");
                if (url != null) {
                    resultUrl(url, time);
                }

            }

        });
//左边按钮点击事件
        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
//右边按钮点击事件
        jCameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(
                        CmeraActivity.this,
                        "Right",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

    }

    private void resultUrl(String path, int time) {
        Intent intent = new Intent();
        intent.putExtra("url", path);
        intent.putExtra("time", time);
        setResult(RESULT_OK, intent);
        finish();
    }
}