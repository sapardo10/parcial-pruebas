package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;

public class ContainerMediaChunk extends BaseMediaChunk {
    private static final PositionHolder DUMMY_POSITION_HOLDER = new PositionHolder();
    private final int chunkCount;
    private final ChunkExtractorWrapper extractorWrapper;
    private volatile boolean loadCanceled;
    private boolean loadCompleted;
    private long nextLoadPosition;
    private final long sampleOffsetUs;

    public final void load() throws java.io.IOException, java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:37:0x009c in {6, 7, 10, 11, 13, 14, 20, 23, 24, 29, 33, 36} preds:[]
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
        r12 = this;
        r0 = r12.dataSpec;
        r1 = r12.nextLoadPosition;
        r0 = r0.subrange(r1);
        r7 = new com.google.android.exoplayer2.extractor.DefaultExtractorInput;	 Catch:{ all -> 0x0095 }
        r2 = r12.dataSource;	 Catch:{ all -> 0x0095 }
        r3 = r0.absoluteStreamPosition;	 Catch:{ all -> 0x0095 }
        r1 = r12.dataSource;	 Catch:{ all -> 0x0095 }
        r5 = r1.open(r0);	 Catch:{ all -> 0x0095 }
        r1 = r7;	 Catch:{ all -> 0x0095 }
        r1.<init>(r2, r3, r5);	 Catch:{ all -> 0x0095 }
        r1 = r7;	 Catch:{ all -> 0x0095 }
        r2 = r12.nextLoadPosition;	 Catch:{ all -> 0x0095 }
        r4 = 0;	 Catch:{ all -> 0x0095 }
        r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));	 Catch:{ all -> 0x0095 }
        if (r6 != 0) goto L_0x0054;	 Catch:{ all -> 0x0095 }
    L_0x0021:
        r2 = r12.getOutput();	 Catch:{ all -> 0x0095 }
        r3 = r12.sampleOffsetUs;	 Catch:{ all -> 0x0095 }
        r2.setSampleOffsetUs(r3);	 Catch:{ all -> 0x0095 }
        r3 = r12.extractorWrapper;	 Catch:{ all -> 0x0095 }
        r4 = r12.clippedStartTimeUs;	 Catch:{ all -> 0x0095 }
        r6 = -9223372036854775807; // 0x8000000000000001 float:1.4E-45 double:-4.9E-324;	 Catch:{ all -> 0x0095 }
        r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));	 Catch:{ all -> 0x0095 }
        if (r8 != 0) goto L_0x0039;	 Catch:{ all -> 0x0095 }
    L_0x0037:
        r8 = r6;	 Catch:{ all -> 0x0095 }
        goto L_0x003f;	 Catch:{ all -> 0x0095 }
    L_0x0039:
        r4 = r12.clippedStartTimeUs;	 Catch:{ all -> 0x0095 }
        r8 = r12.sampleOffsetUs;	 Catch:{ all -> 0x0095 }
        r4 = r4 - r8;	 Catch:{ all -> 0x0095 }
        r8 = r4;	 Catch:{ all -> 0x0095 }
    L_0x003f:
        r4 = r12.clippedEndTimeUs;	 Catch:{ all -> 0x0095 }
        r10 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));	 Catch:{ all -> 0x0095 }
        if (r10 != 0) goto L_0x0047;	 Catch:{ all -> 0x0095 }
    L_0x0045:
        r10 = r6;	 Catch:{ all -> 0x0095 }
        goto L_0x004d;	 Catch:{ all -> 0x0095 }
    L_0x0047:
        r4 = r12.clippedEndTimeUs;	 Catch:{ all -> 0x0095 }
        r6 = r12.sampleOffsetUs;	 Catch:{ all -> 0x0095 }
        r4 = r4 - r6;	 Catch:{ all -> 0x0095 }
        r10 = r4;	 Catch:{ all -> 0x0095 }
    L_0x004d:
        r4 = r2;	 Catch:{ all -> 0x0095 }
        r5 = r8;	 Catch:{ all -> 0x0095 }
        r7 = r10;	 Catch:{ all -> 0x0095 }
        r3.init(r4, r5, r7);	 Catch:{ all -> 0x0095 }
        goto L_0x0055;
    L_0x0055:
        r2 = r12.extractorWrapper;	 Catch:{ all -> 0x0087 }
        r2 = r2.extractor;	 Catch:{ all -> 0x0087 }
        r3 = 0;	 Catch:{ all -> 0x0087 }
    L_0x005a:
        if (r3 != 0) goto L_0x0068;	 Catch:{ all -> 0x0087 }
    L_0x005c:
        r4 = r12.loadCanceled;	 Catch:{ all -> 0x0087 }
        if (r4 != 0) goto L_0x0068;	 Catch:{ all -> 0x0087 }
    L_0x0060:
        r4 = DUMMY_POSITION_HOLDER;	 Catch:{ all -> 0x0087 }
        r4 = r2.read(r1, r4);	 Catch:{ all -> 0x0087 }
        r3 = r4;	 Catch:{ all -> 0x0087 }
        goto L_0x005a;	 Catch:{ all -> 0x0087 }
        r4 = 1;	 Catch:{ all -> 0x0087 }
        if (r3 == r4) goto L_0x006e;	 Catch:{ all -> 0x0087 }
    L_0x006c:
        r5 = 1;	 Catch:{ all -> 0x0087 }
        goto L_0x006f;	 Catch:{ all -> 0x0087 }
    L_0x006e:
        r5 = 0;	 Catch:{ all -> 0x0087 }
    L_0x006f:
        com.google.android.exoplayer2.util.Assertions.checkState(r5);	 Catch:{ all -> 0x0087 }
        r2 = r1.getPosition();	 Catch:{ all -> 0x0095 }
        r5 = r12.dataSpec;	 Catch:{ all -> 0x0095 }
        r5 = r5.absoluteStreamPosition;	 Catch:{ all -> 0x0095 }
        r2 = r2 - r5;	 Catch:{ all -> 0x0095 }
        r12.nextLoadPosition = r2;	 Catch:{ all -> 0x0095 }
        r1 = r12.dataSource;
        com.google.android.exoplayer2.util.Util.closeQuietly(r1);
        r12.loadCompleted = r4;
        return;
    L_0x0087:
        r2 = move-exception;
        r3 = r1.getPosition();	 Catch:{ all -> 0x0095 }
        r5 = r12.dataSpec;	 Catch:{ all -> 0x0095 }
        r5 = r5.absoluteStreamPosition;	 Catch:{ all -> 0x0095 }
        r3 = r3 - r5;	 Catch:{ all -> 0x0095 }
        r12.nextLoadPosition = r3;	 Catch:{ all -> 0x0095 }
        throw r2;	 Catch:{ all -> 0x0095 }
    L_0x0095:
        r1 = move-exception;
        r2 = r12.dataSource;
        com.google.android.exoplayer2.util.Util.closeQuietly(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.chunk.ContainerMediaChunk.load():void");
    }

    public ContainerMediaChunk(DataSource dataSource, DataSpec dataSpec, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long startTimeUs, long endTimeUs, long clippedStartTimeUs, long clippedEndTimeUs, long chunkIndex, int chunkCount, long sampleOffsetUs, ChunkExtractorWrapper extractorWrapper) {
        super(dataSource, dataSpec, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, clippedStartTimeUs, clippedEndTimeUs, chunkIndex);
        this.chunkCount = chunkCount;
        this.sampleOffsetUs = sampleOffsetUs;
        this.extractorWrapper = extractorWrapper;
    }

    public long getNextChunkIndex() {
        return this.chunkIndex + ((long) this.chunkCount);
    }

    public boolean isLoadCompleted() {
        return this.loadCompleted;
    }

    public final void cancelLoad() {
        this.loadCanceled = true;
    }
}
