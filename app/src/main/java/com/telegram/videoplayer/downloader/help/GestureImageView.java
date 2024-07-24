package com.telegram.videoplayer.downloader.help;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "ResultOfMethodCallIgnored", "StatementWithEmptyBody"})
@SuppressLint("AppCompatCustomView")
public class GestureImageView extends ImageView {
    public static final String GLOBAL_NS = "http://schemas.android.com/apk/res/android";
    public static final String LOCAL_NS = "http://schemas.polites.com/android";
    private final Semaphore drawLock;
    private final float scale;
    public GestureImageViewListener defaultImageViewListener;
    private int alpha;
    private Animator animator;
    private float centerX;
    private float centerY;
    private ColorFilter colorFilter;
    private View.OnTouchListener customOnTouchListener;
    private int deviceOrientation;
    private int displayHeight;
    private int displayWidth;
    private Drawable drawable;
    private float f417x;
    private float f418y;
    private float fitScaleHorizontal;
    private float fitScaleVertical;
    private GestureImageViewListener gestureImageViewListener;
    private GestureImageViewTouchListener gestureImageViewTouchListener;
    private int hHeight;
    private int hWidth;
    private int imageOrientation;
    private boolean layout;
    private float maxScale;
    private float minScale;
    private View.OnClickListener onClickListener;
    private boolean recycle;
    private int resId;
    private float rotation;
    private float scaleAdjust;
    private Float startX;
    private Float startY;
    private float startingScale;
    private boolean strict;

    public GestureImageView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet);
    }

    public GestureImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.drawLock = new Semaphore(0);
        this.f417x = 0.0f;
        this.f418y = 0.0f;
        this.layout = false;
        this.scaleAdjust = 1.0f;
        this.startingScale = -1.0f;
        this.scale = 1.0f;
        this.maxScale = 5.0f;
        this.minScale = 0.75f;
        this.fitScaleHorizontal = 1.0f;
        this.fitScaleVertical = 1.0f;
        this.rotation = 0.0f;
        this.resId = -1;
        this.recycle = false;
        this.strict = false;
        this.alpha = 255;
        this.deviceOrientation = -1;
        this.defaultImageViewListener = new GestureImageViewListener() {

            @Override
            public void onScale() {
            }

            @Override
            public void onTouch() {
            }

            @Override
            public void onPosition(float f) {
                double scaledWidth = GestureImageView.this.getScaledWidth();
                Double.isNaN(scaledWidth);
                double d = scaledWidth / 2.0d;
                double width = GestureImageView.this.getWidth();
                System.out.println(f);
                PrintStream printStream = System.out;
                StringBuilder sb = new StringBuilder();
                sb.append(d);
                sb.append("::");
                Double.isNaN(width);
                double d2 = width - d;
                sb.append(d2);
                printStream.println(sb.toString());
                GestureImageView.this.getParent().requestDisallowInterceptTouchEvent((double) f != d && (double) f != d2);
            }
        };
        String attributeValue = attributeSet.getAttributeValue(GLOBAL_NS, "scaleType");
        if (attributeValue == null || attributeValue.trim().length() == 0) {
            setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
        String attributeValue2 = attributeSet.getAttributeValue(LOCAL_NS, "start-x");
        String attributeValue3 = attributeSet.getAttributeValue(LOCAL_NS, "start-y");
        if (attributeValue2 != null && attributeValue2.trim().length() > 0) {
            this.startX = Float.parseFloat(attributeValue2);
        }
        if (attributeValue3 != null && attributeValue3.trim().length() > 0) {
            this.startY = Float.parseFloat(attributeValue3);
        }
        setStartingScale(attributeSet.getAttributeFloatValue(LOCAL_NS, "start-scale", this.startingScale));
        setMinScale(attributeSet.getAttributeFloatValue(LOCAL_NS, "min-scale", this.minScale));
        setMaxScale(attributeSet.getAttributeFloatValue(LOCAL_NS, "max-scale", this.maxScale));
        setStrict(attributeSet.getAttributeBooleanValue(LOCAL_NS, "strict", this.strict));
        setRecycle(attributeSet.getAttributeBooleanValue(LOCAL_NS, "recycle", this.recycle));
        initImage();
        setGestureImageViewListener(this.defaultImageViewListener);
    }

    public GestureImageView(Context context) {
        super(context);
        this.drawLock = new Semaphore(0);
        this.f417x = 0.0f;
        this.f418y = 0.0f;
        this.layout = false;
        this.scaleAdjust = 1.0f;
        this.startingScale = -1.0f;
        this.scale = 1.0f;
        this.maxScale = 5.0f;
        this.minScale = 0.75f;
        this.fitScaleHorizontal = 1.0f;
        this.fitScaleVertical = 1.0f;
        this.rotation = 0.0f;
        this.resId = -1;
        this.recycle = false;
        this.strict = false;
        this.alpha = 255;
        this.deviceOrientation = -1;
        setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        initImage();
        setGestureImageViewListener(this.defaultImageViewListener);
    }

    public void onMeasure(int i, int i2) {
        if (this.drawable == null) {
            this.displayHeight = View.MeasureSpec.getSize(i2);
            this.displayWidth = View.MeasureSpec.getSize(i);
        } else if (getResources().getConfiguration().orientation == 2) {
            this.displayHeight = View.MeasureSpec.getSize(i2);
            if (getLayoutParams().width == -2) {
                this.displayWidth = Math.round(((float) this.displayHeight) * (((float) getImageWidth()) / ((float) getImageHeight())));
            } else {
                this.displayWidth = View.MeasureSpec.getSize(i);
            }
        } else {
            this.displayWidth = View.MeasureSpec.getSize(i);
            if (getLayoutParams().height == -2) {
                this.displayHeight = Math.round(((float) this.displayWidth) * (((float) getImageHeight()) / ((float) getImageWidth())));
            } else {
                this.displayHeight = View.MeasureSpec.getSize(i2);
            }
        }
        setMeasuredDimension(this.displayWidth, this.displayHeight);
    }

    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (z || !this.layout) {
            setupCanvas(this.displayWidth, this.displayHeight, getResources().getConfiguration().orientation);
        }
    }

    public void setupCanvas(int i, int i2, int i3) {
        if (this.deviceOrientation != i3) {
            this.layout = false;
            this.deviceOrientation = i3;
        }
        if (this.drawable != null && !this.layout) {
            int imageWidth = getImageWidth();
            int imageHeight = getImageHeight();
            this.hWidth = Math.round(((float) imageWidth) / 2.0f);
            this.hHeight = Math.round(((float) imageHeight) / 2.0f);
            int paddingLeft = i - (getPaddingLeft() + getPaddingRight());
            int paddingTop = i2 - (getPaddingTop() + getPaddingBottom());
            computeCropScale(imageWidth, imageHeight, paddingLeft, paddingTop);
            if (this.startingScale <= 0.0f) {
                computeStartingScale(imageWidth, imageHeight, paddingLeft, paddingTop);
            }
            this.scaleAdjust = this.startingScale;
            float f = ((float) paddingLeft) / 2.0f;
            this.centerX = f;
            this.centerY = ((float) paddingTop) / 2.0f;
            Float f2 = this.startX;
            if (f2 == null) {
                this.f417x = f;
            } else {
                this.f417x = f2;
            }
            Float f3 = this.startY;
            if (f3 == null) {
                this.f418y = this.centerY;
            } else {
                this.f418y = f3;
            }
            this.gestureImageViewTouchListener = new GestureImageViewTouchListener(this, paddingLeft, paddingTop);
            if (isLandscape()) {
                this.gestureImageViewTouchListener.setMinScale(this.minScale * this.fitScaleHorizontal);
            } else {
                this.gestureImageViewTouchListener.setMinScale(this.minScale * this.fitScaleVertical);
            }
            this.gestureImageViewTouchListener.setMaxScale(this.maxScale * this.startingScale);
            this.gestureImageViewTouchListener.setFitScaleHorizontal(this.fitScaleHorizontal);
            this.gestureImageViewTouchListener.setFitScaleVertical(this.fitScaleVertical);
            this.gestureImageViewTouchListener.setCanvasWidth(paddingLeft);
            this.gestureImageViewTouchListener.setCanvasHeight(paddingTop);
            this.gestureImageViewTouchListener.setOnClickListener(this.onClickListener);
            Drawable drawable2 = this.drawable;
            int i4 = this.hWidth;
            int i5 = this.hHeight;
            drawable2.setBounds(-i4, -i5, i4, i5);
            super.setOnTouchListener((view, motionEvent) -> {
                if (GestureImageView.this.customOnTouchListener != null) {
                    GestureImageView.this.customOnTouchListener.onTouch(view, motionEvent);
                }
                return GestureImageView.this.gestureImageViewTouchListener.onTouch(view, motionEvent);
            });
            this.layout = true;
        }
    }

    public void computeCropScale(int i, int i2, int i3, int i4) {
        this.fitScaleHorizontal = ((float) i3) / ((float) i);
        this.fitScaleVertical = ((float) i4) / ((float) i2);
    }

    public void computeStartingScale(int i, int i2, int i3, int i4) {
        int i5 = AnonymousClass3.$SwitchMap$android$widget$ImageView$ScaleType[getScaleType().ordinal()];
        if (i5 == 1) {
            this.startingScale = 1.0f;
        } else if (i5 == 2) {
            this.startingScale = Math.max(((float) i4) / ((float) i2), ((float) i3) / ((float) i));
        } else if (i5 != 3) {
        } else {
            if (((float) i) / ((float) i3) > ((float) i2) / ((float) i4)) {
                this.startingScale = this.fitScaleHorizontal;
            } else {
                this.startingScale = this.fitScaleVertical;
            }
        }
    }

    public boolean isRecycled() {
        Bitmap bitmap;
        Drawable drawable2 = this.drawable;
        if (!(drawable2 instanceof BitmapDrawable) || (bitmap = ((BitmapDrawable) drawable2).getBitmap()) == null) {
            return true;
        }
        return !bitmap.isRecycled();
    }

    public void recycle() {
        Drawable drawable2;
        Bitmap bitmap;
        if (this.recycle && (drawable2 = this.drawable) != null && (drawable2 instanceof BitmapDrawable) && (bitmap = ((BitmapDrawable) drawable2).getBitmap()) != null) {
            bitmap.recycle();
        }
    }

    public void onDraw(Canvas canvas) {
        if (this.layout) {
            if (this.drawable != null && isRecycled()) {
                canvas.save();
                float f = this.scale * this.scaleAdjust;
                canvas.translate(this.f417x, this.f418y);
                float f2 = this.rotation;
                if (f2 != 0.0f) {
                    canvas.rotate(f2);
                }
                if (f != 1.0f) {
                    canvas.scale(f, f);
                }
                this.drawable.draw(canvas);
                canvas.restore();
            }
            if (this.drawLock.availablePermits() <= 0) {
                this.drawLock.release();
            }
        }
    }

    public boolean waitForDraw(long j) throws InterruptedException {
        return this.drawLock.tryAcquire(j, TimeUnit.MILLISECONDS);
    }

    public void onAttachedToWindow() {
        Animator animator2 = new Animator(this, "GestureImageViewAnimator");
        this.animator = animator2;
        animator2.start();
        int i = this.resId;
        if (i >= 0 && this.drawable == null) {
            setImageResource(i);
        }
        super.onAttachedToWindow();
    }

    public void animationStart(Animation animation) {
        Animator animator2 = this.animator;
        if (animator2 != null) {
            animator2.play(animation);
        }
    }

    public void animationStop() {
        Animator animator2 = this.animator;
        if (animator2 != null) {
            animator2.cancel();
        }
    }

    public void onDetachedFromWindow() {
        Animator animator2 = this.animator;
        if (animator2 != null) {
            animator2.finish();
        }
        if (this.recycle && this.drawable != null && isRecycled()) {
            recycle();
            this.drawable = null;
        }
        super.onDetachedFromWindow();
    }

    public void initImage() {
        Drawable drawable2 = this.drawable;
        if (drawable2 != null) {
            drawable2.setAlpha(this.alpha);
            this.drawable.setFilterBitmap(true);
            ColorFilter colorFilter2 = this.colorFilter;
            if (colorFilter2 != null) {
                this.drawable.setColorFilter(colorFilter2);
            }
        }
        if (!this.layout) {
            requestLayout();
            redraw();
        }
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.drawable = new BitmapDrawable(getResources(), bitmap);
        initImage();
    }

    public void setImageDrawable(Drawable drawable2) {
        this.drawable = drawable2;
        initImage();
    }

    @SuppressLint("ResourceType")
    public void setImageResource(int i) {
        if (this.drawable != null) {
            recycle();
        }
        if (i >= 0) {
            this.resId = i;
            setImageDrawable(getContext().getResources().getDrawable(i));
        }
    }

    public int getScaledWidth() {
        return Math.round(((float) getImageWidth()) * getScale());
    }

    public int getScaledHeight() {
        return Math.round(((float) getImageHeight()) * getScale());
    }

    public int getImageWidth() {
        Drawable drawable2 = this.drawable;
        if (drawable2 != null) {
            return drawable2.getIntrinsicWidth();
        }
        return 0;
    }

    public int getImageHeight() {
        Drawable drawable2 = this.drawable;
        if (drawable2 != null) {
            return drawable2.getIntrinsicHeight();
        }
        return 0;
    }


    public void setPosition(float f, float f2) {
        this.f417x = f;
        this.f418y = f2;
    }

    public void redraw() {
        postInvalidate();
    }

    public void setMinScale(float f) {
        this.minScale = f;
        GestureImageViewTouchListener gestureImageViewTouchListener2 = this.gestureImageViewTouchListener;
        if (gestureImageViewTouchListener2 != null) {
            gestureImageViewTouchListener2.setMinScale(f * this.fitScaleHorizontal);
        }
    }

    public void setMaxScale(float f) {
        this.maxScale = f;
        GestureImageViewTouchListener gestureImageViewTouchListener2 = this.gestureImageViewTouchListener;
        if (gestureImageViewTouchListener2 != null) {
            gestureImageViewTouchListener2.setMaxScale(f * this.startingScale);
        }
    }

    public float getScale() {
        return this.scaleAdjust;
    }

    public void setScale(float f) {
        this.scaleAdjust = f;
    }

    public float getImageX() {
        return this.f417x;
    }

    public float getImageY() {
        return this.f418y;
    }


    public void setStrict(boolean z) {
        this.strict = z;
    }

    public void setRecycle(boolean z) {
        this.recycle = z;
    }


    public void setRotation(float f) {
        this.rotation = f;
    }

    public GestureImageViewListener getGestureImageViewListener() {
        return this.gestureImageViewListener;
    }

    public void setGestureImageViewListener(GestureImageViewListener gestureImageViewListener2) {
        this.gestureImageViewListener = gestureImageViewListener2;
    }

    public Drawable getDrawable() {
        return this.drawable;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setAlpha(int i) {
        this.alpha = i;
        Drawable drawable2 = this.drawable;
        if (drawable2 != null) {
            drawable2.setAlpha(i);
        }
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter2) {
        this.colorFilter = colorFilter2;
        Drawable drawable2 = this.drawable;
        if (drawable2 != null) {
            drawable2.setColorFilter(colorFilter2);
        }
    }

    @SuppressLint("Range")
    public void setImageURI(Uri uri) {
        if ("content".equals(uri.getScheme())) {
            try {
                String[] strArr = {"orientation"};
                Cursor query = getContext().getContentResolver().query(uri, strArr, null, null, null);
                if (query != null && query.moveToFirst()) {
                    this.imageOrientation = query.getInt(query.getColumnIndex(strArr[0]));
                }
                InputStream openInputStream = getContext().getContentResolver().openInputStream(uri);
                Bitmap decodeStream = BitmapFactory.decodeStream(openInputStream);
                if (this.imageOrientation != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate((float) this.imageOrientation);
                    Bitmap createBitmap = Bitmap.createBitmap(decodeStream, 0, 0, decodeStream.getWidth(), decodeStream.getHeight(), matrix, true);
                    decodeStream.recycle();
                    setImageDrawable(new BitmapDrawable(getResources(), createBitmap));
                } else {
                    setImageDrawable(new BitmapDrawable(getResources(), decodeStream));
                }
                if (openInputStream != null) {
                    openInputStream.close();
                }
                if (query != null) {
                    query.close();
                }
            } catch (Throwable th) {
                Log.w("GestureImageView", "Unable to open content: " + uri, th);
            }
        } else {
            setImageDrawable(Drawable.createFromPath(uri.toString()));
        }
        if (this.drawable == null) {
            Log.e("GestureImageView", "resolveUri failed on bad bitmap uri: " + uri);
        }
    }

    public Matrix getImageMatrix() {
        if (!this.strict) {
            return super.getImageMatrix();
        }
        throw new UnsupportedOperationException("Not supported");
    }

    public void setImageMatrix(Matrix matrix) {
        if (this.strict) {
            throw new UnsupportedOperationException("Not supported");
        }
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        if (scaleType == ImageView.ScaleType.CENTER || scaleType == ImageView.ScaleType.CENTER_CROP || scaleType == ImageView.ScaleType.CENTER_INSIDE) {
            super.setScaleType(scaleType);
        } else if (this.strict) {
            throw new UnsupportedOperationException("Not supported");
        }
    }

    public void invalidateDrawable(Drawable drawable2) {
        if (!this.strict) {
            super.invalidateDrawable(drawable2);
            return;
        }
        throw new UnsupportedOperationException("Not supported");
    }

    public int[] onCreateDrawableState(int i) {
        if (!this.strict) {
            return super.onCreateDrawableState(i);
        }
        throw new UnsupportedOperationException("Not supported");
    }

    public void setAdjustViewBounds(boolean z) {
        if (!this.strict) {
            super.setAdjustViewBounds(z);
            return;
        }
        throw new UnsupportedOperationException("Not supported");
    }

    public void setImageLevel(int i) {
        if (!this.strict) {
            super.setImageLevel(i);
            return;
        }
        throw new UnsupportedOperationException("Not supported");
    }

    public void setImageState(int[] iArr, boolean z) {
        if (this.strict) {
            throw new UnsupportedOperationException("Not supported");
        }
    }

    public void setSelected(boolean z) {
        if (!this.strict) {
            super.setSelected(z);
            return;
        }
        throw new UnsupportedOperationException("Not supported");
    }

    public void setOnTouchListener(View.OnTouchListener onTouchListener) {
        this.customOnTouchListener = onTouchListener;
    }

    public float getCenterX() {
        return this.centerX;
    }

    public float getCenterY() {
        return this.centerY;
    }

    public boolean isLandscape() {
        return getImageWidth() >= getImageHeight();
    }


    public void setStartingScale(float f) {
        this.startingScale = f;
    }

    public void setOnClickListener(View.OnClickListener onClickListener2) {
        this.onClickListener = onClickListener2;
        GestureImageViewTouchListener gestureImageViewTouchListener2 = this.gestureImageViewTouchListener;
        if (gestureImageViewTouchListener2 != null) {
            gestureImageViewTouchListener2.setOnClickListener(onClickListener2);
        }
    }


    public int getDeviceOrientation() {
        return this.deviceOrientation;
    }

    public static class AnonymousClass3 {
        static final int[] $SwitchMap$android$widget$ImageView$ScaleType;

        static {
            int[] iArr = new int[ImageView.ScaleType.values().length];
            $SwitchMap$android$widget$ImageView$ScaleType = iArr;
            iArr[ImageView.ScaleType.CENTER.ordinal()] = 1;
        }

    }
}
