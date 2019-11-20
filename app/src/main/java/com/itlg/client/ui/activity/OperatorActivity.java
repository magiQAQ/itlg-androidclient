package com.itlg.client.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
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
import com.itlg.client.config.Config;
import com.itlg.client.net.CommonCallback;
import com.itlg.client.ui.adapter.FarmInfoAdapter;
import com.itlg.client.utils.MyUtils;
import com.itlg.client.utils.ToastUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 操作员登录后看到的界面
 */
public class OperatorActivity extends BaseActivity {

    public static final int REQUEST_CODE_CAMERA = 5;
    private static final String TAG = "OperatorActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.userImg_imageView)
    ImageView userImgImageView;
    @BindView(R.id.farmInfo_recyclerView)
    RecyclerView farmInfoRecyclerView;

    private UserInfoHolder holder = UserInfoHolder.getInstance();
    private FarmInfoBiz farmInfoBiz;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operater);
        ButterKnife.bind(this);

        user = holder.getUser();
        //验证用户是否为合法登录
        if (user.getPrivilege() != 1) {
            ToastUtils.showToast("你的身份不是操作员");
            finish();
        }
        initView();
    }

    private void initView() {
        setStatusBarColor(R.color.transparent, false);
        setSupportActionBar(toolbar);

        //加载头像
        Glide.with(this)
                .load(Config.FILEURL + user.getUserImg())
                .circleCrop()
                .into(userImgImageView);

        //显示当前用户的名字
        setTitle(user.getName());

        //加载"我的农场"信息
        farmInfoBiz = new FarmInfoBiz();
        farmInfoBiz.getFarmInfoModels(new CommonCallback<ArrayList<FarmInfoModel>>() {
            @Override
            public void onFail(Exception e) {
                ToastUtils.showToast(e.getMessage());
            }

            @Override
            public void onSuccess(ArrayList<FarmInfoModel> response) {
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
                                intent.putExtra(MyUtils.KEY_FARM_INFO_MODEL, response);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (farmInfoBiz != null) {
            farmInfoBiz.onDestroy();
        }
    }

    /**
     * 点击虚浮按钮出现菜单
     */
    @SuppressLint("PrivateApi")
    @OnClick(R.id.menu_floatButton)
    public void showMenu(View view) {
        //创建弹出式菜单
        PopupMenu popupMenu = new PopupMenu(this, view);
        //设置菜单内容
        popupMenu.getMenuInflater().inflate(R.menu.operator_menu, popupMenu.getMenu());
        //设置菜单选项点击事件
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.scan_qr_code:
                    toScanActivity();
                    break;
                case R.id.logout:
                    logout();
                    break;
            }
            return false;
        });
        //默认不显示图标,但我想要显示,通过java反射实现
        Class<?> clazz;
        try {
            clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            //传入参数
            m.invoke(popupMenu.getMenu(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //显示
        popupMenu.show();
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
