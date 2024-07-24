package com.telegram.videoplayer.downloader.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.activitys.DashBoardActivity;
import com.telegram.videoplayer.downloader.activitys.VideoPlayerActivity;
import com.telegram.videoplayer.downloader.model.VideoItem;
import com.telegram.videoplayer.downloader.utildata.PreferenceUtil;
import com.simplemobiletools.commons.helpers.ConstantsKt;

import org.apache.commons.lang3.BooleanUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unchecked", "rawtypes", "ConstantConditions", "EmptyMethod", "UnusedReturnValue"})
public class BackgroundSoundService extends Service {
    public static Bitmap bitVideoThumb;
    public static int currentPosition;
    public static Notification customNotification;
    public static int f421a;
    public static RemoteViews notificationLayout;
    public static NotificationManager notificationmanager;
    public static long notpos;
    public static MediaPlayer player;
    public static List<VideoItem> videoList = new ArrayList();
    private static int seekpos;
    private final BroadcastReceiver shutdownReceiver = new ShutdownReceiver();
    private final String channelId = BooleanUtils.NO;
    Intent intent;

    static int access008() {
        int i = currentPosition;
        currentPosition = i + 1;
        return i;
    }

    static int access010() {
        int i = currentPosition;
        currentPosition = i - 1;
        return i;
    }

    static int access308() {
        int i = f421a;
        f421a = i + 1;
        return i;
    }

    public IBinder onBind(Intent intent2) {
        return null;
    }

    public void onLowMemory() {
    }

    @SuppressWarnings({"deprecation", "ResultOfMethodCallIgnored"})
    public int onStartCommand(Intent intent2, int i, int i2) {
        String str;
        String str2;
        registerBroadcastReceiver();
        this.intent = intent2;
        new VideoItem().setDATA("/storage/emulated/0/Z1/all bindi.mp4");
        videoList = (List) intent2.getSerializableExtra("list");
        currentPosition = intent2.getIntExtra("position", 0);
        seekpos = (int) intent2.getLongExtra("current", 0);
        MediaPlayer create = MediaPlayer.create(this, Uri.parse(videoList.get(currentPosition).getDATA()));
        player = create;
        create.setVolume(100.0f, 100.0f);
        player.seekTo(seekpos);
        player.setOnCompletionListener(mediaPlayer -> {
            if (BackgroundSoundService.currentPosition == BackgroundSoundService.videoList.size() - 1) {
                BackgroundSoundService.player.stop();
                return;
            }
            BackgroundSoundService.access008();
            Log.d("position", "pos" + BackgroundSoundService.currentPosition);
            Log.d("position", "size" + BackgroundSoundService.videoList.size());
            new File(BackgroundSoundService.videoList.get(BackgroundSoundService.currentPosition).getDATA()).getName();
            BackgroundSoundService.player = MediaPlayer.create(BackgroundSoundService.this.getApplicationContext(), Uri.parse(BackgroundSoundService.videoList.get(BackgroundSoundService.currentPosition).getDATA()));
            BackgroundSoundService.player.setVolume(100.0f, 100.0f);
            BackgroundSoundService.player.start();
            BackgroundSoundService.notificationLayout.setTextViewText(R.id.title, new File(BackgroundSoundService.videoList.get(BackgroundSoundService.currentPosition).getDATA()).getName());
            BackgroundSoundService.bitVideoThumb = BackgroundSoundService.this.getBitmapFromPath(BackgroundSoundService.videoList.get(BackgroundSoundService.currentPosition).getDATA());
            BackgroundSoundService.notificationLayout.setImageViewBitmap(R.id.custimage, BackgroundSoundService.bitVideoThumb);
            BackgroundSoundService.notificationmanager.notify(0, BackgroundSoundService.customNotification);
        });
        if (Build.VERSION.SDK_INT < 21) {
            notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            bitVideoThumb = getBitmapFromPath(videoList.get(currentPosition).getDATA());
            notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);
            str = NOTIFICATION_SERVICE;
            Notification build = new NotificationCompat.Builder(this, this.channelId).setSmallIcon(R.drawable.ic_play_circle_filled_black_24dp).setStyle(new NotificationCompat.DecoratedCustomViewStyle()).setCustomContentView(notificationLayout).setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, VideoPlayerActivity.class).putExtra("list", (Serializable) videoList).putExtra("position", currentPosition).putExtra("current", (long) player.getCurrentPosition()), ConstantsKt.LICENSE_SMS_MMS)).setPriority(2).build();
            customNotification = build;
            build.flags = 2;
            customNotification.contentView = notificationLayout;
            customNotification.flags |= 32;
            PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, new Intent(this, playPauseButtonListener.class), 0);
            PendingIntent broadcast2 = PendingIntent.getBroadcast(this, 0, new Intent(this, forwardButtonListener.class), 0);
            PendingIntent broadcast3 = PendingIntent.getBroadcast(this, 0, new Intent(this, backwardButtonListener.class), 0);
            PendingIntent broadcast4 = PendingIntent.getBroadcast(this, 0, new Intent(this, closeButtonListener.class), 0);
            notificationLayout.setOnClickPendingIntent(R.id.imageView3, broadcast);
            notificationLayout.setOnClickPendingIntent(R.id.imageView4, broadcast2);
            notificationLayout.setOnClickPendingIntent(R.id.imageView2, broadcast3);
            notificationLayout.setOnClickPendingIntent(R.id.imageView5, broadcast4);
            notificationLayout.setImageViewResource(R.id.imageView2, R.drawable.previous);
            notificationLayout.setImageViewResource(R.id.imageView3, R.drawable.pause);
            notificationLayout.setImageViewResource(R.id.imageView4, R.drawable.forward);
            notificationLayout.setImageViewResource(R.id.imageView5, R.drawable.ic_close_black_24dp);
            notificationLayout.setTextViewText(R.id.title, new File(videoList.get(currentPosition).getDATA()).getName());
            notificationLayout.setTextColor(R.id.title, -1);
            Log.e("bitmap", String.valueOf(bitVideoThumb));
            notificationLayout.setImageViewBitmap(R.id.custimage, bitVideoThumb);
            notificationmanager.notify(0, customNotification);
        } else {
            str = NOTIFICATION_SERVICE;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            str2 = str;
            notificationmanager = (NotificationManager) getSystemService(str2);
            bitVideoThumb = getBitmapFromPath(videoList.get(currentPosition).getDATA());
            notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);
            Notification build2 = new NotificationCompat.Builder(this, this.channelId).setSmallIcon(R.drawable.ic_play_circle_filled_black_24dp).setStyle(new NotificationCompat.DecoratedCustomViewStyle()).setCustomContentView(notificationLayout).setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, VideoPlayerActivity.class).putExtra("list", (Serializable) videoList).putExtra("position", currentPosition).putExtra("current", (long) player.getCurrentPosition()), ConstantsKt.LICENSE_SMS_MMS)).setPriority(2).build();
            customNotification = build2;
            build2.flags = 2;
            Intent intent3 = new Intent(this, DashBoardActivity.class);
            intent3.putExtra("key", "hello");
            PendingIntent.getActivity(this, 0, intent3, 0);
            customNotification.contentView = notificationLayout;
            customNotification.flags |= 32;
            PendingIntent broadcast5 = PendingIntent.getBroadcast(this, 0, new Intent(this, playPauseButtonListener.class), 0);
            PendingIntent broadcast6 = PendingIntent.getBroadcast(this, 0, new Intent(this, forwardButtonListener.class), 0);
            PendingIntent broadcast7 = PendingIntent.getBroadcast(this, 0, new Intent(this, backwardButtonListener.class), 0);
            PendingIntent broadcast8 = PendingIntent.getBroadcast(this, 0, new Intent(this, closeButtonListener.class), 0);
            notificationLayout.setOnClickPendingIntent(R.id.imageView3, broadcast5);
            notificationLayout.setOnClickPendingIntent(R.id.imageView4, broadcast6);
            notificationLayout.setOnClickPendingIntent(R.id.imageView2, broadcast7);
            notificationLayout.setOnClickPendingIntent(R.id.imageView5, broadcast8);
            notificationLayout.setImageViewResource(R.id.imageView2, R.drawable.previous);
            notificationLayout.setImageViewResource(R.id.imageView3, R.drawable.pause);
            notificationLayout.setImageViewResource(R.id.imageView4, R.drawable.next);
            notificationLayout.setImageViewResource(R.id.imageView5, R.drawable.ic_close_black_24dp);
            notificationLayout.setTextViewText(R.id.title, new File(videoList.get(currentPosition).getDATA()).getName());
            notificationLayout.setTextColor(R.id.title, -16777216);
            Log.e("bitmap", String.valueOf(bitVideoThumb));
            notificationLayout.setImageViewBitmap(R.id.custimage, bitVideoThumb);
            notificationmanager.notify(0, customNotification);
        } else {
            str2 = str;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            notificationmanager = (NotificationManager) getSystemService(str2);
            bitVideoThumb = getBitmapFromPath(videoList.get(currentPosition).getDATA());
            notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);
            Notification build3 = new NotificationCompat.Builder(this, this.channelId).setSmallIcon(R.drawable.ic_play_circle_filled_black_24dp).setStyle(new NotificationCompat.DecoratedCustomViewStyle()).setCustomContentView(notificationLayout).setPriority(2).setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, VideoPlayerActivity.class).putExtra("list", (Serializable) videoList).putExtra("position", currentPosition).putExtra("current", (long) player.getCurrentPosition()), ConstantsKt.LICENSE_SMS_MMS)).setChannelId(this.channelId).build();
            customNotification = build3;
            build3.flags = 2;
            NotificationChannel notificationChannel = new NotificationChannel(this.channelId, "hello", 4);
            customNotification.contentView = notificationLayout;
            customNotification.flags |= 32;
            notificationLayout.setOnClickPendingIntent(R.id.notilayout, PendingIntent.getBroadcast(this, 0, new Intent(this, layoytClickListener.class), 0));
            PendingIntent broadcast9 = PendingIntent.getBroadcast(this, 0, new Intent(this, playPauseButtonListener.class), 0);
            PendingIntent broadcast10 = PendingIntent.getBroadcast(this, 0, new Intent(this, forwardButtonListener.class), 0);
            PendingIntent broadcast11 = PendingIntent.getBroadcast(this, 0, new Intent(this, backwardButtonListener.class), 0);
            PendingIntent broadcast12 = PendingIntent.getBroadcast(this, 0, new Intent(this, closeButtonListener.class), 0);
            notificationLayout.setOnClickPendingIntent(R.id.imageView3, broadcast9);
            notificationLayout.setOnClickPendingIntent(R.id.imageView4, broadcast10);
            notificationLayout.setOnClickPendingIntent(R.id.imageView2, broadcast11);
            notificationLayout.setOnClickPendingIntent(R.id.imageView5, broadcast12);
            notificationLayout.setImageViewResource(R.id.imageView2, R.drawable.previous);
            notificationLayout.setImageViewResource(R.id.imageView3, R.drawable.pause);
            notificationLayout.setImageViewResource(R.id.imageView4, R.drawable.next);
            notificationLayout.setImageViewResource(R.id.imageView5, R.drawable.ic_close_black_24dp);
            notificationLayout.setTextViewText(R.id.title, new File(videoList.get(currentPosition).getDATA()).getName());
            notificationLayout.setTextColor(R.id.title, -16777216);
            Log.e("bitmap", String.valueOf(bitVideoThumb));
            notificationLayout.setImageViewBitmap(R.id.custimage, bitVideoThumb);
            if (Build.VERSION.SDK_INT >= 26) {
                notificationmanager.createNotificationChannel(notificationChannel);
            }
            notificationmanager.notify(0, customNotification);
        }
        player.start();
        return 1;
    }

    public void onCreate() {
        super.onCreate();
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
        registerReceiver(this.shutdownReceiver, intentFilter);
    }


    public Bitmap getBitmapFromPath(String str) {
        return ThumbnailUtils.createVideoThumbnail(str, 1);
    }

    public void onTaskRemoved(Intent intent2) {
        super.onTaskRemoved(intent2);
    }

    public void onDestroy() {
        PreferenceUtil.getInstance(this).saveResumBool(true);
        player.stop();
        player.release();
        customNotification.flags = 2;
        ((NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE)).cancelAll();
    }

    public static class layoytClickListener extends BroadcastReceiver {
        @SuppressLint("WrongConstant")
        public void onReceive(Context context, Intent intent) {
            Intent putExtra = new Intent(context, VideoPlayerActivity.class).putExtra("list", (Serializable) BackgroundSoundService.videoList).putExtra("position", BackgroundSoundService.currentPosition).putExtra("current", (long) BackgroundSoundService.player.getCurrentPosition());
            putExtra.setFlags(268435456);
            context.startActivity(putExtra);
        }
    }

    public static class playPauseButtonListener extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (BackgroundSoundService.f421a % 2 == 0) {
                try {
                    BackgroundSoundService.notpos = BackgroundSoundService.player.getCurrentPosition();
                } catch (Exception unused) {
                    BackgroundSoundService.notpos = 0;
                }
                BackgroundSoundService.player.stop();
                if (Build.VERSION.SDK_INT < 21) {
                    BackgroundSoundService.notificationLayout.setImageViewResource(R.id.imageView3, R.drawable.play);
                } else {
                    BackgroundSoundService.notificationLayout.setImageViewResource(R.id.imageView3, R.drawable.play);
                }
            } else {
                Toast.makeText(context, "stop", Toast.LENGTH_SHORT).show();
                BackgroundSoundService.player.stop();
                BackgroundSoundService.player = MediaPlayer.create(context, Uri.parse(BackgroundSoundService.videoList.get(BackgroundSoundService.currentPosition).getDATA()));
                BackgroundSoundService.player.setVolume(100.0f, 100.0f);
                BackgroundSoundService.player.seekTo((int) BackgroundSoundService.notpos);
                BackgroundSoundService.player.start();
                if (Build.VERSION.SDK_INT < 21) {
                    BackgroundSoundService.notificationLayout.setImageViewResource(R.id.imageView3, R.drawable.pause);
                } else {
                    BackgroundSoundService.notificationLayout.setImageViewResource(R.id.imageView3, R.drawable.pause);
                }
            }
            BackgroundSoundService.access308();
            BackgroundSoundService.notificationmanager.notify(0, BackgroundSoundService.customNotification);
        }
    }

    public static class forwardButtonListener extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (BackgroundSoundService.currentPosition < BackgroundSoundService.videoList.size() - 1) {
                Toast.makeText(context, "forwar", Toast.LENGTH_SHORT).show();
                BackgroundSoundService.player.stop();
                BackgroundSoundService.f421a = 0;
                if (Build.VERSION.SDK_INT < 21) {
                    BackgroundSoundService.notificationLayout.setImageViewResource(R.id.imageView3, R.drawable.pause);
                } else {
                    BackgroundSoundService.notificationLayout.setImageViewResource(R.id.imageView3, R.drawable.pause);
                }
                BackgroundSoundService.access008();
                BackgroundSoundService.player = MediaPlayer.create(context, Uri.parse(BackgroundSoundService.videoList.get(BackgroundSoundService.currentPosition).getDATA()));
                BackgroundSoundService.player.setVolume(100.0f, 100.0f);
                BackgroundSoundService.player.start();
                BackgroundSoundService.notificationLayout.setTextViewText(R.id.title, new File(BackgroundSoundService.videoList.get(BackgroundSoundService.currentPosition).getDATA()).getName());
                BackgroundSoundService.bitVideoThumb = getBitmapFromPath(BackgroundSoundService.videoList.get(BackgroundSoundService.currentPosition).getDATA());
                BackgroundSoundService.notificationLayout.setImageViewBitmap(R.id.custimage, BackgroundSoundService.bitVideoThumb);
                BackgroundSoundService.notificationmanager.notify(0, BackgroundSoundService.customNotification);
            }
        }

        public Bitmap getBitmapFromPath(String str) {
            return ThumbnailUtils.createVideoThumbnail(str, 1);
        }
    }

    public static class backwardButtonListener extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (BackgroundSoundService.currentPosition > 0) {
                BackgroundSoundService.player.stop();
                BackgroundSoundService.f421a = 0;
                if (Build.VERSION.SDK_INT < 21) {
                    BackgroundSoundService.notificationLayout.setImageViewResource(R.id.imageView3, R.drawable.pause);
                } else {
                    BackgroundSoundService.notificationLayout.setImageViewResource(R.id.imageView3, R.drawable.pause);
                }
                BackgroundSoundService.access010();
                BackgroundSoundService.player = MediaPlayer.create(context, Uri.parse(BackgroundSoundService.videoList.get(BackgroundSoundService.currentPosition).getDATA()));
                BackgroundSoundService.player.setVolume(100.0f, 100.0f);
                BackgroundSoundService.player.start();
                BackgroundSoundService.notificationLayout.setTextViewText(R.id.title, new File(BackgroundSoundService.videoList.get(BackgroundSoundService.currentPosition).getDATA()).getName());
                BackgroundSoundService.bitVideoThumb = getBitmapFromPath(BackgroundSoundService.videoList.get(BackgroundSoundService.currentPosition).getDATA());
                BackgroundSoundService.notificationLayout.setImageViewBitmap(R.id.custimage, BackgroundSoundService.bitVideoThumb);
                BackgroundSoundService.notificationmanager.notify(0, BackgroundSoundService.customNotification);
            }
        }

        public Bitmap getBitmapFromPath(String str) {
            return ThumbnailUtils.createVideoThumbnail(str, 1);
        }
    }

    public static class closeButtonListener extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            ((NotificationManager) context.getSystemService(NOTIFICATION_SERVICE)).cancelAll();
            context.stopService(new Intent(context, BackgroundSoundService.class));
        }
    }
}
