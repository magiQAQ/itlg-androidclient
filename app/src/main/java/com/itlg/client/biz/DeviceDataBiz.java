package com.itlg.client.biz;

import com.itlg.client.bean.DeviceDataModel;
import com.itlg.client.bean.DeviceInfo;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

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
     * 得到该农场下的所有设备信息
     *
     * @param farmId         农场id
     * @param commonCallback 得到结果后执行的操作
     */
    public void getDeviceInfoByFarmId(int farmId, CommonCallback<ArrayList<DeviceInfo>> commonCallback) {
        OkHttpUtils.post()
                .url(Config.BASEURL)
                .addParams("key", "DeviceDataTP.getDeviceInfoByFarmId")
                .addParams("farmId", String.valueOf(farmId))
                .tag(this)
                .build()
                .execute(commonCallback);
    }

    /**
     * 提交设备使用记录
     *
     * @param deviceId       设备id
     * @param dataInfo       具体操作
     * @param stringCallback 得到结果后执行的操作
     */
    public void commitDeviceData(int deviceId, String dataInfo, StringCallback stringCallback) {
        OkHttpUtils.post()
                .url(Config.BASEURL)
                .addParams("key", "DeviceDataTP.commitDeviceDataByPhone")
                .addParams("deviceId", String.valueOf(deviceId))
                .addParams("dataInfo", dataInfo)
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
