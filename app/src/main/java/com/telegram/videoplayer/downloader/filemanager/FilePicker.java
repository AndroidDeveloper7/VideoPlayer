package com.telegram.videoplayer.downloader.filemanager;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.telegram.videoplayer.downloader.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "deprecation", "ResultOfMethodCallIgnored"})
public class FilePicker extends ListActivity {
    public static final String EXTRA_ACCEPTED_FILE_EXTENSIONS = "accepted_file_extensions";
    public static final String EXTRA_FILE_PATH = "file_path";
    public static final String EXTRA_SHOW_HIDDEN_FILES = "show_hidden_files";
    private static final String DEFAULT_INITIAL_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
    protected FilePickerListAdapter Adapter;
    protected File Directory;
    protected ArrayList<File> Files;
    protected boolean ShowHiddenFiles = false;
    protected String[] acceptedFileExtensions;
    int cc = 0;
    private int pos;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.pos = getSharedPreferences("theme", 0).getInt("pos", 1);
        View inflate = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.empty_view, null);
        ((ViewGroup) getListView().getParent()).addView(inflate);
        getListView().setEmptyView(inflate);
        this.Directory = new File(DEFAULT_INITIAL_DIRECTORY);
        this.Files = new ArrayList<>();
        FilePickerListAdapter filePickerListAdapter = new FilePickerListAdapter(this, this.Files);
        this.Adapter = filePickerListAdapter;
        setListAdapter(filePickerListAdapter);
        this.acceptedFileExtensions = new String[0];
        if (getIntent().hasExtra(EXTRA_FILE_PATH)) {
            this.Directory = new File(getIntent().getStringExtra(EXTRA_FILE_PATH));
        }
        if (getIntent().hasExtra(EXTRA_SHOW_HIDDEN_FILES)) {
            this.ShowHiddenFiles = getIntent().getBooleanExtra(EXTRA_SHOW_HIDDEN_FILES, false);
        }
        if (getIntent().hasExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS)) {
            ArrayList<String> stringArrayListExtra = getIntent().getStringArrayListExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS);
            this.acceptedFileExtensions = stringArrayListExtra.toArray(new String[0]);
        }
    }

    public void onResume() {
        refreshFilesList();
        super.onResume();
    }

    public void refreshFilesList() {
        this.Files.clear();
        File[] listFiles = this.Directory.listFiles(new ExtensionFilenameFilter(this.acceptedFileExtensions));
        if (listFiles != null && listFiles.length > 0) {
            for (File file : listFiles) {
                if (!file.isHidden() || this.ShowHiddenFiles) {
                    this.Files.add(file);
                }
            }
            Collections.sort(this.Files, new FileComparator());
        }
        this.Adapter.notifyDataSetChanged();
    }

    public int[] countFileList(File file) {
        int[] iArr = new int[2];
        File[] listFiles = file.listFiles(new ExtensionFilenameFilter(this.acceptedFileExtensions));
        if (listFiles != null && listFiles.length > 0) {
            int i = 0;
            int i2 = 0;
            for (File file2 : listFiles) {
                if (file2.isDirectory()) {
                    i++;
                    if (file2.isHidden()) {
                        i--;
                    }
                } else {
                    i2++;
                    if (file2.isHidden()) {
                        i2--;
                    }
                }
                file2.isHidden();
            }
            iArr[0] = i;
            iArr[1] = i2;
        }
        return iArr;
    }

    public void onBackPressed() {
        if (this.cc == 0) {
            finish();
        } else if (this.Directory.getParentFile() != null) {
            this.cc--;
            this.Directory = this.Directory.getParentFile();
            refreshFilesList();
        }
    }

    public void onListItemClick(ListView listView, View view, int i, long j) {
        File file = (File) listView.getItemAtPosition(i);
        this.cc++;
        if (file.isFile()) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_FILE_PATH, file.getAbsolutePath());
            setResult(-1, intent);
            finish();
        } else {
            this.Directory = file;
            refreshFilesList();
        }
        super.onListItemClick(listView, view, i, j);
    }

    public static class FileComparator implements Comparator<File> {
        private FileComparator() {
        }

        public int compare(File file, File file2) {
            if (file == file2) {
                return 0;
            }
            if (file.isDirectory() && file2.isFile()) {
                return -1;
            }
            if (!file.isFile() || !file2.isDirectory()) {
                return file.getName().compareToIgnoreCase(file2.getName());
            }
            return 1;
        }
    }

    @SuppressWarnings("FieldMayBeFinal")
    public static class ExtensionFilenameFilter implements FilenameFilter {
        private final String[] Extensions;

        public ExtensionFilenameFilter(String[] strArr) {
            this.Extensions = strArr;
        }

        public boolean accept(File file, String str) {
            String[] strArr;
            if (new File(file, str).isDirectory() || (strArr = this.Extensions) == null || strArr.length <= 0) {
                return true;
            }
            int i = 0;
            while (true) {
                String[] strArr2 = this.Extensions;
                if (i >= strArr2.length) {
                    return false;
                }
                if (str.endsWith(strArr2[i])) {
                    return true;
                }
                i++;
            }
        }
    }

    @SuppressWarnings("FieldMayBeFinal")
    public class FilePickerListAdapter extends ArrayAdapter<File> {
        private final List<File> mObjects;

        @SuppressLint("ResourceType")
        public FilePickerListAdapter(Context context, List<File> list) {
            super(context, R.layout.list_item, 16908308, list);
            this.mObjects = list;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            String str;
            if (view == null) {
                view = ((LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item, viewGroup, false);
            }
            File file = this.mObjects.get(i);
            ImageView imageView = view.findViewById(R.id.file_picker_image);
            TextView textView = view.findViewById(R.id.file_picker_text);
            TextView textView2 = view.findViewById(R.id.path);
            textView.setSingleLine(true);
            textView.setText(file.getName());
            int[] countFileList = FilePicker.this.countFileList(file);
            if (!file.isFile()) {
                imageView.setImageResource(R.drawable.ic_folder);
            } else if (file.getName().contains(".mp4") || file.getName().contains(".mkv") || file.getName().contains(".3gp")) {
                imageView.setImageResource(R.drawable.ic_action_play);
            } else {
                imageView.setImageResource(R.drawable.ic_file);
            }
            if (file.isDirectory()) {
                textView2.setVisibility(View.VISIBLE);
                if (countFileList[0] == 0 && countFileList[1] == 0) {
                    textView2.setText("Directory is empty");
                } else {
                    if (countFileList[0] > 0 && countFileList[1] > 0) {
                        str = countFileList[0] + " subfolder, " + countFileList[1] + " file";
                    } else if (countFileList[0] == 0 && countFileList[1] > 0) {
                        str = countFileList[1] + " file";
                    } else if (countFileList[0] <= 0 || countFileList[1] != 0) {
                        str = "";
                    } else {
                        str = countFileList[0] + " subfolder";
                    }
                    textView2.setText(str);
                }
            } else {
                textView2.setVisibility(View.GONE);
            }
            return view;
        }
    }
}
