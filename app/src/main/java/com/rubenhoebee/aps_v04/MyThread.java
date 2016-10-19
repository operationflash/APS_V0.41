package com.rubenhoebee.aps_v04;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.SurfaceHolder;


class MyThread extends Thread {
    private final SurfaceHolder msurfaceHolder;
    private final BallView mballView;
    private boolean mrun =false;

    public MyThread(SurfaceHolder holder, BallView ballView) {
        msurfaceHolder = holder;
        mballView=ballView;
    }

    public void startrun(boolean run) {
        mrun=run;
    }
    @SuppressLint("WrongCall")
    @Override
    public void run() {
        super.run();
        Canvas canvas;
        while (mrun) {
            canvas=null;
            try {
                canvas = msurfaceHolder.lockCanvas(null);
                synchronized (msurfaceHolder) {
                    mballView.onDraw(canvas);
                }
            } finally {
                if (canvas != null) {
                    msurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}