package android.support.wearable.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.wearable.C0395R;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowInsets;
import android.widget.FrameLayout;

@TargetApi(20)
@Deprecated
public class BoxInsetLayout extends FrameLayout {
    private static final int DEFAULT_CHILD_GRAVITY = 8388659;
    private static final float FACTOR = 0.146467f;
    private Rect mForegroundPadding;
    private Rect mInsets;
    private boolean mLastKnownRound;
    private final int mScreenHeight;
    private final int mScreenWidth;

    public static class LayoutParams extends android.widget.FrameLayout.LayoutParams {
        public static final int BOX_ALL = 15;
        public static final int BOX_BOTTOM = 8;
        public static final int BOX_LEFT = 1;
        public static final int BOX_NONE = 0;
        public static final int BOX_RIGHT = 4;
        public static final int BOX_TOP = 2;
        public int boxedEdges = 0;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, C0395R.styleable.BoxInsetLayout_Layout, 0, 0);
            this.boxedEdges = a.getInt(C0395R.styleable.BoxInsetLayout_Layout_layout_box, 0);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(int width, int height, int gravity, int boxed) {
            super(width, height, gravity);
            this.boxedEdges = boxed;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(android.widget.FrameLayout.LayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.boxedEdges = source.boxedEdges;
            this.gravity = source.gravity;
        }
    }

    public BoxInsetLayout(Context context) {
        this(context, null);
    }

    public BoxInsetLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BoxInsetLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (this.mForegroundPadding == null) {
            this.mForegroundPadding = new Rect();
        }
        if (this.mInsets == null) {
            this.mInsets = new Rect();
        }
        this.mScreenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        this.mScreenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (VERSION.SDK_INT < 23) {
            requestApplyInsets();
            return;
        }
        this.mLastKnownRound = getResources().getConfiguration().isScreenRound();
        WindowInsets insets = getRootWindowInsets();
        this.mInsets.set(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(), insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
    }

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        insets = super.onApplyWindowInsets(insets);
        if (VERSION.SDK_INT < 23) {
            boolean round = insets.isRound();
            if (round != this.mLastKnownRound) {
                this.mLastKnownRound = round;
                requestLayout();
            }
            this.mInsets.set(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(), insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
        }
        return insets;
    }

    public boolean isRound() {
        return this.mLastKnownRound;
    }

    public Rect getInsets() {
        return this.mInsets;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        int marginLeft;
        BoxInsetLayout boxInsetLayout = this;
        int i2 = widthMeasureSpec;
        int i3 = heightMeasureSpec;
        int count = getChildCount();
        int maxWidth = 0;
        int maxHeight = 0;
        int childState = 0;
        for (i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                int marginLeft2;
                int marginRight;
                int marginTop;
                int marginBottom;
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                marginLeft = 0;
                int marginRight2 = 0;
                int marginTop2 = 0;
                if (boxInsetLayout.mLastKnownRound) {
                    if ((lp.boxedEdges & 1) == 0) {
                        marginLeft = lp.leftMargin;
                    }
                    if ((lp.boxedEdges & 4) == 0) {
                        marginRight2 = lp.rightMargin;
                    }
                    if ((lp.boxedEdges & 2) == 0) {
                        marginTop2 = lp.topMargin;
                    }
                    if ((8 & lp.boxedEdges) == 0) {
                        marginLeft2 = marginLeft;
                        marginRight = marginRight2;
                        marginTop = marginTop2;
                        marginBottom = lp.bottomMargin;
                    } else {
                        marginLeft2 = marginLeft;
                        marginRight = marginRight2;
                        marginTop = marginTop2;
                        marginBottom = 0;
                    }
                } else {
                    marginLeft = lp.leftMargin;
                    marginTop2 = lp.topMargin;
                    marginLeft2 = marginLeft;
                    marginRight = lp.rightMargin;
                    marginTop = marginTop2;
                    marginBottom = lp.bottomMargin;
                }
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                marginLeft = Math.max(maxWidth, (child.getMeasuredWidth() + marginLeft2) + marginRight);
                maxWidth = marginLeft;
                maxHeight = Math.max(maxHeight, (child.getMeasuredHeight() + marginTop) + marginBottom);
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }
        maxWidth += ((getPaddingLeft() + boxInsetLayout.mForegroundPadding.left) + getPaddingRight()) + boxInsetLayout.mForegroundPadding.right;
        marginLeft = Math.max(maxHeight + (((getPaddingTop() + boxInsetLayout.mForegroundPadding.top) + getPaddingBottom()) + boxInsetLayout.mForegroundPadding.bottom), getSuggestedMinimumHeight());
        int maxWidth2 = Math.max(maxWidth, getSuggestedMinimumWidth());
        Drawable drawable = getForeground();
        if (drawable != null) {
            marginLeft = Math.max(marginLeft, drawable.getMinimumHeight());
            maxWidth2 = Math.max(maxWidth2, drawable.getMinimumWidth());
        }
        setMeasuredDimension(resolveSizeAndState(maxWidth2, i2, childState), resolveSizeAndState(marginLeft, i3, childState << 16));
        int boxInset = calculateInset();
        for (i = 0; i < count; i++) {
            measureChild(i2, i3, boxInset, i);
        }
    }

    private void measureChild(int widthMeasureSpec, int heightMeasureSpec, int desiredMinInset, int i) {
        int i2 = desiredMinInset;
        View child = getChildAt(i);
        LayoutParams childLayoutParams = (LayoutParams) child.getLayoutParams();
        int gravity = childLayoutParams.gravity;
        if (gravity == -1) {
            gravity = DEFAULT_CHILD_GRAVITY;
        }
        int verticalGravity = gravity & 112;
        int horizontalGravity = gravity & 7;
        int totalHeightMargin = (((getPaddingTop() + r0.mForegroundPadding.top) + (getPaddingBottom() + r0.mForegroundPadding.bottom)) + calculateChildTopMargin(childLayoutParams, verticalGravity, i2)) + calculateChildBottomMargin(childLayoutParams, verticalGravity, i2);
        int i3 = widthMeasureSpec;
        child.measure(getChildMeasureSpec(i3, (((getPaddingLeft() + r0.mForegroundPadding.left) + (getPaddingRight() + r0.mForegroundPadding.right)) + calculateChildLeftMargin(childLayoutParams, horizontalGravity, i2)) + calculateChildRightMargin(childLayoutParams, horizontalGravity, i2), childLayoutParams.width), getChildMeasureSpec(heightMeasureSpec, totalHeightMargin, childLayoutParams.height));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int parentLeft;
        int count = getChildCount();
        int parentLeft2 = getPaddingLeft() + this.mForegroundPadding.left;
        int parentRight = ((right - left) - getPaddingRight()) - this.mForegroundPadding.right;
        int parentTop = getPaddingTop() + this.mForegroundPadding.top;
        int parentBottom = ((bottom - top) - getPaddingBottom()) - this.mForegroundPadding.bottom;
        int i = 0;
        while (i < count) {
            int count2;
            View child = r0.getChildAt(i);
            if (child.getVisibility() != 8) {
                int childTop;
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                int gravity = lp.gravity;
                if (gravity == -1) {
                    gravity = DEFAULT_CHILD_GRAVITY;
                }
                int absoluteGravity = Gravity.getAbsoluteGravity(gravity, getLayoutDirection());
                int verticalGravity = gravity & 112;
                int horizontalGravity = gravity & 7;
                count2 = count;
                count = calculateInset();
                int leftChildMargin = r0.calculateChildLeftMargin(lp, horizontalGravity, count);
                int rightChildMargin = r0.calculateChildRightMargin(lp, horizontalGravity, count);
                if (lp.width == -1) {
                    gravity = parentLeft2 + leftChildMargin;
                } else {
                    gravity = absoluteGravity & 7;
                    if (gravity == 1) {
                        gravity = (((((parentRight - parentLeft2) - width) / 2) + parentLeft2) + leftChildMargin) - rightChildMargin;
                    } else if (gravity != 5) {
                        gravity = parentLeft2 + leftChildMargin;
                    } else {
                        gravity = (parentRight - width) - rightChildMargin;
                    }
                }
                horizontalGravity = r0.calculateChildTopMargin(lp, verticalGravity, count);
                int bottomChildMargin = r0.calculateChildBottomMargin(lp, verticalGravity, count);
                int desiredInset = count;
                if (lp.height == -1) {
                    childTop = parentTop + horizontalGravity;
                } else if (verticalGravity == 16) {
                    childTop = (((((parentBottom - parentTop) - height) / 2) + parentTop) + horizontalGravity) - bottomChildMargin;
                } else if (verticalGravity != 80) {
                    childTop = parentTop + horizontalGravity;
                } else {
                    childTop = (parentBottom - height) - bottomChildMargin;
                }
                parentLeft = parentLeft2;
                child.layout(gravity, childTop, gravity + width, childTop + height);
            } else {
                count2 = count;
                parentLeft = parentLeft2;
            }
            i++;
            parentLeft2 = parentLeft;
            count = count2;
            BoxInsetLayout boxInsetLayout = this;
        }
        parentLeft = parentLeft2;
    }

    private int calculateChildLeftMargin(LayoutParams lp, int horizontalGravity, int desiredMinInset) {
        if (this.mLastKnownRound && (lp.boxedEdges & 1) != 0) {
            if (lp.width != -1) {
                if (horizontalGravity == 3) {
                }
            }
            return lp.leftMargin + desiredMinInset;
        }
        return lp.leftMargin;
    }

    private int calculateChildRightMargin(LayoutParams lp, int horizontalGravity, int desiredMinInset) {
        if (this.mLastKnownRound && (lp.boxedEdges & 4) != 0) {
            if (lp.width != -1) {
                if (horizontalGravity == 5) {
                }
            }
            return lp.rightMargin + desiredMinInset;
        }
        return lp.rightMargin;
    }

    private int calculateChildTopMargin(LayoutParams lp, int verticalGravity, int desiredMinInset) {
        if (this.mLastKnownRound && (lp.boxedEdges & 2) != 0) {
            if (lp.height != -1) {
                if (verticalGravity == 48) {
                }
            }
            return lp.topMargin + desiredMinInset;
        }
        return lp.topMargin;
    }

    private int calculateChildBottomMargin(LayoutParams lp, int verticalGravity, int desiredMinInset) {
        if (this.mLastKnownRound && (lp.boxedEdges & 8) != 0) {
            if (lp.height != -1) {
                if (verticalGravity == 80) {
                }
            }
            return lp.bottomMargin + desiredMinInset;
        }
        return lp.bottomMargin;
    }

    private int calculateInset() {
        return (int) (((float) Math.max(Math.min(getMeasuredWidth(), this.mScreenWidth), Math.min(getMeasuredHeight(), this.mScreenHeight))) * FACTOR);
    }

    public void setForeground(Drawable drawable) {
        super.setForeground(drawable);
        if (this.mForegroundPadding == null) {
            this.mForegroundPadding = new Rect();
        }
        drawable.getPadding(this.mForegroundPadding);
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }
}
