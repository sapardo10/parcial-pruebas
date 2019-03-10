package com.google.android.exoplayer2.extractor.mp3;

import android.util.Pair;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.extractor.SeekMap.SeekPoints;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.metadata.id3.MlltFrame;
import com.google.android.exoplayer2.util.Util;

final class MlltSeeker implements Seeker {
    private final long durationUs;
    private final long[] referencePositions;
    private final long[] referenceTimesMs;

    public static MlltSeeker create(long firstFramePosition, MlltFrame mlltFrame) {
        int referenceCount = mlltFrame.bytesDeviations.length;
        long[] referencePositions = new long[(referenceCount + 1)];
        long[] referenceTimesMs = new long[(referenceCount + 1)];
        referencePositions[0] = firstFramePosition;
        referenceTimesMs[0] = 0;
        long position = firstFramePosition;
        long timeMs = 0;
        for (int i = 1; i <= referenceCount; i++) {
            position += (long) (mlltFrame.bytesBetweenReference + mlltFrame.bytesDeviations[i - 1]);
            timeMs += (long) (mlltFrame.millisecondsBetweenReference + mlltFrame.millisecondsDeviations[i - 1]);
            referencePositions[i] = position;
            referenceTimesMs[i] = timeMs;
        }
        return new MlltSeeker(referencePositions, referenceTimesMs);
    }

    private MlltSeeker(long[] referencePositions, long[] referenceTimesMs) {
        this.referencePositions = referencePositions;
        this.referenceTimesMs = referenceTimesMs;
        this.durationUs = C0555C.msToUs(referenceTimesMs[referenceTimesMs.length - 1]);
    }

    public boolean isSeekable() {
        return true;
    }

    public SeekPoints getSeekPoints(long timeUs) {
        Pair<Long, Long> timeMsAndPosition = linearlyInterpolate(C0555C.usToMs(Util.constrainValue(timeUs, 0, this.durationUs)), this.referenceTimesMs, this.referencePositions);
        return new SeekPoints(new SeekPoint(C0555C.msToUs(((Long) timeMsAndPosition.first).longValue()), ((Long) timeMsAndPosition.second).longValue()));
    }

    public long getTimeUs(long position) {
        return C0555C.msToUs(((Long) linearlyInterpolate(position, this.referencePositions, this.referenceTimesMs).second).longValue());
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    private static Pair<Long, Long> linearlyInterpolate(long x, long[] xReferences, long[] yReferences) {
        long j = x;
        long[] jArr = xReferences;
        int previousReferenceIndex = Util.binarySearchFloor(jArr, j, true, true);
        long xPreviousReference = jArr[previousReferenceIndex];
        long yPreviousReference = yReferences[previousReferenceIndex];
        int nextReferenceIndex = previousReferenceIndex + 1;
        if (nextReferenceIndex == jArr.length) {
            return Pair.create(Long.valueOf(xPreviousReference), Long.valueOf(yPreviousReference));
        }
        double d;
        double d2;
        long xNextReference = jArr[nextReferenceIndex];
        long yNextReference = yReferences[nextReferenceIndex];
        if (xNextReference == xPreviousReference) {
            d = 0.0d;
        } else {
            d = (double) j;
            d2 = (double) xPreviousReference;
            Double.isNaN(d);
            Double.isNaN(d2);
            d -= d2;
            d2 = (double) (xNextReference - xPreviousReference);
            Double.isNaN(d2);
            d /= d2;
        }
        d2 = d;
        d = (double) (yNextReference - yPreviousReference);
        Double.isNaN(d);
        return Pair.create(Long.valueOf(x), Long.valueOf(((long) (d * d2)) + yPreviousReference));
    }

    public long getDataEndPosition() {
        return -1;
    }
}
