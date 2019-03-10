package com.google.android.exoplayer2.source;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.ShuffleOrder.UnshuffledShuffleOrder;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.util.HashMap;
import java.util.Map;

public final class LoopingMediaSource extends CompositeMediaSource<Void> {
    private final Map<MediaPeriodId, MediaPeriodId> childMediaPeriodIdToMediaPeriodId;
    private final MediaSource childSource;
    private final int loopCount;
    private final Map<MediaPeriod, MediaPeriodId> mediaPeriodToChildMediaPeriodId;

    private static final class InfinitelyLoopingTimeline extends ForwardingTimeline {
        public InfinitelyLoopingTimeline(Timeline timeline) {
            super(timeline);
        }

        public int getNextWindowIndex(int windowIndex, int repeatMode, boolean shuffleModeEnabled) {
            int childNextWindowIndex = this.timeline.getNextWindowIndex(windowIndex, repeatMode, shuffleModeEnabled);
            return childNextWindowIndex == -1 ? getFirstWindowIndex(shuffleModeEnabled) : childNextWindowIndex;
        }

        public int getPreviousWindowIndex(int windowIndex, int repeatMode, boolean shuffleModeEnabled) {
            int childPreviousWindowIndex = this.timeline.getPreviousWindowIndex(windowIndex, repeatMode, shuffleModeEnabled);
            return childPreviousWindowIndex == -1 ? getLastWindowIndex(shuffleModeEnabled) : childPreviousWindowIndex;
        }
    }

    private static final class LoopingTimeline extends AbstractConcatenatedTimeline {
        private final int childPeriodCount;
        private final Timeline childTimeline;
        private final int childWindowCount;
        private final int loopCount;

        public LoopingTimeline(Timeline childTimeline, int loopCount) {
            boolean z = false;
            super(false, new UnshuffledShuffleOrder(loopCount));
            this.childTimeline = childTimeline;
            this.childPeriodCount = childTimeline.getPeriodCount();
            this.childWindowCount = childTimeline.getWindowCount();
            this.loopCount = loopCount;
            int i = this.childPeriodCount;
            if (i > 0) {
                if (loopCount <= Integer.MAX_VALUE / i) {
                    z = true;
                }
                Assertions.checkState(z, "LoopingMediaSource contains too many periods");
            }
        }

        public int getWindowCount() {
            return this.childWindowCount * this.loopCount;
        }

        public int getPeriodCount() {
            return this.childPeriodCount * this.loopCount;
        }

        protected int getChildIndexByPeriodIndex(int periodIndex) {
            return periodIndex / this.childPeriodCount;
        }

        protected int getChildIndexByWindowIndex(int windowIndex) {
            return windowIndex / this.childWindowCount;
        }

        protected int getChildIndexByChildUid(Object childUid) {
            if (childUid instanceof Integer) {
                return ((Integer) childUid).intValue();
            }
            return -1;
        }

        protected Timeline getTimelineByChildIndex(int childIndex) {
            return this.childTimeline;
        }

        protected int getFirstPeriodIndexByChildIndex(int childIndex) {
            return this.childPeriodCount * childIndex;
        }

        protected int getFirstWindowIndexByChildIndex(int childIndex) {
            return this.childWindowCount * childIndex;
        }

        protected Object getChildUidByChildIndex(int childIndex) {
            return Integer.valueOf(childIndex);
        }
    }

    public LoopingMediaSource(MediaSource childSource) {
        this(childSource, Integer.MAX_VALUE);
    }

    public LoopingMediaSource(MediaSource childSource, int loopCount) {
        Assertions.checkArgument(loopCount > 0);
        this.childSource = childSource;
        this.loopCount = loopCount;
        this.childMediaPeriodIdToMediaPeriodId = new HashMap();
        this.mediaPeriodToChildMediaPeriodId = new HashMap();
    }

    @Nullable
    public Object getTag() {
        return this.childSource.getTag();
    }

    public void prepareSourceInternal(ExoPlayer player, boolean isTopLevelSource, @Nullable TransferListener mediaTransferListener) {
        super.prepareSourceInternal(player, isTopLevelSource, mediaTransferListener);
        prepareChildSource(null, this.childSource);
    }

    public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        if (this.loopCount == Integer.MAX_VALUE) {
            return this.childSource.createPeriod(id, allocator);
        }
        MediaPeriodId childMediaPeriodId = id.copyWithPeriodUid(AbstractConcatenatedTimeline.getChildPeriodUidFromConcatenatedUid(id.periodUid));
        this.childMediaPeriodIdToMediaPeriodId.put(childMediaPeriodId, id);
        MediaPeriod mediaPeriod = this.childSource.createPeriod(childMediaPeriodId, allocator);
        this.mediaPeriodToChildMediaPeriodId.put(mediaPeriod, childMediaPeriodId);
        return mediaPeriod;
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        this.childSource.releasePeriod(mediaPeriod);
        MediaPeriodId childMediaPeriodId = (MediaPeriodId) this.mediaPeriodToChildMediaPeriodId.remove(mediaPeriod);
        if (childMediaPeriodId != null) {
            this.childMediaPeriodIdToMediaPeriodId.remove(childMediaPeriodId);
        }
    }

    protected void onChildSourceInfoRefreshed(Void id, MediaSource mediaSource, Timeline timeline, @Nullable Object manifest) {
        int i = this.loopCount;
        refreshSourceInfo(i != Integer.MAX_VALUE ? new LoopingTimeline(timeline, i) : new InfinitelyLoopingTimeline(timeline), manifest);
    }

    @Nullable
    protected MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(Void id, MediaPeriodId mediaPeriodId) {
        return this.loopCount != Integer.MAX_VALUE ? (MediaPeriodId) this.childMediaPeriodIdToMediaPeriodId.get(mediaPeriodId) : mediaPeriodId;
    }
}
