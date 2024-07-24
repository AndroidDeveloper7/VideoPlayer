package com.telegram.videoplayer.downloader.track_selection_dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.telegram.videoplayer.downloader.R;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public final class TrackSelectionDialog extends DialogFragment {
    private static final String TAG = "TrackSelectionDialog";
    private final SparseArray<TrackSelectionViewFragment> tabFragments = new SparseArray<>();
    private final ArrayList<Integer> tabTrackTypes = new ArrayList<>();
    private DialogInterface.OnClickListener onClickListener;
    private DialogInterface.OnDismissListener onDismissListener;
    private int titleId;

    @SuppressWarnings("deprecation")
    public TrackSelectionDialog() {
        setRetainInstance(true);
    }

    private static boolean isSupportedTrackType(int i) {
        return i == 1 || i == 2 || i == 3;
    }

    public static boolean willHaveContent(DefaultTrackSelector defaultTrackSelector) {
        MappingTrackSelector.MappedTrackInfo currentMappedTrackInfo = defaultTrackSelector.getCurrentMappedTrackInfo();
        return currentMappedTrackInfo != null && willHaveContent(currentMappedTrackInfo);
    }

    public static boolean willHaveContent(MappingTrackSelector.MappedTrackInfo mappedTrackInfo) {
        for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
            if (showTabForRenderer(mappedTrackInfo, i)) {
                return true;
            }
        }
        return false;
    }

    public static TrackSelectionDialog createForTrackSelector(final DefaultTrackSelector defaultTrackSelector, DialogInterface.OnDismissListener onDismissListener2) {
        final MappingTrackSelector.MappedTrackInfo mappedTrackInfo = Assertions.checkNotNull(defaultTrackSelector.getCurrentMappedTrackInfo());
        final TrackSelectionDialog trackSelectionDialog = new TrackSelectionDialog();
        final DefaultTrackSelector.Parameters parameters = defaultTrackSelector.getParameters();
        trackSelectionDialog.init(R.string.track_selection_title, mappedTrackInfo, parameters, (dialogInterface, i) -> TrackSelectionDialog.createForTrackSelector(parameters, mappedTrackInfo, trackSelectionDialog, defaultTrackSelector), onDismissListener2);
        return trackSelectionDialog;
    }

    static void createForTrackSelector(DefaultTrackSelector.Parameters parameters, MappingTrackSelector.MappedTrackInfo mappedTrackInfo, TrackSelectionDialog trackSelectionDialog, DefaultTrackSelector defaultTrackSelector) {
        DefaultTrackSelector.ParametersBuilder buildUpon = parameters.buildUpon();
        for (int i2 = 0; i2 < mappedTrackInfo.getRendererCount(); i2++) {
            buildUpon.clearSelectionOverrides(i2).setRendererDisabled(i2, trackSelectionDialog.getIsDisabled(i2));
            List<DefaultTrackSelector.SelectionOverride> overrides = trackSelectionDialog.getOverrides(i2);
            if (!overrides.isEmpty()) {
                Log.d(TAG, "override: " + new Gson().toJson(overrides.get(0)));
                buildUpon.setSelectionOverride(i2, mappedTrackInfo.getTrackGroups(i2), overrides.get(0));
            }
        }
        defaultTrackSelector.setParameters(buildUpon);
    }

    public static boolean showTabForRenderer(MappingTrackSelector.MappedTrackInfo mappedTrackInfo, int i) {
        if (mappedTrackInfo.getTrackGroups(i).length == 0) {
            return false;
        }
        return isSupportedTrackType(mappedTrackInfo.getRendererType(i));
    }

    private void init(int i, MappingTrackSelector.MappedTrackInfo mappedTrackInfo, DefaultTrackSelector.Parameters parameters, DialogInterface.OnClickListener onClickListener2, DialogInterface.OnDismissListener onDismissListener2) {
        this.titleId = i;
        this.onClickListener = onClickListener2;
        this.onDismissListener = onDismissListener2;
        for (int i2 = 0; i2 < mappedTrackInfo.getRendererCount(); i2++) {
            if (showTabForRenderer(mappedTrackInfo, i2)) {
                int rendererType = mappedTrackInfo.getRendererType(i2);
                TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(i2);
                TrackSelectionViewFragment trackSelectionViewFragment = new TrackSelectionViewFragment();
                trackSelectionViewFragment.init(parameters.getRendererDisabled(i2), parameters.getSelectionOverride(i2, trackGroups), true, false);
                this.tabFragments.put(i2, trackSelectionViewFragment);
                this.tabTrackTypes.add(rendererType);
            }
        }
    }

    public boolean getIsDisabled(int i) {
        TrackSelectionViewFragment trackSelectionViewFragment = this.tabFragments.get(i);
        return trackSelectionViewFragment != null && trackSelectionViewFragment.isDisabled;
    }

    public List<DefaultTrackSelector.SelectionOverride> getOverrides(int i) {
        TrackSelectionViewFragment trackSelectionViewFragment = this.tabFragments.get(i);
        return trackSelectionViewFragment == null ? Collections.emptyList() : trackSelectionViewFragment.overrides;
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AppCompatDialog appCompatDialog = new AppCompatDialog(getActivity(), R.style.CustomDialog);
        appCompatDialog.setTitle(this.titleId);
        return appCompatDialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        this.onDismissListener.onDismiss(dialogInterface);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        int i = 0;
        View inflate = layoutInflater.inflate(R.layout.track_selection_dialog, viewGroup, false);
        TabLayout tabLayout = inflate.findViewById(R.id.track_selection_dialog_tab_layout);
        ViewPager viewPager = inflate.findViewById(R.id.track_selection_dialog_view_pager);
        Button button = inflate.findViewById(R.id.track_selection_dialog_cancel_button);
        Button button2 = inflate.findViewById(R.id.track_selection_dialog_ok_button);
        viewPager.setAdapter(new FragmentAdapter(getChildFragmentManager(), requireContext(), this.tabFragments, this.tabTrackTypes));
        tabLayout.setupWithViewPager(viewPager);
        if (this.tabFragments.size() <= 1) {
            i = 8;
        }
        tabLayout.setVisibility(i);
        button.setOnClickListener(view -> TrackSelectionDialog.this.lambda$onCreateView$1$TrackSelectionDialog());
        button2.setOnClickListener(view -> TrackSelectionDialog.this.lambda$onCreateView$2$TrackSelectionDialog());
        return inflate;
    }

    public void lambda$onCreateView$1$TrackSelectionDialog() {
        dismiss();
    }

    public void lambda$onCreateView$2$TrackSelectionDialog() {
        this.onClickListener.onClick(getDialog(), -1);
        dismiss();
    }
}
