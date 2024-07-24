package com.telegram.videoplayer.downloader.activitys;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.telegram.videoplayer.downloader.Adshandler;
import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.adapter.MVideoAdapter;
import com.telegram.videoplayer.downloader.model.MediaQuery;
import com.telegram.videoplayer.downloader.model.VideoItem;
import com.telegram.videoplayer.downloader.utildata.PreferenceUtil;
import com.telegram.videoplayer.downloader.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unchecked", "ConstantConditions", "EmptyMethod", "UseCompareMethod"})
public class VideoListActivity extends BaseActivity {
    private static final String TAG = "VideoListActivity";
    public MVideoAdapter mVideoAdapter;
    public RecyclerView recyclerView;
    AppCompatActivity activity;
    Utils prefenrencUtils;
    SharedPreferences sharedPreferences;
    private String id;
    private List<VideoItem> list;
    private MediaQuery mediaQuery;
    private SearchView searchView;
    private PreferenceUtil util;
    private int widthPixels;
    private AdView mAdView;

    private InterstitialAd interstitialAd;


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
        String stringExtra = getIntent().getStringExtra("name");
        Log.e("name ", " : " + stringExtra);
        toolbar.setTitle(stringExtra);
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
        Log.e("iddkf", "onCreate: " + this.id);
        Log.e(TAG, "onCreate: ===============<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>" + this.id);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem;
        getMenuInflater().inflate(R.menu.video_list_toolbar, menu);
        SearchView searchView2 = (SearchView) menu.findItem(R.id.search).getActionView();
        this.searchView = searchView2;
        EditText editText = searchView2.findViewById(R.id.search_src_text);
        editText.setTextColor(-1);
        editText.setHintTextColor(-1);
        ((ImageView) this.searchView.findViewById(R.id.search_close_btn)).setColorFilter(-1);
       // this.searchView.setSearchableInfo(((SearchManager) getSystemService(SEARCH_SERVICE)).getSearchableInfo(getComponentName()));
        SearchableInfo searchableInfo = ((SearchManager) getSystemService(SEARCH_SERVICE)).getSearchableInfo(getComponentName());
        this.searchView.setSearchableInfo(searchableInfo);

        this.searchView.setMaxWidth(Integer.MAX_VALUE);
        this.searchView.setImeOptions(1);
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String str) {
                VideoListActivity.this.recyclerView.getRecycledViewPool().clear();
                VideoListActivity.this.mVideoAdapter.getFilter().filter(str);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String str) {
                VideoListActivity.this.recyclerView.getRecycledViewPool().clear();
                VideoListActivity.this.mVideoAdapter.getFilter().filter(str);
                return false;
            }
        });
        MenuItem findItem = menu.findItem(R.id.toggle);
        if (this.util.getViewType()) {
            findItem.setIcon(R.drawable.grid);
        } else {
            findItem.setIcon(R.drawable.list);
        }
        int sortOrder = this.util.getSortOrder();
        if (sortOrder == 0) {
            menuItem = menu.findItem(R.id.name_sort_mode);
        } else if (sortOrder == 1) {
            menuItem = menu.findItem(R.id.date_taken_sort_mode);
        } else if (sortOrder == 2) {
            menuItem = menu.findItem(R.id.size_sort_mode);
        } else if (sortOrder != 3) {
            menuItem = menu.findItem(R.id.name_sort_mode);
        } else {
            menuItem = menu.findItem(R.id.numeric_sort_mode);
        }
        menuItem.setChecked(true);
        menu.findItem(R.id.ascending_sort_order).setChecked(this.util.getListAsc());
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 16908332:
                onBackPressed();
                break;
            case R.id.ascending_sort_order:
                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                    this.util.saveListAsc(true);
                    loadVideoList();
                    break;
                } else {
                    menuItem.setChecked(false);
                    this.util.saveListAsc(false);
                    loadVideoList();
                    break;
                }
            case R.id.date_taken_sort_mode:
                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                    this.util.saveSortOrder(1);
                    loadVideoList();
                    break;
                }
                break;
            case R.id.name_sort_mode:
                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                    this.util.saveSortOrder(0);
                    loadVideoList();
                    break;
                }
                break;
            case R.id.nav_setting:
                Adshandler.showAd(VideoListActivity.this, () -> startActivity(new Intent(VideoListActivity.this, SettingsActivity.class)));
                break;
            case R.id.nav_share_app:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey, download this app!" + " https://play.google.com/store/apps/details?id=" + getPackageName());
                    startActivity(shareIntent);

                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            case R.id.numeric_sort_mode:
                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                    this.util.saveSortOrder(3);
                    loadVideoList();
                    break;
                }
                break;
            case R.id.search:
                return true;
            case R.id.size_sort_mode:
                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                    this.util.saveSortOrder(2);
                    loadVideoList();
                    break;
                }
                break;
            case R.id.toggle:
                if (!this.util.getViewType()) {
                    this.util.saveViewType(true);
                    loadVideoList();
                    menuItem.setIcon(R.drawable.grid);
                    this.util.saveViewType(true);
                    break;
                } else {
                    this.util.saveViewType(false);
                    loadVideoList();
                    menuItem.setIcon(R.drawable.list);
                    this.util.saveViewType(false);
                    break;
                }
        }
        loadVideoList();
        return super.onOptionsItemSelected(menuItem);
    }

    private void loadVideoList() {
        this.mediaQuery = new MediaQuery(getApplicationContext());
        boolean valueOf = this.util.getViewType();
        int sortOrder = this.util.getSortOrder();
        boolean listAsc = this.util.getListAsc();
        /*List<VideoItem> allVideo = this.mediaQuery.getAllVideo(this.id);
        for (int i = 0; i < allVideo.size(); i++) {
            if (i != 0 && i % 2 == 0) {
                allVideo.add(null);
            }
        }
        this.list = allVideo;*/
        List<VideoItem> allVideo = this.mediaQuery.getAllVideo(this.id);
        this.list = allVideo;
        if (sortOrder == 0) {
            if (listAsc) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(allVideo, Comparator.comparing(VideoItem::getDISPLAY_NAME));
                }
            } else {
                Collections.sort(allVideo, (videoItem, videoItem2) -> videoItem2.getDISPLAY_NAME().compareTo(videoItem.getDISPLAY_NAME()));
            }
        } else if (sortOrder == 1) {
            if (listAsc) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(allVideo, Comparator.comparing(VideoItem::getDATE));
                }
            } else {
                Collections.sort(allVideo, (videoItem, videoItem2) -> videoItem2.getDATE().compareTo(videoItem.getDATE()));
            }
        } else if (sortOrder == 2) {
            if (listAsc) {
                Collections.sort(allVideo, (videoItem, videoItem2) -> Long.valueOf(videoItem.getVideoSize()).compareTo(videoItem2.getVideoSize()));
            } else {
                Collections.sort(allVideo, (videoItem, videoItem2) -> Long.valueOf(videoItem2.getVideoSize()).compareTo(videoItem.getVideoSize()));
            }
        } else if (sortOrder == 3) {
            if (listAsc) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(allVideo, Comparator.comparing(VideoItem::getDURATION));
                }
            } else {
                Collections.sort(allVideo, (videoItem, videoItem2) -> videoItem2.getDURATION().compareTo(videoItem.getDURATION()));
            }
        }
        RecyclerView recyclerView2 = findViewById(R.id.recv);
        this.recyclerView = recyclerView2;
        recyclerView2.setHasFixedSize(true);
        if (valueOf) {
            this.mVideoAdapter = new MVideoAdapter(this, this.list, this.widthPixels, true, false);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            this.recyclerView.setAdapter(this.mVideoAdapter);
            return;
        }
        this.mVideoAdapter = new MVideoAdapter(this, this.list, this.widthPixels, false, false);
        this.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        this.recyclerView.setAdapter(this.mVideoAdapter);
    }

    public void onResume() {
        super.onResume();
        loadVideoList();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(VideoListActivity.this, DashBoardActivity.class);
        startActivity(intent);

        interstitialAd = new InterstitialAd(VideoListActivity.this, getResources().getString(R.string.Facebook_Interstitial_placement));
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };
        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).build());

    }
}
