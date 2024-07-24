package com.telegram.videoplayer.downloader.activitys;

import static com.android.volley.VolleyLog.TAG;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.fragments.EndlessRecyclerViewScrollListener;
import com.telegram.videoplayer.downloader.fragments.HomeCategoryList;
import com.telegram.videoplayer.downloader.fragments.OLplayAdepter;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OnlinePlayer extends AppCompatActivity {
    public static OLplayAdepter adapter;
    private static String video_detail = "";
    YouTubePlayerView youtubePlayerView;
    String videoId = "";
    String channelId = "";
    TextView tvYvTitle, tvYvUploadDate, tvYvDetail, tvYvView;
    LinearLayout lilShare, lilWhatsapp;
    Button diogsHide, diogsShow;
    String nextPageToken = "";
    private RecyclerView recyclerView;
    private ArrayList<HomeCategoryList> tutorialList;

    private final String api_key = "AIzaSyBM-EZzcKthefWDSv_RsXwYgvy3gRK6dqs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_player);

        find_view_by_id();
        youtubePlayerView = findViewById(R.id.youtube_player_view);
        final Bundle extras = getIntent().getExtras();
        videoId = extras.getString("url_id");

        getLifecycle().addObserver(youtubePlayerView);

        youtubePlayerView.initialize(initializedYouTubePlayer -> initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady() {
                initializedYouTubePlayer.loadVideo(videoId, 0);
            }
        }), true);
        video_detail = "https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&id=" + videoId + "&key=" + api_key;

        video_ditail_fetch();
        more_ditails();
        Share_url();
        tutorialList = new ArrayList<>();
        loadTutorialList();
    }

    private void find_view_by_id() {
        recyclerView = findViewById(R.id.ol_category_recycler_view);
        tvYvTitle = findViewById(R.id.tv_yv_title);
        tvYvUploadDate = findViewById(R.id.tv_yv_upload_date);
        tvYvView = findViewById(R.id.tv_yv_view);
        tvYvDetail = findViewById(R.id.tv_yv_detail);
        lilShare = findViewById(R.id.lil_share);
        lilWhatsapp = findViewById(R.id.lil_whatsapp);
        diogsHide = findViewById(R.id.diogs_hide);
        diogsShow = findViewById(R.id.diogs_show);
    }

    private void video_ditail_fetch() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, video_detail,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONObject tutorialsObject = obj.getJSONArray("items").getJSONObject(0);

                        String currentString = tutorialsObject.getJSONObject("snippet").getString("publishedAt");
                        String[] separated = currentString.split("T");

                        channelId = tutorialsObject.getJSONObject("snippet").getString("title");
                        tvYvTitle.setText(tutorialsObject.getJSONObject("snippet").getString("title"));
                        tvYvView.setText(tutorialsObject.getJSONObject("statistics").getString("viewCount"));
                        tvYvUploadDate.setText(separated[0]);
                        tvYvDetail.setText(tutorialsObject.getJSONObject("snippet").getString("description"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

            if (error instanceof NetworkError) {
            } else if (error instanceof ServerError) {
            } else if (error instanceof AuthFailureError) {
            } else if (error instanceof ParseError) {
            } else if (error instanceof NoConnectionError) {
            } else if (error instanceof TimeoutError) {
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setTag(TAG);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void Share_url() {
        lilShare.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Sharing VIdeo");
            i.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=" + videoId);
            try {
                startActivity(Intent.createChooser(i, "Share URL"));
            } catch (ActivityNotFoundException e) {
                startActivity(Intent.createChooser(i, null));
            }
        });

        lilWhatsapp.setOnClickListener(v -> {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage("com.whatsapp");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=" + videoId);
            shareIntent.setType("text/plain");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "Kindly install whatsapp first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void more_ditails() {
        diogsHide.setOnClickListener(v -> {
            diogsShow.setVisibility(View.VISIBLE);
            diogsHide.setVisibility(View.GONE);
            tvYvDetail.setVisibility(View.VISIBLE);

        });
        diogsShow.setOnClickListener(v -> {
            diogsShow.setVisibility(View.GONE);
            diogsHide.setVisibility(View.VISIBLE);
            tvYvDetail.setVisibility(View.GONE);
        });
    }


    public void recyclerView_code() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                fetch_new_data();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void fetch_new_data() {
        String JSON_URL_NEW = "https://www.googleapis.com/youtube/v3/videos?pageToken=" + nextPageToken + "&part=contentDetails&chart=mostPopular&regionCode=IN&maxResults=10&key=" + api_key;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL_NEW,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        nextPageToken = obj.getString("nextPageToken");
                        JSONArray tutorialsArray = obj.getJSONArray("items");
                        for (int i = 0; i < tutorialsArray.length(); i++) {
                            JSONObject tutorialsObject = tutorialsArray.getJSONObject(i);
                            if (!tutorialsObject.getJSONObject("contentDetails").has("regionRestriction")) {
                                if (!tutorialsObject.getString("id").equals(videoId)) {
                                    tutorialList.add(new HomeCategoryList(tutorialsObject.getString("id")));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

            if (error instanceof NetworkError) {
            } else if (error instanceof ServerError) {
            } else if (error instanceof AuthFailureError) {
            } else if (error instanceof ParseError) {
            } else if (error instanceof NoConnectionError) {
            } else if (error instanceof TimeoutError) {
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setTag(TAG);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void loadTutorialList() {
        String JSON_URL = "https://www.googleapis.com/youtube/v3/videos?part=contentDetails&chart=mostPopular&regionCode=IN&maxResults=10&key=" + api_key;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        nextPageToken = obj.getString("nextPageToken");
                        JSONArray tutorialsArray = obj.getJSONArray("items");
                        for (int i = 0; i < tutorialsArray.length(); i++) {
                            JSONObject tutorialsObject = tutorialsArray.getJSONObject(i);
                            if (!tutorialsObject.getJSONObject("contentDetails").has("regionRestriction")) {
                                if (!tutorialsObject.getString("id").equals(videoId)) {
                                    tutorialList.add(new HomeCategoryList(tutorialsObject.getString("id")));
                                }
                            }
                        }
                        adapter = new OLplayAdepter(OnlinePlayer.this, tutorialList);
                        recyclerView_code();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

            if (error instanceof NetworkError) {
            } else if (error instanceof ServerError) {
            } else if (error instanceof AuthFailureError) {
            } else if (error instanceof ParseError) {
            } else if (error instanceof NoConnectionError) {
            } else if (error instanceof TimeoutError) {
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setTag(TAG);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
