package android.support.wearable.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.Recycler;
import android.support.v7.widget.RecyclerView.State;
import android.support.wearable.input.RotaryEncoder;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewConfiguration;
import android.widget.Scroller;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@TargetApi(20)
@Deprecated
public class WearableListView extends RecyclerView {
    private static final float BOTTOM_TAP_REGION_PERCENTAGE = 0.33f;
    private static final long CENTERING_ANIMATION_DURATION_MS = 150;
    private static final String TAG = "WearableListView";
    private static final int THIRD = 3;
    private static final float TOP_TAP_REGION_PERCENTAGE = 0.33f;
    private boolean mCanClick;
    private ClickListener mClickListener;
    private boolean mGestureDirectionLocked;
    private boolean mGestureNavigationEnabled;
    private boolean mGreedyTouchMode;
    private int mInitialOffset;
    private int mLastScrollChange;
    private final int[] mLocation;
    private final int mMaxFlingVelocity;
    private boolean mMaximizeSingleItem;
    private final int mMinFlingVelocity;
    private final Runnable mNotifyChildrenPostLayoutRunnable;
    private final OnChangeObserver mObserver;
    private final List<OnCentralPositionChangedListener> mOnCentralPositionChangedListeners;
    private final List<OnScrollListener> mOnScrollListeners;
    private OnOverScrollListener mOverScrollListener;
    private boolean mPossibleVerticalSwipe;
    private final Runnable mPressedRunnable;
    private View mPressedView;
    private int mPreviousBaseline;
    private int mPreviousCentral;
    private final Runnable mReleasedRunnable;
    private Animator mScrollAnimator;
    private Scroller mScroller;
    private final SetScrollVerticallyProperty mSetScrollVerticallyProperty;
    private float mStartFirstTop;
    private float mStartX;
    private float mStartY;
    private int mTapPositionX;
    private int mTapPositionY;
    private final float[] mTapRegions;
    private final int mTouchSlop;

    /* renamed from: android.support.wearable.view.WearableListView$1 */
    class C04581 implements Runnable {
        C04581() {
        }

        public void run() {
            if (WearableListView.this.getChildCount() > 0) {
                WearableListView wearableListView = WearableListView.this;
                wearableListView.mPressedView = wearableListView.getChildAt(wearableListView.findCenterViewIndex());
                WearableListView.this.mPressedView.setPressed(true);
                return;
            }
            Log.w(WearableListView.TAG, "mPressedRunnable: the children were removed, skipping.");
        }
    }

    /* renamed from: android.support.wearable.view.WearableListView$2 */
    class C04592 implements Runnable {
        C04592() {
        }

        public void run() {
            WearableListView.this.releasePressedItem();
        }
    }

    /* renamed from: android.support.wearable.view.WearableListView$3 */
    class C04603 implements Runnable {
        C04603() {
        }

        public void run() {
            WearableListView.this.notifyChildrenAboutProximity(false);
        }
    }

    public interface ClickListener {
        void onClick(ViewHolder viewHolder);

        void onTopEmptyRegionClick();
    }

    public interface OnCenterProximityListener {
        void onCenterPosition(boolean z);

        void onNonCenterPosition(boolean z);
    }

    public interface OnCentralPositionChangedListener {
        void onCentralPositionChanged(int i);
    }

    public interface OnOverScrollListener {
        void onOverScroll();
    }

    public interface OnScrollListener {
        @Deprecated
        void onAbsoluteScrollChange(int i);

        void onCentralPositionChanged(int i);

        void onScroll(int i);

        void onScrollStateChanged(int i);
    }

    private static class SetScrollVerticallyProperty extends Property<WearableListView, Integer> {
        public SetScrollVerticallyProperty() {
            super(Integer.class, "scrollVertically");
        }

        public Integer get(WearableListView wearableListView) {
            return Integer.valueOf(wearableListView.mLastScrollChange);
        }

        public void set(WearableListView wearableListView, Integer value) {
            wearableListView.setScrollVertically(value.intValue());
        }
    }

    /* renamed from: android.support.wearable.view.WearableListView$4 */
    class C09174 extends android.support.v7.widget.RecyclerView.OnScrollListener {
        C09174() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 0 && WearableListView.this.getChildCount() > 0) {
                WearableListView.this.handleTouchUp(null, newState);
            }
            for (OnScrollListener listener : WearableListView.this.mOnScrollListeners) {
                listener.onScrollStateChanged(newState);
            }
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            WearableListView.this.onScroll(dy);
        }
    }

    /* renamed from: android.support.wearable.view.WearableListView$5 */
    class C09185 extends SimpleAnimatorListener {
        C09185() {
        }

        public void onAnimationEnd(Animator animator) {
            if (!wasCanceled()) {
                WearableListView.this.mCanClick = true;
            }
        }
    }

    @Deprecated
    public static abstract class Adapter extends android.support.v7.widget.RecyclerView.Adapter<ViewHolder> {
    }

    public static abstract class GenericAdapter<T extends ViewHolder> extends android.support.v7.widget.RecyclerView.Adapter<T> {
    }

    private class LayoutManager extends android.support.v7.widget.RecyclerView.LayoutManager {
        private int mAbsoluteScroll;
        private android.support.v7.widget.RecyclerView.SmoothScroller mDefaultSmoothScroller;
        private int mFirstPosition;
        private boolean mPushFirstHigher;
        private android.support.v7.widget.RecyclerView.SmoothScroller mSmoothScroller;
        private boolean mUseOldViewTop;
        private boolean mWasZoomedIn;

        private int findCenterViewIndex() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x0041 in {4, 5, 6, 9, 11} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r8 = this;
            r0 = r8.getChildCount();
            r1 = -1;
            r2 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
            r3 = android.support.wearable.view.WearableListView.this;
            r3 = android.support.wearable.view.WearableListView.getCenterYPos(r3);
            r4 = 0;
        L_0x000f:
            if (r4 >= r0) goto L_0x0035;
        L_0x0011:
            r5 = android.support.wearable.view.WearableListView.this;
            r5 = r5.getLayoutManager();
            r5 = r5.getChildAt(r4);
            r6 = android.support.wearable.view.WearableListView.this;
            r6 = r6.getTop();
            r7 = android.support.wearable.view.WearableListView.getCenterYPos(r5);
            r6 = r6 + r7;
            r7 = r3 - r6;
            r7 = java.lang.Math.abs(r7);
            if (r7 >= r2) goto L_0x0031;
        L_0x002e:
            r2 = r7;
            r1 = r4;
            goto L_0x0032;
        L_0x0032:
            r4 = r4 + 1;
            goto L_0x000f;
        L_0x0035:
            r4 = -1;
            if (r1 == r4) goto L_0x0039;
        L_0x0038:
            return r1;
        L_0x0039:
            r4 = new java.lang.IllegalStateException;
            r5 = "Can't find central view.";
            r4.<init>(r5);
            throw r4;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.wearable.view.WearableListView.LayoutManager.findCenterViewIndex():int");
        }

        private LayoutManager() {
            this.mUseOldViewTop = true;
            this.mWasZoomedIn = null;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onLayoutChildren(android.support.v7.widget.RecyclerView.Recycler r11, android.support.v7.widget.RecyclerView.State r12) {
            /*
            r10 = this;
            r0 = r10.getHeight();
            r1 = r10.getPaddingBottom();
            r0 = r0 - r1;
            r1 = android.support.wearable.view.WearableListView.this;
            r1 = r1.getCentralViewTop();
            r2 = android.support.wearable.view.WearableListView.this;
            r2 = r2.mInitialOffset;
            r1 = r1 + r2;
            r2 = r10.mUseOldViewTop;
            r3 = 0;
            if (r2 == 0) goto L_0x00b8;
        L_0x001b:
            r2 = r10.getChildCount();
            if (r2 <= 0) goto L_0x00b8;
        L_0x0021:
            r2 = r10.findCenterViewIndex();
            r4 = r10.getChildAt(r2);
            r4 = r10.getPosition(r4);
            r5 = -1;
            if (r4 != r5) goto L_0x0066;
        L_0x0030:
            r6 = 0;
            r7 = r10.getChildCount();
        L_0x0035:
            r8 = r2 + r6;
            if (r8 < r7) goto L_0x003f;
        L_0x0039:
            r8 = r2 - r6;
            if (r8 < 0) goto L_0x003e;
        L_0x003d:
            goto L_0x003f;
        L_0x003e:
            goto L_0x0067;
        L_0x003f:
            r8 = r2 + r6;
            r8 = r10.getChildAt(r8);
            if (r8 == 0) goto L_0x0050;
        L_0x0047:
            r4 = r10.getPosition(r8);
            if (r4 == r5) goto L_0x004f;
        L_0x004d:
            r2 = r2 + r6;
            goto L_0x0067;
        L_0x004f:
            goto L_0x0051;
        L_0x0051:
            r9 = r2 - r6;
            r8 = r10.getChildAt(r9);
            if (r8 == 0) goto L_0x0062;
        L_0x0059:
            r4 = r10.getPosition(r8);
            if (r4 == r5) goto L_0x0061;
        L_0x005f:
            r2 = r2 - r6;
            goto L_0x0067;
        L_0x0061:
            goto L_0x0063;
        L_0x0063:
            r6 = r6 + 1;
            goto L_0x0035;
        L_0x0067:
            if (r4 != r5) goto L_0x0082;
        L_0x0069:
            r5 = r10.getChildAt(r3);
            r1 = r5.getTop();
            r5 = r12.getItemCount();
        L_0x0075:
            r6 = r10.mFirstPosition;
            if (r6 < r5) goto L_0x0080;
        L_0x0079:
            if (r6 <= 0) goto L_0x0080;
        L_0x007b:
            r6 = r6 + -1;
            r10.mFirstPosition = r6;
            goto L_0x0075;
            goto L_0x00cc;
        L_0x0082:
            r5 = r10.mWasZoomedIn;
            if (r5 != 0) goto L_0x008f;
        L_0x0086:
            r5 = r10.getChildAt(r2);
            r1 = r5.getTop();
            goto L_0x0090;
        L_0x0090:
            r5 = r10.getPaddingTop();
            if (r1 <= r5) goto L_0x00a2;
        L_0x0096:
            if (r4 <= 0) goto L_0x00a2;
        L_0x0098:
            r4 = r4 + -1;
            r5 = android.support.wearable.view.WearableListView.this;
            r5 = r5.getItemHeight();
            r1 = r1 - r5;
            goto L_0x0090;
            if (r4 != 0) goto L_0x00b4;
        L_0x00a5:
            r5 = android.support.wearable.view.WearableListView.this;
            r5 = r5.getCentralViewTop();
            if (r1 <= r5) goto L_0x00b4;
        L_0x00ad:
            r5 = android.support.wearable.view.WearableListView.this;
            r1 = r5.getCentralViewTop();
            goto L_0x00b5;
        L_0x00b5:
            r10.mFirstPosition = r4;
            goto L_0x00cc;
            r2 = r10.mPushFirstHigher;
            if (r2 == 0) goto L_0x00cc;
        L_0x00bd:
            r2 = android.support.wearable.view.WearableListView.this;
            r2 = r2.getCentralViewTop();
            r4 = android.support.wearable.view.WearableListView.this;
            r4 = r4.getItemHeight();
            r1 = r2 - r4;
            goto L_0x00cd;
        L_0x00cd:
            r10.performLayoutChildren(r11, r12, r0, r1);
            r2 = r10.getChildCount();
            if (r2 != 0) goto L_0x00da;
        L_0x00d6:
            r10.setAbsoluteScroll(r3);
            goto L_0x00fe;
        L_0x00da:
            r2 = r10.findCenterViewIndex();
            r2 = r10.getChildAt(r2);
            r4 = r2.getTop();
            r5 = android.support.wearable.view.WearableListView.this;
            r5 = r5.getCentralViewTop();
            r4 = r4 - r5;
            r5 = r10.getPosition(r2);
            r6 = android.support.wearable.view.WearableListView.this;
            r6 = r6.getItemHeight();
            r5 = r5 * r6;
            r4 = r4 + r5;
            r10.setAbsoluteScroll(r4);
        L_0x00fe:
            r2 = 1;
            r10.mUseOldViewTop = r2;
            r10.mPushFirstHigher = r3;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.wearable.view.WearableListView.LayoutManager.onLayoutChildren(android.support.v7.widget.RecyclerView$Recycler, android.support.v7.widget.RecyclerView$State):void");
        }

        private void performLayoutChildren(Recycler recycler, State state, int parentBottom, int top) {
            detachAndScrapAttachedViews(recycler);
            if (WearableListView.this.mMaximizeSingleItem && state.getItemCount() == 1) {
                performLayoutOneChild(recycler, parentBottom);
                this.mWasZoomedIn = true;
            } else {
                performLayoutMultipleChildren(recycler, state, parentBottom, top);
                this.mWasZoomedIn = false;
            }
            if (getChildCount() > 0) {
                WearableListView wearableListView = WearableListView.this;
                wearableListView.post(wearableListView.mNotifyChildrenPostLayoutRunnable);
            }
        }

        private void performLayoutOneChild(Recycler recycler, int parentBottom) {
            int right = getWidth() - getPaddingRight();
            View v = recycler.getViewForPosition(getFirstPosition());
            addView(v, 0);
            measureZoomView(v);
            v.layout(getPaddingLeft(), getPaddingTop(), right, parentBottom);
        }

        private void performLayoutMultipleChildren(Recycler recycler, State state, int parentBottom, int top) {
            int left = getPaddingLeft();
            int right = getWidth() - getPaddingRight();
            int count = state.getItemCount();
            int i = 0;
            while (getFirstPosition() + i < count) {
                if (top < parentBottom) {
                    View v = recycler.getViewForPosition(getFirstPosition() + i);
                    addView(v, i);
                    measureThirdView(v);
                    int bottom = WearableListView.this.getItemHeight() + top;
                    v.layout(left, top, right, bottom);
                    i++;
                    top = bottom;
                } else {
                    return;
                }
            }
        }

        private void setAbsoluteScroll(int absoluteScroll) {
            this.mAbsoluteScroll = absoluteScroll;
            for (OnScrollListener listener : WearableListView.this.mOnScrollListeners) {
                listener.onAbsoluteScrollChange(this.mAbsoluteScroll);
            }
        }

        private void measureView(View v, int height) {
            LayoutParams lp = (LayoutParams) v.getLayoutParams();
            v.measure(android.support.v7.widget.RecyclerView.LayoutManager.getChildMeasureSpec(getWidth(), ((getPaddingLeft() + getPaddingRight()) + lp.leftMargin) + lp.rightMargin, lp.width, canScrollHorizontally()), android.support.v7.widget.RecyclerView.LayoutManager.getChildMeasureSpec(getHeight(), ((getPaddingTop() + getPaddingBottom()) + lp.topMargin) + lp.bottomMargin, height, canScrollVertically()));
        }

        private void measureThirdView(View v) {
            measureView(v, (getHeight() / 3) + 1);
        }

        private void measureZoomView(View v) {
            measureView(v, getHeight());
        }

        public LayoutParams generateDefaultLayoutParams() {
            return new LayoutParams(-1, -2);
        }

        public boolean canScrollVertically() {
            if (getItemCount() == 1) {
                return !this.mWasZoomedIn;
            } else {
                return true;
            }
        }

        public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
            if (getChildCount() == 0) {
                return 0;
            }
            int scrolled = 0;
            int left = getPaddingLeft();
            int right = getWidth() - getPaddingRight();
            if (dy < 0) {
                while (scrolled > dy) {
                    View topView = getChildAt(0);
                    if (getFirstPosition() > 0) {
                        int scrollBy = Math.min(scrolled - dy, Math.max(-topView.getTop(), 0));
                        scrolled -= scrollBy;
                        offsetChildrenVertical(scrollBy);
                        if (getFirstPosition() <= 0 || scrolled <= dy) {
                            break;
                        }
                        this.mFirstPosition--;
                        View v = recycler.getViewForPosition(getFirstPosition());
                        addView(v, 0);
                        measureThirdView(v);
                        int bottom = topView.getTop();
                        v.layout(left, bottom - WearableListView.this.getItemHeight(), right, bottom);
                    } else {
                        this.mPushFirstHigher = false;
                        int scrollBy2 = Math.min((-dy) + scrolled, (WearableListView.this.mOverScrollListener != null ? getHeight() : WearableListView.this.getTopViewMaxTop()) - topView.getTop());
                        scrolled -= scrollBy2;
                        offsetChildrenVertical(scrollBy2);
                    }
                }
            } else if (dy > 0) {
                int parentHeight = getHeight();
                while (scrolled < dy) {
                    View bottomView = getChildAt(getChildCount() - 1);
                    if (state.getItemCount() <= this.mFirstPosition + getChildCount()) {
                        int scrollBy3 = Math.max((-dy) + scrolled, (getHeight() / 2) - bottomView.getBottom());
                        scrolled -= scrollBy3;
                        offsetChildrenVertical(scrollBy3);
                        break;
                    }
                    int scrollBy4 = -Math.min(dy - scrolled, Math.max(bottomView.getBottom() - parentHeight, 0));
                    scrolled -= scrollBy4;
                    offsetChildrenVertical(scrollBy4);
                    if (scrolled >= dy) {
                        break;
                    }
                    View v2 = recycler.getViewForPosition(this.mFirstPosition + getChildCount());
                    int top = getChildAt(getChildCount() - 1).getBottom();
                    addView(v2);
                    measureThirdView(v2);
                    v2.layout(left, top, right, WearableListView.this.getItemHeight() + top);
                }
            }
            recycleViewsOutOfBounds(recycler);
            setAbsoluteScroll(this.mAbsoluteScroll + scrolled);
            return scrolled;
        }

        public void scrollToPosition(int position) {
            this.mUseOldViewTop = false;
            if (position > 0) {
                this.mFirstPosition = position - 1;
                this.mPushFirstHigher = true;
            } else {
                this.mFirstPosition = position;
                this.mPushFirstHigher = false;
            }
            requestLayout();
        }

        public void setCustomSmoothScroller(android.support.v7.widget.RecyclerView.SmoothScroller smoothScroller) {
            this.mSmoothScroller = smoothScroller;
        }

        public void clearCustomSmoothScroller() {
            this.mSmoothScroller = null;
        }

        public android.support.v7.widget.RecyclerView.SmoothScroller getDefaultSmoothScroller(RecyclerView recyclerView) {
            if (this.mDefaultSmoothScroller == null) {
                this.mDefaultSmoothScroller = new SmoothScroller(recyclerView.getContext(), this);
            }
            return this.mDefaultSmoothScroller;
        }

        public void smoothScrollToPosition(RecyclerView recyclerView, State state, int position) {
            android.support.v7.widget.RecyclerView.SmoothScroller scroller = this.mSmoothScroller;
            if (scroller == null) {
                scroller = getDefaultSmoothScroller(recyclerView);
            }
            scroller.setTargetPosition(position);
            startSmoothScroll(scroller);
        }

        private void recycleViewsOutOfBounds(Recycler recycler) {
            int i;
            int childCount = getChildCount();
            int parentWidth = getWidth();
            int parentHeight = getHeight();
            boolean foundFirst = false;
            int first = 0;
            int last = 0;
            for (i = 0; i < childCount; i++) {
                View v = getChildAt(i);
                if (!v.hasFocus()) {
                    if (v.getRight() >= 0) {
                        if (v.getLeft() <= parentWidth) {
                            if (v.getBottom() >= 0) {
                                if (v.getTop() <= parentHeight) {
                                }
                            }
                        }
                    }
                }
                if (!foundFirst) {
                    first = i;
                    foundFirst = true;
                }
                last = i;
            }
            for (i = childCount - 1; i > last; i--) {
                removeAndRecycleViewAt(i, recycler);
            }
            for (i = first - 1; i >= 0; i--) {
                removeAndRecycleViewAt(i, recycler);
            }
            if (getChildCount() == 0) {
                this.mFirstPosition = 0;
            } else if (first > 0) {
                this.mPushFirstHigher = true;
                this.mFirstPosition += first;
            }
        }

        public int getFirstPosition() {
            return this.mFirstPosition;
        }

        public void onAdapterChanged(android.support.v7.widget.RecyclerView.Adapter oldAdapter, android.support.v7.widget.RecyclerView.Adapter newAdapter) {
            removeAllViews();
        }
    }

    private static class OnChangeObserver extends AdapterDataObserver implements OnLayoutChangeListener {
        private android.support.v7.widget.RecyclerView.Adapter mAdapter;
        private boolean mIsListeningToLayoutChange;
        private boolean mIsObservingAdapter;
        private WeakReference<WearableListView> mListView;

        private OnChangeObserver() {
        }

        public void setListView(WearableListView listView) {
            stopOnLayoutChangeListening();
            this.mListView = new WeakReference(listView);
        }

        public void setAdapter(android.support.v7.widget.RecyclerView.Adapter adapter) {
            stopDataObserving();
            this.mAdapter = adapter;
            startDataObserving();
        }

        private void startDataObserving() {
            android.support.v7.widget.RecyclerView.Adapter adapter = this.mAdapter;
            if (adapter != null) {
                adapter.registerAdapterDataObserver(this);
                this.mIsObservingAdapter = true;
            }
        }

        private void stopDataObserving() {
            stopOnLayoutChangeListening();
            if (this.mIsObservingAdapter) {
                this.mAdapter.unregisterAdapterDataObserver(this);
                this.mIsObservingAdapter = false;
            }
        }

        private void startOnLayoutChangeListening() {
            WeakReference weakReference = this.mListView;
            WearableListView listView = weakReference == null ? null : (WearableListView) weakReference.get();
            if (!this.mIsListeningToLayoutChange && listView != null) {
                listView.addOnLayoutChangeListener(this);
                this.mIsListeningToLayoutChange = true;
            }
        }

        private void stopOnLayoutChangeListening() {
            if (this.mIsListeningToLayoutChange) {
                WeakReference weakReference = this.mListView;
                WearableListView listView = weakReference == null ? null : (WearableListView) weakReference.get();
                if (listView != null) {
                    listView.removeOnLayoutChangeListener(this);
                }
                this.mIsListeningToLayoutChange = false;
            }
        }

        public void onChanged() {
            startOnLayoutChangeListening();
        }

        public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
            WearableListView listView = (WearableListView) this.mListView.get();
            if (listView != null) {
                stopOnLayoutChangeListening();
                if (listView.getChildCount() > 0) {
                    listView.animateToCenter();
                }
            }
        }
    }

    public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        protected void onCenterProximity(boolean isCentralItem, boolean animate) {
            if (this.itemView instanceof OnCenterProximityListener) {
                OnCenterProximityListener item = this.itemView;
                if (isCentralItem) {
                    item.onCenterPosition(animate);
                } else {
                    item.onNonCenterPosition(animate);
                }
            }
        }
    }

    private static class SmoothScroller extends LinearSmoothScroller {
        private static final float MILLISECONDS_PER_INCH = 100.0f;
        private final LayoutManager mLayoutManager;

        public SmoothScroller(Context context, LayoutManager manager) {
            super(context);
            this.mLayoutManager = manager;
        }

        protected void onStart() {
            super.onStart();
        }

        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return MILLISECONDS_PER_INCH / ((float) displayMetrics.densityDpi);
        }

        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return ((boxStart + boxEnd) / 2) - ((viewStart + viewEnd) / 2);
        }

        public PointF computeScrollVectorForPosition(int targetPosition) {
            if (targetPosition < this.mLayoutManager.getFirstPosition()) {
                return new PointF(0.0f, -1.0f);
            }
            return new PointF(0.0f, 1.0f);
        }
    }

    private int findCenterViewIndex() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:12:0x0037 in {4, 5, 6, 9, 11} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r8 = this;
        r0 = r8.getChildCount();
        r1 = -1;
        r2 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r3 = getCenterYPos(r8);
        r4 = 0;
    L_0x000d:
        if (r4 >= r0) goto L_0x002b;
    L_0x000f:
        r5 = r8.getChildAt(r4);
        r6 = r8.getTop();
        r7 = getCenterYPos(r5);
        r6 = r6 + r7;
        r7 = r3 - r6;
        r7 = java.lang.Math.abs(r7);
        if (r7 >= r2) goto L_0x0027;
    L_0x0024:
        r2 = r7;
        r1 = r4;
        goto L_0x0028;
    L_0x0028:
        r4 = r4 + 1;
        goto L_0x000d;
    L_0x002b:
        r4 = -1;
        if (r1 == r4) goto L_0x002f;
    L_0x002e:
        return r1;
    L_0x002f:
        r4 = new java.lang.IllegalStateException;
        r5 = "Can't find central view.";
        r4.<init>(r5);
        throw r4;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.wearable.view.WearableListView.findCenterViewIndex():int");
    }

    public WearableListView(Context context) {
        this(context, null);
    }

    public WearableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mCanClick = true;
        this.mGestureNavigationEnabled = true;
        this.mSetScrollVerticallyProperty = new SetScrollVerticallyProperty();
        this.mOnScrollListeners = new ArrayList();
        this.mOnCentralPositionChangedListeners = new ArrayList();
        this.mInitialOffset = 0;
        this.mTapRegions = new float[2];
        this.mPreviousCentral = -1;
        this.mPreviousBaseline = -1;
        this.mLocation = new int[2];
        this.mPressedView = null;
        this.mPressedRunnable = new C04581();
        this.mReleasedRunnable = new C04592();
        this.mNotifyChildrenPostLayoutRunnable = new C04603();
        this.mObserver = new OnChangeObserver();
        setHasFixedSize(true);
        setOverScrollMode(2);
        setLayoutManager(new LayoutManager());
        setOnScrollListener(new C09174());
        ViewConfiguration vc = ViewConfiguration.get(context);
        this.mTouchSlop = vc.getScaledTouchSlop();
        this.mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        this.mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
    }

    public void setAdapter(android.support.v7.widget.RecyclerView.Adapter adapter) {
        this.mObserver.setAdapter(adapter);
        super.setAdapter(adapter);
    }

    public int getBaseline() {
        if (getChildCount() == 0) {
            return super.getBaseline();
        }
        int centerChildBaseline = getChildAt(findCenterViewIndex()).getBaseline();
        if (centerChildBaseline == -1) {
            return super.getBaseline();
        }
        return getCentralViewTop() + centerChildBaseline;
    }

    public boolean isAtTop() {
        boolean z = true;
        if (getChildCount() == 0) {
            return true;
        }
        if (getChildAdapterPosition(getChildAt(findCenterViewIndex())) == 0) {
            if (getScrollState() == 0) {
                return z;
            }
        }
        z = false;
        return z;
    }

    public void resetLayoutManager() {
        setLayoutManager(new LayoutManager());
    }

    public void setGreedyTouchMode(boolean greedy) {
        this.mGreedyTouchMode = greedy;
    }

    public void setInitialOffset(int top) {
        this.mInitialOffset = top;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        if (this.mGreedyTouchMode && getChildCount() > 0) {
            int action = event.getActionMasked();
            if (action == 0) {
                this.mStartX = event.getX();
                this.mStartY = event.getY();
                this.mStartFirstTop = getChildCount() > 0 ? (float) getChildAt(0).getTop() : 0.0f;
                this.mPossibleVerticalSwipe = true;
                this.mGestureDirectionLocked = false;
            } else if (action == 2 && this.mPossibleVerticalSwipe) {
                handlePossibleVerticalSwipe(event);
            }
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(this.mPossibleVerticalSwipe);
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    private boolean handlePossibleVerticalSwipe(MotionEvent event) {
        if (this.mGestureDirectionLocked) {
            return this.mPossibleVerticalSwipe;
        }
        float deltaX = Math.abs(this.mStartX - event.getX());
        float deltaY = Math.abs(this.mStartY - event.getY());
        float distance = (deltaX * deltaX) + (deltaY * deltaY);
        int i = this.mTouchSlop;
        if (distance > ((float) (i * i))) {
            if (deltaX > deltaY) {
                this.mPossibleVerticalSwipe = false;
            }
            this.mGestureDirectionLocked = true;
        }
        return this.mPossibleVerticalSwipe;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        int scrollState = getScrollState();
        boolean result = super.onTouchEvent(event);
        if (getChildCount() > 0) {
            int action = event.getActionMasked();
            if (action == 0) {
                handleTouchDown(event);
            } else if (action == 1) {
                handleTouchUp(event, scrollState);
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            } else if (action == 2) {
                if (Math.abs(this.mTapPositionX - ((int) event.getX())) < this.mTouchSlop) {
                    if (Math.abs(this.mTapPositionY - ((int) event.getY())) < this.mTouchSlop) {
                        result |= handlePossibleVerticalSwipe(event);
                        if (getParent() != null) {
                            getParent().requestDisallowInterceptTouchEvent(this.mPossibleVerticalSwipe);
                        }
                    }
                }
                releasePressedItem();
                this.mCanClick = false;
                result |= handlePossibleVerticalSwipe(event);
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(this.mPossibleVerticalSwipe);
                }
            } else if (action == 3) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                this.mCanClick = true;
            }
        }
        return result;
    }

    public boolean onGenericMotionEvent(MotionEvent ev) {
        if (!RotaryEncoder.isFromRotaryEncoder(ev) || ev.getAction() != 8) {
            return super.onGenericMotionEvent(ev);
        }
        scrollBy(0, Math.round((-RotaryEncoder.getRotaryAxisValue(ev)) * RotaryEncoder.getScaledScrollFactor(getContext())));
        return true;
    }

    private void releasePressedItem() {
        View view = this.mPressedView;
        if (view != null) {
            view.setPressed(false);
            this.mPressedView = null;
        }
        Handler handler = getHandler();
        if (handler != null) {
            handler.removeCallbacks(this.mPressedRunnable);
        }
    }

    private void onScroll(int dy) {
        for (OnScrollListener listener : this.mOnScrollListeners) {
            listener.onScroll(dy);
        }
        notifyChildrenAboutProximity(true);
    }

    public void addOnScrollListener(OnScrollListener listener) {
        this.mOnScrollListeners.add(listener);
    }

    public void removeOnScrollListener(OnScrollListener listener) {
        this.mOnScrollListeners.remove(listener);
    }

    public void addOnCentralPositionChangedListener(OnCentralPositionChangedListener listener) {
        this.mOnCentralPositionChangedListeners.add(listener);
    }

    public void removeOnCentralPositionChangedListener(OnCentralPositionChangedListener listener) {
        this.mOnCentralPositionChangedListeners.remove(listener);
    }

    public boolean isGestureNavigationEnabled() {
        return this.mGestureNavigationEnabled;
    }

    public void setEnableGestureNavigation(boolean enabled) {
        this.mGestureNavigationEnabled = enabled;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.mGestureNavigationEnabled) {
            switch (keyCode) {
                case CallbackHandler.MSG_ROUTE_VOLUME_CHANGED /*260*/:
                    fling(0, -this.mMinFlingVelocity);
                    return true;
                case CallbackHandler.MSG_ROUTE_PRESENTATION_DISPLAY_CHANGED /*261*/:
                    fling(0, this.mMinFlingVelocity);
                    return true;
                case CallbackHandler.MSG_ROUTE_SELECTED /*262*/:
                    return tapCenterView();
                case CallbackHandler.MSG_ROUTE_UNSELECTED /*263*/:
                    return false;
                default:
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean tapCenterView() {
        if (isEnabled() && getVisibility() == 0) {
            if (getChildCount() >= 1) {
                View view = getChildAt(findCenterViewIndex());
                ViewHolder holder = getChildViewHolder(view);
                if (view.performClick()) {
                    return true;
                }
                ClickListener clickListener = this.mClickListener;
                if (clickListener == null) {
                    return false;
                }
                clickListener.onClick(holder);
                return true;
            }
        }
        return false;
    }

    private boolean checkForTap(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        float eventX = event.getX();
        float eventY = event.getY();
        int index = findCenterViewIndex();
        View view = findChildViewUnder(eventX, eventY);
        if (view == null) {
            return false;
        }
        ClickListener clickListener;
        ViewHolder holder = getChildViewHolder(view);
        computeTapRegions(this.mTapRegions);
        if (index == 0 && event.getRawY() <= this.mTapRegions[0]) {
            clickListener = this.mClickListener;
            if (clickListener != null) {
                clickListener.onTopEmptyRegionClick();
                return true;
            }
        }
        clickListener = this.mClickListener;
        if (clickListener != null) {
            clickListener.onClick(holder);
        }
        return true;
    }

    private void startScrollAnimation(int scroll, long duration, long delay, AnimatorListener listener) {
        startScrollAnimation(null, scroll, duration, delay, listener);
    }

    private void startScrollAnimation(List<Animator> animators, int scroll, long duration, long delay, AnimatorListener listener) {
        Animator animator = this.mScrollAnimator;
        if (animator != null) {
            animator.cancel();
        }
        this.mLastScrollChange = 0;
        ObjectAnimator scrollAnimator = ObjectAnimator.ofInt(this, this.mSetScrollVerticallyProperty, new int[]{0, -scroll});
        if (animators != null) {
            animators.add(scrollAnimator);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animators);
            this.mScrollAnimator = animatorSet;
        } else {
            this.mScrollAnimator = scrollAnimator;
        }
        this.mScrollAnimator.setDuration(duration);
        if (listener != null) {
            this.mScrollAnimator.addListener(listener);
        }
        if (delay > 0) {
            this.mScrollAnimator.setStartDelay(delay);
        }
        this.mScrollAnimator.start();
    }

    public boolean fling(int velocityX, int velocityY) {
        WearableListView wearableListView = this;
        int i = velocityY;
        if (getChildCount() == 0) {
            return false;
        }
        int currentPosition = getChildPosition(getChildAt(findCenterViewIndex()));
        if (currentPosition == 0) {
            if (i < 0) {
                return super.fling(velocityX, velocityY);
            }
        }
        if (currentPosition != getAdapter().getItemCount() - 1 || i <= 0) {
            if (Math.abs(velocityY) < wearableListView.mMinFlingVelocity) {
                return false;
            }
            i = Math.max(Math.min(i, wearableListView.mMaxFlingVelocity), -wearableListView.mMaxFlingVelocity);
            if (wearableListView.mScroller == null) {
                wearableListView.mScroller = new Scroller(getContext(), null, true);
            }
            wearableListView.mScroller.fling(0, 0, 0, i, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            int delta = wearableListView.mScroller.getFinalY() / (getPaddingTop() + (getAdjustedHeight() / 2));
            if (delta == 0) {
                delta = i > 0 ? 1 : -1;
            }
            smoothScrollToPosition(Math.max(0, Math.min(getAdapter().getItemCount() - 1, currentPosition + delta)));
            return true;
        }
        return super.fling(velocityX, velocityY);
    }

    public void smoothScrollToPosition(int position, android.support.v7.widget.RecyclerView.SmoothScroller smoothScroller) {
        LayoutManager layoutManager = (LayoutManager) getLayoutManager();
        layoutManager.setCustomSmoothScroller(smoothScroller);
        smoothScrollToPosition(position);
        layoutManager.clearCustomSmoothScroller();
    }

    public ViewHolder getChildViewHolder(View child) {
        return (ViewHolder) super.getChildViewHolder(child);
    }

    public void setClickListener(ClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public void setOverScrollListener(OnOverScrollListener listener) {
        this.mOverScrollListener = listener;
    }

    private static int getCenterYPos(View v) {
        return (v.getTop() + v.getPaddingTop()) + (getAdjustedHeight(v) / 2);
    }

    private void handleTouchUp(MotionEvent event, int scrollState) {
        if (this.mCanClick && event != null && checkForTap(event)) {
            Handler handler = getHandler();
            if (handler != null) {
                handler.postDelayed(this.mReleasedRunnable, (long) ViewConfiguration.getTapTimeout());
            }
        } else if (scrollState == 0) {
            if (isOverScrolling()) {
                this.mOverScrollListener.onOverScroll();
            } else {
                animateToCenter();
            }
        }
    }

    private boolean isOverScrolling() {
        if (getChildCount() > 0) {
            if (this.mStartFirstTop <= ((float) getCentralViewTop())) {
                if (getChildAt(0).getTop() >= getTopViewMaxTop() && this.mOverScrollListener != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getTopViewMaxTop() {
        return getHeight() / 2;
    }

    private int getItemHeight() {
        return (getAdjustedHeight() / 3) + 1;
    }

    public int getCentralViewTop() {
        return getPaddingTop() + getItemHeight();
    }

    public void animateToCenter() {
        if (getChildCount() != 0) {
            startScrollAnimation(getCentralViewTop() - getChildAt(findCenterViewIndex()).getTop(), CENTERING_ANIMATION_DURATION_MS, 0, new C09185());
        }
    }

    public void animateToInitialPosition(final Runnable endAction) {
        startScrollAnimation((getCentralViewTop() + this.mInitialOffset) - getChildAt(null).getTop(), CENTERING_ANIMATION_DURATION_MS, 0, new SimpleAnimatorListener(this) {
            public void onAnimationEnd(Animator animator) {
                Runnable runnable = endAction;
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }

    private void handleTouchDown(MotionEvent event) {
        if (this.mCanClick) {
            this.mTapPositionX = (int) event.getX();
            this.mTapPositionY = (int) event.getY();
            float rawY = event.getRawY();
            computeTapRegions(this.mTapRegions);
            float[] fArr = this.mTapRegions;
            if (rawY > fArr[0] && rawY < fArr[1]) {
                if (getChildAt(findCenterViewIndex()) instanceof OnCenterProximityListener) {
                    Handler handler = getHandler();
                    if (handler != null) {
                        handler.removeCallbacks(this.mReleasedRunnable);
                        handler.postDelayed(this.mPressedRunnable, (long) ViewConfiguration.getTapTimeout());
                    }
                }
            }
        }
    }

    private void setScrollVertically(int scroll) {
        scrollBy(0, scroll - this.mLastScrollChange);
        this.mLastScrollChange = scroll;
    }

    private int getAdjustedHeight() {
        return getAdjustedHeight(this);
    }

    private static int getAdjustedHeight(View v) {
        return (v.getHeight() - v.getPaddingBottom()) - v.getPaddingTop();
    }

    private void computeTapRegions(float[] tapRegions) {
        int[] iArr = this.mLocation;
        iArr[1] = 0;
        iArr[0] = 0;
        getLocationOnScreen(iArr);
        int mScreenTop = this.mLocation[1];
        int height = getHeight();
        tapRegions[0] = ((float) mScreenTop) + (((float) height) * 0.33f);
        tapRegions[1] = ((float) mScreenTop) + (((float) height) * 0.66999996f);
    }

    public boolean getMaximizeSingleItem() {
        return this.mMaximizeSingleItem;
    }

    public void setMaximizeSingleItem(boolean maximizeSingleItem) {
        this.mMaximizeSingleItem = maximizeSingleItem;
    }

    private void notifyChildrenAboutProximity(boolean animate) {
        LayoutManager layoutManager = (LayoutManager) getLayoutManager();
        int count = layoutManager.getChildCount();
        if (count != 0) {
            int index = layoutManager.findCenterViewIndex();
            int i = 0;
            while (i < count) {
                getChildViewHolder(layoutManager.getChildAt(i)).onCenterProximity(i == index, animate);
                i++;
            }
            i = getChildViewHolder(getChildAt(index)).getPosition();
            if (i != this.mPreviousCentral) {
                int baseline = getBaseline();
                if (this.mPreviousBaseline != baseline) {
                    this.mPreviousBaseline = baseline;
                    requestLayout();
                }
                for (OnScrollListener listener : this.mOnScrollListeners) {
                    listener.onCentralPositionChanged(i);
                }
                for (OnCentralPositionChangedListener listener2 : this.mOnCentralPositionChangedListeners) {
                    listener2.onCentralPositionChanged(i);
                }
                this.mPreviousCentral = i;
            }
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mObserver.setListView(this);
    }

    protected void onDetachedFromWindow() {
        this.mObserver.setListView(null);
        super.onDetachedFromWindow();
    }
}
