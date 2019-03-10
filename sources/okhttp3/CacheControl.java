package okhttp3;

import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import okhttp3.internal.http.HttpHeaders;

public final class CacheControl {
    public static final CacheControl FORCE_CACHE = new CacheControl$Builder().onlyIfCached().maxStale(Integer.MAX_VALUE, TimeUnit.SECONDS).build();
    public static final CacheControl FORCE_NETWORK = new CacheControl$Builder().noCache().build();
    @Nullable
    String headerValue;
    private final boolean immutable;
    private final boolean isPrivate;
    private final boolean isPublic;
    private final int maxAgeSeconds;
    private final int maxStaleSeconds;
    private final int minFreshSeconds;
    private final boolean mustRevalidate;
    private final boolean noCache;
    private final boolean noStore;
    private final boolean noTransform;
    private final boolean onlyIfCached;
    private final int sMaxAgeSeconds;

    private CacheControl(boolean noCache, boolean noStore, int maxAgeSeconds, int sMaxAgeSeconds, boolean isPrivate, boolean isPublic, boolean mustRevalidate, int maxStaleSeconds, int minFreshSeconds, boolean onlyIfCached, boolean noTransform, boolean immutable, @Nullable String headerValue) {
        this.noCache = noCache;
        this.noStore = noStore;
        this.maxAgeSeconds = maxAgeSeconds;
        this.sMaxAgeSeconds = sMaxAgeSeconds;
        this.isPrivate = isPrivate;
        this.isPublic = isPublic;
        this.mustRevalidate = mustRevalidate;
        this.maxStaleSeconds = maxStaleSeconds;
        this.minFreshSeconds = minFreshSeconds;
        this.onlyIfCached = onlyIfCached;
        this.noTransform = noTransform;
        this.immutable = immutable;
        this.headerValue = headerValue;
    }

    CacheControl(CacheControl$Builder builder) {
        this.noCache = builder.noCache;
        this.noStore = builder.noStore;
        this.maxAgeSeconds = builder.maxAgeSeconds;
        this.sMaxAgeSeconds = -1;
        this.isPrivate = false;
        this.isPublic = false;
        this.mustRevalidate = false;
        this.maxStaleSeconds = builder.maxStaleSeconds;
        this.minFreshSeconds = builder.minFreshSeconds;
        this.onlyIfCached = builder.onlyIfCached;
        this.noTransform = builder.noTransform;
        this.immutable = builder.immutable;
    }

    public boolean noCache() {
        return this.noCache;
    }

    public boolean noStore() {
        return this.noStore;
    }

    public int maxAgeSeconds() {
        return this.maxAgeSeconds;
    }

    public int sMaxAgeSeconds() {
        return this.sMaxAgeSeconds;
    }

    public boolean isPrivate() {
        return this.isPrivate;
    }

    public boolean isPublic() {
        return this.isPublic;
    }

    public boolean mustRevalidate() {
        return this.mustRevalidate;
    }

    public int maxStaleSeconds() {
        return this.maxStaleSeconds;
    }

    public int minFreshSeconds() {
        return this.minFreshSeconds;
    }

    public boolean onlyIfCached() {
        return this.onlyIfCached;
    }

    public boolean noTransform() {
        return this.noTransform;
    }

    public boolean immutable() {
        return this.immutable;
    }

    public static CacheControl parse(Headers headers) {
        boolean immutable;
        Headers headers2 = headers;
        int maxStaleSeconds = -1;
        int minFreshSeconds = -1;
        boolean onlyIfCached = false;
        boolean noTransform = false;
        boolean immutable2 = false;
        boolean canUseHeaderValue = true;
        String headerValue = null;
        int i = 0;
        boolean noCache = false;
        int size = headers.size();
        boolean mustRevalidate = false;
        boolean isPublic = false;
        boolean isPrivate = false;
        int sMaxAgeSeconds = -1;
        int maxAgeSeconds = -1;
        boolean noStore = false;
        boolean noCache2 = noCache;
        while (i < size) {
            boolean noCache3;
            boolean noStore2;
            int size2 = size;
            size = headers2.name(i);
            immutable = immutable2;
            String value = headers2.value(i);
            if (size.equalsIgnoreCase("Cache-Control")) {
                if (headerValue != null) {
                    canUseHeaderValue = false;
                } else {
                    headerValue = value;
                }
            } else if (size.equalsIgnoreCase("Pragma")) {
                canUseHeaderValue = false;
            } else {
                String name = size;
                immutable2 = immutable;
                i++;
                size = size2;
                headers2 = headers;
            }
            int pos = 0;
            while (true) {
                name = size;
                if (pos >= value.length()) {
                    break;
                }
                String parameter;
                int pos2;
                size = pos;
                noCache3 = noCache2;
                pos = HttpHeaders.skipUntil(value, pos, "=,;");
                String directive = value.substring(size, pos).trim();
                int tokenStart = size;
                if (pos != value.length()) {
                    noStore2 = noStore;
                    if (!value.charAt(pos)) {
                        if (!value.charAt(pos)) {
                            pos = HttpHeaders.skipWhitespace(value, pos + 1);
                            if (pos >= value.length() || !value.charAt(pos)) {
                                size = pos;
                                pos = HttpHeaders.skipUntil(value, pos, ",;");
                                parameter = value.substring(size, pos).trim();
                            } else {
                                pos++;
                                size = pos;
                                pos = HttpHeaders.skipUntil(value, pos, "\"");
                                parameter = value.substring(size, pos);
                                pos++;
                            }
                            if ("no-cache".equalsIgnoreCase(directive) != 0) {
                                pos2 = pos;
                                noCache2 = true;
                                noStore = noStore2;
                            } else if ("no-store".equalsIgnoreCase(directive) == 0) {
                                pos2 = pos;
                                noStore = true;
                                noCache2 = noCache3;
                            } else {
                                pos2 = pos;
                                if ("max-age".equalsIgnoreCase(directive) != 0) {
                                    maxAgeSeconds = HttpHeaders.parseSeconds(parameter, -1);
                                    noCache2 = noCache3;
                                    noStore = noStore2;
                                } else if ("s-maxage".equalsIgnoreCase(directive) != 0) {
                                    sMaxAgeSeconds = HttpHeaders.parseSeconds(parameter, -1);
                                    noCache2 = noCache3;
                                    noStore = noStore2;
                                } else if ("private".equalsIgnoreCase(directive) != 0) {
                                    isPrivate = true;
                                    noCache2 = noCache3;
                                    noStore = noStore2;
                                } else if ("public".equalsIgnoreCase(directive) != 0) {
                                    isPublic = true;
                                    noCache2 = noCache3;
                                    noStore = noStore2;
                                } else if ("must-revalidate".equalsIgnoreCase(directive) != 0) {
                                    mustRevalidate = true;
                                    noCache2 = noCache3;
                                    noStore = noStore2;
                                } else if ("max-stale".equalsIgnoreCase(directive) != 0) {
                                    maxStaleSeconds = HttpHeaders.parseSeconds(parameter, Integer.MAX_VALUE);
                                    noCache2 = noCache3;
                                    noStore = noStore2;
                                } else if ("min-fresh".equalsIgnoreCase(directive) != 0) {
                                    minFreshSeconds = HttpHeaders.parseSeconds(parameter, -1);
                                    noCache2 = noCache3;
                                    noStore = noStore2;
                                } else if ("only-if-cached".equalsIgnoreCase(directive)) {
                                    onlyIfCached = true;
                                    noCache2 = noCache3;
                                    noStore = noStore2;
                                } else if ("no-transform".equalsIgnoreCase(directive)) {
                                    noTransform = true;
                                    noCache2 = noCache3;
                                    noStore = noStore2;
                                } else if ("immutable".equalsIgnoreCase(directive)) {
                                    noCache2 = noCache3;
                                    noStore = noStore2;
                                } else {
                                    immutable = true;
                                    noCache2 = noCache3;
                                    noStore = noStore2;
                                }
                            }
                            size = name;
                            pos = pos2;
                        }
                    }
                } else {
                    noStore2 = noStore;
                }
                pos++;
                parameter = null;
                if ("no-cache".equalsIgnoreCase(directive) != 0) {
                    pos2 = pos;
                    noCache2 = true;
                    noStore = noStore2;
                } else if ("no-store".equalsIgnoreCase(directive) == 0) {
                    pos2 = pos;
                    if ("max-age".equalsIgnoreCase(directive) != 0) {
                        maxAgeSeconds = HttpHeaders.parseSeconds(parameter, -1);
                        noCache2 = noCache3;
                        noStore = noStore2;
                    } else if ("s-maxage".equalsIgnoreCase(directive) != 0) {
                        sMaxAgeSeconds = HttpHeaders.parseSeconds(parameter, -1);
                        noCache2 = noCache3;
                        noStore = noStore2;
                    } else if ("private".equalsIgnoreCase(directive) != 0) {
                        isPrivate = true;
                        noCache2 = noCache3;
                        noStore = noStore2;
                    } else if ("public".equalsIgnoreCase(directive) != 0) {
                        isPublic = true;
                        noCache2 = noCache3;
                        noStore = noStore2;
                    } else if ("must-revalidate".equalsIgnoreCase(directive) != 0) {
                        mustRevalidate = true;
                        noCache2 = noCache3;
                        noStore = noStore2;
                    } else if ("max-stale".equalsIgnoreCase(directive) != 0) {
                        maxStaleSeconds = HttpHeaders.parseSeconds(parameter, Integer.MAX_VALUE);
                        noCache2 = noCache3;
                        noStore = noStore2;
                    } else if ("min-fresh".equalsIgnoreCase(directive) != 0) {
                        minFreshSeconds = HttpHeaders.parseSeconds(parameter, -1);
                        noCache2 = noCache3;
                        noStore = noStore2;
                    } else if ("only-if-cached".equalsIgnoreCase(directive)) {
                        onlyIfCached = true;
                        noCache2 = noCache3;
                        noStore = noStore2;
                    } else if ("no-transform".equalsIgnoreCase(directive)) {
                        noTransform = true;
                        noCache2 = noCache3;
                        noStore = noStore2;
                    } else if ("immutable".equalsIgnoreCase(directive)) {
                        noCache2 = noCache3;
                        noStore = noStore2;
                    } else {
                        immutable = true;
                        noCache2 = noCache3;
                        noStore = noStore2;
                    }
                } else {
                    pos2 = pos;
                    noStore = true;
                    noCache2 = noCache3;
                }
                size = name;
                pos = pos2;
            }
            noCache3 = noCache2;
            noStore2 = noStore;
            immutable2 = immutable;
            i++;
            size = size2;
            headers2 = headers;
        }
        immutable = immutable2;
        if (!canUseHeaderValue) {
            headerValue = null;
        }
        return new CacheControl(noCache2, noStore, maxAgeSeconds, sMaxAgeSeconds, isPrivate, isPublic, mustRevalidate, maxStaleSeconds, minFreshSeconds, onlyIfCached, noTransform, immutable, headerValue);
    }

    public String toString() {
        String result = this.headerValue;
        if (result != null) {
            return result;
        }
        String headerValue = headerValue();
        this.headerValue = headerValue;
        return headerValue;
    }

    private String headerValue() {
        StringBuilder result = new StringBuilder();
        if (this.noCache) {
            result.append("no-cache, ");
        }
        if (this.noStore) {
            result.append("no-store, ");
        }
        if (this.maxAgeSeconds != -1) {
            result.append("max-age=");
            result.append(this.maxAgeSeconds);
            result.append(", ");
        }
        if (this.sMaxAgeSeconds != -1) {
            result.append("s-maxage=");
            result.append(this.sMaxAgeSeconds);
            result.append(", ");
        }
        if (this.isPrivate) {
            result.append("private, ");
        }
        if (this.isPublic) {
            result.append("public, ");
        }
        if (this.mustRevalidate) {
            result.append("must-revalidate, ");
        }
        if (this.maxStaleSeconds != -1) {
            result.append("max-stale=");
            result.append(this.maxStaleSeconds);
            result.append(", ");
        }
        if (this.minFreshSeconds != -1) {
            result.append("min-fresh=");
            result.append(this.minFreshSeconds);
            result.append(", ");
        }
        if (this.onlyIfCached) {
            result.append("only-if-cached, ");
        }
        if (this.noTransform) {
            result.append("no-transform, ");
        }
        if (this.immutable) {
            result.append("immutable, ");
        }
        if (result.length() == 0) {
            return "";
        }
        result.delete(result.length() - 2, result.length());
        return result.toString();
    }
}
