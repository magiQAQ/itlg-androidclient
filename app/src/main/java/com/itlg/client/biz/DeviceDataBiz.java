package com.itlg.client.biz;

import com.itlg.client.bean.DeviceDataModel;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

public class DeviceDataBiz {
    /**
     * 得到设备使用记录的方法
     *
     * @param farmId         农场Id
     * @param sch_page       要查询的页数
     * @param commonCallback 得到结果后执行的操作
     */
    public void getDeviceData(int farmId, int sch_page, CommonCallback<ArrayList<DeviceDataModel>> commonCallback) {
        OkHttpUtils.post()
                .url(Config.BASEURL)
                .addParams("key", "DeviceDataTP.getDeviceDataByPhone")
                .addParams("farmId", String.valueOf(farmId))
                .addParams("sch_page", String.valueOf(sch_page))
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
