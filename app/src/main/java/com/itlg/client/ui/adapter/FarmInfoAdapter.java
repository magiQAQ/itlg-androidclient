package com.itlg.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itlg.client.R;
import com.itlg.client.bean.FarmInfoModel;

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
        if (model.getFarmInfo().getTypeId() == 1) {
            holder.farmIconImageView.setImageResource(R.mipmap.nongchang);
        } else if (model.getFarmInfo().getTypeId() == 2) {
            holder.farmIconImageView.setImageResource(R.mipmap.yangzhichang);
        }
        holder.farmNameTextView.setText(String.format(context.getString(R.string.typename_id),
                model.getTypeName(), model.getFarmInfo().getId()));
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

        FarmInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
