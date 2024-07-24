package com.telegram.videoplayer.downloader.help;

public class MoveAnimation implements Animation {
    private boolean firstFrame = true;
    private MoveAnimationListener moveAnimationListener;
    private float startX;
    private float startY;
    private float targetX;
    private float targetY;
    private long totalTime = 0;

    @Override
    public boolean update(GestureImageView gestureImageView, long j) {
        this.totalTime += j;
        if (this.firstFrame) {
            this.firstFrame = false;
            this.startX = gestureImageView.getImageX();
            this.startY = gestureImageView.getImageY();
        }
        long j2 = this.totalTime;
        if (j2 < (long) 100) {
            float f = ((float) j2) / ((float) (long) 100);
            float f2 = this.targetX;
            float f3 = this.startX;
            float f4 = ((f2 - f3) * f) + f3;
            float f5 = this.targetY;
            float f6 = this.startY;
            float f7 = ((f5 - f6) * f) + f6;
            MoveAnimationListener moveAnimationListener2 = this.moveAnimationListener;
            if (moveAnimationListener2 == null) {
                return true;
            }
            moveAnimationListener2.onMove(f4, f7);
            return true;
        }
        MoveAnimationListener moveAnimationListener3 = this.moveAnimationListener;
        if (moveAnimationListener3 != null) {
            moveAnimationListener3.onMove(this.targetX, this.targetY);
        }
        return false;
    }

    public void setMoveAnimationListener(MoveAnimationListener moveAnimationListener2) {
        this.moveAnimationListener = moveAnimationListener2;
    }
}
