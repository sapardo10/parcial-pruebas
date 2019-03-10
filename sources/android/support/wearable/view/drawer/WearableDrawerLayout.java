package android.support.wearable.view.drawer;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.wearable.view.drawer.FlingWatcher.FlingListener;
import android.support.wearable.view.drawer.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;

@TargetApi(23)
@Deprecated
public class WearableDrawerLayout extends FrameLayout implements OnLayoutChangeListener, NestedScrollingParent, FlingListener {
    private static final int DOWN = 1;
    private static final int GRAVITY_UNDEFINED = -1;
    private static final int NESTED_SCROLL_SLOP_DP = 5;
    private static final float OPENED_PERCENT_THRESHOLD = 0.5f;
    private static final int PEEK_AUTO_CLOSE_DELAY_MS = 1000;
    private static final int PEEK_FADE_DURATION_MS = 150;
    private static final String TAG = "WearableDrawerLayout";
    private static final int UP = -1;
    private final ViewDragHelper mBottomDrawerDragger;
    @VisibleForTesting
    final Callback mBottomDrawerDraggerCallback;
    @Nullable
    private WearableDrawerView mBottomDrawerView;
    private boolean mCanBottomDrawerBeClosed;
    private boolean mCanTopDrawerBeClosed;
    private final ClosePeekRunnable mCloseBottomPeekRunnable;
    private final ClosePeekRunnable mCloseTopPeekRunnable;
    private int mCurrentNestedScrollSlopTracker;
    private MotionEvent mDrawerOpenLastInterceptedTouchEvent;
    private DrawerStateCallback mDrawerStateCallback;
    private final FlingWatcher mFlingWatcher;
    private final boolean mIsAccessibilityEnabled;
    private boolean mLastScrollWasFling;
    private final Handler mMainThreadHandler;
    private final int mNestedScrollSlopPx;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    @Nullable
    private View mScrollingContentView;
    private boolean mShouldOpenBottomDrawerAfterLayout;
    private boolean mShouldOpenTopDrawerAfterLayout;
    private boolean mShouldPeekBottomDrawerAfterLayout;
    private boolean mShouldPeekTopDrawerAfterLayout;
    private int mSystemWindowInsetBottom;
    private final ViewDragHelper mTopDrawerDragger;
    @VisibleForTesting
    final Callback mTopDrawerDraggerCallback;
    @Nullable
    private WearableDrawerView mTopDrawerView;

    /* renamed from: android.support.wearable.view.drawer.WearableDrawerLayout$1 */
    class C04661 implements OnGlobalLayoutListener {
        C04661() {
        }

        public void onGlobalLayout() {
            WearableDrawerLayout.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            if (WearableDrawerLayout.this.mShouldOpenBottomDrawerAfterLayout) {
                WearableDrawerLayout wearableDrawerLayout = WearableDrawerLayout.this;
                wearableDrawerLayout.openDrawerWithoutAnimation(wearableDrawerLayout.mBottomDrawerView);
                WearableDrawerLayout.this.mShouldOpenBottomDrawerAfterLayout = false;
            } else if (WearableDrawerLayout.this.mShouldPeekBottomDrawerAfterLayout) {
                WearableDrawerLayout.this.peekDrawer(80);
                WearableDrawerLayout.this.mShouldPeekBottomDrawerAfterLayout = false;
            }
            if (WearableDrawerLayout.this.mShouldOpenTopDrawerAfterLayout) {
                wearableDrawerLayout = WearableDrawerLayout.this;
                wearableDrawerLayout.openDrawerWithoutAnimation(wearableDrawerLayout.mTopDrawerView);
                WearableDrawerLayout.this.mShouldOpenTopDrawerAfterLayout = false;
            } else if (WearableDrawerLayout.this.mShouldPeekTopDrawerAfterLayout) {
                WearableDrawerLayout.this.peekDrawer(48);
                WearableDrawerLayout.this.mShouldPeekTopDrawerAfterLayout = false;
            }
        }
    }

    /* renamed from: android.support.wearable.view.drawer.WearableDrawerLayout$2 */
    class C04672 implements Runnable {
        final /* synthetic */ View val$content;

        C04672(View view) {
            this.val$content = view;
        }

        public void run() {
            this.val$content.setVisibility(8);
        }
    }

    private class ClosePeekRunnable implements Runnable {
        private final int gravity;

        private ClosePeekRunnable(int gravity) {
            this.gravity = gravity;
        }

        public void run() {
            WearableDrawerView drawer = WearableDrawerLayout.this.findDrawerWithGravity(this.gravity);
            if (drawer == null) {
                return;
            }
            if (!drawer.isOpened()) {
                if (drawer.getDrawerState() == 0) {
                    WearableDrawerLayout.this.closeDrawer(this.gravity);
                }
            }
        }
    }

    public static abstract class DrawerStateCallback {
        public abstract void onDrawerClosed(View view);

        public abstract void onDrawerOpened(View view);

        public abstract void onDrawerStateChanged(@WearableDrawerView$DrawerState int i);
    }

    private abstract class DrawerDraggerCallback extends Callback {
        public abstract WearableDrawerView getDrawerView();

        private DrawerDraggerCallback() {
        }

        public boolean tryCaptureView(View child, int pointerId) {
            View drawerView = getDrawerView();
            return child == drawerView && !drawerView.isLocked() && drawerView.hasDrawerContent();
        }

        public int getViewVerticalDragRange(View child) {
            return child == getDrawerView() ? child.getHeight() : 0;
        }

        public void onViewCaptured(View capturedChild, int activePointerId) {
            WearableDrawerLayout.showDrawerContentMaybeAnimate((WearableDrawerView) capturedChild);
        }

        public void onViewDragStateChanged(int state) {
            WearableDrawerView drawerView = getDrawerView();
            if (state == 0) {
                boolean openedOrClosed = false;
                if (drawerView.isOpened()) {
                    openedOrClosed = true;
                    drawerView.onDrawerOpened();
                    if (WearableDrawerLayout.this.mDrawerStateCallback != null) {
                        WearableDrawerLayout.this.mDrawerStateCallback.onDrawerOpened(drawerView);
                    }
                    WearableDrawerLayout wearableDrawerLayout = WearableDrawerLayout.this;
                    wearableDrawerLayout.mCanTopDrawerBeClosed = wearableDrawerLayout.canDrawerContentScrollVertically(wearableDrawerLayout.mTopDrawerView, 1) ^ true;
                    wearableDrawerLayout = WearableDrawerLayout.this;
                    wearableDrawerLayout.mCanBottomDrawerBeClosed = wearableDrawerLayout.canDrawerContentScrollVertically(wearableDrawerLayout.mBottomDrawerView, -1) ^ true;
                } else if (drawerView.isClosed()) {
                    openedOrClosed = true;
                    drawerView.onDrawerClosed();
                    if (WearableDrawerLayout.this.mDrawerStateCallback != null) {
                        WearableDrawerLayout.this.mDrawerStateCallback.onDrawerClosed(drawerView);
                    }
                }
                if (openedOrClosed && drawerView.isPeeking()) {
                    drawerView.setIsPeeking(false);
                    drawerView.getPeekContainer().setVisibility(4);
                }
            }
            if (drawerView.getDrawerState() != state) {
                drawerView.setDrawerState(state);
                drawerView.onDrawerStateChanged(state);
                if (WearableDrawerLayout.this.mDrawerStateCallback != null) {
                    WearableDrawerLayout.this.mDrawerStateCallback.onDrawerStateChanged(state);
                }
            }
        }
    }

    private class BottomDrawerDraggerCallback extends DrawerDraggerCallback {
        private BottomDrawerDraggerCallback() {
            super();
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            if (WearableDrawerLayout.this.mBottomDrawerView != child) {
                return 0;
            }
            int parentHeight = WearableDrawerLayout.this.getHeight();
            return Math.max(parentHeight - child.getHeight(), Math.min(top, parentHeight - WearableDrawerLayout.this.mBottomDrawerView.getPeekContainer().getHeight()));
        }

        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            if (WearableDrawerLayout.this.mBottomDrawerView != null && edgeFlags == 8) {
                if (!WearableDrawerLayout.this.mBottomDrawerView.isLocked()) {
                    if (WearableDrawerLayout.this.mTopDrawerView != null) {
                        if (WearableDrawerLayout.this.mTopDrawerView.isOpened()) {
                            return;
                        }
                    }
                    if (WearableDrawerLayout.this.mBottomDrawerView.hasDrawerContent()) {
                        WearableDrawerLayout.this.mBottomDrawerDragger.captureChildView(WearableDrawerLayout.this.mBottomDrawerView, pointerId);
                    }
                }
            }
        }

        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (releasedChild == WearableDrawerLayout.this.mBottomDrawerView) {
                int finalTop;
                int parentHeight = WearableDrawerLayout.this.getHeight();
                float openedPercent = WearableDrawerLayout.this.mBottomDrawerView.getOpenedPercent();
                if (yvel >= 0.0f) {
                    if (yvel != 0.0f || openedPercent <= WearableDrawerLayout.OPENED_PERCENT_THRESHOLD) {
                        WearableDrawerLayout.animatePeekVisibleAfterBeingClosed(WearableDrawerLayout.this.mBottomDrawerView);
                        finalTop = WearableDrawerLayout.this.getHeight() - WearableDrawerLayout.this.mBottomDrawerView.getPeekContainer().getHeight();
                        WearableDrawerLayout.this.mBottomDrawerDragger.settleCapturedViewAt(0, finalTop);
                        WearableDrawerLayout.this.invalidate();
                    }
                }
                finalTop = parentHeight - releasedChild.getHeight();
                WearableDrawerLayout.this.mBottomDrawerDragger.settleCapturedViewAt(0, finalTop);
                WearableDrawerLayout.this.invalidate();
            }
        }

        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == WearableDrawerLayout.this.mBottomDrawerView) {
                int height = changedView.getHeight();
                WearableDrawerLayout.this.mBottomDrawerView.setOpenedPercent(((float) (WearableDrawerLayout.this.getHeight() - top)) / ((float) height));
                WearableDrawerLayout.this.invalidate();
            }
        }

        public WearableDrawerView getDrawerView() {
            return WearableDrawerLayout.this.mBottomDrawerView;
        }
    }

    private class TopDrawerDraggerCallback extends DrawerDraggerCallback {
        private TopDrawerDraggerCallback() {
            super();
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            if (WearableDrawerLayout.this.mTopDrawerView == child) {
                return Math.max(WearableDrawerLayout.this.mTopDrawerView.getPeekContainer().getHeight() - child.getHeight(), Math.min(top, 0));
            }
            return 0;
        }

        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            if (WearableDrawerLayout.this.mTopDrawerView != null && edgeFlags == 4) {
                if (!WearableDrawerLayout.this.mTopDrawerView.isLocked()) {
                    if (WearableDrawerLayout.this.mBottomDrawerView != null) {
                        if (WearableDrawerLayout.this.mBottomDrawerView.isOpened()) {
                            return;
                        }
                    }
                    if (WearableDrawerLayout.this.mTopDrawerView.hasDrawerContent()) {
                        boolean atTop;
                        if (WearableDrawerLayout.this.mScrollingContentView != null) {
                            if (WearableDrawerLayout.this.mScrollingContentView.canScrollVertically(-1)) {
                                atTop = false;
                                if (WearableDrawerLayout.this.mTopDrawerView.shouldOnlyOpenWhenAtTop()) {
                                    if (atTop) {
                                        return;
                                    }
                                }
                                WearableDrawerLayout.this.mTopDrawerDragger.captureChildView(WearableDrawerLayout.this.mTopDrawerView, pointerId);
                            }
                        }
                        atTop = true;
                        if (WearableDrawerLayout.this.mTopDrawerView.shouldOnlyOpenWhenAtTop()) {
                            if (atTop) {
                                return;
                            }
                        }
                        WearableDrawerLayout.this.mTopDrawerDragger.captureChildView(WearableDrawerLayout.this.mTopDrawerView, pointerId);
                    }
                }
            }
        }

        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (releasedChild == WearableDrawerLayout.this.mTopDrawerView) {
                int finalTop;
                float openedPercent = WearableDrawerLayout.this.mTopDrawerView.getOpenedPercent();
                if (yvel <= 0.0f) {
                    if (yvel != 0.0f || openedPercent <= WearableDrawerLayout.OPENED_PERCENT_THRESHOLD) {
                        WearableDrawerLayout.animatePeekVisibleAfterBeingClosed(WearableDrawerLayout.this.mTopDrawerView);
                        finalTop = WearableDrawerLayout.this.mTopDrawerView.getPeekContainer().getHeight() - releasedChild.getHeight();
                        WearableDrawerLayout.this.mTopDrawerDragger.settleCapturedViewAt(0, finalTop);
                        WearableDrawerLayout.this.invalidate();
                    }
                }
                finalTop = 0;
                WearableDrawerLayout.this.mTopDrawerDragger.settleCapturedViewAt(0, finalTop);
                WearableDrawerLayout.this.invalidate();
            }
        }

        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == WearableDrawerLayout.this.mTopDrawerView) {
                int height = changedView.getHeight();
                WearableDrawerLayout.this.mTopDrawerView.setOpenedPercent(((float) (top + height)) / ((float) height));
                WearableDrawerLayout.this.invalidate();
            }
        }

        public WearableDrawerView getDrawerView() {
            return WearableDrawerLayout.this.mTopDrawerView;
        }
    }

    public WearableDrawerLayout(Context context) {
        this(context, null);
    }

    public WearableDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WearableDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        this.mMainThreadHandler = new Handler(Looper.getMainLooper());
        this.mCloseTopPeekRunnable = new ClosePeekRunnable(48);
        this.mCloseBottomPeekRunnable = new ClosePeekRunnable(80);
        this.mFlingWatcher = new FlingWatcher(this);
        this.mTopDrawerDraggerCallback = new TopDrawerDraggerCallback();
        this.mTopDrawerDragger = ViewDragHelper.create(this, 1.0f, this.mTopDrawerDraggerCallback);
        this.mTopDrawerDragger.setEdgeTrackingEnabled(4);
        this.mBottomDrawerDraggerCallback = new BottomDrawerDraggerCallback();
        this.mBottomDrawerDragger = ViewDragHelper.create(this, 1.0f, this.mBottomDrawerDraggerCallback);
        this.mBottomDrawerDragger.setEdgeTrackingEnabled(8);
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        this.mNestedScrollSlopPx = Math.round(metrics.density * 5.0f);
        this.mIsAccessibilityEnabled = ((AccessibilityManager) context.getSystemService("accessibility")).isEnabled();
    }

    @VisibleForTesting
    WearableDrawerLayout(Context context, FlingWatcher flingWatcher, @Nullable WearableDrawerView topDrawerView, @Nullable WearableDrawerView bottomDrawerView, ViewDragHelper topDrawerDragger, ViewDragHelper bottomDrawerDragger, boolean isAccessibilityEnabled) {
        super(context);
        this.mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        this.mMainThreadHandler = new Handler(Looper.getMainLooper());
        this.mCloseTopPeekRunnable = new ClosePeekRunnable(48);
        this.mCloseBottomPeekRunnable = new ClosePeekRunnable(80);
        this.mFlingWatcher = flingWatcher;
        this.mTopDrawerDragger = topDrawerDragger;
        this.mBottomDrawerDragger = bottomDrawerDragger;
        this.mTopDrawerView = topDrawerView;
        this.mBottomDrawerView = bottomDrawerView;
        this.mTopDrawerDraggerCallback = new TopDrawerDraggerCallback();
        this.mBottomDrawerDraggerCallback = new BottomDrawerDraggerCallback();
        this.mNestedScrollSlopPx = 5;
        this.mIsAccessibilityEnabled = isAccessibilityEnabled;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mTopDrawerDragger.refreshEdgeSize();
        this.mBottomDrawerDragger.refreshEdgeSize();
    }

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        this.mSystemWindowInsetBottom = insets.getSystemWindowInsetBottom();
        if (this.mSystemWindowInsetBottom != 0) {
            MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
            layoutParams.bottomMargin = this.mSystemWindowInsetBottom;
            setLayoutParams(layoutParams);
        }
        return super.onApplyWindowInsets(insets);
    }

    @VisibleForTesting
    void closeDrawerDelayed(int gravity, long delayMs) {
        if (gravity == 48) {
            this.mMainThreadHandler.removeCallbacks(this.mCloseTopPeekRunnable);
            this.mMainThreadHandler.postDelayed(this.mCloseTopPeekRunnable, delayMs);
        } else if (gravity != 80) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder(67);
            stringBuilder.append("Invoked a delayed drawer close with an invalid gravity: ");
            stringBuilder.append(gravity);
            Log.w(str, stringBuilder.toString());
        } else {
            this.mMainThreadHandler.removeCallbacks(this.mCloseBottomPeekRunnable);
            this.mMainThreadHandler.postDelayed(this.mCloseBottomPeekRunnable, delayMs);
        }
    }

    public void closeDrawer(int gravity) {
        closeDrawer(findDrawerWithGravity(gravity));
    }

    public void closeDrawer(View drawer) {
        if (drawer != null) {
            View view = this.mTopDrawerView;
            if (drawer == view) {
                this.mTopDrawerDragger.smoothSlideViewTo(view, 0, -view.getHeight());
                invalidate();
            } else {
                view = this.mBottomDrawerView;
                if (drawer == view) {
                    this.mBottomDrawerDragger.smoothSlideViewTo(view, 0, getHeight());
                    invalidate();
                } else {
                    Log.w(TAG, "closeDrawer(View) should be passed in the top or bottom drawer");
                }
            }
        }
    }

    public void openDrawer(int gravity) {
        if (isLaidOut()) {
            openDrawer(findDrawerWithGravity(gravity));
            return;
        }
        if (gravity == 48) {
            this.mShouldOpenTopDrawerAfterLayout = true;
        } else if (gravity == 80) {
            this.mShouldOpenBottomDrawerAfterLayout = true;
        }
    }

    public void openDrawer(View drawer) {
        if (drawer != null) {
            if (isLaidOut()) {
                View view = this.mTopDrawerView;
                if (drawer == view) {
                    this.mTopDrawerDragger.smoothSlideViewTo(view, 0, 0);
                    showDrawerContentMaybeAnimate(this.mTopDrawerView);
                    invalidate();
                } else {
                    view = this.mBottomDrawerView;
                    if (drawer == view) {
                        this.mBottomDrawerDragger.smoothSlideViewTo(view, 0, getHeight() - this.mBottomDrawerView.getHeight());
                        showDrawerContentMaybeAnimate(this.mBottomDrawerView);
                        invalidate();
                    } else {
                        Log.w(TAG, "openDrawer(View) should be passed in the top or bottom drawer");
                    }
                }
                return;
            }
            if (drawer == this.mTopDrawerView) {
                this.mShouldOpenTopDrawerAfterLayout = true;
            } else if (drawer == this.mBottomDrawerView) {
                this.mShouldOpenBottomDrawerAfterLayout = true;
            }
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        WearableDrawerView wearableDrawerView = this.mBottomDrawerView;
        boolean z = false;
        if (wearableDrawerView != null && wearableDrawerView.isOpened()) {
            if (!this.mCanBottomDrawerBeClosed) {
                this.mDrawerOpenLastInterceptedTouchEvent = ev;
                return false;
            }
        }
        wearableDrawerView = this.mTopDrawerView;
        if (wearableDrawerView != null) {
            if (!wearableDrawerView.isOpened() || this.mCanTopDrawerBeClosed) {
            }
            this.mDrawerOpenLastInterceptedTouchEvent = ev;
            return false;
        }
        boolean shouldInterceptTop = this.mTopDrawerDragger.shouldInterceptTouchEvent(ev);
        boolean shouldInterceptBottom = this.mBottomDrawerDragger.shouldInterceptTouchEvent(ev);
        if (!shouldInterceptTop) {
            if (!shouldInterceptBottom) {
                return z;
            }
        }
        z = true;
        return z;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (ev == null) {
            Log.w(TAG, "null MotionEvent passed to onTouchEvent");
            return false;
        }
        this.mTopDrawerDragger.processTouchEvent(ev);
        this.mBottomDrawerDragger.processTouchEvent(ev);
        return true;
    }

    public void computeScroll() {
        boolean topSettling = this.mTopDrawerDragger.continueSettling(true);
        boolean bottomSettling = this.mBottomDrawerDragger.continueSettling(true);
        if (!topSettling) {
            if (!bottomSettling) {
                return;
            }
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void addView(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
        if (child instanceof WearableDrawerView) {
            WearableDrawerView drawerView;
            WearableDrawerView drawerChild = (WearableDrawerView) child;
            int childGravity = ((FrameLayout.LayoutParams) params).gravity;
            if (childGravity != 0) {
                if (childGravity != -1) {
                    if (childGravity == 48) {
                        this.mTopDrawerView = drawerChild;
                        drawerView = this.mTopDrawerView;
                    } else if (childGravity != 80) {
                        this.mBottomDrawerView = drawerChild;
                        drawerView = this.mBottomDrawerView;
                    } else {
                        drawerView = null;
                    }
                    if (drawerView != null) {
                        drawerView.addOnLayoutChangeListener(this);
                    }
                }
            }
            ((FrameLayout.LayoutParams) params).gravity = drawerChild.preferGravity();
            childGravity = drawerChild.preferGravity();
            drawerChild.setLayoutParams(params);
            if (childGravity == 48) {
                this.mTopDrawerView = drawerChild;
                drawerView = this.mTopDrawerView;
            } else if (childGravity != 80) {
                drawerView = null;
            } else {
                this.mBottomDrawerView = drawerChild;
                drawerView = this.mBottomDrawerView;
            }
            if (drawerView != null) {
                drawerView.addOnLayoutChangeListener(this);
            }
        }
    }

    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        float openedPercent = this.mTopDrawerView;
        int height;
        int childTop;
        if (v == openedPercent) {
            openedPercent = openedPercent.getOpenedPercent();
            height = v.getHeight();
            childTop = (-height) + ((int) (((float) height) * openedPercent));
            v.layout(v.getLeft(), childTop, v.getRight(), childTop + height);
        } else {
            View view = this.mBottomDrawerView;
            if (v == view) {
                openedPercent = view.getOpenedPercent();
                height = v.getHeight();
                childTop = (int) (((float) getHeight()) - (((float) height) * openedPercent));
                v.layout(v.getLeft(), childTop, v.getRight(), childTop + height);
            }
        }
    }

    public void setDrawerStateCallback(DrawerStateCallback callback) {
        this.mDrawerStateCallback = callback;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!(this.mShouldPeekBottomDrawerAfterLayout || this.mShouldPeekTopDrawerAfterLayout || this.mShouldOpenTopDrawerAfterLayout)) {
            if (!this.mShouldOpenBottomDrawerAfterLayout) {
                return;
            }
        }
        getViewTreeObserver().addOnGlobalLayoutListener(new C04661());
    }

    public void peekDrawer(int gravity) {
        if (isLaidOut()) {
            maybePeekDrawer(findDrawerWithGravity(gravity));
            return;
        }
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "WearableDrawerLayout not laid out yet. Postponing peek.");
        }
        if (gravity == 48) {
            this.mShouldPeekTopDrawerAfterLayout = true;
        } else if (gravity == 80) {
            this.mShouldPeekBottomDrawerAfterLayout = true;
        }
    }

    public void peekDrawer(WearableDrawerView drawer) {
        if (drawer != null) {
            if (drawer != this.mTopDrawerView) {
                if (drawer != this.mBottomDrawerView) {
                    throw new IllegalArgumentException("peekDrawer(View) received a drawer that isn't a child.");
                }
            }
            if (isLaidOut()) {
                maybePeekDrawer(drawer);
                return;
            }
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "WearableDrawerLayout not laid out yet. Postponing peek.");
            }
            if (drawer == this.mTopDrawerView) {
                this.mShouldPeekTopDrawerAfterLayout = true;
            } else if (drawer == this.mBottomDrawerView) {
                this.mShouldPeekBottomDrawerAfterLayout = true;
            }
            return;
        }
        throw new IllegalArgumentException("peekDrawer(View) received a null drawer.");
    }

    public void onFlingComplete(View view) {
        WearableDrawerView wearableDrawerView = this.mTopDrawerView;
        boolean canBottomPeek = false;
        boolean canTopPeek = wearableDrawerView != null && wearableDrawerView.canAutoPeek();
        WearableDrawerView wearableDrawerView2 = this.mBottomDrawerView;
        if (wearableDrawerView2 != null && wearableDrawerView2.canAutoPeek()) {
            canBottomPeek = true;
        }
        boolean canScrollUp = view.canScrollVertically(true);
        boolean canScrollDown = view.canScrollVertically(true);
        if (canTopPeek && !canScrollUp && !this.mTopDrawerView.isPeeking()) {
            peekDrawer(48);
        }
        if (canBottomPeek && ((!canScrollUp || !canScrollDown) && !this.mBottomDrawerView.isPeeking())) {
            peekDrawer(80);
        }
    }

    public int getNestedScrollAxes() {
        return this.mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        maybeUpdateScrollingContentView(target);
        this.mLastScrollWasFling = true;
        View view = this.mScrollingContentView;
        if (target == view) {
            this.mFlingWatcher.start(view);
        }
        return false;
    }

    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        maybeUpdateScrollingContentView(target);
    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        WearableDrawerLayout wearableDrawerLayout = this;
        boolean z = true;
        boolean scrolledUp = dyConsumed < 0;
        boolean scrolledDown = dyConsumed > 0;
        boolean overScrolledUp = dyUnconsumed < 0;
        boolean overScrolledDown = dyUnconsumed > 0;
        WearableDrawerView wearableDrawerView = wearableDrawerLayout.mTopDrawerView;
        if (wearableDrawerView == null || !wearableDrawerView.isOpened()) {
            wearableDrawerView = wearableDrawerLayout.mBottomDrawerView;
            if (wearableDrawerView == null || !wearableDrawerView.isOpened()) {
                boolean shouldPeekOnScrollDown;
                wearableDrawerLayout.mLastScrollWasFling = false;
                wearableDrawerView = wearableDrawerLayout.mTopDrawerView;
                boolean canTopAutoPeek = wearableDrawerView != null && wearableDrawerView.canAutoPeek();
                WearableDrawerView wearableDrawerView2 = wearableDrawerLayout.mBottomDrawerView;
                boolean canBottomAutoPeek = wearableDrawerView2 != null && wearableDrawerView2.canAutoPeek();
                WearableDrawerView wearableDrawerView3 = wearableDrawerLayout.mTopDrawerView;
                boolean isTopDrawerPeeking = wearableDrawerView3 != null && wearableDrawerView3.isPeeking();
                WearableDrawerView wearableDrawerView4 = wearableDrawerLayout.mBottomDrawerView;
                boolean isBottomDrawerPeeking = wearableDrawerView4 != null && wearableDrawerView4.isPeeking();
                boolean scrolledDownPastSlop = false;
                WearableDrawerView wearableDrawerView5 = wearableDrawerLayout.mBottomDrawerView;
                if (wearableDrawerView5 != null) {
                    if (wearableDrawerView5.shouldPeekOnScrollDown()) {
                        shouldPeekOnScrollDown = true;
                        if (scrolledDown) {
                            wearableDrawerLayout.mCurrentNestedScrollSlopTracker += dyConsumed;
                            if (wearableDrawerLayout.mCurrentNestedScrollSlopTracker > wearableDrawerLayout.mNestedScrollSlopPx) {
                                z = false;
                            }
                            scrolledDownPastSlop = z;
                        }
                        if (canTopAutoPeek) {
                            if (!overScrolledUp && !isTopDrawerPeeking) {
                                peekDrawer(48);
                            } else if (!scrolledDown && isTopDrawerPeeking && !isClosingPeek(wearableDrawerLayout.mTopDrawerView)) {
                                closeDrawer(48);
                            }
                        }
                        if (canBottomAutoPeek) {
                            if ((!overScrolledDown || overScrolledUp) && !isBottomDrawerPeeking) {
                                peekDrawer(80);
                            } else if (shouldPeekOnScrollDown && scrolledDownPastSlop && !isBottomDrawerPeeking) {
                                peekDrawer(80);
                            } else if ((scrolledUp || (!shouldPeekOnScrollDown && scrolledDown)) && isBottomDrawerPeeking) {
                                if (!isClosingPeek(wearableDrawerLayout.mBottomDrawerView)) {
                                    closeDrawer(wearableDrawerLayout.mBottomDrawerView);
                                }
                            }
                        }
                        return;
                    }
                }
                shouldPeekOnScrollDown = false;
                if (scrolledDown) {
                    wearableDrawerLayout.mCurrentNestedScrollSlopTracker += dyConsumed;
                    if (wearableDrawerLayout.mCurrentNestedScrollSlopTracker > wearableDrawerLayout.mNestedScrollSlopPx) {
                        z = false;
                    }
                    scrolledDownPastSlop = z;
                }
                if (canTopAutoPeek) {
                    if (!overScrolledUp) {
                    }
                    if (!scrolledDown) {
                    }
                }
                if (canBottomAutoPeek) {
                    if (overScrolledDown) {
                    }
                    peekDrawer(80);
                }
                return;
            }
            wearableDrawerLayout.mCanBottomDrawerBeClosed = overScrolledUp;
            if (wearableDrawerLayout.mCanBottomDrawerBeClosed && wearableDrawerLayout.mLastScrollWasFling) {
                onTouchEvent(wearableDrawerLayout.mDrawerOpenLastInterceptedTouchEvent);
            }
            wearableDrawerLayout.mLastScrollWasFling = false;
            return;
        }
        if (!overScrolledDown) {
            if (wearableDrawerLayout.mTopDrawerView.getDrawerContent().canScrollVertically(1)) {
                z = false;
                wearableDrawerLayout.mCanTopDrawerBeClosed = z;
                if (!wearableDrawerLayout.mCanTopDrawerBeClosed && wearableDrawerLayout.mLastScrollWasFling) {
                    onTouchEvent(wearableDrawerLayout.mDrawerOpenLastInterceptedTouchEvent);
                }
                wearableDrawerLayout.mLastScrollWasFling = false;
            }
        }
        wearableDrawerLayout.mCanTopDrawerBeClosed = z;
        if (!wearableDrawerLayout.mCanTopDrawerBeClosed) {
        }
        wearableDrawerLayout.mLastScrollWasFling = false;
    }

    private void maybePeekDrawer(WearableDrawerView drawerView) {
        if (drawerView != null) {
            View peekView = drawerView.getPeekContainer();
            if (peekView != null) {
                View drawerContent = drawerView.getDrawerContent();
                int layoutGravity = ((FrameLayout.LayoutParams) drawerView.getLayoutParams()).gravity;
                int gravity = layoutGravity == 0 ? drawerView.preferGravity() : layoutGravity;
                drawerView.setIsPeeking(true);
                peekView.setAlpha(1.0f);
                peekView.setScaleX(1.0f);
                peekView.setScaleY(1.0f);
                peekView.setVisibility(0);
                if (drawerContent != null) {
                    drawerContent.setAlpha(0.0f);
                    drawerContent.setVisibility(8);
                }
                if (gravity == 80) {
                    this.mBottomDrawerDragger.smoothSlideViewTo(drawerView, 0, getHeight() - peekView.getHeight());
                } else if (gravity == 48) {
                    this.mTopDrawerDragger.smoothSlideViewTo(drawerView, 0, -(drawerView.getHeight() - peekView.getHeight()));
                    if (!this.mIsAccessibilityEnabled) {
                        closeDrawerDelayed(gravity, 1000);
                    }
                }
                invalidate();
            }
        }
    }

    private void openDrawerWithoutAnimation(WearableDrawerView drawer) {
        if (drawer != null) {
            int offset;
            WearableDrawerView wearableDrawerView = this.mTopDrawerView;
            if (drawer == wearableDrawerView) {
                offset = wearableDrawerView.getHeight();
            } else {
                wearableDrawerView = this.mBottomDrawerView;
                if (drawer == wearableDrawerView) {
                    offset = -wearableDrawerView.getHeight();
                } else {
                    Log.w(TAG, "openDrawer(View) should be passed in the top or bottom drawer");
                    return;
                }
            }
            drawer.offsetTopAndBottom(offset);
            drawer.setOpenedPercent(1.0f);
            drawer.onDrawerOpened();
            DrawerStateCallback drawerStateCallback = this.mDrawerStateCallback;
            if (drawerStateCallback != null) {
                drawerStateCallback.onDrawerOpened(drawer);
            }
            showDrawerContentMaybeAnimate(drawer);
            invalidate();
        }
    }

    private static void animatePeekVisibleAfterBeingClosed(WearableDrawerView drawer) {
        View content = drawer.getDrawerContent();
        if (content != null) {
            content.animate().setDuration(150).alpha(0.0f).withEndAction(new C04672(content)).start();
        }
        ViewGroup peek = drawer.getPeekContainer();
        peek.setVisibility(0);
        peek.animate().setStartDelay(150).setDuration(150).alpha(1.0f).scaleX(1.0f).scaleY(1.0f).start();
        drawer.setIsPeeking(true);
    }

    @Nullable
    private WearableDrawerView findDrawerWithGravity(int gravity) {
        if (gravity == 48) {
            return this.mTopDrawerView;
        }
        if (gravity == 80) {
            return this.mBottomDrawerView;
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder(35);
        stringBuilder.append("Invalid drawer gravity: ");
        stringBuilder.append(gravity);
        Log.w(str, stringBuilder.toString());
        return null;
    }

    private void maybeUpdateScrollingContentView(View view) {
        if (view != this.mScrollingContentView && !isDrawerOrChildOfDrawer(view)) {
            this.mScrollingContentView = view;
        }
    }

    private boolean isDrawerOrChildOfDrawer(View view) {
        while (view != null && view != this) {
            if (view instanceof WearableDrawerView) {
                return true;
            }
            view = (View) view.getParent();
        }
        return false;
    }

    private boolean isClosingPeek(WearableDrawerView drawerView) {
        return drawerView != null && drawerView.getDrawerState() == 2;
    }

    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        this.mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        this.mCurrentNestedScrollSlopTracker = 0;
        return true;
    }

    public void onStopNestedScroll(View target) {
        this.mNestedScrollingParentHelper.onStopNestedScroll(target);
    }

    private boolean canDrawerContentScrollVertically(@Nullable WearableDrawerView drawerView, int direction) {
        if (drawerView == null) {
            return false;
        }
        View drawerContent = drawerView.getDrawerContent();
        if (drawerContent == null) {
            return false;
        }
        return drawerContent.canScrollVertically(direction);
    }

    private static void showDrawerContentMaybeAnimate(WearableDrawerView drawerView) {
        drawerView.bringToFront();
        View contentView = drawerView.getDrawerContent();
        if (contentView != null) {
            contentView.setVisibility(0);
        }
        if (drawerView.isPeeking()) {
            drawerView.getPeekContainer().animate().alpha(0.0f).scaleX(0.0f).scaleY(0.0f).setDuration(150).start();
            if (contentView != null) {
                contentView.setAlpha(0.0f);
                contentView.animate().setStartDelay(150).alpha(1.0f).setDuration(150).start();
            }
            return;
        }
        drawerView.getPeekContainer().setAlpha(0.0f);
        if (contentView != null) {
            contentView.setAlpha(1.0f);
        }
    }
}
