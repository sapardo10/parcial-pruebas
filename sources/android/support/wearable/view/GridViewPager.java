package android.support.wearable.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.view.InputDeviceCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.WindowInsets;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ScrollView;
import android.widget.Scroller;

@TargetApi(20)
@Deprecated
public class GridViewPager extends ViewGroup {
    private static final int CLOSE_ENOUGH = 2;
    private static final boolean DEBUG_ADAPTER = false;
    private static final boolean DEBUG_LAYOUT = false;
    private static final boolean DEBUG_LIFECYCLE = false;
    private static final boolean DEBUG_LISTENERS = false;
    private static final boolean DEBUG_POPULATE = false;
    private static final boolean DEBUG_ROUND = false;
    private static final boolean DEBUG_SCROLLING = false;
    private static final boolean DEBUG_SETTLING = false;
    private static final boolean DEBUG_TOUCH = false;
    private static final boolean DEBUG_TOUCHSLOP = false;
    private static final int DEFAULT_OFFSCREEN_PAGES = 1;
    private static final int[] LAYOUT_ATTRS = new int[]{16842931};
    private static final int MIN_ACCURATE_VELOCITY = 200;
    private static final int MIN_DISTANCE_FOR_FLING_DP = 40;
    private static final int NO_POINTER = -1;
    private static final Interpolator OVERSCROLL_INTERPOLATOR = new DragFrictionInterpolator();
    private static final int SCROLL_AXIS_X = 0;
    private static final int SCROLL_AXIS_Y = 1;
    public static final int SCROLL_STATE_CONTENT_SETTLING = 3;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_SETTLING = 2;
    private static final int SLIDE_ANIMATION_DURATION_NORMAL_MS = 300;
    private static final Interpolator SLIDE_INTERPOLATOR = new DecelerateInterpolator(2.5f);
    private static final String TAG = "GridViewPager";
    private int mActivePointerId;
    private GridPagerAdapter mAdapter;
    private OnAdapterChangeListener mAdapterChangeListener;
    private boolean mAdapterChangeNotificationPending;
    private final BackgroundController mBackgroundController;
    private boolean mCalledSuper;
    private final int mCloseEnough;
    private int mColMargin;
    private boolean mConsumeInsets;
    private final Point mCurItem;
    private boolean mDatasetChangePending;
    private boolean mDelayPopulate;
    private final Runnable mEndScrollRunnable;
    private int mExpectedCurrentColumnCount;
    private int mExpectedRowCount;
    private boolean mFirstLayout;
    private int mGestureInitialScrollY;
    private float mGestureInitialX;
    private float mGestureInitialY;
    private boolean mInLayout;
    private boolean mIsAbleToDrag;
    private boolean mIsBeingDragged;
    private final SimpleArrayMap<Point, ItemInfo> mItems;
    private final int mMinFlingDistance;
    private final int mMinFlingVelocity;
    private final int mMinUsableVelocity;
    private PagerObserver mObserver;
    private int mOffscreenPageCount;
    private GridPagerAdapter mOldAdapter;
    private OnApplyWindowInsetsListener mOnApplyWindowInsetsListener;
    private OnPageChangeListener mOnPageChangeListener;
    private float mPointerLastX;
    private float mPointerLastY;
    private final Rect mPopulatedPageBounds;
    private final Rect mPopulatedPages;
    private final SimpleArrayMap<Point, ItemInfo> mRecycledItems;
    private Parcelable mRestoredAdapterState;
    private ClassLoader mRestoredClassLoader;
    private Point mRestoredCurItem;
    private int mRowMargin;
    private final SparseIntArray mRowScrollX;
    private int mScrollAxis;
    private int mScrollState;
    private final Scroller mScroller;
    private View mScrollingContent;
    private int mSlideAnimationDurationMs;
    private final Point mTempPoint1;
    private final int mTouchSlop;
    private final int mTouchSlopSquared;
    private VelocityTracker mVelocityTracker;
    private WindowInsets mWindowInsets;

    /* renamed from: android.support.wearable.view.GridViewPager$1 */
    class C04481 implements Runnable {
        C04481() {
        }

        public void run() {
            GridViewPager.this.setScrollState(0);
            GridViewPager.this.populate();
        }
    }

    private static final class DragFrictionInterpolator implements Interpolator {
        private static final float DEFAULT_FALLOFF = 4.0f;
        private final float falloffRate;

        public DragFrictionInterpolator() {
            this(DEFAULT_FALLOFF);
        }

        public DragFrictionInterpolator(float falloffRate) {
            this.falloffRate = falloffRate;
        }

        public float getInterpolation(float input) {
            double e = Math.exp((double) ((2.0f * input) * this.falloffRate));
            return ((float) ((e - 1.0d) / (1.0d + e))) * (1.0f / this.falloffRate);
        }
    }

    static class ItemInfo {
        Object object;
        int positionX;
        int positionY;

        ItemInfo() {
        }

        public String toString() {
            int i = this.positionX;
            int i2 = this.positionY;
            String valueOf = String.valueOf(this.object);
            StringBuilder stringBuilder = new StringBuilder(String.valueOf(valueOf).length() + 27);
            stringBuilder.append(i);
            stringBuilder.append(",");
            stringBuilder.append(i2);
            stringBuilder.append(" => ");
            stringBuilder.append(valueOf);
            return stringBuilder.toString();
        }
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int gravity;
        public boolean needsMeasure;

        public LayoutParams() {
            super(-1, -1);
        }

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, GridViewPager.LAYOUT_ATTRS);
            this.gravity = a.getInteger(0, 48);
            a.recycle();
        }
    }

    public interface OnAdapterChangeListener {
        void onAdapterChanged(GridPagerAdapter gridPagerAdapter, GridPagerAdapter gridPagerAdapter2);

        void onDataSetChanged();
    }

    public interface OnPageChangeListener {
        void onPageScrollStateChanged(int i);

        void onPageScrolled(int i, int i2, float f, float f2, int i3, int i4);

        void onPageSelected(int i, int i2);
    }

    private class PagerObserver extends DataSetObserver {
        private PagerObserver() {
        }

        public void onChanged() {
            GridViewPager.this.dataSetChanged();
        }

        public void onInvalidated() {
            GridViewPager.this.dataSetChanged();
        }
    }

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new C04491();
        int currentX;
        int currentY;

        /* renamed from: android.support.wearable.view.GridViewPager$SavedState$1 */
        class C04491 implements Creator<SavedState> {
            C04491() {
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.currentX);
            out.writeInt(this.currentY);
        }

        private SavedState(Parcel in) {
            super(in);
            this.currentX = in.readInt();
            this.currentY = in.readInt();
        }
    }

    private void populate(int r17, int r18) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:73:0x01e0 in {4, 5, 6, 9, 12, 25, 26, 33, 34, 35, 36, 42, 43, 44, 50, 51, 54, 55, 56, 59, 62, 63, 66, 67, 68, 70, 72} preds:[]
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
        r16 = this;
        r0 = r16;
        r1 = r17;
        r2 = r18;
        r3 = new android.graphics.Point;
        r3.<init>();
        r4 = r0.mCurItem;
        r4 = r4.x;
        if (r4 != r1) goto L_0x0019;
    L_0x0011:
        r4 = r0.mCurItem;
        r4 = r4.y;
        if (r4 == r2) goto L_0x0018;
    L_0x0017:
        goto L_0x0019;
    L_0x0018:
        goto L_0x0029;
    L_0x0019:
        r4 = r0.mCurItem;
        r4 = r4.x;
        r5 = r0.mCurItem;
        r5 = r5.y;
        r3.set(r4, r5);
        r4 = r0.mCurItem;
        r4.set(r1, r2);
    L_0x0029:
        r4 = r0.mDelayPopulate;
        if (r4 == 0) goto L_0x002e;
    L_0x002d:
        return;
    L_0x002e:
        r4 = r16.getWindowToken();
        if (r4 != 0) goto L_0x0035;
    L_0x0034:
        return;
    L_0x0035:
        r4 = r0.mAdapter;
        r4.startUpdate(r0);
        r4 = r0.mPopulatedPageBounds;
        r4.setEmpty();
        r4 = r0.mAdapter;
        r4 = r4.getRowCount();
        r5 = r0.mExpectedRowCount;
        if (r5 != r4) goto L_0x01d8;
    L_0x0049:
        r5 = r0.mAdapter;
        r5 = r5.getColumnCount(r2);
        r6 = 1;
        if (r5 < r6) goto L_0x01d0;
    L_0x0052:
        r0.mExpectedRowCount = r4;
        r0.mExpectedCurrentColumnCount = r5;
        r7 = r0.mOffscreenPageCount;
        r7 = java.lang.Math.max(r6, r7);
        r8 = r2 - r7;
        r9 = 0;
        r8 = java.lang.Math.max(r9, r8);
        r10 = r4 + -1;
        r11 = r2 + r7;
        r10 = java.lang.Math.min(r10, r11);
        r11 = r1 - r7;
        r11 = java.lang.Math.max(r9, r11);
        r12 = r5 + -1;
        r13 = r1 + r7;
        r12 = java.lang.Math.min(r12, r13);
        r13 = r0.mItems;
        r13 = r13.size();
        r13 = r13 - r6;
    L_0x0080:
        if (r13 < 0) goto L_0x00d0;
    L_0x0082:
        r14 = r0.mItems;
        r14 = r14.valueAt(r13);
        r14 = (android.support.wearable.view.GridViewPager.ItemInfo) r14;
        r15 = r14.positionY;
        if (r15 != r2) goto L_0x0098;
    L_0x008e:
        r15 = r14.positionX;
        if (r15 < r11) goto L_0x0097;
    L_0x0092:
        r15 = r14.positionX;
        if (r15 > r12) goto L_0x0097;
    L_0x0096:
        goto L_0x00cb;
    L_0x0097:
        goto L_0x00b2;
    L_0x0098:
        r15 = r0.mAdapter;
        r9 = r14.positionY;
        r6 = r0.mCurItem;
        r6 = r6.x;
        r6 = r15.getCurrentColumnForRow(r9, r6);
        r9 = r14.positionX;
        if (r9 != r6) goto L_0x00b1;
    L_0x00a8:
        r9 = r14.positionY;
        if (r9 < r8) goto L_0x00b1;
    L_0x00ac:
        r9 = r14.positionY;
        if (r9 > r10) goto L_0x00b1;
    L_0x00b0:
        goto L_0x00cb;
    L_0x00b2:
        r6 = r0.mItems;
        r6 = r6.keyAt(r13);
        r6 = (android.graphics.Point) r6;
        r9 = r0.mItems;
        r9.removeAt(r13);
        r9 = r14.positionX;
        r15 = r14.positionY;
        r6.set(r9, r15);
        r9 = r0.mRecycledItems;
        r9.put(r6, r14);
    L_0x00cb:
        r13 = r13 + -1;
        r6 = 1;
        r9 = 0;
        goto L_0x0080;
    L_0x00d0:
        r6 = r0.mTempPoint1;
        r6.y = r2;
        r6.x = r11;
    L_0x00d6:
        r6 = r0.mTempPoint1;
        r6 = r6.x;
        if (r6 > r12) goto L_0x00fc;
    L_0x00dc:
        r6 = r0.mItems;
        r9 = r0.mTempPoint1;
        r6 = r6.containsKey(r9);
        if (r6 != 0) goto L_0x00f2;
    L_0x00e6:
        r6 = r0.mTempPoint1;
        r6 = r6.x;
        r9 = r0.mTempPoint1;
        r9 = r9.y;
        r0.addNewItem(r6, r9);
        goto L_0x00f3;
    L_0x00f3:
        r6 = r0.mTempPoint1;
        r9 = r6.x;
        r13 = 1;
        r9 = r9 + r13;
        r6.x = r9;
        goto L_0x00d6;
    L_0x00fc:
        r6 = r0.mTempPoint1;
        r6.y = r8;
    L_0x0100:
        r6 = r0.mTempPoint1;
        r6 = r6.y;
        if (r6 > r10) goto L_0x0152;
    L_0x0106:
        r6 = r0.mTempPoint1;
        r9 = r0.mAdapter;
        r13 = r6.y;
        r9 = r9.getCurrentColumnForRow(r13, r1);
        r6.x = r9;
        r6 = r0.mItems;
        r9 = r0.mTempPoint1;
        r6 = r6.containsKey(r9);
        if (r6 != 0) goto L_0x0128;
    L_0x011c:
        r6 = r0.mTempPoint1;
        r6 = r6.x;
        r9 = r0.mTempPoint1;
        r9 = r9.y;
        r0.addNewItem(r6, r9);
        goto L_0x0129;
    L_0x0129:
        r6 = r0.mTempPoint1;
        r6 = r6.y;
        r9 = r0.mCurItem;
        r9 = r9.y;
        if (r6 == r9) goto L_0x0148;
    L_0x0133:
        r6 = r0.mTempPoint1;
        r6 = r6.y;
        r9 = r0.mTempPoint1;
        r9 = r9.x;
        r9 = r0.computePageLeft(r9);
        r13 = r16.getPaddingLeft();
        r9 = r9 - r13;
        r0.setRowScrollX(r6, r9);
        goto L_0x0149;
    L_0x0149:
        r6 = r0.mTempPoint1;
        r9 = r6.y;
        r13 = 1;
        r9 = r9 + r13;
        r6.y = r9;
        goto L_0x0100;
    L_0x0152:
        r13 = 1;
        r6 = r0.mRecycledItems;
        r6 = r6.size();
        r6 = r6 - r13;
    L_0x015a:
        if (r6 < 0) goto L_0x0174;
    L_0x015c:
        r9 = r0.mRecycledItems;
        r9 = r9.removeAt(r6);
        r9 = (android.support.wearable.view.GridViewPager.ItemInfo) r9;
        r13 = r0.mAdapter;
        r14 = r9.positionY;
        r15 = r9.positionX;
        r1 = r9.object;
        r13.destroyItem(r0, r14, r15, r1);
        r6 = r6 + -1;
        r1 = r17;
        goto L_0x015a;
    L_0x0174:
        r1 = r0.mRecycledItems;
        r1.clear();
        r1 = r0.mAdapter;
        r1.finishUpdate(r0);
        r1 = r0.mPopulatedPages;
        r1.set(r11, r8, r12, r10);
        r1 = r0.mPopulatedPageBounds;
        r6 = r0.computePageLeft(r11);
        r9 = r16.getPaddingLeft();
        r6 = r6 - r9;
        r9 = r0.computePageTop(r8);
        r13 = r16.getPaddingTop();
        r9 = r9 - r13;
        r13 = r12 + 1;
        r13 = r0.computePageLeft(r13);
        r14 = r16.getPaddingRight();
        r13 = r13 - r14;
        r14 = r10 + 1;
        r14 = r0.computePageTop(r14);
        r15 = r16.getPaddingBottom();
        r14 = r14 + r15;
        r1.set(r6, r9, r13, r14);
        r1 = r0.mAdapterChangeNotificationPending;
        if (r1 == 0) goto L_0x01c2;
    L_0x01b4:
        r1 = 0;
        r0.mAdapterChangeNotificationPending = r1;
        r1 = r0.mOldAdapter;
        r6 = r0.mAdapter;
        r0.adapterChanged(r1, r6);
        r1 = 0;
        r0.mOldAdapter = r1;
        goto L_0x01c3;
    L_0x01c3:
        r1 = r0.mDatasetChangePending;
        if (r1 == 0) goto L_0x01ce;
    L_0x01c7:
        r1 = 0;
        r0.mDatasetChangePending = r1;
        r16.dispatchOnDataSetChanged();
        goto L_0x01cf;
    L_0x01cf:
        return;
    L_0x01d0:
        r1 = new java.lang.IllegalStateException;
        r6 = "All rows must have at least 1 column";
        r1.<init>(r6);
        throw r1;
    L_0x01d8:
        r1 = new java.lang.IllegalStateException;
        r5 = "Adapter row count changed without a call to notifyDataSetChanged()";
        r1.<init>(r5);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.wearable.view.GridViewPager.populate(int, int):void");
    }

    public GridViewPager(Context context) {
        this(context, null, 0);
    }

    public GridViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mConsumeInsets = true;
        this.mSlideAnimationDurationMs = SLIDE_ANIMATION_DURATION_NORMAL_MS;
        this.mEndScrollRunnable = new C04481();
        this.mOffscreenPageCount = 1;
        this.mActivePointerId = -1;
        this.mVelocityTracker = null;
        this.mFirstLayout = true;
        this.mScrollState = 0;
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        float density = context.getResources().getDisplayMetrics().density;
        this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(vc);
        int i = this.mTouchSlop;
        this.mTouchSlopSquared = i * i;
        this.mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        this.mMinFlingDistance = (int) (40.0f * density);
        this.mMinUsableVelocity = (int) (200.0f * density);
        this.mCloseEnough = (int) (2.0f * density);
        this.mCurItem = new Point();
        this.mItems = new SimpleArrayMap();
        this.mRecycledItems = new SimpleArrayMap();
        this.mPopulatedPages = new Rect();
        this.mPopulatedPageBounds = new Rect();
        this.mScroller = new Scroller(context, SLIDE_INTERPOLATOR, true);
        this.mTempPoint1 = new Point();
        setOverScrollMode(1);
        this.mRowScrollX = new SparseIntArray();
        this.mBackgroundController = new BackgroundController();
        this.mBackgroundController.attachTo(this);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
        getParent().requestFitSystemWindows();
    }

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).dispatchApplyWindowInsets(insets);
        }
        this.mWindowInsets = insets;
        return insets;
    }

    public void setConsumeWindowInsets(boolean consume) {
        this.mConsumeInsets = consume;
    }

    public void setOnApplyWindowInsetsListener(OnApplyWindowInsetsListener listener) {
        this.mOnApplyWindowInsetsListener = listener;
    }

    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        insets = onApplyWindowInsets(insets);
        OnApplyWindowInsetsListener onApplyWindowInsetsListener = this.mOnApplyWindowInsetsListener;
        if (onApplyWindowInsetsListener != null) {
            onApplyWindowInsetsListener.onApplyWindowInsets(this, insets);
        }
        return this.mConsumeInsets ? insets.consumeSystemWindowInsets() : insets;
    }

    public void requestFitSystemWindows() {
    }

    protected void onDetachedFromWindow() {
        removeCallbacks(this.mEndScrollRunnable);
        super.onDetachedFromWindow();
    }

    public void setAdapter(GridPagerAdapter adapter) {
        GridPagerAdapter gridPagerAdapter = this.mAdapter;
        if (gridPagerAdapter != null) {
            gridPagerAdapter.unregisterDataSetObserver(this.mObserver);
            this.mAdapter.setOnBackgroundChangeListener(null);
            this.mAdapter.startUpdate(this);
            for (int i = 0; i < this.mItems.size(); i++) {
                ItemInfo ii = (ItemInfo) this.mItems.valueAt(i);
                this.mAdapter.destroyItem(this, ii.positionY, ii.positionX, ii.object);
            }
            this.mAdapter.finishUpdate(this);
            this.mItems.clear();
            removeAllViews();
            scrollTo(0, 0);
            this.mRowScrollX.clear();
        }
        gridPagerAdapter = this.mAdapter;
        this.mCurItem.set(0, 0);
        this.mAdapter = adapter;
        this.mExpectedRowCount = 0;
        this.mExpectedCurrentColumnCount = 0;
        if (this.mAdapter != null) {
            if (this.mObserver == null) {
                this.mObserver = new PagerObserver();
            }
            this.mAdapter.registerDataSetObserver(this.mObserver);
            this.mAdapter.setOnBackgroundChangeListener(this.mBackgroundController);
            this.mDelayPopulate = false;
            boolean wasFirstLayout = this.mFirstLayout;
            this.mFirstLayout = true;
            this.mExpectedRowCount = this.mAdapter.getRowCount();
            if (this.mExpectedRowCount > 0) {
                this.mCurItem.set(0, 0);
                this.mExpectedCurrentColumnCount = this.mAdapter.getColumnCount(this.mCurItem.y);
            }
            if (this.mRestoredCurItem != null) {
                this.mAdapter.restoreState(this.mRestoredAdapterState, this.mRestoredClassLoader);
                setCurrentItemInternal(this.mRestoredCurItem.y, this.mRestoredCurItem.x, false, true);
                this.mRestoredCurItem = null;
                this.mRestoredAdapterState = null;
                this.mRestoredClassLoader = null;
            } else if (wasFirstLayout) {
                requestLayout();
            } else {
                populate();
            }
        } else if (this.mIsBeingDragged) {
            cancelDragGesture();
            if (gridPagerAdapter != adapter) {
                this.mAdapterChangeNotificationPending = false;
                this.mOldAdapter = null;
            } else if (adapter != null) {
                this.mAdapterChangeNotificationPending = false;
                adapterChanged(gridPagerAdapter, adapter);
                this.mOldAdapter = null;
            } else {
                this.mAdapterChangeNotificationPending = true;
                this.mOldAdapter = gridPagerAdapter;
            }
        }
        if (gridPagerAdapter != adapter) {
            this.mAdapterChangeNotificationPending = false;
            this.mOldAdapter = null;
        } else if (adapter != null) {
            this.mAdapterChangeNotificationPending = true;
            this.mOldAdapter = gridPagerAdapter;
        } else {
            this.mAdapterChangeNotificationPending = false;
            adapterChanged(gridPagerAdapter, adapter);
            this.mOldAdapter = null;
        }
    }

    public GridPagerAdapter getAdapter() {
        return this.mAdapter;
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mOnPageChangeListener = listener;
    }

    public void setOnAdapterChangeListener(OnAdapterChangeListener listener) {
        this.mAdapterChangeListener = listener;
        if (listener != null) {
            GridPagerAdapter gridPagerAdapter = this.mAdapter;
            if (!(gridPagerAdapter == null || this.mAdapterChangeNotificationPending)) {
                listener.onAdapterChanged(null, gridPagerAdapter);
            }
        }
    }

    private void adapterChanged(GridPagerAdapter oldAdapter, GridPagerAdapter newAdapter) {
        OnAdapterChangeListener onAdapterChangeListener = this.mAdapterChangeListener;
        if (onAdapterChangeListener != null) {
            onAdapterChangeListener.onAdapterChanged(oldAdapter, newAdapter);
        }
        BackgroundController backgroundController = this.mBackgroundController;
        if (backgroundController != null) {
            backgroundController.onAdapterChanged(oldAdapter, newAdapter);
        }
    }

    public void scrollTo(int x, int y) {
        if (this.mScrollState == 2 && this.mScrollAxis == 1) {
            x = getRowScrollX(this.mCurItem.y);
        }
        super.scrollTo(0, y);
        scrollCurrentRowTo(x);
    }

    private void setScrollState(int newState) {
        if (this.mScrollState != newState) {
            this.mScrollState = newState;
            OnPageChangeListener onPageChangeListener = this.mOnPageChangeListener;
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageScrollStateChanged(newState);
            }
            BackgroundController backgroundController = this.mBackgroundController;
            if (backgroundController != null) {
                backgroundController.onPageScrollStateChanged(newState);
            }
        }
    }

    private int getRowScrollX(int row) {
        return this.mRowScrollX.get(row, 0);
    }

    private void setRowScrollX(int row, int scrollX) {
        this.mRowScrollX.put(row, scrollX);
    }

    private void scrollRowTo(int row, int x) {
        if (getRowScrollX(row) != x) {
            int size = getChildCount();
            int scrollAmount = x - getRowScrollX(row);
            for (int i = 0; i < size; i++) {
                View child = getChildAt(i);
                ItemInfo ii = infoForChild(child);
                if (ii != null && ii.positionY == row) {
                    child.offsetLeftAndRight(-scrollAmount);
                    postInvalidateOnAnimation();
                }
            }
            setRowScrollX(row, x);
        }
    }

    private void scrollCurrentRowTo(int x) {
        scrollRowTo(this.mCurItem.y, x);
    }

    private int getContentWidth() {
        return getMeasuredWidth() - (getPaddingLeft() + getPaddingRight());
    }

    private int getContentHeight() {
        return getMeasuredHeight() - (getPaddingTop() + getPaddingBottom());
    }

    public void setCurrentItem(int row, int column) {
        this.mDelayPopulate = false;
        setCurrentItemInternal(row, column, this.mFirstLayout ^ 1, false);
    }

    public void setCurrentItem(int row, int column, boolean smoothScroll) {
        this.mDelayPopulate = false;
        setCurrentItemInternal(row, column, smoothScroll, false);
    }

    public Point getCurrentItem() {
        return new Point(this.mCurItem);
    }

    void setCurrentItemInternal(int row, int column, boolean smoothScroll, boolean always) {
        setCurrentItemInternal(row, column, smoothScroll, always, 0);
    }

    void setCurrentItemInternal(int row, int column, boolean smoothScroll, boolean always, int velocity) {
        GridPagerAdapter gridPagerAdapter = this.mAdapter;
        if (gridPagerAdapter != null) {
            if (gridPagerAdapter.getRowCount() > 0) {
                if (always || !this.mCurItem.equals(column, row) || this.mItems.size() == 0) {
                    boolean dispatchSelected;
                    row = limit(row, 0, this.mAdapter.getRowCount() - 1);
                    column = limit(column, 0, this.mAdapter.getColumnCount(row) - 1);
                    if (column != this.mCurItem.x) {
                        this.mScrollAxis = 0;
                        dispatchSelected = true;
                    } else if (row != this.mCurItem.y) {
                        this.mScrollAxis = 1;
                        dispatchSelected = true;
                    } else {
                        dispatchSelected = false;
                    }
                    if (this.mFirstLayout) {
                        this.mCurItem.set(0, 0);
                        this.mAdapter.setCurrentColumnForRow(row, column);
                        if (dispatchSelected) {
                            OnPageChangeListener onPageChangeListener = this.mOnPageChangeListener;
                            if (onPageChangeListener != null) {
                                onPageChangeListener.onPageSelected(row, column);
                            }
                            BackgroundController backgroundController = this.mBackgroundController;
                            if (backgroundController != null) {
                                backgroundController.onPageSelected(row, column);
                            }
                        }
                        requestLayout();
                    } else {
                        populate(column, row);
                        scrollToItem(column, row, smoothScroll, velocity, dispatchSelected);
                    }
                }
            }
        }
    }

    private void scrollToItem(int x, int y, boolean smoothScroll, int velocity, boolean dispatchSelected) {
        ItemInfo curInfo = infoForPosition(x, y);
        int destX = 0;
        int destY = 0;
        if (curInfo != null) {
            destX = computePageLeft(curInfo.positionX) - getPaddingLeft();
            destY = computePageTop(curInfo.positionY) - getPaddingTop();
        }
        this.mAdapter.setCurrentColumnForRow(y, x);
        if (dispatchSelected) {
            OnPageChangeListener onPageChangeListener = this.mOnPageChangeListener;
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageSelected(y, x);
            }
            BackgroundController backgroundController = this.mBackgroundController;
            if (backgroundController != null) {
                backgroundController.onPageSelected(y, x);
            }
        }
        if (smoothScroll) {
            smoothScrollTo(destX, destY, velocity);
            return;
        }
        completeScroll(false);
        scrollTo(destX, destY);
        pageScrolled(destX, destY);
    }

    public int getOffscreenPageCount() {
        return this.mOffscreenPageCount;
    }

    public void setOffscreenPageCount(int limit) {
        if (limit < 1) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder(69);
            stringBuilder.append("Requested offscreen page limit ");
            stringBuilder.append(limit);
            stringBuilder.append(" too small; defaulting to ");
            stringBuilder.append(1);
            Log.w(str, stringBuilder.toString());
            limit = 1;
        }
        if (limit != this.mOffscreenPageCount) {
            this.mOffscreenPageCount = limit;
            populate();
        }
    }

    public void setPageMargins(int rowMarginPx, int columnMarginPx) {
        int oldRowMargin = this.mRowMargin;
        this.mRowMargin = rowMarginPx;
        int oldColMargin = this.mColMargin;
        this.mColMargin = columnMarginPx;
        int width = getWidth();
        int height = getHeight();
        if (!this.mFirstLayout && !this.mItems.isEmpty()) {
            recomputeScrollPosition(width, width, height, height, this.mColMargin, oldColMargin, this.mRowMargin, oldRowMargin);
            requestLayout();
        }
    }

    public void setSlideAnimationDuration(int slideAnimationDuration) {
        this.mSlideAnimationDurationMs = slideAnimationDuration;
    }

    public int getPageRowMargin() {
        return this.mRowMargin;
    }

    public int getPageColumnMargin() {
        return this.mColMargin;
    }

    void smoothScrollTo(int x, int y) {
        smoothScrollTo(x, y, 0);
    }

    void smoothScrollTo(int x, int y, int velocity) {
        if (getChildCount() != 0) {
            int sx = getRowScrollX(this.mCurItem.y);
            int sy = getScrollY();
            int dx = x - sx;
            int dy = y - sy;
            if (dx == 0 && dy == 0) {
                completeScroll(false);
                populate();
                setScrollState(0);
                return;
            }
            setScrollState(2);
            this.mScroller.startScroll(sx, sy, dx, dy, this.mSlideAnimationDurationMs);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    void flingContent(int limitX, int limitY, int velocityX, int velocityY) {
        if (this.mScrollingContent != null) {
            if (velocityX == 0 && velocityY == 0) {
                completeScroll(false);
                setScrollState(0);
                return;
            }
            int minX;
            int maxX;
            int minY;
            int maxY;
            int sx = r0.mScrollingContent.getScrollX();
            int sy = r0.mScrollingContent.getScrollY();
            setScrollState(3);
            if (velocityX > 0) {
                minX = sx;
                maxX = sx + limitX;
            } else {
                minX = sx + limitX;
                maxX = sx;
            }
            if (velocityY > 0) {
                minY = sy;
                maxY = sy + limitY;
            } else {
                minY = sy + limitY;
                maxY = sy;
            }
            r0.mScroller.fling(sx, sy, velocityX, velocityY, minX, maxX, minY, maxY);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private ItemInfo addNewItem(int positionX, int positionY) {
        Point key = new Point(positionX, positionY);
        ItemInfo ii = (ItemInfo) this.mRecycledItems.remove(key);
        if (ii == null) {
            ii = new ItemInfo();
            ii.object = this.mAdapter.instantiateItem(this, positionY, positionX);
            ii.positionX = positionX;
            ii.positionY = positionY;
        }
        key.set(positionX, positionY);
        ii.positionX = positionX;
        ii.positionY = positionY;
        this.mItems.put(key, ii);
        return ii;
    }

    void rowBackgroundChanged(int row) {
        BackgroundController backgroundController = this.mBackgroundController;
        if (backgroundController != null) {
            backgroundController.onRowBackgroundChanged(row);
        }
    }

    void pageBackgroundChanged(int row, int column) {
        BackgroundController backgroundController = this.mBackgroundController;
        if (backgroundController != null) {
            backgroundController.onPageBackgroundChanged(row, column);
        }
    }

    private void dataSetChanged() {
        int adapterRowCount = this.mAdapter.getRowCount();
        this.mExpectedRowCount = adapterRowCount;
        Point newCurrItem = new Point(this.mCurItem);
        boolean isUpdating = false;
        SimpleArrayMap<Point, ItemInfo> newItemMap = new SimpleArrayMap();
        for (int i = this.mItems.size() - 1; i >= 0; i--) {
            Point itemKey = (Point) this.mItems.keyAt(i);
            ItemInfo itemInfo = (ItemInfo) this.mItems.valueAt(i);
            Point newItemPos = this.mAdapter.getItemPosition(itemInfo.object);
            this.mAdapter.applyItemPosition(itemInfo.object, newItemPos);
            if (newItemPos == GridPagerAdapter.POSITION_UNCHANGED) {
                newItemMap.put(itemKey, itemInfo);
            } else if (newItemPos == GridPagerAdapter.POSITION_NONE) {
                if (!isUpdating) {
                    this.mAdapter.startUpdate(this);
                    isUpdating = true;
                }
                this.mAdapter.destroyItem(this, itemInfo.positionY, itemInfo.positionX, itemInfo.object);
                if (this.mCurItem.equals(itemInfo.positionX, itemInfo.positionY)) {
                    newCurrItem.y = limit(this.mCurItem.y, 0, Math.max(0, adapterRowCount - 1));
                    if (newCurrItem.y < adapterRowCount) {
                        newCurrItem.x = limit(this.mCurItem.x, 0, this.mAdapter.getColumnCount(newCurrItem.y) - 1);
                    } else {
                        newCurrItem.x = 0;
                    }
                }
            } else if (!newItemPos.equals(itemInfo.positionX, itemInfo.positionY)) {
                if (this.mCurItem.equals(itemInfo.positionX, itemInfo.positionY)) {
                    newCurrItem.set(newItemPos.x, newItemPos.y);
                }
                itemInfo.positionX = newItemPos.x;
                itemInfo.positionY = newItemPos.y;
                newItemMap.put(new Point(newItemPos), itemInfo);
            }
        }
        this.mItems.clear();
        this.mItems.putAll(newItemMap);
        if (isUpdating) {
            this.mAdapter.finishUpdate(this);
        }
        if (this.mExpectedRowCount > 0) {
            this.mExpectedCurrentColumnCount = this.mAdapter.getColumnCount(newCurrItem.y);
        } else {
            this.mExpectedCurrentColumnCount = 0;
        }
        dispatchOnDataSetChanged();
        setCurrentItemInternal(newCurrItem.y, newCurrItem.x, false, true);
        requestLayout();
    }

    private void dispatchOnDataSetChanged() {
        OnAdapterChangeListener onAdapterChangeListener = this.mAdapterChangeListener;
        if (onAdapterChangeListener != null) {
            onAdapterChangeListener.onDataSetChanged();
        }
        BackgroundController backgroundController = this.mBackgroundController;
        if (backgroundController != null) {
            backgroundController.onDataSetChanged();
        }
    }

    private void populate() {
        GridPagerAdapter gridPagerAdapter = this.mAdapter;
        if (gridPagerAdapter != null && gridPagerAdapter.getRowCount() > 0) {
            populate(this.mCurItem.x, this.mCurItem.y);
        }
    }

    private void cancelDragGesture() {
        cancelPendingInputEvents();
        long now = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(now, now, 3, 0.0f, 0.0f, 0);
        event.setSource(InputDeviceCompat.SOURCE_TOUCHSCREEN);
        dispatchTouchEvent(event);
        event.recycle();
    }

    public Parcelable onSaveInstanceState() {
        SavedState state = new SavedState(super.onSaveInstanceState());
        state.currentX = this.mCurItem.x;
        state.currentY = this.mCurItem.y;
        return state;
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState ss = (SavedState) state;
            super.onRestoreInstanceState(ss.getSuperState());
            if (pointInRange(ss.currentX, ss.currentY)) {
                this.mRestoredCurItem = new Point(ss.currentX, ss.currentY);
            } else {
                this.mCurItem.set(0, 0);
                scrollTo(0, 0);
            }
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        ItemInfo ii = infoForChild(child);
        if (!checkLayoutParams(params)) {
            params = generateLayoutParams(params);
        }
        LayoutParams lp = (LayoutParams) params;
        if (this.mInLayout) {
            lp.needsMeasure = true;
            addViewInLayout(child, index, params);
        } else {
            super.addView(child, index, params);
        }
        WindowInsets windowInsets = this.mWindowInsets;
        if (windowInsets != null) {
            child.onApplyWindowInsets(windowInsets);
        }
    }

    public void removeView(View view) {
        ItemInfo ii = infoForChild(view);
        if (this.mInLayout) {
            removeViewInLayout(view);
        } else {
            super.removeView(view);
        }
    }

    private ItemInfo infoForChild(View child) {
        for (int i = 0; i < this.mItems.size(); i++) {
            ItemInfo ii = (ItemInfo) this.mItems.valueAt(i);
            if (ii != null && this.mAdapter.isViewFromObject(child, ii.object)) {
                return ii;
            }
        }
        return null;
    }

    private ItemInfo infoForPosition(Point p) {
        return (ItemInfo) this.mItems.get(p);
    }

    private ItemInfo infoForPosition(int x, int y) {
        this.mTempPoint1.set(x, y);
        return (ItemInfo) this.mItems.get(this.mTempPoint1);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        this.mInLayout = true;
        populate();
        this.mInLayout = false;
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp != null) {
                    measureChild(child, lp);
                }
            }
        }
    }

    public void measureChild(View child, LayoutParams lp) {
        int childDefaultWidth = getContentWidth();
        int childDefaultHeight = getContentHeight();
        int heightMode = 0;
        int widthMode = lp.width == -2 ? 0 : 1073741824;
        if (lp.height != -2) {
            heightMode = 1073741824;
        }
        child.measure(getChildMeasureSpec(MeasureSpec.makeMeasureSpec(childDefaultWidth, widthMode), lp.leftMargin + lp.rightMargin, lp.width), getChildMeasureSpec(MeasureSpec.makeMeasureSpec(childDefaultHeight, heightMode), lp.topMargin + lp.bottomMargin, lp.height));
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!this.mItems.isEmpty()) {
            int i = this.mColMargin;
            int i2 = this.mRowMargin;
            recomputeScrollPosition(w, oldw, h, oldh, i, i, i2, i2);
        }
    }

    private int computePageLeft(int column) {
        return ((getContentWidth() + this.mColMargin) * column) + getPaddingLeft();
    }

    private int computePageTop(int row) {
        return ((getContentHeight() + this.mRowMargin) * row) + getPaddingTop();
    }

    private void recomputeScrollPosition(int width, int oldWidth, int height, int oldHeight, int colMargin, int oldColMargin, int rowMargin, int oldRowMargin) {
        GridViewPager gridViewPager = this;
        if (oldWidth <= 0 || oldHeight <= 0) {
            ItemInfo ii = infoForPosition(gridViewPager.mCurItem);
            if (ii != null) {
                int targetX = computePageLeft(ii.positionX) - getPaddingLeft();
                int targetY = computePageTop(ii.positionY) - getPaddingTop();
                if (targetX == getRowScrollX(ii.positionY)) {
                    if (targetY == getScrollY()) {
                        return;
                    }
                }
                completeScroll(false);
                scrollTo(targetX, targetY);
                return;
            }
            return;
        }
        float pageOffset = ((float) getRowScrollX(gridViewPager.mCurItem.y)) / ((float) (((oldWidth - getPaddingLeft()) - getPaddingRight()) + oldColMargin));
        int newOffsetXPixels = (int) (((float) (((width - getPaddingLeft()) - getPaddingRight()) + colMargin)) * pageOffset);
        float pageOffsetY = ((float) getScrollY()) / ((float) (((oldHeight - getPaddingTop()) - getPaddingBottom()) + oldRowMargin));
        int newOffsetYPixels = (int) (((float) (((height - getPaddingTop()) - getPaddingBottom()) + rowMargin)) * pageOffsetY);
        scrollTo(newOffsetXPixels, newOffsetYPixels);
        if (gridViewPager.mScroller.isFinished()) {
        } else {
            ItemInfo targetInfo = infoForPosition(gridViewPager.mCurItem);
            gridViewPager.mScroller.startScroll(newOffsetXPixels, newOffsetYPixels, computePageLeft(targetInfo.positionX) - getPaddingLeft(), computePageTop(targetInfo.positionY) - getPaddingTop(), gridViewPager.mScroller.getDuration() - gridViewPager.mScroller.timePassed());
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int children = getChildCount();
        for (int i = 0; i < children; i++) {
            View view = getChildAt(i);
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            String str;
            if (lp == null) {
                str = TAG;
                String valueOf = String.valueOf(view);
                StringBuilder stringBuilder = new StringBuilder(String.valueOf(valueOf).length() + 34);
                stringBuilder.append("Got null layout params for child: ");
                stringBuilder.append(valueOf);
                Log.w(str, stringBuilder.toString());
            } else {
                ItemInfo ii = infoForChild(view);
                if (ii == null) {
                    str = TAG;
                    String valueOf2 = String.valueOf(view);
                    StringBuilder stringBuilder2 = new StringBuilder(String.valueOf(valueOf2).length() + 44);
                    stringBuilder2.append("Unknown child view, not claimed by adapter: ");
                    stringBuilder2.append(valueOf2);
                    Log.w(str, stringBuilder2.toString());
                } else {
                    if (lp.needsMeasure) {
                        lp.needsMeasure = false;
                        measureChild(view, lp);
                    }
                    int left = computePageLeft(ii.positionX);
                    left = (left - getRowScrollX(ii.positionY)) + lp.leftMargin;
                    int top = computePageTop(ii.positionY) + lp.topMargin;
                    view.layout(left, top, view.getMeasuredWidth() + left, view.getMeasuredHeight() + top);
                }
            }
        }
        if (this.mFirstLayout && !this.mItems.isEmpty()) {
            scrollToItem(this.mCurItem.x, this.mCurItem.y, false, 0, false);
        }
        this.mFirstLayout = false;
    }

    public void computeScroll() {
        if (this.mScroller.isFinished() || !this.mScroller.computeScrollOffset()) {
            completeScroll(true);
            return;
        }
        if (this.mScrollState != 3) {
            int oldX = getRowScrollX(this.mCurItem.y);
            int oldY = getScrollY();
            int x = this.mScroller.getCurrX();
            int y = this.mScroller.getCurrY();
            if (oldX == x) {
                if (oldY != y) {
                }
            }
            scrollTo(x, y);
            if (!pageScrolled(x, y)) {
                this.mScroller.abortAnimation();
                scrollTo(0, 0);
            }
        } else if (this.mScrollingContent == null) {
            this.mScroller.abortAnimation();
        } else {
            this.mScrollingContent.scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private static String scrollStateToString(int state) {
        switch (state) {
            case 0:
                return "IDLE";
            case 1:
                return "DRAGGING";
            case 2:
                return "SETTLING";
            case 3:
                return "CONTENT_SETTLING";
            default:
                return "";
        }
    }

    private boolean pageScrolled(int xpos, int ypos) {
        if (this.mItems.size() == 0) {
            r7.mCalledSuper = false;
            onPageScrolled(0, 0, 0.0f, 0.0f, 0, 0);
            if (r7.mCalledSuper) {
                return false;
            }
            throw new IllegalStateException("onPageScrolled did not call superclass implementation");
        }
        ItemInfo ii = infoForCurrentScrollPosition();
        int pageLeft = computePageLeft(ii.positionX);
        int offsetLeftPx = (xpos + getPaddingLeft()) - pageLeft;
        int offsetTopPx = (ypos + getPaddingTop()) - computePageTop(ii.positionY);
        float offsetLeft = getXIndex((float) offsetLeftPx);
        float offsetTop = getYIndex((float) offsetTopPx);
        r7.mCalledSuper = false;
        onPageScrolled(ii.positionX, ii.positionY, offsetLeft, offsetTop, offsetLeftPx, offsetTopPx);
        if (r7.mCalledSuper) {
            return true;
        }
        throw new IllegalStateException("onPageScrolled did not call superclass implementation");
    }

    public void onPageScrolled(int positionX, int positionY, float offsetX, float offsetY, int offsetLeftPx, int offsetTopPx) {
        this.mCalledSuper = true;
        OnPageChangeListener onPageChangeListener = this.mOnPageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(positionY, positionX, offsetY, offsetX, offsetTopPx, offsetLeftPx);
        }
        BackgroundController backgroundController = this.mBackgroundController;
        if (backgroundController != null) {
            backgroundController.onPageScrolled(positionY, positionX, offsetY, offsetX, offsetTopPx, offsetLeftPx);
        }
    }

    private void completeScroll(boolean postEvents) {
        boolean needPopulate = this.mScrollState == 2;
        if (needPopulate) {
            this.mScroller.abortAnimation();
            int oldX = getRowScrollX(this.mCurItem.y);
            int oldY = getScrollY();
            int x = this.mScroller.getCurrX();
            int y = this.mScroller.getCurrY();
            if (oldX == x) {
                if (oldY != y) {
                }
            }
            scrollTo(x, y);
        }
        this.mScrollingContent = null;
        this.mDelayPopulate = false;
        if (!needPopulate) {
            return;
        }
        if (postEvents) {
            ViewCompat.postOnAnimation(this, this.mEndScrollRunnable);
        } else {
            this.mEndScrollRunnable.run();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction() & 255;
        if (action != 3) {
            if (action != 1) {
                if (action != 0) {
                    if (this.mIsBeingDragged) {
                        return true;
                    }
                    if (!this.mIsAbleToDrag) {
                        return false;
                    }
                }
                if (action == 0) {
                    handlePointerDown(ev);
                } else if (action == 2) {
                    handlePointerMove(ev);
                } else if (action == 6) {
                    onSecondaryPointerUp(ev);
                }
                return this.mIsBeingDragged;
            }
        }
        this.mIsBeingDragged = false;
        this.mIsAbleToDrag = false;
        this.mActivePointerId = -1;
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (this.mAdapter == null) {
            return false;
        }
        int action = ev.getAction();
        int i = action & 255;
        if (i != 6) {
            switch (i) {
                case 0:
                    handlePointerDown(ev);
                    break;
                case 1:
                case 3:
                    handlePointerUp(ev);
                    break;
                case 2:
                    handlePointerMove(ev);
                    break;
                default:
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder(32);
                    stringBuilder.append("Unknown action type: ");
                    stringBuilder.append(action);
                    Log.e(str, stringBuilder.toString());
                    break;
            }
        }
        onSecondaryPointerUp(ev);
        return true;
    }

    private void requestParentDisallowInterceptTouchEvent(boolean disallowIntercept) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    private static float limit(float input, int limit) {
        if (limit > 0) {
            return Math.max(0.0f, Math.min(input, (float) limit));
        }
        return Math.min(0.0f, Math.max(input, (float) limit));
    }

    private boolean performDrag(float x, float y) {
        int scrollable;
        float scrollY;
        boolean wouldOverscroll;
        int i;
        int mode;
        boolean z;
        boolean couldScroll;
        int i2;
        float f;
        float overscrollX;
        float f2 = x;
        float f3 = y;
        float deltaX = this.mPointerLastX - f2;
        float deltaY = this.mPointerLastY - f3;
        this.mPointerLastX = f2;
        this.mPointerLastY = f3;
        Rect pages = this.mPopulatedPages;
        int leftBound = computePageLeft(pages.left) - getPaddingLeft();
        int rightBound = computePageLeft(pages.right) - getPaddingLeft();
        int topBound = computePageTop(pages.top) - getPaddingTop();
        int bottomBound = computePageTop(pages.bottom) - getPaddingTop();
        float scrollX = (float) getRowScrollX(this.mCurItem.y);
        float scrollY2 = (float) getScrollY();
        if (this.mScrollAxis == 1) {
            float distanceToFocusPoint;
            int pageSpacingY = getContentHeight() + r0.mRowMargin;
            if (deltaY < 0.0f) {
                distanceToFocusPoint = -(scrollY2 % ((float) pageSpacingY));
            } else {
                distanceToFocusPoint = (((float) pageSpacingY) - (scrollY2 % ((float) pageSpacingY))) % ((float) pageSpacingY);
            }
            boolean focalPointCrossed = false;
            if (Math.abs(distanceToFocusPoint) <= Math.abs(deltaY)) {
                deltaY -= distanceToFocusPoint;
                scrollY2 += distanceToFocusPoint;
                focalPointCrossed = true;
            }
            if (focalPointCrossed) {
                View child = getChildForInfo(infoForScrollPosition((int) scrollX, (int) scrollY2));
                if (child != null) {
                    scrollable = getScrollableDistance(child, (int) Math.signum(deltaY));
                    pages = limit(deltaY, scrollable);
                    scrollY = scrollY2;
                    child.scrollBy(0.0f, (int) pages);
                    deltaY -= pages;
                    r0.mPointerLastY += pages - ((float) ((int) pages));
                } else {
                    scrollY = scrollY2;
                }
            } else {
                scrollY = scrollY2;
            }
        } else {
            scrollY = scrollY2;
        }
        int targetX = (int) (((float) ((int) deltaX)) + scrollX);
        scrollable = (int) (scrollY + ((float) ((int) deltaY)));
        if (targetX >= leftBound && targetX <= rightBound && scrollable >= topBound) {
            if (scrollable <= bottomBound) {
                wouldOverscroll = false;
                if (wouldOverscroll) {
                    i = scrollable;
                } else {
                    mode = getOverScrollMode();
                    if (r0.mScrollAxis == 0) {
                        if (leftBound >= rightBound) {
                        }
                        z = true;
                        couldScroll = z;
                        if (mode != 0) {
                            if (couldScroll || mode != 1) {
                                deltaX = limit(deltaX, ((float) leftBound) - scrollX, ((float) rightBound) - scrollX);
                                deltaY = limit(deltaY, ((float) topBound) - scrollY, ((float) bottomBound) - scrollY);
                                i2 = targetX;
                                i = scrollable;
                            }
                        }
                        f = scrollX > ((float) rightBound) ? scrollX - ((float) rightBound) : scrollX < ((float) leftBound) ? scrollX - ((float) leftBound) : 0.0f;
                        overscrollX = f;
                        f = scrollY > ((float) bottomBound) ? scrollY - ((float) bottomBound) : scrollY < ((float) topBound) ? scrollY - ((float) topBound) : 0.0f;
                        if (Math.abs(overscrollX) > 0.0f || Math.signum(overscrollX) != Math.signum(deltaX)) {
                        } else {
                            deltaX *= OVERSCROLL_INTERPOLATOR.getInterpolation(1.0f - (Math.abs(overscrollX) / ((float) getContentWidth())));
                        }
                        if (Math.abs(f) > 0.0f || Math.signum(f) != Math.signum(deltaY)) {
                        } else {
                            deltaY *= OVERSCROLL_INTERPOLATOR.getInterpolation(1.0f - (Math.abs(f) / ((float) getContentHeight())));
                        }
                    }
                    if (r0.mScrollAxis == 1 || topBound >= bottomBound) {
                        z = false;
                        couldScroll = z;
                        if (mode != 0) {
                            if (couldScroll) {
                            }
                            deltaX = limit(deltaX, ((float) leftBound) - scrollX, ((float) rightBound) - scrollX);
                            deltaY = limit(deltaY, ((float) topBound) - scrollY, ((float) bottomBound) - scrollY);
                            i2 = targetX;
                            i = scrollable;
                        }
                        if (scrollX > ((float) rightBound)) {
                            if (scrollX < ((float) leftBound)) {
                            }
                        }
                        overscrollX = f;
                        if (scrollY > ((float) bottomBound)) {
                            if (scrollY < ((float) topBound)) {
                            }
                        }
                        if (Math.abs(overscrollX) > 0.0f) {
                        }
                        if (Math.abs(f) > 0.0f) {
                        }
                    } else {
                        z = true;
                        couldScroll = z;
                        if (mode != 0) {
                            if (couldScroll) {
                            }
                            deltaX = limit(deltaX, ((float) leftBound) - scrollX, ((float) rightBound) - scrollX);
                            deltaY = limit(deltaY, ((float) topBound) - scrollY, ((float) bottomBound) - scrollY);
                            i2 = targetX;
                            i = scrollable;
                        }
                        if (scrollX > ((float) rightBound)) {
                        }
                        overscrollX = f;
                        if (scrollY > ((float) bottomBound)) {
                        }
                        if (Math.abs(overscrollX) > 0.0f) {
                        }
                        if (Math.abs(f) > 0.0f) {
                        }
                    }
                }
                scrollX += deltaX;
                f2 = scrollY + deltaY;
                r0.mPointerLastX += scrollX - ((float) ((int) scrollX));
                r0.mPointerLastY += f2 - ((float) ((int) f2));
                scrollTo((int) scrollX, (int) f2);
                pageScrolled((int) scrollX, (int) f2);
                return true;
            }
        }
        wouldOverscroll = true;
        if (wouldOverscroll) {
            i = scrollable;
        } else {
            mode = getOverScrollMode();
            if (r0.mScrollAxis == 0) {
                if (leftBound >= rightBound) {
                }
                z = true;
                couldScroll = z;
                if (mode != 0) {
                    if (couldScroll) {
                    }
                    deltaX = limit(deltaX, ((float) leftBound) - scrollX, ((float) rightBound) - scrollX);
                    deltaY = limit(deltaY, ((float) topBound) - scrollY, ((float) bottomBound) - scrollY);
                    i2 = targetX;
                    i = scrollable;
                }
                if (scrollX > ((float) rightBound)) {
                    if (scrollX < ((float) leftBound)) {
                    }
                }
                overscrollX = f;
                if (scrollY > ((float) bottomBound)) {
                    if (scrollY < ((float) topBound)) {
                    }
                }
                if (Math.abs(overscrollX) > 0.0f) {
                }
                if (Math.abs(f) > 0.0f) {
                }
            }
            if (r0.mScrollAxis == 1) {
            }
            z = false;
            couldScroll = z;
            if (mode != 0) {
                if (couldScroll) {
                }
                deltaX = limit(deltaX, ((float) leftBound) - scrollX, ((float) rightBound) - scrollX);
                deltaY = limit(deltaY, ((float) topBound) - scrollY, ((float) bottomBound) - scrollY);
                i2 = targetX;
                i = scrollable;
            }
            if (scrollX > ((float) rightBound)) {
            }
            overscrollX = f;
            if (scrollY > ((float) bottomBound)) {
            }
            if (Math.abs(overscrollX) > 0.0f) {
            }
            if (Math.abs(f) > 0.0f) {
            }
        }
        scrollX += deltaX;
        f2 = scrollY + deltaY;
        r0.mPointerLastX += scrollX - ((float) ((int) scrollX));
        r0.mPointerLastY += f2 - ((float) ((int) f2));
        scrollTo((int) scrollX, (int) f2);
        pageScrolled((int) scrollX, (int) f2);
        return true;
    }

    private int getScrollableDistance(View child, int dir) {
        if (child instanceof CardScrollView) {
            return ((CardScrollView) child).getAvailableScrollDelta(dir);
        }
        if (child instanceof ScrollView) {
            return getScrollableDistance((ScrollView) child, dir);
        }
        return 0;
    }

    private int getScrollableDistance(ScrollView view, int direction) {
        if (view.getChildCount() <= 0) {
            return 0;
        }
        View content = view.getChildAt(0);
        int height = view.getHeight();
        int contentHeight = content.getHeight();
        int extra = contentHeight - height;
        if (contentHeight <= height) {
            return 0;
        }
        if (direction > 0) {
            return Math.min(extra - view.getScrollY(), 0);
        }
        if (direction < 0) {
            return -view.getScrollY();
        }
        return 0;
    }

    private View getChildForInfo(ItemInfo ii) {
        if (ii.object != null) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (this.mAdapter.isViewFromObject(child, ii.object)) {
                    return child;
                }
            }
        }
        return null;
    }

    private ItemInfo infoForCurrentScrollPosition() {
        return infoForScrollPosition(getRowScrollX((int) getYIndex((float) getScrollY())), getScrollY());
    }

    private ItemInfo infoForScrollPosition(int scrollX, int scrollY) {
        int y = (int) getYIndex((float) scrollY);
        int x = (int) getXIndex((float) scrollX);
        ItemInfo ii = infoForPosition(x, y);
        if (ii != null) {
            return ii;
        }
        ii = new ItemInfo();
        ii.positionX = x;
        ii.positionY = y;
        return ii;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = MotionEventCompat.getActionIndex(ev);
        if (MotionEventCompat.getPointerId(ev, pointerIndex) == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            this.mPointerLastX = MotionEventCompat.getX(ev, newPointerIndex);
            this.mPointerLastY = MotionEventCompat.getY(ev, newPointerIndex);
            this.mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.clear();
            }
        }
    }

    private void endDrag() {
        this.mIsBeingDragged = false;
        this.mIsAbleToDrag = false;
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    public boolean canScrollHorizontally(int direction) {
        boolean z = false;
        if (getVisibility() == 0 && this.mAdapter != null) {
            if (!this.mItems.isEmpty()) {
                int scrollX = getRowScrollX(this.mCurItem.y);
                int lastColumnIndex = this.mExpectedCurrentColumnCount - 1;
                if (direction > 0) {
                    if (getPaddingLeft() + scrollX < computePageLeft(lastColumnIndex)) {
                        z = true;
                    }
                    return z;
                }
                if (scrollX > 0) {
                    z = true;
                }
                return z;
            }
        }
        return false;
    }

    public boolean canScrollVertically(int direction) {
        boolean z = false;
        if (getVisibility() == 0 && this.mAdapter != null) {
            if (!this.mItems.isEmpty()) {
                int scrollY = getScrollY();
                int lastRowIndex = this.mExpectedRowCount - 1;
                if (direction > 0) {
                    if (getPaddingTop() + scrollY < computePageTop(lastRowIndex)) {
                        z = true;
                    }
                    return z;
                }
                if (scrollY > 0) {
                    z = true;
                }
                return z;
            }
        }
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!super.dispatchKeyEvent(event)) {
            if (!executeKeyEvent(event)) {
                return false;
            }
        }
        return true;
    }

    private boolean executeKeyEvent(KeyEvent event) {
        boolean handled = false;
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case 19:
                handled = pageUp();
                break;
            case 20:
                handled = pageDown();
                break;
            case 21:
                handled = pageLeft();
                break;
            case 22:
                handled = pageRight();
                break;
            default:
                switch (keyCode) {
                    case 61:
                        break;
                    case 62:
                        debug();
                        return true;
                    default:
                        break;
                }
        }
        return handled;
    }

    private boolean pageLeft() {
        if (this.mCurItem.x <= 0) {
            return false;
        }
        setCurrentItem(this.mCurItem.x - 1, this.mCurItem.y, true);
        return true;
    }

    private boolean pageRight() {
        if (this.mAdapter == null || this.mCurItem.x >= this.mAdapter.getColumnCount(this.mCurItem.y) - 1) {
            return false;
        }
        setCurrentItem(this.mCurItem.x + 1, this.mCurItem.y, true);
        return true;
    }

    private boolean pageUp() {
        if (this.mCurItem.y <= 0) {
            return false;
        }
        setCurrentItem(this.mCurItem.x, this.mCurItem.y - 1, true);
        return true;
    }

    private boolean pageDown() {
        if (this.mAdapter == null || this.mCurItem.y >= this.mAdapter.getRowCount() - 1) {
            return false;
        }
        setCurrentItem(this.mCurItem.x, this.mCurItem.y + 1, true);
        return true;
    }

    private boolean handlePointerDown(MotionEvent ev) {
        if (this.mIsBeingDragged) {
            return false;
        }
        this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
        this.mGestureInitialX = ev.getX();
        this.mGestureInitialY = ev.getY();
        this.mGestureInitialScrollY = getScrollY();
        this.mPointerLastX = this.mGestureInitialX;
        this.mPointerLastY = this.mGestureInitialY;
        this.mIsAbleToDrag = true;
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mVelocityTracker.addMovement(ev);
        this.mScroller.computeScrollOffset();
        int i = this.mScrollState;
        if ((i == 2 || i == 3) && this.mScrollAxis == 0) {
            if (Math.abs(this.mScroller.getFinalX() - this.mScroller.getCurrX()) > this.mCloseEnough) {
                this.mScroller.abortAnimation();
                this.mDelayPopulate = false;
                populate();
                this.mIsBeingDragged = true;
                requestParentDisallowInterceptTouchEvent(true);
                setScrollState(1);
                return false;
            }
        }
        if (this.mScrollAxis == 1) {
            if (Math.abs(this.mScroller.getFinalY() - this.mScroller.getCurrY()) <= this.mCloseEnough) {
            }
            this.mScroller.abortAnimation();
            this.mDelayPopulate = false;
            populate();
            this.mIsBeingDragged = true;
            requestParentDisallowInterceptTouchEvent(true);
            setScrollState(1);
            return false;
        }
        completeScroll(false);
        this.mIsBeingDragged = false;
        return false;
    }

    private boolean handlePointerMove(MotionEvent ev) {
        MotionEvent motionEvent = ev;
        int activePointerId = this.mActivePointerId;
        if (activePointerId == -1) {
            return false;
        }
        int pointerIndex = motionEvent.findPointerIndex(activePointerId);
        if (pointerIndex == -1) {
            return r0.mIsBeingDragged;
        }
        float x;
        float x2 = MotionEventCompat.getX(motionEvent, pointerIndex);
        float y = MotionEventCompat.getY(motionEvent, pointerIndex);
        float dx = x2 - r0.mPointerLastX;
        float xDiff = Math.abs(dx);
        float dy = y - r0.mPointerLastY;
        float yDiff = Math.abs(dy);
        if (r0.mIsBeingDragged) {
            x = x2;
        } else if ((xDiff * xDiff) + (yDiff * yDiff) > ((float) r0.mTouchSlopSquared)) {
            float sy;
            r0.mIsBeingDragged = true;
            requestParentDisallowInterceptTouchEvent(true);
            setScrollState(1);
            if (yDiff >= xDiff) {
                r0.mScrollAxis = 1;
            } else {
                r0.mScrollAxis = 0;
            }
            if (yDiff <= 0.0f || xDiff <= 0.0f) {
                x = x2;
                if (yDiff == 0) {
                    activePointerId = (float) r0.mTouchSlop;
                    sy = 0.0f;
                } else {
                    activePointerId = 0;
                    sy = (float) r0.mTouchSlop;
                }
            } else {
                x = x2;
                double h = Math.hypot((double) xDiff, (double) yDiff);
                double d = (double) xDiff;
                Double.isNaN(d);
                d = Math.acos(d / h);
                double sin = Math.sin(d);
                double t = d;
                double d2 = (double) r0.mTouchSlop;
                Double.isNaN(d2);
                sy = (float) (d2 * sin);
                d = Math.cos(t);
                activePointerId = (double) r0.mTouchSlop;
                Double.isNaN(activePointerId);
                activePointerId = (float) (d * activePointerId);
            }
            r0.mPointerLastX = dx > 0.0f ? r0.mPointerLastX + activePointerId : r0.mPointerLastX - activePointerId;
            r0.mPointerLastY = dy > 0.0f ? r0.mPointerLastY + sy : r0.mPointerLastY - sy;
        } else {
            x = x2;
        }
        if (r0.mIsBeingDragged) {
            if (performDrag(r0.mScrollAxis == 0 ? x : r0.mPointerLastX, r0.mScrollAxis == 1 ? y : r0.mPointerLastY)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
        r0.mVelocityTracker.addMovement(motionEvent);
        return r0.mIsBeingDragged;
    }

    private boolean handlePointerUp(MotionEvent ev) {
        boolean z;
        MotionEvent motionEvent = ev;
        if (!this.mIsBeingDragged) {
            z = false;
        } else if (r8.mExpectedRowCount == 0) {
            z = false;
        } else {
            int velocity;
            VelocityTracker velocityTracker = r8.mVelocityTracker;
            velocityTracker.addMovement(motionEvent);
            velocityTracker.computeCurrentVelocity(1000);
            int activePointerIndex = motionEvent.findPointerIndex(r8.mActivePointerId);
            int targetPageX = r8.mCurItem.x;
            int targetPageY = r8.mCurItem.y;
            ItemInfo ii = infoForCurrentScrollPosition();
            ItemInfo ii2;
            int xVelocity;
            int currentPageX;
            int distanceX;
            int i;
            int i2;
            switch (r8.mScrollAxis) {
                case 0:
                    ii2 = ii;
                    int totalDeltaX = (int) (ev.getRawX() - r8.mGestureInitialX);
                    xVelocity = (int) velocityTracker.getXVelocity(r8.mActivePointerId);
                    currentPageX = ii2.positionX;
                    distanceX = getRowScrollX(ii2.positionY) - computePageLeft(ii2.positionX);
                    float pageOffsetX = getXIndex((float) distanceX);
                    i = r8.mCurItem.x;
                    i2 = r8.mPopulatedPages.left;
                    distanceX = i2;
                    currentPageX = r8.mPopulatedPages.right;
                    velocity = xVelocity;
                    targetPageX = determineTargetPage(i, currentPageX, pageOffsetX, distanceX, currentPageX, xVelocity, totalDeltaX);
                    break;
                case 1:
                    float y = motionEvent.getX(activePointerIndex);
                    int totalDeltaY = r8.mGestureInitialScrollY - getScrollY();
                    xVelocity = (int) velocityTracker.getYVelocity(r8.mActivePointerId);
                    currentPageX = ii.positionY;
                    distanceX = getScrollY() - computePageTop(ii.positionY);
                    float pageOffsetY = getYIndex((float) distanceX);
                    if (pageOffsetY != 0.0f) {
                        i = r8.mCurItem.y;
                        i2 = r8.mPopulatedPages.top;
                        distanceX = i2;
                        currentPageX = r8.mPopulatedPages.bottom;
                        int velocity2 = xVelocity;
                        ii2 = ii;
                        targetPageY = determineTargetPage(i, currentPageX, pageOffsetY, distanceX, currentPageX, xVelocity, totalDeltaY);
                        velocity = velocity2;
                        break;
                    }
                    View child = getChildForInfo(infoForCurrentScrollPosition());
                    i = getScrollableDistance(child, -xVelocity);
                    if (i != 0) {
                        r8.mScrollingContent = child;
                        if (Math.abs(xVelocity) >= Math.abs(r8.mMinFlingVelocity)) {
                            flingContent(0, i, 0, -xVelocity);
                            endDrag();
                        }
                    }
                    velocity = xVelocity;
                    ii2 = ii;
                    break;
                default:
                    velocity = 0;
                    break;
            }
            if (r8.mScrollState != 3) {
                r8.mDelayPopulate = true;
                if (targetPageY != r8.mCurItem.y) {
                    targetPageX = r8.mAdapter.getCurrentColumnForRow(targetPageY, r8.mCurItem.x);
                }
                setCurrentItemInternal(targetPageY, targetPageX, true, true, velocity);
            }
            r8.mActivePointerId = -1;
            endDrag();
            return false;
        }
        r8.mActivePointerId = -1;
        endDrag();
        return z;
    }

    private float getXIndex(float distanceX) {
        int width = getContentWidth() + this.mColMargin;
        if (width != 0) {
            return distanceX / ((float) width);
        }
        Log.e(TAG, "getXIndex() called with zero width.");
        return 0.0f;
    }

    private float getYIndex(float distanceY) {
        int height = getContentHeight() + this.mRowMargin;
        if (height != 0) {
            return distanceY / ((float) height);
        }
        Log.e(TAG, "getYIndex() called with zero height.");
        return 0.0f;
    }

    private int determineTargetPage(int previousPage, int currentPage, float pageOffset, int firstPage, int lastPage, int velocity, int totalDragDistance) {
        int targetPage;
        if (Math.abs(velocity) < this.mMinUsableVelocity) {
            velocity = (int) Math.copySign((float) velocity, (float) totalDragDistance);
        }
        float flingBoost = (0.5f / Math.max(Math.abs(0.5f - pageOffset), 0.001f)) * 100.0f;
        if (Math.abs(totalDragDistance) > this.mMinFlingDistance) {
            if (((float) Math.abs(velocity)) + flingBoost > ((float) this.mMinFlingVelocity)) {
                targetPage = velocity > 0 ? currentPage : currentPage + 1;
                return limit(targetPage, firstPage, lastPage);
            }
        }
        targetPage = Math.round(((float) currentPage) + pageOffset);
        return limit(targetPage, firstPage, lastPage);
    }

    private static int limit(int val, int min, int max) {
        if (val < min) {
            return min;
        }
        if (val > max) {
            return max;
        }
        return val;
    }

    private static float limit(float val, float min, float max) {
        if (val < min) {
            return min;
        }
        if (val > max) {
            return max;
        }
        return val;
    }

    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return generateDefaultLayoutParams();
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return (p instanceof LayoutParams) && super.checkLayoutParams(p);
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public void debug() {
        debug(0);
    }

    protected void debug(int depth) {
        super.debug(depth);
        String valueOf = String.valueOf(debugIndent(depth));
        String valueOf2 = String.valueOf(this.mCurItem);
        StringBuilder stringBuilder = new StringBuilder((String.valueOf(valueOf).length() + 11) + String.valueOf(valueOf2).length());
        stringBuilder.append(valueOf);
        stringBuilder.append("mCurItem={");
        stringBuilder.append(valueOf2);
        stringBuilder.append("}");
        Log.d("View", stringBuilder.toString());
        valueOf = String.valueOf(debugIndent(depth));
        valueOf2 = String.valueOf(this.mAdapter);
        stringBuilder = new StringBuilder((String.valueOf(valueOf).length() + 11) + String.valueOf(valueOf2).length());
        stringBuilder.append(valueOf);
        stringBuilder.append("mAdapter={");
        stringBuilder.append(valueOf2);
        stringBuilder.append("}");
        Log.d("View", stringBuilder.toString());
        valueOf = String.valueOf(debugIndent(depth));
        int i = this.mExpectedRowCount;
        stringBuilder = new StringBuilder(String.valueOf(valueOf).length() + 21);
        stringBuilder.append(valueOf);
        stringBuilder.append("mRowCount=");
        stringBuilder.append(i);
        Log.d("View", stringBuilder.toString());
        valueOf = String.valueOf(debugIndent(depth));
        i = this.mExpectedCurrentColumnCount;
        stringBuilder = new StringBuilder(String.valueOf(valueOf).length() + 31);
        stringBuilder.append(valueOf);
        stringBuilder.append("mCurrentColumnCount=");
        stringBuilder.append(i);
        Log.d("View", stringBuilder.toString());
        int count = this.mItems.size();
        if (count != 0) {
            Log.d("View", String.valueOf(debugIndent(depth)).concat("mItems={"));
        }
        for (i = 0; i < count; i++) {
            String valueOf3 = String.valueOf(debugIndent(depth + 1));
            String valueOf4 = String.valueOf(this.mItems.keyAt(i));
            String valueOf5 = String.valueOf(this.mItems.valueAt(i));
            StringBuilder stringBuilder2 = new StringBuilder(((String.valueOf(valueOf3).length() + 4) + String.valueOf(valueOf4).length()) + String.valueOf(valueOf5).length());
            stringBuilder2.append(valueOf3);
            stringBuilder2.append(valueOf4);
            stringBuilder2.append(" => ");
            stringBuilder2.append(valueOf5);
            Log.d("View", stringBuilder2.toString());
        }
        if (count != 0) {
            Log.d("View", String.valueOf(debugIndent(depth)).concat("}"));
        }
    }

    private static String debugIndent(int depth) {
        StringBuilder spaces = new StringBuilder(((depth * 2) + 3) * 2);
        for (int i = 0; i < (depth * 2) + 3; i++) {
            spaces.append(' ');
            spaces.append(' ');
        }
        return spaces.toString();
    }

    private static boolean inRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    private boolean pointInRange(int x, int y) {
        return inRange(y, 0, this.mExpectedRowCount - 1) && inRange(x, 0, this.mAdapter.getColumnCount(y) - 1);
    }
}
