package com.itlg.client.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.itlg.client.R;
import com.itlg.client.bean.FarmInfoModel;
import com.itlg.client.bean.FrontFarmModel;
import com.itlg.client.bean.ProductInfo;
import com.itlg.client.biz.FarmInfoBiz;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.itlg.client.ui.adapter.PreviewProductAdapter;
import com.itlg.client.utils.MyUtils;
import com.itlg.client.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FrontDetailFarmActivity extends AppCompatActivity {

    private static final String TAG = "FrontDetailFarmActivity";
    @BindView(R.id.farm_img_imageView)
    ImageView farmImgImageView;
    @BindView(R.id.farm_price_textView)
    TextView farmPriceTextView;
    @BindView(R.id.farm_note_textView)
    TextView farmNoteTextView;
    @BindView(R.id.longitude_textView)
    TextView longitudeTextView;
    @BindView(R.id.latitude_textView)
    TextView latitudeTextView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.product_list_textView)
    TextView productListTextView;

    private FrontFarmModel frontFarmModel;
    private FarmInfoModel farmInfoModel;
    private FarmInfoBiz farmInfoBiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_farm_detail);
        ButterKnife.bind(this);

        farmInfoBiz = new FarmInfoBiz();
        frontFarmModel = getIntent().getParcelableExtra(MyUtils.KEY_FRONT_FARM_MODEL);
        if (frontFarmModel == null) {
            Log.e(TAG, "没有获得农田信息");
            finish();
        }

        initView();
    }

    private void loadData() {
        farmInfoBiz.getFarmInfoModel(frontFarmModel.getFarmInfo().getId(), new CommonCallback<FarmInfoModel>() {
            @Override
            public void onFail(Exception e) {
                ToastUtils.showToast(e.getMessage());
            }

            @Override
            public void onSuccess(FarmInfoModel response) {
                farmInfoModel = response;
                List<ProductInfo> productInfos = farmInfoModel.getProductInfos();
                if (productInfos == null || productInfos.isEmpty()) {
                    productListTextView.setVisibility(View.GONE);
                    return;
                }
                PreviewProductAdapter adapter = new PreviewProductAdapter(FrontDetailFarmActivity.this,
                        productInfos);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private void initView() {
        //农场图片
        Glide.with(this).load(Config.FILEURL + frontFarmModel.getFarmInfo().getImg())
                .placeholder(R.drawable.placeholder).into(farmImgImageView);
        //农场名字
        setTitle(getString(R.string.typename_id,
                frontFarmModel.getTypeName(), frontFarmModel.getFarmInfo().getId()));
        //农场价格
        farmPriceTextView.setText(getString(R.string.price,
                frontFarmModel.getSaleList().getPrice()));
        //农场描述
        farmNoteTextView.setText("暂无消息");
        //农场经度
        longitudeTextView.setText(getString(R.string.longitude, frontFarmModel.getFarmInfo().getLongitude()));
        //农场纬度
        latitudeTextView.setText(getString(R.string.latitude, frontFarmModel.getFarmInfo().getLatitude()));
        //农产品清单
        loadData();
    }


}
