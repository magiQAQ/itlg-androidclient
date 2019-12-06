package com.itlg.client.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.itlg.client.R;
import com.itlg.client.config.Config;
import com.itlg.client.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WatchMonitorActivity extends AppCompatActivity {

    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.play_button)
    ImageView playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_monitor);
        ButterKnife.bind(this);

        fullScreen();
        initView();
    }

    private void fullScreen() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void initView() {
        //监控视频
        videoView.setVideoURI(Uri.parse(Config.VIDEOURL));
        videoView.setOnPreparedListener(mp -> playButton.setVisibility(View.VISIBLE));

        videoView.setOnCompletionListener(mp -> ToastUtils.showToast("监控结束,摄像头已撤下"));

        videoView.setOnErrorListener((mp, what, extra) -> {
            ToastUtils.showToast("监控打开失败");
            return false;
        });
    }

    //播放时点击视频会暂停
    @OnClick(R.id.videoView)
    void pauseVideo() {
        if (videoView.isPlaying()) {
            videoView.pause();
            playButton.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.play_button)
    void startVideo() {
        videoView.start();
        playButton.setVisibility(View.GONE);
    }
}
