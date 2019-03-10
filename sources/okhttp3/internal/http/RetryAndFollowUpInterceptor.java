package okhttp3.internal.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.Address;
import okhttp3.CertificatePinner;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.connection.StreamAllocation;

public final class RetryAndFollowUpInterceptor implements Interceptor {
    private static final int MAX_FOLLOW_UPS = 20;
    private Object callStackTrace;
    private volatile boolean canceled;
    private final OkHttpClient client;
    private final boolean forWebSocket;
    private volatile StreamAllocation streamAllocation;

    public okhttp3.Response intercept(okhttp3.Interceptor$Chain r20) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:69:0x0170 in {8, 9, 11, 12, 17, 18, 19, 26, 29, 31, 33, 35, 37, 43, 48, 50, 55, 56, 57, 58, 61, 62, 64, 65, 66, 68} preds:[]
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
        r19 = this;
        r1 = r19;
        r0 = r20.request();
        r2 = r20;
        r2 = (okhttp3.internal.http.RealInterceptorChain) r2;
        r9 = r2.call();
        r10 = r2.eventListener();
        r11 = new okhttp3.internal.connection.StreamAllocation;
        r3 = r1.client;
        r4 = r3.connectionPool();
        r3 = r0.url();
        r5 = r1.createAddress(r3);
        r8 = r1.callStackTrace;
        r3 = r11;
        r6 = r9;
        r7 = r10;
        r3.<init>(r4, r5, r6, r7, r8);
        r1.streamAllocation = r3;
        r4 = 0;
        r5 = 0;
        r12 = r0;
        r13 = r5;
    L_0x0030:
        r0 = r1.canceled;
        if (r0 != 0) goto L_0x0163;
    L_0x0034:
        r3 = 1;
        r5 = 0;
        r6 = 0;
        r0 = r2.proceed(r12, r11, r6, r6);	 Catch:{ RouteException -> 0x0134, IOException -> 0x011b, all -> 0x0117 }
        r14 = 0;
        if (r14 == 0) goto L_0x0045;
    L_0x003e:
        r11.streamFailed(r6);
        r11.release();
        goto L_0x0046;
    L_0x0046:
        if (r13 == 0) goto L_0x0061;
    L_0x0048:
        r3 = r0.newBuilder();
        r5 = r13.newBuilder();
        r5 = r5.body(r6);
        r5 = r5.build();
        r3 = r3.priorResponse(r5);
        r0 = r3.build();
        goto L_0x0062;
    L_0x0062:
        r3 = r11.route();
        r15 = r1.followUpRequest(r0, r3);
        if (r15 != 0) goto L_0x0076;
    L_0x006c:
        r3 = r1.forWebSocket;
        if (r3 != 0) goto L_0x0074;
    L_0x0070:
        r11.release();
        goto L_0x0075;
    L_0x0075:
        return r0;
    L_0x0076:
        r3 = r0.body();
        okhttp3.internal.Util.closeQuietly(r3);
        r8 = r4 + 1;
        r3 = 20;
        if (r8 > r3) goto L_0x00fa;
    L_0x0083:
        r3 = r15.body();
        r3 = r3 instanceof okhttp3.internal.http.UnrepeatableRequestBody;
        if (r3 != 0) goto L_0x00e8;
    L_0x008b:
        r3 = r15.url();
        r3 = r1.sameConnection(r0, r3);
        if (r3 != 0) goto L_0x00bc;
    L_0x0095:
        r11.release();
        r16 = new okhttp3.internal.connection.StreamAllocation;
        r3 = r1.client;
        r4 = r3.connectionPool();
        r3 = r15.url();
        r5 = r1.createAddress(r3);
        r7 = r1.callStackTrace;
        r3 = r16;
        r6 = r9;
        r17 = r7;
        r7 = r10;
        r18 = r2;
        r2 = r8;
        r8 = r17;
        r3.<init>(r4, r5, r6, r7, r8);
        r1.streamAllocation = r3;
        r11 = r3;
        goto L_0x00c5;
    L_0x00bc:
        r18 = r2;
        r2 = r8;
        r3 = r11.codec();
        if (r3 != 0) goto L_0x00cc;
    L_0x00c5:
        r12 = r15;
        r13 = r0;
        r4 = r2;
        r2 = r18;
        goto L_0x0030;
    L_0x00cc:
        r3 = new java.lang.IllegalStateException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Closing the body of ";
        r4.append(r5);
        r4.append(r0);
        r5 = " didn't close its backing stream. Bad interceptor?";
        r4.append(r5);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
    L_0x00e8:
        r18 = r2;
        r2 = r8;
        r11.release();
        r3 = new java.net.HttpRetryException;
        r4 = r0.code();
        r5 = "Cannot retry streamed HTTP body";
        r3.<init>(r5, r4);
        throw r3;
    L_0x00fa:
        r18 = r2;
        r2 = r8;
        r11.release();
        r3 = new java.net.ProtocolException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Too many follow-up requests: ";
        r4.append(r5);
        r4.append(r2);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
    L_0x0117:
        r0 = move-exception;
        r18 = r2;
        goto L_0x0158;
    L_0x011b:
        r0 = move-exception;
        r18 = r2;
        r2 = r0;
        r0 = r2;
        r2 = r0 instanceof okhttp3.internal.http2.ConnectionShutdownException;	 Catch:{ all -> 0x0157 }
        if (r2 != 0) goto L_0x0127;	 Catch:{ all -> 0x0157 }
    L_0x0125:
        r5 = 1;	 Catch:{ all -> 0x0157 }
    L_0x0127:
        r2 = r5;	 Catch:{ all -> 0x0157 }
        r5 = r1.recover(r0, r11, r2, r12);	 Catch:{ all -> 0x0157 }
        if (r5 == 0) goto L_0x0132;	 Catch:{ all -> 0x0157 }
    L_0x012e:
        r3 = 0;	 Catch:{ all -> 0x0157 }
        if (r3 == 0) goto L_0x014d;	 Catch:{ all -> 0x0157 }
    L_0x0131:
        goto L_0x0146;	 Catch:{ all -> 0x0157 }
        throw r0;	 Catch:{ all -> 0x0157 }
    L_0x0134:
        r0 = move-exception;	 Catch:{ all -> 0x0157 }
        r18 = r2;	 Catch:{ all -> 0x0157 }
        r2 = r0;	 Catch:{ all -> 0x0157 }
        r0 = r2;	 Catch:{ all -> 0x0157 }
        r2 = r0.getLastConnectException();	 Catch:{ all -> 0x0157 }
        r2 = r1.recover(r2, r11, r5, r12);	 Catch:{ all -> 0x0157 }
        if (r2 == 0) goto L_0x0152;
    L_0x0143:
        r2 = 0;
        if (r2 == 0) goto L_0x014d;
    L_0x0146:
        r11.streamFailed(r6);
        r11.release();
        goto L_0x014e;
    L_0x014e:
        r2 = r18;
        goto L_0x0030;
    L_0x0152:
        r2 = r0.getLastConnectException();	 Catch:{ all -> 0x0157 }
        throw r2;	 Catch:{ all -> 0x0157 }
    L_0x0157:
        r0 = move-exception;
    L_0x0158:
        if (r3 == 0) goto L_0x0161;
    L_0x015a:
        r11.streamFailed(r6);
        r11.release();
        goto L_0x0162;
    L_0x0162:
        throw r0;
    L_0x0163:
        r18 = r2;
        r11.release();
        r0 = new java.io.IOException;
        r2 = "Canceled";
        r0.<init>(r2);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http.RetryAndFollowUpInterceptor.intercept(okhttp3.Interceptor$Chain):okhttp3.Response");
    }

    public RetryAndFollowUpInterceptor(OkHttpClient client, boolean forWebSocket) {
        this.client = client;
        this.forWebSocket = forWebSocket;
    }

    public void cancel() {
        this.canceled = true;
        StreamAllocation streamAllocation = this.streamAllocation;
        if (streamAllocation != null) {
            streamAllocation.cancel();
        }
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public void setCallStackTrace(Object callStackTrace) {
        this.callStackTrace = callStackTrace;
    }

    public StreamAllocation streamAllocation() {
        return this.streamAllocation;
    }

    private Address createAddress(HttpUrl url) {
        RetryAndFollowUpInterceptor retryAndFollowUpInterceptor = this;
        SSLSocketFactory sslSocketFactory = null;
        HostnameVerifier hostnameVerifier = null;
        CertificatePinner certificatePinner = null;
        if (url.isHttps()) {
            sslSocketFactory = retryAndFollowUpInterceptor.client.sslSocketFactory();
            hostnameVerifier = retryAndFollowUpInterceptor.client.hostnameVerifier();
            certificatePinner = retryAndFollowUpInterceptor.client.certificatePinner();
        }
        return new Address(url.host(), url.port(), retryAndFollowUpInterceptor.client.dns(), retryAndFollowUpInterceptor.client.socketFactory(), sslSocketFactory, hostnameVerifier, certificatePinner, retryAndFollowUpInterceptor.client.proxyAuthenticator(), retryAndFollowUpInterceptor.client.proxy(), retryAndFollowUpInterceptor.client.protocols(), retryAndFollowUpInterceptor.client.connectionSpecs(), retryAndFollowUpInterceptor.client.proxySelector());
    }

    private boolean recover(IOException e, StreamAllocation streamAllocation, boolean requestSendStarted, Request userRequest) {
        streamAllocation.streamFailed(e);
        if (!this.client.retryOnConnectionFailure()) {
            return false;
        }
        if ((!requestSendStarted || !(userRequest.body() instanceof UnrepeatableRequestBody)) && isRecoverable(e, requestSendStarted) && streamAllocation.hasMoreRoutes()) {
            return true;
        }
        return false;
    }

    private boolean isRecoverable(IOException e, boolean requestSendStarted) {
        boolean z = false;
        if (e instanceof ProtocolException) {
            return false;
        }
        if (e instanceof InterruptedIOException) {
            if ((e instanceof SocketTimeoutException) && !requestSendStarted) {
                z = true;
            }
            return z;
        }
        if (e instanceof SSLHandshakeException) {
            if (e.getCause() instanceof CertificateException) {
                return false;
            }
        }
        if (e instanceof SSLPeerUnverifiedException) {
            return false;
        }
        return true;
    }

    private Request followUpRequest(Response userResponse, Route route) throws IOException {
        if (userResponse != null) {
            int responseCode = userResponse.code();
            String method = userResponse.request().method();
            RequestBody requestBody = null;
            switch (responseCode) {
                case 300:
                case 301:
                case 302:
                case 303:
                    break;
                case StatusLine.HTTP_TEMP_REDIRECT /*307*/:
                case StatusLine.HTTP_PERM_REDIRECT /*308*/:
                    if (method.equals("GET") || method.equals("HEAD")) {
                        break;
                    }
                    return null;
                case 401:
                    return this.client.authenticator().authenticate(route, userResponse);
                case 407:
                    Proxy selectedProxy;
                    if (route != null) {
                        selectedProxy = route.proxy();
                    } else {
                        selectedProxy = this.client.proxy();
                    }
                    if (selectedProxy.type() == Type.HTTP) {
                        return this.client.proxyAuthenticator().authenticate(route, userResponse);
                    }
                    throw new ProtocolException("Received HTTP_PROXY_AUTH (407) code while not using proxy");
                case 408:
                    if (!this.client.retryOnConnectionFailure() || (userResponse.request().body() instanceof UnrepeatableRequestBody)) {
                        return null;
                    }
                    if (userResponse.priorResponse() != null) {
                        if (userResponse.priorResponse().code() == 408) {
                            return null;
                        }
                    }
                    if (retryAfter(userResponse, 0) > 0) {
                        return null;
                    }
                    return userResponse.request();
                case 503:
                    if (userResponse.priorResponse() != null) {
                        if (userResponse.priorResponse().code() == 503) {
                            return null;
                        }
                    }
                    if (retryAfter(userResponse, Integer.MAX_VALUE) == 0) {
                        return userResponse.request();
                    }
                    return null;
                default:
                    return null;
            }
            if (!this.client.followRedirects()) {
                return null;
            }
            String location = userResponse.header("Location");
            if (location == null) {
                return null;
            }
            HttpUrl url = userResponse.request().url().resolve(location);
            if (url == null) {
                return null;
            }
            if (!url.scheme().equals(userResponse.request().url().scheme()) && !this.client.followSslRedirects()) {
                return null;
            }
            Builder requestBuilder = userResponse.request().newBuilder();
            if (HttpMethod.permitsRequestBody(method)) {
                boolean maintainBody = HttpMethod.redirectsWithBody(method);
                if (HttpMethod.redirectsToGet(method)) {
                    requestBuilder.method("GET", null);
                } else {
                    if (maintainBody) {
                        requestBody = userResponse.request().body();
                    }
                    requestBuilder.method(method, requestBody);
                }
                if (!maintainBody) {
                    requestBuilder.removeHeader("Transfer-Encoding");
                    requestBuilder.removeHeader("Content-Length");
                    requestBuilder.removeHeader("Content-Type");
                }
            }
            if (!sameConnection(userResponse, url)) {
                requestBuilder.removeHeader("Authorization");
            }
            return requestBuilder.url(url).build();
        }
        throw new IllegalStateException();
    }

    private int retryAfter(Response userResponse, int defaultDelay) {
        String header = userResponse.header("Retry-After");
        if (header == null) {
            return defaultDelay;
        }
        if (header.matches("\\d+")) {
            return Integer.valueOf(header).intValue();
        }
        return Integer.MAX_VALUE;
    }

    private boolean sameConnection(Response response, HttpUrl followUp) {
        HttpUrl url = response.request().url();
        if (url.host().equals(followUp.host())) {
            if (url.port() == followUp.port()) {
                if (url.scheme().equals(followUp.scheme())) {
                    return true;
                }
            }
        }
        return false;
    }
}
