package com.yimaxiaoerlang.im_kit.utils;

import com.yimaxiaoerlang.im_kit.modlue.LoginResult;

public class UserUtils {
    private static final UserUtils ourInstance = new UserUtils();

   public static UserUtils getInstance() {
        return ourInstance;
    }

    private UserUtils() {
    }
    private LoginResult user;

    public void setUser(LoginResult user) {
        this.user = user;
    }

    public LoginResult getUser() {
        return user;
    }
}
