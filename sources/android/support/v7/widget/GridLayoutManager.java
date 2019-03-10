package android.support.v7.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager.LayoutPrefetchRegistry;
import android.support.v7.widget.RecyclerView.Recycler;
import android.support.v7.widget.RecyclerView.State;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import java.util.Arrays;

public class GridLayoutManager extends LinearLayoutManager {
    private static final boolean DEBUG = false;
    public static final int DEFAULT_SPAN_COUNT = -1;
    private static final String TAG = "GridLayoutManager";
    int[] mCachedBorders;
    final Rect mDecorInsets = new Rect();
    boolean mPendingSpanCountChange = false;
    final SparseIntArray mPreLayoutSpanIndexCache = new SparseIntArray();
    final SparseIntArray mPreLayoutSpanSizeCache = new SparseIntArray();
    View[] mSet;
    int mSpanCount = -1;
    SpanSizeLookup mSpanSizeLookup = new DefaultSpanSizeLookup();

    public static abstract class SpanSizeLookup {
        private boolean mCacheSpanIndices = false;
        final SparseIntArray mSpanIndexCache = new SparseIntArray();

        public abstract int getSpanSize(int i);

        public void setSpanIndexCacheEnabled(boolean cacheSpanIndices) {
            this.mCacheSpanIndices = cacheSpanIndices;
        }

        public void invalidateSpanIndexCache() {
            this.mSpanIndexCache.clear();
        }

        public boolean isSpanIndexCacheEnabled() {
            return this.mCacheSpanIndices;
        }

        int getCachedSpanIndex(int position, int spanCount) {
            if (!this.mCacheSpanIndices) {
                return getSpanIndex(position, spanCount);
            }
            int existing = this.mSpanIndexCache.get(position, -1);
            if (existing != -1) {
                return existing;
            }
            int value = getSpanIndex(position, spanCount);
            this.mSpanIndexCache.put(position, value);
            return value;
        }

        public int getSpanIndex(int position, int spanCount) {
            int positionSpanSize = getSpanSize(position);
            if (positionSpanSize == spanCount) {
                return 0;
            }
            int prevKey;
            int span = 0;
            int startPos = 0;
            if (this.mCacheSpanIndices && this.mSpanIndexCache.size() > 0) {
                prevKey = findReferenceIndexFromCache(position);
                if (prevKey >= 0) {
                    span = this.mSpanIndexCache.get(prevKey) + getSpanSize(prevKey);
                    startPos = prevKey + 1;
                }
            }
            for (prevKey = startPos; prevKey < position; prevKey++) {
                int size = getSpanSize(prevKey);
                span += size;
                if (span == spanCount) {
                    span = 0;
                } else if (span > spanCount) {
                    span = size;
                }
            }
            if (span + positionSpanSize <= spanCount) {
                return span;
            }
            return 0;
        }

        int findReferenceIndexFromCache(int position) {
            int mid;
            int lo = 0;
            int hi = this.mSpanIndexCache.size() - 1;
            while (lo <= hi) {
                mid = (lo + hi) >>> 1;
                if (this.mSpanIndexCache.keyAt(mid) < position) {
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
            mid = lo - 1;
            if (mid < 0 || mid >= this.mSpanIndexCache.size()) {
                return -1;
            }
            return this.mSpanIndexCache.keyAt(mid);
        }

        public int getSpanGroupIndex(int adapterPosition, int spanCount) {
            int span = 0;
            int group = 0;
            int positionSpanSize = getSpanSize(adapterPosition);
            for (int i = 0; i < adapterPosition; i++) {
                int size = getSpanSize(i);
                span += size;
                if (span == spanCount) {
                    span = 0;
                    group++;
                } else if (span > spanCount) {
                    span = size;
                    group++;
                }
            }
            if (span + positionSpanSize > spanCount) {
                return group + 1;
            }
            return group;
        }
    }

    public static final class DefaultSpanSizeLookup extends SpanSizeLookup {
        public int getSpanSize(int position) {
            return 1;
        }

        public int getSpanIndex(int position, int spanCount) {
            return position % spanCount;
        }
    }

    public static class LayoutParams extends android.support.v7.widget.RecyclerView.LayoutParams {
        public static final int INVALID_SPAN_ID = -1;
        int mSpanIndex = -1;
        int mSpanSize = 0;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(android.support.v7.widget.RecyclerView.LayoutParams source) {
            super(source);
        }

        public int getSpanIndex() {
            return this.mSpanIndex;
        }

        public int getSpanSize() {
            return this.mSpanSize;
        }
    }

    public GridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setSpanCount(LayoutManager.getProperties(context, attrs, defStyleAttr, defStyleRes).spanCount);
    }

    public GridLayoutManager(Context context, int spanCount) {
        super(context);
        setSpanCount(spanCount);
    }

    public GridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        setSpanCount(spanCount);
    }

    public void setStackFromEnd(boolean stackFromEnd) {
        if (stackFromEnd) {
            throw new UnsupportedOperationException("GridLayoutManager does not support stack from end. Consider using reverse layout");
        }
        super.setStackFromEnd(false);
    }

    public int getRowCountForAccessibility(Recycler recycler, State state) {
        if (this.mOrientation == 0) {
            return this.mSpanCount;
        }
        if (state.getItemCount() < 1) {
            return 0;
        }
        return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
    }

    public int getColumnCountForAccessibility(Recycler recycler, State state) {
        if (this.mOrientation == 1) {
            return this.mSpanCount;
        }
        if (state.getItemCount() < 1) {
            return 0;
        }
        return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
    }

    public void onInitializeAccessibilityNodeInfoForItem(Recycler recycler, State state, View host, AccessibilityNodeInfoCompat info) {
        android.view.ViewGroup.LayoutParams lp = host.getLayoutParams();
        if (lp instanceof LayoutParams) {
            LayoutParams glp = (LayoutParams) lp;
            int spanGroupIndex = getSpanGroupIndex(recycler, state, glp.getViewLayoutPosition());
            if (this.mOrientation == 0) {
                boolean z;
                int spanIndex = glp.getSpanIndex();
                int spanSize = glp.getSpanSize();
                if (this.mSpanCount > 1) {
                    if (glp.getSpanSize() == this.mSpanCount) {
                        z = true;
                        info.setCollectionItemInfo(CollectionItemInfoCompat.obtain(spanIndex, spanSize, spanGroupIndex, 1, z, false));
                    }
                }
                z = false;
                info.setCollectionItemInfo(CollectionItemInfoCompat.obtain(spanIndex, spanSize, spanGroupIndex, 1, z, false));
            } else {
                boolean z2;
                int spanIndex2 = glp.getSpanIndex();
                int spanSize2 = glp.getSpanSize();
                if (this.mSpanCount > 1) {
                    if (glp.getSpanSize() == this.mSpanCount) {
                        z2 = true;
                        info.setCollectionItemInfo(CollectionItemInfoCompat.obtain(spanGroupIndex, 1, spanIndex2, spanSize2, z2, false));
                    }
                }
                z2 = false;
                info.setCollectionItemInfo(CollectionItemInfoCompat.obtain(spanGroupIndex, 1, spanIndex2, spanSize2, z2, false));
            }
            return;
        }
        super.onInitializeAccessibilityNodeInfoForItem(host, info);
    }

    public void onLayoutChildren(Recycler recycler, State state) {
        if (state.isPreLayout()) {
            cachePreLayoutSpanMapping();
        }
        super.onLayoutChildren(recycler, state);
        clearPreLayoutSpanMappingCache();
    }

    public void onLayoutCompleted(State state) {
        super.onLayoutCompleted(state);
        this.mPendingSpanCountChange = false;
    }

    private void clearPreLayoutSpanMappingCache() {
        this.mPreLayoutSpanSizeCache.clear();
        this.mPreLayoutSpanIndexCache.clear();
    }

    private void cachePreLayoutSpanMapping() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
            int viewPosition = lp.getViewLayoutPosition();
            this.mPreLayoutSpanSizeCache.put(viewPosition, lp.getSpanSize());
            this.mPreLayoutSpanIndexCache.put(viewPosition, lp.getSpanIndex());
        }
    }

    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsChanged(RecyclerView recyclerView) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount, Object payload) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public android.support.v7.widget.RecyclerView.LayoutParams generateDefaultLayoutParams() {
        if (this.mOrientation == 0) {
            return new LayoutParams(-2, -1);
        }
        return new LayoutParams(-1, -2);
    }

    public android.support.v7.widget.RecyclerView.LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
        return new LayoutParams(c, attrs);
    }

    public android.support.v7.widget.RecyclerView.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams lp) {
        if (lp instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) lp);
        }
        return new LayoutParams(lp);
    }

    public boolean checkLayoutParams(android.support.v7.widget.RecyclerView.LayoutParams lp) {
        return lp instanceof LayoutParams;
    }

    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
        this.mSpanSizeLookup = spanSizeLookup;
    }

    public SpanSizeLookup getSpanSizeLookup() {
        return this.mSpanSizeLookup;
    }

    private void updateMeasurements() {
        int totalSpace;
        if (getOrientation() == 1) {
            totalSpace = (getWidth() - getPaddingRight()) - getPaddingLeft();
        } else {
            totalSpace = (getHeight() - getPaddingBottom()) - getPaddingTop();
        }
        calculateItemBorders(totalSpace);
    }

    public void setMeasuredDimension(Rect childrenBounds, int wSpec, int hSpec) {
        int height;
        int width;
        if (this.mCachedBorders == null) {
            super.setMeasuredDimension(childrenBounds, wSpec, hSpec);
        }
        int horizontalPadding = getPaddingLeft() + getPaddingRight();
        int verticalPadding = getPaddingTop() + getPaddingBottom();
        int[] iArr;
        if (this.mOrientation == 1) {
            height = LayoutManager.chooseSize(hSpec, childrenBounds.height() + verticalPadding, getMinimumHeight());
            iArr = this.mCachedBorders;
            width = LayoutManager.chooseSize(wSpec, iArr[iArr.length - 1] + horizontalPadding, getMinimumWidth());
        } else {
            height = LayoutManager.chooseSize(wSpec, childrenBounds.width() + horizontalPadding, getMinimumWidth());
            iArr = this.mCachedBorders;
            int i = height;
            height = LayoutManager.chooseSize(hSpec, iArr[iArr.length - 1] + verticalPadding, getMinimumHeight());
            width = i;
        }
        setMeasuredDimension(width, height);
    }

    private void calculateItemBorders(int totalSpace) {
        this.mCachedBorders = calculateItemBorders(this.mCachedBorders, this.mSpanCount, totalSpace);
    }

    static int[] calculateItemBorders(int[] cachedBorders, int spanCount, int totalSpace) {
        int sizePerSpan;
        int sizePerSpanRemainder;
        int consumedPixels;
        int additionalSize;
        int i;
        int itemSize;
        if (cachedBorders != null && cachedBorders.length == spanCount + 1) {
            if (cachedBorders[cachedBorders.length - 1] == totalSpace) {
                cachedBorders[0] = 0;
                sizePerSpan = totalSpace / spanCount;
                sizePerSpanRemainder = totalSpace % spanCount;
                consumedPixels = 0;
                additionalSize = 0;
                for (i = 1; i <= spanCount; i++) {
                    itemSize = sizePerSpan;
                    additionalSize += sizePerSpanRemainder;
                    if (additionalSize <= 0 && spanCount - additionalSize < sizePerSpanRemainder) {
                        itemSize++;
                        additionalSize -= spanCount;
                    }
                    consumedPixels += itemSize;
                    cachedBorders[i] = consumedPixels;
                }
                return cachedBorders;
            }
        }
        cachedBorders = new int[(spanCount + 1)];
        cachedBorders[0] = 0;
        sizePerSpan = totalSpace / spanCount;
        sizePerSpanRemainder = totalSpace % spanCount;
        consumedPixels = 0;
        additionalSize = 0;
        for (i = 1; i <= spanCount; i++) {
            itemSize = sizePerSpan;
            additionalSize += sizePerSpanRemainder;
            if (additionalSize <= 0) {
            }
            consumedPixels += itemSize;
            cachedBorders[i] = consumedPixels;
        }
        return cachedBorders;
    }

    int getSpaceForSpanRange(int startSpan, int spanSize) {
        if (this.mOrientation == 1 && isLayoutRTL()) {
            int[] iArr = this.mCachedBorders;
            int i = this.mSpanCount;
            return iArr[i - startSpan] - iArr[(i - startSpan) - spanSize];
        }
        iArr = this.mCachedBorders;
        return iArr[startSpan + spanSize] - iArr[startSpan];
    }

    void onAnchorReady(Recycler recycler, State state, AnchorInfo anchorInfo, int itemDirection) {
        super.onAnchorReady(recycler, state, anchorInfo, itemDirection);
        updateMeasurements();
        if (state.getItemCount() > 0 && !state.isPreLayout()) {
            ensureAnchorIsInCorrectSpan(recycler, state, anchorInfo, itemDirection);
        }
        ensureViewSet();
    }

    private void ensureViewSet() {
        View[] viewArr = this.mSet;
        if (viewArr != null) {
            if (viewArr.length == this.mSpanCount) {
                return;
            }
        }
        this.mSet = new View[this.mSpanCount];
    }

    public int scrollHorizontallyBy(int dx, Recycler recycler, State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    private void ensureAnchorIsInCorrectSpan(Recycler recycler, State state, AnchorInfo anchorInfo, int itemDirection) {
        boolean layingOutInPrimaryDirection = itemDirection == 1;
        int span = getSpanIndex(recycler, state, anchorInfo.mPosition);
        if (layingOutInPrimaryDirection) {
            while (span > 0 && anchorInfo.mPosition > 0) {
                anchorInfo.mPosition--;
                span = getSpanIndex(recycler, state, anchorInfo.mPosition);
            }
            return;
        }
        int indexLimit = state.getItemCount() - 1;
        int pos = anchorInfo.mPosition;
        int bestSpan = span;
        while (pos < indexLimit) {
            int next = getSpanIndex(recycler, state, pos + 1);
            if (next <= bestSpan) {
                break;
            }
            pos++;
            bestSpan = next;
        }
        anchorInfo.mPosition = pos;
    }

    View findReferenceChild(Recycler recycler, State state, int start, int end, int itemCount) {
        ensureLayoutState();
        View invalidMatch = null;
        View outOfBoundsMatch = null;
        int boundsStart = this.mOrientationHelper.getStartAfterPadding();
        int boundsEnd = this.mOrientationHelper.getEndAfterPadding();
        int diff = end > start ? 1 : -1;
        for (int i = start; i != end; i += diff) {
            View view = getChildAt(i);
            int position = getPosition(view);
            if (position >= 0 && position < itemCount) {
                if (getSpanIndex(recycler, state, position) == 0) {
                    if (!((android.support.v7.widget.RecyclerView.LayoutParams) view.getLayoutParams()).isItemRemoved()) {
                        if (this.mOrientationHelper.getDecoratedStart(view) < boundsEnd) {
                            if (this.mOrientationHelper.getDecoratedEnd(view) >= boundsStart) {
                                return view;
                            }
                        }
                        if (outOfBoundsMatch == null) {
                            outOfBoundsMatch = view;
                        }
                    } else if (invalidMatch == null) {
                        invalidMatch = view;
                    }
                }
            }
        }
        return outOfBoundsMatch != null ? outOfBoundsMatch : invalidMatch;
    }

    private int getSpanGroupIndex(Recycler recycler, State state, int viewPosition) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getSpanGroupIndex(viewPosition, this.mSpanCount);
        }
        int adapterPosition = recycler.convertPreLayoutPositionToPostLayout(viewPosition);
        if (adapterPosition != -1) {
            return this.mSpanSizeLookup.getSpanGroupIndex(adapterPosition, this.mSpanCount);
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cannot find span size for pre layout position. ");
        stringBuilder.append(viewPosition);
        Log.w(str, stringBuilder.toString());
        return 0;
    }

    private int getSpanIndex(Recycler recycler, State state, int pos) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getCachedSpanIndex(pos, this.mSpanCount);
        }
        int cached = this.mPreLayoutSpanIndexCache.get(pos, -1);
        if (cached != -1) {
            return cached;
        }
        int adapterPosition = recycler.convertPreLayoutPositionToPostLayout(pos);
        if (adapterPosition != -1) {
            return this.mSpanSizeLookup.getCachedSpanIndex(adapterPosition, this.mSpanCount);
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:");
        stringBuilder.append(pos);
        Log.w(str, stringBuilder.toString());
        return 0;
    }

    private int getSpanSize(Recycler recycler, State state, int pos) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getSpanSize(pos);
        }
        int cached = this.mPreLayoutSpanSizeCache.get(pos, -1);
        if (cached != -1) {
            return cached;
        }
        int adapterPosition = recycler.convertPreLayoutPositionToPostLayout(pos);
        if (adapterPosition != -1) {
            return this.mSpanSizeLookup.getSpanSize(adapterPosition);
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:");
        stringBuilder.append(pos);
        Log.w(str, stringBuilder.toString());
        return 1;
    }

    void collectPrefetchPositionsForLayoutState(State state, LayoutState layoutState, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int remainingSpan = this.mSpanCount;
        for (int count = 0; count < this.mSpanCount && layoutState.hasMore(state) && remainingSpan > 0; count++) {
            int pos = layoutState.mCurrentPosition;
            layoutPrefetchRegistry.addPosition(pos, Math.max(0, layoutState.mScrollingOffset));
            remainingSpan -= this.mSpanSizeLookup.getSpanSize(pos);
            layoutState.mCurrentPosition += layoutState.mItemDirection;
        }
    }

    void layoutChunk(Recycler recycler, State state, LayoutState layoutState, LayoutChunkResult result) {
        int count;
        int consumedSpanCount;
        Recycler recycler2 = recycler;
        State state2 = state;
        LayoutState layoutState2 = layoutState;
        LayoutChunkResult layoutChunkResult = result;
        int otherDirSpecMode = this.mOrientationHelper.getModeInOther();
        boolean z = false;
        boolean flexibleInOtherDir = otherDirSpecMode != 1073741824;
        int currentOtherDirSize = getChildCount() > 0 ? r6.mCachedBorders[r6.mSpanCount] : 0;
        if (flexibleInOtherDir) {
            updateMeasurements();
        }
        boolean layingOutInPrimaryDirection = layoutState2.mItemDirection == 1;
        int remainingSpan = r6.mSpanCount;
        if (layingOutInPrimaryDirection) {
            count = 0;
            consumedSpanCount = 0;
        } else {
            remainingSpan = getSpanIndex(recycler2, state2, layoutState2.mCurrentPosition) + getSpanSize(recycler2, state2, layoutState2.mCurrentPosition);
            count = 0;
            consumedSpanCount = 0;
        }
        while (count < r6.mSpanCount && layoutState2.hasMore(state2) && remainingSpan > 0) {
            int pos = layoutState2.mCurrentPosition;
            int spanSize = getSpanSize(recycler2, state2, pos);
            if (spanSize <= r6.mSpanCount) {
                remainingSpan -= spanSize;
                if (remainingSpan < 0) {
                    break;
                }
                View view = layoutState2.next(recycler2);
                if (view == null) {
                    break;
                }
                consumedSpanCount += spanSize;
                r6.mSet[count] = view;
                count++;
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Item at position ");
                stringBuilder.append(pos);
                stringBuilder.append(" requires ");
                stringBuilder.append(spanSize);
                stringBuilder.append(" spans but GridLayoutManager has only ");
                stringBuilder.append(r6.mSpanCount);
                stringBuilder.append(" spans.");
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
        int remainingSpan2 = remainingSpan;
        if (count == 0) {
            layoutChunkResult.mFinished = true;
            return;
        }
        int size;
        int horizontalInsets;
        int otherDirSpecMode2;
        int i;
        int maxSize;
        int count2 = count;
        int currentOtherDirSize2 = currentOtherDirSize;
        assignSpans(recycler, state, count, consumedSpanCount, layingOutInPrimaryDirection);
        pos = 0;
        spanSize = 0;
        float maxSizeInOther = 0.0f;
        while (pos < count2) {
            View view2 = r6.mSet[pos];
            if (layoutState2.mScrapList == null) {
                if (layingOutInPrimaryDirection) {
                    addView(view2);
                } else {
                    addView(view2, z);
                }
            } else if (layingOutInPrimaryDirection) {
                addDisappearingView(view2);
            } else {
                addDisappearingView(view2, z);
            }
            calculateItemDecorationsForChild(view2, r6.mDecorInsets);
            measureChild(view2, otherDirSpecMode, z);
            size = r6.mOrientationHelper.getDecoratedMeasurement(view2);
            if (size > spanSize) {
                spanSize = size;
            }
            int maxSize2 = spanSize;
            float otherSize = (((float) r6.mOrientationHelper.getDecoratedMeasurementInOther(view2)) * 1.0f) / ((float) ((LayoutParams) view2.getLayoutParams()).mSpanSize);
            if (otherSize > maxSizeInOther) {
                maxSizeInOther = otherSize;
            }
            pos++;
            spanSize = maxSize2;
            z = false;
        }
        if (flexibleInOtherDir) {
            guessMeasurement(maxSizeInOther, currentOtherDirSize2);
            pos = 0;
            for (spanSize = 0; spanSize < count2; spanSize++) {
                view2 = r6.mSet[spanSize];
                measureChild(view2, 1073741824, true);
                size = r6.mOrientationHelper.getDecoratedMeasurement(view2);
                if (size > pos) {
                    pos = size;
                }
            }
            count = pos;
        } else {
            count = spanSize;
        }
        pos = 0;
        while (pos < count2) {
            float maxSizeInOther2;
            View view3 = r6.mSet[pos];
            if (r6.mOrientationHelper.getDecoratedMeasurement(view3) != count) {
                int wSpec;
                LayoutParams lp = (LayoutParams) view3.getLayoutParams();
                Rect decorInsets = lp.mDecorInsets;
                maxSizeInOther2 = maxSizeInOther;
                maxSizeInOther = ((decorInsets.top + decorInsets.bottom) + lp.topMargin) + lp.bottomMargin;
                horizontalInsets = ((decorInsets.left + decorInsets.right) + lp.leftMargin) + lp.rightMargin;
                size = getSpaceForSpanRange(lp.mSpanIndex, lp.mSpanSize);
                otherDirSpecMode2 = otherDirSpecMode;
                if (r6.mOrientation == 1) {
                    i = remainingSpan2;
                    wSpec = LayoutManager.getChildMeasureSpec(size, 1073741824, horizontalInsets, lp.width, false);
                    otherDirSpecMode = MeasureSpec.makeMeasureSpec(count - maxSizeInOther, 1073741824);
                    LayoutParams layoutParams = lp;
                } else {
                    i = remainingSpan2;
                    wSpec = MeasureSpec.makeMeasureSpec(count - horizontalInsets, 1073741824);
                    otherDirSpecMode = LayoutManager.getChildMeasureSpec(size, 1073741824, maxSizeInOther, lp.height, null);
                }
                measureChildWithDecorationsAndMargin(view3, wSpec, otherDirSpecMode, true);
            } else {
                maxSizeInOther2 = maxSizeInOther;
                otherDirSpecMode2 = otherDirSpecMode;
                i = remainingSpan2;
            }
            pos++;
            remainingSpan2 = i;
            maxSizeInOther = maxSizeInOther2;
            otherDirSpecMode = otherDirSpecMode2;
            recycler2 = recycler;
            state2 = state;
        }
        otherDirSpecMode2 = otherDirSpecMode;
        i = remainingSpan2;
        layoutChunkResult.mConsumed = count;
        pos = 0;
        spanSize = 0;
        remainingSpan = 0;
        size = 0;
        if (r6.mOrientation == 1) {
            if (layoutState2.mLayoutDirection == -1) {
                size = layoutState2.mOffset;
                remainingSpan = size - count;
            } else {
                remainingSpan = layoutState2.mOffset;
                size = remainingSpan + count;
            }
        } else if (layoutState2.mLayoutDirection == -1) {
            spanSize = layoutState2.mOffset;
            pos = spanSize - count;
        } else {
            pos = layoutState2.mOffset;
            spanSize = pos + count;
        }
        horizontalInsets = 0;
        while (horizontalInsets < count2) {
            int left;
            int bottom;
            View view4 = r6.mSet[horizontalInsets];
            LayoutParams params = (LayoutParams) view4.getLayoutParams();
            if (r6.mOrientation != 1) {
                left = pos;
                currentOtherDirSize2 = spanSize;
                pos = getPaddingTop() + r6.mCachedBorders[params.mSpanIndex];
                remainingSpan2 = pos;
                bottom = r6.mOrientationHelper.getDecoratedMeasurementInOther(view4) + pos;
            } else if (isLayoutRTL()) {
                currentOtherDirSize = getPaddingLeft() + r6.mCachedBorders[r6.mSpanCount - params.mSpanIndex];
                left = currentOtherDirSize - r6.mOrientationHelper.getDecoratedMeasurementInOther(view4);
                remainingSpan2 = remainingSpan;
                bottom = size;
                currentOtherDirSize2 = currentOtherDirSize;
            } else {
                currentOtherDirSize2 = spanSize;
                pos = getPaddingLeft() + r6.mCachedBorders[params.mSpanIndex];
                left = pos;
                currentOtherDirSize2 = r6.mOrientationHelper.getDecoratedMeasurementInOther(view4) + pos;
                remainingSpan2 = remainingSpan;
                bottom = size;
            }
            maxSize = count;
            layoutDecoratedWithMargins(view4, left, remainingSpan2, currentOtherDirSize2, bottom);
            if (params.isItemRemoved() == 0) {
                if (params.isItemChanged() == 0) {
                    layoutChunkResult.mFocusable |= view4.hasFocusable();
                    horizontalInsets++;
                    remainingSpan = remainingSpan2;
                    pos = left;
                    spanSize = currentOtherDirSize2;
                    size = bottom;
                    count = maxSize;
                }
            }
            layoutChunkResult.mIgnoreConsumed = true;
            layoutChunkResult.mFocusable |= view4.hasFocusable();
            horizontalInsets++;
            remainingSpan = remainingSpan2;
            pos = left;
            spanSize = currentOtherDirSize2;
            size = bottom;
            count = maxSize;
        }
        currentOtherDirSize2 = spanSize;
        maxSize = count;
        Arrays.fill(r6.mSet, null);
    }

    private void measureChild(View view, int otherDirParentSpecMode, boolean alreadyMeasured) {
        int wSpec;
        int hSpec;
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        Rect decorInsets = lp.mDecorInsets;
        int verticalInsets = ((decorInsets.top + decorInsets.bottom) + lp.topMargin) + lp.bottomMargin;
        int horizontalInsets = ((decorInsets.left + decorInsets.right) + lp.leftMargin) + lp.rightMargin;
        int availableSpaceInOther = getSpaceForSpanRange(lp.mSpanIndex, lp.mSpanSize);
        if (this.mOrientation == 1) {
            wSpec = LayoutManager.getChildMeasureSpec(availableSpaceInOther, otherDirParentSpecMode, horizontalInsets, lp.width, false);
            hSpec = LayoutManager.getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getHeightMode(), verticalInsets, lp.height, true);
        } else {
            hSpec = LayoutManager.getChildMeasureSpec(availableSpaceInOther, otherDirParentSpecMode, verticalInsets, lp.height, false);
            wSpec = LayoutManager.getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getWidthMode(), horizontalInsets, lp.width, true);
        }
        measureChildWithDecorationsAndMargin(view, wSpec, hSpec, alreadyMeasured);
    }

    private void guessMeasurement(float maxSizeInOther, int currentOtherDirSize) {
        calculateItemBorders(Math.max(Math.round(((float) this.mSpanCount) * maxSizeInOther), currentOtherDirSize));
    }

    private void measureChildWithDecorationsAndMargin(View child, int widthSpec, int heightSpec, boolean alreadyMeasured) {
        boolean measure;
        android.support.v7.widget.RecyclerView.LayoutParams lp = (android.support.v7.widget.RecyclerView.LayoutParams) child.getLayoutParams();
        if (alreadyMeasured) {
            measure = shouldReMeasureChild(child, widthSpec, heightSpec, lp);
        } else {
            measure = shouldMeasureChild(child, widthSpec, heightSpec, lp);
        }
        if (measure) {
            child.measure(widthSpec, heightSpec);
        }
    }

    private void assignSpans(Recycler recycler, State state, int count, int consumedSpanCount, boolean layingOutInPrimaryDirection) {
        int start;
        int end;
        int diff;
        if (layingOutInPrimaryDirection) {
            start = 0;
            end = count;
            diff = 1;
        } else {
            start = count - 1;
            end = -1;
            diff = -1;
        }
        int span = 0;
        for (int i = start; i != end; i += diff) {
            View view = this.mSet[i];
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            params.mSpanSize = getSpanSize(recycler, state, getPosition(view));
            params.mSpanIndex = span;
            span += params.mSpanSize;
        }
    }

    public int getSpanCount() {
        return this.mSpanCount;
    }

    public void setSpanCount(int spanCount) {
        if (spanCount != this.mSpanCount) {
            this.mPendingSpanCountChange = true;
            if (spanCount >= 1) {
                this.mSpanCount = spanCount;
                this.mSpanSizeLookup.invalidateSpanIndexCache();
                requestLayout();
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Span count should be at least 1. Provided ");
            stringBuilder.append(spanCount);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public View onFocusSearchFailed(View focused, int focusDirection, Recycler recycler, State state) {
        GridLayoutManager gridLayoutManager = this;
        Recycler recycler2 = recycler;
        State state2 = state;
        View prevFocusedChild = findContainingItemView(focused);
        if (prevFocusedChild == null) {
            return null;
        }
        LayoutParams lp = (LayoutParams) prevFocusedChild.getLayoutParams();
        int prevSpanStart = lp.mSpanIndex;
        int prevSpanEnd = lp.mSpanIndex + lp.mSpanSize;
        View view = super.onFocusSearchFailed(focused, focusDirection, recycler, state);
        if (view == null) {
            return null;
        }
        int start;
        int inc;
        int limit;
        int start2;
        int i;
        int i2;
        int i3;
        int layoutDir = convertFocusDirectionToLayoutDirection(focusDirection);
        boolean ascend = (layoutDir == 1) != gridLayoutManager.mShouldReverseLayout;
        if (ascend) {
            start = getChildCount() - 1;
            inc = -1;
            limit = -1;
        } else {
            start = 0;
            inc = 1;
            limit = getChildCount();
        }
        boolean preferLastSpan = gridLayoutManager.mOrientation == 1 && isLayoutRTL();
        View focusableWeakCandidate = null;
        View unfocusableWeakCandidate = null;
        int focusableSpanGroupIndex = getSpanGroupIndex(recycler2, state2, start);
        int i4 = start;
        int focusableWeakCandidateSpanIndex = -1;
        int focusableWeakCandidateOverlap = 0;
        layoutDir = -1;
        int unfocusableWeakCandidateOverlap = 0;
        while (i4 != limit) {
            start2 = start;
            start = getSpanGroupIndex(recycler2, state2, i4);
            View candidate = getChildAt(i4);
            if (candidate == prevFocusedChild) {
                View view2 = prevFocusedChild;
                i = focusableWeakCandidateSpanIndex;
                i2 = focusableWeakCandidateOverlap;
                i3 = focusableSpanGroupIndex;
                break;
            }
            if (!candidate.hasFocusable() || start == focusableSpanGroupIndex) {
                LayoutParams candidateLp = (LayoutParams) candidate.getLayoutParams();
                view2 = prevFocusedChild;
                prevFocusedChild = candidateLp.mSpanIndex;
                i3 = focusableSpanGroupIndex;
                focusableSpanGroupIndex = candidateLp.mSpanIndex + candidateLp.mSpanSize;
                if (candidate.hasFocusable() && prevFocusedChild == prevSpanStart && focusableSpanGroupIndex == prevSpanEnd) {
                    return candidate;
                }
                boolean assignAsWeek;
                View unfocusableWeakCandidate2;
                if (candidate.hasFocusable()) {
                    if (focusableWeakCandidate == null) {
                        i = focusableWeakCandidateSpanIndex;
                        i2 = focusableWeakCandidateOverlap;
                        assignAsWeek = true;
                        if (!assignAsWeek) {
                            if (candidate.hasFocusable()) {
                                i2 = Math.min(focusableSpanGroupIndex, prevSpanEnd) - Math.max(prevFocusedChild, prevSpanStart);
                                focusableWeakCandidate = candidate;
                                focusableWeakCandidateSpanIndex = candidateLp.mSpanIndex;
                            } else {
                                unfocusableWeakCandidate2 = candidate;
                                layoutDir = candidateLp.mSpanIndex;
                                unfocusableWeakCandidate = unfocusableWeakCandidate2;
                                unfocusableWeakCandidateOverlap = Math.min(focusableSpanGroupIndex, prevSpanEnd) - Math.max(prevFocusedChild, prevSpanStart);
                                focusableWeakCandidateSpanIndex = i;
                            }
                            i4 += inc;
                            focusableWeakCandidateOverlap = i2;
                            start = start2;
                            prevFocusedChild = view2;
                            focusableSpanGroupIndex = i3;
                            recycler2 = recycler;
                            state2 = state;
                        }
                    }
                }
                if (candidate.hasFocusable() || unfocusableWeakCandidate != null) {
                    assignAsWeek = false;
                    start = Math.min(focusableSpanGroupIndex, prevSpanEnd) - Math.max(prevFocusedChild, prevSpanStart);
                    boolean z;
                    if (!candidate.hasFocusable()) {
                        i = focusableWeakCandidateSpanIndex;
                        if (focusableWeakCandidate == null) {
                            i2 = focusableWeakCandidateOverlap;
                            z = true;
                            if (isViewPartiallyVisible(candidate, 0, true)) {
                                if (start > unfocusableWeakCandidateOverlap) {
                                    assignAsWeek = true;
                                } else if (start == unfocusableWeakCandidateOverlap) {
                                    if (prevFocusedChild <= layoutDir) {
                                        z = false;
                                    }
                                    if (preferLastSpan == z) {
                                        assignAsWeek = true;
                                    }
                                }
                            }
                        } else {
                            i2 = focusableWeakCandidateOverlap;
                        }
                    } else if (start > focusableWeakCandidateOverlap) {
                        assignAsWeek = true;
                        i = focusableWeakCandidateSpanIndex;
                        i2 = focusableWeakCandidateOverlap;
                    } else {
                        if (start == focusableWeakCandidateOverlap) {
                            if (prevFocusedChild > focusableWeakCandidateSpanIndex) {
                                i = focusableWeakCandidateSpanIndex;
                                z = true;
                            } else {
                                i = focusableWeakCandidateSpanIndex;
                                z = false;
                            }
                            if (preferLastSpan == z) {
                                assignAsWeek = true;
                                i2 = focusableWeakCandidateOverlap;
                            }
                        } else {
                            i = focusableWeakCandidateSpanIndex;
                        }
                        i2 = focusableWeakCandidateOverlap;
                    }
                    if (!assignAsWeek) {
                        if (candidate.hasFocusable()) {
                            unfocusableWeakCandidate2 = candidate;
                            layoutDir = candidateLp.mSpanIndex;
                            unfocusableWeakCandidate = unfocusableWeakCandidate2;
                            unfocusableWeakCandidateOverlap = Math.min(focusableSpanGroupIndex, prevSpanEnd) - Math.max(prevFocusedChild, prevSpanStart);
                            focusableWeakCandidateSpanIndex = i;
                        } else {
                            i2 = Math.min(focusableSpanGroupIndex, prevSpanEnd) - Math.max(prevFocusedChild, prevSpanStart);
                            focusableWeakCandidate = candidate;
                            focusableWeakCandidateSpanIndex = candidateLp.mSpanIndex;
                        }
                        i4 += inc;
                        focusableWeakCandidateOverlap = i2;
                        start = start2;
                        prevFocusedChild = view2;
                        focusableSpanGroupIndex = i3;
                        recycler2 = recycler;
                        state2 = state;
                    }
                }
                i = focusableWeakCandidateSpanIndex;
                i2 = focusableWeakCandidateOverlap;
                assignAsWeek = true;
                if (!assignAsWeek) {
                    if (candidate.hasFocusable()) {
                        i2 = Math.min(focusableSpanGroupIndex, prevSpanEnd) - Math.max(prevFocusedChild, prevSpanStart);
                        focusableWeakCandidate = candidate;
                        focusableWeakCandidateSpanIndex = candidateLp.mSpanIndex;
                    } else {
                        unfocusableWeakCandidate2 = candidate;
                        layoutDir = candidateLp.mSpanIndex;
                        unfocusableWeakCandidate = unfocusableWeakCandidate2;
                        unfocusableWeakCandidateOverlap = Math.min(focusableSpanGroupIndex, prevSpanEnd) - Math.max(prevFocusedChild, prevSpanStart);
                        focusableWeakCandidateSpanIndex = i;
                    }
                    i4 += inc;
                    focusableWeakCandidateOverlap = i2;
                    start = start2;
                    prevFocusedChild = view2;
                    focusableSpanGroupIndex = i3;
                    recycler2 = recycler;
                    state2 = state;
                }
            } else if (focusableWeakCandidate != null) {
                view2 = prevFocusedChild;
                i = focusableWeakCandidateSpanIndex;
                i2 = focusableWeakCandidateOverlap;
                i3 = focusableSpanGroupIndex;
                break;
            } else {
                view2 = prevFocusedChild;
                i = focusableWeakCandidateSpanIndex;
                i2 = focusableWeakCandidateOverlap;
                i3 = focusableSpanGroupIndex;
            }
            focusableWeakCandidateSpanIndex = i;
            i4 += inc;
            focusableWeakCandidateOverlap = i2;
            start = start2;
            prevFocusedChild = view2;
            focusableSpanGroupIndex = i3;
            recycler2 = recycler;
            state2 = state;
        }
        i = focusableWeakCandidateSpanIndex;
        i2 = focusableWeakCandidateOverlap;
        i3 = focusableSpanGroupIndex;
        start2 = start;
        return focusableWeakCandidate != null ? focusableWeakCandidate : unfocusableWeakCandidate;
    }

    public boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null && !this.mPendingSpanCountChange;
    }
}
