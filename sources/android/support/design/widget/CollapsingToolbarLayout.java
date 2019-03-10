package android.support.design.widget;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.design.C0035R;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.math.MathUtils;
import android.support.v4.util.ObjectsCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.widget.ViewGroupUtils;
import android.support.v7.appcompat.C0286R;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.widget.FrameLayout;

public class CollapsingToolbarLayout extends FrameLayout {
    private static final int DEFAULT_SCRIM_ANIMATION_DURATION = 600;
    final CollapsingTextHelper mCollapsingTextHelper;
    private boolean mCollapsingTitleEnabled;
    private Drawable mContentScrim;
    int mCurrentOffset;
    private boolean mDrawCollapsingTitle;
    private View mDummyView;
    private int mExpandedMarginBottom;
    private int mExpandedMarginEnd;
    private int mExpandedMarginStart;
    private int mExpandedMarginTop;
    WindowInsetsCompat mLastInsets;
    private OnOffsetChangedListener mOnOffsetChangedListener;
    private boolean mRefreshToolbar;
    private int mScrimAlpha;
    private long mScrimAnimationDuration;
    private ValueAnimator mScrimAnimator;
    private int mScrimVisibleHeightTrigger;
    private boolean mScrimsAreShown;
    Drawable mStatusBarScrim;
    private final Rect mTmpRect;
    private Toolbar mToolbar;
    private View mToolbarDirectChild;
    private int mToolbarId;

    /* renamed from: android.support.design.widget.CollapsingToolbarLayout$2 */
    class C00532 implements AnimatorUpdateListener {
        C00532() {
        }

        public void onAnimationUpdate(ValueAnimator animator) {
            CollapsingToolbarLayout.this.setScrimAlpha(((Integer) animator.getAnimatedValue()).intValue());
        }
    }

    public static class LayoutParams extends android.widget.FrameLayout.LayoutParams {
        public static final int COLLAPSE_MODE_OFF = 0;
        public static final int COLLAPSE_MODE_PARALLAX = 2;
        public static final int COLLAPSE_MODE_PIN = 1;
        private static final float DEFAULT_PARALLAX_MULTIPLIER = 0.5f;
        int mCollapseMode = 0;
        float mParallaxMult = DEFAULT_PARALLAX_MULTIPLIER;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, C0035R.styleable.CollapsingToolbarLayout_Layout);
            this.mCollapseMode = a.getInt(C0035R.styleable.CollapsingToolbarLayout_Layout_layout_collapseMode, 0);
            setParallaxMultiplier(a.getFloat(C0035R.styleable.CollapsingToolbarLayout_Layout_layout_collapseParallaxMultiplier, DEFAULT_PARALLAX_MULTIPLIER));
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        @RequiresApi(19)
        public LayoutParams(android.widget.FrameLayout.LayoutParams source) {
            super(source);
        }

        public void setCollapseMode(int collapseMode) {
            this.mCollapseMode = collapseMode;
        }

        public int getCollapseMode() {
            return this.mCollapseMode;
        }

        public void setParallaxMultiplier(float multiplier) {
            this.mParallaxMult = multiplier;
        }

        public float getParallaxMultiplier() {
            return this.mParallaxMult;
        }
    }

    /* renamed from: android.support.design.widget.CollapsingToolbarLayout$1 */
    class C08191 implements OnApplyWindowInsetsListener {
        C08191() {
        }

        public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
            return CollapsingToolbarLayout.this.onWindowInsetChanged(insets);
        }
    }

    private class OffsetUpdateListener implements OnOffsetChangedListener {
        OffsetUpdateListener() {
        }

        public void onOffsetChanged(AppBarLayout layout, int verticalOffset) {
            CollapsingToolbarLayout collapsingToolbarLayout = CollapsingToolbarLayout.this;
            collapsingToolbarLayout.mCurrentOffset = verticalOffset;
            int insetTop = collapsingToolbarLayout.mLastInsets != null ? CollapsingToolbarLayout.this.mLastInsets.getSystemWindowInsetTop() : 0;
            int z = CollapsingToolbarLayout.this.getChildCount();
            for (int i = 0; i < z; i++) {
                View child = CollapsingToolbarLayout.this.getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                ViewOffsetHelper offsetHelper = CollapsingToolbarLayout.getViewOffsetHelper(child);
                switch (lp.mCollapseMode) {
                    case 1:
                        offsetHelper.setTopAndBottomOffset(MathUtils.clamp(-verticalOffset, 0, CollapsingToolbarLayout.this.getMaxOffsetForPinChild(child)));
                        break;
                    case 2:
                        offsetHelper.setTopAndBottomOffset(Math.round(((float) (-verticalOffset)) * lp.mParallaxMult));
                        break;
                    default:
                        break;
                }
            }
            CollapsingToolbarLayout.this.updateScrimVisibility();
            if (CollapsingToolbarLayout.this.mStatusBarScrim != null && insetTop > 0) {
                ViewCompat.postInvalidateOnAnimation(CollapsingToolbarLayout.this);
            }
            CollapsingToolbarLayout.this.mCollapsingTextHelper.setExpansionFraction(((float) Math.abs(verticalOffset)) / ((float) ((CollapsingToolbarLayout.this.getHeight() - ViewCompat.getMinimumHeight(CollapsingToolbarLayout.this)) - insetTop)));
        }
    }

    public CollapsingToolbarLayout(Context context) {
        this(context, null);
    }

    public CollapsingToolbarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mRefreshToolbar = true;
        this.mTmpRect = new Rect();
        this.mScrimVisibleHeightTrigger = -1;
        ThemeUtils.checkAppCompatTheme(context);
        this.mCollapsingTextHelper = new CollapsingTextHelper(this);
        this.mCollapsingTextHelper.setTextSizeInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
        TypedArray a = context.obtainStyledAttributes(attrs, C0035R.styleable.CollapsingToolbarLayout, defStyleAttr, C0035R.style.Widget_Design_CollapsingToolbar);
        this.mCollapsingTextHelper.setExpandedTextGravity(a.getInt(C0035R.styleable.CollapsingToolbarLayout_expandedTitleGravity, 8388691));
        this.mCollapsingTextHelper.setCollapsedTextGravity(a.getInt(C0035R.styleable.CollapsingToolbarLayout_collapsedTitleGravity, 8388627));
        int dimensionPixelSize = a.getDimensionPixelSize(C0035R.styleable.CollapsingToolbarLayout_expandedTitleMargin, 0);
        this.mExpandedMarginBottom = dimensionPixelSize;
        this.mExpandedMarginEnd = dimensionPixelSize;
        this.mExpandedMarginTop = dimensionPixelSize;
        this.mExpandedMarginStart = dimensionPixelSize;
        if (a.hasValue(C0035R.styleable.CollapsingToolbarLayout_expandedTitleMarginStart)) {
            this.mExpandedMarginStart = a.getDimensionPixelSize(C0035R.styleable.CollapsingToolbarLayout_expandedTitleMarginStart, 0);
        }
        if (a.hasValue(C0035R.styleable.CollapsingToolbarLayout_expandedTitleMarginEnd)) {
            this.mExpandedMarginEnd = a.getDimensionPixelSize(C0035R.styleable.CollapsingToolbarLayout_expandedTitleMarginEnd, 0);
        }
        if (a.hasValue(C0035R.styleable.CollapsingToolbarLayout_expandedTitleMarginTop)) {
            this.mExpandedMarginTop = a.getDimensionPixelSize(C0035R.styleable.CollapsingToolbarLayout_expandedTitleMarginTop, 0);
        }
        if (a.hasValue(C0035R.styleable.CollapsingToolbarLayout_expandedTitleMarginBottom)) {
            this.mExpandedMarginBottom = a.getDimensionPixelSize(C0035R.styleable.CollapsingToolbarLayout_expandedTitleMarginBottom, 0);
        }
        this.mCollapsingTitleEnabled = a.getBoolean(C0035R.styleable.CollapsingToolbarLayout_titleEnabled, true);
        setTitle(a.getText(C0035R.styleable.CollapsingToolbarLayout_title));
        this.mCollapsingTextHelper.setExpandedTextAppearance(C0035R.style.TextAppearance_Design_CollapsingToolbar_Expanded);
        this.mCollapsingTextHelper.setCollapsedTextAppearance(C0286R.style.TextAppearance_AppCompat_Widget_ActionBar_Title);
        if (a.hasValue(C0035R.styleable.CollapsingToolbarLayout_expandedTitleTextAppearance)) {
            this.mCollapsingTextHelper.setExpandedTextAppearance(a.getResourceId(C0035R.styleable.CollapsingToolbarLayout_expandedTitleTextAppearance, 0));
        }
        if (a.hasValue(C0035R.styleable.CollapsingToolbarLayout_collapsedTitleTextAppearance)) {
            this.mCollapsingTextHelper.setCollapsedTextAppearance(a.getResourceId(C0035R.styleable.CollapsingToolbarLayout_collapsedTitleTextAppearance, 0));
        }
        this.mScrimVisibleHeightTrigger = a.getDimensionPixelSize(C0035R.styleable.CollapsingToolbarLayout_scrimVisibleHeightTrigger, -1);
        this.mScrimAnimationDuration = (long) a.getInt(C0035R.styleable.CollapsingToolbarLayout_scrimAnimationDuration, DEFAULT_SCRIM_ANIMATION_DURATION);
        setContentScrim(a.getDrawable(C0035R.styleable.CollapsingToolbarLayout_contentScrim));
        setStatusBarScrim(a.getDrawable(C0035R.styleable.CollapsingToolbarLayout_statusBarScrim));
        this.mToolbarId = a.getResourceId(C0035R.styleable.CollapsingToolbarLayout_toolbarId, -1);
        a.recycle();
        setWillNotDraw(false);
        ViewCompat.setOnApplyWindowInsetsListener(this, new C08191());
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent parent = getParent();
        if (parent instanceof AppBarLayout) {
            ViewCompat.setFitsSystemWindows(this, ViewCompat.getFitsSystemWindows((View) parent));
            if (this.mOnOffsetChangedListener == null) {
                this.mOnOffsetChangedListener = new OffsetUpdateListener();
            }
            ((AppBarLayout) parent).addOnOffsetChangedListener(this.mOnOffsetChangedListener);
            ViewCompat.requestApplyInsets(this);
        }
    }

    protected void onDetachedFromWindow() {
        ViewParent parent = getParent();
        OnOffsetChangedListener onOffsetChangedListener = this.mOnOffsetChangedListener;
        if (onOffsetChangedListener != null && (parent instanceof AppBarLayout)) {
            ((AppBarLayout) parent).removeOnOffsetChangedListener(onOffsetChangedListener);
        }
        super.onDetachedFromWindow();
    }

    WindowInsetsCompat onWindowInsetChanged(WindowInsetsCompat insets) {
        WindowInsetsCompat newInsets = null;
        if (ViewCompat.getFitsSystemWindows(this)) {
            newInsets = insets;
        }
        if (!ObjectsCompat.equals(this.mLastInsets, newInsets)) {
            this.mLastInsets = newInsets;
            requestLayout();
        }
        return insets.consumeSystemWindowInsets();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        ensureToolbar();
        if (this.mToolbar == null) {
            Drawable drawable = this.mContentScrim;
            if (drawable != null && this.mScrimAlpha > 0) {
                drawable.mutate().setAlpha(this.mScrimAlpha);
                this.mContentScrim.draw(canvas);
                if (!this.mCollapsingTitleEnabled && this.mDrawCollapsingTitle) {
                    this.mCollapsingTextHelper.draw(canvas);
                }
                if (this.mStatusBarScrim == null && this.mScrimAlpha > 0) {
                    WindowInsetsCompat windowInsetsCompat = this.mLastInsets;
                    int topInset = windowInsetsCompat != null ? windowInsetsCompat.getSystemWindowInsetTop() : 0;
                    if (topInset > 0) {
                        this.mStatusBarScrim.setBounds(0, -this.mCurrentOffset, getWidth(), topInset - this.mCurrentOffset);
                        this.mStatusBarScrim.mutate().setAlpha(this.mScrimAlpha);
                        this.mStatusBarScrim.draw(canvas);
                        return;
                    }
                    return;
                }
            }
        }
        if (!this.mCollapsingTitleEnabled) {
        }
        if (this.mStatusBarScrim == null) {
        }
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean invalidated = false;
        if (this.mContentScrim != null && this.mScrimAlpha > 0 && isToolbarChild(child)) {
            this.mContentScrim.mutate().setAlpha(this.mScrimAlpha);
            this.mContentScrim.draw(canvas);
            invalidated = true;
        }
        if (!super.drawChild(canvas, child, drawingTime)) {
            if (!invalidated) {
                return false;
            }
        }
        return true;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Drawable drawable = this.mContentScrim;
        if (drawable != null) {
            drawable.setBounds(0, 0, w, h);
        }
    }

    private void ensureToolbar() {
        if (this.mRefreshToolbar) {
            this.mToolbar = null;
            this.mToolbarDirectChild = null;
            int i = this.mToolbarId;
            if (i != -1) {
                this.mToolbar = (Toolbar) findViewById(i);
                View view = this.mToolbar;
                if (view != null) {
                    this.mToolbarDirectChild = findDirectChild(view);
                }
            }
            if (this.mToolbar == null) {
                Toolbar toolbar = null;
                int count = getChildCount();
                for (int i2 = 0; i2 < count; i2++) {
                    View child = getChildAt(i2);
                    if (child instanceof Toolbar) {
                        toolbar = (Toolbar) child;
                        break;
                    }
                }
                this.mToolbar = toolbar;
            }
            updateDummyView();
            this.mRefreshToolbar = false;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isToolbarChild(android.view.View r4) {
        /*
        r3 = this;
        r0 = r3.mToolbarDirectChild;
        r1 = 1;
        r2 = 0;
        if (r0 == 0) goto L_0x000c;
    L_0x0006:
        if (r0 != r3) goto L_0x0009;
    L_0x0008:
        goto L_0x000c;
    L_0x0009:
        if (r4 != r0) goto L_0x0011;
    L_0x000b:
        goto L_0x0010;
    L_0x000c:
        r0 = r3.mToolbar;
        if (r4 != r0) goto L_0x0011;
    L_0x0010:
        goto L_0x0012;
    L_0x0011:
        r1 = 0;
    L_0x0012:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.design.widget.CollapsingToolbarLayout.isToolbarChild(android.view.View):boolean");
    }

    private View findDirectChild(View descendant) {
        View directChild = descendant;
        View p = descendant.getParent();
        while (p != this && p != null) {
            if (p instanceof View) {
                directChild = p;
            }
            p = p.getParent();
        }
        return directChild;
    }

    private void updateDummyView() {
        if (!this.mCollapsingTitleEnabled) {
            ViewParent parent = this.mDummyView;
            if (parent != null) {
                parent = parent.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(this.mDummyView);
                }
                if (!this.mCollapsingTitleEnabled && this.mToolbar != null) {
                    if (this.mDummyView == null) {
                        this.mDummyView = new View(getContext());
                    }
                    if (this.mDummyView.getParent() == null) {
                        this.mToolbar.addView(this.mDummyView, -1, -1);
                        return;
                    }
                    return;
                }
            }
        }
        if (!this.mCollapsingTitleEnabled) {
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ensureToolbar();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        WindowInsetsCompat windowInsetsCompat = this.mLastInsets;
        int topInset = windowInsetsCompat != null ? windowInsetsCompat.getSystemWindowInsetTop() : 0;
        if (mode == 0 && topInset > 0) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(getMeasuredHeight() + topInset, 1073741824));
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int i;
        View view;
        super.onLayout(changed, left, top, right, bottom);
        int insetTop = this.mLastInsets;
        if (insetTop != 0) {
            insetTop = insetTop.getSystemWindowInsetTop();
            int z = getChildCount();
            for (i = 0; i < z; i++) {
                View child = getChildAt(i);
                if (!ViewCompat.getFitsSystemWindows(child)) {
                    if (child.getTop() < insetTop) {
                        ViewCompat.offsetTopAndBottom(child, insetTop);
                    }
                }
            }
        }
        if (this.mCollapsingTitleEnabled) {
            view = this.mDummyView;
            if (view != null) {
                boolean z2;
                CollapsingTextHelper collapsingTextHelper;
                int i2;
                int titleMarginStart;
                int i3;
                int titleMarginEnd;
                boolean z3 = false;
                if (ViewCompat.isAttachedToWindow(view)) {
                    if (this.mDummyView.getVisibility() == 0) {
                        z2 = true;
                        this.mDrawCollapsingTitle = z2;
                        if (this.mDrawCollapsingTitle) {
                            if (ViewCompat.getLayoutDirection(this) == 1) {
                                z3 = true;
                            }
                            z2 = z3;
                            i = this.mToolbarDirectChild;
                            if (i != 0) {
                                i = this.mToolbar;
                            }
                            i = getMaxOffsetForPinChild(i);
                            ViewGroupUtils.getDescendantRect(this, this.mDummyView, this.mTmpRect);
                            collapsingTextHelper = this.mCollapsingTextHelper;
                            i2 = this.mTmpRect.left;
                            if (z2) {
                                titleMarginStart = this.mToolbar.getTitleMarginStart();
                            } else {
                                titleMarginStart = this.mToolbar.getTitleMarginEnd();
                            }
                            i2 += titleMarginStart;
                            titleMarginStart = (this.mTmpRect.top + i) + this.mToolbar.getTitleMarginTop();
                            i3 = this.mTmpRect.right;
                            if (z2) {
                                titleMarginEnd = this.mToolbar.getTitleMarginEnd();
                            } else {
                                titleMarginEnd = this.mToolbar.getTitleMarginStart();
                            }
                            collapsingTextHelper.setCollapsedBounds(i2, titleMarginStart, i3 + titleMarginEnd, (this.mTmpRect.bottom + i) - this.mToolbar.getTitleMarginBottom());
                            this.mCollapsingTextHelper.setExpandedBounds(z2 ? this.mExpandedMarginEnd : this.mExpandedMarginStart, this.mTmpRect.top + this.mExpandedMarginTop, (right - left) - (z2 ? this.mExpandedMarginStart : this.mExpandedMarginEnd), (bottom - top) - this.mExpandedMarginBottom);
                            this.mCollapsingTextHelper.recalculate();
                        }
                        i = getChildCount();
                        for (insetTop = 0; insetTop < i; insetTop++) {
                            getViewOffsetHelper(getChildAt(insetTop)).onViewLayout();
                        }
                        if (this.mToolbar == null) {
                            if (!this.mCollapsingTitleEnabled && TextUtils.isEmpty(this.mCollapsingTextHelper.getText())) {
                                this.mCollapsingTextHelper.setText(this.mToolbar.getTitle());
                            }
                            view = this.mToolbarDirectChild;
                            if (view != null) {
                                if (view == this) {
                                    setMinimumHeight(getHeightWithMargins(view));
                                }
                            }
                            setMinimumHeight(getHeightWithMargins(this.mToolbar));
                        }
                        updateScrimVisibility();
                    }
                }
                z2 = false;
                this.mDrawCollapsingTitle = z2;
                if (this.mDrawCollapsingTitle) {
                    if (ViewCompat.getLayoutDirection(this) == 1) {
                        z3 = true;
                    }
                    z2 = z3;
                    i = this.mToolbarDirectChild;
                    if (i != 0) {
                        i = this.mToolbar;
                    }
                    i = getMaxOffsetForPinChild(i);
                    ViewGroupUtils.getDescendantRect(this, this.mDummyView, this.mTmpRect);
                    collapsingTextHelper = this.mCollapsingTextHelper;
                    i2 = this.mTmpRect.left;
                    if (z2) {
                        titleMarginStart = this.mToolbar.getTitleMarginStart();
                    } else {
                        titleMarginStart = this.mToolbar.getTitleMarginEnd();
                    }
                    i2 += titleMarginStart;
                    titleMarginStart = (this.mTmpRect.top + i) + this.mToolbar.getTitleMarginTop();
                    i3 = this.mTmpRect.right;
                    if (z2) {
                        titleMarginEnd = this.mToolbar.getTitleMarginEnd();
                    } else {
                        titleMarginEnd = this.mToolbar.getTitleMarginStart();
                    }
                    collapsingTextHelper.setCollapsedBounds(i2, titleMarginStart, i3 + titleMarginEnd, (this.mTmpRect.bottom + i) - this.mToolbar.getTitleMarginBottom());
                    if (z2) {
                    }
                    if (z2) {
                    }
                    this.mCollapsingTextHelper.setExpandedBounds(z2 ? this.mExpandedMarginEnd : this.mExpandedMarginStart, this.mTmpRect.top + this.mExpandedMarginTop, (right - left) - (z2 ? this.mExpandedMarginStart : this.mExpandedMarginEnd), (bottom - top) - this.mExpandedMarginBottom);
                    this.mCollapsingTextHelper.recalculate();
                }
                i = getChildCount();
                for (insetTop = 0; insetTop < i; insetTop++) {
                    getViewOffsetHelper(getChildAt(insetTop)).onViewLayout();
                }
                if (this.mToolbar == null) {
                    if (!this.mCollapsingTitleEnabled) {
                    }
                    view = this.mToolbarDirectChild;
                    if (view != null) {
                        if (view == this) {
                            setMinimumHeight(getHeightWithMargins(view));
                        }
                    }
                    setMinimumHeight(getHeightWithMargins(this.mToolbar));
                }
                updateScrimVisibility();
            }
        }
        i = getChildCount();
        for (insetTop = 0; insetTop < i; insetTop++) {
            getViewOffsetHelper(getChildAt(insetTop)).onViewLayout();
        }
        if (this.mToolbar == null) {
            if (!this.mCollapsingTitleEnabled) {
            }
            view = this.mToolbarDirectChild;
            if (view != null) {
                if (view == this) {
                    setMinimumHeight(getHeightWithMargins(view));
                }
            }
            setMinimumHeight(getHeightWithMargins(this.mToolbar));
        }
        updateScrimVisibility();
    }

    private static int getHeightWithMargins(@NonNull View view) {
        android.view.ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (!(lp instanceof MarginLayoutParams)) {
            return view.getHeight();
        }
        MarginLayoutParams mlp = (MarginLayoutParams) lp;
        return (view.getHeight() + mlp.topMargin) + mlp.bottomMargin;
    }

    static ViewOffsetHelper getViewOffsetHelper(View view) {
        ViewOffsetHelper offsetHelper = (ViewOffsetHelper) view.getTag(C0035R.id.view_offset_helper);
        if (offsetHelper != null) {
            return offsetHelper;
        }
        offsetHelper = new ViewOffsetHelper(view);
        view.setTag(C0035R.id.view_offset_helper, offsetHelper);
        return offsetHelper;
    }

    public void setTitle(@Nullable CharSequence title) {
        this.mCollapsingTextHelper.setText(title);
    }

    @Nullable
    public CharSequence getTitle() {
        return this.mCollapsingTitleEnabled ? this.mCollapsingTextHelper.getText() : null;
    }

    public void setTitleEnabled(boolean enabled) {
        if (enabled != this.mCollapsingTitleEnabled) {
            this.mCollapsingTitleEnabled = enabled;
            updateDummyView();
            requestLayout();
        }
    }

    public boolean isTitleEnabled() {
        return this.mCollapsingTitleEnabled;
    }

    public void setScrimsShown(boolean shown) {
        boolean z = ViewCompat.isLaidOut(this) && !isInEditMode();
        setScrimsShown(shown, z);
    }

    public void setScrimsShown(boolean shown, boolean animate) {
        if (this.mScrimsAreShown != shown) {
            int i = 255;
            if (animate) {
                if (!shown) {
                    i = 0;
                }
                animateScrim(i);
            } else {
                if (!shown) {
                    i = 0;
                }
                setScrimAlpha(i);
            }
            this.mScrimsAreShown = shown;
        }
    }

    private void animateScrim(int targetAlpha) {
        ensureToolbar();
        ValueAnimator valueAnimator = this.mScrimAnimator;
        if (valueAnimator == null) {
            this.mScrimAnimator = new ValueAnimator();
            this.mScrimAnimator.setDuration(this.mScrimAnimationDuration);
            this.mScrimAnimator.setInterpolator(targetAlpha > this.mScrimAlpha ? AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR : AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR);
            this.mScrimAnimator.addUpdateListener(new C00532());
        } else if (valueAnimator.isRunning()) {
            this.mScrimAnimator.cancel();
        }
        this.mScrimAnimator.setIntValues(new int[]{this.mScrimAlpha, targetAlpha});
        this.mScrimAnimator.start();
    }

    void setScrimAlpha(int alpha) {
        if (alpha != this.mScrimAlpha) {
            if (this.mContentScrim != null) {
                View view = this.mToolbar;
                if (view != null) {
                    ViewCompat.postInvalidateOnAnimation(view);
                    this.mScrimAlpha = alpha;
                    ViewCompat.postInvalidateOnAnimation(this);
                }
            }
            this.mScrimAlpha = alpha;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    int getScrimAlpha() {
        return this.mScrimAlpha;
    }

    public void setContentScrim(@Nullable Drawable drawable) {
        Drawable drawable2 = this.mContentScrim;
        if (drawable2 != drawable) {
            Drawable drawable3 = null;
            if (drawable2 != null) {
                drawable2.setCallback(null);
            }
            if (drawable != null) {
                drawable3 = drawable.mutate();
            }
            this.mContentScrim = drawable3;
            drawable2 = this.mContentScrim;
            if (drawable2 != null) {
                drawable2.setBounds(0, 0, getWidth(), getHeight());
                this.mContentScrim.setCallback(this);
                this.mContentScrim.setAlpha(this.mScrimAlpha);
            }
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setContentScrimColor(@ColorInt int color) {
        setContentScrim(new ColorDrawable(color));
    }

    public void setContentScrimResource(@DrawableRes int resId) {
        setContentScrim(ContextCompat.getDrawable(getContext(), resId));
    }

    @Nullable
    public Drawable getContentScrim() {
        return this.mContentScrim;
    }

    public void setStatusBarScrim(@Nullable Drawable drawable) {
        Drawable drawable2 = this.mStatusBarScrim;
        if (drawable2 != drawable) {
            Drawable drawable3 = null;
            if (drawable2 != null) {
                drawable2.setCallback(null);
            }
            if (drawable != null) {
                drawable3 = drawable.mutate();
            }
            this.mStatusBarScrim = drawable3;
            drawable2 = this.mStatusBarScrim;
            if (drawable2 != null) {
                if (drawable2.isStateful()) {
                    this.mStatusBarScrim.setState(getDrawableState());
                }
                DrawableCompat.setLayoutDirection(this.mStatusBarScrim, ViewCompat.getLayoutDirection(this));
                this.mStatusBarScrim.setVisible(getVisibility() == 0, false);
                this.mStatusBarScrim.setCallback(this);
                this.mStatusBarScrim.setAlpha(this.mScrimAlpha);
            }
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] state = getDrawableState();
        boolean changed = false;
        Drawable d = this.mStatusBarScrim;
        if (d != null && d.isStateful()) {
            changed = false | d.setState(state);
        }
        d = this.mContentScrim;
        if (d != null && d.isStateful()) {
            changed |= d.setState(state);
        }
        CollapsingTextHelper collapsingTextHelper = this.mCollapsingTextHelper;
        if (collapsingTextHelper != null) {
            changed |= collapsingTextHelper.setState(state);
        }
        if (changed) {
            invalidate();
        }
    }

    protected boolean verifyDrawable(Drawable who) {
        if (!(super.verifyDrawable(who) || who == this.mContentScrim)) {
            if (who != this.mStatusBarScrim) {
                return false;
            }
        }
        return true;
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        boolean visible = visibility == 0;
        Drawable drawable = this.mStatusBarScrim;
        if (drawable != null && drawable.isVisible() != visible) {
            this.mStatusBarScrim.setVisible(visible, false);
        }
        drawable = this.mContentScrim;
        if (drawable != null && drawable.isVisible() != visible) {
            this.mContentScrim.setVisible(visible, false);
        }
    }

    public void setStatusBarScrimColor(@ColorInt int color) {
        setStatusBarScrim(new ColorDrawable(color));
    }

    public void setStatusBarScrimResource(@DrawableRes int resId) {
        setStatusBarScrim(ContextCompat.getDrawable(getContext(), resId));
    }

    @Nullable
    public Drawable getStatusBarScrim() {
        return this.mStatusBarScrim;
    }

    public void setCollapsedTitleTextAppearance(@StyleRes int resId) {
        this.mCollapsingTextHelper.setCollapsedTextAppearance(resId);
    }

    public void setCollapsedTitleTextColor(@ColorInt int color) {
        setCollapsedTitleTextColor(ColorStateList.valueOf(color));
    }

    public void setCollapsedTitleTextColor(@NonNull ColorStateList colors) {
        this.mCollapsingTextHelper.setCollapsedTextColor(colors);
    }

    public void setCollapsedTitleGravity(int gravity) {
        this.mCollapsingTextHelper.setCollapsedTextGravity(gravity);
    }

    public int getCollapsedTitleGravity() {
        return this.mCollapsingTextHelper.getCollapsedTextGravity();
    }

    public void setExpandedTitleTextAppearance(@StyleRes int resId) {
        this.mCollapsingTextHelper.setExpandedTextAppearance(resId);
    }

    public void setExpandedTitleColor(@ColorInt int color) {
        setExpandedTitleTextColor(ColorStateList.valueOf(color));
    }

    public void setExpandedTitleTextColor(@NonNull ColorStateList colors) {
        this.mCollapsingTextHelper.setExpandedTextColor(colors);
    }

    public void setExpandedTitleGravity(int gravity) {
        this.mCollapsingTextHelper.setExpandedTextGravity(gravity);
    }

    public int getExpandedTitleGravity() {
        return this.mCollapsingTextHelper.getExpandedTextGravity();
    }

    public void setCollapsedTitleTypeface(@Nullable Typeface typeface) {
        this.mCollapsingTextHelper.setCollapsedTypeface(typeface);
    }

    @NonNull
    public Typeface getCollapsedTitleTypeface() {
        return this.mCollapsingTextHelper.getCollapsedTypeface();
    }

    public void setExpandedTitleTypeface(@Nullable Typeface typeface) {
        this.mCollapsingTextHelper.setExpandedTypeface(typeface);
    }

    @NonNull
    public Typeface getExpandedTitleTypeface() {
        return this.mCollapsingTextHelper.getExpandedTypeface();
    }

    public void setExpandedTitleMargin(int start, int top, int end, int bottom) {
        this.mExpandedMarginStart = start;
        this.mExpandedMarginTop = top;
        this.mExpandedMarginEnd = end;
        this.mExpandedMarginBottom = bottom;
        requestLayout();
    }

    public int getExpandedTitleMarginStart() {
        return this.mExpandedMarginStart;
    }

    public void setExpandedTitleMarginStart(int margin) {
        this.mExpandedMarginStart = margin;
        requestLayout();
    }

    public int getExpandedTitleMarginTop() {
        return this.mExpandedMarginTop;
    }

    public void setExpandedTitleMarginTop(int margin) {
        this.mExpandedMarginTop = margin;
        requestLayout();
    }

    public int getExpandedTitleMarginEnd() {
        return this.mExpandedMarginEnd;
    }

    public void setExpandedTitleMarginEnd(int margin) {
        this.mExpandedMarginEnd = margin;
        requestLayout();
    }

    public int getExpandedTitleMarginBottom() {
        return this.mExpandedMarginBottom;
    }

    public void setExpandedTitleMarginBottom(int margin) {
        this.mExpandedMarginBottom = margin;
        requestLayout();
    }

    public void setScrimVisibleHeightTrigger(@IntRange(from = 0) int height) {
        if (this.mScrimVisibleHeightTrigger != height) {
            this.mScrimVisibleHeightTrigger = height;
            updateScrimVisibility();
        }
    }

    public int getScrimVisibleHeightTrigger() {
        int i = this.mScrimVisibleHeightTrigger;
        if (i >= 0) {
            return i;
        }
        WindowInsetsCompat windowInsetsCompat = this.mLastInsets;
        i = windowInsetsCompat != null ? windowInsetsCompat.getSystemWindowInsetTop() : 0;
        int minHeight = ViewCompat.getMinimumHeight(this);
        if (minHeight > 0) {
            return Math.min((minHeight * 2) + i, getHeight());
        }
        return getHeight() / 3;
    }

    public void setScrimAnimationDuration(@IntRange(from = 0) long duration) {
        this.mScrimAnimationDuration = duration;
    }

    public long getScrimAnimationDuration() {
        return this.mScrimAnimationDuration;
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -1);
    }

    public android.widget.FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    protected android.widget.FrameLayout.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    final void updateScrimVisibility() {
        if (this.mContentScrim == null) {
            if (this.mStatusBarScrim == null) {
                return;
            }
        }
        setScrimsShown(getHeight() + this.mCurrentOffset < getScrimVisibleHeightTrigger());
    }

    final int getMaxOffsetForPinChild(View child) {
        return ((getHeight() - getViewOffsetHelper(child).getLayoutTop()) - child.getHeight()) - ((LayoutParams) child.getLayoutParams()).bottomMargin;
    }
}
