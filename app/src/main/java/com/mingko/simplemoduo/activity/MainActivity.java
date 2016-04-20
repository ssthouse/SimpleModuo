package com.mingko.simplemoduo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mingko.simplemoduo.R;
import com.mingko.simplemoduo.control.util.QrCodeUtil;
import com.mingko.simplemoduo.control.util.SettingManager;
import com.mingko.simplemoduo.control.util.Toast;
import com.mingko.simplemoduo.control.xpg.CmdCenter;
import com.mingko.simplemoduo.model.event.scan.ScanDeviceEvent;
import com.mingko.simplemoduo.model.event.xpg.XPGLoginResultEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 主界面Activity, 控制toolbar和侧边栏
 */
public class MainActivity extends AppCompatActivity {
    //点两次退出程序
    private long exitTimeInMils = 0;

    @Bind(R.id.id_tb)
    Toolbar toolbar;

    /**
     * 启动当前activity
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        //登录
        loginXpg();

        if (SettingManager.getInstance(this).hasLocalModuo()) {
            Toast.show("未魔哆设备");
        }

        initView();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Simple Moduo");
    }

    //登录
    private void loginXpg() {
        //默认匿名登录
        if (SettingManager.getInstance(this).isAnonymousUser()) {
            CmdCenter.getInstance(this).cLoginAnonymousUser();
        } else {
            CmdCenter.getInstance(this).cLogin(
                    SettingManager.getInstance(this).getUserName(),
                    SettingManager.getInstance(this).getPassword()
            );
        }
    }


    //********************** event回调 ********************************************
    //扫描设备二维码回调
    public void onEventMainThread(ScanDeviceEvent event) {
        SettingManager settingManager = SettingManager.getInstance(this);
        if (!settingManager.isLogined()) {
            Toast.show("请先登录");
            return;
        }
        //将设备数据保存在本地
        settingManager.setCurrentDid(event.getDid());
        settingManager.setPasscode(event.getPassCode());
        //绑定当前连接设备
        CmdCenter.getInstance(this).cBindDevice(settingManager.getUid(),
                settingManager.getToken(),
                settingManager.getCurrentDid(),
                settingManager.getPasscode(),
                "我的魔哆");
    }

    //机智云登录回调
    public void onEventMainThread(XPGLoginResultEvent event) {
        SettingManager settingManager = SettingManager.getInstance(this);
        if (event.isSuccess()) {
            //将登录数据缓存本地
            settingManager.setUid(event.getUid());
            settingManager.setToken(event.getToken());
            //登录设备
            CmdCenter.getInstance(this).cGetBoundDevices(
                    settingManager.getUid(),
                    settingManager.getToken()
            );
        } else {
            Toast.show("机智云登录失败");
        }
    }

    //********************** menu控制 ************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_menu_add_moduo:
                //开启扫描activity
                QrCodeUtil.startScan(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() < (exitTimeInMils + 1500)) {
            super.onBackPressed();
        } else {
            exitTimeInMils = System.currentTimeMillis();
            Toast.show("再次点击退出");
        }
    }

    //************************* 二维码扫描回调 *******************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //扫描二维码回调
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        QrCodeUtil.parseScanResult(this, result);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
