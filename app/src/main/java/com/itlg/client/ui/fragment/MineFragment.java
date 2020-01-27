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
import com.itlg.client.ui.activity.MyDetailFarmsActivity;
import com.itlg.client.ui.activity.NormalUserActivity;
import com.itlg.client.ui.adapter.PreViewMyCartAdapter;
import com.itlg.client.utils.MyUtils;
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

    @BindView(R.id.userImg_imageView)
    ImageView userImgImageView;
    @BindView(R.id.user_name_textView)
    TextView userNameTextView;
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
            buyInfoModels = getArguments().getParcelableArrayList(MyUtils.KEY_BUY_INFO_MODELS);
            farmInfoModels = getArguments().getParcelableArrayList(MyUtils.KEY_FARM_INFO_MODELS);
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
        Glide.with(this).load(Config.FILEURL + UserInfoHolder.getInstance().getUserInfo().getUserImg())
                .placeholder(R.drawable.user_default_img).circleCrop().into(userImgImageView);
        //显示当前用户名字
        userNameTextView.setText(getString(R.string.welcome, UserInfoHolder.getInstance().getUserInfo().getName()));
    }

    //用户每次点击我的慧农时,需要重新加载购物车
    @Override
    public void onResume() {
        super.onResume();
        //加载购物车预览
        loadMyCart();
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
                bundle.putParcelableArrayList(MyUtils.KEY_FARM_INFO_MODELS, farmInfoModels);
                setArguments(bundle);
                setupFarmInfoView();
            }
        });
    }

    //显示农场信息
    private void setupFarmInfoView() {
        FarmInfoModel model = farmInfoModels.get(0);
        Glide.with(Objects.requireNonNull(getActivity())).load(Config.FILEURL + model.getFarmInfo().getImg())
                .placeholder(R.drawable.placeholder).into(farmIconImageView);
        //农场名字
        farmNameTextView.setText(String.format(getString(R.string.typename_id), model.getTypeName(), model.getFarmInfo().getId()));
        //组装当前农场种植的农产品的字符串
        StringBuilder builder = new StringBuilder();
        if (model.getTypeName().equals("农场")) {
            builder.append("当前种植：");
        } else if (model.getTypeName().equals("养殖场")) {
            builder.append("当前养殖：");
        }
        if (model.getProductInfos() != null && !model.getProductInfos().isEmpty()) {
            //显示第一个农作物的名字
            builder.append(model.getProductInfos().get(0).getProductName());
            //如果有多个,加一个"等"字
            if (model.getProductInfos().size() > 1) {
                builder.append("等");
            }
        } else {
            //没有就显示一个"无"字
            builder.append("无");
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

                //之前请求完成后,再加载农田
                if (UserInfoHolder.getInstance().getUserInfo().getPrivilege() == 70) {
                    //农田信息
                    loadFirstFarmInfo();
                }
            }

            @Override
            public void onSuccess(ArrayList<BuyInfoModel> response) {
                if (buyInfoModels == null) {
                    buyInfoModels = response;
                } else {
                    buyInfoModels.clear();
                    buyInfoModels.addAll(response);
                }
                Bundle bundle = getArguments() != null ? getArguments() : new Bundle();
                bundle.putParcelableArrayList(MyUtils.KEY_BUY_INFO_MODELS, buyInfoModels);
                setArguments(bundle);
                setupMyCartRecyclerView();

                //之前请求完成后,再加载农田
                if (UserInfoHolder.getInstance().getUserInfo().getPrivilege() == 70) {
                    //农田信息
                    loadFirstFarmInfo();
                }
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

    @OnClick({R.id.watch_all_my_cart_textView, R.id.watch_all_my_cart_imageView, R.id.my_cart_recyclerView})
    void toMyCartDetailActivity() {
        Intent intent = new Intent(getActivity(), MyCartDetailActivity.class);
        intent.putExtra(MyUtils.KEY_BUY_INFO_MODELS, buyInfoModels);
        startActivity(intent);
    }

    @OnClick({R.id.watch_all_my_farms_imageView, R.id.watch_all_my_farms_textView, R.id.my_farms_cardView})
    void toMyFarmsDetailActivity() {
        Intent intent = new Intent(getActivity(), MyDetailFarmsActivity.class);
        intent.putExtra(MyUtils.KEY_FARM_INFO_MODELS, farmInfoModels);
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
