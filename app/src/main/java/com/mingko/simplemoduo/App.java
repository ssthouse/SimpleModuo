package com.mingko.simplemoduo;

import android.app.Application;

import com.mingko.simplemoduo.control.util.AssertsUtils;
import com.mingko.simplemoduo.control.util.Toast;
import com.mingko.simplemoduo.control.xpg.XPGController;
import com.mingko.simplemoduo.model.cons.Constant;
import com.xtremeprog.xpgconnect.XPGWifiSDK;

import java.io.IOException;

import timber.log.Timber;

/**
 * 启动application
 * Created by ssthouse on 2015/12/17.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化 log
        Timber.plant(new Timber.DebugTree());
        //Toast全局初始化
        Toast.init(this);
        //初始化机智云sdk
        XPGWifiSDK.sharedInstance().startWithAppID(this, Constant.SettingSdkCons.APP_ID);
        XPGWifiSDK.sharedInstance().setLogLevel(Constant.SettingSdkCons.LOG_LEVEL,
                Constant.SettingSdkCons.LOG_FILE_NAME, Constant.isDebug);
        XPGController.getInstance(this);
        try {
            //复制assert文件夹中的json文件到设备安装目录。json文件是解析数据点必备的文件
            AssertsUtils.copyAllAssertToCacheFolder(this.getApplicationContext());
        } catch (IOException e) {
            Timber.e("复制出错");
            e.printStackTrace();
        }
    }
}
