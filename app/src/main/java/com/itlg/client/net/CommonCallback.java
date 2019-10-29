package com.itlg.client.net;

import android.util.Log;

import com.google.gson.internal.$Gson$Types;
import com.itlg.client.utils.GsonUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;

public abstract class CommonCallback<T> extends StringCallback {

    private static final String TAG = "CommonCallback";
    private Type type;
    private String typeName;

    public CommonCallback() {
        type = getSuperclassTypeParameter(getClass());
        typeName = getTypeName(type);
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
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    /**
     * @param type type
     * @return 得到当前type的类名
     */
    private static String getTypeName(Type type) {
        // 像. | 这样的符号需要加上转义
        String[] temp = type.toString().split("\\.");
        return temp[temp.length - 1];
    }


    @Override
    public void onError(Call call, Exception e, int id) {
        onFail(e);
    }

    public abstract void onFail(Exception e);

    public abstract void onSuccess(T response);

    @Override
    public void onResponse(String response, int id) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean succ = jsonObject.getBoolean("succ");
            if (succ) {
                JSONObject data = jsonObject.getJSONObject("data");
                onSuccess(GsonUtils.getGson().fromJson(data.getString(typeName.toLowerCase()), type));
            } else {
                onFail(new RuntimeException(jsonObject.getString("stmt")));
            }
        } catch (JSONException e) {
            Log.e("json", response);
            onFail(e);
            e.printStackTrace();
        }
    }


}
