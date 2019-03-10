package org.shredzone.flattr4j.oauth;

import java.io.Serializable;

public final class AccessToken implements Serializable {
    private static final long serialVersionUID = 7715751842047101911L;
    private final String token;

    public AccessToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
