package com.google.android.exoplayer2.extractor.mp3;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.extractor.SeekMap.SeekPoints;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;

final class XingSeeker implements Seeker {
    private static final String TAG = "XingSeeker";
    private final long dataEndPosition;
    private final long dataSize;
    private final long dataStartPosition;
    private final long durationUs;
    @Nullable
    private final long[] tableOfContents;
    private final int xingFrameSize;

    @Nullable
    public static XingSeeker create(long inputLength, long position, MpegAudioHeader mpegAudioHeader, ParsableByteArray frame) {
        long j = inputLength;
        MpegAudioHeader mpegAudioHeader2 = mpegAudioHeader;
        int samplesPerFrame = mpegAudioHeader2.samplesPerFrame;
        int sampleRate = mpegAudioHeader2.sampleRate;
        int flags = frame.readInt();
        if ((flags & 1) == 1) {
            int readUnsignedIntToInt = frame.readUnsignedIntToInt();
            int frameCount = readUnsignedIntToInt;
            if (readUnsignedIntToInt != 0) {
                long durationUs = Util.scaleLargeTimestamp((long) frameCount, ((long) samplesPerFrame) * 1000000, (long) sampleRate);
                if ((flags & 6) != 6) {
                    return new XingSeeker(position, mpegAudioHeader2.frameSize, durationUs);
                }
                long readUnsignedIntToInt2 = (long) frame.readUnsignedIntToInt();
                long[] tableOfContents = new long[100];
                for (int i = 0; i < 100; i++) {
                    tableOfContents[i] = (long) frame.readUnsignedByte();
                }
                if (j != -1 && j != position + readUnsignedIntToInt2) {
                    String str = TAG;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("XING data size mismatch: ");
                    stringBuilder.append(j);
                    stringBuilder.append(", ");
                    stringBuilder.append(position + readUnsignedIntToInt2);
                    Log.m10w(str, stringBuilder.toString());
                }
                long dataSize = readUnsignedIntToInt2;
                return new XingSeeker(position, mpegAudioHeader2.frameSize, durationUs, readUnsignedIntToInt2, tableOfContents);
            }
        }
        return null;
    }

    private XingSeeker(long dataStartPosition, int xingFrameSize, long durationUs) {
        this(dataStartPosition, xingFrameSize, durationUs, -1, null);
    }

    private XingSeeker(long dataStartPosition, int xingFrameSize, long durationUs, long dataSize, @Nullable long[] tableOfContents) {
        this.dataStartPosition = dataStartPosition;
        this.xingFrameSize = xingFrameSize;
        this.durationUs = durationUs;
        this.tableOfContents = tableOfContents;
        this.dataSize = dataSize;
        long j = -1;
        if (dataSize != -1) {
            j = dataStartPosition + dataSize;
        }
        this.dataEndPosition = j;
    }

    public boolean isSeekable() {
        return this.tableOfContents != null;
    }

    public SeekPoints getSeekPoints(long timeUs) {
        XingSeeker xingSeeker = this;
        if (!isSeekable()) {
            return new SeekPoints(new SeekPoint(0, xingSeeker.dataStartPosition + ((long) xingSeeker.xingFrameSize)));
        }
        double scaledPosition;
        long timeUs2 = Util.constrainValue(timeUs, 0, xingSeeker.durationUs);
        double d = (double) timeUs2;
        Double.isNaN(d);
        d *= 100.0d;
        double d2 = (double) xingSeeker.durationUs;
        Double.isNaN(d2);
        d /= d2;
        if (d <= 0.0d) {
            scaledPosition = 0.0d;
        } else if (d >= 100.0d) {
            scaledPosition = 256.0d;
        } else {
            int prevTableIndex = (int) d;
            long[] tableOfContents = (long[]) Assertions.checkNotNull(xingSeeker.tableOfContents);
            d2 = (double) tableOfContents[prevTableIndex];
            double nextScaledPosition = prevTableIndex == 99 ? 256.0d : (double) tableOfContents[prevTableIndex + 1];
            double interpolateFraction = (double) prevTableIndex;
            Double.isNaN(interpolateFraction);
            interpolateFraction = d - interpolateFraction;
            Double.isNaN(d2);
            double d3 = (nextScaledPosition - d2) * interpolateFraction;
            Double.isNaN(d2);
            scaledPosition = d2 + d3;
        }
        d2 = scaledPosition / 256.0d;
        double d4 = (double) xingSeeker.dataSize;
        Double.isNaN(d4);
        return new SeekPoints(new SeekPoint(timeUs2, xingSeeker.dataStartPosition + Util.constrainValue(Math.round(d2 * d4), (long) xingSeeker.xingFrameSize, xingSeeker.dataSize - 1)));
    }

    public long getTimeUs(long position) {
        long positionOffset = position - this.dataStartPosition;
        if (!isSeekable()) {
        } else if (positionOffset <= ((long) r0.xingFrameSize)) {
            r15 = positionOffset;
        } else {
            double interpolateFraction;
            double d;
            long[] tableOfContents = (long[]) Assertions.checkNotNull(r0.tableOfContents);
            double d2 = (double) positionOffset;
            Double.isNaN(d2);
            d2 *= 256.0d;
            double d3 = (double) r0.dataSize;
            Double.isNaN(d3);
            d2 /= d3;
            int prevTableIndex = Util.binarySearchFloor(tableOfContents, (long) d2, true, true);
            long prevTimeUs = getTimeUsForTableIndex(prevTableIndex);
            long prevScaledPosition = tableOfContents[prevTableIndex];
            long nextTimeUs = getTimeUsForTableIndex(prevTableIndex + 1);
            long nextScaledPosition = prevTableIndex == 99 ? 256 : tableOfContents[prevTableIndex + 1];
            if (prevScaledPosition == nextScaledPosition) {
                long[] jArr = tableOfContents;
                interpolateFraction = 0.0d;
                r15 = positionOffset;
            } else {
                interpolateFraction = (double) prevScaledPosition;
                Double.isNaN(interpolateFraction);
                interpolateFraction = d2 - interpolateFraction;
                d = (double) (nextScaledPosition - prevScaledPosition);
                Double.isNaN(d);
                interpolateFraction /= d;
            }
            d = (double) (nextTimeUs - prevTimeUs);
            Double.isNaN(d);
            return Math.round(d * interpolateFraction) + prevTimeUs;
        }
        return 0;
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    public long getDataEndPosition() {
        return this.dataEndPosition;
    }

    private long getTimeUsForTableIndex(int tableIndex) {
        return (this.durationUs * ((long) tableIndex)) / 100;
    }
}
