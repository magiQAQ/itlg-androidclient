package com.itlg.client.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.itlg.client.R;
import com.itlg.client.bean.NewsInfo;
import com.itlg.client.biz.NewsInfoBiz;
import com.itlg.client.net.CommonCallback;
import com.itlg.client.ui.adapter.NewsInfoAdapter;
import com.itlg.client.ui.view.SwipeRefreshLayout;
import com.itlg.client.utils.ToastUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 新闻资讯碎片
 */
public class NewsFragment extends Fragment {

    private static final String KEY_SCH_PAGE = "sch_page";
    private static final String KEY_NEWS_INFOS = "newsInfos";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private int sch_page;
    private ArrayList<NewsInfo> newsInfos;
    private NewsInfoAdapter adapter;

    private NewsInfoBiz newsInfoBiz;

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sch_page = getArguments().getInt(KEY_SCH_PAGE, 1);
            newsInfos = getArguments().getParcelableArrayList(KEY_NEWS_INFOS);
        } else {
            sch_page = 1;
            newsInfos = null;
        }
        newsInfoBiz = new NewsInfoBiz();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (newsInfos != null) {
            setupRecyclerView();
        } else {
            loadNewsInfos();
        }

        swipeRefreshLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLACK, Color.GREEN, Color.YELLOW);
        swipeRefreshLayout.setOnRefreshListener(this::loadNewsInfos);
        swipeRefreshLayout.setOnPullUpRefreshListener(this::loadMoreNewsInfo);
    }

    private void loadNewsInfos() {
        sch_page = 1;
        newsInfoBiz.getNewsInfos(sch_page, new CommonCallback<ArrayList<NewsInfo>>() {
            @Override
            public void onFail(Exception e) {
                ToastUtils.showToast(e.getMessage());
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (newsInfos != null) {
                    newsInfos.clear();
                }
                Bundle bundle = getArguments() == null ? new Bundle() : getArguments();
                bundle.putInt(KEY_SCH_PAGE, sch_page);
                bundle.remove(KEY_NEWS_INFOS);
                setArguments(bundle);
                setupRecyclerView();
            }

            @Override
            public void onSuccess(ArrayList<NewsInfo> response) {
                if (newsInfos != null) {
                    newsInfos.clear();
                    newsInfos.addAll(response);
                } else {
                    newsInfos = response;
                }
                Bundle bundle = getArguments() == null ? new Bundle() : getArguments();
                bundle.putInt(KEY_SCH_PAGE, sch_page);
                bundle.putParcelableArrayList(KEY_NEWS_INFOS, newsInfos);
                setArguments(bundle);
                setupRecyclerView();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void loadMoreNewsInfo() {
        newsInfoBiz.getNewsInfos(++sch_page, new CommonCallback<ArrayList<NewsInfo>>() {
            @Override
            public void onFail(Exception e) {
                ToastUtils.showToast(e.getMessage());
                swipeRefreshLayout.setPullUpRefreshing(false);
                sch_page--;
            }

            @Override
            public void onSuccess(ArrayList<NewsInfo> response) {
                if (newsInfos == null) {
                    return;
                }
                newsInfos.addAll(response);
                Bundle bundle = getArguments() == null ? new Bundle() : getArguments();
                bundle.putInt(KEY_SCH_PAGE, sch_page);
                bundle.putParcelableArrayList(KEY_NEWS_INFOS, newsInfos);
                setArguments(bundle);
                setupRecyclerView();
                swipeRefreshLayout.setPullUpRefreshing(false);
            }
        });
    }

    private void setupRecyclerView() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            adapter = new NewsInfoAdapter(getActivity(), newsInfos);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onDestroy() {
        newsInfoBiz.onDestroy();
        super.onDestroy();
    }
}
