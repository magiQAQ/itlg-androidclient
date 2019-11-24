package com.itlg.client.biz;

import com.itlg.client.bean.NewsInfo;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

public class NewsInfoBiz {

    /**
     * 获得新闻列表
     *
     * @param sch_page       要搜索的页数
     * @param commonCallback 得到结果的回调
     */
    public void getNewsInfos(int sch_page, CommonCallback<ArrayList<NewsInfo>> commonCallback) {
        OkHttpUtils.post()
                .url(Config.BASEURL)
                .addParams("key", "FrontTP.getNewsInfosByPhone")
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
