package com.itlg.client.ui.activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.itlg.client.R;
import com.itlg.client.bean.ProductInfo;
import com.itlg.client.config.Config;
import com.itlg.client.utils.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductDetailActivity extends BaseActivity {

    @BindView(R.id.product_img_imageView)
    ImageView productImgImageView;
    @BindView(R.id.product_price_textView)
    TextView productPriceTextView;
    @BindView(R.id.product_unit_textView)
    TextView productUnitTextView;
    @BindView(R.id.remove_button)
    ImageButton removeButton;
    @BindView(R.id.product_note_textView)
    TextView productNoteTextView;
    @BindView(R.id.add_button)
    ImageButton addButton;
    private ProductInfo productInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);

        productInfo = getIntent().getParcelableExtra(MyUtils.KEY_PRODUCT_INFO_DETAIL);

        initView();
    }

    private void initView() {
        setupCommonToolbar();
        Glide.with(this).load(Config.FILEURL + productInfo.getImg()).into(productImgImageView);
        setTitle(productInfo.getProductName());

        productPriceTextView.setText(String.valueOf(productInfo.getPrice()));
        productUnitTextView.setText(String.valueOf(productInfo.getUnit()));
        productNoteTextView.setText(productInfo.getNote());
    }


}
