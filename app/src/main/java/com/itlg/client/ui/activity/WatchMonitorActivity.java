package com.itlg.client.ui.activity;

import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.itlg.client.R;
import com.itlg.client.biz.VideoBiz;
import com.itlg.client.utils.ToastUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class WatchMonitorActivity extends AppCompatActivity {

    private static final String TAG = "WatchMonitorActivity";
    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.progress_linearLayout)
    LinearLayout progressLinearLayout;

    private VideoBiz biz;
    private IjkMediaPlayer player;
    //private String url = Config.VIDEOURL;
    private String url = "http://ivi.bupt.edu.cn/hls/cctv3hd.m3u8";
    private TryConnectUrlThread tryConnectUrlThread;

    //surface的创建,销毁监听
    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //发送请求
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
                            tryConnectUrlThread = new TryConnectUrlThread();
                            tryConnectUrlThread.setThreadStop(false);
                            // 让线程每秒一次去检测url是否可用
                            tryConnectUrlThread.start();
                        } else {
                            ToastUtils.showToast(jsonObject.getString("stmt"));
                            onBackPressed();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            //提示用户正在打开监控
            progressLinearLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (player != null) {
                player.setDisplay(holder);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            tryConnectUrlThread.setThreadStop(true);
            if (player != null) {
                player.stop();
                player.release();
                player = null;
            }
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
            if (surfaceView != null) {
                surfaceView.getHolder().removeCallback(callback);
            }
        }
    };


    private void fullScreen() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_watch_monitor);
        ButterKnife.bind(this);
        biz = new VideoBiz();
        //surfaceView.getHolder().addCallback(callback);
        initPlayer();
    }

    private void initPlayer() {
        if (player == null) {
            //实例化播放器
            player = new IjkMediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "reconnect", 5);

            //开启硬件解码
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);//开启硬解码
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
        }
        try {
            player.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.prepareAsync();
        player.setDisplay(surfaceView.getHolder());
    }

    @Override
    protected void onDestroy() {
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
            player = null;
        }
        super.onDestroy();
    }

    //用来检测直播流是否可用
    class TryConnectUrlThread extends Thread {

        private boolean threadStop;

        TryConnectUrlThread() {
            super();
        }

        @Override
        public void run() {
            super.run();
            HttpURLConnection connection = null;
            while (!threadStop) {
                Log.e(TAG, "尝试连接url");
                try {
                    connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setConnectTimeout(1000);
                    connection.setReadTimeout(1000);
                    //测试连接是否可用
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        //如果连接可用就初始化播放器并播放
                        runOnUiThread(() -> {
                            initPlayer();
                            //关闭正在打开监控的提示
                            progressLinearLayout.setVisibility(View.GONE);
                        });
                        break;
                    } else {
                        Log.e(TAG, "url ResponseCode" + connection.getResponseCode());
                    }
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) connection.disconnect();
                }

                //线程等待1秒
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        void setThreadStop(boolean threadStop) {
            this.threadStop = threadStop;
        }

    }
}
