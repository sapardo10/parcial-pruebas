package org.shredzone.flattr4j.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.EnumSet;
import org.shredzone.flattr4j.connector.Connection;
import org.shredzone.flattr4j.connector.Connector;
import org.shredzone.flattr4j.connector.FlattrObject;
import org.shredzone.flattr4j.connector.RequestType;
import org.shredzone.flattr4j.connector.impl.FlattrConnector;
import org.shredzone.flattr4j.exception.FlattrException;

public class FlattrAuthenticator {
    private static final String ENCODING = "utf-8";
    private String accessTokenUrl;
    private String callbackUrl;
    private final ConsumerKey consumerKey;
    private String requestTokenUrl;
    private String responseType;
    private EnumSet<Scope> scope;

    public String getRequestTokenUrl() {
        return this.requestTokenUrl;
    }

    public void setRequestTokenUrl(String requestTokenUrl) {
        this.requestTokenUrl = requestTokenUrl;
    }

    public String getAccessTokenUrl() {
        return this.accessTokenUrl;
    }

    public void setAccessTokenUrl(String accessTokenUrl) {
        this.accessTokenUrl = accessTokenUrl;
    }

    public String getCallbackUrl() {
        return this.callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public EnumSet<Scope> getScope() {
        return this.scope;
    }

    public void setScope(EnumSet<Scope> scope) {
        this.scope = scope;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getResponseType() {
        return this.responseType;
    }

    public FlattrAuthenticator(ConsumerKey consumerKey) {
        this.requestTokenUrl = "https://flattr.com/oauth/authorize";
        this.accessTokenUrl = "https://flattr.com/oauth/token";
        this.responseType = "code";
        this.callbackUrl = null;
        this.scope = EnumSet.noneOf(Scope.class);
        this.consumerKey = consumerKey;
    }

    public FlattrAuthenticator(String key, String secret) {
        this(new ConsumerKey(key, secret));
    }

    public String authenticate() throws FlattrException {
        try {
            StringBuilder url = new StringBuilder();
            url.append(this.requestTokenUrl);
            url.append("?response_type=");
            url.append(URLEncoder.encode(this.responseType, ENCODING));
            url.append("&client_id=");
            url.append(URLEncoder.encode(this.consumerKey.getKey(), ENCODING));
            if (this.callbackUrl != null) {
                url.append("&redirect_uri=");
                url.append(URLEncoder.encode(this.callbackUrl, ENCODING));
            }
            if (!this.scope.isEmpty()) {
                url.append("&scope=");
                url.append(URLEncoder.encode(buildScopeString(), ENCODING));
            }
            return url.toString();
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public AccessToken fetchAccessToken(String code) throws FlattrException {
        Connection conn = createConnector().create(RequestType.POST).url(this.accessTokenUrl).key(this.consumerKey).form("code", code).form("grant_type", "authorization_code");
        String str = this.callbackUrl;
        if (str != null) {
            conn.form("redirect_uri", str);
        }
        FlattrObject response = conn.singleResult();
        String accessToken = response.get("access_token");
        String tokenType = response.get("token_type");
        if ("bearer".equalsIgnoreCase(tokenType)) {
            return new AccessToken(accessToken);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unknown token type ");
        stringBuilder.append(tokenType);
        throw new FlattrException(stringBuilder.toString());
    }

    protected Connector createConnector() {
        return new FlattrConnector();
    }

    protected String buildScopeString() {
        StringBuilder sb = new StringBuilder();
        if (this.scope.contains(Scope.FLATTR)) {
            sb.append(" flattr");
        }
        if (this.scope.contains(Scope.THING)) {
            sb.append(" thing");
        }
        if (this.scope.contains(Scope.EMAIL)) {
            sb.append(" email");
        }
        if (this.scope.contains(Scope.EXTENDEDREAD)) {
            sb.append(" extendedread");
        }
        return sb.toString().trim();
    }
}
