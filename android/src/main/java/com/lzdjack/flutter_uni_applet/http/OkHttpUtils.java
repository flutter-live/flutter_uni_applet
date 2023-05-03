package com.lzdjack.flutter_uni_applet.http;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import dc.squareup.okhttp3.Call;
import dc.squareup.okhttp3.Callback;
import dc.squareup.okhttp3.Headers;
import dc.squareup.okhttp3.OkHttpClient;
import dc.squareup.okhttp3.Request;
import dc.squareup.okhttp3.RequestBody;
import dc.squareup.okhttp3.Response;

public class OkHttpUtils {
    private final OkHttpClient okHttpClient ;
    private File file;
    /**
     * 静态内部类，实例化对象使用
     */
    private static class SingletonOkhttpUtils {
        private static final OkHttpUtils INSTANCE = new OkHttpUtils();
    }

    /**
     * 同步
     * 对外唯一实例的接口
     *
     * @return
     */
    public static OkHttpUtils getInstance() {
        return SingletonOkhttpUtils.INSTANCE;
    }
    /**
     * 构造方法初始化OkHttpClient
     */
    public OkHttpUtils() {
        this.okHttpClient = OkhttpHelper.getInstance().initOkHttpClient();
    }
    /**
     * @param url 下载地址
     * @return InputStream
     */
    public InputStream OkHttp2InputStream(String url){
        //设置请求
        Request request = new Request.Builder()
                .url(url)
                .build();
        InputStream is = null;
        try {
            //获取行响应
            Response response = okHttpClient.newCall(request).execute();
            if(response.code() == 200){
                is = response.body().byteStream();
            }
        } catch (IOException e) {
            return null;
        }
        return is;
    }
    /**
     * 同步
     * @param url 访问地址
     * @return String
     */
    public String OkHttp2String(String url){
        //设置请求
        Request request = new Request.Builder()
                .url(url)
                .build();
        //返回结果
        String result = null;
        try {
            Response response = okHttpClient.newCall(request).execute();
            if(response.code() == 200){
                result = response.body().string();
            }
        } catch (IOException e) {
            return null;
        }
        return result;
    }
    /**
     * @param url 访问地址
     * @param requestBody 请求参数
     * @param headers 请求头
     * @return String
     */
    public String OkHttp2String(String url, RequestBody requestBody, Headers headers){
        //设置 请求
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .headers(headers)
                .build();
        //获取行响应
        String result = null;
        try {
            result = okHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            return null;
        }
        return result;
    }
    /**
     * 异步
     * 下载文件，支持断点下载
     * @param url 下载地址
     */
    public void OkHttp2Sava(final String url , final String appName, String savePath, DownloadCallback callback){
        Request request = new Request.Builder()
                .url(url)
                .build();
        //使用异步请求
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.i("DOWNLOAD","download fail");
                callback.onFail();
            }

            @Override
            public void onResponse(Call call, Response response)  {
                //文件输出流
                FileOutputStream fos = null;
                //输入流
                InputStream is = null;
                //输入流缓存
                BufferedInputStream bis = null;
                //文件总长度
                long totalLength ;
                //判断是否挂载
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    return ;
                }
                File save = new File(savePath);
                if (!save.exists()) {
                    //创建目录
                    save.mkdir();
                }
                file = new File(savePath + "/" + appName + ".wgt");
                try {

                    if (!file.exists()) {
                        //创建文件
                        file.createNewFile();
                    }
                    totalLength = response.body().contentLength();
                    //如果存在相同文件且大小相同则直接使用
                    if (file.isFile() && file.exists() && file.length() == totalLength) {
                        Log.i("DOWNLOAD","download success");
                        callback.onSuccess();
                        return ;
                    }
                    //如果存在相同文件且文件大小比服务器的小，那么就继续下载
                    if(file.isFile() && file.exists() && file.length() < totalLength){

                    }
                    if(response.isSuccessful()) {
                        is = response.body().byteStream();
                        //缓存2kb
                        byte[] buffer = new byte[1024 * 20];
                        fos = new FileOutputStream(file);
                        bis = new BufferedInputStream(is);
                        int len;

                        while ((len = bis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        fos.flush();
                        // 下载完成
                        Log.i("DOWNLOAD","download success");
                        callback.onSuccess();
                    }
                }catch (FileNotFoundException e) {
                    callback.onFail();
                    e.printStackTrace();
                } catch (IOException e) {
                    callback.onFail();
                    e.printStackTrace();
                } finally {
                    try {
                        if(fos !=null){
                            fos.close();
                        }
                        if(is != null){
                            is.close();
                        }
                        if(bis !=null){
                            bis.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}