package com.telegram.videoplayer.downloader.duplicate;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.activitys.BaseActivity;

@SuppressWarnings("EmptyMethod")
public class ResultActivity extends BaseActivity {
    Toolbar toolbar;

    @Override

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_row);
        getWindow().setFlags(1024, 1024);
        intView();
        intData();
    }

    public void intView() {
        Toolbar toolbar2 = findViewById(R.id.toolbar);
        this.toolbar = toolbar2;
        toolbar2.setTitle(getIntent().getStringExtra("title_tool_bar"));
        setSupportActionBar(this.toolbar);
    }

    public void intData() {
        int intExtra = getIntent().getIntExtra("value", 0);
        ((TextView) findViewById(R.id.tvStatus)).setText(intExtra + " Files Removed");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
