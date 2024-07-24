package com.telegram.videoplayer.downloader.duplicate.Model;

import java.io.File;

@SuppressWarnings("UnusedReturnValue")
public class Duplicate {
    private File file;
    private boolean isChecked = true;


    public int setTypeFile(int i) {
        return i;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file2) {
        this.file = file2;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean z) {
        this.isChecked = z;
    }
}
