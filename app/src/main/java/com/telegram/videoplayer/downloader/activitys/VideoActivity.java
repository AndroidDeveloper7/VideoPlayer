package com.telegram.videoplayer.downloader.activitys;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.UriUtil;
import com.telegram.videoplayer.downloader.BuildConfig;
import com.telegram.videoplayer.downloader.R;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

public class VideoActivity extends AppCompatActivity implements VideoRendererEventListener {
    private static final String TAG = "MainActivity";
    public SimpleExoPlayer player;
    public PlayerView simpleExoPlayerView;
    ImageButton BTNFullScreen;
    ImageButton BTNFullScreena;
    String Link;
    ProgressBar progressBar;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_video);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);

        BTNFullScreena = findViewById(R.id.fullscreen);
        BTNFullScreena.setOnClickListener(view -> {
            if (getResources().getConfiguration().orientation == 2) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return;
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            new CountDownTimer(2000, 1000) {
                public void onTick(long j) {
                }

                public void onFinish() {
                    if (Build.VERSION.SDK_INT >= 19) {
                        getWindow().getDecorView().setSystemUiVisibility(5894);
                    }
                }
            }.start();
        });
        BTNFullScreen = findViewById(R.id.btnfullscreen);
        BTNFullScreen.setOnClickListener(view -> {
            if (getResources().getConfiguration().orientation == 2) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                BTNFullScreen.setVisibility(View.GONE);
                return;
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            new CountDownTimer(2000, 1000) {
                public void onTick(long j) {
                }

                public void onFinish() {
                    if (Build.VERSION.SDK_INT >= 19) {
                        getWindow().getDecorView().setSystemUiVisibility(5894);
                    }
                }
            }.start();
        });
    }


    private void VideoLoad() {
        Uri parse = Uri.parse(Link);
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(defaultBandwidthMeter)));
        simpleExoPlayerView = new SimpleExoPlayerView(this);
        simpleExoPlayerView = findViewById(R.id.player_view);
        int i = simpleExoPlayerView.getResources().getConfiguration().screenHeightDp;
        int i2 = simpleExoPlayerView.getResources().getConfiguration().screenWidthDp;
        Log.v(TAG, "height : " + i + " weight: " + i2);
        simpleExoPlayerView.setUseController(true);
        simpleExoPlayerView.requestFocus();
        simpleExoPlayerView.setPlayer(player);
        final LoopingMediaSource loopingMediaSource = new LoopingMediaSource(buildMediaSource(parse));
        player.prepare(buildMediaSource(parse));
        BTNFullScreen = findViewById(R.id.btnfullscreen);
        simpleExoPlayerView.setOnTouchListener((view, motionEvent) -> {
            if (BTNFullScreen.getVisibility() == View.GONE && getResources().getConfiguration().orientation == 2) {
                BTNFullScreen.setVisibility(View.VISIBLE);
                new CountDownTimer(DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS, 1000) {
                    public void onTick(long j) {
                    }

                    public void onFinish() {
                        BTNFullScreen.setVisibility(View.GONE);
                    }
                }.start();
            }
            return false;
        });
        player.addListener(new ExoPlayer.EventListener() {
            public void onLoadingChanged(boolean z) {
            }

            public void onPlaybackParametersChanged(@NonNull PlaybackParameters playbackParameters) {
            }

            public void onPositionDiscontinuity(int i) {
            }

            public void onRepeatModeChanged(int i) {
            }

            public void onSeekProcessed() {
            }

            public void onShuffleModeEnabledChanged(boolean z) {
            }

            @SuppressWarnings("deprecation")
            public void onTimelineChanged(@NonNull Timeline timeline, @NonNull Object obj, int i) {
            }

            public void onTracksChanged(@NonNull TrackGroupArray trackGroupArray, @NonNull TrackSelectionArray trackSelectionArray) {
                Log.v(VideoActivity.TAG, "Listener-onTracksChanged... ");
            }

            public void onPlayerStateChanged(boolean z, int i) {
                Log.v(VideoActivity.TAG, "Listener-onPlayerStateChanged..." + i + "|||isDrawingCacheEnabled():" + simpleExoPlayerView.isDrawingCacheEnabled());
                simpleExoPlayerView.setKeepScreenOn(i != 1 && i != 4 && z);
            }

            public void onPlayerError(@NonNull ExoPlaybackException exoPlaybackException) {
                Log.v(VideoActivity.TAG, "Listener-onPlayerError...");
                player.stop();
                player.prepare(loopingMediaSource);
                player.setPlayWhenReady(true);
            }
        });
        player.setPlayWhenReady(true);
        player.setVideoDebugListener(this);
    }

    private MediaSource buildMediaSource(Uri parse) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, getString(R.string.app_name));
        if(parse.getLastPathSegment().contains("m3u8")) {
            return new HlsMediaSource.Factory(dataSourceFactory)
                    .setAllowChunklessPreparation(false)
                    .createMediaSource(parse);
        }
        else {
            return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(parse);
        }
    }

//    private MediaSource buildMediaSource(Uri uri)  {
//        String userAgent = Util.getUserAgent(videoPlayerView.context, getString(R.string.app_name))
//        String lastPathSegment = uri.lastPathSegment;
//         if (lastPathSegment.contains("mp3") ||
//                lastPathSegment.contains("mp4")
//        ) {
//             return ProgressiveMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
//                    .createMediaSource(uri);
//        } else if (lastPathSegment?.contains("m3u8") == true) {
//            HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
//                    .createMediaSource(uri)
//        } else {
//            val dashChunkSourceFactory = DefaultDashChunkSource.Factory(
//                    DefaultHttpDataSourceFactory("ua", null)
//            )
//            val manifestDataSourceFactory = DefaultHttpDataSourceFactory(userAgent)
//            DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory)
//                    .createMediaSource(uri)
//        }
//    }

    public void onVideoDecoderInitialized(@NonNull String str, long j, long j2) {
        progressBar.setVisibility(View.GONE);
    }

    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
        Log.v(TAG, "onVideoSizeChanged [ width: " + i + " height: " + i2 + "]");
    }

    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()...");
        player.stop();
        player.release();
    }

    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()...");
    }

    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()...");
        if (getIntent() != null) {
            Bundle extras = getIntent().getExtras();
            if (extras.containsKey("Link")) {
                Link = extras.getString("Link");
                VideoLoad();
            }
        }
    }

    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()...");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()...");
        player.release();
    }

    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == 2) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        player.stop();
        finish();
        super.onBackPressed();

    }
}
