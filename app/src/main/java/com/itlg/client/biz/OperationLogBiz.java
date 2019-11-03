package com.itlg.client.biz;

import com.itlg.client.bean.OperationLog;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

public class OperationLogBiz {

    /**
     * 用户获得操作日志的方法
     *
     * @param userId 当前用户的id
     * @param sch_page 查询的页数
     * @param commonCallback 获得数据后的回调操作
     */
    public void getOperationLogs(int userId, int sch_page, CommonCallback<ArrayList<OperationLog>> commonCallback){
        OkHttpUtils.post()
                .url(Config.BASEURL)
                .addParams("key", "OperationLogTP.getOperationLogByPhone")
                .addParams("userId",String.valueOf(userId))
                .addParams("sch_page",String.valueOf(sch_page))
                .tag(this)
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
