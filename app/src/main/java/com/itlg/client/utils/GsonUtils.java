package com.itlg.client.utils;

import com.google.gson.Gson;

/**
 * Created by zhy on 16/10/23.
 */
public class GsonUtils {

    public static Gson gson = new Gson();

    public static Gson getGson() {
        return gson;
    }

}
