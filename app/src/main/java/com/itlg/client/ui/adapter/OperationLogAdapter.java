package com.itlg.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itlg.client.R;
import com.itlg.client.bean.OperationLog;
import com.itlg.client.utils.MyUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OperationLogAdapter extends RecyclerView.Adapter<OperationLogAdapter.OperationLogViewHolder> {

    private List<OperationLog> list;
    private Context context;

    public OperationLogAdapter(Context context, List<OperationLog> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public OperationLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OperationLogViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_operation_log, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OperationLogViewHolder holder, int position) {
        OperationLog operationLog = list.get(position);
        //显示样式为: 浇水
        holder.operationInfo.setText(operationLog.getOperationInfo());
        //人性化的时间显示
        holder.operationTime.setText(MyUtils.longTypeTimeToString(operationLog.getOperationTime()));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class OperationLogViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.operationInfo)
        TextView operationInfo;
        @BindView(R.id.operationTime)
        TextView operationTime;

        OperationLogViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
