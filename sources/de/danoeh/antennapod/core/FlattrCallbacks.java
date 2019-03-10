package de.danoeh.antennapod.core;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import org.shredzone.flattr4j.oauth.AccessToken;

public interface FlattrCallbacks {
    boolean flattrEnabled();

    String getFlattrAppKey();

    String getFlattrAppSecret();

    Intent getFlattrAuthenticationActivityIntent(Context context);

    PendingIntent getFlattrFailedNotificationContentIntent(Context context);

    void handleFlattrAuthenticationSuccess(AccessToken accessToken);
}
