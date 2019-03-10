package com.bumptech.glide.disklrucache;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

class StrictLineReader implements Closeable {
    private static final byte CR = (byte) 13;
    private static final byte LF = (byte) 10;
    private byte[] buf;
    private final Charset charset;
    private int end;
    private final InputStream in;
    private int pos;

    /* renamed from: com.bumptech.glide.disklrucache.StrictLineReader$1 */
    class C05241 extends ByteArrayOutputStream {
        C05241(int x0) {
            super(x0);
        }

        public String toString() {
            int length = (this.count <= 0 || this.buf[this.count - 1] != StrictLineReader.CR) ? this.count : this.count - 1;
            try {
                return new String(this.buf, 0, length, StrictLineReader.this.charset.name());
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError(e);
            }
        }
    }

    public java.lang.String readLine() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:44:0x00a2 in {7, 8, 18, 19, 22, 23, 32, 33, 36, 37, 38, 40, 43} preds:[]
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
        r0 = r8.in;
        monitor-enter(r0);
        r1 = r8.buf;	 Catch:{ all -> 0x009f }
        if (r1 == 0) goto L_0x0097;	 Catch:{ all -> 0x009f }
    L_0x0007:
        r1 = r8.pos;	 Catch:{ all -> 0x009f }
        r2 = r8.end;	 Catch:{ all -> 0x009f }
        if (r1 < r2) goto L_0x0011;	 Catch:{ all -> 0x009f }
    L_0x000d:
        r8.fillBuf();	 Catch:{ all -> 0x009f }
        goto L_0x0012;	 Catch:{ all -> 0x009f }
    L_0x0012:
        r1 = r8.pos;	 Catch:{ all -> 0x009f }
    L_0x0014:
        r2 = r8.end;	 Catch:{ all -> 0x009f }
        r3 = 10;	 Catch:{ all -> 0x009f }
        if (r1 == r2) goto L_0x004e;	 Catch:{ all -> 0x009f }
    L_0x001a:
        r2 = r8.buf;	 Catch:{ all -> 0x009f }
        r2 = r2[r1];	 Catch:{ all -> 0x009f }
        if (r2 != r3) goto L_0x004b;	 Catch:{ all -> 0x009f }
    L_0x0020:
        r2 = r8.pos;	 Catch:{ all -> 0x009f }
        if (r1 == r2) goto L_0x0031;	 Catch:{ all -> 0x009f }
    L_0x0024:
        r2 = r8.buf;	 Catch:{ all -> 0x009f }
        r3 = r1 + -1;	 Catch:{ all -> 0x009f }
        r2 = r2[r3];	 Catch:{ all -> 0x009f }
        r3 = 13;	 Catch:{ all -> 0x009f }
        if (r2 != r3) goto L_0x0031;	 Catch:{ all -> 0x009f }
    L_0x002e:
        r2 = r1 + -1;	 Catch:{ all -> 0x009f }
        goto L_0x0032;	 Catch:{ all -> 0x009f }
    L_0x0031:
        r2 = r1;	 Catch:{ all -> 0x009f }
    L_0x0032:
        r3 = new java.lang.String;	 Catch:{ all -> 0x009f }
        r4 = r8.buf;	 Catch:{ all -> 0x009f }
        r5 = r8.pos;	 Catch:{ all -> 0x009f }
        r6 = r8.pos;	 Catch:{ all -> 0x009f }
        r6 = r2 - r6;	 Catch:{ all -> 0x009f }
        r7 = r8.charset;	 Catch:{ all -> 0x009f }
        r7 = r7.name();	 Catch:{ all -> 0x009f }
        r3.<init>(r4, r5, r6, r7);	 Catch:{ all -> 0x009f }
        r4 = r1 + 1;	 Catch:{ all -> 0x009f }
        r8.pos = r4;	 Catch:{ all -> 0x009f }
        monitor-exit(r0);	 Catch:{ all -> 0x009f }
        return r3;	 Catch:{ all -> 0x009f }
    L_0x004b:
        r1 = r1 + 1;	 Catch:{ all -> 0x009f }
        goto L_0x0014;	 Catch:{ all -> 0x009f }
    L_0x004e:
        r1 = new com.bumptech.glide.disklrucache.StrictLineReader$1;	 Catch:{ all -> 0x009f }
        r2 = r8.end;	 Catch:{ all -> 0x009f }
        r4 = r8.pos;	 Catch:{ all -> 0x009f }
        r2 = r2 - r4;	 Catch:{ all -> 0x009f }
        r2 = r2 + 80;	 Catch:{ all -> 0x009f }
        r1.<init>(r2);	 Catch:{ all -> 0x009f }
    L_0x005a:
        r2 = r8.buf;	 Catch:{ all -> 0x009f }
        r4 = r8.pos;	 Catch:{ all -> 0x009f }
        r5 = r8.end;	 Catch:{ all -> 0x009f }
        r6 = r8.pos;	 Catch:{ all -> 0x009f }
        r5 = r5 - r6;	 Catch:{ all -> 0x009f }
        r1.write(r2, r4, r5);	 Catch:{ all -> 0x009f }
        r2 = -1;	 Catch:{ all -> 0x009f }
        r8.end = r2;	 Catch:{ all -> 0x009f }
        r8.fillBuf();	 Catch:{ all -> 0x009f }
        r2 = r8.pos;	 Catch:{ all -> 0x009f }
    L_0x006e:
        r4 = r8.end;	 Catch:{ all -> 0x009f }
        if (r2 == r4) goto L_0x0096;	 Catch:{ all -> 0x009f }
    L_0x0072:
        r4 = r8.buf;	 Catch:{ all -> 0x009f }
        r4 = r4[r2];	 Catch:{ all -> 0x009f }
        if (r4 != r3) goto L_0x0093;	 Catch:{ all -> 0x009f }
    L_0x0078:
        r3 = r8.pos;	 Catch:{ all -> 0x009f }
        if (r2 == r3) goto L_0x0088;	 Catch:{ all -> 0x009f }
    L_0x007c:
        r3 = r8.buf;	 Catch:{ all -> 0x009f }
        r4 = r8.pos;	 Catch:{ all -> 0x009f }
        r5 = r8.pos;	 Catch:{ all -> 0x009f }
        r5 = r2 - r5;	 Catch:{ all -> 0x009f }
        r1.write(r3, r4, r5);	 Catch:{ all -> 0x009f }
        goto L_0x0089;	 Catch:{ all -> 0x009f }
    L_0x0089:
        r3 = r2 + 1;	 Catch:{ all -> 0x009f }
        r8.pos = r3;	 Catch:{ all -> 0x009f }
        r3 = r1.toString();	 Catch:{ all -> 0x009f }
        monitor-exit(r0);	 Catch:{ all -> 0x009f }
        return r3;	 Catch:{ all -> 0x009f }
    L_0x0093:
        r2 = r2 + 1;	 Catch:{ all -> 0x009f }
        goto L_0x006e;	 Catch:{ all -> 0x009f }
    L_0x0096:
        goto L_0x005a;	 Catch:{ all -> 0x009f }
    L_0x0097:
        r1 = new java.io.IOException;	 Catch:{ all -> 0x009f }
        r2 = "LineReader is closed";	 Catch:{ all -> 0x009f }
        r1.<init>(r2);	 Catch:{ all -> 0x009f }
        throw r1;	 Catch:{ all -> 0x009f }
    L_0x009f:
        r1 = move-exception;	 Catch:{ all -> 0x009f }
        monitor-exit(r0);	 Catch:{ all -> 0x009f }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.disklrucache.StrictLineReader.readLine():java.lang.String");
    }

    public StrictLineReader(InputStream in, Charset charset) {
        this(in, 8192, charset);
    }

    public StrictLineReader(InputStream in, int capacity, Charset charset) {
        if (in == null || charset == null) {
            throw new NullPointerException();
        } else if (capacity < 0) {
            throw new IllegalArgumentException("capacity <= 0");
        } else if (charset.equals(Util.US_ASCII)) {
            this.in = in;
            this.charset = charset;
            this.buf = new byte[capacity];
        } else {
            throw new IllegalArgumentException("Unsupported encoding");
        }
    }

    public void close() throws IOException {
        synchronized (this.in) {
            if (this.buf != null) {
                this.buf = null;
                this.in.close();
            }
        }
    }

    public boolean hasUnterminatedLine() {
        return this.end == -1;
    }

    private void fillBuf() throws IOException {
        int result = this.in;
        byte[] bArr = this.buf;
        result = result.read(bArr, 0, bArr.length);
        if (result != -1) {
            this.pos = 0;
            this.end = result;
            return;
        }
        throw new EOFException();
    }
}
