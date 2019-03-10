package com.afollestad.materialdialogs.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ScrollView;
import com.afollestad.materialdialogs.C0498R;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog.NotImplementedException;
import com.afollestad.materialdialogs.StackingBehavior;
import com.afollestad.materialdialogs.util.DialogUtils;

public class MDRootLayout extends ViewGroup {
    private static final int INDEX_NEGATIVE = 1;
    private static final int INDEX_NEUTRAL = 0;
    private static final int INDEX_POSITIVE = 2;
    private OnScrollChangedListener mBottomOnScrollChangedListener;
    private int mButtonBarHeight;
    private GravityEnum mButtonGravity;
    private int mButtonHorizontalEdgeMargin;
    private int mButtonPaddingFull;
    private final MDButton[] mButtons;
    private View mContent;
    private Paint mDividerPaint;
    private int mDividerWidth;
    private boolean mDrawBottomDivider;
    private boolean mDrawTopDivider;
    private boolean mIsStacked;
    private boolean mNoTitleNoPadding;
    private int mNoTitlePaddingFull;
    private boolean mReducePaddingNoTitleNoButtons;
    private StackingBehavior mStackBehavior;
    private View mTitleBar;
    private OnScrollChangedListener mTopOnScrollChangedListener;
    private boolean mUseFullPadding;

    public MDRootLayout(Context context) {
        super(context);
        this.mDrawTopDivider = false;
        this.mDrawBottomDivider = false;
        this.mButtons = new MDButton[3];
        this.mStackBehavior = StackingBehavior.ADAPTIVE;
        this.mIsStacked = false;
        this.mUseFullPadding = true;
        this.mButtonGravity = GravityEnum.START;
        init(context, null, 0);
    }

    public MDRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDrawTopDivider = false;
        this.mDrawBottomDivider = false;
        this.mButtons = new MDButton[3];
        this.mStackBehavior = StackingBehavior.ADAPTIVE;
        this.mIsStacked = false;
        this.mUseFullPadding = true;
        this.mButtonGravity = GravityEnum.START;
        init(context, attrs, 0);
    }

    @TargetApi(11)
    public MDRootLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mDrawTopDivider = false;
        this.mDrawBottomDivider = false;
        this.mButtons = new MDButton[3];
        this.mStackBehavior = StackingBehavior.ADAPTIVE;
        this.mIsStacked = false;
        this.mUseFullPadding = true;
        this.mButtonGravity = GravityEnum.START;
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public MDRootLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mDrawTopDivider = false;
        this.mDrawBottomDivider = false;
        this.mButtons = new MDButton[3];
        this.mStackBehavior = StackingBehavior.ADAPTIVE;
        this.mIsStacked = false;
        this.mUseFullPadding = true;
        this.mButtonGravity = GravityEnum.START;
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        Resources r = context.getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, C0498R.styleable.MDRootLayout, defStyleAttr, 0);
        this.mReducePaddingNoTitleNoButtons = a.getBoolean(C0498R.styleable.MDRootLayout_md_reduce_padding_no_title_no_buttons, true);
        a.recycle();
        this.mNoTitlePaddingFull = r.getDimensionPixelSize(C0498R.dimen.md_notitle_vertical_padding);
        this.mButtonPaddingFull = r.getDimensionPixelSize(C0498R.dimen.md_button_frame_vertical_padding);
        this.mButtonHorizontalEdgeMargin = r.getDimensionPixelSize(C0498R.dimen.md_button_padding_frame_side);
        this.mButtonBarHeight = r.getDimensionPixelSize(C0498R.dimen.md_button_height);
        this.mDividerPaint = new Paint();
        this.mDividerWidth = r.getDimensionPixelSize(C0498R.dimen.md_divider_height);
        this.mDividerPaint.setColor(DialogUtils.resolveColor(context, C0498R.attr.md_divider_color));
        setWillNotDraw(false);
    }

    public void noTitleNoPadding() {
        this.mNoTitleNoPadding = true;
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v.getId() == C0498R.id.md_titleFrame) {
                this.mTitleBar = v;
            } else if (v.getId() == C0498R.id.md_buttonDefaultNeutral) {
                this.mButtons[0] = (MDButton) v;
            } else if (v.getId() == C0498R.id.md_buttonDefaultNegative) {
                this.mButtons[1] = (MDButton) v;
            } else if (v.getId() == C0498R.id.md_buttonDefaultPositive) {
                this.mButtons[2] = (MDButton) v;
            } else {
                this.mContent = v;
            }
        }
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean stacked;
        int stackedHeight;
        int i;
        int i2 = widthMeasureSpec;
        int i3 = heightMeasureSpec;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        this.mUseFullPadding = true;
        boolean z = false;
        if (this.mStackBehavior == StackingBehavior.ALWAYS) {
            stacked = true;
        } else if (r0.mStackBehavior == StackingBehavior.NEVER) {
            stacked = false;
        } else {
            int buttonsWidth = 0;
            boolean hasButtons = false;
            for (MDButton button : r0.mButtons) {
                if (button != null && isVisible(button)) {
                    button.setStacked(false, false);
                    measureChild(button, i2, i3);
                    buttonsWidth += button.getMeasuredWidth();
                    hasButtons = true;
                }
            }
            stacked = buttonsWidth > width - (getContext().getResources().getDimensionPixelSize(C0498R.dimen.md_neutral_button_margin) * 2);
            z = hasButtons;
        }
        int stackedHeight2 = 0;
        r0.mIsStacked = stacked;
        if (stacked) {
            stackedHeight = 0;
            boolean hasButtons2 = z;
            for (MDButton button2 : r0.mButtons) {
                if (button2 != null && isVisible(button2)) {
                    button2.setStacked(true, false);
                    measureChild(button2, i2, i3);
                    stackedHeight += button2.getMeasuredHeight();
                    hasButtons2 = true;
                }
            }
            z = hasButtons2;
            stackedHeight2 = stackedHeight;
        }
        int i4 = height;
        stackedHeight = 0;
        if (!z) {
            i = 0 + (r0.mButtonPaddingFull * 2);
        } else if (r0.mIsStacked) {
            i4 -= stackedHeight2;
            int i5 = r0.mButtonPaddingFull;
            i = 0 + (i5 * 2);
            stackedHeight = 0 + (i5 * 2);
        } else {
            i4 -= r0.mButtonBarHeight;
            i = 0 + (r0.mButtonPaddingFull * 2);
        }
        if (isVisible(r0.mTitleBar)) {
            r0.mTitleBar.measure(MeasureSpec.makeMeasureSpec(width, 1073741824), 0);
            i4 -= r0.mTitleBar.getMeasuredHeight();
        } else if (!r0.mNoTitleNoPadding) {
            i += r0.mNoTitlePaddingFull;
        }
        if (isVisible(r0.mContent)) {
            r0.mContent.measure(MeasureSpec.makeMeasureSpec(width, 1073741824), MeasureSpec.makeMeasureSpec(i4 - stackedHeight, Integer.MIN_VALUE));
            if (r0.mContent.getMeasuredHeight() <= i4 - i) {
                if (r0.mReducePaddingNoTitleNoButtons && !isVisible(r0.mTitleBar)) {
                    if (!z) {
                        r0.mUseFullPadding = false;
                        i4 -= r0.mContent.getMeasuredHeight() + stackedHeight;
                    }
                }
                r0.mUseFullPadding = true;
                i4 -= r0.mContent.getMeasuredHeight() + i;
            } else {
                r0.mUseFullPadding = false;
                i4 = 0;
            }
        }
        setMeasuredDimension(width, height - i4);
    }

    private static boolean isVisible(View v) {
        boolean z = true;
        boolean visible = (v == null || v.getVisibility() == 8) ? false : true;
        if (!visible || !(v instanceof MDButton)) {
            return visible;
        }
        if (((MDButton) v).getText().toString().trim().length() <= 0) {
            z = false;
        }
        return z;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int y = this.mContent;
        if (y != 0) {
            if (this.mDrawTopDivider) {
                y = y.getTop();
                canvas.drawRect(0.0f, (float) (y - this.mDividerWidth), (float) getMeasuredWidth(), (float) y, this.mDividerPaint);
            }
            if (this.mDrawBottomDivider) {
                y = this.mContent.getBottom();
                canvas.drawRect(0.0f, (float) y, (float) getMeasuredWidth(), (float) (this.mDividerWidth + y), this.mDividerPaint);
            }
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int i = l;
        int t2 = t;
        int i2 = r;
        if (isVisible(this.mTitleBar)) {
            int height = r0.mTitleBar.getMeasuredHeight();
            r0.mTitleBar.layout(i, t2, i2, t2 + height);
            t2 += height;
        } else if (!r0.mNoTitleNoPadding && r0.mUseFullPadding) {
            t2 += r0.mNoTitlePaddingFull;
        }
        if (isVisible(r0.mContent)) {
            View view = r0.mContent;
            view.layout(i, t2, i2, view.getMeasuredHeight() + t2);
        }
        int i3 = 0;
        int length;
        if (r0.mIsStacked) {
            height = b - r0.mButtonPaddingFull;
            MDButton[] mDButtonArr = r0.mButtons;
            length = mDButtonArr.length;
            while (i3 < length) {
                MDButton mButton = mDButtonArr[i3];
                if (isVisible(mButton)) {
                    mButton.layout(i, height - mButton.getMeasuredHeight(), i2, height);
                    height -= mButton.getMeasuredHeight();
                }
                i3++;
            }
        } else {
            int bl;
            int br;
            height = b;
            if (r0.mUseFullPadding) {
                height -= r0.mButtonPaddingFull;
            }
            int barTop = height - r0.mButtonBarHeight;
            length = r0.mButtonHorizontalEdgeMargin;
            int neutralLeft = -1;
            int neutralRight = -1;
            if (isVisible(r0.mButtons[2])) {
                if (r0.mButtonGravity == GravityEnum.END) {
                    bl = i + length;
                    br = r0.mButtons[2].getMeasuredWidth() + bl;
                } else {
                    br = i2 - length;
                    bl = br - r0.mButtons[2].getMeasuredWidth();
                    neutralRight = bl;
                }
                r0.mButtons[2].layout(bl, barTop, br, height);
                length += r0.mButtons[2].getMeasuredWidth();
            }
            if (isVisible(r0.mButtons[1])) {
                if (r0.mButtonGravity == GravityEnum.END) {
                    bl = i + length;
                    br = r0.mButtons[1].getMeasuredWidth() + bl;
                } else if (r0.mButtonGravity == GravityEnum.START) {
                    br = i2 - length;
                    bl = br - r0.mButtons[1].getMeasuredWidth();
                } else {
                    bl = r0.mButtonHorizontalEdgeMargin + i;
                    br = r0.mButtons[1].getMeasuredWidth() + bl;
                    neutralLeft = br;
                }
                r0.mButtons[1].layout(bl, barTop, br, height);
            }
            if (isVisible(r0.mButtons[0])) {
                int bl2;
                if (r0.mButtonGravity == GravityEnum.END) {
                    bl = i2 - r0.mButtonHorizontalEdgeMargin;
                    bl2 = bl - r0.mButtons[0].getMeasuredWidth();
                } else if (r0.mButtonGravity == GravityEnum.START) {
                    bl2 = i + r0.mButtonHorizontalEdgeMargin;
                    bl = r0.mButtons[0].getMeasuredWidth() + bl2;
                } else {
                    if (neutralLeft == -1 && neutralRight != -1) {
                        neutralLeft = neutralRight - r0.mButtons[0].getMeasuredWidth();
                    } else if (neutralRight == -1 && neutralLeft != -1) {
                        neutralRight = r0.mButtons[0].getMeasuredWidth() + neutralLeft;
                    } else if (neutralRight == -1) {
                        bl = ((i2 - i) / 2) - (r0.mButtons[0].getMeasuredWidth() / 2);
                        neutralRight = r0.mButtons[0].getMeasuredWidth() + bl;
                        neutralLeft = bl;
                    }
                    bl2 = neutralLeft;
                    bl = neutralRight;
                }
                r0.mButtons[0].layout(bl2, barTop, bl, height);
            }
        }
        setUpDividersVisibility(r0.mContent, true, true);
    }

    public void setStackingBehavior(StackingBehavior behavior) {
        this.mStackBehavior = behavior;
        invalidate();
    }

    public void setDividerColor(int color) {
        this.mDividerPaint.setColor(color);
        invalidate();
    }

    public void setButtonGravity(GravityEnum gravity) {
        this.mButtonGravity = gravity;
        invertGravityIfNecessary();
    }

    private void invertGravityIfNecessary() {
        if (VERSION.SDK_INT >= 17) {
            if (getResources().getConfiguration().getLayoutDirection() == 1) {
                switch (this.mButtonGravity) {
                    case START:
                        this.mButtonGravity = GravityEnum.END;
                        break;
                    case END:
                        this.mButtonGravity = GravityEnum.START;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void setButtonStackedGravity(GravityEnum gravity) {
        for (MDButton mButton : this.mButtons) {
            if (mButton != null) {
                mButton.setStackedGravity(gravity);
            }
        }
    }

    private void setUpDividersVisibility(final View view, final boolean setForTop, final boolean setForBottom) {
        if (view != null) {
            if (view instanceof ScrollView) {
                ScrollView sv = (ScrollView) view;
                if (canScrollViewScroll(sv)) {
                    addScrollListener(sv, setForTop, setForBottom);
                } else {
                    if (setForTop) {
                        this.mDrawTopDivider = false;
                    }
                    if (setForBottom) {
                        this.mDrawBottomDivider = false;
                    }
                }
            } else if (view instanceof AdapterView) {
                AdapterView sv2 = (AdapterView) view;
                if (canAdapterViewScroll(sv2)) {
                    addScrollListener(sv2, setForTop, setForBottom);
                } else {
                    if (setForTop) {
                        this.mDrawTopDivider = false;
                    }
                    if (setForBottom) {
                        this.mDrawBottomDivider = false;
                    }
                }
            } else if (view instanceof WebView) {
                view.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                    public boolean onPreDraw() {
                        if (view.getMeasuredHeight() != 0) {
                            if (MDRootLayout.canWebViewScroll((WebView) view)) {
                                MDRootLayout.this.addScrollListener((ViewGroup) view, setForTop, setForBottom);
                            } else {
                                if (setForTop) {
                                    MDRootLayout.this.mDrawTopDivider = false;
                                }
                                if (setForBottom) {
                                    MDRootLayout.this.mDrawBottomDivider = false;
                                }
                            }
                            view.getViewTreeObserver().removeOnPreDrawListener(this);
                        }
                        return true;
                    }
                });
            } else if (view instanceof RecyclerView) {
                boolean canScroll = canRecyclerViewScroll((RecyclerView) view);
                if (setForTop) {
                    this.mDrawTopDivider = canScroll;
                }
                if (setForBottom) {
                    this.mDrawBottomDivider = canScroll;
                }
                if (canScroll) {
                    addScrollListener((ViewGroup) view, setForTop, setForBottom);
                }
            } else if (view instanceof ViewGroup) {
                View topView = getTopView((ViewGroup) view);
                setUpDividersVisibility(topView, setForTop, setForBottom);
                View bottomView = getBottomView((ViewGroup) view);
                if (bottomView != topView) {
                    setUpDividersVisibility(bottomView, false, true);
                }
            }
        }
    }

    private void addScrollListener(final ViewGroup vg, final boolean setForTop, final boolean setForBottom) {
        OnScrollChangedListener onScrollChangedListener;
        if (!setForBottom) {
            if (this.mTopOnScrollChangedListener != null) {
            }
            if (vg instanceof RecyclerView) {
                onScrollChangedListener = new OnScrollChangedListener() {
                    public void onScrollChanged() {
                        boolean hasButtons = false;
                        for (MDButton button : MDRootLayout.this.mButtons) {
                            if (button != null && button.getVisibility() != 8) {
                                hasButtons = true;
                                break;
                            }
                        }
                        ViewGroup viewGroup = vg;
                        if (viewGroup instanceof WebView) {
                            MDRootLayout.this.invalidateDividersForWebView((WebView) viewGroup, setForTop, setForBottom, hasButtons);
                        } else {
                            MDRootLayout.this.invalidateDividersForScrollingView(viewGroup, setForTop, setForBottom, hasButtons);
                        }
                        MDRootLayout.this.invalidate();
                    }
                };
                if (setForBottom) {
                    this.mTopOnScrollChangedListener = onScrollChangedListener;
                    vg.getViewTreeObserver().addOnScrollChangedListener(this.mTopOnScrollChangedListener);
                } else {
                    this.mBottomOnScrollChangedListener = onScrollChangedListener;
                    vg.getViewTreeObserver().addOnScrollChangedListener(this.mBottomOnScrollChangedListener);
                }
                onScrollChangedListener.onScrollChanged();
                return;
            }
            OnScrollListener scrollListener = new OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    boolean hasButtons = false;
                    for (MDButton button : MDRootLayout.this.mButtons) {
                        if (button != null && button.getVisibility() != 8) {
                            hasButtons = true;
                            break;
                        }
                    }
                    MDRootLayout.this.invalidateDividersForScrollingView(vg, setForTop, setForBottom, hasButtons);
                    MDRootLayout.this.invalidate();
                }
            };
            ((RecyclerView) vg).addOnScrollListener(scrollListener);
            scrollListener.onScrolled((RecyclerView) vg, 0, 0);
        }
        if (!setForBottom || this.mBottomOnScrollChangedListener != null) {
            return;
        }
        if (vg instanceof RecyclerView) {
            onScrollChangedListener = /* anonymous class already generated */;
            if (setForBottom) {
                this.mBottomOnScrollChangedListener = onScrollChangedListener;
                vg.getViewTreeObserver().addOnScrollChangedListener(this.mBottomOnScrollChangedListener);
            } else {
                this.mTopOnScrollChangedListener = onScrollChangedListener;
                vg.getViewTreeObserver().addOnScrollChangedListener(this.mTopOnScrollChangedListener);
            }
            onScrollChangedListener.onScrollChanged();
            return;
        }
        OnScrollListener scrollListener2 = /* anonymous class already generated */;
        ((RecyclerView) vg).addOnScrollListener(scrollListener2);
        scrollListener2.onScrolled((RecyclerView) vg, 0, 0);
    }

    private void invalidateDividersForScrollingView(ViewGroup view, boolean setForTop, boolean setForBottom, boolean hasButtons) {
        boolean z = true;
        if (setForTop && view.getChildCount() > 0) {
            boolean z2;
            View view2 = this.mTitleBar;
            if (view2 != null) {
                if (view2.getVisibility() != 8) {
                    if (view.getScrollY() + view.getPaddingTop() > view.getChildAt(0).getTop()) {
                        z2 = true;
                        this.mDrawTopDivider = z2;
                    }
                }
            }
            z2 = false;
            this.mDrawTopDivider = z2;
        }
        if (setForBottom && view.getChildCount() > 0) {
            if (hasButtons) {
                if ((view.getScrollY() + view.getHeight()) - view.getPaddingBottom() < view.getChildAt(view.getChildCount() - 1).getBottom()) {
                    this.mDrawBottomDivider = z;
                }
            }
            z = false;
            this.mDrawBottomDivider = z;
        }
    }

    private void invalidateDividersForWebView(WebView view, boolean setForTop, boolean setForBottom, boolean hasButtons) {
        boolean z = true;
        if (setForTop) {
            boolean z2;
            View view2 = this.mTitleBar;
            if (view2 != null) {
                if (view2.getVisibility() != 8) {
                    if (view.getScrollY() + view.getPaddingTop() > 0) {
                        z2 = true;
                        this.mDrawTopDivider = z2;
                    }
                }
            }
            z2 = false;
            this.mDrawTopDivider = z2;
        }
        if (setForBottom) {
            if (hasButtons) {
                if (((float) ((view.getScrollY() + view.getMeasuredHeight()) - view.getPaddingBottom())) < ((float) view.getContentHeight()) * view.getScale()) {
                    this.mDrawBottomDivider = z;
                }
            }
            z = false;
            this.mDrawBottomDivider = z;
        }
    }

    public static boolean canRecyclerViewScroll(RecyclerView view) {
        boolean z = false;
        if (!(view == null || view.getAdapter() == null)) {
            if (view.getLayoutManager() != null) {
                int lastVisible;
                LayoutManager lm = view.getLayoutManager();
                int count = view.getAdapter().getItemCount();
                if (lm instanceof LinearLayoutManager) {
                    lastVisible = ((LinearLayoutManager) lm).findLastVisibleItemPosition();
                } else if (lm instanceof GridLayoutManager) {
                    lastVisible = ((GridLayoutManager) lm).findLastVisibleItemPosition();
                } else {
                    throw new NotImplementedException("Material Dialogs currently only supports LinearLayoutManager/GridLayoutManager. Please report any new layout managers.");
                }
                if (lastVisible == -1) {
                    return false;
                }
                if (lastVisible == count + -1) {
                    if (view.getChildCount() <= 0 || view.getChildAt(view.getChildCount() - 1).getBottom() <= view.getHeight() - view.getPaddingBottom()) {
                        return z;
                    }
                }
                z = true;
                return z;
            }
        }
        return false;
    }

    private static boolean canScrollViewScroll(ScrollView sv) {
        boolean z = false;
        if (sv.getChildCount() == 0) {
            return false;
        }
        if ((sv.getMeasuredHeight() - sv.getPaddingTop()) - sv.getPaddingBottom() < sv.getChildAt(0).getMeasuredHeight()) {
            z = true;
        }
        return z;
    }

    private static boolean canWebViewScroll(WebView view) {
        return ((float) view.getMeasuredHeight()) < ((float) view.getContentHeight()) * view.getScale();
    }

    private static boolean canAdapterViewScroll(AdapterView lv) {
        boolean z = false;
        if (lv.getLastVisiblePosition() == -1) {
            return false;
        }
        boolean firstItemVisible = lv.getFirstVisiblePosition() == 0;
        boolean lastItemVisible = lv.getLastVisiblePosition() == lv.getCount() - 1;
        if (!firstItemVisible || !lastItemVisible || lv.getChildCount() <= 0) {
            return true;
        }
        if (lv.getChildAt(0).getTop() < lv.getPaddingTop()) {
            return true;
        }
        if (lv.getChildAt(lv.getChildCount() - 1).getBottom() > lv.getHeight() - lv.getPaddingBottom()) {
            z = true;
        }
        return z;
    }

    @Nullable
    private static View getBottomView(ViewGroup viewGroup) {
        if (viewGroup != null) {
            if (viewGroup.getChildCount() != 0) {
                View bottomView = null;
                for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
                    View child = viewGroup.getChildAt(i);
                    if (child.getVisibility() == 0 && child.getBottom() == viewGroup.getMeasuredHeight()) {
                        bottomView = child;
                        break;
                    }
                }
                return bottomView;
            }
        }
        return null;
    }

    @Nullable
    private static View getTopView(ViewGroup viewGroup) {
        if (viewGroup != null) {
            if (viewGroup.getChildCount() != 0) {
                View topView = null;
                for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
                    View child = viewGroup.getChildAt(i);
                    if (child.getVisibility() == 0 && child.getTop() == 0) {
                        topView = child;
                        break;
                    }
                }
                return topView;
            }
        }
        return null;
    }
}
