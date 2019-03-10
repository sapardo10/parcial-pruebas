package okhttp3;

import java.net.URL;
import java.util.List;
import javax.annotation.Nullable;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpMethod;

public final class Request {
    @Nullable
    final RequestBody body;
    private volatile CacheControl cacheControl;
    final Headers headers;
    final String method;
    final Object tag;
    final HttpUrl url;

    public static class Builder {
        RequestBody body;
        Headers$Builder headers;
        String method;
        Object tag;
        HttpUrl url;

        public Builder() {
            this.method = "GET";
            this.headers = new Headers$Builder();
        }

        Builder(Request request) {
            this.url = request.url;
            this.method = request.method;
            this.body = request.body;
            this.tag = request.tag;
            this.headers = request.headers.newBuilder();
        }

        public Builder url(HttpUrl url) {
            if (url != null) {
                this.url = url;
                return this;
            }
            throw new NullPointerException("url == null");
        }

        public Builder url(String url) {
            if (url != null) {
                StringBuilder stringBuilder;
                if (url.regionMatches(true, 0, "ws:", 0, 3)) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("http:");
                    stringBuilder.append(url.substring(3));
                    url = stringBuilder.toString();
                } else {
                    if (url.regionMatches(true, 0, "wss:", 0, 4)) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("https:");
                        stringBuilder.append(url.substring(4));
                        url = stringBuilder.toString();
                    }
                }
                HttpUrl parsed = HttpUrl.parse(url);
                if (parsed != null) {
                    return url(parsed);
                }
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("unexpected url: ");
                stringBuilder2.append(url);
                throw new IllegalArgumentException(stringBuilder2.toString());
            }
            throw new NullPointerException("url == null");
        }

        public Builder url(URL url) {
            if (url != null) {
                HttpUrl parsed = HttpUrl.get(url);
                if (parsed != null) {
                    return url(parsed);
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("unexpected url: ");
                stringBuilder.append(url);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
            throw new NullPointerException("url == null");
        }

        public Builder header(String name, String value) {
            this.headers.set(name, value);
            return this;
        }

        public Builder addHeader(String name, String value) {
            this.headers.add(name, value);
            return this;
        }

        public Builder removeHeader(String name) {
            this.headers.removeAll(name);
            return this;
        }

        public Builder headers(Headers headers) {
            this.headers = headers.newBuilder();
            return this;
        }

        public Builder cacheControl(CacheControl cacheControl) {
            String value = cacheControl.toString();
            if (value.isEmpty()) {
                return removeHeader("Cache-Control");
            }
            return header("Cache-Control", value);
        }

        public Builder get() {
            return method("GET", null);
        }

        public Builder head() {
            return method("HEAD", null);
        }

        public Builder post(RequestBody body) {
            return method("POST", body);
        }

        public Builder delete(@Nullable RequestBody body) {
            return method("DELETE", body);
        }

        public Builder delete() {
            return delete(Util.EMPTY_REQUEST);
        }

        public Builder put(RequestBody body) {
            return method("PUT", body);
        }

        public Builder patch(RequestBody body) {
            return method("PATCH", body);
        }

        public Builder method(String method, @Nullable RequestBody body) {
            if (method == null) {
                throw new NullPointerException("method == null");
            } else if (method.length() != 0) {
                StringBuilder stringBuilder;
                if (body != null) {
                    if (!HttpMethod.permitsRequestBody(method)) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("method ");
                        stringBuilder.append(method);
                        stringBuilder.append(" must not have a request body.");
                        throw new IllegalArgumentException(stringBuilder.toString());
                    }
                }
                if (body == null) {
                    if (HttpMethod.requiresRequestBody(method)) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("method ");
                        stringBuilder.append(method);
                        stringBuilder.append(" must have a request body.");
                        throw new IllegalArgumentException(stringBuilder.toString());
                    }
                }
                this.method = method;
                this.body = body;
                return this;
            } else {
                throw new IllegalArgumentException("method.length() == 0");
            }
        }

        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Request build() {
            if (this.url != null) {
                return new Request(this);
            }
            throw new IllegalStateException("url == null");
        }
    }

    Request(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers.build();
        this.body = builder.body;
        this.tag = builder.tag != null ? builder.tag : this;
    }

    public HttpUrl url() {
        return this.url;
    }

    public String method() {
        return this.method;
    }

    public Headers headers() {
        return this.headers;
    }

    @Nullable
    public String header(String name) {
        return this.headers.get(name);
    }

    public List<String> headers(String name) {
        return this.headers.values(name);
    }

    @Nullable
    public RequestBody body() {
        return this.body;
    }

    public Object tag() {
        return this.tag;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public CacheControl cacheControl() {
        CacheControl result = this.cacheControl;
        if (result != null) {
            return result;
        }
        CacheControl parse = CacheControl.parse(this.headers);
        this.cacheControl = parse;
        return parse;
    }

    public boolean isHttps() {
        return this.url.isHttps();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Request{method=");
        stringBuilder.append(this.method);
        stringBuilder.append(", url=");
        stringBuilder.append(this.url);
        stringBuilder.append(", tag=");
        Object obj = this.tag;
        if (obj == this) {
            obj = null;
        }
        stringBuilder.append(obj);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
