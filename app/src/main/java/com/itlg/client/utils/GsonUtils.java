package com.itlg.client.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by zhy on 16/10/23.
 */
public class GsonUtils {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static Gson getGson() {
        return gson;
    }

}
