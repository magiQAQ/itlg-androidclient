package com.itlg.client.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itlg.client.bean.DeviceDataModel;

import java.util.List;

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
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceDataViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class DeviceDataViewHolder extends RecyclerView.ViewHolder{

        public DeviceDataViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
