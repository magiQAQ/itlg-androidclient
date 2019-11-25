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

public class ProductMallAdapter extends RecyclerView.Adapter<ProductMallAdapter.ProductMallViewHolder> {

    private Context context;
    private List<ProductInfo> productInfos;
    private OnItemClickListener onItemClickListener;

    public ProductMallAdapter(Context context, List<ProductInfo> productInfos) {
        this.context = context;
        this.productInfos = productInfos;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductMallViewHolder holder, int position) {
        ProductInfo productInfo = productInfos.get(position);
        Glide.with(context).load(Config.FILEURL + productInfo.getImg())
                .placeholder(R.drawable.placeholder).into(holder.productImgImageView);
        holder.productNameTextView.setText(productInfo.getProductName());
        holder.productPriceTextView.setText(String.valueOf(productInfo.getPrice()));
        holder.productUnitTextView.setText(productInfo.getUnit());
        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(position);
            }
        });
    }

    @NonNull
    @Override
    public ProductMallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductMallViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product_mall, parent, false));
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    @Override
    public int getItemCount() {
        return productInfos == null ? 0 : productInfos.size();
    }

    class ProductMallViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.product_img_imageView)
        ImageView productImgImageView;
        @BindView(R.id.product_name_textView)
        TextView productNameTextView;
        @BindView(R.id.product_price_textView)
        TextView productPriceTextView;
        @BindView(R.id.product_unit_textView)
        TextView productUnitTextView;

        ProductMallViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
