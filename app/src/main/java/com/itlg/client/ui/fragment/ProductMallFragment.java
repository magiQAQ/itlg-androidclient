package com.itlg.client.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.itlg.client.R;
import com.itlg.client.bean.ProductInfo;
import com.itlg.client.bean.ProductTypes;
import com.itlg.client.biz.ProductInfoBiz;
import com.itlg.client.net.CommonCallback;
import com.itlg.client.ui.adapter.ProductMallAdapter;
import com.itlg.client.ui.view.SwipeRefreshLayout;
import com.itlg.client.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * 农产品商城
 */
public class ProductMallFragment extends Fragment {

    private static final String KEY_SCH_PAGE = "sch_page";
    private static final String KEY_SCH_KEYWORD = "sch_keyword";
    private static final String KEY_SCH_TYPE = "sch_type";
    private static final String KEY_SCH_ORDER = "sch_order";
    private static final String KEY_PRODUCT_TYPES = "productTypes";
    private static final String KEY_PRODUCT_INFOS = "productInfos";
    private static final String TAG = "ProductMallFragment";

    @BindView(R.id.search_editText)
    EditText searchEditText;
    @BindView(R.id.type_spinner)
    Spinner typeSpinner;
    @BindView(R.id.price_spinner)
    Spinner priceSpinner;
    @BindView(R.id.product_recyclerView)
    RecyclerView productRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private int sch_page;
    private String sch_keyword;
    private int sch_type;
    private String sch_order;
    private ArrayList<ProductTypes> productTypes;
    private ArrayList<ProductInfo> productInfos;

    private ProductInfoBiz productInfoBiz;
    private ProductMallAdapter adapter;

    public static ProductMallFragment newInstance() {
        return new ProductMallFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sch_page = getArguments().getInt(KEY_SCH_PAGE, 1);
            sch_keyword = getArguments().getString(KEY_SCH_KEYWORD, "");
            sch_type = getArguments().getInt(KEY_SCH_TYPE, 0);
            sch_order = getArguments().getString(KEY_SCH_ORDER, "");
        } else {
            sch_page = 1;
            sch_type = 0;
            sch_order = "";
            sch_keyword = "";
        }
        productInfoBiz = new ProductInfoBiz();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_mall, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //加载商品类型下拉框
        if (productTypes != null) {
            setupTypeSpinner();
        } else {
            loadTypeSpinner();
        }
        //加载价格排序下拉框
        loadPriceSpinner();
        //加载商品列表
        loadProductInfos();

        swipeRefreshLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLACK, Color.GREEN, Color.YELLOW);
        swipeRefreshLayout.setOnRefreshListener(this::loadProductInfos);
        swipeRefreshLayout.setOnPullUpRefreshListener(this::loadMoreProductInfo);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        productInfoBiz.onDestroy();
    }

    private void loadPriceSpinner() {
        List<String> itemList = new ArrayList<>();
        itemList.add("默认排列");
        itemList.add("升序排列");
        itemList.add("降序排列");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_spinner_item, itemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceSpinner.setAdapter(adapter);
        priceSpinner.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    sch_order = "";
                    break;
                case 1:
                    sch_order = "asc";
                    break;
                case 2:
                    sch_order = "desc";
            }
            loadProductInfos();
        });
    }

    private void loadTypeSpinner() {
        productInfoBiz.getProductTypes(new CommonCallback<ArrayList<ProductTypes>>() {
            @Override
            public void onFail(Exception e) {
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onSuccess(ArrayList<ProductTypes> response) {
                productTypes = response;
                Bundle bundle = getArguments() == null ? new Bundle() : getArguments();
                bundle.putParcelableArrayList(KEY_PRODUCT_TYPES, productTypes);
                setArguments(bundle);
                setupTypeSpinner();
            }
        });
    }

    private void setupTypeSpinner() {
        List<String> types = new ArrayList<>();
        for (ProductTypes type : productTypes) {
            types.add(type.getName());
        }
        types.add(0, "全部分类");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_spinner_item, types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(arrayAdapter);
        typeSpinner.setOnItemClickListener((parent, view, position, id) -> {
            sch_type = productTypes.get(position).getId();
            loadProductInfos();
        });
    }

    //第一次载入商品列表或者刷新商品列表时调用
    private void loadProductInfos() {
        sch_page = 1;
        productInfoBiz.getProductInfos(sch_page, sch_type, sch_keyword, sch_order,
                new CommonCallback<ArrayList<ProductInfo>>() {
                    @Override
                    public void onFail(Exception e) {
                        ToastUtils.showToast(e.getMessage());
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onSuccess(ArrayList<ProductInfo> response) {
                        if (productInfos == null) {
                            productInfos = response;
                        } else {
                            productInfos.clear();
                            productInfos.addAll(response);
                        }
                        Bundle bundle = getArguments() == null ? new Bundle() : getArguments();
                        bundle.putParcelableArrayList(KEY_PRODUCT_INFOS, productInfos);
                        setArguments(bundle);
                        setupRecyclerView();
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    private void loadMoreProductInfo() {
        productInfoBiz.getProductInfos(++sch_page, sch_type, sch_keyword, sch_order,
                new CommonCallback<ArrayList<ProductInfo>>() {
                    @Override
                    public void onFail(Exception e) {
                        ToastUtils.showToast(e.getMessage());
                        //关闭上拉更多载入图标
                        swipeRefreshLayout.setPullUpRefreshing(false);
                        sch_page--;
                    }

                    @Override
                    public void onSuccess(ArrayList<ProductInfo> response) {
                        if (productInfos == null) {
                            sch_page--;
                            return;
                        }
                        productInfos.addAll(response);
                        Bundle bundle = getArguments() == null ? new Bundle() : getArguments();
                        bundle.putParcelableArrayList(KEY_PRODUCT_INFOS, productInfos);
                        setArguments(bundle);
                        setupRecyclerView();
                        swipeRefreshLayout.setPullUpRefreshing(false);
                    }
                });
    }

    private void setupRecyclerView() {
        if (adapter == null) {
            adapter = new ProductMallAdapter(getActivity(), productInfos);
        } else {
            adapter.notifyDataSetChanged();
        }
        productRecyclerView.setAdapter(adapter);
    }

    @OnTextChanged(R.id.search_editText)
    void setSchKeyword() {
        sch_keyword = searchEditText.getEditableText().toString();
    }

    @OnClick(R.id.search_button)
    void searchProductInfos() {
        loadProductInfos();
    }

}
