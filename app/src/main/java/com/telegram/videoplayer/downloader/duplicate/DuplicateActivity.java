package com.telegram.videoplayer.downloader.duplicate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.activitys.BaseActivity;
import com.telegram.videoplayer.downloader.duplicate.Model.Duplicate;
import com.telegram.videoplayer.downloader.duplicate.adapter.RecyclerViewType;
import com.telegram.videoplayer.downloader.duplicate.adapter.SectionRecyclerViewAdapter;
import com.telegram.videoplayer.downloader.duplicate.task.DeleteFilesAsyncTask;

import java.io.File;
import java.security.AccessController;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class DuplicateActivity extends BaseActivity {
    public static boolean checktrue = false;
    final ArrayList<File> listFile = new ArrayList<>();
    final String titleToolBar = "Duplicate video file";
    public FrameLayout adfrm;
    SectionRecyclerViewAdapter adapter;
    TextView deletebtn;
    DeleteFilesAsyncTask mDeleteFilesAsyncTask;
    SharedPreferences sharedPreferences;
    Toolbar toolbar;
    private RecyclerView recyclerView;
    private RecyclerViewType recyclerViewType;

    private static boolean checkIfSDCardRoot(Uri uri) {
        return isExternalStorageDocument(uri) && isRootUri(uri) && !isInternalStorage(uri);
    }

    private static boolean isRootUri(Uri uri) {
        if (Build.VERSION.SDK_INT >= 21) {
            return DocumentsContract.getTreeDocumentId(uri).endsWith(":");
        }
        return false;
    }

    public static boolean isInternalStorage(Uri uri) {
        return Build.VERSION.SDK_INT >= 21 && isExternalStorageDocument(uri) && DocumentsContract.getTreeDocumentId(uri).contains("primary");
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_duplicate);
        getWindow().setFlags(1024, 1024);
        Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar = toolbar2;
        toolbar2.setTitle(this.titleToolBar);
        setSupportActionBar(this.toolbar);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.recyclerViewType = RecyclerViewType.LINEAR_VERTICAL;
        setUpRecyclerView();
        populateRecyclerView();
        this.adfrm = (FrameLayout) findViewById(R.id.adsfrm);
        TextView textView = findViewById(R.id.btnRestore);
        this.deletebtn = textView;
        textView.setOnClickListener(view -> {
            View inflate = LayoutInflater.from(DuplicateActivity.this).inflate(R.layout.checkbox, (ViewGroup) null);
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(DuplicateActivity.this, (int) R.style.MyAlertDialogStyle));
            builder.setTitle("Delete video from device?");
            builder.setMessage("Video will be deleted permanently from device.");
            builder.setView(inflate);
            builder.setPositiveButton("DELETE", (dialogInterface, i) -> {
                deleteSelectedItem();
                if (listFile.size() == 0) {
                    Toast.makeText(DuplicateActivity.this, "Cannot delete, all items are unchecked!", Toast.LENGTH_LONG).show();
                } else if (!sharedPreferences.getString("sdCardUri", "").equals("")) {
                    deleteFiles();
                } else {
                    deleteFiles();
                }
            });
            builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.cancel());
            CheckBox checkBox = (CheckBox) inflate.findViewById(R.id.checkbox);
            checkBox.setText("Move to Recycle Bin");
            checkBox.setOnCheckedChangeListener((compoundButton, z) -> DuplicateActivity.checktrue = z);
            builder.show();
        });
    }

    public void deleteFiles() {
        DeleteFilesAsyncTask deleteFilesAsyncTask = new DeleteFilesAsyncTask(this, this.listFile, () -> {
            adapter.notifyDataSetChanged();
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("value", listFile.size());
            intent.putExtra("title_tool_bar", titleToolBar);
            startActivityForResult(intent, 200);
        });
        this.mDeleteFilesAsyncTask = deleteFilesAsyncTask;
        deleteFilesAsyncTask.execute();
    }

    public boolean SDCardCheck() {
        File[] externalFilesDirs = ContextCompat.getExternalFilesDirs(this, null);
        return externalFilesDirs.length > 1 && externalFilesDirs[0] != null && externalFilesDirs[1] != null;
    }

    public void showDalogConfirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Do you want to delete the file?");
        builder.setPositiveButton("delete", (dialogInterface, i) -> {
            if (!sharedPreferences.getString("sdCardUri", "").equals("")) {
                deleteFiles();
            } else if (SDCardCheck()) {
                SDcardFilesDialog();
            } else {
                deleteFiles();
            }
            dialogInterface.dismiss();
        });
        builder.setNegativeButton("Cancle", (dialogInterface, i) -> {
        });
        builder.create().show();
    }

    public void SDcardFilesDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.requestWindowFeature(1);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.sdcard_dialog);
        ((Button) progressDialog.findViewById(R.id.ok_sd)).setOnClickListener(view -> {
            ProgressDialog progressDialog1 = null;
            if (progressDialog1 != null) {
                progressDialog1.dismiss();
            }
            fileSearch();
        });
    }

    @SuppressWarnings("deprecation")
    public void fileSearch() {
        startActivityForResult(new Intent("android.intent.action.OPEN_DOCUMENT_TREE"), 100);
    }

    public void deleteSelectedItem() {
        this.listFile.clear();
        Log.e("ouuuujj", ": outer ");
        if (MainActivity.mListData != null) {
            Log.e("ouuuujj", ": inner ");
            for (int i = 0; i < MainActivity.mListData.size(); i++) {
                ArrayList<Duplicate> listDuplicate = MainActivity.mListData.get(i).getListDuplicate();
                Log.e("ouuuujj", ": for " + listDuplicate.size());
                int size = listDuplicate.size();
                while (true) {
                    size--;
                    if (size < 0) {
                        break;
                    } else if (listDuplicate.get(size).isChecked()) {
                        this.listFile.add(listDuplicate.get(size).getFile());
                        listDuplicate.remove(listDuplicate.get(size));
                    }
                }
            }
        }
        Log.e("ouuuujj", ": size " + this.listFile.size());
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView2 = findViewById(R.id.my_recycler_view);
        this.recyclerView = recyclerView2;
        recyclerView2.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void populateRecyclerView() {
        SectionRecyclerViewAdapter sectionRecyclerViewAdapter = new SectionRecyclerViewAdapter(this, this.recyclerViewType, MainActivity.mListData);
        this.adapter = sectionRecyclerViewAdapter;
        this.recyclerView.setAdapter(sectionRecyclerViewAdapter);
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 100 && i2 == -1) {
            SharedPreferences.Editor edit = this.sharedPreferences.edit();
            boolean z = false;
            if (intent != null) {
                Uri data = intent.getData();
                if (Build.VERSION.SDK_INT >= 19 && AccessController.getContext() != null) {
                    getContentResolver().takePersistableUriPermission(data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                if (checkIfSDCardRoot(data)) {
                    edit.putString("sdCardUri", data.toString());
                    edit.putBoolean("storagePermission", true);
                    z = true;
                } else {
                    Toast.makeText(this, "Please Select Right SD Card.", Toast.LENGTH_SHORT).show();
                    edit.putBoolean("storagePermission", false);
                    edit.putString("sdCardUri", "");
                }
            } else {
                Toast.makeText(this, "Please Select Right SD Card.", Toast.LENGTH_SHORT).show();
                edit.putString("sdCardUri", "");
            }
            if (edit.commit()) {
                edit.apply();
                if (z) {
                    deleteFiles();
                }
            }
        }
        if (i == 200 && i2 == -1) {
            finish();
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
