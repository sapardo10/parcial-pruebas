package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.design.C0035R;
import android.support.design.widget.SwipeDismissBehavior.OnDismissListener;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseTransientBottomBar<B extends BaseTransientBottomBar<B>> {
    static final int ANIMATION_DURATION = 250;
    static final int ANIMATION_FADE_DURATION = 180;
    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_LONG = 0;
    public static final int LENGTH_SHORT = -1;
    static final int MSG_DISMISS = 1;
    static final int MSG_SHOW = 0;
    private static final boolean USE_OFFSET_API;
    static final Handler sHandler = new Handler(Looper.getMainLooper(), new C00431());
    private final AccessibilityManager mAccessibilityManager;
    private List<BaseCallback<B>> mCallbacks;
    private final ContentViewCallback mContentViewCallback;
    private final Context mContext;
    private int mDuration;
    final Callback mManagerCallback = new C08103();
    private final ViewGroup mTargetParent;
    final SnackbarBaseLayout mView;

    /* renamed from: android.support.design.widget.BaseTransientBottomBar$1 */
    static class C00431 implements Callback {
        C00431() {
        }

        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    ((BaseTransientBottomBar) message.obj).showView();
                    return true;
                case 1:
                    ((BaseTransientBottomBar) message.obj).hideView(message.arg1);
                    return true;
                default:
                    return false;
            }
        }
    }

    /* renamed from: android.support.design.widget.BaseTransientBottomBar$7 */
    class C00457 extends AnimatorListenerAdapter {
        C00457() {
        }

        public void onAnimationStart(Animator animator) {
            BaseTransientBottomBar.this.mContentViewCallback.animateContentIn(70, BaseTransientBottomBar.ANIMATION_FADE_DURATION);
        }

        public void onAnimationEnd(Animator animator) {
            BaseTransientBottomBar.this.onViewShown();
        }
    }

    /* renamed from: android.support.design.widget.BaseTransientBottomBar$9 */
    class C00479 implements AnimationListener {
        C00479() {
        }

        public void onAnimationEnd(Animation animation) {
            BaseTransientBottomBar.this.onViewShown();
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    public static abstract class BaseCallback<B> {
        public static final int DISMISS_EVENT_ACTION = 1;
        public static final int DISMISS_EVENT_CONSECUTIVE = 4;
        public static final int DISMISS_EVENT_MANUAL = 3;
        public static final int DISMISS_EVENT_SWIPE = 0;
        public static final int DISMISS_EVENT_TIMEOUT = 2;

        public void onDismissed(B b, int event) {
        }

        public void onShown(B b) {
        }
    }

    public interface ContentViewCallback {
        void animateContentIn(int i, int i2);

        void animateContentOut(int i, int i2);
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    interface OnAttachStateChangeListener {
        void onViewAttachedToWindow(View view);

        void onViewDetachedFromWindow(View view);
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    interface OnLayoutChangeListener {
        void onLayoutChange(View view, int i, int i2, int i3, int i4);
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    static class SnackbarBaseLayout extends FrameLayout {
        private OnAttachStateChangeListener mOnAttachStateChangeListener;
        private OnLayoutChangeListener mOnLayoutChangeListener;

        SnackbarBaseLayout(Context context) {
            this(context, null);
        }

        SnackbarBaseLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, C0035R.styleable.SnackbarLayout);
            if (a.hasValue(C0035R.styleable.SnackbarLayout_elevation)) {
                ViewCompat.setElevation(this, (float) a.getDimensionPixelSize(C0035R.styleable.SnackbarLayout_elevation, 0));
            }
            a.recycle();
            setClickable(true);
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            OnLayoutChangeListener onLayoutChangeListener = this.mOnLayoutChangeListener;
            if (onLayoutChangeListener != null) {
                onLayoutChangeListener.onLayoutChange(this, l, t, r, b);
            }
        }

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            OnAttachStateChangeListener onAttachStateChangeListener = this.mOnAttachStateChangeListener;
            if (onAttachStateChangeListener != null) {
                onAttachStateChangeListener.onViewAttachedToWindow(this);
            }
            ViewCompat.requestApplyInsets(this);
        }

        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            OnAttachStateChangeListener onAttachStateChangeListener = this.mOnAttachStateChangeListener;
            if (onAttachStateChangeListener != null) {
                onAttachStateChangeListener.onViewDetachedFromWindow(this);
            }
        }

        void setOnLayoutChangeListener(OnLayoutChangeListener onLayoutChangeListener) {
            this.mOnLayoutChangeListener = onLayoutChangeListener;
        }

        void setOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
            this.mOnAttachStateChangeListener = listener;
        }
    }

    /* renamed from: android.support.design.widget.BaseTransientBottomBar$2 */
    class C08092 implements OnApplyWindowInsetsListener {
        C08092() {
        }

        public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), insets.getSystemWindowInsetBottom());
            return insets;
        }
    }

    /* renamed from: android.support.design.widget.BaseTransientBottomBar$3 */
    class C08103 implements Callback {
        C08103() {
        }

        public void show() {
            BaseTransientBottomBar.sHandler.sendMessage(BaseTransientBottomBar.sHandler.obtainMessage(0, BaseTransientBottomBar.this));
        }

        public void dismiss(int event) {
            BaseTransientBottomBar.sHandler.sendMessage(BaseTransientBottomBar.sHandler.obtainMessage(1, event, 0, BaseTransientBottomBar.this));
        }
    }

    /* renamed from: android.support.design.widget.BaseTransientBottomBar$4 */
    class C08114 implements OnDismissListener {
        C08114() {
        }

        public void onDismiss(View view) {
            view.setVisibility(8);
            BaseTransientBottomBar.this.dispatchDismiss(0);
        }

        public void onDragStateChanged(int state) {
            switch (state) {
                case 0:
                    SnackbarManager.getInstance().restoreTimeoutIfPaused(BaseTransientBottomBar.this.mManagerCallback);
                    return;
                case 1:
                case 2:
                    SnackbarManager.getInstance().pauseTimeout(BaseTransientBottomBar.this.mManagerCallback);
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: android.support.design.widget.BaseTransientBottomBar$5 */
    class C08125 implements OnAttachStateChangeListener {

        /* renamed from: android.support.design.widget.BaseTransientBottomBar$5$1 */
        class C00441 implements Runnable {
            C00441() {
            }

            public void run() {
                BaseTransientBottomBar.this.onViewHidden(3);
            }
        }

        C08125() {
        }

        public void onViewAttachedToWindow(View v) {
        }

        public void onViewDetachedFromWindow(View v) {
            if (BaseTransientBottomBar.this.isShownOrQueued()) {
                BaseTransientBottomBar.sHandler.post(new C00441());
            }
        }
    }

    /* renamed from: android.support.design.widget.BaseTransientBottomBar$6 */
    class C08136 implements OnLayoutChangeListener {
        C08136() {
        }

        public void onLayoutChange(View view, int left, int top, int right, int bottom) {
            BaseTransientBottomBar.this.mView.setOnLayoutChangeListener(null);
            if (BaseTransientBottomBar.this.shouldAnimate()) {
                BaseTransientBottomBar.this.animateViewIn();
            } else {
                BaseTransientBottomBar.this.onViewShown();
            }
        }
    }

    final class Behavior extends SwipeDismissBehavior<SnackbarBaseLayout> {
        Behavior() {
        }

        public boolean canSwipeDismissView(View child) {
            return child instanceof SnackbarBaseLayout;
        }

        public boolean onInterceptTouchEvent(CoordinatorLayout parent, SnackbarBaseLayout child, MotionEvent event) {
            int actionMasked = event.getActionMasked();
            if (actionMasked != 3) {
                switch (actionMasked) {
                    case 0:
                        if (!parent.isPointInChildBounds(child, (int) event.getX(), (int) event.getY())) {
                            break;
                        }
                        SnackbarManager.getInstance().pauseTimeout(BaseTransientBottomBar.this.mManagerCallback);
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
            }
            SnackbarManager.getInstance().restoreTimeoutIfPaused(BaseTransientBottomBar.this.mManagerCallback);
            return super.onInterceptTouchEvent(parent, child, event);
        }
    }

    static {
        boolean z = VERSION.SDK_INT >= 16 && VERSION.SDK_INT <= 19;
        USE_OFFSET_API = z;
    }

    protected BaseTransientBottomBar(@NonNull ViewGroup parent, @NonNull View content, @NonNull ContentViewCallback contentViewCallback) {
        if (parent == null) {
            throw new IllegalArgumentException("Transient bottom bar must have non-null parent");
        } else if (content == null) {
            throw new IllegalArgumentException("Transient bottom bar must have non-null content");
        } else if (contentViewCallback != null) {
            this.mTargetParent = parent;
            this.mContentViewCallback = contentViewCallback;
            this.mContext = parent.getContext();
            ThemeUtils.checkAppCompatTheme(this.mContext);
            this.mView = (SnackbarBaseLayout) LayoutInflater.from(this.mContext).inflate(C0035R.layout.design_layout_snackbar, this.mTargetParent, false);
            this.mView.addView(content);
            ViewCompat.setAccessibilityLiveRegion(this.mView, 1);
            ViewCompat.setImportantForAccessibility(this.mView, 1);
            ViewCompat.setFitsSystemWindows(this.mView, true);
            ViewCompat.setOnApplyWindowInsetsListener(this.mView, new C08092());
            this.mAccessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
        } else {
            throw new IllegalArgumentException("Transient bottom bar must have non-null callback");
        }
    }

    @NonNull
    public B setDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

    public int getDuration() {
        return this.mDuration;
    }

    @NonNull
    public Context getContext() {
        return this.mContext;
    }

    @NonNull
    public View getView() {
        return this.mView;
    }

    public void show() {
        SnackbarManager.getInstance().show(this.mDuration, this.mManagerCallback);
    }

    public void dismiss() {
        dispatchDismiss(3);
    }

    void dispatchDismiss(int event) {
        SnackbarManager.getInstance().dismiss(this.mManagerCallback, event);
    }

    @NonNull
    public B addCallback(@NonNull BaseCallback<B> callback) {
        if (callback == null) {
            return this;
        }
        if (this.mCallbacks == null) {
            this.mCallbacks = new ArrayList();
        }
        this.mCallbacks.add(callback);
        return this;
    }

    @NonNull
    public B removeCallback(@NonNull BaseCallback<B> callback) {
        if (callback == null) {
            return this;
        }
        List list = this.mCallbacks;
        if (list == null) {
            return this;
        }
        list.remove(callback);
        return this;
    }

    public boolean isShown() {
        return SnackbarManager.getInstance().isCurrent(this.mManagerCallback);
    }

    public boolean isShownOrQueued() {
        return SnackbarManager.getInstance().isCurrentOrNext(this.mManagerCallback);
    }

    final void showView() {
        if (this.mView.getParent() == null) {
            LayoutParams lp = this.mView.getLayoutParams();
            if (lp instanceof CoordinatorLayout.LayoutParams) {
                CoordinatorLayout.LayoutParams clp = (CoordinatorLayout.LayoutParams) lp;
                Behavior behavior = new Behavior();
                behavior.setStartAlphaSwipeDistance(0.1f);
                behavior.setEndAlphaSwipeDistance(0.6f);
                behavior.setSwipeDirection(0);
                behavior.setListener(new C08114());
                clp.setBehavior(behavior);
                clp.insetEdge = 80;
            }
            this.mTargetParent.addView(this.mView);
        }
        this.mView.setOnAttachStateChangeListener(new C08125());
        if (!ViewCompat.isLaidOut(this.mView)) {
            this.mView.setOnLayoutChangeListener(new C08136());
        } else if (shouldAnimate()) {
            animateViewIn();
        } else {
            onViewShown();
        }
    }

    void animateViewIn() {
        if (VERSION.SDK_INT >= 12) {
            final int viewHeight = this.mView.getHeight();
            if (USE_OFFSET_API) {
                ViewCompat.offsetTopAndBottom(this.mView, viewHeight);
            } else {
                this.mView.setTranslationY((float) viewHeight);
            }
            ValueAnimator animator = new ValueAnimator();
            animator.setIntValues(new int[]{viewHeight, 0});
            animator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            animator.setDuration(250);
            animator.addListener(new C00457());
            animator.addUpdateListener(new AnimatorUpdateListener() {
                private int mPreviousAnimatedIntValue = viewHeight;

                public void onAnimationUpdate(ValueAnimator animator) {
                    int currentAnimatedIntValue = ((Integer) animator.getAnimatedValue()).intValue();
                    if (BaseTransientBottomBar.USE_OFFSET_API) {
                        ViewCompat.offsetTopAndBottom(BaseTransientBottomBar.this.mView, currentAnimatedIntValue - this.mPreviousAnimatedIntValue);
                    } else {
                        BaseTransientBottomBar.this.mView.setTranslationY((float) currentAnimatedIntValue);
                    }
                    this.mPreviousAnimatedIntValue = currentAnimatedIntValue;
                }
            });
            animator.start();
            return;
        }
        Animation anim = AnimationUtils.loadAnimation(this.mView.getContext(), C0035R.anim.design_snackbar_in);
        anim.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        anim.setDuration(250);
        anim.setAnimationListener(new C00479());
        this.mView.startAnimation(anim);
    }

    private void animateViewOut(final int event) {
        if (VERSION.SDK_INT >= 12) {
            ValueAnimator animator = new ValueAnimator();
            animator.setIntValues(new int[]{0, this.mView.getHeight()});
            animator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            animator.setDuration(250);
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    BaseTransientBottomBar.this.mContentViewCallback.animateContentOut(0, BaseTransientBottomBar.ANIMATION_FADE_DURATION);
                }

                public void onAnimationEnd(Animator animator) {
                    BaseTransientBottomBar.this.onViewHidden(event);
                }
            });
            animator.addUpdateListener(new AnimatorUpdateListener() {
                private int mPreviousAnimatedIntValue = 0;

                public void onAnimationUpdate(ValueAnimator animator) {
                    int currentAnimatedIntValue = ((Integer) animator.getAnimatedValue()).intValue();
                    if (BaseTransientBottomBar.USE_OFFSET_API) {
                        ViewCompat.offsetTopAndBottom(BaseTransientBottomBar.this.mView, currentAnimatedIntValue - this.mPreviousAnimatedIntValue);
                    } else {
                        BaseTransientBottomBar.this.mView.setTranslationY((float) currentAnimatedIntValue);
                    }
                    this.mPreviousAnimatedIntValue = currentAnimatedIntValue;
                }
            });
            animator.start();
            return;
        }
        Animation anim = AnimationUtils.loadAnimation(this.mView.getContext(), C0035R.anim.design_snackbar_out);
        anim.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        anim.setDuration(250);
        anim.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                BaseTransientBottomBar.this.onViewHidden(event);
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
        this.mView.startAnimation(anim);
    }

    final void hideView(int event) {
        if (shouldAnimate() && this.mView.getVisibility() == 0) {
            animateViewOut(event);
        } else {
            onViewHidden(event);
        }
    }

    void onViewShown() {
        SnackbarManager.getInstance().onShown(this.mManagerCallback);
        int callbackCount = this.mCallbacks;
        if (callbackCount != 0) {
            for (int i = callbackCount.size() - 1; i >= 0; i--) {
                ((BaseCallback) this.mCallbacks.get(i)).onShown(this);
            }
        }
    }

    void onViewHidden(int event) {
        SnackbarManager.getInstance().onDismissed(this.mManagerCallback);
        int callbackCount = this.mCallbacks;
        if (callbackCount != 0) {
            for (int i = callbackCount.size() - 1; i >= 0; i--) {
                ((BaseCallback) this.mCallbacks.get(i)).onDismissed(this, event);
            }
        }
        if (VERSION.SDK_INT < 11) {
            this.mView.setVisibility(8);
        }
        ViewParent parent = this.mView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(this.mView);
        }
    }

    boolean shouldAnimate() {
        return this.mAccessibilityManager.isEnabled() ^ 1;
    }
}
