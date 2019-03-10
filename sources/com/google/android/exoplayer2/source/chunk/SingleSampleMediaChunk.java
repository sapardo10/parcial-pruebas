package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;

public final class SingleSampleMediaChunk extends BaseMediaChunk {
    private boolean loadCompleted;
    private long nextLoadPosition;
    private final Format sampleFormat;
    private final int trackType;

    public void load() throws java.io.IOException, java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x0072 in {4, 5, 9, 12, 15} preds:[]
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
        r18 = this;
        r1 = r18;
        r0 = r1.dataSpec;
        r2 = r1.nextLoadPosition;
        r2 = r0.subrange(r2);
        r0 = r1.dataSource;	 Catch:{ all -> 0x006b }
        r3 = r0.open(r2);	 Catch:{ all -> 0x006b }
        r5 = -1;	 Catch:{ all -> 0x006b }
        r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));	 Catch:{ all -> 0x006b }
        if (r0 == 0) goto L_0x001a;	 Catch:{ all -> 0x006b }
    L_0x0016:
        r5 = r1.nextLoadPosition;	 Catch:{ all -> 0x006b }
        r3 = r3 + r5;	 Catch:{ all -> 0x006b }
        goto L_0x001b;	 Catch:{ all -> 0x006b }
    L_0x001b:
        r0 = new com.google.android.exoplayer2.extractor.DefaultExtractorInput;	 Catch:{ all -> 0x006b }
        r6 = r1.dataSource;	 Catch:{ all -> 0x006b }
        r7 = r1.nextLoadPosition;	 Catch:{ all -> 0x006b }
        r5 = r0;	 Catch:{ all -> 0x006b }
        r9 = r3;	 Catch:{ all -> 0x006b }
        r5.<init>(r6, r7, r9);	 Catch:{ all -> 0x006b }
        r5 = r18.getOutput();	 Catch:{ all -> 0x006b }
        r6 = 0;	 Catch:{ all -> 0x006b }
        r5.setSampleOffsetUs(r6);	 Catch:{ all -> 0x006b }
        r6 = 0;	 Catch:{ all -> 0x006b }
        r7 = r1.trackType;	 Catch:{ all -> 0x006b }
        r6 = r5.track(r6, r7);	 Catch:{ all -> 0x006b }
        r7 = r1.sampleFormat;	 Catch:{ all -> 0x006b }
        r6.format(r7);	 Catch:{ all -> 0x006b }
        r7 = 0;	 Catch:{ all -> 0x006b }
        r14 = r7;	 Catch:{ all -> 0x006b }
    L_0x003d:
        r7 = -1;	 Catch:{ all -> 0x006b }
        r15 = 1;	 Catch:{ all -> 0x006b }
        if (r14 == r7) goto L_0x0050;	 Catch:{ all -> 0x006b }
    L_0x0041:
        r7 = r1.nextLoadPosition;	 Catch:{ all -> 0x006b }
        r9 = (long) r14;	 Catch:{ all -> 0x006b }
        r7 = r7 + r9;	 Catch:{ all -> 0x006b }
        r1.nextLoadPosition = r7;	 Catch:{ all -> 0x006b }
        r7 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;	 Catch:{ all -> 0x006b }
        r7 = r6.sampleData(r0, r7, r15);	 Catch:{ all -> 0x006b }
        r14 = r7;	 Catch:{ all -> 0x006b }
        goto L_0x003d;	 Catch:{ all -> 0x006b }
    L_0x0050:
        r7 = r1.nextLoadPosition;	 Catch:{ all -> 0x006b }
        r13 = (int) r7;	 Catch:{ all -> 0x006b }
        r8 = r1.startTimeUs;	 Catch:{ all -> 0x006b }
        r10 = 1;	 Catch:{ all -> 0x006b }
        r12 = 0;	 Catch:{ all -> 0x006b }
        r16 = 0;	 Catch:{ all -> 0x006b }
        r7 = r6;	 Catch:{ all -> 0x006b }
        r11 = r13;	 Catch:{ all -> 0x006b }
        r17 = r13;	 Catch:{ all -> 0x006b }
        r13 = r16;	 Catch:{ all -> 0x006b }
        r7.sampleMetadata(r8, r10, r11, r12, r13);	 Catch:{ all -> 0x006b }
        r0 = r1.dataSource;
        com.google.android.exoplayer2.util.Util.closeQuietly(r0);
        r1.loadCompleted = r15;
        return;
    L_0x006b:
        r0 = move-exception;
        r3 = r1.dataSource;
        com.google.android.exoplayer2.util.Util.closeQuietly(r3);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.chunk.SingleSampleMediaChunk.load():void");
    }

    public SingleSampleMediaChunk(DataSource dataSource, DataSpec dataSpec, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long startTimeUs, long endTimeUs, long chunkIndex, int trackType, Format sampleFormat) {
        SingleSampleMediaChunk singleSampleMediaChunk = this;
        super(dataSource, dataSpec, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, C0555C.TIME_UNSET, C0555C.TIME_UNSET, chunkIndex);
        this.trackType = trackType;
        this.sampleFormat = sampleFormat;
    }

    public boolean isLoadCompleted() {
        return this.loadCompleted;
    }

    public void cancelLoad() {
    }
}
