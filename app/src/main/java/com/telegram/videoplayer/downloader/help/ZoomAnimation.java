package com.telegram.videoplayer.downloader.help;

import android.graphics.PointF;

public class ZoomAnimation implements Animation {
    private boolean firstFrame = true;
    private float scaleDiff;
    private float startScale;
    private float startX;
    private float startY;
    private long totalTime = 0;
    private float touchX;
    private float touchY;
    private float xDiff;
    private float yDiff;
    private float zoom;
    private ZoomAnimationListener zoomAnimationListener;

    @Override
    public boolean update(GestureImageView gestureImageView, long j) {
        if (this.firstFrame) {
            this.firstFrame = false;
            this.startX = gestureImageView.getImageX();
            this.startY = gestureImageView.getImageY();
            float scale = gestureImageView.getScale();
            this.startScale = scale;
            float f = (this.zoom * scale) - scale;
            this.scaleDiff = f;
            if (f > 0.0f) {
                VectorF vectorF = new VectorF();
                vectorF.setStart(new PointF(this.touchX, this.touchY));
                vectorF.setEnd(new PointF(this.startX, this.startY));
                vectorF.calculateAngle();
                vectorF.length = vectorF.calculateLength() * this.zoom;
                vectorF.calculateEndPoint();
                this.xDiff = vectorF.end.x - this.startX;
                this.yDiff = vectorF.end.y - this.startY;
            } else {
                this.xDiff = gestureImageView.getCenterX() - this.startX;
                this.yDiff = gestureImageView.getCenterY() - this.startY;
            }
        }
        long j2 = this.totalTime + j;
        this.totalTime = j2;
        long animationLengthMS = 200;
        float f2 = ((float) j2) / ((float) animationLengthMS);
        if (f2 >= 1.0f) {
            float f3 = this.scaleDiff + this.startScale;
            float f4 = this.xDiff + this.startX;
            float f5 = this.yDiff + this.startY;
            ZoomAnimationListener zoomAnimationListener2 = this.zoomAnimationListener;
            if (zoomAnimationListener2 != null) {
                zoomAnimationListener2.onZoom(f3, f4, f5);
                this.zoomAnimationListener.onComplete();
            }
            return false;
        } else if (f2 <= 0.0f) {
            return true;
        } else {
            float f6 = (this.scaleDiff * f2) + this.startScale;
            float f7 = (this.xDiff * f2) + this.startX;
            float f8 = (f2 * this.yDiff) + this.startY;
            ZoomAnimationListener zoomAnimationListener3 = this.zoomAnimationListener;
            if (zoomAnimationListener3 == null) {
                return true;
            }
            zoomAnimationListener3.onZoom(f6, f7, f8);
            return true;
        }
    }

    public void reset() {
        this.firstFrame = true;
        this.totalTime = 0;
    }

    public void setZoom(float f) {
        this.zoom = f;
    }

    public void setTouchX(float f) {
        this.touchX = f;
    }

    public void setTouchY(float f) {
        this.touchY = f;
    }

    public void setZoomAnimationListener(ZoomAnimationListener zoomAnimationListener2) {
        this.zoomAnimationListener = zoomAnimationListener2;
    }
}
