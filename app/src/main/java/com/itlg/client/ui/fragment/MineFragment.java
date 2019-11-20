package com.itlg.client.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.itlg.client.R;
import com.itlg.client.UserInfoHolder;
import com.itlg.client.bean.BuyInfoModel;
import com.itlg.client.bean.FarmInfoModel;
import com.itlg.client.biz.BuyInfoBiz;
import com.itlg.client.biz.FarmInfoBiz;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.itlg.client.ui.activity.MyCartDetailActivity;
import com.itlg.client.ui.activity.MyFarmsListActivity;
import com.itlg.client.ui.activity.NormalUserActivity;
import com.itlg.client.ui.adapter.PreViewMyCartAdapter;
import com.itlg.client.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的慧农
 */
public class MineFragment extends Fragment {

    private static final String TAG = "MineFragment";
    private static final String KEY_BUY_INFO_MODELS = "buyInfoModels";
    private static final String KEY_FARM_INFO_MODELS = "farmInfoModels";
    @BindView(R.id.userImg_imageView)
    ImageView userImgImageView;
    @BindView(R.id.my_cart_recyclerView)
    RecyclerView myCartRecyclerView;
    @BindView(R.id.empty_cart_linearLayout)
    LinearLayout emptyCartLinearLayout;
    @BindView(R.id.farm_icon_imageView)
    ImageView farmIconImageView;
    @BindView(R.id.farm_name_textView)
    TextView farmNameTextView;
    @BindView(R.id.farm_current_product_textView)
    TextView farmCurrentProductTextView;
    @BindView(R.id.my_farms_cardView)
    CardView myFarmsCardView;

    private ArrayList<BuyInfoModel> buyInfoModels;
    private ArrayList<FarmInfoModel> farmInfoModels;
    private PreViewMyCartAdapter adapter;

    private BuyInfoBiz buyInfoBiz;
    private FarmInfoBiz farmInfoBiz;

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buyInfoModels = getArguments().getParcelableArrayList(KEY_BUY_INFO_MODELS);
            farmInfoModels = getArguments().getParcelableArrayList(KEY_FARM_INFO_MODELS);
        }
        buyInfoBiz = new BuyInfoBiz();
        farmInfoBiz = new FarmInfoBiz();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //加载用户头像
        Glide.with(this).load(Config.FILEURL + UserInfoHolder.getInstance().getUser().getUserImg())
                .placeholder(R.drawable.user_default_img).circleCrop().into(userImgImageView);

        //加载购物车预览
        if (buyInfoModels != null) {
            setupMyCartRecyclerView();
        } else {
            loadMyCart();
        }

        //加载第一个农田信息
        if (farmInfoModels != null) {
            setupFarmInfoView();
        } else {
            loadFirstFarmInfo();
        }
    }

    @Override
    public void onDestroy() {
        buyInfoBiz.onDestroy();
        farmInfoBiz.onDestroy();
        super.onDestroy();
    }

    private void loadFirstFarmInfo() {
        farmInfoBiz.getFarmInfoModels(new CommonCallback<ArrayList<FarmInfoModel>>() {
            @Override
            public void onFail(Exception e) {
                Log.e(TAG, e.getMessage());
                //没有农田信息说明不是农场主
                myFarmsCardView.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(ArrayList<FarmInfoModel> response) {
                farmInfoModels = response;
                Bundle bundle = getArguments() != null ? getArguments() : new Bundle();
                bundle.putParcelableArrayList(KEY_FARM_INFO_MODELS, farmInfoModels);
                setArguments(bundle);
                setupFarmInfoView();
            }
        });
    }

    //显示农场信息
    private void setupFarmInfoView() {
        FarmInfoModel model = farmInfoModels.get(0);
        //组装当前农场种植的农产品的字符串
        StringBuilder builder = new StringBuilder();
        if (model.getFarmInfo().getTypeId() == 1) {
            builder.append("当前种植：");
            //显示农场图标
            Glide.with(Objects.requireNonNull(getActivity())).load(R.drawable.nongchang).into(farmIconImageView);
        } else if (model.getFarmInfo().getTypeId() == 2) {
            builder.append("当前养殖：");
            //显示养殖场图标
            Glide.with(Objects.requireNonNull(getActivity())).load(R.drawable.yangzhichang).into(farmIconImageView);
        }
        //农场名字
        farmNameTextView.setText(String.format(getString(R.string.typename_id), model.getTypeName(), model.getFarmInfo().getId()));
        for (int i = 0; i < model.getProductInfos().size(); i++) {
            if (i != 0) {
                builder.append("、");
            }
            builder.append(model.getProductInfos().get(i).getProductName());
            if (i > 1) {
                builder.append("等");
                break;
            }
        }
        //当前农场的农产品
        farmCurrentProductTextView.setText(builder.toString());
        //显示我拥有的农场的标签
        myFarmsCardView.setVisibility(View.VISIBLE);
    }

    /**
     * 加载购物车
     */
    private void loadMyCart() {
        buyInfoBiz.getBuyInfoModels(new CommonCallback<ArrayList<BuyInfoModel>>() {
            @Override
            public void onFail(Exception e) {
                Log.e(TAG, e.getMessage());
                //显示空购物车提示,隐藏列表
                myCartRecyclerView.setVisibility(View.GONE);
                emptyCartLinearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(ArrayList<BuyInfoModel> response) {
                buyInfoModels = response;
                Bundle bundle = getArguments() != null ? getArguments() : new Bundle();
                bundle.putParcelableArrayList(KEY_BUY_INFO_MODELS, buyInfoModels);
                setArguments(bundle);
                setupMyCartRecyclerView();
            }
        });
    }

    /**
     * 显示购物车里的列表
     */
    private void setupMyCartRecyclerView() {
        if (adapter == null) {
            adapter = new PreViewMyCartAdapter(getActivity(), buyInfoModels);
        }
        myCartRecyclerView.setAdapter(adapter);
        //显示购物车内的物品,隐藏空购物车提示
        myCartRecyclerView.setVisibility(View.VISIBLE);
        emptyCartLinearLayout.setVisibility(View.GONE);
    }

    @OnClick({R.id.watch_all_my_cart_textView, R.id.watch_all_my_cart_imageView})
    void toMyCartDetailActivity() {
        Intent intent = new Intent(getActivity(), MyCartDetailActivity.class);
        intent.putExtra(KEY_BUY_INFO_MODELS, buyInfoModels);
        startActivity(intent);
    }

    @OnClick({R.id.watch_all_my_farms_imageView, R.id.watch_all_my_farms_textView})
    void toMyFarmsDetailActivity() {
        Intent intent = new Intent(getActivity(), MyFarmsListActivity.class);
        intent.putExtra(KEY_FARM_INFO_MODELS, farmInfoModels);
        startActivity(intent);
    }

    @OnClick(R.id.logout_button)
    void logout() {
        if (getActivity() instanceof NormalUserActivity) {
            //退出登录
            ((NormalUserActivity) getActivity()).logout();
        } else {
            ToastUtils.showToast("非法操作");
        }
    }

    @OnClick(R.id.go_shopping_button)
    void goShopping() {
        if (getActivity() instanceof NormalUserActivity) {
            //跳转到农产品购买界面
            ((NormalUserActivity) getActivity()).toShoppingFragment();
        } else {
            ToastUtils.showToast("非法操作");
        }
    }
}
