package com.google.android.exoplayer2.source;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import java.io.IOException;

public abstract class DefaultMediaSourceEventListener implements MediaSourceEventListener {
    public void onMediaPeriodCreated(int windowIndex, MediaPeriodId mediaPeriodId) {
    }

    public void onMediaPeriodReleased(int windowIndex, MediaPeriodId mediaPeriodId) {
    }

    public void onLoadStarted(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$LoadEventInfo loadEventInfo, MediaSourceEventListener$MediaLoadData mediaLoadData) {
    }

    public void onLoadCompleted(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$LoadEventInfo loadEventInfo, MediaSourceEventListener$MediaLoadData mediaLoadData) {
    }

    public void onLoadCanceled(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$LoadEventInfo loadEventInfo, MediaSourceEventListener$MediaLoadData mediaLoadData) {
    }

    public void onLoadError(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$LoadEventInfo loadEventInfo, MediaSourceEventListener$MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
    }

    public void onReadingStarted(int windowIndex, MediaPeriodId mediaPeriodId) {
    }

    public void onUpstreamDiscarded(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$MediaLoadData mediaLoadData) {
    }

    public void onDownstreamFormatChanged(int windowIndex, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$MediaLoadData mediaLoadData) {
    }
}
