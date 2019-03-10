package de.danoeh.antennapod.core;

import android.app.PendingIntent;
import android.content.Context;

public interface GpodnetCallbacks {
    PendingIntent getGpodnetSyncServiceErrorNotificationPendingIntent(Context context);

    boolean gpodnetEnabled();
}
