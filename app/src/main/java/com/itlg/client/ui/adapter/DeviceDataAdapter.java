package com.itlg.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itlg.client.R;
import com.itlg.client.bean.DeviceDataModel;
import com.itlg.client.utils.MyUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceDataAdapter extends RecyclerView.Adapter<DeviceDataAdapter.DeviceDataViewHolder> {

    private Context context;
    private List<DeviceDataModel> deviceDataModels;

    public DeviceDataAdapter(Context context, List<DeviceDataModel> deviceDataModels) {
        this.context = context;
        this.deviceDataModels = deviceDataModels;
    }

    @NonNull
    @Override
    public DeviceDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceDataViewHolder(LayoutInflater.from(context).inflate(R.layout.item_device_data, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceDataViewHolder holder, int position) {
        DeviceDataModel model = deviceDataModels.get(position);
        //样式为: 液晶显示器GF40
        holder.deviceNameTextView.setText(String.format("%s%s",
                model.getDeviceInfo().getDeviceName(), model.getDeviceInfo().getDeviceCode()));
        holder.deviceDataInfoTextView.setText(model.getDeviceData().getDataInfo());
        //人性化的时间显示
        holder.dataTimeTextView.setText(MyUtils.longTypeTimeToString(model.getDeviceData().getDataTime()));
    }

    @Override
    public int getItemCount() {
        return deviceDataModels == null ? 0 : deviceDataModels.size();
    }

    class DeviceDataViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.device_name_textView)
        TextView deviceNameTextView;
        @BindView(R.id.device_data_info_textView)
        TextView deviceDataInfoTextView;
        @BindView(R.id.data_time_textView)
        TextView dataTimeTextView;

        DeviceDataViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
