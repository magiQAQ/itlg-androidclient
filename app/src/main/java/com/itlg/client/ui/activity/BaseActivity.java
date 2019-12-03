package com.itlg.client.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.itlg.client.R;
import com.itlg.client.UserInfoHolder;
import com.itlg.client.biz.UserBiz;
import com.itlg.client.utils.ToastUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public abstract class BaseActivity extends AppCompatActivity {

    private UserBiz userBiz;

    protected void setupCommonToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    protected void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }



    protected void setStatusBarColor(int resColorId,boolean currentDark){
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(resColorId));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (currentDark){
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

        }
    }

    protected int getStatusBarColor() {
        return getWindow().getStatusBarColor();
    }

    protected void showCompleteDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams")
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm, null, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        //把警告图标换掉
        ImageView iconImageView = dialogView.findViewById(R.id.icon_imageView);
        iconImageView.setImageResource(R.drawable.ic_done_green_24dp);
        //设置提示信息
        TextView messageTextView = dialogView.findViewById(R.id.message_textView);
        messageTextView.setText(message);
        //设置取消按钮不可见
        dialogView.findViewById(R.id.cancel_button).setVisibility(View.GONE);
        //设置确认按钮
        dialogView.findViewById(R.id.confirm_button).setOnClickListener(v -> alertDialog.dismiss());
        //显示提示消息
        alertDialog.show();
    }

    /**
     * 用户退出登录
     */
    public void logout() {
        //用户选择登出,同时把session中的用户登录状态清除
        userBiz = new UserBiz();
        userBiz.logout(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(this.getClass().getName(), e.getMessage());
                ToastUtils.showToast("登出失败,请确认网络连接");
            }

            @Override
            public void onResponse(String response, int id) {
                //清理用户登录登录信息
                UserInfoHolder.getInstance().clearUser();
                ToastUtils.showToast("登出成功");
                startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (userBiz != null) {
            userBiz.onDestroy();
        }
        super.onDestroy();
    }
}
