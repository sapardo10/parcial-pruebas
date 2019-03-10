package okhttp3;

import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import okhttp3.internal.Util;

public final class Handshake {
    private final CipherSuite cipherSuite;
    private final List<Certificate> localCertificates;
    private final List<Certificate> peerCertificates;
    private final TlsVersion tlsVersion;

    private Handshake(TlsVersion tlsVersion, CipherSuite cipherSuite, List<Certificate> peerCertificates, List<Certificate> localCertificates) {
        this.tlsVersion = tlsVersion;
        this.cipherSuite = cipherSuite;
        this.peerCertificates = peerCertificates;
        this.localCertificates = localCertificates;
    }

    public static Handshake get(SSLSession session) {
        String cipherSuiteString = session.getCipherSuite();
        if (cipherSuiteString != null) {
            CipherSuite cipherSuite = CipherSuite.forJavaName(cipherSuiteString);
            String tlsVersionString = session.getProtocol();
            if (tlsVersionString != null) {
                Object[] peerCertificates;
                List<Certificate> peerCertificatesList;
                List<Certificate> localCertificatesList;
                TlsVersion tlsVersion = TlsVersion.forJavaName(tlsVersionString);
                try {
                    peerCertificates = session.getPeerCertificates();
                } catch (SSLPeerUnverifiedException e) {
                    peerCertificates = null;
                }
                if (peerCertificates != null) {
                    peerCertificatesList = Util.immutableList(peerCertificates);
                } else {
                    peerCertificatesList = Collections.emptyList();
                }
                Object[] localCertificates = session.getLocalCertificates();
                if (localCertificates != null) {
                    localCertificatesList = Util.immutableList(localCertificates);
                } else {
                    localCertificatesList = Collections.emptyList();
                }
                return new Handshake(tlsVersion, cipherSuite, peerCertificatesList, localCertificatesList);
            }
            throw new IllegalStateException("tlsVersion == null");
        }
        throw new IllegalStateException("cipherSuite == null");
    }

    public static Handshake get(TlsVersion tlsVersion, CipherSuite cipherSuite, List<Certificate> peerCertificates, List<Certificate> localCertificates) {
        if (tlsVersion == null) {
            throw new NullPointerException("tlsVersion == null");
        } else if (cipherSuite != null) {
            return new Handshake(tlsVersion, cipherSuite, Util.immutableList((List) peerCertificates), Util.immutableList((List) localCertificates));
        } else {
            throw new NullPointerException("cipherSuite == null");
        }
    }

    public TlsVersion tlsVersion() {
        return this.tlsVersion;
    }

    public CipherSuite cipherSuite() {
        return this.cipherSuite;
    }

    public List<Certificate> peerCertificates() {
        return this.peerCertificates;
    }

    @Nullable
    public Principal peerPrincipal() {
        if (this.peerCertificates.isEmpty()) {
            return null;
        }
        return ((X509Certificate) this.peerCertificates.get(0)).getSubjectX500Principal();
    }

    public List<Certificate> localCertificates() {
        return this.localCertificates;
    }

    @Nullable
    public Principal localPrincipal() {
        if (this.localCertificates.isEmpty()) {
            return null;
        }
        return ((X509Certificate) this.localCertificates.get(0)).getSubjectX500Principal();
    }

    public boolean equals(@Nullable Object other) {
        boolean z = false;
        if (!(other instanceof Handshake)) {
            return false;
        }
        Handshake that = (Handshake) other;
        if (this.tlsVersion.equals(that.tlsVersion)) {
            if (this.cipherSuite.equals(that.cipherSuite)) {
                if (this.peerCertificates.equals(that.peerCertificates)) {
                    if (this.localCertificates.equals(that.localCertificates)) {
                        z = true;
                        return z;
                    }
                }
            }
        }
        return z;
    }

    public int hashCode() {
        return (((((((17 * 31) + this.tlsVersion.hashCode()) * 31) + this.cipherSuite.hashCode()) * 31) + this.peerCertificates.hashCode()) * 31) + this.localCertificates.hashCode();
    }
}
