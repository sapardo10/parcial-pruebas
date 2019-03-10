package android.support.wearable.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GravityCompat;
import android.support.wearable.C0395R;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews.RemoteView;
import java.util.ArrayList;

@RemoteView
@TargetApi(21)
@Deprecated
public class WearableFrameLayout extends ViewGroup {
    private static final int DEFAULT_CHILD_GRAVITY = 8388659;
    private static final String TAG = "WearableFrameLayout";
    @ExportedProperty(category = "drawing")
    private Drawable mForeground;
    boolean mForegroundBoundsChanged;
    @ExportedProperty(category = "drawing")
    private int mForegroundGravity;
    @ExportedProperty(category = "drawing")
    private boolean mForegroundInPadding;
    @ExportedProperty(category = "padding")
    private int mForegroundPaddingBottom;
    @ExportedProperty(category = "padding")
    private int mForegroundPaddingLeft;
    @ExportedProperty(category = "padding")
    private int mForegroundPaddingRight;
    @ExportedProperty(category = "padding")
    private int mForegroundPaddingTop;
    private ColorStateList mForegroundTintList;
    private Mode mForegroundTintMode;
    private boolean mHasForegroundTint;
    private boolean mHasForegroundTintMode;
    private final ArrayList<View> mMatchParentChildren;
    @ExportedProperty(category = "measurement")
    boolean mMeasureAllChildren;
    private final Rect mOverlayBounds;
    private boolean mRound;
    private final Rect mSelfBounds;

    public static class LayoutParams extends android.widget.FrameLayout.LayoutParams {
        public int bottomMarginRound;
        public int gravityRound = -1;
        public int heightRound;
        public int leftMarginRound;
        public int rightMarginRound;
        public int topMarginRound;
        public int widthRound;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, C0395R.styleable.WearableFrameLayout);
            this.gravityRound = a.getInt(C0395R.styleable.WearableFrameLayout_layout_gravityRound, this.gravity);
            this.widthRound = a.getLayoutDimension(C0395R.styleable.WearableFrameLayout_layout_widthRound, this.width);
            this.heightRound = a.getLayoutDimension(C0395R.styleable.WearableFrameLayout_layout_heightRound, this.height);
            int marginRound = a.getDimensionPixelSize(C0395R.styleable.WearableFrameLayout_layout_marginRound, -1);
            if (marginRound >= 0) {
                this.bottomMarginRound = marginRound;
                this.topMarginRound = marginRound;
                this.rightMarginRound = marginRound;
                this.leftMarginRound = marginRound;
            } else {
                this.leftMarginRound = a.getDimensionPixelSize(C0395R.styleable.WearableFrameLayout_layout_marginLeftRound, this.leftMargin);
                this.topMarginRound = a.getDimensionPixelSize(C0395R.styleable.WearableFrameLayout_layout_marginTopRound, this.topMargin);
                this.rightMarginRound = a.getDimensionPixelSize(C0395R.styleable.WearableFrameLayout_layout_marginRightRound, this.rightMargin);
                this.bottomMarginRound = a.getDimensionPixelSize(C0395R.styleable.WearableFrameLayout_layout_marginBottomRound, this.bottomMargin);
            }
            a.recycle();
        }

        public LayoutParams(int width, int height, int gravity, int widthRound, int heightRound, int gravityRound) {
            super(width, height, gravity);
            this.widthRound = widthRound;
            this.heightRound = heightRound;
            this.gravityRound = gravityRound;
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
            this.widthRound = width;
            this.heightRound = height;
            this.gravityRound = gravity;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.widthRound = width;
            this.heightRound = height;
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.widthRound = source.widthRound;
            this.heightRound = source.heightRound;
            this.gravityRound = source.gravityRound;
            this.leftMarginRound = source.leftMarginRound;
            this.topMarginRound = source.topMarginRound;
            this.rightMarginRound = source.rightMarginRound;
            this.bottomMarginRound = source.bottomMarginRound;
        }
    }

    public WearableFrameLayout(Context context) {
        super(context);
        this.mMeasureAllChildren = false;
        this.mForegroundTintList = null;
        this.mForegroundTintMode = null;
        this.mHasForegroundTint = false;
        this.mHasForegroundTintMode = false;
        this.mForegroundPaddingLeft = 0;
        this.mForegroundPaddingTop = 0;
        this.mForegroundPaddingRight = 0;
        this.mForegroundPaddingBottom = 0;
        this.mSelfBounds = new Rect();
        this.mOverlayBounds = new Rect();
        this.mForegroundGravity = 119;
        this.mForegroundInPadding = true;
        this.mForegroundBoundsChanged = false;
        this.mMatchParentChildren = new ArrayList(1);
        this.mRound = false;
    }

    public WearableFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WearableFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mMeasureAllChildren = false;
        this.mForegroundTintList = null;
        this.mForegroundTintMode = null;
        this.mHasForegroundTint = false;
        this.mHasForegroundTintMode = false;
        this.mForegroundPaddingLeft = 0;
        this.mForegroundPaddingTop = 0;
        this.mForegroundPaddingRight = 0;
        this.mForegroundPaddingBottom = 0;
        this.mSelfBounds = new Rect();
        this.mOverlayBounds = new Rect();
        this.mForegroundGravity = 119;
        this.mForegroundInPadding = true;
        this.mForegroundBoundsChanged = false;
        this.mMatchParentChildren = new ArrayList(1);
        this.mRound = false;
        TypedArray a = context.obtainStyledAttributes(attrs, C0395R.styleable.WearableFrameLayout, defStyleAttr, defStyleRes);
        this.mForegroundGravity = a.getInt(C0395R.styleable.WearableFrameLayout_android_foregroundGravity, this.mForegroundGravity);
        Drawable d = a.getDrawable(C0395R.styleable.WearableFrameLayout_android_foreground);
        if (d != null) {
            setForeground(d);
        }
        if (a.getBoolean(C0395R.styleable.WearableFrameLayout_android_measureAllChildren, false)) {
            setMeasureAllChildren(true);
        }
        if (a.hasValue(C0395R.styleable.WearableFrameLayout_android_foregroundTint)) {
            this.mForegroundTintList = a.getColorStateList(C0395R.styleable.WearableFrameLayout_android_foregroundTint);
            this.mHasForegroundTint = true;
        }
        a.recycle();
        applyForegroundTint();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        requestApplyInsets();
    }

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        boolean changed = this.mRound != insets.isRound();
        this.mRound = insets.isRound();
        if (changed) {
            requestLayout();
        }
        return super.onApplyWindowInsets(insets);
    }

    public int getForegroundGravity() {
        return this.mForegroundGravity;
    }

    public void setForegroundGravity(int foregroundGravity) {
        if (this.mForegroundGravity != foregroundGravity) {
            if ((GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK & foregroundGravity) == 0) {
                foregroundGravity |= 8388611;
            }
            if ((foregroundGravity & 112) == 0) {
                foregroundGravity |= 48;
            }
            this.mForegroundGravity = foregroundGravity;
            if (this.mForegroundGravity != 119 || this.mForeground == null) {
                this.mForegroundPaddingLeft = 0;
                this.mForegroundPaddingTop = 0;
                this.mForegroundPaddingRight = 0;
                this.mForegroundPaddingBottom = 0;
            } else {
                Rect padding = new Rect();
                if (this.mForeground.getPadding(padding)) {
                    this.mForegroundPaddingLeft = padding.left;
                    this.mForegroundPaddingTop = padding.top;
                    this.mForegroundPaddingRight = padding.right;
                    this.mForegroundPaddingBottom = padding.bottom;
                }
            }
            requestLayout();
        }
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        Drawable drawable = this.mForeground;
        if (drawable != null) {
            drawable.setVisible(visibility == 0, false);
        }
    }

    protected boolean verifyDrawable(Drawable who) {
        if (!super.verifyDrawable(who)) {
            if (who != this.mForeground) {
                return false;
            }
        }
        return true;
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.mForeground;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.mForeground;
        if (drawable != null && drawable.isStateful()) {
            this.mForeground.setState(getDrawableState());
        }
    }

    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        Drawable drawable = this.mForeground;
        if (drawable != null) {
            drawable.setHotspot(x, y);
        }
    }

    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -1);
    }

    public void setForeground(Drawable d) {
        Drawable drawable = this.mForeground;
        if (drawable != d) {
            if (drawable != null) {
                drawable.setCallback(null);
                unscheduleDrawable(this.mForeground);
            }
            this.mForeground = d;
            this.mForegroundPaddingLeft = 0;
            this.mForegroundPaddingTop = 0;
            this.mForegroundPaddingRight = 0;
            this.mForegroundPaddingBottom = 0;
            if (d != null) {
                setWillNotDraw(false);
                d.setCallback(this);
                if (d.isStateful()) {
                    d.setState(getDrawableState());
                }
                applyForegroundTint();
                if (this.mForegroundGravity == 119) {
                    Rect padding = new Rect();
                    if (d.getPadding(padding)) {
                        this.mForegroundPaddingLeft = padding.left;
                        this.mForegroundPaddingTop = padding.top;
                        this.mForegroundPaddingRight = padding.right;
                        this.mForegroundPaddingBottom = padding.bottom;
                    }
                }
            } else {
                setWillNotDraw(true);
            }
            requestLayout();
            invalidate();
        }
    }

    public Drawable getForeground() {
        return this.mForeground;
    }

    public void setForegroundTintList(ColorStateList tint) {
        this.mForegroundTintList = tint;
        this.mHasForegroundTint = true;
        applyForegroundTint();
    }

    public void setForegroundInPadding(boolean value) {
        this.mForegroundInPadding = value;
    }

    public ColorStateList getForegroundTintList() {
        return this.mForegroundTintList;
    }

    public void setForegroundTintMode(Mode tintMode) {
        this.mForegroundTintMode = tintMode;
        this.mHasForegroundTintMode = true;
        applyForegroundTint();
    }

    public Mode getForegroundTintMode() {
        return this.mForegroundTintMode;
    }

    private void applyForegroundTint() {
        if (this.mForeground != null && (this.mHasForegroundTint || this.mHasForegroundTintMode)) {
            this.mForeground = this.mForeground.mutate();
            if (this.mHasForegroundTint) {
                this.mForeground.setTintList(this.mForegroundTintList);
            }
            if (this.mHasForegroundTintMode) {
                this.mForeground.setTintMode(this.mForegroundTintMode);
            }
            if (this.mForeground.isStateful()) {
                this.mForeground.setState(getDrawableState());
            }
        }
    }

    int getPaddingLeftWithForeground() {
        if (this.mForegroundInPadding) {
            return Math.max(getPaddingLeft(), this.mForegroundPaddingLeft);
        }
        return getPaddingLeft() + this.mForegroundPaddingLeft;
    }

    int getPaddingRightWithForeground() {
        if (this.mForegroundInPadding) {
            return Math.max(getPaddingRight(), this.mForegroundPaddingRight);
        }
        return getPaddingRight() + this.mForegroundPaddingRight;
    }

    private int getPaddingTopWithForeground() {
        if (this.mForegroundInPadding) {
            return Math.max(getPaddingTop(), this.mForegroundPaddingTop);
        }
        return getPaddingTop() + this.mForegroundPaddingTop;
    }

    private int getPaddingBottomWithForeground() {
        if (this.mForegroundInPadding) {
            return Math.max(getPaddingBottom(), this.mForegroundPaddingBottom);
        }
        return getPaddingBottom() + this.mForegroundPaddingBottom;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean z;
        boolean measureMatchParentChildren;
        int maxHeight;
        int maxWidth;
        int childState;
        int i;
        View child;
        View child2;
        int childState2;
        LayoutParams lp;
        int maxWidth2;
        int childState3;
        int i2;
        int maxHeight2;
        Drawable drawable;
        int i3;
        LayoutParams lp2;
        WearableFrameLayout wearableFrameLayout = this;
        int i4 = widthMeasureSpec;
        int i5 = heightMeasureSpec;
        int count = getChildCount();
        if (MeasureSpec.getMode(widthMeasureSpec) == 1073741824) {
            if (MeasureSpec.getMode(heightMeasureSpec) == 1073741824) {
                z = false;
                measureMatchParentChildren = z;
                wearableFrameLayout.mMatchParentChildren.clear();
                maxHeight = 0;
                maxWidth = 0;
                childState = 0;
                for (i = 0; i < count; i++) {
                    child = getChildAt(i);
                    if (!wearableFrameLayout.mMeasureAllChildren) {
                        if (child.getVisibility() != 8) {
                        }
                    }
                    child2 = child;
                    childState2 = childState;
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    lp = (LayoutParams) child2.getLayoutParams();
                    maxWidth2 = Math.max(maxWidth, (child2.getMeasuredWidth() + getParamsLeftMargin(lp)) + getParamsRightMargin(lp));
                    int maxHeight3 = Math.max(maxHeight, (child2.getMeasuredHeight() + getParamsTopMargin(lp)) + getParamsBottomMargin(lp));
                    childState3 = combineMeasuredStates(childState2, child2.getMeasuredState());
                    if (measureMatchParentChildren) {
                    } else {
                        if (getParamsWidth(lp) != -1) {
                            if (getParamsHeight(lp) != -1) {
                            }
                        }
                        wearableFrameLayout.mMatchParentChildren.add(child2);
                    }
                    maxWidth = maxWidth2;
                    maxHeight = maxHeight3;
                    childState = childState3;
                }
                childState2 = childState;
                i2 = -1;
                maxWidth += getPaddingLeftWithForeground() + getPaddingRightWithForeground();
                maxHeight2 = Math.max(maxHeight + (getPaddingTopWithForeground() + getPaddingBottomWithForeground()), getSuggestedMinimumHeight());
                maxWidth2 = Math.max(maxWidth, getSuggestedMinimumWidth());
                drawable = getForeground();
                if (drawable != null) {
                    maxHeight2 = Math.max(maxHeight2, drawable.getMinimumHeight());
                    maxWidth2 = Math.max(maxWidth2, drawable.getMinimumWidth());
                }
                setMeasuredDimension(resolveSizeAndState(maxWidth2, i4, childState2), resolveSizeAndState(maxHeight2, i5, childState2 << 16));
                childState3 = wearableFrameLayout.mMatchParentChildren.size();
                if (childState3 > 1) {
                    i3 = 0;
                    while (i3 < childState3) {
                        View child3 = (View) wearableFrameLayout.mMatchParentChildren.get(i3);
                        lp2 = (LayoutParams) child3.getLayoutParams();
                        if (getParamsWidth(lp2) != i2) {
                            i = MeasureSpec.makeMeasureSpec((((getMeasuredWidth() - getPaddingLeftWithForeground()) - getPaddingRightWithForeground()) - getParamsLeftMargin(lp2)) - getParamsRightMargin(lp2), 1073741824);
                        } else {
                            i = getChildMeasureSpec(i4, ((getPaddingLeftWithForeground() + getPaddingRightWithForeground()) + getParamsLeftMargin(lp2)) + getParamsRightMargin(lp2), getParamsWidth(lp2));
                        }
                        if (getParamsHeight(lp2) != i2) {
                            maxWidth = MeasureSpec.makeMeasureSpec((((getMeasuredHeight() - getPaddingTopWithForeground()) - getPaddingBottomWithForeground()) - getParamsTopMargin(lp2)) - getParamsBottomMargin(lp2), 1073741824);
                        } else {
                            maxWidth = getChildMeasureSpec(i5, ((getPaddingTopWithForeground() + getPaddingBottomWithForeground()) + getParamsTopMargin(lp2)) + getParamsBottomMargin(lp2), getParamsHeight(lp2));
                        }
                        child3.measure(i, maxWidth);
                        i3++;
                        i2 = -1;
                    }
                }
            }
        }
        z = true;
        measureMatchParentChildren = z;
        wearableFrameLayout.mMatchParentChildren.clear();
        maxHeight = 0;
        maxWidth = 0;
        childState = 0;
        for (i = 0; i < count; i++) {
            child = getChildAt(i);
            if (wearableFrameLayout.mMeasureAllChildren) {
                if (child.getVisibility() != 8) {
                }
            }
            child2 = child;
            childState2 = childState;
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            lp = (LayoutParams) child2.getLayoutParams();
            maxWidth2 = Math.max(maxWidth, (child2.getMeasuredWidth() + getParamsLeftMargin(lp)) + getParamsRightMargin(lp));
            int maxHeight32 = Math.max(maxHeight, (child2.getMeasuredHeight() + getParamsTopMargin(lp)) + getParamsBottomMargin(lp));
            childState3 = combineMeasuredStates(childState2, child2.getMeasuredState());
            if (measureMatchParentChildren) {
            } else {
                if (getParamsWidth(lp) != -1) {
                    if (getParamsHeight(lp) != -1) {
                    }
                }
                wearableFrameLayout.mMatchParentChildren.add(child2);
            }
            maxWidth = maxWidth2;
            maxHeight = maxHeight32;
            childState = childState3;
        }
        childState2 = childState;
        i2 = -1;
        maxWidth += getPaddingLeftWithForeground() + getPaddingRightWithForeground();
        maxHeight2 = Math.max(maxHeight + (getPaddingTopWithForeground() + getPaddingBottomWithForeground()), getSuggestedMinimumHeight());
        maxWidth2 = Math.max(maxWidth, getSuggestedMinimumWidth());
        drawable = getForeground();
        if (drawable != null) {
            maxHeight2 = Math.max(maxHeight2, drawable.getMinimumHeight());
            maxWidth2 = Math.max(maxWidth2, drawable.getMinimumWidth());
        }
        setMeasuredDimension(resolveSizeAndState(maxWidth2, i4, childState2), resolveSizeAndState(maxHeight2, i5, childState2 << 16));
        childState3 = wearableFrameLayout.mMatchParentChildren.size();
        if (childState3 > 1) {
            i3 = 0;
            while (i3 < childState3) {
                View child32 = (View) wearableFrameLayout.mMatchParentChildren.get(i3);
                lp2 = (LayoutParams) child32.getLayoutParams();
                if (getParamsWidth(lp2) != i2) {
                    i = getChildMeasureSpec(i4, ((getPaddingLeftWithForeground() + getPaddingRightWithForeground()) + getParamsLeftMargin(lp2)) + getParamsRightMargin(lp2), getParamsWidth(lp2));
                } else {
                    i = MeasureSpec.makeMeasureSpec((((getMeasuredWidth() - getPaddingLeftWithForeground()) - getPaddingRightWithForeground()) - getParamsLeftMargin(lp2)) - getParamsRightMargin(lp2), 1073741824);
                }
                if (getParamsHeight(lp2) != i2) {
                    maxWidth = getChildMeasureSpec(i5, ((getPaddingTopWithForeground() + getPaddingBottomWithForeground()) + getParamsTopMargin(lp2)) + getParamsBottomMargin(lp2), getParamsHeight(lp2));
                } else {
                    maxWidth = MeasureSpec.makeMeasureSpec((((getMeasuredHeight() - getPaddingTopWithForeground()) - getPaddingBottomWithForeground()) - getParamsTopMargin(lp2)) - getParamsBottomMargin(lp2), 1073741824);
                }
                child32.measure(i, maxWidth);
                i3++;
                i2 = -1;
            }
        }
    }

    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        child.measure(getChildMeasureSpec(parentWidthMeasureSpec, (((getPaddingLeft() + getPaddingRight()) + getParamsLeftMargin(lp)) + getParamsRightMargin(lp)) + widthUsed, getParamsWidth(lp)), getChildMeasureSpec(parentHeightMeasureSpec, (((getPaddingTop() + getPaddingBottom()) + getParamsTopMargin(lp)) + getParamsBottomMargin(lp)) + heightUsed, getParamsHeight(lp)));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutChildren(left, top, right, bottom, false);
    }

    void layoutChildren(int left, int top, int right, int bottom, boolean forceLeftGravity) {
        int parentLeft;
        int count = getChildCount();
        int parentLeft2 = getPaddingLeftWithForeground();
        int parentRight = (right - left) - getPaddingRightWithForeground();
        int parentTop = getPaddingTopWithForeground();
        int parentBottom = (bottom - top) - getPaddingBottomWithForeground();
        this.mForegroundBoundsChanged = true;
        int i = 0;
        while (i < count) {
            int count2;
            View child = r0.getChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                int gravity = r0.getParamsGravity(lp);
                if (gravity == -1) {
                    gravity = DEFAULT_CHILD_GRAVITY;
                }
                int verticalGravity = gravity & 112;
                int absoluteGravity = Gravity.getAbsoluteGravity(gravity, getLayoutDirection()) & 7;
                count2 = count;
                if (absoluteGravity != 1) {
                    if (absoluteGravity == 5) {
                        if (!forceLeftGravity) {
                            count = (parentRight - width) - r0.getParamsRightMargin(lp);
                        }
                    }
                    count = r0.getParamsLeftMargin(lp) + parentLeft2;
                } else {
                    count = (((((parentRight - parentLeft2) - width) / 2) + parentLeft2) + r0.getParamsLeftMargin(lp)) - r0.getParamsRightMargin(lp);
                }
                if (verticalGravity == 16) {
                    absoluteGravity = (((((parentBottom - parentTop) - height) / 2) + parentTop) + r0.getParamsTopMargin(lp)) - r0.getParamsBottomMargin(lp);
                } else if (verticalGravity == 48) {
                    absoluteGravity = r0.getParamsTopMargin(lp) + parentTop;
                } else if (verticalGravity != 80) {
                    absoluteGravity = r0.getParamsTopMargin(lp) + parentTop;
                } else {
                    absoluteGravity = (parentBottom - height) - r0.getParamsBottomMargin(lp);
                }
                parentLeft = parentLeft2;
                child.layout(count, absoluteGravity, count + width, absoluteGravity + height);
            } else {
                count2 = count;
                parentLeft = parentLeft2;
            }
            i++;
            count = count2;
            parentLeft2 = parentLeft;
            WearableFrameLayout wearableFrameLayout = this;
        }
        parentLeft = parentLeft2;
    }

    private int getParamsWidth(LayoutParams params) {
        return this.mRound ? params.widthRound : params.width;
    }

    private int getParamsHeight(LayoutParams params) {
        return this.mRound ? params.heightRound : params.height;
    }

    private int getParamsLeftMargin(LayoutParams params) {
        return this.mRound ? params.leftMarginRound : params.leftMargin;
    }

    private int getParamsTopMargin(LayoutParams params) {
        return this.mRound ? params.topMarginRound : params.topMargin;
    }

    private int getParamsRightMargin(LayoutParams params) {
        return this.mRound ? params.rightMarginRound : params.rightMargin;
    }

    private int getParamsBottomMargin(LayoutParams params) {
        return this.mRound ? params.bottomMarginRound : params.bottomMargin;
    }

    private int getParamsGravity(LayoutParams params) {
        return this.mRound ? params.gravityRound : params.gravity;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mForegroundBoundsChanged = true;
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mForeground != null) {
            Drawable foreground = this.mForeground;
            if (this.mForegroundBoundsChanged) {
                this.mForegroundBoundsChanged = false;
                Rect selfBounds = this.mSelfBounds;
                Rect overlayBounds = this.mOverlayBounds;
                int w = getRight() - getLeft();
                int h = getBottom() - getTop();
                if (this.mForegroundInPadding) {
                    selfBounds.set(0, 0, w, h);
                } else {
                    selfBounds.set(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());
                }
                Gravity.apply(this.mForegroundGravity, foreground.getIntrinsicWidth(), foreground.getIntrinsicHeight(), selfBounds, overlayBounds, getLayoutDirection());
                foreground.setBounds(overlayBounds);
            }
            foreground.draw(canvas);
        }
    }

    public void setMeasureAllChildren(boolean measureAll) {
        this.mMeasureAllChildren = measureAll;
    }

    public boolean getMeasureAllChildren() {
        return this.mMeasureAllChildren;
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams((LayoutParams) p);
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(WearableFrameLayout.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(WearableFrameLayout.class.getName());
    }
}
