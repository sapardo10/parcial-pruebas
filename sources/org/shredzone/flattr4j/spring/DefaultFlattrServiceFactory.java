package org.shredzone.flattr4j.spring;

import org.shredzone.flattr4j.FlattrFactory;
import org.shredzone.flattr4j.FlattrService;
import org.shredzone.flattr4j.oauth.AccessToken;
import org.shredzone.flattr4j.oauth.ConsumerKey;
import org.shredzone.flattr4j.oauth.FlattrAuthenticator;

public class DefaultFlattrServiceFactory implements FlattrServiceFactory {
    private final AccessToken accessToken;
    private final ConsumerKey consumerKey;

    public DefaultFlattrServiceFactory(ConsumerKey consumerKey) {
        this(consumerKey, null);
    }

    public DefaultFlattrServiceFactory(ConsumerKey consumerKey, AccessToken accessToken) {
        if (consumerKey != null) {
            this.consumerKey = consumerKey;
            this.accessToken = accessToken;
            return;
        }
        throw new IllegalArgumentException("A ConsumerKey is required");
    }

    public FlattrService getFlattrService() {
        return getFlattrService(this.accessToken);
    }

    public FlattrService getFlattrService(AccessToken at) {
        if (at != null) {
            return FlattrFactory.getInstance().createFlattrService(at);
        }
        throw new IllegalStateException("An AccessToken is required");
    }

    public FlattrAuthenticator getFlattrAuthenticator() {
        return new FlattrAuthenticator(this.consumerKey);
    }
}
