package okhttp3.internal.http2;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import okio.AsyncTimeout;
import okio.Buffer;
import okio.BufferedSource;
import okio.Sink;
import okio.Source;
import okio.Timeout;

public final class Http2Stream {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    long bytesLeftInWriteWindow;
    final Http2Connection connection;
    ErrorCode errorCode = null;
    private boolean hasResponseHeaders;
    final int id;
    final StreamTimeout readTimeout = new StreamTimeout();
    private final List<Header> requestHeaders;
    private List<Header> responseHeaders;
    final FramingSink sink;
    private final FramingSource source;
    long unacknowledgedBytesRead = 0;
    final StreamTimeout writeTimeout = new StreamTimeout();

    final class FramingSink implements Sink {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private static final long EMIT_BUFFER_SIZE = 16384;
        boolean closed;
        boolean finished;
        private final Buffer sendBuffer = new Buffer();

        private void emitFrame(boolean r10) throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:40:0x0090 in {14, 25, 26, 29, 32, 36, 39} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r9 = this;
            r0 = okhttp3.internal.http2.Http2Stream.this;
            monitor-enter(r0);
            r1 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x008d }
            r1 = r1.writeTimeout;	 Catch:{ all -> 0x008d }
            r1.enter();	 Catch:{ all -> 0x008d }
        L_0x000a:
            r1 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x0084 }
            r1 = r1.bytesLeftInWriteWindow;	 Catch:{ all -> 0x0084 }
            r3 = 0;	 Catch:{ all -> 0x0084 }
            r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));	 Catch:{ all -> 0x0084 }
            if (r5 > 0) goto L_0x0028;	 Catch:{ all -> 0x0084 }
        L_0x0014:
            r1 = r9.finished;	 Catch:{ all -> 0x0084 }
            if (r1 != 0) goto L_0x0028;	 Catch:{ all -> 0x0084 }
        L_0x0018:
            r1 = r9.closed;	 Catch:{ all -> 0x0084 }
            if (r1 != 0) goto L_0x0028;	 Catch:{ all -> 0x0084 }
        L_0x001c:
            r1 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x0084 }
            r1 = r1.errorCode;	 Catch:{ all -> 0x0084 }
            if (r1 != 0) goto L_0x0028;	 Catch:{ all -> 0x0084 }
        L_0x0022:
            r1 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x0084 }
            r1.waitForIo();	 Catch:{ all -> 0x0084 }
            goto L_0x000a;
            r1 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x008d }
            r1 = r1.writeTimeout;	 Catch:{ all -> 0x008d }
            r1.exitAndThrowIfTimedOut();	 Catch:{ all -> 0x008d }
            r1 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x008d }
            r1.checkOutNotClosed();	 Catch:{ all -> 0x008d }
            r1 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x008d }
            r1 = r1.bytesLeftInWriteWindow;	 Catch:{ all -> 0x008d }
            r3 = r9.sendBuffer;	 Catch:{ all -> 0x008d }
            r3 = r3.size();	 Catch:{ all -> 0x008d }
            r1 = java.lang.Math.min(r1, r3);	 Catch:{ all -> 0x008d }
            r3 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x008d }
            r4 = r3.bytesLeftInWriteWindow;	 Catch:{ all -> 0x008d }
            r4 = r4 - r1;	 Catch:{ all -> 0x008d }
            r3.bytesLeftInWriteWindow = r4;	 Catch:{ all -> 0x008d }
            monitor-exit(r0);	 Catch:{ all -> 0x008d }
            r0 = okhttp3.internal.http2.Http2Stream.this;
            r0 = r0.writeTimeout;
            r0.enter();
            r0 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x007b }
            r3 = r0.connection;	 Catch:{ all -> 0x007b }
            r0 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x007b }
            r4 = r0.id;	 Catch:{ all -> 0x007b }
            if (r10 == 0) goto L_0x006a;	 Catch:{ all -> 0x007b }
        L_0x005d:
            r0 = r9.sendBuffer;	 Catch:{ all -> 0x007b }
            r5 = r0.size();	 Catch:{ all -> 0x007b }
            r0 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));	 Catch:{ all -> 0x007b }
            if (r0 != 0) goto L_0x006a;	 Catch:{ all -> 0x007b }
        L_0x0067:
            r0 = 1;	 Catch:{ all -> 0x007b }
            r5 = 1;	 Catch:{ all -> 0x007b }
            goto L_0x006c;	 Catch:{ all -> 0x007b }
        L_0x006a:
            r0 = 0;	 Catch:{ all -> 0x007b }
            r5 = 0;	 Catch:{ all -> 0x007b }
        L_0x006c:
            r6 = r9.sendBuffer;	 Catch:{ all -> 0x007b }
            r7 = r1;	 Catch:{ all -> 0x007b }
            r3.writeData(r4, r5, r6, r7);	 Catch:{ all -> 0x007b }
            r0 = okhttp3.internal.http2.Http2Stream.this;
            r0 = r0.writeTimeout;
            r0.exitAndThrowIfTimedOut();
            return;
        L_0x007b:
            r0 = move-exception;
            r3 = okhttp3.internal.http2.Http2Stream.this;
            r3 = r3.writeTimeout;
            r3.exitAndThrowIfTimedOut();
            throw r0;
        L_0x0084:
            r1 = move-exception;
            r2 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x008d }
            r2 = r2.writeTimeout;	 Catch:{ all -> 0x008d }
            r2.exitAndThrowIfTimedOut();	 Catch:{ all -> 0x008d }
            throw r1;	 Catch:{ all -> 0x008d }
        L_0x008d:
            r1 = move-exception;	 Catch:{ all -> 0x008d }
            monitor-exit(r0);	 Catch:{ all -> 0x008d }
            throw r1;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Stream.FramingSink.emitFrame(boolean):void");
        }

        public void close() throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:33:0x0059 in {6, 14, 15, 16, 17, 24, 28, 32} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
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
            r0 = okhttp3.internal.http2.Http2Stream.this;
            monitor-enter(r0);
            r1 = r8.closed;	 Catch:{ all -> 0x0056 }
            if (r1 == 0) goto L_0x000a;	 Catch:{ all -> 0x0056 }
        L_0x0008:
            monitor-exit(r0);	 Catch:{ all -> 0x0056 }
            return;	 Catch:{ all -> 0x0056 }
        L_0x000a:
            monitor-exit(r0);	 Catch:{ all -> 0x0056 }
            r0 = okhttp3.internal.http2.Http2Stream.this;
            r0 = r0.sink;
            r0 = r0.finished;
            r1 = 1;
            if (r0 != 0) goto L_0x003f;
        L_0x0014:
            r0 = r8.sendBuffer;
            r2 = r0.size();
            r4 = 0;
            r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
            if (r0 <= 0) goto L_0x002f;
        L_0x0020:
            r0 = r8.sendBuffer;
            r2 = r0.size();
            r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
            if (r0 <= 0) goto L_0x002e;
        L_0x002a:
            r8.emitFrame(r1);
            goto L_0x0020;
        L_0x002e:
            goto L_0x0040;
        L_0x002f:
            r0 = okhttp3.internal.http2.Http2Stream.this;
            r2 = r0.connection;
            r0 = okhttp3.internal.http2.Http2Stream.this;
            r3 = r0.id;
            r4 = 1;
            r5 = 0;
            r6 = 0;
            r2.writeData(r3, r4, r5, r6);
            goto L_0x0040;
        L_0x0040:
            r2 = okhttp3.internal.http2.Http2Stream.this;
            monitor-enter(r2);
            r8.closed = r1;	 Catch:{ all -> 0x0053 }
            monitor-exit(r2);	 Catch:{ all -> 0x0053 }
            r0 = okhttp3.internal.http2.Http2Stream.this;
            r0 = r0.connection;
            r0.flush();
            r0 = okhttp3.internal.http2.Http2Stream.this;
            r0.cancelStreamIfNecessary();
            return;
        L_0x0053:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x0053 }
            throw r0;
        L_0x0056:
            r1 = move-exception;
            monitor-exit(r0);	 Catch:{ all -> 0x0056 }
            throw r1;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Stream.FramingSink.close():void");
        }

        public void flush() throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:13:0x0026 in {7, 8, 12} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
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
            r0 = okhttp3.internal.http2.Http2Stream.this;
            monitor-enter(r0);
            r1 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x0023 }
            r1.checkOutNotClosed();	 Catch:{ all -> 0x0023 }
            monitor-exit(r0);	 Catch:{ all -> 0x0023 }
        L_0x000a:
            r0 = r5.sendBuffer;
            r0 = r0.size();
            r2 = 0;
            r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
            if (r4 <= 0) goto L_0x0022;
        L_0x0016:
            r0 = 0;
            r5.emitFrame(r0);
            r0 = okhttp3.internal.http2.Http2Stream.this;
            r0 = r0.connection;
            r0.flush();
            goto L_0x000a;
        L_0x0022:
            return;
        L_0x0023:
            r1 = move-exception;
            monitor-exit(r0);	 Catch:{ all -> 0x0023 }
            throw r1;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Stream.FramingSink.flush():void");
        }

        static {
            Class cls = Http2Stream.class;
        }

        FramingSink() {
        }

        public void write(Buffer source, long byteCount) throws IOException {
            this.sendBuffer.write(source, byteCount);
            while (this.sendBuffer.size() >= 16384) {
                emitFrame(false);
            }
        }

        public Timeout timeout() {
            return Http2Stream.this.writeTimeout;
        }
    }

    private final class FramingSource implements Source {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        boolean closed;
        boolean finished;
        private final long maxByteCount;
        private final Buffer readBuffer = new Buffer();
        private final Buffer receiveBuffer = new Buffer();

        private void waitUntilReadable() throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x003a in {11, 13, 16} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
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
            r0 = okhttp3.internal.http2.Http2Stream.this;
            r0 = r0.readTimeout;
            r0.enter();
        L_0x0007:
            r0 = r5.readBuffer;	 Catch:{ all -> 0x0031 }
            r0 = r0.size();	 Catch:{ all -> 0x0031 }
            r2 = 0;	 Catch:{ all -> 0x0031 }
            r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));	 Catch:{ all -> 0x0031 }
            if (r4 != 0) goto L_0x0027;	 Catch:{ all -> 0x0031 }
        L_0x0013:
            r0 = r5.finished;	 Catch:{ all -> 0x0031 }
            if (r0 != 0) goto L_0x0027;	 Catch:{ all -> 0x0031 }
        L_0x0017:
            r0 = r5.closed;	 Catch:{ all -> 0x0031 }
            if (r0 != 0) goto L_0x0027;	 Catch:{ all -> 0x0031 }
        L_0x001b:
            r0 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x0031 }
            r0 = r0.errorCode;	 Catch:{ all -> 0x0031 }
            if (r0 != 0) goto L_0x0027;	 Catch:{ all -> 0x0031 }
        L_0x0021:
            r0 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x0031 }
            r0.waitForIo();	 Catch:{ all -> 0x0031 }
            goto L_0x0007;
            r0 = okhttp3.internal.http2.Http2Stream.this;
            r0 = r0.readTimeout;
            r0.exitAndThrowIfTimedOut();
            return;
        L_0x0031:
            r0 = move-exception;
            r1 = okhttp3.internal.http2.Http2Stream.this;
            r1 = r1.readTimeout;
            r1.exitAndThrowIfTimedOut();
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Stream.FramingSource.waitUntilReadable():void");
        }

        static {
            Class cls = Http2Stream.class;
        }

        FramingSource(long maxByteCount) {
            this.maxByteCount = maxByteCount;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public long read(okio.Buffer r11, long r12) throws java.io.IOException {
            /*
            r10 = this;
            r0 = 0;
            r2 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1));
            if (r2 < 0) goto L_0x00a2;
        L_0x0006:
            r2 = okhttp3.internal.http2.Http2Stream.this;
            monitor-enter(r2);
            r10.waitUntilReadable();	 Catch:{ all -> 0x009f }
            r10.checkNotClosed();	 Catch:{ all -> 0x009f }
            r3 = r10.readBuffer;	 Catch:{ all -> 0x009f }
            r3 = r3.size();	 Catch:{ all -> 0x009f }
            r5 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1));
            if (r5 != 0) goto L_0x001d;
        L_0x0019:
            r0 = -1;
            monitor-exit(r2);	 Catch:{ all -> 0x009f }
            return r0;
        L_0x001d:
            r3 = r10.readBuffer;	 Catch:{ all -> 0x009f }
            r4 = r10.readBuffer;	 Catch:{ all -> 0x009f }
            r4 = r4.size();	 Catch:{ all -> 0x009f }
            r4 = java.lang.Math.min(r12, r4);	 Catch:{ all -> 0x009f }
            r3 = r3.read(r11, r4);	 Catch:{ all -> 0x009f }
            r5 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x009f }
            r6 = r5.unacknowledgedBytesRead;	 Catch:{ all -> 0x009f }
            r6 = r6 + r3;
            r5.unacknowledgedBytesRead = r6;	 Catch:{ all -> 0x009f }
            r5 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x009f }
            r5 = r5.unacknowledgedBytesRead;	 Catch:{ all -> 0x009f }
            r7 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x009f }
            r7 = r7.connection;	 Catch:{ all -> 0x009f }
            r7 = r7.okHttpSettings;	 Catch:{ all -> 0x009f }
            r7 = r7.getInitialWindowSize();	 Catch:{ all -> 0x009f }
            r7 = r7 / 2;
            r7 = (long) r7;	 Catch:{ all -> 0x009f }
            r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
            if (r9 < 0) goto L_0x005d;
        L_0x0049:
            r5 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x009f }
            r5 = r5.connection;	 Catch:{ all -> 0x009f }
            r6 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x009f }
            r6 = r6.id;	 Catch:{ all -> 0x009f }
            r7 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x009f }
            r7 = r7.unacknowledgedBytesRead;	 Catch:{ all -> 0x009f }
            r5.writeWindowUpdateLater(r6, r7);	 Catch:{ all -> 0x009f }
            r5 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x009f }
            r5.unacknowledgedBytesRead = r0;	 Catch:{ all -> 0x009f }
            goto L_0x005e;
        L_0x005e:
            monitor-exit(r2);	 Catch:{ all -> 0x009f }
            r2 = okhttp3.internal.http2.Http2Stream.this;
            r5 = r2.connection;
            monitor-enter(r5);
            r2 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x009c }
            r2 = r2.connection;	 Catch:{ all -> 0x009c }
            r6 = r2.unacknowledgedBytesRead;	 Catch:{ all -> 0x009c }
            r6 = r6 + r3;
            r2.unacknowledgedBytesRead = r6;	 Catch:{ all -> 0x009c }
            r2 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x009c }
            r2 = r2.connection;	 Catch:{ all -> 0x009c }
            r6 = r2.unacknowledgedBytesRead;	 Catch:{ all -> 0x009c }
            r2 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x009c }
            r2 = r2.connection;	 Catch:{ all -> 0x009c }
            r2 = r2.okHttpSettings;	 Catch:{ all -> 0x009c }
            r2 = r2.getInitialWindowSize();	 Catch:{ all -> 0x009c }
            r2 = r2 / 2;
            r8 = (long) r2;	 Catch:{ all -> 0x009c }
            r2 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
            if (r2 < 0) goto L_0x0099;
        L_0x0084:
            r2 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x009c }
            r2 = r2.connection;	 Catch:{ all -> 0x009c }
            r6 = 0;
            r7 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x009c }
            r7 = r7.connection;	 Catch:{ all -> 0x009c }
            r7 = r7.unacknowledgedBytesRead;	 Catch:{ all -> 0x009c }
            r2.writeWindowUpdateLater(r6, r7);	 Catch:{ all -> 0x009c }
            r2 = okhttp3.internal.http2.Http2Stream.this;	 Catch:{ all -> 0x009c }
            r2 = r2.connection;	 Catch:{ all -> 0x009c }
            r2.unacknowledgedBytesRead = r0;	 Catch:{ all -> 0x009c }
            goto L_0x009a;
        L_0x009a:
            monitor-exit(r5);	 Catch:{ all -> 0x009c }
            return r3;
        L_0x009c:
            r0 = move-exception;
            monitor-exit(r5);	 Catch:{ all -> 0x009c }
            throw r0;
        L_0x009f:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x009f }
            throw r0;
        L_0x00a2:
            r0 = new java.lang.IllegalArgumentException;
            r1 = new java.lang.StringBuilder;
            r1.<init>();
            r2 = "byteCount < 0: ";
            r1.append(r2);
            r1.append(r12);
            r1 = r1.toString();
            r0.<init>(r1);
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Stream.FramingSource.read(okio.Buffer, long):long");
        }

        void receive(BufferedSource in, long byteCount) throws IOException {
            while (byteCount > 0) {
                synchronized (Http2Stream.this) {
                    boolean finished = this.finished;
                    boolean z = true;
                    boolean flowControlError = this.readBuffer.size() + byteCount > this.maxByteCount;
                }
                if (flowControlError) {
                    in.skip(byteCount);
                    Http2Stream.this.closeLater(ErrorCode.FLOW_CONTROL_ERROR);
                    return;
                } else if (finished) {
                    in.skip(byteCount);
                    return;
                } else {
                    long read = in.read(this.receiveBuffer, byteCount);
                    if (read != -1) {
                        long byteCount2 = byteCount - read;
                        synchronized (Http2Stream.this) {
                            if (this.readBuffer.size() != 0) {
                                z = false;
                            }
                            boolean wasEmpty = z;
                            this.readBuffer.writeAll(this.receiveBuffer);
                            if (wasEmpty) {
                                Http2Stream.this.notifyAll();
                            }
                        }
                        byteCount = byteCount2;
                    } else {
                        throw new EOFException();
                    }
                }
            }
        }

        public Timeout timeout() {
            return Http2Stream.this.readTimeout;
        }

        public void close() throws IOException {
            synchronized (Http2Stream.this) {
                this.closed = true;
                this.readBuffer.clear();
                Http2Stream.this.notifyAll();
            }
            Http2Stream.this.cancelStreamIfNecessary();
        }

        private void checkNotClosed() throws IOException {
            if (this.closed) {
                throw new IOException("stream closed");
            } else if (Http2Stream.this.errorCode != null) {
                throw new StreamResetException(Http2Stream.this.errorCode);
            }
        }
    }

    class StreamTimeout extends AsyncTimeout {
        StreamTimeout() {
        }

        protected void timedOut() {
            Http2Stream.this.closeLater(ErrorCode.CANCEL);
        }

        protected IOException newTimeoutException(IOException cause) {
            SocketTimeoutException socketTimeoutException = new SocketTimeoutException("timeout");
            if (cause != null) {
                socketTimeoutException.initCause(cause);
            }
            return socketTimeoutException;
        }

        public void exitAndThrowIfTimedOut() throws IOException {
            if (exit()) {
                throw newTimeoutException(null);
            }
        }
    }

    public synchronized boolean isOpen() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:28:0x002e in {6, 12, 17, 21, 24, 27} preds:[]
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
        r2 = this;
        monitor-enter(r2);
        r0 = r2.errorCode;	 Catch:{ all -> 0x002b }
        r1 = 0;
        if (r0 == 0) goto L_0x0008;
    L_0x0006:
        monitor-exit(r2);
        return r1;
    L_0x0008:
        r0 = r2.source;	 Catch:{ all -> 0x002b }
        r0 = r0.finished;	 Catch:{ all -> 0x002b }
        if (r0 != 0) goto L_0x0016;	 Catch:{ all -> 0x002b }
    L_0x000e:
        r0 = r2.source;	 Catch:{ all -> 0x002b }
        r0 = r0.closed;	 Catch:{ all -> 0x002b }
        if (r0 == 0) goto L_0x0015;	 Catch:{ all -> 0x002b }
    L_0x0014:
        goto L_0x0016;	 Catch:{ all -> 0x002b }
    L_0x0015:
        goto L_0x0028;	 Catch:{ all -> 0x002b }
    L_0x0016:
        r0 = r2.sink;	 Catch:{ all -> 0x002b }
        r0 = r0.finished;	 Catch:{ all -> 0x002b }
        if (r0 != 0) goto L_0x0022;	 Catch:{ all -> 0x002b }
    L_0x001c:
        r0 = r2.sink;	 Catch:{ all -> 0x002b }
        r0 = r0.closed;	 Catch:{ all -> 0x002b }
        if (r0 == 0) goto L_0x0015;	 Catch:{ all -> 0x002b }
    L_0x0022:
        r0 = r2.hasResponseHeaders;	 Catch:{ all -> 0x002b }
        if (r0 == 0) goto L_0x0015;
    L_0x0026:
        monitor-exit(r2);
        return r1;
    L_0x0028:
        r0 = 1;
        monitor-exit(r2);
        return r0;
    L_0x002b:
        r0 = move-exception;
        monitor-exit(r2);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Stream.isOpen():boolean");
    }

    public synchronized java.util.List<okhttp3.internal.http2.Header> takeResponseHeaders() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:30:0x0042 in {11, 18, 21, 24, 26, 29} preds:[]
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
        r3 = this;
        monitor-enter(r3);
        r0 = r3.isLocallyInitiated();	 Catch:{ all -> 0x003f }
        if (r0 == 0) goto L_0x0037;	 Catch:{ all -> 0x003f }
    L_0x0007:
        r0 = r3.readTimeout;	 Catch:{ all -> 0x003f }
        r0.enter();	 Catch:{ all -> 0x003f }
    L_0x000c:
        r0 = r3.responseHeaders;	 Catch:{ all -> 0x0030 }
        if (r0 != 0) goto L_0x0018;	 Catch:{ all -> 0x0030 }
    L_0x0010:
        r0 = r3.errorCode;	 Catch:{ all -> 0x0030 }
        if (r0 != 0) goto L_0x0018;	 Catch:{ all -> 0x0030 }
    L_0x0014:
        r3.waitForIo();	 Catch:{ all -> 0x0030 }
        goto L_0x000c;
        r0 = r3.readTimeout;	 Catch:{ all -> 0x003f }
        r0.exitAndThrowIfTimedOut();	 Catch:{ all -> 0x003f }
        r0 = r3.responseHeaders;	 Catch:{ all -> 0x003f }
        if (r0 == 0) goto L_0x0028;	 Catch:{ all -> 0x003f }
    L_0x0023:
        r1 = 0;	 Catch:{ all -> 0x003f }
        r3.responseHeaders = r1;	 Catch:{ all -> 0x003f }
        monitor-exit(r3);
        return r0;
    L_0x0028:
        r1 = new okhttp3.internal.http2.StreamResetException;	 Catch:{ all -> 0x003f }
        r2 = r3.errorCode;	 Catch:{ all -> 0x003f }
        r1.<init>(r2);	 Catch:{ all -> 0x003f }
        throw r1;	 Catch:{ all -> 0x003f }
    L_0x0030:
        r0 = move-exception;	 Catch:{ all -> 0x003f }
        r1 = r3.readTimeout;	 Catch:{ all -> 0x003f }
        r1.exitAndThrowIfTimedOut();	 Catch:{ all -> 0x003f }
        throw r0;	 Catch:{ all -> 0x003f }
    L_0x0037:
        r0 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x003f }
        r1 = "servers cannot read response headers";	 Catch:{ all -> 0x003f }
        r0.<init>(r1);	 Catch:{ all -> 0x003f }
        throw r0;	 Catch:{ all -> 0x003f }
    L_0x003f:
        r0 = move-exception;
        monitor-exit(r3);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Stream.takeResponseHeaders():java.util.List<okhttp3.internal.http2.Header>");
    }

    Http2Stream(int id, Http2Connection connection, boolean outFinished, boolean inFinished, List<Header> requestHeaders) {
        if (connection == null) {
            throw new NullPointerException("connection == null");
        } else if (requestHeaders != null) {
            this.id = id;
            this.connection = connection;
            this.bytesLeftInWriteWindow = (long) connection.peerSettings.getInitialWindowSize();
            this.source = new FramingSource((long) connection.okHttpSettings.getInitialWindowSize());
            this.sink = new FramingSink();
            this.source.finished = inFinished;
            this.sink.finished = outFinished;
            this.requestHeaders = requestHeaders;
        } else {
            throw new NullPointerException("requestHeaders == null");
        }
    }

    public int getId() {
        return this.id;
    }

    public boolean isLocallyInitiated() {
        if (this.connection.client == ((this.id & 1) == 1)) {
            return true;
        }
        return false;
    }

    public Http2Connection getConnection() {
        return this.connection;
    }

    public List<Header> getRequestHeaders() {
        return this.requestHeaders;
    }

    public synchronized ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public void sendResponseHeaders(List<Header> responseHeaders, boolean out) throws IOException {
        if (responseHeaders != null) {
            boolean outFinished = false;
            synchronized (this) {
                this.hasResponseHeaders = true;
                if (!out) {
                    this.sink.finished = true;
                    outFinished = true;
                }
            }
            this.connection.writeSynReply(this.id, outFinished, responseHeaders);
            if (outFinished) {
                this.connection.flush();
                return;
            }
            return;
        }
        throw new NullPointerException("responseHeaders == null");
    }

    public Timeout readTimeout() {
        return this.readTimeout;
    }

    public Timeout writeTimeout() {
        return this.writeTimeout;
    }

    public Source getSource() {
        return this.source;
    }

    public Sink getSink() {
        synchronized (this) {
            if (!this.hasResponseHeaders) {
                if (!isLocallyInitiated()) {
                    throw new IllegalStateException("reply before requesting the sink");
                }
            }
        }
        return this.sink;
    }

    public void close(ErrorCode rstStatusCode) throws IOException {
        if (closeInternal(rstStatusCode)) {
            this.connection.writeSynReset(this.id, rstStatusCode);
        }
    }

    public void closeLater(ErrorCode errorCode) {
        if (closeInternal(errorCode)) {
            this.connection.writeSynResetLater(this.id, errorCode);
        }
    }

    private boolean closeInternal(ErrorCode errorCode) {
        synchronized (this) {
            if (this.errorCode != null) {
                return false;
            } else if (this.source.finished && this.sink.finished) {
                return false;
            } else {
                this.errorCode = errorCode;
                notifyAll();
                this.connection.removeStream(this.id);
                return true;
            }
        }
    }

    void receiveHeaders(List<Header> headers) {
        boolean open = true;
        synchronized (this) {
            this.hasResponseHeaders = true;
            if (this.responseHeaders == null) {
                this.responseHeaders = headers;
                open = isOpen();
                notifyAll();
            } else {
                List<Header> newHeaders = new ArrayList();
                newHeaders.addAll(this.responseHeaders);
                newHeaders.add(null);
                newHeaders.addAll(headers);
                this.responseHeaders = newHeaders;
            }
        }
        if (!open) {
            this.connection.removeStream(this.id);
        }
    }

    void receiveData(BufferedSource in, int length) throws IOException {
        this.source.receive(in, (long) length);
    }

    void receiveFin() {
        synchronized (this) {
            this.source.finished = true;
            boolean open = isOpen();
            notifyAll();
        }
        if (!open) {
            this.connection.removeStream(this.id);
        }
    }

    synchronized void receiveRstStream(ErrorCode errorCode) {
        if (this.errorCode == null) {
            this.errorCode = errorCode;
            notifyAll();
        }
    }

    void cancelStreamIfNecessary() throws IOException {
        synchronized (this) {
            boolean cancel = !this.source.finished && this.source.closed && (this.sink.finished || this.sink.closed);
            boolean open = isOpen();
        }
        if (cancel) {
            close(ErrorCode.CANCEL);
        } else if (!open) {
            this.connection.removeStream(this.id);
        }
    }

    void addBytesToWriteWindow(long delta) {
        this.bytesLeftInWriteWindow += delta;
        if (delta > 0) {
            notifyAll();
        }
    }

    void checkOutNotClosed() throws IOException {
        if (this.sink.closed) {
            throw new IOException("stream closed");
        } else if (this.sink.finished) {
            throw new IOException("stream finished");
        } else {
            ErrorCode errorCode = this.errorCode;
            if (errorCode != null) {
                throw new StreamResetException(errorCode);
            }
        }
    }

    void waitForIo() throws InterruptedIOException {
        try {
            wait();
        } catch (InterruptedException e) {
            throw new InterruptedIOException();
        }
    }
}
