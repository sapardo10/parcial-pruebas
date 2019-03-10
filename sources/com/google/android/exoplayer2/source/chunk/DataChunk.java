package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.C0555C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import java.io.IOException;
import java.util.Arrays;

public abstract class DataChunk extends Chunk {
    private static final int READ_GRANULARITY = 16384;
    private byte[] data;
    private volatile boolean loadCanceled;

    protected abstract void consume(byte[] bArr, int i) throws IOException;

    public final void load() throws java.io.IOException, java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:20:0x003d in {8, 9, 13, 14, 16, 19} preds:[]
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
        r6 = this;
        r0 = r6.dataSource;	 Catch:{ all -> 0x0036 }
        r1 = r6.dataSpec;	 Catch:{ all -> 0x0036 }
        r0.open(r1);	 Catch:{ all -> 0x0036 }
        r0 = 0;	 Catch:{ all -> 0x0036 }
        r1 = 0;	 Catch:{ all -> 0x0036 }
    L_0x0009:
        r2 = -1;	 Catch:{ all -> 0x0036 }
        if (r1 == r2) goto L_0x0023;	 Catch:{ all -> 0x0036 }
    L_0x000c:
        r3 = r6.loadCanceled;	 Catch:{ all -> 0x0036 }
        if (r3 != 0) goto L_0x0023;	 Catch:{ all -> 0x0036 }
    L_0x0010:
        r6.maybeExpandData(r0);	 Catch:{ all -> 0x0036 }
        r3 = r6.dataSource;	 Catch:{ all -> 0x0036 }
        r4 = r6.data;	 Catch:{ all -> 0x0036 }
        r5 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;	 Catch:{ all -> 0x0036 }
        r3 = r3.read(r4, r0, r5);	 Catch:{ all -> 0x0036 }
        r1 = r3;	 Catch:{ all -> 0x0036 }
        if (r1 == r2) goto L_0x0022;	 Catch:{ all -> 0x0036 }
    L_0x0020:
        r0 = r0 + r1;	 Catch:{ all -> 0x0036 }
        goto L_0x0009;	 Catch:{ all -> 0x0036 }
    L_0x0022:
        goto L_0x0009;	 Catch:{ all -> 0x0036 }
        r2 = r6.loadCanceled;	 Catch:{ all -> 0x0036 }
        if (r2 != 0) goto L_0x002e;	 Catch:{ all -> 0x0036 }
    L_0x0028:
        r2 = r6.data;	 Catch:{ all -> 0x0036 }
        r6.consume(r2, r0);	 Catch:{ all -> 0x0036 }
        goto L_0x002f;
    L_0x002f:
        r0 = r6.dataSource;
        com.google.android.exoplayer2.util.Util.closeQuietly(r0);
        return;
    L_0x0036:
        r0 = move-exception;
        r1 = r6.dataSource;
        com.google.android.exoplayer2.util.Util.closeQuietly(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.chunk.DataChunk.load():void");
    }

    public DataChunk(DataSource dataSource, DataSpec dataSpec, int type, Format trackFormat, int trackSelectionReason, Object trackSelectionData, byte[] data) {
        super(dataSource, dataSpec, type, trackFormat, trackSelectionReason, trackSelectionData, C0555C.TIME_UNSET, C0555C.TIME_UNSET);
        this.data = data;
    }

    public byte[] getDataHolder() {
        return this.data;
    }

    public final void cancelLoad() {
        this.loadCanceled = true;
    }

    private void maybeExpandData(int limit) {
        byte[] bArr = this.data;
        if (bArr == null) {
            this.data = new byte[16384];
        } else if (bArr.length < limit + 16384) {
            this.data = Arrays.copyOf(bArr, bArr.length + 16384);
        }
    }
}
