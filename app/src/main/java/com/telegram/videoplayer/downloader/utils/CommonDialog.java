package com.telegram.videoplayer.downloader.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.telegram.videoplayer.downloader.R;

@SuppressWarnings({"FinalStaticMethod", "FieldMayBeFinal"})
public final class CommonDialog extends Dialog {
    private final boolean buttonLandscap;
    private final String msg;
    private final boolean negHide;
    private final String negativeButtonText;
    private final String positiveButtonText;
    private final boolean postHide;
    private final String title;
    private Context c;
    private OnButtonClick onButtonClick;

    public CommonDialog(Context context, String str, String str2, String str3, String str4, boolean z, boolean z2, boolean z3) {
        super(context);
        this.c = context;
        this.title = str;
        this.msg = str2;
        this.negativeButtonText = str3;
        this.positiveButtonText = str4;
        this.negHide = z;
        this.postHide = z2;
        this.buttonLandscap = z3;
    }


    public static final void m15onCreate$lambda0(CommonDialog commonDialog) {
        OnButtonClick onButtonClick2 = commonDialog.onButtonClick;
        onButtonClick2.onNegativeButtonClick();
    }


    public static final void m16onCreate$lambda1(CommonDialog commonDialog) {
        OnButtonClick onButtonClick2 = commonDialog.onButtonClick;
        onButtonClick2.onPositiveButtonClick();
    }

    public final Context getC() {
        return this.c;
    }

    public final void setC(Context context) {
        this.c = context;
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.buttonLandscap) {
            setContentView(R.layout.dialog_common_landscap_button);
        } else {
            setContentView(R.layout.dialog_common);
        }
        boolean z = false;
        if (this.msg != null) {
            if (Build.VERSION.SDK_INT >= 24) {
                TextView textView = findViewById(R.id.message);
                textView.setText(Html.fromHtml(this.msg, 0));
            } else {
                TextView textView2 = findViewById(R.id.message);
                textView2.setText(Html.fromHtml(this.msg));
            }
        }
        String str = this.title;
        if (!(str == null || str.length() == 0)) {
            ((TextView) findViewById(R.id.dilog_title)).setText(this.title);
        }
        String str2 = this.negativeButtonText;
        if (!(str2 == null || str2.length() == 0)) {
            ((TextView) findViewById(R.id.negative_button)).setText(this.negativeButtonText);
        }
        String str3 = this.positiveButtonText;
        if (str3 == null || str3.length() == 0) {
            z = true;
        }
        if (!z) {
            ((TextView) findViewById(R.id.positive_button)).setText(this.positiveButtonText);
        }
        if (this.negHide) {
            findViewById(R.id.negative_button).setVisibility(View.GONE);
        }
        if (this.postHide) {
            findViewById(R.id.positive_button).setVisibility(View.GONE);
        }
        findViewById(R.id.negative_button).setOnClickListener(view -> CommonDialog.m15onCreate$lambda0(CommonDialog.this));
        findViewById(R.id.positive_button).setOnClickListener(view -> CommonDialog.m16onCreate$lambda1(CommonDialog.this));
    }

    public interface OnButtonClick {
        void onNegativeButtonClick();

        void onPositiveButtonClick();
    }
}
