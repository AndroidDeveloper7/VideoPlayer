package com.telegram.videoplayer.downloader.help;

import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class GestureImageViewTouchListener implements View.OnTouchListener {
    public final PointF current = new PointF();
    private final PointF last = new PointF();
    private final PointF midpoint;
    private final PointF next;
    private final VectorF pinchVector;
    private final VectorF scaleVector;
    private final int displayHeight;
    private final int displayWidth;
    private final FlingAnimation flingAnimation;
    private final GestureDetector flingDetector;
    private final FlingListener flingListener;
    private final GestureImageView image;
    private final int imageHeight;
    private final GestureImageViewListener imageListener;
    private final int imageWidth;
    private final MoveAnimation moveAnimation;
    private final GestureDetector tapDetector;
    private final ZoomAnimation zoomAnimation;
    public boolean inZoom;
    public float maxScale;
    public float minScale;
    public View.OnClickListener onClickListener;
    private float boundaryBottom;
    private float boundaryLeft;
    private float boundaryRight;
    private float boundaryTop;
    private boolean canDragX;
    private boolean canDragY;
    private int canvasHeight;
    private int canvasWidth;
    private float centerX;
    private float centerY;
    private float currentScale;
    private float fitScaleHorizontal;
    private float fitScaleVertical;
    private float initialDistance;
    private float lastScale;
    private boolean multiTouch;
    private boolean touched;

    public GestureImageViewTouchListener(final GestureImageView gestureImageView, int i, int i2) {
        PointF pointF = new PointF();
        this.next = pointF;
        this.midpoint = new PointF();
        this.scaleVector = new VectorF();
        this.pinchVector = new VectorF();
        this.touched = false;
        this.inZoom = false;
        this.lastScale = 1.0f;
        this.currentScale = 1.0f;
        this.boundaryLeft = 0.0f;
        this.boundaryTop = 0.0f;
        this.boundaryRight = 0.0f;
        this.boundaryBottom = 0.0f;
        this.maxScale = 5.0f;
        this.minScale = 0.25f;
        this.fitScaleHorizontal = 1.0f;
        this.fitScaleVertical = 1.0f;
        this.canvasWidth = 0;
        this.canvasHeight = 0;
        this.centerX = 0.0f;
        this.centerY = 0.0f;
        this.canDragX = false;
        this.canDragY = false;
        this.multiTouch = false;
        this.image = gestureImageView;
        this.displayWidth = i;
        this.displayHeight = i2;
        float f = (float) i;
        this.centerX = f / 2.0f;
        float f2 = (float) i2;
        this.centerY = f2 / 2.0f;
        this.imageWidth = gestureImageView.getImageWidth();
        this.imageHeight = gestureImageView.getImageHeight();
        float scale = gestureImageView.getScale();
        this.currentScale = scale;
        this.lastScale = scale;
        this.boundaryRight = f;
        this.boundaryBottom = f2;
        this.boundaryLeft = 0.0f;
        this.boundaryTop = 0.0f;
        pointF.x = gestureImageView.getImageX();
        pointF.y = gestureImageView.getImageY();
        this.flingListener = new FlingListener();
        this.flingAnimation = new FlingAnimation();
        this.zoomAnimation = new ZoomAnimation();
        this.moveAnimation = new MoveAnimation();
        this.flingAnimation.setListener(new FlingAnimationListener() {

            @Override
            public void onComplete() {
            }

            @Override
            public void onMove(float f, float f2) {
                GestureImageViewTouchListener gestureImageViewTouchListener = GestureImageViewTouchListener.this;
                gestureImageViewTouchListener.handleDrag(gestureImageViewTouchListener.current.x + f, GestureImageViewTouchListener.this.current.y + f2);
            }
        });
        this.zoomAnimation.setZoom(2.0f);
        this.zoomAnimation.setZoomAnimationListener(new ZoomAnimationListener() {

            @Override
            public void onZoom(float f, float f2, float f3) {
                if (f <= GestureImageViewTouchListener.this.maxScale && f >= GestureImageViewTouchListener.this.minScale) {
                    GestureImageViewTouchListener.this.handleScale(f, f2, f3);
                }
            }

            @Override
            public void onComplete() {
                GestureImageViewTouchListener.this.inZoom = false;
                GestureImageViewTouchListener.this.handleUp();
            }
        });
        this.moveAnimation.setMoveAnimationListener((f1, f21) -> {
            gestureImageView.setPosition(f1, f21);
            gestureImageView.redraw();
        });
        this.tapDetector = new GestureDetector(gestureImageView.getContext(), new GestureDetector.SimpleOnGestureListener() {

            public boolean onDoubleTap(MotionEvent motionEvent) {
                GestureImageViewTouchListener.this.startZoom(motionEvent);
                return true;
            }

            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                if (GestureImageViewTouchListener.this.inZoom || GestureImageViewTouchListener.this.onClickListener == null) {
                    return false;
                }
                GestureImageViewTouchListener.this.onClickListener.onClick(gestureImageView);
                return true;
            }
        });
        this.flingDetector = new GestureDetector(gestureImageView.getContext(), this.flingListener);
        this.imageListener = gestureImageView.getGestureImageViewListener();
        calculateBoundaries();
    }

    private void startFling() {
        this.flingAnimation.setVelocityX(this.flingListener.getVelocityX());
        this.flingAnimation.setVelocityY(this.flingListener.getVelocityY());
        this.image.animationStart(this.flingAnimation);
    }

    public void startZoom(MotionEvent motionEvent) {
        float f;
        this.inZoom = true;
        this.zoomAnimation.reset();
        float f2 = 0.0f;
        if (this.image.isLandscape()) {
            if (this.image.getDeviceOrientation() != 1) {
                int scaledWidth = this.image.getScaledWidth();
                int i = this.canvasWidth;
                if (scaledWidth == i) {
                    f = this.currentScale * 4.0f;
                    this.zoomAnimation.setTouchX(motionEvent.getX());
                    this.zoomAnimation.setTouchY(motionEvent.getY());
                } else if (scaledWidth < i) {
                    f = this.fitScaleHorizontal / this.currentScale;
                    this.zoomAnimation.setTouchX(this.image.getCenterX());
                    this.zoomAnimation.setTouchY(motionEvent.getY());
                } else {
                    this.zoomAnimation.setTouchX(this.image.getCenterX());
                    this.zoomAnimation.setTouchY(this.image.getCenterY());
                    this.zoomAnimation.setZoom(0.0f);
                    this.image.animationStart(this.zoomAnimation);
                    f = 0.0f;
                }
            } else if (this.image.getScaledHeight() < this.canvasHeight) {
                f = this.fitScaleVertical / this.currentScale;
                this.zoomAnimation.setTouchX(motionEvent.getX());
                this.zoomAnimation.setTouchY(this.image.getCenterY());
            } else {
                f = this.fitScaleHorizontal / this.currentScale;
                this.zoomAnimation.setTouchX(this.image.getCenterX());
                this.zoomAnimation.setTouchY(this.image.getCenterY());
            }
            this.zoomAnimation.setZoom(f);
            this.image.animationStart(this.zoomAnimation);
        } else {
            f = 0.0f;
        }
        if (this.image.getDeviceOrientation() == 1) {
            int scaledHeight = this.image.getScaledHeight();
            int i2 = this.canvasHeight;
            if (scaledHeight == i2) {
                f = this.currentScale * 4.0f;
                this.zoomAnimation.setTouchX(motionEvent.getX());
                this.zoomAnimation.setTouchY(motionEvent.getY());
            } else if (scaledHeight < i2) {
                f = this.fitScaleVertical / this.currentScale;
                this.zoomAnimation.setTouchX(motionEvent.getX());
                this.zoomAnimation.setTouchY(this.image.getCenterY());
            } else {
                f2 = this.fitScaleVertical / this.currentScale;
                this.zoomAnimation.setTouchX(this.image.getCenterX());
                this.zoomAnimation.setTouchY(this.image.getCenterY());
            }
        } else if (this.image.getScaledWidth() < this.canvasWidth) {
            f = this.fitScaleHorizontal / this.currentScale;
            this.zoomAnimation.setTouchX(this.image.getCenterX());
            this.zoomAnimation.setTouchY(motionEvent.getY());
        } else {
            f = this.fitScaleVertical / this.currentScale;
            this.zoomAnimation.setTouchX(this.image.getCenterX());
            this.zoomAnimation.setTouchY(this.image.getCenterY());
        }
        this.zoomAnimation.setZoom(f);
        this.image.animationStart(this.zoomAnimation);
        this.zoomAnimation.setZoom(f2);
        this.image.animationStart(this.zoomAnimation);
    }

    private void stopAnimations() {
        this.image.animationStop();
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!this.inZoom && !this.tapDetector.onTouchEvent(motionEvent)) {
            if (motionEvent.getPointerCount() == 1 && this.flingDetector.onTouchEvent(motionEvent)) {
                startFling();
            }
            if (motionEvent.getAction() == 1) {
                handleUp();
            } else if (motionEvent.getAction() == 0) {
                stopAnimations();
                this.last.x = motionEvent.getX();
                this.last.y = motionEvent.getY();
                GestureImageViewListener gestureImageViewListener = this.imageListener;
                if (gestureImageViewListener != null) {
                    gestureImageViewListener.onTouch();
                }
                this.touched = true;
            } else if (motionEvent.getAction() == 2) {
                if (motionEvent.getPointerCount() > 1) {
                    this.multiTouch = true;
                    if (this.initialDistance > 0.0f) {
                        this.pinchVector.set(motionEvent);
                        this.pinchVector.calculateLength();
                        float f = this.pinchVector.length;
                        float f2 = this.initialDistance;
                        if (f2 != f) {
                            float f3 = (f / f2) * this.lastScale;
                            if (f3 <= this.maxScale) {
                                this.scaleVector.length *= f3;
                                this.scaleVector.calculateEndPoint();
                                this.scaleVector.length /= f3;
                                handleScale(f3, this.scaleVector.end.x, this.scaleVector.end.y);
                            }
                        }
                    } else {
                        this.initialDistance = MathUtils.distance(motionEvent);
                        MathUtils.midpoint(motionEvent, this.midpoint);
                        this.scaleVector.setStart(this.midpoint);
                        this.scaleVector.setEnd(this.next);
                        this.scaleVector.calculateLength();
                        this.scaleVector.calculateAngle();
                        this.scaleVector.length /= this.lastScale;
                    }
                } else if (!this.touched) {
                    this.touched = true;
                    this.last.x = motionEvent.getX();
                    this.last.y = motionEvent.getY();
                    this.next.x = this.image.getImageX();
                    this.next.y = this.image.getImageY();
                } else if (!this.multiTouch && handleDrag(motionEvent.getX(), motionEvent.getY())) {
                    this.image.redraw();
                }
            }
        }
        return true;
    }

    public void handleUp() {
        this.multiTouch = false;
        this.initialDistance = 0.0f;
        this.lastScale = this.currentScale;
        if (!this.canDragX) {
            this.next.x = this.centerX;
        }
        if (!this.canDragY) {
            this.next.y = this.centerY;
        }
        boundCoordinates();
        if (!this.canDragX && !this.canDragY) {
            if (this.image.isLandscape()) {
                float f = this.fitScaleHorizontal;
                this.currentScale = f;
                this.lastScale = f;
            } else {
                float f2 = this.fitScaleVertical;
                this.currentScale = f2;
                this.lastScale = f2;
            }
        }
        this.image.setScale(this.currentScale);
        this.image.setPosition(this.next.x, this.next.y);
        GestureImageViewListener gestureImageViewListener = this.imageListener;
        if (gestureImageViewListener != null) {
            gestureImageViewListener.onScale();
            this.imageListener.onPosition(this.next.x);
        }
        this.image.redraw();
    }

    public void handleScale(float f, float f2, float f3) {
        this.currentScale = f;
        float f4 = this.maxScale;
        if (f > f4) {
            this.currentScale = f4;
        } else {
            float f5 = this.minScale;
            if (f < f5) {
                this.currentScale = f5;
            } else {
                this.next.x = f2;
                this.next.y = f3;
            }
        }
        calculateBoundaries();
        this.image.setScale(this.currentScale);
        this.image.setPosition(this.next.x, this.next.y);
        GestureImageViewListener gestureImageViewListener = this.imageListener;
        if (gestureImageViewListener != null) {
            gestureImageViewListener.onScale();
            this.imageListener.onPosition(this.next.x);
        }
        this.image.redraw();
    }

    public boolean handleDrag(float f, float f2) {
        this.current.x = f;
        this.current.y = f2;
        float f3 = this.current.x - this.last.x;
        float f4 = this.current.y - this.last.y;
        if (f3 == 0.0f && f4 == 0.0f) {
            return false;
        }
        if (this.canDragX) {
            this.next.x += f3;
        }
        if (this.canDragY) {
            this.next.y += f4;
        }
        boundCoordinates();
        this.last.x = this.current.x;
        this.last.y = this.current.y;
        if (!this.canDragX && !this.canDragY) {
            return false;
        }
        this.image.setPosition(this.next.x, this.next.y);
        GestureImageViewListener gestureImageViewListener = this.imageListener;
        if (gestureImageViewListener == null) {
            return true;
        }
        gestureImageViewListener.onPosition(this.next.x);
        return true;
    }


    public void setMaxScale(float f) {
        this.maxScale = f;
    }

    public void setMinScale(float f) {
        this.minScale = f;
    }

    public void setOnClickListener(View.OnClickListener onClickListener2) {
        this.onClickListener = onClickListener2;
    }

    public void setCanvasWidth(int i) {
        this.canvasWidth = i;
    }

    public void setCanvasHeight(int i) {
        this.canvasHeight = i;
    }

    public void setFitScaleHorizontal(float f) {
        this.fitScaleHorizontal = f;
    }

    public void setFitScaleVertical(float f) {
        this.fitScaleVertical = f;
    }

    public void boundCoordinates() {
        float f = this.next.x;
        float f2 = this.boundaryLeft;
        if (f < f2) {
            this.next.x = f2;
        } else {
            float f3 = this.next.x;
            float f4 = this.boundaryRight;
            if (f3 > f4) {
                this.next.x = f4;
            }
        }
        float f5 = this.next.y;
        float f6 = this.boundaryTop;
        if (f5 < f6) {
            this.next.y = f6;
            return;
        }
        float f7 = this.next.y;
        float f8 = this.boundaryBottom;
        if (f7 > f8) {
            this.next.y = f8;
        }
    }

    public void calculateBoundaries() {
        int round = Math.round(((float) this.imageWidth) * this.currentScale);
        int round2 = Math.round(((float) this.imageHeight) * this.currentScale);
        int i = this.displayWidth;
        boolean z = false;
        boolean z2 = round > i;
        this.canDragX = z2;
        int i2 = this.displayHeight;
        if (round2 > i2) {
            z = true;
        }
        this.canDragY = z;
        if (z2) {
            float f = ((float) (round - i)) / 2.0f;
            float f2 = this.centerX;
            this.boundaryLeft = f2 - f;
            this.boundaryRight = f2 + f;
        }
        if (z) {
            float f3 = ((float) (round2 - i2)) / 2.0f;
            float f4 = this.centerY;
            this.boundaryTop = f4 - f3;
            this.boundaryBottom = f4 + f3;
        }
    }
}
