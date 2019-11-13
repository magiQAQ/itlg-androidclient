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

    private static final String KEY_OPERATION_LOGS = "operationLogs";
    private static final String KEY_SCH_PAGE = "sch_page";
    private static final String KEY_FARM_ID = "farmId";
    private Unbinder unbinder;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<OperationLog> operationLogs;
    private int sch_page;
    private int farmId;
    private OperationLogAdapter adapter;
    private OperationLogBiz operationLogBiz;

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
        if (getArguments() != null) {
            operationLogs = getArguments().getParcelableArrayList(KEY_OPERATION_LOGS);
            sch_page = getArguments().getInt(KEY_SCH_PAGE);
            farmId = getArguments().getInt(KEY_FARM_ID);
        }
        operationLogBiz = new OperationLogBiz();
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
        if (operationLogBiz != null) {
            operationLogBiz.onDestroy();
        }
        super.onDestroyView();
    }

    /**
     * 给Activity调用的刷新列表的方法
     */
    public void refreshRecyclerList() {
        loadData();
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
                sch_page = 1;
                Bundle args = getArguments() == null ? new Bundle() : getArguments();
                args.putParcelableArrayList(KEY_OPERATION_LOGS, response);
                args.putInt(KEY_SCH_PAGE, sch_page);
                args.putInt(KEY_FARM_ID, farmId);
                setArguments(args);
                if (operationLogs == null) operationLogs = new ArrayList<>();
                operationLogs.clear();
                operationLogs.addAll(response);
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
        operationLogBiz.getOperationLogs(farmId, sch_page + 1, new CommonCallback<ArrayList<OperationLog>>() {
            @Override
            public void onFail(Exception e) {
                ToastUtils.showToast(e.getMessage());
                swipeRefreshLayout.setPullUpRefreshing(false);
            }

            @Override
            public void onSuccess(ArrayList<OperationLog> response) {
                sch_page += 1;
                operationLogs.addAll(response);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setPullUpRefreshing(false);
                Bundle args = getArguments() == null ? new Bundle() : getArguments();
                args.putInt(KEY_SCH_PAGE, sch_page);
                setArguments(args);
            }
        });
    }


}
