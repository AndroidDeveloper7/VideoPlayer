package com.telegram.videoplayer.downloader.duplicate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.duplicate.Model.DataModel;

import java.util.ArrayList;

@SuppressWarnings("FieldMayBeFinal")
public class SectionRecyclerViewAdapter extends RecyclerView.Adapter<SectionRecyclerViewAdapter.SectionViewHolder> {
    private final Context context;
    private final RecyclerViewType recyclerViewType;
    private final ArrayList<DataModel> sectionModelArrayList;

    public SectionRecyclerViewAdapter(Context context2, RecyclerViewType recyclerViewType2, ArrayList<DataModel> arrayList) {
        this.context = context2;
        this.recyclerViewType = recyclerViewType2;
        this.sectionModelArrayList = arrayList;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new SectionViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.section_custom_row_layout, viewGroup, false));
    }

    public void onBindViewHolder(SectionViewHolder sectionViewHolder, int i) {
        DataModel dataModel = this.sectionModelArrayList.get(i);
        sectionViewHolder.sectionLabel.setText(dataModel.getTitleGroup());
        sectionViewHolder.itemRecyclerView.setHasFixedSize(true);
        sectionViewHolder.itemRecyclerView.setNestedScrollingEnabled(false);
        int i2 = AnonymousClass1.bde[this.recyclerViewType.ordinal()];
        if (i2 == 1) {
            sectionViewHolder.itemRecyclerView.setLayoutManager(new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false));
        } else if (i2 == 2) {
            sectionViewHolder.itemRecyclerView.setLayoutManager(new LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false));
        } else if (i2 == 3) {
            sectionViewHolder.itemRecyclerView.setLayoutManager(new GridLayoutManager(this.context, 3));
        }
        sectionViewHolder.itemRecyclerView.setAdapter(new ItemRecyclerViewAdapter(this.context, dataModel.getListDuplicate()));
    }

    @Override
    public int getItemCount() {
        return this.sectionModelArrayList.size();
    }

    public static class AnonymousClass1 {
        static final int[] bde;

        static {
            int[] iArr = new int[RecyclerViewType.values().length];
            bde = iArr;
            iArr[RecyclerViewType.LINEAR_VERTICAL.ordinal()] = 1;
            iArr[RecyclerViewType.LINEAR_HORIZONTAL.ordinal()] = 2;
            try {
                iArr[RecyclerViewType.GRID.ordinal()] = 3;
            } catch (NoSuchFieldError e) {
                e.printStackTrace();
            }
        }

    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        public final RecyclerView itemRecyclerView;
        public final TextView sectionLabel;

        public SectionViewHolder(View view) {
            super(view);
            this.sectionLabel = view.findViewById(R.id.section_label);
            this.itemRecyclerView = view.findViewById(R.id.item_recycler_view);
        }
    }
}
