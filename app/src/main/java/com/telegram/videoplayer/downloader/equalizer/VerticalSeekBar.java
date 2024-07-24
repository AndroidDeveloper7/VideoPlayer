package com.telegram.videoplayer.downloader.equalizer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.view.ViewCompat;

import com.telegram.videoplayer.downloader.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings({"FieldCanBeLocal", "ConstantConditions"})
public class VerticalSeekBar extends AppCompatSeekBar {
    private boolean mIsDragging;
    private Method mMethodSetProgressFromUser;
    private int mRotationAngle = 270;

    public VerticalSeekBar(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public VerticalSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context, attributeSet, 0);
    }

    public VerticalSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initialize(context, attributeSet, i);
    }

    private static boolean isValidRotationAngle(int i) {
        return i == 90 || i == 270;
    }

    private void initialize(Context context, AttributeSet attributeSet, int i) {
        ViewCompat.setLayoutDirection(this, ViewCompat.LAYOUT_DIRECTION_LTR);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.VerticalSeekBar, i, 0);
            int integer = obtainStyledAttributes.getInteger(0, 0);
            if (isValidRotationAngle(integer)) {
                this.mRotationAngle = integer;
            }
            obtainStyledAttributes.recycle();
        }
    }

    public void setThumb(Drawable drawable) {
        super.setThumb(drawable);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (useViewRotation()) {
            return onTouchEventUseViewRotation(motionEvent);
        }
        return onTouchEventTraditionalRotation(motionEvent);
    }

    private boolean onTouchEventTraditionalRotation(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            setPressed(true);
            onStartTrackingTouch();
            trackTouchEvent(motionEvent);
            attemptClaimDrag(true);
            invalidate();
        } else if (action == 1) {
            if (this.mIsDragging) {
                trackTouchEvent(motionEvent);
                onStopTrackingTouch();
                setPressed(false);
            } else {
                onStartTrackingTouch();
                trackTouchEvent(motionEvent);
                onStopTrackingTouch();
                attemptClaimDrag(false);
            }
            invalidate();
        } else if (action != 2) {
            if (action == 3) {
                if (this.mIsDragging) {
                    onStopTrackingTouch();
                    setPressed(false);
                }
                invalidate();
            }
        } else if (this.mIsDragging) {
            trackTouchEvent(motionEvent);
        }
        return true;
    }

    private boolean onTouchEventUseViewRotation(MotionEvent motionEvent) {
        boolean onTouchEvent = super.onTouchEvent(motionEvent);
        if (onTouchEvent) {
            int action = motionEvent.getAction();
            if (action == 0) {
                attemptClaimDrag(true);
            } else if (action == 1 || action == 3) {
                attemptClaimDrag(false);
            }
        }
        return onTouchEvent;
    }

    private void trackTouchEvent(MotionEvent motionEvent) {
        int paddingLeft = super.getPaddingLeft();
        int paddingRight = super.getPaddingRight();
        int height = getHeight() - paddingLeft;
        int i = height - paddingRight;
        int y = (int) motionEvent.getY();
        int i2 = this.mRotationAngle;
        float f = 0.0f;
        float f2 = i2 != 90 ? i2 != 270 ? 0.0f : (float) (height - y) : (float) (y - paddingLeft);
        if (f2 >= 0.0f && i != 0) {
            float f3 = (float) i;
            f = f2 > f3 ? 1.0f : f2 / f3;
        }
        setProgressFromUser((int) (f * ((float) getMax())));
    }

    private void attemptClaimDrag(boolean z) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(z);
        }
    }

    private void onStartTrackingTouch() {
        this.mIsDragging = true;
    }

    private void onStopTrackingTouch() {
        this.mIsDragging = false;
    }


    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (isEnabled()) {
            int i2 = -1;
            boolean z = false;
            switch (i) {
                case 19:
                case 20:
                    break;
                case 21:
                case 22:
                    return false;
                default:
                    i2 = 0;
                    break;
            }
            if (z) {
                int progress = getProgress() + (i2 * getKeyProgressIncrement());
                if (progress >= 0 && progress <= getMax()) {
                    setProgressFromUser(progress);
                }
                return true;
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    public synchronized void setProgress(int i) {
        super.setProgress(i);
        if (!useViewRotation()) {
            refreshThumb();
        }
    }

    private synchronized void setProgressFromUser(int i) {
        if (this.mMethodSetProgressFromUser == null) {
            try {
                Method declaredMethod = ProgressBar.class.getDeclaredMethod("setProgress", Integer.TYPE, Boolean.TYPE);
                declaredMethod.setAccessible(true);
                this.mMethodSetProgressFromUser = declaredMethod;
            } catch (NoSuchMethodException ignored) {
            }
        }
        Method method = this.mMethodSetProgressFromUser;
        if (method != null) {
            try {
                method.invoke(this, i, Boolean.TRUE);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignored) {
            }
        } else {
            super.setProgress(i);
        }
        refreshThumb();
    }


    public synchronized void onMeasure(int i, int i2) {
        if (useViewRotation()) {
            super.onMeasure(i, i2);
        } else {
            super.onMeasure(i2, i);
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (!isInEditMode() || layoutParams == null || layoutParams.height < 0) {
                setMeasuredDimension(super.getMeasuredHeight(), super.getMeasuredWidth());
            } else {
                setMeasuredDimension(super.getMeasuredHeight(), layoutParams.height);
            }
        }
    }


    public void onSizeChanged(int i, int i2, int i3, int i4) {
        if (useViewRotation()) {
            super.onSizeChanged(i, i2, i3, i4);
        } else {
            super.onSizeChanged(i2, i, i4, i3);
        }
    }


    @Override
    public synchronized void onDraw(Canvas canvas) {
        if (!useViewRotation()) {
            int i = this.mRotationAngle;
            if (i == 90) {
                canvas.rotate(90.0f);
                canvas.translate(0.0f, (float) (-super.getWidth()));
            } else if (i == 270) {
                canvas.rotate(-90.0f);
                canvas.translate((float) (-super.getHeight()), 0.0f);
            }
        }
        super.onDraw(canvas);
    }

    public int getRotationAngle() {
        return this.mRotationAngle;
    }


    private void refreshThumb() {
        onSizeChanged(super.getWidth(), super.getHeight(), 0, 0);
    }


    public boolean useViewRotation() {
        return !isInEditMode();
    }

}
