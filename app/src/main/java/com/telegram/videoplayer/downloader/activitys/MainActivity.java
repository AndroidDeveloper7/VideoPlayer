package com.telegram.videoplayer.downloader.activitys;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.onesignal.OneSignal;
import com.telegram.videoplayer.downloader.Adshandler;
import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.utildata.PreferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


@SuppressWarnings("rawtypes")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Dialog exitDailog;
    private static final String ONESIGNAL_APP_ID = "b26f9c9d-0668-43f3-b2d9-2124a7de72ea";

    public boolean shouldShowRequestPermissionRationale(String str) {
        return false;
    }

    String TAG = "Tag";
    private NativeAd nativeAd;
    private NativeAdLayout nativeAdLayout;
    private LinearLayout ladView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(1024, 1024);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        inIt();

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        FrameLayout nativeads = findViewById(R.id.native_ad_container);
        Adshandler.mAdmobNative(nativeads, this);
//        AudienceNetworkAds.initialize(this);

//        loadNativeAd();

        notificationPermission();



    }


    private void notificationPermission() {
//        if (PreferenceUtil.getInstance(getApplicationContext()).getNotification())
//            return;

        int checkSelfPermission = ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS");
        ArrayList arrayList = new ArrayList();
        if (checkSelfPermission != 0) {
            arrayList.add("android.permission.POST_NOTIFICATIONS");
        }
        if (arrayList.isEmpty()) {
            return;
        }
        ActivityCompat.requestPermissions(this, (String[]) arrayList.toArray(new String[0]), 201);
//        PreferenceUtil.getInstance(getApplicationContext()).setNotification(true);
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
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        // Inflate the Ad view. The layout referenced should be the one you created in the last step.
        ladView = (LinearLayout) inflater.inflate(R.layout.custom_native_ad_layout, nativeAdLayout, false);
        nativeAdLayout.addView(ladView);
        // Add the AdOptionsView
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(MainActivity.this, nativeAd, nativeAdLayout);
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

    private void inIt() {
        findViewById(R.id.lilTube).setOnClickListener(this);
        findViewById(R.id.lilLocal).setOnClickListener(this);
        findViewById(R.id.lilLiveUrl).setOnClickListener(this);
        findViewById(R.id.llShare).setOnClickListener(this);
        findViewById(R.id.llRate).setOnClickListener(this);
        findViewById(R.id.llFavorite).setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lilLocal:
                startActivity(new Intent(MainActivity.this, DashBoardActivity.class));
                return;
            case R.id.lilTube:
                Adshandler.showAd(MainActivity.this, new Adshandler.OnClose() {
                    @Override
                    public void onclick() {
                        startActivity(new Intent(MainActivity.this, Online.class));
                    }
                });
                return;
            case R.id.lilLiveUrl:
                Adshandler.showAd(MainActivity.this, new Adshandler.OnClose() {
                    @Override
                    public void onclick() {
                        startActivity(new Intent(MainActivity.this, WebUrlPlayer.class));
                    }
                });
                return;
            case R.id.llFavorite:
                startActivity(new Intent(MainActivity.this, FavVideoListActivity.class));
                return;
            case R.id.llRate:
                MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + MainActivity.this.getPackageName())));
                return;
            case R.id.llShare:
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.SUBJECT", getResources().getString(R.string.app_name));
                intent.putExtra("android.intent.extra.TEXT", "Download " + getResources().getString(R.string.app_name) + " app from   - https://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(Intent.createChooser(intent, "Share Application"));
                return;
            default:
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 2000) {
            HashMap hashMap = new HashMap();
            //noinspection unchecked
            hashMap.put("android.permission.WRITE_EXTERNAL_STORAGE", 0);
            //noinspection unchecked
            hashMap.put("android.permission.READ_EXTERNAL_STORAGE", 0);
            if (iArr.length > 0) {
                for (int i2 = 0; i2 < strArr.length; i2++) {
                    //noinspection unchecked
                    hashMap.put(strArr[i2], iArr[i2]);
                }
                if ((Integer) hashMap.get("android.permission.WRITE_EXTERNAL_STORAGE") != 0 || (Integer) hashMap.get("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_EXTERNAL_STORAGE")) {
                        showDialogOK((dialogInterface, i1) -> {
                            if (i1 == -1) {
                                checkAndRequestPermissions();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_SHORT).show();
                    }
                } else {
                }
            }
        }
    }

    private void showDialogOK(DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(this).setMessage("Permission required for this app").setPositiveButton("OK", onClickListener).setNegativeButton("Cancel", onClickListener).create().show();
    }

    private void checkAndRequestPermissions() {
        int checkSelfPermission = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
        int checkSelfPermission2 = ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
        ArrayList arrayList = new ArrayList();
        if (checkSelfPermission != 0) {
            //noinspection unchecked
            arrayList.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (checkSelfPermission2 != 0) {
            //noinspection unchecked
            arrayList.add("android.permission.READ_EXTERNAL_STORAGE");
        }
        if (arrayList.isEmpty()) {
            return;
        }
        ActivityCompat.requestPermissions(this, (String[]) arrayList.toArray(new String[0]), 2000);
    }

    @Override
    public void onBackPressed() {
        closeAppDialog();
    }

    public void closeAppDialog() {
        exitDailog = new Dialog(this);
        exitDailog.setContentView(R.layout.dialog_exit);
        exitDailog.getWindow();
        exitDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitDailog.setCanceledOnTouchOutside(false);
        exitDailog.getWindow().isFloating();
        exitDailog.setCancelable(false);

        TextView btnexit = (TextView) exitDailog.findViewById(R.id.buttonExit);
        TextView buttonCancel = (TextView) exitDailog.findViewById(R.id.buttonCancel);
        FrameLayout nativeads = exitDailog.findViewById(R.id.nativeads);
        Adshandler.mAdmobNative_small(nativeads, this);
        btnexit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finishAffinity();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                exitDailog.dismiss();
            }
        });
        exitDailog.show();
    }
}
