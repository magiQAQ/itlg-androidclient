package com.itlg.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.itlg.client.R;
import com.itlg.client.bean.FarmInfoModel;
import com.itlg.client.ui.adapter.FragmentAdapter;
import com.itlg.client.ui.fragment.DeviceDataFragment;
import com.itlg.client.ui.fragment.OperationLogFragment;
import com.itlg.client.utils.MyUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnPageChange;

public class FarmDetailActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;
    @BindView(R.id.farm_icon_imageView)
    ImageView farmIconImageView;
    @BindView(R.id.farm_name_textView)
    TextView farmNameTextView;
    @BindView(R.id.farm_status_textView)
    TextView farmStatusTextView;
    private FarmInfoModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setStatusBarColor(R.color.blueGreen, true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Intent data = getIntent();
        model = data.getParcelableExtra(MyUtils.KEY_FARMINFOMODEL);
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        //农场详情
        if (model.getFarmInfo().getTypeId() == 1) {
            farmIconImageView.setImageResource(R.mipmap.nongchang);
        } else if (model.getFarmInfo().getTypeId() == 2) {
            farmIconImageView.setImageResource(R.mipmap.yangzhichang);
        }
        farmNameTextView.setText(String.format(getResources().getString(R.string.typename_id),
                model.getTypeName(), model.getFarmInfo().getId()));
        farmStatusTextView.setText("状态正常");

        //操作记录和设备日志
        Fragment[] fragments = {OperationLogFragment.newInstance(model.getFarmInfo().getId()),
                DeviceDataFragment.newInstance(model.getFarmInfo().getId())};
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);

        bottomNavigation.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_operation:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.navigation_device_data:
                    viewPager.setCurrentItem(1);
                    break;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.farm_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_operation_log:
                break;
            case R.id.menu_add_device_data:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnPageChange(R.id.viewPager)
    public void onViewPagerChange(int position) {
        bottomNavigation.getMenu().getItem(position).setChecked(true);
    }
}
