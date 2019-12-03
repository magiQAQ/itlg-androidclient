package com.itlg.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itlg.client.R;
import com.itlg.client.bean.OperationLogModel;
import com.itlg.client.utils.MyUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TimeAxisAdapter extends RecyclerView.Adapter<TimeAxisAdapter.TimeAxisViewHolder> {

    private Context context;
    private List<OperationLogModel> operationLogModels;

    public TimeAxisAdapter(Context context, List<OperationLogModel> operationLogModels) {
        this.context = context;
        this.operationLogModels = operationLogModels;
    }

    @NonNull
    @Override
    public TimeAxisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TimeAxisViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_time_axis, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TimeAxisViewHolder holder, int position) {
        OperationLogModel model = operationLogModels.get(position);
        if (model == null) {
            return;
        }
        holder.axisYearMonthTextView.setText(MyUtils.getTimeyyyyMM(model
                .getOperationLog().getOperationTime()));
        holder.axisMonthDayTextView.setText(MyUtils.getTimeMMdd(model
                .getOperationLog().getOperationTime()));
        holder.axisOperatorTextView.setText(context.getString(R.string.operator_format, model.getOperatorName()));
        holder.axisOperationTextView.setText(context.getString(R.string.operation_format, model.getOperationLog().getOperationInfo()));
        holder.axisDeviceDataTextView.setText(context.getString(R.string.device_data_format, model.getDataInfo()));
    }

    @Override
    public int getItemCount() {
        return operationLogModels == null ? 0 : operationLogModels.size();
    }

    class TimeAxisViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.axis_year_month_textView)
        TextView axisYearMonthTextView;
        @BindView(R.id.axis_month_day_textView)
        TextView axisMonthDayTextView;
        @BindView(R.id.axis_operator_textView)
        TextView axisOperatorTextView;
        @BindView(R.id.axis_operation_textView)
        TextView axisOperationTextView;
        @BindView(R.id.axis_device_data_textView)
        TextView axisDeviceDataTextView;

        TimeAxisViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
