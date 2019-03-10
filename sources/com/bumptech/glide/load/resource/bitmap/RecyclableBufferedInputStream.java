package com.bumptech.glide.load.resource.bitmap;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RecyclableBufferedInputStream extends FilterInputStream {
    private volatile byte[] buf;
    private final ArrayPool byteArrayPool;
    private int count;
    private int marklimit;
    private int markpos;
    private int pos;

    static class InvalidMarkException extends IOException {
        private static final long serialVersionUID = -4338378848813561757L;

        InvalidMarkException(String detailMessage) {
            super(detailMessage);
        }
    }

    public synchronized int read(@android.support.annotation.NonNull byte[] r6, int r7, int r8) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:95:0x00dc in {7, 15, 16, 21, 22, 25, 26, 38, 39, 42, 43, 51, 52, 55, 62, 64, 65, 68, 69, 71, 76, 84, 85, 89, 91, 94} preds:[]
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
        r5 = this;
        monitor-enter(r5);
        r0 = r5.buf;	 Catch:{ all -> 0x00d9 }
        if (r0 == 0) goto L_0x00d2;
    L_0x0005:
        if (r8 != 0) goto L_0x000a;
    L_0x0007:
        r1 = 0;
        monitor-exit(r5);
        return r1;
    L_0x000a:
        r1 = r5.in;	 Catch:{ all -> 0x00d9 }
        if (r1 == 0) goto L_0x00cb;	 Catch:{ all -> 0x00d9 }
    L_0x000e:
        r2 = r5.pos;	 Catch:{ all -> 0x00d9 }
        r3 = r5.count;	 Catch:{ all -> 0x00d9 }
        if (r2 >= r3) goto L_0x003c;	 Catch:{ all -> 0x00d9 }
    L_0x0014:
        r2 = r5.count;	 Catch:{ all -> 0x00d9 }
        r3 = r5.pos;	 Catch:{ all -> 0x00d9 }
        r2 = r2 - r3;	 Catch:{ all -> 0x00d9 }
        if (r2 < r8) goto L_0x001d;	 Catch:{ all -> 0x00d9 }
    L_0x001b:
        r2 = r8;	 Catch:{ all -> 0x00d9 }
        goto L_0x0022;	 Catch:{ all -> 0x00d9 }
    L_0x001d:
        r2 = r5.count;	 Catch:{ all -> 0x00d9 }
        r3 = r5.pos;	 Catch:{ all -> 0x00d9 }
        r2 = r2 - r3;	 Catch:{ all -> 0x00d9 }
    L_0x0022:
        r3 = r5.pos;	 Catch:{ all -> 0x00d9 }
        java.lang.System.arraycopy(r0, r3, r6, r7, r2);	 Catch:{ all -> 0x00d9 }
        r3 = r5.pos;	 Catch:{ all -> 0x00d9 }
        r3 = r3 + r2;	 Catch:{ all -> 0x00d9 }
        r5.pos = r3;	 Catch:{ all -> 0x00d9 }
        if (r2 == r8) goto L_0x0039;	 Catch:{ all -> 0x00d9 }
    L_0x002e:
        r3 = r1.available();	 Catch:{ all -> 0x00d9 }
        if (r3 != 0) goto L_0x0035;
    L_0x0034:
        goto L_0x0039;
    L_0x0035:
        r7 = r7 + r2;
        r2 = r8 - r2;
        goto L_0x003e;
        monitor-exit(r5);
        return r2;
        r2 = r8;
        r3 = r5.markpos;	 Catch:{ all -> 0x00d9 }
        r4 = -1;	 Catch:{ all -> 0x00d9 }
        if (r3 != r4) goto L_0x0060;	 Catch:{ all -> 0x00d9 }
        r3 = r0.length;	 Catch:{ all -> 0x00d9 }
        if (r2 < r3) goto L_0x0060;	 Catch:{ all -> 0x00d9 }
        r3 = r1.read(r6, r7, r2);	 Catch:{ all -> 0x00d9 }
        if (r3 != r4) goto L_0x005e;
        if (r2 != r8) goto L_0x0058;
        goto L_0x005b;
        r4 = r8 - r2;
        monitor-exit(r5);
        return r4;
        goto L_0x00b1;
        r3 = r5.fillbuf(r1, r0);	 Catch:{ all -> 0x00d9 }
        if (r3 != r4) goto L_0x0074;
        if (r2 != r8) goto L_0x006e;
        goto L_0x0071;
        r4 = r8 - r2;
        monitor-exit(r5);
        return r4;
        r3 = r5.buf;	 Catch:{ all -> 0x00d9 }
        if (r0 == r3) goto L_0x008b;	 Catch:{ all -> 0x00d9 }
        r3 = r5.buf;	 Catch:{ all -> 0x00d9 }
        r0 = r3;	 Catch:{ all -> 0x00d9 }
        if (r0 == 0) goto L_0x0084;	 Catch:{ all -> 0x00d9 }
        goto L_0x008c;	 Catch:{ all -> 0x00d9 }
        r3 = streamClosed();	 Catch:{ all -> 0x00d9 }
        throw r3;	 Catch:{ all -> 0x00d9 }
        r3 = r5.count;	 Catch:{ all -> 0x00d9 }
        r4 = r5.pos;	 Catch:{ all -> 0x00d9 }
        r3 = r3 - r4;	 Catch:{ all -> 0x00d9 }
        if (r3 < r2) goto L_0x0099;	 Catch:{ all -> 0x00d9 }
        r3 = r2;	 Catch:{ all -> 0x00d9 }
        goto L_0x00a1;	 Catch:{ all -> 0x00d9 }
        r3 = r5.count;	 Catch:{ all -> 0x00d9 }
        r4 = r5.pos;	 Catch:{ all -> 0x00d9 }
        r3 = r3 - r4;	 Catch:{ all -> 0x00d9 }
        r4 = r5.pos;	 Catch:{ all -> 0x00d9 }
        java.lang.System.arraycopy(r0, r4, r6, r7, r3);	 Catch:{ all -> 0x00d9 }
        r4 = r5.pos;	 Catch:{ all -> 0x00d9 }
        r4 = r4 + r3;	 Catch:{ all -> 0x00d9 }
        r5.pos = r4;	 Catch:{ all -> 0x00d9 }
        r2 = r2 - r3;
        if (r2 != 0) goto L_0x00b9;
        monitor-exit(r5);
        return r8;
        r4 = r1.available();	 Catch:{ all -> 0x00d9 }
        if (r4 != 0) goto L_0x00c6;
        r4 = r8 - r2;
        monitor-exit(r5);
        return r4;
        r7 = r7 + r3;
        goto L_0x003e;
        r2 = streamClosed();	 Catch:{ all -> 0x00d9 }
        throw r2;	 Catch:{ all -> 0x00d9 }
        r1 = streamClosed();	 Catch:{ all -> 0x00d9 }
        throw r1;	 Catch:{ all -> 0x00d9 }
    L_0x00d9:
        r6 = move-exception;
        monitor-exit(r5);
        throw r6;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream.read(byte[], int, int):int");
    }

    public RecyclableBufferedInputStream(@NonNull InputStream in, @NonNull ArrayPool byteArrayPool) {
        this(in, byteArrayPool, 65536);
    }

    @VisibleForTesting
    RecyclableBufferedInputStream(@NonNull InputStream in, @NonNull ArrayPool byteArrayPool, int bufferSize) {
        super(in);
        this.markpos = -1;
        this.byteArrayPool = byteArrayPool;
        this.buf = (byte[]) byteArrayPool.get(bufferSize, byte[].class);
    }

    public synchronized int available() throws IOException {
        InputStream localIn;
        localIn = this.in;
        if (this.buf == null || localIn == null) {
            throw streamClosed();
        }
        return (this.count - this.pos) + localIn.available();
    }

    private static IOException streamClosed() throws IOException {
        throw new IOException("BufferedInputStream is closed");
    }

    public synchronized void fixMarkLimit() {
        this.marklimit = this.buf.length;
    }

    public synchronized void release() {
        if (this.buf != null) {
            this.byteArrayPool.put(this.buf);
            this.buf = null;
        }
    }

    public void close() throws IOException {
        if (this.buf != null) {
            this.byteArrayPool.put(this.buf);
            this.buf = null;
        }
        InputStream localIn = this.in;
        this.in = null;
        if (localIn != null) {
            localIn.close();
        }
    }

    private int fillbuf(InputStream localIn, byte[] localBuf) throws IOException {
        int i = this.markpos;
        if (i != -1) {
            int i2 = this.pos - i;
            int i3 = this.marklimit;
            if (i2 < i3) {
                if (i == 0 && i3 > localBuf.length && this.count == localBuf.length) {
                    i = localBuf.length * 2;
                    if (i > i3) {
                        i = this.marklimit;
                    }
                    byte[] newbuf = (byte[]) this.byteArrayPool.get(i, byte[].class);
                    System.arraycopy(localBuf, 0, newbuf, 0, localBuf.length);
                    byte[] oldbuf = localBuf;
                    this.buf = newbuf;
                    localBuf = newbuf;
                    this.byteArrayPool.put(oldbuf);
                } else {
                    i = this.markpos;
                    if (i > 0) {
                        System.arraycopy(localBuf, i, localBuf, 0, localBuf.length - i);
                        this.pos -= this.markpos;
                        this.markpos = 0;
                        this.count = 0;
                        i = this.pos;
                        i = localIn.read(localBuf, i, localBuf.length - i);
                        this.count = i > 0 ? this.pos : this.pos + i;
                        return i;
                    }
                }
                this.pos -= this.markpos;
                this.markpos = 0;
                this.count = 0;
                i = this.pos;
                i = localIn.read(localBuf, i, localBuf.length - i);
                if (i > 0) {
                }
                this.count = i > 0 ? this.pos : this.pos + i;
                return i;
            }
        }
        i = localIn.read(localBuf);
        if (i > 0) {
            this.markpos = -1;
            this.pos = 0;
            this.count = i;
        }
        return i;
    }

    public synchronized void mark(int readlimit) {
        this.marklimit = Math.max(this.marklimit, readlimit);
        this.markpos = this.pos;
    }

    public boolean markSupported() {
        return true;
    }

    public synchronized int read() throws IOException {
        byte[] localBuf = this.buf;
        InputStream localIn = this.in;
        if (localBuf == null || localIn == null) {
            throw streamClosed();
        } else if (this.pos >= this.count && fillbuf(localIn, localBuf) == -1) {
            return -1;
        } else {
            if (localBuf != this.buf) {
                localBuf = this.buf;
                if (localBuf == null) {
                    throw streamClosed();
                }
            }
            if (this.count - this.pos <= 0) {
                return -1;
            }
            int i = this.pos;
            this.pos = i + 1;
            return localBuf[i] & 255;
        }
    }

    public synchronized void reset() throws IOException {
        if (this.buf == null) {
            throw new IOException("Stream is closed");
        } else if (-1 != this.markpos) {
            this.pos = this.markpos;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Mark has been invalidated, pos: ");
            stringBuilder.append(this.pos);
            stringBuilder.append(" markLimit: ");
            stringBuilder.append(this.marklimit);
            throw new InvalidMarkException(stringBuilder.toString());
        }
    }

    public synchronized long skip(long byteCount) throws IOException {
        if (byteCount < 1) {
            return 0;
        }
        byte[] localBuf = this.buf;
        if (localBuf != null) {
            InputStream localIn = this.in;
            if (localIn == null) {
                throw streamClosed();
            } else if (((long) (this.count - this.pos)) >= byteCount) {
                this.pos = (int) (((long) this.pos) + byteCount);
                return byteCount;
            } else {
                long read = ((long) this.count) - ((long) this.pos);
                this.pos = this.count;
                if (this.markpos == -1 || byteCount > ((long) this.marklimit)) {
                    return localIn.skip(byteCount - read) + read;
                } else if (fillbuf(localIn, localBuf) == -1) {
                    return read;
                } else {
                    if (((long) (this.count - this.pos)) >= byteCount - read) {
                        this.pos = (int) ((((long) this.pos) + byteCount) - read);
                        return byteCount;
                    }
                    long read2 = (((long) this.count) + read) - ((long) this.pos);
                    this.pos = this.count;
                    return read2;
                }
            }
        }
        throw streamClosed();
    }
}
