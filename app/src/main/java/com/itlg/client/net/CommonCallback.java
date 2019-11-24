package com.itlg.client.net;

import android.util.Log;

import com.google.gson.internal.$Gson$Types;
import com.itlg.client.utils.GsonUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

import okhttp3.Call;

public abstract class CommonCallback<T> extends StringCallback {

    private static final String TAG = "CommonCallback";
    private Type type;

    protected CommonCallback() {
        type = getSuperclassTypeParameter(getClass());
    }

    /**
     * 得到泛型参数T
     *
     * @param subclass 需要被提取泛型参数的类
     * @return 第一个泛型参数
     */
    private static Type getSuperclassTypeParameter(Class<?> subclass) {
        //返回包含泛型参数的超类
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {//说明没有给泛型
            throw new RuntimeException("Missing type parameter.");
        }
        //参数化类型
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(Objects.requireNonNull(parameterized).getActualTypeArguments()[0]);
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        onFail(e);
    }

    public abstract void onFail(Exception e);

    public abstract void onSuccess(T response);

    @Override
    public void onResponse(String response, int id) {
        if (response.isEmpty()) {
            Log.e(TAG, "response 为空字符串");
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean succ = jsonObject.getBoolean("succ");
            if (succ) {
                String data = jsonObject.getString("data");
                onSuccess(GsonUtils.getGson().fromJson(data, type));
            } else {
                onFail(new RuntimeException(jsonObject.getString("stmt")));
            }
        } catch (JSONException e) {
            Log.e(TAG, response);
            onFail(e);
            e.printStackTrace();
        }
    }


}
