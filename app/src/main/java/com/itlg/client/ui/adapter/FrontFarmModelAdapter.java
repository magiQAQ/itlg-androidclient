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
import com.itlg.client.config.Config;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FrontFarmModelAdapter extends RecyclerView.Adapter<FrontFarmModelAdapter.FrontFarmModelViewHolder> {

    private Context context;
    private List<FrontFarmModel> frontFarmModels;
    private OnItemClickListener listener;

    public FrontFarmModelAdapter(Context context, List<FrontFarmModel> frontFarmModels) {
        this.context = context;
        this.frontFarmModels = frontFarmModels;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull FrontFarmModelViewHolder holder, int position) {
        FrontFarmModel model = frontFarmModels.get(position);
        if (model == null) {
            return;
        }
        Glide.with(context).load(Config.FILEURL + model.getFarmInfo().getImg()).into(holder.farmIconImageView);
        holder.farmNameTextView.setText(context.getString(R.string.typename_id,
                model.getTypeName(), model.getFarmInfo().getId()));
        holder.productPriceTextView.setText(context.getString(R.string.price,
                model.getSaleList().getPrice()));

        //如果已售出,显示已售出遮罩,未售出就设置点击事件
        if (model.getSaleList().getState() == 1) {
            holder.soldTextView.setVisibility(View.VISIBLE);
        } else {
            holder.soldTextView.setVisibility(View.INVISIBLE);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClick(position);
                }
            });
        }
    }

    @NonNull
    @Override
    public FrontFarmModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FrontFarmModelViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_front_farm_model, parent, false));
    }

    public interface OnItemClickListener {
        void onClick(int position);
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
        @BindView(R.id.sold_textView)
        TextView soldTextView;
        FrontFarmModelViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
