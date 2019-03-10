package okhttp3.internal.cache;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.Headers$Builder;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.internal.Internal;
import okhttp3.internal.http.HttpDate;
import okhttp3.internal.http.HttpHeaders;
import org.apache.commons.lang3.time.DateUtils;

public class CacheStrategy$Factory {
    private int ageSeconds = -1;
    final Response cacheResponse;
    private String etag;
    private Date expires;
    private Date lastModified;
    private String lastModifiedString;
    final long nowMillis;
    private long receivedResponseMillis;
    final Request request;
    private long sentRequestMillis;
    private Date servedDate;
    private String servedDateString;

    public CacheStrategy$Factory(long nowMillis, Request request, Response cacheResponse) {
        this.nowMillis = nowMillis;
        this.request = request;
        this.cacheResponse = cacheResponse;
        if (cacheResponse != null) {
            this.sentRequestMillis = cacheResponse.sentRequestAtMillis();
            this.receivedResponseMillis = cacheResponse.receivedResponseAtMillis();
            Headers headers = cacheResponse.headers();
            int size = headers.size();
            for (int i = 0; i < size; i++) {
                String fieldName = headers.name(i);
                String value = headers.value(i);
                if ("Date".equalsIgnoreCase(fieldName)) {
                    this.servedDate = HttpDate.parse(value);
                    this.servedDateString = value;
                } else if ("Expires".equalsIgnoreCase(fieldName)) {
                    this.expires = HttpDate.parse(value);
                } else if ("Last-Modified".equalsIgnoreCase(fieldName)) {
                    this.lastModified = HttpDate.parse(value);
                    this.lastModifiedString = value;
                } else if ("ETag".equalsIgnoreCase(fieldName)) {
                    this.etag = value;
                } else if ("Age".equalsIgnoreCase(fieldName)) {
                    this.ageSeconds = HttpHeaders.parseSeconds(value, -1);
                }
            }
        }
    }

    public CacheStrategy get() {
        CacheStrategy candidate = getCandidate();
        if (candidate.networkRequest == null || !this.request.cacheControl().onlyIfCached()) {
            return candidate;
        }
        return new CacheStrategy(null, null);
    }

    private CacheStrategy getCandidate() {
        if (this.cacheResponse == null) {
            return new CacheStrategy(r0.request, null);
        }
        if (r0.request.isHttps() && r0.cacheResponse.handshake() == null) {
            return new CacheStrategy(r0.request, null);
        }
        if (!CacheStrategy.isCacheable(r0.cacheResponse, r0.request)) {
            return new CacheStrategy(r0.request, null);
        }
        Response response;
        CacheControl requestCaching = r0.request.cacheControl();
        if (requestCaching.noCache()) {
            response = null;
        } else if (hasConditions(r0.request)) {
            CacheControl cacheControl = requestCaching;
            response = null;
        } else {
            CacheControl responseCaching = r0.cacheResponse.cacheControl();
            if (responseCaching.immutable()) {
                return new CacheStrategy(null, r0.cacheResponse);
            }
            long ageMillis = cacheResponseAge();
            long freshMillis = computeFreshnessLifetime();
            if (requestCaching.maxAgeSeconds() != -1) {
                freshMillis = Math.min(freshMillis, TimeUnit.SECONDS.toMillis((long) requestCaching.maxAgeSeconds()));
            }
            long minFreshMillis = 0;
            if (requestCaching.minFreshSeconds() != -1) {
                minFreshMillis = TimeUnit.SECONDS.toMillis((long) requestCaching.minFreshSeconds());
            }
            long maxStaleMillis = 0;
            if (!responseCaching.mustRevalidate() && requestCaching.maxStaleSeconds() != -1) {
                maxStaleMillis = TimeUnit.SECONDS.toMillis((long) requestCaching.maxStaleSeconds());
            }
            if (responseCaching.noCache() || ageMillis + minFreshMillis >= freshMillis + maxStaleMillis) {
                String conditionValue;
                if (r0.etag != null) {
                    requestCaching = "If-None-Match";
                    conditionValue = r0.etag;
                } else if (r0.lastModified != null) {
                    requestCaching = "If-Modified-Since";
                    conditionValue = r0.lastModifiedString;
                } else if (r0.servedDate == null) {
                    return new CacheStrategy(r0.request, null);
                } else {
                    requestCaching = "If-Modified-Since";
                    conditionValue = r0.servedDateString;
                }
                Headers$Builder conditionalRequestHeaders = r0.request.headers().newBuilder();
                Internal.instance.addLenient(conditionalRequestHeaders, requestCaching, conditionValue);
                return new CacheStrategy(r0.request.newBuilder().headers(conditionalRequestHeaders.build()).build(), r0.cacheResponse);
            }
            Builder builder = r0.cacheResponse.newBuilder();
            if (ageMillis + minFreshMillis >= freshMillis) {
                builder.addHeader("Warning", "110 HttpURLConnection \"Response is stale\"");
            }
            if (ageMillis > DateUtils.MILLIS_PER_DAY && isFreshnessLifetimeHeuristic()) {
                builder.addHeader("Warning", "113 HttpURLConnection \"Heuristic expiration\"");
            }
            return new CacheStrategy(null, builder.build());
        }
        return new CacheStrategy(r0.request, response);
    }

    private long computeFreshnessLifetime() {
        CacheControl responseCaching = this.cacheResponse.cacheControl();
        if (responseCaching.maxAgeSeconds() != -1) {
            return TimeUnit.SECONDS.toMillis((long) responseCaching.maxAgeSeconds());
        }
        long j = 0;
        Date date;
        long servedMillis;
        long delta;
        if (this.expires != null) {
            date = this.servedDate;
            if (date != null) {
                servedMillis = date.getTime();
            } else {
                servedMillis = this.receivedResponseMillis;
            }
            delta = this.expires.getTime() - servedMillis;
            if (delta > 0) {
                j = delta;
            }
            return j;
        }
        if (this.lastModified != null) {
            if (this.cacheResponse.request().url().query() == null) {
                date = this.servedDate;
                if (date != null) {
                    servedMillis = date.getTime();
                } else {
                    servedMillis = this.sentRequestMillis;
                }
                delta = servedMillis - this.lastModified.getTime();
                if (delta > 0) {
                    j = delta / 10;
                }
                return j;
            }
        }
        return 0;
    }

    private long cacheResponseAge() {
        long receivedAge;
        Date date = this.servedDate;
        long j = 0;
        if (date != null) {
            j = Math.max(0, this.receivedResponseMillis - date.getTime());
        }
        long apparentReceivedAge = j;
        if (this.ageSeconds != -1) {
            receivedAge = Math.max(apparentReceivedAge, TimeUnit.SECONDS.toMillis((long) this.ageSeconds));
        } else {
            receivedAge = apparentReceivedAge;
        }
        long j2 = this.receivedResponseMillis;
        return (receivedAge + (j2 - this.sentRequestMillis)) + (this.nowMillis - j2);
    }

    private boolean isFreshnessLifetimeHeuristic() {
        return this.cacheResponse.cacheControl().maxAgeSeconds() == -1 && this.expires == null;
    }

    private static boolean hasConditions(Request request) {
        if (request.header("If-Modified-Since") == null) {
            if (request.header("If-None-Match") == null) {
                return false;
            }
        }
        return true;
    }
}
