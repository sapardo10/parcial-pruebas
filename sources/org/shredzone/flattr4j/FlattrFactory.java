package org.shredzone.flattr4j;

import java.util.concurrent.atomic.AtomicReference;
import org.shredzone.flattr4j.connector.impl.FlattrConnector;
import org.shredzone.flattr4j.impl.FlattrServiceImpl;
import org.shredzone.flattr4j.oauth.AccessToken;

public final class FlattrFactory {
    private static final AtomicReference<FlattrFactory> instance = new AtomicReference();

    private FlattrFactory() {
    }

    public static FlattrFactory getInstance() {
        FlattrFactory result = (FlattrFactory) instance.get();
        if (result != null) {
            return result;
        }
        result = new FlattrFactory();
        if (instance.compareAndSet(null, result)) {
            return result;
        }
        return (FlattrFactory) instance.get();
    }

    public FlattrService createFlattrService(String accessToken) {
        return createFlattrService(new AccessToken(accessToken));
    }

    public FlattrService createFlattrService(AccessToken accessToken) {
        FlattrConnector connector = new FlattrConnector();
        connector.setAccessToken(accessToken);
        return new FlattrServiceImpl(connector);
    }

    public FlattrService createFlattrService() {
        return new FlattrServiceImpl(new FlattrConnector());
    }
}
