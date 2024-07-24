package com.telegram.videoplayer.downloader.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.telegram.videoplayer.downloader.R;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WebUrlPlayer extends BaseActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
    String TAG = "Tag";
    private NativeAd nativeAd;
    private NativeAdLayout nativeAdLayout;
    private LinearLayout ladView;

    private AdView adView;

    EditText lilLiveUrl;
    String string;
    boolean result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_url_player);

        ImageView img_backs = findViewById(R.id.img_backs);
        img_backs.setOnClickListener(view -> onBackPressed());

        AudienceNetworkAds.initialize(this);

        loadNativeAd();

        lilLiveUrl = findViewById(R.id.lilLiveUrl);
        Button btnplay = findViewById(R.id.btnplay);
        Button btnDownload = findViewById(R.id.btn_download);
        Button btnHowUse = findViewById(R.id.btn_how_use);
        result = checkPermission();
        lilLiveUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().toLowerCase(Locale.ROOT).contains(".mp4") || editable.toString().contains(".mkv")) {
                    btnDownload.setAlpha(1);
                    btnDownload.setClickable(true);
                    btnDownload.setEnabled(true);
                } else {
                    btnDownload.setAlpha(0.5f);
                    btnDownload.setClickable(false);
                    btnDownload.setEnabled(false);
                }
            }
        });
        btnDownload.setOnClickListener(view -> {
            if (result) downloadVideo(lilLiveUrl.getText().toString());
            else checkPermission();
//            else checkAgain();
        });
        btnHowUse.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/+Lt4r7LDztBY5ZGM1"));
            startActivity(browserIntent);
        });
        btnplay.setOnClickListener(view -> {
            string = lilLiveUrl.getText().toString();
            if (!string.equals("")) {
                Intent intent = new Intent(WebUrlPlayer.this, VideoActivity.class);
                intent.putExtra("Link", string);
                startActivity(intent);
            }

//            AudienceNetworkAds.initialize(this);
//
//            loadNativeAd();
        });

        adView = new AdView(this, getResources().getString(R.string.Facebook_banner_placement), AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        // Add the ad view to your activity layout
        adContainer.addView(adView);
        // Request an ad
        adView.loadAd();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission2() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(WebUrlPlayer.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(WebUrlPlayer.this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(WebUrlPlayer.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(WebUrlPlayer.this, Manifest.permission.READ_MEDIA_VIDEO)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(WebUrlPlayer.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write Storage permission is necessary to Download Videos!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, (dialog, which) -> requestPermission());
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    requestPermission();
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    public boolean checkPermission() {

        if (ContextCompat.checkSelfPermission(WebUrlPlayer.this,
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        ? Manifest.permission.READ_MEDIA_VIDEO
                        : Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(WebUrlPlayer.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage("Write Storage permission is necessary to Download Videos!!!");
            alertBuilder.setPositiveButton(android.R.string.yes, (dialog, which) -> requestPermission());
            AlertDialog alert = alertBuilder.create();
            alert.show();

            return false;

        } else {
            return true;
        }

    }

    void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(WebUrlPlayer.this, new String[]{Manifest.permission.READ_MEDIA_VIDEO}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        } else {
            ActivityCompat.requestPermissions(WebUrlPlayer.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
    }


    public void checkAgain() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(WebUrlPlayer.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(WebUrlPlayer.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    requestPermission();
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
//            ActivityCompat.requestPermissions(WebUrlPlayer.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }
    }

    //Here you can check App Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadVideo(lilLiveUrl.getText().toString());
                } else {
                    //code for deny
                    checkPermission();
//                    checkAgain();
                }
                break;
        }
    }

    private void downloadVideo(String url) {
        if (!url.isEmpty()) {
            try {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                String title = URLUtil.guessFileName(url, null, null);
                request.setTitle(title);
                request.setDescription("Downloading file please wait....");
                String cookie = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookie);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, title);
                DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                manager.enqueue(request);
                Toast.makeText(this, "Downloading start....", Toast.LENGTH_SHORT).show();
            } catch (Exception e){
                Toast.makeText(this, "Please Enter Valid Url", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadNativeAd() {
        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        nativeAd = new NativeAd(this, getResources().getString(R.string.Facebook_Native_placement));
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }
        };
        // Request an ad
        nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
    }

    private void inflateAd(NativeAd nativeAd) {
        nativeAd.unregisterView();
        // Add the Ad view into the ad container.
        nativeAdLayout = findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(WebUrlPlayer.this);
        // Inflate the Ad view. The layout referenced should be the one you created in the last step.
        ladView = (LinearLayout) inflater.inflate(R.layout.custom_native_ad_layout, nativeAdLayout, false);
        nativeAdLayout.addView(ladView);
        // Add the AdOptionsView
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(WebUrlPlayer.this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);
        // Create native UI using the ad metadata.
        MediaView nativeAdIcon = ladView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = ladView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = ladView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = ladView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = ladView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = ladView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = ladView.findViewById(R.id.native_ad_call_to_action);
        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());
        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                ladView,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);

    }
}