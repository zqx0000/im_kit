package com.yimaxiaoerlang.im_kit.utils;

import java.text.ParseException;

public class ConversationTime {
//    fun convertConversationTime(time: String): String {//2021-12-09 13:49:34
//        val longTime = TimeUtils.stringToLong(time, "yyyy-MM-dd HH:mm:ss")
//        val nowTime = System.currentTimeMillis()
//        return when {
//            isToday(longTime, nowTime) -> TimeUtils.formatTime(longTime, "HH:mm")
//            isYear(longTime, nowTime) -> TimeUtils.formatTime(longTime, "MM月dd日 HH:mm")
//            else -> TimeUtils.formatTime(longTime, "yyyy年MM月dd日 HH:mm")
//        }
//
//
//    }
//
//    private fun isToday(time1: Long, nowTime: Long) =
//            (TimeUtils.formatTime(time1, "yyyy-MM-dd") == TimeUtils.formatTime(nowTime, "yyyy-MM-dd"))
//
//    private fun isYear(time1: Long, nowTime: Long) =
//            (TimeUtils.formatTime(time1, "yyyy") == TimeUtils.formatTime(nowTime, "yyyy"))

    public static String convertConversationTime(String time){
        try {
            long longTime = TimeUtils.stringToLong(time, "yyyy-MM-dd HH:mm:ss");
            long nowTime = System.currentTimeMillis();
            if ( isToday(longTime, nowTime)){
                return TimeUtils.formatTime(longTime, "HH:mm");
            }else if (isYear(longTime, nowTime)){
                return TimeUtils.formatTime(longTime, "MM月dd日 HH:mm");
            }else {
                return TimeUtils.formatTime(longTime, "yyyy年MM月dd日 HH:mm");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    private static boolean isToday(long time1, long nowTime){
        return  (TimeUtils.formatTime(time1, "yyyy-MM-dd") == TimeUtils.formatTime(nowTime, "yyyy-MM-dd"));
    }
    private static boolean isYear(long time1, long nowTime){
        return   (TimeUtils.formatTime(time1, "yyyy") == TimeUtils.formatTime(nowTime, "yyyy"));
    }
}
