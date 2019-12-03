package com.itlg.client.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.itlg.client.R;
import com.itlg.client.bean.NewsInfo;
import com.itlg.client.config.Config;
import com.itlg.client.utils.MyUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsInfoAdapter extends RecyclerView.Adapter<NewsInfoAdapter.NewsInfoViewHolder> {

    private Context context;
    private ArrayList<NewsInfo> newsInfos;

    public NewsInfoAdapter(Context context, ArrayList<NewsInfo> newsInfos) {
        this.context = context;
        this.newsInfos = newsInfos;
    }

    @NonNull
    @Override
    public NewsInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsInfoViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_news_info, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsInfoViewHolder holder, int position) {
        NewsInfo newsInfo = newsInfos.get(position);
        if (newsInfo == null) {
            return;
        }
        holder.newsTitleTextView.setText(newsInfo.getTitle());
        CharSequence charSequence = Html.fromHtml(newsInfo.getContent());
        holder.newsContentTextView.setText(charSequence);
        holder.newsTimeTextView.setText(MyUtils.longTypeTimeToString(newsInfo.getNewsTime()));
        if (position % 3 == 1) {
            Glide.with(context).load(Config.FILEURL + "/itlg/uploadfiles/default2.jpg")
                    .placeholder(R.drawable.placeholder).into(holder.newsImgImageView);
        } else if (position % 3 == 2) {
            Glide.with(context).load(Config.FILEURL + "/itlg/uploadfiles/hfsapple.jpg")
                    .placeholder(R.drawable.placeholder).into(holder.newsImgImageView);
        } else {
            Glide.with(context).load(Config.FILEURL + "/itlg/uploadfiles/yzc.jpg")
                    .placeholder(R.drawable.placeholder).into(holder.newsImgImageView);
        }
    }

    @Override
    public int getItemCount() {
        return newsInfos == null ? 0 : newsInfos.size();
    }

    class NewsInfoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.news_title_textView)
        TextView newsTitleTextView;
        @BindView(R.id.news_content_textView)
        TextView newsContentTextView;
        @BindView(R.id.news_img_imageView)
        ImageView newsImgImageView;
        @BindView(R.id.news_time_textView)
        TextView newsTimeTextView;

        NewsInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
