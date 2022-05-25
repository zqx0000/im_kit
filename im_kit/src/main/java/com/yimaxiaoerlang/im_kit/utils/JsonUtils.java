package com.yimaxiaoerlang.im_kit.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public  class JsonUtils {
    public static boolean isJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return false;
        }
        try {
            new JSONObject(json);
        } catch (JSONException ex) {
            try {
              new JSONArray(json);
            } catch (JSONException ex1 ) {
                return false;
            }
        }
        return true;

    }
}
