package com.itlg.client.biz;

import androidx.constraintlayout.solver.widgets.ConstraintWidgetGroup;

import com.itlg.client.bean.User;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;


public class UserBiz {

    /**
     * 用户登录的方法
     *
     * @param username 用户名
     * @param password 密码
     * @param commonCallback post发送后执行的回调
     */
    public void login(String username, String password, CommonCallback<User> commonCallback){
        OkHttpUtils.post()
                .url(Config.BASEURL)
                .tag(this)
                .addParams("key","UserInfoTP.loginByPhone")
                .addParams("username", username)
                .addParams("password", password)
                .build()
                .execute(commonCallback);
    }

    /**
     * 用户注册的方法 (含头像)
     *
     * @param user 构造好的user对象
     * @param password2 第二遍重复输入的密码
     * @param userImg 用户的头像
     * @param commonCallback post发送后执行的回调
     */
    public void register(User user, String password2, File userImg, StringCallback commonCallback){

        OkHttpUtils.post()
                .url(Config.BASEURL+"?key=UserInfoTP.registerByPhone")
                //.url(Config.BASEURL)
                .tag(this)
                //.addParams("key","UserInfoTP.registerByPhone")
                .addParams("id", String.valueOf(user.getId()))
                .addParams("name",user.getName())
                .addParams("username",user.getUsername())
                .addParams("password",user.getPassword())
                .addParams("password2",password2)
                .addParams("cellphone",user.getCellphone())
                .addParams("email",user.getEmail())
                .addParams("privilege",String.valueOf(user.getPrivilege()))
                .addFile("file",userImg.getName(),userImg)
                .build()
                .execute(commonCallback);

    }

    /**
     * 用户注册的方法 (无头像)
     *
     * @param user 构造好的user对象
     * @param password2 第二遍重复输入的密码
     * @param commonCallback post发送后执行的回调
     */
    public void register(User user, String password2, StringCallback commonCallback){

        OkHttpUtils.post()
                .url(Config.BASEURL)
                .tag(this)
                .addParams("key","UserInfoTP.registerByPhone")
                .addParams("id", String.valueOf(user.getId()))
                .addParams("name",user.getName())
                .addParams("username",user.getUsername())
                .addParams("password",user.getPassword())
                .addParams("password2",password2)
                .addParams("cellphone",user.getCellphone())
                .addParams("email",user.getEmail())
                .addParams("privilege",String.valueOf(user.getPrivilege()))
                .build()
                .execute(commonCallback);

    }

    /**
     * 取消该Biz中的网络请求,一般在Activity中OnDestroy()时调用
     */
    public void onDestroy(){
        //取消相应的请求
        OkHttpUtils.getInstance().cancelTag(this);
    }
}
