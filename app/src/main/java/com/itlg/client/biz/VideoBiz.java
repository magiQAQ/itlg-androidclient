package com.itlg.client.biz;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

public class VideoBiz {

    /**
     * 打开视频直播流
     *
     * @param stringCallback 回调操作
     */
    public void openVideo(StringCallback stringCallback) {
        OkHttpUtils.post()
                .url("http://39.99.134.143:8080/itlg/tp.do")
                .addParams("key", "FrontFarmTP.openVideo")
                .build()
                .execute(stringCallback);
    }

    /**
     * 关闭视频直播流
     */
    public void closeVideo(StringCallback stringCallback) {
        OkHttpUtils.post()
                .url("http://39.99.134.143:8080/itlg/tp.do")
                .addParams("key", "FrontFarmTP.closeVideo")
                .build()
                .execute(stringCallback);
    }


}
