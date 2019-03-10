package com.google.android.exoplayer2;

import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;

final class PlaybackInfo {
    private static final MediaPeriodId DUMMY_MEDIA_PERIOD_ID = new MediaPeriodId(new Object());
    public volatile long bufferedPositionUs;
    public final long contentPositionUs;
    public final boolean isLoading;
    public final MediaPeriodId loadingMediaPeriodId;
    @Nullable
    public final Object manifest;
    public final MediaPeriodId periodId;
    public final int playbackState;
    public volatile long positionUs;
    public final long startPositionUs;
    public final Timeline timeline;
    public volatile long totalBufferedDurationUs;
    public final TrackGroupArray trackGroups;
    public final TrackSelectorResult trackSelectorResult;

    public static PlaybackInfo createDummy(long startPositionUs, TrackSelectorResult emptyTrackSelectorResult) {
        return new PlaybackInfo(Timeline.EMPTY, null, DUMMY_MEDIA_PERIOD_ID, startPositionUs, C0555C.TIME_UNSET, 1, false, TrackGroupArray.EMPTY, emptyTrackSelectorResult, DUMMY_MEDIA_PERIOD_ID, startPositionUs, 0, startPositionUs);
    }

    public PlaybackInfo(Timeline timeline, @Nullable Object manifest, MediaPeriodId periodId, long startPositionUs, long contentPositionUs, int playbackState, boolean isLoading, TrackGroupArray trackGroups, TrackSelectorResult trackSelectorResult, MediaPeriodId loadingMediaPeriodId, long bufferedPositionUs, long totalBufferedDurationUs, long positionUs) {
        this.timeline = timeline;
        this.manifest = manifest;
        this.periodId = periodId;
        this.startPositionUs = startPositionUs;
        this.contentPositionUs = contentPositionUs;
        this.playbackState = playbackState;
        this.isLoading = isLoading;
        this.trackGroups = trackGroups;
        this.trackSelectorResult = trackSelectorResult;
        this.loadingMediaPeriodId = loadingMediaPeriodId;
        this.bufferedPositionUs = bufferedPositionUs;
        this.totalBufferedDurationUs = totalBufferedDurationUs;
        this.positionUs = positionUs;
    }

    public MediaPeriodId getDummyFirstMediaPeriodId(boolean shuffleModeEnabled, Window window) {
        if (this.timeline.isEmpty()) {
            return DUMMY_MEDIA_PERIOD_ID;
        }
        Timeline timeline = this.timeline;
        return new MediaPeriodId(this.timeline.getUidOfPeriod(timeline.getWindow(timeline.getFirstWindowIndex(shuffleModeEnabled), window).firstPeriodIndex));
    }

    @CheckResult
    public PlaybackInfo resetToNewPosition(MediaPeriodId periodId, long startPositionUs, long contentPositionUs) {
        return new PlaybackInfo(this.timeline, this.manifest, periodId, startPositionUs, periodId.isAd() ? contentPositionUs : C0555C.TIME_UNSET, r0.playbackState, r0.isLoading, r0.trackGroups, r0.trackSelectorResult, periodId, startPositionUs, 0, startPositionUs);
    }

    @CheckResult
    public PlaybackInfo copyWithNewPosition(MediaPeriodId periodId, long positionUs, long contentPositionUs, long totalBufferedDurationUs) {
        return new PlaybackInfo(this.timeline, this.manifest, periodId, positionUs, periodId.isAd() ? contentPositionUs : C0555C.TIME_UNSET, r0.playbackState, r0.isLoading, r0.trackGroups, r0.trackSelectorResult, r0.loadingMediaPeriodId, r0.bufferedPositionUs, totalBufferedDurationUs, positionUs);
    }

    @CheckResult
    public PlaybackInfo copyWithTimeline(Timeline timeline, Object manifest) {
        return new PlaybackInfo(timeline, manifest, this.periodId, this.startPositionUs, this.contentPositionUs, this.playbackState, this.isLoading, this.trackGroups, this.trackSelectorResult, this.loadingMediaPeriodId, this.bufferedPositionUs, this.totalBufferedDurationUs, this.positionUs);
    }

    @CheckResult
    public PlaybackInfo copyWithPlaybackState(int playbackState) {
        int i = playbackState;
        return new PlaybackInfo(this.timeline, this.manifest, this.periodId, this.startPositionUs, this.contentPositionUs, i, this.isLoading, this.trackGroups, this.trackSelectorResult, this.loadingMediaPeriodId, this.bufferedPositionUs, this.totalBufferedDurationUs, this.positionUs);
    }

    @CheckResult
    public PlaybackInfo copyWithIsLoading(boolean isLoading) {
        boolean z = isLoading;
        return new PlaybackInfo(this.timeline, this.manifest, this.periodId, this.startPositionUs, this.contentPositionUs, this.playbackState, z, this.trackGroups, this.trackSelectorResult, this.loadingMediaPeriodId, this.bufferedPositionUs, this.totalBufferedDurationUs, this.positionUs);
    }

    @CheckResult
    public PlaybackInfo copyWithTrackInfo(TrackGroupArray trackGroups, TrackSelectorResult trackSelectorResult) {
        TrackGroupArray trackGroupArray = trackGroups;
        TrackSelectorResult trackSelectorResult2 = trackSelectorResult;
        return new PlaybackInfo(this.timeline, this.manifest, this.periodId, this.startPositionUs, this.contentPositionUs, this.playbackState, this.isLoading, trackGroupArray, trackSelectorResult2, this.loadingMediaPeriodId, this.bufferedPositionUs, this.totalBufferedDurationUs, this.positionUs);
    }

    @CheckResult
    public PlaybackInfo copyWithLoadingMediaPeriodId(MediaPeriodId loadingMediaPeriodId) {
        MediaPeriodId mediaPeriodId = loadingMediaPeriodId;
        return new PlaybackInfo(this.timeline, this.manifest, this.periodId, this.startPositionUs, this.contentPositionUs, this.playbackState, this.isLoading, this.trackGroups, this.trackSelectorResult, mediaPeriodId, this.bufferedPositionUs, this.totalBufferedDurationUs, this.positionUs);
    }
}
