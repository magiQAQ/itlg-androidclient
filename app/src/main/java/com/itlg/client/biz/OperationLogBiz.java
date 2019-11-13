package com.itlg.client.biz;

import com.itlg.client.bean.OperationLog;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

public class OperationLogBiz {

    /**
     * 用户获得操作日志的方法
     *
     * @param farmId 当前农场的id
     * @param sch_page 查询的页数
     * @param commonCallback 获得数据后的回调操作
     */
    public void getOperationLogs(int farmId, int sch_page, CommonCallback<ArrayList<OperationLog>> commonCallback) {
        OkHttpUtils.post()
                .url(Config.BASEURL)
                .addParams("key", "OperationLogTP.getOperationLogByPhone")
                .addParams("farmId", String.valueOf(farmId))
                .addParams("sch_page",String.valueOf(sch_page))
                .tag(this)
                .build()
                .execute(commonCallback);
    }

    /**
     * 用户提交操作日志的方法
     *
     * @param farmId         当前农场的id
     * @param operationInfo  操作内容
     * @param stringCallback 获得数据后的回调操作
     */
    public void commitOperationLog(int farmId, String operationInfo, StringCallback stringCallback) {
        OkHttpUtils.post()
                .url(Config.BASEURL)
                .addParams("key", "OperationLogTP.commitOperationLogByPhone")
                .addParams("farmId", String.valueOf(farmId))
                .addParams("operationInfo", operationInfo)
                .tag(this)
                .build()
                .execute(stringCallback);
    }

    /**
     * 取消该Biz中的网络请求,一般在Activity中OnDestroy()时调用
     */
    public void onDestroy(){
        //取消相应的请求
        OkHttpUtils.getInstance().cancelTag(this);
    }
}
