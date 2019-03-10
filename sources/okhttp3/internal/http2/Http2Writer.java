package okhttp3.internal.http2;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;

final class Http2Writer implements Closeable {
    private static final Logger logger = Logger.getLogger(Http2.class.getName());
    private final boolean client;
    private boolean closed;
    private final Buffer hpackBuffer = new Buffer();
    final Writer hpackWriter = new Writer(this.hpackBuffer);
    private int maxFrameSize = 16384;
    private final BufferedSink sink;

    public synchronized void settings(okhttp3.internal.http2.Settings r9) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:28:0x004c in {9, 12, 15, 16, 17, 18, 21, 24, 27} preds:[]
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
        r8 = this;
        monitor-enter(r8);
        r0 = r8.closed;	 Catch:{ all -> 0x0049 }
        if (r0 != 0) goto L_0x0041;	 Catch:{ all -> 0x0049 }
    L_0x0005:
        r0 = r9.size();	 Catch:{ all -> 0x0049 }
        r0 = r0 * 6;	 Catch:{ all -> 0x0049 }
        r1 = 4;	 Catch:{ all -> 0x0049 }
        r2 = 0;	 Catch:{ all -> 0x0049 }
        r3 = 0;	 Catch:{ all -> 0x0049 }
        r8.frameHeader(r3, r0, r1, r2);	 Catch:{ all -> 0x0049 }
        r4 = 0;	 Catch:{ all -> 0x0049 }
    L_0x0012:
        r5 = 10;	 Catch:{ all -> 0x0049 }
        if (r4 >= r5) goto L_0x003a;	 Catch:{ all -> 0x0049 }
    L_0x0016:
        r5 = r9.isSet(r4);	 Catch:{ all -> 0x0049 }
        if (r5 != 0) goto L_0x001d;	 Catch:{ all -> 0x0049 }
    L_0x001c:
        goto L_0x0037;	 Catch:{ all -> 0x0049 }
    L_0x001d:
        r5 = r4;	 Catch:{ all -> 0x0049 }
        r6 = 4;	 Catch:{ all -> 0x0049 }
        if (r5 != r6) goto L_0x0023;	 Catch:{ all -> 0x0049 }
    L_0x0021:
        r5 = 3;	 Catch:{ all -> 0x0049 }
        goto L_0x0029;	 Catch:{ all -> 0x0049 }
    L_0x0023:
        r6 = 7;	 Catch:{ all -> 0x0049 }
        if (r5 != r6) goto L_0x0028;	 Catch:{ all -> 0x0049 }
    L_0x0026:
        r5 = 4;	 Catch:{ all -> 0x0049 }
        goto L_0x0029;	 Catch:{ all -> 0x0049 }
    L_0x0029:
        r6 = r8.sink;	 Catch:{ all -> 0x0049 }
        r6.writeShort(r5);	 Catch:{ all -> 0x0049 }
        r6 = r8.sink;	 Catch:{ all -> 0x0049 }
        r7 = r9.get(r4);	 Catch:{ all -> 0x0049 }
        r6.writeInt(r7);	 Catch:{ all -> 0x0049 }
    L_0x0037:
        r4 = r4 + 1;	 Catch:{ all -> 0x0049 }
        goto L_0x0012;	 Catch:{ all -> 0x0049 }
    L_0x003a:
        r4 = r8.sink;	 Catch:{ all -> 0x0049 }
        r4.flush();	 Catch:{ all -> 0x0049 }
        monitor-exit(r8);
        return;
    L_0x0041:
        r0 = new java.io.IOException;	 Catch:{ all -> 0x0049 }
        r1 = "closed";	 Catch:{ all -> 0x0049 }
        r0.<init>(r1);	 Catch:{ all -> 0x0049 }
        throw r0;	 Catch:{ all -> 0x0049 }
    L_0x0049:
        r9 = move-exception;
        monitor-exit(r8);
        throw r9;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Writer.settings(okhttp3.internal.http2.Settings):void");
    }

    Http2Writer(BufferedSink sink, boolean client) {
        this.sink = sink;
        this.client = client;
    }

    public synchronized void connectionPreface() throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        } else if (this.client) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(Util.format(">> CONNECTION %s", new Object[]{Http2.CONNECTION_PREFACE.hex()}));
            }
            this.sink.write(Http2.CONNECTION_PREFACE.toByteArray());
            this.sink.flush();
        }
    }

    public synchronized void applyAndAckSettings(Settings peerSettings) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        this.maxFrameSize = peerSettings.getMaxFrameSize(this.maxFrameSize);
        if (peerSettings.getHeaderTableSize() != -1) {
            this.hpackWriter.setHeaderTableSizeSetting(peerSettings.getHeaderTableSize());
        }
        frameHeader(0, 0, (byte) 4, (byte) 1);
        this.sink.flush();
    }

    public synchronized void pushPromise(int streamId, int promisedStreamId, List<Header> requestHeaders) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        this.hpackWriter.writeHeaders(requestHeaders);
        long byteCount = this.hpackBuffer.size();
        byte flags = (byte) 4;
        int length = (int) Math.min((long) (this.maxFrameSize - 4), byteCount);
        if (byteCount != ((long) length)) {
            flags = (byte) 0;
        }
        frameHeader(streamId, length + 4, (byte) 5, flags);
        this.sink.writeInt(Integer.MAX_VALUE & promisedStreamId);
        this.sink.write(this.hpackBuffer, (long) length);
        if (byteCount > ((long) length)) {
            writeContinuationFrames(streamId, byteCount - ((long) length));
        }
    }

    public synchronized void flush() throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        this.sink.flush();
    }

    public synchronized void synStream(boolean outFinished, int streamId, int associatedStreamId, List<Header> headerBlock) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        headers(outFinished, streamId, headerBlock);
    }

    public synchronized void synReply(boolean outFinished, int streamId, List<Header> headerBlock) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        headers(outFinished, streamId, headerBlock);
    }

    public synchronized void headers(int streamId, List<Header> headerBlock) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        headers(false, streamId, headerBlock);
    }

    public synchronized void rstStream(int streamId, ErrorCode errorCode) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        } else if (errorCode.httpCode != -1) {
            frameHeader(streamId, 4, (byte) 3, (byte) 0);
            this.sink.writeInt(errorCode.httpCode);
            this.sink.flush();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public int maxDataLength() {
        return this.maxFrameSize;
    }

    public synchronized void data(boolean outFinished, int streamId, Buffer source, int byteCount) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        byte flags = (byte) 0;
        if (outFinished) {
            flags = (byte) (0 | 1);
        }
        dataFrame(streamId, flags, source, byteCount);
    }

    void dataFrame(int streamId, byte flags, Buffer buffer, int byteCount) throws IOException {
        frameHeader(streamId, byteCount, (byte) 0, flags);
        if (byteCount > 0) {
            this.sink.write(buffer, (long) byteCount);
        }
    }

    public synchronized void ping(boolean ack, int payload1, int payload2) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        frameHeader(0, 8, (byte) 6, ack ? (byte) 1 : (byte) 0);
        this.sink.writeInt(payload1);
        this.sink.writeInt(payload2);
        this.sink.flush();
    }

    public synchronized void goAway(int lastGoodStreamId, ErrorCode errorCode, byte[] debugData) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        } else if (errorCode.httpCode != -1) {
            frameHeader(0, debugData.length + 8, (byte) 7, (byte) 0);
            this.sink.writeInt(lastGoodStreamId);
            this.sink.writeInt(errorCode.httpCode);
            if (debugData.length > 0) {
                this.sink.write(debugData);
            }
            this.sink.flush();
        } else {
            throw Http2.illegalArgument("errorCode.httpCode == -1", new Object[0]);
        }
    }

    public synchronized void windowUpdate(int streamId, long windowSizeIncrement) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        } else if (windowSizeIncrement == 0 || windowSizeIncrement > 2147483647L) {
            throw Http2.illegalArgument("windowSizeIncrement == 0 || windowSizeIncrement > 0x7fffffffL: %s", Long.valueOf(windowSizeIncrement));
        } else {
            frameHeader(streamId, 4, (byte) 8, (byte) 0);
            this.sink.writeInt((int) windowSizeIncrement);
            this.sink.flush();
        }
    }

    public void frameHeader(int streamId, int length, byte type, byte flags) throws IOException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(Http2.frameLog(false, streamId, length, type, flags));
        }
        if (length > this.maxFrameSize) {
            throw Http2.illegalArgument("FRAME_SIZE_ERROR length > %d: %d", Integer.valueOf(this.maxFrameSize), Integer.valueOf(length));
        } else if ((Integer.MIN_VALUE & streamId) == 0) {
            writeMedium(this.sink, length);
            this.sink.writeByte(type & 255);
            this.sink.writeByte(flags & 255);
            this.sink.writeInt(Integer.MAX_VALUE & streamId);
        } else {
            throw Http2.illegalArgument("reserved bit set: %s", Integer.valueOf(streamId));
        }
    }

    public synchronized void close() throws IOException {
        this.closed = true;
        this.sink.close();
    }

    private static void writeMedium(BufferedSink sink, int i) throws IOException {
        sink.writeByte((i >>> 16) & 255);
        sink.writeByte((i >>> 8) & 255);
        sink.writeByte(i & 255);
    }

    private void writeContinuationFrames(int streamId, long byteCount) throws IOException {
        while (byteCount > 0) {
            int length = (int) Math.min((long) this.maxFrameSize, byteCount);
            byteCount -= (long) length;
            frameHeader(streamId, length, (byte) 9, byteCount == 0 ? (byte) 4 : (byte) 0);
            this.sink.write(this.hpackBuffer, (long) length);
        }
    }

    void headers(boolean outFinished, int streamId, List<Header> headerBlock) throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        this.hpackWriter.writeHeaders(headerBlock);
        long byteCount = this.hpackBuffer.size();
        int length = (int) Math.min((long) this.maxFrameSize, byteCount);
        byte flags = byteCount == ((long) length) ? (byte) 4 : (byte) 0;
        if (outFinished) {
            flags = (byte) (flags | 1);
        }
        frameHeader(streamId, length, (byte) 1, flags);
        this.sink.write(this.hpackBuffer, (long) length);
        if (byteCount > ((long) length)) {
            writeContinuationFrames(streamId, byteCount - ((long) length));
        }
    }
}
