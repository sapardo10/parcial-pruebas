package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.extractor.SeekMap.SeekPoints;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class ChunkIndex implements SeekMap {
    private final long durationUs;
    public final long[] durationsUs;
    public final int length;
    public final long[] offsets;
    public final int[] sizes;
    public final long[] timesUs;

    public ChunkIndex(int[] sizes, long[] offsets, long[] durationsUs, long[] timesUs) {
        this.sizes = sizes;
        this.offsets = offsets;
        this.durationsUs = durationsUs;
        this.timesUs = timesUs;
        this.length = sizes.length;
        int i = this.length;
        if (i > 0) {
            this.durationUs = durationsUs[i - 1] + timesUs[i - 1];
        } else {
            this.durationUs = 0;
        }
    }

    public int getChunkIndex(long timeUs) {
        return Util.binarySearchFloor(this.timesUs, timeUs, true, true);
    }

    public boolean isSeekable() {
        return true;
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    public SeekPoints getSeekPoints(long timeUs) {
        int chunkIndex = getChunkIndex(timeUs);
        SeekPoint seekPoint = new SeekPoint(this.timesUs[chunkIndex], this.offsets[chunkIndex]);
        if (seekPoint.timeUs < timeUs) {
            if (chunkIndex != this.length - 1) {
                return new SeekPoints(seekPoint, new SeekPoint(this.timesUs[chunkIndex + 1], this.offsets[chunkIndex + 1]));
            }
        }
        return new SeekPoints(seekPoint);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ChunkIndex(length=");
        stringBuilder.append(this.length);
        stringBuilder.append(", sizes=");
        stringBuilder.append(Arrays.toString(this.sizes));
        stringBuilder.append(", offsets=");
        stringBuilder.append(Arrays.toString(this.offsets));
        stringBuilder.append(", timeUs=");
        stringBuilder.append(Arrays.toString(this.timesUs));
        stringBuilder.append(", durationsUs=");
        stringBuilder.append(Arrays.toString(this.durationsUs));
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
