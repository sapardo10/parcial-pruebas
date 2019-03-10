package okhttp3.internal.platform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.net.ssl.SSLPeerUnverifiedException;
import okhttp3.internal.tls.CertificateChainCleaner;

final class AndroidPlatform$AndroidCertificateChainCleaner extends CertificateChainCleaner {
    private final Method checkServerTrusted;
    private final Object x509TrustManagerExtensions;

    AndroidPlatform$AndroidCertificateChainCleaner(Object x509TrustManagerExtensions, Method checkServerTrusted) {
        this.x509TrustManagerExtensions = x509TrustManagerExtensions;
        this.checkServerTrusted = checkServerTrusted;
    }

    public List<Certificate> clean(List<Certificate> chain, String hostname) throws SSLPeerUnverifiedException {
        try {
            X509Certificate[] certificates = (X509Certificate[]) chain.toArray(new X509Certificate[chain.size()]);
            return (List) this.checkServerTrusted.invoke(this.x509TrustManagerExtensions, new Object[]{certificates, "RSA", hostname});
        } catch (InvocationTargetException e) {
            SSLPeerUnverifiedException exception = new SSLPeerUnverifiedException(e.getMessage());
            exception.initCause(e);
            throw exception;
        } catch (IllegalAccessException e2) {
            throw new AssertionError(e2);
        }
    }

    public boolean equals(Object other) {
        return other instanceof AndroidPlatform$AndroidCertificateChainCleaner;
    }

    public int hashCode() {
        return 0;
    }
}
