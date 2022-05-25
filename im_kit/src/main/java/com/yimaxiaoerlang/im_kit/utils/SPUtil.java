package com.yimaxiaoerlang.im_kit.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 数据存储工具类
 * <p>
 * *
 */

public class SPUtil {
    private static String PreferenceName = "YM-IM";
    private static Context context;

    public static void setContext(Context context) {
        SPUtil.context = context;
    }

    public static void init(Context context) {
       setContext(context);
    }

    /**
     * 储存复杂的数据对象字段
     *
     * @param key
     * @param t
     * @return
     */

//    public static boolean saveObjectToShare(String key, T t) {
//        return saveObjectToShare(context, PreferenceName,
//                key, t);
//
//    }

    /**
     * 储存复杂的数据字段对象
     *
     * @param context
     * @param key
     * @param t
     * @return
     */

//    public static boolean saveObjectToShare(Context context, String key, T t) {
//        return saveObjectToShare(context, PreferenceName, key, t);
//
//    }

    /**
     * @param context
     * @param name
     * @param key
     * @param t
     * @return
     */

//    public static boolean saveObjectToShare(Context context, String name,
//
//                                            String key, T t) {
//        try {
//            SharedPreferences sp = context.getSharedPreferences(name,
//
//                    Context.MODE_PRIVATE);
//
//// 存储
//
//            Editor editor = sp.edit();
//
//            if (t == null) {
//                editor.putString(key, "");
//
//                editor.commit();
//
//                return true;
//
//            }
//
//            ByteArrayOutputStream toByte = new ByteArrayOutputStream();
//
//            ObjectOutputStream oos;
//
//            oos = new ObjectOutputStream(toByte);
//
//            oos.writeObject(t);
//
//// 对byte[]进行Base64编码
//
//            String payCityMapBase64 = new String(Base64.encode(
//
//                    toByte.toByteArray(), Base64.DEFAULT));
//
//            editor.putString(key, payCityMapBase64);
//
//            editor.commit();
//
//            return true;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//
//            return false;
//
//        }
//
//    }

    /**
     * 得到复杂的数据对象
     *
     * @param key
     * @return
     */

//    public static T getObjectFromShare(String key) {
//        return getObjectFromShare(AppContext.getApplication(), PreferenceName,
//
//                key);
//
//    }

    /**
     * 得到复杂的数据对象
     *
     * @param context
     * @param key
     * @return
     */

//    public static T getObjectFromShare(Context context, String key) {
//        return getObjectFromShare(context, PreferenceName, key);
//
//    }

    /**
     * 得到复杂的数据对象
     *
     * @param key
     * @param object
     */

//    @SuppressWarnings("unchecked")
//
//    public static T getObjectFromShare(Context context, String name,
//
//                                       String key) {
//        try {
//            SharedPreferences sp = context.getSharedPreferences(name,
//
//                    Context.MODE_PRIVATE);
//
//            String payCityMapBase64 = sp.getString(key, "");
//
//            if (payCityMapBase64.length() == 0) {
//                return null;
//
//            }
//
//            byte[] base64Bytes = Base64
//
//                    .decode(payCityMapBase64, Base64.DEFAULT);
//
//            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
//
//            ObjectInputStream ois = new ObjectInputStream(bais);
//
//            return (T) ois.readObject();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//
//        return null;
//
//    }


    public static void saveString(String key, String value) {
        SharedPreferences sp = context

                .getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();

        editor.putString(key, value);

        editor.commit();

    }

    /**
     * 返回String类型数据，默认是“”；
     *
     * @param key
     * @return
     */

    public static String getString(String key) {
        SharedPreferences sp = context

                .getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);

        return sp.getString(key, "");

    }

    /**
     * 存储boolean数据类型
     *
     * @param key
     * @param value
     */

    public static void saveboolean(String key, boolean value) {
        SharedPreferences sp =context

                .getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(key, value);

        editor.commit();

    }

    /**
     * 返回boolean类型数据，默认是true；
     *
     * @param key
     * @return
     */

    public static boolean getBoolean(String key) {
        SharedPreferences sp = context

                .getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);

        return sp.getBoolean(key, false);

    }

    /**
     * 存储int数据类型
     *
     * @param key
     * @param value
     */

    public static void saveInt(String key, int value) {
        SharedPreferences sp = context

                .getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();

        editor.putInt(key, value);

        editor.commit();

    }

    /**
     * 返回int类型数据，默认是true；
     *
     * @param key
     * @return
     */

    public static int getInt(String key) {
        SharedPreferences sp =context

                .getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);

        return sp.getInt(key, 0);

    }

    /**
     * 存储float数据类型
     *
     * @param key
     * @param value
     */

    public static void saveFloat(String key, float value) {
        SharedPreferences sp = context

                .getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();

        editor.putFloat(key, value);

        editor.commit();

    }

    /**
     * 返回float类型数据，默认是true；
     *
     * @param key
     * @return
     */

    public static float getFloat(String key) {
        SharedPreferences sp = context

                .getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);

        return sp.getFloat(key, 0);

    }

    /**
     * 存储long数据类型
     *
     * @param key
     * @param value
     */

    public static void saveLong(String key, long value) {
        SharedPreferences sp = context

                .getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();

        editor.putLong(key, value);

        editor.commit();

    }

    /**
     * 返回long类型数据，默认是true；
     *
     * @param key
     * @return
     */

    public static long getLong(String key) {
        SharedPreferences sp = context

                .getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);

        return sp.getLong(key, 0);

    }

}