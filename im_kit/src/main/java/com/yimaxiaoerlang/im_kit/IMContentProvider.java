package com.yimaxiaoerlang.im_kit;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.ImageView;

import com.lqr.emoji.IImageLoader;
import com.lqr.emoji.LQREmotionKit;
import com.yimaxiaoerlang.im_kit.utils.SPUtil;
import com.yimaxiaoerlang.im_kit.utils.ToastUtils;
//import com.yimaxiaoerlang.rtc_lib.YMRLClient;
import com.squareup.picasso.Picasso;
import com.yimaxiaoerlang.ym_base.YMConfig;
import com.zlw.main.recorderlib.RecordManager;
import com.zlw.main.recorderlib.recorder.RecordConfig;

public class IMContentProvider extends ContentProvider {
    public IMContentProvider() {


//        IMClient.getInstance()
//                .configAddress("ws://192.168.1.6:8001/websocket", "http://192.168.1.6:8001/")
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        Request original = chain.request();
//                        String token = SPUtil.getString("token");
//                        if (token == null || TextUtils.isEmpty(token)) {
//                            return chain.proceed(
//                                    original.newBuilder()
//                                            .header("Content-Type", "application/json")
//
//                                            .method(original.method(), original.body())
//                                            .build()
//                            );
//                        } else {
//                            return chain.proceed(
//                                    original.newBuilder()
//                                            .header("Content-Type", "application/json")
//                                            .header("Authorization", token)
//                                            .method(original.method(), original.body())
//                                            .build()
//                            );
//                        }
//
//                    }
//                });
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return "";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public boolean onCreate() {
        SPUtil.init(getContext());
        ToastUtils.init(getContext());


        LQREmotionKit.init(getContext(), new IImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Picasso.with(getContext()).load(path).into(imageView);
            }
        });

        RecordManager.getInstance().init((Application) getContext(), true);
        RecordManager.getInstance().changeFormat(RecordConfig.RecordFormat.MP3);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }
}