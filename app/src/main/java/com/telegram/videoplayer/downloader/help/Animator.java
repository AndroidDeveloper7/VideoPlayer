package com.telegram.videoplayer.downloader.help;

@SuppressWarnings("FieldMayBeFinal")
public class Animator extends Thread {
    private final GestureImageView view;
    private boolean active = false;
    private Animation animation;
    private long lastTime = -1;
    private boolean running = false;

    public Animator(GestureImageView gestureImageView, String str) {
        super(str);
        this.view = gestureImageView;
    }

    public void run() {
        this.running = true;
        while (this.running) {
            while (this.active && this.animation != null) {
                long currentTimeMillis = System.currentTimeMillis();
                this.active = this.animation.update(this.view, currentTimeMillis - this.lastTime);
                this.view.redraw();
                this.lastTime = currentTimeMillis;
                while (this.active) {
                    try {
                        if (this.view.waitForDraw(32)) {
                            break;
                        }
                    } catch (InterruptedException unused) {
                        this.active = false;
                    }
                }
            }
            synchronized (this) {
                if (this.running) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public synchronized void finish() {
        this.running = false;
        this.active = false;
        notifyAll();
    }

    public void play(Animation animation2) {
        if (this.active) {
            cancel();
        }
        this.animation = animation2;
        activate();
    }

    public synchronized void activate() {
        this.lastTime = System.currentTimeMillis();
        this.active = true;
        notifyAll();
    }

    public void cancel() {
        this.active = false;
    }
}
