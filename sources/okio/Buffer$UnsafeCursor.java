package okio;

import java.io.Closeable;

public final class Buffer$UnsafeCursor implements Closeable {
    public Buffer buffer;
    public byte[] data;
    public int end = -1;
    public long offset = -1;
    public boolean readWrite;
    private Segment segment;
    public int start = -1;

    public final long resizeBuffer(long r12) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:37:0x00b6 in {13, 14, 15, 16, 18, 26, 27, 28, 29, 30, 32, 34, 36} preds:[]
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
        r11 = this;
        r0 = r11.buffer;
        if (r0 == 0) goto L_0x00ae;
    L_0x0004:
        r1 = r11.readWrite;
        if (r1 == 0) goto L_0x00a6;
    L_0x0008:
        r0 = r0.size;
        r2 = 0;
        r4 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1));
        if (r4 > 0) goto L_0x0065;
    L_0x0010:
        r4 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1));
        if (r4 < 0) goto L_0x004e;
    L_0x0014:
        r4 = r0 - r12;
    L_0x0016:
        r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r6 <= 0) goto L_0x0040;
    L_0x001a:
        r6 = r11.buffer;
        r6 = r6.head;
        r6 = r6.prev;
        r7 = r6.limit;
        r8 = r6.pos;
        r7 = r7 - r8;
        r8 = (long) r7;
        r10 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1));
        if (r10 > 0) goto L_0x0038;
    L_0x002a:
        r8 = r11.buffer;
        r9 = r6.pop();
        r8.head = r9;
        okio.SegmentPool.recycle(r6);
        r8 = (long) r7;
        r4 = r4 - r8;
        goto L_0x0016;
    L_0x0038:
        r2 = r6.limit;
        r2 = (long) r2;
        r2 = r2 - r4;
        r2 = (int) r2;
        r6.limit = r2;
        goto L_0x0041;
    L_0x0041:
        r2 = 0;
        r11.segment = r2;
        r11.offset = r12;
        r11.data = r2;
        r2 = -1;
        r11.start = r2;
        r11.end = r2;
        goto L_0x00a1;
    L_0x004e:
        r2 = new java.lang.IllegalArgumentException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "newSize < 0: ";
        r3.append(r4);
        r3.append(r12);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
    L_0x0065:
        r4 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1));
        if (r4 <= 0) goto L_0x00a0;
    L_0x0069:
        r4 = 1;
        r5 = r12 - r0;
    L_0x006c:
        r7 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1));
        if (r7 <= 0) goto L_0x009f;
    L_0x0070:
        r7 = r11.buffer;
        r8 = 1;
        r7 = r7.writableSegment(r8);
        r8 = r7.limit;
        r8 = 8192 - r8;
        r8 = (long) r8;
        r8 = java.lang.Math.min(r5, r8);
        r8 = (int) r8;
        r9 = r7.limit;
        r9 = r9 + r8;
        r7.limit = r9;
        r9 = (long) r8;
        r5 = r5 - r9;
        if (r4 == 0) goto L_0x009d;
    L_0x008a:
        r11.segment = r7;
        r11.offset = r0;
        r9 = r7.data;
        r11.data = r9;
        r9 = r7.limit;
        r9 = r9 - r8;
        r11.start = r9;
        r9 = r7.limit;
        r11.end = r9;
        r4 = 0;
        goto L_0x009e;
    L_0x009e:
        goto L_0x006c;
    L_0x009f:
        goto L_0x00a1;
    L_0x00a1:
        r2 = r11.buffer;
        r2.size = r12;
        return r0;
    L_0x00a6:
        r0 = new java.lang.IllegalStateException;
        r1 = "resizeBuffer() only permitted for read/write buffers";
        r0.<init>(r1);
        throw r0;
    L_0x00ae:
        r0 = new java.lang.IllegalStateException;
        r1 = "not attached to a buffer";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer$UnsafeCursor.resizeBuffer(long):long");
    }

    public final int seek(long r13) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:44:0x00e2 in {8, 13, 14, 15, 21, 22, 26, 27, 34, 35, 36, 37, 39, 41, 43} preds:[]
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
        r12 = this;
        r0 = -1;
        r2 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1));
        if (r2 < 0) goto L_0x00c0;
    L_0x0006:
        r2 = r12.buffer;
        r2 = r2.size;
        r4 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1));
        if (r4 > 0) goto L_0x00c0;
    L_0x000e:
        r2 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1));
        if (r2 == 0) goto L_0x00b2;
    L_0x0012:
        r0 = r12.buffer;
        r0 = r0.size;
        r2 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1));
        if (r2 != 0) goto L_0x001c;
    L_0x001a:
        goto L_0x00b2;
    L_0x001c:
        r0 = 0;
        r2 = r12.buffer;
        r2 = r2.size;
        r4 = r12.buffer;
        r4 = r4.head;
        r5 = r12.buffer;
        r5 = r5.head;
        r6 = r12.segment;
        if (r6 == 0) goto L_0x0043;
    L_0x002e:
        r7 = r12.offset;
        r9 = r12.start;
        r6 = r6.pos;
        r9 = r9 - r6;
        r9 = (long) r9;
        r7 = r7 - r9;
        r6 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1));
        if (r6 <= 0) goto L_0x003f;
    L_0x003b:
        r2 = r7;
        r5 = r12.segment;
        goto L_0x0044;
    L_0x003f:
        r0 = r7;
        r4 = r12.segment;
        goto L_0x0044;
    L_0x0044:
        r6 = r2 - r13;
        r8 = r13 - r0;
        r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r10 <= 0) goto L_0x0064;
    L_0x004c:
        r6 = r4;
        r7 = r0;
    L_0x004e:
        r9 = r6.limit;
        r10 = r6.pos;
        r9 = r9 - r10;
        r9 = (long) r9;
        r9 = r9 + r7;
        r11 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1));
        if (r11 < 0) goto L_0x0063;
    L_0x0059:
        r9 = r6.limit;
        r10 = r6.pos;
        r9 = r9 - r10;
        r9 = (long) r9;
        r7 = r7 + r9;
        r6 = r6.next;
        goto L_0x004e;
    L_0x0063:
        goto L_0x0075;
    L_0x0064:
        r6 = r5;
        r7 = r2;
    L_0x0066:
        r9 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1));
        if (r9 <= 0) goto L_0x0074;
    L_0x006a:
        r6 = r6.prev;
        r9 = r6.limit;
        r10 = r6.pos;
        r9 = r9 - r10;
        r9 = (long) r9;
        r7 = r7 - r9;
        goto L_0x0066;
    L_0x0075:
        r9 = r12.readWrite;
        if (r9 == 0) goto L_0x0097;
    L_0x0079:
        r9 = r6.shared;
        if (r9 == 0) goto L_0x0097;
    L_0x007d:
        r9 = r6.unsharedCopy();
        r10 = r12.buffer;
        r10 = r10.head;
        if (r10 != r6) goto L_0x008c;
    L_0x0087:
        r10 = r12.buffer;
        r10.head = r9;
        goto L_0x008d;
    L_0x008d:
        r6 = r6.push(r9);
        r10 = r6.prev;
        r10.pop();
        goto L_0x0098;
    L_0x0098:
        r12.segment = r6;
        r12.offset = r13;
        r9 = r6.data;
        r12.data = r9;
        r9 = r6.pos;
        r10 = r13 - r7;
        r10 = (int) r10;
        r9 = r9 + r10;
        r12.start = r9;
        r9 = r6.limit;
        r12.end = r9;
        r9 = r12.end;
        r10 = r12.start;
        r9 = r9 - r10;
        return r9;
        r0 = 0;
        r12.segment = r0;
        r12.offset = r13;
        r12.data = r0;
        r0 = -1;
        r12.start = r0;
        r12.end = r0;
        return r0;
        r0 = new java.lang.ArrayIndexOutOfBoundsException;
        r1 = 2;
        r1 = new java.lang.Object[r1];
        r2 = 0;
        r3 = java.lang.Long.valueOf(r13);
        r1[r2] = r3;
        r2 = 1;
        r3 = r12.buffer;
        r3 = r3.size;
        r3 = java.lang.Long.valueOf(r3);
        r1[r2] = r3;
        r2 = "offset=%s > size=%s";
        r1 = java.lang.String.format(r2, r1);
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.Buffer$UnsafeCursor.seek(long):int");
    }

    public final int next() {
        if (this.offset != this.buffer.size) {
            long j = this.offset;
            if (j == -1) {
                return seek(0);
            }
            return seek(j + ((long) (this.end - this.start)));
        }
        throw new IllegalStateException();
    }

    public final long expandBuffer(int minByteCount) {
        StringBuilder stringBuilder;
        if (minByteCount <= 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("minByteCount <= 0: ");
            stringBuilder.append(minByteCount);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (minByteCount <= 8192) {
            long oldSize = this.buffer;
            if (oldSize == null) {
                throw new IllegalStateException("not attached to a buffer");
            } else if (this.readWrite) {
                oldSize = oldSize.size;
                Segment tail = this.buffer.writableSegment(minByteCount);
                int result = 8192 - tail.limit;
                tail.limit = 8192;
                this.buffer.size = ((long) result) + oldSize;
                this.segment = tail;
                this.offset = oldSize;
                this.data = tail.data;
                this.start = 8192 - result;
                this.end = 8192;
                return (long) result;
            } else {
                throw new IllegalStateException("expandBuffer() only permitted for read/write buffers");
            }
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("minByteCount > Segment.SIZE: ");
            stringBuilder.append(minByteCount);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public void close() {
        if (this.buffer != null) {
            this.buffer = null;
            this.segment = null;
            this.offset = -1;
            this.data = null;
            this.start = -1;
            this.end = -1;
            return;
        }
        throw new IllegalStateException("not attached to a buffer");
    }
}
