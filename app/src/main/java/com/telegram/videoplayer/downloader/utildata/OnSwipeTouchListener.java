package com.telegram.videoplayer.downloader.utildata;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.equalizer.VerticalSeekBar;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.MimeTypes;

import java.io.Console;
import java.util.concurrent.TimeUnit;


@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "ConstantConditions"})
public class OnSwipeTouchListener implements View.OnTouchListener {
    private final Context context;
    private final GestureDetector gestureDetector;
    private final SimpleExoPlayer player;
    private final PlayerView playerView;
    private final int mTouchSlop;
    private AudioManager audioManager;
    private float mCurBrightness;
    private int mCurVolume;
    private float mDecidedX = -1.0f;
    private float mDecidedY = -1.0f;
    private float mDiffTime = -1.0f;
    private float mFinalTime = -1.0f;
    private float mInitialX = -1.0f;
    private float mInitialY = -1.0f;
    private int mMaxBrightness;
    private int mMaxVolume;
    private int mTouchFlag;

    public OnSwipeTouchListener(Context context2, SimpleExoPlayer simpleExoPlayer, PlayerView playerView2, AudioManager audioManager2) {
        this.gestureDetector = new GestureDetector(context2, new GestureListener());
        this.player = simpleExoPlayer;
        this.playerView = playerView2;
        this.audioManager = audioManager2;
        this.context = context2;
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context2);
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        setGestureListener();
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        float f;
        float f2;
        float f3;
        if (PreferenceUtil.getInstance(this.context).getLock()) {
            return false;
        }
        int action = motionEvent.getAction() & 255;
        if (action == 0) {
            this.mInitialX = motionEvent.getX();
            this.mInitialY = motionEvent.getY();
            this.mTouchFlag = -1;
        } else if (action == 1) {
            this.mTouchFlag = -1;
            this.mCurVolume = -1;
            this.mCurBrightness = -1.0f;
            FrameLayout frameLayout = ((Activity) this.context).findViewById(R.id.vadfrm);
            frameLayout.setVisibility(View.GONE);
            this.player.setPlayWhenReady(true);
            frameLayout.setVisibility(View.GONE);
            ((Activity) this.context).findViewById(R.id.volum_ll).setVisibility(View.GONE);
            ((Activity) this.context).findViewById(R.id.text).setVisibility(View.GONE);
            ((Activity) this.context).findViewById(R.id.bright_ll).setVisibility(View.GONE);
        } else if (action == 2) {
            if (this.mTouchFlag == -1) {
                f = motionEvent.getX() - this.mInitialX;
                f3 = motionEvent.getY();
                f2 = this.mInitialY;
            } else {
                f = motionEvent.getX() - this.mDecidedX;
                f3 = motionEvent.getY();
                f2 = this.mDecidedY;
            }
            float f4 = f3 - f2;
            if (this.mTouchFlag == -1 && Math.abs(f) > 100.0f) {
                this.mTouchFlag = 0;
                this.mDecidedX = motionEvent.getX();
                this.mDecidedY = motionEvent.getY();
            } else if (this.mTouchFlag == -1 && Math.abs(f4) > 100.0f) {
                this.mDecidedX = motionEvent.getX();
                this.mDecidedY = motionEvent.getY();
                if (motionEvent.getRawX() >= ((float) (this.playerView.getRootView().getWidth() / 2))) {
                    this.mTouchFlag = 1;
                    this.mCurVolume = this.audioManager.getStreamVolume(3);
                }
                if (motionEvent.getRawX() < ((float) (this.playerView.getRootView().getWidth() / 2))) {
                    this.mTouchFlag = 2;
                    this.mMaxBrightness = 100;
                    this.mCurBrightness = ((Activity) this.context).getWindow().getAttributes().screenBrightness * 100.0f;
                }
            }
            int i = this.mTouchFlag;
            if (i == 0) {
                FrameLayout frameLayout2 = ((Activity) this.context).findViewById(R.id.vadfrm);
                frameLayout2.setVisibility(View.GONE);
                this.player.setPlayWhenReady(false);
                frameLayout2.setVisibility(View.GONE);
                int i2 = this.mTouchSlop;
                if (f > ((float) i2) || f >= ((float) (-i2))) {
                    seekPlayer(f, 1);
                    this.mDecidedX = motionEvent.getX() - ((float) (this.mTouchSlop / 2));
                } else {
                    seekPlayer(-f, 2);
                    this.mDecidedX = motionEvent.getX() + ((float) (this.mTouchSlop / 2));
                }
            } else if (i == 1 || i == 2 || motionEvent.getPointerCount() != 2 || this.mTouchFlag != 3) {
                this.mFinalTime = -1.0f;
                int i3 = this.mTouchFlag;
                if (i3 == 1) {
                    if (f4 > 0.0f) {
                        updateVolume(f4, 4);
                    } else {
                        updateVolume(-f4, 3);
                    }
                } else if (i3 == 2) {
                    if (f4 > 0.0f) {
                        updateBrightness(f4, 4);
                    } else {
                        updateBrightness(-f4, 3);
                    }
                }
            }
        }
        return this.gestureDetector.onTouchEvent(motionEvent);
    }


    private void seekPlayer(float f, int i) {
        if (this.player.getDuration() <= 60) {
            this.mDiffTime = (((float) this.player.getDuration()) * f) / ScreenUtility.getDeviceWidth(this.context, ScreenUtility.PIXEL);
        } else {
            this.mDiffTime = (f * 80000.0f) / ScreenUtility.getDeviceWidth(this.context, ScreenUtility.PIXEL);
        }
        if (i == 2) {
            this.mDiffTime *= -1.0f;
        }
        float currentPosition = this.mDiffTime + ((float) this.player.getCurrentPosition());
        this.mFinalTime = currentPosition;
        if (currentPosition < 0.0f) {
            this.mFinalTime = 0.0f;
        } else if (currentPosition > ((float) this.player.getDuration())) {
            this.mFinalTime = (float) this.player.getDuration();
        }
        this.player.seekTo((int) this.mFinalTime);
        TextView textView = ((Activity) this.context).findViewById(R.id.text);
        textView.setText(duration(this.player.getCurrentPosition()));
        textView.setVisibility(View.VISIBLE);
    }


    private void updateBrightness(float f, int i) {
        LinearLayout linearLayout = ((Activity) this.context).findViewById(R.id.bright_ll);
        TextView textView = ((Activity) this.context).findViewById(R.id.text);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        VerticalSeekBar verticalSeekBar = ((Activity) this.context).findViewById(R.id.volumeseek);
        verticalSeekBar.setMax(15);
        float width = (((float) this.mMaxBrightness) * f) / ((float) (this.playerView.getRootView().getWidth() / 2));
        if (i == 4) {
            width *= -1.0f;
        }
        int i2 = (int) (this.mCurBrightness + width);
        if (i2 < 0) {
            i2 = 0;
        } else {
            int i3 = this.mMaxBrightness;
            if (i2 > i3) {
                i2 = i3;
            }
        }
        WindowManager.LayoutParams attributes = ((Activity) this.context).getWindow().getAttributes();
        try {
            attributes.screenBrightness = ((float) i2) / 100.0f;
            ((Activity) this.context).getWindow().setAttributes(attributes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        textView.setText("" + ((int) (attributes.screenBrightness * 15.0f)));
        textView.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        verticalSeekBar.setProgress((int) (attributes.screenBrightness * 15.0f));
    }

    private void setGestureListener() {
        @SuppressLint("WrongConstant") AudioManager audioManager2 = (AudioManager) this.context.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        this.audioManager = audioManager2;
        this.mMaxVolume = audioManager2.getStreamMaxVolume(3);
    }

    private void updateVolume(float f, int i) {
        LinearLayout linearLayout = ((Activity) this.context).findViewById(R.id.volum_ll);
        TextView textView = ((Activity) this.context).findViewById(R.id.text);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        VerticalSeekBar verticalSeekBar = ((Activity) this.context).findViewById(R.id.brightseek);
        verticalSeekBar.setMax(this.mMaxVolume);
        float width = (((float) this.mMaxVolume) * f) / ((float) (this.playerView.getRootView().getWidth() / 2));
        if (i == 4) {
            width = -width;
        }
        int i2 = ((int) width) + this.mCurVolume;
        if (i2 < 0) {
            i2 = 0;
        } else {
            int i3 = this.mMaxVolume;
            if (i2 > i3) {
                i2 = i3;
            }
        }
        try {
            this.audioManager.setStreamVolume(3, i2, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int i4 = i2 * 15;
        String sb = "" +
                i4 / this.mMaxVolume;
        textView.setText(sb);
        textView.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        verticalSeekBar.setProgress(i4 / this.mMaxVolume);

        Log.d("TAG22", "updateVolume: " + i4 / this.mMaxVolume + " " + i4 + " " + this.mMaxVolume + " " + i);
    }


    private String duration(Long l) {
        long hours = TimeUnit.MILLISECONDS.toHours(l);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(l));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l));
        if (hours >= 1) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }

    static class GestureListener extends GestureDetector.SimpleOnGestureListener {

        GestureListener() {
        }

        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            try {
                motionEvent2.getY();
                motionEvent.getY();
                motionEvent2.getX();
                motionEvent.getX();
                motionEvent.getX();
                motionEvent.getY();
                motionEvent2.getY();
                motionEvent2.getX();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        }
    }
}
