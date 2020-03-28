package com.itlg.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.itlg.client.R;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.background_imageView)
    ImageView backgroundImageView;
    @BindView(R.id.logo_imageView)
    ImageView logoImageView;
    private MyHandler handler = new MyHandler(this);
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        //黄油刀绑定view
        bind = ButterKnife.bind(this);

        Glide.with(this).load(R.drawable.logo_big).into(logoImageView);
        Glide.with(this).load(R.drawable.slide).into(backgroundImageView);
        handler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    protected void onDestroy() {
        //黄油刀解绑
        bind.unbind();
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    //静态防止内存溢出
    static class MyHandler extends Handler {
        //弱引用持有这个activity,防止对象无法被cg回收
        private WeakReference<SplashActivity> activityWeakReference;

        MyHandler(SplashActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            activityWeakReference.get().startActivity(new Intent(activityWeakReference.get(), LoginActivity.class));
            activityWeakReference.get().finish();
        }
    }

}
