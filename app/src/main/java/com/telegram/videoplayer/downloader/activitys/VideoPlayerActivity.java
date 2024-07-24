package com.telegram.videoplayer.downloader.activitys;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.media.audiofx.PresetReverb;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.telegram.videoplayer.downloader.Adshandler;
import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.adapter.PlayListAdapter;
import com.telegram.videoplayer.downloader.equalizer.EqualizerDataList;
import com.telegram.videoplayer.downloader.equalizer.VerticalSeekBar;
import com.telegram.videoplayer.downloader.equalizer.eq.BassBoosts;
import com.telegram.videoplayer.downloader.equalizer.eq.Equalizers;
import com.telegram.videoplayer.downloader.equalizer.eq.Loud;
import com.telegram.videoplayer.downloader.equalizer.eq.Virtualizers;
import com.telegram.videoplayer.downloader.filemanager.FilePicker;
import com.telegram.videoplayer.downloader.model.PrivateItem;
import com.telegram.videoplayer.downloader.model.VideoItem;
import com.telegram.videoplayer.downloader.service.BackgroundSoundService;
import com.telegram.videoplayer.downloader.service.MyBroadcastReceiver;
import com.telegram.videoplayer.downloader.service.MyService;
import com.telegram.videoplayer.downloader.service.PlayerService;
import com.telegram.videoplayer.downloader.service.floating;
import com.telegram.videoplayer.downloader.trackSelectionDialog.TrackSelectionDialog;
import com.telegram.videoplayer.downloader.utildata.OnSwipeTouchListener;
import com.telegram.videoplayer.downloader.utildata.PreferenceUtil;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unchecked", "PointlessBooleanExpression", "rawtypes", "ConstantConditions", "ResultOfMethodCallIgnored", "SameReturnValue", "NullableProblems", "SuspiciousNameCombination"})
public class VideoPlayerActivity extends AppCompatActivity {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private static ArrayList<EqualizerDataList> equalizerListData = null;
    private static MediaSource videoSource;
    private final VerticalSeekBar[] seekBarFinal = new VerticalSeekBar[5];
    ImageView adclosebtn;
    CardView adfrm;
    TextView batteryTxt;
    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra(PreferenceUtil.BAND_LEVEL, 0);
            TextView textView = VideoPlayerActivity.this.batteryTxt;
            textView.setText(intExtra + "%");
        }
    };
    DataSource.Factory dataSourceFactory;
    LinearLayout frembatery;
    MergingMediaSource mergedSource;
    int seekArcProgress;
    boolean subbool = false;
    CheckBox subcheck;
    Format subtitleFormat;
    MediaSource subtitleSource;
    DefaultTrackSelector trackSelector;
    FrameLayout vadfrm;
    MediaSource[] videoSources;
    private boolean adbool = true;
    private WindowManager.LayoutParams attributes;
    private AudioManager audioManager;
    private ImageButton back;
    private Boolean batterseen = false;
    private BottomSheetDialog bottomSheetDialog;
    private ImageButton brightness;
    private float brightnessv;
    private boolean checktrue = false;
    private ConstraintLayout controlLayout;
    private ImageView crop;
    private long current;
    private boolean db = true;
    private boolean defualt = false;
    private boolean dislis = false;
    private TextView dspeed;
    private ImageButton imgMute;
    private ImageButton equalizer;
    private ImageButton imgMore;
    private boolean isAsk = false;
    private boolean isOrientation = true;
    private boolean isShowingTrackSelectionDialog = false;
    private boolean isloaded = false;
    private TrackGroupArray lastSeenTrackGroupArray;
    private List<VideoItem> list;
    private ImageButton lock;
    private Boolean lockstatus = false;
    private PictureInPictureParams.Builder mPictureInPictureParamsBuilder;
    private MediaMetadataRetriever mediaMetadataRetriever;
    private MyAdapter myAdapter;
    private int orientation;
    private PlaybackParameters parameters;
    private SimpleExoPlayer player;
    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            VideoPlayerActivity.this.player.stop();
        }
    };
    private PlayerView playerView;
    private ImageButton playlist;
    private ImageButton popup;
    private int position;
    private PresetReverb presetReverb;
    private TextView pspeed;
    private ImageButton repeat;
    private Boolean repeatstatus = false;
    private int reverbSetting = 0;
    private ImageButton rotate;
    private RecyclerView rvEqualizer;
    private VerticalSeekBar seekBar;
    private PlayerService service;
    private boolean serviceBound;
    private ServiceConnection serviceConnection = new ServiceConnection() {


        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            VideoPlayerActivity.this.serviceBound = true;
            Log.e("Service Started ", " : ");
        }

        public void onServiceDisconnected(ComponentName componentName) {
            VideoPlayerActivity.this.serviceBound = false;
            Log.e("Service Started ", " : " + VideoPlayerActivity.this.serviceBound);
        }
    };
    private ImageButton share;
    private ConstraintLayout sheetmain;
    private float speedv;
    private ImageButton subtitle;
    private Boolean swdecoder = false;
    private ImageButton takePicture;
    private TextView textView;
    private TextView title;
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    private TextView tvolume;
    private ImageButton unlock;
    private int view = 0;
    private ImageView volume;
    private SeekBar vseekBar;
    private int width;

    private boolean isMute = false;


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressWarnings("deprecation")
    @SuppressLint({"WrongConstant", "ResourceType", "SuspiciousIndentation", "MissingInflatedId"})
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_videoplayer);
        getWindow().addFlags(128);
        this.mediaMetadataRetriever = new MediaMetadataRetriever();
        registerReceiver(this.broadcastReceiver, new IntentFilter("Stop_play_video"), Context.RECEIVER_NOT_EXPORTED);
        this.controlLayout = findViewById(R.id.control_layout);
        this.batteryTxt = findViewById(R.id.batteryTxt);
        this.frembatery = findViewById(R.id.frembatery);
        this.adclosebtn = findViewById(R.id.adclosebtn);
        this.adfrm = findViewById(R.id.adfrm);
        this.vadfrm = findViewById(R.id.vadfrm);
        this.adclosebtn.setOnClickListener(view -> {
            VideoPlayerActivity.this.vadfrm.setVisibility(View.GONE);
            VideoPlayerActivity.this.adbool = false;
            VideoPlayerActivity.this.isloaded = false;
        });
        this.vadfrm.setVisibility(View.GONE);
        this.batterseen = PreferenceUtil.getInstance(this).getBatterylock();
        if (Build.VERSION.SDK_INT >= 26) {
            this.mPictureInPictureParamsBuilder = new PictureInPictureParams.Builder();
        }
        registerReceiver(this.mBatInfoReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getRealMetrics(displayMetrics);

        // Set video Speed
        PreferenceUtil.getInstance(getApplicationContext()).saveLastSpeed(1f);


        this.brightnessv = PreferenceUtil.getInstance(this).getLastBrightness();
        this.audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        this.speedv = PreferenceUtil.getInstance(this).getLastSpeed();
        this.orientation = PreferenceUtil.getInstance(getApplicationContext()).getOrientation();
        this.playerView = findViewById(R.id.player_view);
        this.position = getIntent().getIntExtra("position", 0);
        this.list = (List) getIntent().getSerializableExtra("list");
        this.current = getIntent().getLongExtra("current", 0);
        this.trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory());
        DefaultTrackSelector.Parameters build = new DefaultTrackSelector.ParametersBuilder().build();
        this.trackSelectorParameters = build;
        this.trackSelector.setParameters(build);
        this.player = ExoPlayerFactory.newSimpleInstance(this, this.trackSelector);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
        stopService(new Intent(this, BackgroundSoundService.class));
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getPackageName()));
        this.dataSourceFactory = defaultDataSourceFactory;
        final List<VideoItem> list2 = this.list;
        if (list2 != null) {
            this.videoSources = new MediaSource[list2.size()];
            for (int i = 0; i < this.list.size(); i++) {
                this.videoSources[i] = new ExtractorMediaSource.Factory(this.dataSourceFactory).createMediaSource(Uri.parse(this.list.get(i).data));
            }
            MediaSource[] mediaSourceArr = this.videoSources;
            videoSource = mediaSourceArr.length == 1 ? mediaSourceArr[0] : new ConcatenatingMediaSource(mediaSourceArr);
        } else {
            videoSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse(getIntent().getDataString()));
        }
        WindowManager.LayoutParams attributes2 = getWindow().getAttributes();
        this.attributes = attributes2;
        attributes2.screenBrightness = this.brightnessv;
        getWindow().setAttributes(this.attributes);
        PlaybackParameters playbackParameters = new PlaybackParameters(this.speedv);
        this.parameters = playbackParameters;
        this.player.setPlaybackParameters(playbackParameters);
        this.playerView.setPlayer(this.player);
        this.player.prepare(videoSource);
        this.player.seekTo(this.position, this.current);
        this.player.setPlayWhenReady(false);
        int resumestatus = PreferenceUtil.getInstance(this).getResumestatus();
        int currentWindowIndex = this.player.getCurrentWindowIndex();
        long j = 0;
        try {
            j = PreferenceUtil.getInstance(this).getresumeVideotime(this.list.get(currentWindowIndex).getDISPLAY_NAME());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("kmmjjenndi", currentWindowIndex + " onCreate:r rst= " + resumestatus + " time= " + j);
        if (!PreferenceUtil.getInstance(this).getResumBool()) {
            this.player.setPlayWhenReady(true);
            this.player.seekTo(this.position, this.current);
            PreferenceUtil.getInstance().saveResumBool(true);
        } else if (resumestatus == 0) {
            this.player.setPlayWhenReady(true);
            this.player.seekTo(this.position, j);
        } else if (resumestatus == 1) {
            this.player.setPlayWhenReady(true);
            this.player.seekTo(this.position, this.current);
        } else {
            try {
                resumeAskDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.playerView.setControllerVisibilityListener(i -> {
            if (VideoPlayerActivity.this.batterseen) {
                if (i == 0) {
                    VideoPlayerActivity.this.frembatery.setVisibility(View.GONE);
                } else {
                    VideoPlayerActivity.this.frembatery.setVisibility(View.VISIBLE);
                }
            }
        });
        this.playerView.setOnClickListener(new DoubleClick(new DoubleClickListener() {

            @Override
            public void onSingleClick(View view) {
            }

            @Override
            public void onDoubleClick(View view) {
                if (VideoPlayerActivity.this.db) {
                    VideoPlayerActivity.this.player.setPlayWhenReady(false);
                    VideoPlayerActivity.this.db = false;
                    return;
                }
                VideoPlayerActivity.this.player.setPlayWhenReady(true);
                VideoPlayerActivity.this.db = true;
            }
        }));
        this.imgMute = findViewById(R.id.imgMute);
        this.equalizer = findViewById(R.id.equalizer);
        this.rotate = findViewById(R.id.rotate);
        this.lock = findViewById(R.id.lock);
        this.unlock = findViewById(R.id.unlock);
        this.crop = findViewById(R.id.exo_crop);
        this.back = findViewById(R.id.back);
        this.share = findViewById(R.id.share);
        this.imgMore = findViewById(R.id.imgMore);
        this.takePicture = findViewById(R.id.takePicture);
        this.brightness = findViewById(R.id.brightness);
        this.subtitle = findViewById(R.id.subtitle);
        this.volume = findViewById(R.id.exo_volume);
        this.pspeed = findViewById(R.id.pspeed);
        this.repeat = findViewById(R.id.repeat);
        this.popup = findViewById(R.id.popup);
        this.playlist = findViewById(R.id.playlist);
        this.sheetmain = findViewById(R.id.sheetmain);
        this.bottomSheetDialog = new BottomSheetDialog(this);
        getLayoutInflater().inflate(R.layout.playlist_sheet, null);
        RecyclerView recyclerView = findViewById(R.id.r1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(0);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.getLayoutManager().scrollToPosition(this.position);
        if(this.list.size()>0) {
            recyclerView.setAdapter(new PlayListAdapter(this, this.list, this.player));
        }
        this.pspeed.setText(String.format("%sX", this.speedv));
        this.back.setOnClickListener(view -> VideoPlayerActivity.this.onBackPressed());
        this.subtitle.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayerActivity.this);
            View inflate = LayoutInflater.from(VideoPlayerActivity.this).inflate(R.layout.subtitle_dialog, null);
            TextView textView = inflate.findViewById(R.id.txtonline);
            VideoPlayerActivity.this.subcheck = inflate.findViewById(R.id.none_check);
            builder.setView(inflate);
            final AlertDialog create = builder.create();
            WindowManager.LayoutParams attributes = create.getWindow().getAttributes();
            attributes.gravity = BadgeDrawable.TOP_START;
            attributes.x = view.getLeft();
            attributes.y = view.getTop();
            VideoPlayerActivity.this.subcheck.setChecked(VideoPlayerActivity.this.subbool);
            inflate.findViewById(R.id.txtoffline).setOnClickListener(view123 -> {
                create.dismiss();
                ArrayList arrayList = new ArrayList();
                arrayList.add(".srt");
                Adshandler.showAd(VideoPlayerActivity.this, () -> {
                    Intent intent = new Intent(VideoPlayerActivity.this, FilePicker.class);
                    intent.putExtra(FilePicker.EXTRA_ACCEPTED_FILE_EXTENSIONS, arrayList);
                    VideoPlayerActivity.this.startActivityForResult(intent, 25);
                });
            });
            inflate.findViewById(R.id.txtnone).setOnClickListener(view122 -> {
                create.dismiss();
                if (VideoPlayerActivity.this.subbool) {
                    if (VideoPlayerActivity.this.mergedSource != null) {
                        VideoPlayerActivity.this.player.prepare(VideoPlayerActivity.this.mergedSource, false, false);
                        VideoPlayerActivity.this.player.setPlayWhenReady(true);
                    }
                    VideoPlayerActivity.this.subbool = false;
                    return;
                }
                VideoPlayerActivity.this.player.prepare(VideoPlayerActivity.videoSource, false, false);
                VideoPlayerActivity.this.player.setPlayWhenReady(true);
                VideoPlayerActivity.this.subbool = true;
            });
            create.show();
        });
        this.imgMore.setOnClickListener(view -> {
            view.getLeft();
            view.getWidth();
            view.getTop();
            view.getHeight();
            AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayerActivity.this, R.style.CustomDialog);
            View inflate = LayoutInflater.from(VideoPlayerActivity.this).inflate(R.layout.more_dialog, null);
            final CheckBox checkBox = inflate.findViewById(R.id.cbRepeatOne);
            checkBox.setChecked(VideoPlayerActivity.this.repeatstatus);
            builder.setView(inflate);
            final AlertDialog create = builder.create();
            WindowManager.LayoutParams attributes = create.getWindow().getAttributes();
            attributes.gravity = BadgeDrawable.TOP_START;
            attributes.x = view.getLeft();
            attributes.y = view.getTop();
            inflate.findViewById(R.id.txtother).setOnClickListener(view121 -> {
                create.dismiss();
                AlertDialog.Builder builder12 = new AlertDialog.Builder(VideoPlayerActivity.this, R.style.CustomDialog);
                View inflate12 = LayoutInflater.from(VideoPlayerActivity.this).inflate(R.layout.other_dialog, null);
                builder12.setView(inflate12);
                final CheckBox checkBox1 = inflate12.findViewById(R.id.swcheckOne);
                final AlertDialog create12 = builder12.create();
                WindowManager.LayoutParams attributes1 = create12.getWindow().getAttributes();
                attributes1.gravity = BadgeDrawable.TOP_END;
                attributes1.x = inflate12.getLeft();
                attributes1.y = inflate12.getTop();
                checkBox1.setChecked(VideoPlayerActivity.this.swdecoder);
                inflate12.findViewById(R.id.txtproperty).setOnClickListener(view120 -> {
                    create12.dismiss();
                    VideoPlayerActivity.this.propertiDialog();
                });
                inflate12.findViewById(R.id.txtrename).setOnClickListener(view119 -> {
                    create12.dismiss();
                    VideoPlayerActivity.this.renameVideofile();
                });
                inflate12.findViewById(R.id.txtlock).setOnClickListener(view118 -> VideoPlayerActivity.this.lockVideofile());
                inflate12.findViewById(R.id.txtdelete).setOnClickListener(view117 -> {
                    VideoPlayerActivity.this.player.setPlayWhenReady(false);
                    VideoPlayerActivity.this.deleteVideo();
                });
                checkBox1.setOnClickListener(view116 -> {
                    if (VideoPlayerActivity.this.swdecoder) {
                        checkBox1.setChecked(false);
                    }
                    checkBox1.setChecked(true);
                    VideoPlayerActivity.this.swdecoder = true ^ VideoPlayerActivity.this.swdecoder;
                    create12.dismiss();
                });
                create12.show();
            });
            inflate.findViewById(R.id.txtAudioTrack).setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    create.dismiss();
                    if (!VideoPlayerActivity.this.isShowingTrackSelectionDialog && TrackSelectionDialog.willHaveContent(VideoPlayerActivity.this.trackSelector)) {
                        TrackSelectionDialog.createForTrackSelector(VideoPlayerActivity.this.trackSelector, DialogInterface::dismiss).show(VideoPlayerActivity.this.getSupportFragmentManager(), null);
                    }
                }

                public void lambda$onClick$0$VideoPlayerActivity$7$2() {
                    VideoPlayerActivity.this.isShowingTrackSelectionDialog = false;
                }
            });
            inflate.findViewById(R.id.txtbackground).setOnClickListener(view115 -> {
                create.dismiss();
                int currentWindowIndex1 = VideoPlayerActivity.this.player.getCurrentWindowIndex();
                long currentPosition = VideoPlayerActivity.this.player.getCurrentPosition();
                Intent intent = new Intent(VideoPlayerActivity.this, BackgroundSoundService.class);
                intent.putExtra("list", (Serializable) VideoPlayerActivity.this.list);
                intent.putExtra("position", currentWindowIndex1);
                intent.putExtra("current", currentPosition);
                VideoPlayerActivity.this.startService(intent);
                VideoPlayerActivity.this.player.setPlayWhenReady(false);
                PreferenceUtil.getInstance(VideoPlayerActivity.this).saveResumBool(false);
                VideoPlayerActivity.this.finish();
            });
            inflate.findViewById(R.id.txtSetting).setOnClickListener(view114 -> {
                create.dismiss();
                Adshandler.showAd(VideoPlayerActivity.this, () -> {
                    VideoPlayerActivity.this.isOrientation = true;
                    VideoPlayerActivity.this.startActivity(new Intent(VideoPlayerActivity.this, SettingsActivity.class));
                });
            });
            inflate.findViewById(R.id.txtShareVideo).setOnClickListener(view113 -> {
                try {
                    create.dismiss();
                    Context applicationContext = VideoPlayerActivity.this.getApplicationContext();
                    Uri uriForFile = FileProvider.getUriForFile(applicationContext, VideoPlayerActivity.this.getPackageName() + ".provider", new File(VideoPlayerActivity.this.list.get(VideoPlayerActivity.this.position).getDATA()));
                    VideoPlayerActivity.this.player.setPlayWhenReady(false);
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("video/*");
                    intent.putExtra("android.intent.extra.STREAM", uriForFile);
                    intent.putExtra("android.intent.extra.TEXT", VideoPlayerActivity.this.list.get(VideoPlayerActivity.this.position).getDISPLAY_NAME());
                    intent.putExtra("android.intent.extra.SUBJECT", VideoPlayerActivity.this.list.get(VideoPlayerActivity.this.position).getDISPLAY_NAME());
                    VideoPlayerActivity.this.startActivity(Intent.createChooser(intent, "Share Video"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            checkBox.setOnClickListener(view112 -> {
                if (VideoPlayerActivity.this.repeatstatus) {
                    checkBox.setChecked(false);
                    VideoPlayerActivity.this.repeat.setImageResource(R.drawable.repeat);
                    VideoPlayerActivity.this.player.setRepeatMode(0);
                    VideoPlayerActivity.this.repeatstatus = true ^ VideoPlayerActivity.this.repeatstatus;
                    return;
                }
                checkBox.setChecked(true);
                VideoPlayerActivity.this.repeat.setImageResource(R.drawable.repeatone);
                VideoPlayerActivity.this.player.setRepeatMode(1);
                VideoPlayerActivity.this.repeatstatus = true ^ VideoPlayerActivity.this.repeatstatus;
            });
            inflate.findViewById(R.id.txtSleepTimer).setOnClickListener(view111 -> {
                create.dismiss();
                if (VideoPlayerActivity.this.isPlaying()) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(VideoPlayerActivity.this, R.style.CustomDialog);
                    View inflate1 = LayoutInflater.from(VideoPlayerActivity.this).inflate(R.layout.dialog_sleep_timer, null);
                    SeekBar seekBar = inflate1.findViewById(R.id.seek_arc);
                    TextView textView = inflate1.findViewById(R.id.txtTitle);
                    final TextView textView2 = inflate1.findViewById(R.id.timer_display);
                    TextView textView3 = inflate1.findViewById(R.id.timer_cancel);
                    builder1.setView(inflate1);
                    final AlertDialog create1 = builder1.create();
                    VideoPlayerActivity.this.seekArcProgress = PreferenceUtil.getInstance(VideoPlayerActivity.this.getApplicationContext()).getLastSleepTimerValue();
                    textView2.setText("Stop Playing After: " + VideoPlayerActivity.this.seekArcProgress + " minute(s)");
                    seekBar.setProgress(VideoPlayerActivity.this.seekArcProgress);
                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                            if (i < 1) {
                                seekBar.setProgress(1);
                                return;
                            }
                            VideoPlayerActivity.this.seekArcProgress = i;
                            textView2.setText("Stop Playing After: " + VideoPlayerActivity.this.seekArcProgress + " minute(s)");
                        }

                        public void onStopTrackingTouch(SeekBar seekBar) {
                            PreferenceUtil.getInstance(VideoPlayerActivity.this.getApplicationContext()).setLastSleepTimerValue(VideoPlayerActivity.this.seekArcProgress);
                        }
                    });
                    inflate1.findViewById(R.id.txtSave).setOnClickListener(view110 -> {
                        int i = VideoPlayerActivity.this.seekArcProgress;
                        long j12 = (long) i * 60 * 1000;
                        Log.e("nextSleepTimer", " : " + j12);
                        MyService.millisecs = j12;
                        ((AlarmManager) VideoPlayerActivity.this.getApplicationContext().getSystemService(NotificationCompat.CATEGORY_ALARM)).set(0, System.currentTimeMillis() + j12, PendingIntent.getBroadcast(VideoPlayerActivity.this.getApplicationContext(), 234324243, new Intent(VideoPlayerActivity.this.getApplicationContext(), MyBroadcastReceiver.class), 0));
                        Toast.makeText(VideoPlayerActivity.this.getApplicationContext(), VideoPlayerActivity.this.getApplicationContext().getResources().getString(R.string.sleep_timer_set, i), Toast.LENGTH_SHORT).show();
                        create1.dismiss();
                    });
                    inflate1.findViewById(R.id.txtCancel).setOnClickListener(view19 -> create1.dismiss());
                    inflate1.findViewById(R.id.txtDisable).setOnClickListener(view18 -> {
                        PendingIntent broadcast = PendingIntent.getBroadcast(VideoPlayerActivity.this.getApplicationContext(), 234324243, new Intent(VideoPlayerActivity.this.getApplicationContext(), MyBroadcastReceiver.class), 0);
                        if (broadcast != null) {
                            ((AlarmManager) VideoPlayerActivity.this.getApplicationContext().getSystemService(NotificationCompat.CATEGORY_ALARM)).cancel(broadcast);
                            broadcast.cancel();
                            Toast.makeText(VideoPlayerActivity.this.getApplicationContext(), VideoPlayerActivity.this.getApplicationContext().getResources().getString(R.string.sleep_timer_canceled), Toast.LENGTH_SHORT).show();
                        }
                        create1.dismiss();
                    });
                    create1.show();
                    return;
                }
                AlertDialog.Builder builder2 = new AlertDialog.Builder(VideoPlayerActivity.this, R.style.CustomDialog);
                View inflate2 = LayoutInflater.from(VideoPlayerActivity.this).inflate(R.layout.dialog_sleep_timer_off, null);
                TextView textView4 = inflate2.findViewById(R.id.txtTitle);
                TextView textView5 = inflate2.findViewById(R.id.timer_display);
                builder2.setView(inflate2);
                final AlertDialog create2 = builder2.create();
                inflate2.findViewById(R.id.txtCancel).setOnClickListener(view17 -> create2.dismiss());
                create2.show();
            });
            create.show();
        });
        this.share.setOnClickListener(view -> {
            VideoPlayerActivity.this.player.setPlayWhenReady(false);
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            intent.putExtra("android.intent.extra.STREAM", Uri.parse(VideoPlayerActivity.this.list.get(VideoPlayerActivity.this.position).getDATA()));
            intent.putExtra("android.intent.extra.SUBJECT", VideoPlayerActivity.this.list.get(VideoPlayerActivity.this.position).getDISPLAY_NAME());
            VideoPlayerActivity.this.startActivity(Intent.createChooser(intent, "Share Video"));
        });
        this.takePicture.setOnClickListener(view -> {
            MediaMetadataRetriever mediaMetadataRetriever = VideoPlayerActivity.this.mediaMetadataRetriever;
            VideoPlayerActivity videoPlayerActivity = VideoPlayerActivity.this;
            mediaMetadataRetriever.setDataSource(videoPlayerActivity, Uri.parse(list2.get(videoPlayerActivity.position).getDATA()));
            VideoPlayerActivity videoPlayerActivity2 = VideoPlayerActivity.this;
            videoPlayerActivity2.save_image(videoPlayerActivity2.mediaMetadataRetriever.getFrameAtTime(VideoPlayerActivity.this.player.getCurrentPosition() * 1000));
        });
        this.lock.setOnClickListener(view -> VideoPlayerActivity.this.lock());
        this.unlock.setOnClickListener(view -> VideoPlayerActivity.this.unlock());
        this.crop.setOnClickListener(view -> {
            int i = VideoPlayerActivity.this.view;
            if (i == 0) {
                VideoPlayerActivity.this.playerView.setResizeMode(3);
                VideoPlayerActivity.this.view = 3;
                VideoPlayerActivity.this.crop.setImageResource(R.drawable.fit);
            } else if (i == 3) {
                VideoPlayerActivity.this.playerView.setResizeMode(4);
                VideoPlayerActivity.this.crop.setImageResource(R.drawable.zoom);
                VideoPlayerActivity.this.view = 4;
            } else if (i == 4) {
                VideoPlayerActivity.this.playerView.setResizeMode(0);
                VideoPlayerActivity.this.crop.setImageResource(R.drawable.full);
                VideoPlayerActivity.this.view = 0;
            }
        });
        this.brightness.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayerActivity.this, R.style.CustomDialog);
            View inflate = LayoutInflater.from(VideoPlayerActivity.this).inflate(R.layout.brightness_dialog, null);
            final TextView textView = inflate.findViewById(R.id.progress);
            SeekBar seekBar = inflate.findViewById(R.id.seekBar);
            int lastBrightness = (int) (PreferenceUtil.getInstance(VideoPlayerActivity.this).getLastBrightness() * 100.0f);
            seekBar.setProgress(lastBrightness);
            textView.setText(Integer.toString(lastBrightness));
            builder.setView(inflate);
            final AlertDialog create = builder.create();
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    VideoPlayerActivity.this.attributes.screenBrightness = ((float) i) / 100.0f;
                    VideoPlayerActivity.this.getWindow().setAttributes(VideoPlayerActivity.this.attributes);
                    seekBar.setProgress(i);
                    textView.setText(Integer.toString(i));
                    PreferenceUtil.getInstance(VideoPlayerActivity.this.getApplicationContext()).saveLastBrightness(VideoPlayerActivity.this.attributes.screenBrightness);
                }
            });
            inflate.findViewById(R.id.imgClose).setOnClickListener(view16 -> create.dismiss());
            create.show();
        });
        this.volume.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayerActivity.this, R.style.CustomDialog);
            View inflate = LayoutInflater.from(VideoPlayerActivity.this).inflate(R.layout.volume_dialog, null);
            VideoPlayerActivity.this.tvolume = inflate.findViewById(R.id.progress);
            VideoPlayerActivity.this.vseekBar = inflate.findViewById(R.id.seekBar);
            int streamVolume = VideoPlayerActivity.this.audioManager.getStreamVolume(3);
            VideoPlayerActivity.this.vseekBar.setMax(VideoPlayerActivity.this.audioManager.getStreamMaxVolume(3));
            VideoPlayerActivity.this.vseekBar.setProgress(streamVolume);
            VideoPlayerActivity.this.tvolume.setText(Integer.toString(streamVolume));
            builder.setView(inflate);
            final AlertDialog create = builder.create();
            VideoPlayerActivity.this.vseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    seekBar.setProgress(i);
                    VideoPlayerActivity.this.tvolume.setText(Integer.toString(i));
                    VideoPlayerActivity.this.audioManager.setStreamVolume(3, i, 0);
                }
            });
            inflate.findViewById(R.id.imgClose).setOnClickListener(view15 -> create.dismiss());
            create.show();
        });
        this.imgMute.setOnClickListener(view -> {

            int halfVolume = VideoPlayerActivity.this.audioManager.getStreamMaxVolume(3) / 2;

            if (isMute) {
                isMute = false;
                this.imgMute.setImageResource(R.drawable.ic_unmute);
                VideoPlayerActivity.this.audioManager.setStreamVolume(3, halfVolume, 0);
            } else {
                isMute = true;
                this.imgMute.setImageResource(R.drawable.ic_mute);
                VideoPlayerActivity.this.audioManager.setStreamVolume(3, 0, 0);
            }


        });
        this.equalizer.setOnClickListener(view -> {
            try {
                VideoPlayerActivity.this.presetReverb = new PresetReverb(0, VideoPlayerActivity.this.player.getAudioSessionId());
                VideoPlayerActivity videoPlayerActivity = VideoPlayerActivity.this;
                videoPlayerActivity.enableDisable(PreferenceUtil.getInstance(videoPlayerActivity.getApplicationContext()).geteqSwitch());
                Equalizers.initEq(VideoPlayerActivity.this.player.getAudioSessionId());
                BassBoosts.initBass(VideoPlayerActivity.this.player.getAudioSessionId());
                Virtualizers.initVirtualizer(VideoPlayerActivity.this.player.getAudioSessionId());
                if (Build.VERSION.SDK_INT >= 19) {
                    Loud.initLoudnessEnhancer(VideoPlayerActivity.this.player.getAudioSessionId());
                }
                VideoPlayerActivity.this.loadEqualizer();
            } catch (Exception e) {
                Log.e("ikmhhhuhuj", "onClick: " + e.getMessage());
                e.printStackTrace();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayerActivity.this, R.style.CustomDialog);
            View inflate = LayoutInflater.from(VideoPlayerActivity.this).inflate(R.layout.equalizer_dialog, null);
            ImageView imageView = inflate.findViewById(R.id.imgClose);
            final LinearLayout linearLayout = inflate.findViewById(R.id.llEqualizerView);
            final SwitchCompat switchCompat = inflate.findViewById(R.id.switch_button);
            Spinner spinner = inflate.findViewById(R.id.reverb_spinner);
            VideoPlayerActivity.this.rvEqualizer = inflate.findViewById(R.id.rvEqualizer);
            VideoPlayerActivity.this.rvEqualizer.setLayoutManager(new LinearLayoutManager(VideoPlayerActivity.this.getApplicationContext(), 0, false));
            VideoPlayerActivity.this.rvEqualizer.setFocusable(true);
            VideoPlayerActivity.this.rvEqualizer.setHasFixedSize(true);
            VideoPlayerActivity videoPlayerActivity2 = VideoPlayerActivity.this;
            if (VideoPlayerActivity.equalizerListData != null) {
                videoPlayerActivity2.myAdapter = new MyAdapter(videoPlayerActivity2.getApplicationContext(), VideoPlayerActivity.equalizerListData);
                VideoPlayerActivity.this.rvEqualizer.setAdapter(VideoPlayerActivity.this.myAdapter);
                VideoPlayerActivity.this.myAdapter.notifyDataSetChanged();
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(VideoPlayerActivity.this.getString(R.string.preset_none));
            arrayList.add(VideoPlayerActivity.this.getString(R.string.preset_large_hall));
            arrayList.add(VideoPlayerActivity.this.getString(R.string.preset_large_room));
            arrayList.add(VideoPlayerActivity.this.getString(R.string.preset_medium_hall));
            arrayList.add(VideoPlayerActivity.this.getString(R.string.preset_medium_room));
            arrayList.add(VideoPlayerActivity.this.getString(R.string.preset_small_room));
            arrayList.add(VideoPlayerActivity.this.getString(R.string.preset_plate));
            ArrayAdapter arrayAdapter = new ArrayAdapter(VideoPlayerActivity.this.getApplicationContext(), R.layout.custom_textview_to_spinner, arrayList);
            arrayAdapter.setDropDownViewResource(17367049);
            spinner.setAdapter(arrayAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j1) {
                    if (i == 0) {
                        try {
                            VideoPlayerActivity.this.presetReverb.setPreset(PresetReverb.PRESET_NONE);
                            VideoPlayerActivity.this.reverbSetting = 0;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (i == 1) {
                        VideoPlayerActivity.this.presetReverb.setPreset(PresetReverb.PRESET_LARGEHALL);
                        VideoPlayerActivity.this.reverbSetting = 1;
                    } else if (i == 2) {
                        VideoPlayerActivity.this.presetReverb.setPreset(PresetReverb.PRESET_LARGEROOM);
                        VideoPlayerActivity.this.reverbSetting = 2;
                    } else if (i == 3) {
                        VideoPlayerActivity.this.presetReverb.setPreset(PresetReverb.PRESET_MEDIUMHALL);
                        VideoPlayerActivity.this.reverbSetting = 3;
                    } else if (i == 4) {
                        VideoPlayerActivity.this.presetReverb.setPreset(PresetReverb.PRESET_MEDIUMROOM);
                        VideoPlayerActivity.this.reverbSetting = 4;
                    } else if (i == 5) {
                        VideoPlayerActivity.this.presetReverb.setPreset(PresetReverb.PRESET_SMALLROOM);
                        VideoPlayerActivity.this.reverbSetting = 5;
                    } else if (i == 6) {
                        VideoPlayerActivity.this.presetReverb.setPreset(PresetReverb.PRESET_PLATE);
                        VideoPlayerActivity.this.reverbSetting = 6;
                    } else {
                        VideoPlayerActivity.this.reverbSetting = 0;
                    }
                }
            });
            switchCompat.setChecked(PreferenceUtil.getInstance(VideoPlayerActivity.this.getApplicationContext()).geteqSwitch());
            if (PreferenceUtil.getInstance(VideoPlayerActivity.this.getApplicationContext()).geteqSwitch()) {
                linearLayout.setVisibility(View.GONE);
            } else {
                linearLayout.setVisibility(View.VISIBLE);
            }
            switchCompat.setOnClickListener(view14 -> {
                if (switchCompat.isChecked()) {
                    PreferenceUtil.getInstance(VideoPlayerActivity.this.getApplicationContext()).eqSwitch(true);
                    VideoPlayerActivity.this.enableDisable(true);
                    linearLayout.setVisibility(View.GONE);
                    return;
                }
                PreferenceUtil.getInstance(VideoPlayerActivity.this.getApplicationContext()).eqSwitch(false);
                VideoPlayerActivity.this.enableDisable(false);
                linearLayout.setVisibility(View.VISIBLE);
            });
            SeekBar seekBar = inflate.findViewById(R.id.seek_bar_bass_boots);
            SeekBar seekBar2 = inflate.findViewById(R.id.seek_bar_virtualizer);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    BassBoosts.setBassBoostStrength((short) ((int) (((float) i) * 50.0f)));
                }
            });
            int i = (PreferenceUtil.getInstance().saveEq().getInt(PreferenceUtil.BASS_BOOST, 0) * 20) / 1000;
            if (i > 0) {
                seekBar.setProgress(i);
                BassBoosts.setBassBoostStrength((short) i);
            } else {
                seekBar.setProgress(1);
            }
            seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    Virtualizers.setVirtualizerStrength((short) ((int) (((float) i) * 50.0f)));
                }
            });
            int i2 = (PreferenceUtil.getInstance().saveEq().getInt(PreferenceUtil.VIRTUAL_BOOST, 0) * 20) / 1000;
            if (i2 > 0) {
                seekBar2.setProgress(i2);
                Virtualizers.setVirtualizerStrength((short) i2);
            } else {
                seekBar2.setProgress(1);
            }
            VideoPlayerActivity.this.initEq(inflate);
            builder.setView(inflate);
            final AlertDialog create = builder.create();
            imageView.setOnClickListener(view13 -> create.dismiss());
            create.show();
        });
        this.pspeed.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayerActivity.this, R.style.CustomDialog);
            View inflate = LayoutInflater.from(VideoPlayerActivity.this).inflate(R.layout.playback_dialog, null);
            VideoPlayerActivity.this.dspeed = inflate.findViewById(R.id.dspeed);
            final AtomicInteger atomicInteger = new AtomicInteger((int) (PreferenceUtil.getInstance(VideoPlayerActivity.this).getLastSpeed() * 100.0f));
            VideoPlayerActivity.this.dspeed.setText("1.0X");
//            VideoPlayerActivity.this.dspeed.setText(Integer.toString(atomicInteger.get()));
            inflate.findViewById(R.id.sdown).setOnClickListener(view12 -> {
//                Speed (0.25x, 0.5x, 0.75x, 1x, 1.25x, 1.5x, 1.75x, 2x)
                int ai = atomicInteger.get();

                if (atomicInteger.get() <= 100) {
                    atomicInteger.set(atomicInteger.get() - 25);
                    if (atomicInteger.get() < 24) {
                        atomicInteger.set(atomicInteger.get() + 25);
                    }
                } else {
                    atomicInteger.set(atomicInteger.get() - 25);
                }
                VideoPlayerActivity.this.setSpeed(atomicInteger.get());


//                if (atomicInteger.get() <= 100) {
//                    atomicInteger.set(atomicInteger.get() - 5);
//                    if (atomicInteger.get() < 24) {
//                        atomicInteger.set(atomicInteger.get() + 5);
//                    }
//                } else {
//                    atomicInteger.set(atomicInteger.get() - 10);
//                }
//                VideoPlayerActivity.this.setSpeed(atomicInteger.get());
            });
            inflate.findViewById(R.id.sup).setOnClickListener(view1 -> {

                if (atomicInteger.get() < 100) {
                    atomicInteger.set(atomicInteger.get() + 25);
                } else {
                    atomicInteger.set(atomicInteger.get() + 25);
                    if (atomicInteger.get() > 201) {
                        atomicInteger.set(atomicInteger.get() - 25);
                    }
                }
                VideoPlayerActivity.this.setSpeed(atomicInteger.get());


//                if (atomicInteger.get() < 100) {
//                    atomicInteger.set(atomicInteger.get() + 5);
//                } else {
//                    atomicInteger.set(atomicInteger.get() + 10);
//                    if (atomicInteger.get() > 401) {
//                        atomicInteger.set(atomicInteger.get() - 10);
//                    }
//                }
//                VideoPlayerActivity.this.setSpeed(atomicInteger.get());
            });
            builder.setView(inflate);
            builder.show();
        });
        this.repeat.setOnClickListener(view -> {
            if (VideoPlayerActivity.this.repeatstatus) {
                PreferenceUtil.getInstance(VideoPlayerActivity.this).saveRepeatOne(false);
                VideoPlayerActivity.this.repeat.setImageResource(R.drawable.repeat);
                VideoPlayerActivity.this.player.setRepeatMode(0);
                VideoPlayerActivity videoPlayerActivity = VideoPlayerActivity.this;
                videoPlayerActivity.repeatstatus = true ^ videoPlayerActivity.repeatstatus;
                return;
            }
            PreferenceUtil.getInstance(VideoPlayerActivity.this).saveRepeatOne(true);
            VideoPlayerActivity.this.repeat.setImageResource(R.drawable.repeatone);
            VideoPlayerActivity.this.player.setRepeatMode(1);
            VideoPlayerActivity videoPlayerActivity2 = VideoPlayerActivity.this;
            videoPlayerActivity2.repeatstatus = true ^ videoPlayerActivity2.repeatstatus;
        });
        this.title = findViewById(R.id.title);
        if (getResources().getConfiguration().orientation == 1) {
            this.width = displayMetrics.widthPixels;
            titlepot();
        } else {
            this.width = displayMetrics.heightPixels;
            titleland();
        }
        List<VideoItem> list3 = this.list;
        if (list3 != null) {
            this.title.setText(list3.get(this.position).displayName);
            Gson gson = new Gson();
            ArrayList arrayList = new ArrayList(this.list);
            PreferenceUtil.getInstance(getApplicationContext()).setVideoURL(gson.toJson(arrayList));
            PreferenceUtil.getInstance(getApplicationContext()).setVideoPosition(this.position);
        } else {
            this.title.setText(uri2filename());
        }
        Log.e("dfdjmkk", "onClick: " + this.orientation);
        this.rotate.setOnClickListener(view -> {
            VideoPlayerActivity.this.isOrientation = false;
            VideoPlayerActivity videoPlayerActivity = VideoPlayerActivity.this;
            videoPlayerActivity.orientation = PreferenceUtil.getInstance(videoPlayerActivity.getApplicationContext()).getOrientation();
            Log.e("dfdjmkk", "onClick: " + VideoPlayerActivity.this.orientation);
            if (VideoPlayerActivity.this.orientation == 0) {
                Toast.makeText(VideoPlayerActivity.this.getApplicationContext(), "Landscape Locked", Toast.LENGTH_SHORT).show();
//                VideoPlayerActivity.this.rotate.setImageDrawable(VideoPlayerActivity.this.getResources().getDrawable(R.drawable.ic_screen_lock_landscape_black_24dp));
                VideoPlayerActivity.this.rotate.setImageDrawable(VideoPlayerActivity.this.getResources().getDrawable(R.drawable.ic_rotation_lock));
                PreferenceUtil.getInstance(VideoPlayerActivity.this.getApplicationContext()).saveOrientation(1);
                VideoPlayerActivity.this.setRequestedOrientation(6);
            } else if (VideoPlayerActivity.this.orientation == 1) {
                Toast.makeText(VideoPlayerActivity.this.getApplicationContext(), "Portrait Locked", Toast.LENGTH_SHORT).show();
//                VideoPlayerActivity.this.rotate.setImageDrawable(VideoPlayerActivity.this.getResources().getDrawable(R.drawable.ic_screen_lock_portrait_black_24dp));
                VideoPlayerActivity.this.rotate.setImageDrawable(VideoPlayerActivity.this.getResources().getDrawable(R.drawable.ic_rotation_lock));
                PreferenceUtil.getInstance(VideoPlayerActivity.this.getApplicationContext()).saveOrientation(2);
                VideoPlayerActivity.this.setRequestedOrientation(1);
            } else {
                Toast.makeText(VideoPlayerActivity.this.getApplicationContext(), "Auto Rotate", Toast.LENGTH_SHORT).show();
//                VideoPlayerActivity.this.rotate.setImageDrawable(VideoPlayerActivity.this.getResources().getDrawable(R.drawable.rotate));
                VideoPlayerActivity.this.rotate.setImageDrawable(VideoPlayerActivity.this.getResources().getDrawable(R.drawable.ic_auto_rotate));
                PreferenceUtil.getInstance(VideoPlayerActivity.this.getApplicationContext()).saveOrientation(0);
                VideoPlayerActivity.this.setRequestedOrientation(4);
            }
        });
        this.player.addListener(new Player.EventListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onTimelineChanged(@NonNull Timeline timeline, @NonNull Object obj, int i) {
            }

            @Override
            public void onTracksChanged(@NonNull TrackGroupArray trackGroupArray, @NonNull TrackSelectionArray trackSelectionArray) {
                if (trackGroupArray != VideoPlayerActivity.this.lastSeenTrackGroupArray) {
                    MappingTrackSelector.MappedTrackInfo currentMappedTrackInfo = VideoPlayerActivity.this.trackSelector.getCurrentMappedTrackInfo();
                    if (currentMappedTrackInfo != null) {
                        if (currentMappedTrackInfo.getTypeSupport(2) == 1) {
                            Toast.makeText(VideoPlayerActivity.this, "Media includes video tracks, but none are playable by this device", Toast.LENGTH_SHORT).show();
                        }
                        if (currentMappedTrackInfo.getTypeSupport(1) == 1) {
                            Toast.makeText(VideoPlayerActivity.this, "Media includes audio tracks, but none are playable by this device", Toast.LENGTH_SHORT).show();
                        }
                    }
                    VideoPlayerActivity.this.lastSeenTrackGroupArray = trackGroupArray;
                }
            }

            @Override
            public void onPositionDiscontinuity(int i) {
                if (!PreferenceUtil.getInstance().getAutoplaynext() && i == 0) {
                    VideoPlayerActivity.this.player.setPlayWhenReady(false);
                }
                int currentWindowIndex = VideoPlayerActivity.this.player.getCurrentWindowIndex();
                if (currentWindowIndex != VideoPlayerActivity.this.position) {
                    VideoPlayerActivity.this.position = currentWindowIndex;
                    VideoPlayerActivity.this.title.setText(VideoPlayerActivity.this.list.get(currentWindowIndex).displayName);
                    Gson gson = new Gson();
                    ArrayList arrayList = new ArrayList(VideoPlayerActivity.this.list);
                    PreferenceUtil.getInstance(VideoPlayerActivity.this.getApplicationContext()).setVideoURL(gson.toJson(arrayList));
                    PreferenceUtil.getInstance(VideoPlayerActivity.this.getApplicationContext()).setVideoPosition(VideoPlayerActivity.this.position);
                }
            }

            @Override
            public void onPlayerStateChanged(boolean z, int i) {
                if (VideoPlayerActivity.this.adbool && VideoPlayerActivity.this.isloaded) {
                    if (z) {
                        VideoPlayerActivity.this.vadfrm.setVisibility(View.GONE);
                    } else if (VideoPlayerActivity.this.isAsk) {
                        VideoPlayerActivity.this.vadfrm.setVisibility(View.GONE);
                    } else {
                        VideoPlayerActivity.this.vadfrm.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        PlayerView playerView2 = this.playerView;
        playerView2.setOnTouchListener(new OnSwipeTouchListener(this, this.player, playerView2, this.audioManager));
        this.popup.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(VideoPlayerActivity.this.getApplicationContext())) {
                VideoPlayerActivity.this.popup();
                return;
            }
            VideoPlayerActivity videoPlayerActivity = VideoPlayerActivity.this;
            videoPlayerActivity.startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + VideoPlayerActivity.this.getPackageName())), VideoPlayerActivity.CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        });
        this.sheetmain.setOnTouchListener((view, motionEvent) -> {
            VideoPlayerActivity.this.sheetmain.setVisibility(View.GONE);
            return false;
        });
        this.playlist.setOnClickListener(view -> VideoPlayerActivity.this.sheetmain.setVisibility(View.VISIBLE));
        try {
            this.presetReverb = new PresetReverb(0, this.player.getAudioSessionId());
            Equalizers.initEq(this.player.getAudioSessionId());
            BassBoosts.initBass(this.player.getAudioSessionId());
            Virtualizers.initVirtualizer(this.player.getAudioSessionId());
            Loud.initLoudnessEnhancer(this.player.getAudioSessionId());
            loadEqualizer();
            enableDisable(PreferenceUtil.getInstance(getApplicationContext()).geteqSwitch());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onResume() {
        super.onResume();
        this.vadfrm.setVisibility(View.GONE);
        if (this.isOrientation) {
            this.orientation = PreferenceUtil.getInstance(getApplicationContext()).getOrientation();
            Log.e("llmdfmkd", "onResume: " + this.orientation);
            int i = this.orientation;
            if (i == 0) {
                setRequestedOrientation(4);
//                this.rotate.setImageDrawable(getResources().getDrawable(R.drawable.rotate));
                this.rotate.setImageDrawable(getResources().getDrawable(R.drawable.ic_auto_rotate));
            } else if (i == 1) {
                setRequestedOrientation(6);
//                this.rotate.setImageDrawable(getResources().getDrawable(R.drawable.ic_screen_lock_landscape_black_24dp));
                this.rotate.setImageDrawable(getResources().getDrawable(R.drawable.ic_rotation_lock));
            } else {
                setRequestedOrientation(1);
//                this.rotate.setImageDrawable(getResources().getDrawable(R.drawable.ic_screen_lock_portrait_black_24dp));
                this.rotate.setImageDrawable(getResources().getDrawable(R.drawable.ic_rotation_lock));
            }
        }
    }

    @SuppressLint("WrongConstant")
    public boolean isPlaying() {
        return this.player.getPlaybackState() == 3 && this.player.getPlayWhenReady();
    }


    private void loadEqualizer() {
        List<String> presetNames = getPresetNames();
        Log.e("presetList ", " : " + presetNames.size());
        if (presetNames.size() != 0) {
            int presetPos = PreferenceUtil.getInstance(getApplicationContext()).getPresetPos();
            equalizerListData = new ArrayList<>();
            for (int i = 0; i < presetNames.size(); i++) {
                if (i == presetPos) {
                    equalizerListData.add(i, new EqualizerDataList(presetNames.get(i), true));
                } else {
                    equalizerListData.add(i, new EqualizerDataList(presetNames.get(i), false));
                }
            }
            Log.e("equalizerListData ", " : " + equalizerListData.size());
        }
    }

    private ArrayList getPresetNames() {
        ArrayList arrayList = new ArrayList();
        short presetNo = Equalizers.getPresetNo();
        if (presetNo != 0) {
            for (short s = 0; s < presetNo; s = (short) (s + 1)) {
                arrayList.add(Equalizers.getPresetNames(s));
            }
            arrayList.add("Custom");
        }
        return arrayList;
    }


    private void enableDisable(Boolean bool) {
        Equalizers.setEnabled(bool);
        BassBoosts.setEnabled(bool);
        Virtualizers.setEnabled(bool);
        Loud.setEnabled(bool);
        this.presetReverb.setEnabled(bool);
    }

    @SuppressLint("Range")
    private String uri2filename() {
        Cursor query;
        String scheme = getIntent().getData().getScheme();
        if (scheme.equals("file")) {
            return getIntent().getData().getLastPathSegment();
        }
        return (!scheme.equals("content") || (query = getContentResolver().query(getIntent().getData(), null, null, null, null)) == null || !query.moveToFirst()) ? "" : query.getString(query.getColumnIndex("_display_name"));
    }


    private void lock() {
        this.lockstatus = !this.lockstatus;
        findViewById(R.id.bottom_control).setVisibility(View.INVISIBLE);
        findViewById(R.id.center_left_control).setVisibility(View.INVISIBLE);
        findViewById(R.id.center_left_control1).setVisibility(View.INVISIBLE);
        findViewById(R.id.top_control).setVisibility(View.INVISIBLE);
        findViewById(R.id.center_right_control).setVisibility(View.INVISIBLE);
        this.unlock.setVisibility(View.VISIBLE);
        PreferenceUtil.getInstance(getApplicationContext()).setLock(true);
    }


    private void unlock() {
        this.lockstatus = !this.lockstatus;
        findViewById(R.id.bottom_control).setVisibility(View.VISIBLE);
        findViewById(R.id.center_left_control).setVisibility(View.VISIBLE);
        findViewById(R.id.center_left_control1).setVisibility(View.VISIBLE);
        findViewById(R.id.top_control).setVisibility(View.VISIBLE);
        findViewById(R.id.center_right_control).setVisibility(View.VISIBLE);
        this.unlock.setVisibility(View.INVISIBLE);
        PreferenceUtil.getInstance(getApplicationContext()).setLock(false);
    }

    private void titlepot() {
        ViewGroup.LayoutParams layoutParams = this.title.getLayoutParams();
        layoutParams.width = this.width / 7;
        this.title.setLayoutParams(layoutParams);
    }

    private void titleland() {
        ViewGroup.LayoutParams layoutParams = this.title.getLayoutParams();
        layoutParams.width = this.width;
        this.title.setLayoutParams(layoutParams);
    }


    /*private void setSpeed(int i) {
        this.dspeed.setText(Integer.toString(i));
        float f = ((float) i) / 100.0f;
        this.pspeed.setText(String.format("%sX", f));
        PlaybackParameters playbackParameters = new PlaybackParameters(f);
        this.parameters = playbackParameters;
        this.player.setPlaybackParameters(playbackParameters);
        PreferenceUtil.getInstance(getApplicationContext()).saveLastSpeed(f);
    }*/


    private void setSpeed(int i) {
        float f = ((float) i) / 100.0f;
        String speedTxt = String.format("%sX", f);
        this.dspeed.setText(speedTxt);
        this.pspeed.setText(speedTxt);
        PlaybackParameters playbackParameters = new PlaybackParameters(f);
        this.parameters = playbackParameters;
        this.player.setPlaybackParameters(playbackParameters);
        PreferenceUtil.getInstance(getApplicationContext()).saveLastSpeed(f);
    }


    private void popup() {
        Intent intent = new Intent(this, floating.class);
        intent.putExtra("position", this.position);
        intent.putExtra("list", (Serializable) this.list);
        intent.putExtra("current", this.player.getCurrentPosition());
        startService(intent);
        onBackPressed();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);
        adjustFullScreen(configuration);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        int currentWindowIndex = this.player.getCurrentWindowIndex();
        Log.e("kmmke", this.list.get(currentWindowIndex).displayName + " onPositionDiscontinuity: ");
        PreferenceUtil.getInstance(this).setResumeVideotime(this.player.getContentPosition(), this.list.get(currentWindowIndex).displayName);
        Log.e("kmmke", "onDestroy: " + this.player.getContentPosition());
        unregisterReceiver(this.broadcastReceiver);
    }

    @Override
    public void onPause() {
        if (this.lockstatus) {
            unlock();
        }
        this.player.setPlayWhenReady(false);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        this.vadfrm.setVisibility(View.GONE);
        this.adbool = false;
        this.isloaded = false;
        if (!this.lockstatus) {
            if (list.size() > 0) {
                PreferenceUtil.getInstance().setResumeVideotime(this.player.getContentPosition(), this.list.get(this.player.getCurrentWindowIndex()).displayName);
                this.player.release();
                finish();
            }
        }
    }

    private void checkSetPer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialogInterface, i) -> {
            dialogInterface.cancel();
            VideoPlayerActivity.this.openSettings();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.show();
    }


    @SuppressWarnings("deprecation")
    private void openSettings() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= 23) {
            intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + getPackageName()));
        } else {
            intent = null;
        }
        startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (i2 == -1) {
                popup();
                return;
            }
            checkSetPer();
            finish();
        } else if (i != 25) {
            super.onActivityResult(i, i2, intent);
        } else if (i2 == -1 && intent.hasExtra(FilePicker.EXTRA_FILE_PATH)) {
            String stringExtra = intent.getStringExtra(FilePicker.EXTRA_FILE_PATH);
            stringExtra.getClass();
            Uri fromFile = Uri.fromFile(new File(stringExtra));
            if (this.subtitle != null) {
                this.subtitleFormat = Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP, -1, "en");
                SingleSampleMediaSource createMediaSource = new SingleSampleMediaSource.Factory(this.dataSourceFactory).createMediaSource(fromFile, this.subtitleFormat, C.TIME_UNSET);
                this.subtitleSource = createMediaSource;
                MergingMediaSource mergingMediaSource = new MergingMediaSource(videoSource, createMediaSource);
                this.mergedSource = mergingMediaSource;
                this.player.prepare(mergingMediaSource, false, false);
                this.player.setPlayWhenReady(true);
                this.subbool = false;
            }
        }
    }


    private void initEq(View view2) {
        for (short s = 0; s < Equalizers.getNumberOfBands(); s = (short) (s + 1)) {
            try {
                final short[] bandLevelRange = Equalizers.getBandLevelRange();
                this.seekBar = new VerticalSeekBar(getApplicationContext());
                this.textView = new TextView(getApplicationContext());
                if (s == 0) {
                    this.seekBar = view2.findViewById(R.id.seek_bar1);
                    this.textView = view2.findViewById(R.id.level1);
                } else if (s == 1) {
                    this.seekBar = view2.findViewById(R.id.seek_bar2);
                    this.textView = view2.findViewById(R.id.level2);
                } else if (s == 2) {
                    this.seekBar = view2.findViewById(R.id.seek_bar3);
                    this.textView = view2.findViewById(R.id.level3);
                } else if (s == 3) {
                    this.seekBar = view2.findViewById(R.id.seek_bar4);
                    this.textView = view2.findViewById(R.id.level4);
                } else if (s == 4) {
                    this.seekBar = view2.findViewById(R.id.seek_bar5);
                    this.textView = view2.findViewById(R.id.level5);
                }
                VerticalSeekBar verticalSeekBar = this.seekBar;
                this.seekBarFinal[s] = verticalSeekBar;
                verticalSeekBar.setId(s);
                if (bandLevelRange != null) {
                    this.seekBar.setMax(bandLevelRange[1] - bandLevelRange[0]);
                    if (PreferenceUtil.getInstance().getPresetPos() < Equalizers.getPresetNo()) {
                        this.seekBarFinal[s].setProgress(Equalizers.getBandLevel(s) - bandLevelRange[0]);
                    } else {
                        VerticalSeekBar verticalSeekBar2 = this.seekBarFinal[s];
                        SharedPreferences saveEq = PreferenceUtil.getInstance().saveEq();
                        verticalSeekBar2.setProgress(saveEq.getInt(PreferenceUtil.BAND_LEVEL + ((int) s), 0) - bandLevelRange[0]);
                    }
                }
                int centerFreq = Equalizers.getCenterFreq(s);
                if (centerFreq < 1000000) {
                    TextView textView2 = this.textView;
                    textView2.setText((centerFreq / 1000) + "Hz");
                } else {
                    TextView textView3 = this.textView;
                    textView3.setText((centerFreq / 1000000) + "kHz");
                }
                short finalS = s;
                this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                        if (z) {
                            try {
                                if (bandLevelRange != null) {
                                    int progress = seekBar.getProgress() + bandLevelRange[0];
                                    Equalizers.setBandLevel(finalS, (short) progress);
                                    if (Equalizers.getPresetNo() != 0) {
                                        VideoPlayerActivity.this.rvEqualizer.smoothScrollToPosition(Equalizers.getPresetNo());
                                        for (short s = 0; s < VideoPlayerActivity.equalizerListData.size(); s = (short) (s + 1)) {
                                            if (s == Equalizers.getPresetNo()) {
                                                VideoPlayerActivity.equalizerListData.set(s, new EqualizerDataList(VideoPlayerActivity.equalizerListData.get(s).getName(), true));
                                            } else {
                                                VideoPlayerActivity.equalizerListData.set(s, new EqualizerDataList(VideoPlayerActivity.equalizerListData.get(s).getName(), false));
                                            }
                                        }
                                        VideoPlayerActivity.this.myAdapter.notifyDataSetChanged();
                                    } else {
                                        VideoPlayerActivity.this.rvEqualizer.smoothScrollToPosition(0);
                                        for (int i2 = 0; i2 < VideoPlayerActivity.equalizerListData.size(); i2++) {
                                            if (i2 == 0) {
                                                VideoPlayerActivity.equalizerListData.set(i2, new EqualizerDataList(VideoPlayerActivity.equalizerListData.get(i2).getName(), true));
                                            } else {
                                                VideoPlayerActivity.equalizerListData.set(i2, new EqualizerDataList(VideoPlayerActivity.equalizerListData.get(i2).getName(), false));
                                            }
                                        }
                                        VideoPlayerActivity.this.myAdapter.notifyDataSetChanged();
                                    }
                                    Equalizers.savePrefs(finalS, progress);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("", "Failed to init eq");
                return;
            }
        }
    }

    public void save_image(Bitmap bitmap) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/");
        file.mkdirs();
        File file2 = new File(file, this.list.get(this.position).getDISPLAY_NAME() + "_" + new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date()) + ".png");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Context applicationContext = getApplicationContext();
            Toast.makeText(applicationContext, "Saved to " + file2.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MakeSureFileWasCreatedThenMakeAvabile(file2);
    }

    private void MakeSureFileWasCreatedThenMakeAvabile(File file) {
        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.toString()}, null, (str, uri) -> {
        });
    }

    public void minimize() {
        if (this.playerView != null) {
            Rational rational = null;
            if (Build.VERSION.SDK_INT >= 21) {
                rational = new Rational(this.playerView.getWidth(), this.playerView.getHeight());
            }
            if (Build.VERSION.SDK_INT >= 26) {
                this.mPictureInPictureParamsBuilder.setAspectRatio(rational).build();
                enterPictureInPictureMode(this.mPictureInPictureParamsBuilder.build());
            }
        }
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (z) {
            adjustFullScreen(getResources().getConfiguration());
        }
    }

    private void adjustFullScreen(Configuration configuration) {
        View decorView = getWindow().getDecorView();
        if (configuration.orientation == 2) {
            decorView.setSystemUiVisibility(5894);
            Log.e("wwwwww", "adjustFullScreen: first");
            return;
        }
        decorView.setSystemUiVisibility(256);
        this.controlLayout.setVisibility(View.VISIBLE);
        Log.e("wwwwww", "adjustFullScreen: sec");
    }

    private void rotateScreen() {
        try {
            MediaMetadataRetriever mediaMetadataRetriever2 = new MediaMetadataRetriever();
            mediaMetadataRetriever2.setDataSource(this, Uri.parse(this.list.get(this.position).getDATA()));
            Bitmap frameAtTime = mediaMetadataRetriever2.getFrameAtTime();
            int width2 = frameAtTime.getWidth();
            int height = frameAtTime.getHeight();
            if (width2 > height) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            if (width2 < height) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } catch (RuntimeException unused) {
            Log.e("MediaMetadataRetriever", "- Failed to rotate the video");
        }
    }

    public void deleteVideo() {
        final int currentWindowIndex = this.player.getCurrentWindowIndex();
        if (this.list.size() != 0) {
            View inflate = LayoutInflater.from(this).inflate(R.layout.checkbox, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete video from device?");
            builder.setMessage("Video will be deleted permanently from device.");
            builder.setView(inflate);
            builder.setPositiveButton("DELETE", (dialogInterface, i) -> {
                List list;
                if (VideoPlayerActivity.this.checktrue) {
                    String recycleVideo = PreferenceUtil.getInstance(VideoPlayerActivity.this).getRecycleVideo();
                    if (recycleVideo.equalsIgnoreCase("")) {
                        list = new ArrayList();
                    } else {
                        list = new Gson().fromJson(recycleVideo, new TypeToken<ArrayList<PrivateItem>>() {
                        }.getType());
                    }
                    VideoPlayerActivity videoPlayerActivity = VideoPlayerActivity.this;
                    File file = new File(String.valueOf(videoPlayerActivity.getExternalFilesDir(videoPlayerActivity.getResources().getString(R.string.recycler_folder_name))));
                    File file2 = new File(VideoPlayerActivity.this.list.get(VideoPlayerActivity.this.position).getDATA());
                    Gson gson = new Gson();
                    list.add(new PrivateItem(file2.getParent(), VideoPlayerActivity.this.list.get(VideoPlayerActivity.this.position).getDISPLAY_NAME()));
                    PreferenceUtil.getInstance(VideoPlayerActivity.this).setRecycleVideo(gson.toJson(list));
                    try {
                        VideoPlayerActivity.this.moveFile(file2, file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String[] strArr = {file2.getAbsolutePath()};
                    ContentResolver contentResolver = VideoPlayerActivity.this.getContentResolver();
                    Uri contentUri = MediaStore.Files.getContentUri("external");
                    contentResolver.delete(contentUri, "_data=?", strArr);
                    if (file2.exists()) {
                        contentResolver.delete(contentUri, "_data=?", strArr);
                    }
                } else {
                    File file3 = new File(VideoPlayerActivity.this.list.get(currentWindowIndex).getDATA());
                    file3.delete();
                    String[] strArr2 = {file3.getAbsolutePath()};
                    ContentResolver contentResolver2 = VideoPlayerActivity.this.getContentResolver();
                    Uri contentUri2 = MediaStore.Files.getContentUri("external");
                    contentResolver2.delete(contentUri2, "_data=?", strArr2);
                    if (file3.exists()) {
                        contentResolver2.delete(contentUri2, "_data=?", strArr2);
                    }
                }
                VideoPlayerActivity.this.finish();
            });
            builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.cancel());
            builder.show();
            builder.setOnDismissListener(dialogInterface -> VideoPlayerActivity.this.checktrue = false);
            CheckBox checkBox = inflate.findViewById(R.id.checkbox);
            checkBox.setText("Move to Recycle Bin");
            checkBox.setOnCheckedChangeListener((compoundButton, z) -> VideoPlayerActivity.this.checktrue = z);
        }
    }

    public void resumeAskDialog() {
        this.defualt = false;
        this.dislis = true;
        View inflate = LayoutInflater.from(this).inflate(R.layout.pcheckbox, null);
        final long j = PreferenceUtil.getInstance(this).getresumeVideotime(this.list.get(this.position).getDISPLAY_NAME());
        Log.e("plkkkmmejjj", j + " resumeAskDialog: " + this.position + "  " + this.list.get(this.position).getDISPLAY_NAME());
        if (j == 0) {
            Log.e("kmmjjenndi", "resumeAskDialog:==0 ");
            this.player.setPlayWhenReady(true);
            this.player.seekTo(this.position, this.current);
            return;
        }
        this.isAsk = true;
        Log.e("kmmjjenndi", "resumeAskDialog:>=0 ");
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setTitle(this.list.get(this.position).getDISPLAY_NAME());
        builder.setMessage("Do you wish to resume from where you stopped?");
        builder.setView(inflate);
        builder.setPositiveButton("RESUME", (dialogInterface, i) -> {
            VideoPlayerActivity.this.dislis = false;
            if (VideoPlayerActivity.this.defualt) {
                PreferenceUtil.getInstance(VideoPlayerActivity.this).saveResumestatus(0);
            }
            VideoPlayerActivity.this.isAsk = false;
            VideoPlayerActivity.this.player.setPlayWhenReady(true);
            VideoPlayerActivity.this.player.seekTo(VideoPlayerActivity.this.position, j);
        });
        builder.setNegativeButton("START OVER", (dialogInterface, i) -> {
            VideoPlayerActivity.this.dislis = false;
            if (VideoPlayerActivity.this.defualt) {
                PreferenceUtil.getInstance(VideoPlayerActivity.this).saveResumestatus(1);
            }
            VideoPlayerActivity.this.isAsk = false;
            VideoPlayerActivity.this.player.setPlayWhenReady(true);
            VideoPlayerActivity.this.player.seekTo(VideoPlayerActivity.this.position, VideoPlayerActivity.this.current);
        });
        builder.setOnDismissListener(dialogInterface -> {
            Log.e("kkkjne", "onDismiss: ");
            VideoPlayerActivity.this.isAsk = false;
            if (VideoPlayerActivity.this.dislis) {
                VideoPlayerActivity.this.vadfrm.setVisibility(View.GONE);
                VideoPlayerActivity.this.player.setPlayWhenReady(true);
                VideoPlayerActivity.this.player.seekTo(VideoPlayerActivity.this.position, VideoPlayerActivity.this.current);
            }
        });
        builder.show();
        CheckBox checkBox = inflate.findViewById(R.id.checkbox);
        checkBox.setText("Use by default");
        checkBox.setOnCheckedChangeListener((compoundButton, z) -> VideoPlayerActivity.this.defualt = z);
    }

    public void lockVideofile() {
        final int currentWindowIndex = this.player.getCurrentWindowIndex();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lock video?");
        builder.setMessage("Videos will be moved in private folder.Only you can watch them.");
        builder.setPositiveButton("LOCK", (dialogInterface, i) -> {
            List list;
            String lockVideo = PreferenceUtil.getInstance(VideoPlayerActivity.this).getLockVideo();
            if (lockVideo.equalsIgnoreCase("")) {
                list = new ArrayList();
            } else {
                list = new Gson().fromJson(lockVideo, new TypeToken<ArrayList<PrivateItem>>() {
                }.getType());
            }
            VideoPlayerActivity videoPlayerActivity = VideoPlayerActivity.this;
            File file = new File(String.valueOf(videoPlayerActivity.getExternalFilesDir(videoPlayerActivity.getResources().getString(R.string.private_folder_name))));
            File file2 = new File(VideoPlayerActivity.this.list.get(currentWindowIndex).getDATA());
            Gson gson = new Gson();
            list.add(new PrivateItem(file2.getParent(), VideoPlayerActivity.this.list.get(currentWindowIndex).getDISPLAY_NAME()));
            PreferenceUtil.getInstance(VideoPlayerActivity.this).setLockVideo(gson.toJson(list));
            try {
                VideoPlayerActivity.this.moveFile(file2, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String[] strArr = {file2.getAbsolutePath()};
            ContentResolver contentResolver = VideoPlayerActivity.this.getContentResolver();
            Uri contentUri = MediaStore.Files.getContentUri("external");
            contentResolver.delete(contentUri, "_data=?", strArr);
            if (file2.exists()) {
                contentResolver.delete(contentUri, "_data=?", strArr);
            }
            VideoPlayerActivity.this.finish();
        });
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.cancel());
        builder.show();
    }

    public void moveFile(File file, File file2) throws IOException {
        File file3 = new File(file2, file.getName());
        Log.e("ffdff", "moveFile: " + file.getAbsolutePath());
        Log.e("ffdff", "moveFile: " + file2.getAbsolutePath());
        try (FileChannel channel = new FileOutputStream(file3).getChannel(); FileChannel fileChannel = new FileInputStream(file).getChannel()) {
            fileChannel.transferTo(0, fileChannel.size(), channel);
            fileChannel.close();
            file.delete();
        }
    }


    private void renameVideofile() {
        File file = new File(this.list.get(this.player.getCurrentWindowIndex()).getDATA());
        Log.e("file ", " : " + file.getAbsoluteFile().getParent());
        Log.e("file ", " : " + file.getName());
        final String parent = file.getAbsoluteFile().getParent();
        final String name = file.getName();
        String substring = name.substring(0, name.lastIndexOf("."));
        final String substring2 = name.substring(name.lastIndexOf("."));
        Log.e("Name : " + substring, " : " + substring2);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_file_rename, null);
        @SuppressWarnings("unused") TextView textView2 = inflate.findViewById(R.id.txtTitle);
        final EditText editText = inflate.findViewById(R.id.edtName);
        builder.setView(inflate);
        final AlertDialog create = builder.create();
        editText.setText(substring);
        inflate.findViewById(R.id.txtSave).setOnClickListener(view -> {
            if (editText.getText().toString().trim().equalsIgnoreCase("")) {
                Toast.makeText(VideoPlayerActivity.this, "Enter file name", Toast.LENGTH_LONG).show();
                return;
            }
            File file1 = new File(parent, name);
            File file2 = new File(parent, editText.getText().toString().trim() + substring2);
            boolean renameTo = file1.renameTo(file2);
            Log.e("success : ", " : " + renameTo);
            VideoPlayerActivity.this.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file2)));
            VideoPlayerActivity.this.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file1)));
            create.dismiss();
            VideoPlayerActivity.this.finish();
            VideoPlayerActivity.this.finish();
        });
        inflate.findViewById(R.id.txtCancel).setOnClickListener(view -> create.dismiss());
        create.show();
    }


    private void propertiDialog() {
        int currentWindowIndex = this.player.getCurrentWindowIndex();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        LayoutInflater from = LayoutInflater.from(this);
        builder.setTitle("  ");
        View inflate = from.inflate(R.layout.properties_player, null);
        ((TextView) inflate.findViewById(R.id.name)).setText(this.list.get(this.position).getDISPLAY_NAME());
        ((TextView) inflate.findViewById(R.id.duration)).setText(this.list.get(currentWindowIndex).getDURATION());
        ((TextView) inflate.findViewById(R.id.fsize)).setText(this.list.get(currentWindowIndex).getSIZE());
        ((TextView) inflate.findViewById(R.id.location)).setText(this.list.get(currentWindowIndex).getDATA());
        ((TextView) inflate.findViewById(R.id.date)).setText(this.list.get(currentWindowIndex).getDATE());
        builder.setView(inflate).setPositiveButton("OK", (dialogInterface, i) -> {
        });
        builder.show();
    }

    private boolean isPlay() {
        this.player.setPlayWhenReady(false);
        this.player.getPlaybackState();
        return true;
    }

    private boolean isPause() {
        this.player.setPlayWhenReady(true);
        this.player.getPlaybackState();
        return true;
    }

    @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private final Context context;
        private final ArrayList<EqualizerDataList> equalizerList;

        public MyAdapter(Context context2, ArrayList<EqualizerDataList> arrayList) {
            this.context = context2;
            this.equalizerList = arrayList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_equalizer, viewGroup, false));
        }

        public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
            viewHolder.txtName.setText(this.equalizerList.get(i).getName());
            viewHolder.txtName.setOnClickListener(view -> {
                for (int i1 = 0; i1 < VideoPlayerActivity.equalizerListData.size(); i1++) {
                    if (i1 == i1) {
                        VideoPlayerActivity.equalizerListData.set(i1, new EqualizerDataList(VideoPlayerActivity.equalizerListData.get(i1).getName(), true));
                    } else {
                        VideoPlayerActivity.equalizerListData.set(i1, new EqualizerDataList(VideoPlayerActivity.equalizerListData.get(i1).getName(), false));
                    }
                }
                if (i < VideoPlayerActivity.equalizerListData.size()) {
                    PreferenceUtil.getInstance(VideoPlayerActivity.this.getApplicationContext()).savePresetPos(i);
                    short presetNo = Equalizers.getPresetNo();
                    short numberOfBands = Equalizers.getNumberOfBands();
                    if (!(presetNo == -1 || numberOfBands == -1)) {
                        short s = (short) i;
                        if (s < presetNo) {
                            Equalizers.usePreset(s);
                            for (short s2 = 0; s2 < numberOfBands; s2 = (short) (s2 + 1)) {
                                short[] bandLevelRange = Equalizers.getBandLevelRange();
                                short bandLevel = Equalizers.getBandLevel(s2);
                                if (bandLevelRange != null && s2 < VideoPlayerActivity.this.seekBarFinal.length) {
                                    VideoPlayerActivity.this.seekBarFinal[s2].setProgress(bandLevel - bandLevelRange[0]);
                                }
                            }
                        } else {
                            for (short s3 = 0; s3 < numberOfBands; s3 = (short) (s3 + 1)) {
                                short[] bandLevelRange2 = Equalizers.getBandLevelRange();
                                if (bandLevelRange2 != null && s3 < VideoPlayerActivity.this.seekBarFinal.length) {
                                    VerticalSeekBar verticalSeekBar = VideoPlayerActivity.this.seekBarFinal[s3];
                                    SharedPreferences saveEq = PreferenceUtil.getInstance().saveEq();
                                    verticalSeekBar.setProgress(saveEq.getInt(PreferenceUtil.BAND_LEVEL + ((int) s3), 0) - bandLevelRange2[0]);
                                }
                            }
                        }
                    }
                }
                MyAdapter.this.notifyDataSetChanged();
            });
            if (this.equalizerList.get(i).isSelectItem()) {
                viewHolder.txtName.getBackground().setColorFilter(VideoPlayerActivity.this.getResources().getColor(R.color.t15), PorterDuff.Mode.SRC_ATOP);
                viewHolder.txtName.setTextColor(VideoPlayerActivity.this.getResources().getColor(R.color.white));
                return;
            }
            viewHolder.txtName.getBackground().setColorFilter(VideoPlayerActivity.this.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            viewHolder.txtName.setTextColor(VideoPlayerActivity.this.getResources().getColor(R.color.black));
        }

        @Override
        public int getItemCount() {
            return this.equalizerList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final Button txtName;

            public ViewHolder(View view) {
                super(view);
                this.txtName = view.findViewById(R.id.txtName);
            }
        }
    }
}
