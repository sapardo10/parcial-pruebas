package android.support.wearable.view.drawer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.wearable.C0395R;
import android.support.wearable.internal.view.drawer.MultiPagePresenter;
import android.support.wearable.internal.view.drawer.MultiPageUi;
import android.support.wearable.internal.view.drawer.SinglePagePresenter;
import android.support.wearable.internal.view.drawer.SinglePageUi;
import android.support.wearable.internal.view.drawer.WearableNavigationDrawerPresenter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityManager;
import java.util.concurrent.TimeUnit;

@TargetApi(23)
@Deprecated
public class WearableNavigationDrawer extends WearableDrawerView {
    private static final long AUTO_CLOSE_DRAWER_DELAY_MS = TimeUnit.SECONDS.toMillis(5);
    private static final int DEFAULT_STYLE = 1;
    private static final String TAG = "WearableNavDrawer";
    private final Runnable mCloseDrawerRunnable;
    @Nullable
    private final GestureDetector mGestureDetector;
    private final boolean mIsAccessibilityEnabled;
    private final Handler mMainThreadHandler;
    private final SimpleOnGestureListener mOnGestureListener;
    private final WearableNavigationDrawerPresenter mPresenter;

    /* renamed from: android.support.wearable.view.drawer.WearableNavigationDrawer$1 */
    class C04691 implements Runnable {
        C04691() {
        }

        public void run() {
            WearableNavigationDrawer.this.closeDrawer();
        }
    }

    /* renamed from: android.support.wearable.view.drawer.WearableNavigationDrawer$2 */
    class C04702 extends SimpleOnGestureListener {
        C04702() {
        }

        public boolean onSingleTapUp(MotionEvent e) {
            return WearableNavigationDrawer.this.mPresenter.onDrawerTapped();
        }
    }

    public static abstract class WearableNavigationDrawerAdapter {
        @Nullable
        private WearableNavigationDrawerPresenter mPresenter;

        public abstract int getCount();

        public abstract Drawable getItemDrawable(int i);

        public abstract String getItemText(int i);

        public abstract void onItemSelected(int i);

        public void notifyDataSetChanged() {
            WearableNavigationDrawerPresenter wearableNavigationDrawerPresenter = this.mPresenter;
            if (wearableNavigationDrawerPresenter != null) {
                wearableNavigationDrawerPresenter.onDataSetChanged();
            } else {
                Log.w(WearableNavigationDrawer.TAG, "adapter.notifyDataSetChanged called before drawer.setAdapter; ignoring.");
            }
        }

        public void setPresenter(WearableNavigationDrawerPresenter presenter) {
            this.mPresenter = presenter;
        }
    }

    public WearableNavigationDrawer(Context context) {
        this(context, (AttributeSet) null);
    }

    public WearableNavigationDrawer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableNavigationDrawer(Context context, AttributeSet attrs, int defStyleAttr) {
        WearableNavigationDrawerPresenter singlePagePresenter;
        super(context, attrs, defStyleAttr);
        this.mMainThreadHandler = new Handler(Looper.getMainLooper());
        this.mCloseDrawerRunnable = new C04691();
        this.mOnGestureListener = new C04702();
        this.mGestureDetector = new GestureDetector(getContext(), this.mOnGestureListener);
        boolean singlePage = false;
        if (attrs != null) {
            boolean z = false;
            TypedArray typedArray = context.obtainStyledAttributes(attrs, C0395R.styleable.WearableNavigationDrawer, defStyleAttr, 0);
            try {
                if (typedArray.getInt(C0395R.styleable.WearableNavigationDrawer_navigation_style, 1) == 0) {
                    z = true;
                }
                singlePage = z;
                typedArray.recycle();
            } catch (Throwable th) {
                typedArray.recycle();
            }
        }
        this.mIsAccessibilityEnabled = ((AccessibilityManager) context.getSystemService("accessibility")).isEnabled();
        if (singlePage) {
            singlePagePresenter = new SinglePagePresenter(new SinglePageUi(this), this.mIsAccessibilityEnabled);
        } else {
            singlePagePresenter = new MultiPagePresenter(this, new MultiPageUi(), this.mIsAccessibilityEnabled);
        }
        this.mPresenter = singlePagePresenter;
        getPeekContainer().setContentDescription(context.getString(C0395R.string.navigation_drawer_content_description));
        setShouldOnlyOpenWhenAtTop(true);
    }

    @VisibleForTesting
    public WearableNavigationDrawer(Context context, WearableNavigationDrawerPresenter presenter, GestureDetector gestureDetector) {
        super(context);
        this.mMainThreadHandler = new Handler(Looper.getMainLooper());
        this.mCloseDrawerRunnable = new C04691();
        this.mOnGestureListener = new C04702();
        this.mPresenter = presenter;
        this.mGestureDetector = gestureDetector;
        this.mIsAccessibilityEnabled = false;
    }

    public void setAdapter(WearableNavigationDrawerAdapter adapter) {
        this.mPresenter.onNewAdapter(adapter);
    }

    public void setCurrentItem(int index, boolean smoothScrollTo) {
        this.mPresenter.onSetCurrentItemRequested(index, smoothScrollTo);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        autoCloseDrawerAfterDelay();
        GestureDetector gestureDetector = this.mGestureDetector;
        return gestureDetector != null && gestureDetector.onTouchEvent(ev);
    }

    public boolean canScrollHorizontally(int direction) {
        return isOpened();
    }

    public void onDrawerOpened() {
        autoCloseDrawerAfterDelay();
    }

    public void onDrawerClosed() {
        this.mMainThreadHandler.removeCallbacks(this.mCloseDrawerRunnable);
    }

    private void autoCloseDrawerAfterDelay() {
        if (!this.mIsAccessibilityEnabled) {
            this.mMainThreadHandler.removeCallbacks(this.mCloseDrawerRunnable);
            this.mMainThreadHandler.postDelayed(this.mCloseDrawerRunnable, AUTO_CLOSE_DRAWER_DELAY_MS);
        }
    }

    int preferGravity() {
        return 48;
    }
}
