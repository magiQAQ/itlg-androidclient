package com.itlg.client.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.itlg.client.R;
import com.itlg.client.bean.BuyInfoModel;
import com.itlg.client.biz.BuyInfoBiz;
import com.itlg.client.ui.adapter.DetailMyCartAdapter;
import com.itlg.client.utils.MyUtils;
import com.itlg.client.utils.ToastUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class MyCartDetailActivity extends BaseActivity {

    private static final String TAG = "MyCartDetailActivity";
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.total_price_textView)
    TextView totalPriceTextView;
    private ArrayList<BuyInfoModel> buyInfoModels;
    private BuyInfoBiz buyInfoBiz;
    private DetailMyCartAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart_detail);
        ButterKnife.bind(this);
        setStatusBarColor(R.color.white, false);
        buyInfoBiz = new BuyInfoBiz();

        buyInfoModels = getIntent().getParcelableArrayListExtra(MyUtils.KEY_BUY_INFO_MODELS);
        adapter = new DetailMyCartAdapter(this, buyInfoModels);
        recyclerView.setAdapter(adapter);
        adapter.setOnRemoveButtonClickListener(buyInfoModel -> {
            //弹出提示信息询问用户是否确认删除
            AlertDialog.Builder builder = new AlertDialog.Builder(MyCartDetailActivity.this);
            View dialogView = LayoutInflater.from(MyCartDetailActivity.this).inflate(R.layout.dialog_confirm, linearLayout, false);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            //设置提示信息
            TextView messageTextView = dialogView.findViewById(R.id.message_textView);
            String message = "确定要删除商品" + buyInfoModel.getProductname() + "吗";
            messageTextView.setText(message);
            //设置取消按钮
            dialogView.findViewById(R.id.cancel_button).setOnClickListener(v -> alertDialog.dismiss());
            //设置确认按钮
            dialogView.findViewById(R.id.confirm_button).setOnClickListener(v -> {
                removeBuyInfoModel(buyInfoModel);
                alertDialog.dismiss();
            });
            //显示提示消息
            alertDialog.show();
        });

        calculateTotalPrice();
    }

    @OnClick(R.id.pay_button)
    void onPayButtonClick() {
        //弹出提示信息询问用户是否确认付款
        AlertDialog.Builder builder = new AlertDialog.Builder(MyCartDetailActivity.this);
        View dialogView = LayoutInflater.from(MyCartDetailActivity.this).inflate(R.layout.dialog_confirm, linearLayout, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        //设置提示信息
        TextView messageTextView = dialogView.findViewById(R.id.message_textView);
        String message = "确定要付款吗";
        messageTextView.setText(message);
        //设置取消按钮
        dialogView.findViewById(R.id.cancel_button).setOnClickListener(v -> alertDialog.dismiss());
        //设置确认按钮
        dialogView.findViewById(R.id.confirm_button).setOnClickListener(v -> {
            pay();
            alertDialog.dismiss();
        });
        //显示提示消息
        alertDialog.show();
    }

    private void calculateTotalPrice() {
        float totalPrice = 0f;
        if (buyInfoModels != null) {
            for (BuyInfoModel model : buyInfoModels) {
                totalPrice += model.getPrice();
            }
        }
        totalPriceTextView.setText(String.format(Locale.CHINA, "￥%.1f", totalPrice));
    }

    //删除购物车内物品的操作
    private void removeBuyInfoModel(BuyInfoModel buyInfoModel) {
        buyInfoBiz.removeBuyInfoModel(buyInfoModel.getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("succ")) {
                        showCompleteDialog("删除成功");
                        buyInfoModels.remove(buyInfoModel);
                        //刷新列表
                        adapter.notifyDataSetChanged();
                        //重新计算总价
                        calculateTotalPrice();
                    } else {
                        ToastUtils.showToast(jsonObject.getString("stmt"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    //完成购买操作
    private void pay() {
        buyInfoBiz.pay(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("succ")) {
                        showCompleteDialog("支付成功");
                        //刷新列表
                        buyInfoModels.clear();
                        adapter.notifyDataSetChanged();
                        //计算总价
                        calculateTotalPrice();
                    } else {
                        ToastUtils.showToast(jsonObject.getString("stmt"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

}
