package android.support.design.widget;

import android.content.Context;
import android.support.v4.math.MathUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

abstract class HeaderBehavior<V extends View> extends ViewOffsetBehavior<V> {
    private static final int INVALID_POINTER = -1;
    private int mActivePointerId = -1;
    private Runnable mFlingRunnable;
    private boolean mIsBeingDragged;
    private int mLastMotionY;
    OverScroller mScroller;
    private int mTouchSlop = -1;
    private VelocityTracker mVelocityTracker;

    private class FlingRunnable implements Runnable {
        private final V mLayout;
        private final CoordinatorLayout mParent;

        FlingRunnable(CoordinatorLayout parent, V layout) {
            this.mParent = parent;
            this.mLayout = layout;
        }

        public void run() {
            if (this.mLayout != null && HeaderBehavior.this.mScroller != null) {
                if (HeaderBehavior.this.mScroller.computeScrollOffset()) {
                    HeaderBehavior headerBehavior = HeaderBehavior.this;
                    headerBehavior.setHeaderTopBottomOffset(this.mParent, this.mLayout, headerBehavior.mScroller.getCurrY());
                    ViewCompat.postOnAnimation(this.mLayout, this);
                    return;
                }
                HeaderBehavior.this.onFlingFinished(this.mParent, this.mLayout);
            }
        }
    }

    public HeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent ev) {
        if (this.mTouchSlop < 0) {
            this.mTouchSlop = ViewConfiguration.get(parent.getContext()).getScaledTouchSlop();
        }
        if (ev.getAction() == 2 && this.mIsBeingDragged) {
            return true;
        }
        VelocityTracker velocityTracker;
        int x;
        switch (ev.getActionMasked()) {
            case 0:
                this.mIsBeingDragged = false;
                x = (int) ev.getX();
                int y = (int) ev.getY();
                if (!canDragView(child) || !parent.isPointInChildBounds(child, x, y)) {
                    break;
                }
                this.mLastMotionY = y;
                this.mActivePointerId = ev.getPointerId(0);
                ensureVelocityTracker();
                break;
            case 1:
            case 3:
                this.mIsBeingDragged = false;
                this.mActivePointerId = -1;
                velocityTracker = this.mVelocityTracker;
                if (velocityTracker == null) {
                    break;
                }
                velocityTracker.recycle();
                this.mVelocityTracker = null;
                break;
            case 2:
                x = this.mActivePointerId;
                if (x != -1) {
                    int pointerIndex = ev.findPointerIndex(x);
                    if (pointerIndex != -1) {
                        int y2 = (int) ev.getY(pointerIndex);
                        if (Math.abs(y2 - this.mLastMotionY) <= this.mTouchSlop) {
                            break;
                        }
                        this.mIsBeingDragged = true;
                        this.mLastMotionY = y2;
                        break;
                    }
                    break;
                }
                break;
            default:
                break;
        }
        velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.addMovement(ev);
        }
        return this.mIsBeingDragged;
    }

    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent ev) {
        VelocityTracker velocityTracker;
        if (this.mTouchSlop < 0) {
            this.mTouchSlop = ViewConfiguration.get(parent.getContext()).getScaledTouchSlop();
        }
        int y;
        switch (ev.getActionMasked()) {
            case 0:
                y = (int) ev.getY();
                if (parent.isPointInChildBounds(child, (int) ev.getX(), y) && canDragView(child)) {
                    this.mLastMotionY = y;
                    this.mActivePointerId = ev.getPointerId(0);
                    ensureVelocityTracker();
                    break;
                }
                return false;
            case 1:
                velocityTracker = this.mVelocityTracker;
                if (velocityTracker == null) {
                    break;
                }
                velocityTracker.addMovement(ev);
                this.mVelocityTracker.computeCurrentVelocity(1000);
                CoordinatorLayout coordinatorLayout = parent;
                V v = child;
                fling(coordinatorLayout, v, -getScrollRangeForDragFling(child), 0, this.mVelocityTracker.getYVelocity(this.mActivePointerId));
                break;
            case 2:
                int activePointerIndex = ev.findPointerIndex(this.mActivePointerId);
                if (activePointerIndex == -1) {
                    return false;
                }
                y = (int) ev.getY(activePointerIndex);
                int dy = this.mLastMotionY - y;
                if (!this.mIsBeingDragged) {
                    int abs = Math.abs(dy);
                    int i = this.mTouchSlop;
                    if (abs > i) {
                        this.mIsBeingDragged = true;
                        if (dy > 0) {
                            dy -= i;
                        } else {
                            dy += i;
                        }
                        if (this.mIsBeingDragged) {
                            this.mLastMotionY = y;
                            scroll(parent, child, dy, getMaxDragOffset(child), 0);
                            break;
                        }
                        break;
                    }
                }
                if (this.mIsBeingDragged) {
                    this.mLastMotionY = y;
                    scroll(parent, child, dy, getMaxDragOffset(child), 0);
                }
            case 3:
                break;
            default:
                break;
        }
        this.mIsBeingDragged = false;
        this.mActivePointerId = -1;
        velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
        velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.addMovement(ev);
        }
        return true;
    }

    int setHeaderTopBottomOffset(CoordinatorLayout parent, V header, int newOffset) {
        return setHeaderTopBottomOffset(parent, header, newOffset, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    int setHeaderTopBottomOffset(CoordinatorLayout parent, V v, int newOffset, int minOffset, int maxOffset) {
        int curOffset = getTopAndBottomOffset();
        if (minOffset == 0 || curOffset < minOffset || curOffset > maxOffset) {
            return 0;
        }
        newOffset = MathUtils.clamp(newOffset, minOffset, maxOffset);
        if (curOffset == newOffset) {
            return 0;
        }
        setTopAndBottomOffset(newOffset);
        return curOffset - newOffset;
    }

    int getTopBottomOffsetForScrollingSibling() {
        return getTopAndBottomOffset();
    }

    final int scroll(CoordinatorLayout coordinatorLayout, V header, int dy, int minOffset, int maxOffset) {
        return setHeaderTopBottomOffset(coordinatorLayout, header, getTopBottomOffsetForScrollingSibling() - dy, minOffset, maxOffset);
    }

    final boolean fling(CoordinatorLayout coordinatorLayout, V layout, int minOffset, int maxOffset, float velocityY) {
        V v = layout;
        Runnable runnable = this.mFlingRunnable;
        if (runnable != null) {
            layout.removeCallbacks(runnable);
            r0.mFlingRunnable = null;
        }
        if (r0.mScroller == null) {
            r0.mScroller = new OverScroller(layout.getContext());
        }
        r0.mScroller.fling(0, getTopAndBottomOffset(), 0, Math.round(velocityY), 0, 0, minOffset, maxOffset);
        if (r0.mScroller.computeScrollOffset()) {
            CoordinatorLayout coordinatorLayout2 = coordinatorLayout;
            r0.mFlingRunnable = new FlingRunnable(coordinatorLayout, layout);
            ViewCompat.postOnAnimation(layout, r0.mFlingRunnable);
            return true;
        }
        coordinatorLayout2 = coordinatorLayout;
        onFlingFinished(coordinatorLayout, layout);
        return false;
    }

    void onFlingFinished(CoordinatorLayout parent, V v) {
    }

    boolean canDragView(V v) {
        return false;
    }

    int getMaxDragOffset(V view) {
        return -view.getHeight();
    }

    int getScrollRangeForDragFling(V view) {
        return view.getHeight();
    }

    private void ensureVelocityTracker() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
    }
}
