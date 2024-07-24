package com.telegram.videoplayer.downloader.activitys;

import static com.android.volley.VolleyLog.TAG;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.telegram.videoplayer.downloader.fragments.HomeCategoryList;
import com.telegram.videoplayer.downloader.fragments.OLhomeAdapter;
import com.telegram.videoplayer.downloader.fragments.OLslider;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

@SuppressWarnings("EmptyMethod")
public class Online extends AppCompatActivity {
    private static final String JSON_URL = "https://www.googleapis.com/youtube/v3/videos?part=contentDetails&chart=mostPopular&regionCode=IN&maxResults=10&key=AIzaSyBM-EZzcKthefWDSv_RsXwYgvy3gRK6dqs";
    private static final String SLIDER_JSON_URL = "https://www.googleapis.com/youtube/v3/videos?part=contentDetails&chart=mostPopular&regionCode=IN&key=AIzaSyBM-EZzcKthefWDSv_RsXwYgvy3gRK6dqs";
    public static OLhomeAdapter adapter;
    LinearLayout lilActionBar;
    RelativeLayout rloScreen, rloSearchBar;
    String nextPageToken = "";
    SliderLayout sliderLayout;
    ImageView imgBack, imgClear;
    EditText etSearch;
    LinearLayout olActionBar;
    private ImageView img_backs;
    private ImageView imgIcSearch;
    private XRecyclerView recyclerView;
    private ArrayList<HomeCategoryList> tutorialList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        find_view_by_id();
        bottom_navigation();
        search_code();

        tutorialList = new ArrayList<>();
        loadTutorialList();

    }

    private void find_view_by_id() {
        img_backs = findViewById(R.id.img_backs);
        img_backs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imgIcSearch = findViewById(R.id.img_ic_search);
        olActionBar = findViewById(R.id.ol_action_bar);
        recyclerView = findViewById(R.id.ol_category_recycler_view);
        rloScreen = findViewById(R.id.rlo_screen);
        rloSearchBar = findViewById(R.id.rlo_search_bar);
        lilActionBar = findViewById(R.id.lil_action_bar);
        etSearch = findViewById(R.id.et_search);
        imgBack = findViewById(R.id.img_back);
        imgClear = findViewById(R.id.img_clear);
    }

    private void bottom_navigation() {
    }


    private void search_code() {
        imgIcSearch.setOnClickListener(v -> {
            rloSearchBar.setVisibility(View.VISIBLE);
            lilActionBar.setVisibility(View.GONE);
        });
        imgBack.setOnClickListener(v -> {

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.rlo_screen);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (fragment != null) {
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
            }
            if (fragment instanceof OLslider) {
            } else {
                rloSearchBar.setVisibility(View.GONE);
                lilActionBar.setVisibility(View.VISIBLE);
                etSearch.setText(null);
            }


        });
        imgClear.setOnClickListener(v -> etSearch.setText(null));

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String text = String.valueOf(etSearch.getText());
                if (!text.isEmpty()) {
                    OLslider frag = new OLslider();
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.add(R.id.rlo_screen, frag, null);
                    transaction.commit();

                    Bundle arguments = new Bundle();
                    arguments.putString("search_string", text);
                    frag.setArguments(arguments);

                }
                return true;
            }
            return false;
        });

    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.rlo_screen);
        if (fragment instanceof OLslider) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();

            rloSearchBar.setVisibility(View.GONE);
            lilActionBar.setVisibility(View.VISIBLE);
            etSearch.setText(null);
        } else {
            startActivity(new Intent(Online.this, MainActivity.class));
            finish();
        }
    }

    private void setSliderViews() {
        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.FILL);
        sliderLayout.setScrollTimeInSec(1);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, SLIDER_JSON_URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray tutorialsArrays = obj.getJSONArray("items");
                        for (int i = 0; i < tutorialsArrays.length(); i++) {

                            JSONObject tutorialsObject = tutorialsArrays.getJSONObject(i);
                            SliderView sliderView = new SliderView(Online.this);
                            sliderView.setImageUrl("http://img.youtube.com/vi/" + tutorialsObject.getString("id") + "/0.jpg");
                            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                            new LongOperation(sliderView, tutorialsObject.getString("id")).execute(tutorialsObject.getString("id"));
                            final String finalI = tutorialsObject.getString("id");
                            sliderView.setOnSliderClickListener(sliderView1 -> {
                                Intent intent = new Intent(Online.this, OnlinePlayer.class);
                                intent.putExtra("url_id", finalI);
                                startActivity(intent);

                            });
                            sliderLayout.addSliderView(sliderView);
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setTag(TAG);
        RequestQueue requestQueue = Volley.newRequestQueue(Online.this);
        requestQueue.add(stringRequest);
    }

    public void recyclerView_code() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setPullRefreshEnabled(false);
        View header = LayoutInflater.from(this).inflate(R.layout.slider_header, findViewById(android.R.id.content), false);
        sliderLayout = header.findViewById(R.id.imageSlider);
        setSliderViews();
        recyclerView.addHeaderView(header);
        recyclerView.setAdapter(adapter);

        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                recyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
                fetch_new_data();
            }
        });
    }

    private void fetch_new_data() {
        String JSON_URL_NEW = "https://www.googleapis.com/youtube/v3/videos?pageToken=" + nextPageToken + "&part=contentDetails&chart=mostPopular&regionCode=IN&maxResults=10&key=AIzaSyBM-EZzcKthefWDSv_RsXwYgvy3gRK6dqs";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL_NEW,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        nextPageToken = obj.getString("nextPageToken");
                        JSONArray tutorialsArray = obj.getJSONArray("items");
                        for (int i = 0; i < tutorialsArray.length(); i++) {
                            JSONObject tutorialsObject = tutorialsArray.getJSONObject(i);
                            if (!tutorialsObject.getJSONObject("contentDetails").has("regionRestriction")) {
                                tutorialList.add(new HomeCategoryList(tutorialsObject.getString("id")));
                            }
                        }
                        recyclerView.loadMoreComplete();
                        adapter.notifyDataSetChanged();
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
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        nextPageToken = obj.getString("nextPageToken");
                        JSONArray tutorialsArray = obj.getJSONArray("items");
                        for (int i = 0; i < tutorialsArray.length(); i++) {
                            JSONObject tutorialsObject = tutorialsArray.getJSONObject(i);
                            if (!tutorialsObject.getJSONObject("contentDetails").has("regionRestriction")) {
                                tutorialList.add(new HomeCategoryList(tutorialsObject.getString("id")));
                            }
                        }
                        adapter = new OLhomeAdapter(Online.this, tutorialList);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public static class LongOperation extends AsyncTask<String, Void, String> {
        final String[] title = {null};
        SliderView sliderView;
        String str_id;

        @SuppressWarnings("deprecation")
        public LongOperation(SliderView sliderView, String str_id) {
            this.sliderView = sliderView;
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
            sliderView.setDescription(title[0]);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


}
