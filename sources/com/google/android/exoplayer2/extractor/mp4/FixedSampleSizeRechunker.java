package com.google.android.exoplayer2.extractor.mp4;

import com.google.android.exoplayer2.util.Util;

final class FixedSampleSizeRechunker {
    private static final int MAX_SAMPLE_SIZE = 8192;

    public static final class Results {
        public final long duration;
        public final int[] flags;
        public final int maximumSize;
        public final long[] offsets;
        public final int[] sizes;
        public final long[] timestamps;

        private Results(long[] offsets, int[] sizes, int maximumSize, long[] timestamps, int[] flags, long duration) {
            this.offsets = offsets;
            this.sizes = sizes;
            this.maximumSize = maximumSize;
            this.timestamps = timestamps;
            this.flags = flags;
            this.duration = duration;
        }
    }

    public static Results rechunk(int fixedSampleSize, long[] chunkOffsets, int[] chunkSampleCounts, long timestampDeltaInTimeUnits) {
        int[] iArr = chunkSampleCounts;
        int maxSampleCount = 8192 / fixedSampleSize;
        int rechunkedSampleCount = 0;
        for (int chunkSampleCount : iArr) {
            int chunkSampleCount2;
            rechunkedSampleCount += Util.ceilDivide(chunkSampleCount2, maxSampleCount);
        }
        long[] offsets = new long[rechunkedSampleCount];
        int[] sizes = new int[rechunkedSampleCount];
        long[] timestamps = new long[rechunkedSampleCount];
        int[] flags = new int[rechunkedSampleCount];
        int chunkIndex = 0;
        int maximumSize = 0;
        int originalSampleIndex = 0;
        int newSampleIndex = 0;
        while (chunkIndex < iArr.length) {
            chunkSampleCount2 = iArr[chunkIndex];
            long sampleOffset = chunkOffsets[chunkIndex];
            int maximumSize2 = maximumSize;
            while (chunkSampleCount2 > 0) {
                int bufferSampleCount = Math.min(maxSampleCount, chunkSampleCount2);
                offsets[newSampleIndex] = sampleOffset;
                sizes[newSampleIndex] = fixedSampleSize * bufferSampleCount;
                maximumSize2 = Math.max(maximumSize2, sizes[newSampleIndex]);
                timestamps[newSampleIndex] = ((long) originalSampleIndex) * timestampDeltaInTimeUnits;
                flags[newSampleIndex] = 1;
                sampleOffset += (long) sizes[newSampleIndex];
                originalSampleIndex += bufferSampleCount;
                chunkSampleCount2 -= bufferSampleCount;
                newSampleIndex++;
            }
            chunkIndex++;
            maximumSize = maximumSize2;
        }
        return new Results(offsets, sizes, maximumSize, timestamps, flags, timestampDeltaInTimeUnits * ((long) originalSampleIndex));
    }

    private FixedSampleSizeRechunker() {
    }
}
