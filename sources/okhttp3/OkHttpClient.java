package okhttp3;

import java.net.Proxy;
import java.net.ProxySelector;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.WebSocket.Factory;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.cache.InternalCache;
import okhttp3.internal.platform.Platform;
import okhttp3.internal.tls.CertificateChainCleaner;
import okhttp3.internal.tls.OkHostnameVerifier;
import okhttp3.internal.ws.RealWebSocket;

public class OkHttpClient implements Cloneable, Call$Factory, Factory {
    static final List<ConnectionSpec> DEFAULT_CONNECTION_SPECS = Util.immutableList(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT);
    static final List<Protocol> DEFAULT_PROTOCOLS = Util.immutableList(Protocol.HTTP_2, Protocol.HTTP_1_1);
    final Authenticator authenticator;
    @Nullable
    final Cache cache;
    @Nullable
    final CertificateChainCleaner certificateChainCleaner;
    final CertificatePinner certificatePinner;
    final int connectTimeout;
    final ConnectionPool connectionPool;
    final List<ConnectionSpec> connectionSpecs;
    final CookieJar cookieJar;
    final Dispatcher dispatcher;
    final Dns dns;
    final EventListener$Factory eventListenerFactory;
    final boolean followRedirects;
    final boolean followSslRedirects;
    final HostnameVerifier hostnameVerifier;
    final List<Interceptor> interceptors;
    @Nullable
    final InternalCache internalCache;
    final List<Interceptor> networkInterceptors;
    final int pingInterval;
    final List<Protocol> protocols;
    @Nullable
    final Proxy proxy;
    final Authenticator proxyAuthenticator;
    final ProxySelector proxySelector;
    final int readTimeout;
    final boolean retryOnConnectionFailure;
    final SocketFactory socketFactory;
    @Nullable
    final SSLSocketFactory sslSocketFactory;
    final int writeTimeout;

    public static final class Builder {
        Authenticator authenticator;
        @Nullable
        Cache cache;
        @Nullable
        CertificateChainCleaner certificateChainCleaner;
        CertificatePinner certificatePinner;
        int connectTimeout;
        ConnectionPool connectionPool;
        List<ConnectionSpec> connectionSpecs;
        CookieJar cookieJar;
        Dispatcher dispatcher;
        Dns dns;
        EventListener$Factory eventListenerFactory;
        boolean followRedirects;
        boolean followSslRedirects;
        HostnameVerifier hostnameVerifier;
        final List<Interceptor> interceptors;
        @Nullable
        InternalCache internalCache;
        final List<Interceptor> networkInterceptors;
        int pingInterval;
        List<Protocol> protocols;
        @Nullable
        Proxy proxy;
        Authenticator proxyAuthenticator;
        ProxySelector proxySelector;
        int readTimeout;
        boolean retryOnConnectionFailure;
        SocketFactory socketFactory;
        @Nullable
        SSLSocketFactory sslSocketFactory;
        int writeTimeout;

        public Builder() {
            this.interceptors = new ArrayList();
            this.networkInterceptors = new ArrayList();
            this.dispatcher = new Dispatcher();
            this.protocols = OkHttpClient.DEFAULT_PROTOCOLS;
            this.connectionSpecs = OkHttpClient.DEFAULT_CONNECTION_SPECS;
            this.eventListenerFactory = EventListener.factory(EventListener.NONE);
            this.proxySelector = ProxySelector.getDefault();
            this.cookieJar = CookieJar.NO_COOKIES;
            this.socketFactory = SocketFactory.getDefault();
            this.hostnameVerifier = OkHostnameVerifier.INSTANCE;
            this.certificatePinner = CertificatePinner.DEFAULT;
            this.proxyAuthenticator = Authenticator.NONE;
            this.authenticator = Authenticator.NONE;
            this.connectionPool = new ConnectionPool();
            this.dns = Dns.SYSTEM;
            this.followSslRedirects = true;
            this.followRedirects = true;
            this.retryOnConnectionFailure = true;
            this.connectTimeout = 10000;
            this.readTimeout = 10000;
            this.writeTimeout = 10000;
            this.pingInterval = 0;
        }

        Builder(OkHttpClient okHttpClient) {
            this.interceptors = new ArrayList();
            this.networkInterceptors = new ArrayList();
            this.dispatcher = okHttpClient.dispatcher;
            this.proxy = okHttpClient.proxy;
            this.protocols = okHttpClient.protocols;
            this.connectionSpecs = okHttpClient.connectionSpecs;
            this.interceptors.addAll(okHttpClient.interceptors);
            this.networkInterceptors.addAll(okHttpClient.networkInterceptors);
            this.eventListenerFactory = okHttpClient.eventListenerFactory;
            this.proxySelector = okHttpClient.proxySelector;
            this.cookieJar = okHttpClient.cookieJar;
            this.internalCache = okHttpClient.internalCache;
            this.cache = okHttpClient.cache;
            this.socketFactory = okHttpClient.socketFactory;
            this.sslSocketFactory = okHttpClient.sslSocketFactory;
            this.certificateChainCleaner = okHttpClient.certificateChainCleaner;
            this.hostnameVerifier = okHttpClient.hostnameVerifier;
            this.certificatePinner = okHttpClient.certificatePinner;
            this.proxyAuthenticator = okHttpClient.proxyAuthenticator;
            this.authenticator = okHttpClient.authenticator;
            this.connectionPool = okHttpClient.connectionPool;
            this.dns = okHttpClient.dns;
            this.followSslRedirects = okHttpClient.followSslRedirects;
            this.followRedirects = okHttpClient.followRedirects;
            this.retryOnConnectionFailure = okHttpClient.retryOnConnectionFailure;
            this.connectTimeout = okHttpClient.connectTimeout;
            this.readTimeout = okHttpClient.readTimeout;
            this.writeTimeout = okHttpClient.writeTimeout;
            this.pingInterval = okHttpClient.pingInterval;
        }

        public Builder connectTimeout(long timeout, TimeUnit unit) {
            this.connectTimeout = Util.checkDuration("timeout", timeout, unit);
            return this;
        }

        public Builder readTimeout(long timeout, TimeUnit unit) {
            this.readTimeout = Util.checkDuration("timeout", timeout, unit);
            return this;
        }

        public Builder writeTimeout(long timeout, TimeUnit unit) {
            this.writeTimeout = Util.checkDuration("timeout", timeout, unit);
            return this;
        }

        public Builder pingInterval(long interval, TimeUnit unit) {
            this.pingInterval = Util.checkDuration("interval", interval, unit);
            return this;
        }

        public Builder proxy(@Nullable Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public Builder proxySelector(ProxySelector proxySelector) {
            this.proxySelector = proxySelector;
            return this;
        }

        public Builder cookieJar(CookieJar cookieJar) {
            if (cookieJar != null) {
                this.cookieJar = cookieJar;
                return this;
            }
            throw new NullPointerException("cookieJar == null");
        }

        void setInternalCache(@Nullable InternalCache internalCache) {
            this.internalCache = internalCache;
            this.cache = null;
        }

        public Builder cache(@Nullable Cache cache) {
            this.cache = cache;
            this.internalCache = null;
            return this;
        }

        public Builder dns(Dns dns) {
            if (dns != null) {
                this.dns = dns;
                return this;
            }
            throw new NullPointerException("dns == null");
        }

        public Builder socketFactory(SocketFactory socketFactory) {
            if (socketFactory != null) {
                this.socketFactory = socketFactory;
                return this;
            }
            throw new NullPointerException("socketFactory == null");
        }

        public Builder sslSocketFactory(SSLSocketFactory sslSocketFactory) {
            if (sslSocketFactory != null) {
                this.sslSocketFactory = sslSocketFactory;
                this.certificateChainCleaner = Platform.get().buildCertificateChainCleaner(sslSocketFactory);
                return this;
            }
            throw new NullPointerException("sslSocketFactory == null");
        }

        public Builder sslSocketFactory(SSLSocketFactory sslSocketFactory, X509TrustManager trustManager) {
            if (sslSocketFactory == null) {
                throw new NullPointerException("sslSocketFactory == null");
            } else if (trustManager != null) {
                this.sslSocketFactory = sslSocketFactory;
                this.certificateChainCleaner = CertificateChainCleaner.get(trustManager);
                return this;
            } else {
                throw new NullPointerException("trustManager == null");
            }
        }

        public Builder hostnameVerifier(HostnameVerifier hostnameVerifier) {
            if (hostnameVerifier != null) {
                this.hostnameVerifier = hostnameVerifier;
                return this;
            }
            throw new NullPointerException("hostnameVerifier == null");
        }

        public Builder certificatePinner(CertificatePinner certificatePinner) {
            if (certificatePinner != null) {
                this.certificatePinner = certificatePinner;
                return this;
            }
            throw new NullPointerException("certificatePinner == null");
        }

        public Builder authenticator(Authenticator authenticator) {
            if (authenticator != null) {
                this.authenticator = authenticator;
                return this;
            }
            throw new NullPointerException("authenticator == null");
        }

        public Builder proxyAuthenticator(Authenticator proxyAuthenticator) {
            if (proxyAuthenticator != null) {
                this.proxyAuthenticator = proxyAuthenticator;
                return this;
            }
            throw new NullPointerException("proxyAuthenticator == null");
        }

        public Builder connectionPool(ConnectionPool connectionPool) {
            if (connectionPool != null) {
                this.connectionPool = connectionPool;
                return this;
            }
            throw new NullPointerException("connectionPool == null");
        }

        public Builder followSslRedirects(boolean followProtocolRedirects) {
            this.followSslRedirects = followProtocolRedirects;
            return this;
        }

        public Builder followRedirects(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        public Builder retryOnConnectionFailure(boolean retryOnConnectionFailure) {
            this.retryOnConnectionFailure = retryOnConnectionFailure;
            return this;
        }

        public Builder dispatcher(Dispatcher dispatcher) {
            if (dispatcher != null) {
                this.dispatcher = dispatcher;
                return this;
            }
            throw new IllegalArgumentException("dispatcher == null");
        }

        public Builder protocols(List<Protocol> protocols) {
            ArrayList protocols2 = new ArrayList(protocols);
            StringBuilder stringBuilder;
            if (!protocols2.contains(Protocol.HTTP_1_1)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("protocols doesn't contain http/1.1: ");
                stringBuilder.append(protocols2);
                throw new IllegalArgumentException(stringBuilder.toString());
            } else if (protocols2.contains(Protocol.HTTP_1_0)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("protocols must not contain http/1.0: ");
                stringBuilder.append(protocols2);
                throw new IllegalArgumentException(stringBuilder.toString());
            } else if (protocols2.contains(null)) {
                throw new IllegalArgumentException("protocols must not contain null");
            } else {
                protocols2.remove(Protocol.SPDY_3);
                this.protocols = Collections.unmodifiableList(protocols2);
                return this;
            }
        }

        public Builder connectionSpecs(List<ConnectionSpec> connectionSpecs) {
            this.connectionSpecs = Util.immutableList((List) connectionSpecs);
            return this;
        }

        public List<Interceptor> interceptors() {
            return this.interceptors;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            if (interceptor != null) {
                this.interceptors.add(interceptor);
                return this;
            }
            throw new IllegalArgumentException("interceptor == null");
        }

        public List<Interceptor> networkInterceptors() {
            return this.networkInterceptors;
        }

        public Builder addNetworkInterceptor(Interceptor interceptor) {
            if (interceptor != null) {
                this.networkInterceptors.add(interceptor);
                return this;
            }
            throw new IllegalArgumentException("interceptor == null");
        }

        public Builder eventListener(EventListener eventListener) {
            if (eventListener != null) {
                this.eventListenerFactory = EventListener.factory(eventListener);
                return this;
            }
            throw new NullPointerException("eventListener == null");
        }

        public Builder eventListenerFactory(EventListener$Factory eventListenerFactory) {
            if (eventListenerFactory != null) {
                this.eventListenerFactory = eventListenerFactory;
                return this;
            }
            throw new NullPointerException("eventListenerFactory == null");
        }

        public OkHttpClient build() {
            return new OkHttpClient(this);
        }
    }

    OkHttpClient(okhttp3.OkHttpClient.Builder r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:26:0x00fb in {7, 8, 9, 10, 14, 15, 16, 21, 23, 25} preds:[]
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
        r4 = this;
        r4.<init>();
        r0 = r5.dispatcher;
        r4.dispatcher = r0;
        r0 = r5.proxy;
        r4.proxy = r0;
        r0 = r5.protocols;
        r4.protocols = r0;
        r0 = r5.connectionSpecs;
        r4.connectionSpecs = r0;
        r0 = r5.interceptors;
        r0 = okhttp3.internal.Util.immutableList(r0);
        r4.interceptors = r0;
        r0 = r5.networkInterceptors;
        r0 = okhttp3.internal.Util.immutableList(r0);
        r4.networkInterceptors = r0;
        r0 = r5.eventListenerFactory;
        r4.eventListenerFactory = r0;
        r0 = r5.proxySelector;
        r4.proxySelector = r0;
        r0 = r5.cookieJar;
        r4.cookieJar = r0;
        r0 = r5.cache;
        r4.cache = r0;
        r0 = r5.internalCache;
        r4.internalCache = r0;
        r0 = r5.socketFactory;
        r4.socketFactory = r0;
        r0 = 0;
        r1 = r4.connectionSpecs;
        r1 = r1.iterator();
    L_0x0042:
        r2 = r1.hasNext();
        if (r2 == 0) goto L_0x005c;
    L_0x0048:
        r2 = r1.next();
        r2 = (okhttp3.ConnectionSpec) r2;
        if (r0 != 0) goto L_0x0059;
    L_0x0050:
        r3 = r2.isTls();
        if (r3 == 0) goto L_0x0057;
    L_0x0056:
        goto L_0x0059;
    L_0x0057:
        r3 = 0;
        goto L_0x005a;
    L_0x0059:
        r3 = 1;
    L_0x005a:
        r0 = r3;
        goto L_0x0042;
    L_0x005c:
        r1 = r5.sslSocketFactory;
        if (r1 != 0) goto L_0x0074;
    L_0x0060:
        if (r0 != 0) goto L_0x0063;
    L_0x0062:
        goto L_0x0074;
    L_0x0063:
        r1 = r4.systemDefaultTrustManager();
        r2 = r4.systemDefaultSslSocketFactory(r1);
        r4.sslSocketFactory = r2;
        r2 = okhttp3.internal.tls.CertificateChainCleaner.get(r1);
        r4.certificateChainCleaner = r2;
        goto L_0x007d;
        r1 = r5.sslSocketFactory;
        r4.sslSocketFactory = r1;
        r1 = r5.certificateChainCleaner;
        r4.certificateChainCleaner = r1;
    L_0x007d:
        r1 = r5.hostnameVerifier;
        r4.hostnameVerifier = r1;
        r1 = r5.certificatePinner;
        r2 = r4.certificateChainCleaner;
        r1 = r1.withCertificateChainCleaner(r2);
        r4.certificatePinner = r1;
        r1 = r5.proxyAuthenticator;
        r4.proxyAuthenticator = r1;
        r1 = r5.authenticator;
        r4.authenticator = r1;
        r1 = r5.connectionPool;
        r4.connectionPool = r1;
        r1 = r5.dns;
        r4.dns = r1;
        r1 = r5.followSslRedirects;
        r4.followSslRedirects = r1;
        r1 = r5.followRedirects;
        r4.followRedirects = r1;
        r1 = r5.retryOnConnectionFailure;
        r4.retryOnConnectionFailure = r1;
        r1 = r5.connectTimeout;
        r4.connectTimeout = r1;
        r1 = r5.readTimeout;
        r4.readTimeout = r1;
        r1 = r5.writeTimeout;
        r4.writeTimeout = r1;
        r1 = r5.pingInterval;
        r4.pingInterval = r1;
        r1 = r4.interceptors;
        r2 = 0;
        r1 = r1.contains(r2);
        if (r1 != 0) goto L_0x00e2;
    L_0x00c0:
        r1 = r4.networkInterceptors;
        r1 = r1.contains(r2);
        if (r1 != 0) goto L_0x00c9;
    L_0x00c8:
        return;
    L_0x00c9:
        r1 = new java.lang.IllegalStateException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Null network interceptor: ";
        r2.append(r3);
        r3 = r4.networkInterceptors;
        r2.append(r3);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
    L_0x00e2:
        r1 = new java.lang.IllegalStateException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Null interceptor: ";
        r2.append(r3);
        r3 = r4.interceptors;
        r2.append(r3);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.OkHttpClient.<init>(okhttp3.OkHttpClient$Builder):void");
    }

    static {
        Internal.instance = new OkHttpClient$1();
    }

    public OkHttpClient() {
        this(new Builder());
    }

    private X509TrustManager systemDefaultTrustManager() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length == 1 && (trustManagers[0] instanceof X509TrustManager)) {
                return (X509TrustManager) trustManagers[0];
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unexpected default trust managers:");
            stringBuilder.append(Arrays.toString(trustManagers));
            throw new IllegalStateException(stringBuilder.toString());
        } catch (GeneralSecurityException e) {
            throw Util.assertionError("No System TLS", e);
        }
    }

    private SSLSocketFactory systemDefaultSslSocketFactory(X509TrustManager trustManager) {
        try {
            SSLContext sslContext = Platform.get().getSSLContext();
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            return sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw Util.assertionError("No System TLS", e);
        }
    }

    public int connectTimeoutMillis() {
        return this.connectTimeout;
    }

    public int readTimeoutMillis() {
        return this.readTimeout;
    }

    public int writeTimeoutMillis() {
        return this.writeTimeout;
    }

    public int pingIntervalMillis() {
        return this.pingInterval;
    }

    public Proxy proxy() {
        return this.proxy;
    }

    public ProxySelector proxySelector() {
        return this.proxySelector;
    }

    public CookieJar cookieJar() {
        return this.cookieJar;
    }

    public Cache cache() {
        return this.cache;
    }

    InternalCache internalCache() {
        Cache cache = this.cache;
        return cache != null ? cache.internalCache : this.internalCache;
    }

    public Dns dns() {
        return this.dns;
    }

    public SocketFactory socketFactory() {
        return this.socketFactory;
    }

    public SSLSocketFactory sslSocketFactory() {
        return this.sslSocketFactory;
    }

    public HostnameVerifier hostnameVerifier() {
        return this.hostnameVerifier;
    }

    public CertificatePinner certificatePinner() {
        return this.certificatePinner;
    }

    public Authenticator authenticator() {
        return this.authenticator;
    }

    public Authenticator proxyAuthenticator() {
        return this.proxyAuthenticator;
    }

    public ConnectionPool connectionPool() {
        return this.connectionPool;
    }

    public boolean followSslRedirects() {
        return this.followSslRedirects;
    }

    public boolean followRedirects() {
        return this.followRedirects;
    }

    public boolean retryOnConnectionFailure() {
        return this.retryOnConnectionFailure;
    }

    public Dispatcher dispatcher() {
        return this.dispatcher;
    }

    public List<Protocol> protocols() {
        return this.protocols;
    }

    public List<ConnectionSpec> connectionSpecs() {
        return this.connectionSpecs;
    }

    public List<Interceptor> interceptors() {
        return this.interceptors;
    }

    public List<Interceptor> networkInterceptors() {
        return this.networkInterceptors;
    }

    public EventListener$Factory eventListenerFactory() {
        return this.eventListenerFactory;
    }

    public Call newCall(Request request) {
        return RealCall.newRealCall(this, request, false);
    }

    public WebSocket newWebSocket(Request request, WebSocketListener listener) {
        RealWebSocket webSocket = new RealWebSocket(request, listener, new Random(), (long) this.pingInterval);
        webSocket.connect(this);
        return webSocket;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }
}
