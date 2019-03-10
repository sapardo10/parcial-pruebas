package android.support.wearable.view;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.VelocityTracker;

@TargetApi(23)
class ScrollManager {
    private static final float FLING_EDGE_RATIO = 1.5f;
    private static final int ONE_SEC_IN_MS = 1000;
    private static final float VELOCITY_MULTIPLIER = 1.5f;
    private boolean mDown;
    private float mLastAngleRadians;
    private float mMinRadiusFraction = 0.0f;
    private float mMinRadiusFractionSquared;
    private RecyclerView mRecyclerView;
    private float mScreenRadiusPx;
    private float mScreenRadiusPxSquared;
    private float mScrollDegreesPerScreen;
    private float mScrollPixelsPerRadian;
    private float mScrollRadiansPerScreen;
    private boolean mScrolling;
    VelocityTracker mVelocityTracker;

    ScrollManager() {
        float f = this.mMinRadiusFraction;
        this.mMinRadiusFractionSquared = f * f;
        this.mScrollDegreesPerScreen = 180.0f;
        this.mScrollRadiansPerScreen = (float) Math.toRadians((double) this.mScrollDegreesPerScreen);
    }

    void setRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        Point displaySize = new Point();
        this.mRecyclerView.getDisplay().getSize(displaySize);
        this.mScreenRadiusPx = ((float) Math.max(displaySize.x, displaySize.y)) / 2.0f;
        float f = this.mScreenRadiusPx;
        this.mScreenRadiusPxSquared = f * f;
        this.mScrollPixelsPerRadian = ((float) displaySize.y) / this.mScrollRadiansPerScreen;
        this.mVelocityTracker = VelocityTracker.obtain();
    }

    void clearRecyclerView() {
        this.mRecyclerView = null;
    }

    boolean onTouchEvent(MotionEvent event) {
        float deltaX = event.getRawX() - this.mScreenRadiusPx;
        float deltaY = event.getRawY() - this.mScreenRadiusPx;
        float radiusSquared = (deltaX * deltaX) + (deltaY * deltaY);
        MotionEvent vtev = MotionEvent.obtain(event);
        this.mVelocityTracker.addMovement(vtev);
        vtev.recycle();
        switch (event.getActionMasked()) {
            case 0:
                if (radiusSquared / this.mScreenRadiusPxSquared <= this.mMinRadiusFractionSquared) {
                    break;
                }
                this.mDown = true;
                return true;
            case 1:
                this.mDown = false;
                this.mScrolling = false;
                this.mVelocityTracker.computeCurrentVelocity(1000, (float) this.mRecyclerView.getMaxFlingVelocity());
                int velocityY = (int) this.mVelocityTracker.getYVelocity();
                if (event.getX() < this.mScreenRadiusPx * 1.5f) {
                    velocityY = -velocityY;
                }
                this.mVelocityTracker.clear();
                if (Math.abs(velocityY) <= this.mRecyclerView.getMinFlingVelocity()) {
                    break;
                }
                return this.mRecyclerView.fling(0, (int) (((float) velocityY) * 1.5f));
            case 2:
                if (!this.mScrolling) {
                    if (!this.mDown) {
                        if (radiusSquared / this.mScreenRadiusPxSquared <= this.mMinRadiusFractionSquared) {
                            break;
                        }
                        this.mDown = true;
                        return true;
                    }
                    float deltaXFromCenter = event.getRawX() - this.mScreenRadiusPx;
                    float deltaYFromCenter = event.getRawY() - this.mScreenRadiusPx;
                    float distFromCenter = (float) Math.hypot((double) deltaXFromCenter, (double) deltaYFromCenter);
                    deltaXFromCenter /= distFromCenter;
                    deltaYFromCenter /= distFromCenter;
                    this.mScrolling = true;
                    this.mRecyclerView.invalidate();
                    this.mLastAngleRadians = (float) Math.atan2((double) deltaYFromCenter, (double) deltaXFromCenter);
                    return true;
                }
                int scrollPixels = Math.round(this.mScrollPixelsPerRadian * normalizeAngleRadians(((float) Math.atan2((double) deltaY, (double) deltaX)) - this.mLastAngleRadians));
                if (scrollPixels != 0) {
                    this.mRecyclerView.scrollBy(0, scrollPixels);
                    this.mLastAngleRadians += ((float) scrollPixels) / this.mScrollPixelsPerRadian;
                    this.mLastAngleRadians = normalizeAngleRadians(this.mLastAngleRadians);
                }
                return true;
            case 3:
                if (!this.mDown) {
                    break;
                }
                this.mDown = false;
                this.mScrolling = false;
                this.mRecyclerView.invalidate();
                return true;
            default:
                break;
        }
        return false;
    }

    private static float normalizeAngleRadians(float angleRadians) {
        if (((double) angleRadians) < -3.141592653589793d) {
            double d = (double) angleRadians;
            Double.isNaN(d);
            angleRadians = (float) (d + 6.283185307179586d);
        }
        if (((double) angleRadians) <= 3.141592653589793d) {
            return angleRadians;
        }
        d = (double) angleRadians;
        Double.isNaN(d);
        return (float) (d - 6.283185307179586d);
    }

    public void setScrollDegreesPerScreen(float degreesPerScreen) {
        this.mScrollDegreesPerScreen = degreesPerScreen;
        this.mScrollRadiansPerScreen = (float) Math.toRadians((double) this.mScrollDegreesPerScreen);
    }

    public void setBezelWidth(float fraction) {
        this.mMinRadiusFraction = 1.0f - fraction;
        float f = this.mMinRadiusFraction;
        this.mMinRadiusFractionSquared = f * f;
    }

    public float getScrollDegreesPerScreen() {
        return this.mScrollDegreesPerScreen;
    }

    public float getBezelWidth() {
        return 1.0f - this.mMinRadiusFraction;
    }
}
