package com.yimaxiaoerlang.im_kit.service;


import com.yimaxiaoerlang.im_kit.modlue.LoginResult;
import com.yimaxiaoerlang.http.ResultBean;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {
    @POST("/chat/login")
    Observable<ResultBean<LoginResult>> login(@Body HashMap<String, String> map);

    @POST("http://192.168.1.6:8001/chat/login")
    Observable<ResultBean<LoginResult>> callLogin(@Body HashMap<String, Object> map);
}
