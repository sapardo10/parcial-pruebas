package org.apache.commons.io.output;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ByteArrayOutputStream extends OutputStream {
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private final List<byte[]> buffers;
    private int count;
    private byte[] currentBuffer;
    private int currentBufferIndex;
    private int filledBufferSum;
    private boolean reuseBuffers;

    public synchronized byte[] toByteArray() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:22:0x0034 in {6, 14, 15, 16, 18, 21} preds:[]
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
        r7 = this;
        monitor-enter(r7);
        r0 = r7.count;	 Catch:{ all -> 0x0031 }
        if (r0 != 0) goto L_0x0009;	 Catch:{ all -> 0x0031 }
    L_0x0005:
        r1 = EMPTY_BYTE_ARRAY;	 Catch:{ all -> 0x0031 }
        monitor-exit(r7);
        return r1;
    L_0x0009:
        r1 = new byte[r0];	 Catch:{ all -> 0x0031 }
        r2 = 0;	 Catch:{ all -> 0x0031 }
        r3 = r7.buffers;	 Catch:{ all -> 0x0031 }
        r3 = r3.iterator();	 Catch:{ all -> 0x0031 }
    L_0x0012:
        r4 = r3.hasNext();	 Catch:{ all -> 0x0031 }
        if (r4 == 0) goto L_0x002e;	 Catch:{ all -> 0x0031 }
    L_0x0018:
        r4 = r3.next();	 Catch:{ all -> 0x0031 }
        r4 = (byte[]) r4;	 Catch:{ all -> 0x0031 }
        r5 = r4.length;	 Catch:{ all -> 0x0031 }
        r5 = java.lang.Math.min(r5, r0);	 Catch:{ all -> 0x0031 }
        r6 = 0;	 Catch:{ all -> 0x0031 }
        java.lang.System.arraycopy(r4, r6, r1, r2, r5);	 Catch:{ all -> 0x0031 }
        r2 = r2 + r5;
        r0 = r0 - r5;
        if (r0 != 0) goto L_0x002c;
    L_0x002b:
        goto L_0x002f;
        goto L_0x0012;
    L_0x002f:
        monitor-exit(r7);
        return r1;
    L_0x0031:
        r0 = move-exception;
        monitor-exit(r7);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.output.ByteArrayOutputStream.toByteArray():byte[]");
    }

    public synchronized java.io.InputStream toInputStream() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:22:0x004e in {6, 13, 14, 15, 18, 21} preds:[]
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
        r7 = this;
        monitor-enter(r7);
        r0 = r7.count;	 Catch:{ all -> 0x004b }
        if (r0 != 0) goto L_0x000c;	 Catch:{ all -> 0x004b }
    L_0x0005:
        r1 = new org.apache.commons.io.input.ClosedInputStream;	 Catch:{ all -> 0x004b }
        r1.<init>();	 Catch:{ all -> 0x004b }
        monitor-exit(r7);
        return r1;
    L_0x000c:
        r1 = new java.util.ArrayList;	 Catch:{ all -> 0x004b }
        r2 = r7.buffers;	 Catch:{ all -> 0x004b }
        r2 = r2.size();	 Catch:{ all -> 0x004b }
        r1.<init>(r2);	 Catch:{ all -> 0x004b }
        r2 = r7.buffers;	 Catch:{ all -> 0x004b }
        r2 = r2.iterator();	 Catch:{ all -> 0x004b }
    L_0x001d:
        r3 = r2.hasNext();	 Catch:{ all -> 0x004b }
        r4 = 0;	 Catch:{ all -> 0x004b }
        if (r3 == 0) goto L_0x003d;	 Catch:{ all -> 0x004b }
    L_0x0024:
        r3 = r2.next();	 Catch:{ all -> 0x004b }
        r3 = (byte[]) r3;	 Catch:{ all -> 0x004b }
        r5 = r3.length;	 Catch:{ all -> 0x004b }
        r5 = java.lang.Math.min(r5, r0);	 Catch:{ all -> 0x004b }
        r6 = new java.io.ByteArrayInputStream;	 Catch:{ all -> 0x004b }
        r6.<init>(r3, r4, r5);	 Catch:{ all -> 0x004b }
        r1.add(r6);	 Catch:{ all -> 0x004b }
        r0 = r0 - r5;	 Catch:{ all -> 0x004b }
        if (r0 != 0) goto L_0x003b;	 Catch:{ all -> 0x004b }
    L_0x003a:
        goto L_0x003e;	 Catch:{ all -> 0x004b }
        goto L_0x001d;	 Catch:{ all -> 0x004b }
    L_0x003e:
        r7.reuseBuffers = r4;	 Catch:{ all -> 0x004b }
        r2 = new java.io.SequenceInputStream;	 Catch:{ all -> 0x004b }
        r3 = java.util.Collections.enumeration(r1);	 Catch:{ all -> 0x004b }
        r2.<init>(r3);	 Catch:{ all -> 0x004b }
        monitor-exit(r7);
        return r2;
    L_0x004b:
        r0 = move-exception;
        monitor-exit(r7);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.output.ByteArrayOutputStream.toInputStream():java.io.InputStream");
    }

    public synchronized int write(java.io.InputStream r6) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x003a in {8, 9, 11, 13, 16} preds:[]
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
        r0 = 0;
        r1 = r5.count;	 Catch:{ all -> 0x0037 }
        r2 = r5.filledBufferSum;	 Catch:{ all -> 0x0037 }
        r1 = r1 - r2;	 Catch:{ all -> 0x0037 }
        r2 = r5.currentBuffer;	 Catch:{ all -> 0x0037 }
        r3 = r5.currentBuffer;	 Catch:{ all -> 0x0037 }
        r3 = r3.length;	 Catch:{ all -> 0x0037 }
        r3 = r3 - r1;	 Catch:{ all -> 0x0037 }
        r2 = r6.read(r2, r1, r3);	 Catch:{ all -> 0x0037 }
    L_0x0011:
        r3 = -1;	 Catch:{ all -> 0x0037 }
        if (r2 == r3) goto L_0x0035;	 Catch:{ all -> 0x0037 }
    L_0x0014:
        r0 = r0 + r2;	 Catch:{ all -> 0x0037 }
        r1 = r1 + r2;	 Catch:{ all -> 0x0037 }
        r3 = r5.count;	 Catch:{ all -> 0x0037 }
        r3 = r3 + r2;	 Catch:{ all -> 0x0037 }
        r5.count = r3;	 Catch:{ all -> 0x0037 }
        r3 = r5.currentBuffer;	 Catch:{ all -> 0x0037 }
        r3 = r3.length;	 Catch:{ all -> 0x0037 }
        if (r1 != r3) goto L_0x0028;	 Catch:{ all -> 0x0037 }
    L_0x0020:
        r3 = r5.currentBuffer;	 Catch:{ all -> 0x0037 }
        r3 = r3.length;	 Catch:{ all -> 0x0037 }
        r5.needNewBuffer(r3);	 Catch:{ all -> 0x0037 }
        r1 = 0;	 Catch:{ all -> 0x0037 }
        goto L_0x0029;	 Catch:{ all -> 0x0037 }
    L_0x0029:
        r3 = r5.currentBuffer;	 Catch:{ all -> 0x0037 }
        r4 = r5.currentBuffer;	 Catch:{ all -> 0x0037 }
        r4 = r4.length;	 Catch:{ all -> 0x0037 }
        r4 = r4 - r1;	 Catch:{ all -> 0x0037 }
        r3 = r6.read(r3, r1, r4);	 Catch:{ all -> 0x0037 }
        r2 = r3;
        goto L_0x0011;
    L_0x0035:
        monitor-exit(r5);
        return r0;
    L_0x0037:
        r6 = move-exception;
        monitor-exit(r5);
        throw r6;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.output.ByteArrayOutputStream.write(java.io.InputStream):int");
    }

    public void write(byte[] r7, int r8, int r9) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:27:0x0047 in {9, 16, 17, 18, 21, 24, 26} preds:[]
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
        if (r8 < 0) goto L_0x0040;
    L_0x0002:
        r0 = r7.length;
        if (r8 > r0) goto L_0x0040;
    L_0x0005:
        if (r9 < 0) goto L_0x0040;
    L_0x0007:
        r0 = r8 + r9;
        r1 = r7.length;
        if (r0 > r1) goto L_0x0040;
    L_0x000c:
        r0 = r8 + r9;
        if (r0 < 0) goto L_0x0040;
    L_0x0010:
        if (r9 != 0) goto L_0x0013;
    L_0x0012:
        return;
    L_0x0013:
        monitor-enter(r6);
        r0 = r6.count;	 Catch:{ all -> 0x003d }
        r0 = r0 + r9;	 Catch:{ all -> 0x003d }
        r1 = r9;	 Catch:{ all -> 0x003d }
        r2 = r6.count;	 Catch:{ all -> 0x003d }
        r3 = r6.filledBufferSum;	 Catch:{ all -> 0x003d }
        r2 = r2 - r3;	 Catch:{ all -> 0x003d }
    L_0x001d:
        if (r1 <= 0) goto L_0x0039;	 Catch:{ all -> 0x003d }
    L_0x001f:
        r3 = r6.currentBuffer;	 Catch:{ all -> 0x003d }
        r3 = r3.length;	 Catch:{ all -> 0x003d }
        r3 = r3 - r2;	 Catch:{ all -> 0x003d }
        r3 = java.lang.Math.min(r1, r3);	 Catch:{ all -> 0x003d }
        r4 = r8 + r9;	 Catch:{ all -> 0x003d }
        r4 = r4 - r1;	 Catch:{ all -> 0x003d }
        r5 = r6.currentBuffer;	 Catch:{ all -> 0x003d }
        java.lang.System.arraycopy(r7, r4, r5, r2, r3);	 Catch:{ all -> 0x003d }
        r1 = r1 - r3;	 Catch:{ all -> 0x003d }
        if (r1 <= 0) goto L_0x0037;	 Catch:{ all -> 0x003d }
    L_0x0032:
        r6.needNewBuffer(r0);	 Catch:{ all -> 0x003d }
        r2 = 0;	 Catch:{ all -> 0x003d }
        goto L_0x0038;	 Catch:{ all -> 0x003d }
    L_0x0038:
        goto L_0x001d;	 Catch:{ all -> 0x003d }
    L_0x0039:
        r6.count = r0;	 Catch:{ all -> 0x003d }
        monitor-exit(r6);	 Catch:{ all -> 0x003d }
        return;	 Catch:{ all -> 0x003d }
    L_0x003d:
        r0 = move-exception;	 Catch:{ all -> 0x003d }
        monitor-exit(r6);	 Catch:{ all -> 0x003d }
        throw r0;
        r0 = new java.lang.IndexOutOfBoundsException;
        r0.<init>();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.output.ByteArrayOutputStream.write(byte[], int, int):void");
    }

    public synchronized void writeTo(java.io.OutputStream r6) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:0x002a in {8, 9, 10, 12, 15} preds:[]
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
        r0 = r5.count;	 Catch:{ all -> 0x0027 }
        r1 = r5.buffers;	 Catch:{ all -> 0x0027 }
        r1 = r1.iterator();	 Catch:{ all -> 0x0027 }
    L_0x0009:
        r2 = r1.hasNext();	 Catch:{ all -> 0x0027 }
        if (r2 == 0) goto L_0x0024;	 Catch:{ all -> 0x0027 }
    L_0x000f:
        r2 = r1.next();	 Catch:{ all -> 0x0027 }
        r2 = (byte[]) r2;	 Catch:{ all -> 0x0027 }
        r3 = r2.length;	 Catch:{ all -> 0x0027 }
        r3 = java.lang.Math.min(r3, r0);	 Catch:{ all -> 0x0027 }
        r4 = 0;	 Catch:{ all -> 0x0027 }
        r6.write(r2, r4, r3);	 Catch:{ all -> 0x0027 }
        r0 = r0 - r3;
        if (r0 != 0) goto L_0x0022;
    L_0x0021:
        goto L_0x0025;
        goto L_0x0009;
    L_0x0025:
        monitor-exit(r5);
        return;
    L_0x0027:
        r6 = move-exception;
        monitor-exit(r5);
        throw r6;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.io.output.ByteArrayOutputStream.writeTo(java.io.OutputStream):void");
    }

    public ByteArrayOutputStream() {
        this(1024);
    }

    public ByteArrayOutputStream(int size) {
        this.buffers = new ArrayList();
        this.reuseBuffers = true;
        if (size >= 0) {
            synchronized (this) {
                needNewBuffer(size);
            }
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Negative initial size: ");
        stringBuilder.append(size);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    private void needNewBuffer(int newcount) {
        if (this.currentBufferIndex < this.buffers.size() - 1) {
            this.filledBufferSum += this.currentBuffer.length;
            this.currentBufferIndex++;
            this.currentBuffer = (byte[]) this.buffers.get(this.currentBufferIndex);
            return;
        }
        int newBufferSize = this.currentBuffer;
        if (newBufferSize == 0) {
            newBufferSize = newcount;
            this.filledBufferSum = 0;
        } else {
            newBufferSize = Math.max(newBufferSize.length << 1, newcount - this.filledBufferSum);
            this.filledBufferSum += this.currentBuffer.length;
        }
        this.currentBufferIndex++;
        this.currentBuffer = new byte[newBufferSize];
        this.buffers.add(this.currentBuffer);
    }

    public synchronized void write(int b) {
        int inBufferPos = this.count - this.filledBufferSum;
        if (inBufferPos == this.currentBuffer.length) {
            needNewBuffer(this.count + 1);
            inBufferPos = 0;
        }
        this.currentBuffer[inBufferPos] = (byte) b;
        this.count++;
    }

    public synchronized int size() {
        return this.count;
    }

    public void close() throws IOException {
    }

    public synchronized void reset() {
        this.count = 0;
        this.filledBufferSum = 0;
        this.currentBufferIndex = 0;
        if (this.reuseBuffers) {
            this.currentBuffer = (byte[]) this.buffers.get(this.currentBufferIndex);
        } else {
            this.currentBuffer = null;
            int size = ((byte[]) this.buffers.get(0)).length;
            this.buffers.clear();
            needNewBuffer(size);
            this.reuseBuffers = true;
        }
    }

    public static InputStream toBufferedInputStream(InputStream input) throws IOException {
        return toBufferedInputStream(input, 1024);
    }

    public static InputStream toBufferedInputStream(InputStream input, int size) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream(size);
        output.write(input);
        return output.toInputStream();
    }

    @Deprecated
    public String toString() {
        return new String(toByteArray(), Charset.defaultCharset());
    }

    public String toString(String enc) throws UnsupportedEncodingException {
        return new String(toByteArray(), enc);
    }

    public String toString(Charset charset) {
        return new String(toByteArray(), charset);
    }
}
