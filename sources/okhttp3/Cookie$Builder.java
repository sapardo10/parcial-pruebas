package okhttp3;

import okhttp3.internal.Util;
import okhttp3.internal.http.HttpDate;

public final class Cookie$Builder {
    String domain;
    long expiresAt = HttpDate.MAX_DATE;
    boolean hostOnly;
    boolean httpOnly;
    String name;
    String path = "/";
    boolean persistent;
    boolean secure;
    String value;

    public Cookie$Builder name(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        } else if (name.trim().equals(name)) {
            this.name = name;
            return this;
        } else {
            throw new IllegalArgumentException("name is not trimmed");
        }
    }

    public Cookie$Builder value(String value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        } else if (value.trim().equals(value)) {
            this.value = value;
            return this;
        } else {
            throw new IllegalArgumentException("value is not trimmed");
        }
    }

    public Cookie$Builder expiresAt(long expiresAt) {
        if (expiresAt <= 0) {
            expiresAt = Long.MIN_VALUE;
        }
        if (expiresAt > HttpDate.MAX_DATE) {
            expiresAt = HttpDate.MAX_DATE;
        }
        this.expiresAt = expiresAt;
        this.persistent = true;
        return this;
    }

    public Cookie$Builder domain(String domain) {
        return domain(domain, false);
    }

    public Cookie$Builder hostOnlyDomain(String domain) {
        return domain(domain, true);
    }

    private Cookie$Builder domain(String domain, boolean hostOnly) {
        if (domain != null) {
            String canonicalDomain = Util.canonicalizeHost(domain);
            if (canonicalDomain != null) {
                this.domain = canonicalDomain;
                this.hostOnly = hostOnly;
                return this;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("unexpected domain: ");
            stringBuilder.append(domain);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        throw new NullPointerException("domain == null");
    }

    public Cookie$Builder path(String path) {
        if (path.startsWith("/")) {
            this.path = path;
            return this;
        }
        throw new IllegalArgumentException("path must start with '/'");
    }

    public Cookie$Builder secure() {
        this.secure = true;
        return this;
    }

    public Cookie$Builder httpOnly() {
        this.httpOnly = true;
        return this;
    }

    public Cookie build() {
        return new Cookie(this);
    }
}
