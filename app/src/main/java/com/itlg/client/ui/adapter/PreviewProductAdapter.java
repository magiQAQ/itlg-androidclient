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
import com.itlg.client.bean.ProductInfo;
import com.itlg.client.config.Config;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreviewProductAdapter extends RecyclerView.Adapter<PreviewProductAdapter.PreviewProductViewHolder> {

    private Context context;
    private List<ProductInfo> productInfos;

    public PreviewProductAdapter(Context context, List<ProductInfo> productInfos) {
        this.context = context;
        this.productInfos = productInfos;
    }

    @NonNull
    @Override
    public PreviewProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PreviewProductViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_preview_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewProductViewHolder holder, int position) {
        ProductInfo productInfo = productInfos.get(position);
        if (productInfo == null) {
            return;
        }
        Glide.with(context).load(Config.FILEURL + productInfo.getImg())
                .placeholder(R.drawable.placeholder).into(holder.productImgImageView);
        holder.productNameTextView.setText(productInfo.getProductName());
    }

    @Override
    public int getItemCount() {
        return productInfos == null ? 0 : productInfos.size();
    }

    class PreviewProductViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.product_img_imageView)
        ImageView productImgImageView;
        @BindView(R.id.product_name_textView)
        TextView productNameTextView;

        PreviewProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
