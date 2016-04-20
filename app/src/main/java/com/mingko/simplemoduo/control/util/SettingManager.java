/**
 * Project Name:XPGSdkV4AppBase
 * File Name:SettingManager.java
 * Package Name:com.gizwits.framework.sdk
 * Date:2015-1-27 14:47:24
 * Copyright (c) 2014~2015 Xtreme Programming Group, Inc.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.mingko.simplemoduo.control.util;

import android.content.Context;
import android.content.SharedPreferences;

import timber.log.Timber;

/**
 * SharePreference处理类.
 * 增删:
 * 机智云参数  众云参数  机智云登录缓存参数
 */
public class SettingManager {

    private SharedPreferences spf;
    private static SettingManager instance;

    //第一次进去
    private static final String IS_FIST_IN = "isFistIn";

    /**
     * preference文件名
     */
    private static final String SHARE_PREFERENCES = "set";

    /**
     * 用户参数
     */
    private static final String USER_NAME = "username";
    private static final String PASSWORD = "password";
    private static final String KEY_GESTURE_LOCK = "gesture_lock";
    // 用户登陆缓存数据
    private static final String TOKEN = "token";
    private static final String UID = "uid";

    /**
     * 机智云参数
     */
    private static final String DID = "did";
    private static final String PASSCODE = "passcode";

    /**
     * 构造方法
     *
     * @param context
     */
    private SettingManager(Context context) {
        Context context1 = context;
        spf = context.getSharedPreferences(SHARE_PREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static SettingManager getInstance(Context context) {
        if (instance == null) {
            instance = new SettingManager(context);
        }
        return instance;
    }

    /**
     * SharePreference cleanUserInfo.
     */
    public void cleanUserInfo() {
        //清除登陆信息
        setUid("");
        setToken("");
        //清除账户信息
        setPassword("");
        setUserName("");
    }

    //是否第一次
    public boolean isFistIn() {
        return spf.getBoolean(IS_FIST_IN, true);
    }

    public void setIsFistIn(boolean isFistIn) {
        spf.edit()
                .putBoolean(IS_FIST_IN, isFistIn)
                .commit();
    }

    //清除本地魔哆数据
    public void cleanLocalModuo() {
        //清除设备信息
        setCurrentDid("");
        setPasscode("");
    }

    public boolean hasLocalModuo() {
        if (getCurrentDid() != null && getCurrentDid().length() != 0
                && getPasscode() != null && getPasscode().length() != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否已经登陆
     *
     * @return
     */
    public boolean isLogined() {
        String uid = spf.getString(UID, null);
        String token = spf.getString(TOKEN, null);
        if (uid != null && uid.length() > 0
                && token != null && token.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否为匿名用户
     *
     * @return
     */
    public boolean isAnonymousUser() {
        String username = spf.getString(USER_NAME, null);
        String password = spf.getString(PASSWORD, null);
        if (username != null && username.length() > 0
                && password != null && password.length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void setCurrentDid(String did) {
        Timber.e("将当前设备Did改为:\t" + did);
        spf.edit()
                .putString(DID, did)
                .commit();
    }

    public String getCurrentDid() {
        return spf.getString(DID, null);
    }

    public void setPasscode(String passcode) {
        spf.edit()
                .putString(PASSCODE, passcode)
                .commit();
    }

    public String getPasscode() {
        return spf.getString(PASSCODE, null);
    }

    public void setUserName(String name) {
        spf.edit().putString(USER_NAME, name).commit();
    }

    public String getUserName() {
        return spf.getString(USER_NAME, "");
    }

    public void setPassword(String psw) {
        spf.edit().putString(PASSWORD, psw).commit();
    }

    public String getPassword() {
        return spf.getString(PASSWORD, "");
    }

    public void setToken(String token) {
        spf.edit().putString(TOKEN, token).commit();
    }

    public String getToken() {
        return spf.getString(TOKEN, "");
    }

    public void setUid(String uid) {
        spf.edit().putString(UID, uid).commit();
    }

    public String getUid() {
        return spf.getString(UID, "");
    }

}
