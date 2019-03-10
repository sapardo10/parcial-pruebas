package okhttp3.internal.cache;

import javax.annotation.Nullable;
import okhttp3.Request;
import okhttp3.Response;

public final class CacheStrategy {
    @Nullable
    public final Response cacheResponse;
    @Nullable
    public final Request networkRequest;

    CacheStrategy(Request networkRequest, Response cacheResponse) {
        this.networkRequest = networkRequest;
        this.cacheResponse = cacheResponse;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isCacheable(okhttp3.Response r3, okhttp3.Request r4) {
        /*
        r0 = r3.code();
        r1 = 0;
        switch(r0) {
            case 200: goto L_0x0035;
            case 203: goto L_0x0035;
            case 204: goto L_0x0035;
            case 300: goto L_0x0035;
            case 301: goto L_0x0035;
            case 302: goto L_0x0009;
            case 307: goto L_0x0009;
            case 308: goto L_0x0035;
            case 404: goto L_0x0035;
            case 405: goto L_0x0035;
            case 410: goto L_0x0035;
            case 414: goto L_0x0035;
            case 501: goto L_0x0035;
            default: goto L_0x0008;
        };
    L_0x0008:
        goto L_0x004d;
    L_0x0009:
        r0 = "Expires";
        r0 = r3.header(r0);
        if (r0 != 0) goto L_0x0034;
    L_0x0011:
        r0 = r3.cacheControl();
        r0 = r0.maxAgeSeconds();
        r2 = -1;
        if (r0 != r2) goto L_0x0033;
    L_0x001c:
        r0 = r3.cacheControl();
        r0 = r0.isPublic();
        if (r0 != 0) goto L_0x0032;
    L_0x0026:
        r0 = r3.cacheControl();
        r0 = r0.isPrivate();
        if (r0 == 0) goto L_0x0031;
    L_0x0030:
        goto L_0x0036;
    L_0x0031:
        goto L_0x004d;
    L_0x0032:
        goto L_0x0036;
    L_0x0033:
        goto L_0x0036;
    L_0x0034:
        goto L_0x0036;
    L_0x0036:
        r0 = r3.cacheControl();
        r0 = r0.noStore();
        if (r0 != 0) goto L_0x004c;
    L_0x0040:
        r0 = r4.cacheControl();
        r0 = r0.noStore();
        if (r0 != 0) goto L_0x004c;
    L_0x004a:
        r1 = 1;
    L_0x004c:
        return r1;
    L_0x004d:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.CacheStrategy.isCacheable(okhttp3.Response, okhttp3.Request):boolean");
    }
}
