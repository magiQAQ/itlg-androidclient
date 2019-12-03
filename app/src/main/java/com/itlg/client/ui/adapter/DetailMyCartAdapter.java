package com.itlg.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.itlg.client.R;
import com.itlg.client.bean.BuyInfoModel;
import com.itlg.client.config.Config;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailMyCartAdapter extends RecyclerView.Adapter<DetailMyCartAdapter.DetailMyCartHolder> {

    private Context context;
    private ArrayList<BuyInfoModel> buyInfoModels;
    private OnRemoveButtonClickListener listener;

    public DetailMyCartAdapter(Context context, ArrayList<BuyInfoModel> buyInfoModels) {
        this.context = context;
        this.buyInfoModels = buyInfoModels;
    }

    public void setOnRemoveButtonClickListener(OnRemoveButtonClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DetailMyCartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DetailMyCartHolder(LayoutInflater.from(context).inflate(R.layout.item_detail_my_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DetailMyCartHolder holder, int position) {
        BuyInfoModel model = buyInfoModels.get(position);
        if (model == null) {
            return;
        }
        Glide.with(context).load(Config.FILEURL + model.getImg()).into(holder.productImgImageView);
        holder.productNameTextView.setText(model.getProductname());
        holder.productPriceTextView.setText(context.getString(R.string.price, model.getPrice()));
        holder.productCountTextView.setText(context.getString(R.string.count, model.getCount()));
        holder.removeButton.setOnClickListener(v -> {
            if (listener != null) listener.onclick(model);
        });
    }

    @Override
    public int getItemCount() {
        return buyInfoModels == null ? 0 : buyInfoModels.size();
    }

    public interface OnRemoveButtonClickListener {
        void onclick(BuyInfoModel buyInfoModel);
    }

    class DetailMyCartHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.product_img_imageView)
        ImageView productImgImageView;
        @BindView(R.id.product_name_textView)
        TextView productNameTextView;
        @BindView(R.id.product_price_textView)
        TextView productPriceTextView;
        @BindView(R.id.product_count_textView)
        TextView productCountTextView;
        @BindView(R.id.remove_button)
        ImageButton removeButton;

        DetailMyCartHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
