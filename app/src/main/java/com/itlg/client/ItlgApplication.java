package com.itlg.client;

import android.app.Application;

import com.itlg.client.utils.SharedPreferencesUtils;
import com.itlg.client.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;

public class ItlgApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtils.init(this);
        SharedPreferencesUtils.init(this, "sp_user.pref");
        //用于保存目前已登录的用户信息等
        CookieJar cookieJar = new CookieJarImpl(new PersistentCookieStore(this));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(4, TimeUnit.SECONDS)
                .readTimeout(4, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .cookieJar(cookieJar)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }
}
