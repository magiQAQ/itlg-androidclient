package com.itlg.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.itlg.client.R;
import com.itlg.client.bean.FrontFarmModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        FrontFarmModel model = frontFarmModels.get(position);
        if (model.getFarmInfo().getTypeId() == 1) {
            Glide.with(context).load(R.drawable.nongchang).into(holder.farmIconImageView);
        } else if (model.getFarmInfo().getTypeId() == 2) {
            Glide.with(context).load(R.drawable.yangzhichang).into(holder.farmIconImageView);
        }
        holder.farmNameTextView.setText(context.getString(R.string.typename_id,
                model.getTypeName(), model.getFarmInfo().getId()));
        holder.productPriceTextView.setText(context.getString(R.string.price,
                model.getSaleList().getPrice()));
    }

    @Override
    public int getItemCount() {
        return frontFarmModels == null ? 0 : frontFarmModels.size();
    }

    class FrontFarmModelViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.farm_icon_imageView)
        ImageView farmIconImageView;
        @BindView(R.id.farm_name_textView)
        TextView farmNameTextView;
        @BindView(R.id.product_price_textView)
        TextView productPriceTextView;
        FrontFarmModelViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
