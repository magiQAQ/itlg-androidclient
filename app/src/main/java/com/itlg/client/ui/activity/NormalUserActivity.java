package com.itlg.client.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.itlg.client.R;
import com.itlg.client.ui.adapter.FragmentAdapter;
import com.itlg.client.ui.fragment.FarmMallFragment;
import com.itlg.client.ui.fragment.MineFragment;
import com.itlg.client.ui.fragment.NewsFragment;
import com.itlg.client.ui.fragment.ProductMallFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NormalUserActivity extends BaseActivity {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    private Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_user);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        fragments = new Fragment[]{ProductMallFragment.newInstance(), FarmMallFragment.newInstance(),
                NewsFragment.newInstance(), MineFragment.newInstance()};
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        //该方法虽然能与viewPager关联但是会清除所有的tab并生成自己的tab,所以调用后需要手动remove所有生成的tab
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.removeAllTabs();
        //添加自定义tab
        tabLayout.addTab(tabLayout.newTab().setCustomView(getTabView(R.drawable.icon_product_mall, "农产品商城")));
        tabLayout.addTab(tabLayout.newTab().setCustomView(getTabView(R.drawable.icon_farm_mall, "农田商城")));
        tabLayout.addTab(tabLayout.newTab().setCustomView(getTabView(R.drawable.icon_news, "农业资讯")));
        tabLayout.addTab(tabLayout.newTab().setCustomView(getTabView(R.drawable.icon_mine, "我的慧农")));
    }


    //系统自带的tabItem不能改变图标大小,图标会显得很小,这里自定义tabView的样式
    private View getTabView(int iconRes, String title) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.tab_view, null);
        ((ImageView) view.findViewById(R.id.tab_icon))
                .setImageDrawable(getResources().getDrawable(iconRes));
        ((TextView) view.findViewById(R.id.tab_title)).setText(title);
        return view;
    }


}
