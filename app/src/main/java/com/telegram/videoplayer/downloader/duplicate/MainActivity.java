package com.telegram.videoplayer.downloader.duplicate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.os.EnvironmentCompat;

import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.activitys.BaseActivity;
import com.telegram.videoplayer.downloader.duplicate.Model.DataModel;
import com.telegram.videoplayer.downloader.duplicate.Model.Duplicate;
import com.telegram.videoplayer.downloader.duplicate.Model.TypeFile;
import com.telegram.videoplayer.downloader.duplicate.utilts.Utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@SuppressWarnings({"FieldCanBeLocal", "unchecked", "rawtypes", "ConstantConditions", "EqualsWithItself", "SuspiciousMethodCalls"})
public class MainActivity extends BaseActivity {
    public static final ArrayList<DataModel> mListData = new ArrayList<>();
    final HashMap<String, ArrayList<File>> mListVideo = new HashMap<>();
    public String path = "";
    public HashMap<String, ArrayList<File>> requiredContent;
    boolean isback = true;
    ScanImagesAsyncTask mScanImagesAsyncTask;
    private FrameLayout adfrm;
    private ArrayList<String> arrPermission;
    private ImageView imgBack;

    @Override

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main_dup);
        getWindow().setFlags(1024, 1024);
        this.imgBack = findViewById(R.id.img_back);
        this.adfrm = findViewById(R.id.adsfrm);
        checkPermission();
        this.requiredContent = new HashMap<>();
        this.path = getIntent().getStringExtra("dup");
        Log.e("duplicate", "==>" + this.path);
        this.imgBack.setOnClickListener(view -> onBackPressed());
        try {
            scanFile();
        } catch (Exception e) {
            new AlertDialog.Builder(this).setTitle("Error").setMessage(e.getMessage()).setPositiveButton("Okay", (dialogInterface, i) -> {
                finish();
                dialogInterface.cancel();
            }).show();
        }
    }

    public void scanFile() {
        ScanImagesAsyncTask scanImagesAsyncTask = new ScanImagesAsyncTask();
        this.mScanImagesAsyncTask = scanImagesAsyncTask;
        if (scanImagesAsyncTask.getStatus() != AsyncTask.Status.RUNNING) {
            mListData.clear();
            this.mScanImagesAsyncTask.execute();
            return;
        }
        Toast.makeText(this, "scan files...", Toast.LENGTH_LONG).show();
    }

    public void collectRequiredFilesContent() {
        this.requiredContent.put(getString(R.string._3gp), new ArrayList<>());
        this.requiredContent.put(getString(R.string.mp4), new ArrayList<>());
        this.requiredContent.put(getString(R.string.mkv), new ArrayList<>());
        this.requiredContent.put(getString(R.string.webm), new ArrayList<>());
    }

    public HashMap<String, ArrayList<File>> getMediaGroup() {
        this.mListVideo.put(getString(R.string._3gp), this.requiredContent.get(getString(R.string._3gp)));
        this.mListVideo.put(getString(R.string.mp4), this.requiredContent.get(getString(R.string.mp4)));
        this.mListVideo.put(getString(R.string.mkv), this.requiredContent.get(getString(R.string.mkv)));
        this.mListVideo.put(getString(R.string.webm), this.requiredContent.get(getString(R.string.webm)));
        return this.mListVideo;
    }

    public void checkFileOfDirectory(File[] fileArr) {
        if (fileArr != null) {
            for (File file : fileArr) {
                if (file.isDirectory()) {
                    checkFileOfDirectory(Utils.getFileList(file.getPath()));
                } else {
                    detectFileTypeAndAddInCategory(file);
                }
            }
        }
    }

    private void detectFileTypeAndAddInCategory(File file) {
        ArrayList<File> arrayList;
        String name = file.getName();
        File file2 = new File(String.valueOf(getExternalFilesDir(getResources().getString(R.string.recycler_folder_name))));
        if (file2.equals(file.getParentFile())) {
            Log.e("kjfdkfj", "detectFileT directo  :" + file2);
            Log.e("kjfdkfj", "detectFile file path :" + file2);
        } else if (name.endsWith(".3gp")) {
            ArrayList<File> arrayList2 = this.requiredContent.get(getString(R.string._3gp));
            if (arrayList2 != null) {
                arrayList2.add(file);
            }
        } else if (name.endsWith(".mp4")) {
            ArrayList<File> arrayList3 = this.requiredContent.get(getString(R.string.mp4));
            if (arrayList3 != null) {
                arrayList3.add(file);
            }
        } else if (name.endsWith(".mkv")) {
            ArrayList<File> arrayList4 = this.requiredContent.get(getString(R.string.mkv));
            if (arrayList4 != null) {
                arrayList4.add(file);
            }
        } else if (name.endsWith(".webm") && (arrayList = this.requiredContent.get(getString(R.string.webm))) != null) {
            arrayList.add(file);
        }
    }

    public void getSdCard() {
        String[] externalStorageDirectories = getExternalStorageDirectories();
        Log.e("dfdff", "getSdCard: " + externalStorageDirectories.length);
        if (externalStorageDirectories != null && externalStorageDirectories.length > 0) {
            for (String str : externalStorageDirectories) {
                File file = new File(str);
                if (file.exists()) {
                    checkFileOfDirectory(file.listFiles());
                }
            }
        }
    }

    public HashMap<Long, ArrayList<File>> findExactDuplicates(ArrayList<File> arrayList) {
        HashMap<Long, ArrayList<File>> hashMap = new HashMap<>();
        if (arrayList != null) {
            HashMap<Long, ArrayList<File>> findDuplicatesBySize = findDuplicatesBySize(arrayList);
            ArrayList arrayList2 = new ArrayList(findDuplicatesBySize.keySet());
            for (int i = 0; i < arrayList2.size(); i++) {
                ArrayList<File> arrayList3 = findDuplicatesBySize.get(arrayList2.get(i));
                int size = arrayList3.size();
                for (int i2 = 0; i2 < size; i2++) {
                    for (int i3 = 0; i3 < size; i3++) {
                        if (i2 != i3 && i2 < size && i3 < size) {
                            try {
                                if (contentEquals(arrayList3.get(i2), arrayList3.get(i3))) {
                                    File file = arrayList3.get(i2);
                                    if (hashMap.containsKey(file.length())) {
                                        ArrayList<File> arrayList4 = hashMap.get(file.length());
                                        if (!arrayList4.contains(file)) {
                                            arrayList4.add(file);
                                        }
                                    } else {
                                        ArrayList<File> arrayList5 = new ArrayList<>();
                                        arrayList5.add(file);
                                        hashMap.put(file.length(), arrayList5);
                                    }
                                }
                            } catch (Exception ignored) {
                            } catch (Throwable th) {
                                th.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return hashMap;
    }

    private HashMap<Long, ArrayList<File>> findDuplicatesBySize(ArrayList<File> arrayList) {
        HashMap<Long, ArrayList<File>> hashMap = new HashMap<>();
        for (File next : arrayList) {
            long length = next.length();
            if (hashMap.containsKey(length)) {
                hashMap.get(length).add(next);
            } else {
                ArrayList<File> arrayList2 = new ArrayList<>();
                arrayList2.add(next);
                hashMap.put(length, arrayList2);
            }
        }
        ArrayList arrayList3 = new ArrayList(hashMap.keySet());
        for (int i = 0; i < arrayList3.size(); i++) {
            try {
                if (hashMap.get(arrayList3.get(i)).size() == 1) {
                    hashMap.remove(arrayList3.get(i));
                }
            } catch (Exception ignored) {
            }
        }
        return hashMap;
    }

    private boolean contentEquals(File file, File file2) throws Throwable {
        Throwable th;
        if (file.exists() && file2.exists() && file.length() == file2.length()) {
            if (file.length() <= 3000) {
                try {
                    return FileUtils.contentEquals(file, file2);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                FileInputStream fileInputStream = null;
                try {
                    if (file.exists()) {
                        FileInputStream fileInputStream2 = new FileInputStream(file);
                        try {
                            fileInputStream = new FileInputStream(file2);
                            try {
                                byte[] bArr = new byte[512];
                                IOUtils.read(fileInputStream, bArr, 0, 512);
                                new String(bArr);
                                IOUtils.skip(fileInputStream, (file.length() / 2) - 256);
                                byte[] bArr2 = new byte[512];
                                IOUtils.read(fileInputStream, bArr2, 0, 512);
                                String str = new String(bArr2);
                                IOUtils.skip(fileInputStream, file.length() - 512);
                                byte[] bArr3 = new byte[512];
                                IOUtils.read(fileInputStream, bArr3, 0, 512);
                                String str2 = new String(bArr3);
                                byte[] bArr4 = new byte[512];
                                IOUtils.read(fileInputStream, bArr4, 0, 512);
                                new String(bArr4);
                                IOUtils.skip(fileInputStream, (file2.length() / 2) - 256);
                                byte[] bArr5 = new byte[512];
                                IOUtils.read(fileInputStream, bArr5, 0, 512);
                                String str3 = new String(bArr5);
                                IOUtils.skip(fileInputStream, file2.length() - 512);
                                byte[] bArr6 = new byte[512];
                                IOUtils.read(fileInputStream, bArr6, 0, 512);
                                String str4 = new String(bArr6);
                                if (!str3.equals(str3) || !str.equals(str3) || !str2.equals(str4)) {
                                    fileInputStream.close();
                                    fileInputStream.close();
                                    fileInputStream.close();
                                    fileInputStream.close();
                                    fileInputStream.close();
                                    fileInputStream.close();
                                    return false;
                                }
                                fileInputStream.close();
                                fileInputStream.close();
                                fileInputStream.close();
                                fileInputStream.close();
                                fileInputStream.close();
                                fileInputStream.close();
                                fileInputStream.close();
                                fileInputStream.close();
                                return true;
                            } catch (IOException unused) {
                                fileInputStream.close();
                                fileInputStream.close();
                                fileInputStream.close();
                                fileInputStream.close();
                                return false;
                            } catch (Throwable th2) {
                                th = th2;
                                try {
                                    throw th;
                                } catch (Throwable th3) {
                                    fileInputStream.close();
                                    fileInputStream.close();
                                    throw th3;
                                }
                            }
                        } catch (IOException unused2) {
                            fileInputStream = fileInputStream2;
                            fileInputStream.close();
                            fileInputStream.close();
                            fileInputStream.close();
                            fileInputStream.close();
                            return false;
                        } catch (Throwable th4) {
                            th = th4;
                            fileInputStream = fileInputStream2;
                            throw th;
                        }
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                } catch (Throwable th5) {
                    fileInputStream.close();
                    fileInputStream.close();
                    throw th5;
                }
                fileInputStream.close();
                fileInputStream.close();
            }
        }
        return false;
    }

    public String[] getExternalStorageDirectories() {
        File[] externalFilesDirs;
        String[] split;
        boolean z;
        ArrayList arrayList = new ArrayList();
        int i = Build.VERSION.SDK_INT;
        Log.e("sssssss", "getExternalStorageDirectories: ");
        if (i >= 19 && (externalFilesDirs = getExternalFilesDirs(null)) != null && externalFilesDirs.length > 0) {
            Log.e("sssssss", "getExternalStorageDirectories: ");
            for (File file : externalFilesDirs) {
                if (!(file == null || (split = file.getPath().split("/Android")) == null || split.length <= 0)) {
                    String str = split[0];
                    if (i >= 21) {
                        Log.e("if", "==>" + i);
                        Log.e("externalFilesDirs", "==>" + Arrays.toString(externalFilesDirs));
                        Log.e("file", "==>" + file);
                        z = Environment.isExternalStorageRemovable(file);
                    } else {
                        Log.e("else", "==>" + i);
                        z = "mounted".equals(EnvironmentCompat.getStorageState(file));
                    }
                    Log.e("sssssss", "==>" + z);
                    if (z) {
                        Log.e("sssssss", "getExternalStorageDirectories: " + str);
                        arrayList.add(str);
                    }
                }
            }
        }
        if (arrayList.isEmpty()) {
            StringBuilder str2 = new StringBuilder();
            try {
                Process start = new ProcessBuilder().command("mount | grep /dev/block/vold").redirectErrorStream(true).start();
                start.waitFor();
                InputStream inputStream = start.getInputStream();
                byte[] bArr = new byte[1024];
                while (inputStream.read(bArr) != -1) {
                    str2.append(new String(bArr));
                }
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!str2.toString().trim().isEmpty()) {
                String[] split2 = str2.toString().split("\n");
                if (split2.length > 0) {
                    for (String str3 : split2) {
                        arrayList.add(str3.split(StringUtils.SPACE)[2]);
                    }
                }
            }
        }
        String[] strArr = new String[arrayList.size()];
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            strArr[i2] = (String) arrayList.get(i2);
        }
        return strArr;
    }

    private void checkPermission() {
        this.arrPermission = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 23) {
            if (Utils.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
                this.arrPermission.add("android.permission.WRITE_EXTERNAL_STORAGE");
            }
            if (Utils.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE")) {
                this.arrPermission.add("android.permission.READ_EXTERNAL_STORAGE");
            }
            if (!this.arrPermission.isEmpty()) {
                requestPermissions(this.arrPermission.toArray(new String[0]), 100);
            }
        }
    }

    @Override
    public void onBackPressed() {
        this.isback = false;
        ScanImagesAsyncTask scanImagesAsyncTask = this.mScanImagesAsyncTask;
        if (!(scanImagesAsyncTask == null || scanImagesAsyncTask.getStatus() == AsyncTask.Status.FINISHED)) {
            this.mScanImagesAsyncTask.cancel(true);
        }
        super.onBackPressed();
    }

    @SuppressWarnings({"unchecked", "rawtypes", "SuspiciousMethodCalls"})
    public class ScanImagesAsyncTask extends AsyncTask<String, Integer, ArrayList<DataModel>> {

        @SuppressWarnings("deprecation")
        public ScanImagesAsyncTask() {
        }

        public void onPreExecute() {
            super.onPreExecute();
        }

        public void onProgressUpdate(Integer... numArr) {
            super.onProgressUpdate(numArr);
        }

        public ArrayList<DataModel> doInBackground(String... strArr) {
            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            StringBuilder sb = new StringBuilder();
            sb.append("root = ");
            sb.append(absolutePath);
            requiredContent.clear();
            collectRequiredFilesContent();
            if (path.equals("all")) {
                checkFileOfDirectory(Utils.getFileList(absolutePath));
            } else {
                Log.e("fddfdfed", "doInBackground: " + path);
                MainActivity mainActivity = MainActivity.this;
                mainActivity.checkFileOfDirectory(Utils.getFileList(mainActivity.path));
            }
            getSdCard();
            HashMap<String, ArrayList<File>> mediaGroup = getMediaGroup();
            ArrayList arrayList = new ArrayList(mediaGroup.keySet());
            int i = 1;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                HashMap<Long, ArrayList<File>> findExactDuplicates = findExactDuplicates(mediaGroup.get(arrayList.get(i2)));
                if (findExactDuplicates != null && findExactDuplicates.size() > 0) {
                    for (Object o : new ArrayList(findExactDuplicates.keySet())) {
                        DataModel dataModel = new DataModel();
                        ArrayList<File> arrayList2 = findExactDuplicates.get(o);
                        if (arrayList2 != null && arrayList2.size() > 0) {
                            dataModel.setTitleGroup("Group: " + i);
                            ArrayList<Duplicate> arrayList3 = new ArrayList<>();
                            for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                                Duplicate duplicate = new Duplicate();
                                duplicate.setFile(arrayList2.get(i3));
                                duplicate.setTypeFile(TypeFile.getType(arrayList2.get(i3).getPath()));
                                if (i3 == 0) {
                                    duplicate.setChecked(false);
                                }
                                arrayList3.add(duplicate);
                            }
                            dataModel.setListDuplicate(arrayList3);
                            publishProgress(i);
                            i++;
                        }
                        MainActivity.mListData.add(dataModel);
                    }
                }
            }
            return null;
        }

        public void onPostExecute(ArrayList<DataModel> arrayList) {
            super.onPostExecute(arrayList);
            if (isback) {
                if (MainActivity.mListData.size() == 0) {
                    startActivity(new Intent(getApplicationContext(), NoFileActiviy.class));
                    finish();
                    return;
                }
                startActivity(new Intent(getApplicationContext(), DuplicateActivity.class));
                finish();
            }
        }
    }
}
