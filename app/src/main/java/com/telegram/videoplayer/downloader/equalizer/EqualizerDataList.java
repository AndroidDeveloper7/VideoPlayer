package com.telegram.videoplayer.downloader.equalizer;

public class EqualizerDataList {
    final String name;
    final boolean selectItem;

    public EqualizerDataList(String str, boolean z) {
        this.name = str;
        this.selectItem = z;
    }

    public String getName() {
        return this.name;
    }

    public boolean isSelectItem() {
        return this.selectItem;
    }

}
