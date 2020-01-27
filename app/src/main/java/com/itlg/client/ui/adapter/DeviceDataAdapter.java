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
        if (model == null) {
            return;
        }
        //显示温湿度
        holder.deviceTemperatureTextView.setText(model.getTemperature());
        holder.deviceHumidityTextView.setText(model.getHumidity());
        //按照 xx月xx日 xx:xx的样式显示
        holder.deviceDataTimeTextView.setText(MyUtils.getTimeMMddhhmm(model.getDataTime()));
    }

    @Override
    public int getItemCount() {
        return deviceDataModels == null ? 0 : deviceDataModels.size();
    }


    static
    class DeviceDataViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.device_temperature_textView)
        TextView deviceTemperatureTextView;
        @BindView(R.id.device_humidity_textView)
        TextView deviceHumidityTextView;
        @BindView(R.id.device_dataTime_textView)
        TextView deviceDataTimeTextView;

        DeviceDataViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
