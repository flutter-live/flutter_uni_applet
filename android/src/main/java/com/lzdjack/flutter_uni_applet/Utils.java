package com.lzdjack.flutter_uni_applet;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.lzdjack.flutter_uni_applet.http.DownloadCallback;
import com.lzdjack.flutter_uni_applet.http.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.dcloud.common.DHInterface.ICallBack;
import io.dcloud.common.adapter.util.Logger;
import io.dcloud.feature.sdk.DCUniMPSDK;
import io.dcloud.feature.sdk.Interface.IDCUniMPOnCapsuleCloseButtontCallBack;
import io.dcloud.feature.sdk.Interface.IDCUniMPOnCapsuleMenuButtontCallBack;
import io.dcloud.feature.sdk.Interface.IMenuButtonClickCallBack;
import io.dcloud.feature.sdk.Interface.IOnUniMPEventCallBack;
import io.dcloud.feature.sdk.Interface.IUniMP;
import io.dcloud.feature.sdk.Interface.IUniMPOnCloseCallBack;
import io.dcloud.feature.unimp.DCUniMPJSCallback;

public class Utils {
    /**
     * @deprecated  释放小程序文件,并打开小程序
     * @param context
     * @param arg
     * @throws Exception
     */
    public static void releaseResources(Context context, JSONObject arg, HashMap<String, IUniMP> mUniMPCaches) throws Exception {
        String appId = (String) arg.get("appId");
        String newVersion = (String) arg.get("version");
        String appName = (String) arg.get("appName");
        String url = (String) arg.get("url");

        String cacheDir = context.getExternalCacheDir().getPath();
        String wgtPath = cacheDir + "/" + appId + ".wgt";
        Boolean isExistsApp = DCUniMPSDK.getInstance().isExistsApp(appId);
        //检测小程序是否被释放
        if (isExistsApp) {  // 已释放
            Boolean isVersion = comparisonVersion(appId, newVersion);
            if(isVersion == null){ //版本号为空
                Toast.makeText(context, "模块不存在", Toast.LENGTH_SHORT).show();
                return;
            }else if(isVersion){ //版本相同
                IUniMP uniMP = DCUniMPSDK.getInstance().openUniMP(context, appId, arg);
                mUniMPCaches.put(uniMP.getAppid(), uniMP);
            }else { //版本不同，下载新的版本
                downloadFile(url, appName, cacheDir, appId, wgtPath, mUniMPCaches, context, arg);
            }
        } else { //没有释放
            //判断小程序包是否存在
            File file = new File(wgtPath);
            if(file .exists()) { //文件存在
                //释放小程序
                DCUniMPSDK.getInstance().releaseWgtToRunPathFromePath(appId, wgtPath, new ICallBack() {
                    @Override
                    public Object onCallBack(int code, Object pArgs) {
                        if (code == 1) {//释放wgt成功，判断wgt版本
                            try {
                                Boolean isVersion = comparisonVersion(appId, newVersion);
                                if(isVersion == null){ //版本号没空
                                    Toast.makeText(context, "模块不存在", Toast.LENGTH_SHORT).show();
                                    return null;
                                }else if(isVersion){ //版本相同
                                    IUniMP uniMP = DCUniMPSDK.getInstance().openUniMP(context, appId, arg);
                                    mUniMPCaches.put(uniMP.getAppid(), uniMP);
                                }else{ //版本不同，下载新的版本
                                    downloadFile(url, appName, cacheDir, appId, wgtPath, mUniMPCaches, context, arg);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {//释放wgt失败
                            Toast.makeText(context, "模块释放失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                        return null;
                    }
                });
            }else{ //不存在下载文件
                downloadFile(url, appName, cacheDir, appId, wgtPath, mUniMPCaches, context, arg);
            }
        }
    }

    /**
     * @deprecated 开启线程方式打开小程序资源
     * @param context
     * @param appId
     * @param mUniMPCaches
     * @param arg
     */
    public static void threadUniMP(Context context, String appId, HashMap<String, IUniMP> mUniMPCaches, JSONObject arg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    IUniMP uniMP = DCUniMPSDK.getInstance().openUniMP(context, appId, arg);
                    mUniMPCaches.put(uniMP.getAppid(), uniMP);
                    Looper.loop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * @deprecated 以线程方式打开提示
     * @param context
     */
    public static void threadToast(Context context, String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * @deprecated 下载文件，并释放资源
     * @param url
     * @param appName
     * @param cacheDir
     * @param appId
     * @param wgtPath
     * @param mUniMPCaches
     * @param context
     * @param arg
     */
    public static void downloadFile(String url, String appName, String cacheDir, String appId, String wgtPath, HashMap<String, IUniMP> mUniMPCaches, Context context, JSONObject arg){
        OkHttpUtils.getInstance().OkHttp2Sava(url, appName, cacheDir, new DownloadCallback() {
            @Override
            public void onFail() {}

            @Override
            public void onSuccess() {
                //新版本下载成功释放资源
                DCUniMPSDK.getInstance().releaseWgtToRunPathFromePath(appId, wgtPath, new ICallBack() {
                    @Override
                    public Object onCallBack(int code, Object pArgs) {
                        //释放
                        if (code == 1) {//释放wgt完成
                            threadUniMP(context, appId, mUniMPCaches, arg);
                        } else {//释放wgt失败
                            threadToast(context, "资源释放失败");
                        }
                        return null;
                    }
                });
            }
        });
    }

    /**
     * @deprecated 判断版本号是否一致
     * @param appId
     * @param newVersion
     * @return
     * @throws JSONException
     */
    public static Boolean comparisonVersion(String appId, String newVersion) throws JSONException {
        JSONObject getAppVersionInfo = getAppVersionInfo(appId);
        if(getAppVersionInfo == null){
            return null;
        }
        String version = (String) getAppVersionInfo.get("name");
        if(version.equals(newVersion)){
            return true;
        }else {
            return false;
        }
    }

    /**
     * @deprecated 获取已运行过的小程序应用版本信息,没有运行过的小程序是无法正常获取到版本信息的。返回值需要判空处理!!!
     * @param appId
     * @return
     */
    public static JSONObject getAppVersionInfo(String appId){
        JSONObject jsonObject = DCUniMPSDK.getInstance().getAppVersionInfo(appId);
        if(jsonObject != null) {
            Logger.d("__UNI__04E3A11版本信息为"+jsonObject.toString());
        }
        return jsonObject;
    }

    /**
     * @deprecated 设置menu点击事件回调接口
     */
    public static void setDefMenuButtonClickCallBack(){
        DCUniMPSDK.getInstance().setDefMenuButtonClickCallBack(new IMenuButtonClickCallBack() {
            @Override
            public void onClick(String appid, String id) {
                switch (id) {
                    case "gy":{
                        Log.e("unimp", "点击了关于" + appid);
                        break;
                    }
                }
            }
        });
    }

    /**
     * @deprecated 设置小程序被关闭事件监听
     */
    public static void setUniMPOnCloseCallBack(){
        DCUniMPSDK.getInstance().setUniMPOnCloseCallBack(new IUniMPOnCloseCallBack() {
            @Override
            public void onClose(String appid) {
                Log.e("unimp", appid+"被关闭了");
            }
        });
    }

    /**
     * @deprecated 设置小程序胶囊按钮点击"X"关闭事件监听，设置后原关闭逻辑将不再执行！交由宿主实现相关逻辑
     */
    public static void setCapsuleCloseButtonClickCallBack(){
        DCUniMPSDK.getInstance().setCapsuleCloseButtonClickCallBack(new IDCUniMPOnCapsuleCloseButtontCallBack() {
            @Override
            public void closeButtonClicked(String appid) {
                Log.e("unimp", appid+"胶囊点击了关闭按钮");
            }
        });
    }

    /**
     * @deprecated 设置小程序胶囊按钮点击"..."菜单事件监听，设置后原菜单弹窗逻辑将不再执行！交由宿主实现相关逻辑
     */
    public static void setCapsuleMenuButtonClickCallBack(){
        DCUniMPSDK.getInstance().setCapsuleMenuButtonClickCallBack(new IDCUniMPOnCapsuleMenuButtontCallBack() {
            @Override
            public void menuButtonClicked(String appid) {
                Log.e("unimp", appid+"胶囊点击了菜单按钮");
//                Intent intent = new Intent(context, MenuActivity.class);
//                启动一个Activity，当前Activity运行在小程序任务堆栈中。进程还是属于宿主进程。可正常使用宿主内存数据！关闭当前activity还会回到小程序。
//                DCUniMPSDK.getInstance().startActivityForUniMPTask(appid, intent);
            }
        });
    }

    /**
     * JAVA监听小程序发来的事件 通过callback返回参数
     */
    public static void setOnUniMPEventCallBack(){
        DCUniMPSDK.getInstance().setOnUniMPEventCallBack(new IOnUniMPEventCallBack() {
            @Override
            public void onUniMPEventReceive(String s, String s1, Object o, DCUniMPJSCallback dcUniMPJSCallback) {
                dcUniMPJSCallback.invoke( "测试数据");
            }
        });
    }

    /**
     * @deprecated 获取运行时uni小程序的当前页面url 可用于页面直达等操作的地址。
     * @param map
     * @param id
     * @return
     */
    public static String getCurrentPageUrl(HashMap<String, IUniMP> map, String id){
        IUniMP iUniMP = map.get(id);
        String url = iUniMP.getCurrentPageUrl();
        return url;
    }

    /**
     * @deprecated 宿主主动触发事件到正在运行的小程序。注意：需要已有小程序在运行才可成功
     * @param map
     * @param id
     * @param event
     * @param data
     * @throws JSONException
     */
    public static void sendUniMPEvent(HashMap<String, IUniMP> map, String id,String event, JSONObject data) throws JSONException {
        IUniMP iUniMP = map.get(id);
        data.put("sj", "点击了关于");
        iUniMP.sendUniMPEvent(event, data);
    }

    /**
     * @deprecated 判定当前小程序是否运行中
     * @param iUniMP
     * @return
     */
    public static Boolean isRuning(IUniMP iUniMP){
        return iUniMP.isRuning();
    }

    /**
     * @deprecated 当前小程序显示到前台。仅开启后台模式生效！
     * @param iUniMP
     * @return
     */
    public static Boolean showUniMP(IUniMP iUniMP){
        return iUniMP.showUniMP();
    }

    /**
     * @deprecated 当前小程序退到后台。仅开启后台模式生效!
     * @param iUniMP
     * @return
     */
    public static Boolean hideUniMP(IUniMP iUniMP){
        return iUniMP.hideUniMP();
    }

    /**
     * @deprecated 关闭当前小程序
     * @param iUniMP
     * @return
     */
    public static Boolean closeUniMP(IUniMP iUniMP){
        return iUniMP.closeUniMP();
    }


    /**
     * @deprecated 转JSONObject
     * @param map
     * @return
     * @throws JSONException
     */
    public static JSONObject toJsonObj(Object map) throws JSONException {
        JSONObject resultJson = new JSONObject();
        if(map == null){
            resultJson = null;
        } else if (map instanceof Map) {
            Iterator it = ((Map) map).keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                resultJson.put(key, ((Map) map).get(key));
            }
        } else if (map instanceof JSONObject) {
            resultJson = (JSONObject) map;
        } else {
            throw new ClassCastException();
        }
        return resultJson;
    }
}
