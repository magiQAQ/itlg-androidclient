package com.itlg.client.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.itlg.client.R;
import com.itlg.client.UserInfoHolder;
import com.itlg.client.bean.FarmInfoModel;
import com.itlg.client.bean.FrontFarmModel;
import com.itlg.client.bean.ProductInfo;
import com.itlg.client.bean.UserInfo;
import com.itlg.client.biz.FarmInfoBiz;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.itlg.client.ui.adapter.PreviewProductAdapter;
import com.itlg.client.utils.MyUtils;
import com.itlg.client.utils.ToastUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class FrontDetailFarmActivity extends BaseActivity {

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

    @Override
    protected void onDestroy() {
        farmInfoBiz.onDestroy();
        super.onDestroy();
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
        setupCommonToolbar();
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
        farmNoteTextView.setText(frontFarmModel.getFarmInfo().getNote());
        //农场经度
        longitudeTextView.setText(getString(R.string.longitude, frontFarmModel.getFarmInfo().getLongitude()));
        //农场纬度
        latitudeTextView.setText(getString(R.string.latitude, frontFarmModel.getFarmInfo().getLatitude()));
        //农产品清单
        loadData();
    }

    @OnClick(R.id.buy_farm_button)
    void buyFarm() {
        farmInfoBiz.buyFarm(frontFarmModel.getSaleList().getId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showToast("网络连接异常");
            }

            @Override
            public void onResponse(String response, int id) {
                if (response.isEmpty()) {
                    ToastUtils.showToast("服务器异常,返回空值");
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("succ")) {
                        //购买成功的操作
                        UserInfo userInfo = UserInfoHolder.getInstance().getUserInfo();
                        if (userInfo.getPrivilege() != 70) {
                            //将当前用户权限设为农场主权限
                            userInfo.setPrivilege(70);
                            UserInfoHolder.getInstance().setUserInfo(userInfo);
                            ToastUtils.showToast("购买成功,您现在已经是农场主了");
                        } else {
                            ToastUtils.showToast("您的名下又增加了一块田地");
                        }
                        finish();
                    } else {
                        ToastUtils.showToast(jsonObject.getString("stmt"));
                    }
                } catch (JSONException e) {
                    ToastUtils.showToast(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }


}
