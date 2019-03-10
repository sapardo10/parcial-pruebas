package de.danoeh.antennapod.core.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import de.danoeh.antennapod.core.BuildConfig;

public final class URLChecker {
    private static final String AP_SUBSCRIBE = "antennapod-subscribe://";
    private static final String TAG = "URLChecker";

    private URLChecker() {
    }

    public static String prepareURL(@NonNull String url) {
        url = url.trim();
        if (url.startsWith("feed://")) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Replacing feed:// with http://");
            }
            return url.replaceFirst("feed://", "http://");
        } else if (url.startsWith("pcast://")) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Removing pcast://");
            }
            return prepareURL(url.substring("pcast://".length()));
        } else if (url.startsWith("pcast:")) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Removing pcast:");
            }
            return prepareURL(url.substring("pcast:".length()));
        } else if (url.startsWith("itpc")) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Replacing itpc:// with http://");
            }
            return url.replaceFirst("itpc://", "http://");
        } else if (url.startsWith(AP_SUBSCRIBE)) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Removing antennapod-subscribe://");
            }
            return prepareURL(url.substring(AP_SUBSCRIBE.length()));
        } else if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Adding http:// at the beginning of the URL");
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("http://");
            stringBuilder.append(url);
            return stringBuilder.toString();
        }
    }

    public static String prepareURL(String url, String base) {
        if (base == null) {
            return prepareURL(url);
        }
        url = url.trim();
        base = prepareURL(base);
        Uri urlUri = Uri.parse(url);
        Uri baseUri = Uri.parse(base);
        if (urlUri.isRelative() && baseUri.isAbsolute()) {
            return urlUri.buildUpon().scheme(baseUri.getScheme()).build().toString();
        }
        return prepareURL(url);
    }
}
