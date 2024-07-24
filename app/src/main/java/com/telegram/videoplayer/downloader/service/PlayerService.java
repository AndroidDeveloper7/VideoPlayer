package com.telegram.videoplayer.downloader.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.telegram.videoplayer.downloader.model.VideoItem;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

@SuppressWarnings({"rawtypes", "ResultOfMethodCallIgnored", "NullableProblems"})
public class PlayerService extends Service implements AudioManager.OnAudioFocusChangeListener, Player.EventListener {
    private final IBinder playerBind = new PlayerBinder();
    public MediaSessionCompat mediaSession;
    private AudioManager audioManager;
    private SimpleExoPlayer exoPlayer;
    private Intent mIntent;
    private String status = "Playing";
    private String streamUrl;
    private final MediaSessionCompat.Callback mediasSessionCallback = new MediaSessionCompat.Callback() {

        @Override
        public void onPause() {
            super.onPause();
            PlayerService.this.pause();
        }

        @Override
        public void onStop() {
            super.onStop();
            PlayerService.this.stop();
        }

        @Override
        public void onPlay() {
            super.onPlay();
            PlayerService.this.resume();
        }
    };
    private MediaControllerCompat.TransportControls transportControls;


    @Override
    public void onIsPlayingChanged(boolean z) {
    }

    @Override
    public void onLoadingChanged(boolean z) {
    }


    @Override
    public void onPlaybackParametersChanged(@NonNull PlaybackParameters playbackParameters) {
    }


    @Override
    public void onPlaybackSuppressionReasonChanged(int i) {
    }

    @Override
    public void onPlayerError(@NonNull ExoPlaybackException exoPlaybackException) {
    }

    @Override
    public void onPositionDiscontinuity(int i) {
    }

    @Override
    public void onRepeatModeChanged(int i) {
    }

    @Override
    public void onSeekProcessed() {
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean z) {
    }

    @Override
    public void onTimelineChanged(@NonNull Timeline timeline, int i) {
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onTimelineChanged(@NonNull Timeline timeline, @NonNull Object obj, int i) {
    }

    @Override
    public void onTracksChanged(@NonNull TrackGroupArray trackGroupArray, @NonNull TrackSelectionArray trackSelectionArray) {
    }

    public IBinder onBind(Intent intent) {
        this.mIntent = intent;
        return this.playerBind;
    }

    @SuppressLint("WrongConstant")
    public void onCreate() {
        super.onCreate();
        this.audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, getClass().getSimpleName());
        this.mediaSession = mediaSessionCompat;
        this.transportControls = mediaSessionCompat.getController().getTransportControls();
        this.mediaSession.setActive(true);
        this.mediaSession.setFlags(3);
        this.mediaSession.setCallback(this.mediasSessionCallback);
    }

    @SuppressWarnings("deprecation")
    public int onStartCommand(Intent intent, int i, int i2) {
        String action = intent.getAction();
        this.mIntent = intent;
        Log.e("Player ", " : 1");
        if (TextUtils.isEmpty(action)) {
            return START_NOT_STICKY;
        }
        Log.e("Player ", " : 2");
        if (this.audioManager.requestAudioFocus(this, 3, 1) != 1) {
            stop();
            return START_NOT_STICKY;
        }
        Log.e("Player ", " : 3");
        if (action.equalsIgnoreCase(PlayerNotificationManager.ACTION_PLAY)) {
            this.transportControls.play();
        } else if (action.equalsIgnoreCase(PlayerNotificationManager.ACTION_PAUSE)) {
            if ("PlaybackStatus_STOPPED".equals(this.status)) {
                this.transportControls.stop();
            } else {
                this.transportControls.pause();
            }
        } else if (action.equalsIgnoreCase(PlayerNotificationManager.ACTION_STOP)) {
            pause();
        }
        Log.e("Player ", " : 4");
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "com.abc.maxvideoplayer"));
        this.exoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        List list = (List) this.mIntent.getSerializableExtra("list");
        Log.e("list ", " Player service" + list.size());
        int size = list.size();
        MediaSource[] mediaSourceArr = new MediaSource[size];
        for (int i3 = 0; i3 < list.size(); i3++) {
            mediaSourceArr[i3] = new ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse(((VideoItem) list.get(i3)).data));
        }
        this.exoPlayer.prepare(size == 1 ? mediaSourceArr[0] : new ConcatenatingMediaSource(mediaSourceArr));
        this.exoPlayer.setPlayWhenReady(true);
        this.exoPlayer.addListener(this);
        this.status = "PlaybackStatus_IDLE";
        return START_NOT_STICKY;
    }

    public boolean onUnbind(Intent intent) {
        if (this.status.equals("PlaybackStatus_IDLE")) {
            stopSelf();
        }
        return super.onUnbind(intent);
    }

    public void onDestroy() {
        pause();
        this.exoPlayer.release();
        this.exoPlayer.removeListener(this);
        this.mediaSession.release();
        super.onDestroy();
    }

    public void onAudioFocusChange(int i) {
        if (i != -3) {
            if (i != -2) {
                if (i == -1) {
                    stop();
                } else if (i == 1) {
                    this.exoPlayer.setVolume(0.8f);
                    resume();
                }
            } else if (isPlaying()) {
                pause();
            }
        } else if (isPlaying()) {
            this.exoPlayer.setVolume(0.1f);
        }
    }


    @Override
    public void onPlayerStateChanged(boolean z, int i) {
        if (i == 1) {
            this.status = "PlaybackStatus.IDLE";
        } else if (i == 2) {
            this.status = "PlaybackStatus.LOADING";
        } else if (i == 3) {
            this.status = z ? "PlaybackStatus.PLAYING" : "PlaybackStatus.PAUSED";
        } else if (i != 4) {
            this.status = "PlaybackStatus.IDLE";
        } else {
            this.status = "PlaybackStatus.STOPPED";
        }
        this.status.equals("PlaybackStatus.IDLE");
    }


    private void play() {
        this.exoPlayer.setPlayWhenReady(true);
    }


    private void pause() {
        this.exoPlayer.setPlayWhenReady(false);
        this.audioManager.abandonAudioFocus(this);
    }


    private void resume() {
        if (this.streamUrl != null) {
            play();
        }
    }


    private void stop() {
        this.exoPlayer.stop();
        this.audioManager.abandonAudioFocus(this);
    }

    public boolean isPlaying() {
        return this.status.equals("PlaybackStatus.PLAYING");
    }


    public class PlayerBinder extends Binder {
        public PlayerBinder() {
        }

        public PlayerService getService() {
            return PlayerService.this;
        }
    }
}
