package com.itlg.client.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.itlg.client.R;
import com.itlg.client.bean.OperationLogModel;
import com.itlg.client.bean.ProductInfoModel;
import com.itlg.client.biz.ProductInfoBiz;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.itlg.client.ui.adapter.TimeAxisAdapter;
import com.itlg.client.utils.MyUtils;
import com.itlg.client.utils.ToastUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class ProductDetailActivity extends BaseActivity {

    private static final String TAG = "ProductDetailActivity";

    @BindView(R.id.product_img_imageView)
    ImageView productImgImageView;
    @BindView(R.id.product_price_textView)
    TextView productPriceTextView;
    @BindView(R.id.product_unit_textView)
    TextView productUnitTextView;
    @BindView(R.id.decrease_button)
    ImageButton decreaseButton;
    @BindView(R.id.insert_button)
    ImageButton addButton;
    @BindView(R.id.product_note_textView)
    TextView productNoteTextView;
    @BindView(R.id.tip1)
    TextView tip1;
    @BindView(R.id.tip2)
    TextView tip2;
    @BindView(R.id.product_count_editText)
    EditText productCountEditText;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private int productId;
    private ProductInfoBiz productInfoBiz;
    private MyHandler myHandler = new MyHandler(this);
    private TimeAxisAdapter adapter;
    private ProductInfoModel productInfoModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);

        productInfoBiz = new ProductInfoBiz();
        productId = getIntent().getIntExtra(MyUtils.KEY_PRODUCT_ID, 0);
        if (productId == 0) {
            finish();
        }
        initView();
    }

    private void initView() {
        setupCommonToolbar();
        disableAddButton();
        disableRemoveButton();
        getProductInfoModel();

        productCountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int size) {
                if (size == 0) {
                    return;
                }
                int count = Integer.parseInt(s.toString());
                refreshButtonStatus(count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    productCountEditText.setText("1");
                }
            }

            private void refreshButtonStatus(int count) {
                if (count < 2) {
                    disableRemoveButton();
                    enableAddButton();
                } else if (count > 98) {
                    disableAddButton();
                    enableRemoveButton();
                } else {
                    enableAddButton();
                    enableRemoveButton();
                }
            }
        });
    }

    @OnClick(R.id.insert_button)
    void increaseCount() {
        int count = Integer.parseInt(productCountEditText.getEditableText().toString());
        if (count < 99) {
            count++;
            productCountEditText.setText(String.valueOf(count));
        }
    }

    @OnClick(R.id.decrease_button)
    void decreaseCount() {
        int count = Integer.parseInt(productCountEditText.getEditableText().toString());
        if (count > 1) {
            count--;
            productCountEditText.setText(String.valueOf(count));
        }
    }

    @OnClick(R.id.add_cart_button)
    void addToCart() {
        productInfoBiz.addToCart(productInfoModel.getProductInfo().getId(),
                Integer.parseInt(productCountEditText.getEditableText().toString()),
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("succ")) {
                                ToastUtils.showToast("商品已加入购物车");
                            } else {
                                ToastUtils.showToast(jsonObject.getString("stmt"));
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                            ToastUtils.showToast("添加商品失败,错误" + e.getMessage());
                        }
                    }
                });
    }

    //得到商品信息
    public void getProductInfoModel() {
        productInfoBiz.getProductInfoModel(productId, new CommonCallback<ProductInfoModel>() {
            @Override
            public void onFail(Exception e) {
                Log.e(TAG, e.getMessage());
                ToastUtils.showToast("网络不给力哦,3s后为您重试");
                myHandler.sendEmptyMessageDelayed(0, 3000);
            }

            @Override
            public void onSuccess(ProductInfoModel response) {
                productInfoModel = response;
                Glide.with(ProductDetailActivity.this)
                        .load(Config.FILEURL + productInfoModel.getProductInfo().getImg()).into(productImgImageView);
                setTitle(productInfoModel.getProductInfo().getProductName());

                productPriceTextView.setText(String.valueOf(productInfoModel.getProductInfo().getPrice()));
                productUnitTextView.setText(String.valueOf(productInfoModel.getProductInfo().getUnit()));
                productNoteTextView.setText(productInfoModel.getProductInfo().getNote());

                tip1.setText(productInfoModel.getTypeOneName());
                tip2.setText(productInfoModel.getTypeTwoName());

                enableAddButton();

                loadTimeAxis();
            }
        });
    }

    private void loadTimeAxis() {
        productInfoBiz.getTimeAxis(productInfoModel.getProductInfo().getFarmId(), new CommonCallback<List<OperationLogModel>>() {
            @Override
            public void onFail(Exception e) {
                ToastUtils.showToast(e.getMessage());
            }

            @Override
            public void onSuccess(List<OperationLogModel> response) {
                adapter = new TimeAxisAdapter(ProductDetailActivity.this, response);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private void disableRemoveButton() {
        decreaseButton.setEnabled(false);
        decreaseButton.setColorFilter(Color.rgb(0xAA, 0xAA, 0xAA));
    }

    private void enableRemoveButton() {
        decreaseButton.setEnabled(true);
        decreaseButton.setColorFilter(Color.rgb(0x8A, 0x8A, 0x8A));
    }

    private void disableAddButton() {
        addButton.setEnabled(false);
        addButton.setColorFilter(Color.rgb(0xAA, 0xAA, 0xAA));
    }

    private void enableAddButton() {
        addButton.setEnabled(true);
        addButton.setColorFilter(Color.rgb(0x8A, 0x8A, 0x8A));
    }

    @Override
    protected void onDestroy() {
        productInfoBiz.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    //用于计时三秒
    static class MyHandler extends Handler {


        private WeakReference<ProductDetailActivity> activityWeakReference;

        MyHandler(ProductDetailActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0)
                activityWeakReference.get().getProductInfoModel();
        }
    }


}
