package android.support.design.widget;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class SwipeDismissBehavior<V extends View> extends Behavior<V> {
    private static final float DEFAULT_ALPHA_END_DISTANCE = 0.5f;
    private static final float DEFAULT_ALPHA_START_DISTANCE = 0.0f;
    private static final float DEFAULT_DRAG_DISMISS_THRESHOLD = 0.5f;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_SETTLING = 2;
    public static final int SWIPE_DIRECTION_ANY = 2;
    public static final int SWIPE_DIRECTION_END_TO_START = 1;
    public static final int SWIPE_DIRECTION_START_TO_END = 0;
    float mAlphaEndSwipeDistance = 0.5f;
    float mAlphaStartSwipeDistance = 0.0f;
    private final Callback mDragCallback = new C08231();
    float mDragDismissThreshold = 0.5f;
    private boolean mInterceptingEvents;
    OnDismissListener mListener;
    private float mSensitivity = 0.0f;
    private boolean mSensitivitySet;
    int mSwipeDirection = 2;
    ViewDragHelper mViewDragHelper;

    public interface OnDismissListener {
        void onDismiss(View view);

        void onDragStateChanged(int i);
    }

    private class SettleRunnable implements Runnable {
        private final boolean mDismiss;
        private final View mView;

        SettleRunnable(View view, boolean dismiss) {
            this.mView = view;
            this.mDismiss = dismiss;
        }

        public void run() {
            if (SwipeDismissBehavior.this.mViewDragHelper != null && SwipeDismissBehavior.this.mViewDragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(this.mView, this);
            } else if (this.mDismiss && SwipeDismissBehavior.this.mListener != null) {
                SwipeDismissBehavior.this.mListener.onDismiss(this.mView);
            }
        }
    }

    /* renamed from: android.support.design.widget.SwipeDismissBehavior$1 */
    class C08231 extends Callback {
        private static final int INVALID_POINTER_ID = -1;
        private int mActivePointerId = -1;
        private int mOriginalCapturedViewLeft;

        C08231() {
        }

        public boolean tryCaptureView(View child, int pointerId) {
            return this.mActivePointerId == -1 && SwipeDismissBehavior.this.canSwipeDismissView(child);
        }

        public void onViewCaptured(View capturedChild, int activePointerId) {
            this.mActivePointerId = activePointerId;
            this.mOriginalCapturedViewLeft = capturedChild.getLeft();
            ViewParent parent = capturedChild.getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }

        public void onViewDragStateChanged(int state) {
            if (SwipeDismissBehavior.this.mListener != null) {
                SwipeDismissBehavior.this.mListener.onDragStateChanged(state);
            }
        }

        public void onViewReleased(View child, float xvel, float yvel) {
            int left;
            this.mActivePointerId = -1;
            int childWidth = child.getWidth();
            boolean dismiss = false;
            if (shouldDismiss(child, xvel)) {
                left = child.getLeft();
                int i = this.mOriginalCapturedViewLeft;
                left = left < i ? i - childWidth : i + childWidth;
                dismiss = true;
            } else {
                left = this.mOriginalCapturedViewLeft;
            }
            if (SwipeDismissBehavior.this.mViewDragHelper.settleCapturedViewAt(left, child.getTop())) {
                ViewCompat.postOnAnimation(child, new SettleRunnable(child, dismiss));
            } else if (dismiss && SwipeDismissBehavior.this.mListener != null) {
                SwipeDismissBehavior.this.mListener.onDismiss(child);
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private boolean shouldDismiss(android.view.View r7, float r8) {
            /*
            r6 = this;
            r0 = 0;
            r1 = 0;
            r2 = 1;
            r3 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
            if (r3 == 0) goto L_0x0043;
        L_0x0007:
            r3 = android.support.v4.view.ViewCompat.getLayoutDirection(r7);
            if (r3 != r2) goto L_0x000f;
        L_0x000d:
            r3 = 1;
            goto L_0x0010;
        L_0x000f:
            r3 = 0;
        L_0x0010:
            r4 = android.support.design.widget.SwipeDismissBehavior.this;
            r4 = r4.mSwipeDirection;
            r5 = 2;
            if (r4 != r5) goto L_0x0018;
        L_0x0017:
            return r2;
        L_0x0018:
            r4 = android.support.design.widget.SwipeDismissBehavior.this;
            r4 = r4.mSwipeDirection;
            if (r4 != 0) goto L_0x002c;
        L_0x001e:
            if (r3 == 0) goto L_0x0025;
        L_0x0020:
            r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
            if (r0 >= 0) goto L_0x002b;
        L_0x0024:
            goto L_0x0029;
        L_0x0025:
            r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
            if (r0 <= 0) goto L_0x002b;
        L_0x0029:
            r1 = 1;
        L_0x002b:
            return r1;
        L_0x002c:
            r4 = android.support.design.widget.SwipeDismissBehavior.this;
            r4 = r4.mSwipeDirection;
            if (r4 != r2) goto L_0x0040;
        L_0x0032:
            if (r3 == 0) goto L_0x0039;
        L_0x0034:
            r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
            if (r0 <= 0) goto L_0x003f;
        L_0x0038:
            goto L_0x003d;
        L_0x0039:
            r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
            if (r0 >= 0) goto L_0x003f;
        L_0x003d:
            r1 = 1;
        L_0x003f:
            return r1;
            return r1;
        L_0x0043:
            r0 = r7.getLeft();
            r3 = r6.mOriginalCapturedViewLeft;
            r0 = r0 - r3;
            r3 = r7.getWidth();
            r3 = (float) r3;
            r4 = android.support.design.widget.SwipeDismissBehavior.this;
            r4 = r4.mDragDismissThreshold;
            r3 = r3 * r4;
            r3 = java.lang.Math.round(r3);
            r4 = java.lang.Math.abs(r0);
            if (r4 < r3) goto L_0x0061;
        L_0x005f:
            r1 = 1;
        L_0x0061:
            return r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.design.widget.SwipeDismissBehavior.1.shouldDismiss(android.view.View, float):boolean");
        }

        public int getViewHorizontalDragRange(View child) {
            return child.getWidth();
        }

        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int min;
            int max;
            boolean isRtl = ViewCompat.getLayoutDirection(child) == 1;
            if (SwipeDismissBehavior.this.mSwipeDirection == 0) {
                if (isRtl) {
                    min = this.mOriginalCapturedViewLeft - child.getWidth();
                    max = this.mOriginalCapturedViewLeft;
                } else {
                    min = this.mOriginalCapturedViewLeft;
                    max = this.mOriginalCapturedViewLeft + child.getWidth();
                }
            } else if (SwipeDismissBehavior.this.mSwipeDirection != 1) {
                min = this.mOriginalCapturedViewLeft - child.getWidth();
                max = this.mOriginalCapturedViewLeft + child.getWidth();
            } else if (isRtl) {
                min = this.mOriginalCapturedViewLeft;
                max = this.mOriginalCapturedViewLeft + child.getWidth();
            } else {
                min = this.mOriginalCapturedViewLeft - child.getWidth();
                max = this.mOriginalCapturedViewLeft;
            }
            return SwipeDismissBehavior.clamp(min, left, max);
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            return child.getTop();
        }

        public void onViewPositionChanged(View child, int left, int top, int dx, int dy) {
            float startAlphaDistance = ((float) this.mOriginalCapturedViewLeft) + (((float) child.getWidth()) * SwipeDismissBehavior.this.mAlphaStartSwipeDistance);
            float endAlphaDistance = ((float) this.mOriginalCapturedViewLeft) + (((float) child.getWidth()) * SwipeDismissBehavior.this.mAlphaEndSwipeDistance);
            if (((float) left) <= startAlphaDistance) {
                child.setAlpha(1.0f);
            } else if (((float) left) >= endAlphaDistance) {
                child.setAlpha(0.0f);
            } else {
                child.setAlpha(SwipeDismissBehavior.clamp(0.0f, 1.0f - SwipeDismissBehavior.fraction(startAlphaDistance, endAlphaDistance, (float) left), 1.0f));
            }
        }
    }

    public void setListener(OnDismissListener listener) {
        this.mListener = listener;
    }

    public void setSwipeDirection(int direction) {
        this.mSwipeDirection = direction;
    }

    public void setDragDismissDistance(float distance) {
        this.mDragDismissThreshold = clamp(0.0f, distance, 1.0f);
    }

    public void setStartAlphaSwipeDistance(float fraction) {
        this.mAlphaStartSwipeDistance = clamp(0.0f, fraction, 1.0f);
    }

    public void setEndAlphaSwipeDistance(float fraction) {
        this.mAlphaEndSwipeDistance = clamp(0.0f, fraction, 1.0f);
    }

    public void setSensitivity(float sensitivity) {
        this.mSensitivity = sensitivity;
        this.mSensitivitySet = true;
    }

    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        boolean dispatchEventToHelper = this.mInterceptingEvents;
        int actionMasked = event.getActionMasked();
        if (actionMasked != 3) {
            switch (actionMasked) {
                case 0:
                    this.mInterceptingEvents = parent.isPointInChildBounds(child, (int) event.getX(), (int) event.getY());
                    dispatchEventToHelper = this.mInterceptingEvents;
                    break;
                case 1:
                    break;
                default:
                    break;
            }
        }
        this.mInterceptingEvents = false;
        if (!dispatchEventToHelper) {
            return false;
        }
        ensureViewDragHelper(parent);
        return this.mViewDragHelper.shouldInterceptTouchEvent(event);
    }

    public boolean onTouchEvent(CoordinatorLayout parent, V v, MotionEvent event) {
        ViewDragHelper viewDragHelper = this.mViewDragHelper;
        if (viewDragHelper == null) {
            return false;
        }
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    public boolean canSwipeDismissView(@NonNull View view) {
        return true;
    }

    private void ensureViewDragHelper(ViewGroup parent) {
        if (this.mViewDragHelper == null) {
            ViewDragHelper create;
            if (this.mSensitivitySet) {
                create = ViewDragHelper.create(parent, this.mSensitivity, this.mDragCallback);
            } else {
                create = ViewDragHelper.create(parent, this.mDragCallback);
            }
            this.mViewDragHelper = create;
        }
    }

    static float clamp(float min, float value, float max) {
        return Math.min(Math.max(min, value), max);
    }

    static int clamp(int min, int value, int max) {
        return Math.min(Math.max(min, value), max);
    }

    public int getDragState() {
        ViewDragHelper viewDragHelper = this.mViewDragHelper;
        return viewDragHelper != null ? viewDragHelper.getViewDragState() : 0;
    }

    static float fraction(float startValue, float endValue, float value) {
        return (value - startValue) / (endValue - startValue);
    }
}
