package com.itlg.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.itlg.client.R;
import com.itlg.client.bean.BuyInfoModel;
import com.itlg.client.config.Config;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreViewMyCartAdapter extends RecyclerView.Adapter<PreViewMyCartAdapter.PreViewMyCartViewHolder> {

    private Context context;
    private List<BuyInfoModel> buyInfoModels;

    public PreViewMyCartAdapter(Context context, List<BuyInfoModel> buyInfoModels) {
        this.context = context;
        this.buyInfoModels = buyInfoModels;
    }

    @NonNull
    @Override
    public PreViewMyCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PreViewMyCartViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_preview_my_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PreViewMyCartViewHolder holder, int position) {
        Glide.with(context).load(Config.FILEURL + buyInfoModels.get(position).getImg())
                .placeholder(R.drawable.placeholder)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                .into(holder.cartPreviewImageView);
    }

    @Override
    public int getItemCount() {
        //购物车预览最多显示5个
        return buyInfoModels.size() > 5 ? 5 : buyInfoModels.size();
    }

    class PreViewMyCartViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cart_preview_imageView)
        ImageView cartPreviewImageView;

        PreViewMyCartViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
