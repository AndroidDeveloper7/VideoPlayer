package com.telegram.videoplayer.downloader.activitys;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.adapter.MVideoAdapter;
import com.telegram.videoplayer.downloader.model.VideoItem;
import com.telegram.videoplayer.downloader.utildata.PreferenceUtil;
import com.telegram.videoplayer.downloader.utils.AppPref;
import com.telegram.videoplayer.downloader.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unchecked", "ConstantConditions", "EmptyMethod", "UseCompareMethod"})
public class FavVideoListActivity extends BaseActivity {
    public MVideoAdapter mVideoAdapter;
    public RecyclerView recyclerView;
    AppCompatActivity activity;
    Utils prefenrencUtils;
    SharedPreferences sharedPreferences;
    private String id;
    private List<VideoItem> list;
    private PreferenceUtil util;
    private int widthPixels;
    private AdView mAdView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_videolist);
        getWindow().setFlags(1024, 1024);


        MobileAds.initialize(this, initializationStatus -> {
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        this.sharedPreferences = getSharedPreferences("EASYMONEY", 0);
        this.activity = this;
        this.prefenrencUtils = new Utils(this);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getRealMetrics(displayMetrics);
        this.widthPixels = displayMetrics.widthPixels;
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbar.setTitle("favourite");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Drawable overflowIcon = toolbar.getOverflowIcon();
        overflowIcon.getClass();
        if (Build.VERSION.SDK_INT >= 21) {
            overflowIcon.setTint(getResources().getColor(R.color.white));
        }
        this.util = PreferenceUtil.getInstance(getApplicationContext());
        findViewById(R.id.swipeToRefresh).setEnabled(false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.id = getIntent().getStringExtra("id");
    }


    private void loadVideoList() {
        list = new ArrayList<>();
        boolean valueOf = this.util.getViewType();
        AppPref appPref = new AppPref(this);
        List<VideoItem> allVideo = appPref.getList();
        for (VideoItem videoItem : allVideo) {
            if (new File(videoItem.getDATA()).exists()) {
                list.add(videoItem);
            }
        }
        RecyclerView recyclerView2 = findViewById(R.id.recv);
        this.recyclerView = recyclerView2;
        recyclerView2.setHasFixedSize(true);

        if (list.size() != 0) {
            if (valueOf) {
                this.mVideoAdapter = new MVideoAdapter(this, this.list, this.widthPixels, true, true);
                this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
                this.recyclerView.setAdapter(this.mVideoAdapter);
                return;
            }
            this.mVideoAdapter = new MVideoAdapter(this, this.list, this.widthPixels, false, true);
            this.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            this.recyclerView.setAdapter(this.mVideoAdapter);
        }
    }

    public void onResume() {
        super.onResume();
        loadVideoList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
