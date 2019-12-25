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
import com.itlg.client.bean.FarmInfoModel;
import com.itlg.client.config.Config;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FarmInfoAdapter extends RecyclerView.Adapter<FarmInfoAdapter.FarmInfoViewHolder> {

    private Context context;
    private List<FarmInfoModel> list;

    public FarmInfoAdapter(Context context, List<FarmInfoModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public FarmInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FarmInfoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_farm_info, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FarmInfoViewHolder holder, int position) {
        FarmInfoModel model = list.get(position);
        if (model == null) {
            return;
        }
        int typeId = model.getFarmInfo().getTypeId();
        StringBuilder builder = new StringBuilder();
        Glide.with(context).load(Config.FILEURL + model.getFarmInfo().getImg()).into(holder.farmIconImageView);
        if (typeId == 1) {
            builder.append("当前种植：");
        } else if (typeId == 2) {
            builder.append("当前养殖：");
        }
        holder.farmNameTextView.setText(String.format(context.getString(R.string.typename_id),
                model.getTypeName(), model.getFarmInfo().getId()));
        if (model.getProductInfos() != null && !model.getProductInfos().isEmpty()) {
            builder.append(model.getProductInfos().get(0).getProductName());
            if (model.getProductInfos().size() > 1) {
                builder.append("等");
            }
        } else {
            builder.append("无");
        }
        //当前种植:苹果等
        holder.farmCurrentProductTextView.setText(builder.toString());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class FarmInfoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.farm_icon_imageView)
        ImageView farmIconImageView;
        @BindView(R.id.farm_name_textView)
        TextView farmNameTextView;
        @BindView(R.id.farm_current_product_textView)
        TextView farmCurrentProductTextView;

        FarmInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
