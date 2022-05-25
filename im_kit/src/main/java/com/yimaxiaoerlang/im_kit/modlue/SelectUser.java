package com.yimaxiaoerlang.im_kit.modlue;

public class SelectUser {
    private String username;
    private String uid;
    private String userAvatar;

    public SelectUser(String username, String uid, String userAvatar) {
        this.username = username;
        this.uid = uid;
        this.userAvatar = userAvatar;
    }

    public SelectUser(String username, String uid) {
        this.username = username;
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

}
