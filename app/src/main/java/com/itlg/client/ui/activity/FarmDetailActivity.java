package com.itlg.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itlg.client.R;
import com.itlg.client.bean.FarmInfoModel;
import com.itlg.client.biz.OperationLogBiz;
import com.itlg.client.ui.adapter.FragmentAdapter;
import com.itlg.client.ui.fragment.DeviceDataFragment;
import com.itlg.client.ui.fragment.OperationLogFragment;
import com.itlg.client.utils.MyUtils;
import com.itlg.client.utils.ToastUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnPageChange;
import okhttp3.Call;

public class FarmDetailActivity extends BaseActivity {

    @BindView(R.id.farm_detail_layout)
    LinearLayout farmDetailLayout;
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
    private OperationLogBiz operationLogBiz;
    private int farmId;
    private OperationLogFragment operationLogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_detail);
        ButterKnife.bind(this);

        //得到扫码获得的农场信息
        Intent data = getIntent();
        model = data.getParcelableExtra(MyUtils.KEY_FARM_INFO_MODEL);

        operationLogBiz = new OperationLogBiz();

        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        //设置ToolBar和StatusBar
        setSupportActionBar(toolbar);
        setStatusBarColor(R.color.colorTheme, true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        int typeId = model.getFarmInfo().getTypeId();
        farmId = model.getFarmInfo().getId();

        //农场详情
        if (typeId == 1) {
            Glide.with(this).load(R.drawable.nongchang)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(20))).into(farmIconImageView);
        } else if (typeId == 2) {
            Glide.with(this).load(R.drawable.yangzhichang)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(20))).into(farmIconImageView);
        }
        farmNameTextView.setText(String.format(getResources().getString(R.string.typename_id),
                model.getTypeName(), farmId));
        farmStatusTextView.setText("状态正常");

        //操作日志和设备日志
        operationLogFragment = OperationLogFragment.newInstance(farmId);
        DeviceDataFragment deviceDataFragment = DeviceDataFragment.newInstance(farmId);
        Fragment[] fragments = new Fragment[]{operationLogFragment, deviceDataFragment};

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
        if (item.getItemId() == R.id.menu_add_operation_log) {
            openAddOperationLogDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnPageChange(R.id.viewPager)
    public void onViewPagerChange(int position) {
        bottomNavigation.getMenu().getItem(position).setChecked(true);
    }

    @Override
    protected void onDestroy() {
        operationLogBiz.onDestroy();
        super.onDestroy();
    }

    /**
     * 打开添加操作日志消息框
     */
    private void openAddOperationLogDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_commit_operation_log, farmDetailLayout, false);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        TextInputLayout operationLogLayout = view.findViewById(R.id.operation_log_Layout);
        TextInputEditText operationLogEditText = view.findViewById(R.id.operation_log_editText);
        Button commitButton = view.findViewById(R.id.commit_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> {
            if (alertDialog.isShowing()) alertDialog.dismiss();
        });
        commitButton.setOnClickListener(v -> {
            String operationInfo = operationLogEditText.getEditableText().toString();
            if (operationInfo.isEmpty()) {
                //打开错误提示
                operationLogLayout.setErrorEnabled(true);
                operationLogLayout.setError("必须填写");
            } else {
                //关闭错误提示
                operationLogLayout.setErrorEnabled(false);
                //使按钮不可用
                commitButton.setText("提交中...");
                commitButton.setEnabled(false);
                cancelButton.setEnabled(false);
                //提交
                operationLogBiz.commitOperationLog(farmId, operationInfo, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("commitOperationLog", e.getMessage());
                        //重新变回可用
                        commitButton.setText(R.string.commit);
                        commitButton.setEnabled(true);
                        cancelButton.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("succ")) {
                                alertDialog.dismiss();
                                operationLogFragment.refreshRecyclerList();
                            } else {
                                //重新变回可用
                                commitButton.setText(R.string.commit);
                                commitButton.setEnabled(true);
                                cancelButton.setEnabled(true);
                            }
                            //无论成功是否都显示消息
                            ToastUtils.showToast(jsonObject.getString("stmt"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

}
