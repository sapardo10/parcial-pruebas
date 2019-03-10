package android.support.v7.widget;

import android.support.annotation.Nullable;
import android.support.v4.os.TraceCompat;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager.LayoutPrefetchRegistry;
import android.support.v7.widget.RecyclerView.Recycler;
import android.support.v7.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

final class GapWorker implements Runnable {
    static final ThreadLocal<GapWorker> sGapWorker = new ThreadLocal();
    static Comparator<Task> sTaskComparator = new C03571();
    long mFrameIntervalNs;
    long mPostTimeNs;
    ArrayList<RecyclerView> mRecyclerViews = new ArrayList();
    private ArrayList<Task> mTasks = new ArrayList();

    /* renamed from: android.support.v7.widget.GapWorker$1 */
    static class C03571 implements Comparator<Task> {
        C03571() {
        }

        public int compare(Task lhs, Task rhs) {
            int i = 1;
            if ((lhs.view == null ? 1 : null) != (rhs.view == null ? 1 : null)) {
                if (lhs.view != null) {
                    i = -1;
                }
                return i;
            } else if (lhs.immediate != rhs.immediate) {
                if (lhs.immediate) {
                    i = -1;
                }
                return i;
            } else {
                int deltaViewVelocity = rhs.viewVelocity - lhs.viewVelocity;
                if (deltaViewVelocity != 0) {
                    return deltaViewVelocity;
                }
                i = lhs.distanceToItem - rhs.distanceToItem;
                if (i != 0) {
                    return i;
                }
                return 0;
            }
        }
    }

    static class Task {
        public int distanceToItem;
        public boolean immediate;
        public int position;
        public RecyclerView view;
        public int viewVelocity;

        Task() {
        }

        public void clear() {
            this.immediate = false;
            this.viewVelocity = 0;
            this.distanceToItem = 0;
            this.view = null;
            this.position = 0;
        }
    }

    static class LayoutPrefetchRegistryImpl implements LayoutPrefetchRegistry {
        int mCount;
        int[] mPrefetchArray;
        int mPrefetchDx;
        int mPrefetchDy;

        LayoutPrefetchRegistryImpl() {
        }

        void setPrefetchVector(int dx, int dy) {
            this.mPrefetchDx = dx;
            this.mPrefetchDy = dy;
        }

        void collectPrefetchPositionsFromView(RecyclerView view, boolean nested) {
            this.mCount = 0;
            int[] iArr = this.mPrefetchArray;
            if (iArr != null) {
                Arrays.fill(iArr, -1);
            }
            LayoutManager layout = view.mLayout;
            if (view.mAdapter != null && layout != null) {
                if (layout.isItemPrefetchEnabled()) {
                    if (nested) {
                        if (!view.mAdapterHelper.hasPendingUpdates()) {
                            layout.collectInitialPrefetchPositions(view.mAdapter.getItemCount(), this);
                        }
                    } else if (!view.hasPendingAdapterUpdates()) {
                        layout.collectAdjacentPrefetchPositions(this.mPrefetchDx, this.mPrefetchDy, view.mState, this);
                    }
                    if (this.mCount > layout.mPrefetchMaxCountObserved) {
                        layout.mPrefetchMaxCountObserved = this.mCount;
                        layout.mPrefetchMaxObservedInInitialPrefetch = nested;
                        view.mRecycler.updateViewCacheSize();
                    }
                }
            }
        }

        public void addPosition(int layoutPosition, int pixelDistance) {
            if (layoutPosition < 0) {
                throw new IllegalArgumentException("Layout positions must be non-negative");
            } else if (pixelDistance >= 0) {
                int storagePosition = this.mCount * 2;
                int[] iArr = this.mPrefetchArray;
                if (iArr == null) {
                    this.mPrefetchArray = new int[4];
                    Arrays.fill(this.mPrefetchArray, -1);
                } else if (storagePosition >= iArr.length) {
                    iArr = this.mPrefetchArray;
                    this.mPrefetchArray = new int[(storagePosition * 2)];
                    System.arraycopy(iArr, 0, this.mPrefetchArray, 0, iArr.length);
                }
                iArr = this.mPrefetchArray;
                iArr[storagePosition] = layoutPosition;
                iArr[storagePosition + 1] = pixelDistance;
                this.mCount++;
            } else {
                throw new IllegalArgumentException("Pixel distance must be non-negative");
            }
        }

        boolean lastPrefetchIncludedPosition(int position) {
            if (this.mPrefetchArray != null) {
                int count = this.mCount * 2;
                for (int i = 0; i < count; i += 2) {
                    if (this.mPrefetchArray[i] == position) {
                        return true;
                    }
                }
            }
            return false;
        }

        void clearPrefetchPositions() {
            int[] iArr = this.mPrefetchArray;
            if (iArr != null) {
                Arrays.fill(iArr, -1);
            }
            this.mCount = 0;
        }
    }

    public void run() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:26:0x0060 in {5, 12, 13, 14, 18, 22, 25} preds:[]
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
        r9 = this;
        r0 = 0;
        r2 = "RV Prefetch";	 Catch:{ all -> 0x0059 }
        android.support.v4.os.TraceCompat.beginSection(r2);	 Catch:{ all -> 0x0059 }
        r2 = r9.mRecyclerViews;	 Catch:{ all -> 0x0059 }
        r2 = r2.isEmpty();	 Catch:{ all -> 0x0059 }
        if (r2 == 0) goto L_0x0015;
    L_0x000f:
        r9.mPostTimeNs = r0;
        android.support.v4.os.TraceCompat.endSection();
        return;
    L_0x0015:
        r2 = r9.mRecyclerViews;	 Catch:{ all -> 0x0059 }
        r2 = r2.size();	 Catch:{ all -> 0x0059 }
        r3 = 0;	 Catch:{ all -> 0x0059 }
        r5 = 0;	 Catch:{ all -> 0x0059 }
    L_0x001e:
        if (r5 >= r2) goto L_0x003c;	 Catch:{ all -> 0x0059 }
    L_0x0020:
        r6 = r9.mRecyclerViews;	 Catch:{ all -> 0x0059 }
        r6 = r6.get(r5);	 Catch:{ all -> 0x0059 }
        r6 = (android.support.v7.widget.RecyclerView) r6;	 Catch:{ all -> 0x0059 }
        r7 = r6.getWindowVisibility();	 Catch:{ all -> 0x0059 }
        if (r7 != 0) goto L_0x0038;	 Catch:{ all -> 0x0059 }
    L_0x002e:
        r7 = r6.getDrawingTime();	 Catch:{ all -> 0x0059 }
        r7 = java.lang.Math.max(r7, r3);	 Catch:{ all -> 0x0059 }
        r3 = r7;
        goto L_0x0039;
    L_0x0039:
        r5 = r5 + 1;
        goto L_0x001e;
    L_0x003c:
        r5 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1));
        if (r5 != 0) goto L_0x0046;
    L_0x0040:
        r9.mPostTimeNs = r0;
        android.support.v4.os.TraceCompat.endSection();
        return;
    L_0x0046:
        r5 = java.util.concurrent.TimeUnit.MILLISECONDS;	 Catch:{ all -> 0x0059 }
        r5 = r5.toNanos(r3);	 Catch:{ all -> 0x0059 }
        r7 = r9.mFrameIntervalNs;	 Catch:{ all -> 0x0059 }
        r5 = r5 + r7;	 Catch:{ all -> 0x0059 }
        r9.prefetch(r5);	 Catch:{ all -> 0x0059 }
        r9.mPostTimeNs = r0;
        android.support.v4.os.TraceCompat.endSection();
        return;
    L_0x0059:
        r2 = move-exception;
        r9.mPostTimeNs = r0;
        android.support.v4.os.TraceCompat.endSection();
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.GapWorker.run():void");
    }

    GapWorker() {
    }

    public void add(RecyclerView recyclerView) {
        this.mRecyclerViews.add(recyclerView);
    }

    public void remove(RecyclerView recyclerView) {
        boolean removeSuccess = this.mRecyclerViews.remove(recyclerView);
    }

    void postFromTraversal(RecyclerView recyclerView, int prefetchDx, int prefetchDy) {
        if (recyclerView.isAttachedToWindow()) {
            if (this.mPostTimeNs == 0) {
                this.mPostTimeNs = recyclerView.getNanoTime();
                recyclerView.post(this);
            }
        }
        recyclerView.mPrefetchRegistry.setPrefetchVector(prefetchDx, prefetchDy);
    }

    private void buildTaskList() {
        int i;
        int viewCount = this.mRecyclerViews.size();
        int totalTaskCount = 0;
        for (i = 0; i < viewCount; i++) {
            RecyclerView view = (RecyclerView) this.mRecyclerViews.get(i);
            if (view.getWindowVisibility() == 0) {
                view.mPrefetchRegistry.collectPrefetchPositionsFromView(view, false);
                totalTaskCount += view.mPrefetchRegistry.mCount;
            }
        }
        this.mTasks.ensureCapacity(totalTaskCount);
        i = 0;
        for (int i2 = 0; i2 < viewCount; i2++) {
            RecyclerView view2 = (RecyclerView) this.mRecyclerViews.get(i2);
            if (view2.getWindowVisibility() == 0) {
                LayoutPrefetchRegistryImpl prefetchRegistry = view2.mPrefetchRegistry;
                int viewVelocity = Math.abs(prefetchRegistry.mPrefetchDx) + Math.abs(prefetchRegistry.mPrefetchDy);
                for (int j = 0; j < prefetchRegistry.mCount * 2; j += 2) {
                    Task task;
                    if (i >= this.mTasks.size()) {
                        task = new Task();
                        this.mTasks.add(task);
                    } else {
                        task = (Task) this.mTasks.get(i);
                    }
                    int distanceToItem = prefetchRegistry.mPrefetchArray[j + 1];
                    task.immediate = distanceToItem <= viewVelocity;
                    task.viewVelocity = viewVelocity;
                    task.distanceToItem = distanceToItem;
                    task.view = view2;
                    task.position = prefetchRegistry.mPrefetchArray[j];
                    i++;
                }
            }
        }
        Collections.sort(this.mTasks, sTaskComparator);
    }

    static boolean isPrefetchPositionAttached(RecyclerView view, int position) {
        int childCount = view.mChildHelper.getUnfilteredChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder holder = RecyclerView.getChildViewHolderInt(view.mChildHelper.getUnfilteredChildAt(i));
            if (holder.mPosition == position && !holder.isInvalid()) {
                return true;
            }
        }
        return false;
    }

    private ViewHolder prefetchPositionWithDeadline(RecyclerView view, int position, long deadlineNs) {
        if (isPrefetchPositionAttached(view, position)) {
            return null;
        }
        Recycler recycler = view.mRecycler;
        try {
            view.onEnterLayoutOrScroll();
            ViewHolder holder = recycler.tryGetViewHolderForPositionByDeadline(position, false, deadlineNs);
            if (holder != null) {
                if (!holder.isBound() || holder.isInvalid()) {
                    recycler.addViewHolderToRecycledViewPool(holder, false);
                } else {
                    recycler.recycleView(holder.itemView);
                }
            }
            view.onExitLayoutOrScroll(false);
            return holder;
        } catch (Throwable th) {
            view.onExitLayoutOrScroll(false);
        }
    }

    private void prefetchInnerRecyclerViewWithDeadline(@Nullable RecyclerView innerView, long deadlineNs) {
        if (innerView != null) {
            if (innerView.mDataSetHasChangedAfterLayout) {
                if (innerView.mChildHelper.getUnfilteredChildCount() != 0) {
                    innerView.removeAndRecycleViews();
                }
            }
            LayoutPrefetchRegistryImpl innerPrefetchRegistry = innerView.mPrefetchRegistry;
            innerPrefetchRegistry.collectPrefetchPositionsFromView(innerView, true);
            if (innerPrefetchRegistry.mCount != 0) {
                try {
                    TraceCompat.beginSection("RV Nested Prefetch");
                    innerView.mState.prepareForNestedPrefetch(innerView.mAdapter);
                    for (int i = 0; i < innerPrefetchRegistry.mCount * 2; i += 2) {
                        prefetchPositionWithDeadline(innerView, innerPrefetchRegistry.mPrefetchArray[i], deadlineNs);
                    }
                } finally {
                    TraceCompat.endSection();
                }
            }
        }
    }

    private void flushTaskWithDeadline(Task task, long deadlineNs) {
        ViewHolder holder = prefetchPositionWithDeadline(task.view, task.position, task.immediate ? Long.MAX_VALUE : deadlineNs);
        if (holder != null && holder.mNestedRecyclerView != null) {
            if (!holder.isBound()) {
                return;
            }
            if (!holder.isInvalid()) {
                prefetchInnerRecyclerViewWithDeadline((RecyclerView) holder.mNestedRecyclerView.get(), deadlineNs);
            }
        }
    }

    private void flushTasksWithDeadline(long deadlineNs) {
        int i = 0;
        while (i < this.mTasks.size()) {
            Task task = (Task) this.mTasks.get(i);
            if (task.view != null) {
                flushTaskWithDeadline(task, deadlineNs);
                task.clear();
                i++;
            } else {
                return;
            }
        }
    }

    void prefetch(long deadlineNs) {
        buildTaskList();
        flushTasksWithDeadline(deadlineNs);
    }
}
