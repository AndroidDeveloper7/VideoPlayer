package com.telegram.videoplayer.downloader.duplicate.dialog;

import android.app.Dialog;
import android.content.Context;

import com.telegram.videoplayer.downloader.R;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        super(context);
        requestWindowFeature(1);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.layout_loading_dialog);
    }
}
