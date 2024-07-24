package com.telegram.videoplayer.downloader.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.telegram.videoplayer.downloader.Adshandler;
import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.activitys.VideoPlayerActivity;
import com.telegram.videoplayer.downloader.adapter.FolderAdapter;
import com.telegram.videoplayer.downloader.equalizer.EqualizerDataList;
import com.telegram.videoplayer.downloader.equalizer.VerticalSeekBar;
import com.telegram.videoplayer.downloader.equalizer.eq.Equalizers;
import com.telegram.videoplayer.downloader.filemanager.FilePicker;
import com.telegram.videoplayer.downloader.model.MediaQuery;
import com.telegram.videoplayer.downloader.model.VideoItem;
import com.telegram.videoplayer.downloader.model.folder;
import com.telegram.videoplayer.downloader.utildata.PreferenceUtil;
import com.telegram.videoplayer.downloader.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kabouzeid.appthemehelper.ThemeStore;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

@SuppressWarnings({"FieldCanBeLocal", "unchecked", "rawtypes", "ConstantConditions", "ResultOfMethodCallIgnored", "EmptyMethod", "UseCompareMethod"})
public class MainActivityFragment extends BaseFragment implements FolderAdapter.MyListener {
    private static final String TAG = "MainActivityold";
    public final ArrayList<EqualizerDataList> equalizerListData = new ArrayList<>();
    public final VerticalSeekBar[] seekBarFinal = new VerticalSeekBar[5];
    public SwipeRefreshLayout mSwipeRefreshLayout;
    AppCompatActivity activity;
    Context context;
    int pos;
    Utils prefenrencUtils;
    SharedPreferences sharedPreferences;
    private FolderAdapter folderAdapter;
    private ArrayList<folder> folderList;
    private ArrayList<folder> folderArrayList = new ArrayList<>();
    private PreferenceUtil preferenceUtil;
    private RecyclerView rvFolders;
    private int theme = 0;
    private AdView mAdView;

    public static MainActivityFragment newInstance() {
        return new MainActivityFragment();
    }

    @Override

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.activity_mainfragment, viewGroup, false);
        Context context2 = getContext();

        MobileAds.initialize(getContext(), initializationStatus -> {
        });

        mAdView = inflate.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        this.context = context2;
        this.sharedPreferences = context2.getSharedPreferences("EASYMONEY", 0);
        Context context3 = this.context;
        this.activity = (AppCompatActivity) context3;
        this.prefenrencUtils = new Utils(context3);
        setHasOptionsMenu(true);
        Toolbar toolbar = inflate.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((TextView) toolbar.getChildAt(0)).setTextSize(16.0f);
        this.preferenceUtil = PreferenceUtil.getInstance(this.context);
        this.theme = PreferenceUtil.getInstance(this.context).getGeneralTheme1();
        ThemeStore.accentColor(this.context);
        this.mSwipeRefreshLayout = inflate.findViewById(R.id.swipe);
        this.rvFolders = inflate.findViewById(R.id.recycle);
        new LinearLayoutManager(this.context, RecyclerView.VERTICAL, false);
        this.rvFolders.setLayoutManager(new LinearLayoutManager(this.context));
        this.mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(17170444), getResources().getColor(17170459), getResources().getColor(17170452), getResources().getColor(17170456), getResources().getColor(17170454));
        this.mSwipeRefreshLayout.setOnRefreshListener(MainActivityFragment.this::loadVideoFolder);
        try {
            File file = new File(String.valueOf(this.context.getExternalFilesDir(getResources().getString(R.string.private_folder_name))));
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inflate;
    }


    @Override
    public void onResume() {
        super.onResume();
        this.pos = this.context.getSharedPreferences("theme", 0).getInt("pos", 1);
        loadVideoFolder();
        try {
            PreferenceUtil.getInstance(this.context).getVideoURL().equalsIgnoreCase("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        MenuItem menuItem;
        menuInflater.inflate(R.menu.folder_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
        int folderSortOrder = this.preferenceUtil.getFolderSortOrder();
        if (folderSortOrder == 0) {
            menuItem = menu.findItem(R.id.name_sort_mode);
        } else if (folderSortOrder == 1) {
            menuItem = menu.findItem(R.id.date_taken_sort_mode);
        } else if (folderSortOrder == 2) {
            menuItem = menu.findItem(R.id.size_sort_mode);
        } else if (folderSortOrder != 3) {
            menuItem = menu.findItem(R.id.name_sort_mode);
        } else {
            menuItem = menu.findItem(R.id.numeric_sort_mode);
        }
        menuItem.setChecked(true);
        menu.findItem(R.id.ascending_sort_order).setChecked(this.preferenceUtil.getFolderAsc());
    }


    @SuppressWarnings("deprecation")
    @SuppressLint("WrongConstant")
    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == 21 && intent.hasExtra(FilePicker.EXTRA_FILE_PATH)) {
            File file = new File(intent.getStringExtra(FilePicker.EXTRA_FILE_PATH));
            ArrayList arrayList = new ArrayList();
            VideoItem videoItem = new VideoItem();
            videoItem.set_ID("kfdked");
            videoItem.setSIZE("");
            videoItem.setDATE("");
            videoItem.setDATA(file.getAbsolutePath());
            videoItem.setDISPLAY_NAME(file.getName());
            videoItem.setDURATION("");
            videoItem.setVideoSize(2121121);
            arrayList.add(videoItem);
            Adshandler.showAd(getActivity(), () -> {
                Intent intent2 = new Intent(context, VideoPlayerActivity.class);
                intent2.setFlags(402653184);
                intent2.putExtra("list", arrayList);
                intent2.putExtra("position", 0);
                startActivity(intent2);
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.ascending_sort_order:
                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                    this.preferenceUtil.saveFolderAsc(true);
                    sortByVideo();
                    break;
                } else {
                    menuItem.setChecked(false);
                    this.preferenceUtil.saveFolderAsc(false);
                    sortByVideo();
                    break;
                }
            case R.id.date_taken_sort_mode:
                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                    this.preferenceUtil.saveFolderSortOrder(1);
                    sortByVideo();
                    break;
                }
                break;
            case R.id.name_sort_mode:
                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                    this.preferenceUtil.saveFolderSortOrder(0);
                    sortByVideo();
                    break;
                }
                break;
            case R.id.nav_refresh:
                loadVideoFolder();
                return true;
            case R.id.numeric_sort_mode:
                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                    this.preferenceUtil.saveFolderSortOrder(3);
                    sortByVideo();
                    break;
                }
                break;
            case R.id.size_sort_mode:
                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                    this.preferenceUtil.saveFolderSortOrder(2);
                    sortByVideo();
                    break;
                }
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void sortByVideo() {
        int folderSortOrder = this.preferenceUtil.getFolderSortOrder();
        boolean folderAsc = this.preferenceUtil.getFolderAsc();
        if (folderSortOrder == 0) {
            if (folderAsc) {
                Collections.sort(this.folderList, Comparator.comparing(folder::getBucket));
            } else {
                Collections.sort(this.folderList, (folder, folder2) -> folder2.getBucket().compareTo(folder.getBucket()));
            }
        } else if (folderSortOrder == 1) {
            if (folderAsc) {
                Collections.sort(this.folderList, Comparator.comparing(folder::getDate));
            } else {
                Collections.sort(this.folderList, (folder, folder2) -> folder2.getDate().compareTo(folder.getDate()));
            }
        } else if (folderSortOrder == 2) {
            if (folderAsc) {
                Collections.sort(this.folderList, (folder, folder2) -> Long.valueOf(folder.getFolderSize()).compareTo(folder2.getFolderSize()));
            } else {
                Collections.sort(this.folderList, (folder, folder2) -> Long.valueOf(folder2.getFolderSize()).compareTo(folder.getFolderSize()));
            }
        } else if (folderSortOrder == 3) {
            if (folderAsc) {
                Collections.sort(this.folderList, Comparator.comparing(folder::getCount));
            } else {
                Collections.sort(this.folderList, (folder, folder2) -> folder2.getCount().compareTo(folder.getCount()));
            }
        }
        this.rvFolders.getRecycledViewPool().clear();
        this.folderAdapter.notifyDataSetChanged();
    }

    public void loadVideoFolder() {
        this.folderList = null;
        folderArrayList.clear();
        int folderSortOrder = this.preferenceUtil.getFolderSortOrder();
        boolean folderAsc = this.preferenceUtil.getFolderAsc();
        this.mSwipeRefreshLayout.setRefreshing(true);
        this.folderList = new MediaQuery(this.context).getFolderList();
        Log.e(TAG, "loadVideoFolder: ================================,,,,,,,,,,,,,,pppppppppppppp11111111==?>>" + this.folderList.size());
        ArrayList<folder> arrayList = this.folderList;
//        for (int i = 0; i < folderList.size(); i++) {
//            if (i != 0 && i % 2 == 0) {
//                folderList.add(null);
//            }
//        }

        if (arrayList != null) {
            if (folderSortOrder == 0) {
                if (folderAsc) {
                    Collections.sort(arrayList, Comparator.comparing(folder::getBucket));
                } else {
                    Collections.sort(arrayList, (folder, folder2) -> folder2.getBucket().compareTo(folder.getBucket()));
                }
            } else if (folderSortOrder == 1) {
                if (folderAsc) {
                    Collections.sort(arrayList, Comparator.comparing(folder::getDate));
                } else {
                    Collections.sort(arrayList, (folder, folder2) -> folder2.getDate().compareTo(folder.getDate()));
                }
            } else if (folderSortOrder == 2) {
                if (folderAsc) {
                    Collections.sort(arrayList, (folder, folder2) -> Long.valueOf(folder.getFolderSize()).compareTo(folder2.getFolderSize()));
                } else {
                    Collections.sort(arrayList, (folder, folder2) -> Long.valueOf(folder2.getFolderSize()).compareTo(folder.getFolderSize()));
                }
            } else if (folderSortOrder == 3) {
                if (folderAsc) {
                    Collections.sort(arrayList, Comparator.comparing(folder::getCount));
                } else {
                    Collections.sort(arrayList, (folder, folder2) -> folder2.getCount().compareTo(folder.getCount()));
                }
            }
        }

        for (int i = 0; i < folderList.size(); i++) {
            if (i != 0 && i % 4 == 0) { // folder ads mate aya malo
                folderArrayList.add(null);
            }
            folderArrayList.add(folderList.get(i));
        }

        try {
            for (folder fol : folderArrayList){
                if (fol != null) {
                    if (fol.bucket.equalsIgnoreCase("telegram")) {
                        folderArrayList.remove(fol);
                        folderArrayList.add(0, fol);
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }


        this.rvFolders.getRecycledViewPool().clear();
        FolderAdapter folderAdapter2 = new FolderAdapter((Activity) this.context, folderArrayList, this);
        this.folderAdapter = folderAdapter2;
        this.rvFolders.setAdapter(folderAdapter2);
        this.mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void clickFoldermenu(int i2) {
        if (i2 == R.id.renamef) {
            this.folderList.clear();
            this.mSwipeRefreshLayout.setRefreshing(true);
            new Handler().postDelayed(() -> {
                MainActivityFragment.this.mSwipeRefreshLayout.setRefreshing(false);
                MainActivityFragment.this.loadVideoFolder();
            }, 500);
        }
    }


    @SuppressWarnings("FieldMayBeFinal")
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        public final Context context;
        private final ArrayList<EqualizerDataList> equalizerList;

        public MyAdapter(Context context2, ArrayList<EqualizerDataList> arrayList) {
            this.context = context2;
            this.equalizerList = arrayList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_equalizer, viewGroup, false));
        }

        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            viewHolder.txtName.setText(this.equalizerList.get(i).getName());
            viewHolder.txtName.setOnClickListener(view -> {
                for (int i1 = 0; i1 < MainActivityFragment.this.equalizerListData.size(); i1++) {
                    if (i1 == i1) {
                        MainActivityFragment.this.equalizerListData.set(i1, new EqualizerDataList(MainActivityFragment.this.equalizerListData.get(i1).getName(), true));
                    } else {
                        MainActivityFragment.this.equalizerListData.set(i1, new EqualizerDataList(MainActivityFragment.this.equalizerListData.get(i1).getName(), false));
                    }
                }
                if (i < MainActivityFragment.this.equalizerListData.size()) {
                    PreferenceUtil.getInstance(MyAdapter.this.context).savePresetPos(i);
                    short presetNo = Equalizers.getPresetNo();
                    short numberOfBands = Equalizers.getNumberOfBands();
                    if (!(presetNo == -1 || numberOfBands == -1)) {
                        if (i < presetNo) {
                            Equalizers.usePreset((short) i);
                            for (short s = 0; s < numberOfBands; s = (short) (s + 1)) {
                                short[] bandLevelRange = Equalizers.getBandLevelRange();
                                short bandLevel = Equalizers.getBandLevel(s);
                                if (bandLevelRange != null && s < MainActivityFragment.this.seekBarFinal.length) {
                                    MainActivityFragment.this.seekBarFinal[s].setProgress(bandLevel - bandLevelRange[0]);
                                }
                            }
                        } else {
                            for (int i3 = 0; i3 < numberOfBands; i3++) {
                                short[] bandLevelRange2 = Equalizers.getBandLevelRange();
                                if (bandLevelRange2 != null && i3 < MainActivityFragment.this.seekBarFinal.length) {
                                    VerticalSeekBar verticalSeekBar = MainActivityFragment.this.seekBarFinal[i3];
                                    SharedPreferences saveEq = PreferenceUtil.getInstance().saveEq();
                                    verticalSeekBar.setProgress(saveEq.getInt(PreferenceUtil.BAND_LEVEL + i3, 0) - bandLevelRange2[0]);
                                }
                            }
                        }
                    }
                }
                MyAdapter.this.notifyDataSetChanged();
            });
            if (this.equalizerList.get(i).isSelectItem()) {
                viewHolder.txtName.getBackground().setColorFilter(MainActivityFragment.this.getResources().getColor(R.color.t15), PorterDuff.Mode.SRC_ATOP);
                viewHolder.txtName.setTextColor(MainActivityFragment.this.getResources().getColor(R.color.white));
                return;
            }
            viewHolder.txtName.getBackground().setColorFilter(MainActivityFragment.this.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            viewHolder.txtName.setTextColor(MainActivityFragment.this.getResources().getColor(R.color.black));
        }

        @Override
        public int getItemCount() {
            return this.equalizerList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final Button txtName;

            public ViewHolder(View view) {
                super(view);
                this.txtName = view.findViewById(R.id.txtName);
            }
        }
    }
}
