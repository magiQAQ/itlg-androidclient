package com.itlg.client.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.itlg.client.R;
import com.itlg.client.UserInfoHolder;
import com.itlg.client.bean.FarmInfoModel;
import com.itlg.client.bean.User;
import com.itlg.client.biz.FarmInfoBiz;
import com.itlg.client.biz.UserBiz;
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.itlg.client.ui.adapter.FarmInfoAdapter;
import com.itlg.client.utils.MyUtils;
import com.itlg.client.utils.ToastUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 操作员登录后看到的界面
 */
public class OperatorActivity extends BaseActivity {

    public static final int REQUEST_CODE_CAMERA = 5;
    private static final String TAG = "OperatorActivity";
    @BindView(R.id.userImg_imageView)
    ImageView userImgImageView;
    @BindView(R.id.farmInfo_recyclerView)
    RecyclerView farmInfoRecyclerView;

    private UserInfoHolder holder = UserInfoHolder.getInstance();
    private UserBiz userBiz;
    private FarmInfoBiz farmInfoBiz;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operater);
        ButterKnife.bind(this);
        setStatusBarColor(R.color.blueGreen, true);

        user = holder.getUser();
        setTitle(user.getName());
        setupToolbar();
        //验证用户是否为合法登录
        if (user.getPrivilege() != 1) {
            ToastUtils.showToast("你的身份不是操作员");
            finish();
        }

        initView();
    }

    private void initView() {
        //加载头像
        Glide.with(this)
                .load(Config.FILEURL + user.getUserImg())
                .circleCrop()
                .into(userImgImageView);

        //加载"我的农场"信息
        farmInfoBiz = new FarmInfoBiz();
        farmInfoBiz.getFarmInfoModels(new CommonCallback<List<FarmInfoModel>>() {
            @Override
            public void onFail(Exception e) {
                ToastUtils.showToast(e.getMessage());
            }

            @Override
            public void onSuccess(List<FarmInfoModel> response) {
                FarmInfoAdapter adapter = new FarmInfoAdapter(OperatorActivity.this, response);
                farmInfoRecyclerView.setAdapter(adapter);
                farmInfoRecyclerView.setLayoutManager(new LinearLayoutManager(OperatorActivity.this));
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //获取解析结果
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //result.getContents()即为二维码的内容,应为key=FarmInfoTP.getFarmInfo&farmId=3这样的参数
            if (result.getContents() == null) {
                ToastUtils.showToast("取消扫描");
            } else {
                farmInfoBiz = new FarmInfoBiz();
                farmInfoBiz.getFarmInfoModel(result.getContents(),
                        new CommonCallback<FarmInfoModel>() {
                            @Override
                            public void onFail(Exception e) {
                                ToastUtils.showToast(e.getMessage());
                            }

                            @Override
                            public void onSuccess(FarmInfoModel response) {
                                Intent intent = new Intent(OperatorActivity.this, FarmDetailActivity.class);
                                intent.putExtra(MyUtils.KEY_FARMINFOMODEL, response);
                                startActivity(intent);
                            }
                        });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA &&
                permissions[0].equals(Manifest.permission.CAMERA) &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //说明权限已经授予
            toScanActivity();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.operator_menu, menu);
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
            case R.id.scan_qr_code:
                toScanActivity();
                break;
            case R.id.logout:
                logout();
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userBiz != null) {
            userBiz.onDestroy();
        }
        if (farmInfoBiz != null) {
            farmInfoBiz.onDestroy();
        }
    }


    /**
     * 用户退出登录
     */
    private void logout() {
        //用户选择登出,同时把session中的用户登录状态清除
        userBiz = new UserBiz();
        userBiz.logout(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, e.getMessage());
                ToastUtils.showToast("登出失败,请确认网络连接");
            }

            @Override
            public void onResponse(String response, int id) {
                //清理用户登录登录信息
                UserInfoHolder.getInstance().clearUser();
                ToastUtils.showToast("登出成功");
                startActivity(new Intent(OperatorActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    /**
     * 得到拍二维码需要的权限
     */
    private boolean getPermission() {
        //大于6.0的系统需要获得拍摄权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                return false;
            }
        }
        return true;
    }


    /**
     * 跳转到扫码页面
     */
    private void toScanActivity() {
        if (!getPermission()) {
            return;
        }
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        //被扫描的二维码图片是否保存到本地
        intentIntegrator.setBarcodeImageEnabled(false);
        //是否开启扫码后的提示音
        intentIntegrator.setBeepEnabled(false);
        //设置使用的摄像头,0表示后置,1表示前置
        intentIntegrator.setCameraId(0);
        //设置期望的条形码格式,这里选择二维码
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        //设置活动的方向是否锁定,这里不锁定,配合用户手机的方向
        intentIntegrator.setOrientationLocked(false);
        //设置扫描界面的提示信息
        intentIntegrator.setPrompt("请扫描农场二维码");
        //开启扫码
        intentIntegrator.initiateScan();
    }


}
