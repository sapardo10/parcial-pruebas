package com.google.android.exoplayer2.analytics;

import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;

final class AnalyticsCollector$MediaPeriodInfo {
    public final MediaPeriodId mediaPeriodId;
    public final Timeline timeline;
    public final int windowIndex;

    public AnalyticsCollector$MediaPeriodInfo(MediaPeriodId mediaPeriodId, Timeline timeline, int windowIndex) {
        this.mediaPeriodId = mediaPeriodId;
        this.timeline = timeline;
        this.windowIndex = windowIndex;
    }
}
