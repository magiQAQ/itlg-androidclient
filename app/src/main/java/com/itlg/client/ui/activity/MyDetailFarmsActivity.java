package com.itlg.client.ui.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.itlg.client.R;
import com.itlg.client.bean.FarmInfoModel;
import com.itlg.client.ui.adapter.FragmentAdapter;
import com.itlg.client.ui.fragment.DetailFarmsFragment;
import com.itlg.client.utils.MyUtils;
import com.itlg.client.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyDetailFarmsActivity extends BaseActivity {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private ArrayList<FarmInfoModel> farmInfoModels;
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_detail_farms);
        ButterKnife.bind(this);
        setStatusBarColor(R.color.list_background, false);

        farmInfoModels = getIntent().getParcelableArrayListExtra(MyUtils.KEY_FARM_INFO_MODELS);
        if (farmInfoModels == null) {
            ToastUtils.showToast("你不是农场主哦");
            finish();
        }

        initView();
    }

    private void initView() {
        for (FarmInfoModel model : farmInfoModels) {
            DetailFarmsFragment fragment = DetailFarmsFragment.newInstance(model);
            fragments.add(fragment);
        }

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(fragments.size() > 2 ? fragments.size() - 1 : 1);
        tabLayout.setupWithViewPager(viewPager);


    }
}
