package com.google.android.exoplayer2.trackselection;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.Util;
import java.util.List;

public class AdaptiveTrackSelection extends BaseTrackSelection {
    public static final float DEFAULT_BANDWIDTH_FRACTION = 0.75f;
    public static final float DEFAULT_BUFFERED_FRACTION_TO_LIVE_EDGE_FOR_QUALITY_INCREASE = 0.75f;
    public static final int DEFAULT_MAX_DURATION_FOR_QUALITY_DECREASE_MS = 25000;
    public static final int DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS = 10000;
    public static final int DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS = 25000;
    public static final long DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS = 2000;
    private final float bandwidthFraction;
    private final BandwidthMeter bandwidthMeter;
    private final float bufferedFractionToLiveEdgeForQualityIncrease;
    private final Clock clock;
    private long lastBufferEvaluationMs;
    private final long maxDurationForQualityDecreaseUs;
    private final long minDurationForQualityIncreaseUs;
    private final long minDurationToRetainAfterDiscardUs;
    private final long minTimeBetweenBufferReevaluationMs;
    private float playbackSpeed;
    private int reason;
    private int selectedIndex;

    public static final class Factory implements com.google.android.exoplayer2.trackselection.TrackSelection.Factory {
        private final float bandwidthFraction;
        @Nullable
        private final BandwidthMeter bandwidthMeter;
        private final float bufferedFractionToLiveEdgeForQualityIncrease;
        private final Clock clock;
        private final int maxDurationForQualityDecreaseMs;
        private final int minDurationForQualityIncreaseMs;
        private final int minDurationToRetainAfterDiscardMs;
        private final long minTimeBetweenBufferReevaluationMs;

        public Factory() {
            this(10000, 25000, 25000, 0.75f, 0.75f, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, Clock.DEFAULT);
        }

        @Deprecated
        public Factory(BandwidthMeter bandwidthMeter) {
            this(bandwidthMeter, 10000, 25000, 25000, 0.75f, 0.75f, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, Clock.DEFAULT);
        }

        public Factory(int minDurationForQualityIncreaseMs, int maxDurationForQualityDecreaseMs, int minDurationToRetainAfterDiscardMs, float bandwidthFraction) {
            this(minDurationForQualityIncreaseMs, maxDurationForQualityDecreaseMs, minDurationToRetainAfterDiscardMs, bandwidthFraction, 0.75f, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, Clock.DEFAULT);
        }

        @Deprecated
        public Factory(BandwidthMeter bandwidthMeter, int minDurationForQualityIncreaseMs, int maxDurationForQualityDecreaseMs, int minDurationToRetainAfterDiscardMs, float bandwidthFraction) {
            this(bandwidthMeter, minDurationForQualityIncreaseMs, maxDurationForQualityDecreaseMs, minDurationToRetainAfterDiscardMs, bandwidthFraction, 0.75f, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, Clock.DEFAULT);
        }

        public Factory(int minDurationForQualityIncreaseMs, int maxDurationForQualityDecreaseMs, int minDurationToRetainAfterDiscardMs, float bandwidthFraction, float bufferedFractionToLiveEdgeForQualityIncrease, long minTimeBetweenBufferReevaluationMs, Clock clock) {
            this(null, minDurationForQualityIncreaseMs, maxDurationForQualityDecreaseMs, minDurationToRetainAfterDiscardMs, bandwidthFraction, bufferedFractionToLiveEdgeForQualityIncrease, minTimeBetweenBufferReevaluationMs, clock);
        }

        @Deprecated
        public Factory(@Nullable BandwidthMeter bandwidthMeter, int minDurationForQualityIncreaseMs, int maxDurationForQualityDecreaseMs, int minDurationToRetainAfterDiscardMs, float bandwidthFraction, float bufferedFractionToLiveEdgeForQualityIncrease, long minTimeBetweenBufferReevaluationMs, Clock clock) {
            this.bandwidthMeter = bandwidthMeter;
            this.minDurationForQualityIncreaseMs = minDurationForQualityIncreaseMs;
            this.maxDurationForQualityDecreaseMs = maxDurationForQualityDecreaseMs;
            this.minDurationToRetainAfterDiscardMs = minDurationToRetainAfterDiscardMs;
            this.bandwidthFraction = bandwidthFraction;
            this.bufferedFractionToLiveEdgeForQualityIncrease = bufferedFractionToLiveEdgeForQualityIncrease;
            this.minTimeBetweenBufferReevaluationMs = minTimeBetweenBufferReevaluationMs;
            this.clock = clock;
        }

        public AdaptiveTrackSelection createTrackSelection(TrackGroup group, BandwidthMeter bandwidthMeter, int... tracks) {
            BandwidthMeter bandwidthMeter2;
            if (this.bandwidthMeter != null) {
                bandwidthMeter2 = r0.bandwidthMeter;
            } else {
                bandwidthMeter2 = bandwidthMeter;
            }
            return new AdaptiveTrackSelection(group, tracks, bandwidthMeter2, (long) r0.minDurationForQualityIncreaseMs, (long) r0.maxDurationForQualityDecreaseMs, (long) r0.minDurationToRetainAfterDiscardMs, r0.bandwidthFraction, r0.bufferedFractionToLiveEdgeForQualityIncrease, r0.minTimeBetweenBufferReevaluationMs, r0.clock);
        }
    }

    public AdaptiveTrackSelection(TrackGroup group, int[] tracks, BandwidthMeter bandwidthMeter) {
        this(group, tracks, bandwidthMeter, 10000, 25000, 25000, 0.75f, 0.75f, DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, Clock.DEFAULT);
    }

    public AdaptiveTrackSelection(TrackGroup group, int[] tracks, BandwidthMeter bandwidthMeter, long minDurationForQualityIncreaseMs, long maxDurationForQualityDecreaseMs, long minDurationToRetainAfterDiscardMs, float bandwidthFraction, float bufferedFractionToLiveEdgeForQualityIncrease, long minTimeBetweenBufferReevaluationMs, Clock clock) {
        super(group, tracks);
        this.bandwidthMeter = bandwidthMeter;
        this.minDurationForQualityIncreaseUs = minDurationForQualityIncreaseMs * 1000;
        this.maxDurationForQualityDecreaseUs = maxDurationForQualityDecreaseMs * 1000;
        this.minDurationToRetainAfterDiscardUs = 1000 * minDurationToRetainAfterDiscardMs;
        this.bandwidthFraction = bandwidthFraction;
        this.bufferedFractionToLiveEdgeForQualityIncrease = bufferedFractionToLiveEdgeForQualityIncrease;
        this.minTimeBetweenBufferReevaluationMs = minTimeBetweenBufferReevaluationMs;
        this.clock = clock;
        this.playbackSpeed = 1.0f;
        this.reason = 1;
        this.lastBufferEvaluationMs = C0555C.TIME_UNSET;
        this.selectedIndex = determineIdealSelectedIndex(0);
    }

    public void enable() {
        this.lastBufferEvaluationMs = C0555C.TIME_UNSET;
    }

    public void onPlaybackSpeed(float playbackSpeed) {
        this.playbackSpeed = playbackSpeed;
    }

    public void updateSelectedTrack(long playbackPositionUs, long bufferedDurationUs, long availableDurationUs, List<? extends MediaChunk> list, MediaChunkIterator[] mediaChunkIterators) {
        long nowMs = this.clock.elapsedRealtime();
        int currentSelectedIndex = this.selectedIndex;
        this.selectedIndex = determineIdealSelectedIndex(nowMs);
        if (this.selectedIndex != currentSelectedIndex) {
            long j;
            if (isBlacklisted(currentSelectedIndex, nowMs)) {
                j = availableDurationUs;
            } else {
                Format currentFormat = getFormat(currentSelectedIndex);
                Format selectedFormat = getFormat(r0.selectedIndex);
                if (selectedFormat.bitrate <= currentFormat.bitrate) {
                    j = availableDurationUs;
                } else if (bufferedDurationUs < minDurationForQualityIncreaseUs(availableDurationUs)) {
                    r0.selectedIndex = currentSelectedIndex;
                }
                if (selectedFormat.bitrate < currentFormat.bitrate && bufferedDurationUs >= r0.maxDurationForQualityDecreaseUs) {
                    r0.selectedIndex = currentSelectedIndex;
                }
            }
            if (r0.selectedIndex != currentSelectedIndex) {
                r0.reason = 3;
            }
        }
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public int getSelectionReason() {
        return this.reason;
    }

    @Nullable
    public Object getSelectionData() {
        return null;
    }

    public int evaluateQueueSize(long playbackPositionUs, List<? extends MediaChunk> queue) {
        List list = queue;
        long nowMs = this.clock.elapsedRealtime();
        long j = this.lastBufferEvaluationMs;
        if (j != C0555C.TIME_UNSET && nowMs - j < r0.minTimeBetweenBufferReevaluationMs) {
            return queue.size();
        }
        r0.lastBufferEvaluationMs = nowMs;
        if (queue.isEmpty()) {
            return 0;
        }
        int queueSize = queue.size();
        if (Util.getPlayoutDurationForMediaDuration(((MediaChunk) list.get(queueSize - 1)).startTimeUs - playbackPositionUs, r0.playbackSpeed) < r0.minDurationToRetainAfterDiscardUs) {
            return queueSize;
        }
        Format idealFormat = getFormat(determineIdealSelectedIndex(nowMs));
        int i = 0;
        while (i < queueSize) {
            MediaChunk chunk = (MediaChunk) list.get(i);
            Format format = chunk.trackFormat;
            long nowMs2 = nowMs;
            if (Util.getPlayoutDurationForMediaDuration(chunk.startTimeUs - playbackPositionUs, r0.playbackSpeed) >= r0.minDurationToRetainAfterDiscardUs && format.bitrate < idealFormat.bitrate && format.height != -1 && format.height < 720 && format.width != -1 && format.width < 1280 && format.height < idealFormat.height) {
                return i;
            }
            i++;
            nowMs = nowMs2;
            List<? extends MediaChunk> list2 = queue;
        }
        return queueSize;
    }

    private int determineIdealSelectedIndex(long nowMs) {
        long effectiveBitrate = (long) (((float) this.bandwidthMeter.getBitrateEstimate()) * this.bandwidthFraction);
        int lowestBitrateNonBlacklistedIndex = 0;
        for (int i = 0; i < this.length; i++) {
            if (nowMs != Long.MIN_VALUE) {
                if (isBlacklisted(i, nowMs)) {
                }
            }
            if (((long) Math.round(((float) getFormat(i).bitrate) * this.playbackSpeed)) <= effectiveBitrate) {
                return i;
            }
            lowestBitrateNonBlacklistedIndex = i;
        }
        return lowestBitrateNonBlacklistedIndex;
    }

    private long minDurationForQualityIncreaseUs(long availableDurationUs) {
        boolean isAvailableDurationTooShort = availableDurationUs != C0555C.TIME_UNSET && availableDurationUs <= this.minDurationForQualityIncreaseUs;
        return isAvailableDurationTooShort ? (long) (((float) availableDurationUs) * this.bufferedFractionToLiveEdgeForQualityIncrease) : this.minDurationForQualityIncreaseUs;
    }
}
