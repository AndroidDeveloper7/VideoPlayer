package com.telegram.videoplayer.downloader.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.activitys.VideoPlayerActivity;
import com.telegram.videoplayer.downloader.model.VideoItem;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "rawtypes"})
public class floating extends Service {
    public View mFloatingView;
    public WindowManager mWindowManager;
    public SimpleExoPlayer player;
    public PlayerView playerView;
    public int position;
    private int layoutFlag = 2002;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        super.onDestroy();
        this.player.setPlayWhenReady(false);
        View view = this.mFloatingView;
        if (view != null) {
            this.mWindowManager.removeView(view);
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        dothis(intent);
        return super.onStartCommand(intent, i, i2);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("WrongConstant")
    private void dothis(Intent intent) {
        this.mFloatingView = LayoutInflater.from(this).inflate(R.layout.popup_window, null);
        if (Build.VERSION.SDK_INT >= 26) {
            this.layoutFlag = 2038;
        } else {
            this.layoutFlag = 2002;
        }
        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, this.layoutFlag, 8, -3);
        layoutParams.gravity = 17;
        layoutParams.x = 0;
        layoutParams.y = 100;
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        this.mWindowManager = windowManager;
        windowManager.addView(this.mFloatingView, layoutParams);
        this.mFloatingView.findViewById(R.id.player_view).setOnTouchListener(new View.OnTouchListener() {
            private float initialTouchX;
            private float initialTouchY;
            private int initialX;
            private int initialY;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    if (floating.this.playerView.isControllerVisible()) {
                        floating.this.playerView.hideController();
                    } else {
                        floating.this.playerView.showController();
                    }
                    this.initialX = layoutParams.x;
                    this.initialY = layoutParams.y;
                    this.initialTouchX = motionEvent.getRawX();
                    this.initialTouchY = motionEvent.getRawY();
                    return true;
                } else if (action != 2) {
                    return false;
                } else {
                    layoutParams.x = this.initialX + ((int) (motionEvent.getRawX() - this.initialTouchX));
                    layoutParams.y = this.initialY + ((int) (motionEvent.getRawY() - this.initialTouchY));
                    floating.this.mWindowManager.updateViewLayout(floating.this.mFloatingView, layoutParams);
                    return true;
                }
            }
        });
        this.position = intent.getIntExtra("position", 0);
        final List list = (List) intent.getSerializableExtra("list");
        long longExtra = intent.getLongExtra("current", 0);
        this.playerView = this.mFloatingView.findViewById(R.id.player_view);
        this.player = ExoPlayerFactory.newSimpleInstance(this);
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "com.abc.maxvideoplayer"));
        int size = list.size();
        MediaSource[] mediaSourceArr = new MediaSource[size];
        for (int i = 0; i < list.size(); i++) {
            mediaSourceArr[i] = new ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse(((VideoItem) list.get(i)).data));
        }
        MediaSource concatenatingMediaSource = size == 1 ? mediaSourceArr[0] : new ConcatenatingMediaSource(mediaSourceArr);
        this.playerView.setPlayer(this.player);
        this.player.prepare(concatenatingMediaSource);
        this.player.setPlayWhenReady(true);
        this.player.seekTo(this.position, longExtra);
        this.playerView.setResizeMode(0);
        this.mFloatingView.findViewById(R.id.close).setOnClickListener(view -> floating.this.stopSelf());
        this.mFloatingView.findViewById(R.id.full).setOnClickListener(view -> {
            Intent intent1 = new Intent(getApplicationContext(), VideoPlayerActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("position", player.getCurrentWindowIndex());
            intent1.putExtra("list", (Serializable) list);
            intent1.putExtra("current", player.getCurrentPosition());
            startActivity(intent1);
            stopSelf();
        });
    }
}
