package de.danoeh.antennapod.config;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.GpodnetCallbacks;

public class GpodnetCallbacksImpl implements GpodnetCallbacks {
    public boolean gpodnetEnabled() {
        return true;
    }

    public PendingIntent getGpodnetSyncServiceErrorNotificationPendingIntent(Context context) {
        return PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 134217728);
    }
}
