package com.yimaxiaoerlang.im_kit.core;

import static com.yimaxiaoerlang.module_signalling.core.YMRTMLoginStatusCode.RTMLoginSuccess;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.yimaxiaoerlang.im_kit.modlue.LoginResult;
import com.yimaxiaoerlang.im_kit.modlue.SelectUser;
import com.yimaxiaoerlang.im_kit.service.LoginService;
import com.yimaxiaoerlang.im_kit.utils.SPUtil;
import com.yimaxiaoerlang.im_kit.utils.UserUtils;
import com.yimaxiaoerlang.module_signalling.core.YMRTMLoginCallback;
import com.yimaxiaoerlang.module_signalling.core.YMRTMLoginStatusCode;
import com.yimaxiaoerlang.rtc_lib.YMRLClient;
import com.yimaxiaoerlang.rtc_kit.CallManager;
import com.yimaxiaoerlang.ym_base.YMConfig;
import com.yimaxiaoerlang.http.NetException;
import com.yimaxiaoerlang.http.NetModel;
import com.yimaxiaoerlang.http.ResultListener;
import com.yimaxiaoerlang.http.RetrofitHelper;
import com.yimaxiaoerlang.im_core.core.other.YMConnectCallback;
import com.yimaxiaoerlang.im_core.core.other.YMConnectError;
import com.yimaxiaoerlang.im_core.core.YMIMClient;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class YMIMKit {
    private final static String TAG = "IMManager";
    private static final YMIMKit ourInstance = new YMIMKit();
    private String imAppKey = "7ZC217279tDS3Z659hnw";
    private String imAppSecret = "A2K8ac23wnPK0427i2ss";
    private LoginCallback loginCallback;
    private static Context appContext;
    private static boolean isOpenAV = false;// 是否开通音视频

    private int keyboardHeight = 0;

    public static YMIMKit getInstance() {
        return ourInstance;
    }

    private YMIMKit() {

    }

    public void setAppContext(Context context) {
        appContext = context;
        YMIMClient.getInstance().setAppContext(context);
    }

    public void init(YMConfig ymIMConfig, YMConfig ymRLConfig) {
        this.imAppKey = ymIMConfig.getAppkey();
        this.imAppSecret = ymIMConfig.getAppsecret();
        YMIMClient.getInstance().initSDKWithConfig(ymIMConfig);
        YMRLClient.getInstance().initSDKWithConfig(ymRLConfig);
        isOpenAV = true;
    }

    public void initIM(String appKey, String appSecret) {
        this.imAppKey = appKey;
        this.imAppSecret = appSecret;
        YMConfig ymIMConfig = new YMConfig();
        ymIMConfig.setAppsecret(appSecret);
        ymIMConfig.setAppkey(appKey);
        YMIMClient.getInstance().initSDKWithConfig(ymIMConfig);
    }

    public void initRTM(String appKey, String appSecret) {
        YMConfig ymRLConfig = new YMConfig();
        ymRLConfig.setAppsecret(appSecret);
        ymRLConfig.setAppkey(appKey);
        YMRLClient.getInstance().initSDKWithConfig(ymRLConfig);
        isOpenAV = true;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static boolean getIsOpenAV() {
        return isOpenAV;
    }

    public void configAddress(String api, String imSocketAddress, String signalSocketAddress) {
        YMIMClient.getInstance()
                .configAddress(imSocketAddress, api)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Request original = chain.request();
                        String token = SPUtil.getString("token");
                        if (token == null || TextUtils.isEmpty(token)) {
                            return chain.proceed(
                                    original.newBuilder()
                                            .header("Content-Type", "application/json")

                                            .method(original.method(), original.body())
                                            .build()
                            );
                        } else {
                            return chain.proceed(
                                    original.newBuilder()
                                            .header("Content-Type", "application/json")
                                            .header("Authorization", token)
                                            .method(original.method(), original.body())
                                            .build()
                            );
                        }

                    }
                });
        if (isOpenAV) YMRLClient.getInstance().setSocketAddress(signalSocketAddress);
    }

    public void login(SelectUser selectUser, LoginCallback loginCallback) {
        this.loginCallback = loginCallback;
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", selectUser.getUid());
        map.put("userAvatar", selectUser.getUserAvatar());
        map.put("username", selectUser.getUsername());
        map.put("appKey", imAppKey);
        map.put("type", "0");
        map.put("appSecret", imAppSecret);

        NetModel.getInstance().request(RetrofitHelper.getInstance().getRequest(LoginService.class).login(map), new ResultListener<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("zgj", "成功");
                map.put("type", "1");
                NetModel.getInstance().request(RetrofitHelper.getInstance().getRequest(LoginService.class).login(map), new ResultListener<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult2) {

                        loginIM(loginResult, loginResult2.getToken());
                    }

                    @Override
                    public void onError(NetException netException) {

                    }
                });
                UserUtils.getInstance().setUser(loginResult);
                CallManager.getInstance().init(appContext, UserUtils.getInstance().getUser().toCallUser());
                SPUtil.saveString("token", loginResult.getToken());
            }

            @Override
            public void onError(NetException netException) {
                Log.e("zgj", "失败");
                if (loginCallback != null) {
                    loginCallback.loginError();
                }
            }
        });
    }

    public void login(LoginResult loginResult, String signallingToken, LoginCallback loginCallback) {

        this.loginCallback = loginCallback;
        if (loginResult == null) {
            loginCallback.loginError();
            return;
        }
        loginIM(loginResult, signallingToken);
        UserUtils.getInstance().setUser(loginResult);
        CallManager.getInstance().init(appContext, UserUtils.getInstance().getUser().toCallUser());
        SPUtil.saveString("token", loginResult.getToken());
    }

    private void loginIM(LoginResult loginResult, String signallingToken) {

        YMIMClient.getInstance().loginWithToken(loginResult.getToken(), loginResult.getUid(), new YMConnectCallback() {
            @Override
            public void onSuccess(Object obj) {
                Log.e(TAG, "登录成功");
                if (isOpenAV) {
                    connectRTC(signallingToken, loginResult.getUid());
                } else {
                    if (loginCallback != null) {
                        loginCallback.loginSuccess();
                    }
                }
            }


            @Override
            public void onError(YMConnectError error) {
                Log.e(TAG, "登录失败");
                if (loginCallback != null) {
                    loginCallback.loginError();
                }
            }

        });
    }

    private void loginRTC(SelectUser user) {
        HashMap<String, String> map = new HashMap<>();
        map.put("appKey", imAppKey);
        map.put("appSecret", imAppSecret);
        map.put("uid", user.getUid());
        map.put("userName", user.getUsername());
        NetModel.getInstance().request(RetrofitHelper.getInstance().getRequest(LoginService.class).login(map), new ResultListener<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("zgj", "成功");
                if (isOpenAV) {
                    connectRTC(loginResult.getToken(), loginResult.getUid());
                } else {
                    if (loginCallback != null) {
                        loginCallback.loginSuccess();
                    }
                }
            }

            @Override
            public void onError(NetException netException) {
                Log.e("zgj", "失败");
                if (loginCallback != null) {
                    loginCallback.loginError();
                    YMIMClient.getInstance().logout();
                }
            }
        });
    }

    private void connectRTC(String token, String uid) {

        YMRLClient.getInstance().connect(token, uid, new YMRTMLoginCallback() {
            @Override
            public void completion(YMRTMLoginStatusCode code) {
                Log.e(TAG, "音视频信令code" + code);
                if (code == RTMLoginSuccess) {
                    Log.e(TAG, "音视频信令链接成功");
                    if (loginCallback != null) {
                        loginCallback.loginSuccess();
                    }
                } else {
                    if (loginCallback != null) {
                        YMRLClient.getInstance().logout();
                        loginCallback.loginError();
                    }
                }
            }
        });
    }


    public void logout() {
        YMIMClient.getInstance().logout();
        YMRLClient.getInstance().logout();
        UserUtils.getInstance().setUser(null);
    }

    public int getKeyboardHeight() {
        return keyboardHeight;
    }

    public void setKeyboardHeight(int keyboardHeight) {
        this.keyboardHeight = keyboardHeight;
    }
}
