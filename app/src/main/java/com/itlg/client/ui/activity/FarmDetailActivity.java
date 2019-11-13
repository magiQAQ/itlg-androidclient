package com.itlg.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itlg.client.R;
import com.itlg.client.bean.DeviceInfo;
import com.itlg.client.bean.FarmInfoModel;
import com.itlg.client.biz.DeviceDataBiz;
import com.itlg.client.biz.OperationLogBiz;
import com.itlg.client.net.CommonCallback;
import com.itlg.client.ui.adapter.FragmentAdapter;
import com.itlg.client.ui.fragment.DeviceDataFragment;
import com.itlg.client.ui.fragment.OperationLogFragment;
import com.itlg.client.utils.MyUtils;
import com.itlg.client.utils.ToastUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
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
    private DeviceDataBiz deviceDataBiz;
    private OperationLogBiz operationLogBiz;
    private int farmId;
    private OperationLogFragment operationLogFragment;
    private DeviceDataFragment deviceDataFragment;
    private List<DeviceInfo> deviceInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_detail);
        ButterKnife.bind(this);

        //得到扫码获得的农场信息
        Intent data = getIntent();
        model = data.getParcelableExtra(MyUtils.KEY_FARMINFOMODEL);

        //初始化事务处理
        deviceDataBiz = new DeviceDataBiz();
        operationLogBiz = new OperationLogBiz();

        initView();

        getDeviceInfos();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        //设置ToolBar和StatusBar
        setSupportActionBar(toolbar);
        setStatusBarColor(R.color.blueGreen, true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        int typeId = model.getFarmInfo().getTypeId();
        farmId = model.getFarmInfo().getId();

        //农场详情
        if (typeId == 1) {
            farmIconImageView.setImageResource(R.mipmap.nongchang);
        } else if (typeId == 2) {
            farmIconImageView.setImageResource(R.mipmap.yangzhichang);
        }
        farmNameTextView.setText(String.format(getResources().getString(R.string.typename_id),
                model.getTypeName(), farmId));
        farmStatusTextView.setText("状态正常");

        //操作日志和设备日志
        operationLogFragment = OperationLogFragment.newInstance(farmId);
        deviceDataFragment = DeviceDataFragment.newInstance(farmId);
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
        switch (item.getItemId()) {
            case R.id.menu_add_operation_log:
                openAddOperationLogDialog();
                break;
            case R.id.menu_add_device_data:
                openAddDeviceDataDialog();
                break;
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
        deviceDataBiz.onDestroy();
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

    /**
     * 打开添加设备记录消息框
     */
    private void openAddDeviceDataDialog() {
        //生成下拉框列表的数据
        List<String> list = new ArrayList<>();
        for (DeviceInfo deviceInfo : deviceInfos) {
            list.add(deviceInfo.getDeviceName() + deviceInfo.getDeviceCode());
        }
        list.add("请选择设备");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_commit_device_data, farmDetailLayout, false);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        TextInputLayout dataInfoLayout = view.findViewById(R.id.data_info_Layout);
        TextInputEditText dataInfoEditText = view.findViewById(R.id.data_info_editText);
        Button commitButton = view.findViewById(R.id.commit_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Spinner deviceNameSpinner = view.findViewById(R.id.device_name_spinner);
        TextView spinnerErrorTextView = view.findViewById(R.id.spinner_error_textView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list) {
            @Override
            public int getCount() {
                //下拉列表中不会显示list里面最后一项,因此最后一项可以用来当hint
                int count = super.getCount();
                return count > 0 ? count - 1 : count;
            }
        };
        deviceNameSpinner.setAdapter(adapter);
        deviceNameSpinner.setSelection(list.size() - 1, false);
        cancelButton.setOnClickListener(v -> {
            if (alertDialog.isShowing()) alertDialog.dismiss();
        });
        commitButton.setOnClickListener(v -> {
            String deviceInfo = dataInfoEditText.getEditableText().toString();
            //检测用户是否选择了设备
            if (deviceNameSpinner.getSelectedItemPosition() >= deviceInfos.size()) {
                spinnerErrorTextView.setVisibility(View.VISIBLE);
                return;
            } else {
                spinnerErrorTextView.setVisibility(View.GONE);
            }
            int deviceId = deviceInfos.get(deviceNameSpinner.getSelectedItemPosition()).getId();
            if (deviceInfo.isEmpty()) {
                //打开错误提示
                dataInfoLayout.setErrorEnabled(true);
                dataInfoLayout.setError("必须填写");
            } else {
                //关闭错误提示
                dataInfoLayout.setErrorEnabled(false);
                //使按钮不可用
                commitButton.setText("提交中...");
                commitButton.setEnabled(false);
                cancelButton.setEnabled(false);
                //提交
                deviceDataBiz.commitDeviceData(deviceId, deviceInfo, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("commitDeviceData", e.getMessage());
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
                                deviceDataFragment.refreshRecyclerList();
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

    private void getDeviceInfos() {
        deviceDataBiz.getDeviceInfoByFarmId(farmId, new CommonCallback<ArrayList<DeviceInfo>>() {
            @Override
            public void onFail(Exception e) {
                ToastUtils.showToast("网络异常");
                Log.e("getDeviceInfoByFarmId", e.getMessage());
            }

            @Override
            public void onSuccess(ArrayList<DeviceInfo> response) {
                deviceInfos = response;
            }
        });
    }
}
