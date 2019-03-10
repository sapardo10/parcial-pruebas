package android.support.v7.widget.helper;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.recyclerview.C0321R;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ChildDrawingOrderCallback;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.OnChildAttachStateChangeListener;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import java.util.ArrayList;
import java.util.List;

public class ItemTouchHelper extends ItemDecoration implements OnChildAttachStateChangeListener {
    static final int ACTION_MODE_DRAG_MASK = 16711680;
    private static final int ACTION_MODE_IDLE_MASK = 255;
    static final int ACTION_MODE_SWIPE_MASK = 65280;
    public static final int ACTION_STATE_DRAG = 2;
    public static final int ACTION_STATE_IDLE = 0;
    public static final int ACTION_STATE_SWIPE = 1;
    static final int ACTIVE_POINTER_ID_NONE = -1;
    public static final int ANIMATION_TYPE_DRAG = 8;
    public static final int ANIMATION_TYPE_SWIPE_CANCEL = 4;
    public static final int ANIMATION_TYPE_SWIPE_SUCCESS = 2;
    static final boolean DEBUG = false;
    static final int DIRECTION_FLAG_COUNT = 8;
    public static final int DOWN = 2;
    public static final int END = 32;
    public static final int LEFT = 4;
    private static final int PIXELS_PER_SECOND = 1000;
    public static final int RIGHT = 8;
    public static final int START = 16;
    static final String TAG = "ItemTouchHelper";
    public static final int UP = 1;
    int mActionState = 0;
    int mActivePointerId = -1;
    Callback mCallback;
    private ChildDrawingOrderCallback mChildDrawingOrderCallback = null;
    private List<Integer> mDistances;
    private long mDragScrollStartTimeInMs;
    float mDx;
    float mDy;
    GestureDetectorCompat mGestureDetector;
    float mInitialTouchX;
    float mInitialTouchY;
    private ItemTouchHelperGestureListener mItemTouchHelperGestureListener;
    float mMaxSwipeVelocity;
    private final OnItemTouchListener mOnItemTouchListener = new C09062();
    View mOverdrawChild = null;
    int mOverdrawChildPosition = -1;
    final List<View> mPendingCleanup = new ArrayList();
    List<RecoverAnimation> mRecoverAnimations = new ArrayList();
    RecyclerView mRecyclerView;
    final Runnable mScrollRunnable = new C03901();
    ViewHolder mSelected = null;
    int mSelectedFlags;
    float mSelectedStartX;
    float mSelectedStartY;
    private int mSlop;
    private List<ViewHolder> mSwapTargets;
    float mSwipeEscapeVelocity;
    private final float[] mTmpPosition = new float[2];
    private Rect mTmpRect;
    VelocityTracker mVelocityTracker;

    /* renamed from: android.support.v7.widget.helper.ItemTouchHelper$1 */
    class C03901 implements Runnable {
        C03901() {
        }

        public void run() {
            if (ItemTouchHelper.this.mSelected != null && ItemTouchHelper.this.scrollIfNecessary()) {
                if (ItemTouchHelper.this.mSelected != null) {
                    ItemTouchHelper itemTouchHelper = ItemTouchHelper.this;
                    itemTouchHelper.moveIfNecessary(itemTouchHelper.mSelected);
                }
                ItemTouchHelper.this.mRecyclerView.removeCallbacks(ItemTouchHelper.this.mScrollRunnable);
                ViewCompat.postOnAnimation(ItemTouchHelper.this.mRecyclerView, this);
            }
        }
    }

    public static abstract class Callback {
        private static final int ABS_HORIZONTAL_DIR_FLAGS = 789516;
        public static final int DEFAULT_DRAG_ANIMATION_DURATION = 200;
        public static final int DEFAULT_SWIPE_ANIMATION_DURATION = 250;
        private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 2000;
        static final int RELATIVE_DIR_FLAGS = 3158064;
        private static final Interpolator sDragScrollInterpolator = new C03921();
        private static final Interpolator sDragViewScrollCapInterpolator = new C03932();
        private static final ItemTouchUIUtil sUICallback;
        private int mCachedMaxScrollSpeed = -1;

        /* renamed from: android.support.v7.widget.helper.ItemTouchHelper$Callback$1 */
        static class C03921 implements Interpolator {
            C03921() {
            }

            public float getInterpolation(float t) {
                return (((t * t) * t) * t) * t;
            }
        }

        /* renamed from: android.support.v7.widget.helper.ItemTouchHelper$Callback$2 */
        static class C03932 implements Interpolator {
            C03932() {
            }

            public float getInterpolation(float t) {
                t -= 1.0f;
                return ((((t * t) * t) * t) * t) + 1.0f;
            }
        }

        public abstract int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder);

        public abstract boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder viewHolder2);

        public abstract void onSwiped(ViewHolder viewHolder, int i);

        static {
            if (VERSION.SDK_INT >= 21) {
                sUICallback = new Api21Impl();
            } else {
                sUICallback = new BaseImpl();
            }
        }

        public static ItemTouchUIUtil getDefaultUIUtil() {
            return sUICallback;
        }

        public static int convertToRelativeDirection(int flags, int layoutDirection) {
            int masked = flags & ABS_HORIZONTAL_DIR_FLAGS;
            if (masked == 0) {
                return flags;
            }
            flags &= masked ^ -1;
            if (layoutDirection == 0) {
                return flags | (masked << 2);
            }
            return (flags | ((masked << 1) & -789517)) | ((ABS_HORIZONTAL_DIR_FLAGS & (masked << 1)) << 2);
        }

        public static int makeMovementFlags(int dragFlags, int swipeFlags) {
            return (makeFlag(0, swipeFlags | dragFlags) | makeFlag(1, swipeFlags)) | makeFlag(2, dragFlags);
        }

        public static int makeFlag(int actionState, int directions) {
            return directions << (actionState * 8);
        }

        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            int masked = flags & RELATIVE_DIR_FLAGS;
            if (masked == 0) {
                return flags;
            }
            flags &= masked ^ -1;
            if (layoutDirection == 0) {
                return flags | (masked >> 2);
            }
            return (flags | ((masked >> 1) & -3158065)) | ((RELATIVE_DIR_FLAGS & (masked >> 1)) >> 2);
        }

        final int getAbsoluteMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            return convertToAbsoluteDirection(getMovementFlags(recyclerView, viewHolder), ViewCompat.getLayoutDirection(recyclerView));
        }

        boolean hasDragFlag(RecyclerView recyclerView, ViewHolder viewHolder) {
            return (ItemTouchHelper.ACTION_MODE_DRAG_MASK & getAbsoluteMovementFlags(recyclerView, viewHolder)) != 0;
        }

        boolean hasSwipeFlag(RecyclerView recyclerView, ViewHolder viewHolder) {
            return (65280 & getAbsoluteMovementFlags(recyclerView, viewHolder)) != 0;
        }

        public boolean canDropOver(RecyclerView recyclerView, ViewHolder current, ViewHolder target) {
            return true;
        }

        public boolean isLongPressDragEnabled() {
            return true;
        }

        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        public int getBoundingBoxMargin() {
            return 0;
        }

        public float getSwipeThreshold(ViewHolder viewHolder) {
            return 0.5f;
        }

        public float getMoveThreshold(ViewHolder viewHolder) {
            return 0.5f;
        }

        public float getSwipeEscapeVelocity(float defaultValue) {
            return defaultValue;
        }

        public float getSwipeVelocityThreshold(float defaultValue) {
            return defaultValue;
        }

        public ViewHolder chooseDropTarget(ViewHolder selected, List<ViewHolder> dropTargets, int curX, int curY) {
            ViewHolder viewHolder = selected;
            int right = curX + viewHolder.itemView.getWidth();
            int bottom = curY + viewHolder.itemView.getHeight();
            ViewHolder winner = null;
            int winnerScore = -1;
            int dx = curX - viewHolder.itemView.getLeft();
            int dy = curY - viewHolder.itemView.getTop();
            int targetsSize = dropTargets.size();
            for (int i = 0; i < targetsSize; i++) {
                int diff;
                int score;
                ViewHolder target = (ViewHolder) dropTargets.get(i);
                if (dx > 0) {
                    diff = target.itemView.getRight() - right;
                    if (diff < 0 && target.itemView.getRight() > viewHolder.itemView.getRight()) {
                        score = Math.abs(diff);
                        if (score > winnerScore) {
                            winnerScore = score;
                            winner = target;
                        }
                    }
                }
                if (dx < 0) {
                    diff = target.itemView.getLeft() - curX;
                    if (diff > 0 && target.itemView.getLeft() < viewHolder.itemView.getLeft()) {
                        score = Math.abs(diff);
                        if (score > winnerScore) {
                            winnerScore = score;
                            winner = target;
                        }
                    }
                }
                if (dy < 0) {
                    diff = target.itemView.getTop() - curY;
                    if (diff > 0 && target.itemView.getTop() < viewHolder.itemView.getTop()) {
                        score = Math.abs(diff);
                        if (score > winnerScore) {
                            winnerScore = score;
                            winner = target;
                        }
                    }
                }
                if (dy > 0) {
                    diff = target.itemView.getBottom() - bottom;
                    if (diff < 0 && target.itemView.getBottom() > viewHolder.itemView.getBottom()) {
                        score = Math.abs(diff);
                        if (score > winnerScore) {
                            winnerScore = score;
                            winner = target;
                        }
                    }
                }
            }
            List<ViewHolder> list = dropTargets;
            return winner;
        }

        public void onSelectedChanged(ViewHolder viewHolder, int actionState) {
            if (viewHolder != null) {
                sUICallback.onSelected(viewHolder.itemView);
            }
        }

        private int getMaxDragScroll(RecyclerView recyclerView) {
            if (this.mCachedMaxScrollSpeed == -1) {
                this.mCachedMaxScrollSpeed = recyclerView.getResources().getDimensionPixelSize(C0321R.dimen.item_touch_helper_max_drag_scroll_per_frame);
            }
            return this.mCachedMaxScrollSpeed;
        }

        public void onMoved(RecyclerView recyclerView, ViewHolder viewHolder, int fromPos, ViewHolder target, int toPos, int x, int y) {
            LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof ViewDropHandler) {
                ((ViewDropHandler) layoutManager).prepareForDrop(viewHolder.itemView, target.itemView, x, y);
                return;
            }
            if (layoutManager.canScrollHorizontally()) {
                if (layoutManager.getDecoratedLeft(target.itemView) <= recyclerView.getPaddingLeft()) {
                    recyclerView.scrollToPosition(toPos);
                }
                if (layoutManager.getDecoratedRight(target.itemView) >= recyclerView.getWidth() - recyclerView.getPaddingRight()) {
                    recyclerView.scrollToPosition(toPos);
                }
            }
            if (layoutManager.canScrollVertically()) {
                if (layoutManager.getDecoratedTop(target.itemView) <= recyclerView.getPaddingTop()) {
                    recyclerView.scrollToPosition(toPos);
                }
                if (layoutManager.getDecoratedBottom(target.itemView) >= recyclerView.getHeight() - recyclerView.getPaddingBottom()) {
                    recyclerView.scrollToPosition(toPos);
                }
            }
        }

        void onDraw(Canvas c, RecyclerView parent, ViewHolder selected, List<RecoverAnimation> recoverAnimationList, int actionState, float dX, float dY) {
            int i;
            Canvas canvas = c;
            int recoverAnimSize = recoverAnimationList.size();
            for (i = 0; i < recoverAnimSize; i++) {
                RecoverAnimation anim = (RecoverAnimation) recoverAnimationList.get(i);
                anim.update();
                int count = c.save();
                onChildDraw(c, parent, anim.mViewHolder, anim.mX, anim.mY, anim.mActionState, false);
                c.restoreToCount(count);
            }
            List<RecoverAnimation> list = recoverAnimationList;
            if (selected != null) {
                i = c.save();
                onChildDraw(c, parent, selected, dX, dY, actionState, true);
                c.restoreToCount(i);
            }
        }

        void onDrawOver(Canvas c, RecyclerView parent, ViewHolder selected, List<RecoverAnimation> recoverAnimationList, int actionState, float dX, float dY) {
            int i;
            Canvas canvas = c;
            List<RecoverAnimation> list = recoverAnimationList;
            int recoverAnimSize = recoverAnimationList.size();
            for (i = 0; i < recoverAnimSize; i++) {
                RecoverAnimation anim = (RecoverAnimation) list.get(i);
                int count = c.save();
                onChildDrawOver(c, parent, anim.mViewHolder, anim.mX, anim.mY, anim.mActionState, false);
                c.restoreToCount(count);
            }
            if (selected != null) {
                i = c.save();
                onChildDrawOver(c, parent, selected, dX, dY, actionState, true);
                c.restoreToCount(i);
            }
            boolean hasRunningAnimation = false;
            for (int i2 = recoverAnimSize - 1; i2 >= 0; i2--) {
                RecoverAnimation anim2 = (RecoverAnimation) list.get(i2);
                if (anim2.mEnded && !anim2.mIsPendingCleanup) {
                    list.remove(i2);
                } else if (!anim2.mEnded) {
                    hasRunningAnimation = true;
                }
            }
            if (hasRunningAnimation) {
                parent.invalidate();
            }
        }

        public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
            sUICallback.clearView(viewHolder.itemView);
        }

        public void onChildDraw(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            sUICallback.onDraw(c, recyclerView, viewHolder.itemView, dX, dY, actionState, isCurrentlyActive);
        }

        public void onChildDrawOver(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            sUICallback.onDrawOver(c, recyclerView, viewHolder.itemView, dX, dY, actionState, isCurrentlyActive);
        }

        public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            ItemAnimator itemAnimator = recyclerView.getItemAnimator();
            if (itemAnimator == null) {
                return animationType == 8 ? 200 : 250;
            }
            long moveDuration;
            if (animationType == 8) {
                moveDuration = itemAnimator.getMoveDuration();
            } else {
                moveDuration = itemAnimator.getRemoveDuration();
            }
            return moveDuration;
        }

        public int interpolateOutOfBoundsScroll(RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {
            float timeRatio;
            int cappedScroll = (int) (((float) (((int) Math.signum((float) viewSizeOutOfBounds)) * getMaxDragScroll(recyclerView))) * sDragViewScrollCapInterpolator.getInterpolation(Math.min(1.0f, (((float) Math.abs(viewSizeOutOfBounds)) * 1.0f) / ((float) viewSize))));
            if (msSinceStartScroll > 2000) {
                timeRatio = 1.0f;
            } else {
                timeRatio = ((float) msSinceStartScroll) / 2000.0f;
            }
            int value = (int) (((float) cappedScroll) * sDragScrollInterpolator.getInterpolation(timeRatio));
            if (value != 0) {
                return value;
            }
            return viewSizeOutOfBounds > 0 ? 1 : -1;
        }
    }

    private class ItemTouchHelperGestureListener extends SimpleOnGestureListener {
        private boolean mShouldReactToLongPress = true;

        ItemTouchHelperGestureListener() {
        }

        void doNotReactToLongPress() {
            this.mShouldReactToLongPress = false;
        }

        public boolean onDown(MotionEvent e) {
            return true;
        }

        public void onLongPress(MotionEvent e) {
            if (this.mShouldReactToLongPress) {
                View child = ItemTouchHelper.this.findChildView(e);
                if (child != null) {
                    ViewHolder vh = ItemTouchHelper.this.mRecyclerView.getChildViewHolder(child);
                    if (vh != null) {
                        if (!ItemTouchHelper.this.mCallback.hasDragFlag(ItemTouchHelper.this.mRecyclerView, vh)) {
                            return;
                        }
                        if (e.getPointerId(0) == ItemTouchHelper.this.mActivePointerId) {
                            int index = e.findPointerIndex(ItemTouchHelper.this.mActivePointerId);
                            float x = e.getX(index);
                            float y = e.getY(index);
                            ItemTouchHelper itemTouchHelper = ItemTouchHelper.this;
                            itemTouchHelper.mInitialTouchX = x;
                            itemTouchHelper.mInitialTouchY = y;
                            itemTouchHelper.mDy = 0.0f;
                            itemTouchHelper.mDx = 0.0f;
                            if (itemTouchHelper.mCallback.isLongPressDragEnabled()) {
                                ItemTouchHelper.this.select(vh, 2);
                            }
                        }
                    }
                }
            }
        }
    }

    private static class RecoverAnimation implements AnimatorListener {
        final int mActionState;
        final int mAnimationType;
        boolean mEnded = false;
        private float mFraction;
        public boolean mIsPendingCleanup;
        boolean mOverridden = false;
        final float mStartDx;
        final float mStartDy;
        final float mTargetX;
        final float mTargetY;
        private final ValueAnimator mValueAnimator;
        final ViewHolder mViewHolder;
        float mX;
        float mY;

        /* renamed from: android.support.v7.widget.helper.ItemTouchHelper$RecoverAnimation$1 */
        class C03941 implements AnimatorUpdateListener {
            C03941() {
            }

            public void onAnimationUpdate(ValueAnimator animation) {
                RecoverAnimation.this.setFraction(animation.getAnimatedFraction());
            }
        }

        RecoverAnimation(ViewHolder viewHolder, int animationType, int actionState, float startDx, float startDy, float targetX, float targetY) {
            this.mActionState = actionState;
            this.mAnimationType = animationType;
            this.mViewHolder = viewHolder;
            this.mStartDx = startDx;
            this.mStartDy = startDy;
            this.mTargetX = targetX;
            this.mTargetY = targetY;
            this.mValueAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.mValueAnimator.addUpdateListener(new C03941());
            this.mValueAnimator.setTarget(viewHolder.itemView);
            this.mValueAnimator.addListener(this);
            setFraction(0.0f);
        }

        public void setDuration(long duration) {
            this.mValueAnimator.setDuration(duration);
        }

        public void start() {
            this.mViewHolder.setIsRecyclable(false);
            this.mValueAnimator.start();
        }

        public void cancel() {
            this.mValueAnimator.cancel();
        }

        public void setFraction(float fraction) {
            this.mFraction = fraction;
        }

        public void update() {
            float f = this.mStartDx;
            float f2 = this.mTargetX;
            if (f == f2) {
                this.mX = this.mViewHolder.itemView.getTranslationX();
            } else {
                this.mX = f + (this.mFraction * (f2 - f));
            }
            f = this.mStartDy;
            f2 = this.mTargetY;
            if (f == f2) {
                this.mY = this.mViewHolder.itemView.getTranslationY();
            } else {
                this.mY = f + (this.mFraction * (f2 - f));
            }
        }

        public void onAnimationStart(Animator animation) {
        }

        public void onAnimationEnd(Animator animation) {
            if (!this.mEnded) {
                this.mViewHolder.setIsRecyclable(true);
            }
            this.mEnded = true;
        }

        public void onAnimationCancel(Animator animation) {
            setFraction(1.0f);
        }

        public void onAnimationRepeat(Animator animation) {
        }
    }

    public interface ViewDropHandler {
        void prepareForDrop(View view, View view2, int i, int i2);
    }

    /* renamed from: android.support.v7.widget.helper.ItemTouchHelper$2 */
    class C09062 implements OnItemTouchListener {
        C09062() {
        }

        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent event) {
            ItemTouchHelper.this.mGestureDetector.onTouchEvent(event);
            int action = event.getActionMasked();
            if (action == 0) {
                ItemTouchHelper.this.mActivePointerId = event.getPointerId(0);
                ItemTouchHelper.this.mInitialTouchX = event.getX();
                ItemTouchHelper.this.mInitialTouchY = event.getY();
                ItemTouchHelper.this.obtainVelocityTracker();
                if (ItemTouchHelper.this.mSelected == null) {
                    RecoverAnimation animation = ItemTouchHelper.this.findAnimation(event);
                    if (animation != null) {
                        ItemTouchHelper itemTouchHelper = ItemTouchHelper.this;
                        itemTouchHelper.mInitialTouchX -= animation.mX;
                        itemTouchHelper = ItemTouchHelper.this;
                        itemTouchHelper.mInitialTouchY -= animation.mY;
                        ItemTouchHelper.this.endRecoverAnimation(animation.mViewHolder, true);
                        if (ItemTouchHelper.this.mPendingCleanup.remove(animation.mViewHolder.itemView)) {
                            ItemTouchHelper.this.mCallback.clearView(ItemTouchHelper.this.mRecyclerView, animation.mViewHolder);
                        }
                        ItemTouchHelper.this.select(animation.mViewHolder, animation.mActionState);
                        itemTouchHelper = ItemTouchHelper.this;
                        itemTouchHelper.updateDxDy(event, itemTouchHelper.mSelectedFlags, 0);
                    }
                }
            } else {
                if (action != 3) {
                    if (action != 1) {
                        if (ItemTouchHelper.this.mActivePointerId != -1) {
                            int index = event.findPointerIndex(ItemTouchHelper.this.mActivePointerId);
                            if (index >= 0) {
                                ItemTouchHelper.this.checkSelectForSwipe(action, event, index);
                            }
                        }
                    }
                }
                ItemTouchHelper itemTouchHelper2 = ItemTouchHelper.this;
                itemTouchHelper2.mActivePointerId = -1;
                itemTouchHelper2.select(null, 0);
            }
            if (ItemTouchHelper.this.mVelocityTracker != null) {
                ItemTouchHelper.this.mVelocityTracker.addMovement(event);
            }
            if (ItemTouchHelper.this.mSelected != null) {
                return true;
            }
            return false;
        }

        public void onTouchEvent(RecyclerView recyclerView, MotionEvent event) {
            ItemTouchHelper.this.mGestureDetector.onTouchEvent(event);
            if (ItemTouchHelper.this.mVelocityTracker != null) {
                ItemTouchHelper.this.mVelocityTracker.addMovement(event);
            }
            if (ItemTouchHelper.this.mActivePointerId != -1) {
                int action = event.getActionMasked();
                int activePointerIndex = event.findPointerIndex(ItemTouchHelper.this.mActivePointerId);
                if (activePointerIndex >= 0) {
                    ItemTouchHelper.this.checkSelectForSwipe(action, event, activePointerIndex);
                }
                ViewHolder viewHolder = ItemTouchHelper.this.mSelected;
                if (viewHolder != null) {
                    int newPointerIndex = 0;
                    if (action != 6) {
                        switch (action) {
                            case 1:
                                break;
                            case 2:
                                if (activePointerIndex < 0) {
                                    break;
                                }
                                ItemTouchHelper itemTouchHelper = ItemTouchHelper.this;
                                itemTouchHelper.updateDxDy(event, itemTouchHelper.mSelectedFlags, activePointerIndex);
                                ItemTouchHelper.this.moveIfNecessary(viewHolder);
                                ItemTouchHelper.this.mRecyclerView.removeCallbacks(ItemTouchHelper.this.mScrollRunnable);
                                ItemTouchHelper.this.mScrollRunnable.run();
                                ItemTouchHelper.this.mRecyclerView.invalidate();
                                break;
                            case 3:
                                if (ItemTouchHelper.this.mVelocityTracker == null) {
                                    break;
                                }
                                ItemTouchHelper.this.mVelocityTracker.clear();
                                break;
                            default:
                                break;
                        }
                        ItemTouchHelper.this.select(null, 0);
                        ItemTouchHelper.this.mActivePointerId = -1;
                    } else {
                        int pointerIndex = event.getActionIndex();
                        if (event.getPointerId(pointerIndex) == ItemTouchHelper.this.mActivePointerId) {
                            if (pointerIndex == 0) {
                                newPointerIndex = 1;
                            }
                            ItemTouchHelper.this.mActivePointerId = event.getPointerId(newPointerIndex);
                            ItemTouchHelper itemTouchHelper2 = ItemTouchHelper.this;
                            itemTouchHelper2.updateDxDy(event, itemTouchHelper2.mSelectedFlags, pointerIndex);
                        }
                    }
                }
            }
        }

        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            if (disallowIntercept) {
                ItemTouchHelper.this.select(null, 0);
            }
        }
    }

    /* renamed from: android.support.v7.widget.helper.ItemTouchHelper$5 */
    class C09085 implements ChildDrawingOrderCallback {
        C09085() {
        }

        public int onGetChildDrawingOrder(int childCount, int i) {
            if (ItemTouchHelper.this.mOverdrawChild == null) {
                return i;
            }
            int childPosition = ItemTouchHelper.this.mOverdrawChildPosition;
            if (childPosition == -1) {
                childPosition = ItemTouchHelper.this.mRecyclerView.indexOfChild(ItemTouchHelper.this.mOverdrawChild);
                ItemTouchHelper.this.mOverdrawChildPosition = childPosition;
            }
            if (i == childCount - 1) {
                return childPosition;
            }
            return i < childPosition ? i : i + 1;
        }
    }

    public static abstract class SimpleCallback extends Callback {
        private int mDefaultDragDirs;
        private int mDefaultSwipeDirs;

        public SimpleCallback(int dragDirs, int swipeDirs) {
            this.mDefaultSwipeDirs = swipeDirs;
            this.mDefaultDragDirs = dragDirs;
        }

        public void setDefaultSwipeDirs(int defaultSwipeDirs) {
            this.mDefaultSwipeDirs = defaultSwipeDirs;
        }

        public void setDefaultDragDirs(int defaultDragDirs) {
            this.mDefaultDragDirs = defaultDragDirs;
        }

        public int getSwipeDirs(RecyclerView recyclerView, ViewHolder viewHolder) {
            return this.mDefaultSwipeDirs;
        }

        public int getDragDirs(RecyclerView recyclerView, ViewHolder viewHolder) {
            return this.mDefaultDragDirs;
        }

        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            return Callback.makeMovementFlags(getDragDirs(recyclerView, viewHolder), getSwipeDirs(recyclerView, viewHolder));
        }
    }

    public ItemTouchHelper(Callback callback) {
        this.mCallback = callback;
    }

    private static boolean hitTest(View child, float x, float y, float left, float top) {
        if (x >= left) {
            if (x <= ((float) child.getWidth()) + left && y >= top) {
                if (y <= ((float) child.getHeight()) + top) {
                    return true;
                }
            }
        }
        return false;
    }

    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        RecyclerView recyclerView2 = this.mRecyclerView;
        if (recyclerView2 != recyclerView) {
            if (recyclerView2 != null) {
                destroyCallbacks();
            }
            this.mRecyclerView = recyclerView;
            if (recyclerView != null) {
                Resources resources = recyclerView.getResources();
                this.mSwipeEscapeVelocity = resources.getDimension(C0321R.dimen.item_touch_helper_swipe_escape_velocity);
                this.mMaxSwipeVelocity = resources.getDimension(C0321R.dimen.item_touch_helper_swipe_escape_max_velocity);
                setupCallbacks();
            }
        }
    }

    private void setupCallbacks() {
        this.mSlop = ViewConfiguration.get(this.mRecyclerView.getContext()).getScaledTouchSlop();
        this.mRecyclerView.addItemDecoration(this);
        this.mRecyclerView.addOnItemTouchListener(this.mOnItemTouchListener);
        this.mRecyclerView.addOnChildAttachStateChangeListener(this);
        startGestureDetection();
    }

    private void destroyCallbacks() {
        this.mRecyclerView.removeItemDecoration(this);
        this.mRecyclerView.removeOnItemTouchListener(this.mOnItemTouchListener);
        this.mRecyclerView.removeOnChildAttachStateChangeListener(this);
        for (int i = this.mRecoverAnimations.size() - 1; i >= 0; i--) {
            this.mCallback.clearView(this.mRecyclerView, ((RecoverAnimation) this.mRecoverAnimations.get(0)).mViewHolder);
        }
        this.mRecoverAnimations.clear();
        this.mOverdrawChild = null;
        this.mOverdrawChildPosition = -1;
        releaseVelocityTracker();
        stopGestureDetection();
    }

    private void startGestureDetection() {
        this.mItemTouchHelperGestureListener = new ItemTouchHelperGestureListener();
        this.mGestureDetector = new GestureDetectorCompat(this.mRecyclerView.getContext(), this.mItemTouchHelperGestureListener);
    }

    private void stopGestureDetection() {
        ItemTouchHelperGestureListener itemTouchHelperGestureListener = this.mItemTouchHelperGestureListener;
        if (itemTouchHelperGestureListener != null) {
            itemTouchHelperGestureListener.doNotReactToLongPress();
            this.mItemTouchHelperGestureListener = null;
        }
        if (this.mGestureDetector != null) {
            this.mGestureDetector = null;
        }
    }

    private void getSelectedDxDy(float[] outPosition) {
        if ((this.mSelectedFlags & 12) != 0) {
            outPosition[0] = (this.mSelectedStartX + this.mDx) - ((float) this.mSelected.itemView.getLeft());
        } else {
            outPosition[0] = this.mSelected.itemView.getTranslationX();
        }
        if ((this.mSelectedFlags & 3) != 0) {
            outPosition[1] = (this.mSelectedStartY + this.mDy) - ((float) this.mSelected.itemView.getTop());
        } else {
            outPosition[1] = this.mSelected.itemView.getTranslationY();
        }
    }

    public void onDrawOver(Canvas c, RecyclerView parent, State state) {
        float dx = 0.0f;
        float dy = 0.0f;
        if (this.mSelected != null) {
            getSelectedDxDy(this.mTmpPosition);
            float[] fArr = this.mTmpPosition;
            dx = fArr[0];
            dy = fArr[1];
        }
        this.mCallback.onDrawOver(c, parent, this.mSelected, this.mRecoverAnimations, this.mActionState, dx, dy);
    }

    public void onDraw(Canvas c, RecyclerView parent, State state) {
        this.mOverdrawChildPosition = -1;
        float dx = 0.0f;
        float dy = 0.0f;
        if (this.mSelected != null) {
            getSelectedDxDy(this.mTmpPosition);
            float[] fArr = this.mTmpPosition;
            dx = fArr[0];
            dy = fArr[1];
        }
        this.mCallback.onDraw(c, parent, this.mSelected, this.mRecoverAnimations, this.mActionState, dx, dy);
    }

    void select(ViewHolder selected, int actionState) {
        ViewHolder viewHolder = selected;
        int i = actionState;
        if (viewHolder != this.mSelected || i != r11.mActionState) {
            boolean z;
            r11.mDragScrollStartTimeInMs = Long.MIN_VALUE;
            int prevActionState = r11.mActionState;
            endRecoverAnimation(viewHolder, true);
            r11.mActionState = i;
            if (i == 2) {
                r11.mOverdrawChild = viewHolder.itemView;
                addChildDrawingOrderCallback();
            }
            int actionStateMask = (1 << ((i * 8) + 8)) - 1;
            boolean preventLayout = false;
            if (r11.mSelected != null) {
                ViewHolder prevSelected = r11.mSelected;
                if (prevSelected.itemView.getParent() != null) {
                    int i2;
                    float targetTranslateX;
                    float targetTranslateY;
                    int animationType;
                    if (prevActionState == 2) {
                        i2 = 0;
                    } else {
                        i2 = swipeIfNecessary(prevSelected);
                    }
                    int swipeDir = i2;
                    releaseVelocityTracker();
                    if (swipeDir != 4 && swipeDir != 8 && swipeDir != 16 && swipeDir != 32) {
                        switch (swipeDir) {
                            case 1:
                            case 2:
                                targetTranslateX = 0.0f;
                                targetTranslateY = Math.signum(r11.mDy) * ((float) r11.mRecyclerView.getHeight());
                                break;
                            default:
                                targetTranslateX = 0.0f;
                                targetTranslateY = 0.0f;
                                break;
                        }
                    }
                    targetTranslateY = 0.0f;
                    targetTranslateX = Math.signum(r11.mDx) * ((float) r11.mRecyclerView.getWidth());
                    if (prevActionState == 2) {
                        animationType = 8;
                    } else if (swipeDir > 0) {
                        animationType = 2;
                    } else {
                        animationType = 4;
                    }
                    getSelectedDxDy(r11.mTmpPosition);
                    float[] fArr = r11.mTmpPosition;
                    float currentTranslateX = fArr[0];
                    float currentTranslateY = fArr[1];
                    int animationType2 = animationType;
                    ViewHolder prevSelected2 = prevSelected;
                    final int i3 = swipeDir;
                    prevActionState = 2;
                    final ViewHolder viewHolder2 = prevSelected2;
                    RecoverAnimation rv = new RecoverAnimation(prevSelected, animationType, prevActionState, currentTranslateX, currentTranslateY, targetTranslateX, targetTranslateY) {
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (!this.mOverridden) {
                                if (i3 <= 0) {
                                    ItemTouchHelper.this.mCallback.clearView(ItemTouchHelper.this.mRecyclerView, viewHolder2);
                                } else {
                                    ItemTouchHelper.this.mPendingCleanup.add(viewHolder2.itemView);
                                    this.mIsPendingCleanup = true;
                                    int i = i3;
                                    if (i > 0) {
                                        ItemTouchHelper.this.postDispatchSwipe(this, i);
                                    }
                                }
                                if (ItemTouchHelper.this.mOverdrawChild == viewHolder2.itemView) {
                                    ItemTouchHelper.this.removeChildDrawingOrderCallbackIfNecessary(viewHolder2.itemView);
                                }
                            }
                        }
                    };
                    rv.setDuration(r11.mCallback.getAnimationDuration(r11.mRecyclerView, animationType2, targetTranslateX - currentTranslateX, targetTranslateY - currentTranslateY));
                    r11.mRecoverAnimations.add(rv);
                    rv.start();
                    preventLayout = true;
                    boolean preventLayout2 = prevSelected2;
                } else {
                    int i4 = prevActionState;
                    prevActionState = 2;
                    ViewHolder prevSelected3 = prevSelected;
                    removeChildDrawingOrderCallbackIfNecessary(prevSelected3.itemView);
                    r11.mCallback.clearView(r11.mRecyclerView, prevSelected3);
                }
                r11.mSelected = null;
            } else {
                prevActionState = 2;
            }
            if (viewHolder != null) {
                r11.mSelectedFlags = (r11.mCallback.getAbsoluteMovementFlags(r11.mRecyclerView, viewHolder) & actionStateMask) >> (r11.mActionState * 8);
                r11.mSelectedStartX = (float) viewHolder.itemView.getLeft();
                r11.mSelectedStartY = (float) viewHolder.itemView.getTop();
                r11.mSelected = viewHolder;
                if (i == prevActionState) {
                    z = false;
                    r11.mSelected.itemView.performHapticFeedback(0);
                } else {
                    z = false;
                }
            } else {
                z = false;
            }
            ViewParent rvParent = r11.mRecyclerView.getParent();
            if (rvParent != null) {
                if (r11.mSelected != null) {
                    z = true;
                }
                rvParent.requestDisallowInterceptTouchEvent(z);
            }
            if (!preventLayout) {
                r11.mRecyclerView.getLayoutManager().requestSimpleAnimationsInNextLayout();
            }
            r11.mCallback.onSelectedChanged(r11.mSelected, r11.mActionState);
            r11.mRecyclerView.invalidate();
        }
    }

    void postDispatchSwipe(final RecoverAnimation anim, final int swipeDir) {
        this.mRecyclerView.post(new Runnable() {
            public void run() {
                if (ItemTouchHelper.this.mRecyclerView != null && ItemTouchHelper.this.mRecyclerView.isAttachedToWindow() && !anim.mOverridden) {
                    if (anim.mViewHolder.getAdapterPosition() != -1) {
                        ItemAnimator animator = ItemTouchHelper.this.mRecyclerView.getItemAnimator();
                        if (animator != null) {
                            if (animator.isRunning(null)) {
                                ItemTouchHelper.this.mRecyclerView.post(this);
                            }
                        }
                        if (ItemTouchHelper.this.hasRunningRecoverAnim()) {
                            ItemTouchHelper.this.mRecyclerView.post(this);
                        } else {
                            ItemTouchHelper.this.mCallback.onSwiped(anim.mViewHolder, swipeDir);
                        }
                    }
                }
            }
        });
    }

    boolean hasRunningRecoverAnim() {
        int size = this.mRecoverAnimations.size();
        for (int i = 0; i < size; i++) {
            if (!((RecoverAnimation) this.mRecoverAnimations.get(i)).mEnded) {
                return true;
            }
        }
        return false;
    }

    boolean scrollIfNecessary() {
        if (this.mSelected == null) {
            r0.mDragScrollStartTimeInMs = Long.MIN_VALUE;
            return false;
        }
        int curX;
        long now = System.currentTimeMillis();
        long j = r0.mDragScrollStartTimeInMs;
        long scrollDuration = j == Long.MIN_VALUE ? 0 : now - j;
        LayoutManager lm = r0.mRecyclerView.getLayoutManager();
        if (r0.mTmpRect == null) {
            r0.mTmpRect = new Rect();
        }
        int scrollX = 0;
        int scrollY = 0;
        lm.calculateItemDecorationsForChild(r0.mSelected.itemView, r0.mTmpRect);
        if (lm.canScrollHorizontally()) {
            curX = (int) (r0.mSelectedStartX + r0.mDx);
            int leftDiff = (curX - r0.mTmpRect.left) - r0.mRecyclerView.getPaddingLeft();
            if (r0.mDx < 0.0f && leftDiff < 0) {
                scrollX = leftDiff;
            } else if (r0.mDx > 0.0f) {
                int rightDiff = ((r0.mSelected.itemView.getWidth() + curX) + r0.mTmpRect.right) - (r0.mRecyclerView.getWidth() - r0.mRecyclerView.getPaddingRight());
                if (rightDiff > 0) {
                    scrollX = rightDiff;
                }
            }
        }
        if (lm.canScrollVertically()) {
            int curY = (int) (r0.mSelectedStartY + r0.mDy);
            curX = (curY - r0.mTmpRect.top) - r0.mRecyclerView.getPaddingTop();
            if (r0.mDy < 0.0f && curX < 0) {
                scrollY = curX;
            } else if (r0.mDy > 0.0f) {
                int bottomDiff = ((r0.mSelected.itemView.getHeight() + curY) + r0.mTmpRect.bottom) - (r0.mRecyclerView.getHeight() - r0.mRecyclerView.getPaddingBottom());
                if (bottomDiff > 0) {
                    scrollY = bottomDiff;
                }
            }
        }
        if (scrollX != 0) {
            scrollX = r0.mCallback.interpolateOutOfBoundsScroll(r0.mRecyclerView, r0.mSelected.itemView.getWidth(), scrollX, r0.mRecyclerView.getWidth(), scrollDuration);
        }
        if (scrollY != 0) {
            scrollY = r0.mCallback.interpolateOutOfBoundsScroll(r0.mRecyclerView, r0.mSelected.itemView.getHeight(), scrollY, r0.mRecyclerView.getHeight(), scrollDuration);
        }
        if (scrollX == 0) {
            if (scrollY == 0) {
                r0.mDragScrollStartTimeInMs = Long.MIN_VALUE;
                return false;
            }
        }
        if (r0.mDragScrollStartTimeInMs == Long.MIN_VALUE) {
            r0.mDragScrollStartTimeInMs = now;
        }
        r0.mRecyclerView.scrollBy(scrollX, scrollY);
        return true;
    }

    private List<ViewHolder> findSwapTargets(ViewHolder viewHolder) {
        int left;
        ViewHolder viewHolder2 = viewHolder;
        List list = this.mSwapTargets;
        if (list == null) {
            r0.mSwapTargets = new ArrayList();
            r0.mDistances = new ArrayList();
        } else {
            list.clear();
            r0.mDistances.clear();
        }
        int margin = r0.mCallback.getBoundingBoxMargin();
        int left2 = Math.round(r0.mSelectedStartX + r0.mDx) - margin;
        int top = Math.round(r0.mSelectedStartY + r0.mDy) - margin;
        int right = (viewHolder2.itemView.getWidth() + left2) + (margin * 2);
        int bottom = (viewHolder2.itemView.getHeight() + top) + (margin * 2);
        int centerX = (left2 + right) / 2;
        int centerY = (top + bottom) / 2;
        LayoutManager lm = r0.mRecyclerView.getLayoutManager();
        int childCount = lm.getChildCount();
        int i = 0;
        while (i < childCount) {
            int margin2;
            View other = lm.getChildAt(i);
            if (other == viewHolder2.itemView) {
                margin2 = margin;
                left = left2;
            } else if (other.getBottom() < top || other.getTop() > bottom) {
                margin2 = margin;
                left = left2;
            } else if (other.getRight() < left2) {
                margin2 = margin;
                left = left2;
            } else if (other.getLeft() > right) {
                margin2 = margin;
                left = left2;
            } else {
                ViewHolder otherVh = r0.mRecyclerView.getChildViewHolder(other);
                if (r0.mCallback.canDropOver(r0.mRecyclerView, r0.mSelected, otherVh)) {
                    int dx = Math.abs(centerX - ((other.getLeft() + other.getRight()) / 2));
                    int dy = Math.abs(centerY - ((other.getTop() + other.getBottom()) / 2));
                    int dist = (dx * dx) + (dy * dy);
                    dx = r0.mSwapTargets.size();
                    margin2 = margin;
                    margin = 0;
                    left = left2;
                    left2 = 0;
                    while (margin < dx) {
                        int cnt = dx;
                        if (dist <= ((Integer) r0.mDistances.get(margin)).intValue()) {
                            break;
                        }
                        left2++;
                        margin++;
                        dx = cnt;
                    }
                    r0.mSwapTargets.add(left2, otherVh);
                    r0.mDistances.add(left2, Integer.valueOf(dist));
                } else {
                    margin2 = margin;
                    left = left2;
                }
            }
            i++;
            left2 = left;
            margin = margin2;
            viewHolder2 = viewHolder;
        }
        left = left2;
        return r0.mSwapTargets;
    }

    void moveIfNecessary(ViewHolder viewHolder) {
        ViewHolder viewHolder2 = viewHolder;
        if (!this.mRecyclerView.isLayoutRequested() && r0.mActionState == 2) {
            float threshold = r0.mCallback.getMoveThreshold(viewHolder2);
            int x = (int) (r0.mSelectedStartX + r0.mDx);
            int y = (int) (r0.mSelectedStartY + r0.mDy);
            if (((float) Math.abs(y - viewHolder2.itemView.getTop())) < ((float) viewHolder2.itemView.getHeight()) * threshold) {
                if (((float) Math.abs(x - viewHolder2.itemView.getLeft())) < ((float) viewHolder2.itemView.getWidth()) * threshold) {
                    return;
                }
            }
            List<ViewHolder> swapTargets = findSwapTargets(viewHolder);
            if (swapTargets.size() != 0) {
                ViewHolder target = r0.mCallback.chooseDropTarget(viewHolder2, swapTargets, x, y);
                if (target == null) {
                    r0.mSwapTargets.clear();
                    r0.mDistances.clear();
                    return;
                }
                int toPosition = target.getAdapterPosition();
                int fromPosition = viewHolder.getAdapterPosition();
                if (r0.mCallback.onMove(r0.mRecyclerView, viewHolder2, target)) {
                    r0.mCallback.onMoved(r0.mRecyclerView, viewHolder, fromPosition, target, toPosition, x, y);
                }
            }
        }
    }

    public void onChildViewAttachedToWindow(View view) {
    }

    public void onChildViewDetachedFromWindow(View view) {
        removeChildDrawingOrderCallbackIfNecessary(view);
        ViewHolder holder = this.mRecyclerView.getChildViewHolder(view);
        if (holder != null) {
            ViewHolder viewHolder = this.mSelected;
            if (viewHolder == null || holder != viewHolder) {
                endRecoverAnimation(holder, false);
                if (this.mPendingCleanup.remove(holder.itemView)) {
                    this.mCallback.clearView(this.mRecyclerView, holder);
                }
            } else {
                select(null, 0);
            }
        }
    }

    int endRecoverAnimation(ViewHolder viewHolder, boolean override) {
        for (int i = this.mRecoverAnimations.size() - 1; i >= 0; i--) {
            RecoverAnimation anim = (RecoverAnimation) this.mRecoverAnimations.get(i);
            if (anim.mViewHolder == viewHolder) {
                anim.mOverridden |= override;
                if (!anim.mEnded) {
                    anim.cancel();
                }
                this.mRecoverAnimations.remove(i);
                return anim.mAnimationType;
            }
        }
        return 0;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        outRect.setEmpty();
    }

    void obtainVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
        }
        this.mVelocityTracker = VelocityTracker.obtain();
    }

    private void releaseVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private ViewHolder findSwipedView(MotionEvent motionEvent) {
        LayoutManager lm = this.mRecyclerView.getLayoutManager();
        int pointerIndex = this.mActivePointerId;
        if (pointerIndex == -1) {
            return null;
        }
        pointerIndex = motionEvent.findPointerIndex(pointerIndex);
        float dy = motionEvent.getY(pointerIndex) - this.mInitialTouchY;
        float absDx = Math.abs(motionEvent.getX(pointerIndex) - this.mInitialTouchX);
        float absDy = Math.abs(dy);
        int i = this.mSlop;
        if (absDx < ((float) i) && absDy < ((float) i)) {
            return null;
        }
        if (absDx > absDy && lm.canScrollHorizontally()) {
            return null;
        }
        if (absDy > absDx && lm.canScrollVertically()) {
            return null;
        }
        View child = findChildView(motionEvent);
        if (child == null) {
            return null;
        }
        return this.mRecyclerView.getChildViewHolder(child);
    }

    boolean checkSelectForSwipe(int action, MotionEvent motionEvent, int pointerIndex) {
        MotionEvent motionEvent2 = motionEvent;
        if (this.mSelected != null) {
            int i = action;
        } else if (action == 2 && r0.mActionState != 2) {
            if (r0.mCallback.isItemViewSwipeEnabled()) {
                if (r0.mRecyclerView.getScrollState() == 1) {
                    return false;
                }
                ViewHolder vh = findSwipedView(motionEvent2);
                if (vh == null) {
                    return false;
                }
                int swipeFlags = (65280 & r0.mCallback.getAbsoluteMovementFlags(r0.mRecyclerView, vh)) >> 8;
                if (swipeFlags == 0) {
                    return false;
                }
                float x = motionEvent.getX(pointerIndex);
                float dx = x - r0.mInitialTouchX;
                float dy = motionEvent.getY(pointerIndex) - r0.mInitialTouchY;
                float absDx = Math.abs(dx);
                float absDy = Math.abs(dy);
                int i2 = r0.mSlop;
                if (absDx < ((float) i2) && absDy < ((float) i2)) {
                    return false;
                }
                if (absDx > absDy) {
                    if (dx < 0.0f && (swipeFlags & 4) == 0) {
                        return false;
                    }
                    if (dx > 0.0f && (swipeFlags & 8) == 0) {
                        return false;
                    }
                } else if (dy < 0.0f && (swipeFlags & 1) == 0) {
                    return false;
                } else {
                    if (dy > 0.0f && (swipeFlags & 2) == 0) {
                        return false;
                    }
                }
                r0.mDy = 0.0f;
                r0.mDx = 0.0f;
                r0.mActivePointerId = motionEvent2.getPointerId(0);
                select(vh, 1);
                return true;
            }
        }
        return false;
    }

    View findChildView(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        View selectedView = this.mSelected;
        if (selectedView != null) {
            selectedView = selectedView.itemView;
            if (hitTest(selectedView, x, y, this.mSelectedStartX + this.mDx, this.mSelectedStartY + this.mDy)) {
                return selectedView;
            }
        }
        for (int i = this.mRecoverAnimations.size() - 1; i >= 0; i--) {
            RecoverAnimation anim = (RecoverAnimation) this.mRecoverAnimations.get(i);
            View view = anim.mViewHolder.itemView;
            if (hitTest(view, x, y, anim.mX, anim.mY)) {
                return view;
            }
        }
        return this.mRecyclerView.findChildViewUnder(x, y);
    }

    public void startDrag(ViewHolder viewHolder) {
        if (!this.mCallback.hasDragFlag(this.mRecyclerView, viewHolder)) {
            Log.e(TAG, "Start drag has been called but dragging is not enabled");
        } else if (viewHolder.itemView.getParent() != this.mRecyclerView) {
            Log.e(TAG, "Start drag has been called with a view holder which is not a child of the RecyclerView which is controlled by this ItemTouchHelper.");
        } else {
            obtainVelocityTracker();
            this.mDy = 0.0f;
            this.mDx = 0.0f;
            select(viewHolder, 2);
        }
    }

    public void startSwipe(ViewHolder viewHolder) {
        if (!this.mCallback.hasSwipeFlag(this.mRecyclerView, viewHolder)) {
            Log.e(TAG, "Start swipe has been called but swiping is not enabled");
        } else if (viewHolder.itemView.getParent() != this.mRecyclerView) {
            Log.e(TAG, "Start swipe has been called with a view holder which is not a child of the RecyclerView controlled by this ItemTouchHelper.");
        } else {
            obtainVelocityTracker();
            this.mDy = 0.0f;
            this.mDx = 0.0f;
            select(viewHolder, 1);
        }
    }

    RecoverAnimation findAnimation(MotionEvent event) {
        if (this.mRecoverAnimations.isEmpty()) {
            return null;
        }
        View target = findChildView(event);
        for (int i = this.mRecoverAnimations.size() - 1; i >= 0; i--) {
            RecoverAnimation anim = (RecoverAnimation) this.mRecoverAnimations.get(i);
            if (anim.mViewHolder.itemView == target) {
                return anim;
            }
        }
        return null;
    }

    void updateDxDy(MotionEvent ev, int directionFlags, int pointerIndex) {
        float x = ev.getX(pointerIndex);
        float y = ev.getY(pointerIndex);
        this.mDx = x - this.mInitialTouchX;
        this.mDy = y - this.mInitialTouchY;
        if ((directionFlags & 4) == 0) {
            this.mDx = Math.max(0.0f, this.mDx);
        }
        if ((directionFlags & 8) == 0) {
            this.mDx = Math.min(0.0f, this.mDx);
        }
        if ((directionFlags & 1) == 0) {
            this.mDy = Math.max(0.0f, this.mDy);
        }
        if ((directionFlags & 2) == 0) {
            this.mDy = Math.min(0.0f, this.mDy);
        }
    }

    private int swipeIfNecessary(ViewHolder viewHolder) {
        if (this.mActionState == 2) {
            return 0;
        }
        int originalMovementFlags = this.mCallback.getMovementFlags(this.mRecyclerView, viewHolder);
        int flags = (this.mCallback.convertToAbsoluteDirection(originalMovementFlags, ViewCompat.getLayoutDirection(this.mRecyclerView)) & 65280) >> 8;
        if (flags == 0) {
            return 0;
        }
        int originalFlags = (65280 & originalMovementFlags) >> 8;
        int checkHorizontalSwipe;
        int swipeDir;
        if (Math.abs(this.mDx) > Math.abs(this.mDy)) {
            checkHorizontalSwipe = checkHorizontalSwipe(viewHolder, flags);
            swipeDir = checkHorizontalSwipe;
            if (checkHorizontalSwipe <= 0) {
                checkHorizontalSwipe = checkVerticalSwipe(viewHolder, flags);
                swipeDir = checkHorizontalSwipe;
                if (checkHorizontalSwipe > 0) {
                    return swipeDir;
                }
            } else if ((originalFlags & swipeDir) == 0) {
                return Callback.convertToRelativeDirection(swipeDir, ViewCompat.getLayoutDirection(this.mRecyclerView));
            } else {
                return swipeDir;
            }
        }
        checkHorizontalSwipe = checkVerticalSwipe(viewHolder, flags);
        swipeDir = checkHorizontalSwipe;
        if (checkHorizontalSwipe > 0) {
            return swipeDir;
        }
        checkHorizontalSwipe = checkHorizontalSwipe(viewHolder, flags);
        swipeDir = checkHorizontalSwipe;
        if (checkHorizontalSwipe > 0) {
            if ((originalFlags & swipeDir) == 0) {
                return Callback.convertToRelativeDirection(swipeDir, ViewCompat.getLayoutDirection(this.mRecyclerView));
            }
            return swipeDir;
        }
        return 0;
    }

    private int checkHorizontalSwipe(ViewHolder viewHolder, int flags) {
        if ((flags & 12) != 0) {
            int velDirFlag = 8;
            int dirFlag = this.mDx > 0.0f ? 8 : 4;
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker != null && this.mActivePointerId > -1) {
                velocityTracker.computeCurrentVelocity(1000, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
                float xVelocity = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
                float yVelocity = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
                if (xVelocity <= 0.0f) {
                    velDirFlag = 4;
                }
                float absXVelocity = Math.abs(xVelocity);
                if ((velDirFlag & flags) != 0 && dirFlag == velDirFlag) {
                    if (absXVelocity >= this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity)) {
                        if (absXVelocity > Math.abs(yVelocity)) {
                            return velDirFlag;
                        }
                    }
                }
            }
            float threshold = ((float) this.mRecyclerView.getWidth()) * this.mCallback.getSwipeThreshold(viewHolder);
            if ((flags & dirFlag) != 0 && Math.abs(this.mDx) > threshold) {
                return dirFlag;
            }
        }
        return 0;
    }

    private int checkVerticalSwipe(ViewHolder viewHolder, int flags) {
        if ((flags & 3) != 0) {
            int velDirFlag = 2;
            int dirFlag = this.mDy > 0.0f ? 2 : 1;
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker != null && this.mActivePointerId > -1) {
                velocityTracker.computeCurrentVelocity(1000, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
                float xVelocity = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
                float yVelocity = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
                if (yVelocity <= 0.0f) {
                    velDirFlag = 1;
                }
                float absYVelocity = Math.abs(yVelocity);
                if ((velDirFlag & flags) != 0 && velDirFlag == dirFlag) {
                    if (absYVelocity >= this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity)) {
                        if (absYVelocity > Math.abs(xVelocity)) {
                            return velDirFlag;
                        }
                    }
                }
            }
            float threshold = ((float) this.mRecyclerView.getHeight()) * this.mCallback.getSwipeThreshold(viewHolder);
            if ((flags & dirFlag) != 0 && Math.abs(this.mDy) > threshold) {
                return dirFlag;
            }
        }
        return 0;
    }

    private void addChildDrawingOrderCallback() {
        if (VERSION.SDK_INT < 21) {
            if (this.mChildDrawingOrderCallback == null) {
                this.mChildDrawingOrderCallback = new C09085();
            }
            this.mRecyclerView.setChildDrawingOrderCallback(this.mChildDrawingOrderCallback);
        }
    }

    void removeChildDrawingOrderCallbackIfNecessary(View view) {
        if (view == this.mOverdrawChild) {
            this.mOverdrawChild = null;
            if (this.mChildDrawingOrderCallback != null) {
                this.mRecyclerView.setChildDrawingOrderCallback(null);
            }
        }
    }
}
