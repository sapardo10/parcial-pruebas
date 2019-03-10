package okhttp3.internal.connection;

import io.reactivex.annotations.SchedulerSupport;
import java.io.IOException;
import java.lang.ref.Reference;
import java.net.ConnectException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import okhttp3.Address;
import okhttp3.Call;
import okhttp3.CertificatePinner;
import okhttp3.Connection;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.Interceptor$Chain;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.Version;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http1.Http1Codec;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Http2Codec;
import okhttp3.internal.http2.Http2Connection;
import okhttp3.internal.http2.Http2Connection$Listener;
import okhttp3.internal.http2.Http2Connection.Builder;
import okhttp3.internal.http2.Http2Stream;
import okhttp3.internal.platform.Platform;
import okhttp3.internal.tls.OkHostnameVerifier;
import okhttp3.internal.ws.RealWebSocket$Streams;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public final class RealConnection extends Http2Connection$Listener implements Connection {
    private static final int MAX_TUNNEL_ATTEMPTS = 21;
    private static final String NPE_THROW_WITH_NULL = "throw with null exception";
    public int allocationLimit = 1;
    public final List<Reference<StreamAllocation>> allocations = new ArrayList();
    private final ConnectionPool connectionPool;
    private Handshake handshake;
    private Http2Connection http2Connection;
    public long idleAtNanos = Long.MAX_VALUE;
    public boolean noNewStreams;
    private Protocol protocol;
    private Socket rawSocket;
    private final Route route;
    private BufferedSink sink;
    private Socket socket;
    private BufferedSource source;
    public int successCount;

    private okhttp3.Request createTunnel(int r10, int r11, okhttp3.Request r12, okhttp3.HttpUrl r13) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:26:0x00e6 in {3, 4, 13, 14, 16, 18, 23, 25} preds:[]
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
        r9 = this;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "CONNECT ";
        r0.append(r1);
        r1 = 1;
        r1 = okhttp3.internal.Util.hostHeader(r13, r1);
        r0.append(r1);
        r1 = " HTTP/1.1";
        r0.append(r1);
        r0 = r0.toString();
    L_0x001b:
        r1 = new okhttp3.internal.http1.Http1Codec;
        r2 = r9.source;
        r3 = r9.sink;
        r4 = 0;
        r1.<init>(r4, r4, r2, r3);
        r2 = r9.source;
        r2 = r2.timeout();
        r5 = (long) r10;
        r3 = java.util.concurrent.TimeUnit.MILLISECONDS;
        r2.timeout(r5, r3);
        r2 = r9.sink;
        r2 = r2.timeout();
        r5 = (long) r11;
        r3 = java.util.concurrent.TimeUnit.MILLISECONDS;
        r2.timeout(r5, r3);
        r2 = r12.headers();
        r1.writeRequest(r2, r0);
        r1.finishRequest();
        r2 = 0;
        r2 = r1.readResponseHeaders(r2);
        r2 = r2.request(r12);
        r2 = r2.build();
        r5 = okhttp3.internal.http.HttpHeaders.contentLength(r2);
        r7 = -1;
        r3 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r3 != 0) goto L_0x0061;
    L_0x005e:
        r5 = 0;
        goto L_0x0062;
    L_0x0062:
        r3 = r1.newFixedLengthSource(r5);
        r7 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r8 = java.util.concurrent.TimeUnit.MILLISECONDS;
        okhttp3.internal.Util.skipAll(r3, r7, r8);
        r3.close();
        r7 = r2.code();
        r8 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r7 == r8) goto L_0x00c4;
    L_0x0079:
        r4 = 407; // 0x197 float:5.7E-43 double:2.01E-321;
        if (r7 != r4) goto L_0x00a9;
    L_0x007d:
        r4 = r9.route;
        r4 = r4.address();
        r4 = r4.proxyAuthenticator();
        r7 = r9.route;
        r12 = r4.authenticate(r7, r2);
        if (r12 == 0) goto L_0x00a1;
    L_0x008f:
        r4 = "close";
        r7 = "Connection";
        r7 = r2.header(r7);
        r4 = r4.equalsIgnoreCase(r7);
        if (r4 == 0) goto L_0x009e;
    L_0x009d:
        return r12;
        goto L_0x001b;
    L_0x00a1:
        r4 = new java.io.IOException;
        r7 = "Failed to authenticate with proxy";
        r4.<init>(r7);
        throw r4;
    L_0x00a9:
        r4 = new java.io.IOException;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "Unexpected response code for CONNECT: ";
        r7.append(r8);
        r8 = r2.code();
        r7.append(r8);
        r7 = r7.toString();
        r4.<init>(r7);
        throw r4;
    L_0x00c4:
        r7 = r9.source;
        r7 = r7.buffer();
        r7 = r7.exhausted();
        if (r7 == 0) goto L_0x00dd;
    L_0x00d0:
        r7 = r9.sink;
        r7 = r7.buffer();
        r7 = r7.exhausted();
        if (r7 == 0) goto L_0x00dd;
    L_0x00dc:
        return r4;
        r4 = new java.io.IOException;
        r7 = "TLS tunnel buffered too many bytes!";
        r4.<init>(r7);
        throw r4;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.RealConnection.createTunnel(int, int, okhttp3.Request, okhttp3.HttpUrl):okhttp3.Request");
    }

    public void connect(int r17, int r18, int r19, int r20, boolean r21, okhttp3.Call r22, okhttp3.EventListener r23) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:68:0x014d in {8, 10, 12, 13, 20, 21, 24, 28, 33, 35, 43, 46, 47, 48, 50, 52, 54, 55, 58, 59, 63, 65, 67} preds:[]
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
        r16 = this;
        r7 = r16;
        r8 = r22;
        r9 = r23;
        r0 = r7.protocol;
        if (r0 != 0) goto L_0x013f;
    L_0x000a:
        r0 = 0;
        r1 = r7.route;
        r1 = r1.address();
        r10 = r1.connectionSpecs();
        r1 = new okhttp3.internal.connection.ConnectionSpecSelector;
        r1.<init>(r10);
        r11 = r1;
        r1 = r7.route;
        r1 = r1.address();
        r1 = r1.sslSocketFactory();
        if (r1 != 0) goto L_0x0076;
    L_0x0027:
        r1 = okhttp3.ConnectionSpec.CLEARTEXT;
        r1 = r10.contains(r1);
        if (r1 == 0) goto L_0x0069;
    L_0x002f:
        r1 = r7.route;
        r1 = r1.address();
        r1 = r1.url();
        r1 = r1.host();
        r2 = okhttp3.internal.platform.Platform.get();
        r2 = r2.isCleartextTrafficPermitted(r1);
        if (r2 == 0) goto L_0x0048;
    L_0x0047:
        goto L_0x0077;
    L_0x0048:
        r2 = new okhttp3.internal.connection.RouteException;
        r3 = new java.net.UnknownServiceException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "CLEARTEXT communication to ";
        r4.append(r5);
        r4.append(r1);
        r5 = " not permitted by network security policy";
        r4.append(r5);
        r4 = r4.toString();
        r3.<init>(r4);
        r2.<init>(r3);
        throw r2;
    L_0x0069:
        r1 = new okhttp3.internal.connection.RouteException;
        r2 = new java.net.UnknownServiceException;
        r3 = "CLEARTEXT communication not enabled for client";
        r2.<init>(r3);
        r1.<init>(r2);
        throw r1;
    L_0x0077:
        r12 = r0;
    L_0x0078:
        r0 = r7.route;	 Catch:{ IOException -> 0x00f2 }
        r0 = r0.requiresTunnel();	 Catch:{ IOException -> 0x00f2 }
        if (r0 == 0) goto L_0x009f;	 Catch:{ IOException -> 0x00f2 }
    L_0x0080:
        r1 = r16;	 Catch:{ IOException -> 0x00f2 }
        r2 = r17;	 Catch:{ IOException -> 0x00f2 }
        r3 = r18;	 Catch:{ IOException -> 0x00f2 }
        r4 = r19;	 Catch:{ IOException -> 0x00f2 }
        r5 = r22;	 Catch:{ IOException -> 0x00f2 }
        r6 = r23;	 Catch:{ IOException -> 0x00f2 }
        r1.connectTunnel(r2, r3, r4, r5, r6);	 Catch:{ IOException -> 0x00f2 }
        r0 = r7.rawSocket;	 Catch:{ IOException -> 0x00f2 }
        if (r0 != 0) goto L_0x009a;
    L_0x0093:
        r13 = r17;
        r14 = r18;
        r15 = r20;
        goto L_0x00bd;
    L_0x009a:
        r13 = r17;
        r14 = r18;
        goto L_0x00a6;
    L_0x009f:
        r13 = r17;
        r14 = r18;
        r7.connectSocket(r13, r14, r8, r9);	 Catch:{ IOException -> 0x00f0 }
    L_0x00a6:
        r15 = r20;
        r7.establishProtocol(r11, r15, r8, r9);	 Catch:{ IOException -> 0x00ee }
        r0 = r7.route;	 Catch:{ IOException -> 0x00ee }
        r0 = r0.socketAddress();	 Catch:{ IOException -> 0x00ee }
        r1 = r7.route;	 Catch:{ IOException -> 0x00ee }
        r1 = r1.proxy();	 Catch:{ IOException -> 0x00ee }
        r2 = r7.protocol;	 Catch:{ IOException -> 0x00ee }
        r9.connectEnd(r8, r0, r1, r2);	 Catch:{ IOException -> 0x00ee }
    L_0x00bd:
        r0 = r7.route;
        r0 = r0.requiresTunnel();
        if (r0 == 0) goto L_0x00d7;
    L_0x00c5:
        r0 = r7.rawSocket;
        if (r0 == 0) goto L_0x00ca;
    L_0x00c9:
        goto L_0x00d7;
    L_0x00ca:
        r0 = new java.net.ProtocolException;
        r1 = "Too many tunnel connections attempted: 21";
        r0.<init>(r1);
        r1 = new okhttp3.internal.connection.RouteException;
        r1.<init>(r0);
        throw r1;
        r0 = r7.http2Connection;
        if (r0 == 0) goto L_0x00ec;
    L_0x00dc:
        r1 = r7.connectionPool;
        monitor-enter(r1);
        r0 = r7.http2Connection;	 Catch:{ all -> 0x00e9 }
        r0 = r0.maxConcurrentStreams();	 Catch:{ all -> 0x00e9 }
        r7.allocationLimit = r0;	 Catch:{ all -> 0x00e9 }
        monitor-exit(r1);	 Catch:{ all -> 0x00e9 }
        goto L_0x00ed;	 Catch:{ all -> 0x00e9 }
    L_0x00e9:
        r0 = move-exception;	 Catch:{ all -> 0x00e9 }
        monitor-exit(r1);	 Catch:{ all -> 0x00e9 }
        throw r0;
    L_0x00ed:
        return;
    L_0x00ee:
        r0 = move-exception;
        goto L_0x00f9;
    L_0x00f0:
        r0 = move-exception;
        goto L_0x00f7;
    L_0x00f2:
        r0 = move-exception;
        r13 = r17;
        r14 = r18;
    L_0x00f7:
        r15 = r20;
    L_0x00f9:
        r1 = r7.socket;
        okhttp3.internal.Util.closeQuietly(r1);
        r1 = r7.rawSocket;
        okhttp3.internal.Util.closeQuietly(r1);
        r1 = 0;
        r7.socket = r1;
        r7.rawSocket = r1;
        r7.source = r1;
        r7.sink = r1;
        r7.handshake = r1;
        r7.protocol = r1;
        r7.http2Connection = r1;
        r1 = r7.route;
        r3 = r1.socketAddress();
        r1 = r7.route;
        r4 = r1.proxy();
        r5 = 0;
        r1 = r23;
        r2 = r22;
        r6 = r0;
        r1.connectFailed(r2, r3, r4, r5, r6);
        if (r12 != 0) goto L_0x0130;
    L_0x0129:
        r1 = new okhttp3.internal.connection.RouteException;
        r1.<init>(r0);
        r12 = r1;
        goto L_0x0133;
    L_0x0130:
        r12.addConnectException(r0);
    L_0x0133:
        if (r21 == 0) goto L_0x013d;
    L_0x0135:
        r1 = r11.connectionFailed(r0);
        if (r1 == 0) goto L_0x013d;
    L_0x013b:
        goto L_0x0078;
        throw r12;
    L_0x013f:
        r13 = r17;
        r14 = r18;
        r15 = r20;
        r0 = new java.lang.IllegalStateException;
        r1 = "already connected";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.RealConnection.connect(int, int, int, int, boolean, okhttp3.Call, okhttp3.EventListener):void");
    }

    public RealConnection(ConnectionPool connectionPool, Route route) {
        this.connectionPool = connectionPool;
        this.route = route;
    }

    public static RealConnection testConnection(ConnectionPool connectionPool, Route route, Socket socket, long idleAtNanos) {
        RealConnection result = new RealConnection(connectionPool, route);
        result.socket = socket;
        result.idleAtNanos = idleAtNanos;
        return result;
    }

    private void connectTunnel(int connectTimeout, int readTimeout, int writeTimeout, Call call, EventListener eventListener) throws IOException {
        Request tunnelRequest = createTunnelRequest();
        HttpUrl url = tunnelRequest.url();
        int i = 0;
        while (i < 21) {
            connectSocket(connectTimeout, readTimeout, call, eventListener);
            tunnelRequest = createTunnel(readTimeout, writeTimeout, tunnelRequest, url);
            if (tunnelRequest != null) {
                Util.closeQuietly(this.rawSocket);
                this.rawSocket = null;
                this.sink = null;
                this.source = null;
                eventListener.connectEnd(call, this.route.socketAddress(), this.route.proxy(), null);
                i++;
            } else {
                return;
            }
        }
    }

    private void connectSocket(int connectTimeout, int readTimeout, Call call, EventListener eventListener) throws IOException {
        Socket socket;
        Proxy proxy = this.route.proxy();
        Address address = this.route.address();
        if (proxy.type() != Type.DIRECT) {
            if (proxy.type() != Type.HTTP) {
                socket = new Socket(proxy);
                this.rawSocket = socket;
                eventListener.connectStart(call, this.route.socketAddress(), proxy);
                this.rawSocket.setSoTimeout(readTimeout);
                Platform.get().connectSocket(this.rawSocket, this.route.socketAddress(), connectTimeout);
                this.source = Okio.buffer(Okio.source(this.rawSocket));
                this.sink = Okio.buffer(Okio.sink(this.rawSocket));
            }
        }
        socket = address.socketFactory().createSocket();
        this.rawSocket = socket;
        eventListener.connectStart(call, this.route.socketAddress(), proxy);
        this.rawSocket.setSoTimeout(readTimeout);
        try {
            Platform.get().connectSocket(this.rawSocket, this.route.socketAddress(), connectTimeout);
            try {
                this.source = Okio.buffer(Okio.source(this.rawSocket));
                this.sink = Okio.buffer(Okio.sink(this.rawSocket));
            } catch (NullPointerException npe) {
                if (NPE_THROW_WITH_NULL.equals(npe.getMessage())) {
                    throw new IOException(npe);
                }
            }
        } catch (ConnectException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to connect to ");
            stringBuilder.append(this.route.socketAddress());
            ConnectException ce = new ConnectException(stringBuilder.toString());
            ce.initCause(e);
            throw ce;
        }
    }

    private void establishProtocol(ConnectionSpecSelector connectionSpecSelector, int pingIntervalMillis, Call call, EventListener eventListener) throws IOException {
        if (this.route.address().sslSocketFactory() == null) {
            this.protocol = Protocol.HTTP_1_1;
            this.socket = this.rawSocket;
            return;
        }
        eventListener.secureConnectStart(call);
        connectTls(connectionSpecSelector);
        eventListener.secureConnectEnd(call, this.handshake);
        if (this.protocol == Protocol.HTTP_2) {
            this.socket.setSoTimeout(0);
            this.http2Connection = new Builder(true).socket(this.socket, this.route.address().url().host(), this.source, this.sink).listener(this).pingIntervalMillis(pingIntervalMillis).build();
            this.http2Connection.start();
        }
    }

    private void connectTls(ConnectionSpecSelector connectionSpecSelector) throws IOException {
        Address address = this.route.address();
        Socket sslSocket = null;
        try {
            sslSocket = (SSLSocket) address.sslSocketFactory().createSocket(this.rawSocket, address.url().host(), address.url().port(), true);
            ConnectionSpec connectionSpec = connectionSpecSelector.configureSecureSocket(sslSocket);
            if (connectionSpec.supportsTlsExtensions()) {
                Platform.get().configureTlsExtensions(sslSocket, address.url().host(), address.protocols());
            }
            sslSocket.startHandshake();
            SSLSession sslSocketSession = sslSocket.getSession();
            if (isValid(sslSocketSession)) {
                Handshake unverifiedHandshake = Handshake.get(sslSocketSession);
                if (address.hostnameVerifier().verify(address.url().host(), sslSocketSession)) {
                    String maybeProtocol;
                    Protocol protocol;
                    address.certificatePinner().check(address.url().host(), unverifiedHandshake.peerCertificates());
                    if (connectionSpec.supportsTlsExtensions()) {
                        maybeProtocol = Platform.get().getSelectedProtocol(sslSocket);
                    } else {
                        maybeProtocol = null;
                    }
                    this.socket = sslSocket;
                    this.source = Okio.buffer(Okio.source(this.socket));
                    this.sink = Okio.buffer(Okio.sink(this.socket));
                    this.handshake = unverifiedHandshake;
                    if (maybeProtocol != null) {
                        protocol = Protocol.get(maybeProtocol);
                    } else {
                        protocol = Protocol.HTTP_1_1;
                    }
                    this.protocol = protocol;
                    if (sslSocket != null) {
                        Platform.get().afterHandshake(sslSocket);
                    }
                    if (!true) {
                        Util.closeQuietly(sslSocket);
                        return;
                    }
                    return;
                }
                X509Certificate cert = (X509Certificate) unverifiedHandshake.peerCertificates().get(0);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Hostname ");
                stringBuilder.append(address.url().host());
                stringBuilder.append(" not verified:\n    certificate: ");
                stringBuilder.append(CertificatePinner.pin(cert));
                stringBuilder.append("\n    DN: ");
                stringBuilder.append(cert.getSubjectDN().getName());
                stringBuilder.append("\n    subjectAltNames: ");
                stringBuilder.append(OkHostnameVerifier.allSubjectAltNames(cert));
                throw new SSLPeerUnverifiedException(stringBuilder.toString());
            }
            throw new IOException("a valid ssl session was not established");
        } catch (AssertionError e) {
            if (Util.isAndroidGetsocknameError(e)) {
                throw new IOException(e);
            }
            throw e;
        } catch (Throwable th) {
            if (sslSocket != null) {
                Platform.get().afterHandshake(sslSocket);
            }
            if (!false) {
                Util.closeQuietly(sslSocket);
            }
        }
    }

    private boolean isValid(SSLSession sslSocketSession) {
        return ("NONE".equals(sslSocketSession.getProtocol()) || "SSL_NULL_WITH_NULL_NULL".equals(sslSocketSession.getCipherSuite())) ? false : true;
    }

    private Request createTunnelRequest() {
        return new Request.Builder().url(this.route.address().url()).header("Host", Util.hostHeader(this.route.address().url(), true)).header("Proxy-Connection", "Keep-Alive").header("User-Agent", Version.userAgent()).build();
    }

    public boolean isEligible(Address address, @Nullable Route route) {
        if (this.allocations.size() < this.allocationLimit) {
            if (!this.noNewStreams) {
                if (!Internal.instance.equalsNonHost(this.route.address(), address)) {
                    return false;
                }
                if (address.url().host().equals(route().address().url().host())) {
                    return true;
                }
                if (this.http2Connection == null || route == null || route.proxy().type() != Type.DIRECT || this.route.proxy().type() != Type.DIRECT || !this.route.socketAddress().equals(route.socketAddress()) || route.address().hostnameVerifier() != OkHostnameVerifier.INSTANCE || !supportsUrl(address.url())) {
                    return false;
                }
                try {
                    address.certificatePinner().check(address.url().host(), handshake().peerCertificates());
                    return true;
                } catch (SSLPeerUnverifiedException e) {
                    return false;
                }
            }
        }
        return false;
    }

    public boolean supportsUrl(HttpUrl url) {
        if (url.port() != this.route.address().url().port()) {
            return false;
        }
        boolean z = true;
        if (url.host().equals(this.route.address().url().host())) {
            return true;
        }
        if (this.handshake == null || !OkHostnameVerifier.INSTANCE.verify(url.host(), (X509Certificate) this.handshake.peerCertificates().get(0))) {
            z = false;
        }
        return z;
    }

    public HttpCodec newCodec(OkHttpClient client, Interceptor$Chain chain, StreamAllocation streamAllocation) throws SocketException {
        Http2Connection http2Connection = this.http2Connection;
        if (http2Connection != null) {
            return new Http2Codec(client, chain, streamAllocation, http2Connection);
        }
        this.socket.setSoTimeout(chain.readTimeoutMillis());
        this.source.timeout().timeout((long) chain.readTimeoutMillis(), TimeUnit.MILLISECONDS);
        this.sink.timeout().timeout((long) chain.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
        return new Http1Codec(client, streamAllocation, this.source, this.sink);
    }

    public RealWebSocket$Streams newWebSocketStreams(StreamAllocation streamAllocation) {
        return new RealConnection$1(this, true, this.source, this.sink, streamAllocation);
    }

    public Route route() {
        return this.route;
    }

    public void cancel() {
        Util.closeQuietly(this.rawSocket);
    }

    public Socket socket() {
        return this.socket;
    }

    public boolean isHealthy(boolean doExtensiveChecks) {
        if (!(this.socket.isClosed() || this.socket.isInputShutdown())) {
            if (!this.socket.isOutputShutdown()) {
                Http2Connection http2Connection = this.http2Connection;
                if (http2Connection != null) {
                    return http2Connection.isShutdown() ^ true;
                }
                if (!doExtensiveChecks) {
                    return true;
                }
                int readTimeout;
                try {
                    readTimeout = this.socket.getSoTimeout();
                    this.socket.setSoTimeout(1);
                    if (this.source.exhausted()) {
                        this.socket.setSoTimeout(readTimeout);
                        return false;
                    }
                    this.socket.setSoTimeout(readTimeout);
                    return true;
                } catch (SocketTimeoutException e) {
                } catch (IOException e2) {
                    return false;
                } catch (Throwable th) {
                    this.socket.setSoTimeout(readTimeout);
                }
            }
        }
        return false;
    }

    public void onStream(Http2Stream stream) throws IOException {
        stream.close(ErrorCode.REFUSED_STREAM);
    }

    public void onSettings(Http2Connection connection) {
        synchronized (this.connectionPool) {
            this.allocationLimit = connection.maxConcurrentStreams();
        }
    }

    public Handshake handshake() {
        return this.handshake;
    }

    public boolean isMultiplexed() {
        return this.http2Connection != null;
    }

    public Protocol protocol() {
        return this.protocol;
    }

    public String toString() {
        Object cipherSuite;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Connection{");
        stringBuilder.append(this.route.address().url().host());
        stringBuilder.append(":");
        stringBuilder.append(this.route.address().url().port());
        stringBuilder.append(", proxy=");
        stringBuilder.append(this.route.proxy());
        stringBuilder.append(" hostAddress=");
        stringBuilder.append(this.route.socketAddress());
        stringBuilder.append(" cipherSuite=");
        Handshake handshake = this.handshake;
        if (handshake != null) {
            cipherSuite = handshake.cipherSuite();
        } else {
            cipherSuite = SchedulerSupport.NONE;
        }
        stringBuilder.append(cipherSuite);
        stringBuilder.append(" protocol=");
        stringBuilder.append(this.protocol);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
