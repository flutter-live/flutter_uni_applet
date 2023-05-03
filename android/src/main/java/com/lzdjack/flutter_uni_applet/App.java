package com.lzdjack.flutter_uni_applet;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;

import java.util.ArrayList;
import java.util.List;

import io.dcloud.feature.sdk.DCSDKInitConfig;
import io.dcloud.feature.sdk.DCUniMPSDK;
import io.dcloud.feature.sdk.Interface.IDCUniMPPreInitCallback;
import io.dcloud.feature.sdk.MenuActionSheetItem;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化 uni小程序SDK ----start----------
        MenuActionSheetItem item = new MenuActionSheetItem("关于", "gy");
        MenuActionSheetItem item1 = new MenuActionSheetItem("获取当前页面url", "hqdqym");
        MenuActionSheetItem item2 = new MenuActionSheetItem("跳转到宿主原生测试页面", "gotoTestPage");
        List<MenuActionSheetItem> sheetItems = new ArrayList<>();
        sheetItems.add(item);
        sheetItems.add(item1);
        sheetItems.add(item2);
        Log.i("unimp","onCreate----");
        DCSDKInitConfig config = new DCSDKInitConfig.Builder()
                .setCapsule(true)
                .setMenuDefFontSize("16px")
                .setMenuDefFontColor("#ff00ff")
                .setMenuDefFontWeight("normal")
                .setMenuActionSheetItems(sheetItems)
                .setUniMPFromRecents(false)
                .setEnableBackground(true)//开启后台运行
                .build();
        DCUniMPSDK.getInstance().initialize(this, config, new IDCUniMPPreInitCallback() {
            @Override
            public void onInitFinished(boolean b) {
                Log.i("unimp","onInitFinished----"+b);
            }
        });
        //初始化 uni小程序SDK ----end----------
    }

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(base);
        super.attachBaseContext(base);
    }
}
