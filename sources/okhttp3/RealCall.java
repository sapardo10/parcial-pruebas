package okhttp3;

import android.support.v4.app.NotificationCompat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.internal.NamedRunnable;
import okhttp3.internal.cache.CacheInterceptor;
import okhttp3.internal.connection.ConnectInterceptor;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.http.BridgeInterceptor;
import okhttp3.internal.http.CallServerInterceptor;
import okhttp3.internal.http.RealInterceptorChain;
import okhttp3.internal.http.RetryAndFollowUpInterceptor;
import okhttp3.internal.platform.Platform;

final class RealCall implements Call {
    final OkHttpClient client;
    private EventListener eventListener;
    private boolean executed;
    final boolean forWebSocket;
    final Request originalRequest;
    final RetryAndFollowUpInterceptor retryAndFollowUpInterceptor;

    final class AsyncCall extends NamedRunnable {
        private final Callback responseCallback;

        protected void execute() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:18:0x007a in {4, 5, 12, 14, 15, 17} preds:[]
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
            r6 = this;
            r0 = 0;
            r1 = okhttp3.RealCall.this;	 Catch:{ IOException -> 0x0037 }
            r1 = r1.getResponseWithInterceptorChain();	 Catch:{ IOException -> 0x0037 }
            r2 = okhttp3.RealCall.this;	 Catch:{ IOException -> 0x0037 }
            r2 = r2.retryAndFollowUpInterceptor;	 Catch:{ IOException -> 0x0037 }
            r2 = r2.isCanceled();	 Catch:{ IOException -> 0x0037 }
            if (r2 == 0) goto L_0x0021;	 Catch:{ IOException -> 0x0037 }
        L_0x0011:
            r0 = 1;	 Catch:{ IOException -> 0x0037 }
            r2 = r6.responseCallback;	 Catch:{ IOException -> 0x0037 }
            r3 = okhttp3.RealCall.this;	 Catch:{ IOException -> 0x0037 }
            r4 = new java.io.IOException;	 Catch:{ IOException -> 0x0037 }
            r5 = "Canceled";	 Catch:{ IOException -> 0x0037 }
            r4.<init>(r5);	 Catch:{ IOException -> 0x0037 }
            r2.onFailure(r3, r4);	 Catch:{ IOException -> 0x0037 }
            goto L_0x0029;	 Catch:{ IOException -> 0x0037 }
        L_0x0021:
            r0 = 1;	 Catch:{ IOException -> 0x0037 }
            r2 = r6.responseCallback;	 Catch:{ IOException -> 0x0037 }
            r3 = okhttp3.RealCall.this;	 Catch:{ IOException -> 0x0037 }
            r2.onResponse(r3, r1);	 Catch:{ IOException -> 0x0037 }
        L_0x0029:
            r1 = okhttp3.RealCall.this;
            r1 = r1.client;
            r1 = r1.dispatcher();
            r1.finished(r6);
            goto L_0x006d;
        L_0x0035:
            r1 = move-exception;
            goto L_0x006e;
        L_0x0037:
            r1 = move-exception;
            if (r0 == 0) goto L_0x005a;
        L_0x003a:
            r2 = okhttp3.internal.platform.Platform.get();	 Catch:{ all -> 0x0035 }
            r3 = 4;	 Catch:{ all -> 0x0035 }
            r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0035 }
            r4.<init>();	 Catch:{ all -> 0x0035 }
            r5 = "Callback failure for ";	 Catch:{ all -> 0x0035 }
            r4.append(r5);	 Catch:{ all -> 0x0035 }
            r5 = okhttp3.RealCall.this;	 Catch:{ all -> 0x0035 }
            r5 = r5.toLoggableString();	 Catch:{ all -> 0x0035 }
            r4.append(r5);	 Catch:{ all -> 0x0035 }
            r4 = r4.toString();	 Catch:{ all -> 0x0035 }
            r2.log(r3, r4, r1);	 Catch:{ all -> 0x0035 }
            goto L_0x0029;	 Catch:{ all -> 0x0035 }
        L_0x005a:
            r2 = okhttp3.RealCall.this;	 Catch:{ all -> 0x0035 }
            r2 = r2.eventListener;	 Catch:{ all -> 0x0035 }
            r3 = okhttp3.RealCall.this;	 Catch:{ all -> 0x0035 }
            r2.callFailed(r3, r1);	 Catch:{ all -> 0x0035 }
            r2 = r6.responseCallback;	 Catch:{ all -> 0x0035 }
            r3 = okhttp3.RealCall.this;	 Catch:{ all -> 0x0035 }
            r2.onFailure(r3, r1);	 Catch:{ all -> 0x0035 }
            goto L_0x0029;
        L_0x006d:
            return;
        L_0x006e:
            r2 = okhttp3.RealCall.this;
            r2 = r2.client;
            r2 = r2.dispatcher();
            r2.finished(r6);
            throw r1;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.RealCall.AsyncCall.execute():void");
        }

        AsyncCall(Callback responseCallback) {
            super("OkHttp %s", this$0.redactedUrl());
            this.responseCallback = responseCallback;
        }

        String host() {
            return RealCall.this.originalRequest.url().host();
        }

        Request request() {
            return RealCall.this.originalRequest;
        }

        RealCall get() {
            return RealCall.this;
        }
    }

    private RealCall(OkHttpClient client, Request originalRequest, boolean forWebSocket) {
        this.client = client;
        this.originalRequest = originalRequest;
        this.forWebSocket = forWebSocket;
        this.retryAndFollowUpInterceptor = new RetryAndFollowUpInterceptor(client, forWebSocket);
    }

    static RealCall newRealCall(OkHttpClient client, Request originalRequest, boolean forWebSocket) {
        RealCall call = new RealCall(client, originalRequest, forWebSocket);
        call.eventListener = client.eventListenerFactory().create(call);
        return call;
    }

    public Request request() {
        return this.originalRequest;
    }

    public Response execute() throws IOException {
        synchronized (this) {
            if (this.executed) {
                throw new IllegalStateException("Already Executed");
            }
            this.executed = true;
        }
        captureCallStackTrace();
        this.eventListener.callStart(this);
        try {
            this.client.dispatcher().executed(this);
            Response result = getResponseWithInterceptorChain();
            if (result != null) {
                this.client.dispatcher().finished(this);
                return result;
            }
            throw new IOException("Canceled");
        } catch (IOException e) {
            this.eventListener.callFailed(this, e);
            throw e;
        } catch (Throwable th) {
            this.client.dispatcher().finished(this);
        }
    }

    private void captureCallStackTrace() {
        this.retryAndFollowUpInterceptor.setCallStackTrace(Platform.get().getStackTraceForCloseable("response.body().close()"));
    }

    public void enqueue(Callback responseCallback) {
        synchronized (this) {
            if (this.executed) {
                throw new IllegalStateException("Already Executed");
            }
            this.executed = true;
        }
        captureCallStackTrace();
        this.eventListener.callStart(this);
        this.client.dispatcher().enqueue(new AsyncCall(responseCallback));
    }

    public void cancel() {
        this.retryAndFollowUpInterceptor.cancel();
    }

    public synchronized boolean isExecuted() {
        return this.executed;
    }

    public boolean isCanceled() {
        return this.retryAndFollowUpInterceptor.isCanceled();
    }

    public RealCall clone() {
        return newRealCall(this.client, this.originalRequest, this.forWebSocket);
    }

    StreamAllocation streamAllocation() {
        return this.retryAndFollowUpInterceptor.streamAllocation();
    }

    String toLoggableString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(isCanceled() ? "canceled " : "");
        stringBuilder.append(this.forWebSocket ? "web socket" : NotificationCompat.CATEGORY_CALL);
        stringBuilder.append(" to ");
        stringBuilder.append(redactedUrl());
        return stringBuilder.toString();
    }

    String redactedUrl() {
        return this.originalRequest.url().redact();
    }

    Response getResponseWithInterceptorChain() throws IOException {
        List<Interceptor> interceptors = new ArrayList();
        interceptors.addAll(this.client.interceptors());
        interceptors.add(this.retryAndFollowUpInterceptor);
        interceptors.add(new BridgeInterceptor(this.client.cookieJar()));
        interceptors.add(new CacheInterceptor(this.client.internalCache()));
        interceptors.add(new ConnectInterceptor(this.client));
        if (!this.forWebSocket) {
            interceptors.addAll(this.client.networkInterceptors());
        }
        interceptors.add(new CallServerInterceptor(this.forWebSocket));
        return new RealInterceptorChain(interceptors, null, null, null, 0, this.originalRequest, this, this.eventListener, this.client.connectTimeoutMillis(), this.client.readTimeoutMillis(), this.client.writeTimeoutMillis()).proceed(this.originalRequest);
    }
}
