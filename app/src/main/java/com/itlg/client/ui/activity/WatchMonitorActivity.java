package com.itlg.client.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.itlg.client.R;
import com.itlg.client.biz.VideoBiz;
import com.itlg.client.utils.ToastUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class WatchMonitorActivity extends AppCompatActivity {

    private static final String TAG = "WatchMonitorActivity";
    @BindView(R.id.playerView)
    PlayerView playerView;
    private VideoBiz biz;
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_watch_monitor);
        ButterKnife.bind(this);
        biz = new VideoBiz();
    }

    @Override
    protected void onStart() {
        super.onStart();
        biz.openVideo(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showToast("网络连接失败");
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("succ")) {
                        initView();
                    } else {
                        ToastUtils.showToast(jsonObject.getString("stmt"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void fullScreen() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initView() {
        //实例化播放器
        player = ExoPlayerFactory.newSimpleInstance(this);
        Uri uri = Uri.parse("http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8");
        //资源工厂
        DefaultDataSourceFactory factory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "智慧农业"));
        //解析播放资源,m3u8属于hls
        MediaSource mediaSource = new HlsMediaSource.Factory(factory).createMediaSource(uri);
        //播放器开始加载
        player.prepare(mediaSource);
        //视图与播放器关联
        playerView.setPlayer(player);
        //加载完成就自动播放
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onStop() {
        biz.closeVideo(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showToast("网络异常,非正常关闭");
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "视频流已关闭");
            }
        });
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (player != null) {
            player.release();
        }
        super.onDestroy();
    }
}
