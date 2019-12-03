package com.itlg.client.biz;

import android.text.TextUtils;

import com.itlg.client.bean.FarmInfoModel;
import com.itlg.client.bean.FrontFarmModel;
import com.itlg.client.bean.ProductTypes;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
     * 得到农场信息
     *
     * @param farmId         农场序号
     * @param commonCallback 得到信息后执行的操作
     */
    public void getFarmInfoModel(int farmId, CommonCallback<FarmInfoModel> commonCallback) {
        OkHttpUtils.get()
                .url(Config.BASEURL)
                .addParams("key", "FarmInfoTP.getFarmInfoModel")
                .addParams("farmId", String.valueOf(farmId))
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
     * 获得待售农田列表
     *
     * @param sch_page       搜索的页数
     * @param sch_type       农场还是养殖场
     * @param sch_state      已售还是未售
     * @param sch_order      排序方式
     * @param commonCallback 得到结果后的回调
     */
    public void getFrontFarmModels(int sch_page, int sch_type, int sch_state, String sch_order,
                                   CommonCallback<ArrayList<FrontFarmModel>> commonCallback) {
        Map<String, String> map = new HashMap<>();
        map.put("key", "FrontFarmTP.getFrontFarmModelsByPhone");
        map.put("sch_page", String.valueOf(sch_page));
        if (sch_type > 0) {
            map.put("sch_type", String.valueOf(sch_type));
        }
        if (sch_state > 0) {
            map.put("sch_state", String.valueOf(sch_state));
        }
        if (!TextUtils.isEmpty(sch_order)) {
            map.put("sch_order", sch_order);
        }

        OkHttpUtils.post()
                .url(Config.BASEURL)
                .params(map)
                .tag(this)
                .build()
                .execute(commonCallback);
    }

    /**
     * 获得所有农场类型
     *
     * @param commonCallback 得到结果后的回调
     */
    public void getFarmTypes(CommonCallback<ArrayList<ProductTypes>> commonCallback) {
        OkHttpUtils.post()
                .url(Config.BASEURL)
                .addParams("key", "FrontFarmTP.getFarmTypesByPhone")
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
