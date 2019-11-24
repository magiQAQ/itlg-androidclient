package com.itlg.client.ui.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.itlg.client.R;
import com.itlg.client.bean.FrontFarmModel;
import com.itlg.client.bean.ProductTypes;
import com.itlg.client.biz.FarmInfoBiz;
import com.itlg.client.net.CommonCallback;
import com.itlg.client.ui.adapter.FrontFarmModelAdapter;
import com.itlg.client.ui.view.SwipeRefreshLayout;
import com.itlg.client.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FarmMallFragment extends Fragment {

    private static final String TAG = "FarmMallFragment";
    private static final String KEY_SCH_PAGE = "sch_page";
    private static final String KEY_SCH_TYPE = "sch_type";
    private static final String KEY_SCH_ORDER = "sch_order";
    private static final String KEY_SCH_STATE = "sch_state";
    private static final String KEY_FRONT_FARM_MODELS = "frontFarmModels";
    private static final String KEY_FARM_TYPES = "farmTypes";

    @BindView(R.id.type_spinner)
    Spinner typeSpinner;
    @BindView(R.id.status_spinner)
    Spinner statusSpinner;
    @BindView(R.id.price_spinner)
    Spinner priceSpinner;
    @BindView(R.id.frontFarmModels_recyclerView)
    RecyclerView frontFarmModelsRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private int sch_page;
    private int sch_type;
    private String sch_order;
    private int sch_state;
    private ArrayList<FrontFarmModel> frontFarmModels;
    private ArrayList<ProductTypes> farmTypes;

    private FarmInfoBiz farmInfoBiz;
    private FrontFarmModelAdapter adapter;
    private List<String> types;
    private ArrayAdapter<String> arrayAdapter;

    public static FarmMallFragment newInstance() {
        return new FarmMallFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sch_page = getArguments().getInt(KEY_SCH_PAGE, 1);
            sch_type = getArguments().getInt(KEY_SCH_TYPE, 0);
            sch_order = getArguments().getString(KEY_SCH_ORDER);
            sch_state = getArguments().getInt(KEY_SCH_STATE, 0);
            farmTypes = getArguments().getParcelableArrayList(KEY_FARM_TYPES);
            frontFarmModels = getArguments().getParcelableArrayList(KEY_FRONT_FARM_MODELS);
        } else {
            sch_page = 1;
            sch_type = 0;
            sch_order = "";
            sch_state = 0;
            farmTypes = null;
            frontFarmModels = null;
        }
        farmInfoBiz = new FarmInfoBiz();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farm_mall, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //加载农田类型下拉框
        types = new ArrayList<>();
        types.add("全部分类");
        arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_spinner_item, types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(arrayAdapter);
        if (farmTypes != null) {
            setupTypeSpinner();
        } else {
            loadTypeSpinner();
        }
        //加载农田状态选项下拉框
        setupStateSpinner();
        //加载价格排序下拉框
        setupPriceSpinner();

        //加载待售农田列表
        if (frontFarmModels != null) {
            setupRecyclerView();
        } else {
            loadFrontFarmModels();
        }
        swipeRefreshLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLACK, Color.GREEN, Color.YELLOW);
        swipeRefreshLayout.setOnRefreshListener(this::loadFrontFarmModels);
        swipeRefreshLayout.setOnPullUpRefreshListener(this::loadMoreFrontFarmModels);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        farmInfoBiz.onDestroy();
    }

    private void loadTypeSpinner() {
        farmInfoBiz.getFarmTypes(new CommonCallback<ArrayList<ProductTypes>>() {
            @Override
            public void onFail(Exception e) {
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onSuccess(ArrayList<ProductTypes> response) {
                farmTypes = response;
                Bundle bundle = getArguments() == null ? new Bundle() : getArguments();
                bundle.putParcelableArrayList(KEY_FARM_TYPES, farmTypes);
                setArguments(bundle);
                setupTypeSpinner();
            }
        });
    }

    private void setupTypeSpinner() {
        for (ProductTypes productTypes : farmTypes) {
            types.add(productTypes.getName());
        }
        arrayAdapter.notifyDataSetChanged();
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //sch_type为要搜索的类型的id，0表示全部
                sch_type = position > 0 ? farmTypes.get(position - 1).getId() : 0;
                loadFrontFarmModels();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                adapterView.setSelection(0);
            }
        });
        //如果用户在页面销毁前选择过，就重新帮用户选择上
        if (sch_type > 0) {
            typeSpinner.setSelection(sch_type + 1);
        }
    }

    private void setupStateSpinner() {
        List<String> states = new ArrayList<>();
        states.add("全部");
        states.add("已售");
        states.add("未售");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_spinner_item, states);
        //下拉显示样式
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(arrayAdapter);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                sch_state = position;
                loadFrontFarmModels();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                adapterView.setSelection(0);
            }
        });
        //如果用户在页面销毁前选择过，就重新帮用户选择上
        if (sch_state > 0) {
            statusSpinner.setSelection(sch_state);
        }
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
        priceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
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
                loadFrontFarmModels();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                adapterView.setSelection(0);
            }
        });
        //如果用户在页面销毁前选择过，就重新帮用户选择上
        if (sch_order.equals("asc")) {
            priceSpinner.setSelection(1);
        } else if (sch_order.equals("desc")) {
            priceSpinner.setSelection(2);
        }
    }

    private void loadFrontFarmModels() {
        sch_page = 1;
        farmInfoBiz.getFrontFarmModels(sch_page, sch_type, sch_state, sch_order,
                new CommonCallback<ArrayList<FrontFarmModel>>() {
                    @Override
                    public void onFail(Exception e) {
                        ToastUtils.showToast(e.getMessage());
                        Bundle bundle = getArguments() == null ? new Bundle() : getArguments();
                        bundle.putInt(KEY_SCH_PAGE, 1);
                        setArguments(bundle);
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        if (frontFarmModels != null) {
                            frontFarmModels.clear();
                            setupRecyclerView();
                        }
                    }

                    @Override
                    public void onSuccess(ArrayList<FrontFarmModel> response) {
                        if (frontFarmModels == null) {
                            frontFarmModels = response;
                        } else {
                            frontFarmModels.clear();
                            frontFarmModels.addAll(response);
                        }
                        Bundle bundle = getArguments() == null ? new Bundle() : getArguments();
                        bundle.putParcelableArrayList(KEY_FRONT_FARM_MODELS, frontFarmModels);
                        bundle.putString(KEY_SCH_ORDER, sch_order);
                        bundle.putInt(KEY_SCH_PAGE, sch_page);
                        bundle.putInt(KEY_SCH_STATE, sch_state);
                        setArguments(bundle);
                        setupRecyclerView();
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    private void loadMoreFrontFarmModels() {
        farmInfoBiz.getFrontFarmModels(++sch_page, sch_type, sch_state, sch_order,
                new CommonCallback<ArrayList<FrontFarmModel>>() {
                    @Override
                    public void onFail(Exception e) {
                        ToastUtils.showToast(e.getMessage());
                        swipeRefreshLayout.setPullUpRefreshing(false);
                        sch_page--;
                    }

                    @Override
                    public void onSuccess(ArrayList<FrontFarmModel> response) {
                        if (frontFarmModels == null) {
                            sch_page--;
                            return;
                        }
                        frontFarmModels.addAll(response);
                        Bundle bundle = getArguments() == null ? new Bundle() : getArguments();
                        bundle.putInt(KEY_SCH_PAGE, sch_page);
                        bundle.putParcelableArrayList(KEY_FRONT_FARM_MODELS, frontFarmModels);
                        setArguments(bundle);
                        setupRecyclerView();
                        swipeRefreshLayout.setPullUpRefreshing(false);
                    }
                });
    }

    private void setupRecyclerView() {
        if (adapter == null) {
            adapter = new FrontFarmModelAdapter(getActivity(), frontFarmModels);
            frontFarmModelsRecyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}
