package com.itlg.client.biz;

import com.itlg.client.bean.FarmInfoModel;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

public class FarmInfoBiz {

    /**
     * 得到农场信息
     *
     * @param qrCodeContent  从二维码中解析出的参数
     * @param commonCallback 得到信息后执行的操作
     */
    public void getFarmInfoModel(String qrCodeContent, CommonCallback<FarmInfoModel> commonCallback) {
        OkHttpUtils.get()
                .url(qrCodeContent)
                .tag(this)
                .build()
                .execute(commonCallback);
    }

    /**
     * 得到当前登录用户负责的所有农场信息
     *
     * @param commonCallback 得到信息后执行的操作
     */
    public void getFarmInfoModels(CommonCallback<ArrayList<FarmInfoModel>> commonCallback) {
        OkHttpUtils.post()
                .url(Config.BASEURL)
                .addParams("key", "FarmInfoTP.getFarmInfosByPhone")
                .tag(this)
                .build()
                .execute(commonCallback);
    }

    /**
     * 取消该Biz中的网络请求,一般在Activity中OnDestroy()时调用
     */
    public void onDestroy() {
        //取消相应的请求
        OkHttpUtils.getInstance().cancelTag(this);
    }

}
