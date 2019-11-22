package com.itlg.client.biz;

import com.itlg.client.bean.ProductInfo;
import com.itlg.client.bean.ProductTypes;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductInfoBiz {

    /**
     * 得到产品类型的方法
     *
     * @param commonCallback 得到结果后的回调
     */
    public void getProductTypes(CommonCallback<ArrayList<ProductTypes>> commonCallback) {
        OkHttpUtils.post()
                .url(Config.BASEURL)
                .addParams("key", "FrontShopTP.getProductTypesByPhone")
                .tag(this)
                .build()
                .execute(commonCallback);
    }

    /**
     * 得到农产品列表的方法
     *
     * @param sch_page       查找的页数
     * @param sch_type       查找的类型
     * @param sch_keyword    查找的关键字
     * @param sch_order      价格排序顺序
     * @param commonCallback 得到结果后的回调
     */
    public void getProductInfos(int sch_page, int sch_type, String sch_keyword, String sch_order,
                                CommonCallback<ArrayList<ProductInfo>> commonCallback) {

        Map<String, String> map = new HashMap<>();
        map.put("sch_page", String.valueOf(sch_page));
        if (sch_type != 0) {
            map.put("sch_type", String.valueOf(sch_type));
        }
        if (!sch_keyword.isEmpty()) {
            map.put("sch_keyword", sch_keyword);
        }
        if (!sch_order.isEmpty()) {
            map.put("sch_order", sch_order);
        }

        OkHttpUtils.post()
                .url(Config.BASEURL)
                .addParams("key", "FrontShopTP.getProductInfosByPhone")
                .params(map)
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
