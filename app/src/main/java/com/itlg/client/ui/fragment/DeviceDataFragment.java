package com.itlg.client.ui.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itlg.client.R;
import com.itlg.client.bean.DeviceDataModel;
import com.itlg.client.biz.DeviceDataBiz;
import com.itlg.client.net.CommonCallback;
import com.itlg.client.ui.adapter.DeviceDataAdapter;
import com.itlg.client.ui.view.SwipeRefreshLayout;
import com.itlg.client.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 设备记录的碎片
 */
public class DeviceDataFragment extends Fragment {

    public static final String KEY_DEVICE_DATA_MODELS = "deviceDataModels";
    public static final String KEY_SCH_KEY = "sch_key";
    public static final String KEY_FARM_ID = "farmId";
    private static DeviceDataBiz deviceDataBiz = new DeviceDataBiz();
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private Unbinder unbinder;
    private List<DeviceDataModel> deviceDataModels;
    private int sch_page;
    private int farmId;
    private DeviceDataAdapter adapter;

    public static DeviceDataFragment newInstance(int farmId) {
        DeviceDataFragment fragment = new DeviceDataFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_FARM_ID, farmId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            deviceDataModels = savedInstanceState.getParcelableArrayList(KEY_DEVICE_DATA_MODELS);
            sch_page = savedInstanceState.getInt(KEY_SCH_KEY);
            farmId = savedInstanceState.getInt(KEY_FARM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_data, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        swipeRefreshLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLACK, Color.GREEN, Color.YELLOW);
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(this::loadData);
        //底部上拉载入更多
        swipeRefreshLayout.setOnPullUpRefreshListener(this::loadMore);
        if (deviceDataModels != null) {
            setupRecyclerView();
        } else {
            loadData();
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        deviceDataBiz.onDestroy();
        super.onDestroy();
    }

    private void setupRecyclerView() {
        if (adapter == null) {
            adapter = new DeviceDataAdapter(getActivity(), deviceDataModels);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void loadData() {
        deviceDataBiz.getDeviceData(farmId, 1, new CommonCallback<ArrayList<DeviceDataModel>>() {
            @Override
            public void onFail(Exception e) {
                ToastUtils.showToast(e.getMessage());
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (e.getMessage().contains("没有更多了")) {
                    deviceDataModels.clear();
                    setupRecyclerView();
                }
            }

            @Override
            public void onSuccess(ArrayList<DeviceDataModel> response) {
                Bundle args = getArguments() == null ? new Bundle() : getArguments();
                args.putParcelableArrayList(KEY_DEVICE_DATA_MODELS, response);
                args.putInt(KEY_SCH_KEY, 1);
                args.putInt(KEY_FARM_ID, farmId);

            }
        });
    }


    private void loadMore() {

    }
}
