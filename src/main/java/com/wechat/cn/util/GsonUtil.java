package com.wechat.cn.util;

import com.google.gson.Gson;

/**
 * Created by IntelliJ IDEA.
 * @Author : 镜像
 * @create 2022/8/25 23:27
 */
public class GsonUtil {
    public static Gson gson;

    public static <T> T fromJson(String jsonStr, Class<T> tClass) {
        try {
            if (gson == null) {
                gson = new Gson();
            }
            return gson.fromJson(jsonStr, tClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends Object> T parseJson(String jsonStr, Class<T> tClass) {
        try {
            if (gson == null) {
                gson = new Gson();
            }
            return gson.fromJson(jsonStr, tClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String toJson(Object object) {
        try {
            if (gson == null) {
                gson = new Gson();
            }
            return gson.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
