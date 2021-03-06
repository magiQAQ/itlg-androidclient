package com.itlg.client.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.itlg.client.R;
import com.itlg.client.bean.ProductInfo;
import com.itlg.client.bean.ProductTypes;
import com.itlg.client.biz.ProductInfoBiz;
import com.itlg.client.net.CommonCallback;
import com.itlg.client.ui.activity.ProductDetailActivity;
import com.itlg.client.ui.adapter.ProductMallAdapter;
import com.itlg.client.ui.view.SwipeRefreshLayout;
import com.itlg.client.utils.MyUtils;
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
public class ProductMallFragment extends BaseFragment {

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
    private List<String> types;
    private ArrayAdapter<String> arrayAdapter;

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
            productTypes = getArguments().getParcelableArrayList(KEY_PRODUCT_TYPES);
            productInfos = getArguments().getParcelableArrayList(KEY_PRODUCT_INFOS);
        } else {
            sch_page = 1;
            sch_type = 0;
            sch_order = "";
            sch_keyword = "";
            productTypes = null;
            productInfos = null;
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
        types = new ArrayList<>();
        //商品类型无论是否加载成功都需要先显示一个全部分类
        types.add("全部分类");
        arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_spinner_item, types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(arrayAdapter);
        if (productTypes != null) {
            setupTypeSpinner();
        }
        if (productInfos != null) {
            setupRecyclerView();
        }

        //加载价格排序下拉框
        setupPriceSpinner();

        swipeRefreshLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLACK, Color.GREEN, Color.YELLOW);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadProductInfos();
            ToastUtils.showToast("刷新成功");
        });
        swipeRefreshLayout.setOnPullUpRefreshListener(this::loadMoreProductInfo);
    }

    @Override
    public void onLazyLoad() {
        //加载商品类型下拉框
        loadTypeSpinner();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        productInfoBiz.onDestroy();
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
                //加载完类型列表再加载商品列表,怕服务器炸裂
                loadProductInfos();
            }
        });
    }

    private void setupTypeSpinner() {
        //提取类型名字
        for (ProductTypes type : productTypes) {
            types.add(type.getName());
        }
        //通知适配器更新界面
        arrayAdapter.notifyDataSetChanged();
        //如果用户在页面销毁前选择过，就重新帮用户选择上
        if (sch_type > 0) {
            typeSpinner.setSelection(sch_type + 1);
        }
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                int before = sch_type;
                if (position > 0) {
                    sch_type = productTypes.get(position - 1).getId();
                } else {
                    sch_type = 0;
                }
                if (before != sch_type) {
                    loadProductInfos();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                adapterView.setSelection(0);
            }
        });

    }

    private void setupPriceSpinner() {
        List<String> itemList = new ArrayList<>();
        itemList.add("默认排列");
        itemList.add("升序排列");
        itemList.add("降序排列");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_spinner_item, itemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceSpinner.setAdapter(adapter);
        //如果用户在页面销毁前选择过，就重新帮用户选择上
        if (sch_order.equals("asc")) {
            priceSpinner.setSelection(1);
        } else if (sch_order.equals("desc")) {
            priceSpinner.setSelection(2);
        }
        priceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String before = sch_order;
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
                if (!before.equals(sch_order)) {
                    loadProductInfos();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                adapterView.setSelection(0);
            }
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
                        //如果刷新图标还在显示，就关闭它
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        //非空判断，有两种原因
                        //1.用户网络原因，productInfos将不会被初始化
                        //2.当后台确实没有此类商品时，也需要清空原列表
                        if (productInfos == null) {
                            productInfos = new ArrayList<>();
                        } else {
                            productInfos.clear();
                        }
                        Bundle bundle = getArguments() == null ? new Bundle() : getArguments();
                        bundle.putInt(KEY_SCH_PAGE, sch_page);
                        bundle.putInt(KEY_SCH_TYPE, sch_type);
                        bundle.putString(KEY_SCH_KEYWORD, sch_keyword);
                        bundle.putString(KEY_SCH_ORDER, sch_order);
                        bundle.remove(KEY_PRODUCT_INFOS);
                        setArguments(bundle);
                        setupRecyclerView();
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
                        bundle.putInt(KEY_SCH_PAGE, sch_page);
                        bundle.putInt(KEY_SCH_TYPE, sch_type);
                        bundle.putString(KEY_SCH_KEYWORD, sch_keyword);
                        bundle.putString(KEY_SCH_ORDER, sch_order);
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
                        bundle.putInt(KEY_SCH_PAGE, sch_page);
                        setArguments(bundle);
                        setupRecyclerView();
                        swipeRefreshLayout.setPullUpRefreshing(false);
                    }
                });
    }

    private void setupRecyclerView() {
        if (adapter == null) {
            adapter = new ProductMallAdapter(getActivity(), productInfos);
            productRecyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(this::toProductDetailActivity);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void toProductDetailActivity(int position) {
        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
        intent.putExtra(MyUtils.KEY_PRODUCT_ID, productInfos.get(position).getId());
        startActivity(intent);
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
