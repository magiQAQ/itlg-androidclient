package com.itlg.client.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.itlg.client.R;
import com.itlg.client.UserInfoHolder;
import com.itlg.client.bean.OperationLog;
import com.itlg.client.biz.OperationLogBiz;
import com.itlg.client.net.CommonCallback;
import com.itlg.client.ui.view.SwipeRefreshLayout;
import com.itlg.client.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 操作日志的碎片
 */
public class OperationLogFragment extends Fragment {

    private Unbinder unbinder;

    private static OperationLogBiz operationLogBiz;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private OperationLogFragment() {
        operationLogBiz = new OperationLogBiz();
    }

    public static OperationLogFragment getInstance() {
        OperationLogFragment fragment = new OperationLogFragment();
        int userId = UserInfoHolder.getInstance().getUser().getId();
        operationLogBiz.getOperationLogs(userId, 1, new CommonCallback<ArrayList<OperationLog>>() {
            @Override
            public void onFail(Exception e) {
                ToastUtils.showToast(e.getMessage());
            }

            @Override
            public void onSuccess(ArrayList<OperationLog> response) {
                Bundle args = new Bundle();
                args.putParcelableArrayList("operationLogs", response);
                fragment.setArguments(args);
            }
        });
        return fragment;
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_operation_log, container, false);
        unbinder = ButterKnife.bind(this, view);
        Bundle args = getArguments();
        if (args != null) {
            List<OperationLog> operationLogs = args.getParcelableArrayList("operationLogs");
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        operationLogBiz.onDestroy();
        super.onDestroy();
    }
}
