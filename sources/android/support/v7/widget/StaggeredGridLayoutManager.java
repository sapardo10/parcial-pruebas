package android.support.v7.widget;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager.LayoutPrefetchRegistry;
import android.support.v7.widget.RecyclerView.LayoutManager.Properties;
import android.support.v7.widget.RecyclerView.Recycler;
import android.support.v7.widget.RecyclerView.SmoothScroller.ScrollVectorProvider;
import android.support.v7.widget.RecyclerView.State;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class StaggeredGridLayoutManager extends LayoutManager implements ScrollVectorProvider {
    static final boolean DEBUG = false;
    @Deprecated
    public static final int GAP_HANDLING_LAZY = 1;
    public static final int GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS = 2;
    public static final int GAP_HANDLING_NONE = 0;
    public static final int HORIZONTAL = 0;
    static final int INVALID_OFFSET = Integer.MIN_VALUE;
    private static final float MAX_SCROLL_FACTOR = 0.33333334f;
    private static final String TAG = "StaggeredGridLManager";
    public static final int VERTICAL = 1;
    private final AnchorInfo mAnchorInfo = new AnchorInfo();
    private final Runnable mCheckForGapsRunnable = new C03801();
    private int mFullSizeSpec;
    private int mGapStrategy = 2;
    private boolean mLaidOutInvalidFullSpan = false;
    private boolean mLastLayoutFromEnd;
    private boolean mLastLayoutRTL;
    @NonNull
    private final LayoutState mLayoutState;
    LazySpanLookup mLazySpanLookup = new LazySpanLookup();
    private int mOrientation;
    private SavedState mPendingSavedState;
    int mPendingScrollPosition = -1;
    int mPendingScrollPositionOffset = Integer.MIN_VALUE;
    private int[] mPrefetchDistances;
    @NonNull
    OrientationHelper mPrimaryOrientation;
    private BitSet mRemainingSpans;
    boolean mReverseLayout = false;
    @NonNull
    OrientationHelper mSecondaryOrientation;
    boolean mShouldReverseLayout = false;
    private int mSizePerSpan;
    private boolean mSmoothScrollbarEnabled = true;
    private int mSpanCount = -1;
    Span[] mSpans;
    private final Rect mTmpRect = new Rect();

    /* renamed from: android.support.v7.widget.StaggeredGridLayoutManager$1 */
    class C03801 implements Runnable {
        C03801() {
        }

        public void run() {
            StaggeredGridLayoutManager.this.checkForGaps();
        }
    }

    class AnchorInfo {
        boolean mInvalidateOffsets;
        boolean mLayoutFromEnd;
        int mOffset;
        int mPosition;
        int[] mSpanReferenceLines;
        boolean mValid;

        AnchorInfo() {
            reset();
        }

        void reset() {
            this.mPosition = -1;
            this.mOffset = Integer.MIN_VALUE;
            this.mLayoutFromEnd = false;
            this.mInvalidateOffsets = false;
            this.mValid = false;
            int[] iArr = this.mSpanReferenceLines;
            if (iArr != null) {
                Arrays.fill(iArr, -1);
            }
        }

        void saveSpanReferenceLines(Span[] spans) {
            int i;
            int spanCount = spans.length;
            int[] iArr = this.mSpanReferenceLines;
            if (iArr != null) {
                if (iArr.length >= spanCount) {
                    for (i = 0; i < spanCount; i++) {
                        this.mSpanReferenceLines[i] = spans[i].getStartLine(Integer.MIN_VALUE);
                    }
                }
            }
            this.mSpanReferenceLines = new int[StaggeredGridLayoutManager.this.mSpans.length];
            for (i = 0; i < spanCount; i++) {
                this.mSpanReferenceLines[i] = spans[i].getStartLine(Integer.MIN_VALUE);
            }
        }

        void assignCoordinateFromPadding() {
            int endAfterPadding;
            if (this.mLayoutFromEnd) {
                endAfterPadding = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding();
            } else {
                endAfterPadding = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding();
            }
            this.mOffset = endAfterPadding;
        }

        void assignCoordinateFromPadding(int addedDistance) {
            if (this.mLayoutFromEnd) {
                this.mOffset = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding() - addedDistance;
            } else {
                this.mOffset = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding() + addedDistance;
            }
        }
    }

    static class LazySpanLookup {
        private static final int MIN_SIZE = 10;
        int[] mData;
        List<FullSpanItem> mFullSpanItems;

        static class FullSpanItem implements Parcelable {
            public static final Creator<FullSpanItem> CREATOR = new C03811();
            int mGapDir;
            int[] mGapPerSpan;
            boolean mHasUnwantedGapAfter;
            int mPosition;

            /* renamed from: android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem$1 */
            static class C03811 implements Creator<FullSpanItem> {
                C03811() {
                }

                public FullSpanItem createFromParcel(Parcel in) {
                    return new FullSpanItem(in);
                }

                public FullSpanItem[] newArray(int size) {
                    return new FullSpanItem[size];
                }
            }

            FullSpanItem(Parcel in) {
                this.mPosition = in.readInt();
                this.mGapDir = in.readInt();
                boolean z = true;
                if (in.readInt() != 1) {
                    z = false;
                }
                this.mHasUnwantedGapAfter = z;
                int spanCount = in.readInt();
                if (spanCount > 0) {
                    this.mGapPerSpan = new int[spanCount];
                    in.readIntArray(this.mGapPerSpan);
                }
            }

            FullSpanItem() {
            }

            int getGapForSpan(int spanIndex) {
                int[] iArr = this.mGapPerSpan;
                return iArr == null ? 0 : iArr[spanIndex];
            }

            public int describeContents() {
                return 0;
            }

            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.mPosition);
                dest.writeInt(this.mGapDir);
                dest.writeInt(this.mHasUnwantedGapAfter);
                int[] iArr = this.mGapPerSpan;
                if (iArr == null || iArr.length <= 0) {
                    dest.writeInt(0);
                    return;
                }
                dest.writeInt(iArr.length);
                dest.writeIntArray(this.mGapPerSpan);
            }

            public String toString() {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("FullSpanItem{mPosition=");
                stringBuilder.append(this.mPosition);
                stringBuilder.append(", mGapDir=");
                stringBuilder.append(this.mGapDir);
                stringBuilder.append(", mHasUnwantedGapAfter=");
                stringBuilder.append(this.mHasUnwantedGapAfter);
                stringBuilder.append(", mGapPerSpan=");
                stringBuilder.append(Arrays.toString(this.mGapPerSpan));
                stringBuilder.append('}');
                return stringBuilder.toString();
            }
        }

        LazySpanLookup() {
        }

        int forceInvalidateAfter(int position) {
            List list = this.mFullSpanItems;
            if (list != null) {
                for (int i = list.size() - 1; i >= 0; i--) {
                    if (((FullSpanItem) this.mFullSpanItems.get(i)).mPosition >= position) {
                        this.mFullSpanItems.remove(i);
                    }
                }
            }
            return invalidateAfter(position);
        }

        int invalidateAfter(int position) {
            int[] iArr = this.mData;
            if (iArr == null || position >= iArr.length) {
                return -1;
            }
            int endPosition = invalidateFullSpansAfter(position);
            if (endPosition == -1) {
                int[] iArr2 = this.mData;
                Arrays.fill(iArr2, position, iArr2.length, -1);
                return this.mData.length;
            }
            Arrays.fill(this.mData, position, endPosition + 1, -1);
            return endPosition + 1;
        }

        int getSpan(int position) {
            int[] iArr = this.mData;
            if (iArr != null) {
                if (position < iArr.length) {
                    return iArr[position];
                }
            }
            return -1;
        }

        void setSpan(int position, Span span) {
            ensureSize(position);
            this.mData[position] = span.mIndex;
        }

        int sizeForPosition(int position) {
            int len = this.mData.length;
            while (len <= position) {
                len *= 2;
            }
            return len;
        }

        void ensureSize(int position) {
            int[] iArr = this.mData;
            if (iArr == null) {
                this.mData = new int[(Math.max(position, 10) + 1)];
                Arrays.fill(this.mData, -1);
            } else if (position >= iArr.length) {
                iArr = this.mData;
                this.mData = new int[sizeForPosition(position)];
                System.arraycopy(iArr, 0, this.mData, 0, iArr.length);
                int[] iArr2 = this.mData;
                Arrays.fill(iArr2, iArr.length, iArr2.length, -1);
            }
        }

        void clear() {
            int[] iArr = this.mData;
            if (iArr != null) {
                Arrays.fill(iArr, -1);
            }
            this.mFullSpanItems = null;
        }

        void offsetForRemoval(int positionStart, int itemCount) {
            int[] iArr = this.mData;
            if (iArr != null) {
                if (positionStart < iArr.length) {
                    ensureSize(positionStart + itemCount);
                    Object obj = this.mData;
                    System.arraycopy(obj, positionStart + itemCount, obj, positionStart, (obj.length - positionStart) - itemCount);
                    iArr = this.mData;
                    Arrays.fill(iArr, iArr.length - itemCount, iArr.length, -1);
                    offsetFullSpansForRemoval(positionStart, itemCount);
                }
            }
        }

        private void offsetFullSpansForRemoval(int positionStart, int itemCount) {
            List list = this.mFullSpanItems;
            if (list != null) {
                int end = positionStart + itemCount;
                for (int i = list.size() - 1; i >= 0; i--) {
                    FullSpanItem fsi = (FullSpanItem) this.mFullSpanItems.get(i);
                    if (fsi.mPosition >= positionStart) {
                        if (fsi.mPosition < end) {
                            this.mFullSpanItems.remove(i);
                        } else {
                            fsi.mPosition -= itemCount;
                        }
                    }
                }
            }
        }

        void offsetForAddition(int positionStart, int itemCount) {
            int[] iArr = this.mData;
            if (iArr != null) {
                if (positionStart < iArr.length) {
                    ensureSize(positionStart + itemCount);
                    Object obj = this.mData;
                    System.arraycopy(obj, positionStart, obj, positionStart + itemCount, (obj.length - positionStart) - itemCount);
                    Arrays.fill(this.mData, positionStart, positionStart + itemCount, -1);
                    offsetFullSpansForAddition(positionStart, itemCount);
                }
            }
        }

        private void offsetFullSpansForAddition(int positionStart, int itemCount) {
            List list = this.mFullSpanItems;
            if (list != null) {
                for (int i = list.size() - 1; i >= 0; i--) {
                    FullSpanItem fsi = (FullSpanItem) this.mFullSpanItems.get(i);
                    if (fsi.mPosition >= positionStart) {
                        fsi.mPosition += itemCount;
                    }
                }
            }
        }

        private int invalidateFullSpansAfter(int position) {
            if (this.mFullSpanItems == null) {
                return -1;
            }
            FullSpanItem item = getFullSpanItem(position);
            if (item != null) {
                this.mFullSpanItems.remove(item);
            }
            int nextFsiIndex = -1;
            int count = this.mFullSpanItems.size();
            for (int i = 0; i < count; i++) {
                if (((FullSpanItem) this.mFullSpanItems.get(i)).mPosition >= position) {
                    nextFsiIndex = i;
                    break;
                }
            }
            if (nextFsiIndex == -1) {
                return -1;
            }
            FullSpanItem fsi = (FullSpanItem) this.mFullSpanItems.get(nextFsiIndex);
            this.mFullSpanItems.remove(nextFsiIndex);
            return fsi.mPosition;
        }

        public void addFullSpanItem(FullSpanItem fullSpanItem) {
            if (this.mFullSpanItems == null) {
                this.mFullSpanItems = new ArrayList();
            }
            int size = this.mFullSpanItems.size();
            for (int i = 0; i < size; i++) {
                FullSpanItem other = (FullSpanItem) this.mFullSpanItems.get(i);
                if (other.mPosition == fullSpanItem.mPosition) {
                    this.mFullSpanItems.remove(i);
                }
                if (other.mPosition >= fullSpanItem.mPosition) {
                    this.mFullSpanItems.add(i, fullSpanItem);
                    return;
                }
            }
            this.mFullSpanItems.add(fullSpanItem);
        }

        public FullSpanItem getFullSpanItem(int position) {
            List list = this.mFullSpanItems;
            if (list == null) {
                return null;
            }
            for (int i = list.size() - 1; i >= 0; i--) {
                FullSpanItem fsi = (FullSpanItem) this.mFullSpanItems.get(i);
                if (fsi.mPosition == position) {
                    return fsi;
                }
            }
            return null;
        }

        public FullSpanItem getFirstFullSpanItemInRange(int minPos, int maxPos, int gapDir, boolean hasUnwantedGapAfter) {
            int limit = this.mFullSpanItems;
            if (limit == 0) {
                return null;
            }
            limit = limit.size();
            for (int i = 0; i < limit; i++) {
                FullSpanItem fsi = (FullSpanItem) this.mFullSpanItems.get(i);
                if (fsi.mPosition >= maxPos) {
                    return null;
                }
                if (fsi.mPosition >= minPos && (gapDir == 0 || fsi.mGapDir == gapDir || (hasUnwantedGapAfter && fsi.mHasUnwantedGapAfter))) {
                    return fsi;
                }
            }
            return null;
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public static class SavedState implements Parcelable {
        public static final Creator<SavedState> CREATOR = new C03821();
        boolean mAnchorLayoutFromEnd;
        int mAnchorPosition;
        List<FullSpanItem> mFullSpanItems;
        boolean mLastLayoutRTL;
        boolean mReverseLayout;
        int[] mSpanLookup;
        int mSpanLookupSize;
        int[] mSpanOffsets;
        int mSpanOffsetsSize;
        int mVisibleAnchorPosition;

        /* renamed from: android.support.v7.widget.StaggeredGridLayoutManager$SavedState$1 */
        static class C03821 implements Creator<SavedState> {
            C03821() {
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        SavedState(Parcel in) {
            this.mAnchorPosition = in.readInt();
            this.mVisibleAnchorPosition = in.readInt();
            this.mSpanOffsetsSize = in.readInt();
            int i = this.mSpanOffsetsSize;
            if (i > 0) {
                this.mSpanOffsets = new int[i];
                in.readIntArray(this.mSpanOffsets);
            }
            this.mSpanLookupSize = in.readInt();
            i = this.mSpanLookupSize;
            if (i > 0) {
                this.mSpanLookup = new int[i];
                in.readIntArray(this.mSpanLookup);
            }
            boolean z = false;
            this.mReverseLayout = in.readInt() == 1;
            this.mAnchorLayoutFromEnd = in.readInt() == 1;
            if (in.readInt() == 1) {
                z = true;
            }
            this.mLastLayoutRTL = z;
            this.mFullSpanItems = in.readArrayList(FullSpanItem.class.getClassLoader());
        }

        public SavedState(SavedState other) {
            this.mSpanOffsetsSize = other.mSpanOffsetsSize;
            this.mAnchorPosition = other.mAnchorPosition;
            this.mVisibleAnchorPosition = other.mVisibleAnchorPosition;
            this.mSpanOffsets = other.mSpanOffsets;
            this.mSpanLookupSize = other.mSpanLookupSize;
            this.mSpanLookup = other.mSpanLookup;
            this.mReverseLayout = other.mReverseLayout;
            this.mAnchorLayoutFromEnd = other.mAnchorLayoutFromEnd;
            this.mLastLayoutRTL = other.mLastLayoutRTL;
            this.mFullSpanItems = other.mFullSpanItems;
        }

        void invalidateSpanInfo() {
            this.mSpanOffsets = null;
            this.mSpanOffsetsSize = 0;
            this.mSpanLookupSize = 0;
            this.mSpanLookup = null;
            this.mFullSpanItems = null;
        }

        void invalidateAnchorPositionInfo() {
            this.mSpanOffsets = null;
            this.mSpanOffsetsSize = 0;
            this.mAnchorPosition = -1;
            this.mVisibleAnchorPosition = -1;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mAnchorPosition);
            dest.writeInt(this.mVisibleAnchorPosition);
            dest.writeInt(this.mSpanOffsetsSize);
            if (this.mSpanOffsetsSize > 0) {
                dest.writeIntArray(this.mSpanOffsets);
            }
            dest.writeInt(this.mSpanLookupSize);
            if (this.mSpanLookupSize > 0) {
                dest.writeIntArray(this.mSpanLookup);
            }
            dest.writeInt(this.mReverseLayout);
            dest.writeInt(this.mAnchorLayoutFromEnd);
            dest.writeInt(this.mLastLayoutRTL);
            dest.writeList(this.mFullSpanItems);
        }
    }

    class Span {
        static final int INVALID_LINE = Integer.MIN_VALUE;
        int mCachedEnd = Integer.MIN_VALUE;
        int mCachedStart = Integer.MIN_VALUE;
        int mDeletedSize = 0;
        final int mIndex;
        ArrayList<View> mViews = new ArrayList();

        Span(int index) {
            this.mIndex = index;
        }

        int getStartLine(int def) {
            int i = this.mCachedStart;
            if (i != Integer.MIN_VALUE) {
                return i;
            }
            if (this.mViews.size() == 0) {
                return def;
            }
            calculateCachedStart();
            return this.mCachedStart;
        }

        void calculateCachedStart() {
            View startView = (View) this.mViews.get(0);
            LayoutParams lp = getLayoutParams(startView);
            this.mCachedStart = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart(startView);
            if (lp.mFullSpan) {
                FullSpanItem fsi = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(lp.getViewLayoutPosition());
                if (fsi != null && fsi.mGapDir == -1) {
                    this.mCachedStart -= fsi.getGapForSpan(this.mIndex);
                }
            }
        }

        int getStartLine() {
            int i = this.mCachedStart;
            if (i != Integer.MIN_VALUE) {
                return i;
            }
            calculateCachedStart();
            return this.mCachedStart;
        }

        int getEndLine(int def) {
            int i = this.mCachedEnd;
            if (i != Integer.MIN_VALUE) {
                return i;
            }
            if (this.mViews.size() == 0) {
                return def;
            }
            calculateCachedEnd();
            return this.mCachedEnd;
        }

        void calculateCachedEnd() {
            ArrayList arrayList = this.mViews;
            View endView = (View) arrayList.get(arrayList.size() - 1);
            LayoutParams lp = getLayoutParams(endView);
            this.mCachedEnd = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(endView);
            if (lp.mFullSpan) {
                FullSpanItem fsi = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(lp.getViewLayoutPosition());
                if (fsi != null && fsi.mGapDir == 1) {
                    this.mCachedEnd += fsi.getGapForSpan(this.mIndex);
                }
            }
        }

        int getEndLine() {
            int i = this.mCachedEnd;
            if (i != Integer.MIN_VALUE) {
                return i;
            }
            calculateCachedEnd();
            return this.mCachedEnd;
        }

        void prependToSpan(View view) {
            LayoutParams lp = getLayoutParams(view);
            lp.mSpan = this;
            this.mViews.add(0, view);
            this.mCachedStart = Integer.MIN_VALUE;
            if (this.mViews.size() == 1) {
                this.mCachedEnd = Integer.MIN_VALUE;
            }
            if (!lp.isItemRemoved()) {
                if (!lp.isItemChanged()) {
                    return;
                }
            }
            this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view);
        }

        void appendToSpan(View view) {
            LayoutParams lp = getLayoutParams(view);
            lp.mSpan = this;
            this.mViews.add(view);
            this.mCachedEnd = Integer.MIN_VALUE;
            if (this.mViews.size() == 1) {
                this.mCachedStart = Integer.MIN_VALUE;
            }
            if (!lp.isItemRemoved()) {
                if (!lp.isItemChanged()) {
                    return;
                }
            }
            this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view);
        }

        void cacheReferenceLineAndClear(boolean reverseLayout, int offset) {
            int reference;
            if (reverseLayout) {
                reference = getEndLine(Integer.MIN_VALUE);
            } else {
                reference = getStartLine(Integer.MIN_VALUE);
            }
            clear();
            if (reference != Integer.MIN_VALUE) {
                if (reverseLayout) {
                    if (reference < StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding()) {
                        return;
                    }
                }
                if (!reverseLayout) {
                    if (reference <= StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding()) {
                    }
                    return;
                }
                if (offset != Integer.MIN_VALUE) {
                    reference += offset;
                }
                this.mCachedEnd = reference;
                this.mCachedStart = reference;
            }
        }

        void clear() {
            this.mViews.clear();
            invalidateCache();
            this.mDeletedSize = 0;
        }

        void invalidateCache() {
            this.mCachedStart = Integer.MIN_VALUE;
            this.mCachedEnd = Integer.MIN_VALUE;
        }

        void setLine(int line) {
            this.mCachedStart = line;
            this.mCachedEnd = line;
        }

        void popEnd() {
            int size = this.mViews.size();
            View end = (View) this.mViews.remove(size - 1);
            LayoutParams lp = getLayoutParams(end);
            lp.mSpan = null;
            if (!lp.isItemRemoved()) {
                if (!lp.isItemChanged()) {
                    if (size == 1) {
                        this.mCachedStart = Integer.MIN_VALUE;
                    }
                    this.mCachedEnd = Integer.MIN_VALUE;
                }
            }
            this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(end);
            if (size == 1) {
                this.mCachedStart = Integer.MIN_VALUE;
            }
            this.mCachedEnd = Integer.MIN_VALUE;
        }

        void popStart() {
            View start = (View) this.mViews.remove(0);
            LayoutParams lp = getLayoutParams(start);
            lp.mSpan = null;
            if (this.mViews.size() == 0) {
                this.mCachedEnd = Integer.MIN_VALUE;
            }
            if (!lp.isItemRemoved()) {
                if (!lp.isItemChanged()) {
                    this.mCachedStart = Integer.MIN_VALUE;
                }
            }
            this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(start);
            this.mCachedStart = Integer.MIN_VALUE;
        }

        public int getDeletedSize() {
            return this.mDeletedSize;
        }

        LayoutParams getLayoutParams(View view) {
            return (LayoutParams) view.getLayoutParams();
        }

        void onOffset(int dt) {
            int i = this.mCachedStart;
            if (i != Integer.MIN_VALUE) {
                this.mCachedStart = i + dt;
            }
            i = this.mCachedEnd;
            if (i != Integer.MIN_VALUE) {
                this.mCachedEnd = i + dt;
            }
        }

        public int findFirstVisibleItemPosition() {
            if (StaggeredGridLayoutManager.this.mReverseLayout) {
                return findOneVisibleChild(this.mViews.size() - 1, -1, false);
            }
            return findOneVisibleChild(0, this.mViews.size(), false);
        }

        public int findFirstPartiallyVisibleItemPosition() {
            if (StaggeredGridLayoutManager.this.mReverseLayout) {
                return findOnePartiallyVisibleChild(this.mViews.size() - 1, -1, true);
            }
            return findOnePartiallyVisibleChild(0, this.mViews.size(), true);
        }

        public int findFirstCompletelyVisibleItemPosition() {
            if (StaggeredGridLayoutManager.this.mReverseLayout) {
                return findOneVisibleChild(this.mViews.size() - 1, -1, true);
            }
            return findOneVisibleChild(0, this.mViews.size(), true);
        }

        public int findLastVisibleItemPosition() {
            if (StaggeredGridLayoutManager.this.mReverseLayout) {
                return findOneVisibleChild(0, this.mViews.size(), false);
            }
            return findOneVisibleChild(this.mViews.size() - 1, -1, false);
        }

        public int findLastPartiallyVisibleItemPosition() {
            if (StaggeredGridLayoutManager.this.mReverseLayout) {
                return findOnePartiallyVisibleChild(0, this.mViews.size(), true);
            }
            return findOnePartiallyVisibleChild(this.mViews.size() - 1, -1, true);
        }

        public int findLastCompletelyVisibleItemPosition() {
            if (StaggeredGridLayoutManager.this.mReverseLayout) {
                return findOneVisibleChild(0, this.mViews.size(), true);
            }
            return findOneVisibleChild(this.mViews.size() - 1, -1, true);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        int findOnePartiallyOrCompletelyVisibleChild(int r15, int r16, boolean r17, boolean r18, boolean r19) {
            /*
            r14 = this;
            r0 = r14;
            r1 = r16;
            r2 = android.support.v7.widget.StaggeredGridLayoutManager.this;
            r2 = r2.mPrimaryOrientation;
            r2 = r2.getStartAfterPadding();
            r3 = android.support.v7.widget.StaggeredGridLayoutManager.this;
            r3 = r3.mPrimaryOrientation;
            r3 = r3.getEndAfterPadding();
            r4 = -1;
            r5 = 1;
            r6 = r15;
            if (r1 <= r6) goto L_0x001a;
        L_0x0018:
            r7 = 1;
            goto L_0x001b;
        L_0x001a:
            r7 = -1;
        L_0x001b:
            r8 = r15;
        L_0x001c:
            if (r8 == r1) goto L_0x0078;
        L_0x001e:
            r9 = r0.mViews;
            r9 = r9.get(r8);
            r9 = (android.view.View) r9;
            r10 = android.support.v7.widget.StaggeredGridLayoutManager.this;
            r10 = r10.mPrimaryOrientation;
            r10 = r10.getDecoratedStart(r9);
            r11 = android.support.v7.widget.StaggeredGridLayoutManager.this;
            r11 = r11.mPrimaryOrientation;
            r11 = r11.getDecoratedEnd(r9);
            r12 = 0;
            if (r19 == 0) goto L_0x003c;
        L_0x0039:
            if (r10 > r3) goto L_0x0040;
        L_0x003b:
            goto L_0x003e;
        L_0x003c:
            if (r10 >= r3) goto L_0x0040;
        L_0x003e:
            r13 = 1;
            goto L_0x0041;
        L_0x0040:
            r13 = 0;
        L_0x0041:
            if (r19 == 0) goto L_0x0046;
        L_0x0043:
            if (r11 < r2) goto L_0x004a;
        L_0x0045:
            goto L_0x0048;
        L_0x0046:
            if (r11 <= r2) goto L_0x004a;
        L_0x0048:
            r12 = 1;
        L_0x004a:
            if (r13 == 0) goto L_0x0075;
        L_0x004c:
            if (r12 == 0) goto L_0x0075;
        L_0x004e:
            if (r17 == 0) goto L_0x005e;
        L_0x0050:
            if (r18 == 0) goto L_0x005e;
        L_0x0052:
            if (r10 < r2) goto L_0x005d;
        L_0x0054:
            if (r11 > r3) goto L_0x005d;
        L_0x0056:
            r4 = android.support.v7.widget.StaggeredGridLayoutManager.this;
            r4 = r4.getPosition(r9);
            return r4;
        L_0x005d:
            goto L_0x0076;
            if (r18 == 0) goto L_0x0068;
        L_0x0061:
            r4 = android.support.v7.widget.StaggeredGridLayoutManager.this;
            r4 = r4.getPosition(r9);
            return r4;
        L_0x0068:
            if (r10 < r2) goto L_0x006e;
        L_0x006a:
            if (r11 <= r3) goto L_0x006d;
        L_0x006c:
            goto L_0x006e;
        L_0x006d:
            goto L_0x0076;
        L_0x006e:
            r4 = android.support.v7.widget.StaggeredGridLayoutManager.this;
            r4 = r4.getPosition(r9);
            return r4;
        L_0x0076:
            r8 = r8 + r7;
            goto L_0x001c;
        L_0x0078:
            return r4;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.StaggeredGridLayoutManager.Span.findOnePartiallyOrCompletelyVisibleChild(int, int, boolean, boolean, boolean):int");
        }

        int findOneVisibleChild(int fromIndex, int toIndex, boolean completelyVisible) {
            return findOnePartiallyOrCompletelyVisibleChild(fromIndex, toIndex, completelyVisible, true, false);
        }

        int findOnePartiallyVisibleChild(int fromIndex, int toIndex, boolean acceptEndPointInclusion) {
            return findOnePartiallyOrCompletelyVisibleChild(fromIndex, toIndex, false, false, acceptEndPointInclusion);
        }

        public View getFocusableViewAfter(int referenceChildPosition, int layoutDir) {
            View candidate = null;
            int i;
            if (layoutDir != -1) {
                for (i = this.mViews.size() - 1; i >= 0; i--) {
                    View view = (View) this.mViews.get(i);
                    if (StaggeredGridLayoutManager.this.mReverseLayout) {
                        if (StaggeredGridLayoutManager.this.getPosition(view) >= referenceChildPosition) {
                            break;
                        }
                    }
                    if (!StaggeredGridLayoutManager.this.mReverseLayout) {
                        if (StaggeredGridLayoutManager.this.getPosition(view) <= referenceChildPosition) {
                            break;
                        }
                    }
                    if (!view.hasFocusable()) {
                        break;
                    }
                    candidate = view;
                }
            } else {
                i = this.mViews.size();
                for (int i2 = 0; i2 < i; i2++) {
                    View view2 = (View) this.mViews.get(i2);
                    if (StaggeredGridLayoutManager.this.mReverseLayout) {
                        if (StaggeredGridLayoutManager.this.getPosition(view2) <= referenceChildPosition) {
                            break;
                        }
                    }
                    if (!StaggeredGridLayoutManager.this.mReverseLayout) {
                        if (StaggeredGridLayoutManager.this.getPosition(view2) >= referenceChildPosition) {
                            break;
                        }
                    }
                    if (!view2.hasFocusable()) {
                        break;
                    }
                    candidate = view2;
                }
            }
            return candidate;
        }
    }

    public static class LayoutParams extends android.support.v7.widget.RecyclerView.LayoutParams {
        public static final int INVALID_SPAN_ID = -1;
        boolean mFullSpan;
        Span mSpan;

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

        public void setFullSpan(boolean fullSpan) {
            this.mFullSpan = fullSpan;
        }

        public boolean isFullSpan() {
            return this.mFullSpan;
        }

        public final int getSpanIndex() {
            Span span = this.mSpan;
            if (span == null) {
                return -1;
            }
            return span.mIndex;
        }
    }

    public int[] findFirstCompletelyVisibleItemPositions(int[] r4) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x0041 in {1, 7, 8, 10} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r3 = this;
        if (r4 != 0) goto L_0x0007;
    L_0x0002:
        r0 = r3.mSpanCount;
        r4 = new int[r0];
        goto L_0x000c;
    L_0x0007:
        r0 = r4.length;
        r1 = r3.mSpanCount;
        if (r0 < r1) goto L_0x001f;
    L_0x000c:
        r0 = 0;
    L_0x000d:
        r1 = r3.mSpanCount;
        if (r0 >= r1) goto L_0x001e;
    L_0x0011:
        r1 = r3.mSpans;
        r1 = r1[r0];
        r1 = r1.findFirstCompletelyVisibleItemPosition();
        r4[r0] = r1;
        r0 = r0 + 1;
        goto L_0x000d;
    L_0x001e:
        return r4;
    L_0x001f:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Provided int[]'s size must be more than or equal to span count. Expected:";
        r1.append(r2);
        r2 = r3.mSpanCount;
        r1.append(r2);
        r2 = ", array size:";
        r1.append(r2);
        r2 = r4.length;
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.StaggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(int[]):int[]");
    }

    public int[] findFirstVisibleItemPositions(int[] r4) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x0041 in {1, 7, 8, 10} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r3 = this;
        if (r4 != 0) goto L_0x0007;
    L_0x0002:
        r0 = r3.mSpanCount;
        r4 = new int[r0];
        goto L_0x000c;
    L_0x0007:
        r0 = r4.length;
        r1 = r3.mSpanCount;
        if (r0 < r1) goto L_0x001f;
    L_0x000c:
        r0 = 0;
    L_0x000d:
        r1 = r3.mSpanCount;
        if (r0 >= r1) goto L_0x001e;
    L_0x0011:
        r1 = r3.mSpans;
        r1 = r1[r0];
        r1 = r1.findFirstVisibleItemPosition();
        r4[r0] = r1;
        r0 = r0 + 1;
        goto L_0x000d;
    L_0x001e:
        return r4;
    L_0x001f:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Provided int[]'s size must be more than or equal to span count. Expected:";
        r1.append(r2);
        r2 = r3.mSpanCount;
        r1.append(r2);
        r2 = ", array size:";
        r1.append(r2);
        r2 = r4.length;
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.StaggeredGridLayoutManager.findFirstVisibleItemPositions(int[]):int[]");
    }

    public int[] findLastCompletelyVisibleItemPositions(int[] r4) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x0041 in {1, 7, 8, 10} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r3 = this;
        if (r4 != 0) goto L_0x0007;
    L_0x0002:
        r0 = r3.mSpanCount;
        r4 = new int[r0];
        goto L_0x000c;
    L_0x0007:
        r0 = r4.length;
        r1 = r3.mSpanCount;
        if (r0 < r1) goto L_0x001f;
    L_0x000c:
        r0 = 0;
    L_0x000d:
        r1 = r3.mSpanCount;
        if (r0 >= r1) goto L_0x001e;
    L_0x0011:
        r1 = r3.mSpans;
        r1 = r1[r0];
        r1 = r1.findLastCompletelyVisibleItemPosition();
        r4[r0] = r1;
        r0 = r0 + 1;
        goto L_0x000d;
    L_0x001e:
        return r4;
    L_0x001f:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Provided int[]'s size must be more than or equal to span count. Expected:";
        r1.append(r2);
        r2 = r3.mSpanCount;
        r1.append(r2);
        r2 = ", array size:";
        r1.append(r2);
        r2 = r4.length;
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.StaggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(int[]):int[]");
    }

    public int[] findLastVisibleItemPositions(int[] r4) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x0041 in {1, 7, 8, 10} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r3 = this;
        if (r4 != 0) goto L_0x0007;
    L_0x0002:
        r0 = r3.mSpanCount;
        r4 = new int[r0];
        goto L_0x000c;
    L_0x0007:
        r0 = r4.length;
        r1 = r3.mSpanCount;
        if (r0 < r1) goto L_0x001f;
    L_0x000c:
        r0 = 0;
    L_0x000d:
        r1 = r3.mSpanCount;
        if (r0 >= r1) goto L_0x001e;
    L_0x0011:
        r1 = r3.mSpans;
        r1 = r1[r0];
        r1 = r1.findLastVisibleItemPosition();
        r4[r0] = r1;
        r0 = r0 + 1;
        goto L_0x000d;
    L_0x001e:
        return r4;
    L_0x001f:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Provided int[]'s size must be more than or equal to span count. Expected:";
        r1.append(r2);
        r2 = r3.mSpanCount;
        r1.append(r2);
        r2 = ", array size:";
        r1.append(r2);
        r2 = r4.length;
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.StaggeredGridLayoutManager.findLastVisibleItemPositions(int[]):int[]");
    }

    public StaggeredGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        Properties properties = LayoutManager.getProperties(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(properties.orientation);
        setSpanCount(properties.spanCount);
        setReverseLayout(properties.reverseLayout);
        this.mLayoutState = new LayoutState();
        createOrientationHelpers();
    }

    public StaggeredGridLayoutManager(int spanCount, int orientation) {
        this.mOrientation = orientation;
        setSpanCount(spanCount);
        this.mLayoutState = new LayoutState();
        createOrientationHelpers();
    }

    public boolean isAutoMeasureEnabled() {
        return this.mGapStrategy != 0;
    }

    private void createOrientationHelpers() {
        this.mPrimaryOrientation = OrientationHelper.createOrientationHelper(this, this.mOrientation);
        this.mSecondaryOrientation = OrientationHelper.createOrientationHelper(this, 1 - this.mOrientation);
    }

    boolean checkForGaps() {
        if (!(getChildCount() == 0 || this.mGapStrategy == 0)) {
            if (isAttachedToWindow()) {
                int minPos;
                int maxPos;
                if (this.mShouldReverseLayout) {
                    minPos = getLastChildPosition();
                    maxPos = getFirstChildPosition();
                } else {
                    minPos = getFirstChildPosition();
                    maxPos = getLastChildPosition();
                }
                if (minPos == 0) {
                    if (hasGapsToFix() != null) {
                        this.mLazySpanLookup.clear();
                        requestSimpleAnimationsInNextLayout();
                        requestLayout();
                        return true;
                    }
                }
                if (!this.mLaidOutInvalidFullSpan) {
                    return false;
                }
                int invalidGapDir = this.mShouldReverseLayout ? -1 : 1;
                FullSpanItem invalidFsi = this.mLazySpanLookup.getFirstFullSpanItemInRange(minPos, maxPos + 1, invalidGapDir, true);
                if (invalidFsi == null) {
                    this.mLaidOutInvalidFullSpan = false;
                    this.mLazySpanLookup.forceInvalidateAfter(maxPos + 1);
                    return false;
                }
                FullSpanItem validFsi = this.mLazySpanLookup.getFirstFullSpanItemInRange(minPos, invalidFsi.mPosition, invalidGapDir * -1, true);
                if (validFsi == null) {
                    this.mLazySpanLookup.forceInvalidateAfter(invalidFsi.mPosition);
                } else {
                    this.mLazySpanLookup.forceInvalidateAfter(validFsi.mPosition + 1);
                }
                requestSimpleAnimationsInNextLayout();
                requestLayout();
                return true;
            }
        }
        return false;
    }

    public void onScrollStateChanged(int state) {
        if (state == 0) {
            checkForGaps();
        }
    }

    public void onDetachedFromWindow(RecyclerView view, Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        removeCallbacks(this.mCheckForGapsRunnable);
        for (int i = 0; i < this.mSpanCount; i++) {
            this.mSpans[i].clear();
        }
        view.requestLayout();
    }

    View hasGapsToFix() {
        int firstChildIndex;
        int childLimit;
        int endChildIndex = getChildCount() - 1;
        BitSet mSpansToCheck = new BitSet(this.mSpanCount);
        mSpansToCheck.set(0, this.mSpanCount, true);
        int nextChildDiff = -1;
        int preferredSpanDir = (this.mOrientation == 1 && isLayoutRTL()) ? 1 : -1;
        if (r0.mShouldReverseLayout) {
            firstChildIndex = endChildIndex;
            childLimit = 0 - 1;
        } else {
            firstChildIndex = 0;
            childLimit = endChildIndex + 1;
        }
        if (firstChildIndex < childLimit) {
            nextChildDiff = 1;
        }
        for (int i = firstChildIndex; i != childLimit; i += nextChildDiff) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (mSpansToCheck.get(lp.mSpan.mIndex)) {
                if (checkSpanForGap(lp.mSpan)) {
                    return child;
                }
                mSpansToCheck.clear(lp.mSpan.mIndex);
            }
            if (!lp.mFullSpan) {
                if (i + nextChildDiff != childLimit) {
                    View nextChild = getChildAt(i + nextChildDiff);
                    boolean compareSpans = false;
                    int myEnd;
                    int nextEnd;
                    if (r0.mShouldReverseLayout) {
                        myEnd = r0.mPrimaryOrientation.getDecoratedEnd(child);
                        nextEnd = r0.mPrimaryOrientation.getDecoratedEnd(nextChild);
                        if (myEnd < nextEnd) {
                            return child;
                        }
                        if (myEnd == nextEnd) {
                            compareSpans = true;
                        }
                    } else {
                        nextEnd = r0.mPrimaryOrientation.getDecoratedStart(child);
                        myEnd = r0.mPrimaryOrientation.getDecoratedStart(nextChild);
                        if (nextEnd > myEnd) {
                            return child;
                        }
                        if (nextEnd == myEnd) {
                            compareSpans = true;
                        }
                    }
                    if (compareSpans) {
                        if ((lp.mSpan.mIndex - ((LayoutParams) nextChild.getLayoutParams()).mSpan.mIndex < 0 ? 1 : null) != (preferredSpanDir < 0 ? 1 : null)) {
                            return child;
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean checkSpanForGap(Span span) {
        if (this.mShouldReverseLayout) {
            if (span.getEndLine() < this.mPrimaryOrientation.getEndAfterPadding()) {
                return span.getLayoutParams((View) span.mViews.get(span.mViews.size() - 1)).mFullSpan ^ 1;
            }
        } else if (span.getStartLine() > this.mPrimaryOrientation.getStartAfterPadding()) {
            return span.getLayoutParams((View) span.mViews.get(0)).mFullSpan ^ 1;
        }
        return false;
    }

    public void setSpanCount(int spanCount) {
        assertNotInLayoutOrScroll(null);
        if (spanCount != this.mSpanCount) {
            invalidateSpanAssignments();
            this.mSpanCount = spanCount;
            this.mRemainingSpans = new BitSet(this.mSpanCount);
            this.mSpans = new Span[this.mSpanCount];
            for (int i = 0; i < this.mSpanCount; i++) {
                this.mSpans[i] = new Span(i);
            }
            requestLayout();
        }
    }

    public void setOrientation(int orientation) {
        if (orientation != 0) {
            if (orientation != 1) {
                throw new IllegalArgumentException("invalid orientation.");
            }
        }
        assertNotInLayoutOrScroll(null);
        if (orientation != this.mOrientation) {
            this.mOrientation = orientation;
            OrientationHelper tmp = this.mPrimaryOrientation;
            this.mPrimaryOrientation = this.mSecondaryOrientation;
            this.mSecondaryOrientation = tmp;
            requestLayout();
        }
    }

    public void setReverseLayout(boolean reverseLayout) {
        assertNotInLayoutOrScroll(null);
        SavedState savedState = this.mPendingSavedState;
        if (savedState != null && savedState.mReverseLayout != reverseLayout) {
            this.mPendingSavedState.mReverseLayout = reverseLayout;
        }
        this.mReverseLayout = reverseLayout;
        requestLayout();
    }

    public int getGapStrategy() {
        return this.mGapStrategy;
    }

    public void setGapStrategy(int gapStrategy) {
        assertNotInLayoutOrScroll(null);
        if (gapStrategy != this.mGapStrategy) {
            if (gapStrategy != 0) {
                if (gapStrategy != 2) {
                    throw new IllegalArgumentException("invalid gap strategy. Must be GAP_HANDLING_NONE or GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS");
                }
            }
            this.mGapStrategy = gapStrategy;
            requestLayout();
        }
    }

    public void assertNotInLayoutOrScroll(String message) {
        if (this.mPendingSavedState == null) {
            super.assertNotInLayoutOrScroll(message);
        }
    }

    public int getSpanCount() {
        return this.mSpanCount;
    }

    public void invalidateSpanAssignments() {
        this.mLazySpanLookup.clear();
        requestLayout();
    }

    private void resolveShouldLayoutReverse() {
        if (this.mOrientation != 1) {
            if (isLayoutRTL()) {
                this.mShouldReverseLayout = this.mReverseLayout ^ true;
                return;
            }
        }
        this.mShouldReverseLayout = this.mReverseLayout;
    }

    boolean isLayoutRTL() {
        return getLayoutDirection() == 1;
    }

    public boolean getReverseLayout() {
        return this.mReverseLayout;
    }

    public void setMeasuredDimension(Rect childrenBounds, int wSpec, int hSpec) {
        int height;
        int width;
        int horizontalPadding = getPaddingLeft() + getPaddingRight();
        int verticalPadding = getPaddingTop() + getPaddingBottom();
        if (this.mOrientation == 1) {
            height = LayoutManager.chooseSize(hSpec, childrenBounds.height() + verticalPadding, getMinimumHeight());
            width = LayoutManager.chooseSize(wSpec, (this.mSizePerSpan * this.mSpanCount) + horizontalPadding, getMinimumWidth());
        } else {
            int chooseSize = LayoutManager.chooseSize(wSpec, childrenBounds.width() + horizontalPadding, getMinimumWidth());
            height = LayoutManager.chooseSize(hSpec, (this.mSizePerSpan * this.mSpanCount) + verticalPadding, getMinimumHeight());
            width = chooseSize;
        }
        setMeasuredDimension(width, height);
    }

    public void onLayoutChildren(Recycler recycler, State state) {
        onLayoutChildren(recycler, state, true);
    }

    private void onLayoutChildren(Recycler recycler, State state, boolean shouldCheckForGaps) {
        boolean needToCheckForGaps;
        boolean recalculateAnchor;
        SavedState savedState;
        int i;
        Span span;
        boolean hasGaps;
        AnchorInfo anchorInfo = this.mAnchorInfo;
        if (this.mPendingSavedState == null) {
            if (this.mPendingScrollPosition == -1) {
                needToCheckForGaps = true;
                if (anchorInfo.mValid && this.mPendingScrollPosition == -1) {
                    if (this.mPendingSavedState != null) {
                        recalculateAnchor = false;
                        if (!recalculateAnchor) {
                            anchorInfo.reset();
                            if (this.mPendingSavedState == null) {
                                applyPendingSavedState(anchorInfo);
                            } else {
                                resolveShouldLayoutReverse();
                                anchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
                            }
                            updateAnchorInfoForLayout(state, anchorInfo);
                            anchorInfo.mValid = true;
                        }
                        if (this.mPendingSavedState != null && this.mPendingScrollPosition == -1) {
                            if (anchorInfo.mLayoutFromEnd == this.mLastLayoutFromEnd) {
                                if (isLayoutRTL() != this.mLastLayoutRTL) {
                                }
                            }
                            this.mLazySpanLookup.clear();
                            anchorInfo.mInvalidateOffsets = true;
                        }
                        if (getChildCount() > 0) {
                            savedState = this.mPendingSavedState;
                            if (savedState == null || savedState.mSpanOffsetsSize < 1) {
                                if (anchorInfo.mInvalidateOffsets) {
                                    if (!recalculateAnchor) {
                                        if (this.mAnchorInfo.mSpanReferenceLines != null) {
                                            for (i = 0; i < this.mSpanCount; i++) {
                                                span = this.mSpans[i];
                                                span.clear();
                                                span.setLine(this.mAnchorInfo.mSpanReferenceLines[i]);
                                            }
                                        }
                                    }
                                    for (i = 0; i < this.mSpanCount; i++) {
                                        this.mSpans[i].cacheReferenceLineAndClear(this.mShouldReverseLayout, anchorInfo.mOffset);
                                    }
                                    this.mAnchorInfo.saveSpanReferenceLines(this.mSpans);
                                } else {
                                    for (i = 0; i < this.mSpanCount; i++) {
                                        this.mSpans[i].clear();
                                        if (anchorInfo.mOffset == Integer.MIN_VALUE) {
                                            this.mSpans[i].setLine(anchorInfo.mOffset);
                                        }
                                    }
                                }
                                detachAndScrapAttachedViews(recycler);
                                this.mLayoutState.mRecycle = false;
                                this.mLaidOutInvalidFullSpan = false;
                                updateMeasureSpecs(this.mSecondaryOrientation.getTotalSpace());
                                updateLayoutState(anchorInfo.mPosition, state);
                                if (anchorInfo.mLayoutFromEnd) {
                                    setLayoutStateDirection(1);
                                    fill(recycler, this.mLayoutState, state);
                                    setLayoutStateDirection(-1);
                                    this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
                                    fill(recycler, this.mLayoutState, state);
                                } else {
                                    setLayoutStateDirection(-1);
                                    fill(recycler, this.mLayoutState, state);
                                    setLayoutStateDirection(1);
                                    this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
                                    fill(recycler, this.mLayoutState, state);
                                }
                                repositionToWrapContentIfNecessary();
                                if (getChildCount() <= 0) {
                                    if (this.mShouldReverseLayout) {
                                        fixStartGap(recycler, state, true);
                                        fixEndGap(recycler, state, false);
                                    } else {
                                        fixEndGap(recycler, state, true);
                                        fixStartGap(recycler, state, false);
                                    }
                                }
                                hasGaps = false;
                                if (!shouldCheckForGaps && !state.isPreLayout()) {
                                    if (this.mGapStrategy != 0) {
                                        if (getChildCount() > 0) {
                                            if (!this.mLaidOutInvalidFullSpan) {
                                                if (hasGapsToFix() != null) {
                                                }
                                            }
                                            if (needToCheckForGaps) {
                                                removeCallbacks(this.mCheckForGapsRunnable);
                                                if (checkForGaps()) {
                                                    hasGaps = true;
                                                }
                                            }
                                        }
                                    }
                                    needToCheckForGaps = false;
                                    if (needToCheckForGaps) {
                                        removeCallbacks(this.mCheckForGapsRunnable);
                                        if (checkForGaps()) {
                                            hasGaps = true;
                                        }
                                    }
                                }
                                if (!state.isPreLayout()) {
                                    this.mAnchorInfo.reset();
                                }
                                this.mLastLayoutFromEnd = anchorInfo.mLayoutFromEnd;
                                this.mLastLayoutRTL = isLayoutRTL();
                                if (!hasGaps) {
                                    this.mAnchorInfo.reset();
                                    onLayoutChildren(recycler, state, false);
                                }
                            }
                        }
                        detachAndScrapAttachedViews(recycler);
                        this.mLayoutState.mRecycle = false;
                        this.mLaidOutInvalidFullSpan = false;
                        updateMeasureSpecs(this.mSecondaryOrientation.getTotalSpace());
                        updateLayoutState(anchorInfo.mPosition, state);
                        if (anchorInfo.mLayoutFromEnd) {
                            setLayoutStateDirection(1);
                            fill(recycler, this.mLayoutState, state);
                            setLayoutStateDirection(-1);
                            this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
                            fill(recycler, this.mLayoutState, state);
                        } else {
                            setLayoutStateDirection(-1);
                            fill(recycler, this.mLayoutState, state);
                            setLayoutStateDirection(1);
                            this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
                            fill(recycler, this.mLayoutState, state);
                        }
                        repositionToWrapContentIfNecessary();
                        if (getChildCount() <= 0) {
                            if (this.mShouldReverseLayout) {
                                fixStartGap(recycler, state, true);
                                fixEndGap(recycler, state, false);
                            } else {
                                fixEndGap(recycler, state, true);
                                fixStartGap(recycler, state, false);
                            }
                        }
                        hasGaps = false;
                        if (!shouldCheckForGaps) {
                        }
                        if (!state.isPreLayout()) {
                            this.mAnchorInfo.reset();
                        }
                        this.mLastLayoutFromEnd = anchorInfo.mLayoutFromEnd;
                        this.mLastLayoutRTL = isLayoutRTL();
                        if (!hasGaps) {
                            this.mAnchorInfo.reset();
                            onLayoutChildren(recycler, state, false);
                        }
                    }
                }
                recalculateAnchor = true;
                if (!recalculateAnchor) {
                    anchorInfo.reset();
                    if (this.mPendingSavedState == null) {
                        resolveShouldLayoutReverse();
                        anchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
                    } else {
                        applyPendingSavedState(anchorInfo);
                    }
                    updateAnchorInfoForLayout(state, anchorInfo);
                    anchorInfo.mValid = true;
                }
                if (this.mPendingSavedState != null) {
                }
                if (getChildCount() > 0) {
                    savedState = this.mPendingSavedState;
                    if (anchorInfo.mInvalidateOffsets) {
                        if (recalculateAnchor) {
                            if (this.mAnchorInfo.mSpanReferenceLines != null) {
                                for (i = 0; i < this.mSpanCount; i++) {
                                    span = this.mSpans[i];
                                    span.clear();
                                    span.setLine(this.mAnchorInfo.mSpanReferenceLines[i]);
                                }
                            }
                        }
                        for (i = 0; i < this.mSpanCount; i++) {
                            this.mSpans[i].cacheReferenceLineAndClear(this.mShouldReverseLayout, anchorInfo.mOffset);
                        }
                        this.mAnchorInfo.saveSpanReferenceLines(this.mSpans);
                    } else {
                        for (i = 0; i < this.mSpanCount; i++) {
                            this.mSpans[i].clear();
                            if (anchorInfo.mOffset == Integer.MIN_VALUE) {
                                this.mSpans[i].setLine(anchorInfo.mOffset);
                            }
                        }
                    }
                    detachAndScrapAttachedViews(recycler);
                    this.mLayoutState.mRecycle = false;
                    this.mLaidOutInvalidFullSpan = false;
                    updateMeasureSpecs(this.mSecondaryOrientation.getTotalSpace());
                    updateLayoutState(anchorInfo.mPosition, state);
                    if (anchorInfo.mLayoutFromEnd) {
                        setLayoutStateDirection(-1);
                        fill(recycler, this.mLayoutState, state);
                        setLayoutStateDirection(1);
                        this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
                        fill(recycler, this.mLayoutState, state);
                    } else {
                        setLayoutStateDirection(1);
                        fill(recycler, this.mLayoutState, state);
                        setLayoutStateDirection(-1);
                        this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
                        fill(recycler, this.mLayoutState, state);
                    }
                    repositionToWrapContentIfNecessary();
                    if (getChildCount() <= 0) {
                        if (this.mShouldReverseLayout) {
                            fixEndGap(recycler, state, true);
                            fixStartGap(recycler, state, false);
                        } else {
                            fixStartGap(recycler, state, true);
                            fixEndGap(recycler, state, false);
                        }
                    }
                    hasGaps = false;
                    if (!shouldCheckForGaps) {
                    }
                    if (!state.isPreLayout()) {
                        this.mAnchorInfo.reset();
                    }
                    this.mLastLayoutFromEnd = anchorInfo.mLayoutFromEnd;
                    this.mLastLayoutRTL = isLayoutRTL();
                    if (!hasGaps) {
                        this.mAnchorInfo.reset();
                        onLayoutChildren(recycler, state, false);
                    }
                }
                detachAndScrapAttachedViews(recycler);
                this.mLayoutState.mRecycle = false;
                this.mLaidOutInvalidFullSpan = false;
                updateMeasureSpecs(this.mSecondaryOrientation.getTotalSpace());
                updateLayoutState(anchorInfo.mPosition, state);
                if (anchorInfo.mLayoutFromEnd) {
                    setLayoutStateDirection(1);
                    fill(recycler, this.mLayoutState, state);
                    setLayoutStateDirection(-1);
                    this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
                    fill(recycler, this.mLayoutState, state);
                } else {
                    setLayoutStateDirection(-1);
                    fill(recycler, this.mLayoutState, state);
                    setLayoutStateDirection(1);
                    this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
                    fill(recycler, this.mLayoutState, state);
                }
                repositionToWrapContentIfNecessary();
                if (getChildCount() <= 0) {
                    if (this.mShouldReverseLayout) {
                        fixStartGap(recycler, state, true);
                        fixEndGap(recycler, state, false);
                    } else {
                        fixEndGap(recycler, state, true);
                        fixStartGap(recycler, state, false);
                    }
                }
                hasGaps = false;
                if (!shouldCheckForGaps) {
                }
                if (!state.isPreLayout()) {
                    this.mAnchorInfo.reset();
                }
                this.mLastLayoutFromEnd = anchorInfo.mLayoutFromEnd;
                this.mLastLayoutRTL = isLayoutRTL();
                if (!hasGaps) {
                    this.mAnchorInfo.reset();
                    onLayoutChildren(recycler, state, false);
                }
            }
        }
        if (state.getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            anchorInfo.reset();
            return;
        }
        needToCheckForGaps = true;
        if (this.mPendingSavedState != null) {
            recalculateAnchor = false;
            if (!recalculateAnchor) {
                anchorInfo.reset();
                if (this.mPendingSavedState == null) {
                    applyPendingSavedState(anchorInfo);
                } else {
                    resolveShouldLayoutReverse();
                    anchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
                }
                updateAnchorInfoForLayout(state, anchorInfo);
                anchorInfo.mValid = true;
            }
            if (this.mPendingSavedState != null) {
            }
            if (getChildCount() > 0) {
                savedState = this.mPendingSavedState;
                if (anchorInfo.mInvalidateOffsets) {
                    for (i = 0; i < this.mSpanCount; i++) {
                        this.mSpans[i].clear();
                        if (anchorInfo.mOffset == Integer.MIN_VALUE) {
                            this.mSpans[i].setLine(anchorInfo.mOffset);
                        }
                    }
                } else {
                    if (recalculateAnchor) {
                        if (this.mAnchorInfo.mSpanReferenceLines != null) {
                            for (i = 0; i < this.mSpanCount; i++) {
                                span = this.mSpans[i];
                                span.clear();
                                span.setLine(this.mAnchorInfo.mSpanReferenceLines[i]);
                            }
                        }
                    }
                    for (i = 0; i < this.mSpanCount; i++) {
                        this.mSpans[i].cacheReferenceLineAndClear(this.mShouldReverseLayout, anchorInfo.mOffset);
                    }
                    this.mAnchorInfo.saveSpanReferenceLines(this.mSpans);
                }
                detachAndScrapAttachedViews(recycler);
                this.mLayoutState.mRecycle = false;
                this.mLaidOutInvalidFullSpan = false;
                updateMeasureSpecs(this.mSecondaryOrientation.getTotalSpace());
                updateLayoutState(anchorInfo.mPosition, state);
                if (anchorInfo.mLayoutFromEnd) {
                    setLayoutStateDirection(-1);
                    fill(recycler, this.mLayoutState, state);
                    setLayoutStateDirection(1);
                    this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
                    fill(recycler, this.mLayoutState, state);
                } else {
                    setLayoutStateDirection(1);
                    fill(recycler, this.mLayoutState, state);
                    setLayoutStateDirection(-1);
                    this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
                    fill(recycler, this.mLayoutState, state);
                }
                repositionToWrapContentIfNecessary();
                if (getChildCount() <= 0) {
                    if (this.mShouldReverseLayout) {
                        fixEndGap(recycler, state, true);
                        fixStartGap(recycler, state, false);
                    } else {
                        fixStartGap(recycler, state, true);
                        fixEndGap(recycler, state, false);
                    }
                }
                hasGaps = false;
                if (!shouldCheckForGaps) {
                }
                if (!state.isPreLayout()) {
                    this.mAnchorInfo.reset();
                }
                this.mLastLayoutFromEnd = anchorInfo.mLayoutFromEnd;
                this.mLastLayoutRTL = isLayoutRTL();
                if (!hasGaps) {
                    this.mAnchorInfo.reset();
                    onLayoutChildren(recycler, state, false);
                }
            }
            detachAndScrapAttachedViews(recycler);
            this.mLayoutState.mRecycle = false;
            this.mLaidOutInvalidFullSpan = false;
            updateMeasureSpecs(this.mSecondaryOrientation.getTotalSpace());
            updateLayoutState(anchorInfo.mPosition, state);
            if (anchorInfo.mLayoutFromEnd) {
                setLayoutStateDirection(1);
                fill(recycler, this.mLayoutState, state);
                setLayoutStateDirection(-1);
                this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
                fill(recycler, this.mLayoutState, state);
            } else {
                setLayoutStateDirection(-1);
                fill(recycler, this.mLayoutState, state);
                setLayoutStateDirection(1);
                this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
                fill(recycler, this.mLayoutState, state);
            }
            repositionToWrapContentIfNecessary();
            if (getChildCount() <= 0) {
                if (this.mShouldReverseLayout) {
                    fixStartGap(recycler, state, true);
                    fixEndGap(recycler, state, false);
                } else {
                    fixEndGap(recycler, state, true);
                    fixStartGap(recycler, state, false);
                }
            }
            hasGaps = false;
            if (!shouldCheckForGaps) {
            }
            if (!state.isPreLayout()) {
                this.mAnchorInfo.reset();
            }
            this.mLastLayoutFromEnd = anchorInfo.mLayoutFromEnd;
            this.mLastLayoutRTL = isLayoutRTL();
            if (!hasGaps) {
                this.mAnchorInfo.reset();
                onLayoutChildren(recycler, state, false);
            }
        }
        recalculateAnchor = true;
        if (!recalculateAnchor) {
            anchorInfo.reset();
            if (this.mPendingSavedState == null) {
                resolveShouldLayoutReverse();
                anchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
            } else {
                applyPendingSavedState(anchorInfo);
            }
            updateAnchorInfoForLayout(state, anchorInfo);
            anchorInfo.mValid = true;
        }
        if (this.mPendingSavedState != null) {
        }
        if (getChildCount() > 0) {
            savedState = this.mPendingSavedState;
            if (anchorInfo.mInvalidateOffsets) {
                if (recalculateAnchor) {
                    if (this.mAnchorInfo.mSpanReferenceLines != null) {
                        for (i = 0; i < this.mSpanCount; i++) {
                            span = this.mSpans[i];
                            span.clear();
                            span.setLine(this.mAnchorInfo.mSpanReferenceLines[i]);
                        }
                    }
                }
                for (i = 0; i < this.mSpanCount; i++) {
                    this.mSpans[i].cacheReferenceLineAndClear(this.mShouldReverseLayout, anchorInfo.mOffset);
                }
                this.mAnchorInfo.saveSpanReferenceLines(this.mSpans);
            } else {
                for (i = 0; i < this.mSpanCount; i++) {
                    this.mSpans[i].clear();
                    if (anchorInfo.mOffset == Integer.MIN_VALUE) {
                        this.mSpans[i].setLine(anchorInfo.mOffset);
                    }
                }
            }
            detachAndScrapAttachedViews(recycler);
            this.mLayoutState.mRecycle = false;
            this.mLaidOutInvalidFullSpan = false;
            updateMeasureSpecs(this.mSecondaryOrientation.getTotalSpace());
            updateLayoutState(anchorInfo.mPosition, state);
            if (anchorInfo.mLayoutFromEnd) {
                setLayoutStateDirection(-1);
                fill(recycler, this.mLayoutState, state);
                setLayoutStateDirection(1);
                this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
                fill(recycler, this.mLayoutState, state);
            } else {
                setLayoutStateDirection(1);
                fill(recycler, this.mLayoutState, state);
                setLayoutStateDirection(-1);
                this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
                fill(recycler, this.mLayoutState, state);
            }
            repositionToWrapContentIfNecessary();
            if (getChildCount() <= 0) {
                if (this.mShouldReverseLayout) {
                    fixEndGap(recycler, state, true);
                    fixStartGap(recycler, state, false);
                } else {
                    fixStartGap(recycler, state, true);
                    fixEndGap(recycler, state, false);
                }
            }
            hasGaps = false;
            if (!shouldCheckForGaps) {
            }
            if (!state.isPreLayout()) {
                this.mAnchorInfo.reset();
            }
            this.mLastLayoutFromEnd = anchorInfo.mLayoutFromEnd;
            this.mLastLayoutRTL = isLayoutRTL();
            if (!hasGaps) {
                this.mAnchorInfo.reset();
                onLayoutChildren(recycler, state, false);
            }
        }
        detachAndScrapAttachedViews(recycler);
        this.mLayoutState.mRecycle = false;
        this.mLaidOutInvalidFullSpan = false;
        updateMeasureSpecs(this.mSecondaryOrientation.getTotalSpace());
        updateLayoutState(anchorInfo.mPosition, state);
        if (anchorInfo.mLayoutFromEnd) {
            setLayoutStateDirection(1);
            fill(recycler, this.mLayoutState, state);
            setLayoutStateDirection(-1);
            this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
            fill(recycler, this.mLayoutState, state);
        } else {
            setLayoutStateDirection(-1);
            fill(recycler, this.mLayoutState, state);
            setLayoutStateDirection(1);
            this.mLayoutState.mCurrentPosition = anchorInfo.mPosition + this.mLayoutState.mItemDirection;
            fill(recycler, this.mLayoutState, state);
        }
        repositionToWrapContentIfNecessary();
        if (getChildCount() <= 0) {
            if (this.mShouldReverseLayout) {
                fixStartGap(recycler, state, true);
                fixEndGap(recycler, state, false);
            } else {
                fixEndGap(recycler, state, true);
                fixStartGap(recycler, state, false);
            }
        }
        hasGaps = false;
        if (!shouldCheckForGaps) {
        }
        if (!state.isPreLayout()) {
            this.mAnchorInfo.reset();
        }
        this.mLastLayoutFromEnd = anchorInfo.mLayoutFromEnd;
        this.mLastLayoutRTL = isLayoutRTL();
        if (!hasGaps) {
            this.mAnchorInfo.reset();
            onLayoutChildren(recycler, state, false);
        }
    }

    public void onLayoutCompleted(State state) {
        super.onLayoutCompleted(state);
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mPendingSavedState = null;
        this.mAnchorInfo.reset();
    }

    private void repositionToWrapContentIfNecessary() {
        if (this.mSecondaryOrientation.getMode() != 1073741824) {
            int i;
            float maxSize = 0.0f;
            int childCount = getChildCount();
            for (i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                float size = (float) this.mSecondaryOrientation.getDecoratedMeasurement(child);
                if (size >= maxSize) {
                    if (((LayoutParams) child.getLayoutParams()).isFullSpan()) {
                        size = (1.0f * size) / ((float) this.mSpanCount);
                    }
                    maxSize = Math.max(maxSize, size);
                }
            }
            i = this.mSizePerSpan;
            int desired = Math.round(((float) this.mSpanCount) * maxSize);
            if (this.mSecondaryOrientation.getMode() == Integer.MIN_VALUE) {
                desired = Math.min(desired, this.mSecondaryOrientation.getTotalSpace());
            }
            updateMeasureSpecs(desired);
            if (this.mSizePerSpan != i) {
                for (int i2 = 0; i2 < childCount; i2++) {
                    View child2 = getChildAt(i2);
                    LayoutParams lp = (LayoutParams) child2.getLayoutParams();
                    if (!lp.mFullSpan) {
                        if (isLayoutRTL() && this.mOrientation == 1) {
                            child2.offsetLeftAndRight(((-((this.mSpanCount - 1) - lp.mSpan.mIndex)) * this.mSizePerSpan) - ((-((this.mSpanCount - 1) - lp.mSpan.mIndex)) * i));
                        } else {
                            int newOffset = lp.mSpan.mIndex * this.mSizePerSpan;
                            int prevOffset = lp.mSpan.mIndex * i;
                            if (this.mOrientation == 1) {
                                child2.offsetLeftAndRight(newOffset - prevOffset);
                            } else {
                                child2.offsetTopAndBottom(newOffset - prevOffset);
                            }
                        }
                    }
                }
            }
        }
    }

    private void applyPendingSavedState(AnchorInfo anchorInfo) {
        if (this.mPendingSavedState.mSpanOffsetsSize > 0) {
            if (this.mPendingSavedState.mSpanOffsetsSize == this.mSpanCount) {
                for (int i = 0; i < this.mSpanCount; i++) {
                    this.mSpans[i].clear();
                    int line = this.mPendingSavedState.mSpanOffsets[i];
                    if (line != Integer.MIN_VALUE) {
                        if (this.mPendingSavedState.mAnchorLayoutFromEnd) {
                            line += this.mPrimaryOrientation.getEndAfterPadding();
                        } else {
                            line += this.mPrimaryOrientation.getStartAfterPadding();
                        }
                    }
                    this.mSpans[i].setLine(line);
                }
            } else {
                this.mPendingSavedState.invalidateSpanInfo();
                SavedState savedState = this.mPendingSavedState;
                savedState.mAnchorPosition = savedState.mVisibleAnchorPosition;
            }
        }
        this.mLastLayoutRTL = this.mPendingSavedState.mLastLayoutRTL;
        setReverseLayout(this.mPendingSavedState.mReverseLayout);
        resolveShouldLayoutReverse();
        if (this.mPendingSavedState.mAnchorPosition != -1) {
            this.mPendingScrollPosition = this.mPendingSavedState.mAnchorPosition;
            anchorInfo.mLayoutFromEnd = this.mPendingSavedState.mAnchorLayoutFromEnd;
        } else {
            anchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
        }
        if (this.mPendingSavedState.mSpanLookupSize > 1) {
            this.mLazySpanLookup.mData = this.mPendingSavedState.mSpanLookup;
            this.mLazySpanLookup.mFullSpanItems = this.mPendingSavedState.mFullSpanItems;
        }
    }

    void updateAnchorInfoForLayout(State state, AnchorInfo anchorInfo) {
        if (!updateAnchorFromPendingData(state, anchorInfo) && !updateAnchorFromChildren(state, anchorInfo)) {
            anchorInfo.assignCoordinateFromPadding();
            anchorInfo.mPosition = 0;
        }
    }

    private boolean updateAnchorFromChildren(State state, AnchorInfo anchorInfo) {
        int findLastReferenceChildPosition;
        if (this.mLastLayoutFromEnd) {
            findLastReferenceChildPosition = findLastReferenceChildPosition(state.getItemCount());
        } else {
            findLastReferenceChildPosition = findFirstReferenceChildPosition(state.getItemCount());
        }
        anchorInfo.mPosition = findLastReferenceChildPosition;
        anchorInfo.mOffset = Integer.MIN_VALUE;
        return true;
    }

    boolean updateAnchorFromPendingData(State state, AnchorInfo anchorInfo) {
        boolean z = false;
        if (!state.isPreLayout()) {
            int i = this.mPendingScrollPosition;
            if (i != -1) {
                if (i >= 0) {
                    if (i < state.getItemCount()) {
                        SavedState savedState = this.mPendingSavedState;
                        if (!(savedState == null || savedState.mAnchorPosition == -1)) {
                            if (this.mPendingSavedState.mSpanOffsetsSize >= 1) {
                                anchorInfo.mOffset = Integer.MIN_VALUE;
                                anchorInfo.mPosition = this.mPendingScrollPosition;
                                return true;
                            }
                        }
                        View child = findViewByPosition(this.mPendingScrollPosition);
                        int endAfterPadding;
                        if (child != null) {
                            int lastChildPosition;
                            if (this.mShouldReverseLayout) {
                                lastChildPosition = getLastChildPosition();
                            } else {
                                lastChildPosition = getFirstChildPosition();
                            }
                            anchorInfo.mPosition = lastChildPosition;
                            if (this.mPendingScrollPositionOffset != Integer.MIN_VALUE) {
                                if (anchorInfo.mLayoutFromEnd) {
                                    anchorInfo.mOffset = (this.mPrimaryOrientation.getEndAfterPadding() - this.mPendingScrollPositionOffset) - this.mPrimaryOrientation.getDecoratedEnd(child);
                                } else {
                                    anchorInfo.mOffset = (this.mPrimaryOrientation.getStartAfterPadding() + this.mPendingScrollPositionOffset) - this.mPrimaryOrientation.getDecoratedStart(child);
                                }
                                return true;
                            } else if (this.mPrimaryOrientation.getDecoratedMeasurement(child) > this.mPrimaryOrientation.getTotalSpace()) {
                                if (anchorInfo.mLayoutFromEnd) {
                                    endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding();
                                } else {
                                    endAfterPadding = this.mPrimaryOrientation.getStartAfterPadding();
                                }
                                anchorInfo.mOffset = endAfterPadding;
                                return true;
                            } else {
                                endAfterPadding = this.mPrimaryOrientation.getDecoratedStart(child) - this.mPrimaryOrientation.getStartAfterPadding();
                                if (endAfterPadding < 0) {
                                    anchorInfo.mOffset = -endAfterPadding;
                                    return true;
                                }
                                int endGap = this.mPrimaryOrientation.getEndAfterPadding() - this.mPrimaryOrientation.getDecoratedEnd(child);
                                if (endGap < 0) {
                                    anchorInfo.mOffset = endGap;
                                    return true;
                                }
                                anchorInfo.mOffset = Integer.MIN_VALUE;
                            }
                        } else {
                            anchorInfo.mPosition = this.mPendingScrollPosition;
                            endAfterPadding = this.mPendingScrollPositionOffset;
                            if (endAfterPadding == Integer.MIN_VALUE) {
                                if (calculateScrollDirectionForPosition(anchorInfo.mPosition) == 1) {
                                    z = true;
                                }
                                anchorInfo.mLayoutFromEnd = z;
                                anchorInfo.assignCoordinateFromPadding();
                            } else {
                                anchorInfo.assignCoordinateFromPadding(endAfterPadding);
                            }
                            anchorInfo.mInvalidateOffsets = true;
                        }
                        return true;
                    }
                }
                this.mPendingScrollPosition = -1;
                this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
                return false;
            }
        }
        return false;
    }

    void updateMeasureSpecs(int totalSpace) {
        this.mSizePerSpan = totalSpace / this.mSpanCount;
        this.mFullSizeSpec = MeasureSpec.makeMeasureSpec(totalSpace, this.mSecondaryOrientation.getMode());
    }

    public boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null;
    }

    public int computeHorizontalScrollOffset(State state) {
        return computeScrollOffset(state);
    }

    private int computeScrollOffset(State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        return ScrollbarHelper.computeScrollOffset(state, this.mPrimaryOrientation, findFirstVisibleItemClosestToStart(this.mSmoothScrollbarEnabled ^ 1), findFirstVisibleItemClosestToEnd(this.mSmoothScrollbarEnabled ^ 1), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
    }

    public int computeVerticalScrollOffset(State state) {
        return computeScrollOffset(state);
    }

    public int computeHorizontalScrollExtent(State state) {
        return computeScrollExtent(state);
    }

    private int computeScrollExtent(State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        return ScrollbarHelper.computeScrollExtent(state, this.mPrimaryOrientation, findFirstVisibleItemClosestToStart(this.mSmoothScrollbarEnabled ^ 1), findFirstVisibleItemClosestToEnd(this.mSmoothScrollbarEnabled ^ 1), this, this.mSmoothScrollbarEnabled);
    }

    public int computeVerticalScrollExtent(State state) {
        return computeScrollExtent(state);
    }

    public int computeHorizontalScrollRange(State state) {
        return computeScrollRange(state);
    }

    private int computeScrollRange(State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        return ScrollbarHelper.computeScrollRange(state, this.mPrimaryOrientation, findFirstVisibleItemClosestToStart(this.mSmoothScrollbarEnabled ^ 1), findFirstVisibleItemClosestToEnd(this.mSmoothScrollbarEnabled ^ 1), this, this.mSmoothScrollbarEnabled);
    }

    public int computeVerticalScrollRange(State state) {
        return computeScrollRange(state);
    }

    private void measureChildWithDecorationsAndMargin(View child, LayoutParams lp, boolean alreadyMeasured) {
        if (lp.mFullSpan) {
            if (this.mOrientation == 1) {
                measureChildWithDecorationsAndMargin(child, this.mFullSizeSpec, LayoutManager.getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom(), lp.height, true), alreadyMeasured);
            } else {
                measureChildWithDecorationsAndMargin(child, LayoutManager.getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight(), lp.width, true), this.mFullSizeSpec, alreadyMeasured);
            }
        } else if (this.mOrientation == 1) {
            measureChildWithDecorationsAndMargin(child, LayoutManager.getChildMeasureSpec(this.mSizePerSpan, getWidthMode(), 0, lp.width, false), LayoutManager.getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom(), lp.height, true), alreadyMeasured);
        } else {
            measureChildWithDecorationsAndMargin(child, LayoutManager.getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight(), lp.width, true), LayoutManager.getChildMeasureSpec(this.mSizePerSpan, getHeightMode(), 0, lp.height, false), alreadyMeasured);
        }
    }

    private void measureChildWithDecorationsAndMargin(View child, int widthSpec, int heightSpec, boolean alreadyMeasured) {
        boolean measure;
        calculateItemDecorationsForChild(child, this.mTmpRect);
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        widthSpec = updateSpecWithExtra(widthSpec, lp.leftMargin + this.mTmpRect.left, lp.rightMargin + this.mTmpRect.right);
        heightSpec = updateSpecWithExtra(heightSpec, lp.topMargin + this.mTmpRect.top, lp.bottomMargin + this.mTmpRect.bottom);
        if (alreadyMeasured) {
            measure = shouldReMeasureChild(child, widthSpec, heightSpec, lp);
        } else {
            measure = shouldMeasureChild(child, widthSpec, heightSpec, lp);
        }
        if (measure) {
            child.measure(widthSpec, heightSpec);
        }
    }

    private int updateSpecWithExtra(int spec, int startInset, int endInset) {
        if (startInset == 0 && endInset == 0) {
            return spec;
        }
        int mode = MeasureSpec.getMode(spec);
        if (mode != Integer.MIN_VALUE) {
            if (mode != 1073741824) {
                return spec;
            }
        }
        return MeasureSpec.makeMeasureSpec(Math.max(0, (MeasureSpec.getSize(spec) - startInset) - endInset), mode);
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            this.mPendingSavedState = (SavedState) state;
            requestLayout();
        }
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = this.mPendingSavedState;
        if (savedState != null) {
            return new SavedState(savedState);
        }
        savedState = new SavedState();
        savedState.mReverseLayout = this.mReverseLayout;
        savedState.mAnchorLayoutFromEnd = this.mLastLayoutFromEnd;
        savedState.mLastLayoutRTL = this.mLastLayoutRTL;
        LazySpanLookup lazySpanLookup = this.mLazySpanLookup;
        if (lazySpanLookup == null || lazySpanLookup.mData == null) {
            savedState.mSpanLookupSize = 0;
        } else {
            savedState.mSpanLookup = this.mLazySpanLookup.mData;
            savedState.mSpanLookupSize = savedState.mSpanLookup.length;
            savedState.mFullSpanItems = this.mLazySpanLookup.mFullSpanItems;
        }
        if (getChildCount() > 0) {
            int lastChildPosition;
            if (this.mLastLayoutFromEnd) {
                lastChildPosition = getLastChildPosition();
            } else {
                lastChildPosition = getFirstChildPosition();
            }
            savedState.mAnchorPosition = lastChildPosition;
            savedState.mVisibleAnchorPosition = findFirstVisibleItemPositionInt();
            lastChildPosition = this.mSpanCount;
            savedState.mSpanOffsetsSize = lastChildPosition;
            savedState.mSpanOffsets = new int[lastChildPosition];
            for (lastChildPosition = 0; lastChildPosition < this.mSpanCount; lastChildPosition++) {
                int line;
                if (this.mLastLayoutFromEnd) {
                    line = this.mSpans[lastChildPosition].getEndLine(Integer.MIN_VALUE);
                    if (line != Integer.MIN_VALUE) {
                        line -= this.mPrimaryOrientation.getEndAfterPadding();
                    }
                } else {
                    line = this.mSpans[lastChildPosition].getStartLine(Integer.MIN_VALUE);
                    if (line != Integer.MIN_VALUE) {
                        line -= this.mPrimaryOrientation.getStartAfterPadding();
                    }
                }
                savedState.mSpanOffsets[lastChildPosition] = line;
            }
        } else {
            savedState.mAnchorPosition = -1;
            savedState.mVisibleAnchorPosition = -1;
            savedState.mSpanOffsetsSize = 0;
        }
        return savedState;
    }

    public void onInitializeAccessibilityNodeInfoForItem(Recycler recycler, State state, View host, AccessibilityNodeInfoCompat info) {
        android.view.ViewGroup.LayoutParams lp = host.getLayoutParams();
        if (lp instanceof LayoutParams) {
            LayoutParams sglp = (LayoutParams) lp;
            if (this.mOrientation == 0) {
                info.setCollectionItemInfo(CollectionItemInfoCompat.obtain(sglp.getSpanIndex(), sglp.mFullSpan ? this.mSpanCount : 1, -1, -1, sglp.mFullSpan, false));
            } else {
                info.setCollectionItemInfo(CollectionItemInfoCompat.obtain(-1, -1, sglp.getSpanIndex(), sglp.mFullSpan ? this.mSpanCount : 1, sglp.mFullSpan, false));
            }
            return;
        }
        super.onInitializeAccessibilityNodeInfoForItem(host, info);
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (getChildCount() > 0) {
            View start = findFirstVisibleItemClosestToStart(false);
            View end = findFirstVisibleItemClosestToEnd(false);
            if (start != null) {
                if (end != null) {
                    int startPos = getPosition(start);
                    int endPos = getPosition(end);
                    if (startPos < endPos) {
                        event.setFromIndex(startPos);
                        event.setToIndex(endPos);
                    } else {
                        event.setFromIndex(endPos);
                        event.setToIndex(startPos);
                    }
                }
            }
        }
    }

    int findFirstVisibleItemPositionInt() {
        View first;
        if (this.mShouldReverseLayout) {
            first = findFirstVisibleItemClosestToEnd(true);
        } else {
            first = findFirstVisibleItemClosestToStart(true);
        }
        return first == null ? -1 : getPosition(first);
    }

    public int getRowCountForAccessibility(Recycler recycler, State state) {
        if (this.mOrientation == 0) {
            return this.mSpanCount;
        }
        return super.getRowCountForAccessibility(recycler, state);
    }

    public int getColumnCountForAccessibility(Recycler recycler, State state) {
        if (this.mOrientation == 1) {
            return this.mSpanCount;
        }
        return super.getColumnCountForAccessibility(recycler, state);
    }

    View findFirstVisibleItemClosestToStart(boolean fullyVisible) {
        int boundsStart = this.mPrimaryOrientation.getStartAfterPadding();
        int boundsEnd = this.mPrimaryOrientation.getEndAfterPadding();
        int limit = getChildCount();
        View partiallyVisible = null;
        for (int i = 0; i < limit; i++) {
            View child = getChildAt(i);
            int childStart = this.mPrimaryOrientation.getDecoratedStart(child);
            if (this.mPrimaryOrientation.getDecoratedEnd(child) > boundsStart) {
                if (childStart < boundsEnd) {
                    if (childStart < boundsStart) {
                        if (fullyVisible) {
                            if (partiallyVisible == null) {
                                partiallyVisible = child;
                            }
                        }
                    }
                    return child;
                }
            }
        }
        return partiallyVisible;
    }

    View findFirstVisibleItemClosestToEnd(boolean fullyVisible) {
        int boundsStart = this.mPrimaryOrientation.getStartAfterPadding();
        int boundsEnd = this.mPrimaryOrientation.getEndAfterPadding();
        View partiallyVisible = null;
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            int childStart = this.mPrimaryOrientation.getDecoratedStart(child);
            int childEnd = this.mPrimaryOrientation.getDecoratedEnd(child);
            if (childEnd > boundsStart) {
                if (childStart < boundsEnd) {
                    if (childEnd > boundsEnd) {
                        if (fullyVisible) {
                            if (partiallyVisible == null) {
                                partiallyVisible = child;
                            }
                        }
                    }
                    return child;
                }
            }
        }
        return partiallyVisible;
    }

    private void fixEndGap(Recycler recycler, State state, boolean canOffsetChildren) {
        int maxEndLine = getMaxEnd(Integer.MIN_VALUE);
        if (maxEndLine != Integer.MIN_VALUE) {
            int gap = this.mPrimaryOrientation.getEndAfterPadding() - maxEndLine;
            if (gap > 0) {
                gap -= -scrollBy(-gap, recycler, state);
                if (canOffsetChildren && gap > 0) {
                    this.mPrimaryOrientation.offsetChildren(gap);
                }
            }
        }
    }

    private void fixStartGap(Recycler recycler, State state, boolean canOffsetChildren) {
        int minStartLine = getMinStart(Integer.MAX_VALUE);
        if (minStartLine != Integer.MAX_VALUE) {
            int gap = minStartLine - this.mPrimaryOrientation.getStartAfterPadding();
            if (gap > 0) {
                gap -= scrollBy(gap, recycler, state);
                if (canOffsetChildren && gap > 0) {
                    this.mPrimaryOrientation.offsetChildren(-gap);
                }
            }
        }
    }

    private void updateLayoutState(int anchorPosition, State state) {
        LayoutState layoutState = this.mLayoutState;
        boolean z = false;
        layoutState.mAvailable = 0;
        layoutState.mCurrentPosition = anchorPosition;
        int startExtra = 0;
        int endExtra = 0;
        if (isSmoothScrolling()) {
            int targetPos = state.getTargetScrollPosition();
            if (targetPos != -1) {
                if (this.mShouldReverseLayout == (targetPos < anchorPosition)) {
                    endExtra = this.mPrimaryOrientation.getTotalSpace();
                } else {
                    startExtra = this.mPrimaryOrientation.getTotalSpace();
                }
            }
        }
        if (getClipToPadding()) {
            this.mLayoutState.mStartLine = this.mPrimaryOrientation.getStartAfterPadding() - startExtra;
            this.mLayoutState.mEndLine = this.mPrimaryOrientation.getEndAfterPadding() + endExtra;
        } else {
            this.mLayoutState.mEndLine = this.mPrimaryOrientation.getEnd() + endExtra;
            this.mLayoutState.mStartLine = -startExtra;
        }
        LayoutState layoutState2 = this.mLayoutState;
        layoutState2.mStopInFocusable = false;
        layoutState2.mRecycle = true;
        if (this.mPrimaryOrientation.getMode() == 0) {
            if (this.mPrimaryOrientation.getEnd() == 0) {
                z = true;
                layoutState2.mInfinite = z;
            }
        }
        layoutState2.mInfinite = z;
    }

    private void setLayoutStateDirection(int direction) {
        LayoutState layoutState = this.mLayoutState;
        layoutState.mLayoutDirection = direction;
        int i = 1;
        if (this.mShouldReverseLayout != (direction == -1)) {
            i = -1;
        }
        layoutState.mItemDirection = i;
    }

    public void offsetChildrenHorizontal(int dx) {
        super.offsetChildrenHorizontal(dx);
        for (int i = 0; i < this.mSpanCount; i++) {
            this.mSpans[i].onOffset(dx);
        }
    }

    public void offsetChildrenVertical(int dy) {
        super.offsetChildrenVertical(dy);
        for (int i = 0; i < this.mSpanCount; i++) {
            this.mSpans[i].onOffset(dy);
        }
    }

    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        handleUpdate(positionStart, itemCount, 2);
    }

    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        handleUpdate(positionStart, itemCount, 1);
    }

    public void onItemsChanged(RecyclerView recyclerView) {
        this.mLazySpanLookup.clear();
        requestLayout();
    }

    public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
        handleUpdate(from, to, 8);
    }

    public void onItemsUpdated(RecyclerView recyclerView, int positionStart, int itemCount, Object payload) {
        handleUpdate(positionStart, itemCount, 4);
    }

    private void handleUpdate(int positionStart, int itemCountOrToPosition, int cmd) {
        int affectedRangeStart;
        int affectedRangeEnd;
        int minPosition = this.mShouldReverseLayout ? getLastChildPosition() : getFirstChildPosition();
        if (cmd != 8) {
            affectedRangeStart = positionStart;
            affectedRangeEnd = positionStart + itemCountOrToPosition;
        } else if (positionStart < itemCountOrToPosition) {
            affectedRangeEnd = itemCountOrToPosition + 1;
            affectedRangeStart = positionStart;
        } else {
            affectedRangeEnd = positionStart + 1;
            affectedRangeStart = itemCountOrToPosition;
        }
        this.mLazySpanLookup.invalidateAfter(affectedRangeStart);
        if (cmd != 8) {
            switch (cmd) {
                case 1:
                    this.mLazySpanLookup.offsetForAddition(positionStart, itemCountOrToPosition);
                    break;
                case 2:
                    this.mLazySpanLookup.offsetForRemoval(positionStart, itemCountOrToPosition);
                    break;
                default:
                    break;
            }
        }
        this.mLazySpanLookup.offsetForRemoval(positionStart, 1);
        this.mLazySpanLookup.offsetForAddition(itemCountOrToPosition, 1);
        if (affectedRangeEnd > minPosition) {
            if (affectedRangeStart <= (this.mShouldReverseLayout ? getFirstChildPosition() : getLastChildPosition())) {
                requestLayout();
            }
        }
    }

    private int fill(Recycler recycler, LayoutState layoutState, State state) {
        int targetLine;
        int endAfterPadding;
        int i;
        int start;
        Recycler recycler2 = recycler;
        LayoutState layoutState2 = layoutState;
        boolean z = false;
        boolean z2 = true;
        this.mRemainingSpans.set(0, this.mSpanCount, true);
        if (this.mLayoutState.mInfinite) {
            if (layoutState2.mLayoutDirection == 1) {
                targetLine = Integer.MAX_VALUE;
            } else {
                targetLine = Integer.MIN_VALUE;
            }
        } else if (layoutState2.mLayoutDirection == 1) {
            targetLine = layoutState2.mEndLine + layoutState2.mAvailable;
        } else {
            targetLine = layoutState2.mStartLine - layoutState2.mAvailable;
        }
        updateAllRemainingSpans(layoutState2.mLayoutDirection, targetLine);
        if (r6.mShouldReverseLayout) {
            endAfterPadding = r6.mPrimaryOrientation.getEndAfterPadding();
        } else {
            endAfterPadding = r6.mPrimaryOrientation.getStartAfterPadding();
        }
        int defaultNewViewLine = endAfterPadding;
        boolean added = false;
        while (layoutState.hasMore(state)) {
            Span currentSpan;
            int start2;
            int end;
            int otherEnd;
            int otherStart;
            Span currentSpan2;
            if (!r6.mLayoutState.mInfinite) {
                if (r6.mRemainingSpans.isEmpty()) {
                    i = 0;
                    break;
                }
            }
            View view = layoutState2.next(recycler2);
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            int position = lp.getViewLayoutPosition();
            int spanIndex = r6.mLazySpanLookup.getSpan(position);
            boolean assignSpan = spanIndex == -1;
            if (assignSpan) {
                Span currentSpan3 = lp.mFullSpan ? r6.mSpans[z] : getNextSpan(layoutState2);
                r6.mLazySpanLookup.setSpan(position, currentSpan3);
                currentSpan = currentSpan3;
            } else {
                currentSpan = r6.mSpans[spanIndex];
            }
            lp.mSpan = currentSpan;
            if (layoutState2.mLayoutDirection == z2) {
                addView(view);
            } else {
                addView(view, z);
            }
            measureChildWithDecorationsAndMargin(view, lp, z);
            if (layoutState2.mLayoutDirection == z2) {
                if (lp.mFullSpan) {
                    endAfterPadding = getMaxEnd(defaultNewViewLine);
                } else {
                    endAfterPadding = currentSpan.getEndLine(defaultNewViewLine);
                }
                i = r6.mPrimaryOrientation.getDecoratedMeasurement(view) + endAfterPadding;
                if (assignSpan && lp.mFullSpan) {
                    FullSpanItem fullSpanItem = createFullSpanItemFromEnd(endAfterPadding);
                    fullSpanItem.mGapDir = -1;
                    fullSpanItem.mPosition = position;
                    r6.mLazySpanLookup.addFullSpanItem(fullSpanItem);
                }
                start2 = endAfterPadding;
                end = i;
            } else {
                if (lp.mFullSpan) {
                    endAfterPadding = getMinStart(defaultNewViewLine);
                } else {
                    endAfterPadding = currentSpan.getStartLine(defaultNewViewLine);
                }
                start = endAfterPadding - r6.mPrimaryOrientation.getDecoratedMeasurement(view);
                if (assignSpan && lp.mFullSpan) {
                    FullSpanItem fullSpanItem2 = createFullSpanItemFromStart(endAfterPadding);
                    fullSpanItem2.mGapDir = z2;
                    fullSpanItem2.mPosition = position;
                    r6.mLazySpanLookup.addFullSpanItem(fullSpanItem2);
                }
                end = endAfterPadding;
                start2 = start;
            }
            if (lp.mFullSpan && layoutState2.mItemDirection == -1) {
                if (assignSpan) {
                    r6.mLaidOutInvalidFullSpan = z2;
                } else {
                    boolean hasInvalidGap;
                    if (layoutState2.mLayoutDirection == z2) {
                        hasInvalidGap = areAllEndsEqual() ^ z2;
                    } else {
                        hasInvalidGap = areAllStartsEqual() ^ z2;
                    }
                    if (hasInvalidGap) {
                        FullSpanItem fullSpanItem3 = r6.mLazySpanLookup.getFullSpanItem(position);
                        if (fullSpanItem3 != null) {
                            fullSpanItem3.mHasUnwantedGapAfter = z2;
                        }
                        r6.mLaidOutInvalidFullSpan = z2;
                    }
                }
            }
            attachViewToSpans(view, lp, layoutState2);
            if (isLayoutRTL() && r6.mOrientation == z2) {
                if (lp.mFullSpan) {
                    endAfterPadding = r6.mSecondaryOrientation.getEndAfterPadding();
                } else {
                    endAfterPadding = r6.mSecondaryOrientation.getEndAfterPadding() - (((r6.mSpanCount - z2) - currentSpan.mIndex) * r6.mSizePerSpan);
                }
                otherEnd = endAfterPadding;
                otherStart = endAfterPadding - r6.mSecondaryOrientation.getDecoratedMeasurement(view);
            } else {
                if (lp.mFullSpan) {
                    endAfterPadding = r6.mSecondaryOrientation.getStartAfterPadding();
                } else {
                    endAfterPadding = (currentSpan.mIndex * r6.mSizePerSpan) + r6.mSecondaryOrientation.getStartAfterPadding();
                }
                otherStart = endAfterPadding;
                otherEnd = r6.mSecondaryOrientation.getDecoratedMeasurement(view) + endAfterPadding;
            }
            if (r6.mOrientation == z2) {
                currentSpan2 = currentSpan;
                layoutDecoratedWithMargins(view, otherStart, start2, otherEnd, end);
            } else {
                currentSpan2 = currentSpan;
                int i2 = spanIndex;
                int i3 = position;
                layoutDecoratedWithMargins(view, start2, otherStart, end, otherEnd);
            }
            if (lp.mFullSpan) {
                updateAllRemainingSpans(r6.mLayoutState.mLayoutDirection, targetLine);
            } else {
                updateRemainingSpans(currentSpan2, r6.mLayoutState.mLayoutDirection, targetLine);
            }
            recycle(recycler2, r6.mLayoutState);
            if (r6.mLayoutState.mStopInFocusable && view.hasFocusable()) {
                if (lp.mFullSpan) {
                    r6.mRemainingSpans.clear();
                } else {
                    r6.mRemainingSpans.set(currentSpan2.mIndex, false);
                }
            }
            added = true;
            z = false;
            z2 = true;
        }
        i = 0;
        if (!added) {
            recycle(recycler2, r6.mLayoutState);
        }
        if (r6.mLayoutState.mLayoutDirection == -1) {
            start = r6.mPrimaryOrientation.getStartAfterPadding() - getMinStart(r6.mPrimaryOrientation.getStartAfterPadding());
        } else {
            start = getMaxEnd(r6.mPrimaryOrientation.getEndAfterPadding()) - r6.mPrimaryOrientation.getEndAfterPadding();
        }
        return start > 0 ? Math.min(layoutState2.mAvailable, start) : i;
    }

    private FullSpanItem createFullSpanItemFromEnd(int newItemTop) {
        FullSpanItem fsi = new FullSpanItem();
        fsi.mGapPerSpan = new int[this.mSpanCount];
        for (int i = 0; i < this.mSpanCount; i++) {
            fsi.mGapPerSpan[i] = newItemTop - this.mSpans[i].getEndLine(newItemTop);
        }
        return fsi;
    }

    private FullSpanItem createFullSpanItemFromStart(int newItemBottom) {
        FullSpanItem fsi = new FullSpanItem();
        fsi.mGapPerSpan = new int[this.mSpanCount];
        for (int i = 0; i < this.mSpanCount; i++) {
            fsi.mGapPerSpan[i] = this.mSpans[i].getStartLine(newItemBottom) - newItemBottom;
        }
        return fsi;
    }

    private void attachViewToSpans(View view, LayoutParams lp, LayoutState layoutState) {
        if (layoutState.mLayoutDirection == 1) {
            if (lp.mFullSpan) {
                appendViewToAllSpans(view);
            } else {
                lp.mSpan.appendToSpan(view);
            }
        } else if (lp.mFullSpan) {
            prependViewToAllSpans(view);
        } else {
            lp.mSpan.prependToSpan(view);
        }
    }

    private void recycle(Recycler recycler, LayoutState layoutState) {
        if (layoutState.mRecycle) {
            if (!layoutState.mInfinite) {
                if (layoutState.mAvailable == 0) {
                    if (layoutState.mLayoutDirection == -1) {
                        recycleFromEnd(recycler, layoutState.mEndLine);
                    } else {
                        recycleFromStart(recycler, layoutState.mStartLine);
                    }
                } else if (layoutState.mLayoutDirection == -1) {
                    scrolled = layoutState.mStartLine - getMaxStart(layoutState.mStartLine);
                    if (scrolled < 0) {
                        line = layoutState.mEndLine;
                    } else {
                        line = layoutState.mEndLine - Math.min(scrolled, layoutState.mAvailable);
                    }
                    recycleFromEnd(recycler, line);
                } else {
                    scrolled = getMinEnd(layoutState.mEndLine) - layoutState.mEndLine;
                    if (scrolled < 0) {
                        line = layoutState.mStartLine;
                    } else {
                        line = layoutState.mStartLine + Math.min(scrolled, layoutState.mAvailable);
                    }
                    recycleFromStart(recycler, line);
                }
            }
        }
    }

    private void appendViewToAllSpans(View view) {
        for (int i = this.mSpanCount - 1; i >= 0; i--) {
            this.mSpans[i].appendToSpan(view);
        }
    }

    private void prependViewToAllSpans(View view) {
        for (int i = this.mSpanCount - 1; i >= 0; i--) {
            this.mSpans[i].prependToSpan(view);
        }
    }

    private void updateAllRemainingSpans(int layoutDir, int targetLine) {
        for (int i = 0; i < this.mSpanCount; i++) {
            if (!this.mSpans[i].mViews.isEmpty()) {
                updateRemainingSpans(this.mSpans[i], layoutDir, targetLine);
            }
        }
    }

    private void updateRemainingSpans(Span span, int layoutDir, int targetLine) {
        int deletedSize = span.getDeletedSize();
        if (layoutDir == -1) {
            if (span.getStartLine() + deletedSize <= targetLine) {
                this.mRemainingSpans.set(span.mIndex, false);
            }
        } else if (span.getEndLine() - deletedSize >= targetLine) {
            this.mRemainingSpans.set(span.mIndex, false);
        }
    }

    private int getMaxStart(int def) {
        int maxStart = this.mSpans[0].getStartLine(def);
        for (int i = 1; i < this.mSpanCount; i++) {
            int spanStart = this.mSpans[i].getStartLine(def);
            if (spanStart > maxStart) {
                maxStart = spanStart;
            }
        }
        return maxStart;
    }

    private int getMinStart(int def) {
        int minStart = this.mSpans[0].getStartLine(def);
        for (int i = 1; i < this.mSpanCount; i++) {
            int spanStart = this.mSpans[i].getStartLine(def);
            if (spanStart < minStart) {
                minStart = spanStart;
            }
        }
        return minStart;
    }

    boolean areAllEndsEqual() {
        int end = this.mSpans[0].getEndLine(Integer.MIN_VALUE);
        for (int i = 1; i < this.mSpanCount; i++) {
            if (this.mSpans[i].getEndLine(Integer.MIN_VALUE) != end) {
                return false;
            }
        }
        return true;
    }

    boolean areAllStartsEqual() {
        int start = this.mSpans[0].getStartLine(Integer.MIN_VALUE);
        for (int i = 1; i < this.mSpanCount; i++) {
            if (this.mSpans[i].getStartLine(Integer.MIN_VALUE) != start) {
                return false;
            }
        }
        return true;
    }

    private int getMaxEnd(int def) {
        int maxEnd = this.mSpans[0].getEndLine(def);
        for (int i = 1; i < this.mSpanCount; i++) {
            int spanEnd = this.mSpans[i].getEndLine(def);
            if (spanEnd > maxEnd) {
                maxEnd = spanEnd;
            }
        }
        return maxEnd;
    }

    private int getMinEnd(int def) {
        int minEnd = this.mSpans[0].getEndLine(def);
        for (int i = 1; i < this.mSpanCount; i++) {
            int spanEnd = this.mSpans[i].getEndLine(def);
            if (spanEnd < minEnd) {
                minEnd = spanEnd;
            }
        }
        return minEnd;
    }

    private void recycleFromStart(Recycler recycler, int line) {
        while (getChildCount() > 0) {
            View child = getChildAt(null);
            if (this.mPrimaryOrientation.getDecoratedEnd(child) <= line) {
                if (this.mPrimaryOrientation.getTransformedEndWithDecoration(child) <= line) {
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    if (lp.mFullSpan) {
                        int j = 0;
                        while (j < this.mSpanCount) {
                            if (this.mSpans[j].mViews.size() != 1) {
                                j++;
                            } else {
                                return;
                            }
                        }
                        for (j = 0; j < this.mSpanCount; j++) {
                            this.mSpans[j].popStart();
                        }
                    } else if (lp.mSpan.mViews.size() != 1) {
                        lp.mSpan.popStart();
                    } else {
                        return;
                    }
                    removeAndRecycleView(child, recycler);
                }
            }
            return;
        }
    }

    private void recycleFromEnd(Recycler recycler, int line) {
        int i = getChildCount() - 1;
        while (i >= 0) {
            View child = getChildAt(i);
            if (this.mPrimaryOrientation.getDecoratedStart(child) >= line) {
                if (this.mPrimaryOrientation.getTransformedStartWithDecoration(child) >= line) {
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    if (lp.mFullSpan) {
                        int j = 0;
                        while (j < this.mSpanCount) {
                            if (this.mSpans[j].mViews.size() != 1) {
                                j++;
                            } else {
                                return;
                            }
                        }
                        for (j = 0; j < this.mSpanCount; j++) {
                            this.mSpans[j].popEnd();
                        }
                    } else if (lp.mSpan.mViews.size() != 1) {
                        lp.mSpan.popEnd();
                    } else {
                        return;
                    }
                    removeAndRecycleView(child, recycler);
                    i--;
                }
            }
            return;
        }
    }

    private boolean preferLastSpan(int layoutDir) {
        boolean z = true;
        if (this.mOrientation == 0) {
            if ((layoutDir == -1) == this.mShouldReverseLayout) {
                z = false;
            }
            return z;
        }
        if (((layoutDir == -1) == this.mShouldReverseLayout) != isLayoutRTL()) {
            z = false;
        }
        return z;
    }

    private Span getNextSpan(LayoutState layoutState) {
        int startIndex;
        int endIndex;
        int diff;
        if (preferLastSpan(layoutState.mLayoutDirection)) {
            startIndex = this.mSpanCount - 1;
            endIndex = -1;
            diff = -1;
        } else {
            startIndex = 0;
            endIndex = this.mSpanCount;
            diff = 1;
        }
        Span min;
        int minLine;
        int defaultLine;
        int i;
        Span other;
        int otherLine;
        if (layoutState.mLayoutDirection == 1) {
            min = null;
            minLine = Integer.MAX_VALUE;
            defaultLine = this.mPrimaryOrientation.getStartAfterPadding();
            for (i = startIndex; i != endIndex; i += diff) {
                other = this.mSpans[i];
                otherLine = other.getEndLine(defaultLine);
                if (otherLine < minLine) {
                    min = other;
                    minLine = otherLine;
                }
            }
            return min;
        }
        min = null;
        minLine = Integer.MIN_VALUE;
        defaultLine = this.mPrimaryOrientation.getEndAfterPadding();
        for (i = startIndex; i != endIndex; i += diff) {
            other = this.mSpans[i];
            otherLine = other.getStartLine(defaultLine);
            if (otherLine > minLine) {
                min = other;
                minLine = otherLine;
            }
        }
        return min;
    }

    public boolean canScrollVertically() {
        return this.mOrientation == 1;
    }

    public boolean canScrollHorizontally() {
        return this.mOrientation == 0;
    }

    public int scrollHorizontallyBy(int dx, Recycler recycler, State state) {
        return scrollBy(dx, recycler, state);
    }

    public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
        return scrollBy(dy, recycler, state);
    }

    private int calculateScrollDirectionForPosition(int position) {
        int i = -1;
        if (getChildCount() == 0) {
            if (this.mShouldReverseLayout) {
                i = 1;
            }
            return i;
        }
        if ((position < getFirstChildPosition()) == this.mShouldReverseLayout) {
            i = 1;
        }
        return i;
    }

    public PointF computeScrollVectorForPosition(int targetPosition) {
        int direction = calculateScrollDirectionForPosition(targetPosition);
        PointF outVector = new PointF();
        if (direction == 0) {
            return null;
        }
        if (this.mOrientation == 0) {
            outVector.x = (float) direction;
            outVector.y = 0.0f;
        } else {
            outVector.x = 0.0f;
            outVector.y = (float) direction;
        }
        return outVector;
    }

    public void smoothScrollToPosition(RecyclerView recyclerView, State state, int position) {
        LinearSmoothScroller scroller = new LinearSmoothScroller(recyclerView.getContext());
        scroller.setTargetPosition(position);
        startSmoothScroll(scroller);
    }

    public void scrollToPosition(int position) {
        SavedState savedState = this.mPendingSavedState;
        if (savedState != null && savedState.mAnchorPosition != position) {
            this.mPendingSavedState.invalidateAnchorPositionInfo();
        }
        this.mPendingScrollPosition = position;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        requestLayout();
    }

    public void scrollToPositionWithOffset(int position, int offset) {
        SavedState savedState = this.mPendingSavedState;
        if (savedState != null) {
            savedState.invalidateAnchorPositionInfo();
        }
        this.mPendingScrollPosition = position;
        this.mPendingScrollPositionOffset = offset;
        requestLayout();
    }

    @RestrictTo({Scope.LIBRARY})
    public void collectAdjacentPrefetchPositions(int dx, int dy, State state, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int delta = this.mOrientation == 0 ? dx : dy;
        if (getChildCount() != 0) {
            if (delta != 0) {
                int itemPrefetchCount;
                int i;
                int distance;
                LayoutState layoutState;
                prepareLayoutStateForDelta(delta, state);
                int[] iArr = this.mPrefetchDistances;
                if (iArr != null) {
                    if (iArr.length >= this.mSpanCount) {
                        itemPrefetchCount = 0;
                        for (i = 0; i < this.mSpanCount; i++) {
                            if (this.mLayoutState.mItemDirection != -1) {
                                distance = this.mLayoutState.mStartLine - this.mSpans[i].getStartLine(this.mLayoutState.mStartLine);
                            } else {
                                distance = this.mSpans[i].getEndLine(this.mLayoutState.mEndLine) - this.mLayoutState.mEndLine;
                            }
                            if (distance >= 0) {
                                this.mPrefetchDistances[itemPrefetchCount] = distance;
                                itemPrefetchCount++;
                            }
                        }
                        Arrays.sort(this.mPrefetchDistances, 0, itemPrefetchCount);
                        for (i = 0; i < itemPrefetchCount && this.mLayoutState.hasMore(state); i++) {
                            layoutPrefetchRegistry.addPosition(this.mLayoutState.mCurrentPosition, this.mPrefetchDistances[i]);
                            layoutState = this.mLayoutState;
                            layoutState.mCurrentPosition += this.mLayoutState.mItemDirection;
                        }
                    }
                }
                this.mPrefetchDistances = new int[this.mSpanCount];
                itemPrefetchCount = 0;
                for (i = 0; i < this.mSpanCount; i++) {
                    if (this.mLayoutState.mItemDirection != -1) {
                        distance = this.mSpans[i].getEndLine(this.mLayoutState.mEndLine) - this.mLayoutState.mEndLine;
                    } else {
                        distance = this.mLayoutState.mStartLine - this.mSpans[i].getStartLine(this.mLayoutState.mStartLine);
                    }
                    if (distance >= 0) {
                        this.mPrefetchDistances[itemPrefetchCount] = distance;
                        itemPrefetchCount++;
                    }
                }
                Arrays.sort(this.mPrefetchDistances, 0, itemPrefetchCount);
                for (i = 0; i < itemPrefetchCount; i++) {
                    layoutPrefetchRegistry.addPosition(this.mLayoutState.mCurrentPosition, this.mPrefetchDistances[i]);
                    layoutState = this.mLayoutState;
                    layoutState.mCurrentPosition += this.mLayoutState.mItemDirection;
                }
            }
        }
    }

    void prepareLayoutStateForDelta(int delta, State state) {
        int layoutDir;
        int referenceChildPosition;
        if (delta > 0) {
            layoutDir = 1;
            referenceChildPosition = getLastChildPosition();
        } else {
            layoutDir = -1;
            referenceChildPosition = getFirstChildPosition();
        }
        this.mLayoutState.mRecycle = true;
        updateLayoutState(referenceChildPosition, state);
        setLayoutStateDirection(layoutDir);
        LayoutState layoutState = this.mLayoutState;
        layoutState.mCurrentPosition = layoutState.mItemDirection + referenceChildPosition;
        this.mLayoutState.mAvailable = Math.abs(delta);
    }

    int scrollBy(int dt, Recycler recycler, State state) {
        if (getChildCount() != 0) {
            if (dt != 0) {
                int totalScroll;
                prepareLayoutStateForDelta(dt, state);
                int consumed = fill(recycler, this.mLayoutState, state);
                if (this.mLayoutState.mAvailable < consumed) {
                    totalScroll = dt;
                } else if (dt < 0) {
                    totalScroll = -consumed;
                } else {
                    totalScroll = consumed;
                }
                this.mPrimaryOrientation.offsetChildren(-totalScroll);
                this.mLastLayoutFromEnd = this.mShouldReverseLayout;
                LayoutState layoutState = this.mLayoutState;
                layoutState.mAvailable = 0;
                recycle(recycler, layoutState);
                return totalScroll;
            }
        }
        return 0;
    }

    int getLastChildPosition() {
        int childCount = getChildCount();
        return childCount == 0 ? 0 : getPosition(getChildAt(childCount - 1));
    }

    int getFirstChildPosition() {
        return getChildCount() == 0 ? 0 : getPosition(getChildAt(0));
    }

    private int findFirstReferenceChildPosition(int itemCount) {
        int limit = getChildCount();
        for (int i = 0; i < limit; i++) {
            int position = getPosition(getChildAt(i));
            if (position >= 0 && position < itemCount) {
                return position;
            }
        }
        return 0;
    }

    private int findLastReferenceChildPosition(int itemCount) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            int position = getPosition(getChildAt(i));
            if (position >= 0 && position < itemCount) {
                return position;
            }
        }
        return 0;
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

    public int getOrientation() {
        return this.mOrientation;
    }

    @Nullable
    public View onFocusSearchFailed(View focused, int direction, Recycler recycler, State state) {
        if (getChildCount() == 0) {
            return null;
        }
        View directChild = findContainingItemView(focused);
        if (directChild == null) {
            return null;
        }
        resolveShouldLayoutReverse();
        int layoutDir = convertFocusDirectionToLayoutDirection(direction);
        if (layoutDir == Integer.MIN_VALUE) {
            return null;
        }
        int referenceChildPosition;
        int findFirstPartiallyVisibleItemPosition;
        View unfocusableCandidate;
        LayoutParams prevFocusLayoutParams = (LayoutParams) directChild.getLayoutParams();
        boolean prevFocusFullSpan = prevFocusLayoutParams.mFullSpan;
        Span prevFocusSpan = prevFocusLayoutParams.mSpan;
        if (layoutDir == 1) {
            referenceChildPosition = getLastChildPosition();
        } else {
            referenceChildPosition = getFirstChildPosition();
        }
        updateLayoutState(referenceChildPosition, state);
        setLayoutStateDirection(layoutDir);
        LayoutState layoutState = this.mLayoutState;
        layoutState.mCurrentPosition = layoutState.mItemDirection + referenceChildPosition;
        this.mLayoutState.mAvailable = (int) (((float) this.mPrimaryOrientation.getTotalSpace()) * MAX_SCROLL_FACTOR);
        layoutState = this.mLayoutState;
        layoutState.mStopInFocusable = true;
        boolean z = false;
        layoutState.mRecycle = false;
        fill(recycler, layoutState, state);
        this.mLastLayoutFromEnd = this.mShouldReverseLayout;
        if (!prevFocusFullSpan) {
            View view = prevFocusSpan.getFocusableViewAfter(referenceChildPosition, layoutDir);
            if (view != null && view != directChild) {
                return view;
            }
        }
        int i;
        View view2;
        if (preferLastSpan(layoutDir)) {
            for (i = this.mSpanCount - 1; i >= 0; i--) {
                view2 = this.mSpans[i].getFocusableViewAfter(referenceChildPosition, layoutDir);
                if (view2 != null && view2 != directChild) {
                    return view2;
                }
            }
        } else {
            for (i = 0; i < this.mSpanCount; i++) {
                view2 = this.mSpans[i].getFocusableViewAfter(referenceChildPosition, layoutDir);
                if (view2 != null && view2 != directChild) {
                    return view2;
                }
            }
        }
        if ((this.mReverseLayout ^ 1) == (layoutDir == -1 ? 1 : 0)) {
            z = true;
        }
        boolean shouldSearchFromStart = z;
        if (!prevFocusFullSpan) {
            if (shouldSearchFromStart) {
                findFirstPartiallyVisibleItemPosition = prevFocusSpan.findFirstPartiallyVisibleItemPosition();
            } else {
                findFirstPartiallyVisibleItemPosition = prevFocusSpan.findLastPartiallyVisibleItemPosition();
            }
            unfocusableCandidate = findViewByPosition(findFirstPartiallyVisibleItemPosition);
            if (unfocusableCandidate != null && unfocusableCandidate != directChild) {
                return unfocusableCandidate;
            }
        }
        if (preferLastSpan(layoutDir)) {
            for (findFirstPartiallyVisibleItemPosition = this.mSpanCount - 1; findFirstPartiallyVisibleItemPosition >= 0; findFirstPartiallyVisibleItemPosition--) {
                if (findFirstPartiallyVisibleItemPosition != prevFocusSpan.mIndex) {
                    View unfocusableCandidate2;
                    if (shouldSearchFromStart) {
                        unfocusableCandidate2 = this.mSpans[findFirstPartiallyVisibleItemPosition].findFirstPartiallyVisibleItemPosition();
                    } else {
                        unfocusableCandidate2 = this.mSpans[findFirstPartiallyVisibleItemPosition].findLastPartiallyVisibleItemPosition();
                    }
                    unfocusableCandidate2 = findViewByPosition(unfocusableCandidate2);
                    if (unfocusableCandidate2 != null && unfocusableCandidate2 != directChild) {
                        return unfocusableCandidate2;
                    }
                    unfocusableCandidate = unfocusableCandidate2;
                }
            }
        } else {
            for (int i2 = 0; i2 < this.mSpanCount; i2++) {
                if (shouldSearchFromStart) {
                    findFirstPartiallyVisibleItemPosition = this.mSpans[i2].findFirstPartiallyVisibleItemPosition();
                } else {
                    findFirstPartiallyVisibleItemPosition = this.mSpans[i2].findLastPartiallyVisibleItemPosition();
                }
                unfocusableCandidate = findViewByPosition(findFirstPartiallyVisibleItemPosition);
                if (unfocusableCandidate != null && unfocusableCandidate != directChild) {
                    return unfocusableCandidate;
                }
            }
        }
        return null;
    }

    private int convertFocusDirectionToLayoutDirection(int focusDirection) {
        int i = -1;
        int i2 = Integer.MIN_VALUE;
        if (focusDirection == 17) {
            if (this.mOrientation != 0) {
                i = Integer.MIN_VALUE;
            }
            return i;
        } else if (focusDirection == 33) {
            if (this.mOrientation != 1) {
                i = Integer.MIN_VALUE;
            }
            return i;
        } else if (focusDirection == 66) {
            if (this.mOrientation == 0) {
                i2 = 1;
            }
            return i2;
        } else if (focusDirection != TsExtractor.TS_STREAM_TYPE_HDMV_DTS) {
            switch (focusDirection) {
                case 1:
                    return (this.mOrientation != 1 && isLayoutRTL()) ? 1 : -1;
                case 2:
                    return (this.mOrientation != 1 && isLayoutRTL()) ? -1 : 1;
                default:
                    return Integer.MIN_VALUE;
            }
        } else {
            if (this.mOrientation == 1) {
                i2 = 1;
            }
            return i2;
        }
    }
}
