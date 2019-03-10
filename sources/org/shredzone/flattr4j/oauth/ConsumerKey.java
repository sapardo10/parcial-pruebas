package org.shredzone.flattr4j.oauth;

import java.io.Serializable;

public final class ConsumerKey implements Serializable {
    private static final long serialVersionUID = -2439158677542078353L;
    private final String key;
    private final String secret;

    public ConsumerKey(String key, String secret) {
        this.key = key;
        this.secret = secret;
    }

    public String getKey() {
        return this.key;
    }

    public String getSecret() {
        return this.secret;
    }
}
