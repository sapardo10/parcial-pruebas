package okhttp3;

import java.net.Proxy;
import java.net.ProxySelector;
import java.util.List;
import javax.annotation.Nullable;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.HttpUrl.Builder;
import okhttp3.internal.Util;

public final class Address {
    @Nullable
    final CertificatePinner certificatePinner;
    final List<ConnectionSpec> connectionSpecs;
    final Dns dns;
    @Nullable
    final HostnameVerifier hostnameVerifier;
    final List<Protocol> protocols;
    @Nullable
    final Proxy proxy;
    final Authenticator proxyAuthenticator;
    final ProxySelector proxySelector;
    final SocketFactory socketFactory;
    @Nullable
    final SSLSocketFactory sslSocketFactory;
    final HttpUrl url;

    public Address(String uriHost, int uriPort, Dns dns, SocketFactory socketFactory, @Nullable SSLSocketFactory sslSocketFactory, @Nullable HostnameVerifier hostnameVerifier, @Nullable CertificatePinner certificatePinner, Authenticator proxyAuthenticator, @Nullable Proxy proxy, List<Protocol> protocols, List<ConnectionSpec> connectionSpecs, ProxySelector proxySelector) {
        this.url = new Builder().scheme(sslSocketFactory != null ? "https" : "http").host(uriHost).port(uriPort).build();
        if (dns != null) {
            this.dns = dns;
            if (socketFactory != null) {
                this.socketFactory = socketFactory;
                if (proxyAuthenticator != null) {
                    this.proxyAuthenticator = proxyAuthenticator;
                    if (protocols != null) {
                        this.protocols = Util.immutableList((List) protocols);
                        if (connectionSpecs != null) {
                            this.connectionSpecs = Util.immutableList((List) connectionSpecs);
                            if (proxySelector != null) {
                                this.proxySelector = proxySelector;
                                this.proxy = proxy;
                                this.sslSocketFactory = sslSocketFactory;
                                this.hostnameVerifier = hostnameVerifier;
                                this.certificatePinner = certificatePinner;
                                return;
                            }
                            throw new NullPointerException("proxySelector == null");
                        }
                        throw new NullPointerException("connectionSpecs == null");
                    }
                    throw new NullPointerException("protocols == null");
                }
                throw new NullPointerException("proxyAuthenticator == null");
            }
            throw new NullPointerException("socketFactory == null");
        }
        throw new NullPointerException("dns == null");
    }

    public HttpUrl url() {
        return this.url;
    }

    public Dns dns() {
        return this.dns;
    }

    public SocketFactory socketFactory() {
        return this.socketFactory;
    }

    public Authenticator proxyAuthenticator() {
        return this.proxyAuthenticator;
    }

    public List<Protocol> protocols() {
        return this.protocols;
    }

    public List<ConnectionSpec> connectionSpecs() {
        return this.connectionSpecs;
    }

    public ProxySelector proxySelector() {
        return this.proxySelector;
    }

    @Nullable
    public Proxy proxy() {
        return this.proxy;
    }

    @Nullable
    public SSLSocketFactory sslSocketFactory() {
        return this.sslSocketFactory;
    }

    @Nullable
    public HostnameVerifier hostnameVerifier() {
        return this.hostnameVerifier;
    }

    @Nullable
    public CertificatePinner certificatePinner() {
        return this.certificatePinner;
    }

    public boolean equals(@Nullable Object other) {
        if (other instanceof Address) {
            if (this.url.equals(((Address) other).url)) {
                if (equalsNonHost((Address) other)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int hashCode() {
        int hashCode = ((((((((((((17 * 31) + this.url.hashCode()) * 31) + this.dns.hashCode()) * 31) + this.proxyAuthenticator.hashCode()) * 31) + this.protocols.hashCode()) * 31) + this.connectionSpecs.hashCode()) * 31) + this.proxySelector.hashCode()) * 31;
        Proxy proxy = this.proxy;
        int i = 0;
        int hashCode2 = (hashCode + (proxy != null ? proxy.hashCode() : 0)) * 31;
        SSLSocketFactory sSLSocketFactory = this.sslSocketFactory;
        hashCode = (hashCode2 + (sSLSocketFactory != null ? sSLSocketFactory.hashCode() : 0)) * 31;
        HostnameVerifier hostnameVerifier = this.hostnameVerifier;
        hashCode2 = (hashCode + (hostnameVerifier != null ? hostnameVerifier.hashCode() : 0)) * 31;
        CertificatePinner certificatePinner = this.certificatePinner;
        if (certificatePinner != null) {
            i = certificatePinner.hashCode();
        }
        return hashCode2 + i;
    }

    boolean equalsNonHost(Address that) {
        if (this.dns.equals(that.dns)) {
            if (this.proxyAuthenticator.equals(that.proxyAuthenticator)) {
                if (this.protocols.equals(that.protocols)) {
                    if (this.connectionSpecs.equals(that.connectionSpecs)) {
                        if (this.proxySelector.equals(that.proxySelector)) {
                            if (Util.equal(this.proxy, that.proxy)) {
                                if (Util.equal(this.sslSocketFactory, that.sslSocketFactory)) {
                                    if (Util.equal(this.hostnameVerifier, that.hostnameVerifier)) {
                                        if (Util.equal(this.certificatePinner, that.certificatePinner)) {
                                            if (url().port() == that.url().port()) {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Address{");
        result.append(this.url.host());
        result.append(":");
        result = result.append(this.url.port());
        if (this.proxy != null) {
            result.append(", proxy=");
            result.append(this.proxy);
        } else {
            result.append(", proxySelector=");
            result.append(this.proxySelector);
        }
        result.append("}");
        return result.toString();
    }
}
