package com.telegram.videoplayer.downloader.fragments;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.activitys.OnlinePlayer;

import java.util.ArrayList;

public class OLSearchAdepter extends RecyclerView.Adapter<OLSearchAdepter.MyViewHolder> {
    private final Activity mContext;
    private final ArrayList<SearchVideoList> categoryList;

    public OLSearchAdepter(final Activity mContext, ArrayList<SearchVideoList> category_list) {

        this.mContext = mContext;
        this.categoryList = category_list;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final SearchVideoList search_video_list = categoryList.get(position);
        RequestOptions options = new RequestOptions()
                .centerCrop();

        Glide.with(mContext)
                .load("http://img.youtube.com/vi/" + search_video_list.getMsvl_videoId() + "/0.jpg")
                .apply(options)
                .into(holder.videoImg);
        holder.lilCategoryTitle.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, OnlinePlayer.class);
            intent.putExtra("url_id", search_video_list.getMsvl_videoId());
            mContext.startActivity(intent);


        });
        holder.tvChanul.setText(search_video_list.getMsvl_chanal());
        holder.tvTitle.setText(search_video_list.getMsvl_title());

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvChanul;
        public ImageView videoImg;
        public LinearLayout lilCategoryTitle;


        public MyViewHolder(View view) {
            super(view);
            videoImg = view.findViewById(R.id.video_img);
            tvTitle = view.findViewById(R.id.tv_title);
            tvChanul = view.findViewById(R.id.tv_chanul);
            lilCategoryTitle = view.findViewById(R.id.lil_category_title);
        }
    }

}

