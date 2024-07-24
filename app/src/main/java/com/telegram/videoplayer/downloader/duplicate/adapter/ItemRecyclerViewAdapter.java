package com.telegram.videoplayer.downloader.duplicate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.duplicate.Model.Duplicate;
import com.telegram.videoplayer.downloader.duplicate.utilts.Utils;

import java.util.ArrayList;

@SuppressWarnings({"FieldMayBeFinal", "ConstantConditions"})
public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ItemViewHolder> {
    private final Context context;
    private final ArrayList<Duplicate> mDuplicates;

    public ItemRecyclerViewAdapter(Context context2, ArrayList<Duplicate> arrayList) {
        this.context = context2;
        this.mDuplicates = arrayList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_custom_row_layout, viewGroup, false));
    }

    public void onBindViewHolder(final ItemViewHolder itemViewHolder, int i) {
        final Duplicate duplicate = this.mDuplicates.get(i);
        itemViewHolder.itemLabel.setText(getFileName(duplicate.getFile().getPath()));
        itemViewHolder.tvSize.setText(Utils.formatSize(duplicate.getFile().length()));
        itemViewHolder.tvPath.setText(duplicate.getFile().getPath());
        itemViewHolder.ivCheckbox.setChecked(duplicate.isChecked());
        try {
            Glide.with(this.context);
            RequestManager with = Glide.with(this.context);
            with.load("file://" + duplicate.getFile().getPath()).into(itemViewHolder.image);
        } catch (Exception e) {
            Toast.makeText(this.context, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        itemViewHolder.rlCard.setOnClickListener(view -> {
            if (itemViewHolder.ivCheckbox.isChecked()) {
                itemViewHolder.ivCheckbox.setChecked(false);
                duplicate.setChecked(false);
                return;
            }
            itemViewHolder.ivCheckbox.setChecked(true);
            duplicate.setChecked(true);
        });
    }


    public String getFileName(String str) {
        return str.substring(str.lastIndexOf("/") + 1);
    }

    @Override
    public int getItemCount() {
        return this.mDuplicates.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public final TextView itemLabel;
        public final RelativeLayout rlCard;
        public final TextView tvPath;
        public final TextView tvSize;
        final ImageView image;
        final AppCompatCheckBox ivCheckbox;
        final ImageView ivPlay;

        public ItemViewHolder(View view) {
            super(view);
            this.itemLabel = view.findViewById(R.id.name);
            this.tvSize = view.findViewById(R.id.size);
            this.tvPath = view.findViewById(R.id.path);
            this.image = view.findViewById(R.id.image);
            this.ivCheckbox = view.findViewById(R.id.checked);
            this.ivPlay = view.findViewById(R.id.play);
            this.rlCard = view.findViewById(R.id.rlCard);
        }
    }
}
