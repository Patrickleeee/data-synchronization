package com.witium.util;

import okhttp3.*;

import java.io.IOException;
import java.util.Optional;

/**
 * Desciption okhttp3工具类
 * Create By  li.bo
 * CreateTime 2018/5/15 14:33
 * UpdateTime 2018/5/15 14:33
 */
public class Okhttp3Util {

    private Okhttp3Util() {
        throw new Error("Don't instance of " + getClass());
    }

    /**
     * request type: get
     * @param url       完整请求路径（如"https://127.0.0.0::8088/login"）
     * @param params    拼接参数字段（如"?username=X&password=Y"）
     * @return          网络请求成功的返回体
     */
    public static String get(String url, String params) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url.concat(params))
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            return Optional.ofNullable(response)
                    .filter(r -> "200".equals(r.code()))
                    .get().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    /**
     * request type: post
     * @param url       完整请求路径（如"https://127.0.0.0::8088/login"）
     * @param body      表单参数
     * @return          网络请求成功的返回体
     */
    public static String post(String url, RequestBody body) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            return Optional.ofNullable(response)
                    .filter(r -> 200 == r.code())
                    .map(r -> {
                        try {
                            return r.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return "";
                        }
                    })
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "fail";
    }

}
