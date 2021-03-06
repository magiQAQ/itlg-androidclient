package com.itlg.client.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
public class DeviceDataFragment extends BaseFragment {

    private static final String KEY_DEVICE_DATA_MODELS = "deviceDataModels";
    private static final String KEY_SCH_PAGE = "sch_page";
    private static final String KEY_FARM_ID = "farmId";
    private Unbinder unbinder;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private List<DeviceDataModel> deviceDataModels;
    private int sch_page;
    private int farmId;
    private DeviceDataAdapter adapter;
    private DeviceDataBiz deviceDataBiz;

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
        if (getArguments() != null) {
            deviceDataModels = getArguments().getParcelableArrayList(KEY_DEVICE_DATA_MODELS);
            sch_page = getArguments().getInt(KEY_SCH_PAGE);
            farmId = getArguments().getInt(KEY_FARM_ID);
        }
        deviceDataBiz = new DeviceDataBiz();
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
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onLazyLoad() {
        if (deviceDataModels != null) {
            setupRecyclerView();
        } else {
            loadData();
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        if (deviceDataBiz != null) {
            deviceDataBiz.onDestroy();
        }
        super.onDestroyView();
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

    //第一次载入数据或者刷新RecyclerView时调用
    private void loadData() {
        deviceDataBiz.getDeviceData(farmId, 1, new CommonCallback<ArrayList<DeviceDataModel>>() {
            @Override
            public void onFail(Exception e) {
                ToastUtils.showToast(e.getMessage());
                //如果载入动画在显示的话就关闭载入动画
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (e.getMessage().contains("没有更多了") && deviceDataModels != null) {
                    deviceDataModels.clear();
                    setupRecyclerView();
                }
            }

            @Override
            public void onSuccess(ArrayList<DeviceDataModel> response) {
                sch_page = 1;
                Bundle args = getArguments() == null ? new Bundle() : getArguments();
                args.putParcelableArrayList(KEY_DEVICE_DATA_MODELS, response);
                args.putInt(KEY_SCH_PAGE, sch_page);
                args.putInt(KEY_FARM_ID, farmId);
                setArguments(args);
                if (deviceDataModels == null) deviceDataModels = new ArrayList<>();
                deviceDataModels.clear();
                deviceDataModels.addAll(response);
                setupRecyclerView();

                //如果载入动画在显示的话就关闭载入动画
                if (swipeRefreshLayout.isRefreshing()) {
                    ToastUtils.showToast("刷新成功");
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    //用户底部上拉想看更多内容时调用
    private void loadMore() {
        deviceDataBiz.getDeviceData(farmId, sch_page + 1, new CommonCallback<ArrayList<DeviceDataModel>>() {
            @Override
            public void onFail(Exception e) {
                ToastUtils.showToast(e.getMessage());
                swipeRefreshLayout.setPullUpRefreshing(false);
            }

            @Override
            public void onSuccess(ArrayList<DeviceDataModel> response) {
                sch_page += 1;
                deviceDataModels.addAll(response);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setPullUpRefreshing(false);
                Bundle args = getArguments() == null ? new Bundle() : getArguments();
                args.putInt(KEY_SCH_PAGE, sch_page);
                setArguments(args);
            }
        });
    }
}
