package com.mingko.simplemoduo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mingko.simplemoduo.R;
import com.mingko.simplemoduo.control.util.QrCodeUtil;
import com.mingko.simplemoduo.control.util.SettingManager;
import com.mingko.simplemoduo.control.util.Toast;
import com.mingko.simplemoduo.control.xpg.CmdCenter;
import com.mingko.simplemoduo.control.xpg.XPGController;
import com.mingko.simplemoduo.model.CircleView;
import com.mingko.simplemoduo.model.event.scan.ScanDeviceEvent;
import com.mingko.simplemoduo.model.event.xpg.DeviceBindResultEvent;
import com.mingko.simplemoduo.model.event.xpg.GetBoundDeviceEvent;
import com.mingko.simplemoduo.model.event.xpg.XPGLoginResultEvent;
import com.mingko.simplemoduo.model.event.xpg.XpgDeviceLoginEvent;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 主界面Activity, 控制toolbar和侧边栏
 */
public class MainActivity extends AppCompatActivity {
    //点两次退出程序
    private long exitTimeInMils = 0;

    @Bind(R.id.id_tb)
    Toolbar toolbar;

//    @Bind(R.id.id_seekbar)
//    CircleSeekBar circleSeekBar;

    @Bind(R.id.id_circle_view)
    CircleView circleView;

    @Bind(R.id.id_tv_state)
    Button btnState;

    @Bind(R.id.id_fragment_container)
    LinearLayout ll;

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

        if (!SettingManager.getInstance(this).hasLocalModuo()) {
            Toast.show("未绑定魔哆设备");
        }

        initView();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Simple Moduo");

        //CircleView角度bi
        circleView.setAngleChangeListener(new CircleView.AngleChangeListener() {
            public void onAngleChange(int newAngle) {
                int currentNum = circleView.getCurrentAngle();
                //将 0~360 映射到 -150~150
                if (currentNum > 180) {
                    currentNum = -(360 - currentNum);
                }
                //发送数据
                XPGController.getInstance(MainActivity.this).cWriteXbody(currentNum);
            }
        });

        btnState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI();
                reConnect();
                circleView.setCurrentAngle(90);
            }
        });
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

    //重新连接机智云sdk 和 机智云设备
    private void reConnect() {
        SettingManager settingManager = SettingManager.getInstance(this);
        //sdk未登陆
        if (!XPGController.getInstance(this).isLogin()) {
            loginXpg();
            return;
        }
        //设备未连接
        if (XPGController.getInstance(this).getCurrentDevice() == null) {
            CmdCenter.getInstance(this).cGetBoundDevices(
                    settingManager.getUid(),
                    settingManager.getToken()
            );
            return;
        }
        //设备未连接
        if (!XPGController.getInstance(this).getCurrentDevice().isConnected()) {
            XPGController.getInstance(this).getCurrentDevice().login(
                    settingManager.getUid(),
                    settingManager.getToken()
            );
        }
    }

    //刷新UI
    private void updateUI() {
        if (!XPGController.getInstance(this).isLogin()) {
            btnState.setText("未登录");
            return;
        }
        if (XPGController.getInstance(this).getCurrentDevice() == null) {
            btnState.setText("未连接魔哆");
            return;
        }
        XPGWifiDevice device = XPGController.getInstance(this).getCurrentDevice();
        if (!device.isOnline()) {
            btnState.setText("魔哆不在线");
            return;
        }
        if (!device.isConnected()) {
            btnState.setText("魔哆未连接");
            return;
        }
        btnState.setText("魔哆连接成功");
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
        updateUI();
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

    //设备绑定回调
    public void onEventMainThread(DeviceBindResultEvent event) {
        updateUI();
        if (event.isSuccess()) {
            //登录设备
            SettingManager settingManager = SettingManager.getInstance(this);
            CmdCenter.getInstance(this).cGetBoundDevices(
                    settingManager.getUid(),
                    settingManager.getToken()
            );
            Toast.show("设备绑定成功");
        } else {
            Toast.show("设备绑定失败");
        }
    }

    //获取绑定设备回调
    public void onEventMainThread(GetBoundDeviceEvent event) {
        SettingManager settingManager = SettingManager.getInstance(this);
        if (event.isSuccess()) {
            //为空直接返回
            if (event.getXpgDeviceList().size() == 0) {
                return;
            }
            //登陆本地最后一次扫描的设备
            for (XPGWifiDevice device : event.getXpgDeviceList()) {
                if (device.getDid().equals(settingManager.getCurrentDid())) {
                    //设置当前设备
                    XPGController.getInstance(this).setCurrentDevice(device);
                    XPGController.getInstance(this).refreshCurrentDeviceListener();
                    //登陆当前设备
                    device.login(
                            settingManager.getUid(),
                            settingManager.getToken()
                    );
                }
            }
        } else {
            Toast.show("获取绑定设备失败");
        }
        updateUI();
    }

    //设备登陆回调
    public void onEventMainThread(XpgDeviceLoginEvent event) {
        Timber.e("device login  event in main thread");
        updateUI();
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
            case R.id.id_menu_setting:
                SettingActivity.start(this);
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
