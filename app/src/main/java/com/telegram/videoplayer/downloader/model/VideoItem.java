package com.telegram.videoplayer.downloader.model;

import java.io.Serializable;

public class VideoItem implements Serializable {
    public String data;
    public String displayName;
    private String date;
    private String duration;
    private String size;
    private long videoSize;
    private boolean isfav;

    public VideoItem() {
    }

    public void set_ID(String str) {
    }

    public String getSIZE() {
        return this.size;
    }

    public void setSIZE(String str) {
        this.size = str;
    }

    public String getDATE() {
        return this.date;
    }

    public void setDATE(String str) {
        this.date = str;
    }

    public String getDATA() {
        return this.data;
    }

    public void setDATA(String str) {
        this.data = str;
    }

    public String getDISPLAY_NAME() {
        return this.displayName;
    }

    public void setDISPLAY_NAME(String str) {
        if (str != null) {
            this.displayName = str;
        }
    }

    public String getDURATION() {
        return this.duration;
    }

    public void setDURATION(String str) {
        this.duration = str;
    }

    public long getVideoSize() {
        return this.videoSize;
    }

    public void setVideoSize(long j) {
        this.videoSize = j;
    }

    public boolean isIsfav() {
        return isfav;
    }

    public void setIsfav(boolean isfav) {
        this.isfav = isfav;
    }
}
