package com.mingko.simplemoduo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.mingko.simplemoduo.R;
import com.mingko.simplemoduo.control.util.SettingManager;
import com.mingko.simplemoduo.control.util.Toast;
import com.mingko.simplemoduo.control.xpg.CmdCenter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ssthouse on 16/4/21.
 */
public class SettingActivity extends AppCompatActivity {

    @Bind(R.id.id_et_username)
    EditText etUsername;

    @Bind(R.id.id_et_password)
    EditText etPassword;

    @Bind(R.id.id_et_did)
    EditText etDid;

    @Bind(R.id.id_et_passcode)
    EditText etPasscode;

    public static void start(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.id_tb));
        getSupportActionBar().setTitle("设置");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initSettingView();
    }

    //初始化settingView
    private void initSettingView() {
        SettingManager settingManager = SettingManager.getInstance(this);

        etUsername.setText(settingManager.getUserName());
        etPassword.setText(settingManager.getPassword());

        etDid.setText(settingManager.getCurrentDid());
        etPasscode.setText(settingManager.getPasscode());
    }

    //保存当前setting
    private void saveSetting() {
        SettingManager settingManager = SettingManager.getInstance(this);
        String username = etUsername.getText() + "";
        String password = etPassword.getText() + "";
        String did = etDid.getText() + "";
        String passcode = etPasscode.getText() + "";
        //判空
        if (TextUtils.isEmpty(did)
                || TextUtils.isEmpty(passcode)) {
            Toast.show("did和passcode不可为空");
            return;
        }
        //settingManager.setUserName(username);
        //settingManager.setPassword(password);
        settingManager.setCurrentDid(did);
        settingManager.setPasscode(passcode);

        //修改绑定设备   进行绑定
        CmdCenter.getInstance(this).cBindDevice(settingManager.getUid(),
                settingManager.getToken(),
                settingManager.getCurrentDid(),
                settingManager.getPasscode(),
                "我的魔哆");

        //toast提示保存成功
        Toast.show("保存成功");
    }

    //******************* menu ********************************

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.id_menu_save_setting:
                //保存设置
                saveSetting();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
