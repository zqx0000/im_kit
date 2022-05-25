package com.yimaxiaoerlang.im_kit.ui;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;

import com.yimaxiaoerlang.im_kit.R;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;


public class PlayVideoActivity extends FragmentActivity {
    String url;
    SurfaceView sv;
    MediaPlayer mediaPlayer;
    int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//        );
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        CommonTitleBar bar=findViewById(R.id.titlebar);
        bar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                    finish();
                }
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("url");
            sv = (SurfaceView) findViewById(R.id.sv);
            Log.e("-------video----", "" + url);
        }

        //获取holder 对象 用来维护视频播放的内容
        SurfaceHolder holder = sv.getHolder();

        //[0.1]添加holder 生命周期 方法
        holder.addCallback(new SurfaceHolder.Callback() {
            //当surface view 销毁
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                System.out.println("surfaceDestroyed");
                //停止播放视频
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {

                    //获取到当前播放视频的位置

                    currentPosition = mediaPlayer.getCurrentPosition();
                    mediaPlayer.stop();

                }
            }

            //这个方法执行了 说明sufaceView准备好了
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //[1]初始化mediaplayer
                System.out.println("surfaceCreated");

                mediaPlayer = new MediaPlayer();
                //[2]设置要播放的资源位置  path 可以是网络 路径 也可是本地路径
                try {
//                    mediaPlayer.setDataSource("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");
                    if (url.length() > 0) {
                        mediaPlayer.setDataSource(url);
                    } else {
                        mediaPlayer.setDataSource("http://111.207.84.82:8090/images/avatar/991.mp4");
                    }
                    //[3]准备播放
                    mediaPlayer.prepareAsync();

                    //[3.0]设置显示给sfv sufraceholder 是用来维护视频播放的内容
                    mediaPlayer.setDisplay(holder);

                    mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer
                            .OnVideoSizeChangedListener() {
                        @Override
                        public void
                        onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                            updateVideoViewSize(mediaPlayer.getVideoWidth(), mediaPlayer
                                    .getVideoHeight());
                        }
                    });
                    mediaPlayer.setLooping(true);

                    //[3.1]设置一个准备完成的监听
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            //[4]开始播放
                            mediaPlayer.start();

                            //[5]继续上次的位置继续播放
                            mediaPlayer.seekTo(currentPosition);
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {

            }
        });

    }

    private void updateVideoViewSize(float videoWidth, float videoHeight) {
        if (videoWidth > videoHeight) {
            LinearLayout.LayoutParams videoViewParam;
            int height = (int) ((videoHeight / videoWidth) * sv.getWidth());
            videoViewParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
            videoViewParam.gravity = Gravity.CENTER;
            sv.setLayoutParams(videoViewParam);
        }
    }

    @Override
    protected void onDestroy() {
        ReleasePlayer();
        super.onDestroy();
    }

    /**
     * 释放播放器资源
     */
    private void ReleasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
