package com.telegram.videoplayer.downloader.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
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

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class OLplayAdepter extends RecyclerView.Adapter<OLplayAdepter.MyViewHolder> {
    private final Activity mContext;
    private final ArrayList<HomeCategoryList> categoryList;

    public OLplayAdepter(final Activity mContext, ArrayList<HomeCategoryList> category_list) {

        this.mContext = mContext;
        this.categoryList = category_list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ol_home, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final HomeCategoryList home_category_list = categoryList.get(position);
        new LongOperation(holder.tvTitle, holder.videoImg, home_category_list.getVideoId()).execute(home_category_list.getVideoId());
        RequestOptions options = new RequestOptions()
                .centerCrop();
        Glide.with(mContext)
                .load("http://img.youtube.com/vi/" + home_category_list.getVideoId() + "/0.jpg")
                .apply(options)
                .into(holder.videoImg);

        holder.lilCategoryTitle.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, OnlinePlayer.class);
            intent.putExtra("url_id", home_category_list.getVideoId());
            mContext.startActivity(intent);

        });


    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public ImageView videoImg;
        public LinearLayout lilCategoryTitle;


        public MyViewHolder(View view) {
            super(view);
            videoImg = view.findViewById(R.id.video_img);
            tvTitle = view.findViewById(R.id.tv_title);
            lilCategoryTitle = view.findViewById(R.id.lil_category_title);
        }
    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        final String[] title = {null};
        ImageView video_img;
        TextView tv_title;
        String str_id;

        @SuppressWarnings("deprecation")
        public LongOperation(TextView tv_title, ImageView video_img, String str_id) {
            this.video_img = video_img;
            this.tv_title = tv_title;
            this.str_id = str_id;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(String... params) {
            String youtubeVideoId = params[0];
            try {
                if (youtubeVideoId != null) {
                    InputStream is = new URL("http://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=" + youtubeVideoId + "&format=json").openStream();
                    Log.e("is", String.valueOf(is));
                    title[0] = new JSONObject(IOUtils.toString(is)).getString("title");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return title[0];
        }

        @Override
        protected void onPostExecute(String result) {
            tv_title.setText(title[0]);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
