package com.google.android.exoplayer2.ui.spherical;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

class TouchTracker extends SimpleOnGestureListener implements OnTouchListener {
    static final float MAX_PITCH_DEGREES = 45.0f;
    private final PointF accumulatedTouchOffsetDegrees = new PointF();
    private final GestureDetector gestureDetector;
    private final Listener listener;
    private final PointF previousTouchPointPx = new PointF();
    private final float pxPerDegrees;
    private volatile float roll;
    @Nullable
    private SingleTapListener singleTapListener;

    interface Listener {
        void onScrollChange(PointF pointF);
    }

    public TouchTracker(Context context, Listener listener, float pxPerDegrees) {
        this.listener = listener;
        this.pxPerDegrees = pxPerDegrees;
        this.gestureDetector = new GestureDetector(context, this);
        this.roll = 3.1415927f;
    }

    public void setSingleTapListener(@Nullable SingleTapListener listener) {
        this.singleTapListener = listener;
    }

    public boolean onTouch(View v, MotionEvent event) {
        return this.gestureDetector.onTouchEvent(event);
    }

    public boolean onDown(MotionEvent e) {
        this.previousTouchPointPx.set(e.getX(), e.getY());
        return true;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float touchX = (e2.getX() - this.previousTouchPointPx.x) / this.pxPerDegrees;
        float touchY = (e2.getY() - this.previousTouchPointPx.y) / this.pxPerDegrees;
        this.previousTouchPointPx.set(e2.getX(), e2.getY());
        float r = this.roll;
        float cr = (float) Math.cos((double) r);
        float sr = (float) Math.sin((double) r);
        PointF pointF = this.accumulatedTouchOffsetDegrees;
        pointF.x -= (cr * touchX) - (sr * touchY);
        pointF = this.accumulatedTouchOffsetDegrees;
        pointF.y += (sr * touchX) + (cr * touchY);
        pointF = this.accumulatedTouchOffsetDegrees;
        pointF.y = Math.max(-45.0f, Math.min(MAX_PITCH_DEGREES, pointF.y));
        this.listener.onScrollChange(this.accumulatedTouchOffsetDegrees);
        return true;
    }

    public boolean onSingleTapUp(MotionEvent e) {
        SingleTapListener singleTapListener = this.singleTapListener;
        if (singleTapListener != null) {
            return singleTapListener.onSingleTapUp(e);
        }
        return false;
    }

    @BinderThread
    public void setRoll(float roll) {
        this.roll = -roll;
    }
}
