package com.google.android.exoplayer2.analytics;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import java.util.ArrayList;
import java.util.HashMap;

final class AnalyticsCollector$MediaPeriodQueueTracker {
    private boolean isSeeking;
    @Nullable
    private AnalyticsCollector$MediaPeriodInfo lastReportedPlayingMediaPeriod;
    private final HashMap<MediaPeriodId, AnalyticsCollector$MediaPeriodInfo> mediaPeriodIdToInfo = new HashMap();
    private final ArrayList<AnalyticsCollector$MediaPeriodInfo> mediaPeriodInfoQueue = new ArrayList();
    private final Period period = new Period();
    @Nullable
    private AnalyticsCollector$MediaPeriodInfo readingMediaPeriod;
    private Timeline timeline = Timeline.EMPTY;

    @Nullable
    public AnalyticsCollector$MediaPeriodInfo getPlayingMediaPeriod() {
        AnalyticsCollector$MediaPeriodInfo analyticsCollector$MediaPeriodInfo;
        if (!(this.mediaPeriodInfoQueue.isEmpty() || this.timeline.isEmpty())) {
            if (!this.isSeeking) {
                analyticsCollector$MediaPeriodInfo = (AnalyticsCollector$MediaPeriodInfo) this.mediaPeriodInfoQueue.get(0);
                return analyticsCollector$MediaPeriodInfo;
            }
        }
        analyticsCollector$MediaPeriodInfo = null;
        return analyticsCollector$MediaPeriodInfo;
    }

    @Nullable
    public AnalyticsCollector$MediaPeriodInfo getLastReportedPlayingMediaPeriod() {
        return this.lastReportedPlayingMediaPeriod;
    }

    @Nullable
    public AnalyticsCollector$MediaPeriodInfo getReadingMediaPeriod() {
        return this.readingMediaPeriod;
    }

    @Nullable
    public AnalyticsCollector$MediaPeriodInfo getLoadingMediaPeriod() {
        if (this.mediaPeriodInfoQueue.isEmpty()) {
            return null;
        }
        ArrayList arrayList = this.mediaPeriodInfoQueue;
        return (AnalyticsCollector$MediaPeriodInfo) arrayList.get(arrayList.size() - 1);
    }

    @Nullable
    public AnalyticsCollector$MediaPeriodInfo getMediaPeriodInfo(MediaPeriodId mediaPeriodId) {
        return (AnalyticsCollector$MediaPeriodInfo) this.mediaPeriodIdToInfo.get(mediaPeriodId);
    }

    public boolean isSeeking() {
        return this.isSeeking;
    }

    @Nullable
    public AnalyticsCollector$MediaPeriodInfo tryResolveWindowIndex(int windowIndex) {
        AnalyticsCollector$MediaPeriodInfo match = null;
        for (int i = 0; i < this.mediaPeriodInfoQueue.size(); i++) {
            AnalyticsCollector$MediaPeriodInfo info = (AnalyticsCollector$MediaPeriodInfo) this.mediaPeriodInfoQueue.get(i);
            int periodIndex = this.timeline.getIndexOfPeriod(info.mediaPeriodId.periodUid);
            if (periodIndex != -1) {
                if (this.timeline.getPeriod(periodIndex, this.period).windowIndex == windowIndex) {
                    if (match != null) {
                        return null;
                    }
                    match = info;
                }
            }
        }
        return match;
    }

    public void onPositionDiscontinuity(int reason) {
        updateLastReportedPlayingMediaPeriod();
    }

    public void onTimelineChanged(Timeline timeline) {
        for (int i = 0; i < this.mediaPeriodInfoQueue.size(); i++) {
            AnalyticsCollector$MediaPeriodInfo newMediaPeriodInfo = updateMediaPeriodInfoToNewTimeline((AnalyticsCollector$MediaPeriodInfo) this.mediaPeriodInfoQueue.get(i), timeline);
            this.mediaPeriodInfoQueue.set(i, newMediaPeriodInfo);
            this.mediaPeriodIdToInfo.put(newMediaPeriodInfo.mediaPeriodId, newMediaPeriodInfo);
        }
        AnalyticsCollector$MediaPeriodInfo analyticsCollector$MediaPeriodInfo = this.readingMediaPeriod;
        if (analyticsCollector$MediaPeriodInfo != null) {
            this.readingMediaPeriod = updateMediaPeriodInfoToNewTimeline(analyticsCollector$MediaPeriodInfo, timeline);
        }
        this.timeline = timeline;
        updateLastReportedPlayingMediaPeriod();
    }

    public void onSeekStarted() {
        this.isSeeking = true;
    }

    public void onSeekProcessed() {
        this.isSeeking = false;
        updateLastReportedPlayingMediaPeriod();
    }

    public void onMediaPeriodCreated(int windowIndex, MediaPeriodId mediaPeriodId) {
        AnalyticsCollector$MediaPeriodInfo mediaPeriodInfo = new AnalyticsCollector$MediaPeriodInfo(mediaPeriodId, this.timeline.getIndexOfPeriod(mediaPeriodId.periodUid) != -1 ? this.timeline : Timeline.EMPTY, windowIndex);
        this.mediaPeriodInfoQueue.add(mediaPeriodInfo);
        this.mediaPeriodIdToInfo.put(mediaPeriodId, mediaPeriodInfo);
        if (this.mediaPeriodInfoQueue.size() == 1 && !this.timeline.isEmpty()) {
            updateLastReportedPlayingMediaPeriod();
        }
    }

    public boolean onMediaPeriodReleased(MediaPeriodId mediaPeriodId) {
        AnalyticsCollector$MediaPeriodInfo mediaPeriodInfo = (AnalyticsCollector$MediaPeriodInfo) this.mediaPeriodIdToInfo.remove(mediaPeriodId);
        if (mediaPeriodInfo == null) {
            return false;
        }
        this.mediaPeriodInfoQueue.remove(mediaPeriodInfo);
        AnalyticsCollector$MediaPeriodInfo analyticsCollector$MediaPeriodInfo = this.readingMediaPeriod;
        if (analyticsCollector$MediaPeriodInfo != null && mediaPeriodId.equals(analyticsCollector$MediaPeriodInfo.mediaPeriodId)) {
            this.readingMediaPeriod = this.mediaPeriodInfoQueue.isEmpty() ? null : (AnalyticsCollector$MediaPeriodInfo) this.mediaPeriodInfoQueue.get(0);
        }
        return true;
    }

    public void onReadingStarted(MediaPeriodId mediaPeriodId) {
        this.readingMediaPeriod = (AnalyticsCollector$MediaPeriodInfo) this.mediaPeriodIdToInfo.get(mediaPeriodId);
    }

    private void updateLastReportedPlayingMediaPeriod() {
        if (!this.mediaPeriodInfoQueue.isEmpty()) {
            this.lastReportedPlayingMediaPeriod = (AnalyticsCollector$MediaPeriodInfo) this.mediaPeriodInfoQueue.get(0);
        }
    }

    private AnalyticsCollector$MediaPeriodInfo updateMediaPeriodInfoToNewTimeline(AnalyticsCollector$MediaPeriodInfo info, Timeline newTimeline) {
        int newPeriodIndex = newTimeline.getIndexOfPeriod(info.mediaPeriodId.periodUid);
        if (newPeriodIndex == -1) {
            return info;
        }
        return new AnalyticsCollector$MediaPeriodInfo(info.mediaPeriodId, newTimeline, newTimeline.getPeriod(newPeriodIndex, this.period).windowIndex);
    }
}
