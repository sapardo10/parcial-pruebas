package de.danoeh.antennapod.view;

import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class SwipeGestureDetector extends SimpleOnGestureListener {
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private static final String TAG = "SwipeGestureDetector";
    private final OnSwipeGesture callback;

    public SwipeGestureDetector(OnSwipeGesture callback) {
        this.callback = callback;
    }

    public boolean onDown(MotionEvent e) {
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > 250.0f) {
                return false;
            }
            if (e1.getX() - e2.getX() > 120.0f) {
                if (Math.abs(velocityX) > 200.0f) {
                    return this.callback.onSwipeRightToLeft();
                }
            }
            if (e2.getX() - e1.getX() > 120.0f) {
                if (Math.abs(velocityX) > 200.0f) {
                    return this.callback.onSwipeLeftToRight();
                }
            }
            return false;
        } catch (Exception e) {
            Log.d(TAG, Log.getStackTraceString(e));
        }
    }
}
