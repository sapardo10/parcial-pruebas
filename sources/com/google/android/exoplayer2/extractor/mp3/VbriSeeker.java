package com.google.android.exoplayer2.extractor.mp3;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.extractor.SeekMap.SeekPoints;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;

final class VbriSeeker implements Seeker {
    private static final String TAG = "VbriSeeker";
    private final long dataEndPosition;
    private final long durationUs;
    private final long[] positions;
    private final long[] timesUs;

    @Nullable
    public static VbriSeeker create(long inputLength, long position, MpegAudioHeader mpegAudioHeader, ParsableByteArray frame) {
        long j = inputLength;
        MpegAudioHeader mpegAudioHeader2 = mpegAudioHeader;
        ParsableByteArray parsableByteArray = frame;
        parsableByteArray.skipBytes(10);
        int numFrames = frame.readInt();
        if (numFrames <= 0) {
            return null;
        }
        long durationUs;
        int sampleRate = mpegAudioHeader2.sampleRate;
        long durationUs2 = Util.scaleLargeTimestamp((long) numFrames, 1000000 * ((long) (sampleRate >= 32000 ? 1152 : 576)), (long) sampleRate);
        int entryCount = frame.readUnsignedShort();
        int scale = frame.readUnsignedShort();
        int entrySize = frame.readUnsignedShort();
        parsableByteArray.skipBytes(2);
        long minPosition = position + ((long) mpegAudioHeader2.frameSize);
        long[] timesUs = new long[entryCount];
        long[] positions = new long[entryCount];
        long position2 = position;
        int index = 0;
        while (index < entryCount) {
            int segmentSize;
            int sampleRate2 = sampleRate;
            durationUs = durationUs2;
            timesUs[index] = (((long) index) * durationUs2) / ((long) entryCount);
            positions[index] = Math.max(position2, minPosition);
            switch (entrySize) {
                case 1:
                    segmentSize = frame.readUnsignedByte();
                    break;
                case 2:
                    segmentSize = frame.readUnsignedShort();
                    break;
                case 3:
                    segmentSize = frame.readUnsignedInt24();
                    break;
                case 4:
                    segmentSize = frame.readUnsignedIntToInt();
                    break;
                default:
                    return null;
            }
            position2 += (long) (segmentSize * scale);
            index++;
            sampleRate = sampleRate2;
            durationUs2 = durationUs;
            j = inputLength;
        }
        durationUs = durationUs2;
        long j2 = inputLength;
        if (j2 != -1 && j2 != position2) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("VBRI data size mismatch: ");
            stringBuilder.append(j2);
            stringBuilder.append(", ");
            stringBuilder.append(position2);
            Log.m10w(str, stringBuilder.toString());
        }
        return new VbriSeeker(timesUs, positions, durationUs, position2);
    }

    private VbriSeeker(long[] timesUs, long[] positions, long durationUs, long dataEndPosition) {
        this.timesUs = timesUs;
        this.positions = positions;
        this.durationUs = durationUs;
        this.dataEndPosition = dataEndPosition;
    }

    public boolean isSeekable() {
        return true;
    }

    public SeekPoints getSeekPoints(long timeUs) {
        int tableIndex = Util.binarySearchFloor(this.timesUs, timeUs, true, true);
        SeekPoint seekPoint = new SeekPoint(this.timesUs[tableIndex], this.positions[tableIndex]);
        if (seekPoint.timeUs < timeUs) {
            long[] jArr = this.timesUs;
            if (tableIndex != jArr.length - 1) {
                return new SeekPoints(seekPoint, new SeekPoint(jArr[tableIndex + 1], this.positions[tableIndex + 1]));
            }
        }
        return new SeekPoints(seekPoint);
    }

    public long getTimeUs(long position) {
        return this.timesUs[Util.binarySearchFloor(this.positions, position, true, true)];
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    public long getDataEndPosition() {
        return this.dataEndPosition;
    }
}
