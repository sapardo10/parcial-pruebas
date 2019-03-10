package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.extractor.TrackOutput.CryptoData;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

final class SampleMetadataQueue {
    private static final int SAMPLE_CAPACITY_INCREMENT = 1000;
    private int absoluteFirstIndex;
    private int capacity = 1000;
    private CryptoData[] cryptoDatas;
    private int[] flags;
    private Format[] formats;
    private long largestDiscardedTimestampUs;
    private long largestQueuedTimestampUs;
    private int length;
    private long[] offsets;
    private int readPosition;
    private int relativeFirstIndex;
    private int[] sizes;
    private int[] sourceIds;
    private long[] timesUs;
    private Format upstreamFormat;
    private boolean upstreamFormatRequired;
    private boolean upstreamKeyframeRequired;
    private int upstreamSourceId;

    public static final class SampleExtrasHolder {
        public CryptoData cryptoData;
        public long offset;
        public int size;
    }

    public synchronized boolean attemptSplice(long r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:32:0x0051 in {7, 9, 15, 24, 25, 28, 31} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r8 = this;
        monitor-enter(r8);
        r0 = r8.length;	 Catch:{ all -> 0x004e }
        r1 = 0;	 Catch:{ all -> 0x004e }
        r2 = 1;	 Catch:{ all -> 0x004e }
        if (r0 != 0) goto L_0x0011;	 Catch:{ all -> 0x004e }
    L_0x0007:
        r3 = r8.largestDiscardedTimestampUs;	 Catch:{ all -> 0x004e }
        r0 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1));
        if (r0 <= 0) goto L_0x000f;
    L_0x000d:
        r1 = 1;
    L_0x000f:
        monitor-exit(r8);
        return r1;
    L_0x0011:
        r3 = r8.largestDiscardedTimestampUs;	 Catch:{ all -> 0x004e }
        r0 = r8.readPosition;	 Catch:{ all -> 0x004e }
        r5 = r8.getLargestTimestamp(r0);	 Catch:{ all -> 0x004e }
        r3 = java.lang.Math.max(r3, r5);	 Catch:{ all -> 0x004e }
        r0 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1));
        if (r0 < 0) goto L_0x0023;
    L_0x0021:
        monitor-exit(r8);
        return r1;
    L_0x0023:
        r0 = r8.length;	 Catch:{ all -> 0x004e }
        r1 = r8.length;	 Catch:{ all -> 0x004e }
        r1 = r1 - r2;	 Catch:{ all -> 0x004e }
        r1 = r8.getRelativeIndex(r1);	 Catch:{ all -> 0x004e }
    L_0x002c:
        r5 = r8.readPosition;	 Catch:{ all -> 0x004e }
        if (r0 <= r5) goto L_0x0045;	 Catch:{ all -> 0x004e }
    L_0x0030:
        r5 = r8.timesUs;	 Catch:{ all -> 0x004e }
        r6 = r5[r1];	 Catch:{ all -> 0x004e }
        r5 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1));	 Catch:{ all -> 0x004e }
        if (r5 < 0) goto L_0x0045;	 Catch:{ all -> 0x004e }
    L_0x0038:
        r0 = r0 + -1;	 Catch:{ all -> 0x004e }
        r1 = r1 + -1;	 Catch:{ all -> 0x004e }
        r5 = -1;	 Catch:{ all -> 0x004e }
        if (r1 != r5) goto L_0x0044;	 Catch:{ all -> 0x004e }
    L_0x003f:
        r5 = r8.capacity;	 Catch:{ all -> 0x004e }
        r1 = r5 + -1;	 Catch:{ all -> 0x004e }
        goto L_0x002c;	 Catch:{ all -> 0x004e }
    L_0x0044:
        goto L_0x002c;	 Catch:{ all -> 0x004e }
        r5 = r8.absoluteFirstIndex;	 Catch:{ all -> 0x004e }
        r5 = r5 + r0;	 Catch:{ all -> 0x004e }
        r8.discardUpstreamSamples(r5);	 Catch:{ all -> 0x004e }
        monitor-exit(r8);
        return r2;
    L_0x004e:
        r9 = move-exception;
        monitor-exit(r8);
        throw r9;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.SampleMetadataQueue.attemptSplice(long):boolean");
    }

    public SampleMetadataQueue() {
        int i = this.capacity;
        this.sourceIds = new int[i];
        this.offsets = new long[i];
        this.timesUs = new long[i];
        this.flags = new int[i];
        this.sizes = new int[i];
        this.cryptoDatas = new CryptoData[i];
        this.formats = new Format[i];
        this.largestDiscardedTimestampUs = Long.MIN_VALUE;
        this.largestQueuedTimestampUs = Long.MIN_VALUE;
        this.upstreamFormatRequired = true;
        this.upstreamKeyframeRequired = true;
    }

    public void reset(boolean resetUpstreamFormat) {
        this.length = 0;
        this.absoluteFirstIndex = 0;
        this.relativeFirstIndex = 0;
        this.readPosition = 0;
        this.upstreamKeyframeRequired = true;
        this.largestDiscardedTimestampUs = Long.MIN_VALUE;
        this.largestQueuedTimestampUs = Long.MIN_VALUE;
        if (resetUpstreamFormat) {
            this.upstreamFormat = null;
            this.upstreamFormatRequired = true;
        }
    }

    public int getWriteIndex() {
        return this.absoluteFirstIndex + this.length;
    }

    public long discardUpstreamSamples(int discardFromIndex) {
        int discardCount = getWriteIndex() - discardFromIndex;
        boolean z = discardCount >= 0 && discardCount <= this.length - this.readPosition;
        Assertions.checkArgument(z);
        this.length -= discardCount;
        this.largestQueuedTimestampUs = Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(this.length));
        int i = this.length;
        if (i == 0) {
            return 0;
        }
        int relativeLastWriteIndex = getRelativeIndex(i - 1);
        return this.offsets[relativeLastWriteIndex] + ((long) this.sizes[relativeLastWriteIndex]);
    }

    public void sourceId(int sourceId) {
        this.upstreamSourceId = sourceId;
    }

    public int getFirstIndex() {
        return this.absoluteFirstIndex;
    }

    public int getReadIndex() {
        return this.absoluteFirstIndex + this.readPosition;
    }

    public int peekSourceId() {
        return hasNextSample() ? this.sourceIds[getRelativeIndex(this.readPosition)] : this.upstreamSourceId;
    }

    public synchronized boolean hasNextSample() {
        return this.readPosition != this.length;
    }

    public synchronized Format getUpstreamFormat() {
        return this.upstreamFormatRequired ? null : this.upstreamFormat;
    }

    public synchronized long getLargestQueuedTimestampUs() {
        return this.largestQueuedTimestampUs;
    }

    public synchronized long getFirstTimestampUs() {
        return this.length == 0 ? Long.MIN_VALUE : this.timesUs[this.relativeFirstIndex];
    }

    public synchronized void rewind() {
        this.readPosition = 0;
    }

    public synchronized int read(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired, boolean loadingFinished, Format downstreamFormat, SampleExtrasHolder extrasHolder) {
        if (hasNextSample()) {
            int relativeReadIndex = getRelativeIndex(this.readPosition);
            if (!formatRequired) {
                if (this.formats[relativeReadIndex] == downstreamFormat) {
                    if (buffer.isFlagsOnly()) {
                        return -3;
                    }
                    buffer.timeUs = this.timesUs[relativeReadIndex];
                    buffer.setFlags(this.flags[relativeReadIndex]);
                    extrasHolder.size = this.sizes[relativeReadIndex];
                    extrasHolder.offset = this.offsets[relativeReadIndex];
                    extrasHolder.cryptoData = this.cryptoDatas[relativeReadIndex];
                    this.readPosition++;
                    return -4;
                }
            }
            formatHolder.format = this.formats[relativeReadIndex];
            return -5;
        } else if (loadingFinished) {
            buffer.setFlags(4);
            return -4;
        } else if (this.upstreamFormat == null || (!formatRequired && this.upstreamFormat == downstreamFormat)) {
            return -3;
        } else {
            formatHolder.format = this.upstreamFormat;
            return -5;
        }
    }

    public synchronized int advanceTo(long timeUs, boolean toKeyframe, boolean allowTimeBeyondBuffer) {
        int relativeReadIndex = getRelativeIndex(this.readPosition);
        if (hasNextSample() && timeUs >= this.timesUs[relativeReadIndex]) {
            if (timeUs <= this.largestQueuedTimestampUs || allowTimeBeyondBuffer) {
                int offset = findSampleBefore(relativeReadIndex, this.length - this.readPosition, timeUs, toKeyframe);
                if (offset == -1) {
                    return -1;
                }
                this.readPosition += offset;
                return offset;
            }
        }
        return -1;
    }

    public synchronized int advanceToEnd() {
        int skipCount;
        skipCount = this.length - this.readPosition;
        this.readPosition = this.length;
        return skipCount;
    }

    public synchronized boolean setReadPosition(int sampleIndex) {
        if (this.absoluteFirstIndex > sampleIndex || sampleIndex > this.absoluteFirstIndex + this.length) {
            return false;
        }
        this.readPosition = sampleIndex - this.absoluteFirstIndex;
        return true;
    }

    public synchronized long discardTo(long timeUs, boolean toKeyframe, boolean stopAtReadPosition) {
        if (this.length != 0) {
            if (timeUs >= this.timesUs[this.relativeFirstIndex]) {
                int searchLength = (!stopAtReadPosition || this.readPosition == this.length) ? this.length : this.readPosition + 1;
                int discardCount = findSampleBefore(this.relativeFirstIndex, searchLength, timeUs, toKeyframe);
                if (discardCount == -1) {
                    return -1;
                }
                return discardSamples(discardCount);
            }
        }
        return -1;
    }

    public synchronized long discardToRead() {
        if (this.readPosition == 0) {
            return -1;
        }
        return discardSamples(this.readPosition);
    }

    public synchronized long discardToEnd() {
        if (this.length == 0) {
            return -1;
        }
        return discardSamples(this.length);
    }

    public synchronized boolean format(Format format) {
        if (format == null) {
            this.upstreamFormatRequired = true;
            return false;
        }
        this.upstreamFormatRequired = false;
        if (Util.areEqual(format, this.upstreamFormat)) {
            return false;
        }
        this.upstreamFormat = format;
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void commitSample(long r15, int r17, long r18, int r20, com.google.android.exoplayer2.extractor.TrackOutput.CryptoData r21) {
        /*
        r14 = this;
        r1 = r14;
        monitor-enter(r14);
        r0 = r1.upstreamKeyframeRequired;	 Catch:{ all -> 0x00d7 }
        r2 = 0;
        if (r0 == 0) goto L_0x0010;
    L_0x0007:
        r0 = r17 & 1;
        if (r0 != 0) goto L_0x000d;
    L_0x000b:
        monitor-exit(r14);
        return;
    L_0x000d:
        r1.upstreamKeyframeRequired = r2;	 Catch:{ all -> 0x00d7 }
        goto L_0x0011;
    L_0x0011:
        r0 = r1.upstreamFormatRequired;	 Catch:{ all -> 0x00d7 }
        r3 = 1;
        if (r0 != 0) goto L_0x0018;
    L_0x0016:
        r0 = 1;
        goto L_0x0019;
    L_0x0018:
        r0 = 0;
    L_0x0019:
        com.google.android.exoplayer2.util.Assertions.checkState(r0);	 Catch:{ all -> 0x00d7 }
        r14.commitSampleTimestamp(r15);	 Catch:{ all -> 0x00d7 }
        r0 = r1.length;	 Catch:{ all -> 0x00d7 }
        r0 = r14.getRelativeIndex(r0);	 Catch:{ all -> 0x00d7 }
        r4 = r1.timesUs;	 Catch:{ all -> 0x00d7 }
        r4[r0] = r15;	 Catch:{ all -> 0x00d7 }
        r4 = r1.offsets;	 Catch:{ all -> 0x00d7 }
        r4[r0] = r18;	 Catch:{ all -> 0x00d7 }
        r4 = r1.sizes;	 Catch:{ all -> 0x00d7 }
        r4[r0] = r20;	 Catch:{ all -> 0x00d7 }
        r4 = r1.flags;	 Catch:{ all -> 0x00d7 }
        r4[r0] = r17;	 Catch:{ all -> 0x00d7 }
        r4 = r1.cryptoDatas;	 Catch:{ all -> 0x00d7 }
        r4[r0] = r21;	 Catch:{ all -> 0x00d7 }
        r4 = r1.formats;	 Catch:{ all -> 0x00d7 }
        r5 = r1.upstreamFormat;	 Catch:{ all -> 0x00d7 }
        r4[r0] = r5;	 Catch:{ all -> 0x00d7 }
        r4 = r1.sourceIds;	 Catch:{ all -> 0x00d7 }
        r5 = r1.upstreamSourceId;	 Catch:{ all -> 0x00d7 }
        r4[r0] = r5;	 Catch:{ all -> 0x00d7 }
        r4 = r1.length;	 Catch:{ all -> 0x00d7 }
        r4 = r4 + r3;
        r1.length = r4;	 Catch:{ all -> 0x00d7 }
        r3 = r1.length;	 Catch:{ all -> 0x00d7 }
        r4 = r1.capacity;	 Catch:{ all -> 0x00d7 }
        if (r3 != r4) goto L_0x00d4;
    L_0x0050:
        r3 = r1.capacity;	 Catch:{ all -> 0x00d7 }
        r3 = r3 + 1000;
        r4 = new int[r3];	 Catch:{ all -> 0x00d7 }
        r5 = new long[r3];	 Catch:{ all -> 0x00d7 }
        r6 = new long[r3];	 Catch:{ all -> 0x00d7 }
        r7 = new int[r3];	 Catch:{ all -> 0x00d7 }
        r8 = new int[r3];	 Catch:{ all -> 0x00d7 }
        r9 = new com.google.android.exoplayer2.extractor.TrackOutput.CryptoData[r3];	 Catch:{ all -> 0x00d7 }
        r10 = new com.google.android.exoplayer2.Format[r3];	 Catch:{ all -> 0x00d7 }
        r11 = r1.capacity;	 Catch:{ all -> 0x00d7 }
        r12 = r1.relativeFirstIndex;	 Catch:{ all -> 0x00d7 }
        r11 = r11 - r12;
        r12 = r1.offsets;	 Catch:{ all -> 0x00d7 }
        r13 = r1.relativeFirstIndex;	 Catch:{ all -> 0x00d7 }
        java.lang.System.arraycopy(r12, r13, r5, r2, r11);	 Catch:{ all -> 0x00d7 }
        r12 = r1.timesUs;	 Catch:{ all -> 0x00d7 }
        r13 = r1.relativeFirstIndex;	 Catch:{ all -> 0x00d7 }
        java.lang.System.arraycopy(r12, r13, r6, r2, r11);	 Catch:{ all -> 0x00d7 }
        r12 = r1.flags;	 Catch:{ all -> 0x00d7 }
        r13 = r1.relativeFirstIndex;	 Catch:{ all -> 0x00d7 }
        java.lang.System.arraycopy(r12, r13, r7, r2, r11);	 Catch:{ all -> 0x00d7 }
        r12 = r1.sizes;	 Catch:{ all -> 0x00d7 }
        r13 = r1.relativeFirstIndex;	 Catch:{ all -> 0x00d7 }
        java.lang.System.arraycopy(r12, r13, r8, r2, r11);	 Catch:{ all -> 0x00d7 }
        r12 = r1.cryptoDatas;	 Catch:{ all -> 0x00d7 }
        r13 = r1.relativeFirstIndex;	 Catch:{ all -> 0x00d7 }
        java.lang.System.arraycopy(r12, r13, r9, r2, r11);	 Catch:{ all -> 0x00d7 }
        r12 = r1.formats;	 Catch:{ all -> 0x00d7 }
        r13 = r1.relativeFirstIndex;	 Catch:{ all -> 0x00d7 }
        java.lang.System.arraycopy(r12, r13, r10, r2, r11);	 Catch:{ all -> 0x00d7 }
        r12 = r1.sourceIds;	 Catch:{ all -> 0x00d7 }
        r13 = r1.relativeFirstIndex;	 Catch:{ all -> 0x00d7 }
        java.lang.System.arraycopy(r12, r13, r4, r2, r11);	 Catch:{ all -> 0x00d7 }
        r12 = r1.relativeFirstIndex;	 Catch:{ all -> 0x00d7 }
        r13 = r1.offsets;	 Catch:{ all -> 0x00d7 }
        java.lang.System.arraycopy(r13, r2, r5, r11, r12);	 Catch:{ all -> 0x00d7 }
        r13 = r1.timesUs;	 Catch:{ all -> 0x00d7 }
        java.lang.System.arraycopy(r13, r2, r6, r11, r12);	 Catch:{ all -> 0x00d7 }
        r13 = r1.flags;	 Catch:{ all -> 0x00d7 }
        java.lang.System.arraycopy(r13, r2, r7, r11, r12);	 Catch:{ all -> 0x00d7 }
        r13 = r1.sizes;	 Catch:{ all -> 0x00d7 }
        java.lang.System.arraycopy(r13, r2, r8, r11, r12);	 Catch:{ all -> 0x00d7 }
        r13 = r1.cryptoDatas;	 Catch:{ all -> 0x00d7 }
        java.lang.System.arraycopy(r13, r2, r9, r11, r12);	 Catch:{ all -> 0x00d7 }
        r13 = r1.formats;	 Catch:{ all -> 0x00d7 }
        java.lang.System.arraycopy(r13, r2, r10, r11, r12);	 Catch:{ all -> 0x00d7 }
        r13 = r1.sourceIds;	 Catch:{ all -> 0x00d7 }
        java.lang.System.arraycopy(r13, r2, r4, r11, r12);	 Catch:{ all -> 0x00d7 }
        r1.offsets = r5;	 Catch:{ all -> 0x00d7 }
        r1.timesUs = r6;	 Catch:{ all -> 0x00d7 }
        r1.flags = r7;	 Catch:{ all -> 0x00d7 }
        r1.sizes = r8;	 Catch:{ all -> 0x00d7 }
        r1.cryptoDatas = r9;	 Catch:{ all -> 0x00d7 }
        r1.formats = r10;	 Catch:{ all -> 0x00d7 }
        r1.sourceIds = r4;	 Catch:{ all -> 0x00d7 }
        r1.relativeFirstIndex = r2;	 Catch:{ all -> 0x00d7 }
        r2 = r1.capacity;	 Catch:{ all -> 0x00d7 }
        r1.length = r2;	 Catch:{ all -> 0x00d7 }
        r1.capacity = r3;	 Catch:{ all -> 0x00d7 }
        goto L_0x00d5;
    L_0x00d5:
        monitor-exit(r14);
        return;
    L_0x00d7:
        r0 = move-exception;
        monitor-exit(r14);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.SampleMetadataQueue.commitSample(long, int, long, int, com.google.android.exoplayer2.extractor.TrackOutput$CryptoData):void");
    }

    public synchronized void commitSampleTimestamp(long timeUs) {
        this.largestQueuedTimestampUs = Math.max(this.largestQueuedTimestampUs, timeUs);
    }

    private int findSampleBefore(int relativeStartIndex, int length, long timeUs, boolean keyframe) {
        int sampleCountToTarget = -1;
        int searchIndex = relativeStartIndex;
        for (int i = 0; i < length && this.timesUs[searchIndex] <= timeUs; i++) {
            if (keyframe) {
                if ((this.flags[searchIndex] & 1) == 0) {
                    searchIndex++;
                    if (searchIndex == this.capacity) {
                        searchIndex = 0;
                    }
                }
            }
            sampleCountToTarget = i;
            searchIndex++;
            if (searchIndex == this.capacity) {
                searchIndex = 0;
            }
        }
        return sampleCountToTarget;
    }

    private long discardSamples(int discardCount) {
        this.largestDiscardedTimestampUs = Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(discardCount));
        this.length -= discardCount;
        this.absoluteFirstIndex += discardCount;
        this.relativeFirstIndex += discardCount;
        int i = this.relativeFirstIndex;
        int i2 = this.capacity;
        if (i >= i2) {
            this.relativeFirstIndex = i - i2;
        }
        this.readPosition -= discardCount;
        if (this.readPosition < 0) {
            this.readPosition = 0;
        }
        if (this.length != 0) {
            return this.offsets[this.relativeFirstIndex];
        }
        i = this.relativeFirstIndex;
        if (i == 0) {
            i = this.capacity;
        }
        i--;
        return this.offsets[i] + ((long) this.sizes[i]);
    }

    private long getLargestTimestamp(int length) {
        if (length == 0) {
            return Long.MIN_VALUE;
        }
        long largestTimestampUs = Long.MIN_VALUE;
        int relativeSampleIndex = getRelativeIndex(length - 1);
        for (int i = 0; i < length; i++) {
            largestTimestampUs = Math.max(largestTimestampUs, this.timesUs[relativeSampleIndex]);
            if ((this.flags[relativeSampleIndex] & 1) != 0) {
                break;
            }
            relativeSampleIndex--;
            if (relativeSampleIndex == -1) {
                relativeSampleIndex = this.capacity - 1;
            }
        }
        return largestTimestampUs;
    }

    private int getRelativeIndex(int offset) {
        int relativeIndex = this.relativeFirstIndex + offset;
        int i = this.capacity;
        return relativeIndex < i ? relativeIndex : relativeIndex - i;
    }
}
