package com.telegram.videoplayer.downloader.model;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.simplemobiletools.commons.helpers.ConstantsKt;
import com.simplemobiletools.commons.helpers.MyContentProvider;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unchecked", "rawtypes", "ConstantConditions"})
public class MediaQuery {
    private final Context context;

    public MediaQuery(Context context2) {
        this.context = context2;
    }


    public ArrayList<folder> getFolderList() {
        Log.e("ee", "getFolderList: ======eeeeeeeeeeeeeeee=============>>>>bucket_id");
        Cursor query = this.context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"bucket_id"}, null, null, null);
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        while (query.moveToNext()) {
            Log.e("ee", "getFolderList: ======eeeeeeeeeeeeeeee====/////////////////=11111111111111111111111/////========>>>>" + query.getString(0));
            linkedHashSet.add(query.getString(0));
        }
        ArrayList arrayList = new ArrayList(linkedHashSet);
        String[] strArr = {"bucket_display_name", "_data", "bucket_id", "_size", "date_modified"};
        ArrayList<folder> arrayList2 = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            try {
                Cursor query2 = this.context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, strArr, "bucket_id =?", new String[]{(String) arrayList.get(i)}, "date_modified DESC");
                if (query2 != null && query2.moveToNext()) {
                    folder folder = new folder();
                    String str = "";
                    folder.setBucket(query2.getString(0) != null ? query2.getString(0) : str);
                    folder.setData(query2.getString(1) != null ? query2.getString(1) : str);
                    String str2 = "0";
                    folder.setBid(query2.getString(2) != null ? query2.getString(2) : str2);
                    if (query2.getString(4) != null) {
                        str2 = query2.getString(4);
                    }
                    folder.setDate(date(str2));
                    folder.setCount(String.valueOf(query2.getCount()));
                    if (String.valueOf(folder.getBucket().charAt(0)) != null) {
                        str = String.valueOf(folder.getBucket().charAt(0));
                    }
                    long j = 0;
                    for (int i2 = 0; i2 < query2.getCount(); i2++) {
                        j += query2.getLong(3);
                    }
                    folder.setSize(size(String.valueOf(j)));
                    folder.setFolderSize(j);
                    if (!str.equalsIgnoreCase(".")) {
                        arrayList2.add(folder);
                    }
                }
                query2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return arrayList2;
    }


    public ArrayList getAllVideo(String str) {
        Cursor query = this.context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MyContentProvider.COL_ID, "_size", "date_modified", "_data", "_display_name", "duration"}, "bucket_id =?", new String[]{str}, "date_modified DESC");
        ArrayList arrayList = new ArrayList();
        while (query.moveToNext()) {
            VideoItem videoItem = new VideoItem();
            String str2 = "0";
            videoItem.set_ID(query.getString(0) != null ? query.getString(0) : str2);
            videoItem.setSIZE(size(query.getString(1)));
            if (query.getString(2) != null) {
                str2 = query.getString(2);
            }
            videoItem.setDATE(date(str2));
            String str3 = "";
            videoItem.setDATA(query.getString(3) != null ? query.getString(3) : str3);
            if (query.getString(4) != null) {
                str3 = query.getString(4);
            }
            videoItem.setDISPLAY_NAME(str3);
            videoItem.setDURATION(duration(query.getString(5)));
            videoItem.setVideoSize(query.getLong(1));
            arrayList.add(videoItem);
        }
        query.close();
        return arrayList;
    }


    private String duration(String str) {
        try {
            long parseInt = Integer.parseInt(str);
            long hours = TimeUnit.MILLISECONDS.toHours(parseInt);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(parseInt) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(parseInt));
            long seconds = TimeUnit.MILLISECONDS.toSeconds(parseInt) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(parseInt));
            if (hours >= 1) {
                return String.format("%02d:%02d:%02d", hours, minutes, seconds);
            }
            return String.format("%02d:%02d", minutes, seconds);
        } catch (Exception unused) {
            return "00:00";
        }
    }

    private String date(String str) {
        return new SimpleDateFormat(ConstantsKt.DATE_FORMAT_FOUR).format(new Date(Long.parseLong(str) * 1000));
    }

    private String size(String str) {
        if (str == null) {
            str = "123";
        }
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


}
