package android.support.design.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.support.v4.math.MathUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import java.util.List;

abstract class HeaderScrollingViewBehavior extends ViewOffsetBehavior<View> {
    private int mOverlayTop;
    final Rect mTempRect1 = new Rect();
    final Rect mTempRect2 = new Rect();
    private int mVerticalLayoutGap = 0;

    abstract View findFirstDependency(List<View> list);

    public HeaderScrollingViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onMeasureChild(CoordinatorLayout parent, View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        HeaderScrollingViewBehavior headerScrollingViewBehavior = this;
        int childLpHeight = child.getLayoutParams().height;
        if (childLpHeight != -1) {
            if (childLpHeight != -2) {
                View view = child;
                return false;
            }
        }
        View header = findFirstDependency(parent.getDependencies(child));
        if (header != null) {
            int availableHeight;
            if (!ViewCompat.getFitsSystemWindows(header)) {
                view = child;
            } else if (ViewCompat.getFitsSystemWindows(child)) {
                view = child;
            } else {
                ViewCompat.setFitsSystemWindows(child, true);
                if (ViewCompat.getFitsSystemWindows(child)) {
                    child.requestLayout();
                    return true;
                }
            }
            int availableHeight2 = MeasureSpec.getSize(parentHeightMeasureSpec);
            if (availableHeight2 == 0) {
                availableHeight = parent.getHeight();
            } else {
                availableHeight = availableHeight2;
            }
            parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, MeasureSpec.makeMeasureSpec((availableHeight - header.getMeasuredHeight()) + getScrollRange(header), childLpHeight == -1 ? 1073741824 : Integer.MIN_VALUE), heightUsed);
            return true;
        }
        view = child;
        return false;
    }

    protected void layoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        View header = findFirstDependency(parent.getDependencies(child));
        if (header != null) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            Rect available = this.mTempRect1;
            available.set(parent.getPaddingLeft() + lp.leftMargin, header.getBottom() + lp.topMargin, (parent.getWidth() - parent.getPaddingRight()) - lp.rightMargin, ((parent.getHeight() + header.getBottom()) - parent.getPaddingBottom()) - lp.bottomMargin);
            WindowInsetsCompat parentInsets = parent.getLastWindowInsets();
            if (parentInsets != null && ViewCompat.getFitsSystemWindows(parent)) {
                if (!ViewCompat.getFitsSystemWindows(child)) {
                    available.left += parentInsets.getSystemWindowInsetLeft();
                    available.right -= parentInsets.getSystemWindowInsetRight();
                }
            }
            Rect out = this.mTempRect2;
            GravityCompat.apply(resolveGravity(lp.gravity), child.getMeasuredWidth(), child.getMeasuredHeight(), available, out, layoutDirection);
            int overlap = getOverlapPixelsForOffset(header);
            child.layout(out.left, out.top - overlap, out.right, out.bottom - overlap);
            this.mVerticalLayoutGap = out.top - header.getBottom();
            return;
        }
        super.layoutChild(parent, child, layoutDirection);
        this.mVerticalLayoutGap = 0;
    }

    float getOverlapRatioForOffset(View header) {
        return 1.0f;
    }

    final int getOverlapPixelsForOffset(View header) {
        if (this.mOverlayTop == 0) {
            return 0;
        }
        float overlapRatioForOffset = getOverlapRatioForOffset(header);
        int i = this.mOverlayTop;
        return MathUtils.clamp((int) (overlapRatioForOffset * ((float) i)), 0, i);
    }

    private static int resolveGravity(int gravity) {
        return gravity == 0 ? 8388659 : gravity;
    }

    int getScrollRange(View v) {
        return v.getMeasuredHeight();
    }

    final int getVerticalLayoutGap() {
        return this.mVerticalLayoutGap;
    }

    public final void setOverlayTop(int overlayTop) {
        this.mOverlayTop = overlayTop;
    }

    public final int getOverlayTop() {
        return this.mOverlayTop;
    }
}