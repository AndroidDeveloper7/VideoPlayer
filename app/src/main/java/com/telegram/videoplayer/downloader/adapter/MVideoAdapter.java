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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ActionMode;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.util.DialogUtils;
import com.bumptech.glide.Glide;
import com.telegram.videoplayer.downloader.Adshandler;
import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.activitys.SettingsActivity;
import com.telegram.videoplayer.downloader.activitys.VideoPlayerActivity;
import com.telegram.videoplayer.downloader.model.PrivateItem;
import com.telegram.videoplayer.downloader.model.VideoItem;
import com.telegram.videoplayer.downloader.utildata.PreferenceUtil;
import com.telegram.videoplayer.downloader.utils.AppPref;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.MaterialValueHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.RoundingMode;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldMayBeFinal", "unchecked", "rawtypes"})
public class MVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final Activity ctx;
    private final List<VideoItem> list;
    private final SparseBooleanArray mSparseBoolMultiSelect = new SparseBooleanArray();
    private final Boolean view;
    private final int wi;
    private final boolean action = false;
    private boolean fav = false;
    private boolean checktrue = false;
    private List<VideoItem> filteredvideoList;
    private ActionMode mActionMode;
    private AppPref appPref;

    public MVideoAdapter(Activity activity, List<VideoItem> list2, int i, boolean z, boolean fav) {
        this.ctx = activity;
        this.list = list2;
        this.wi = i;
        this.filteredvideoList = list2;
        this.view = z;
        this.fav = fav;
        this.appPref = new AppPref(ctx);
    }

    private int getRealPosition(int i) {
        return i;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view2;
        LayoutInflater from = LayoutInflater.from(this.ctx);
        if (this.view) {
            view2 = from.inflate(R.layout.video_list, viewGroup, false);
        } else {
            view2 = from.inflate(R.layout.video_tile, viewGroup, false);
            ImageView imageView = view2.findViewById(R.id.thumb);
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.height = (this.wi / 2) - 100;
            imageView.setLayoutParams(layoutParams);
        }
//        if (i == 0) {
//            view2 = from.inflate(R.layout.ad_unified_new, viewGroup, false);
//        }
        return new holder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        int itemview = getItemViewType(i);
        holder holder2 = (holder) viewHolder;
        if (itemview == 0) {
//            FrameLayout nativeads = holder2.nativead.findViewById(R.id.native_ad_container);
            Adshandler.refreshAd(holder2.nativead, ctx);
            holder2.play.setVisibility(View.GONE);
            holder2.nativead.setVisibility(View.VISIBLE);
        } else {
            holder2.play.setVisibility(View.VISIBLE);
            holder2.nativead.setVisibility(View.GONE);
            holder2.position = getRealPosition(i);
            holder2.xposition = i;
            TextView textView = holder2.txtName;
            Activity activity = this.ctx;
            textView.setTextColor(MaterialValueHelper.getPrimaryTextColor(activity, ColorUtil.isColorLight(DialogUtils.resolveColor(activity, R.attr.defaultFooterColor))));
            ImageView imageView = holder2.imgMore;
            Activity activity2 = this.ctx;
            imageView.setColorFilter(MaterialValueHelper.getPrimaryTextColor(activity2, ColorUtil.isColorLight(DialogUtils.resolveColor(activity2, R.attr.defaultFooterColor))), PorterDuff.Mode.SRC_IN);
            holder2.txtName.setText(this.filteredvideoList.get(getRealPosition(i)).getDISPLAY_NAME());
            holder2.txtDuration.setText(this.filteredvideoList.get(getRealPosition(i)).getDURATION());

            if (appPref.chack(filteredvideoList.get(getRealPosition(i)))) {
                filteredvideoList.get(i).setIsfav(true);
                holder2.fav.setColorFilter(ContextCompat.getColor(ctx, R.color.t15), PorterDuff.Mode.MULTIPLY);
            } else {
                holder2.fav.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                filteredvideoList.get(i).setIsfav(false);
            }
            holder2.fav.setOnClickListener(view -> {
                if (filteredvideoList.get(getRealPosition(i)).isIsfav()) {
                    appPref.addremoveFav(filteredvideoList.get(i), false);
                    filteredvideoList.get(i).setIsfav(false);
                    if (fav) {
                        notifyDataSetChanged();
                        filteredvideoList.remove(i);
                    } else {
                        holder2.fav.setColorFilter(ContextCompat.getColor(ctx, R.color.black), PorterDuff.Mode.MULTIPLY);
                    }
                } else {
                    appPref.addremoveFav(filteredvideoList.get(i), true);
                    filteredvideoList.get(i).setIsfav(true);
                    holder2.fav.setColorFilter(ContextCompat.getColor(ctx, R.color.t15), PorterDuff.Mode.MULTIPLY);
                }
            });
            if (this.view) {
                TextView textView2 = holder2.txtSize;
                Activity activity3 = this.ctx;
                textView2.setTextColor(MaterialValueHelper.getPrimaryTextColor(activity3, ColorUtil.isColorLight(DialogUtils.resolveColor(activity3, R.attr.defaultFooterColor))));
                TextView textView3 = holder2.txtDate;
                Activity activity4 = this.ctx;
                textView3.setTextColor(MaterialValueHelper.getPrimaryTextColor(activity4, ColorUtil.isColorLight(DialogUtils.resolveColor(activity4, R.attr.defaultFooterColor))));
                holder2.txtSize.setText(String.format("Size: %s", this.filteredvideoList.get(getRealPosition(i)).getSIZE()));
                holder2.txtDate.setText(String.format("Modified: %s", this.filteredvideoList.get(getRealPosition(i)).getDATE()));
            }
            Glide.with(this.ctx).load(this.filteredvideoList.get(getRealPosition(i)).getDATA()).into(holder2.i1);
            if (this.action) {
                holder2.imgMore.setVisibility(View.INVISIBLE);
                holder2.s1.setVisibility(View.VISIBLE);
            } else {
                holder2.imgMore.setVisibility(View.VISIBLE);
                holder2.s1.setVisibility(View.INVISIBLE);
            }
            if (this.mSparseBoolMultiSelect.get(getRealPosition(i))) {
                holder2.itemView.setSelected(true);
                holder2.s1.setChecked(true);
            } else {
                holder2.itemView.setSelected(false);
                holder2.s1.setChecked(false);
            }
            PreferenceUtil.getInstance(this.ctx);
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(this.filteredvideoList.get(getRealPosition(i)).getDISPLAY_NAME());
        }

    }

    @Override
    public int getItemCount() {
        return this.filteredvideoList.size();
    }

    public Filter getFilter() {
        return new Filter() {

            public Filter.FilterResults performFiltering(CharSequence charSequence) {
                String charSequence2 = charSequence.toString();
                if (charSequence2.isEmpty()) {
                    MVideoAdapter mVideoAdapter = MVideoAdapter.this;
                    mVideoAdapter.filteredvideoList = mVideoAdapter.list;
                } else {
                    ArrayList arrayList = new ArrayList();
                    for (VideoItem videoItem : MVideoAdapter.this.list) {
                        if (videoItem.getDISPLAY_NAME().toLowerCase().contains(charSequence2.toLowerCase())) {
                            arrayList.add(videoItem);
                        }
                    }
                    MVideoAdapter.this.filteredvideoList = arrayList;
                }
                Filter.FilterResults filterResults = new Filter.FilterResults();
                filterResults.values = MVideoAdapter.this.filteredvideoList;
                return filterResults;
            }

            public void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                MVideoAdapter.this.filteredvideoList = (List) filterResults.values;
                MVideoAdapter.this.notifyDataSetChanged();
            }
        };
    }


    public void moveFile(File file, File file2) throws Throwable {
        File file3 = new File(file2, file.getName());
        Log.e("ffdff", "moveFile: " + file.getAbsolutePath());
        Log.e("ffdff", "moveFile: " + file2.getAbsolutePath());
        try (FileChannel channel = new FileOutputStream(file3).getChannel(); FileChannel channel2 = new FileInputStream(file).getChannel()) {
            channel2.transferTo(0, channel2.size(), channel);
            channel2.close();
            file.delete();
            this.ctx.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file3)));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) == null) {
            return 0;
        } else {
            return 1;
        }
    }

    private String size(String str) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        double parseDouble = Double.parseDouble(str) / 1024.0d;
        if (parseDouble < 1024.0d) {
            return decimalFormat.format(parseDouble) + " KB";
        }
        double d = parseDouble / 1024.0d;
        if (d < 1024.0d) {
            return decimalFormat.format(d) + " MB";
        }
        return decimalFormat.format(d / 1024.0d) + " GB";
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    class holder extends RecyclerView.ViewHolder {
        final ImageView fav;
        final ImageView i1;
        final ImageView imgMore;
        final CheckBox s1;
        final TextView txtDuration;
        final TextView txtName;
        final TextView txtNew;
        int position;
        TextView txtDate;
        TextView txtSize;
        int xposition;
        FrameLayout nativead;
        ConstraintLayout play;

        @SuppressLint("WrongConstant")
        holder(View view) {
            super(view);
            this.i1 = view.findViewById(R.id.thumb);
            ImageView imageView = view.findViewById(R.id.extra);
            fav = view.findViewById(R.id.fav);
            this.imgMore = imageView;
            this.txtName = view.findViewById(R.id.name);
            this.txtDuration = view.findViewById(R.id.duration);
            this.s1 = view.findViewById(R.id.delete);
            this.txtNew = view.findViewById(R.id.txtNew);
            this.nativead = view.findViewById(R.id.native_ad_container);
            this.play = view.findViewById(R.id.play);

            if (MVideoAdapter.this.view) {
                this.txtSize = view.findViewById(R.id.size);
                this.txtDate = view.findViewById(R.id.date);
            }
            view.setOnClickListener(view1 -> {
                if (MVideoAdapter.this.action) {
                    if (MVideoAdapter.this.mSparseBoolMultiSelect.get(holder.this.xposition)) {
                        view1.setSelected(false);
                        holder.this.s1.setChecked(false);
                        MVideoAdapter.this.mSparseBoolMultiSelect.delete(holder.this.xposition);
                        if (MVideoAdapter.this.mSparseBoolMultiSelect.size() == 0) {
                            MVideoAdapter.this.mActionMode.finish();
                            MVideoAdapter.this.notifyDataSetChanged();
                            return;
                        }
                    }
                    view1.setSelected(true);
                    holder.this.s1.setChecked(true);
                    MVideoAdapter.this.mSparseBoolMultiSelect.put(holder.this.xposition, true);
                    MVideoAdapter.this.mActionMode.setTitle(String.format("%d selected", MVideoAdapter.this.mSparseBoolMultiSelect.size()));
                    return;
                }
                PreferenceUtil instance = PreferenceUtil.getInstance(MVideoAdapter.this.ctx);
                instance.setIsPlayVideo(true, "" + MVideoAdapter.this.filteredvideoList.get(MVideoAdapter.this.getRealPosition(holder.this.position)).getDISPLAY_NAME());
                Intent intent = new Intent(MVideoAdapter.this.ctx, VideoPlayerActivity.class);
                intent.setFlags(402653184);
                intent.putExtra("list", (Serializable) MVideoAdapter.this.filteredvideoList);
                intent.putExtra("position", holder.this.position);
                MVideoAdapter.this.ctx.startActivity(intent);
            });
            imageView.setOnClickListener(view12 -> {
                final PopupMenu popupMenu = new PopupMenu(MVideoAdapter.this.ctx, holder.this.imgMore);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.delete:
                            View inflate = LayoutInflater.from(MVideoAdapter.this.ctx).inflate(R.layout.checkbox, null);
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MVideoAdapter.this.ctx, R.style.MyAlertDialogStyle));
                            builder.setTitle("Delete video from device?");
                            builder.setMessage("Video will be deleted permanently from device.");
                            builder.setView(inflate);
                            builder.setPositiveButton("DELETE", (dialogInterface, i) -> {
                                List list;
                                if (MVideoAdapter.this.checktrue) {
                                    String recycleVideo = PreferenceUtil.getInstance(MVideoAdapter.this.ctx).getRecycleVideo();
                                    if (recycleVideo.equalsIgnoreCase("")) {
                                        list = new ArrayList();
                                    } else {
                                        list = new Gson().fromJson(recycleVideo, new TypeToken<ArrayList<PrivateItem>>() {
                                        }.getType());
                                    }
                                    File file = new File(String.valueOf(MVideoAdapter.this.ctx.getExternalFilesDir(MVideoAdapter.this.ctx.getResources().getString(R.string.recycler_folder_name))));
                                    File file2 = new File(MVideoAdapter.this.filteredvideoList.get(MVideoAdapter.this.getRealPosition(holder.this.position)).getDATA());
                                    Gson gson = new Gson();
                                    list.add(new PrivateItem(file2.getParent(), MVideoAdapter.this.filteredvideoList.get(MVideoAdapter.this.getRealPosition(holder.this.position)).getDISPLAY_NAME()));
                                    PreferenceUtil.getInstance(MVideoAdapter.this.ctx).setRecycleVideo(gson.toJson(list));
                                    try {
                                        MVideoAdapter.this.moveFile(file2, file);
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                    }
                                    String[] strArr = {file2.getAbsolutePath()};
                                    ContentResolver contentResolver = MVideoAdapter.this.ctx.getContentResolver();
                                    Uri contentUri = MediaStore.Files.getContentUri("external");
                                    contentResolver.delete(contentUri, "_data=?", strArr);
                                    if (file2.exists()) {
                                        contentResolver.delete(contentUri, "_data=?", strArr);
                                    }
                                    MVideoAdapter.this.filteredvideoList.remove(holder.this.position);
                                    MVideoAdapter.this.notifyItemRemoved(holder.this.position);
                                    MVideoAdapter.this.notifyItemRangeChanged(holder.this.position, MVideoAdapter.this.filteredvideoList.size());
                                    return;
                                }
                                Toast.makeText(MVideoAdapter.this.ctx, "permunant delete", Toast.LENGTH_SHORT).show();
                                String[] strArr2 = {new File(MVideoAdapter.this.filteredvideoList.get(MVideoAdapter.this.getRealPosition(holder.this.position)).getDATA()).getAbsolutePath()};
                                ContentResolver contentResolver2 = MVideoAdapter.this.ctx.getContentResolver();
                                Uri contentUri2 = MediaStore.Files.getContentUri("external");
                                contentResolver2.delete(contentUri2, "_data=?", strArr2);
                                if (new File(MVideoAdapter.this.filteredvideoList.get(MVideoAdapter.this.getRealPosition(holder.this.position)).getDATA()).exists()) {
                                    contentResolver2.delete(contentUri2, "_data=?", strArr2);
                                }
                                MVideoAdapter.this.filteredvideoList.remove(holder.this.position);
                                MVideoAdapter.this.notifyItemRemoved(holder.this.position);
                                MVideoAdapter.this.notifyItemRangeChanged(holder.this.position, MVideoAdapter.this.filteredvideoList.size());
                            });
                            builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.cancel());
                            builder.show();
                            builder.setOnDismissListener(dialogInterface -> MVideoAdapter.this.checktrue = false);
                            CheckBox checkBox = inflate.findViewById(R.id.checkbox);
                            checkBox.setText("Move to Recycle Bin");
                            checkBox.setOnCheckedChangeListener((compoundButton, z) -> MVideoAdapter.this.checktrue = z);
                            break;
                        case R.id.help:
                            MVideoAdapter.this.ctx.startActivity(new Intent(MVideoAdapter.this.ctx, SettingsActivity.class));
                            break;
                        case R.id.lock:
                            popupMenu.dismiss();
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(new ContextThemeWrapper(MVideoAdapter.this.ctx, R.style.MyAlertDialogStyle));
                            builder2.setTitle("Lock video?");
                            builder2.setMessage("Videos will be moved in private folder.Only you can watch them.");
                            builder2.setPositiveButton("LOCK", (dialogInterface, i) -> {
                                List list;
                                String lockVideo = PreferenceUtil.getInstance(MVideoAdapter.this.ctx).getLockVideo();
                                if (lockVideo.equalsIgnoreCase("")) {
                                    list = new ArrayList();
                                } else {
                                    list = new Gson().fromJson(lockVideo, new TypeToken<ArrayList<PrivateItem>>() {
                                    }.getType());
                                }
                                File file = new File(String.valueOf(MVideoAdapter.this.ctx.getExternalFilesDir(MVideoAdapter.this.ctx.getResources().getString(R.string.private_folder_name))));
                                File file2 = new File(MVideoAdapter.this.filteredvideoList.get(MVideoAdapter.this.getRealPosition(holder.this.position)).getDATA());
                                Gson gson = new Gson();
                                list.add(new PrivateItem(file2.getParent(), MVideoAdapter.this.filteredvideoList.get(MVideoAdapter.this.getRealPosition(holder.this.position)).getDISPLAY_NAME()));
                                PreferenceUtil.getInstance(MVideoAdapter.this.ctx).setLockVideo(gson.toJson(list));
                                try {
                                    MVideoAdapter.this.moveFile(file2, file);
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                                try {
                                    //noinspection ResultOfMethodCallIgnored
                                    file2.delete();
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                                String[] strArr = {file2.getAbsolutePath()};
                                ContentResolver contentResolver = MVideoAdapter.this.ctx.getContentResolver();
                                Uri contentUri = MediaStore.Files.getContentUri("external");
                                contentResolver.delete(contentUri, "_data=?", strArr);
                                if (file2.exists()) {
                                    contentResolver.delete(contentUri, "_data=?", strArr);
                                }
                                MVideoAdapter.this.filteredvideoList.remove(holder.this.position);
                                MVideoAdapter.this.notifyItemRemoved(holder.this.position);
                                MVideoAdapter.this.notifyItemRangeChanged(holder.this.position, MVideoAdapter.this.filteredvideoList.size());
                            });
                            builder2.setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.cancel());
                            builder2.show();
                            return true;
                        case R.id.properties:
                            AlertDialog.Builder builder3 = new AlertDialog.Builder(new ContextThemeWrapper(MVideoAdapter.this.ctx, R.style.MyAlertDialogStyle));
                            LayoutInflater from = LayoutInflater.from(MVideoAdapter.this.ctx);
                            builder3.setTitle("Properties");
                            View inflate2 = from.inflate(R.layout.properties_dialog, null);
                            ((TextView) inflate2.findViewById(R.id.name)).setText(MVideoAdapter.this.filteredvideoList.get(MVideoAdapter.this.getRealPosition(holder.this.position)).getDISPLAY_NAME());
                            ((TextView) inflate2.findViewById(R.id.duration)).setText(MVideoAdapter.this.filteredvideoList.get(MVideoAdapter.this.getRealPosition(holder.this.position)).getDURATION());
                            ((TextView) inflate2.findViewById(R.id.fsize)).setText(MVideoAdapter.this.filteredvideoList.get(MVideoAdapter.this.getRealPosition(holder.this.position)).getSIZE());
                            ((TextView) inflate2.findViewById(R.id.location)).setText(MVideoAdapter.this.filteredvideoList.get(MVideoAdapter.this.getRealPosition(holder.this.position)).getDATA());
                            ((TextView) inflate2.findViewById(R.id.date)).setText(MVideoAdapter.this.filteredvideoList.get(MVideoAdapter.this.getRealPosition(holder.this.position)).getDATE());
                            builder3.setView(inflate2).setPositiveButton("OK", (dialogInterface, i) -> {
                            });
                            builder3.show();
                            break;
                        case R.id.rename:
                            File file = new File(MVideoAdapter.this.filteredvideoList.get(MVideoAdapter.this.getRealPosition(holder.this.position)).getDATA());
                            Log.e("file ", " : " + file.getAbsoluteFile().getParent());
                            Log.e("file ", " : " + file.getName());
                            final String parent = file.getAbsoluteFile().getParent();
                            final String name = file.getName();
                            String substring = name.substring(0, name.lastIndexOf("."));
                            final String substring2 = name.substring(name.lastIndexOf("."));
                            Log.e("Name : " + substring, " : " + substring2);
                            AlertDialog.Builder builder4 = new AlertDialog.Builder(MVideoAdapter.this.ctx, R.style.CustomDialog1);
                            View inflate3 = LayoutInflater.from(MVideoAdapter.this.ctx).inflate(R.layout.dialog_file_rename, null);
                            TextView textView = inflate3.findViewById(R.id.txtTitle);
                            final EditText editText = inflate3.findViewById(R.id.edtName);
                            builder4.setView(inflate3);
                            final AlertDialog create = builder4.create();
                            editText.setText(substring);
                            inflate3.findViewById(R.id.txtSave).setOnClickListener(view1212 -> {
                                if (editText.getText().toString().trim().equalsIgnoreCase("")) {
                                    Toast.makeText(MVideoAdapter.this.ctx, "Enter file name", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                File file1 = new File(parent, name);
                                File file2 = new File(parent, editText.getText().toString().trim() + substring2);
                                boolean renameTo = file1.renameTo(file2);
                                Log.e("success : ", " : " + renameTo);
                                MVideoAdapter.this.ctx.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file2)));
                                MVideoAdapter.this.ctx.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file1)));
                                MVideoAdapter.this.notifyDataSetChanged();
                                MVideoAdapter.this.ctx.finish();
                                create.dismiss();
                            });
                            inflate3.findViewById(R.id.txtCancel).setOnClickListener(view121 -> create.dismiss());
                            create.show();
                            break;
                        case R.id.retag:
                            AlertDialog.Builder builder5 = new AlertDialog.Builder(new ContextThemeWrapper(MVideoAdapter.this.ctx, R.style.MyAlertDialogStyle));
                            builder5.setTitle("Retag video?");
                            builder5.setMessage("Add tag, This Video is a New.");
                            builder5.setPositiveButton("RETAG", (dialogInterface, i) -> {
                                PreferenceUtil instance = PreferenceUtil.getInstance(MVideoAdapter.this.ctx);
                                instance.setIsPlayVideo(false, "" + MVideoAdapter.this.filteredvideoList.get(MVideoAdapter.this.getRealPosition(holder.this.position)).getDISPLAY_NAME());
                                MVideoAdapter.this.notifyDataSetChanged();
                            });
                            builder5.setNegativeButton("No", null);
                            builder5.show();
                            break;
                        case R.id.share:
                            try {
                                Uri uriForFile = FileProvider.getUriForFile(MVideoAdapter.this.ctx, MVideoAdapter.this.ctx.getPackageName() + ".provider", new File(MVideoAdapter.this.filteredvideoList.get(MVideoAdapter.this.getRealPosition(holder.this.position)).getDATA()));
                                Intent intent = new Intent("android.intent.action.SEND");
                                intent.setType("video/*");
                                intent.putExtra("android.intent.extra.STREAM", uriForFile);
                                intent.putExtra("android.intent.extra.TEXT", MVideoAdapter.this.filteredvideoList.get(MVideoAdapter.this.getRealPosition(holder.this.position)).getDISPLAY_NAME());
                                intent.putExtra("android.intent.extra.SUBJECT", MVideoAdapter.this.filteredvideoList.get(MVideoAdapter.this.getRealPosition(holder.this.position)).getDISPLAY_NAME());
                                intent.addFlags(268435456);
                                MVideoAdapter.this.ctx.startActivity(Intent.createChooser(intent, "Share Video"));
                                break;
                            } catch (Exception e) {
                                Log.e("kjfdkjkjfd", "onMenuItemClick: " + e.getMessage());
                                break;
                            }
                    }
                    return true;
                });
                popupMenu.show();
            });
        }
    }
}
