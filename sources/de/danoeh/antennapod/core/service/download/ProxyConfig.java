package de.danoeh.antennapod.core.service.download;

import android.support.annotation.Nullable;
import java.net.Proxy.Type;

public class ProxyConfig {
    public static final int DEFAULT_PORT = 8080;
    @Nullable
    public final String host;
    @Nullable
    public final String password;
    public final int port;
    public final Type type;
    @Nullable
    public final String username;

    public static ProxyConfig direct() {
        return new ProxyConfig(Type.DIRECT, null, 0, null, null);
    }

    public static ProxyConfig http(String host, int port, String username, String password) {
        return new ProxyConfig(Type.HTTP, host, port, username, password);
    }

    public ProxyConfig(Type type, String host, int port, String username, String password) {
        this.type = type;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
}
