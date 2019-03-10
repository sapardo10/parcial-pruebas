package okhttp3.internal.ws;

import java.io.Closeable;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okio.ByteString;
import org.apache.commons.lang3.StringUtils;

public final class RealWebSocket implements WebSocket, WebSocketReader$FrameCallback {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final long CANCEL_AFTER_CLOSE_MILLIS = 60000;
    private static final long MAX_QUEUE_SIZE = 16777216;
    private static final List<Protocol> ONLY_HTTP1 = Collections.singletonList(Protocol.HTTP_1_1);
    private boolean awaitingPong;
    private Call call;
    private ScheduledFuture<?> cancelFuture;
    private boolean enqueuedClose;
    private ScheduledExecutorService executor;
    private boolean failed;
    private final String key;
    final WebSocketListener listener;
    private final ArrayDeque<Object> messageAndCloseQueue = new ArrayDeque();
    private final Request originalRequest;
    private final long pingIntervalMillis;
    private final ArrayDeque<ByteString> pongQueue = new ArrayDeque();
    private long queueSize;
    private final Random random;
    private WebSocketReader reader;
    private int receivedCloseCode = -1;
    private String receivedCloseReason;
    private int receivedPingCount;
    private int receivedPongCount;
    private int sentPingCount;
    private RealWebSocket$Streams streams;
    private WebSocketWriter writer;
    private final Runnable writerRunnable;

    public RealWebSocket(Request request, WebSocketListener listener, Random random, long pingIntervalMillis) {
        if ("GET".equals(request.method())) {
            this.originalRequest = request;
            this.listener = listener;
            this.random = random;
            this.pingIntervalMillis = pingIntervalMillis;
            byte[] nonce = new byte[16];
            random.nextBytes(nonce);
            this.key = ByteString.of(nonce).base64();
            this.writerRunnable = new RealWebSocket$1(this);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Request must be GET: ");
        stringBuilder.append(request.method());
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public Request request() {
        return this.originalRequest;
    }

    public synchronized long queueSize() {
        return this.queueSize;
    }

    public void cancel() {
        this.call.cancel();
    }

    public void connect(OkHttpClient client) {
        client = client.newBuilder().eventListener(EventListener.NONE).protocols(ONLY_HTTP1).build();
        Request request = this.originalRequest.newBuilder().header("Upgrade", "websocket").header("Connection", "Upgrade").header("Sec-WebSocket-Key", this.key).header("Sec-WebSocket-Version", "13").build();
        this.call = Internal.instance.newWebSocketCall(client, request);
        this.call.enqueue(new RealWebSocket$2(this, request));
    }

    void checkResponse(Response response) throws ProtocolException {
        if (response.code() == 101) {
            String headerConnection = response.header("Connection");
            if ("Upgrade".equalsIgnoreCase(headerConnection)) {
                String headerUpgrade = response.header("Upgrade");
                StringBuilder stringBuilder;
                if ("websocket".equalsIgnoreCase(headerUpgrade)) {
                    String headerAccept = response.header("Sec-WebSocket-Accept");
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(this.key);
                    stringBuilder.append("258EAFA5-E914-47DA-95CA-C5AB0DC85B11");
                    String acceptExpected = ByteString.encodeUtf8(stringBuilder.toString()).sha1().base64();
                    if (!acceptExpected.equals(headerAccept)) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Expected 'Sec-WebSocket-Accept' header value '");
                        stringBuilder2.append(acceptExpected);
                        stringBuilder2.append("' but was '");
                        stringBuilder2.append(headerAccept);
                        stringBuilder2.append("'");
                        throw new ProtocolException(stringBuilder2.toString());
                    }
                    return;
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append("Expected 'Upgrade' header value 'websocket' but was '");
                stringBuilder.append(headerUpgrade);
                stringBuilder.append("'");
                throw new ProtocolException(stringBuilder.toString());
            }
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Expected 'Connection' header value 'Upgrade' but was '");
            stringBuilder3.append(headerConnection);
            stringBuilder3.append("'");
            throw new ProtocolException(stringBuilder3.toString());
        }
        StringBuilder stringBuilder4 = new StringBuilder();
        stringBuilder4.append("Expected HTTP 101 response but was '");
        stringBuilder4.append(response.code());
        stringBuilder4.append(StringUtils.SPACE);
        stringBuilder4.append(response.message());
        stringBuilder4.append("'");
        throw new ProtocolException(stringBuilder4.toString());
    }

    public void initReaderAndWriter(String name, RealWebSocket$Streams streams) throws IOException {
        synchronized (this) {
            this.streams = streams;
            this.writer = new WebSocketWriter(streams.client, streams.sink, this.random);
            this.executor = new ScheduledThreadPoolExecutor(1, Util.threadFactory(name, false));
            if (this.pingIntervalMillis != 0) {
                this.executor.scheduleAtFixedRate(new RealWebSocket$PingRunnable(this), this.pingIntervalMillis, this.pingIntervalMillis, TimeUnit.MILLISECONDS);
            }
            if (!this.messageAndCloseQueue.isEmpty()) {
                runWriter();
            }
        }
        this.reader = new WebSocketReader(streams.client, streams.source, this);
    }

    public void loopReader() throws IOException {
        while (this.receivedCloseCode == -1) {
            this.reader.processNextFrame();
        }
    }

    boolean processNextFrame() throws IOException {
        boolean z = false;
        try {
            this.reader.processNextFrame();
            if (this.receivedCloseCode == -1) {
                z = true;
            }
            return z;
        } catch (Exception e) {
            failWebSocket(e, null);
            return false;
        }
    }

    void awaitTermination(int timeout, TimeUnit timeUnit) throws InterruptedException {
        this.executor.awaitTermination((long) timeout, timeUnit);
    }

    void tearDown() throws InterruptedException {
        ScheduledFuture scheduledFuture = this.cancelFuture;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        this.executor.shutdown();
        this.executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    synchronized int sentPingCount() {
        return this.sentPingCount;
    }

    synchronized int receivedPingCount() {
        return this.receivedPingCount;
    }

    synchronized int receivedPongCount() {
        return this.receivedPongCount;
    }

    public void onReadMessage(String text) throws IOException {
        this.listener.onMessage((WebSocket) this, text);
    }

    public void onReadMessage(ByteString bytes) throws IOException {
        this.listener.onMessage((WebSocket) this, bytes);
    }

    public synchronized void onReadPing(ByteString payload) {
        if (!this.failed) {
            if (!this.enqueuedClose || !this.messageAndCloseQueue.isEmpty()) {
                this.pongQueue.add(payload);
                runWriter();
                this.receivedPingCount++;
            }
        }
    }

    public synchronized void onReadPong(ByteString buffer) {
        this.receivedPongCount++;
        this.awaitingPong = false;
    }

    public void onReadClose(int code, String reason) {
        if (code != -1) {
            Closeable toClose = null;
            synchronized (this) {
                if (this.receivedCloseCode == -1) {
                    this.receivedCloseCode = code;
                    this.receivedCloseReason = reason;
                    if (this.enqueuedClose && this.messageAndCloseQueue.isEmpty()) {
                        toClose = this.streams;
                        this.streams = null;
                        if (this.cancelFuture != null) {
                            this.cancelFuture.cancel(false);
                        }
                        this.executor.shutdown();
                    }
                } else {
                    throw new IllegalStateException("already closed");
                }
            }
            try {
                this.listener.onClosing(this, code, reason);
                if (toClose != null) {
                    this.listener.onClosed(this, code, reason);
                }
                Util.closeQuietly(toClose);
            } catch (Throwable th) {
                Util.closeQuietly(toClose);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public boolean send(String text) {
        if (text != null) {
            return send(ByteString.encodeUtf8(text), 1);
        }
        throw new NullPointerException("text == null");
    }

    public boolean send(ByteString bytes) {
        if (bytes != null) {
            return send(bytes, 2);
        }
        throw new NullPointerException("bytes == null");
    }

    private synchronized boolean send(ByteString data, int formatOpcode) {
        if (!this.failed) {
            if (!this.enqueuedClose) {
                if (this.queueSize + ((long) data.size()) > MAX_QUEUE_SIZE) {
                    close(1001, null);
                    return false;
                }
                this.queueSize += (long) data.size();
                this.messageAndCloseQueue.add(new RealWebSocket$Message(formatOpcode, data));
                runWriter();
                return true;
            }
        }
        return false;
    }

    synchronized boolean pong(ByteString payload) {
        if (!this.failed) {
            if (!this.enqueuedClose || !this.messageAndCloseQueue.isEmpty()) {
                this.pongQueue.add(payload);
                runWriter();
                return true;
            }
        }
        return false;
    }

    public boolean close(int code, String reason) {
        return close(code, reason, 60000);
    }

    synchronized boolean close(int code, String reason, long cancelAfterCloseMillis) {
        WebSocketProtocol.validateCloseCode(code);
        ByteString reasonBytes = null;
        if (reason != null) {
            reasonBytes = ByteString.encodeUtf8(reason);
            if (((long) reasonBytes.size()) > 123) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("reason.size() > 123: ");
                stringBuilder.append(reason);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
        if (!this.failed) {
            if (!this.enqueuedClose) {
                this.enqueuedClose = true;
                this.messageAndCloseQueue.add(new RealWebSocket$Close(code, reasonBytes, cancelAfterCloseMillis));
                runWriter();
                return true;
            }
        }
        return false;
    }

    private void runWriter() {
        ScheduledExecutorService scheduledExecutorService = this.executor;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.execute(this.writerRunnable);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    boolean writeOneFrame() throws java.io.IOException {
        /*
        r12 = this;
        r0 = 0;
        r1 = -1;
        r2 = 0;
        r3 = 0;
        monitor-enter(r12);
        r4 = r12.failed;	 Catch:{ all -> 0x00b4 }
        r5 = 0;
        if (r4 == 0) goto L_0x000c;
    L_0x000a:
        monitor-exit(r12);	 Catch:{ all -> 0x00b4 }
        return r5;
    L_0x000c:
        r4 = r12.writer;	 Catch:{ all -> 0x00b4 }
        r6 = r12.pongQueue;	 Catch:{ all -> 0x00b4 }
        r6 = r6.poll();	 Catch:{ all -> 0x00b4 }
        r6 = (okio.ByteString) r6;	 Catch:{ all -> 0x00b4 }
        if (r6 != 0) goto L_0x0053;
    L_0x0018:
        r7 = r12.messageAndCloseQueue;	 Catch:{ all -> 0x00b4 }
        r7 = r7.poll();	 Catch:{ all -> 0x00b4 }
        r0 = r7;
        r7 = r0 instanceof okhttp3.internal.ws.RealWebSocket$Close;	 Catch:{ all -> 0x00b4 }
        if (r7 == 0) goto L_0x004e;
    L_0x0024:
        r5 = r12.receivedCloseCode;	 Catch:{ all -> 0x00b4 }
        r1 = r5;
        r5 = r12.receivedCloseReason;	 Catch:{ all -> 0x00b4 }
        r2 = r5;
        r5 = -1;
        if (r1 == r5) goto L_0x0039;
    L_0x002d:
        r5 = r12.streams;	 Catch:{ all -> 0x00b4 }
        r3 = r5;
        r5 = 0;
        r12.streams = r5;	 Catch:{ all -> 0x00b4 }
        r5 = r12.executor;	 Catch:{ all -> 0x00b4 }
        r5.shutdown();	 Catch:{ all -> 0x00b4 }
        goto L_0x0054;
    L_0x0039:
        r5 = r12.executor;	 Catch:{ all -> 0x00b4 }
        r7 = new okhttp3.internal.ws.RealWebSocket$CancelRunnable;	 Catch:{ all -> 0x00b4 }
        r7.<init>(r12);	 Catch:{ all -> 0x00b4 }
        r8 = r0;
        r8 = (okhttp3.internal.ws.RealWebSocket$Close) r8;	 Catch:{ all -> 0x00b4 }
        r8 = r8.cancelAfterCloseMillis;	 Catch:{ all -> 0x00b4 }
        r10 = java.util.concurrent.TimeUnit.MILLISECONDS;	 Catch:{ all -> 0x00b4 }
        r5 = r5.schedule(r7, r8, r10);	 Catch:{ all -> 0x00b4 }
        r12.cancelFuture = r5;	 Catch:{ all -> 0x00b4 }
        goto L_0x0054;
    L_0x004e:
        if (r0 != 0) goto L_0x0052;
    L_0x0050:
        monitor-exit(r12);	 Catch:{ all -> 0x00b4 }
        return r5;
    L_0x0052:
        goto L_0x0054;
    L_0x0054:
        monitor-exit(r12);	 Catch:{ all -> 0x00b4 }
        if (r6 == 0) goto L_0x005b;
    L_0x0057:
        r4.writePong(r6);	 Catch:{ all -> 0x00af }
        goto L_0x00a4;
    L_0x005b:
        r5 = r0 instanceof okhttp3.internal.ws.RealWebSocket$Message;	 Catch:{ all -> 0x00af }
        if (r5 == 0) goto L_0x008c;
    L_0x005f:
        r5 = r0;
        r5 = (okhttp3.internal.ws.RealWebSocket$Message) r5;	 Catch:{ all -> 0x00af }
        r5 = r5.data;	 Catch:{ all -> 0x00af }
        r7 = r0;
        r7 = (okhttp3.internal.ws.RealWebSocket$Message) r7;	 Catch:{ all -> 0x00af }
        r7 = r7.formatOpcode;	 Catch:{ all -> 0x00af }
        r8 = r5.size();	 Catch:{ all -> 0x00af }
        r8 = (long) r8;	 Catch:{ all -> 0x00af }
        r7 = r4.newMessageSink(r7, r8);	 Catch:{ all -> 0x00af }
        r7 = okio.Okio.buffer(r7);	 Catch:{ all -> 0x00af }
        r7.write(r5);	 Catch:{ all -> 0x00af }
        r7.close();	 Catch:{ all -> 0x00af }
        monitor-enter(r12);	 Catch:{ all -> 0x00af }
        r8 = r12.queueSize;	 Catch:{ all -> 0x0089 }
        r10 = r5.size();	 Catch:{ all -> 0x0089 }
        r10 = (long) r10;	 Catch:{ all -> 0x0089 }
        r8 = r8 - r10;
        r12.queueSize = r8;	 Catch:{ all -> 0x0089 }
        monitor-exit(r12);	 Catch:{ all -> 0x0089 }
        goto L_0x00a4;
    L_0x0089:
        r8 = move-exception;
        monitor-exit(r12);	 Catch:{ all -> 0x0089 }
        throw r8;	 Catch:{ all -> 0x00af }
    L_0x008c:
        r5 = r0 instanceof okhttp3.internal.ws.RealWebSocket$Close;	 Catch:{ all -> 0x00af }
        if (r5 == 0) goto L_0x00a9;
    L_0x0090:
        r5 = r0;
        r5 = (okhttp3.internal.ws.RealWebSocket$Close) r5;	 Catch:{ all -> 0x00af }
        r7 = r5.code;	 Catch:{ all -> 0x00af }
        r8 = r5.reason;	 Catch:{ all -> 0x00af }
        r4.writeClose(r7, r8);	 Catch:{ all -> 0x00af }
        if (r3 == 0) goto L_0x00a2;
    L_0x009c:
        r7 = r12.listener;	 Catch:{ all -> 0x00af }
        r7.onClosed(r12, r1, r2);	 Catch:{ all -> 0x00af }
        goto L_0x00a3;
    L_0x00a4:
        r5 = 1;
        okhttp3.internal.Util.closeQuietly(r3);
        return r5;
    L_0x00a9:
        r5 = new java.lang.AssertionError;	 Catch:{ all -> 0x00af }
        r5.<init>();	 Catch:{ all -> 0x00af }
        throw r5;	 Catch:{ all -> 0x00af }
    L_0x00af:
        r5 = move-exception;
        okhttp3.internal.Util.closeQuietly(r3);
        throw r5;
    L_0x00b4:
        r4 = move-exception;
        monitor-exit(r12);	 Catch:{ all -> 0x00b4 }
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.writeOneFrame():boolean");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void writePingFrame() {
        /*
        r7 = this;
        monitor-enter(r7);
        r0 = r7.failed;	 Catch:{ all -> 0x0054 }
        if (r0 == 0) goto L_0x0007;
    L_0x0005:
        monitor-exit(r7);	 Catch:{ all -> 0x0054 }
        return;
    L_0x0007:
        r0 = r7.writer;	 Catch:{ all -> 0x0054 }
        r1 = r7.awaitingPong;	 Catch:{ all -> 0x0054 }
        r2 = -1;
        if (r1 == 0) goto L_0x0011;
    L_0x000e:
        r1 = r7.sentPingCount;	 Catch:{ all -> 0x0054 }
        goto L_0x0012;
    L_0x0011:
        r1 = -1;
    L_0x0012:
        r3 = r7.sentPingCount;	 Catch:{ all -> 0x0054 }
        r4 = 1;
        r3 = r3 + r4;
        r7.sentPingCount = r3;	 Catch:{ all -> 0x0054 }
        r7.awaitingPong = r4;	 Catch:{ all -> 0x0054 }
        monitor-exit(r7);	 Catch:{ all -> 0x0054 }
        r3 = 0;
        if (r1 == r2) goto L_0x0049;
    L_0x001e:
        r2 = new java.net.SocketTimeoutException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "sent ping but didn't receive pong within ";
        r4.append(r5);
        r5 = r7.pingIntervalMillis;
        r4.append(r5);
        r5 = "ms (after ";
        r4.append(r5);
        r5 = r1 + -1;
        r4.append(r5);
        r5 = " successful ping/pongs)";
        r4.append(r5);
        r4 = r4.toString();
        r2.<init>(r4);
        r7.failWebSocket(r2, r3);
        return;
    L_0x0049:
        r2 = okio.ByteString.EMPTY;	 Catch:{ IOException -> 0x004f }
        r0.writePing(r2);	 Catch:{ IOException -> 0x004f }
        goto L_0x0053;
    L_0x004f:
        r2 = move-exception;
        r7.failWebSocket(r2, r3);
    L_0x0053:
        return;
    L_0x0054:
        r0 = move-exception;
        monitor-exit(r7);	 Catch:{ all -> 0x0054 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.writePingFrame():void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void failWebSocket(java.lang.Exception r4, @javax.annotation.Nullable okhttp3.Response r5) {
        /*
        r3 = this;
        monitor-enter(r3);
        r0 = r3.failed;	 Catch:{ all -> 0x0032 }
        if (r0 == 0) goto L_0x0007;
    L_0x0005:
        monitor-exit(r3);	 Catch:{ all -> 0x0032 }
        return;
    L_0x0007:
        r0 = 1;
        r3.failed = r0;	 Catch:{ all -> 0x0032 }
        r0 = r3.streams;	 Catch:{ all -> 0x0032 }
        r1 = 0;
        r3.streams = r1;	 Catch:{ all -> 0x0032 }
        r1 = r3.cancelFuture;	 Catch:{ all -> 0x0032 }
        if (r1 == 0) goto L_0x0019;
    L_0x0013:
        r1 = r3.cancelFuture;	 Catch:{ all -> 0x0032 }
        r2 = 0;
        r1.cancel(r2);	 Catch:{ all -> 0x0032 }
    L_0x0019:
        r1 = r3.executor;	 Catch:{ all -> 0x0032 }
        if (r1 == 0) goto L_0x0022;
    L_0x001d:
        r1 = r3.executor;	 Catch:{ all -> 0x0032 }
        r1.shutdown();	 Catch:{ all -> 0x0032 }
    L_0x0022:
        monitor-exit(r3);	 Catch:{ all -> 0x0032 }
        r1 = r3.listener;	 Catch:{ all -> 0x002d }
        r1.onFailure(r3, r4, r5);	 Catch:{ all -> 0x002d }
        okhttp3.internal.Util.closeQuietly(r0);
        return;
    L_0x002d:
        r1 = move-exception;
        okhttp3.internal.Util.closeQuietly(r0);
        throw r1;
    L_0x0032:
        r0 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0032 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.failWebSocket(java.lang.Exception, okhttp3.Response):void");
    }
}
