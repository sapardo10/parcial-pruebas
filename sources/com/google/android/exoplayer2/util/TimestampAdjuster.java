package com.google.android.exoplayer2.util;

import com.google.android.exoplayer2.C0555C;

public final class TimestampAdjuster {
    public static final long DO_NOT_OFFSET = Long.MAX_VALUE;
    private static final long MAX_PTS_PLUS_ONE = 8589934592L;
    private long firstSampleTimestampUs;
    private volatile long lastSampleTimestampUs = C0555C.TIME_UNSET;
    private long timestampOffsetUs;

    public synchronized void waitUntilInitialized() throws java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x0015 in {5, 7, 10} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
        /*
        r5 = this;
        monitor-enter(r5);
    L_0x0001:
        r0 = r5.lastSampleTimestampUs;	 Catch:{ all -> 0x0012 }
        r2 = -9223372036854775807; // 0x8000000000000001 float:1.4E-45 double:-4.9E-324;	 Catch:{ all -> 0x0012 }
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));	 Catch:{ all -> 0x0012 }
        if (r4 != 0) goto L_0x0010;	 Catch:{ all -> 0x0012 }
    L_0x000c:
        r5.wait();	 Catch:{ all -> 0x0012 }
        goto L_0x0001;
    L_0x0010:
        monitor-exit(r5);
        return;
    L_0x0012:
        r0 = move-exception;
        monitor-exit(r5);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.TimestampAdjuster.waitUntilInitialized():void");
    }

    public TimestampAdjuster(long firstSampleTimestampUs) {
        setFirstSampleTimestampUs(firstSampleTimestampUs);
    }

    public synchronized void setFirstSampleTimestampUs(long firstSampleTimestampUs) {
        Assertions.checkState(this.lastSampleTimestampUs == C0555C.TIME_UNSET);
        this.firstSampleTimestampUs = firstSampleTimestampUs;
    }

    public long getFirstSampleTimestampUs() {
        return this.firstSampleTimestampUs;
    }

    public long getLastAdjustedTimestampUs() {
        if (this.lastSampleTimestampUs != C0555C.TIME_UNSET) {
            return this.timestampOffsetUs + this.lastSampleTimestampUs;
        }
        long j = this.firstSampleTimestampUs;
        return j != Long.MAX_VALUE ? j : C0555C.TIME_UNSET;
    }

    public long getTimestampOffsetUs() {
        if (this.firstSampleTimestampUs == Long.MAX_VALUE) {
            return 0;
        }
        return this.lastSampleTimestampUs == C0555C.TIME_UNSET ? C0555C.TIME_UNSET : this.timestampOffsetUs;
    }

    public void reset() {
        this.lastSampleTimestampUs = C0555C.TIME_UNSET;
    }

    public long adjustTsTimestamp(long pts90Khz) {
        if (pts90Khz == C0555C.TIME_UNSET) {
            return C0555C.TIME_UNSET;
        }
        if (this.lastSampleTimestampUs != C0555C.TIME_UNSET) {
            long lastPts = usToPts(this.lastSampleTimestampUs);
            long closestWrapCount = (4294967296L + lastPts) / MAX_PTS_PLUS_ONE;
            long ptsWrapBelow = ((closestWrapCount - 1) * MAX_PTS_PLUS_ONE) + pts90Khz;
            long ptsWrapAbove = (MAX_PTS_PLUS_ONE * closestWrapCount) + pts90Khz;
            pts90Khz = Math.abs(ptsWrapBelow - lastPts) < Math.abs(ptsWrapAbove - lastPts) ? ptsWrapBelow : ptsWrapAbove;
        }
        return adjustSampleTimestamp(ptsToUs(pts90Khz));
    }

    public long adjustSampleTimestamp(long timeUs) {
        if (timeUs == C0555C.TIME_UNSET) {
            return C0555C.TIME_UNSET;
        }
        if (this.lastSampleTimestampUs != C0555C.TIME_UNSET) {
            this.lastSampleTimestampUs = timeUs;
        } else {
            long j = this.firstSampleTimestampUs;
            if (j != Long.MAX_VALUE) {
                this.timestampOffsetUs = j - timeUs;
            }
            synchronized (this) {
                this.lastSampleTimestampUs = timeUs;
                notifyAll();
            }
        }
        return this.timestampOffsetUs + timeUs;
    }

    public static long ptsToUs(long pts) {
        return (1000000 * pts) / 90000;
    }

    public static long usToPts(long us) {
        return (90000 * us) / 1000000;
    }
}
