package com.google.android.exoplayer2.source;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.source.MediaSource.MediaPeriodId;
import java.io.IOException;

public interface MediaSourceEventListener {
    void onDownstreamFormatChanged(int i, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$MediaLoadData mediaSourceEventListener$MediaLoadData);

    void onLoadCanceled(int i, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$LoadEventInfo mediaSourceEventListener$LoadEventInfo, MediaSourceEventListener$MediaLoadData mediaSourceEventListener$MediaLoadData);

    void onLoadCompleted(int i, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$LoadEventInfo mediaSourceEventListener$LoadEventInfo, MediaSourceEventListener$MediaLoadData mediaSourceEventListener$MediaLoadData);

    void onLoadError(int i, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$LoadEventInfo mediaSourceEventListener$LoadEventInfo, MediaSourceEventListener$MediaLoadData mediaSourceEventListener$MediaLoadData, IOException iOException, boolean z);

    void onLoadStarted(int i, @Nullable MediaPeriodId mediaPeriodId, MediaSourceEventListener$LoadEventInfo mediaSourceEventListener$LoadEventInfo, MediaSourceEventListener$MediaLoadData mediaSourceEventListener$MediaLoadData);

    void onMediaPeriodCreated(int i, MediaPeriodId mediaPeriodId);

    void onMediaPeriodReleased(int i, MediaPeriodId mediaPeriodId);

    void onReadingStarted(int i, MediaPeriodId mediaPeriodId);

    void onUpstreamDiscarded(int i, MediaPeriodId mediaPeriodId, MediaSourceEventListener$MediaLoadData mediaSourceEventListener$MediaLoadData);
}
