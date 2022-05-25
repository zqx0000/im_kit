package com.yimaxiaoerlang.im_kit.modlue;

import com.yimaxiaoerlang.rtc_kit.model.CallUser;

public class LoginResult {
//    val expireTime: String,
//    var phone: String?="",
//    val token: String,
//    val uid: Int,
//    val userAvatar: String,
//    val username: String

    private String expireTime;
    private String phone;
    private String token;
    private String uid;
    private String userAvatar;
    private String username;


    public LoginResult(String expireTime, String phone, String token, String uid, String userAvatar, String username) {
        this.expireTime = expireTime;
        this.phone = phone;
        this.token = token;
        this.uid = uid;
        this.userAvatar = userAvatar;
        this.username = username;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CallUser toCallUser() {
        CallUser callUser = new CallUser(uid, username, userAvatar);
        return callUser;
    }

    @Override
    public String toString() {
        return "LoginResult{" +
                "expireTime='" + expireTime + '\'' +
                ", phone='" + phone + '\'' +
                ", token='" + token + '\'' +
                ", uid='" + uid + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}

