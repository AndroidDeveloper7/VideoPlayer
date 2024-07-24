package com.telegram.videoplayer.downloader.utildata;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;

@SuppressWarnings({"FieldCanBeLocal", "ConstantConditions"})
public class AdvanceDrawerLayout extends DrawerLayout {
    final HashMap<Integer, Setting> settings = new HashMap<>();
    public float defaultDrawerElevation;
    public int defaultScrimColor = -1728053248;
    public View drawerView;
    private boolean defaultFitsSystemWindows;
    private FrameLayout frameLayout;
    private int statusBarColor;

    public AdvanceDrawerLayout(Context context) {
        super(context);
        init(context);
    }

    public AdvanceDrawerLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public AdvanceDrawerLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        this.defaultDrawerElevation = getDrawerElevation();
        if (Build.VERSION.SDK_INT >= 16) {
            this.defaultFitsSystemWindows = getFitsSystemWindows();
        }
        if (!isInEditMode() && Build.VERSION.SDK_INT >= 21) {
            this.statusBarColor = getActivity().getWindow().getStatusBarColor();
        }
        addDrawerListener(new DrawerLayout.DrawerListener() {


            @Override
            public void onDrawerClosed(@NonNull View view) {
            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
            }

            @Override
            public void onDrawerStateChanged(int i) {
            }

            @Override
            public void onDrawerSlide(@NonNull View view, float f) {
                AdvanceDrawerLayout.this.updateSlideOffset(view, f);
            }
        });
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.frameLayout = frameLayout2;
        frameLayout2.setPadding(0, 0, 0, 0);
        super.addView(this.frameLayout);
    }

    @Override
    public void addView(View view, ViewGroup.LayoutParams layoutParams) {
        view.setLayoutParams(layoutParams);
        addView(view);
    }

    public void addView(View view) {
        if (view instanceof NavigationView) {
            super.addView(view);
            return;
        }
        CardView cardView = new CardView(getContext());
        cardView.setRadius(0.0f);
        cardView.addView(view);
        cardView.setCardElevation(0.0f);
        if (Build.VERSION.SDK_INT < 21) {
            cardView.setContentPadding(-6, -9, -6, -9);
        }
        this.frameLayout.addView(cardView);
    }


    @Override
    public void setDrawerElevation(float f) {
        this.defaultDrawerElevation = f;
        super.setDrawerElevation(f);
    }

    @Override
    public void setScrimColor(int i) {
        this.defaultScrimColor = i;
        super.setScrimColor(i);
    }


    @Override
    public void openDrawer(@NonNull final View view, boolean z) {
        super.openDrawer(view, z);
        post(() -> {
            AdvanceDrawerLayout advanceDrawerLayout = AdvanceDrawerLayout.this;
            View view1 = null;
            advanceDrawerLayout.updateSlideOffset(view1, advanceDrawerLayout.isDrawerOpen(view1) ? 1.0f : 0.0f);
        });
    }

    @SuppressWarnings("deprecation")
    public void updateSlideOffset(View view, float f) {
        int drawerViewAbsoluteGravity = getDrawerViewAbsoluteGravity(GravityCompat.START);
        int drawerViewAbsoluteGravity2 = getDrawerViewAbsoluteGravity(view);
        @SuppressLint("WrongConstant") boolean z = getLayoutDirection() == LAYOUT_DIRECTION_RTL || getActivity().getWindow().getDecorView().getLayoutDirection() == 1 || getResources().getConfiguration().getLayoutDirection() == 1;
        for (int i = 0; i < this.frameLayout.getChildCount(); i++) {
            CardView cardView = (CardView) this.frameLayout.getChildAt(i);
            Setting setting = this.settings.get(drawerViewAbsoluteGravity2);
            if (setting != null) {
                cardView.setRadius((float) ((int) (setting.radius * f)));
                super.setScrimColor(setting.scrimColor);
                super.setDrawerElevation(setting.drawerElevation);
                ViewCompat.setScaleY(cardView, 1.0f - ((1.0f - setting.percentage) * f));
                cardView.setCardElevation(setting.elevation * f);
                float f2 = setting.elevation;
                boolean z2 = z == (drawerViewAbsoluteGravity2 != drawerViewAbsoluteGravity);
                int width = view.getWidth();
                updateSlideOffset(cardView, z2 ? ((float) width) + f2 : ((float) (-width)) - f2, f);
            } else {
                super.setScrimColor(this.defaultScrimColor);
                super.setDrawerElevation(this.defaultDrawerElevation);
            }
        }
    }


    public Activity getActivity() {
        return getActivity(getContext());
    }

    public Activity getActivity(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            return getActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public void updateSlideOffset(CardView cardView, float f, float f2) {
        ViewCompat.setX(cardView, f * f2);
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        View view = this.drawerView;
        if (view != null) {
            updateSlideOffset(view, isDrawerOpen(view) ? 1.0f : 0.0f);
        }
    }

    public int getDrawerViewAbsoluteGravity(int i) {
        return GravityCompat.getAbsoluteGravity(i, ViewCompat.getLayoutDirection(this)) & 7;
    }

    public int getDrawerViewAbsoluteGravity(View view) {
        return getDrawerViewAbsoluteGravity(((DrawerLayout.LayoutParams) view.getLayoutParams()).gravity);
    }


    public class Setting {
        final float drawerElevation;
        final float elevation;
        final float percentage = 1.0f;
        final int scrimColor;
        float radius;

        Setting() {
            this.scrimColor = AdvanceDrawerLayout.this.defaultScrimColor;
            this.elevation = 0.0f;
            this.drawerElevation = AdvanceDrawerLayout.this.defaultDrawerElevation;
        }


        public float getRadius() {
            return this.radius;
        }


    }
}
