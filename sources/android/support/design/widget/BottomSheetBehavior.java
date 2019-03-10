package android.support.design.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.C0035R;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.support.v4.math.MathUtils;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import java.lang.ref.WeakReference;

public class BottomSheetBehavior<V extends View> extends Behavior<V> {
    private static final float HIDE_FRICTION = 0.1f;
    private static final float HIDE_THRESHOLD = 0.5f;
    public static final int PEEK_HEIGHT_AUTO = -1;
    public static final int STATE_COLLAPSED = 4;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_EXPANDED = 3;
    public static final int STATE_HIDDEN = 5;
    public static final int STATE_SETTLING = 2;
    int mActivePointerId;
    private BottomSheetCallback mCallback;
    private final Callback mDragCallback = new C08152();
    boolean mHideable;
    private boolean mIgnoreEvents;
    private int mInitialY;
    private int mLastNestedScrollDy;
    int mMaxOffset;
    private float mMaximumVelocity;
    int mMinOffset;
    private boolean mNestedScrolled;
    WeakReference<View> mNestedScrollingChildRef;
    int mParentHeight;
    private int mPeekHeight;
    private boolean mPeekHeightAuto;
    private int mPeekHeightMin;
    private boolean mSkipCollapsed;
    int mState = 4;
    boolean mTouchingScrollingChild;
    private VelocityTracker mVelocityTracker;
    ViewDragHelper mViewDragHelper;
    WeakReference<V> mViewRef;

    public static abstract class BottomSheetCallback {
        public abstract void onSlide(@NonNull View view, float f);

        public abstract void onStateChanged(@NonNull View view, int i);
    }

    private class SettleRunnable implements Runnable {
        private final int mTargetState;
        private final View mView;

        SettleRunnable(View view, int targetState) {
            this.mView = view;
            this.mTargetState = targetState;
        }

        public void run() {
            if (BottomSheetBehavior.this.mViewDragHelper == null || !BottomSheetBehavior.this.mViewDragHelper.continueSettling(true)) {
                BottomSheetBehavior.this.setStateInternal(this.mTargetState);
            } else {
                ViewCompat.postOnAnimation(this.mView, this);
            }
        }
    }

    /* renamed from: android.support.design.widget.BottomSheetBehavior$2 */
    class C08152 extends Callback {
        C08152() {
        }

        public boolean tryCaptureView(View child, int pointerId) {
            boolean z = true;
            if (BottomSheetBehavior.this.mState == 1 || BottomSheetBehavior.this.mTouchingScrollingChild) {
                return false;
            }
            if (BottomSheetBehavior.this.mState == 3 && BottomSheetBehavior.this.mActivePointerId == pointerId) {
                View scroll = (View) BottomSheetBehavior.this.mNestedScrollingChildRef.get();
                if (scroll != null && scroll.canScrollVertically(-1)) {
                    return false;
                }
            }
            if (BottomSheetBehavior.this.mViewRef == null || BottomSheetBehavior.this.mViewRef.get() != child) {
                z = false;
            }
            return z;
        }

        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            BottomSheetBehavior.this.dispatchOnSlide(top);
        }

        public void onViewDragStateChanged(int state) {
            if (state == 1) {
                BottomSheetBehavior.this.setStateInternal(1);
            }
        }

        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int top;
            int targetState;
            if (yvel < 0.0f) {
                top = BottomSheetBehavior.this.mMinOffset;
                targetState = 3;
            } else if (BottomSheetBehavior.this.mHideable && BottomSheetBehavior.this.shouldHide(releasedChild, yvel)) {
                top = BottomSheetBehavior.this.mParentHeight;
                targetState = 5;
            } else if (yvel == 0.0f) {
                top = releasedChild.getTop();
                if (Math.abs(top - BottomSheetBehavior.this.mMinOffset) < Math.abs(top - BottomSheetBehavior.this.mMaxOffset)) {
                    top = BottomSheetBehavior.this.mMinOffset;
                    targetState = 3;
                } else {
                    targetState = 4;
                    top = BottomSheetBehavior.this.mMaxOffset;
                }
            } else {
                top = BottomSheetBehavior.this.mMaxOffset;
                targetState = 4;
            }
            if (BottomSheetBehavior.this.mViewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top)) {
                BottomSheetBehavior.this.setStateInternal(2);
                ViewCompat.postOnAnimation(releasedChild, new SettleRunnable(releasedChild, targetState));
                return;
            }
            BottomSheetBehavior.this.setStateInternal(targetState);
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            return MathUtils.clamp(top, BottomSheetBehavior.this.mMinOffset, BottomSheetBehavior.this.mHideable ? BottomSheetBehavior.this.mParentHeight : BottomSheetBehavior.this.mMaxOffset);
        }

        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return child.getLeft();
        }

        public int getViewVerticalDragRange(View child) {
            if (BottomSheetBehavior.this.mHideable) {
                return BottomSheetBehavior.this.mParentHeight - BottomSheetBehavior.this.mMinOffset;
            }
            return BottomSheetBehavior.this.mMaxOffset - BottomSheetBehavior.this.mMinOffset;
        }
    }

    protected static class SavedState extends AbsSavedState {
        public static final Creator<SavedState> CREATOR = new C00501();
        final int state;

        /* renamed from: android.support.design.widget.BottomSheetBehavior$SavedState$1 */
        static class C00501 implements ClassLoaderCreator<SavedState> {
            C00501() {
            }

            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        public SavedState(Parcel source) {
            this(source, null);
        }

        public SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            this.state = source.readInt();
        }

        public SavedState(Parcelable superState, int state) {
            super(superState);
            this.state = state;
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.state);
        }
    }

    public BottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, C0035R.styleable.BottomSheetBehavior_Layout);
        TypedValue value = a.peekValue(C0035R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight);
        if (value == null || value.data != -1) {
            setPeekHeight(a.getDimensionPixelSize(C0035R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight, -1));
        } else {
            setPeekHeight(value.data);
        }
        setHideable(a.getBoolean(C0035R.styleable.BottomSheetBehavior_Layout_behavior_hideable, false));
        setSkipCollapsed(a.getBoolean(C0035R.styleable.BottomSheetBehavior_Layout_behavior_skipCollapsed, false));
        a.recycle();
        this.mMaximumVelocity = (float) ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
    }

    public Parcelable onSaveInstanceState(CoordinatorLayout parent, V child) {
        return new SavedState(super.onSaveInstanceState(parent, child), this.mState);
    }

    public void onRestoreInstanceState(CoordinatorLayout parent, V child, Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(parent, child, ss.getSuperState());
        if (ss.state != 1) {
            if (ss.state != 2) {
                this.mState = ss.state;
                return;
            }
        }
        this.mState = 4;
    }

    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        int peekHeight;
        if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child)) {
            ViewCompat.setFitsSystemWindows(child, true);
        }
        int savedTop = child.getTop();
        parent.onLayoutChild(child, layoutDirection);
        this.mParentHeight = parent.getHeight();
        if (this.mPeekHeightAuto) {
            if (this.mPeekHeightMin == 0) {
                this.mPeekHeightMin = parent.getResources().getDimensionPixelSize(C0035R.dimen.design_bottom_sheet_peek_height_min);
            }
            peekHeight = Math.max(this.mPeekHeightMin, this.mParentHeight - ((parent.getWidth() * 9) / 16));
        } else {
            peekHeight = this.mPeekHeight;
        }
        this.mMinOffset = Math.max(0, this.mParentHeight - child.getHeight());
        this.mMaxOffset = Math.max(this.mParentHeight - peekHeight, this.mMinOffset);
        int i = this.mState;
        if (i == 3) {
            ViewCompat.offsetTopAndBottom(child, this.mMinOffset);
        } else if (this.mHideable && i == 5) {
            ViewCompat.offsetTopAndBottom(child, this.mParentHeight);
        } else {
            i = this.mState;
            if (i == 4) {
                ViewCompat.offsetTopAndBottom(child, this.mMaxOffset);
            } else {
                if (i != 1) {
                    if (i == 2) {
                    }
                }
                ViewCompat.offsetTopAndBottom(child, savedTop - child.getTop());
            }
        }
        if (this.mViewDragHelper == null) {
            this.mViewDragHelper = ViewDragHelper.create(parent, this.mDragCallback);
        }
        this.mViewRef = new WeakReference(child);
        this.mNestedScrollingChildRef = new WeakReference(findScrollingChild(child));
        return true;
    }

    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        boolean z = false;
        if (child.isShown()) {
            int action = event.getActionMasked();
            if (action == 0) {
                reset();
            }
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(event);
            if (action != 3) {
                switch (action) {
                    case 0:
                        boolean z2;
                        int initialX = (int) event.getX();
                        this.mInitialY = (int) event.getY();
                        WeakReference weakReference = this.mNestedScrollingChildRef;
                        View scroll = weakReference != null ? (View) weakReference.get() : null;
                        if (scroll != null && parent.isPointInChildBounds(scroll, initialX, this.mInitialY)) {
                            this.mActivePointerId = event.getPointerId(event.getActionIndex());
                            this.mTouchingScrollingChild = true;
                        }
                        if (this.mActivePointerId == -1) {
                            if (!parent.isPointInChildBounds(child, initialX, this.mInitialY)) {
                                z2 = true;
                                this.mIgnoreEvents = z2;
                                break;
                            }
                        }
                        z2 = false;
                        this.mIgnoreEvents = z2;
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
            }
            this.mTouchingScrollingChild = false;
            this.mActivePointerId = -1;
            if (this.mIgnoreEvents) {
                this.mIgnoreEvents = false;
                return false;
            } else if (!this.mIgnoreEvents && this.mViewDragHelper.shouldInterceptTouchEvent(event)) {
                return true;
            } else {
                View scroll2 = (View) this.mNestedScrollingChildRef.get();
                if (action == 2 && scroll2 != null && !this.mIgnoreEvents && this.mState != 1) {
                    if (!parent.isPointInChildBounds(scroll2, (int) event.getX(), (int) event.getY())) {
                        if (Math.abs(((float) this.mInitialY) - event.getY()) > ((float) this.mViewDragHelper.getTouchSlop())) {
                            z = true;
                            return z;
                        }
                    }
                }
                return z;
            }
        }
        this.mIgnoreEvents = true;
        return false;
    }

    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!child.isShown()) {
            return false;
        }
        int action = event.getActionMasked();
        if (this.mState == 1 && action == 0) {
            return true;
        }
        ViewDragHelper viewDragHelper = this.mViewDragHelper;
        if (viewDragHelper != null) {
            viewDragHelper.processTouchEvent(event);
        }
        if (action == 0) {
            reset();
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
        if (action == 2 && !this.mIgnoreEvents) {
            if (Math.abs(((float) this.mInitialY) - event.getY()) > ((float) this.mViewDragHelper.getTouchSlop())) {
                this.mViewDragHelper.captureChildView(child, event.getPointerId(event.getActionIndex()));
            }
        }
        return this.mIgnoreEvents ^ true;
    }

    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V v, View directTargetChild, View target, int nestedScrollAxes) {
        this.mLastNestedScrollDy = 0;
        this.mNestedScrolled = false;
        if ((nestedScrollAxes & 2) != 0) {
            return true;
        }
        return false;
    }

    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed) {
        if (target == ((View) this.mNestedScrollingChildRef.get())) {
            int currentTop = child.getTop();
            int newTop = currentTop - dy;
            int i;
            if (dy > 0) {
                i = this.mMinOffset;
                if (newTop < i) {
                    consumed[1] = currentTop - i;
                    ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                    setStateInternal(3);
                } else {
                    consumed[1] = dy;
                    ViewCompat.offsetTopAndBottom(child, -dy);
                    setStateInternal(1);
                }
            } else if (dy < 0) {
                if (!target.canScrollVertically(-1)) {
                    i = this.mMaxOffset;
                    if (newTop > i) {
                        if (!this.mHideable) {
                            consumed[1] = currentTop - i;
                            ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                            setStateInternal(4);
                        }
                    }
                    consumed[1] = dy;
                    ViewCompat.offsetTopAndBottom(child, -dy);
                    setStateInternal(1);
                }
            }
            dispatchOnSlide(child.getTop());
            this.mLastNestedScrollDy = dy;
            this.mNestedScrolled = true;
        }
    }

    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {
        if (child.getTop() == this.mMinOffset) {
            setStateInternal(3);
            return;
        }
        WeakReference weakReference = this.mNestedScrollingChildRef;
        if (weakReference != null && target == weakReference.get()) {
            if (this.mNestedScrolled) {
                int top;
                int targetState;
                if (this.mLastNestedScrollDy > 0) {
                    top = this.mMinOffset;
                    targetState = 3;
                } else if (this.mHideable && shouldHide(child, getYVelocity())) {
                    top = this.mParentHeight;
                    targetState = 5;
                } else if (this.mLastNestedScrollDy == 0) {
                    top = child.getTop();
                    if (Math.abs(top - this.mMinOffset) < Math.abs(top - this.mMaxOffset)) {
                        top = this.mMinOffset;
                        targetState = 3;
                    } else {
                        targetState = 4;
                        top = this.mMaxOffset;
                    }
                } else {
                    top = this.mMaxOffset;
                    targetState = 4;
                }
                if (this.mViewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
                    setStateInternal(2);
                    ViewCompat.postOnAnimation(child, new SettleRunnable(child, targetState));
                } else {
                    setStateInternal(targetState);
                }
                this.mNestedScrolled = false;
            }
        }
    }

    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY) {
        if (target == this.mNestedScrollingChildRef.get()) {
            if (this.mState == 3) {
                if (super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)) {
                }
            }
            return true;
        }
        return false;
    }

    public final void setPeekHeight(int peekHeight) {
        boolean layout = false;
        if (peekHeight != -1) {
            if (!this.mPeekHeightAuto) {
                if (this.mPeekHeight != peekHeight) {
                }
            }
            this.mPeekHeightAuto = false;
            this.mPeekHeight = Math.max(0, peekHeight);
            this.mMaxOffset = this.mParentHeight - peekHeight;
            layout = true;
        } else if (!this.mPeekHeightAuto) {
            this.mPeekHeightAuto = true;
            layout = true;
        }
        if (layout && this.mState == 4) {
            WeakReference weakReference = this.mViewRef;
            if (weakReference != null) {
                View view = (View) weakReference.get();
                if (view != null) {
                    view.requestLayout();
                }
            }
        }
    }

    public final int getPeekHeight() {
        return this.mPeekHeightAuto ? -1 : this.mPeekHeight;
    }

    public void setHideable(boolean hideable) {
        this.mHideable = hideable;
    }

    public boolean isHideable() {
        return this.mHideable;
    }

    public void setSkipCollapsed(boolean skipCollapsed) {
        this.mSkipCollapsed = skipCollapsed;
    }

    public boolean getSkipCollapsed() {
        return this.mSkipCollapsed;
    }

    public void setBottomSheetCallback(BottomSheetCallback callback) {
        this.mCallback = callback;
    }

    public final void setState(final int state) {
        if (state != this.mState) {
            WeakReference weakReference = this.mViewRef;
            if (weakReference == null) {
                if (!(state == 4 || state == 3)) {
                    if (!this.mHideable || state != 5) {
                        return;
                    }
                }
                this.mState = state;
                return;
            }
            final View child = (View) weakReference.get();
            if (child != null) {
                ViewParent parent = child.getParent();
                if (parent != null && parent.isLayoutRequested() && ViewCompat.isAttachedToWindow(child)) {
                    child.post(new Runnable() {
                        public void run() {
                            BottomSheetBehavior.this.startSettlingAnimation(child, state);
                        }
                    });
                } else {
                    startSettlingAnimation(child, state);
                }
            }
        }
    }

    public final int getState() {
        return this.mState;
    }

    void setStateInternal(int state) {
        if (this.mState != state) {
            this.mState = state;
            View bottomSheet = (View) this.mViewRef.get();
            if (bottomSheet != null) {
                BottomSheetCallback bottomSheetCallback = this.mCallback;
                if (bottomSheetCallback != null) {
                    bottomSheetCallback.onStateChanged(bottomSheet, state);
                }
            }
        }
    }

    private void reset() {
        this.mActivePointerId = -1;
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    boolean shouldHide(View child, float yvel) {
        boolean z = true;
        if (this.mSkipCollapsed) {
            return true;
        }
        if (child.getTop() < this.mMaxOffset) {
            return false;
        }
        if (Math.abs((((float) child.getTop()) + (0.1f * yvel)) - ((float) this.mMaxOffset)) / ((float) this.mPeekHeight) <= HIDE_THRESHOLD) {
            z = false;
        }
        return z;
    }

    @VisibleForTesting
    View findScrollingChild(View view) {
        if (ViewCompat.isNestedScrollingEnabled(view)) {
            return view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                View scrollingChild = findScrollingChild(group.getChildAt(i));
                if (scrollingChild != null) {
                    return scrollingChild;
                }
            }
        }
        return null;
    }

    private float getYVelocity() {
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
        return this.mVelocityTracker.getYVelocity(this.mActivePointerId);
    }

    void startSettlingAnimation(View child, int state) {
        int top;
        if (state == 4) {
            top = this.mMaxOffset;
        } else if (state == 3) {
            top = this.mMinOffset;
        } else if (this.mHideable && state == 5) {
            top = this.mParentHeight;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Illegal state argument: ");
            stringBuilder.append(state);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        if (this.mViewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
            setStateInternal(2);
            ViewCompat.postOnAnimation(child, new SettleRunnable(child, state));
            return;
        }
        setStateInternal(state);
    }

    void dispatchOnSlide(int top) {
        View bottomSheet = (View) this.mViewRef.get();
        if (bottomSheet != null) {
            BottomSheetCallback bottomSheetCallback = this.mCallback;
            if (bottomSheetCallback != null) {
                int i = this.mMaxOffset;
                if (top > i) {
                    bottomSheetCallback.onSlide(bottomSheet, ((float) (i - top)) / ((float) (this.mParentHeight - i)));
                } else {
                    bottomSheetCallback.onSlide(bottomSheet, ((float) (i - top)) / ((float) (i - this.mMinOffset)));
                }
            }
        }
    }

    @VisibleForTesting
    int getPeekHeightMin() {
        return this.mPeekHeightMin;
    }

    public static <V extends View> BottomSheetBehavior<V> from(V view) {
        LayoutParams params = view.getLayoutParams();
        if (params instanceof CoordinatorLayout.LayoutParams) {
            Behavior behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
            if (behavior instanceof BottomSheetBehavior) {
                return (BottomSheetBehavior) behavior;
            }
            throw new IllegalArgumentException("The view is not associated with BottomSheetBehavior");
        }
        throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
    }
}
