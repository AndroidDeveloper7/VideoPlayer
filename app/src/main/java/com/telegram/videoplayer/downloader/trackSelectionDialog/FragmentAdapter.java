package com.telegram.videoplayer.downloader.trackSelectionDialog;

import android.content.Context;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.telegram.videoplayer.downloader.R;

import java.util.ArrayList;

@SuppressWarnings({"deprecation", "FieldMayBeFinal"})
public class FragmentAdapter extends FragmentPagerAdapter {
    private final SparseArray<TrackSelectionViewFragment> tabFragments;
    private final ArrayList<Integer> tabTrackTypes;
    private final Context context;

    @SuppressWarnings("deprecation")
    public FragmentAdapter(FragmentManager fragmentManager, Context context2, SparseArray<TrackSelectionViewFragment> sparseArray, ArrayList<Integer> arrayList) {
        super(fragmentManager);
        this.context = context2.getApplicationContext();
        this.tabFragments = sparseArray;
        this.tabTrackTypes = arrayList;
    }

    @NonNull
    public Fragment getItem(int i) {
        return this.tabFragments.valueAt(i);
    }

    @Override
    public int getCount() {
        return this.tabFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int i) {
        int intValue = this.tabTrackTypes.get(i);
        if (intValue == 1) {
            return this.context.getResources().getString(R.string.exo_track_selection_title_audio);
        }
        if (intValue == 2) {
            return this.context.getResources().getString(R.string.exo_track_selection_title_video);
        }
        if (intValue == 3) {
            return this.context.getResources().getString(R.string.exo_track_selection_title_text);
        }
        throw new IllegalArgumentException();
    }
}
