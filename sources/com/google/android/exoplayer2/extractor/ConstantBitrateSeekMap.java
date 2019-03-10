package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.extractor.SeekMap.SeekPoints;
import com.google.android.exoplayer2.util.Util;

public class ConstantBitrateSeekMap implements SeekMap {
    private final int bitrate;
    private final long dataSize;
    private final long durationUs;
    private final long firstFrameBytePosition;
    private final int frameSize;
    private final long inputLength;

    public ConstantBitrateSeekMap(long inputLength, long firstFrameBytePosition, int bitrate, int frameSize) {
        this.inputLength = inputLength;
        this.firstFrameBytePosition = firstFrameBytePosition;
        this.frameSize = frameSize == -1 ? 1 : frameSize;
        this.bitrate = bitrate;
        if (inputLength == -1) {
            this.dataSize = -1;
            this.durationUs = C0555C.TIME_UNSET;
            return;
        }
        this.dataSize = inputLength - firstFrameBytePosition;
        this.durationUs = getTimeUsAtPosition(inputLength, firstFrameBytePosition, bitrate);
    }

    public boolean isSeekable() {
        return this.dataSize != -1;
    }

    public SeekPoints getSeekPoints(long timeUs) {
        if (this.dataSize == -1) {
            return new SeekPoints(new SeekPoint(0, this.firstFrameBytePosition));
        }
        long seekFramePosition = getFramePositionForTimeUs(timeUs);
        long seekTimeUs = getTimeUsAtPosition(seekFramePosition);
        SeekPoint seekPoint = new SeekPoint(seekTimeUs, seekFramePosition);
        if (seekTimeUs < timeUs) {
            int i = this.frameSize;
            if (((long) i) + seekFramePosition < this.inputLength) {
                long secondSeekPosition = ((long) i) + seekFramePosition;
                return new SeekPoints(seekPoint, new SeekPoint(getTimeUsAtPosition(secondSeekPosition), secondSeekPosition));
            }
        }
        return new SeekPoints(seekPoint);
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    public long getTimeUsAtPosition(long position) {
        return getTimeUsAtPosition(position, this.firstFrameBytePosition, this.bitrate);
    }

    private static long getTimeUsAtPosition(long position, long firstFrameBytePosition, int bitrate) {
        return ((Math.max(0, position - firstFrameBytePosition) * 8) * 1000000) / ((long) bitrate);
    }

    private long getFramePositionForTimeUs(long timeUs) {
        long positionOffset = (((long) this.bitrate) * timeUs) / 8000000;
        int i = this.frameSize;
        return this.firstFrameBytePosition + Util.constrainValue((positionOffset / ((long) i)) * ((long) i), 0, this.dataSize - ((long) i));
    }
}
