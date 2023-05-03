package com.lzdjack.flutter_uni_applet;
import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.dcloud.feature.sdk.DCUniMPSDK;
import io.dcloud.feature.sdk.Interface.IMenuButtonClickCallBack;
import io.dcloud.feature.sdk.Interface.IUniMP;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** FlutterUniAppletPlugin */
public class FlutterUniAppletPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  private Activity activity;

  HashMap<String, IUniMP> mUniMPCaches = new HashMap<>();



  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_uni_applet");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if(call.method.equals("openUniMP")){
//      {
//        "appId": "__UNI__CC91019",
//        "version": "1.0.0",
//        "appName": "__UNI__CC91019",
//        "url": "http://5b0988e595225.cdn.sohucs.com/images/20180520/0715cc3657094f0d8e02f18d246c7aaf.jpeg"
//        "redirectPath": "pages/component/scroll-view/scroll-view?a=1&b=2&c=3"
//      }
      try {
        JSONObject arg = Utils.toJsonObj(call.arguments);
        String appId = (String) arg.get("appId");
        Utils.releaseResources(activity.getApplicationContext(), arg, mUniMPCaches);
      } catch (JSONException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if(call.method.equals("setIMenuButtonClickCallBack")){
       this.setIMenuButtonClickCallBack();
    } else {
      result.notImplemented();
    }
  }

  /**
   * @deprecated 设置menu点击事件回调接口
   * @param
   */

  public void setIMenuButtonClickCallBack() {
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



  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    this.onDetachedFromActivity();
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    this.onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }

}
