package com.lzdjack.flutter_uni_applet.http;


import java.util.concurrent.TimeUnit;

import dc.squareup.okhttp3.OkHttpClient;


public class OkhttpHelper {
    private static OkHttpClient mOkHttpClient;

    /**
     * 静态内部类，实例化对象使用
     */
    private static class SingletonOkhttpHelper {
        private static final OkhttpHelper INSTANCE = new OkhttpHelper();
    }

    /**
     * 对外唯一实例的接口
     *
     * @return
     */
    public static OkhttpHelper getInstance() {
        return SingletonOkhttpHelper.INSTANCE;
    }

    /**
     * 初始化OKHttpClient
     */
    public OkHttpClient initOkHttpClient(){
        if (mOkHttpClient == null) {
            synchronized (OkhttpHelper.class) {
                if (mOkHttpClient == null) {
                    mOkHttpClient = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .readTimeout(5, TimeUnit.SECONDS)
                            .build();
                    return mOkHttpClient;
                }
            }
        }
        return mOkHttpClient;
    }
}
