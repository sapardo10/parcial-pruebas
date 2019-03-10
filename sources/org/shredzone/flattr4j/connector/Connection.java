package org.shredzone.flattr4j.connector;

import java.util.Collection;
import org.shredzone.flattr4j.exception.FlattrException;
import org.shredzone.flattr4j.oauth.AccessToken;
import org.shredzone.flattr4j.oauth.ConsumerKey;

public interface Connection {
    Connection call(String str);

    Connection data(FlattrObject flattrObject);

    Connection form(String str, String str2);

    Connection key(ConsumerKey consumerKey);

    Connection parameter(String str, String str2);

    Connection parameterArray(String str, String[] strArr);

    Connection query(String str, String str2);

    Connection rateLimit(RateLimit rateLimit);

    Collection<FlattrObject> result() throws FlattrException;

    FlattrObject singleResult() throws FlattrException;

    Connection token(AccessToken accessToken);

    Connection url(String str);
}
