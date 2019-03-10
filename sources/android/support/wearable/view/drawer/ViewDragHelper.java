package android.support.wearable.view.drawer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.wearable.C0395R;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.OverScroller;
import java.util.Arrays;

@TargetApi(23)
@Deprecated
public class ViewDragHelper {
    private static final int BASE_SETTLE_DURATION = 256;
    public static final int DIRECTION_ALL = 3;
    public static final int DIRECTION_HORIZONTAL = 1;
    public static final int DIRECTION_VERTICAL = 2;
    public static final int EDGE_ALL = 15;
    public static final int EDGE_BOTTOM = 8;
    public static final int EDGE_LEFT = 1;
    public static final int EDGE_RIGHT = 2;
    private static final int EDGE_SIZE = 20;
    public static final int EDGE_TOP = 4;
    public static final int INVALID_POINTER = -1;
    private static final int MAX_SETTLE_DURATION = 600;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_SETTLING = 2;
    private static final String TAG = "ViewDragHelper";
    private static final float WATCH_EDGE_SIZE_RATIO = 0.2f;
    private static final Interpolator sInterpolator = new C04631();
    private int mActivePointerId = -1;
    private final Callback mCallback;
    private View mCapturedView;
    private final float mDisplayDensity;
    private int mDragState;
    private int[] mEdgeDragsInProgress;
    private int[] mEdgeDragsLocked;
    private int mEdgeSize;
    private int[] mInitialEdgesTouched;
    private float[] mInitialMotionX;
    private float[] mInitialMotionY;
    private final boolean mIsUiModeTypeWatch;
    private float[] mLastMotionX;
    private float[] mLastMotionY;
    private final float mMaxVelocity;
    private float mMinVelocity;
    private final ViewGroup mParentView;
    private int mPointersDown;
    private boolean mReleaseInProgress;
    private final OverScroller mScroller;
    private final Runnable mSetIdleRunnable = new C04642();
    private int mTouchSlop;
    private int mTrackingEdges;
    private VelocityTracker mVelocityTracker;
    private final int mWearEdgeSizePx;

    /* renamed from: android.support.wearable.view.drawer.ViewDragHelper$1 */
    class C04631 implements Interpolator {
        C04631() {
        }

        public float getInterpolation(float t) {
            t -= 1.0f;
            return ((((t * t) * t) * t) * t) + 1.0f;
        }
    }

    /* renamed from: android.support.wearable.view.drawer.ViewDragHelper$2 */
    class C04642 implements Runnable {
        C04642() {
        }

        public void run() {
            ViewDragHelper.this.setDragState(0);
        }
    }

    public static abstract class Callback {
        public abstract boolean tryCaptureView(View view, int i);

        public void onViewDragStateChanged(int state) {
        }

        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
        }

        public void onViewCaptured(View capturedChild, int activePointerId) {
        }

        public void onViewReleased(View releasedChild, float xvel, float yvel) {
        }

        public void onEdgeTouched(int edgeFlags, int pointerId) {
        }

        public boolean onEdgeLock(int edgeFlags) {
            return false;
        }

        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
        }

        public int getOrderedChildIndex(int index) {
            return index;
        }

        public int getViewHorizontalDragRange(View child) {
            return 0;
        }

        public int getViewVerticalDragRange(View child) {
            return 0;
        }

        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return 0;
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            return 0;
        }
    }

    public static ViewDragHelper create(ViewGroup forParent, Callback cb) {
        return new ViewDragHelper(forParent.getContext(), forParent, cb);
    }

    public static ViewDragHelper create(ViewGroup forParent, float sensitivity, Callback cb) {
        ViewDragHelper helper = create(forParent, cb);
        helper.mTouchSlop = (int) (((float) helper.mTouchSlop) * (1.0f / sensitivity));
        return helper;
    }

    private ViewDragHelper(Context context, ViewGroup forParent, Callback cb) {
        if (forParent == null) {
            throw new IllegalArgumentException("Parent view may not be null");
        } else if (cb != null) {
            this.mParentView = forParent;
            this.mCallback = cb;
            Resources resources = context.getResources();
            this.mDisplayDensity = resources.getDisplayMetrics().density;
            this.mIsUiModeTypeWatch = (resources.getConfiguration().uiMode & 15) == 6;
            refreshEdgeSize();
            ViewConfiguration vc = ViewConfiguration.get(context);
            this.mTouchSlop = vc.getScaledTouchSlop();
            this.mWearEdgeSizePx = resources.getDimensionPixelSize(C0395R.dimen.drawer_view_edge_size);
            this.mMaxVelocity = (float) vc.getScaledMaximumFlingVelocity();
            this.mMinVelocity = (float) vc.getScaledMinimumFlingVelocity();
            this.mScroller = new OverScroller(context, sInterpolator);
        } else {
            throw new IllegalArgumentException("Callback may not be null");
        }
    }

    public void setMinVelocity(float minVel) {
        this.mMinVelocity = minVel;
    }

    public float getMinVelocity() {
        return this.mMinVelocity;
    }

    public int getViewDragState() {
        return this.mDragState;
    }

    public void setEdgeTrackingEnabled(int edgeFlags) {
        this.mTrackingEdges = edgeFlags;
    }

    public int getEdgeSize() {
        return this.mEdgeSize;
    }

    public void refreshEdgeSize() {
        if (this.mIsUiModeTypeWatch) {
            this.mEdgeSize = Math.min((int) (((float) this.mParentView.getHeight()) * WATCH_EDGE_SIZE_RATIO), this.mWearEdgeSizePx);
        } else {
            this.mEdgeSize = (int) ((this.mDisplayDensity * 20.0f) + 0.5f);
        }
    }

    public void captureChildView(View childView, int activePointerId) {
        ViewParent parent = childView.getParent();
        ViewParent viewParent = this.mParentView;
        if (parent == viewParent) {
            this.mCapturedView = childView;
            this.mActivePointerId = activePointerId;
            this.mCallback.onViewCaptured(childView, activePointerId);
            setDragState(1);
            return;
        }
        String valueOf = String.valueOf(viewParent);
        StringBuilder stringBuilder = new StringBuilder(String.valueOf(valueOf).length() + 95);
        stringBuilder.append("captureChildView: parameter must be a descendant of the ViewDragHelper's tracked parent view (");
        stringBuilder.append(valueOf);
        stringBuilder.append(")");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public View getCapturedView() {
        return this.mCapturedView;
    }

    public int getActivePointerId() {
        return this.mActivePointerId;
    }

    public int getTouchSlop() {
        return this.mTouchSlop;
    }

    public void cancel() {
        this.mActivePointerId = -1;
        clearMotionHistory();
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    public void abort() {
        cancel();
        if (this.mDragState == 2) {
            int oldX = this.mScroller.getCurrX();
            int oldY = this.mScroller.getCurrY();
            this.mScroller.abortAnimation();
            int newX = this.mScroller.getCurrX();
            int newY = this.mScroller.getCurrY();
            this.mCallback.onViewPositionChanged(this.mCapturedView, newX, newY, newX - oldX, newY - oldY);
        }
        setDragState(0);
    }

    public boolean smoothSlideViewTo(View child, int finalLeft, int finalTop) {
        this.mCapturedView = child;
        this.mActivePointerId = -1;
        boolean continueSliding = forceSettleCapturedViewAt(finalLeft, finalTop, 0, 0);
        if (!continueSliding && this.mDragState == 0 && this.mCapturedView != null) {
            this.mCapturedView = null;
        }
        return continueSliding;
    }

    public boolean settleCapturedViewAt(int finalLeft, int finalTop) {
        if (this.mReleaseInProgress) {
            return forceSettleCapturedViewAt(finalLeft, finalTop, (int) VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId), (int) VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId));
        }
        throw new IllegalStateException("Cannot settleCapturedViewAt outside of a call to Callback#onViewReleased");
    }

    private boolean forceSettleCapturedViewAt(int finalLeft, int finalTop, int xvel, int yvel) {
        int startLeft = this.mCapturedView.getLeft();
        int startTop = this.mCapturedView.getTop();
        int dx = finalLeft - startLeft;
        int dy = finalTop - startTop;
        if (dx == 0 && dy == 0) {
            this.mScroller.abortAnimation();
            setDragState(0);
            return false;
        }
        this.mScroller.startScroll(startLeft, startTop, dx, dy, computeSettleDuration(this.mCapturedView, dx, dy, xvel, yvel));
        setDragState(2);
        return true;
    }

    private int computeSettleDuration(View child, int dx, int dy, int xvel, int yvel) {
        float xweight;
        float f;
        float f2;
        View view = child;
        int xvel2 = clampMag(xvel, (int) this.mMinVelocity, (int) this.mMaxVelocity);
        int yvel2 = clampMag(yvel, (int) this.mMinVelocity, (int) this.mMaxVelocity);
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);
        int absXVel = Math.abs(xvel2);
        int absYVel = Math.abs(yvel2);
        int addedVel = absXVel + absYVel;
        int addedDistance = absDx + absDy;
        if (xvel2 != 0) {
            xweight = (float) absXVel;
            f = (float) addedVel;
        } else {
            xweight = (float) absDx;
            f = (float) addedDistance;
        }
        xweight /= f;
        if (yvel2 != 0) {
            f = (float) absYVel;
            f2 = (float) addedVel;
        } else {
            f = (float) absDy;
            f2 = (float) addedDistance;
        }
        f /= f2;
        return (int) ((((float) computeAxisDuration(dx, xvel2, r0.mCallback.getViewHorizontalDragRange(view))) * xweight) + (((float) computeAxisDuration(dy, yvel2, r0.mCallback.getViewVerticalDragRange(view))) * f));
    }

    private int computeAxisDuration(int delta, int velocity, int motionRange) {
        if (delta == 0) {
            return 0;
        }
        int duration;
        int width = this.mParentView.getWidth();
        int halfWidth = width / 2;
        float distance = ((float) halfWidth) + (((float) halfWidth) * distanceInfluenceForSnapDuration(Math.min(1.0f, ((float) Math.abs(delta)) / ((float) width))));
        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration = Math.round(Math.abs(distance / ((float) velocity)) * 1000.0f) * 4;
        } else {
            duration = (int) ((1.0f + (((float) Math.abs(delta)) / ((float) motionRange))) * 1132462080);
        }
        return Math.min(duration, MAX_SETTLE_DURATION);
    }

    private int clampMag(int value, int absMin, int absMax) {
        int absValue = Math.abs(value);
        if (absValue < absMin) {
            return 0;
        }
        if (absValue <= absMax) {
            return value;
        }
        return value > 0 ? absMax : -absMax;
    }

    private float clampMag(float value, float absMin, float absMax) {
        float absValue = Math.abs(value);
        if (absValue < absMin) {
            return 0.0f;
        }
        if (absValue <= absMax) {
            return value;
        }
        return value > 0.0f ? absMax : -absMax;
    }

    private float distanceInfluenceForSnapDuration(float f) {
        double d = (double) (f - 0.5f);
        Double.isNaN(d);
        return (float) Math.sin((double) ((float) (d * 0.4712389167638204d)));
    }

    public void flingCapturedView(int minLeft, int minTop, int maxLeft, int maxTop) {
        if (this.mReleaseInProgress) {
            this.mScroller.fling(this.mCapturedView.getLeft(), this.mCapturedView.getTop(), (int) VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId), (int) VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId), minLeft, maxLeft, minTop, maxTop);
            setDragState(2);
            return;
        }
        throw new IllegalStateException("Cannot flingCapturedView outside of a call to Callback#onViewReleased");
    }

    public boolean continueSettling(boolean deferCallbacks) {
        if (this.mDragState == 2) {
            boolean keepGoing = this.mScroller.computeScrollOffset();
            int x = this.mScroller.getCurrX();
            int y = this.mScroller.getCurrY();
            int dx = x - this.mCapturedView.getLeft();
            int dy = y - this.mCapturedView.getTop();
            if (dx != 0) {
                this.mCapturedView.offsetLeftAndRight(dx);
            }
            if (dy != 0) {
                this.mCapturedView.offsetTopAndBottom(dy);
            }
            if (dx == 0) {
                if (dy == 0) {
                    if (!keepGoing && x == this.mScroller.getFinalX() && y == this.mScroller.getFinalY()) {
                        this.mScroller.abortAnimation();
                        keepGoing = false;
                    }
                    if (!keepGoing) {
                        if (deferCallbacks) {
                            setDragState(0);
                        } else {
                            this.mParentView.post(this.mSetIdleRunnable);
                        }
                    }
                }
            }
            this.mCallback.onViewPositionChanged(this.mCapturedView, x, y, dx, dy);
            if (!keepGoing) {
            }
            if (!keepGoing) {
                if (deferCallbacks) {
                    setDragState(0);
                } else {
                    this.mParentView.post(this.mSetIdleRunnable);
                }
            }
        }
        if (this.mDragState == 2) {
            return true;
        }
        return false;
    }

    private void dispatchViewReleased(float xvel, float yvel) {
        this.mReleaseInProgress = true;
        this.mCallback.onViewReleased(this.mCapturedView, xvel, yvel);
        this.mReleaseInProgress = false;
        if (this.mDragState == 1) {
            setDragState(0);
        }
    }

    private void clearMotionHistory() {
        float[] fArr = this.mInitialMotionX;
        if (fArr != null) {
            Arrays.fill(fArr, 0.0f);
            Arrays.fill(this.mInitialMotionY, 0.0f);
            Arrays.fill(this.mLastMotionX, 0.0f);
            Arrays.fill(this.mLastMotionY, 0.0f);
            Arrays.fill(this.mInitialEdgesTouched, 0);
            Arrays.fill(this.mEdgeDragsInProgress, 0);
            Arrays.fill(this.mEdgeDragsLocked, 0);
            this.mPointersDown = 0;
        }
    }

    private void clearMotionHistory(int pointerId) {
        float[] fArr = this.mInitialMotionX;
        if (fArr != null) {
            fArr[pointerId] = 0.0f;
            this.mInitialMotionY[pointerId] = 0.0f;
            this.mLastMotionX[pointerId] = 0.0f;
            this.mLastMotionY[pointerId] = 0.0f;
            this.mInitialEdgesTouched[pointerId] = 0;
            this.mEdgeDragsInProgress[pointerId] = 0;
            this.mEdgeDragsLocked[pointerId] = 0;
            this.mPointersDown &= (1 << pointerId) ^ -1;
        }
    }

    private void ensureMotionHistorySizeForId(int pointerId) {
        float[] fArr = this.mInitialMotionX;
        if (fArr != null) {
            if (fArr.length > pointerId) {
                return;
            }
        }
        fArr = new float[(pointerId + 1)];
        float[] imy = new float[(pointerId + 1)];
        float[] lmx = new float[(pointerId + 1)];
        float[] lmy = new float[(pointerId + 1)];
        int[] iit = new int[(pointerId + 1)];
        int[] edip = new int[(pointerId + 1)];
        int[] edl = new int[(pointerId + 1)];
        Object obj = this.mInitialMotionX;
        if (obj != null) {
            System.arraycopy(obj, 0, fArr, 0, obj.length);
            obj = this.mInitialMotionY;
            System.arraycopy(obj, 0, imy, 0, obj.length);
            obj = this.mLastMotionX;
            System.arraycopy(obj, 0, lmx, 0, obj.length);
            obj = this.mLastMotionY;
            System.arraycopy(obj, 0, lmy, 0, obj.length);
            obj = this.mInitialEdgesTouched;
            System.arraycopy(obj, 0, iit, 0, obj.length);
            obj = this.mEdgeDragsInProgress;
            System.arraycopy(obj, 0, edip, 0, obj.length);
            obj = this.mEdgeDragsLocked;
            System.arraycopy(obj, 0, edl, 0, obj.length);
        }
        this.mInitialMotionX = fArr;
        this.mInitialMotionY = imy;
        this.mLastMotionX = lmx;
        this.mLastMotionY = lmy;
        this.mInitialEdgesTouched = iit;
        this.mEdgeDragsInProgress = edip;
        this.mEdgeDragsLocked = edl;
    }

    private void saveInitialMotion(float x, float y, int pointerId) {
        ensureMotionHistorySizeForId(pointerId);
        float[] fArr = this.mInitialMotionX;
        this.mLastMotionX[pointerId] = x;
        fArr[pointerId] = x;
        fArr = this.mInitialMotionY;
        this.mLastMotionY[pointerId] = y;
        fArr[pointerId] = y;
        this.mInitialEdgesTouched[pointerId] = getEdgesTouched((int) x, (int) y);
        this.mPointersDown |= 1 << pointerId;
    }

    private void saveLastMotion(MotionEvent ev) {
        int pointerCount = MotionEventCompat.getPointerCount(ev);
        for (int i = 0; i < pointerCount; i++) {
            int pointerId = MotionEventCompat.getPointerId(ev, i);
            float x = MotionEventCompat.getX(ev, i);
            float y = MotionEventCompat.getY(ev, i);
            this.mLastMotionX[pointerId] = x;
            this.mLastMotionY[pointerId] = y;
        }
    }

    public boolean isPointerDown(int pointerId) {
        return (this.mPointersDown & (1 << pointerId)) != 0;
    }

    void setDragState(int state) {
        this.mParentView.removeCallbacks(this.mSetIdleRunnable);
        if (this.mDragState != state) {
            this.mDragState = state;
            this.mCallback.onViewDragStateChanged(state);
            if (this.mDragState == 0) {
                this.mCapturedView = null;
            }
        }
    }

    boolean tryCaptureViewForDrag(View toCapture, int pointerId) {
        if (toCapture == this.mCapturedView && this.mActivePointerId == pointerId) {
            return true;
        }
        if (toCapture == null || !this.mCallback.tryCaptureView(toCapture, pointerId)) {
            return false;
        }
        this.mActivePointerId = pointerId;
        captureChildView(toCapture, pointerId);
        return true;
    }

    protected boolean canScroll(View v, boolean checkV, int dx, int dy, int x, int y) {
        View view = v;
        boolean z = true;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int scrollX = v.getScrollX();
            int scrollY = v.getScrollY();
            for (int i = group.getChildCount() - 1; i >= 0; i--) {
                View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft()) {
                    if (x + scrollX < child.getRight()) {
                        if (y + scrollY >= child.getTop()) {
                            if (y + scrollY < child.getBottom()) {
                                if (canScroll(child, true, dx, dy, (x + scrollX) - child.getLeft(), (y + scrollY) - child.getTop())) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (checkV) {
            if (ViewCompat.canScrollHorizontally(view, -dx)) {
                scrollX = dy;
            } else if (ViewCompat.canScrollVertically(view, -dy)) {
            }
            return z;
        }
        int i2 = dx;
        scrollX = dy;
        z = false;
        return z;
    }

    public boolean shouldInterceptTouchEvent(MotionEvent ev) {
        ViewDragHelper viewDragHelper = this;
        MotionEvent motionEvent = ev;
        int action = MotionEventCompat.getActionMasked(ev);
        int actionIndex = MotionEventCompat.getActionIndex(ev);
        if (action == 0) {
            cancel();
        }
        if (viewDragHelper.mVelocityTracker == null) {
            viewDragHelper.mVelocityTracker = VelocityTracker.obtain();
        }
        viewDragHelper.mVelocityTracker.addMovement(motionEvent);
        int i;
        int pointerId;
        int edgesTouched;
        int i2;
        int i3;
        int i4;
        float x;
        switch (action) {
            case 0:
                i = actionIndex;
                float x2 = ev.getX();
                float y = ev.getY();
                pointerId = MotionEventCompat.getPointerId(motionEvent, 0);
                saveInitialMotion(x2, y, pointerId);
                View toCapture = findTopChildUnder((int) x2, (int) y);
                if (toCapture == viewDragHelper.mCapturedView && viewDragHelper.mDragState == 2) {
                    tryCaptureViewForDrag(toCapture, pointerId);
                }
                edgesTouched = viewDragHelper.mInitialEdgesTouched[pointerId];
                i2 = viewDragHelper.mTrackingEdges;
                if ((edgesTouched & i2) == 0) {
                    break;
                }
                viewDragHelper.mCallback.onEdgeTouched(i2 & edgesTouched, pointerId);
                break;
            case 1:
            case 3:
                i = actionIndex;
                cancel();
                break;
            case 2:
                if (viewDragHelper.mInitialMotionX == null) {
                    i = actionIndex;
                    break;
                } else if (viewDragHelper.mInitialMotionY == null) {
                    i3 = action;
                    i = actionIndex;
                    break;
                } else {
                    int pointerCount;
                    edgesTouched = MotionEventCompat.getPointerCount(ev);
                    i4 = 0;
                    while (i4 < edgesTouched) {
                        i2 = MotionEventCompat.getPointerId(motionEvent, i4);
                        x = MotionEventCompat.getX(motionEvent, i4);
                        float y2 = MotionEventCompat.getY(motionEvent, i4);
                        float dx = x - viewDragHelper.mInitialMotionX[i2];
                        float dy = y2 - viewDragHelper.mInitialMotionY[i2];
                        View toCapture2 = findTopChildUnder((int) x, (int) y2);
                        boolean pastSlop = toCapture2 != null && checkTouchSlop(toCapture2, dx, dy);
                        if (pastSlop) {
                            int oldLeft = toCapture2.getLeft();
                            int targetLeft = ((int) dx) + oldLeft;
                            i3 = action;
                            action = viewDragHelper.mCallback.clampViewPositionHorizontal(toCapture2, targetLeft, (int) dx);
                            pointerId = toCapture2.getTop();
                            i = actionIndex;
                            actionIndex = ((int) dy) + pointerId;
                            pointerCount = edgesTouched;
                            edgesTouched = viewDragHelper.mCallback.clampViewPositionVertical(toCapture2, actionIndex, (int) dy);
                            targetLeft = viewDragHelper.mCallback.getViewHorizontalDragRange(toCapture2);
                            actionIndex = viewDragHelper.mCallback.getViewVerticalDragRange(toCapture2);
                            if (targetLeft != 0) {
                                if (targetLeft > 0 && action == oldLeft) {
                                }
                            }
                            if (actionIndex != 0) {
                                if (actionIndex > 0 && edgesTouched == pointerId) {
                                }
                            }
                            saveLastMotion(ev);
                            break;
                        }
                        i3 = action;
                        i = actionIndex;
                        pointerCount = edgesTouched;
                        reportNewEdgeDrags(dx, dy, i2);
                        if (viewDragHelper.mDragState != 1) {
                            if (!pastSlop || !tryCaptureViewForDrag(toCapture2, i2)) {
                                i4++;
                                action = i3;
                                actionIndex = i;
                                edgesTouched = pointerCount;
                            }
                        }
                        saveLastMotion(ev);
                    }
                    i = actionIndex;
                    pointerCount = edgesTouched;
                    saveLastMotion(ev);
                }
                break;
            case 5:
                i4 = MotionEventCompat.getPointerId(motionEvent, actionIndex);
                float x3 = MotionEventCompat.getX(motionEvent, actionIndex);
                x = MotionEventCompat.getY(motionEvent, actionIndex);
                saveInitialMotion(x3, x, i4);
                int i5 = viewDragHelper.mDragState;
                if (i5 != 0) {
                    if (i5 == 2) {
                        View toCapture3 = findTopChildUnder((int) x3, (int) x);
                        if (toCapture3 == viewDragHelper.mCapturedView) {
                            tryCaptureViewForDrag(toCapture3, i4);
                        }
                        i3 = action;
                        i = actionIndex;
                        break;
                    }
                }
                edgesTouched = viewDragHelper.mInitialEdgesTouched[i4];
                i5 = viewDragHelper.mTrackingEdges;
                if ((edgesTouched & i5) != 0) {
                    viewDragHelper.mCallback.onEdgeTouched(i5 & edgesTouched, i4);
                }
                i3 = action;
                i = actionIndex;
                break;
            case 6:
                clearMotionHistory(MotionEventCompat.getPointerId(motionEvent, actionIndex));
                i3 = action;
                i = actionIndex;
                break;
            default:
                break;
        }
        return viewDragHelper.mDragState == 1;
    }

    public void processTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        int actionIndex = MotionEventCompat.getActionIndex(ev);
        if (action == 0) {
            cancel();
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
        float y;
        int pointerId;
        int edgesTouched;
        int i;
        int index;
        float y2;
        int idx;
        int pointerCount;
        View toCapture;
        switch (action) {
            case 0:
                float x = ev.getX();
                y = ev.getY();
                pointerId = MotionEventCompat.getPointerId(ev, 0);
                View toCapture2 = findTopChildUnder((int) x, (int) y);
                saveInitialMotion(x, y, pointerId);
                tryCaptureViewForDrag(toCapture2, pointerId);
                edgesTouched = this.mInitialEdgesTouched[pointerId];
                i = this.mTrackingEdges;
                if ((edgesTouched & i) != 0) {
                    this.mCallback.onEdgeTouched(i & edgesTouched, pointerId);
                    return;
                }
                return;
            case 1:
                if (this.mDragState == 1) {
                    releaseViewForPointerUp();
                }
                cancel();
                return;
            case 2:
                if (this.mDragState == 1) {
                    index = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
                    y = MotionEventCompat.getX(ev, index);
                    y2 = MotionEventCompat.getY(ev, index);
                    float[] fArr = this.mLastMotionX;
                    edgesTouched = this.mActivePointerId;
                    idx = (int) (y - fArr[edgesTouched]);
                    edgesTouched = (int) (y2 - this.mLastMotionY[edgesTouched]);
                    dragTo(this.mCapturedView.getLeft() + idx, this.mCapturedView.getTop() + edgesTouched, idx, edgesTouched);
                    saveLastMotion(ev);
                    return;
                }
                pointerCount = MotionEventCompat.getPointerCount(ev);
                pointerId = 0;
                while (pointerId < pointerCount) {
                    idx = MotionEventCompat.getPointerId(ev, pointerId);
                    float x2 = MotionEventCompat.getX(ev, pointerId);
                    float y3 = MotionEventCompat.getY(ev, pointerId);
                    float dx = x2 - this.mInitialMotionX[idx];
                    float dy = y3 - this.mInitialMotionY[idx];
                    reportNewEdgeDrags(dx, dy, idx);
                    if (this.mDragState != 1) {
                        toCapture = findTopChildUnder((int) x2, (int) y3);
                        if (checkTouchSlop(toCapture, dx, dy)) {
                            if (tryCaptureViewForDrag(toCapture, idx)) {
                            }
                        }
                        pointerId++;
                    }
                    saveLastMotion(ev);
                    return;
                }
                saveLastMotion(ev);
                return;
            case 3:
                if (this.mDragState == 1) {
                    dispatchViewReleased(0.0f, 0.0f);
                }
                cancel();
                return;
            case 5:
                index = MotionEventCompat.getPointerId(ev, actionIndex);
                y = MotionEventCompat.getX(ev, actionIndex);
                y2 = MotionEventCompat.getY(ev, actionIndex);
                saveInitialMotion(y, y2, index);
                if (this.mDragState == 0) {
                    tryCaptureViewForDrag(findTopChildUnder((int) y, (int) y2), index);
                    edgesTouched = this.mInitialEdgesTouched[index];
                    i = this.mTrackingEdges;
                    if ((edgesTouched & i) != 0) {
                        this.mCallback.onEdgeTouched(i & edgesTouched, index);
                    }
                } else if (isCapturedViewUnder((int) y, (int) y2)) {
                    tryCaptureViewForDrag(this.mCapturedView, index);
                    return;
                }
                return;
            case 6:
                pointerCount = MotionEventCompat.getPointerId(ev, actionIndex);
                if (this.mDragState == 1 && pointerCount == this.mActivePointerId) {
                    index = -1;
                    pointerId = MotionEventCompat.getPointerCount(ev);
                    for (idx = 0; idx < pointerId; idx++) {
                        edgesTouched = MotionEventCompat.getPointerId(ev, idx);
                        if (edgesTouched != this.mActivePointerId) {
                            View findTopChildUnder = findTopChildUnder((int) MotionEventCompat.getX(ev, idx), (int) MotionEventCompat.getY(ev, idx));
                            toCapture = this.mCapturedView;
                            if (findTopChildUnder == toCapture) {
                                if (tryCaptureViewForDrag(toCapture, edgesTouched)) {
                                    index = this.mActivePointerId;
                                    if (index == -1) {
                                        releaseViewForPointerUp();
                                    }
                                }
                            }
                        }
                    }
                    if (index == -1) {
                        releaseViewForPointerUp();
                    }
                }
                clearMotionHistory(pointerCount);
                return;
            default:
                return;
        }
    }

    private void reportNewEdgeDrags(float dx, float dy, int pointerId) {
        int dragsStarted = 0;
        if (checkNewEdgeDrag(dx, dy, pointerId, 1)) {
            dragsStarted = 0 | 1;
        }
        if (checkNewEdgeDrag(dy, dx, pointerId, 4)) {
            dragsStarted |= 4;
        }
        if (checkNewEdgeDrag(dx, dy, pointerId, 2)) {
            dragsStarted |= 2;
        }
        if (checkNewEdgeDrag(dy, dx, pointerId, 8)) {
            dragsStarted |= 8;
        }
        if (dragsStarted != 0) {
            int[] iArr = this.mEdgeDragsInProgress;
            iArr[pointerId] = iArr[pointerId] | dragsStarted;
            this.mCallback.onEdgeDragStarted(dragsStarted, pointerId);
        }
    }

    private boolean checkNewEdgeDrag(float delta, float odelta, int pointerId, int edge) {
        float absDelta = Math.abs(delta);
        float absODelta = Math.abs(odelta);
        boolean z = false;
        if (!((this.mInitialEdgesTouched[pointerId] & edge) != edge || (this.mTrackingEdges & edge) == 0 || (this.mEdgeDragsLocked[pointerId] & edge) == edge || (this.mEdgeDragsInProgress[pointerId] & edge) == edge)) {
            int i = this.mTouchSlop;
            if (absDelta > ((float) i) || absODelta > ((float) i)) {
                if (absDelta >= 0.5f * absODelta || !this.mCallback.onEdgeLock(edge)) {
                    if ((this.mEdgeDragsInProgress[pointerId] & edge) == 0 && absDelta > ((float) this.mTouchSlop)) {
                        z = true;
                    }
                    return z;
                }
                int[] iArr = this.mEdgeDragsLocked;
                iArr[pointerId] = iArr[pointerId] | edge;
                return false;
            }
        }
        return false;
    }

    private boolean checkTouchSlop(View child, float dx, float dy) {
        boolean z = false;
        if (child == null) {
            return false;
        }
        boolean checkHorizontal = this.mCallback.getViewHorizontalDragRange(child) > 0;
        boolean checkVertical = this.mCallback.getViewVerticalDragRange(child) > 0;
        if (checkHorizontal && checkVertical) {
            float f = (dx * dx) + (dy * dy);
            int i = this.mTouchSlop;
            if (f > ((float) (i * i))) {
                z = true;
            }
            return z;
        } else if (checkHorizontal) {
            if (Math.abs(dx) > ((float) this.mTouchSlop)) {
                z = true;
            }
            return z;
        } else if (!checkVertical) {
            return false;
        } else {
            if (Math.abs(dy) > ((float) this.mTouchSlop)) {
                z = true;
            }
            return z;
        }
    }

    public boolean checkTouchSlop(int directions) {
        int count = this.mInitialMotionX.length;
        for (int i = 0; i < count; i++) {
            if (checkTouchSlop(directions, i)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkTouchSlop(int directions, int pointerId) {
        boolean z = false;
        if (!isPointerDown(pointerId)) {
            return false;
        }
        boolean checkHorizontal = (directions & 1) == 1;
        boolean checkVertical = (directions & 2) == 2;
        float dx = this.mLastMotionX[pointerId] - this.mInitialMotionX[pointerId];
        float dy = this.mLastMotionY[pointerId] - this.mInitialMotionY[pointerId];
        if (checkHorizontal && checkVertical) {
            float f = (dx * dx) + (dy * dy);
            int i = this.mTouchSlop;
            if (f > ((float) (i * i))) {
                z = true;
            }
            return z;
        } else if (checkHorizontal) {
            if (Math.abs(dx) > ((float) this.mTouchSlop)) {
                z = true;
            }
            return z;
        } else if (!checkVertical) {
            return false;
        } else {
            if (Math.abs(dy) > ((float) this.mTouchSlop)) {
                z = true;
            }
            return z;
        }
    }

    public boolean isEdgeTouched(int edges) {
        int count = this.mInitialEdgesTouched.length;
        for (int i = 0; i < count; i++) {
            if (isEdgeTouched(edges, i)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEdgeTouched(int edges, int pointerId) {
        return isPointerDown(pointerId) && (this.mInitialEdgesTouched[pointerId] & edges) != 0;
    }

    private void releaseViewForPointerUp() {
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxVelocity);
        dispatchViewReleased(clampMag(VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity), clampMag(VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity));
    }

    private void dragTo(int left, int top, int dx, int dy) {
        int i = dx;
        int i2 = dy;
        int clampedX = left;
        int clampedY = top;
        int oldLeft = this.mCapturedView.getLeft();
        int oldTop = this.mCapturedView.getTop();
        if (i != 0) {
            clampedX = r0.mCallback.clampViewPositionHorizontal(r0.mCapturedView, left, i);
            r0.mCapturedView.offsetLeftAndRight(clampedX - oldLeft);
        } else {
            int i3 = left;
        }
        if (i2 != 0) {
            clampedY = r0.mCallback.clampViewPositionVertical(r0.mCapturedView, top, i2);
            r0.mCapturedView.offsetTopAndBottom(clampedY - oldTop);
        } else {
            int i4 = top;
        }
        if (i == 0) {
            if (i2 == 0) {
                return;
            }
        }
        r0.mCallback.onViewPositionChanged(r0.mCapturedView, clampedX, clampedY, clampedX - oldLeft, clampedY - oldTop);
    }

    public boolean isCapturedViewUnder(int x, int y) {
        return isViewUnder(this.mCapturedView, x, y);
    }

    public boolean isViewUnder(View view, int x, int y) {
        boolean z = false;
        if (view == null) {
            return false;
        }
        if (x >= view.getLeft() && x < view.getRight() && y >= view.getTop() && y < view.getBottom()) {
            z = true;
        }
        return z;
    }

    public View findTopChildUnder(int x, int y) {
        for (int i = this.mParentView.getChildCount() - 1; i >= 0; i--) {
            View child = this.mParentView.getChildAt(this.mCallback.getOrderedChildIndex(i));
            if (x >= child.getLeft()) {
                if (x < child.getRight()) {
                    if (y >= child.getTop()) {
                        if (y < child.getBottom()) {
                            return child;
                        }
                    }
                }
            }
        }
        return null;
    }

    private int getEdgesTouched(int x, int y) {
        int result = 0;
        if (x < this.mParentView.getLeft() + this.mEdgeSize) {
            result = 0 | 1;
        }
        if (y < this.mParentView.getTop() + this.mEdgeSize) {
            result |= 4;
        }
        if (x > this.mParentView.getRight() - this.mEdgeSize) {
            result |= 2;
        }
        if (y > this.mParentView.getBottom() - this.mEdgeSize) {
            return result | 8;
        }
        return result;
    }
}
