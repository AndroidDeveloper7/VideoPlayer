package com.telegram.videoplayer.downloader.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kabouzeid.appthemehelper.util.MaterialDialogsUtil;


@SuppressWarnings("EmptyMethod")
public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        MaterialDialogsUtil.updateMaterialDialogsThemeSingleton(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
