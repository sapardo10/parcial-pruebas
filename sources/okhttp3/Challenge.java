package okhttp3;

import java.nio.charset.Charset;
import javax.annotation.Nullable;
import okhttp3.internal.Util;

public final class Challenge {
    private final Charset charset;
    private final String realm;
    private final String scheme;

    public Challenge(String scheme, String realm) {
        this(scheme, realm, Util.ISO_8859_1);
    }

    private Challenge(String scheme, String realm, Charset charset) {
        if (scheme == null) {
            throw new NullPointerException("scheme == null");
        } else if (realm == null) {
            throw new NullPointerException("realm == null");
        } else if (charset != null) {
            this.scheme = scheme;
            this.realm = realm;
            this.charset = charset;
        } else {
            throw new NullPointerException("charset == null");
        }
    }

    public Challenge withCharset(Charset charset) {
        return new Challenge(this.scheme, this.realm, charset);
    }

    public String scheme() {
        return this.scheme;
    }

    public String realm() {
        return this.realm;
    }

    public Charset charset() {
        return this.charset;
    }

    public boolean equals(@Nullable Object other) {
        if (other instanceof Challenge) {
            if (((Challenge) other).scheme.equals(this.scheme)) {
                if (((Challenge) other).realm.equals(this.realm)) {
                    if (((Challenge) other).charset.equals(this.charset)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int hashCode() {
        return (((((29 * 31) + this.realm.hashCode()) * 31) + this.scheme.hashCode()) * 31) + this.charset.hashCode();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.scheme);
        stringBuilder.append(" realm=\"");
        stringBuilder.append(this.realm);
        stringBuilder.append("\" charset=\"");
        stringBuilder.append(this.charset);
        stringBuilder.append("\"");
        return stringBuilder.toString();
    }
}
