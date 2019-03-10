package org.shredzone.flattr4j.oauth;

import android.content.Intent;
import android.net.Uri;
import org.shredzone.flattr4j.exception.FlattrException;

public class AndroidAuthenticator extends FlattrAuthenticator {
    public AndroidAuthenticator(String host, ConsumerKey consumerKey) {
        super(consumerKey);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("flattr4j://");
        stringBuilder.append(host);
        stringBuilder.append("/authenticate");
        setCallbackUrl(stringBuilder.toString());
    }

    public AndroidAuthenticator(String host, String key, String secret) {
        this(host, new ConsumerKey(key, secret));
    }

    public Intent createAuthenticateIntent() throws FlattrException {
        String url = super.authenticate();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(url));
        return intent;
    }

    public AccessToken fetchAccessToken(Uri uri) throws FlattrException {
        if (uri != null) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                return fetchAccessToken(code);
            }
        }
        return null;
    }
}
