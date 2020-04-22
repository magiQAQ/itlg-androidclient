package com.itlg.client.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.itlg.client.R;
import com.itlg.client.biz.VideoBiz;
import com.itlg.client.ui.view.JzvdStdTikTok;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jzvd.JZDataSource;
import cn.jzvd.Jzvd;

public class WatchMonitorActivity extends AppCompatActivity {

    private static final String TAG = "WatchMonitorActivity";
    @BindView(R.id.jz_player)
    JzvdStdTikTok jzPlayer;
    @BindView(R.id.progress_linearLayout)
    LinearLayout progressLinearLayout;

    private VideoBiz biz;
    //private String url = Config.VIDEOURL;
    private String url = "http://ivi.bupt.edu.cn/hls/cctv3hd.m3u8";
    private TryConnectUrlThread tryConnectUrlThread;


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
        //biz = new VideoBiz();
        tryConnectUrlThread = new TryConnectUrlThread();
        tryConnectUrlThread.setThreadStop(false);
        // 让线程每秒一次去检测url是否可用
        tryConnectUrlThread.start();
        //发送请求
//        biz.openVideo(new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                ToastUtils.showToast("网络连接失败");
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    if (jsonObject.getBoolean("succ")) {
//                        tryConnectUrlThread = new TryConnectUrlThread();
//                        tryConnectUrlThread.setThreadStop(false);
//                        // 让线程每秒一次去检测url是否可用
//                        tryConnectUrlThread.start();
//                    } else {
//                        ToastUtils.showToast(jsonObject.getString("stmt"));
//                        onBackPressed();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        //提示用户正在打开监控
        progressLinearLayout.setVisibility(View.VISIBLE);
    }

    private void initPlayer() {
        JZDataSource jzDataSource = new JZDataSource(url, "智慧农业");
        jzDataSource.looping = true;
        jzPlayer.setUp(jzDataSource, Jzvd.SCREEN_FULLSCREEN);
        jzPlayer.startVideoAfterPreloading();
        //监控已打开,关闭提示
        progressLinearLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
//        biz.closeVideo(new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                ToastUtils.showToast("网络异常,非正常关闭");
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                Log.e(TAG, "视频流已关闭");
//            }
//        });
        if (tryConnectUrlThread != null) {
            tryConnectUrlThread.setThreadStop(true);
        }
        JzvdStdTikTok.releaseAllVideos();
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
