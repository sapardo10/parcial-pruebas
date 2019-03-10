package okhttp3;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import javax.net.ssl.SSLSocket;
import okhttp3.internal.Util;

public final class ConnectionSpec {
    private static final CipherSuite[] APPROVED_CIPHER_SUITES = new CipherSuite[]{CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA, CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA, CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA};
    public static final ConnectionSpec CLEARTEXT = new Builder(false).build();
    public static final ConnectionSpec COMPATIBLE_TLS = new Builder(MODERN_TLS).tlsVersions(TlsVersion.TLS_1_0).supportsTlsExtensions(true).build();
    public static final ConnectionSpec MODERN_TLS = new Builder(true).cipherSuites(APPROVED_CIPHER_SUITES).tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0).supportsTlsExtensions(true).build();
    @Nullable
    final String[] cipherSuites;
    final boolean supportsTlsExtensions;
    final boolean tls;
    @Nullable
    final String[] tlsVersions;

    public static final class Builder {
        @Nullable
        String[] cipherSuites;
        boolean supportsTlsExtensions;
        boolean tls;
        @Nullable
        String[] tlsVersions;

        public okhttp3.ConnectionSpec.Builder cipherSuites(okhttp3.CipherSuite... r4) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x0021 in {5, 7, 9} preds:[]
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
            r3 = this;
            r0 = r3.tls;
            if (r0 == 0) goto L_0x0019;
        L_0x0004:
            r0 = r4.length;
            r0 = new java.lang.String[r0];
            r1 = 0;
        L_0x0008:
            r2 = r4.length;
            if (r1 >= r2) goto L_0x0014;
        L_0x000b:
            r2 = r4[r1];
            r2 = r2.javaName;
            r0[r1] = r2;
            r1 = r1 + 1;
            goto L_0x0008;
        L_0x0014:
            r1 = r3.cipherSuites(r0);
            return r1;
        L_0x0019:
            r0 = new java.lang.IllegalStateException;
            r1 = "no cipher suites for cleartext connections";
            r0.<init>(r1);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.ConnectionSpec.Builder.cipherSuites(okhttp3.CipherSuite[]):okhttp3.ConnectionSpec$Builder");
        }

        public okhttp3.ConnectionSpec.Builder tlsVersions(okhttp3.TlsVersion... r4) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:10:0x0021 in {5, 7, 9} preds:[]
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
            r3 = this;
            r0 = r3.tls;
            if (r0 == 0) goto L_0x0019;
        L_0x0004:
            r0 = r4.length;
            r0 = new java.lang.String[r0];
            r1 = 0;
        L_0x0008:
            r2 = r4.length;
            if (r1 >= r2) goto L_0x0014;
        L_0x000b:
            r2 = r4[r1];
            r2 = r2.javaName;
            r0[r1] = r2;
            r1 = r1 + 1;
            goto L_0x0008;
        L_0x0014:
            r1 = r3.tlsVersions(r0);
            return r1;
        L_0x0019:
            r0 = new java.lang.IllegalStateException;
            r1 = "no TLS versions for cleartext connections";
            r0.<init>(r1);
            throw r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.ConnectionSpec.Builder.tlsVersions(okhttp3.TlsVersion[]):okhttp3.ConnectionSpec$Builder");
        }

        Builder(boolean tls) {
            this.tls = tls;
        }

        public Builder(ConnectionSpec connectionSpec) {
            this.tls = connectionSpec.tls;
            this.cipherSuites = connectionSpec.cipherSuites;
            this.tlsVersions = connectionSpec.tlsVersions;
            this.supportsTlsExtensions = connectionSpec.supportsTlsExtensions;
        }

        public Builder allEnabledCipherSuites() {
            if (this.tls) {
                this.cipherSuites = null;
                return this;
            }
            throw new IllegalStateException("no cipher suites for cleartext connections");
        }

        public Builder cipherSuites(String... cipherSuites) {
            if (!this.tls) {
                throw new IllegalStateException("no cipher suites for cleartext connections");
            } else if (cipherSuites.length != 0) {
                this.cipherSuites = (String[]) cipherSuites.clone();
                return this;
            } else {
                throw new IllegalArgumentException("At least one cipher suite is required");
            }
        }

        public Builder allEnabledTlsVersions() {
            if (this.tls) {
                this.tlsVersions = null;
                return this;
            }
            throw new IllegalStateException("no TLS versions for cleartext connections");
        }

        public Builder tlsVersions(String... tlsVersions) {
            if (!this.tls) {
                throw new IllegalStateException("no TLS versions for cleartext connections");
            } else if (tlsVersions.length != 0) {
                this.tlsVersions = (String[]) tlsVersions.clone();
                return this;
            } else {
                throw new IllegalArgumentException("At least one TLS version is required");
            }
        }

        public Builder supportsTlsExtensions(boolean supportsTlsExtensions) {
            if (this.tls) {
                this.supportsTlsExtensions = supportsTlsExtensions;
                return this;
            }
            throw new IllegalStateException("no TLS extensions for cleartext connections");
        }

        public ConnectionSpec build() {
            return new ConnectionSpec(this);
        }
    }

    ConnectionSpec(Builder builder) {
        this.tls = builder.tls;
        this.cipherSuites = builder.cipherSuites;
        this.tlsVersions = builder.tlsVersions;
        this.supportsTlsExtensions = builder.supportsTlsExtensions;
    }

    public boolean isTls() {
        return this.tls;
    }

    @Nullable
    public List<CipherSuite> cipherSuites() {
        String[] strArr = this.cipherSuites;
        return strArr != null ? CipherSuite.forJavaNames(strArr) : null;
    }

    @Nullable
    public List<TlsVersion> tlsVersions() {
        String[] strArr = this.tlsVersions;
        return strArr != null ? TlsVersion.forJavaNames(strArr) : null;
    }

    public boolean supportsTlsExtensions() {
        return this.supportsTlsExtensions;
    }

    void apply(SSLSocket sslSocket, boolean isFallback) {
        ConnectionSpec specToApply = supportedSpec(sslSocket, isFallback);
        String[] strArr = specToApply.tlsVersions;
        if (strArr != null) {
            sslSocket.setEnabledProtocols(strArr);
        }
        strArr = specToApply.cipherSuites;
        if (strArr != null) {
            sslSocket.setEnabledCipherSuites(strArr);
        }
    }

    private ConnectionSpec supportedSpec(SSLSocket sslSocket, boolean isFallback) {
        String[] cipherSuitesIntersection;
        String[] tlsVersionsIntersection;
        if (this.cipherSuites != null) {
            cipherSuitesIntersection = Util.intersect(CipherSuite.ORDER_BY_NAME, sslSocket.getEnabledCipherSuites(), this.cipherSuites);
        } else {
            cipherSuitesIntersection = sslSocket.getEnabledCipherSuites();
        }
        if (this.tlsVersions != null) {
            tlsVersionsIntersection = Util.intersect(Util.NATURAL_ORDER, sslSocket.getEnabledProtocols(), this.tlsVersions);
        } else {
            tlsVersionsIntersection = sslSocket.getEnabledProtocols();
        }
        String[] supportedCipherSuites = sslSocket.getSupportedCipherSuites();
        int indexOfFallbackScsv = Util.indexOf(CipherSuite.ORDER_BY_NAME, supportedCipherSuites, "TLS_FALLBACK_SCSV");
        if (isFallback && indexOfFallbackScsv != -1) {
            cipherSuitesIntersection = Util.concat(cipherSuitesIntersection, supportedCipherSuites[indexOfFallbackScsv]);
        }
        return new Builder(this).cipherSuites(cipherSuitesIntersection).tlsVersions(tlsVersionsIntersection).build();
    }

    public boolean isCompatible(SSLSocket socket) {
        if (!this.tls) {
            return false;
        }
        if (this.tlsVersions != null && !Util.nonEmptyIntersection(Util.NATURAL_ORDER, this.tlsVersions, socket.getEnabledProtocols())) {
            return false;
        }
        if (this.cipherSuites == null || Util.nonEmptyIntersection(CipherSuite.ORDER_BY_NAME, this.cipherSuites, socket.getEnabledCipherSuites())) {
            return true;
        }
        return false;
    }

    public boolean equals(@Nullable Object other) {
        if (!(other instanceof ConnectionSpec)) {
            return false;
        }
        if (other == this) {
            return true;
        }
        ConnectionSpec that = (ConnectionSpec) other;
        boolean z = this.tls;
        if (z != that.tls) {
            return false;
        }
        if (z) {
            if (!Arrays.equals(this.cipherSuites, that.cipherSuites) || !Arrays.equals(this.tlsVersions, that.tlsVersions) || this.supportsTlsExtensions != that.supportsTlsExtensions) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        if (this.tls) {
            return (((((17 * 31) + Arrays.hashCode(this.cipherSuites)) * 31) + Arrays.hashCode(this.tlsVersions)) * 31) + (this.supportsTlsExtensions ^ 1);
        }
        return 17;
    }

    public String toString() {
        if (!this.tls) {
            return "ConnectionSpec()";
        }
        String cipherSuitesString = this.cipherSuites != null ? cipherSuites().toString() : "[all enabled]";
        String tlsVersionsString = this.tlsVersions != null ? tlsVersions().toString() : "[all enabled]";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ConnectionSpec(cipherSuites=");
        stringBuilder.append(cipherSuitesString);
        stringBuilder.append(", tlsVersions=");
        stringBuilder.append(tlsVersionsString);
        stringBuilder.append(", supportsTlsExtensions=");
        stringBuilder.append(this.supportsTlsExtensions);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
