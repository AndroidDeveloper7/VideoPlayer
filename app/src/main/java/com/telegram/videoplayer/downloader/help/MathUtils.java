package com.telegram.videoplayer.downloader.help;

import android.graphics.PointF;
import android.view.MotionEvent;

public class MathUtils {
    public static float distance(MotionEvent motionEvent) {
        float x = motionEvent.getX(0) - motionEvent.getX(1);
        float y = motionEvent.getY(0) - motionEvent.getY(1);
        return (float) Math.sqrt((x * x) + (y * y));
    }

    public static float distance(PointF pointF, PointF pointF2) {
        float f = pointF.x - pointF2.x;
        float f2 = pointF.y - pointF2.y;
        return (float) Math.sqrt((f * f) + (f2 * f2));
    }


    public static void midpoint(MotionEvent motionEvent, PointF pointF) {
        midpoint(motionEvent.getX(0), motionEvent.getY(0), motionEvent.getX(1), motionEvent.getY(1), pointF);
    }

    public static void midpoint(float f, float f2, float f3, float f4, PointF pointF) {
        pointF.x = (f + f3) / 2.0f;
        pointF.y = (f2 + f4) / 2.0f;
    }

    public static float angle(PointF pointF, PointF pointF2) {
        return angle(pointF.x, pointF.y, pointF2.x, pointF2.y);
    }

    public static float angle(float f, float f2, float f3, float f4) {
        return (float) Math.atan2(f4 - f2, f3 - f);
    }

}
