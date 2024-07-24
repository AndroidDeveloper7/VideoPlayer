package com.telegram.videoplayer.downloader.track_selection_dialog;

import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.telegram.videoplayer.downloader.R;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.TrackSelectionView;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public final class TrackSelectionViewFragment extends Fragment implements TrackSelectionView.TrackSelectionListener {
    boolean isDisabled;
    List<DefaultTrackSelector.SelectionOverride> overrides;
    private boolean allowAdaptiveSelections;
    private boolean allowMultipleOverrides;

    @SuppressWarnings("deprecation")
    public TrackSelectionViewFragment() {
        setRetainInstance(true);
    }

    public void init(boolean z, DefaultTrackSelector.SelectionOverride selectionOverride, boolean z2, boolean z3) {
        this.isDisabled = z;
        this.overrides = selectionOverride == null ? Collections.emptyList() : Collections.singletonList(selectionOverride);
        this.allowAdaptiveSelections = z2;
        this.allowMultipleOverrides = z3;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.cloneInContext(new ContextThemeWrapper(getActivity(), R.style.fragtheam)).inflate(R.layout.exo_track_selection_dialog, viewGroup, false);
        TrackSelectionView trackSelectionView = inflate.findViewById(R.id.exo_track_selection_view);
        trackSelectionView.setShowDisableOption(true);
        trackSelectionView.setAllowMultipleOverrides(this.allowMultipleOverrides);
        trackSelectionView.setAllowAdaptiveSelections(this.allowAdaptiveSelections);
        return inflate;
    }

    @Override
    public void onTrackSelectionChanged(boolean z, @NonNull List<DefaultTrackSelector.SelectionOverride> list) {
        this.isDisabled = z;
        this.overrides = list;
    }
}
