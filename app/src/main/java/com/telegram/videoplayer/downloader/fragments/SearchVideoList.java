package com.telegram.videoplayer.downloader.fragments;

public class SearchVideoList {
    private final String msvlVideoId;
    private final String msvlTitle;
    private final String msvlChanal;

    public SearchVideoList(String msvl_videoId, String msvl_title, String msvl_chanal) {
        this.msvlVideoId = msvl_videoId;
        this.msvlTitle = msvl_title;
        this.msvlChanal = msvl_chanal;
    }

    public String getMsvl_videoId() {
        return msvlVideoId;
    }


    public String getMsvl_title() {
        return msvlTitle;
    }


    public String getMsvl_chanal() {
        return msvlChanal;
    }


}
