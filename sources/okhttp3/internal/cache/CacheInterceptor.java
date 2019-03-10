package okhttp3.internal.cache;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Headers;
import okhttp3.Headers$Builder;
import okhttp3.Interceptor;
import okhttp3.Interceptor$Chain;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.HttpMethod;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;
import org.jsoup.helper.HttpConnection;

public final class CacheInterceptor implements Interceptor {
    final InternalCache cache;

    public CacheInterceptor(InternalCache cache) {
        this.cache = cache;
    }

    public Response intercept(Interceptor$Chain chain) throws IOException {
        Response cacheCandidate;
        InternalCache internalCache = this.cache;
        if (internalCache != null) {
            cacheCandidate = internalCache.get(chain.request());
        } else {
            cacheCandidate = null;
        }
        CacheStrategy strategy = new CacheStrategy$Factory(System.currentTimeMillis(), chain.request(), cacheCandidate).get();
        Request networkRequest = strategy.networkRequest;
        Response cacheResponse = strategy.cacheResponse;
        InternalCache internalCache2 = this.cache;
        if (internalCache2 != null) {
            internalCache2.trackResponse(strategy);
        }
        if (cacheCandidate != null && cacheResponse == null) {
            Util.closeQuietly(cacheCandidate.body());
        }
        if (networkRequest == null && cacheResponse == null) {
            return new Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(504).message("Unsatisfiable Request (only-if-cached)").body(Util.EMPTY_RESPONSE).sentRequestAtMillis(-1).receivedResponseAtMillis(System.currentTimeMillis()).build();
        }
        if (networkRequest == null) {
            return cacheResponse.newBuilder().cacheResponse(stripBody(cacheResponse)).build();
        }
        try {
            Response response;
            Response networkResponse = chain.proceed(networkRequest);
            if (networkResponse == null && cacheCandidate != null) {
                Util.closeQuietly(cacheCandidate.body());
            }
            if (cacheResponse != null) {
                if (networkResponse.code() == 304) {
                    response = cacheResponse.newBuilder().headers(combine(cacheResponse.headers(), networkResponse.headers())).sentRequestAtMillis(networkResponse.sentRequestAtMillis()).receivedResponseAtMillis(networkResponse.receivedResponseAtMillis()).cacheResponse(stripBody(cacheResponse)).networkResponse(stripBody(networkResponse)).build();
                    networkResponse.body().close();
                    this.cache.trackConditionalCacheHit();
                    this.cache.update(cacheResponse, response);
                    return response;
                }
                Util.closeQuietly(cacheResponse.body());
            }
            response = networkResponse.newBuilder().cacheResponse(stripBody(cacheResponse)).networkResponse(stripBody(networkResponse)).build();
            if (this.cache != null) {
                if (HttpHeaders.hasBody(response) && CacheStrategy.isCacheable(response, networkRequest)) {
                    return cacheWritingResponse(this.cache.put(response), response);
                }
                if (HttpMethod.invalidatesCache(networkRequest.method())) {
                    try {
                        this.cache.remove(networkRequest);
                    } catch (IOException e) {
                    }
                }
            }
            return response;
        } catch (Throwable th) {
            if (null == null && cacheCandidate != null) {
                Util.closeQuietly(cacheCandidate.body());
            }
        }
    }

    private static Response stripBody(Response response) {
        if (response == null || response.body() == null) {
            return response;
        }
        return response.newBuilder().body(null).build();
    }

    private Response cacheWritingResponse(final CacheRequest cacheRequest, Response response) throws IOException {
        if (cacheRequest == null) {
            return response;
        }
        Sink cacheBodyUnbuffered = cacheRequest.body();
        if (cacheBodyUnbuffered == null) {
            return response;
        }
        final BufferedSource source = response.body().source();
        final BufferedSink cacheBody = Okio.buffer(cacheBodyUnbuffered);
        Source cacheWritingSource = new Source() {
            boolean cacheRequestClosed;

            public long read(Buffer sink, long byteCount) throws IOException {
                try {
                    long bytesRead = source.read(sink, byteCount);
                    if (bytesRead == -1) {
                        if (!this.cacheRequestClosed) {
                            this.cacheRequestClosed = true;
                            cacheBody.close();
                        }
                        return -1;
                    }
                    sink.copyTo(cacheBody.buffer(), sink.size() - bytesRead, bytesRead);
                    cacheBody.emitCompleteSegments();
                    return bytesRead;
                } catch (IOException e) {
                    if (!this.cacheRequestClosed) {
                        this.cacheRequestClosed = true;
                        cacheRequest.abort();
                    }
                    throw e;
                }
            }

            public Timeout timeout() {
                return source.timeout();
            }

            public void close() throws IOException {
                if (!this.cacheRequestClosed) {
                    if (!Util.discard(this, 100, TimeUnit.MILLISECONDS)) {
                        this.cacheRequestClosed = true;
                        cacheRequest.abort();
                    }
                }
                source.close();
            }
        };
        return response.newBuilder().body(new RealResponseBody(response.header("Content-Type"), response.body().contentLength(), Okio.buffer(cacheWritingSource))).build();
    }

    private static Headers combine(Headers cachedHeaders, Headers networkHeaders) {
        int i;
        Headers$Builder result = new Headers$Builder();
        int size = cachedHeaders.size();
        for (i = 0; i < size; i++) {
            String fieldName = cachedHeaders.name(i);
            String value = cachedHeaders.value(i);
            if (!"Warning".equalsIgnoreCase(fieldName) || !value.startsWith("1")) {
                if (!isContentSpecificHeader(fieldName) && isEndToEnd(fieldName)) {
                    if (networkHeaders.get(fieldName) == null) {
                    }
                }
                Internal.instance.addLenient(result, fieldName, value);
            }
        }
        size = networkHeaders.size();
        for (i = 0; i < size; i++) {
            fieldName = networkHeaders.name(i);
            if (!isContentSpecificHeader(fieldName) && isEndToEnd(fieldName)) {
                Internal.instance.addLenient(result, fieldName, networkHeaders.value(i));
            }
        }
        return result.build();
    }

    static boolean isEndToEnd(String fieldName) {
        if (!"Connection".equalsIgnoreCase(fieldName)) {
            if (!"Keep-Alive".equalsIgnoreCase(fieldName)) {
                if (!"Proxy-Authenticate".equalsIgnoreCase(fieldName)) {
                    if (!"Proxy-Authorization".equalsIgnoreCase(fieldName)) {
                        if (!"TE".equalsIgnoreCase(fieldName)) {
                            if (!"Trailers".equalsIgnoreCase(fieldName)) {
                                if (!"Transfer-Encoding".equalsIgnoreCase(fieldName)) {
                                    if (!"Upgrade".equalsIgnoreCase(fieldName)) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    static boolean isContentSpecificHeader(String fieldName) {
        if (!"Content-Length".equalsIgnoreCase(fieldName)) {
            if (!HttpConnection.CONTENT_ENCODING.equalsIgnoreCase(fieldName)) {
                if (!"Content-Type".equalsIgnoreCase(fieldName)) {
                    return false;
                }
            }
        }
        return true;
    }
}
