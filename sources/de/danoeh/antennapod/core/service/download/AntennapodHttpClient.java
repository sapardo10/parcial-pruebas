package de.danoeh.antennapod.core.service.download;

import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.storage.DBWriter;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.Interceptor$Chain;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.StatusLine;

public class AntennapodHttpClient {
    private static final int CONNECTION_TIMEOUT = 30000;
    private static final int MAX_CONNECTIONS = 8;
    private static final int READ_TIMEOUT = 30000;
    private static final String TAG = "AntennapodHttpClient";
    private static volatile OkHttpClient httpClient = null;

    private static class CustomSslSocketFactory extends SSLSocketFactory {
        private SSLSocketFactory factory;

        public CustomSslSocketFactory() {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, null, null);
                this.factory = sslContext.getSocketFactory();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }

        public String[] getDefaultCipherSuites() {
            return this.factory.getDefaultCipherSuites();
        }

        public String[] getSupportedCipherSuites() {
            return this.factory.getSupportedCipherSuites();
        }

        public Socket createSocket() throws IOException {
            SSLSocket result = (SSLSocket) this.factory.createSocket();
            configureSocket(result);
            return result;
        }

        public Socket createSocket(String var1, int var2) throws IOException {
            SSLSocket result = (SSLSocket) this.factory.createSocket(var1, var2);
            configureSocket(result);
            return result;
        }

        public Socket createSocket(Socket var1, String var2, int var3, boolean var4) throws IOException {
            SSLSocket result = (SSLSocket) this.factory.createSocket(var1, var2, var3, var4);
            configureSocket(result);
            return result;
        }

        public Socket createSocket(InetAddress var1, int var2) throws IOException {
            SSLSocket result = (SSLSocket) this.factory.createSocket(var1, var2);
            configureSocket(result);
            return result;
        }

        public Socket createSocket(String var1, int var2, InetAddress var3, int var4) throws IOException {
            SSLSocket result = (SSLSocket) this.factory.createSocket(var1, var2, var3, var4);
            configureSocket(result);
            return result;
        }

        public Socket createSocket(InetAddress var1, int var2, InetAddress var3, int var4) throws IOException {
            SSLSocket result = (SSLSocket) this.factory.createSocket(var1, var2, var3, var4);
            configureSocket(result);
            return result;
        }

        private void configureSocket(SSLSocket s) {
            s.setEnabledProtocols(new String[]{"TLSv1.2", "TLSv1.1", "TLSv1"});
        }
    }

    private AntennapodHttpClient() {
    }

    public static synchronized OkHttpClient getHttpClient() {
        OkHttpClient okHttpClient;
        synchronized (AntennapodHttpClient.class) {
            if (httpClient == null) {
                httpClient = newBuilder().build();
            }
            okHttpClient = httpClient;
        }
        return okHttpClient;
    }

    public static synchronized void reinit() {
        synchronized (AntennapodHttpClient.class) {
            httpClient = newBuilder().build();
        }
    }

    @NonNull
    public static Builder newBuilder() {
        Log.d(TAG, "Creating new instance of HTTP client");
        System.setProperty("http.maxConnections", String.valueOf(8));
        Builder builder = new Builder();
        builder.networkInterceptors().add(-$$Lambda$AntennapodHttpClient$2Xq9YggjpSacmHedsJk0juFgtBc.INSTANCE);
        CookieManager cm = new CookieManager();
        cm.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        builder.cookieJar(new JavaNetCookieJar(cm));
        builder.connectTimeout(30000, TimeUnit.MILLISECONDS);
        builder.readTimeout(30000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(30000, TimeUnit.MILLISECONDS);
        builder.followRedirects(true);
        builder.followSslRedirects(true);
        ProxyConfig config = UserPreferences.getProxyConfig();
        if (config.type != Type.DIRECT) {
            builder.proxy(new Proxy(config.type, InetSocketAddress.createUnresolved(config.host, config.port > 0 ? config.port : ProxyConfig.DEFAULT_PORT)));
            if (!TextUtils.isEmpty(config.username)) {
                builder.interceptors().add(new -$$Lambda$AntennapodHttpClient$3zrpkSXs8XGwdWU6Z46ApYW6JBQ(Credentials.basic(config.username, config.password)));
            }
        }
        if (16 <= VERSION.SDK_INT && VERSION.SDK_INT < 21) {
            builder.sslSocketFactory(new CustomSslSocketFactory(), trustManager());
        }
        return builder;
    }

    static /* synthetic */ Response lambda$newBuilder$0(Interceptor$Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.code() != 301) {
            if (response.code() != StatusLine.HTTP_PERM_REDIRECT) {
                return response;
            }
        }
        String location = response.header("Location");
        HttpUrl url;
        if (location.startsWith("/")) {
            url = request.url();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(url.scheme());
            stringBuilder.append("://");
            stringBuilder.append(url.host());
            stringBuilder.append(location);
            location = stringBuilder.toString();
        } else if (!location.toLowerCase().startsWith("http://")) {
            if (!location.toLowerCase().startsWith("https://")) {
                url = request.url();
                String path = url.encodedPath();
                String newPath = new StringBuilder();
                newPath.append(path.substring(0, path.lastIndexOf("/") + 1));
                newPath.append(location);
                newPath = newPath.toString();
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(url.scheme());
                stringBuilder2.append("://");
                stringBuilder2.append(url.host());
                stringBuilder2.append(newPath);
                location = stringBuilder2.toString();
            }
            DBWriter.updateFeedDownloadURL(request.url().toString(), location).get();
            return response;
        }
        try {
            DBWriter.updateFeedDownloadURL(request.url().toString(), location).get();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return response;
    }

    public static synchronized void cleanup() {
        synchronized (AntennapodHttpClient.class) {
            OkHttpClient okHttpClient = httpClient;
        }
    }

    private static X509TrustManager trustManager() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length == 1 && (trustManagers[0] instanceof X509TrustManager)) {
                return (X509TrustManager) trustManagers[0];
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unexpected default trust managers:");
            stringBuilder.append(Arrays.toString(trustManagers));
            throw new IllegalStateException(stringBuilder.toString());
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }
    }
}
