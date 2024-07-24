package com.telegram.videoplayer.downloader.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.telegram.videoplayer.downloader.Adshandler;
import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.fragments.MainActivityFragment;
import com.telegram.videoplayer.downloader.utildata.ScreenUtility;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings({"unchecked", "rawtypes", "ConstantConditions"})
public class DashBoardActivity extends BaseActivity {
    Bundle bundle;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_dash_board);

        getWindow().setFlags(1024, 1024);
        new ArrayList().add(R.menu.bottom_navigation_items);
        Adshandler.loadad(this);
        this.bundle = bundle;
        if (Build.VERSION.SDK_INT < 23) {
            if (bundle == null && getIntent() != null) {
                loadFragment(MainActivityFragment.newInstance(), ScreenUtility.fragTagVideo);
            }
        } else if (checkAndRequestPermissions()) {
            if (bundle == null && getIntent() != null) {
                loadFragment(MainActivityFragment.newInstance(), ScreenUtility.fragTagVideo);
            }
        }
    }

    private boolean checkAndRequestPermissions() {
        int checkSelfPermission = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
        int checkSelfPermission2 = ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
        int checkSelfPermission3 = ContextCompat.checkSelfPermission(this, "android.permission.READ_MEDIA_VIDEO");
        ArrayList arrayList = new ArrayList();
        if (checkSelfPermission != 0 && !isPhotoPickerAvailable()) {
            arrayList.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (checkSelfPermission2 != 0 && !isPhotoPickerAvailable()) {
            arrayList.add("android.permission.READ_EXTERNAL_STORAGE");
        }
        if (checkSelfPermission3 != 0 && isPhotoPickerAvailable()) {
            arrayList.add("android.permission.READ_MEDIA_VIDEO");
        }
        if (arrayList.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions(this, (String[]) arrayList.toArray(new String[0]), 2000);
        return false;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DashBoardActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void loadFragment(Fragment fragment, String str) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment findFragmentById = getSupportFragmentManager().findFragmentById(R.id.content);
        if (findFragmentById == null || !fragment.getClass().equals(findFragmentById.getClass())) {
            FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
            beginTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            beginTransaction.replace(R.id.content, fragment);
            beginTransaction.addToBackStack(str);
            beginTransaction.commit();
        }
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }

    public Boolean isPhotoPickerAvailable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    @Override
    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 2000) {

            if (iArr.length > 0) {
                boolean hasPermission = false;
                for (int j : iArr) {
                    if (j == PackageManager.PERMISSION_GRANTED) {
                        hasPermission = true;
                        break;
                    }
                }
                if (hasPermission) {

                    loadFragment(MainActivityFragment.newInstance(), ScreenUtility.fragTagVideo);

                } else {

                    Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_SHORT).show();

                }
            }

            return;






            // OLD CODE
            /*HashMap hashMap = new HashMap();
            hashMap.put("android.permission.WRITE_EXTERNAL_STORAGE", 0);
            hashMap.put("android.permission.READ_EXTERNAL_STORAGE", 0);
            hashMap.put("android.permission.READ_MEDIA_VIDEO", 0);
            if (iArr.length > 0) {
                for (int i2 = 0; i2 < strArr.length; i2++) {
                    hashMap.put(strArr[i2], iArr[i2]);
                }
                if ((Integer) hashMap.get("android.permission.WRITE_EXTERNAL_STORAGE") != 0 || (Integer) hashMap.get("android.permission.READ_EXTERNAL_STORAGE") != 0 || (Integer) hashMap.get("android.permission.READ_MEDIA_VIDEO") != 0) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_EXTERNAL_STORAGE") || ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_MEDIA_VIDEO")) {
                        showDialogOK((dialogInterface, i1) -> {
                            if (i1 == -1) {
                                checkAndRequestPermissions();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (bundle == null && getIntent() != null) {
                        loadFragment(MainActivityFragment.newInstance(), ScreenUtility.fragTagVideo);
                    }
                }
            }*/


        }
    }

    public void setData() {

        if (bundle == null && getIntent() != null) {
            loadFragment(MainActivityFragment.newInstance(), ScreenUtility.fragTagVideo);
        }

    }


    private void showDialogOK(DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(this).setMessage("Permission required for this app").setPositiveButton("OK", onClickListener).setNegativeButton("Cancel", onClickListener).create().show();
    }
}
