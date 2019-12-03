package com.itlg.client.biz;

import com.itlg.client.bean.UserInfo;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;


public class UserBiz {

    /**
     * 用户登录的方法
     *
     * @param username       用户名
     * @param password       密码
     * @param commonCallback post发送后执行的回调
     */
    public void login(String username, String password, CommonCallback<UserInfo> commonCallback) {
        OkHttpUtils.post()
                .url(Config.BASEURL)
                .tag(this)
                .addParams("key", "UserInfoTP.loginByPhone")
                .addParams("username", username)
                .addParams("password", password)
                .build()
                .execute(commonCallback);
    }

    /**
     * 用户注册的方法 (含头像)
     *
     * @param userInfo           构造好的user对象
     * @param password2      第二遍重复输入的密码
     * @param userImg        用户的头像
     * @param stringCallback post发送后执行的回调
     */
    public void register(UserInfo userInfo, String password2, File userImg, StringCallback stringCallback) {
        OkHttpUtils.post()
                .url(Config.BASEURL + "?key=UserInfoTP.registerByPhone")
                //.url(Config.BASEURL)
                .tag(this)
                //.addParams("key","UserInfoTP.registerByPhone")
                .addParams("id", String.valueOf(userInfo.getId()))
                .addParams("name", userInfo.getName())
                .addParams("username", userInfo.getUsername())
                .addParams("password", userInfo.getPassword())
                .addParams("password2", password2)
                .addParams("cellphone", userInfo.getCellphone())
                .addParams("email", userInfo.getEmail())
                .addParams("privilege", String.valueOf(userInfo.getPrivilege()))
                .addFile("file", userImg.getName(), userImg)
                .build()
                .execute(stringCallback);
    }

    /**
     * 用户注册的方法 (无头像)
     *
     * @param userInfo           构造好的user对象
     * @param password2      第二遍重复输入的密码
     * @param stringCallback post发送后执行的回调
     */
    public void register(UserInfo userInfo, String password2, StringCallback stringCallback) {
        OkHttpUtils.post()
                .url(Config.BASEURL)
                .tag(this)
                .addParams("key", "UserInfoTP.registerByPhone")
                .addParams("id", String.valueOf(userInfo.getId()))
                .addParams("name", userInfo.getName())
                .addParams("username", userInfo.getUsername())
                .addParams("password", userInfo.getPassword())
                .addParams("password2", password2)
                .addParams("cellphone", userInfo.getCellphone())
                .addParams("email", userInfo.getEmail())
                .addParams("privilege", String.valueOf(userInfo.getPrivilege()))
                .build()
                .execute(stringCallback);
    }

    /**
     * 用户退出登录的方法
     *
     * @param stringCallback get发送后执行的回调
     */
    public void logout(StringCallback stringCallback) {
        OkHttpUtils.get()
                .url(Config.BASEURL)
                .addParams("key", "UserInfoTP.loginout")
                .tag(this)
                .build()
                .execute(stringCallback);
    }

    /**
     * 取消该Biz中的网络请求,一般在Activity中OnDestroy()时调用
     */
    public void onDestroy() {
        //取消相应的请求
        OkHttpUtils.getInstance().cancelTag(this);
    }
}
