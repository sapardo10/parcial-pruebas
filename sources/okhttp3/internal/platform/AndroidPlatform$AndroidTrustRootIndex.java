package okhttp3.internal.platform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;
import okhttp3.internal.Util;
import okhttp3.internal.tls.TrustRootIndex;

final class AndroidPlatform$AndroidTrustRootIndex implements TrustRootIndex {
    private final Method findByIssuerAndSignatureMethod;
    private final X509TrustManager trustManager;

    AndroidPlatform$AndroidTrustRootIndex(X509TrustManager trustManager, Method findByIssuerAndSignatureMethod) {
        this.findByIssuerAndSignatureMethod = findByIssuerAndSignatureMethod;
        this.trustManager = trustManager;
    }

    public X509Certificate findByIssuerAndSignature(X509Certificate cert) {
        X509Certificate x509Certificate = null;
        try {
            TrustAnchor trustAnchor = (TrustAnchor) this.findByIssuerAndSignatureMethod.invoke(this.trustManager, new Object[]{cert});
            if (trustAnchor != null) {
                x509Certificate = trustAnchor.getTrustedCert();
            }
            return x509Certificate;
        } catch (IllegalAccessException e) {
            throw Util.assertionError("unable to get issues and signature", e);
        } catch (InvocationTargetException e2) {
            return null;
        }
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AndroidPlatform$AndroidTrustRootIndex)) {
            return false;
        }
        AndroidPlatform$AndroidTrustRootIndex that = (AndroidPlatform$AndroidTrustRootIndex) obj;
        if (this.trustManager.equals(that.trustManager)) {
            if (this.findByIssuerAndSignatureMethod.equals(that.findByIssuerAndSignatureMethod)) {
                return z;
            }
        }
        z = false;
        return z;
    }

    public int hashCode() {
        return this.trustManager.hashCode() + (this.findByIssuerAndSignatureMethod.hashCode() * 31);
    }
}
