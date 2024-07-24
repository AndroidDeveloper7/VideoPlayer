package com.telegram.videoplayer.downloader.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.model.VideoItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.holder> {
    public final SimpleExoPlayer player;
    private final Context ctx;
    private final List<VideoItem> list;

    public PlayListAdapter(Context context, List<VideoItem> list2, SimpleExoPlayer simpleExoPlayer) {
        this.ctx = context;
        this.list = list2;
        this.player = simpleExoPlayer;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new holder(LayoutInflater.from(this.ctx).inflate(R.layout.playlist_card, viewGroup, false));
    }

    public void onBindViewHolder(holder holder2, @SuppressLint("RecyclerView") int i) {
        holder2.position = i;
        Glide.with(this.ctx).load(this.list.get(i).getDATA()).into(holder2.thumb);
        holder2.name.setText(this.list.get(i).getDISPLAY_NAME());
        holder2.duration.setText(this.list.get(i).getDURATION());
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public class holder extends RecyclerView.ViewHolder {
        final TextView duration;
        final TextView name;
        final RoundedImageView thumb;
        int position;

        holder(View view) {
            super(view);
            this.thumb = view.findViewById(R.id.thumb);
            this.name = view.findViewById(R.id.name);
            this.duration = view.findViewById(R.id.duration);
            view.setOnClickListener(view1 -> PlayListAdapter.this.player.seekTo(holder.this.position, 0));
        }
    }
}
