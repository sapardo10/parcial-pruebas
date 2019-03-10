package android.support.wearable.internal.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

@TargetApi(20)
@Deprecated
public class SwipeDismissLayout extends FrameLayout {
    public static final float DEFAULT_DISMISS_DRAG_WIDTH_RATIO = 0.33f;
    private static final float EDGE_SWIPE_THRESHOLD = 0.1f;
    private static final String TAG = "SwipeDismissLayout";
    private int mActiveTouchId;
    private boolean mCanStartSwipe = true;
    private boolean mDiscardIntercept;
    private float mDismissMinDragWidthRatio = 0.33f;
    private boolean mDismissed;
    private OnDismissedListener mDismissedListener;
    private float mDownX;
    private float mDownY;
    private float mGestureThresholdPx;
    private float mLastX;
    private int mMinFlingVelocity;
    @Nullable
    private OnPreSwipeListener mOnPreSwipeListener;
    private OnSwipeProgressChangedListener mProgressListener;
    private int mSlop;
    private boolean mSwipeable;
    private boolean mSwiping;
    private float mTranslationX;
    private VelocityTracker mVelocityTracker;

    public interface OnDismissedListener {
        void onDismissed(SwipeDismissLayout swipeDismissLayout);
    }

    public interface OnPreSwipeListener {
        @UiThread
        boolean onPreSwipe(float f, float f2);
    }

    public interface OnSwipeProgressChangedListener {
        void onSwipeCancelled(SwipeDismissLayout swipeDismissLayout);

        void onSwipeProgressChanged(SwipeDismissLayout swipeDismissLayout, float f, float f2);
    }

    public SwipeDismissLayout(Context context) {
        super(context);
        init(context);
    }

    public SwipeDismissLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SwipeDismissLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setDismissMinDragWidthRatio(float ratio) {
        this.mDismissMinDragWidthRatio = ratio;
    }

    public void setSwipeable(boolean swipeable) {
        this.mSwipeable = swipeable;
    }

    public boolean isSwipeable() {
        return this.mSwipeable;
    }

    private void init(Context context) {
        ViewConfiguration vc = ViewConfiguration.get(context);
        this.mSlop = vc.getScaledTouchSlop();
        this.mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        this.mGestureThresholdPx = ((float) Resources.getSystem().getDisplayMetrics().widthPixels) * 0.1f;
        setSwipeable(true);
    }

    @UiThread
    public void setOnPreSwipeListener(@Nullable OnPreSwipeListener listener) {
        this.mOnPreSwipeListener = listener;
    }

    public void setOnDismissedListener(OnDismissedListener listener) {
        this.mDismissedListener = listener;
    }

    public void setOnSwipeProgressChangedListener(OnSwipeProgressChangedListener listener) {
        this.mProgressListener = listener;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!this.mSwipeable) {
            return super.onInterceptTouchEvent(ev);
        }
        ev.offsetLocation(this.mTranslationX, 0.0f);
        boolean z = true;
        int pointerIndex;
        switch (ev.getActionMasked()) {
            case 0:
                resetMembers();
                this.mDownX = ev.getRawX();
                this.mDownY = ev.getRawY();
                this.mActiveTouchId = ev.getPointerId(0);
                this.mVelocityTracker = VelocityTracker.obtain();
                this.mVelocityTracker.addMovement(ev);
                break;
            case 1:
            case 3:
                resetMembers();
                break;
            case 2:
                if (this.mVelocityTracker != null) {
                    if (!this.mDiscardIntercept) {
                        pointerIndex = ev.findPointerIndex(this.mActiveTouchId);
                        if (pointerIndex != -1) {
                            float dx = ev.getRawX() - this.mDownX;
                            float x = ev.getX(pointerIndex);
                            float y = ev.getY(pointerIndex);
                            if (dx != 0.0f && this.mDownX >= this.mGestureThresholdPx && canScroll(this, false, dx, x, y)) {
                                this.mDiscardIntercept = true;
                                break;
                            }
                            updateSwiping(ev);
                            break;
                        }
                        Log.e(TAG, "Invalid pointer index: ignoring.");
                        this.mDiscardIntercept = true;
                        break;
                    }
                    break;
                }
                break;
                break;
            case 5:
                this.mActiveTouchId = ev.getPointerId(ev.getActionIndex());
                break;
            case 6:
                pointerIndex = ev.getActionIndex();
                if (ev.getPointerId(pointerIndex) != this.mActiveTouchId) {
                    break;
                }
                this.mActiveTouchId = ev.getPointerId(pointerIndex == 0 ? 1 : 0);
                break;
            default:
                break;
        }
        OnPreSwipeListener onPreSwipeListener = this.mOnPreSwipeListener;
        if (onPreSwipeListener != null) {
            if (!onPreSwipeListener.onPreSwipe(this.mDownX, this.mDownY)) {
                return false;
            }
        }
        if (this.mDiscardIntercept || !this.mSwiping) {
            z = false;
        }
        return z;
    }

    public boolean canScrollHorizontally(int direction) {
        return direction < 0 && isSwipeable() && getVisibility() == 0;
    }

    private boolean isPotentialSwipe(float dx, float dy) {
        float f = (dx * dx) + (dy * dy);
        int i = this.mSlop;
        return f > ((float) (i * i));
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (!this.mSwipeable) {
            return super.onTouchEvent(ev);
        }
        if (this.mVelocityTracker == null) {
            return super.onTouchEvent(ev);
        }
        OnPreSwipeListener onPreSwipeListener = this.mOnPreSwipeListener;
        if (onPreSwipeListener != null && !onPreSwipeListener.onPreSwipe(this.mDownX, this.mDownY)) {
            return super.onTouchEvent(ev);
        }
        ev.offsetLocation(this.mTranslationX, 0.0f);
        switch (ev.getActionMasked()) {
            case 1:
                updateDismiss(ev);
                if (this.mDismissed) {
                    dismiss();
                } else if (this.mSwiping) {
                    cancel();
                }
                resetMembers();
                break;
            case 2:
                this.mVelocityTracker.addMovement(ev);
                this.mLastX = ev.getRawX();
                updateSwiping(ev);
                if (!this.mSwiping) {
                    break;
                }
                setProgress(ev.getRawX() - this.mDownX);
                break;
            case 3:
                cancel();
                resetMembers();
                break;
            default:
                break;
        }
        return true;
    }

    private void setProgress(float deltaX) {
        this.mTranslationX = deltaX;
        OnSwipeProgressChangedListener onSwipeProgressChangedListener = this.mProgressListener;
        if (onSwipeProgressChangedListener != null && deltaX >= 0.0f) {
            onSwipeProgressChangedListener.onSwipeProgressChanged(this, deltaX / ((float) getWidth()), deltaX);
        }
    }

    private void dismiss() {
        OnDismissedListener onDismissedListener = this.mDismissedListener;
        if (onDismissedListener != null) {
            onDismissedListener.onDismissed(this);
        }
    }

    protected void cancel() {
        OnSwipeProgressChangedListener onSwipeProgressChangedListener = this.mProgressListener;
        if (onSwipeProgressChangedListener != null) {
            onSwipeProgressChangedListener.onSwipeCancelled(this);
        }
    }

    private void resetMembers() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
        }
        this.mVelocityTracker = null;
        this.mTranslationX = 0.0f;
        this.mDownX = 0.0f;
        this.mDownY = 0.0f;
        this.mSwiping = false;
        this.mDismissed = false;
        this.mDiscardIntercept = false;
        this.mCanStartSwipe = true;
    }

    private void updateSwiping(MotionEvent ev) {
        if (!this.mSwiping) {
            float deltaX = ev.getRawX() - this.mDownX;
            float deltaY = ev.getRawY() - this.mDownY;
            if (isPotentialSwipe(deltaX, deltaY)) {
                boolean z = this.mCanStartSwipe && Math.abs(deltaY) < Math.abs(deltaX) && deltaX > 0.0f;
                this.mSwiping = z;
                this.mCanStartSwipe = this.mSwiping;
            }
        }
    }

    private void updateDismiss(MotionEvent ev) {
        float deltaX = ev.getRawX() - this.mDownX;
        this.mVelocityTracker.addMovement(ev);
        this.mVelocityTracker.computeCurrentVelocity(1000);
        if (!this.mDismissed) {
            if (deltaX > ((float) getWidth()) * this.mDismissMinDragWidthRatio) {
                if (ev.getRawX() >= this.mLastX) {
                    this.mDismissed = true;
                }
            }
            if (this.mVelocityTracker.getXVelocity() < ((float) this.mMinFlingVelocity)) {
            }
            this.mDismissed = true;
        }
        if (!this.mDismissed || !this.mSwiping) {
            return;
        }
        if (this.mVelocityTracker.getXVelocity() < ((float) (-this.mMinFlingVelocity))) {
            this.mDismissed = false;
        }
    }

    protected boolean canScroll(View v, boolean checkV, float dx, float x, float y) {
        View view = v;
        boolean z = true;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int scrollX = v.getScrollX();
            int scrollY = v.getScrollY();
            for (int i = group.getChildCount() - 1; i >= 0; i--) {
                View child = group.getChildAt(i);
                if (x + ((float) scrollX) >= ((float) child.getLeft())) {
                    if (x + ((float) scrollX) < ((float) child.getRight())) {
                        if (y + ((float) scrollY) >= ((float) child.getTop())) {
                            if (y + ((float) scrollY) < ((float) child.getBottom())) {
                                if (canScroll(child, true, dx, (x + ((float) scrollX)) - ((float) child.getLeft()), (y + ((float) scrollY)) - ((float) child.getTop()))) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!checkV) {
            float f = dx;
        } else if (v.canScrollHorizontally((int) (-dx))) {
            return z;
        }
        z = false;
        return z;
    }
}
