package com.telegram.videoplayer.downloader.duplicate;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.telegram.videoplayer.downloader.R;
import com.telegram.videoplayer.downloader.activitys.BaseActivity;

@SuppressWarnings("EmptyMethod")
public class NoFileActiviy extends BaseActivity implements View.OnClickListener {
    Toolbar toolbar;

    public void intEvent() {
    }

    public void onClick(View view) {
    }

    @Override

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_row);
        getWindow().setFlags(1024, 1024);
        intView();
        intEvent();
    }

    public void intView() {
        Toolbar toolbar2 = findViewById(R.id.toolbar);
        this.toolbar = toolbar2;
        setSupportActionBar(toolbar2);
        this.toolbar.setTitle("Scan Duplicate Videos");
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
