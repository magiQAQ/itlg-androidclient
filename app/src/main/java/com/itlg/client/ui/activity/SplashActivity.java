package com.itlg.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.itlg.client.R;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.background_imageView)
    ImageView backgroundImageView;
    @BindView(R.id.logo_imageView)
    ImageView logoImageView;
    private MyHandler handler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);


        Glide.with(this).load(R.drawable.logo_big).into(logoImageView);
        Glide.with(this).load(R.drawable.slide).into(backgroundImageView);
        handler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    static class MyHandler extends Handler {

        private WeakReference<SplashActivity> activityWeakReference;


        MyHandler(SplashActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            activityWeakReference.get().startActivity(new Intent(activityWeakReference.get(), LoginActivity.class));
            activityWeakReference.get().finish();
        }
    }

}
