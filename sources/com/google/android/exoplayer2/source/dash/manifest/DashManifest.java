package com.google.android.exoplayer2.source.dash.manifest;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.offline.FilterableManifest;
import com.google.android.exoplayer2.offline.StreamKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DashManifest implements FilterableManifest<DashManifest> {
    public final long availabilityStartTimeMs;
    public final long durationMs;
    public final boolean dynamic;
    public final Uri location;
    public final long minBufferTimeMs;
    public final long minUpdatePeriodMs;
    private final List<Period> periods;
    @Nullable
    public final ProgramInformation programInformation;
    public final long publishTimeMs;
    public final long suggestedPresentationDelayMs;
    public final long timeShiftBufferDepthMs;
    public final UtcTimingElement utcTiming;

    @Deprecated
    public DashManifest(long availabilityStartTimeMs, long durationMs, long minBufferTimeMs, boolean dynamic, long minUpdatePeriodMs, long timeShiftBufferDepthMs, long suggestedPresentationDelayMs, long publishTimeMs, UtcTimingElement utcTiming, Uri location, List<Period> periods) {
        this(availabilityStartTimeMs, durationMs, minBufferTimeMs, dynamic, minUpdatePeriodMs, timeShiftBufferDepthMs, suggestedPresentationDelayMs, publishTimeMs, null, utcTiming, location, periods);
    }

    public DashManifest(long availabilityStartTimeMs, long durationMs, long minBufferTimeMs, boolean dynamic, long minUpdatePeriodMs, long timeShiftBufferDepthMs, long suggestedPresentationDelayMs, long publishTimeMs, @Nullable ProgramInformation programInformation, UtcTimingElement utcTiming, Uri location, List<Period> periods) {
        this.availabilityStartTimeMs = availabilityStartTimeMs;
        this.durationMs = durationMs;
        this.minBufferTimeMs = minBufferTimeMs;
        this.dynamic = dynamic;
        this.minUpdatePeriodMs = minUpdatePeriodMs;
        this.timeShiftBufferDepthMs = timeShiftBufferDepthMs;
        this.suggestedPresentationDelayMs = suggestedPresentationDelayMs;
        this.publishTimeMs = publishTimeMs;
        this.programInformation = programInformation;
        this.utcTiming = utcTiming;
        this.location = location;
        r0.periods = periods == null ? Collections.emptyList() : periods;
    }

    public final int getPeriodCount() {
        return this.periods.size();
    }

    public final Period getPeriod(int index) {
        return (Period) this.periods.get(index);
    }

    public final long getPeriodDurationMs(int index) {
        if (index != this.periods.size() - 1) {
            return ((Period) this.periods.get(index + 1)).startMs - ((Period) this.periods.get(index)).startMs;
        }
        long j = this.durationMs;
        return j == C0555C.TIME_UNSET ? C0555C.TIME_UNSET : j - ((Period) this.periods.get(index)).startMs;
    }

    public final long getPeriodDurationUs(int index) {
        return C0555C.msToUs(getPeriodDurationMs(index));
    }

    public final DashManifest copy(List<StreamKey> streamKeys) {
        long newDuration;
        long periodDurationMs;
        DashManifest dashManifest = this;
        LinkedList<StreamKey> keys = new LinkedList(streamKeys);
        Collections.sort(keys);
        keys.add(new StreamKey(-1, -1, -1));
        ArrayList<Period> copyPeriods = new ArrayList();
        int periodIndex = 0;
        long shiftMs = 0;
        while (true) {
            int periodCount = getPeriodCount();
            newDuration = C0555C.TIME_UNSET;
            if (periodIndex >= periodCount) {
                break;
            }
            if (((StreamKey) keys.peek()).periodIndex != periodIndex) {
                periodDurationMs = getPeriodDurationMs(periodIndex);
                if (periodDurationMs != C0555C.TIME_UNSET) {
                    shiftMs += periodDurationMs;
                }
            } else {
                Period period = getPeriod(periodIndex);
                copyPeriods.add(new Period(period.id, period.startMs - shiftMs, copyAdaptationSets(period.adaptationSets, keys), period.eventStreams));
            }
            periodIndex++;
        }
        periodDurationMs = dashManifest.durationMs;
        if (periodDurationMs != C0555C.TIME_UNSET) {
            newDuration = periodDurationMs - shiftMs;
        }
        return new DashManifest(dashManifest.availabilityStartTimeMs, newDuration, dashManifest.minBufferTimeMs, dashManifest.dynamic, dashManifest.minUpdatePeriodMs, dashManifest.timeShiftBufferDepthMs, dashManifest.suggestedPresentationDelayMs, dashManifest.publishTimeMs, dashManifest.programInformation, dashManifest.utcTiming, dashManifest.location, copyPeriods);
    }

    private static ArrayList<AdaptationSet> copyAdaptationSets(List<AdaptationSet> adaptationSets, LinkedList<StreamKey> keys) {
        StreamKey key = (StreamKey) keys.poll();
        int periodIndex = key.periodIndex;
        ArrayList<AdaptationSet> copyAdaptationSets = new ArrayList();
        while (true) {
            int adaptationSetIndex = key.groupIndex;
            AdaptationSet adaptationSet = (AdaptationSet) adaptationSets.get(adaptationSetIndex);
            List<Representation> representations = adaptationSet.representations;
            ArrayList<Representation> copyRepresentations = new ArrayList();
            while (true) {
                copyRepresentations.add((Representation) representations.get(key.trackIndex));
                key = (StreamKey) keys.poll();
                if (key.periodIndex != periodIndex) {
                    break;
                } else if (key.groupIndex != adaptationSetIndex) {
                    break;
                }
            }
            copyAdaptationSets.add(new AdaptationSet(adaptationSet.id, adaptationSet.type, copyRepresentations, adaptationSet.accessibilityDescriptors, adaptationSet.supplementalProperties));
            if (key.periodIndex != periodIndex) {
                keys.addFirst(key);
                return copyAdaptationSets;
            }
        }
    }
}
