package com.itlg.client.biz;

import com.itlg.client.bean.BuyInfoModel;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

public class BuyInfoBiz {

    /**
     * 得到用户购物车的方法
     *
     * @param commonCallback 得到结果后执行的操作
     */
    public void getBuyInfoModels(CommonCallback<ArrayList<BuyInfoModel>> commonCallback) {
        OkHttpUtils.post()
                .url(Config.BASEURL)
                .addParams("key", "BuyInfoTP.getBuyInfoModelsByPhone")
                .tag(this)
                .build()
                .execute(commonCallback);
    }

    /**
     * 从购物车中移除商品
     *
     * @param id       订单id
     * @param callback 得到结果后执行的操作
     */
    public void removeBuyInfoModel(int id, StringCallback callback) {
        OkHttpUtils.post()
                .url(Config.BASEURL)
                .addParams("key", "BuyInfoTP.del")
                .addParams("id", String.valueOf(id))
                .tag(this)
                .build()
                .execute(callback);
    }

    /**
     * 用户完成付款的操作
     *
     * @param callback 得到结果后执行的操作
     */
    public void pay(StringCallback callback) {
        OkHttpUtils.post()
                .url(Config.BASEURL)
                .addParams("key", "FrontShopTP.pay")
                .tag(this)
                .build()
                .execute(callback);
    }

    /**
     * 取消该Biz中的网络请求,一般在Activity中OnDestroy()时调用
     */
    public void onDestroy() {
        //取消相应的请求
        OkHttpUtils.getInstance().cancelTag(this);
    }
}
