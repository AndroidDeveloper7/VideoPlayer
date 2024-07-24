package com.telegram.videoplayer.downloader.duplicate.Model;

import java.util.ArrayList;

public class DataModel {
    ArrayList<Duplicate> listDuplicate;
    String titleGroup;

    public String getTitleGroup() {
        return this.titleGroup;
    }

    public void setTitleGroup(String str) {
        this.titleGroup = str;
    }

    public ArrayList<Duplicate> getListDuplicate() {
        return this.listDuplicate;
    }

    public void setListDuplicate(ArrayList<Duplicate> arrayList) {
        this.listDuplicate = arrayList;
    }
}
