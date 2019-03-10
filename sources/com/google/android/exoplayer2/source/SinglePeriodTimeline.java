package com.google.android.exoplayer2.source;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.util.Assertions;

public final class SinglePeriodTimeline extends Timeline {
    private static final Object UID = new Object();
    private final boolean isDynamic;
    private final boolean isSeekable;
    private final long periodDurationUs;
    private final long presentationStartTimeMs;
    @Nullable
    private final Object tag;
    private final long windowDefaultStartPositionUs;
    private final long windowDurationUs;
    private final long windowPositionInPeriodUs;
    private final long windowStartTimeMs;

    public SinglePeriodTimeline(long durationUs, boolean isSeekable, boolean isDynamic) {
        this(durationUs, isSeekable, isDynamic, null);
    }

    public SinglePeriodTimeline(long durationUs, boolean isSeekable, boolean isDynamic, @Nullable Object tag) {
        this(durationUs, durationUs, 0, 0, isSeekable, isDynamic, tag);
    }

    public SinglePeriodTimeline(long periodDurationUs, long windowDurationUs, long windowPositionInPeriodUs, long windowDefaultStartPositionUs, boolean isSeekable, boolean isDynamic, @Nullable Object tag) {
        this(C0555C.TIME_UNSET, C0555C.TIME_UNSET, periodDurationUs, windowDurationUs, windowPositionInPeriodUs, windowDefaultStartPositionUs, isSeekable, isDynamic, tag);
    }

    public SinglePeriodTimeline(long presentationStartTimeMs, long windowStartTimeMs, long periodDurationUs, long windowDurationUs, long windowPositionInPeriodUs, long windowDefaultStartPositionUs, boolean isSeekable, boolean isDynamic, @Nullable Object tag) {
        this.presentationStartTimeMs = presentationStartTimeMs;
        this.windowStartTimeMs = windowStartTimeMs;
        this.periodDurationUs = periodDurationUs;
        this.windowDurationUs = windowDurationUs;
        this.windowPositionInPeriodUs = windowPositionInPeriodUs;
        this.windowDefaultStartPositionUs = windowDefaultStartPositionUs;
        this.isSeekable = isSeekable;
        this.isDynamic = isDynamic;
        this.tag = tag;
    }

    public int getWindowCount() {
        return 1;
    }

    public Window getWindow(int windowIndex, Window window, boolean setTag, long defaultPositionProjectionUs) {
        SinglePeriodTimeline singlePeriodTimeline = this;
        Assertions.checkIndex(windowIndex, 0, 1);
        Object tag = setTag ? singlePeriodTimeline.tag : null;
        long windowDefaultStartPositionUs = singlePeriodTimeline.windowDefaultStartPositionUs;
        if (singlePeriodTimeline.isDynamic && defaultPositionProjectionUs != 0) {
            long j = singlePeriodTimeline.windowDurationUs;
            if (j == C0555C.TIME_UNSET) {
                windowDefaultStartPositionUs = C0555C.TIME_UNSET;
            } else {
                windowDefaultStartPositionUs += defaultPositionProjectionUs;
                if (windowDefaultStartPositionUs > j) {
                    windowDefaultStartPositionUs = C0555C.TIME_UNSET;
                }
            }
        }
        return window.set(tag, singlePeriodTimeline.presentationStartTimeMs, singlePeriodTimeline.windowStartTimeMs, singlePeriodTimeline.isSeekable, singlePeriodTimeline.isDynamic, windowDefaultStartPositionUs, singlePeriodTimeline.windowDurationUs, 0, 0, singlePeriodTimeline.windowPositionInPeriodUs);
    }

    public int getPeriodCount() {
        return 1;
    }

    public Period getPeriod(int periodIndex, Period period, boolean setIds) {
        Assertions.checkIndex(periodIndex, 0, 1);
        return period.set(null, setIds ? UID : null, 0, this.periodDurationUs, -this.windowPositionInPeriodUs);
    }

    public int getIndexOfPeriod(Object uid) {
        return UID.equals(uid) ? 0 : -1;
    }

    public Object getUidOfPeriod(int periodIndex) {
        Assertions.checkIndex(periodIndex, 0, 1);
        return UID;
    }
}
