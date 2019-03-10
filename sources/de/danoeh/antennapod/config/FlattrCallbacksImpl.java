package de.danoeh.antennapod.config;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.danoeh.antennapod.activity.FlattrAuthActivity;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.FlattrCallbacks;
import org.shredzone.flattr4j.oauth.AccessToken;

public class FlattrCallbacksImpl implements FlattrCallbacks {
    private static final String TAG = "FlattrCallbacksImpl";

    public boolean flattrEnabled() {
        return true;
    }

    public Intent getFlattrAuthenticationActivityIntent(Context context) {
        return new Intent(context, FlattrAuthActivity.class);
    }

    public PendingIntent getFlattrFailedNotificationContentIntent(Context context) {
        return PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
    }

    public String getFlattrAppKey() {
        return "";
    }

    public String getFlattrAppSecret() {
        return "";
    }

    public void handleFlattrAuthenticationSuccess(AccessToken token) {
        FlattrAuthActivity instance = FlattrAuthActivity.getInstance();
        if (instance != null) {
            instance.handleAuthenticationSuccess();
        } else {
            Log.e(TAG, "FlattrAuthActivity instance was null");
        }
    }
}
