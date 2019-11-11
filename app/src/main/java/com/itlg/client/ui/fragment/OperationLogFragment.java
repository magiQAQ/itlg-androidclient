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
import com.itlg.client.bean.OperationLog;
import com.itlg.client.biz.OperationLogBiz;
import com.itlg.client.net.CommonCallback;
import com.itlg.client.ui.adapter.OperationLogAdapter;
import com.itlg.client.ui.view.SwipeRefreshLayout;
import com.itlg.client.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 操作日志的碎片
 */
public class OperationLogFragment extends Fragment {

    public static final String KEY_OPERATION_LOGS = "operationLogs";
    public static final String KEY_SCH_KEY = "sch_key";
    public static final String KEY_FARM_ID = "farmId";
    private Unbinder unbinder;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<OperationLog> operationLogs;
    private int sch_page;
    private static OperationLogBiz operationLogBiz = new OperationLogBiz();
    private OperationLogAdapter adapter;
    private int farmId;

    public static OperationLogFragment newInstance(int farmId) {
        OperationLogFragment fragment = new OperationLogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_FARM_ID, farmId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            operationLogs = savedInstanceState.getParcelableArrayList(KEY_OPERATION_LOGS);
            sch_page = savedInstanceState.getInt(KEY_SCH_KEY);
            farmId = savedInstanceState.getInt(KEY_FARM_ID);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_operation_log, container, false);
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
        if (operationLogs != null) {
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
        operationLogBiz.onDestroy();
        super.onDestroy();
    }

    //第一次载入数据或者刷新RecyclerView时调用
    private void loadData() {
        operationLogBiz.getOperationLogs(farmId, 1, new CommonCallback<ArrayList<OperationLog>>() {
            @Override
            public void onFail(Exception e) {
                ToastUtils.showToast(e.getMessage());
                //如果载入动画在显示的话就关闭载入动画
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (e.getMessage().contains("没有更多了") && operationLogs != null) {
                    operationLogs.clear();
                    setupRecyclerView();
                }
            }

            @Override
            public void onSuccess(ArrayList<OperationLog> response) {
                Bundle args = getArguments() == null ? new Bundle() : getArguments();
                args.putParcelableArrayList(KEY_OPERATION_LOGS, response);
                args.putInt(KEY_SCH_KEY, 1);
                args.putInt(KEY_FARM_ID, farmId);
                setArguments(args);
                if (operationLogs == null) operationLogs = new ArrayList<>();
                operationLogs.clear();
                operationLogs.addAll(response);
                sch_page = 1;
                setupRecyclerView();

                //如果载入动画在显示的话就关闭载入动画
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    ToastUtils.showToast("刷新成功");
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    //用户底部上拉想看更多内容时调用
    private void loadMore() {
        sch_page += 1;
        operationLogBiz.getOperationLogs(farmId, sch_page, new CommonCallback<ArrayList<OperationLog>>() {
            @Override
            public void onFail(Exception e) {
                ToastUtils.showToast(e.getMessage());
                swipeRefreshLayout.setPullUpRefreshing(false);
                if (e.getMessage().contains("没有更多")) {
                    sch_page -= 1;
                }
            }

            @Override
            public void onSuccess(ArrayList<OperationLog> response) {
                operationLogs.addAll(response);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setPullUpRefreshing(false);
            }
        });
    }

    private void setupRecyclerView() {
        if (adapter == null) {
            adapter = new OperationLogAdapter(getActivity(), operationLogs);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            adapter.notifyDataSetChanged();
        }
    }

}
