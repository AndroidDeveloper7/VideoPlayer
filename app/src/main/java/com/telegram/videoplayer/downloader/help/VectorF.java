package com.telegram.videoplayer.downloader.help;

import android.graphics.PointF;
import android.view.MotionEvent;

@SuppressWarnings("UnusedReturnValue")
public class VectorF {
    public final PointF end = new PointF();
    public final PointF start = new PointF();
    public float angle;
    public float length;

    public void calculateEndPoint() {
        this.end.x = (((float) Math.cos(this.angle)) * this.length) + this.start.x;
        this.end.y = (((float) Math.sin(this.angle)) * this.length) + this.start.y;
    }

    public void setStart(PointF pointF) {
        this.start.x = pointF.x;
        this.start.y = pointF.y;
    }

    public void setEnd(PointF pointF) {
        this.end.x = pointF.x;
        this.end.y = pointF.y;
    }

    public void set(MotionEvent motionEvent) {
        this.start.x = motionEvent.getX(0);
        this.start.y = motionEvent.getY(0);
        this.end.x = motionEvent.getX(1);
        this.end.y = motionEvent.getY(1);
    }

    public float calculateLength() {
        float distance = MathUtils.distance(this.start, this.end);
        this.length = distance;
        return distance;
    }

    public float calculateAngle() {
        float angle2 = MathUtils.angle(this.start, this.end);
        this.angle = angle2;
        return angle2;
    }
}
