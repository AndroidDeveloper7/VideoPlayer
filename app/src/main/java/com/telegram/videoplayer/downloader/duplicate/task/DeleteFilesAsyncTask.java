package com.telegram.videoplayer.downloader.duplicate.task;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.preference.PreferenceManager;

import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.duplicate.DuplicateActivity;
import com.telegram.videoplayer.downloader.duplicate.dialog.LoadingDialog;
import com.telegram.videoplayer.downloader.model.PrivateItem;
import com.telegram.videoplayer.downloader.utildata.PreferenceUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldMayBeFinal", "unchecked", "PointlessBooleanExpression", "rawtypes", "ConstantConditions", "ResultOfMethodCallIgnored"})
public class DeleteFilesAsyncTask extends AsyncTask<String, Integer, String> {
    private final String TAG = getClass().getName();
    private final ArrayList<File> listFile;
    private final Context mContext;
    private final OnRestoreListener onRestoreListener;
    int count = 0;
    TextView tvNumber;
    private LoadingDialog progressDialog;

    @SuppressWarnings("deprecation")
    public DeleteFilesAsyncTask(Context context, ArrayList<File> arrayList, OnRestoreListener onRestoreListener2) {
        this.mContext = context;
        this.listFile = arrayList;
        this.onRestoreListener = onRestoreListener2;
    }

    private static String getExtSdCardFolder(File file, Context context) {
        String[] extSdCardPaths = getExtSdCardPaths(context);
        int i = 0;
        while (i < extSdCardPaths.length) {
            try {
                if (file.getCanonicalPath().startsWith(extSdCardPaths[i])) {
                    return extSdCardPaths[i];
                }
                i++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void moveFile(File file, File file2) throws IOException {
        File file3 = new File(file2, file.getName());
        Log.e("ffdff", "moveFile: " + file.getAbsolutePath());
        Log.e("ffdff", "moveFile: " + file2.getAbsolutePath());
        try (FileChannel channel = new FileOutputStream(file3).getChannel(); FileChannel fileChannel = new FileInputStream(file).getChannel()) {
            fileChannel.transferTo(0, fileChannel.size(), channel);
            fileChannel.close();
            file.delete();
        }
    }

    private static String[] getExtSdCardPaths(Context context) {
        ArrayList arrayList = new ArrayList();
        if (Build.VERSION.SDK_INT >= 19) {
            File[] externalFilesDirs = context.getExternalFilesDirs("external");
            for (File file : externalFilesDirs) {
                if (file != null && !file.equals(context.getExternalFilesDir("external"))) {
                    int lastIndexOf = file.getAbsolutePath().lastIndexOf("/Android/data");
                    if (lastIndexOf < 0) {
                        Log.w("lorrgggfggrfg", "Unexpected external file dir: " + file.getAbsolutePath());
                    } else {
                        String substring = file.getAbsolutePath().substring(0, lastIndexOf);
                        try {
                            substring = new File(substring).getCanonicalPath();
                        } catch (IOException ignored) {
                        }
                        arrayList.add(substring);
                    }
                }
            }
        }
        if (arrayList.isEmpty()) {
            arrayList.add("/storage/sdcard1");
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    public static boolean isOnExtSdCard(File file, Context context) {
        return getExtSdCardFolder(file, context) != null;
    }

    public void onPreExecute() {
        super.onPreExecute();
        LoadingDialog loadingDialog = new LoadingDialog(this.mContext);
        this.progressDialog = loadingDialog;
        loadingDialog.setCancelable(false);
        this.progressDialog.show();
    }

    public String doInBackground(String... strArr) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("pkfdjdjferrd", "doInBackground: " + DuplicateActivity.checktrue);
        if (DuplicateActivity.checktrue) {
            deleteMove(this.listFile);
        } else {
            int i = 0;
            while (i < this.listFile.size()) {
                if (this.listFile.get(i).exists()) {
                    isOnExtSdCard(this.listFile.get(i), this.mContext);
                    if (Build.VERSION.SDK_INT < 21 || !isOnExtSdCard(this.listFile.get(i), this.mContext)) {
                        try {
                            this.listFile.get(i).delete();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                        this.mContext.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(this.listFile.get(i))));
                        Log.e("dfnjdfjdf", "doInBackground: in  inerastorlage ");
                    } else {
                        Log.e("dfnjdfjdf", "doInBackground: in  sdcard " + this.listFile.get(i).getAbsoluteFile());
                        File file = new File(String.valueOf(this.listFile.get(i).getAbsoluteFile()));
                        ContentResolver contentResolver = this.mContext.getContentResolver();
                        int delete = contentResolver.delete(FileProvider.getUriForFile(this.mContext, this.mContext.getPackageName() + ".provider", file), null, null);
                        Log.e("dfdfdfdfd", "dfdd " + delete);
                    }
                }
                i++;
                this.count = i;
                publishProgress(i);
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e3) {
            e3.printStackTrace();
        }
        return null;
    }

    public void onPostExecute(String str) {
        super.onPostExecute(str);
        try {
            LoadingDialog loadingDialog = this.progressDialog;
            if (loadingDialog != null && loadingDialog.isShowing()) {
                this.progressDialog.cancel();
                this.progressDialog = null;
            }
            ((Activity) this.mContext).finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
        OnRestoreListener onRestoreListener2 = this.onRestoreListener;
        if (onRestoreListener2 != null) {
            onRestoreListener2.onComplete();
        }
    }

    public void onProgressUpdate(Integer... numArr) {
        super.onProgressUpdate(numArr);
        this.tvNumber = this.progressDialog.findViewById(R.id.tvNumber);
    }

    private DocumentFile getDocumentFile(File file, Context context) {
        String string = PreferenceManager.getDefaultSharedPreferences(context).getString("sdCardUri", "");
        Log.i(this.TAG, "getDocumentFile: " + string);
        String extSdCardFolder = getExtSdCardFolder(file, context);
        if (extSdCardFolder == null) {
            return null;
        }
        try {
            String canonicalPath = file.getCanonicalPath();
            if (!extSdCardFolder.equals(canonicalPath)) {
                String substring = canonicalPath.substring(extSdCardFolder.length() + 1);
                if (string != null && !string.equals("")) {
                    Uri parse = Uri.parse(string);
                    if (parse == null) {
                        DocumentFile fromTreeUri = DocumentFile.fromTreeUri(context, parse);
                        String[] split = substring.split("\\/");
                        for (int i = 0; i < split.length; i++) {
                            DocumentFile findFile = fromTreeUri.findFile(split[i]);
                            if (findFile != null) {
                                fromTreeUri = findFile;
                            } else {
                                if (i >= split.length - 1 && !false) {
                                    String fileExtensionFromUrl = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                                    Log.i(this.TAG, "getDocumentFile: " + fileExtensionFromUrl);
                                    String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionFromUrl);
                                    if (mimeTypeFromExtension != null && !TextUtils.isEmpty(mimeTypeFromExtension)) {
                                        fromTreeUri = fromTreeUri.createFile(mimeTypeFromExtension, split[i]);
                                    }
                                    fromTreeUri = fromTreeUri.createFile(mimeTypeFromExtension, split[i]);
                                }
                                fromTreeUri = fromTreeUri.createDirectory(split[i]);
                            }
                        }
                        return fromTreeUri;
                    }
                }
                return null;
            }
        } catch (IOException ignored) {
        } catch (Exception unused2) {
            Integer.valueOf(1);
        }
        return null;
    }

    private void deleteMove(ArrayList<File> arrayList) {
        List list;
        String recycleVideo = PreferenceUtil.getInstance(this.mContext).getRecycleVideo();
        if (!recycleVideo.equalsIgnoreCase("")) {
            list = new Gson().fromJson(recycleVideo, new TypeToken<ArrayList<PrivateItem>>() {
            }.getType());
        } else {
            list = new ArrayList();
        }
        for (int i = 0; i < arrayList.size(); i++) {
            Context context = this.mContext;
            File file = new File(String.valueOf(context.getExternalFilesDir(context.getResources().getString(R.string.recycler_folder_name))));
            File file2 = arrayList.get(i);
            Gson gson = new Gson();
            list.add(new PrivateItem(file2.getParent(), arrayList.get(i).getName()));
            PreferenceUtil.getInstance(this.mContext).setRecycleVideo(gson.toJson(list));
            try {
                moveFile(file2, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                getDocumentFile(file2, this.mContext).delete();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            this.mContext.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(this.listFile.get(i))));
        }
    }

    public interface OnRestoreListener {
        void onComplete();
    }
}
