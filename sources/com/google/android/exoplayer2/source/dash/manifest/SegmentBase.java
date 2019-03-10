package com.google.android.exoplayer2.source.dash.manifest;

import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.util.Util;
import java.util.List;

public abstract class SegmentBase {
    final RangedUri initialization;
    final long presentationTimeOffset;
    final long timescale;

    public static class SegmentTimelineElement {
        final long duration;
        final long startTime;

        public SegmentTimelineElement(long startTime, long duration) {
            this.startTime = startTime;
            this.duration = duration;
        }
    }

    public static abstract class MultiSegmentBase extends SegmentBase {
        final long duration;
        final List<SegmentTimelineElement> segmentTimeline;
        final long startNumber;

        public abstract int getSegmentCount(long j);

        public abstract RangedUri getSegmentUrl(Representation representation, long j);

        public MultiSegmentBase(RangedUri initialization, long timescale, long presentationTimeOffset, long startNumber, long duration, List<SegmentTimelineElement> segmentTimeline) {
            super(initialization, timescale, presentationTimeOffset);
            this.startNumber = startNumber;
            this.duration = duration;
            this.segmentTimeline = segmentTimeline;
        }

        public long getSegmentNum(long timeUs, long periodDurationUs) {
            long firstSegmentNum = getFirstSegmentNum();
            long segmentCount = (long) getSegmentCount(periodDurationUs);
            if (segmentCount == 0) {
                return firstSegmentNum;
            }
            if (r0.segmentTimeline == null) {
                long segmentNum = r0.startNumber + (timeUs / ((r0.duration * 1000000) / r0.timescale));
                long min = segmentNum < firstSegmentNum ? firstSegmentNum : segmentCount == -1 ? segmentNum : Math.min(segmentNum, (firstSegmentNum + segmentCount) - 1);
                return min;
            }
            long lowIndex = firstSegmentNum;
            segmentNum = (firstSegmentNum + segmentCount) - 1;
            while (lowIndex <= segmentNum) {
                long midIndex = ((segmentNum - lowIndex) / 2) + lowIndex;
                long midTimeUs = getSegmentTimeUs(midIndex);
                if (midTimeUs < timeUs) {
                    lowIndex = midIndex + 1;
                } else if (midTimeUs <= timeUs) {
                    return midIndex;
                } else {
                    segmentNum = midIndex - 1;
                }
            }
            return lowIndex == firstSegmentNum ? lowIndex : segmentNum;
        }

        public final long getSegmentDurationUs(long sequenceNumber, long periodDurationUs) {
            List list = this.segmentTimeline;
            if (list != null) {
                return (1000000 * ((SegmentTimelineElement) list.get((int) (sequenceNumber - this.startNumber))).duration) / this.timescale;
            }
            long segmentTimeUs;
            int segmentCount = getSegmentCount(periodDurationUs);
            if (segmentCount != -1) {
                if (sequenceNumber == (getFirstSegmentNum() + ((long) segmentCount)) - 1) {
                    segmentTimeUs = periodDurationUs - getSegmentTimeUs(sequenceNumber);
                    return segmentTimeUs;
                }
            }
            segmentTimeUs = (this.duration * 1000000) / this.timescale;
            return segmentTimeUs;
        }

        public final long getSegmentTimeUs(long sequenceNumber) {
            long unscaledSegmentTime;
            List list = this.segmentTimeline;
            if (list != null) {
                unscaledSegmentTime = ((SegmentTimelineElement) list.get((int) (sequenceNumber - this.startNumber))).startTime - this.presentationTimeOffset;
            } else {
                unscaledSegmentTime = (sequenceNumber - this.startNumber) * this.duration;
            }
            return Util.scaleLargeTimestamp(unscaledSegmentTime, 1000000, this.timescale);
        }

        public long getFirstSegmentNum() {
            return this.startNumber;
        }

        public boolean isExplicit() {
            return this.segmentTimeline != null;
        }
    }

    public static class SingleSegmentBase extends SegmentBase {
        final long indexLength;
        final long indexStart;

        public SingleSegmentBase(RangedUri initialization, long timescale, long presentationTimeOffset, long indexStart, long indexLength) {
            super(initialization, timescale, presentationTimeOffset);
            this.indexStart = indexStart;
            this.indexLength = indexLength;
        }

        public SingleSegmentBase() {
            this(null, 1, 0, 0, 0);
        }

        public RangedUri getIndex() {
            long j = this.indexLength;
            return j <= 0 ? null : new RangedUri(null, this.indexStart, j);
        }
    }

    public static class SegmentList extends MultiSegmentBase {
        final List<RangedUri> mediaSegments;

        public SegmentList(RangedUri initialization, long timescale, long presentationTimeOffset, long startNumber, long duration, List<SegmentTimelineElement> segmentTimeline, List<RangedUri> mediaSegments) {
            super(initialization, timescale, presentationTimeOffset, startNumber, duration, segmentTimeline);
            this.mediaSegments = mediaSegments;
        }

        public RangedUri getSegmentUrl(Representation representation, long sequenceNumber) {
            return (RangedUri) this.mediaSegments.get((int) (sequenceNumber - this.startNumber));
        }

        public int getSegmentCount(long periodDurationUs) {
            return this.mediaSegments.size();
        }

        public boolean isExplicit() {
            return true;
        }
    }

    public static class SegmentTemplate extends MultiSegmentBase {
        final UrlTemplate initializationTemplate;
        final UrlTemplate mediaTemplate;

        public SegmentTemplate(RangedUri initialization, long timescale, long presentationTimeOffset, long startNumber, long duration, List<SegmentTimelineElement> segmentTimeline, UrlTemplate initializationTemplate, UrlTemplate mediaTemplate) {
            super(initialization, timescale, presentationTimeOffset, startNumber, duration, segmentTimeline);
            this.initializationTemplate = initializationTemplate;
            this.mediaTemplate = mediaTemplate;
        }

        public RangedUri getInitialization(Representation representation) {
            String urlString = this.initializationTemplate;
            if (urlString != null) {
                return new RangedUri(urlString.buildUri(representation.format.id, 0, representation.format.bitrate, 0), 0, -1);
            }
            return super.getInitialization(representation);
        }

        public RangedUri getSegmentUrl(Representation representation, long sequenceNumber) {
            long time;
            Representation representation2 = representation;
            if (this.segmentTimeline != null) {
                time = ((SegmentTimelineElement) r0.segmentTimeline.get((int) (sequenceNumber - r0.startNumber))).startTime;
            } else {
                time = (sequenceNumber - r0.startNumber) * r0.duration;
            }
            return new RangedUri(r0.mediaTemplate.buildUri(representation2.format.id, sequenceNumber, representation2.format.bitrate, time), 0, -1);
        }

        public int getSegmentCount(long periodDurationUs) {
            if (this.segmentTimeline != null) {
                return this.segmentTimeline.size();
            }
            if (periodDurationUs != C0555C.TIME_UNSET) {
                return (int) Util.ceilDivide(periodDurationUs, (this.duration * 1000000) / this.timescale);
            }
            return -1;
        }
    }

    public SegmentBase(RangedUri initialization, long timescale, long presentationTimeOffset) {
        this.initialization = initialization;
        this.timescale = timescale;
        this.presentationTimeOffset = presentationTimeOffset;
    }

    public RangedUri getInitialization(Representation representation) {
        return this.initialization;
    }

    public long getPresentationTimeOffsetUs() {
        return Util.scaleLargeTimestamp(this.presentationTimeOffset, 1000000, this.timescale);
    }
}
