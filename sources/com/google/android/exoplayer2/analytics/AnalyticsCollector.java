package com.google.android.exoplayer2.analytics;

import android.support.annotation.Nullable;
import android.view.Surface;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player$EventListener;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.analytics.AnalyticsListener.EventTime;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.MediaSourceEventListener$LoadEventInfo;
import com.google.android.exoplayer2.source.MediaSourceEventListener$MediaLoadData;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.BandwidthMeter$EventListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.video.VideoListener;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

public class AnalyticsCollector implements Player$EventListener, MetadataOutput, AudioRendererEventListener, VideoRendererEventListener, MediaSourceEventListener, BandwidthMeter$EventListener, DefaultDrmSessionEventListener, VideoListener, AudioListener {
    private final Clock clock;
    private final CopyOnWriteArraySet<AnalyticsListener> listeners;
    private final AnalyticsCollector$MediaPeriodQueueTracker mediaPeriodQueueTracker;
    private Player player;
    private final Window window;

    protected AnalyticsCollector(@Nullable Player player, Clock clock) {
        if (player != null) {
            this.player = player;
        }
        this.clock = (Clock) Assertions.checkNotNull(clock);
        this.listeners = new CopyOnWriteArraySet();
        this.mediaPeriodQueueTracker = new AnalyticsCollector$MediaPeriodQueueTracker();
        this.window = new Window();
    }

    public void addListener(AnalyticsListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(AnalyticsListener listener) {
        this.listeners.remove(listener);
    }

    public void setPlayer(Player player) {
        Assertions.checkState(this.player == null);
        this.player = (Player) Assertions.checkNotNull(player);
    }

    public final void notifySeekStarted() {
        if (!this.mediaPeriodQueueTracker.isSeeking()) {
            EventTime eventTime = generatePlayingMediaPeriodEventTime();
            this.mediaPeriodQueueTracker.onSeekStarted();
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((AnalyticsListener) it.next()).onSeekStarted(eventTime);
            }
        }
    }

    public final void resetForNewMediaSource() {
        for (AnalyticsCollector$MediaPeriodInfo mediaPeriodInfo : new ArrayList(AnalyticsCollector$MediaPeriodQueueTracker.access$000(this.mediaPeriodQueueTracker))) {
            onMediaPeriodReleased(mediaPeriodInfo.windowIndex, mediaPeriodInfo.mediaPeriodId);
        }
    }

    public final void onMetadata(Metadata metadata) {
        EventTime eventTime = generatePlayingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onMetadata(eventTime, metadata);
        }
    }

    public final void onAudioEnabled(DecoderCounters counters) {
        EventTime eventTime = generatePlayingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onDecoderEnabled(eventTime, 1, counters);
        }
    }

    public final void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
        EventTime eventTime = generateReadingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onDecoderInitialized(eventTime, 1, decoderName, initializationDurationMs);
        }
    }

    public final void onAudioInputFormatChanged(Format format) {
        EventTime eventTime = generateReadingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onDecoderInputFormatChanged(eventTime, 1, format);
        }
    }

    public final void onAudioSinkUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
        EventTime eventTime = generateReadingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onAudioUnderrun(eventTime, bufferSize, bufferSizeMs, elapsedSinceLastFeedMs);
        }
    }

    public final void onAudioDisabled(DecoderCounters counters) {
        EventTime eventTime = generateLastReportedPlayingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onDecoderDisabled(eventTime, 1, counters);
        }
    }

    public final void onAudioSessionId(int audioSessionId) {
        EventTime eventTime = generateReadingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onAudioSessionId(eventTime, audioSessionId);
        }
    }

    public void onAudioAttributesChanged(AudioAttributes audioAttributes) {
        EventTime eventTime = generateReadingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onAudioAttributesChanged(eventTime, audioAttributes);
        }
    }

    public void onVolumeChanged(float audioVolume) {
        EventTime eventTime = generateReadingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onVolumeChanged(eventTime, audioVolume);
        }
    }

    public final void onVideoEnabled(DecoderCounters counters) {
        EventTime eventTime = generatePlayingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onDecoderEnabled(eventTime, 2, counters);
        }
    }

    public final void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
        EventTime eventTime = generateReadingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onDecoderInitialized(eventTime, 2, decoderName, initializationDurationMs);
        }
    }

    public final void onVideoInputFormatChanged(Format format) {
        EventTime eventTime = generateReadingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onDecoderInputFormatChanged(eventTime, 2, format);
        }
    }

    public final void onDroppedFrames(int count, long elapsedMs) {
        EventTime eventTime = generateLastReportedPlayingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onDroppedVideoFrames(eventTime, count, elapsedMs);
        }
    }

    public final void onVideoDisabled(DecoderCounters counters) {
        EventTime eventTime = generateLastReportedPlayingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onDecoderDisabled(eventTime, 2, counters);
        }
    }

    public final void onRenderedFirstFrame(@Nullable Surface surface) {
        EventTime eventTime = generateReadingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onRenderedFirstFrame(eventTime, surface);
        }
    }

    public final void onRenderedFirstFrame() {
    }

    public final void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        EventTime eventTime = generateReadingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onVideoSizeChanged(eventTime, width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
        }
    }

    public void onSurfaceSizeChanged(int width, int height) {
        EventTime eventTime = generateReadingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onSurfaceSizeChanged(eventTime, width, height);
        }
    }

    public final void onMediaPeriodCreated(int windowIndex, MediaPeriodId mediaPeriodId) {
        this.mediaPeriodQueueTracker.onMediaPeriodCreated(windowIndex, mediaPeriodId);
        EventTime eventTime = generateMediaPeriodEventTime(windowIndex, mediaPeriodId);
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onMediaPeriodCreated(eventTime);
        }
    }

    public final void onMediaPeriodReleased(int windowIndex, MediaPeriodId mediaPeriodId) {
        EventTime eventTime = generateMediaPeriodEventTime(windowIndex, mediaPeriodId);
        if (this.mediaPeriodQueueTracker.onMediaPeriodReleased(mediaPeriodId)) {
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((AnalyticsListener) it.next()).onMediaPeriodReleased(eventTime);
            }
        }
    }

    public final void onLoadStarted(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$LoadEventInfo loadEventInfo, MediaSourceEventListener$MediaLoadData mediaLoadData) {
        EventTime eventTime = generateMediaPeriodEventTime(windowIndex, mediaPeriodId);
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onLoadStarted(eventTime, loadEventInfo, mediaLoadData);
        }
    }

    public final void onLoadCompleted(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$LoadEventInfo loadEventInfo, MediaSourceEventListener$MediaLoadData mediaLoadData) {
        EventTime eventTime = generateMediaPeriodEventTime(windowIndex, mediaPeriodId);
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onLoadCompleted(eventTime, loadEventInfo, mediaLoadData);
        }
    }

    public final void onLoadCanceled(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$LoadEventInfo loadEventInfo, MediaSourceEventListener$MediaLoadData mediaLoadData) {
        EventTime eventTime = generateMediaPeriodEventTime(windowIndex, mediaPeriodId);
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onLoadCanceled(eventTime, loadEventInfo, mediaLoadData);
        }
    }

    public final void onLoadError(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$LoadEventInfo loadEventInfo, MediaSourceEventListener$MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
        EventTime eventTime = generateMediaPeriodEventTime(windowIndex, mediaPeriodId);
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onLoadError(eventTime, loadEventInfo, mediaLoadData, error, wasCanceled);
        }
    }

    public final void onReadingStarted(int windowIndex, MediaPeriodId mediaPeriodId) {
        this.mediaPeriodQueueTracker.onReadingStarted(mediaPeriodId);
        EventTime eventTime = generateMediaPeriodEventTime(windowIndex, mediaPeriodId);
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onReadingStarted(eventTime);
        }
    }

    public final void onUpstreamDiscarded(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$MediaLoadData mediaLoadData) {
        EventTime eventTime = generateMediaPeriodEventTime(windowIndex, mediaPeriodId);
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onUpstreamDiscarded(eventTime, mediaLoadData);
        }
    }

    public final void onDownstreamFormatChanged(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$MediaLoadData mediaLoadData) {
        EventTime eventTime = generateMediaPeriodEventTime(windowIndex, mediaPeriodId);
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onDownstreamFormatChanged(eventTime, mediaLoadData);
        }
    }

    public final void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
        this.mediaPeriodQueueTracker.onTimelineChanged(timeline);
        EventTime eventTime = generatePlayingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onTimelineChanged(eventTime, reason);
        }
    }

    public final void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        EventTime eventTime = generatePlayingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onTracksChanged(eventTime, trackGroups, trackSelections);
        }
    }

    public final void onLoadingChanged(boolean isLoading) {
        EventTime eventTime = generatePlayingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onLoadingChanged(eventTime, isLoading);
        }
    }

    public final void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        EventTime eventTime = generatePlayingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onPlayerStateChanged(eventTime, playWhenReady, playbackState);
        }
    }

    public final void onRepeatModeChanged(int repeatMode) {
        EventTime eventTime = generatePlayingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onRepeatModeChanged(eventTime, repeatMode);
        }
    }

    public final void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        EventTime eventTime = generatePlayingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onShuffleModeChanged(eventTime, shuffleModeEnabled);
        }
    }

    public final void onPlayerError(ExoPlaybackException error) {
        EventTime eventTime = generatePlayingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onPlayerError(eventTime, error);
        }
    }

    public final void onPositionDiscontinuity(int reason) {
        this.mediaPeriodQueueTracker.onPositionDiscontinuity(reason);
        EventTime eventTime = generatePlayingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onPositionDiscontinuity(eventTime, reason);
        }
    }

    public final void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        EventTime eventTime = generatePlayingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onPlaybackParametersChanged(eventTime, playbackParameters);
        }
    }

    public final void onSeekProcessed() {
        if (this.mediaPeriodQueueTracker.isSeeking()) {
            this.mediaPeriodQueueTracker.onSeekProcessed();
            EventTime eventTime = generatePlayingMediaPeriodEventTime();
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((AnalyticsListener) it.next()).onSeekProcessed(eventTime);
            }
        }
    }

    public final void onBandwidthSample(int elapsedMs, long bytes, long bitrate) {
        EventTime eventTime = generateLoadingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onBandwidthEstimate(eventTime, elapsedMs, bytes, bitrate);
        }
    }

    public final void onDrmSessionAcquired() {
        EventTime eventTime = generateReadingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onDrmSessionAcquired(eventTime);
        }
    }

    public final void onDrmKeysLoaded() {
        EventTime eventTime = generateReadingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onDrmKeysLoaded(eventTime);
        }
    }

    public final void onDrmSessionManagerError(Exception error) {
        EventTime eventTime = generateReadingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onDrmSessionManagerError(eventTime, error);
        }
    }

    public final void onDrmKeysRestored() {
        EventTime eventTime = generateReadingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onDrmKeysRestored(eventTime);
        }
    }

    public final void onDrmKeysRemoved() {
        EventTime eventTime = generateReadingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onDrmKeysRemoved(eventTime);
        }
    }

    public final void onDrmSessionReleased() {
        EventTime eventTime = generateLastReportedPlayingMediaPeriodEventTime();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((AnalyticsListener) it.next()).onDrmSessionReleased(eventTime);
        }
    }

    protected Set<AnalyticsListener> getListeners() {
        return Collections.unmodifiableSet(this.listeners);
    }

    @RequiresNonNull({"player"})
    protected EventTime generateEventTime(Timeline timeline, int windowIndex, @Nullable MediaPeriodId mediaPeriodId) {
        MediaPeriodId mediaPeriodId2;
        long eventPositionMs;
        AnalyticsCollector analyticsCollector = this;
        Timeline timeline2 = timeline;
        int i = windowIndex;
        if (timeline.isEmpty()) {
            mediaPeriodId2 = null;
        } else {
            mediaPeriodId2 = mediaPeriodId;
        }
        long realtimeMs = analyticsCollector.clock.elapsedRealtime();
        boolean z = true;
        boolean z2 = timeline2 == analyticsCollector.player.getCurrentTimeline() && i == analyticsCollector.player.getCurrentWindowIndex();
        boolean isInCurrentWindow = z2;
        long j = 0;
        if (mediaPeriodId2 != null && mediaPeriodId2.isAd()) {
            if (isInCurrentWindow) {
                if (analyticsCollector.player.getCurrentAdGroupIndex() == mediaPeriodId2.adGroupIndex) {
                    if (analyticsCollector.player.getCurrentAdIndexInAdGroup() == mediaPeriodId2.adIndexInAdGroup) {
                        if (z) {
                            j = analyticsCollector.player.getCurrentPosition();
                        }
                        eventPositionMs = j;
                    }
                }
            }
            z = false;
            if (z) {
                j = analyticsCollector.player.getCurrentPosition();
            }
            eventPositionMs = j;
        } else if (isInCurrentWindow) {
            eventPositionMs = analyticsCollector.player.getContentPosition();
        } else {
            if (!timeline.isEmpty()) {
                j = timeline2.getWindow(i, analyticsCollector.window).getDefaultPositionMs();
            }
            eventPositionMs = j;
        }
        return new EventTime(realtimeMs, timeline, windowIndex, mediaPeriodId2, eventPositionMs, analyticsCollector.player.getCurrentPosition(), analyticsCollector.player.getTotalBufferedDuration());
    }

    private EventTime generateEventTime(@Nullable AnalyticsCollector$MediaPeriodInfo mediaPeriodInfo) {
        Assertions.checkNotNull(this.player);
        if (mediaPeriodInfo == null) {
            int windowIndex = this.player.getCurrentWindowIndex();
            mediaPeriodInfo = this.mediaPeriodQueueTracker.tryResolveWindowIndex(windowIndex);
            if (mediaPeriodInfo == null) {
                Timeline timeline = this.player.getCurrentTimeline();
                return generateEventTime(windowIndex < timeline.getWindowCount() ? timeline : Timeline.EMPTY, windowIndex, null);
            }
        }
        return generateEventTime(mediaPeriodInfo.timeline, mediaPeriodInfo.windowIndex, mediaPeriodInfo.mediaPeriodId);
    }

    private EventTime generateLastReportedPlayingMediaPeriodEventTime() {
        return generateEventTime(this.mediaPeriodQueueTracker.getLastReportedPlayingMediaPeriod());
    }

    private EventTime generatePlayingMediaPeriodEventTime() {
        return generateEventTime(this.mediaPeriodQueueTracker.getPlayingMediaPeriod());
    }

    private EventTime generateReadingMediaPeriodEventTime() {
        return generateEventTime(this.mediaPeriodQueueTracker.getReadingMediaPeriod());
    }

    private EventTime generateLoadingMediaPeriodEventTime() {
        return generateEventTime(this.mediaPeriodQueueTracker.getLoadingMediaPeriod());
    }

    private EventTime generateMediaPeriodEventTime(int windowIndex, @Nullable MediaPeriodId mediaPeriodId) {
        Assertions.checkNotNull(this.player);
        if (mediaPeriodId != null) {
            EventTime generateEventTime;
            AnalyticsCollector$MediaPeriodInfo mediaPeriodInfo = this.mediaPeriodQueueTracker.getMediaPeriodInfo(mediaPeriodId);
            if (mediaPeriodInfo != null) {
                generateEventTime = generateEventTime(mediaPeriodInfo);
            } else {
                generateEventTime = generateEventTime(Timeline.EMPTY, windowIndex, mediaPeriodId);
            }
            return generateEventTime;
        }
        Timeline timeline = this.player.getCurrentTimeline();
        return generateEventTime(windowIndex < timeline.getWindowCount() ? timeline : Timeline.EMPTY, windowIndex, null);
    }
}
