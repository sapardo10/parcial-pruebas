package okhttp3;

import okio.ByteString;

final class CertificatePinner$Pin {
    private static final String WILDCARD = "*.";
    final String canonicalHostname;
    final ByteString hash;
    final String hashAlgorithm;
    final String pattern;

    CertificatePinner$Pin(String pattern, String pin) {
        String host;
        this.pattern = pattern;
        StringBuilder stringBuilder;
        if (pattern.startsWith(WILDCARD)) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("http://");
            stringBuilder.append(pattern.substring(WILDCARD.length()));
            host = HttpUrl.parse(stringBuilder.toString()).host();
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("http://");
            stringBuilder.append(pattern);
            host = HttpUrl.parse(stringBuilder.toString()).host();
        }
        this.canonicalHostname = host;
        if (pin.startsWith("sha1/")) {
            this.hashAlgorithm = "sha1/";
            this.hash = ByteString.decodeBase64(pin.substring("sha1/".length()));
        } else if (pin.startsWith("sha256/")) {
            this.hashAlgorithm = "sha256/";
            this.hash = ByteString.decodeBase64(pin.substring("sha256/".length()));
        } else {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("pins must start with 'sha256/' or 'sha1/': ");
            stringBuilder2.append(pin);
            throw new IllegalArgumentException(stringBuilder2.toString());
        }
        if (this.hash == null) {
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("pins must be base64: ");
            stringBuilder2.append(pin);
            throw new IllegalArgumentException(stringBuilder2.toString());
        }
    }

    boolean matches(String hostname) {
        if (!this.pattern.startsWith(WILDCARD)) {
            return hostname.equals(this.canonicalHostname);
        }
        int firstDot = hostname.indexOf(46);
        boolean z = true;
        if ((hostname.length() - firstDot) - 1 == this.canonicalHostname.length()) {
            int i = firstDot + 1;
            String str = this.canonicalHostname;
            if (hostname.regionMatches(false, i, str, 0, str.length())) {
                return z;
            }
        }
        z = false;
        return z;
    }

    public boolean equals(Object other) {
        if (other instanceof CertificatePinner$Pin) {
            if (this.pattern.equals(((CertificatePinner$Pin) other).pattern)) {
                if (this.hashAlgorithm.equals(((CertificatePinner$Pin) other).hashAlgorithm)) {
                    if (this.hash.equals(((CertificatePinner$Pin) other).hash)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int hashCode() {
        return (((((17 * 31) + this.pattern.hashCode()) * 31) + this.hashAlgorithm.hashCode()) * 31) + this.hash.hashCode();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.hashAlgorithm);
        stringBuilder.append(this.hash.base64());
        return stringBuilder.toString();
    }
}
