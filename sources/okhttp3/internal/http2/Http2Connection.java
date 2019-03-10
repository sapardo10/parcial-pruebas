package okhttp3.internal.http2;

import android.support.v4.internal.view.SupportMenu;
import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.Protocol;
import okhttp3.internal.NamedRunnable;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;

public final class Http2Connection implements Closeable {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int OKHTTP_CLIENT_WINDOW_SIZE = 16777216;
    private static final ExecutorService listenerExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue(), Util.threadFactory("OkHttp Http2Connection", true));
    private boolean awaitingPong;
    long bytesLeftInWriteWindow;
    final boolean client;
    final Set<Integer> currentPushRequests = new LinkedHashSet();
    final String hostname;
    int lastGoodStreamId;
    final Http2Connection$Listener listener;
    int nextStreamId;
    Settings okHttpSettings = new Settings();
    final Settings peerSettings = new Settings();
    private final ExecutorService pushExecutor;
    final PushObserver pushObserver;
    final ReaderRunnable readerRunnable;
    boolean receivedInitialPeerSettings = false;
    boolean shutdown;
    final Socket socket;
    final Map<Integer, Http2Stream> streams = new LinkedHashMap();
    long unacknowledgedBytesRead = 0;
    final Http2Writer writer;
    private final ScheduledExecutorService writerExecutor;

    public static class Builder {
        boolean client;
        String hostname;
        Http2Connection$Listener listener = Http2Connection$Listener.REFUSE_INCOMING_STREAMS;
        int pingIntervalMillis;
        PushObserver pushObserver = PushObserver.CANCEL;
        BufferedSink sink;
        Socket socket;
        BufferedSource source;

        public Builder(boolean client) {
            this.client = client;
        }

        public Builder socket(Socket socket) throws IOException {
            return socket(socket, ((InetSocketAddress) socket.getRemoteSocketAddress()).getHostName(), Okio.buffer(Okio.source(socket)), Okio.buffer(Okio.sink(socket)));
        }

        public Builder socket(Socket socket, String hostname, BufferedSource source, BufferedSink sink) {
            this.socket = socket;
            this.hostname = hostname;
            this.source = source;
            this.sink = sink;
            return this;
        }

        public Builder listener(Http2Connection$Listener listener) {
            this.listener = listener;
            return this;
        }

        public Builder pushObserver(PushObserver pushObserver) {
            this.pushObserver = pushObserver;
            return this;
        }

        public Builder pingIntervalMillis(int pingIntervalMillis) {
            this.pingIntervalMillis = pingIntervalMillis;
            return this;
        }

        public Http2Connection build() {
            return new Http2Connection(this);
        }
    }

    final class PingRunnable extends NamedRunnable {
        final int payload1;
        final int payload2;
        final boolean reply;

        PingRunnable(boolean reply, int payload1, int payload2) {
            super("OkHttp %s ping %08x%08x", this$0.hostname, Integer.valueOf(payload1), Integer.valueOf(payload2));
            this.reply = reply;
            this.payload1 = payload1;
            this.payload2 = payload2;
        }

        public void execute() {
            Http2Connection.this.writePing(this.reply, this.payload1, this.payload2);
        }
    }

    class ReaderRunnable extends NamedRunnable implements Handler {
        final Http2Reader reader;

        /* renamed from: okhttp3.internal.http2.Http2Connection$ReaderRunnable$2 */
        class C12002 extends NamedRunnable {
            C12002(String format, Object... args) {
                super(format, args);
            }

            public void execute() {
                Http2Connection.this.listener.onSettings(Http2Connection.this);
            }
        }

        protected void execute() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:32:0x0046 in {5, 10, 12, 20, 21, 22, 24, 28, 29, 31} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r4 = this;
            r0 = okhttp3.internal.http2.ErrorCode.INTERNAL_ERROR;
            r1 = okhttp3.internal.http2.ErrorCode.INTERNAL_ERROR;
            r2 = r4.reader;	 Catch:{ IOException -> 0x0023 }
            r2.readConnectionPreface(r4);	 Catch:{ IOException -> 0x0023 }
        L_0x0009:
            r2 = r4.reader;	 Catch:{ IOException -> 0x0023 }
            r3 = 0;	 Catch:{ IOException -> 0x0023 }
            r2 = r2.nextFrame(r3, r4);	 Catch:{ IOException -> 0x0023 }
            if (r2 == 0) goto L_0x0013;	 Catch:{ IOException -> 0x0023 }
        L_0x0012:
            goto L_0x0009;	 Catch:{ IOException -> 0x0023 }
        L_0x0013:
            r2 = okhttp3.internal.http2.ErrorCode.NO_ERROR;	 Catch:{ IOException -> 0x0023 }
            r0 = r2;	 Catch:{ IOException -> 0x0023 }
            r2 = okhttp3.internal.http2.ErrorCode.CANCEL;	 Catch:{ IOException -> 0x0023 }
            r1 = r2;
            r2 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ IOException -> 0x001f }
            r2.close(r0, r1);	 Catch:{ IOException -> 0x001f }
            goto L_0x002f;
        L_0x001f:
            r2 = move-exception;
            goto L_0x0031;
        L_0x0021:
            r2 = move-exception;
            goto L_0x0038;
        L_0x0023:
            r2 = move-exception;
            r3 = okhttp3.internal.http2.ErrorCode.PROTOCOL_ERROR;	 Catch:{ all -> 0x0021 }
            r0 = r3;	 Catch:{ all -> 0x0021 }
            r3 = okhttp3.internal.http2.ErrorCode.PROTOCOL_ERROR;	 Catch:{ all -> 0x0021 }
            r1 = r3;
            r2 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ IOException -> 0x0030 }
            r2.close(r0, r1);	 Catch:{ IOException -> 0x0030 }
        L_0x002f:
            goto L_0x0031;
        L_0x0030:
            r2 = move-exception;
        L_0x0031:
            r2 = r4.reader;
            okhttp3.internal.Util.closeQuietly(r2);
            return;
            r3 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ IOException -> 0x003f }
            r3.close(r0, r1);	 Catch:{ IOException -> 0x003f }
            goto L_0x0040;
        L_0x003f:
            r3 = move-exception;
        L_0x0040:
            r3 = r4.reader;
            okhttp3.internal.Util.closeQuietly(r3);
            throw r2;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.ReaderRunnable.execute():void");
        }

        public void goAway(int r7, okhttp3.internal.http2.ErrorCode r8, okio.ByteString r9) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:19:0x004d in {11, 12, 13, 14, 18} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r6 = this;
            r9.size();
            r0 = okhttp3.internal.http2.Http2Connection.this;
            monitor-enter(r0);
            r1 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x004a }
            r1 = r1.streams;	 Catch:{ all -> 0x004a }
            r1 = r1.values();	 Catch:{ all -> 0x004a }
            r2 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x004a }
            r2 = r2.streams;	 Catch:{ all -> 0x004a }
            r2 = r2.size();	 Catch:{ all -> 0x004a }
            r2 = new okhttp3.internal.http2.Http2Stream[r2];	 Catch:{ all -> 0x004a }
            r1 = r1.toArray(r2);	 Catch:{ all -> 0x004a }
            r1 = (okhttp3.internal.http2.Http2Stream[]) r1;	 Catch:{ all -> 0x004a }
            r2 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x004a }
            r3 = 1;	 Catch:{ all -> 0x004a }
            r2.shutdown = r3;	 Catch:{ all -> 0x004a }
            monitor-exit(r0);	 Catch:{ all -> 0x004a }
            r0 = r1.length;
            r2 = 0;
        L_0x0026:
            if (r2 >= r0) goto L_0x0049;
        L_0x0028:
            r3 = r1[r2];
            r4 = r3.getId();
            if (r4 <= r7) goto L_0x0045;
        L_0x0030:
            r4 = r3.isLocallyInitiated();
            if (r4 == 0) goto L_0x0045;
        L_0x0036:
            r4 = okhttp3.internal.http2.ErrorCode.REFUSED_STREAM;
            r3.receiveRstStream(r4);
            r4 = okhttp3.internal.http2.Http2Connection.this;
            r5 = r3.getId();
            r4.removeStream(r5);
            goto L_0x0046;
        L_0x0046:
            r2 = r2 + 1;
            goto L_0x0026;
        L_0x0049:
            return;
        L_0x004a:
            r1 = move-exception;
            monitor-exit(r0);	 Catch:{ all -> 0x004a }
            throw r1;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.ReaderRunnable.goAway(int, okhttp3.internal.http2.ErrorCode, okio.ByteString):void");
        }

        public void settings(boolean r13, okhttp3.internal.http2.Settings r14) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:41:0x009f in {5, 11, 12, 15, 16, 17, 30, 33, 34, 35, 36, 40} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:38)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/2106165633.run(Unknown Source)
*/
            /*
            r12 = this;
            r0 = 0;
            r2 = 0;
            r3 = okhttp3.internal.http2.Http2Connection.this;
            monitor-enter(r3);
            r4 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x009c }
            r4 = r4.peerSettings;	 Catch:{ all -> 0x009c }
            r4 = r4.getInitialWindowSize();	 Catch:{ all -> 0x009c }
            if (r13 == 0) goto L_0x0017;	 Catch:{ all -> 0x009c }
        L_0x0010:
            r5 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x009c }
            r5 = r5.peerSettings;	 Catch:{ all -> 0x009c }
            r5.clear();	 Catch:{ all -> 0x009c }
        L_0x0017:
            r5 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x009c }
            r5 = r5.peerSettings;	 Catch:{ all -> 0x009c }
            r5.merge(r14);	 Catch:{ all -> 0x009c }
            r12.applyAndAckSettings(r14);	 Catch:{ all -> 0x009c }
            r5 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x009c }
            r5 = r5.peerSettings;	 Catch:{ all -> 0x009c }
            r5 = r5.getInitialWindowSize();	 Catch:{ all -> 0x009c }
            r6 = -1;	 Catch:{ all -> 0x009c }
            r7 = 1;	 Catch:{ all -> 0x009c }
            if (r5 == r6) goto L_0x0068;	 Catch:{ all -> 0x009c }
        L_0x002d:
            if (r5 == r4) goto L_0x0068;	 Catch:{ all -> 0x009c }
        L_0x002f:
            r6 = r5 - r4;	 Catch:{ all -> 0x009c }
            r0 = (long) r6;	 Catch:{ all -> 0x009c }
            r6 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x009c }
            r6 = r6.receivedInitialPeerSettings;	 Catch:{ all -> 0x009c }
            if (r6 != 0) goto L_0x0042;	 Catch:{ all -> 0x009c }
        L_0x0038:
            r6 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x009c }
            r6.addBytesToWriteWindow(r0);	 Catch:{ all -> 0x009c }
            r6 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x009c }
            r6.receivedInitialPeerSettings = r7;	 Catch:{ all -> 0x009c }
            goto L_0x0043;	 Catch:{ all -> 0x009c }
        L_0x0043:
            r6 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x009c }
            r6 = r6.streams;	 Catch:{ all -> 0x009c }
            r6 = r6.isEmpty();	 Catch:{ all -> 0x009c }
            if (r6 != 0) goto L_0x0067;	 Catch:{ all -> 0x009c }
        L_0x004d:
            r6 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x009c }
            r6 = r6.streams;	 Catch:{ all -> 0x009c }
            r6 = r6.values();	 Catch:{ all -> 0x009c }
            r8 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x009c }
            r8 = r8.streams;	 Catch:{ all -> 0x009c }
            r8 = r8.size();	 Catch:{ all -> 0x009c }
            r8 = new okhttp3.internal.http2.Http2Stream[r8];	 Catch:{ all -> 0x009c }
            r6 = r6.toArray(r8);	 Catch:{ all -> 0x009c }
            r6 = (okhttp3.internal.http2.Http2Stream[]) r6;	 Catch:{ all -> 0x009c }
            r2 = r6;	 Catch:{ all -> 0x009c }
            goto L_0x0069;	 Catch:{ all -> 0x009c }
        L_0x0067:
            goto L_0x0069;	 Catch:{ all -> 0x009c }
        L_0x0069:
            r6 = okhttp3.internal.http2.Http2Connection.listenerExecutor;	 Catch:{ all -> 0x009c }
            r8 = new okhttp3.internal.http2.Http2Connection$ReaderRunnable$2;	 Catch:{ all -> 0x009c }
            r9 = "OkHttp %s settings";	 Catch:{ all -> 0x009c }
            r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x009c }
            r10 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x009c }
            r10 = r10.hostname;	 Catch:{ all -> 0x009c }
            r11 = 0;	 Catch:{ all -> 0x009c }
            r7[r11] = r10;	 Catch:{ all -> 0x009c }
            r8.<init>(r9, r7);	 Catch:{ all -> 0x009c }
            r6.execute(r8);	 Catch:{ all -> 0x009c }
            monitor-exit(r3);	 Catch:{ all -> 0x009c }
            if (r2 == 0) goto L_0x009a;
        L_0x0083:
            r3 = 0;
            r5 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
            if (r5 == 0) goto L_0x009a;
        L_0x0089:
            r3 = r2.length;
        L_0x008a:
            if (r11 >= r3) goto L_0x0099;
        L_0x008c:
            r4 = r2[r11];
            monitor-enter(r4);
            r4.addBytesToWriteWindow(r0);	 Catch:{ all -> 0x0096 }
            monitor-exit(r4);	 Catch:{ all -> 0x0096 }
            r11 = r11 + 1;	 Catch:{ all -> 0x0096 }
            goto L_0x008a;	 Catch:{ all -> 0x0096 }
        L_0x0096:
            r3 = move-exception;	 Catch:{ all -> 0x0096 }
            monitor-exit(r4);	 Catch:{ all -> 0x0096 }
            throw r3;
        L_0x0099:
            goto L_0x009b;
        L_0x009b:
            return;
        L_0x009c:
            r4 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x009c }
            throw r4;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.ReaderRunnable.settings(boolean, okhttp3.internal.http2.Settings):void");
        }

        ReaderRunnable(Http2Reader reader) {
            super("OkHttp %s", this$0.hostname);
            this.reader = reader;
        }

        public void data(boolean inFinished, int streamId, BufferedSource source, int length) throws IOException {
            if (Http2Connection.this.pushedStream(streamId)) {
                Http2Connection.this.pushDataLater(streamId, source, length, inFinished);
                return;
            }
            Http2Stream dataStream = Http2Connection.this.getStream(streamId);
            if (dataStream == null) {
                Http2Connection.this.writeSynResetLater(streamId, ErrorCode.PROTOCOL_ERROR);
                source.skip((long) length);
                return;
            }
            dataStream.receiveData(source, length);
            if (inFinished) {
                dataStream.receiveFin();
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void headers(boolean r12, int r13, int r14, java.util.List<okhttp3.internal.http2.Header> r15) {
            /*
            r11 = this;
            r0 = okhttp3.internal.http2.Http2Connection.this;
            r0 = r0.pushedStream(r13);
            if (r0 == 0) goto L_0x000e;
        L_0x0008:
            r0 = okhttp3.internal.http2.Http2Connection.this;
            r0.pushHeadersLater(r13, r15, r12);
            return;
        L_0x000e:
            r0 = okhttp3.internal.http2.Http2Connection.this;
            monitor-enter(r0);
            r1 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x007b }
            r1 = r1.getStream(r13);	 Catch:{ all -> 0x007b }
            if (r1 != 0) goto L_0x0070;
        L_0x0019:
            r2 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x007b }
            r2 = r2.shutdown;	 Catch:{ all -> 0x007b }
            if (r2 == 0) goto L_0x0021;
        L_0x001f:
            monitor-exit(r0);	 Catch:{ all -> 0x007b }
            return;
        L_0x0021:
            r2 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x007b }
            r2 = r2.lastGoodStreamId;	 Catch:{ all -> 0x007b }
            if (r13 > r2) goto L_0x0029;
        L_0x0027:
            monitor-exit(r0);	 Catch:{ all -> 0x007b }
            return;
        L_0x0029:
            r2 = r13 % 2;
            r3 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x007b }
            r3 = r3.nextStreamId;	 Catch:{ all -> 0x007b }
            r4 = 2;
            r3 = r3 % r4;
            if (r2 != r3) goto L_0x0035;
        L_0x0033:
            monitor-exit(r0);	 Catch:{ all -> 0x007b }
            return;
        L_0x0035:
            r2 = new okhttp3.internal.http2.Http2Stream;	 Catch:{ all -> 0x007b }
            r7 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x007b }
            r8 = 0;
            r5 = r2;
            r6 = r13;
            r9 = r12;
            r10 = r15;
            r5.<init>(r6, r7, r8, r9, r10);	 Catch:{ all -> 0x007b }
            r3 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x007b }
            r3.lastGoodStreamId = r13;	 Catch:{ all -> 0x007b }
            r3 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x007b }
            r3 = r3.streams;	 Catch:{ all -> 0x007b }
            r5 = java.lang.Integer.valueOf(r13);	 Catch:{ all -> 0x007b }
            r3.put(r5, r2);	 Catch:{ all -> 0x007b }
            r3 = okhttp3.internal.http2.Http2Connection.listenerExecutor;	 Catch:{ all -> 0x007b }
            r5 = new okhttp3.internal.http2.Http2Connection$ReaderRunnable$1;	 Catch:{ all -> 0x007b }
            r6 = "OkHttp %s stream %d";
            r4 = new java.lang.Object[r4];	 Catch:{ all -> 0x007b }
            r7 = 0;
            r8 = okhttp3.internal.http2.Http2Connection.this;	 Catch:{ all -> 0x007b }
            r8 = r8.hostname;	 Catch:{ all -> 0x007b }
            r4[r7] = r8;	 Catch:{ all -> 0x007b }
            r7 = 1;
            r8 = java.lang.Integer.valueOf(r13);	 Catch:{ all -> 0x007b }
            r4[r7] = r8;	 Catch:{ all -> 0x007b }
            r5.<init>(r6, r4, r2);	 Catch:{ all -> 0x007b }
            r3.execute(r5);	 Catch:{ all -> 0x007b }
            monitor-exit(r0);	 Catch:{ all -> 0x007b }
            return;
        L_0x0070:
            monitor-exit(r0);	 Catch:{ all -> 0x007b }
            r1.receiveHeaders(r15);
            if (r12 == 0) goto L_0x007a;
        L_0x0076:
            r1.receiveFin();
        L_0x007a:
            return;
        L_0x007b:
            r1 = move-exception;
            monitor-exit(r0);	 Catch:{ all -> 0x007b }
            throw r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.ReaderRunnable.headers(boolean, int, int, java.util.List):void");
        }

        public void rstStream(int streamId, ErrorCode errorCode) {
            if (Http2Connection.this.pushedStream(streamId)) {
                Http2Connection.this.pushResetLater(streamId, errorCode);
                return;
            }
            Http2Stream rstStream = Http2Connection.this.removeStream(streamId);
            if (rstStream != null) {
                rstStream.receiveRstStream(errorCode);
            }
        }

        private void applyAndAckSettings(final Settings peerSettings) {
            try {
                Http2Connection.this.writerExecutor.execute(new NamedRunnable("OkHttp %s ACK Settings", new Object[]{Http2Connection.this.hostname}) {
                    public void execute() {
                        try {
                            Http2Connection.this.writer.applyAndAckSettings(peerSettings);
                        } catch (IOException e) {
                            Http2Connection.this.failConnection();
                        }
                    }
                });
            } catch (RejectedExecutionException e) {
            }
        }

        public void ackSettings() {
        }

        public void ping(boolean reply, int payload1, int payload2) {
            if (reply) {
                synchronized (Http2Connection.this) {
                    Http2Connection.this.awaitingPong = false;
                    Http2Connection.this.notifyAll();
                }
                return;
            }
            try {
                Http2Connection.this.writerExecutor.execute(new PingRunnable(true, payload1, payload2));
            } catch (RejectedExecutionException e) {
            }
        }

        public void windowUpdate(int streamId, long windowSizeIncrement) {
            if (streamId == 0) {
                synchronized (Http2Connection.this) {
                    Http2Connection http2Connection = Http2Connection.this;
                    http2Connection.bytesLeftInWriteWindow += windowSizeIncrement;
                    Http2Connection.this.notifyAll();
                }
                return;
            }
            Http2Stream stream = Http2Connection.this.getStream(streamId);
            if (stream != null) {
                synchronized (stream) {
                    stream.addBytesToWriteWindow(windowSizeIncrement);
                }
            }
        }

        public void priority(int streamId, int streamDependency, int weight, boolean exclusive) {
        }

        public void pushPromise(int streamId, int promisedStreamId, List<Header> requestHeaders) {
            Http2Connection.this.pushRequestLater(promisedStreamId, requestHeaders);
        }

        public void alternateService(int streamId, String origin, ByteString protocol, String host, int port, long maxAge) {
        }
    }

    synchronized void awaitPong() throws java.io.IOException, java.lang.InterruptedException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:11:0x000e in {5, 7, 10} preds:[]
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
        r1 = this;
        monitor-enter(r1);
    L_0x0001:
        r0 = r1.awaitingPong;	 Catch:{ all -> 0x000b }
        if (r0 == 0) goto L_0x0009;	 Catch:{ all -> 0x000b }
    L_0x0005:
        r1.wait();	 Catch:{ all -> 0x000b }
        goto L_0x0001;
    L_0x0009:
        monitor-exit(r1);
        return;
    L_0x000b:
        r0 = move-exception;
        monitor-exit(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.awaitPong():void");
    }

    void close(okhttp3.internal.http2.ErrorCode r7, okhttp3.internal.http2.ErrorCode r8) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:46:0x0069 in {3, 5, 11, 12, 20, 23, 24, 25, 26, 29, 32, 35, 37, 40, 41, 45} preds:[]
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
        r6 = this;
        r0 = 0;
        r6.shutdown(r7);	 Catch:{ IOException -> 0x0006 }
        goto L_0x0008;
    L_0x0006:
        r1 = move-exception;
        r0 = r1;
    L_0x0008:
        r1 = 0;
        monitor-enter(r6);
        r2 = r6.streams;	 Catch:{ all -> 0x0066 }
        r2 = r2.isEmpty();	 Catch:{ all -> 0x0066 }
        if (r2 != 0) goto L_0x002d;	 Catch:{ all -> 0x0066 }
    L_0x0012:
        r2 = r6.streams;	 Catch:{ all -> 0x0066 }
        r2 = r2.values();	 Catch:{ all -> 0x0066 }
        r3 = r6.streams;	 Catch:{ all -> 0x0066 }
        r3 = r3.size();	 Catch:{ all -> 0x0066 }
        r3 = new okhttp3.internal.http2.Http2Stream[r3];	 Catch:{ all -> 0x0066 }
        r2 = r2.toArray(r3);	 Catch:{ all -> 0x0066 }
        r2 = (okhttp3.internal.http2.Http2Stream[]) r2;	 Catch:{ all -> 0x0066 }
        r1 = r2;	 Catch:{ all -> 0x0066 }
        r2 = r6.streams;	 Catch:{ all -> 0x0066 }
        r2.clear();	 Catch:{ all -> 0x0066 }
        goto L_0x002e;	 Catch:{ all -> 0x0066 }
    L_0x002e:
        monitor-exit(r6);	 Catch:{ all -> 0x0066 }
        if (r1 == 0) goto L_0x0044;
    L_0x0031:
        r2 = r1.length;
        r3 = 0;
    L_0x0033:
        if (r3 >= r2) goto L_0x0043;
    L_0x0035:
        r4 = r1[r3];
        r4.close(r8);	 Catch:{ IOException -> 0x003b }
        goto L_0x0040;
    L_0x003b:
        r5 = move-exception;
        if (r0 == 0) goto L_0x0040;
    L_0x003e:
        r0 = r5;
    L_0x0040:
        r3 = r3 + 1;
        goto L_0x0033;
    L_0x0043:
        goto L_0x0045;
    L_0x0045:
        r2 = r6.writer;	 Catch:{ IOException -> 0x004b }
        r2.close();	 Catch:{ IOException -> 0x004b }
        goto L_0x0050;
    L_0x004b:
        r2 = move-exception;
        if (r0 != 0) goto L_0x0050;
    L_0x004e:
        r0 = r2;
    L_0x0050:
        r2 = r6.socket;	 Catch:{ IOException -> 0x0056 }
        r2.close();	 Catch:{ IOException -> 0x0056 }
        goto L_0x0058;
    L_0x0056:
        r2 = move-exception;
        r0 = r2;
    L_0x0058:
        r2 = r6.writerExecutor;
        r2.shutdown();
        r2 = r6.pushExecutor;
        r2.shutdown();
        if (r0 != 0) goto L_0x0065;
    L_0x0064:
        return;
    L_0x0065:
        throw r0;
    L_0x0066:
        r2 = move-exception;
        monitor-exit(r6);	 Catch:{ all -> 0x0066 }
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.close(okhttp3.internal.http2.ErrorCode, okhttp3.internal.http2.ErrorCode):void");
    }

    Http2Connection(Builder builder) {
        Builder builder2 = builder;
        this.pushObserver = builder2.pushObserver;
        this.client = builder2.client;
        this.listener = builder2.listener;
        r0.nextStreamId = builder2.client ? 1 : 2;
        if (builder2.client) {
            r0.nextStreamId += 2;
        }
        if (builder2.client) {
            r0.okHttpSettings.set(7, 16777216);
        }
        r0.hostname = builder2.hostname;
        r0.writerExecutor = new ScheduledThreadPoolExecutor(1, Util.threadFactory(Util.format("OkHttp %s Writer", new Object[]{r0.hostname}), false));
        if (builder2.pingIntervalMillis != 0) {
            r0.writerExecutor.scheduleAtFixedRate(new PingRunnable(false, 0, 0), (long) builder2.pingIntervalMillis, (long) builder2.pingIntervalMillis, TimeUnit.MILLISECONDS);
        }
        r0.pushExecutor = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(), Util.threadFactory(Util.format("OkHttp %s Push Observer", new Object[]{r0.hostname}), true));
        r0.peerSettings.set(7, SupportMenu.USER_MASK);
        r0.peerSettings.set(5, 16384);
        r0.bytesLeftInWriteWindow = (long) r0.peerSettings.getInitialWindowSize();
        r0.socket = builder2.socket;
        r0.writer = new Http2Writer(builder2.sink, r0.client);
        r0.readerRunnable = new ReaderRunnable(new Http2Reader(builder2.source, r0.client));
    }

    public Protocol getProtocol() {
        return Protocol.HTTP_2;
    }

    public synchronized int openStreamCount() {
        return this.streams.size();
    }

    synchronized Http2Stream getStream(int id) {
        return (Http2Stream) this.streams.get(Integer.valueOf(id));
    }

    synchronized Http2Stream removeStream(int streamId) {
        Http2Stream stream;
        stream = (Http2Stream) this.streams.remove(Integer.valueOf(streamId));
        notifyAll();
        return stream;
    }

    public synchronized int maxConcurrentStreams() {
        return this.peerSettings.getMaxConcurrentStreams(Integer.MAX_VALUE);
    }

    public Http2Stream pushStream(int associatedStreamId, List<Header> requestHeaders, boolean out) throws IOException {
        if (!this.client) {
            return newStream(associatedStreamId, requestHeaders, out);
        }
        throw new IllegalStateException("Client cannot push requests.");
    }

    public Http2Stream newStream(List<Header> requestHeaders, boolean out) throws IOException {
        return newStream(0, requestHeaders, out);
    }

    private Http2Stream newStream(int associatedStreamId, List<Header> requestHeaders, boolean out) throws IOException {
        Http2Stream stream;
        boolean flushHeaders;
        boolean outFinished = out ^ 1;
        synchronized (this.writer) {
            synchronized (this) {
                if (this.nextStreamId > 1073741823) {
                    shutdown(ErrorCode.REFUSED_STREAM);
                }
                if (this.shutdown) {
                    throw new ConnectionShutdownException();
                }
                int streamId = this.nextStreamId;
                this.nextStreamId += 2;
                stream = new Http2Stream(streamId, this, outFinished, false, requestHeaders);
                if (out && this.bytesLeftInWriteWindow != 0) {
                    if (stream.bytesLeftInWriteWindow != 0) {
                        flushHeaders = false;
                        if (stream.isOpen()) {
                            this.streams.put(Integer.valueOf(streamId), stream);
                        }
                    }
                }
                flushHeaders = true;
                if (stream.isOpen()) {
                    this.streams.put(Integer.valueOf(streamId), stream);
                }
            }
            if (associatedStreamId == 0) {
                this.writer.synStream(outFinished, streamId, associatedStreamId, requestHeaders);
            } else if (this.client) {
                throw new IllegalArgumentException("client streams shouldn't have associated stream IDs");
            } else {
                this.writer.pushPromise(associatedStreamId, streamId, requestHeaders);
            }
        }
        if (flushHeaders) {
            this.writer.flush();
        }
        return stream;
    }

    void writeSynReply(int streamId, boolean outFinished, List<Header> alternating) throws IOException {
        this.writer.synReply(outFinished, streamId, alternating);
    }

    public void writeData(int streamId, boolean outFinished, Buffer buffer, long byteCount) throws IOException {
        if (byteCount == 0) {
            this.writer.data(outFinished, streamId, buffer, 0);
            return;
        }
        while (byteCount > 0) {
            int toWrite;
            synchronized (this) {
                while (this.bytesLeftInWriteWindow <= 0) {
                    try {
                        if (this.streams.containsKey(Integer.valueOf(streamId))) {
                            wait();
                        } else {
                            throw new IOException("stream closed");
                        }
                    } catch (InterruptedException e) {
                        throw new InterruptedIOException();
                    }
                }
                toWrite = Math.min((int) Math.min(byteCount, this.bytesLeftInWriteWindow), this.writer.maxDataLength());
                this.bytesLeftInWriteWindow -= (long) toWrite;
            }
            byteCount -= (long) toWrite;
            Http2Writer http2Writer = this.writer;
            boolean z = outFinished && byteCount == 0;
            http2Writer.data(z, streamId, buffer, toWrite);
        }
    }

    void addBytesToWriteWindow(long delta) {
        this.bytesLeftInWriteWindow += delta;
        if (delta > 0) {
            notifyAll();
        }
    }

    void writeSynResetLater(int streamId, ErrorCode errorCode) {
        try {
            final int i = streamId;
            final ErrorCode errorCode2 = errorCode;
            this.writerExecutor.execute(new NamedRunnable("OkHttp %s stream %d", new Object[]{this.hostname, Integer.valueOf(streamId)}) {
                public void execute() {
                    try {
                        Http2Connection.this.writeSynReset(i, errorCode2);
                    } catch (IOException e) {
                        Http2Connection.this.failConnection();
                    }
                }
            });
        } catch (RejectedExecutionException e) {
        }
    }

    void writeSynReset(int streamId, ErrorCode statusCode) throws IOException {
        this.writer.rstStream(streamId, statusCode);
    }

    void writeWindowUpdateLater(int streamId, long unacknowledgedBytesRead) {
        try {
            final int i = streamId;
            final long j = unacknowledgedBytesRead;
            this.writerExecutor.execute(new NamedRunnable("OkHttp Window Update %s stream %d", new Object[]{this.hostname, Integer.valueOf(streamId)}) {
                public void execute() {
                    try {
                        Http2Connection.this.writer.windowUpdate(i, j);
                    } catch (IOException e) {
                        Http2Connection.this.failConnection();
                    }
                }
            });
        } catch (RejectedExecutionException e) {
        }
    }

    void writePing(boolean reply, int payload1, int payload2) {
        if (!reply) {
            boolean failedDueToMissingPong;
            synchronized (this) {
                failedDueToMissingPong = this.awaitingPong;
                this.awaitingPong = true;
            }
            if (failedDueToMissingPong) {
                failConnection();
                return;
            }
        }
        try {
            this.writer.ping(reply, payload1, payload2);
        } catch (IOException e) {
            failConnection();
        }
    }

    void writePingAndAwaitPong() throws IOException, InterruptedException {
        writePing(false, 1330343787, -257978967);
        awaitPong();
    }

    public void flush() throws IOException {
        this.writer.flush();
    }

    public void shutdown(ErrorCode statusCode) throws IOException {
        synchronized (this.writer) {
            synchronized (this) {
                if (this.shutdown) {
                    return;
                }
                this.shutdown = true;
                int lastGoodStreamId = this.lastGoodStreamId;
                this.writer.goAway(lastGoodStreamId, statusCode, Util.EMPTY_BYTE_ARRAY);
            }
        }
    }

    public void close() throws IOException {
        close(ErrorCode.NO_ERROR, ErrorCode.CANCEL);
    }

    private void failConnection() {
        try {
            close(ErrorCode.PROTOCOL_ERROR, ErrorCode.PROTOCOL_ERROR);
        } catch (IOException e) {
        }
    }

    public void start() throws IOException {
        start(true);
    }

    void start(boolean sendConnectionPreface) throws IOException {
        if (sendConnectionPreface) {
            this.writer.connectionPreface();
            this.writer.settings(this.okHttpSettings);
            int windowSize = this.okHttpSettings.getInitialWindowSize();
            if (windowSize != SupportMenu.USER_MASK) {
                this.writer.windowUpdate(0, (long) (windowSize - SupportMenu.USER_MASK));
            }
        }
        new Thread(this.readerRunnable).start();
    }

    public void setSettings(Settings settings) throws IOException {
        synchronized (this.writer) {
            synchronized (this) {
                if (this.shutdown) {
                    throw new ConnectionShutdownException();
                }
                this.okHttpSettings.merge(settings);
            }
            this.writer.settings(settings);
        }
    }

    public synchronized boolean isShutdown() {
        return this.shutdown;
    }

    boolean pushedStream(int streamId) {
        return streamId != 0 && (streamId & 1) == 0;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void pushRequestLater(int r9, java.util.List<okhttp3.internal.http2.Header> r10) {
        /*
        r8 = this;
        monitor-enter(r8);
        r0 = r8.currentPushRequests;	 Catch:{ all -> 0x0040 }
        r1 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0040 }
        r0 = r0.contains(r1);	 Catch:{ all -> 0x0040 }
        if (r0 == 0) goto L_0x0014;
    L_0x000d:
        r0 = okhttp3.internal.http2.ErrorCode.PROTOCOL_ERROR;	 Catch:{ all -> 0x0040 }
        r8.writeSynResetLater(r9, r0);	 Catch:{ all -> 0x0040 }
        monitor-exit(r8);	 Catch:{ all -> 0x0040 }
        return;
    L_0x0014:
        r0 = r8.currentPushRequests;	 Catch:{ all -> 0x0040 }
        r1 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0040 }
        r0.add(r1);	 Catch:{ all -> 0x0040 }
        monitor-exit(r8);	 Catch:{ all -> 0x0040 }
        r0 = r8.pushExecutor;	 Catch:{ RejectedExecutionException -> 0x003e }
        r7 = new okhttp3.internal.http2.Http2Connection$3;	 Catch:{ RejectedExecutionException -> 0x003e }
        r3 = "OkHttp %s Push Request[%s]";
        r1 = 2;
        r4 = new java.lang.Object[r1];	 Catch:{ RejectedExecutionException -> 0x003e }
        r1 = 0;
        r2 = r8.hostname;	 Catch:{ RejectedExecutionException -> 0x003e }
        r4[r1] = r2;	 Catch:{ RejectedExecutionException -> 0x003e }
        r1 = 1;
        r2 = java.lang.Integer.valueOf(r9);	 Catch:{ RejectedExecutionException -> 0x003e }
        r4[r1] = r2;	 Catch:{ RejectedExecutionException -> 0x003e }
        r1 = r7;
        r2 = r8;
        r5 = r9;
        r6 = r10;
        r1.<init>(r3, r4, r5, r6);	 Catch:{ RejectedExecutionException -> 0x003e }
        r0.execute(r7);	 Catch:{ RejectedExecutionException -> 0x003e }
        goto L_0x003f;
    L_0x003e:
        r0 = move-exception;
    L_0x003f:
        return;
    L_0x0040:
        r0 = move-exception;
        monitor-exit(r8);	 Catch:{ all -> 0x0040 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.pushRequestLater(int, java.util.List):void");
    }

    void pushHeadersLater(int streamId, List<Header> requestHeaders, boolean inFinished) {
        try {
            final int i = streamId;
            final List<Header> list = requestHeaders;
            final boolean z = inFinished;
            this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Headers[%s]", new Object[]{this.hostname, Integer.valueOf(streamId)}) {
                public void execute() {
                    boolean cancel = Http2Connection.this.pushObserver.onHeaders(i, list, z);
                    if (cancel) {
                        try {
                            Http2Connection.this.writer.rstStream(i, ErrorCode.CANCEL);
                        } catch (IOException e) {
                            return;
                        }
                    }
                    if (!cancel) {
                        if (!z) {
                        }
                    }
                    synchronized (Http2Connection.this) {
                        Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i));
                    }
                }
            });
        } catch (RejectedExecutionException e) {
        }
    }

    void pushDataLater(int streamId, BufferedSource source, int byteCount, boolean inFinished) throws IOException {
        Buffer buffer = new Buffer();
        source.require((long) byteCount);
        source.read(buffer, (long) byteCount);
        if (buffer.size() == ((long) byteCount)) {
            final int i = streamId;
            final Buffer buffer2 = buffer;
            final int i2 = byteCount;
            final boolean z = inFinished;
            this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Data[%s]", new Object[]{this.hostname, Integer.valueOf(streamId)}) {
                public void execute() {
                    try {
                        boolean cancel = Http2Connection.this.pushObserver.onData(i, buffer2, i2, z);
                        if (cancel) {
                            Http2Connection.this.writer.rstStream(i, ErrorCode.CANCEL);
                        }
                        if (!cancel) {
                            if (!z) {
                            }
                        }
                        synchronized (Http2Connection.this) {
                            Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i));
                        }
                    } catch (IOException e) {
                    }
                }
            });
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(buffer.size());
        stringBuilder.append(" != ");
        stringBuilder.append(byteCount);
        throw new IOException(stringBuilder.toString());
    }

    void pushResetLater(int streamId, ErrorCode errorCode) {
        final int i = streamId;
        final ErrorCode errorCode2 = errorCode;
        this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Reset[%s]", new Object[]{this.hostname, Integer.valueOf(streamId)}) {
            public void execute() {
                Http2Connection.this.pushObserver.onReset(i, errorCode2);
                synchronized (Http2Connection.this) {
                    Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i));
                }
            }
        });
    }
}
