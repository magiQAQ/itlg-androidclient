package com.itlg.client.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.itlg.client.R;
import com.itlg.client.bean.ProductInfoModel;
import com.itlg.client.biz.ProductInfoBiz;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.itlg.client.utils.MyUtils;
import com.itlg.client.utils.ToastUtils;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

public class ProductDetailActivity extends BaseActivity {

    private static final String TAG = "ProductDetailActivity";

    @BindView(R.id.product_img_imageView)
    ImageView productImgImageView;
    @BindView(R.id.product_price_textView)
    TextView productPriceTextView;
    @BindView(R.id.product_unit_textView)
    TextView productUnitTextView;
    @BindView(R.id.remove_button)
    ImageButton removeButton;
    @BindView(R.id.add_button)
    ImageButton addButton;
    @BindView(R.id.product_note_textView)
    TextView productNoteTextView;
    @BindView(R.id.tip1)
    TextView tip1;
    @BindView(R.id.tip2)
    TextView tip2;
    @BindView(R.id.product_count_editText)
    EditText productCountEditText;

    private int productId;
    private ProductInfoBiz productInfoBiz;
    private MyHandler myHandler = new MyHandler(this);
    private boolean hasScrolled = false;

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

    @Override
    protected void onDestroy() {
        productInfoBiz.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void initView() {
        setupCommonToolbar();
        disableAddButton();
        disableRemoveButton();
        getProductInfoModel();

        removeButton.setOnClickListener(v -> {
            int count = Integer.parseInt(productCountEditText.getEditableText().toString());
            if (count > 1) {
                count--;
                productCountEditText.setText(String.valueOf(count));
            }
        });

        addButton.setOnClickListener(v -> {
            int count = Integer.parseInt(productCountEditText.getEditableText().toString());
            if (count < 100) {
                count++;
                productCountEditText.setText(String.valueOf(count));
            }
        });
    }

    @OnTextChanged(R.id.product_count_editText)
    void countChanged(CharSequence sequence) {
        int count = Integer.parseInt(sequence.toString());
        if (count < 2) {
            disableRemoveButton();
            enableAddButton();
            count = 1;
        } else if (count > 99) {
            disableAddButton();
            enableRemoveButton();
            count = 100;
        } else {
            enableAddButton();
            enableRemoveButton();
        }
        productCountEditText.setText(String.valueOf(count));
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
                Glide.with(ProductDetailActivity.this)
                        .load(Config.FILEURL + response.getProductInfo().getImg()).into(productImgImageView);
                setTitle(response.getProductInfo().getProductName());

                productPriceTextView.setText(String.valueOf(response.getProductInfo().getPrice()));
                productUnitTextView.setText(String.valueOf(response.getProductInfo().getUnit()));
                productNoteTextView.setText(response.getProductInfo().getNote());

                tip1.setText(response.getTypeOneName());
                tip2.setText(response.getTypeTwoName());

                enableAddButton();
            }
        });
    }

    private void disableRemoveButton() {
        removeButton.setEnabled(false);
        removeButton.setColorFilter(Color.rgb(0xAA, 0xAA, 0xAA));
    }

    private void enableRemoveButton() {
        removeButton.setEnabled(true);
        removeButton.setColorFilter(Color.rgb(0x8A, 0x8A, 0x8A));
    }

    private void disableAddButton() {
        addButton.setEnabled(false);
        addButton.setColorFilter(Color.rgb(0xAA, 0xAA, 0xAA));
    }

    private void enableAddButton() {
        addButton.setEnabled(true);
        addButton.setColorFilter(Color.rgb(0x8A, 0x8A, 0x8A));
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
