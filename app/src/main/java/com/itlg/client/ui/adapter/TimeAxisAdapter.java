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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TimeAxisAdapter extends RecyclerView.Adapter<TimeAxisAdapter.TimeAxisViewHolder> {

    private Context context;
    private ArrayList<OperationLogModel> operationLogModels;

    public TimeAxisAdapter(Context context, ArrayList<OperationLogModel> operationLogModels) {
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
        OperationLogModel operationLogModel = new OperationLogModel();
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
