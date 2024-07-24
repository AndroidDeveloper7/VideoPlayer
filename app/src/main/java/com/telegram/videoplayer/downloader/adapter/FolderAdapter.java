package com.telegram.videoplayer.downloader.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.util.DialogUtils;
import com.telegram.videoplayer.downloader.Adshandler;
import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.activitys.VideoListActivity;
import com.telegram.videoplayer.downloader.duplicate.MainActivity;
import com.telegram.videoplayer.downloader.model.MediaQuery;
import com.telegram.videoplayer.downloader.model.PrivateItem;
import com.telegram.videoplayer.downloader.model.VideoItem;
import com.telegram.videoplayer.downloader.model.folder;
import com.telegram.videoplayer.downloader.utildata.PreferenceUtil;
import com.telegram.videoplayer.downloader.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.MaterialValueHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldMayBeFinal", "unchecked", "rawtypes", "ConstantConditions", "ResultOfMethodCallIgnored"})
public class FolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final MyListener myListener;
    private final SparseBooleanArray mSparseBoolMultiSelect = new SparseBooleanArray();
    private final boolean action = false;
    private final Activity context;
    private final ArrayList<folder> folderList;
    private final MediaQuery mediaQuery;
    int fromWhere = 0;
    Utils prefenrencUtils;
    private boolean checktrue = false;
    private ActionMode mActionMode;


    public FolderAdapter(Activity activity, ArrayList<folder> arrayList, MyListener myListener2) {
        this.context = activity;
        this.folderList = arrayList;
        this.myListener = myListener2;
        this.mediaQuery = new MediaQuery(activity);
    }


    public void redirectM(int i) {
        if (this.fromWhere == 1) {
            Adshandler.showAd(context, () -> {
                Intent intent = new Intent(context, VideoListActivity.class);
                Log.e("ListViewHolder", "callback: =================000000000000000000=================>>" + folderList.get(i).getBucket());
                intent.putExtra("id", folderList.get(i).bid);
                intent.putExtra("name", folderList.get(i).bucket);
                context.startActivity(intent);
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.prefenrencUtils = new Utils(this.context);
        return new ListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_folder, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        final ListViewHolder listViewHolder = (ListViewHolder) viewHolder;
        int itemview = getItemViewType(i);
        if (itemview == 0) {
            Adshandler.refreshAdSmall(listViewHolder.nativead, context); // change the size of ads from here.
            listViewHolder.lnr.setVisibility(View.GONE);
            listViewHolder.nativead.setVisibility(View.VISIBLE);
        } else {
            listViewHolder.lnr.setVisibility(View.VISIBLE);
            listViewHolder.nativead.setVisibility(View.GONE);
            listViewHolder.position = i;
            TextView textView = listViewHolder.folderName;
            textView.setText("" + this.folderList.get(i).bucket);
            Log.e("djfkdjfk", "onBindViewHolder: " + this.folderList.get(i).bucket);
            TextView textView2 = listViewHolder.filesCount;
            textView2.setText("" + this.folderList.get(i).count + " Video(s)");
            TextView textView3 = listViewHolder.folderName;
            Activity activity = this.context;
            textView3.setTextColor(MaterialValueHelper.getPrimaryTextColor(activity, ColorUtil.isColorLight(DialogUtils.resolveColor(activity, R.attr.defaultFooterColor))));
            TextView textView4 = listViewHolder.filesCount;
            Activity activity2 = this.context;
            textView4.setTextColor(MaterialValueHelper.getPrimaryTextColor(activity2, ColorUtil.isColorLight(DialogUtils.resolveColor(activity2, R.attr.defaultFooterColor))));
            AppCompatImageView appCompatImageView = listViewHolder.imgMore;
            Activity activity3 = this.context;
            appCompatImageView.setColorFilter(MaterialValueHelper.getPrimaryTextColor(activity3, ColorUtil.isColorLight(DialogUtils.resolveColor(activity3, R.attr.defaultFooterColor))), PorterDuff.Mode.SRC_IN);
            int i2 = 0;
            Boolean.valueOf(false);
            if (this.action) {
                listViewHolder.imgMore.setVisibility(View.INVISIBLE);
                listViewHolder.s1.setVisibility(View.VISIBLE);
            } else {
                listViewHolder.imgMore.setVisibility(View.VISIBLE);
                listViewHolder.s1.setVisibility(View.INVISIBLE);
            }
            if (this.mSparseBoolMultiSelect.get(i)) {
                listViewHolder.itemView.setSelected(true);
                listViewHolder.s1.setChecked(true);
            } else {
                listViewHolder.itemView.setSelected(false);
                listViewHolder.s1.setChecked(false);
            }
            try {
                List<VideoItem> allVideo = this.mediaQuery.getAllVideo(this.folderList.get(i).bid);
                while (true) {
                    if (i2 >= allVideo.size()) {
                        break;
                    }
                    PreferenceUtil instance = PreferenceUtil.getInstance(this.context);
                    if (!instance.getIsPlayVideo("" + allVideo.get(i2).getDISPLAY_NAME())) {
                        Boolean.valueOf(true);
                        break;
                    }
                    i2++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            listViewHolder.imgMore.setOnClickListener(view -> {
                PopupMenu popupMenu = new PopupMenu(context, listViewHolder.imgMore);
                popupMenu.getMenuInflater().inflate(R.menu.folder_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.delete:
                            View inflate = LayoutInflater.from(context).inflate(R.layout.checkbox, null);
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.MyAlertDialogStyle));
                            builder.setTitle("Delete video from device?");
                            builder.setMessage("Video will be deleted permanently from device.");
                            builder.setView(inflate);
                            builder.setOnDismissListener(dialogInterface -> checktrue = false);
                            CheckBox checkBox = inflate.findViewById(R.id.checkbox);
                            checkBox.setText("Move to Recycle Bin");
                            checkBox.setOnCheckedChangeListener((compoundButton, z) -> checktrue = z);
                            builder.setPositiveButton("DELETE", (dialogInterface, i13) -> {
                                if (checktrue) {
                                    lockFolder(i13);
                                } else {
                                    deletFolder(i13, true);
                                }
                            });
                            builder.setNegativeButton("CANCEL", (dialogInterface, i12) -> dialogInterface.cancel());
                            builder.show();
                            return true;
                        case R.id.duplicate:
                            Adshandler.showAd(context, () -> {
                                File file = new File(folderList.get(i).getData());
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra("dup", file.getParent());
                                context.startActivity(intent);
                            });
                            return true;
                        case R.id.properties:
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.MyAlertDialogStyle));
                            LayoutInflater from = LayoutInflater.from(context);
                            builder2.setTitle("Properties");
                            View inflate2 = from.inflate(R.layout.properties_dialog_folder, null);
                            ((TextView) inflate2.findViewById(R.id.name)).setText(folderList.get(i).getBucket());
                            ((TextView) inflate2.findViewById(R.id.duration)).setText(folderList.get(i).getSize());
                            ((TextView) inflate2.findViewById(R.id.fsize)).setText(folderList.get(i).getCount());
                            ((TextView) inflate2.findViewById(R.id.location)).setText(new File(folderList.get(i).getData()).getParent());
                            ((TextView) inflate2.findViewById(R.id.date)).setText(folderList.get(i).getDate());
                            builder2.setView(inflate2).setPositiveButton("OK", (dialogInterface, i1) -> {
                            });
                            builder2.show();
                            return true;
                        case R.id.renamef:
                            renamFolder(i);
                            return true;
                        case R.id.sharef:
                            shareFolder(i);
                            return true;
                        default:
                            return true;
                    }
                });
                popupMenu.show();
            });
        }

    }

    @Override
    public int getItemCount() {
        return this.folderList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (folderList.get(position) == null) {
            return 0;
        } else {
            return 1;
        }
    }

    public void deletFolder(int i, boolean z) {
        List<VideoItem> allVideo = this.mediaQuery.getAllVideo(this.folderList.get(i).bid);
        for (int i2 = 0; i2 < allVideo.size(); i2++) {
            String[] strArr = {new File(allVideo.get(i2).getDATA()).getAbsolutePath()};
            ContentResolver contentResolver = this.context.getContentResolver();
            Uri contentUri = MediaStore.Files.getContentUri("external");
            contentResolver.delete(contentUri, "_data=?", strArr);
            if (new File(allVideo.get(i2).getDATA()).exists()) {
                contentResolver.delete(contentUri, "_data=?", strArr);
            }
        }
        if (z) {
            this.folderList.remove(i);
            notifyItemRemoved(i);
            notifyItemRangeChanged(i, this.folderList.size());
        }
    }

    public void shareFolder(int i) {
        ArrayList arrayList = new ArrayList();
        List<VideoItem> allVideo = new MediaQuery(this.context).getAllVideo(this.folderList.get(i).bid);
        Log.e("sdfdjdjf", "list: " + allVideo.size());
        for (int i2 = 0; i2 < allVideo.size(); i2++) {
            arrayList.add(FileProvider.getUriForFile(this.context, this.context.getPackageName() + ".provider", new File(allVideo.get(i2).getDATA())));
        }
        Log.e("sdfdjdjf", "uri: " + arrayList.size());
        try {
            Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
            intent.putExtra("android.intent.extra.SUBJECT", "video files.");
            intent.setType("video/*");
            intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
            intent.putExtra("android.intent.extra.STREAM", arrayList);
            this.context.startActivity(Intent.createChooser(intent, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void renamFolder(final int i) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context, R.style.CustomDialog1);
            View inflate = LayoutInflater.from(this.context).inflate(R.layout.dialog_file_rename, null);
            final EditText editText = inflate.findViewById(R.id.edtName);
            TextView textView2 = inflate.findViewById(R.id.txtCancel);
            TextView textView3 = inflate.findViewById(R.id.txtSave);
            builder.setView(inflate);
            final AlertDialog create = builder.create();
            final String parent = new File(this.folderList.get(i).getData()).getParent();
            if (parent != null) {
                int lastIndexOf = parent.lastIndexOf(47);
                int length = parent.length();
                int i2 = lastIndexOf + 1;
                final String substring = parent.substring(0, i2);
                String substring2 = parent.substring(i2, length);
                System.out.println(parent.substring(0, lastIndexOf));
                editText.setText(substring2);
                create.show();
                textView3.setOnClickListener(view -> {
                    if (editText.getText().toString().trim().equalsIgnoreCase("")) {
                        Toast.makeText(context, "Enter file name", Toast.LENGTH_LONG).show();
                    } else {
                        File file = new File(parent);
                        String str = substring + editText.getText().toString() + "/";
                        Log.e("ijjnnerebae", "renamFolder: " + str);
                        File file2 = new File(str);
                        if (file2.exists()) {
                            Toast.makeText(context, "This Folder Already Exists!", Toast.LENGTH_SHORT).show();
                        } else {
                            file.renameTo(file2);
                            Log.e("success : ", " : " + file.renameTo(file2));
                            context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file2)));
                            context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file)));
                            List<VideoItem> allVideo = mediaQuery.getAllVideo(folderList.get(i).bid);
                            for (int i1 = 0; i1 < allVideo.size(); i1++) {
                                File file3 = new File(allVideo.get(i1).getDATA());
                                file3.renameTo(new File(str + file3.getName()));
                                context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file2)));
                                context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file)));
                            }
                            folderList.remove(i);
                            notifyItemRemoved(i);
                            notifyDataSetChanged();
                        }
                    }
                    create.dismiss();
                    myListener.clickFoldermenu(R.id.renamef);
                });
                textView2.setOnClickListener(view -> create.dismiss());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void lockFolder(int i) {
        List list;
        File file;
        List<VideoItem> allVideo = new MediaQuery(this.context).getAllVideo(this.folderList.get(i).bid);
        Log.e("lllldlld", "size: " + allVideo.size());
        String str = null;
        for (int i2 = 0; i2 < allVideo.size(); i2++) {
            if (false) {
                try {
                    str = PreferenceUtil.getInstance(this.context).getLockVideo();
                } catch (Exception e) {
                    Log.e("dwdsddsd", "error: " + e.getMessage());
                }
            } else {
                str = PreferenceUtil.getInstance(this.context).getRecycleVideo();
            }
            if (str.equalsIgnoreCase("")) {
                list = new ArrayList();
            } else {
                list = new Gson().fromJson(str, new TypeToken<ArrayList<PrivateItem>>() {
                }.getType());
            }
            if (false) {
                Activity activity = this.context;
                file = new File(String.valueOf(activity.getExternalFilesDir(activity.getResources().getString(R.string.private_folder_name))));
            } else {
                Activity activity2 = this.context;
                file = new File(String.valueOf(activity2.getExternalFilesDir(activity2.getResources().getString(R.string.recycler_folder_name))));
            }
            File file2 = new File(allVideo.get(i2).getDATA());
            Gson gson = new Gson();
            list.add(new PrivateItem(file2.getParent(), allVideo.get(i2).getDISPLAY_NAME()));
            if (false) {
                PreferenceUtil.getInstance(this.context).setLockVideo(gson.toJson(list));
            } else {
                PreferenceUtil.getInstance(this.context).setRecycleVideo(gson.toJson(list));
            }
            try {
                moveFile(file2, file);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            String[] strArr = {file2.getAbsolutePath()};
            ContentResolver contentResolver = this.context.getContentResolver();
            Uri contentUri = MediaStore.Files.getContentUri("external");
            contentResolver.delete(contentUri, "_data=?", strArr);
            if (file2.exists()) {
                contentResolver.delete(contentUri, "_data=?", strArr);
            }
        }
        if (true) {
            this.folderList.remove(i);
            notifyItemRemoved(i);
            notifyItemRangeChanged(i, this.folderList.size());
        }
    }

    public void moveFile(File file, File file2) throws IOException {
        try (FileChannel channel = new FileOutputStream(new File(file2, file.getName())).getChannel(); FileChannel fileChannel = new FileInputStream(file).getChannel()) {
            fileChannel.transferTo(0, fileChannel.size(), channel);
            fileChannel.close();
            file.delete();
        }
    }

    public interface MyListener {
        void clickFoldermenu(int i2);
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ListViewHolder";
        final TextView filesCount;
        final TextView folderName;
        final ImageView imgFolder;
        final AppCompatImageView imgMore;
        final RelativeLayout rlSelect;
        final CheckBox s1;
        final TextView txtNew;
        int position;
        FrameLayout nativead;
        LinearLayout lnr;

        public ListViewHolder(View view) {
            super(view);
            this.imgFolder = view.findViewById(R.id.imgFolder);
            this.folderName = view.findViewById(R.id.folderName);
            this.filesCount = view.findViewById(R.id.filesCount);
            this.txtNew = view.findViewById(R.id.txtNew);
            this.imgMore = view.findViewById(R.id.imgMore);
            this.rlSelect = view.findViewById(R.id.rlSelect);
            this.s1 = view.findViewById(R.id.checklong);
            this.nativead = view.findViewById(R.id.native_ad_container);
            this.lnr = view.findViewById(R.id.lnr);
            view.setOnClickListener(view1 -> {
                fromWhere = 1;
                if (action) {
                    if (mSparseBoolMultiSelect.get(ListViewHolder.this.position)) {
                        view1.setSelected(false);
                        ListViewHolder.this.s1.setChecked(false);
                        mSparseBoolMultiSelect.delete(ListViewHolder.this.position);
                        if (mSparseBoolMultiSelect.size() == 0) {
                            mActionMode.finish();
                            notifyDataSetChanged();
                            return;
                        }
                    }
                    view1.setSelected(true);
                    ListViewHolder.this.s1.setChecked(true);
                    mSparseBoolMultiSelect.put(ListViewHolder.this.position, true);
                    mActionMode.setTitle(String.format("%d selected", mSparseBoolMultiSelect.size()));
                    return;
                }
                redirectM(ListViewHolder.this.position);
            });
        }
    }
}
