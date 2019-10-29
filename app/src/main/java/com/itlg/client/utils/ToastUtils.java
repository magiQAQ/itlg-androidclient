package com.itlg.client.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast的工具类
 * Created by zhy on 16/10/23.
 */
public class ToastUtils {

    private static Toast toast;

    public static void showToast(
            String content) {
        toast.setText(content);
        toast.show();
    }

    public static void init(Context context) {
        toast = Toast.makeText(context,
                "",
                Toast.LENGTH_SHORT);
    }
}
