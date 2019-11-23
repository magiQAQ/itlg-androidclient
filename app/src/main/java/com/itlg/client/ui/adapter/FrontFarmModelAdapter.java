package com.itlg.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itlg.client.R;
import com.itlg.client.bean.FrontFarmModel;

import java.util.List;

public class FrontFarmModelAdapter extends RecyclerView.Adapter<FrontFarmModelAdapter.FrontFarmModelViewHolder> {

    private Context context;
    private List<FrontFarmModel> frontFarmModels;

    public FrontFarmModelAdapter(Context context, List<FrontFarmModel> frontFarmModels) {
        this.context = context;
        this.frontFarmModels = frontFarmModels;
    }

    @NonNull
    @Override
    public FrontFarmModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FrontFarmModelViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_front_farm_model, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FrontFarmModelViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return frontFarmModels == null ? 0 : frontFarmModels.size();
    }

    class FrontFarmModelViewHolder extends RecyclerView.ViewHolder {
        FrontFarmModelViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
