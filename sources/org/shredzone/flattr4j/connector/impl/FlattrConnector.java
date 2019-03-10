package org.shredzone.flattr4j.connector.impl;

import org.shredzone.flattr4j.connector.Connection;
import org.shredzone.flattr4j.connector.Connector;
import org.shredzone.flattr4j.connector.RequestType;
import org.shredzone.flattr4j.exception.FlattrException;
import org.shredzone.flattr4j.oauth.AccessToken;

public class FlattrConnector implements Connector {
    private AccessToken accessToken;
    private String baseUrl = "https://api.flattr.com/rest/v2/";

    public Connection create() throws FlattrException {
        return create(RequestType.GET);
    }

    public Connection create(RequestType type) throws FlattrException {
        FlattrConnection connection = new FlattrConnection(type);
        connection.url(this.baseUrl);
        AccessToken accessToken = this.accessToken;
        if (accessToken != null) {
            connection.token(accessToken);
        }
        return connection;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public AccessToken getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }
}
