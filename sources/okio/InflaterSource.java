package okio;

import java.io.IOException;
import java.util.zip.Inflater;

public final class InflaterSource implements Source {
    private int bufferBytesHeldByInflater;
    private boolean closed;
    private final Inflater inflater;
    private final BufferedSource source;

    public long read(okio.Buffer r9, long r10) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:36:0x0096 in {6, 12, 17, 19, 21, 25, 26, 28, 31, 33, 35} preds:[]
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
        r0 = 0;
        r2 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1));
        if (r2 < 0) goto L_0x007f;
    L_0x0006:
        r2 = r8.closed;
        if (r2 != 0) goto L_0x0077;
    L_0x000a:
        r2 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1));
        if (r2 != 0) goto L_0x000f;
    L_0x000e:
        return r0;
    L_0x000f:
        r0 = r8.refill();
        r1 = 1;
        r1 = r9.writableSegment(r1);	 Catch:{ DataFormatException -> 0x0070 }
        r2 = r1.limit;	 Catch:{ DataFormatException -> 0x0070 }
        r2 = 8192 - r2;	 Catch:{ DataFormatException -> 0x0070 }
        r2 = (long) r2;	 Catch:{ DataFormatException -> 0x0070 }
        r2 = java.lang.Math.min(r10, r2);	 Catch:{ DataFormatException -> 0x0070 }
        r2 = (int) r2;	 Catch:{ DataFormatException -> 0x0070 }
        r3 = r8.inflater;	 Catch:{ DataFormatException -> 0x0070 }
        r4 = r1.data;	 Catch:{ DataFormatException -> 0x0070 }
        r5 = r1.limit;	 Catch:{ DataFormatException -> 0x0070 }
        r3 = r3.inflate(r4, r5, r2);	 Catch:{ DataFormatException -> 0x0070 }
        if (r3 <= 0) goto L_0x003b;	 Catch:{ DataFormatException -> 0x0070 }
    L_0x002e:
        r4 = r1.limit;	 Catch:{ DataFormatException -> 0x0070 }
        r4 = r4 + r3;	 Catch:{ DataFormatException -> 0x0070 }
        r1.limit = r4;	 Catch:{ DataFormatException -> 0x0070 }
        r4 = r9.size;	 Catch:{ DataFormatException -> 0x0070 }
        r6 = (long) r3;	 Catch:{ DataFormatException -> 0x0070 }
        r4 = r4 + r6;	 Catch:{ DataFormatException -> 0x0070 }
        r9.size = r4;	 Catch:{ DataFormatException -> 0x0070 }
        r4 = (long) r3;	 Catch:{ DataFormatException -> 0x0070 }
        return r4;	 Catch:{ DataFormatException -> 0x0070 }
    L_0x003b:
        r4 = r8.inflater;	 Catch:{ DataFormatException -> 0x0070 }
        r4 = r4.finished();	 Catch:{ DataFormatException -> 0x0070 }
        if (r4 != 0) goto L_0x0058;	 Catch:{ DataFormatException -> 0x0070 }
    L_0x0043:
        r4 = r8.inflater;	 Catch:{ DataFormatException -> 0x0070 }
        r4 = r4.needsDictionary();	 Catch:{ DataFormatException -> 0x0070 }
        if (r4 == 0) goto L_0x004c;	 Catch:{ DataFormatException -> 0x0070 }
    L_0x004b:
        goto L_0x0058;	 Catch:{ DataFormatException -> 0x0070 }
    L_0x004c:
        if (r0 != 0) goto L_0x0050;	 Catch:{ DataFormatException -> 0x0070 }
        goto L_0x000f;	 Catch:{ DataFormatException -> 0x0070 }
    L_0x0050:
        r4 = new java.io.EOFException;	 Catch:{ DataFormatException -> 0x0070 }
        r5 = "source exhausted prematurely";	 Catch:{ DataFormatException -> 0x0070 }
        r4.<init>(r5);	 Catch:{ DataFormatException -> 0x0070 }
        throw r4;	 Catch:{ DataFormatException -> 0x0070 }
        r8.releaseInflatedBytes();	 Catch:{ DataFormatException -> 0x0070 }
        r4 = r1.pos;	 Catch:{ DataFormatException -> 0x0070 }
        r5 = r1.limit;	 Catch:{ DataFormatException -> 0x0070 }
        if (r4 != r5) goto L_0x006c;	 Catch:{ DataFormatException -> 0x0070 }
    L_0x0062:
        r4 = r1.pop();	 Catch:{ DataFormatException -> 0x0070 }
        r9.head = r4;	 Catch:{ DataFormatException -> 0x0070 }
        okio.SegmentPool.recycle(r1);	 Catch:{ DataFormatException -> 0x0070 }
        goto L_0x006d;
    L_0x006d:
        r4 = -1;
        return r4;
    L_0x0070:
        r1 = move-exception;
        r2 = new java.io.IOException;
        r2.<init>(r1);
        throw r2;
    L_0x0077:
        r0 = new java.lang.IllegalStateException;
        r1 = "closed";
        r0.<init>(r1);
        throw r0;
    L_0x007f:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "byteCount < 0: ";
        r1.append(r2);
        r1.append(r10);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.InflaterSource.read(okio.Buffer, long):long");
    }

    public InflaterSource(Source source, Inflater inflater) {
        this(Okio.buffer(source), inflater);
    }

    InflaterSource(BufferedSource source, Inflater inflater) {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        } else if (inflater != null) {
            this.source = source;
            this.inflater = inflater;
        } else {
            throw new IllegalArgumentException("inflater == null");
        }
    }

    public final boolean refill() throws IOException {
        if (!this.inflater.needsInput()) {
            return false;
        }
        releaseInflatedBytes();
        if (this.inflater.getRemaining() != 0) {
            throw new IllegalStateException("?");
        } else if (this.source.exhausted()) {
            return true;
        } else {
            Segment head = this.source.buffer().head;
            this.bufferBytesHeldByInflater = head.limit - head.pos;
            this.inflater.setInput(head.data, head.pos, this.bufferBytesHeldByInflater);
            return false;
        }
    }

    private void releaseInflatedBytes() throws IOException {
        int toRelease = this.bufferBytesHeldByInflater;
        if (toRelease != 0) {
            toRelease -= this.inflater.getRemaining();
            this.bufferBytesHeldByInflater -= toRelease;
            this.source.skip((long) toRelease);
        }
    }

    public Timeout timeout() {
        return this.source.timeout();
    }

    public void close() throws IOException {
        if (!this.closed) {
            this.inflater.end();
            this.closed = true;
            this.source.close();
        }
    }
}
