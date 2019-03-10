package okhttp3.internal.http;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Interceptor;
import okhttp3.Interceptor$Chain;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.connection.StreamAllocation;

public final class RealInterceptorChain implements Interceptor$Chain {
    private final Call call;
    private int calls;
    private final int connectTimeout;
    private final RealConnection connection;
    private final EventListener eventListener;
    private final HttpCodec httpCodec;
    private final int index;
    private final List<Interceptor> interceptors;
    private final int readTimeout;
    private final Request request;
    private final StreamAllocation streamAllocation;
    private final int writeTimeout;

    public RealInterceptorChain(List<Interceptor> interceptors, StreamAllocation streamAllocation, HttpCodec httpCodec, RealConnection connection, int index, Request request, Call call, EventListener eventListener, int connectTimeout, int readTimeout, int writeTimeout) {
        this.interceptors = interceptors;
        this.connection = connection;
        this.streamAllocation = streamAllocation;
        this.httpCodec = httpCodec;
        this.index = index;
        this.request = request;
        this.call = call;
        this.eventListener = eventListener;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
    }

    public Connection connection() {
        return this.connection;
    }

    public int connectTimeoutMillis() {
        return this.connectTimeout;
    }

    public Interceptor$Chain withConnectTimeout(int timeout, TimeUnit unit) {
        int millis = Util.checkDuration("timeout", (long) timeout, unit);
        return new RealInterceptorChain(this.interceptors, this.streamAllocation, this.httpCodec, this.connection, this.index, this.request, this.call, this.eventListener, millis, this.readTimeout, this.writeTimeout);
    }

    public int readTimeoutMillis() {
        return this.readTimeout;
    }

    public Interceptor$Chain withReadTimeout(int timeout, TimeUnit unit) {
        return new RealInterceptorChain(this.interceptors, this.streamAllocation, this.httpCodec, this.connection, this.index, this.request, this.call, this.eventListener, this.connectTimeout, Util.checkDuration("timeout", (long) timeout, unit), this.writeTimeout);
    }

    public int writeTimeoutMillis() {
        return this.writeTimeout;
    }

    public Interceptor$Chain withWriteTimeout(int timeout, TimeUnit unit) {
        return new RealInterceptorChain(this.interceptors, this.streamAllocation, this.httpCodec, this.connection, this.index, this.request, this.call, this.eventListener, this.connectTimeout, this.readTimeout, Util.checkDuration("timeout", (long) timeout, unit));
    }

    public StreamAllocation streamAllocation() {
        return this.streamAllocation;
    }

    public HttpCodec httpStream() {
        return this.httpCodec;
    }

    public Call call() {
        return this.call;
    }

    public EventListener eventListener() {
        return this.eventListener;
    }

    public Request request() {
        return this.request;
    }

    public Response proceed(Request request) throws IOException {
        return proceed(request, this.streamAllocation, this.httpCodec, this.connection);
    }

    public Response proceed(Request request, StreamAllocation streamAllocation, HttpCodec httpCodec, RealConnection connection) throws IOException {
        if (this.index < this.interceptors.size()) {
            StringBuilder stringBuilder;
            StringBuilder stringBuilder2;
            r0.calls++;
            if (r0.httpCodec != null) {
                if (!r0.connection.supportsUrl(request.url())) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("network interceptor ");
                    stringBuilder.append(r0.interceptors.get(r0.index - 1));
                    stringBuilder.append(" must retain the same host and port");
                    throw new IllegalStateException(stringBuilder.toString());
                }
            }
            if (r0.httpCodec != null) {
                if (r0.calls > 1) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("network interceptor ");
                    stringBuilder.append(r0.interceptors.get(r0.index - 1));
                    stringBuilder.append(" must call proceed() exactly once");
                    throw new IllegalStateException(stringBuilder.toString());
                }
            }
            RealInterceptorChain realInterceptorChain = new RealInterceptorChain(r0.interceptors, streamAllocation, httpCodec, connection, r0.index + 1, request, r0.call, r0.eventListener, r0.connectTimeout, r0.readTimeout, r0.writeTimeout);
            Interceptor interceptor = (Interceptor) r0.interceptors.get(r0.index);
            Response response = interceptor.intercept(realInterceptorChain);
            if (httpCodec != null && r0.index + 1 < r0.interceptors.size()) {
                if (realInterceptorChain.calls != 1) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("network interceptor ");
                    stringBuilder2.append(interceptor);
                    stringBuilder2.append(" must call proceed() exactly once");
                    throw new IllegalStateException(stringBuilder2.toString());
                }
            }
            if (response == null) {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("interceptor ");
                stringBuilder2.append(interceptor);
                stringBuilder2.append(" returned null");
                throw new NullPointerException(stringBuilder2.toString());
            } else if (response.body() != null) {
                return response;
            } else {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("interceptor ");
                stringBuilder2.append(interceptor);
                stringBuilder2.append(" returned a response with no body");
                throw new IllegalStateException(stringBuilder2.toString());
            }
        }
        throw new AssertionError();
    }
}
