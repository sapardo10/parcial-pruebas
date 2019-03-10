package okhttp3.internal.huc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketPermission;
import java.net.URL;
import java.security.Permission;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.Handshake;
import okhttp3.Headers;
import okhttp3.Headers$Builder;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Interceptor$Chain;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.internal.Internal;
import okhttp3.internal.JavaNetHeaders;
import okhttp3.internal.URLFilter;
import okhttp3.internal.Util;
import okhttp3.internal.Version;
import okhttp3.internal.http.HttpDate;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.HttpMethod;
import okhttp3.internal.http.StatusLine;
import okhttp3.internal.platform.Platform;

public final class OkHttpURLConnection extends HttpURLConnection implements Callback {
    private static final Set<String> METHODS = new LinkedHashSet(Arrays.asList(new String[]{"OPTIONS", "GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "PATCH"}));
    public static final String RESPONSE_SOURCE;
    public static final String SELECTED_PROTOCOL;
    Call call;
    private Throwable callFailure;
    OkHttpClient client;
    boolean connectPending;
    private boolean executed;
    private long fixedContentLength;
    Handshake handshake;
    private final Object lock;
    private final NetworkInterceptor networkInterceptor;
    Response networkResponse;
    Proxy proxy;
    private Headers$Builder requestHeaders;
    private Response response;
    private Headers responseHeaders;
    URLFilter urlFilter;

    static final class UnexpectedException extends IOException {
        static final Interceptor INTERCEPTOR = new C12031();

        /* renamed from: okhttp3.internal.huc.OkHttpURLConnection$UnexpectedException$1 */
        class C12031 implements Interceptor {
            C12031() {
            }

            public Response intercept(Interceptor$Chain chain) throws IOException {
                try {
                    return chain.proceed(chain.request());
                } catch (Throwable e) {
                    throw new UnexpectedException(e);
                }
            }
        }

        UnexpectedException(Throwable cause) {
            super(cause);
        }
    }

    final class NetworkInterceptor implements Interceptor {
        private boolean proceed;

        public okhttp3.Response intercept(okhttp3.Interceptor$Chain r6) throws java.io.IOException {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:36:0x00a0 in {2, 3, 12, 18, 19, 25, 28, 32, 35} preds:[]
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
            r5 = this;
            r0 = r6.request();
            r1 = okhttp3.internal.huc.OkHttpURLConnection.this;
            r1 = r1.urlFilter;
            if (r1 == 0) goto L_0x001a;
        L_0x000a:
            r1 = okhttp3.internal.huc.OkHttpURLConnection.this;
            r1 = r1.urlFilter;
            r2 = r0.url();
            r2 = r2.url();
            r1.checkURLPermitted(r2);
            goto L_0x001b;
        L_0x001b:
            r1 = okhttp3.internal.huc.OkHttpURLConnection.this;
            r1 = r1.lock;
            monitor-enter(r1);
            r2 = okhttp3.internal.huc.OkHttpURLConnection.this;	 Catch:{ all -> 0x009d }
            r3 = 0;	 Catch:{ all -> 0x009d }
            r2.connectPending = r3;	 Catch:{ all -> 0x009d }
            r2 = okhttp3.internal.huc.OkHttpURLConnection.this;	 Catch:{ all -> 0x009d }
            r3 = r6.connection();	 Catch:{ all -> 0x009d }
            r3 = r3.route();	 Catch:{ all -> 0x009d }
            r3 = r3.proxy();	 Catch:{ all -> 0x009d }
            r2.proxy = r3;	 Catch:{ all -> 0x009d }
            r2 = okhttp3.internal.huc.OkHttpURLConnection.this;	 Catch:{ all -> 0x009d }
            r3 = r6.connection();	 Catch:{ all -> 0x009d }
            r3 = r3.handshake();	 Catch:{ all -> 0x009d }
            r2.handshake = r3;	 Catch:{ all -> 0x009d }
            r2 = okhttp3.internal.huc.OkHttpURLConnection.this;	 Catch:{ all -> 0x009d }
            r2 = r2.lock;	 Catch:{ all -> 0x009d }
            r2.notifyAll();	 Catch:{ all -> 0x009d }
        L_0x004c:
            r2 = r5.proceed;	 Catch:{ InterruptedException -> 0x0096 }
            if (r2 != 0) goto L_0x005a;	 Catch:{ InterruptedException -> 0x0096 }
        L_0x0050:
            r2 = okhttp3.internal.huc.OkHttpURLConnection.this;	 Catch:{ InterruptedException -> 0x0096 }
            r2 = r2.lock;	 Catch:{ InterruptedException -> 0x0096 }
            r2.wait();	 Catch:{ InterruptedException -> 0x0096 }
            goto L_0x004c;
            monitor-exit(r1);	 Catch:{ all -> 0x009d }
            r1 = r0.body();
            r1 = r1 instanceof okhttp3.internal.huc.OutputStreamRequestBody;
            if (r1 == 0) goto L_0x0070;
        L_0x0064:
            r1 = r0.body();
            r1 = (okhttp3.internal.huc.OutputStreamRequestBody) r1;
            r0 = r1.prepareToSendRequest(r0);
            r2 = r0;
            goto L_0x0071;
        L_0x0070:
            r2 = r0;
        L_0x0071:
            r3 = r6.proceed(r2);
            r0 = okhttp3.internal.huc.OkHttpURLConnection.this;
            r4 = r0.lock;
            monitor-enter(r4);
            r0 = okhttp3.internal.huc.OkHttpURLConnection.this;	 Catch:{ all -> 0x0093 }
            r0.networkResponse = r3;	 Catch:{ all -> 0x0093 }
            r0 = okhttp3.internal.huc.OkHttpURLConnection.this;	 Catch:{ all -> 0x0093 }
            r1 = r3.request();	 Catch:{ all -> 0x0093 }
            r1 = r1.url();	 Catch:{ all -> 0x0093 }
            r1 = r1.url();	 Catch:{ all -> 0x0093 }
            r0.url = r1;	 Catch:{ all -> 0x0093 }
            monitor-exit(r4);	 Catch:{ all -> 0x0093 }
            return r3;	 Catch:{ all -> 0x0093 }
        L_0x0093:
            r0 = move-exception;	 Catch:{ all -> 0x0093 }
            monitor-exit(r4);	 Catch:{ all -> 0x0093 }
            throw r0;
        L_0x0096:
            r2 = move-exception;
            r3 = new java.io.InterruptedIOException;	 Catch:{ all -> 0x009d }
            r3.<init>();	 Catch:{ all -> 0x009d }
            throw r3;	 Catch:{ all -> 0x009d }
        L_0x009d:
            r2 = move-exception;	 Catch:{ all -> 0x009d }
            monitor-exit(r1);	 Catch:{ all -> 0x009d }
            throw r2;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.huc.OkHttpURLConnection.NetworkInterceptor.intercept(okhttp3.Interceptor$Chain):okhttp3.Response");
        }

        NetworkInterceptor() {
        }

        public void proceed() {
            synchronized (OkHttpURLConnection.this.lock) {
                this.proceed = true;
                OkHttpURLConnection.this.lock.notifyAll();
            }
        }
    }

    private okhttp3.Response getResponse(boolean r6) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:73:0x0095 in {7, 15, 17, 21, 32, 36, 41, 43, 47, 49, 59, 62, 65, 68, 72} preds:[]
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
        r0 = r5.lock;
        monitor-enter(r0);
        r1 = r5.response;	 Catch:{ all -> 0x0092 }
        if (r1 == 0) goto L_0x000b;	 Catch:{ all -> 0x0092 }
    L_0x0007:
        r1 = r5.response;	 Catch:{ all -> 0x0092 }
        monitor-exit(r0);	 Catch:{ all -> 0x0092 }
        return r1;	 Catch:{ all -> 0x0092 }
    L_0x000b:
        r1 = r5.callFailure;	 Catch:{ all -> 0x0092 }
        if (r1 == 0) goto L_0x0020;	 Catch:{ all -> 0x0092 }
    L_0x000f:
        if (r6 == 0) goto L_0x0019;	 Catch:{ all -> 0x0092 }
    L_0x0011:
        r1 = r5.networkResponse;	 Catch:{ all -> 0x0092 }
        if (r1 == 0) goto L_0x0019;	 Catch:{ all -> 0x0092 }
    L_0x0015:
        r1 = r5.networkResponse;	 Catch:{ all -> 0x0092 }
        monitor-exit(r0);	 Catch:{ all -> 0x0092 }
        return r1;	 Catch:{ all -> 0x0092 }
    L_0x0019:
        r1 = r5.callFailure;	 Catch:{ all -> 0x0092 }
        r1 = propagate(r1);	 Catch:{ all -> 0x0092 }
        throw r1;	 Catch:{ all -> 0x0092 }
    L_0x0020:
        monitor-exit(r0);	 Catch:{ all -> 0x0092 }
        r1 = r5.buildCall();
        r0 = r5.networkInterceptor;
        r0.proceed();
        r0 = r1.request();
        r0 = r0.body();
        r2 = r0;
        r2 = (okhttp3.internal.huc.OutputStreamRequestBody) r2;
        if (r2 == 0) goto L_0x003f;
    L_0x0037:
        r0 = r2.outputStream();
        r0.close();
    L_0x003f:
        r0 = r5.executed;
        if (r0 == 0) goto L_0x0063;
    L_0x0043:
        r0 = r5.lock;
        monitor-enter(r0);
    L_0x0046:
        r3 = r5.response;	 Catch:{ InterruptedException -> 0x005a }
        if (r3 != 0) goto L_0x0054;	 Catch:{ InterruptedException -> 0x005a }
    L_0x004a:
        r3 = r5.callFailure;	 Catch:{ InterruptedException -> 0x005a }
        if (r3 != 0) goto L_0x0054;	 Catch:{ InterruptedException -> 0x005a }
    L_0x004e:
        r3 = r5.lock;	 Catch:{ InterruptedException -> 0x005a }
        r3.wait();	 Catch:{ InterruptedException -> 0x005a }
        goto L_0x0046;
        monitor-exit(r0);	 Catch:{ all -> 0x0058 }
        goto L_0x0072;	 Catch:{ all -> 0x0058 }
    L_0x0058:
        r3 = move-exception;	 Catch:{ all -> 0x0058 }
        goto L_0x0061;	 Catch:{ all -> 0x0058 }
    L_0x005a:
        r3 = move-exception;	 Catch:{ all -> 0x0058 }
        r4 = new java.io.InterruptedIOException;	 Catch:{ all -> 0x0058 }
        r4.<init>();	 Catch:{ all -> 0x0058 }
        throw r4;	 Catch:{ all -> 0x0058 }
    L_0x0061:
        monitor-exit(r0);	 Catch:{ all -> 0x0058 }
        throw r3;
    L_0x0063:
        r0 = 1;
        r5.executed = r0;
        r0 = r1.execute();	 Catch:{ IOException -> 0x006e }
        r5.onResponse(r1, r0);	 Catch:{ IOException -> 0x006e }
        goto L_0x0072;
    L_0x006e:
        r0 = move-exception;
        r5.onFailure(r1, r0);
    L_0x0072:
        r3 = r5.lock;
        monitor-enter(r3);
        r0 = r5.callFailure;	 Catch:{ all -> 0x008f }
        if (r0 != 0) goto L_0x0088;	 Catch:{ all -> 0x008f }
    L_0x0079:
        r0 = r5.response;	 Catch:{ all -> 0x008f }
        if (r0 == 0) goto L_0x0081;	 Catch:{ all -> 0x008f }
    L_0x007d:
        r0 = r5.response;	 Catch:{ all -> 0x008f }
        monitor-exit(r3);	 Catch:{ all -> 0x008f }
        return r0;	 Catch:{ all -> 0x008f }
    L_0x0081:
        monitor-exit(r3);	 Catch:{ all -> 0x008f }
        r0 = new java.lang.AssertionError;
        r0.<init>();
        throw r0;
    L_0x0088:
        r0 = r5.callFailure;	 Catch:{ all -> 0x008f }
        r0 = propagate(r0);	 Catch:{ all -> 0x008f }
        throw r0;	 Catch:{ all -> 0x008f }
    L_0x008f:
        r0 = move-exception;	 Catch:{ all -> 0x008f }
        monitor-exit(r3);	 Catch:{ all -> 0x008f }
        throw r0;
    L_0x0092:
        r1 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x0092 }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.huc.OkHttpURLConnection.getResponse(boolean):okhttp3.Response");
    }

    public void connect() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:30:0x003e in {2, 12, 18, 21, 27, 29} preds:[]
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
        r4 = this;
        r0 = r4.executed;
        if (r0 == 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = r4.buildCall();
        r1 = 1;
        r4.executed = r1;
        r0.enqueue(r4);
        r1 = r4.lock;
        monitor-enter(r1);
    L_0x0012:
        r2 = r4.connectPending;	 Catch:{ InterruptedException -> 0x0035 }
        if (r2 == 0) goto L_0x0024;	 Catch:{ InterruptedException -> 0x0035 }
    L_0x0016:
        r2 = r4.response;	 Catch:{ InterruptedException -> 0x0035 }
        if (r2 != 0) goto L_0x0024;	 Catch:{ InterruptedException -> 0x0035 }
    L_0x001a:
        r2 = r4.callFailure;	 Catch:{ InterruptedException -> 0x0035 }
        if (r2 != 0) goto L_0x0024;	 Catch:{ InterruptedException -> 0x0035 }
    L_0x001e:
        r2 = r4.lock;	 Catch:{ InterruptedException -> 0x0035 }
        r2.wait();	 Catch:{ InterruptedException -> 0x0035 }
        goto L_0x0012;	 Catch:{ InterruptedException -> 0x0035 }
        r2 = r4.callFailure;	 Catch:{ InterruptedException -> 0x0035 }
        if (r2 != 0) goto L_0x002c;
        monitor-exit(r1);	 Catch:{ all -> 0x0033 }
        return;
    L_0x002c:
        r2 = r4.callFailure;	 Catch:{ InterruptedException -> 0x0035 }
        r2 = propagate(r2);	 Catch:{ InterruptedException -> 0x0035 }
        throw r2;	 Catch:{ InterruptedException -> 0x0035 }
    L_0x0033:
        r2 = move-exception;
        goto L_0x003c;
    L_0x0035:
        r2 = move-exception;
        r3 = new java.io.InterruptedIOException;	 Catch:{ all -> 0x0033 }
        r3.<init>();	 Catch:{ all -> 0x0033 }
        throw r3;	 Catch:{ all -> 0x0033 }
    L_0x003c:
        monitor-exit(r1);	 Catch:{ all -> 0x0033 }
        throw r2;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.huc.OkHttpURLConnection.connect():void");
    }

    static {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Platform.get().getPrefix());
        stringBuilder.append("-Selected-Protocol");
        SELECTED_PROTOCOL = stringBuilder.toString();
        stringBuilder = new StringBuilder();
        stringBuilder.append(Platform.get().getPrefix());
        stringBuilder.append("-Response-Source");
        RESPONSE_SOURCE = stringBuilder.toString();
    }

    public OkHttpURLConnection(URL url, OkHttpClient client) {
        super(url);
        this.networkInterceptor = new NetworkInterceptor();
        this.requestHeaders = new Headers$Builder();
        this.fixedContentLength = -1;
        this.lock = new Object();
        this.connectPending = true;
        this.client = client;
    }

    public OkHttpURLConnection(URL url, OkHttpClient client, URLFilter urlFilter) {
        this(url, client);
        this.urlFilter = urlFilter;
    }

    public void disconnect() {
        if (this.call != null) {
            this.networkInterceptor.proceed();
            this.call.cancel();
        }
    }

    public InputStream getErrorStream() {
        try {
            Response response = getResponse(true);
            if (!HttpHeaders.hasBody(response) || response.code() < 400) {
                return null;
            }
            return response.body().byteStream();
        } catch (IOException e) {
            return null;
        }
    }

    private Headers getHeaders() throws IOException {
        if (this.responseHeaders == null) {
            Response response = getResponse(true);
            this.responseHeaders = response.headers().newBuilder().add(SELECTED_PROTOCOL, response.protocol().toString()).add(RESPONSE_SOURCE, responseSourceHeader(response)).build();
        }
        return this.responseHeaders;
    }

    private static String responseSourceHeader(Response response) {
        StringBuilder stringBuilder;
        if (response.networkResponse() == null) {
            if (response.cacheResponse() == null) {
                return "NONE";
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("CACHE ");
            stringBuilder.append(response.code());
            return stringBuilder.toString();
        } else if (response.cacheResponse() == null) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("NETWORK ");
            stringBuilder.append(response.code());
            return stringBuilder.toString();
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("CONDITIONAL_CACHE ");
            stringBuilder.append(response.networkResponse().code());
            return stringBuilder.toString();
        }
    }

    public String getHeaderField(int position) {
        try {
            Headers headers = getHeaders();
            if (position >= 0) {
                if (position < headers.size()) {
                    return headers.value(position);
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public String getHeaderField(String fieldName) {
        String statusLine;
        if (fieldName == null) {
            try {
                statusLine = StatusLine.get(getResponse(true)).toString();
            } catch (IOException e) {
                return null;
            }
        }
        statusLine = getHeaders().get(fieldName);
        return statusLine;
    }

    public String getHeaderFieldKey(int position) {
        try {
            Headers headers = getHeaders();
            if (position >= 0) {
                if (position < headers.size()) {
                    return headers.name(position);
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public Map<String, List<String>> getHeaderFields() {
        try {
            return JavaNetHeaders.toMultimap(getHeaders(), StatusLine.get(getResponse(true)).toString());
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }

    public Map<String, List<String>> getRequestProperties() {
        if (!this.connected) {
            return JavaNetHeaders.toMultimap(this.requestHeaders.build(), null);
        }
        throw new IllegalStateException("Cannot access request header fields after connection is set");
    }

    public InputStream getInputStream() throws IOException {
        if (this.doInput) {
            Response response = getResponse(null);
            if (response.code() < 400) {
                return response.body().byteStream();
            }
            throw new FileNotFoundException(this.url.toString());
        }
        throw new ProtocolException("This protocol does not support input");
    }

    public OutputStream getOutputStream() throws IOException {
        OutputStreamRequestBody requestBody = (OutputStreamRequestBody) buildCall().request().body();
        if (requestBody != null) {
            if (requestBody instanceof StreamedRequestBody) {
                connect();
                this.networkInterceptor.proceed();
            }
            if (!requestBody.isClosed()) {
                return requestBody.outputStream();
            }
            throw new ProtocolException("cannot write request body after response has been read");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("method does not support a request body: ");
        stringBuilder.append(this.method);
        throw new ProtocolException(stringBuilder.toString());
    }

    public Permission getPermission() throws IOException {
        int port;
        URL url = getURL();
        String hostname = url.getHost();
        if (url.getPort() != -1) {
            port = url.getPort();
        } else {
            port = HttpUrl.defaultPort(url.getProtocol());
        }
        if (usingProxy()) {
            InetSocketAddress proxyAddress = (InetSocketAddress) this.client.proxy().address();
            hostname = proxyAddress.getHostName();
            port = proxyAddress.getPort();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(hostname);
        stringBuilder.append(":");
        stringBuilder.append(port);
        return new SocketPermission(stringBuilder.toString(), "connect, resolve");
    }

    public String getRequestProperty(String field) {
        if (field == null) {
            return null;
        }
        return this.requestHeaders.get(field);
    }

    public void setConnectTimeout(int timeoutMillis) {
        this.client = this.client.newBuilder().connectTimeout((long) timeoutMillis, TimeUnit.MILLISECONDS).build();
    }

    public void setInstanceFollowRedirects(boolean followRedirects) {
        this.client = this.client.newBuilder().followRedirects(followRedirects).build();
    }

    public boolean getInstanceFollowRedirects() {
        return this.client.followRedirects();
    }

    public int getConnectTimeout() {
        return this.client.connectTimeoutMillis();
    }

    public void setReadTimeout(int timeoutMillis) {
        this.client = this.client.newBuilder().readTimeout((long) timeoutMillis, TimeUnit.MILLISECONDS).build();
    }

    public int getReadTimeout() {
        return this.client.readTimeoutMillis();
    }

    private Call buildCall() throws IOException {
        Call call = this.call;
        if (call != null) {
            return call;
        }
        boolean stream = true;
        this.connected = true;
        if (this.doOutput) {
            if (this.method.equals("GET")) {
                this.method = "POST";
            } else if (!HttpMethod.permitsRequestBody(this.method)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this.method);
                stringBuilder.append(" does not support writing");
                throw new ProtocolException(stringBuilder.toString());
            }
        }
        if (this.requestHeaders.get("User-Agent") == null) {
            this.requestHeaders.add("User-Agent", defaultUserAgent());
        }
        OutputStreamRequestBody requestBody = null;
        if (HttpMethod.permitsRequestBody(this.method)) {
            OutputStreamRequestBody streamedRequestBody;
            if (this.requestHeaders.get("Content-Type") == null) {
                this.requestHeaders.add("Content-Type", "application/x-www-form-urlencoded");
            }
            if (this.fixedContentLength == -1) {
                if (this.chunkLength <= 0) {
                    stream = false;
                }
            }
            long contentLength = -1;
            String contentLengthString = this.requestHeaders.get("Content-Length");
            if (this.fixedContentLength != -1) {
                contentLength = this.fixedContentLength;
            } else if (contentLengthString != null) {
                contentLength = Long.parseLong(contentLengthString);
            }
            if (stream) {
                streamedRequestBody = new StreamedRequestBody(contentLength);
            } else {
                streamedRequestBody = new BufferedRequestBody(contentLength);
            }
            requestBody = streamedRequestBody;
            requestBody.timeout().timeout((long) this.client.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
        }
        Request request = new Builder().url(Internal.instance.getHttpUrlChecked(getURL().toString())).headers(this.requestHeaders.build()).method(this.method, requestBody).build();
        URLFilter uRLFilter = this.urlFilter;
        if (uRLFilter != null) {
            uRLFilter.checkURLPermitted(request.url().url());
        }
        OkHttpClient.Builder clientBuilder = this.client.newBuilder();
        clientBuilder.interceptors().clear();
        clientBuilder.interceptors().add(UnexpectedException.INTERCEPTOR);
        clientBuilder.networkInterceptors().clear();
        clientBuilder.networkInterceptors().add(this.networkInterceptor);
        clientBuilder.dispatcher(new Dispatcher(this.client.dispatcher().executorService()));
        if (!getUseCaches()) {
            clientBuilder.cache(null);
        }
        Call newCall = clientBuilder.build().newCall(request);
        this.call = newCall;
        return newCall;
    }

    private String defaultUserAgent() {
        String agent = System.getProperty("http.agent");
        return agent != null ? Util.toHumanReadableAscii(agent) : Version.userAgent();
    }

    public boolean usingProxy() {
        boolean z = true;
        if (this.proxy != null) {
            return true;
        }
        Proxy clientProxy = this.client.proxy();
        if (clientProxy == null || clientProxy.type() == Type.DIRECT) {
            z = false;
        }
        return z;
    }

    public String getResponseMessage() throws IOException {
        return getResponse(true).message();
    }

    public int getResponseCode() throws IOException {
        return getResponse(true).code();
    }

    public void setRequestProperty(String field, String newValue) {
        if (this.connected) {
            throw new IllegalStateException("Cannot set request property after connection is made");
        } else if (field == null) {
            throw new NullPointerException("field == null");
        } else if (newValue == null) {
            Platform platform = Platform.get();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Ignoring header ");
            stringBuilder.append(field);
            stringBuilder.append(" because its value was null.");
            platform.log(5, stringBuilder.toString(), null);
        } else {
            this.requestHeaders.set(field, newValue);
        }
    }

    public void setIfModifiedSince(long newValue) {
        super.setIfModifiedSince(newValue);
        if (this.ifModifiedSince != 0) {
            this.requestHeaders.set("If-Modified-Since", HttpDate.format(new Date(this.ifModifiedSince)));
        } else {
            this.requestHeaders.removeAll("If-Modified-Since");
        }
    }

    public void addRequestProperty(String field, String value) {
        if (this.connected) {
            throw new IllegalStateException("Cannot add request property after connection is made");
        } else if (field == null) {
            throw new NullPointerException("field == null");
        } else if (value == null) {
            Platform platform = Platform.get();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Ignoring header ");
            stringBuilder.append(field);
            stringBuilder.append(" because its value was null.");
            platform.log(5, stringBuilder.toString(), null);
        } else {
            this.requestHeaders.add(field, value);
        }
    }

    public void setRequestMethod(String method) throws ProtocolException {
        if (METHODS.contains(method)) {
            this.method = method;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected one of ");
        stringBuilder.append(METHODS);
        stringBuilder.append(" but was ");
        stringBuilder.append(method);
        throw new ProtocolException(stringBuilder.toString());
    }

    public void setFixedLengthStreamingMode(int contentLength) {
        setFixedLengthStreamingMode((long) contentLength);
    }

    public void setFixedLengthStreamingMode(long contentLength) {
        if (this.connected) {
            throw new IllegalStateException("Already connected");
        } else if (this.chunkLength > 0) {
            throw new IllegalStateException("Already in chunked mode");
        } else if (contentLength >= 0) {
            this.fixedContentLength = contentLength;
            this.fixedContentLength = (int) Math.min(contentLength, 2147483647L);
        } else {
            throw new IllegalArgumentException("contentLength < 0");
        }
    }

    public void onFailure(Call call, IOException e) {
        synchronized (this.lock) {
            this.callFailure = e instanceof UnexpectedException ? e.getCause() : e;
            this.lock.notifyAll();
        }
    }

    public void onResponse(Call call, Response response) {
        synchronized (this.lock) {
            this.response = response;
            this.handshake = response.handshake();
            this.url = response.request().url().url();
            this.lock.notifyAll();
        }
    }

    private static IOException propagate(Throwable throwable) throws IOException {
        if (throwable instanceof IOException) {
            throw ((IOException) throwable);
        } else if (throwable instanceof Error) {
            throw ((Error) throwable);
        } else if (throwable instanceof RuntimeException) {
            throw ((RuntimeException) throwable);
        } else {
            throw new AssertionError();
        }
    }
}
