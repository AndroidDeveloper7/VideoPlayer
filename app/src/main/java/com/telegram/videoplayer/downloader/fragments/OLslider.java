package com.telegram.videoplayer.downloader.fragments;

import static com.android.volley.VolleyLog.TAG;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class OLslider extends Fragment {
    public static OLSearchAdepter adapter;
    private static String JSON_URL = "";
    String nextPageToken = "";
    private String searchString;
    private ArrayList<SearchVideoList> tutorialList;
    private RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ol_slider, container, false);
        recyclerView = v.findViewById(R.id.ol_category_recycler_view);

//        searchString = Objects.requireNonNull(getArguments()).getString("searchString");
        searchString = requireArguments().getString("search_string");
        JSON_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=25&q=" + searchString + "&key=AIzaSyBM-EZzcKthefWDSv_RsXwYgvy3gRK6dqs";
        tutorialList = new ArrayList<>();
        loadTutorialList();

        return v;
    }

    private void loadTutorialList() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        nextPageToken = obj.getString("nextPageToken");
                        JSONArray tutorialsArray = obj.getJSONArray("items");
                        for (int i = 0; i < tutorialsArray.length(); i++) {
                            JSONObject tutorialsObject = tutorialsArray.getJSONObject(i);
                            if (tutorialsObject.getJSONObject("id").has("videoId")) {
                                tutorialList.add(new SearchVideoList(tutorialsObject.getJSONObject("id").getString("videoId"), tutorialsObject.getJSONObject("snippet").getString("title"), tutorialsObject.getJSONObject("snippet").getString("channelTitle")));
                            }
                        }
                        adapter = new OLSearchAdepter(getActivity(), tutorialList);
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setTag(TAG);
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        requestQueue.add(stringRequest);
    }

    public void recyclerView_code() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
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
        String JSON_URL_NEW = "https://www.googleapis.com/youtube/v3/search?pageToken=" + nextPageToken + "&part=snippet&maxResults=25&q=" + searchString + "&key=AIzaSyBM-EZzcKthefWDSv_RsXwYgvy3gRK6dqs";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL_NEW,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        nextPageToken = obj.getString("nextPageToken");
                        JSONArray tutorialsArray = obj.getJSONArray("items");
                        for (int i = 0; i < tutorialsArray.length(); i++) {
                            JSONObject tutorialsObject = tutorialsArray.getJSONObject(i);
                            if (tutorialsObject.getJSONObject("id").has("videoId")) {
                                tutorialList.add(new SearchVideoList(tutorialsObject.getJSONObject("id").getString("videoId"), tutorialsObject.getJSONObject("snippet").getString("title"), tutorialsObject.getJSONObject("snippet").getString("channelTitle")));
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
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        requestQueue.add(stringRequest);
    }


}
