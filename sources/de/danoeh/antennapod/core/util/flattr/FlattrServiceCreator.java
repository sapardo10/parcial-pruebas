package de.danoeh.antennapod.core.util.flattr;

import android.util.Log;
import de.danoeh.antennapod.core.BuildConfig;
import org.shredzone.flattr4j.FlattrFactory;
import org.shredzone.flattr4j.FlattrService;
import org.shredzone.flattr4j.oauth.AccessToken;

class FlattrServiceCreator {
    public static final String TAG = "FlattrServiceCreator";
    private static volatile FlattrService flattrService;

    private FlattrServiceCreator() {
    }

    public static synchronized FlattrService getService(AccessToken token) {
        FlattrService flattrService;
        synchronized (FlattrServiceCreator.class) {
            if (flattrService == null) {
                flattrService = FlattrFactory.getInstance().createFlattrService(token);
            }
            flattrService = flattrService;
        }
        return flattrService;
    }

    public static synchronized void deleteFlattrService() {
        synchronized (FlattrServiceCreator.class) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Deleting service instance");
            }
            flattrService = null;
        }
    }
}
