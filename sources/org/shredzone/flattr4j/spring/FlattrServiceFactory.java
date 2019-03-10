package org.shredzone.flattr4j.spring;

import org.shredzone.flattr4j.FlattrService;
import org.shredzone.flattr4j.oauth.AccessToken;
import org.shredzone.flattr4j.oauth.FlattrAuthenticator;

public interface FlattrServiceFactory {
    FlattrAuthenticator getFlattrAuthenticator();

    FlattrService getFlattrService();

    FlattrService getFlattrService(AccessToken accessToken);
}
