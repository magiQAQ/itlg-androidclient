package com.itlg.client.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.itlg.client.R;
import com.itlg.client.UserInfoHolder;
import com.itlg.client.bean.User;
import com.itlg.client.ui.adapter.FragmentAdapter;
import com.itlg.client.ui.fragment.DeviceDataFragment;
import com.itlg.client.ui.fragment.FarmInfoFragment;
import com.itlg.client.ui.fragment.OperationLogFragment;
import com.itlg.client.utils.ToastUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnPageChange;

/**
 * 操作员登录后看到的界面
 */
public class OperatorActivity extends BaseActivity {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;
    private UserInfoHolder holder = UserInfoHolder.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operater);
        ButterKnife.bind(this);

        setStatusBarColor(R.color.blueGreen, true);

        User user = holder.getUser();
        setTitle("操作员 " + user.getName());
        setupSimpleToolbar();
        //验证用户是否为合法登录
        if (user.getPrivilege()!=1){
            ToastUtils.showToast("你的身份不是操作员");
            finish();
        }

        initView();
    }


    private void initView() {
        Fragment[] fragments = {OperationLogFragment.getInstance(),
                DeviceDataFragment.getInstance(),
                FarmInfoFragment.getInstance()};
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
                case R.id.navigation_farm_info:
                    viewPager.setCurrentItem(2);
                    break;
            }
            return false;
        });
    }


    @OnPageChange(R.id.viewPager)
    public void onViewPagerChange(int position) {
        bottomNavigation.setSelectedItemId(bottomNavigation
                .getMenu().getItem(position).getItemId());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        //这样设置使menu item 显示 icon
        if (menu != null && menu.getClass().getSimpleName().equals("MenuBuilder")) {
            try {
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                method.setAccessible(true);
                method.invoke(menu, true);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }
        return super.onMenuOpened(featureId, menu);
    }

    /**
     * 菜单选项被按下的事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_operation_log:
                break;
            case R.id.menu_add_device_data:
                break;
            case R.id.menu_add_new_farm:
                break;
        }
        return false;
    }
}
