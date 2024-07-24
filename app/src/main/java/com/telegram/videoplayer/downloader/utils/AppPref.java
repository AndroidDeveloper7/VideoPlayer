package com.telegram.videoplayer.downloader.utils;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.telegram.videoplayer.downloader.model.VideoItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class AppPref {

    private final Context context;

    public AppPref(Context context) {
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    public ArrayList getList() {
        @SuppressWarnings("unchecked") List<VideoItem> arrayList = new ArrayList();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString("TAG", "");
        Type type = new TypeToken<List<VideoItem>>() {
        }.getType();
        arrayList = gson.fromJson(json, type);

        if (arrayList != null) {
            return (ArrayList) arrayList;
        }
        return new ArrayList();
    }

    public void setList(List<VideoItem> arrayList) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(arrayList);

        editor.putString("TAG", json);
        editor.commit();
    }

    public void addremoveFav(VideoItem videoItem, boolean isadd) {
        List<VideoItem> arrayList = getList();
        if (isadd) {
            arrayList.add(videoItem);
        } else {
            for (int i = 0; i < arrayList.size(); i++) {
                VideoItem videoItemz = arrayList.get(i);
                if (videoItemz.getDATA().equals(videoItem.getDATA())) {
                    arrayList.remove(i);
                }
            }
        }
        setList(arrayList);
    }

    public boolean chack(VideoItem videoItemremove) {
        List<VideoItem> arrayList = getList();
        for (VideoItem videoItem : arrayList) {
            if (videoItem.getDATA().equals(videoItemremove.getDATA())) {
                return true;
            }
        }
        return false;
    }


}
