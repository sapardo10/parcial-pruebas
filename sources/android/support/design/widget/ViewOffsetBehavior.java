package android.support.design.widget;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.util.AttributeSet;
import android.view.View;

class ViewOffsetBehavior<V extends View> extends Behavior<V> {
    private int mTempLeftRightOffset = 0;
    private int mTempTopBottomOffset = 0;
    private ViewOffsetHelper mViewOffsetHelper;

    public ViewOffsetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        layoutChild(parent, child, layoutDirection);
        if (this.mViewOffsetHelper == null) {
            this.mViewOffsetHelper = new ViewOffsetHelper(child);
        }
        this.mViewOffsetHelper.onViewLayout();
        int i = this.mTempTopBottomOffset;
        if (i != 0) {
            this.mViewOffsetHelper.setTopAndBottomOffset(i);
            this.mTempTopBottomOffset = 0;
        }
        i = this.mTempLeftRightOffset;
        if (i != 0) {
            this.mViewOffsetHelper.setLeftAndRightOffset(i);
            this.mTempLeftRightOffset = 0;
        }
        return true;
    }

    protected void layoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);
    }

    public boolean setTopAndBottomOffset(int offset) {
        ViewOffsetHelper viewOffsetHelper = this.mViewOffsetHelper;
        if (viewOffsetHelper != null) {
            return viewOffsetHelper.setTopAndBottomOffset(offset);
        }
        this.mTempTopBottomOffset = offset;
        return false;
    }

    public boolean setLeftAndRightOffset(int offset) {
        ViewOffsetHelper viewOffsetHelper = this.mViewOffsetHelper;
        if (viewOffsetHelper != null) {
            return viewOffsetHelper.setLeftAndRightOffset(offset);
        }
        this.mTempLeftRightOffset = offset;
        return false;
    }

    public int getTopAndBottomOffset() {
        ViewOffsetHelper viewOffsetHelper = this.mViewOffsetHelper;
        return viewOffsetHelper != null ? viewOffsetHelper.getTopAndBottomOffset() : 0;
    }

    public int getLeftAndRightOffset() {
        ViewOffsetHelper viewOffsetHelper = this.mViewOffsetHelper;
        return viewOffsetHelper != null ? viewOffsetHelper.getLeftAndRightOffset() : 0;
    }
}
