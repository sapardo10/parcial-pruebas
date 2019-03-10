package com.google.android.exoplayer2.source;

import android.support.annotation.Nullable;
import android.support.v4.os.EnvironmentCompat;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.ArrayList;

public final class ClippingMediaSource extends CompositeMediaSource<Void> {
    private final boolean allowDynamicClippingUpdates;
    private IllegalClippingException clippingError;
    private ClippingTimeline clippingTimeline;
    private final boolean enableInitialDiscontinuity;
    private final long endUs;
    @Nullable
    private Object manifest;
    private final ArrayList<ClippingMediaPeriod> mediaPeriods;
    private final MediaSource mediaSource;
    private long periodEndUs;
    private long periodStartUs;
    private final boolean relativeToDefaultPosition;
    private final long startUs;
    private final Window window;

    public static final class IllegalClippingException extends IOException {
        public static final int REASON_INVALID_PERIOD_COUNT = 0;
        public static final int REASON_NOT_SEEKABLE_TO_START = 1;
        public static final int REASON_START_EXCEEDS_END = 2;
        public final int reason;

        public IllegalClippingException(int reason) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Illegal clipping: ");
            stringBuilder.append(getReasonDescription(reason));
            super(stringBuilder.toString());
            this.reason = reason;
        }

        private static String getReasonDescription(int reason) {
            switch (reason) {
                case 0:
                    return "invalid period count";
                case 1:
                    return "not seekable to start";
                case 2:
                    return "start exceeds end";
                default:
                    return EnvironmentCompat.MEDIA_UNKNOWN;
            }
        }
    }

    private static final class ClippingTimeline extends ForwardingTimeline {
        private final long durationUs;
        private final long endUs;
        private final boolean isDynamic;
        private final long startUs;

        public ClippingTimeline(Timeline timeline, long startUs, long endUs) throws IllegalClippingException {
            ClippingTimeline clippingTimeline = this;
            long j = endUs;
            super(timeline);
            boolean z = false;
            if (timeline.getPeriodCount() == 1) {
                Window window = timeline.getWindow(0, new Window());
                long startUs2 = Math.max(0, startUs);
                long resolvedEndUs = j == Long.MIN_VALUE ? window.durationUs : Math.max(0, j);
                if (window.durationUs != C0555C.TIME_UNSET) {
                    if (resolvedEndUs > window.durationUs) {
                        resolvedEndUs = window.durationUs;
                    }
                    if (startUs2 != 0) {
                        if (!window.isSeekable) {
                            throw new IllegalClippingException(1);
                        }
                    }
                    if (startUs2 > resolvedEndUs) {
                        throw new IllegalClippingException(2);
                    }
                }
                clippingTimeline.startUs = startUs2;
                clippingTimeline.endUs = resolvedEndUs;
                clippingTimeline.durationUs = resolvedEndUs == C0555C.TIME_UNSET ? C0555C.TIME_UNSET : resolvedEndUs - startUs2;
                if (window.isDynamic && (resolvedEndUs == C0555C.TIME_UNSET || (window.durationUs != C0555C.TIME_UNSET && resolvedEndUs == window.durationUs))) {
                    z = true;
                }
                clippingTimeline.isDynamic = z;
                return;
            }
            Timeline timeline2 = timeline;
            startUs2 = startUs;
            throw new IllegalClippingException(0);
        }

        public Window getWindow(int windowIndex, Window window, boolean setTag, long defaultPositionProjectionUs) {
            long j;
            this.timeline.getWindow(0, window, setTag, 0);
            window.positionInFirstPeriodUs += this.startUs;
            window.durationUs = this.durationUs;
            window.isDynamic = this.isDynamic;
            if (window.defaultPositionUs != C0555C.TIME_UNSET) {
                window.defaultPositionUs = Math.max(window.defaultPositionUs, this.startUs);
                if (this.endUs == C0555C.TIME_UNSET) {
                    j = window.defaultPositionUs;
                } else {
                    j = Math.min(window.defaultPositionUs, this.endUs);
                }
                window.defaultPositionUs = j;
                window.defaultPositionUs -= this.startUs;
            }
            j = C0555C.usToMs(this.startUs);
            if (window.presentationStartTimeMs != C0555C.TIME_UNSET) {
                window.presentationStartTimeMs += j;
            }
            if (window.windowStartTimeMs != C0555C.TIME_UNSET) {
                window.windowStartTimeMs += j;
            }
            return window;
        }

        public Period getPeriod(int periodIndex, Period period, boolean setIds) {
            this.timeline.getPeriod(0, period, setIds);
            long positionInClippedWindowUs = period.getPositionInWindowUs() - this.startUs;
            long j = this.durationUs;
            return period.set(period.id, period.uid, 0, j == C0555C.TIME_UNSET ? C0555C.TIME_UNSET : j - positionInClippedWindowUs, positionInClippedWindowUs);
        }
    }

    public ClippingMediaSource(MediaSource mediaSource, long startPositionUs, long endPositionUs) {
        this(mediaSource, startPositionUs, endPositionUs, true, false, false);
    }

    @Deprecated
    public ClippingMediaSource(MediaSource mediaSource, long startPositionUs, long endPositionUs, boolean enableInitialDiscontinuity) {
        this(mediaSource, startPositionUs, endPositionUs, enableInitialDiscontinuity, false, false);
    }

    public ClippingMediaSource(MediaSource mediaSource, long durationUs) {
        this(mediaSource, 0, durationUs, true, false, true);
    }

    public ClippingMediaSource(MediaSource mediaSource, long startPositionUs, long endPositionUs, boolean enableInitialDiscontinuity, boolean allowDynamicClippingUpdates, boolean relativeToDefaultPosition) {
        Assertions.checkArgument(startPositionUs >= 0);
        this.mediaSource = (MediaSource) Assertions.checkNotNull(mediaSource);
        this.startUs = startPositionUs;
        this.endUs = endPositionUs;
        this.enableInitialDiscontinuity = enableInitialDiscontinuity;
        this.allowDynamicClippingUpdates = allowDynamicClippingUpdates;
        this.relativeToDefaultPosition = relativeToDefaultPosition;
        this.mediaPeriods = new ArrayList();
        this.window = new Window();
    }

    @Nullable
    public Object getTag() {
        return this.mediaSource.getTag();
    }

    public void prepareSourceInternal(ExoPlayer player, boolean isTopLevelSource, @Nullable TransferListener mediaTransferListener) {
        super.prepareSourceInternal(player, isTopLevelSource, mediaTransferListener);
        prepareChildSource(null, this.mediaSource);
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        IllegalClippingException illegalClippingException = this.clippingError;
        if (illegalClippingException == null) {
            super.maybeThrowSourceInfoRefreshError();
            return;
        }
        throw illegalClippingException;
    }

    public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        ClippingMediaPeriod mediaPeriod = new ClippingMediaPeriod(this.mediaSource.createPeriod(id, allocator), this.enableInitialDiscontinuity, this.periodStartUs, this.periodEndUs);
        this.mediaPeriods.add(mediaPeriod);
        return mediaPeriod;
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        Assertions.checkState(this.mediaPeriods.remove(mediaPeriod));
        this.mediaSource.releasePeriod(((ClippingMediaPeriod) mediaPeriod).mediaPeriod);
        if (this.mediaPeriods.isEmpty() && !this.allowDynamicClippingUpdates) {
            refreshClippedTimeline(this.clippingTimeline.timeline);
        }
    }

    public void releaseSourceInternal() {
        super.releaseSourceInternal();
        this.clippingError = null;
        this.clippingTimeline = null;
    }

    protected void onChildSourceInfoRefreshed(Void id, MediaSource mediaSource, Timeline timeline, @Nullable Object manifest) {
        if (this.clippingError == null) {
            this.manifest = manifest;
            refreshClippedTimeline(timeline);
        }
    }

    private void refreshClippedTimeline(Timeline timeline) {
        long windowStartUs;
        long windowEndUs;
        long windowStartUs2;
        timeline.getWindow(0, this.window);
        long windowPositionInPeriodUs = this.window.getPositionInFirstPeriodUs();
        long j = Long.MIN_VALUE;
        if (!(this.clippingTimeline == null || r1.mediaPeriods.isEmpty())) {
            if (!r1.allowDynamicClippingUpdates) {
                windowStartUs = r1.periodStartUs - windowPositionInPeriodUs;
                if (r1.endUs != Long.MIN_VALUE) {
                    j = r1.periodEndUs - windowPositionInPeriodUs;
                }
                windowEndUs = j;
                windowStartUs2 = windowStartUs;
                r1.clippingTimeline = new ClippingTimeline(timeline, windowStartUs2, windowEndUs);
                refreshSourceInfo(r1.clippingTimeline, r1.manifest);
            }
        }
        windowStartUs = r1.startUs;
        long windowEndUs2 = r1.endUs;
        if (r1.relativeToDefaultPosition) {
            windowStartUs2 = r1.window.getDefaultPositionUs();
            windowStartUs += windowStartUs2;
            windowEndUs2 += windowStartUs2;
        }
        r1.periodStartUs = windowPositionInPeriodUs + windowStartUs;
        if (r1.endUs != Long.MIN_VALUE) {
            j = windowPositionInPeriodUs + windowEndUs2;
        }
        r1.periodEndUs = j;
        int count = r1.mediaPeriods.size();
        for (int i = 0; i < count; i++) {
            ((ClippingMediaPeriod) r1.mediaPeriods.get(i)).updateClipping(r1.periodStartUs, r1.periodEndUs);
        }
        windowStartUs2 = windowStartUs;
        windowEndUs = windowEndUs2;
        try {
            r1.clippingTimeline = new ClippingTimeline(timeline, windowStartUs2, windowEndUs);
            refreshSourceInfo(r1.clippingTimeline, r1.manifest);
        } catch (IllegalClippingException e) {
            r1.clippingError = e;
        }
    }

    protected long getMediaTimeForChildMediaTime(Void id, long mediaTimeMs) {
        if (mediaTimeMs == C0555C.TIME_UNSET) {
            return C0555C.TIME_UNSET;
        }
        long startMs = C0555C.usToMs(this.startUs);
        long clippedTimeMs = Math.max(0, mediaTimeMs - startMs);
        long j = this.endUs;
        if (j != Long.MIN_VALUE) {
            clippedTimeMs = Math.min(C0555C.usToMs(j) - startMs, clippedTimeMs);
        }
        return clippedTimeMs;
    }
}
