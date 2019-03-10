package com.google.android.exoplayer2.source.ads;

import android.support.annotation.VisibleForTesting;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Period;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.source.ForwardingTimeline;
import com.google.android.exoplayer2.util.Assertions;

@VisibleForTesting(otherwise = 3)
public final class SinglePeriodAdTimeline extends ForwardingTimeline {
    private final AdPlaybackState adPlaybackState;

    public SinglePeriodAdTimeline(Timeline contentTimeline, AdPlaybackState adPlaybackState) {
        super(contentTimeline);
        boolean z = false;
        Assertions.checkState(contentTimeline.getPeriodCount() == 1);
        if (contentTimeline.getWindowCount() == 1) {
            z = true;
        }
        Assertions.checkState(z);
        this.adPlaybackState = adPlaybackState;
    }

    public Period getPeriod(int periodIndex, Period period, boolean setIds) {
        this.timeline.getPeriod(periodIndex, period, setIds);
        period.set(period.id, period.uid, period.windowIndex, period.durationUs, period.getPositionInWindowUs(), this.adPlaybackState);
        return period;
    }

    public Window getWindow(int windowIndex, Window window, boolean setTag, long defaultPositionProjectionUs) {
        window = super.getWindow(windowIndex, window, setTag, defaultPositionProjectionUs);
        if (window.durationUs == C0555C.TIME_UNSET) {
            window.durationUs = this.adPlaybackState.contentDurationUs;
        }
        return window;
    }
}
