package com.google.android.exoplayer2.source;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.Format;

public final class MediaSourceEventListener$MediaLoadData {
    public final int dataType;
    public final long mediaEndTimeMs;
    public final long mediaStartTimeMs;
    @Nullable
    public final Format trackFormat;
    @Nullable
    public final Object trackSelectionData;
    public final int trackSelectionReason;
    public final int trackType;

    public MediaSourceEventListener$MediaLoadData(int dataType, int trackType, @Nullable Format trackFormat, int trackSelectionReason, @Nullable Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs) {
        this.dataType = dataType;
        this.trackType = trackType;
        this.trackFormat = trackFormat;
        this.trackSelectionReason = trackSelectionReason;
        this.trackSelectionData = trackSelectionData;
        this.mediaStartTimeMs = mediaStartTimeMs;
        this.mediaEndTimeMs = mediaEndTimeMs;
    }
}
