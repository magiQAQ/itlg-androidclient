package com.itlg.client.ui.activity;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.itlg.client.R;
import com.itlg.client.bean.NewsInfo;
import com.itlg.client.utils.MyUtils;
import com.itlg.client.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsInfoDetailActivity extends BaseActivity {

    @BindView(R.id.news_title_textView)
    TextView newsTitleTextView;
    @BindView(R.id.news_time_textView)
    TextView newsTimeTextView;
    @BindView(R.id.news_content_textView)
    TextView newsContentTextView;
    private NewsInfo newsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_info_detail);
        ButterKnife.bind(this);

        newsInfo = getIntent().getParcelableExtra(MyUtils.KEY_NEWS_INFO);
        if (newsInfo == null) {
            ToastUtils.showToast("该新闻不存在");
            finish();
        }

        setStatusBarColor(R.color.colorTheme, false);

        initView();
    }

    private void initView() {
        setupCommonToolbar();
        newsTitleTextView.setText(newsInfo.getTitle());
        newsTimeTextView.setText(MyUtils.longTypeTimeToString(newsInfo.getNewsTime()));
        newsContentTextView.setText(Html.fromHtml(newsInfo.getContent()));
    }
}
