package com.google.android.exoplayer2.source;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlayerMessage.Target;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.ShuffleOrder.DefaultShuffleOrder;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class ConcatenatingMediaSource extends CompositeMediaSource<MediaSourceHolder> implements Target {
    private static final int MSG_ADD = 0;
    private static final int MSG_MOVE = 2;
    private static final int MSG_NOTIFY_LISTENER = 4;
    private static final int MSG_ON_COMPLETION = 5;
    private static final int MSG_REMOVE = 1;
    private static final int MSG_SET_SHUFFLE_ORDER = 3;
    private final boolean isAtomic;
    private boolean listenerNotificationScheduled;
    private final Map<MediaPeriod, MediaSourceHolder> mediaSourceByMediaPeriod;
    private final Map<Object, MediaSourceHolder> mediaSourceByUid;
    private final List<MediaSourceHolder> mediaSourceHolders;
    private final List<MediaSourceHolder> mediaSourcesPublic;
    private final List<Runnable> pendingOnCompletionActions;
    private final Period period;
    private int periodCount;
    @Nullable
    private ExoPlayer player;
    @Nullable
    private Handler playerApplicationHandler;
    private ShuffleOrder shuffleOrder;
    private final boolean useLazyPreparation;
    private final Window window;
    private int windowCount;

    static final class MediaSourceHolder implements Comparable<MediaSourceHolder> {
        public List<DeferredMediaPeriod> activeMediaPeriods = new ArrayList();
        public int childIndex;
        public int firstPeriodIndexInChild;
        public int firstWindowIndexInChild;
        public boolean hasStartedPreparing;
        public boolean isPrepared;
        public boolean isRemoved;
        public final MediaSource mediaSource;
        public DeferredTimeline timeline;
        public final Object uid = new Object();

        public MediaSourceHolder(MediaSource mediaSource) {
            this.mediaSource = mediaSource;
            this.timeline = DeferredTimeline.createWithDummyTimeline(mediaSource.getTag());
        }

        public void reset(int childIndex, int firstWindowIndexInChild, int firstPeriodIndexInChild) {
            this.childIndex = childIndex;
            this.firstWindowIndexInChild = firstWindowIndexInChild;
            this.firstPeriodIndexInChild = firstPeriodIndexInChild;
            this.hasStartedPreparing = false;
            this.isPrepared = false;
            this.isRemoved = false;
            this.activeMediaPeriods.clear();
        }

        public int compareTo(@NonNull MediaSourceHolder other) {
            return this.firstPeriodIndexInChild - other.firstPeriodIndexInChild;
        }
    }

    private static final class MessageData<T> {
        @Nullable
        public final Runnable actionOnCompletion;
        public final T customData;
        public final int index;

        public MessageData(int index, T customData, @Nullable Runnable actionOnCompletion) {
            this.index = index;
            this.actionOnCompletion = actionOnCompletion;
            this.customData = customData;
        }
    }

    private static final class DummyTimeline extends Timeline {
        @Nullable
        private final Object tag;

        public DummyTimeline(@Nullable Object tag) {
            this.tag = tag;
        }

        public int getWindowCount() {
            return 1;
        }

        public Window getWindow(int windowIndex, Window window, boolean setTag, long defaultPositionProjectionUs) {
            return window.set(this.tag, C0555C.TIME_UNSET, C0555C.TIME_UNSET, false, true, 0, C0555C.TIME_UNSET, 0, 0, 0);
        }

        public int getPeriodCount() {
            return 1;
        }

        public Period getPeriod(int periodIndex, Period period, boolean setIds) {
            return period.set(Integer.valueOf(0), DeferredTimeline.DUMMY_ID, 0, C0555C.TIME_UNSET, 0);
        }

        public int getIndexOfPeriod(Object uid) {
            return uid == DeferredTimeline.DUMMY_ID ? 0 : -1;
        }

        public Object getUidOfPeriod(int periodIndex) {
            return DeferredTimeline.DUMMY_ID;
        }
    }

    private static final class ConcatenatedTimeline extends AbstractConcatenatedTimeline {
        private final HashMap<Object, Integer> childIndexByUid = new HashMap();
        private final int[] firstPeriodInChildIndices;
        private final int[] firstWindowInChildIndices;
        private final int periodCount;
        private final Timeline[] timelines;
        private final Object[] uids;
        private final int windowCount;

        public ConcatenatedTimeline(Collection<MediaSourceHolder> mediaSourceHolders, int windowCount, int periodCount, ShuffleOrder shuffleOrder, boolean isAtomic) {
            super(isAtomic, shuffleOrder);
            this.windowCount = windowCount;
            this.periodCount = periodCount;
            int childCount = mediaSourceHolders.size();
            this.firstPeriodInChildIndices = new int[childCount];
            this.firstWindowInChildIndices = new int[childCount];
            this.timelines = new Timeline[childCount];
            this.uids = new Object[childCount];
            int index = 0;
            for (MediaSourceHolder mediaSourceHolder : mediaSourceHolders) {
                this.timelines[index] = mediaSourceHolder.timeline;
                this.firstPeriodInChildIndices[index] = mediaSourceHolder.firstPeriodIndexInChild;
                this.firstWindowInChildIndices[index] = mediaSourceHolder.firstWindowIndexInChild;
                this.uids[index] = mediaSourceHolder.uid;
                int index2 = index + 1;
                this.childIndexByUid.put(this.uids[index], Integer.valueOf(index));
                index = index2;
            }
        }

        protected int getChildIndexByPeriodIndex(int periodIndex) {
            return Util.binarySearchFloor(this.firstPeriodInChildIndices, periodIndex + 1, false, false);
        }

        protected int getChildIndexByWindowIndex(int windowIndex) {
            return Util.binarySearchFloor(this.firstWindowInChildIndices, windowIndex + 1, false, false);
        }

        protected int getChildIndexByChildUid(Object childUid) {
            Integer index = (Integer) this.childIndexByUid.get(childUid);
            return index == null ? -1 : index.intValue();
        }

        protected Timeline getTimelineByChildIndex(int childIndex) {
            return this.timelines[childIndex];
        }

        protected int getFirstPeriodIndexByChildIndex(int childIndex) {
            return this.firstPeriodInChildIndices[childIndex];
        }

        protected int getFirstWindowIndexByChildIndex(int childIndex) {
            return this.firstWindowInChildIndices[childIndex];
        }

        protected Object getChildUidByChildIndex(int childIndex) {
            return this.uids[childIndex];
        }

        public int getWindowCount() {
            return this.windowCount;
        }

        public int getPeriodCount() {
            return this.periodCount;
        }
    }

    private static final class DeferredTimeline extends ForwardingTimeline {
        private static final Object DUMMY_ID = new Object();
        private final Object replacedId;

        public static DeferredTimeline createWithDummyTimeline(@Nullable Object windowTag) {
            return new DeferredTimeline(new DummyTimeline(windowTag), DUMMY_ID);
        }

        public static DeferredTimeline createWithRealTimeline(Timeline timeline, Object firstPeriodUid) {
            return new DeferredTimeline(timeline, firstPeriodUid);
        }

        private DeferredTimeline(Timeline timeline, Object replacedId) {
            super(timeline);
            this.replacedId = replacedId;
        }

        public DeferredTimeline cloneWithUpdatedTimeline(Timeline timeline) {
            return new DeferredTimeline(timeline, this.replacedId);
        }

        public Timeline getTimeline() {
            return this.timeline;
        }

        public Period getPeriod(int periodIndex, Period period, boolean setIds) {
            this.timeline.getPeriod(periodIndex, period, setIds);
            if (Util.areEqual(period.uid, this.replacedId)) {
                period.uid = DUMMY_ID;
            }
            return period;
        }

        public int getIndexOfPeriod(Object uid) {
            return this.timeline.getIndexOfPeriod(DUMMY_ID.equals(uid) ? this.replacedId : uid);
        }

        public Object getUidOfPeriod(int periodIndex) {
            Object uid = this.timeline.getUidOfPeriod(periodIndex);
            return Util.areEqual(uid, this.replacedId) ? DUMMY_ID : uid;
        }
    }

    private static final class DummyMediaSource extends BaseMediaSource {
        private DummyMediaSource() {
        }

        protected void prepareSourceInternal(ExoPlayer player, boolean isTopLevelSource, @Nullable TransferListener mediaTransferListener) {
        }

        @Nullable
        public Object getTag() {
            return null;
        }

        protected void releaseSourceInternal() {
        }

        public void maybeThrowSourceInfoRefreshError() throws IOException {
        }

        public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
            throw new UnsupportedOperationException();
        }

        public void releasePeriod(MediaPeriod mediaPeriod) {
        }
    }

    public final synchronized void addMediaSources(int r5, java.util.Collection<com.google.android.exoplayer2.source.MediaSource> r6, @android.support.annotation.Nullable java.lang.Runnable r7) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:25:0x006b in {5, 9, 14, 18, 19, 21, 24} preds:[]
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
        r4 = this;
        monitor-enter(r4);
        r0 = r6.iterator();	 Catch:{ all -> 0x0068 }
    L_0x0005:
        r1 = r0.hasNext();	 Catch:{ all -> 0x0068 }
        if (r1 == 0) goto L_0x0015;	 Catch:{ all -> 0x0068 }
    L_0x000b:
        r1 = r0.next();	 Catch:{ all -> 0x0068 }
        r1 = (com.google.android.exoplayer2.source.MediaSource) r1;	 Catch:{ all -> 0x0068 }
        com.google.android.exoplayer2.util.Assertions.checkNotNull(r1);	 Catch:{ all -> 0x0068 }
        goto L_0x0005;	 Catch:{ all -> 0x0068 }
    L_0x0015:
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x0068 }
        r1 = r6.size();	 Catch:{ all -> 0x0068 }
        r0.<init>(r1);	 Catch:{ all -> 0x0068 }
        r1 = r6.iterator();	 Catch:{ all -> 0x0068 }
    L_0x0022:
        r2 = r1.hasNext();	 Catch:{ all -> 0x0068 }
        if (r2 == 0) goto L_0x0037;	 Catch:{ all -> 0x0068 }
    L_0x0028:
        r2 = r1.next();	 Catch:{ all -> 0x0068 }
        r2 = (com.google.android.exoplayer2.source.MediaSource) r2;	 Catch:{ all -> 0x0068 }
        r3 = new com.google.android.exoplayer2.source.ConcatenatingMediaSource$MediaSourceHolder;	 Catch:{ all -> 0x0068 }
        r3.<init>(r2);	 Catch:{ all -> 0x0068 }
        r0.add(r3);	 Catch:{ all -> 0x0068 }
        goto L_0x0022;	 Catch:{ all -> 0x0068 }
    L_0x0037:
        r1 = r4.mediaSourcesPublic;	 Catch:{ all -> 0x0068 }
        r1.addAll(r5, r0);	 Catch:{ all -> 0x0068 }
        r1 = r4.player;	 Catch:{ all -> 0x0068 }
        if (r1 == 0) goto L_0x005e;	 Catch:{ all -> 0x0068 }
    L_0x0040:
        r1 = r6.isEmpty();	 Catch:{ all -> 0x0068 }
        if (r1 != 0) goto L_0x005e;	 Catch:{ all -> 0x0068 }
    L_0x0046:
        r1 = r4.player;	 Catch:{ all -> 0x0068 }
        r1 = r1.createMessage(r4);	 Catch:{ all -> 0x0068 }
        r2 = 0;	 Catch:{ all -> 0x0068 }
        r1 = r1.setType(r2);	 Catch:{ all -> 0x0068 }
        r2 = new com.google.android.exoplayer2.source.ConcatenatingMediaSource$MessageData;	 Catch:{ all -> 0x0068 }
        r2.<init>(r5, r0, r7);	 Catch:{ all -> 0x0068 }
        r1 = r1.setPayload(r2);	 Catch:{ all -> 0x0068 }
        r1.send();	 Catch:{ all -> 0x0068 }
        goto L_0x0066;	 Catch:{ all -> 0x0068 }
        if (r7 == 0) goto L_0x0065;	 Catch:{ all -> 0x0068 }
    L_0x0061:
        r7.run();	 Catch:{ all -> 0x0068 }
        goto L_0x0066;
    L_0x0066:
        monitor-exit(r4);
        return;
    L_0x0068:
        r5 = move-exception;
        monitor-exit(r4);
        throw r5;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.ConcatenatingMediaSource.addMediaSources(int, java.util.Collection, java.lang.Runnable):void");
    }

    public ConcatenatingMediaSource(MediaSource... mediaSources) {
        this(false, mediaSources);
    }

    public ConcatenatingMediaSource(boolean isAtomic, MediaSource... mediaSources) {
        this(isAtomic, new DefaultShuffleOrder(0), mediaSources);
    }

    public ConcatenatingMediaSource(boolean isAtomic, ShuffleOrder shuffleOrder, MediaSource... mediaSources) {
        this(isAtomic, false, shuffleOrder, mediaSources);
    }

    public ConcatenatingMediaSource(boolean isAtomic, boolean useLazyPreparation, ShuffleOrder shuffleOrder, MediaSource... mediaSources) {
        for (MediaSource mediaSource : mediaSources) {
            Assertions.checkNotNull(mediaSource);
        }
        this.shuffleOrder = shuffleOrder.getLength() > 0 ? shuffleOrder.cloneAndClear() : shuffleOrder;
        this.mediaSourceByMediaPeriod = new IdentityHashMap();
        this.mediaSourceByUid = new HashMap();
        this.mediaSourcesPublic = new ArrayList();
        this.mediaSourceHolders = new ArrayList();
        this.pendingOnCompletionActions = new ArrayList();
        this.isAtomic = isAtomic;
        this.useLazyPreparation = useLazyPreparation;
        this.window = new Window();
        this.period = new Period();
        addMediaSources(Arrays.asList(mediaSources));
    }

    public final synchronized void addMediaSource(MediaSource mediaSource) {
        addMediaSource(this.mediaSourcesPublic.size(), mediaSource, null);
    }

    public final synchronized void addMediaSource(MediaSource mediaSource, @Nullable Runnable actionOnCompletion) {
        addMediaSource(this.mediaSourcesPublic.size(), mediaSource, actionOnCompletion);
    }

    public final synchronized void addMediaSource(int index, MediaSource mediaSource) {
        addMediaSource(index, mediaSource, null);
    }

    public final synchronized void addMediaSource(int index, MediaSource mediaSource, @Nullable Runnable actionOnCompletion) {
        addMediaSources(index, Collections.singletonList(mediaSource), actionOnCompletion);
    }

    public final synchronized void addMediaSources(Collection<MediaSource> mediaSources) {
        addMediaSources(this.mediaSourcesPublic.size(), mediaSources, null);
    }

    public final synchronized void addMediaSources(Collection<MediaSource> mediaSources, @Nullable Runnable actionOnCompletion) {
        addMediaSources(this.mediaSourcesPublic.size(), mediaSources, actionOnCompletion);
    }

    public final synchronized void addMediaSources(int index, Collection<MediaSource> mediaSources) {
        addMediaSources(index, mediaSources, null);
    }

    public final synchronized void removeMediaSource(int index) {
        removeMediaSource(index, null);
    }

    public final synchronized void removeMediaSource(int index, @Nullable Runnable actionOnCompletion) {
        removeMediaSourceRange(index, index + 1, actionOnCompletion);
    }

    public final synchronized void removeMediaSourceRange(int fromIndex, int toIndex) {
        removeMediaSourceRange(fromIndex, toIndex, null);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized void removeMediaSourceRange(int r4, int r5, @android.support.annotation.Nullable java.lang.Runnable r6) {
        /*
        r3 = this;
        monitor-enter(r3);
        r0 = r3.mediaSourcesPublic;	 Catch:{ all -> 0x003a }
        com.google.android.exoplayer2.util.Util.removeRange(r0, r4, r5);	 Catch:{ all -> 0x003a }
        if (r4 != r5) goto L_0x0011;
    L_0x0008:
        if (r6 == 0) goto L_0x000e;
    L_0x000a:
        r6.run();	 Catch:{ all -> 0x003a }
        goto L_0x000f;
    L_0x000f:
        monitor-exit(r3);
        return;
    L_0x0011:
        r0 = r3.player;	 Catch:{ all -> 0x003a }
        if (r0 == 0) goto L_0x0031;
    L_0x0015:
        r0 = r3.player;	 Catch:{ all -> 0x003a }
        r0 = r0.createMessage(r3);	 Catch:{ all -> 0x003a }
        r1 = 1;
        r0 = r0.setType(r1);	 Catch:{ all -> 0x003a }
        r1 = new com.google.android.exoplayer2.source.ConcatenatingMediaSource$MessageData;	 Catch:{ all -> 0x003a }
        r2 = java.lang.Integer.valueOf(r5);	 Catch:{ all -> 0x003a }
        r1.<init>(r4, r2, r6);	 Catch:{ all -> 0x003a }
        r0 = r0.setPayload(r1);	 Catch:{ all -> 0x003a }
        r0.send();	 Catch:{ all -> 0x003a }
        goto L_0x0038;
    L_0x0031:
        if (r6 == 0) goto L_0x0037;
    L_0x0033:
        r6.run();	 Catch:{ all -> 0x003a }
        goto L_0x0038;
    L_0x0038:
        monitor-exit(r3);
        return;
    L_0x003a:
        r4 = move-exception;
        monitor-exit(r3);
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.ConcatenatingMediaSource.removeMediaSourceRange(int, int, java.lang.Runnable):void");
    }

    public final synchronized void moveMediaSource(int currentIndex, int newIndex) {
        moveMediaSource(currentIndex, newIndex, null);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized void moveMediaSource(int r4, int r5, @android.support.annotation.Nullable java.lang.Runnable r6) {
        /*
        r3 = this;
        monitor-enter(r3);
        if (r4 != r5) goto L_0x000c;
    L_0x0003:
        if (r6 == 0) goto L_0x0009;
    L_0x0005:
        r6.run();	 Catch:{ all -> 0x0040 }
        goto L_0x000a;
    L_0x000a:
        monitor-exit(r3);
        return;
    L_0x000c:
        r0 = r3.mediaSourcesPublic;	 Catch:{ all -> 0x0040 }
        r1 = r3.mediaSourcesPublic;	 Catch:{ all -> 0x0040 }
        r1 = r1.remove(r4);	 Catch:{ all -> 0x0040 }
        r0.add(r5, r1);	 Catch:{ all -> 0x0040 }
        r0 = r3.player;	 Catch:{ all -> 0x0040 }
        if (r0 == 0) goto L_0x0037;
    L_0x001b:
        r0 = r3.player;	 Catch:{ all -> 0x0040 }
        r0 = r0.createMessage(r3);	 Catch:{ all -> 0x0040 }
        r1 = 2;
        r0 = r0.setType(r1);	 Catch:{ all -> 0x0040 }
        r1 = new com.google.android.exoplayer2.source.ConcatenatingMediaSource$MessageData;	 Catch:{ all -> 0x0040 }
        r2 = java.lang.Integer.valueOf(r5);	 Catch:{ all -> 0x0040 }
        r1.<init>(r4, r2, r6);	 Catch:{ all -> 0x0040 }
        r0 = r0.setPayload(r1);	 Catch:{ all -> 0x0040 }
        r0.send();	 Catch:{ all -> 0x0040 }
        goto L_0x003e;
    L_0x0037:
        if (r6 == 0) goto L_0x003d;
    L_0x0039:
        r6.run();	 Catch:{ all -> 0x0040 }
        goto L_0x003e;
    L_0x003e:
        monitor-exit(r3);
        return;
    L_0x0040:
        r4 = move-exception;
        monitor-exit(r3);
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.ConcatenatingMediaSource.moveMediaSource(int, int, java.lang.Runnable):void");
    }

    public final synchronized void clear() {
        clear(null);
    }

    public final synchronized void clear(@Nullable Runnable actionOnCompletion) {
        removeMediaSourceRange(0, getSize(), actionOnCompletion);
    }

    public final synchronized int getSize() {
        return this.mediaSourcesPublic.size();
    }

    public final synchronized MediaSource getMediaSource(int index) {
        return ((MediaSourceHolder) this.mediaSourcesPublic.get(index)).mediaSource;
    }

    public final synchronized void setShuffleOrder(ShuffleOrder shuffleOrder) {
        setShuffleOrder(shuffleOrder, null);
    }

    public final synchronized void setShuffleOrder(ShuffleOrder shuffleOrder, @Nullable Runnable actionOnCompletion) {
        ExoPlayer player = this.player;
        if (player != null) {
            int size = getSize();
            if (shuffleOrder.getLength() != size) {
                shuffleOrder = shuffleOrder.cloneAndClear().cloneAndInsert(0, size);
            }
            player.createMessage(this).setType(3).setPayload(new MessageData(0, shuffleOrder, actionOnCompletion)).send();
        } else {
            this.shuffleOrder = shuffleOrder.getLength() > 0 ? shuffleOrder.cloneAndClear() : shuffleOrder;
            if (actionOnCompletion != null) {
                actionOnCompletion.run();
            }
        }
    }

    @Nullable
    public Object getTag() {
        return null;
    }

    public final synchronized void prepareSourceInternal(ExoPlayer player, boolean isTopLevelSource, @Nullable TransferListener mediaTransferListener) {
        super.prepareSourceInternal(player, isTopLevelSource, mediaTransferListener);
        this.player = player;
        this.playerApplicationHandler = new Handler(player.getApplicationLooper());
        if (this.mediaSourcesPublic.isEmpty()) {
            notifyListener();
        } else {
            this.shuffleOrder = this.shuffleOrder.cloneAndInsert(0, this.mediaSourcesPublic.size());
            addMediaSourcesInternal(0, this.mediaSourcesPublic);
            scheduleListenerNotification(null);
        }
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
    }

    public final MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        MediaSourceHolder holder = (MediaSourceHolder) this.mediaSourceByUid.get(getMediaSourceHolderUid(id.periodUid));
        if (holder == null) {
            holder = new MediaSourceHolder(new DummyMediaSource());
            holder.hasStartedPreparing = true;
        }
        DeferredMediaPeriod mediaPeriod = new DeferredMediaPeriod(holder.mediaSource, id, allocator);
        this.mediaSourceByMediaPeriod.put(mediaPeriod, holder);
        holder.activeMediaPeriods.add(mediaPeriod);
        if (!holder.hasStartedPreparing) {
            holder.hasStartedPreparing = true;
            prepareChildSource(holder, holder.mediaSource);
        } else if (holder.isPrepared) {
            mediaPeriod.createPeriod(id.copyWithPeriodUid(getChildPeriodUid(holder, id.periodUid)));
        }
        return mediaPeriod;
    }

    public final void releasePeriod(MediaPeriod mediaPeriod) {
        MediaSourceHolder holder = (MediaSourceHolder) Assertions.checkNotNull(this.mediaSourceByMediaPeriod.remove(mediaPeriod));
        ((DeferredMediaPeriod) mediaPeriod).releasePeriod();
        holder.activeMediaPeriods.remove(mediaPeriod);
        maybeReleaseChildSource(holder);
    }

    public final void releaseSourceInternal() {
        super.releaseSourceInternal();
        this.mediaSourceHolders.clear();
        this.mediaSourceByUid.clear();
        this.player = null;
        this.playerApplicationHandler = null;
        this.shuffleOrder = this.shuffleOrder.cloneAndClear();
        this.windowCount = 0;
        this.periodCount = 0;
    }

    protected final void onChildSourceInfoRefreshed(MediaSourceHolder mediaSourceHolder, MediaSource mediaSource, Timeline timeline, @Nullable Object manifest) {
        updateMediaSourceInternal(mediaSourceHolder, timeline);
    }

    @Nullable
    protected MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(MediaSourceHolder mediaSourceHolder, MediaPeriodId mediaPeriodId) {
        for (int i = 0; i < mediaSourceHolder.activeMediaPeriods.size(); i++) {
            if (((DeferredMediaPeriod) mediaSourceHolder.activeMediaPeriods.get(i)).id.windowSequenceNumber == mediaPeriodId.windowSequenceNumber) {
                return mediaPeriodId.copyWithPeriodUid(getPeriodUid(mediaSourceHolder, mediaPeriodId.periodUid));
            }
        }
        return null;
    }

    protected int getWindowIndexForChildWindowIndex(MediaSourceHolder mediaSourceHolder, int windowIndex) {
        return mediaSourceHolder.firstWindowIndexInChild + windowIndex;
    }

    public final void handleMessage(int messageType, @Nullable Object message) throws ExoPlaybackException {
        if (this.player != null) {
            MessageData<Integer> removeMessage;
            int toIndex;
            switch (messageType) {
                case 0:
                    MessageData<Collection<MediaSourceHolder>> addMessage = (MessageData) Util.castNonNull(message);
                    this.shuffleOrder = this.shuffleOrder.cloneAndInsert(addMessage.index, ((Collection) addMessage.customData).size());
                    addMediaSourcesInternal(addMessage.index, (Collection) addMessage.customData);
                    scheduleListenerNotification(addMessage.actionOnCompletion);
                    break;
                case 1:
                    removeMessage = (MessageData) Util.castNonNull(message);
                    int fromIndex = removeMessage.index;
                    toIndex = ((Integer) removeMessage.customData).intValue();
                    if (fromIndex == 0 && toIndex == this.shuffleOrder.getLength()) {
                        this.shuffleOrder = this.shuffleOrder.cloneAndClear();
                    } else {
                        this.shuffleOrder = this.shuffleOrder.cloneAndRemove(fromIndex, toIndex);
                    }
                    for (int index = toIndex - 1; index >= fromIndex; index--) {
                        removeMediaSourceInternal(index);
                    }
                    scheduleListenerNotification(removeMessage.actionOnCompletion);
                    break;
                case 2:
                    removeMessage = (MessageData) Util.castNonNull(message);
                    this.shuffleOrder = this.shuffleOrder.cloneAndRemove(removeMessage.index, removeMessage.index + 1);
                    this.shuffleOrder = this.shuffleOrder.cloneAndInsert(((Integer) removeMessage.customData).intValue(), 1);
                    moveMediaSourceInternal(removeMessage.index, ((Integer) removeMessage.customData).intValue());
                    scheduleListenerNotification(removeMessage.actionOnCompletion);
                    break;
                case 3:
                    MessageData<ShuffleOrder> shuffleOrderMessage = (MessageData) Util.castNonNull(message);
                    this.shuffleOrder = (ShuffleOrder) shuffleOrderMessage.customData;
                    scheduleListenerNotification(shuffleOrderMessage.actionOnCompletion);
                    break;
                case 4:
                    notifyListener();
                    break;
                case 5:
                    List<Runnable> actionsOnCompletion = (List) Util.castNonNull(message);
                    Handler handler = (Handler) Assertions.checkNotNull(this.playerApplicationHandler);
                    for (toIndex = 0; toIndex < actionsOnCompletion.size(); toIndex++) {
                        handler.post((Runnable) actionsOnCompletion.get(toIndex));
                    }
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    private void scheduleListenerNotification(@Nullable Runnable actionOnCompletion) {
        if (!this.listenerNotificationScheduled) {
            ((ExoPlayer) Assertions.checkNotNull(this.player)).createMessage(this).setType(4).send();
            this.listenerNotificationScheduled = true;
        }
        if (actionOnCompletion != null) {
            this.pendingOnCompletionActions.add(actionOnCompletion);
        }
    }

    private void notifyListener() {
        this.listenerNotificationScheduled = false;
        List<Runnable> actionsOnCompletion = this.pendingOnCompletionActions.isEmpty() ? Collections.emptyList() : new ArrayList(this.pendingOnCompletionActions);
        this.pendingOnCompletionActions.clear();
        refreshSourceInfo(new ConcatenatedTimeline(this.mediaSourceHolders, this.windowCount, this.periodCount, this.shuffleOrder, this.isAtomic), null);
        if (!actionsOnCompletion.isEmpty()) {
            ((ExoPlayer) Assertions.checkNotNull(this.player)).createMessage(this).setType(5).setPayload(actionsOnCompletion).send();
        }
    }

    private void addMediaSourcesInternal(int index, Collection<MediaSourceHolder> mediaSourceHolders) {
        for (MediaSourceHolder mediaSourceHolder : mediaSourceHolders) {
            int index2 = index + 1;
            addMediaSourceInternal(index, mediaSourceHolder);
            index = index2;
        }
    }

    private void addMediaSourceInternal(int newIndex, MediaSourceHolder newMediaSourceHolder) {
        if (newIndex > 0) {
            MediaSourceHolder previousHolder = (MediaSourceHolder) this.mediaSourceHolders.get(newIndex - 1);
            newMediaSourceHolder.reset(newIndex, previousHolder.firstWindowIndexInChild + previousHolder.timeline.getWindowCount(), previousHolder.firstPeriodIndexInChild + previousHolder.timeline.getPeriodCount());
        } else {
            newMediaSourceHolder.reset(newIndex, 0, 0);
        }
        correctOffsets(newIndex, 1, newMediaSourceHolder.timeline.getWindowCount(), newMediaSourceHolder.timeline.getPeriodCount());
        this.mediaSourceHolders.add(newIndex, newMediaSourceHolder);
        this.mediaSourceByUid.put(newMediaSourceHolder.uid, newMediaSourceHolder);
        if (!this.useLazyPreparation) {
            newMediaSourceHolder.hasStartedPreparing = true;
            prepareChildSource(newMediaSourceHolder, newMediaSourceHolder.mediaSource);
        }
    }

    private void updateMediaSourceInternal(MediaSourceHolder mediaSourceHolder, Timeline timeline) {
        ConcatenatingMediaSource concatenatingMediaSource = this;
        MediaSourceHolder mediaSourceHolder2 = mediaSourceHolder;
        Timeline timeline2 = timeline;
        if (mediaSourceHolder2 != null) {
            DeferredTimeline deferredTimeline = mediaSourceHolder2.timeline;
            if (deferredTimeline.getTimeline() != timeline2) {
                DeferredMediaPeriod deferredMediaPeriod;
                DeferredMediaPeriod deferredMediaPeriod2;
                long windowStartPositionUs;
                long periodPreparePositionUs;
                long windowStartPositionUs2;
                Pair<Object, Long> periodPosition;
                Object periodUid;
                int windowOffsetUpdate = timeline.getWindowCount() - deferredTimeline.getWindowCount();
                int periodOffsetUpdate = timeline.getPeriodCount() - deferredTimeline.getPeriodCount();
                if (windowOffsetUpdate == 0) {
                    if (periodOffsetUpdate == 0) {
                        if (mediaSourceHolder2.isPrepared) {
                            mediaSourceHolder2.timeline = deferredTimeline.cloneWithUpdatedTimeline(timeline2);
                        } else if (timeline.isEmpty()) {
                            Assertions.checkState(mediaSourceHolder2.activeMediaPeriods.size() > 1);
                            if (mediaSourceHolder2.activeMediaPeriods.isEmpty()) {
                                deferredMediaPeriod = (DeferredMediaPeriod) mediaSourceHolder2.activeMediaPeriods.get(0);
                            } else {
                                deferredMediaPeriod = null;
                            }
                            deferredMediaPeriod2 = deferredMediaPeriod;
                            windowStartPositionUs = concatenatingMediaSource.window.getDefaultPositionUs();
                            if (deferredMediaPeriod2 != null) {
                                periodPreparePositionUs = deferredMediaPeriod2.getPreparePositionUs();
                                if (periodPreparePositionUs != 0) {
                                    windowStartPositionUs2 = periodPreparePositionUs;
                                    periodPosition = timeline.getPeriodPosition(concatenatingMediaSource.window, concatenatingMediaSource.period, 0, windowStartPositionUs2);
                                    periodUid = periodPosition.first;
                                    periodPreparePositionUs = ((Long) periodPosition.second).longValue();
                                    mediaSourceHolder2.timeline = DeferredTimeline.createWithRealTimeline(timeline2, periodUid);
                                    if (deferredMediaPeriod2 == null) {
                                        deferredMediaPeriod2.overridePreparePositionUs(periodPreparePositionUs);
                                        deferredMediaPeriod2.createPeriod(deferredMediaPeriod2.id.copyWithPeriodUid(getChildPeriodUid(mediaSourceHolder2, deferredMediaPeriod2.id.periodUid)));
                                    }
                                }
                            }
                            windowStartPositionUs2 = windowStartPositionUs;
                            periodPosition = timeline.getPeriodPosition(concatenatingMediaSource.window, concatenatingMediaSource.period, 0, windowStartPositionUs2);
                            periodUid = periodPosition.first;
                            periodPreparePositionUs = ((Long) periodPosition.second).longValue();
                            mediaSourceHolder2.timeline = DeferredTimeline.createWithRealTimeline(timeline2, periodUid);
                            if (deferredMediaPeriod2 == null) {
                                deferredMediaPeriod2.overridePreparePositionUs(periodPreparePositionUs);
                                deferredMediaPeriod2.createPeriod(deferredMediaPeriod2.id.copyWithPeriodUid(getChildPeriodUid(mediaSourceHolder2, deferredMediaPeriod2.id.periodUid)));
                            }
                        } else {
                            mediaSourceHolder2.timeline = DeferredTimeline.createWithRealTimeline(timeline2, DeferredTimeline.DUMMY_ID);
                        }
                        mediaSourceHolder2.isPrepared = true;
                        scheduleListenerNotification(null);
                        return;
                    }
                }
                correctOffsets(mediaSourceHolder2.childIndex + 1, 0, windowOffsetUpdate, periodOffsetUpdate);
                if (mediaSourceHolder2.isPrepared) {
                    mediaSourceHolder2.timeline = deferredTimeline.cloneWithUpdatedTimeline(timeline2);
                } else if (timeline.isEmpty()) {
                    if (mediaSourceHolder2.activeMediaPeriods.size() > 1) {
                    }
                    Assertions.checkState(mediaSourceHolder2.activeMediaPeriods.size() > 1);
                    if (mediaSourceHolder2.activeMediaPeriods.isEmpty()) {
                        deferredMediaPeriod = (DeferredMediaPeriod) mediaSourceHolder2.activeMediaPeriods.get(0);
                    } else {
                        deferredMediaPeriod = null;
                    }
                    deferredMediaPeriod2 = deferredMediaPeriod;
                    windowStartPositionUs = concatenatingMediaSource.window.getDefaultPositionUs();
                    if (deferredMediaPeriod2 != null) {
                        periodPreparePositionUs = deferredMediaPeriod2.getPreparePositionUs();
                        if (periodPreparePositionUs != 0) {
                            windowStartPositionUs2 = periodPreparePositionUs;
                            periodPosition = timeline.getPeriodPosition(concatenatingMediaSource.window, concatenatingMediaSource.period, 0, windowStartPositionUs2);
                            periodUid = periodPosition.first;
                            periodPreparePositionUs = ((Long) periodPosition.second).longValue();
                            mediaSourceHolder2.timeline = DeferredTimeline.createWithRealTimeline(timeline2, periodUid);
                            if (deferredMediaPeriod2 == null) {
                                deferredMediaPeriod2.overridePreparePositionUs(periodPreparePositionUs);
                                deferredMediaPeriod2.createPeriod(deferredMediaPeriod2.id.copyWithPeriodUid(getChildPeriodUid(mediaSourceHolder2, deferredMediaPeriod2.id.periodUid)));
                            }
                        }
                    }
                    windowStartPositionUs2 = windowStartPositionUs;
                    periodPosition = timeline.getPeriodPosition(concatenatingMediaSource.window, concatenatingMediaSource.period, 0, windowStartPositionUs2);
                    periodUid = periodPosition.first;
                    periodPreparePositionUs = ((Long) periodPosition.second).longValue();
                    mediaSourceHolder2.timeline = DeferredTimeline.createWithRealTimeline(timeline2, periodUid);
                    if (deferredMediaPeriod2 == null) {
                        deferredMediaPeriod2.overridePreparePositionUs(periodPreparePositionUs);
                        deferredMediaPeriod2.createPeriod(deferredMediaPeriod2.id.copyWithPeriodUid(getChildPeriodUid(mediaSourceHolder2, deferredMediaPeriod2.id.periodUid)));
                    }
                } else {
                    mediaSourceHolder2.timeline = DeferredTimeline.createWithRealTimeline(timeline2, DeferredTimeline.DUMMY_ID);
                }
                mediaSourceHolder2.isPrepared = true;
                scheduleListenerNotification(null);
                return;
            }
            return;
        }
        throw new IllegalArgumentException();
    }

    private void removeMediaSourceInternal(int index) {
        MediaSourceHolder holder = (MediaSourceHolder) this.mediaSourceHolders.remove(index);
        this.mediaSourceByUid.remove(holder.uid);
        Timeline oldTimeline = holder.timeline;
        correctOffsets(index, -1, -oldTimeline.getWindowCount(), -oldTimeline.getPeriodCount());
        holder.isRemoved = true;
        maybeReleaseChildSource(holder);
    }

    private void moveMediaSourceInternal(int currentIndex, int newIndex) {
        int startIndex = Math.min(currentIndex, newIndex);
        int endIndex = Math.max(currentIndex, newIndex);
        int windowOffset = ((MediaSourceHolder) this.mediaSourceHolders.get(startIndex)).firstWindowIndexInChild;
        int periodOffset = ((MediaSourceHolder) this.mediaSourceHolders.get(startIndex)).firstPeriodIndexInChild;
        List list = this.mediaSourceHolders;
        list.add(newIndex, list.remove(currentIndex));
        for (int i = startIndex; i <= endIndex; i++) {
            MediaSourceHolder holder = (MediaSourceHolder) this.mediaSourceHolders.get(i);
            holder.firstWindowIndexInChild = windowOffset;
            holder.firstPeriodIndexInChild = periodOffset;
            windowOffset += holder.timeline.getWindowCount();
            periodOffset += holder.timeline.getPeriodCount();
        }
    }

    private void correctOffsets(int startIndex, int childIndexUpdate, int windowOffsetUpdate, int periodOffsetUpdate) {
        this.windowCount += windowOffsetUpdate;
        this.periodCount += periodOffsetUpdate;
        for (int i = startIndex; i < this.mediaSourceHolders.size(); i++) {
            MediaSourceHolder mediaSourceHolder = (MediaSourceHolder) this.mediaSourceHolders.get(i);
            mediaSourceHolder.childIndex += childIndexUpdate;
            mediaSourceHolder = (MediaSourceHolder) this.mediaSourceHolders.get(i);
            mediaSourceHolder.firstWindowIndexInChild += windowOffsetUpdate;
            mediaSourceHolder = (MediaSourceHolder) this.mediaSourceHolders.get(i);
            mediaSourceHolder.firstPeriodIndexInChild += periodOffsetUpdate;
        }
    }

    private void maybeReleaseChildSource(MediaSourceHolder mediaSourceHolder) {
        if (!mediaSourceHolder.isRemoved || !mediaSourceHolder.hasStartedPreparing) {
            return;
        }
        if (mediaSourceHolder.activeMediaPeriods.isEmpty()) {
            releaseChildSource(mediaSourceHolder);
        }
    }

    private static Object getMediaSourceHolderUid(Object periodUid) {
        return AbstractConcatenatedTimeline.getChildTimelineUidFromConcatenatedUid(periodUid);
    }

    private static Object getChildPeriodUid(MediaSourceHolder holder, Object periodUid) {
        Object childUid = AbstractConcatenatedTimeline.getChildPeriodUidFromConcatenatedUid(periodUid);
        return childUid.equals(DeferredTimeline.DUMMY_ID) ? holder.timeline.replacedId : childUid;
    }

    private static Object getPeriodUid(MediaSourceHolder holder, Object childPeriodUid) {
        if (holder.timeline.replacedId.equals(childPeriodUid)) {
            childPeriodUid = DeferredTimeline.DUMMY_ID;
        }
        return AbstractConcatenatedTimeline.getConcatenatedUid(holder.uid, childPeriodUid);
    }
}
