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
import com.mingko.simplemoduo.control.util.Toast;
import com.mingko.simplemoduo.model.event.scan.ScanDeviceEvent;

import butterknife.Bind;
import butterknife.ButterKnife;

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
        initView();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Simple Moduo");
    }


    //********************** event监听 ********************************************
    //扫描设备二维码回调
    public void onEventMainThread(ScanDeviceEvent event){
        //将设备数据保存在本地

        //更新当前连接设备
    }



    //********************** menu控制 ************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.id_menu_add_moduo:
                //todo 开启扫描activity
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //扫描二维码回调
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        QrCodeUtil.parseScanResult(this, result);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
