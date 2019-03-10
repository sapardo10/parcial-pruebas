package okhttp3;

import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.ssl.SSLSocket;
import okhttp3.OkHttpClient.Builder;
import okhttp3.internal.Internal;
import okhttp3.internal.cache.InternalCache;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.connection.RouteDatabase;
import okhttp3.internal.connection.StreamAllocation;

class OkHttpClient$1 extends Internal {
    OkHttpClient$1() {
    }

    public void addLenient(Headers$Builder builder, String line) {
        builder.addLenient(line);
    }

    public void addLenient(Headers$Builder builder, String name, String value) {
        builder.addLenient(name, value);
    }

    public void setCache(Builder builder, InternalCache internalCache) {
        builder.setInternalCache(internalCache);
    }

    public boolean connectionBecameIdle(ConnectionPool pool, RealConnection connection) {
        return pool.connectionBecameIdle(connection);
    }

    public RealConnection get(ConnectionPool pool, Address address, StreamAllocation streamAllocation, Route route) {
        return pool.get(address, streamAllocation, route);
    }

    public boolean equalsNonHost(Address a, Address b) {
        return a.equalsNonHost(b);
    }

    public Socket deduplicate(ConnectionPool pool, Address address, StreamAllocation streamAllocation) {
        return pool.deduplicate(address, streamAllocation);
    }

    public void put(ConnectionPool pool, RealConnection connection) {
        pool.put(connection);
    }

    public RouteDatabase routeDatabase(ConnectionPool connectionPool) {
        return connectionPool.routeDatabase;
    }

    public int code(Response.Builder responseBuilder) {
        return responseBuilder.code;
    }

    public void apply(ConnectionSpec tlsConfiguration, SSLSocket sslSocket, boolean isFallback) {
        tlsConfiguration.apply(sslSocket, isFallback);
    }

    public HttpUrl getHttpUrlChecked(String url) throws MalformedURLException, UnknownHostException {
        return HttpUrl.getChecked(url);
    }

    public StreamAllocation streamAllocation(Call call) {
        return ((RealCall) call).streamAllocation();
    }

    public Call newWebSocketCall(OkHttpClient client, Request originalRequest) {
        return RealCall.newRealCall(client, originalRequest, true);
    }
}
