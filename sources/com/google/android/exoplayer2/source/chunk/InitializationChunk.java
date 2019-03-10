package com.google.android.exoplayer2.source.chunk;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;

public final class InitializationChunk extends Chunk {
    private static final PositionHolder DUMMY_POSITION_HOLDER = new PositionHolder();
    private final ChunkExtractorWrapper extractorWrapper;
    private volatile boolean loadCanceled;
    private long nextLoadPosition;

    public void load() throws java.io.IOException, java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:29:0x0077 in {5, 6, 12, 15, 16, 21, 25, 28} preds:[]
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
        r13 = this;
        r0 = r13.dataSpec;
        r1 = r13.nextLoadPosition;
        r0 = r0.subrange(r1);
        r7 = new com.google.android.exoplayer2.extractor.DefaultExtractorInput;	 Catch:{ all -> 0x0070 }
        r2 = r13.dataSource;	 Catch:{ all -> 0x0070 }
        r3 = r0.absoluteStreamPosition;	 Catch:{ all -> 0x0070 }
        r1 = r13.dataSource;	 Catch:{ all -> 0x0070 }
        r5 = r1.open(r0);	 Catch:{ all -> 0x0070 }
        r1 = r7;	 Catch:{ all -> 0x0070 }
        r1.<init>(r2, r3, r5);	 Catch:{ all -> 0x0070 }
        r1 = r7;	 Catch:{ all -> 0x0070 }
        r2 = r13.nextLoadPosition;	 Catch:{ all -> 0x0070 }
        r4 = 0;	 Catch:{ all -> 0x0070 }
        r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));	 Catch:{ all -> 0x0070 }
        if (r6 != 0) goto L_0x0032;	 Catch:{ all -> 0x0070 }
    L_0x0021:
        r7 = r13.extractorWrapper;	 Catch:{ all -> 0x0070 }
        r8 = 0;	 Catch:{ all -> 0x0070 }
        r9 = -9223372036854775807; // 0x8000000000000001 float:1.4E-45 double:-4.9E-324;	 Catch:{ all -> 0x0070 }
        r11 = -9223372036854775807; // 0x8000000000000001 float:1.4E-45 double:-4.9E-324;	 Catch:{ all -> 0x0070 }
        r7.init(r8, r9, r11);	 Catch:{ all -> 0x0070 }
        goto L_0x0033;
    L_0x0033:
        r2 = r13.extractorWrapper;	 Catch:{ all -> 0x0062 }
        r2 = r2.extractor;	 Catch:{ all -> 0x0062 }
        r3 = 0;	 Catch:{ all -> 0x0062 }
    L_0x0038:
        if (r3 != 0) goto L_0x0046;	 Catch:{ all -> 0x0062 }
    L_0x003a:
        r4 = r13.loadCanceled;	 Catch:{ all -> 0x0062 }
        if (r4 != 0) goto L_0x0046;	 Catch:{ all -> 0x0062 }
    L_0x003e:
        r4 = DUMMY_POSITION_HOLDER;	 Catch:{ all -> 0x0062 }
        r4 = r2.read(r1, r4);	 Catch:{ all -> 0x0062 }
        r3 = r4;	 Catch:{ all -> 0x0062 }
        goto L_0x0038;	 Catch:{ all -> 0x0062 }
        r4 = 1;	 Catch:{ all -> 0x0062 }
        if (r3 == r4) goto L_0x004b;	 Catch:{ all -> 0x0062 }
    L_0x004a:
        goto L_0x004c;	 Catch:{ all -> 0x0062 }
    L_0x004b:
        r4 = 0;	 Catch:{ all -> 0x0062 }
    L_0x004c:
        com.google.android.exoplayer2.util.Assertions.checkState(r4);	 Catch:{ all -> 0x0062 }
        r2 = r1.getPosition();	 Catch:{ all -> 0x0070 }
        r4 = r13.dataSpec;	 Catch:{ all -> 0x0070 }
        r4 = r4.absoluteStreamPosition;	 Catch:{ all -> 0x0070 }
        r2 = r2 - r4;	 Catch:{ all -> 0x0070 }
        r13.nextLoadPosition = r2;	 Catch:{ all -> 0x0070 }
        r1 = r13.dataSource;
        com.google.android.exoplayer2.util.Util.closeQuietly(r1);
        return;
    L_0x0062:
        r2 = move-exception;
        r3 = r1.getPosition();	 Catch:{ all -> 0x0070 }
        r5 = r13.dataSpec;	 Catch:{ all -> 0x0070 }
        r5 = r5.absoluteStreamPosition;	 Catch:{ all -> 0x0070 }
        r3 = r3 - r5;	 Catch:{ all -> 0x0070 }
        r13.nextLoadPosition = r3;	 Catch:{ all -> 0x0070 }
        throw r2;	 Catch:{ all -> 0x0070 }
    L_0x0070:
        r1 = move-exception;
        r2 = r13.dataSource;
        com.google.android.exoplayer2.util.Util.closeQuietly(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.chunk.InitializationChunk.load():void");
    }

    public InitializationChunk(DataSource dataSource, DataSpec dataSpec, Format trackFormat, int trackSelectionReason, @Nullable Object trackSelectionData, ChunkExtractorWrapper extractorWrapper) {
        super(dataSource, dataSpec, 2, trackFormat, trackSelectionReason, trackSelectionData, C0555C.TIME_UNSET, C0555C.TIME_UNSET);
        this.extractorWrapper = extractorWrapper;
    }

    public void cancelLoad() {
        this.loadCanceled = true;
    }
}
